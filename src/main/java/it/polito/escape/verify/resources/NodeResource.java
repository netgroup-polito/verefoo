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
import it.polito.escape.verify.resources.NeighbourResource;
import it.polito.escape.verify.model.Node;
import it.polito.escape.verify.service.NodeService;


@Path("/nodes")
@Api( value = "/nodes", description = "Manage nodes" )
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class NodeResource {
	
	NodeService nodeService = new NodeService();
	
    @GET
    @Path("/prova")
    @ApiOperation(value = "Returns a string",
    notes = "Useless method",
    response = String.class)
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public String getIt() {
        return "Got it!";
    }
    
    @GET
    public List<Node> getNodes(){
    	return nodeService.getAllNodes();
    }
    
    @POST
    public Response addNode(Node node, @Context UriInfo uriInfo) {
        Node newNode = nodeService.addNode(node);
        String newId = String.valueOf(newNode.getId());
        URI uri = uriInfo.getAbsolutePathBuilder().path(newId).build();
        return Response.created(uri)
        			.entity(newNode)
        			.build();
    }
    
    @GET
    @Path("/{nodeId}")
    public Node getNode(@PathParam("nodeId") long id, @Context UriInfo uriInfo){
    	Node node = nodeService.getNode(id);
    	node.addLink(getUriForSelf(uriInfo, node), "self");
    	node.addLink(getUriForNeighbours(uriInfo, node), "neighbours");
    	return node;
    }
    

	@PUT
    @Path("/{nodeId}")
    public Node updateNode(@PathParam("nodeId") long id, Node node){
    	node.setId(id);
    	return nodeService.updateNode(node);
    }
    
    @DELETE
    @Path("/{nodeId}")
    public void deleteNode(@PathParam("nodeId") long id){
    	nodeService.removeNode(id);
    }
    
    private String getUriForSelf(UriInfo uriInfo, Node node) {
		String uri = uriInfo.getBaseUriBuilder()
		 .path(NodeResource.class)
		 .path(Long.toString(node.getId()))
		 .build()
		 .toString();
		return uri;
	}
    
    private String getUriForNeighbours(UriInfo uriInfo, Node node) {
    	String uri = uriInfo.getBaseUriBuilder()
    			 .path(NodeResource.class)
    			 .path(NodeResource.class, "getNeighbourResource")
    			 .path(NeighbourResource.class)
    			 .resolveTemplate("nodeId", node.getId())
    			 .build()
    			 .toString();
    	return uri;
	}
    
	@Path("/{nodeId}/neighbours")
	public NeighbourResource getNeighbourResource(){
		return new NeighbourResource();
	}
}
