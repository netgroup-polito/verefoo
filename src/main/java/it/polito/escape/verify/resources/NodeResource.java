package it.polito.escape.verify.resources;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.polito.escape.verify.exception.BadRequestException;
import it.polito.escape.verify.exception.ForbiddenException;
import it.polito.escape.verify.model.Configuration;
import it.polito.escape.verify.model.ErrorMessage;
import it.polito.escape.verify.model.Graph;
import it.polito.escape.verify.model.Neighbour;
import it.polito.escape.verify.model.Node;
import it.polito.escape.verify.service.GraphService;
import it.polito.escape.verify.service.NodeService;

@Api( hidden= true, value = "", description = "Manage nodes" )
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class NodeResource {
	
	NodeService nodeService = new NodeService();
	
    
    @GET
    @ApiOperation(
    	    httpMethod = "GET",
    	    value = "Returns all nodes of a given graph",
    	    notes = "Returns an array of nodes belonging to a given graph",
    	    response = Node.class,
    	    responseContainer = "List")
    @ApiResponses(value = { @ApiResponse(code = 403, message = "Invalid graph id", response = ErrorMessage.class),
    						@ApiResponse(code = 404, message = "Graph not found", response = ErrorMessage.class),
    						@ApiResponse(code = 500, message = "Internal server error", response = ErrorMessage.class),
    						@ApiResponse(code = 200, message = "All the nodes have been returned in the message body", response = Node.class, responseContainer = "List") })
    public List<Node> getNodes(@ApiParam(value = "Graph id", required = true) @PathParam("graphId") long graphId){
    	return nodeService.getAllNodes(graphId);
    }
    
    @POST
    @ApiOperation(
    	    httpMethod = "POST",
    	    value = "Creates a node in a given graph",
    	    notes = "Creates a single node for a given graph",
    	    response = Response.class)
    @ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid node supplied", response = ErrorMessage.class),
    						@ApiResponse(code = 403, message = "Invalid graph id", response = ErrorMessage.class),
    						@ApiResponse(code = 404, message = "Graph not found", response = ErrorMessage.class),
    						@ApiResponse(code = 500, message = "Internal server error", response = ErrorMessage.class),
    						@ApiResponse(code = 201, message = "Node successfully created", response = Node.class)})
    public Response addNode(
    		@ApiParam(value = "Graph id", required = true) @PathParam("graphId") long graphId,
    		@ApiParam(value = "New node object", required = true) Node node,
    		@Context UriInfo uriInfo) {
        Node newNode = nodeService.addNode(graphId, node);
        String newId = String.valueOf(newNode.getId());
        URI uri = uriInfo.getAbsolutePathBuilder().path(newId).build();
        return Response.created(uri)
        			.entity(newNode)
        			.build();
    }
    
    @GET
    @Path("{nodeId}")
    @ApiOperation(
    	    httpMethod = "GET",
    	    value = "Returns a node of a given graph",
    	    notes = "Returns a single node of a given graph",
    	    response = Node.class)
    @ApiResponses(value = { @ApiResponse(code = 403, message = "Invalid graph and/or node id", response = ErrorMessage.class),
    						@ApiResponse(code = 404, message = "Graph and/or node not found", response = ErrorMessage.class),
    						@ApiResponse(code = 500, message = "Internal server error", response = ErrorMessage.class),
    						@ApiResponse(code = 200, message = "The requested node has been returned in the message body", response = Node.class)})
    public Node getNode(
    		@ApiParam(value = "Graph id", required = true) @PathParam("graphId") long graphId,
    		@ApiParam(value = "Node id", required = true) @PathParam("nodeId") long nodeId,
    		@Context UriInfo uriInfo){
    	Node node = nodeService.getNode(graphId, nodeId);
    	node.addLink(getUriForSelf(uriInfo, graphId, node), "self");
    	node.addLink(getUriForNeighbours(uriInfo, graphId, node), "neighbours");
    	return node;
    }
    
    @PUT
    @Path("{nodeId}/configuration")
    @ApiOperation(
    	    httpMethod = "PUT",
    	    value = "Adds/edits a configuration to a node of a given graph",
    	    notes = "Configures a node. Once all the nodes of a graph have been configured a given policy can be verified for the graph (e.g. 'reachability' between two nodes).")
    @ApiResponses(value = { @ApiResponse(code = 403, message = "Invalid graph and/or node id", response = ErrorMessage.class),
    						@ApiResponse(code = 404, message = "Graph and/or node not found", response = ErrorMessage.class),
    						@ApiResponse(code = 500, message = "Internal server error", response = ErrorMessage.class),
    						@ApiResponse(code = 200, message = "Configuration updated for the requested node")})
    public void addNodeConfiguration(
    		@ApiParam(value = "Graph id", required = true) @PathParam("graphId") long graphId,
    		@ApiParam(value = "Node id", required = true) @PathParam("nodeId") long nodeId,
    		@ApiParam(value = "Node configuration", required = true) Configuration nodeConfiguration,
    		@Context UriInfo uriInfo){
    	if (graphId <= 0) {
			throw new ForbiddenException("Illegal graph id: " + graphId);
		}
		if (nodeId <= 0) {
			throw new ForbiddenException("Illegal node id: " + nodeId);
		}
    	Graph graph = new GraphService().getGraph(graphId);
    	if (graph == null){
    		throw new BadRequestException("Graph with id " + graphId + " not found");
    	}
    	Node node = nodeService.getNode(graphId, nodeId);
    	if (node == null){
    		throw new BadRequestException("Node with id " + nodeId + " not found in graph with id " + graphId);
    	}
    	Node nodeCopy = new Node();
    	nodeCopy.setId(node.getId());
    	nodeCopy.setName(node.getName());
    	nodeCopy.setFunctional_type(node.getFunctional_type());
    	Map<Long,Neighbour> nodes = new HashMap<Long,Neighbour>();
    	nodes.putAll(node.getNeighbours());
    	nodeCopy.setNeighbours(nodes);
    	nodeConfiguration.setId(nodeCopy.getName());
    	nodeCopy.setConfiguration(nodeConfiguration);
    	
    	Graph graphCopy = new Graph();
		graphCopy.setId(graph.getId());
		graphCopy.setNodes(new HashMap<Long, Node>(graph.getNodes()));
		graphCopy.getNodes().remove(node.getId());
		
		NodeService.validateNode(graphCopy, nodeCopy);
		
    	graph.getNodes().put(nodeId, nodeCopy);
    }
    

	@PUT
    @Path("{nodeId}")
    @ApiOperation(
    	    httpMethod = "PUT",
    	    value = "Edits a node of a given graph",
    	    notes = "Edits a single node of a given graph",
    	    response = Node.class)
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid node object", response = ErrorMessage.class),
							@ApiResponse(code = 403, message = "Invalid graph and/or node id", response = ErrorMessage.class),
							@ApiResponse(code = 404, message = "Graph and/or node not found", response = ErrorMessage.class),
							@ApiResponse(code = 500, message = "Internal server error", response = ErrorMessage.class),
							@ApiResponse(code = 200, message = "Node edited successfully", response = Node.class)})
    public Node updateNode(
    		@ApiParam(value = "Graph id", required = true) @PathParam("graphId") long graphId,
    		@ApiParam(value = "Node id", required = true) @PathParam("nodeId") long nodeId,
    		@ApiParam(value = "Updated node object", required = true) Node node){
    	node.setId(nodeId);
    	return nodeService.updateNode(graphId, node);
    }
    
    @DELETE
    @Path("{nodeId}")
    @ApiOperation(
    	    httpMethod = "DELETE",
    	    value = "Deletes a node of a given graph",
    	    notes = "Deletes a single node of a given graph")
    @ApiResponses(value = { @ApiResponse(code = 403, message = "Invalid graph and/or node id", response = ErrorMessage.class),
                            @ApiResponse(code = 500, message = "Internal server error", response = ErrorMessage.class),
    						@ApiResponse(code = 204, message = "Node successfully deleted")})
    public void deleteNode(
    		@ApiParam(value = "Graph id", required = true) @PathParam("graphId") long graphId,
    		@ApiParam(value = "Node id", required = true) @PathParam("nodeId") long nodeId){
    	nodeService.removeNode(graphId, nodeId);
    }
    
    private String getUriForSelf(UriInfo uriInfo, long graphId, Node node) {
		String uri = uriInfo.getBaseUriBuilder()
		 //.path(NodeResource.class)
		 .path(GraphResource.class)
		 .path(GraphResource.class, "getNodeResource")
		 .resolveTemplate("graphId", graphId)
		 .path(Long.toString(node.getId()))
		 .build()
		 .toString();
		return uri;
	}
    
    private String getUriForNeighbours(UriInfo uriInfo, long graphId, Node node) {
		String uri = uriInfo.getBaseUriBuilder()
				.path(GraphResource.class)
				.path(GraphResource.class, "getNodeResource")
				.resolveTemplate("graphId", graphId)
				.path(Long.toString(node.getId()))
				.path("neighbours")
				.build()
				.toString();
//    	 .path(NodeResource.class)
//		 .path(NodeResource.class, "getNeighbourResource")
//		 .path(NeighbourResource.class)
//		 .resolveTemplate("nodeId", node.getId())
//		 .build()
//		 .toString();
    	return uri;
	}
    
	@Path("{nodeId}/neighbours")	
	public NeighbourResource getNeighbourResource(){
		return new NeighbourResource();
	}
}
