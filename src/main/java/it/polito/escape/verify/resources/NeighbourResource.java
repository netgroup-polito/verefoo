package it.polito.escape.verify.resources;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.polito.escape.verify.model.Neighbour;
import it.polito.escape.verify.model.Node;
import it.polito.escape.verify.service.NeighbourService;

@Path("/")
@Api( value = "", description = "Manage neighbours" )
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class NeighbourResource {
	private NeighbourService neighboursService = new NeighbourService();
	
	@GET
    @ApiOperation(
    	    httpMethod = "GET",
    	    value = "Returns all the neighbours of a given node",
    	    notes = "Returns multiple neighbours given a node id",
    	    response = Neighbour.class,
    	    responseContainer = "List")
	@ApiResponses(value = { @ApiResponse(code = 403, message = "Invalid node id"),
							@ApiResponse(code = 404, message = "Node not found")})
	public List<Neighbour> getAllNeighbours(
			@ApiParam(value = "Node id", required = true) @PathParam("nodeId") long nodeId){
		return neighboursService.getAllNeighbours(nodeId);
	}
	
	@POST
    @ApiOperation(
    	    httpMethod = "POST",
    	    value = "Adds a neighbour to a given node",
    	    notes = "A single neighbour can be added to a given node",
    	    response = Neighbour.class)
	@ApiResponses(value = { @ApiResponse(code = 403, message = "Invalid node id"),
							@ApiResponse(code = 404, message = "Node not found"),
							@ApiResponse(code = 400, message = "Invalid neighbour object")})
	public Neighbour addNeighbour(
			@ApiParam(value = "Node id", required = true) @PathParam("nodeId") long nodeId, 
			@ApiParam(value = "New neighbour object", required = true) Neighbour neighbour){
		return neighboursService.addNeighbour(nodeId, neighbour);
	}
	
	@PUT
	@Path("/{neighbourId}")
	@ApiOperation(
    	    httpMethod = "PUT",
    	    value = "Edits a neighbour of a given node",
    	    notes = "A single neighbour of a given node can be edited",
    	    response = Neighbour.class)
	@ApiResponses(value = { @ApiResponse(code = 403, message = "Invalid node or neighbour id"),
							@ApiResponse(code = 404, message = "Node not found"),
							@ApiResponse(code = 400, message = "Invalid neighbour object")})
	public Neighbour updateNeighbour(
			@ApiParam(value = "Node id", required = true) @PathParam("nodeId") long nodeId,
			@ApiParam(value = "Neighbour id", required = true) @PathParam("neighbourId") long neighbourId,
			@ApiParam(value = "Updated neighbour object", required = true) Neighbour neighbour){
		neighbour.setId(neighbourId);
		return neighboursService.updateNeighbour(nodeId, neighbour);
	}
	
	@DELETE
	@Path("/{neighbourId}")
	@ApiOperation(
    	    httpMethod = "DELETE",
    	    value = "Removes a neighbour from a given node",
    	    notes = "A single neighbour of a given node can be removed",
    	    response = Neighbour.class)
	@ApiResponses(value = { @ApiResponse(code = 403, message = "Invalid node id"),
							@ApiResponse(code = 404, message = "Node not found")})
	public void deleteNeighbour(
			@ApiParam(value = "Node id", required = true) @PathParam("nodeId") long nodeId,
			@ApiParam(value = "Neighbour id", required = true) @PathParam("neighbourId") long neighbourId){
		neighboursService.removeNeighbour(nodeId, neighbourId);
	}
	
	@GET
	@Path("/{neighbourId}")
	@ApiOperation(
    	    httpMethod = "GET",
    	    value = "Returns a neighbour of a given node",
    	    notes = "A single neighbour of a given node can be returned",
    	    response = Neighbour.class)
	public Neighbour getNeighbour(
			@ApiParam(value = "Node id", required = true) @PathParam("nodeId") long nodeId,
			@ApiParam(value = "Neighbour id", required = true) @PathParam("neighbourId") long neighbourId){
		return neighboursService.getNeighbour(nodeId, neighbourId);
	}
}
