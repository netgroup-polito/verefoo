package it.polito.escape.verify.resources;

import java.net.URI;
import java.util.List;

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
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ApiResponse;
import it.polito.escape.verify.resources.NeighbourResource;
import it.polito.escape.verify.model.Node;
import it.polito.escape.verify.service.NodeService;

//@Path("/")
@Api( hidden= true, value = "", description = "Manage nodes" )
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class NodeResource {
	
	NodeService nodeService = new NodeService();
	
    
    @GET
    @ApiOperation(
    	    httpMethod = "GET",
    	    value = "Returns all the nodes",
    	    notes = "Returns multiple nodes",
    	    response = Node.class,
    	    responseContainer = "List")
    public List<Node> getNodes(@ApiParam(value = "Graph id", required = true) @PathParam("graphId") long graphId){
    	return nodeService.getAllNodes(graphId);
    }
    
    @POST
    @ApiOperation(
    	    httpMethod = "POST",
    	    value = "Creates a node",
    	    notes = "A single node can be created",
    	    response = Response.class)
    @ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid node supplied") })
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
    	    value = "Returns a node",
    	    notes = "A single node can be returned",
    	    response = Node.class)
    @ApiResponses(value = { @ApiResponse(code = 403, message = "Invalid node id"),
    						@ApiResponse(code = 404, message = "Node not found")})
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
    @Path("{nodeId}")
    @ApiOperation(
    	    httpMethod = "PUT",
    	    value = "Edits a node",
    	    notes = "A single node can be edited",
    	    response = Node.class)
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid node object"),
							@ApiResponse(code = 403, message = "Invalid node id"),
							@ApiResponse(code = 404, message = "Node not found")})
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
    	    value = "Deletes a node",
    	    notes = "A single node can be deleted")
    @ApiResponses(value = { @ApiResponse(code = 403, message = "Invalid node id")})
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
