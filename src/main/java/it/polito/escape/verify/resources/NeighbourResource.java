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

import it.polito.escape.verify.model.Neighbour;
import it.polito.escape.verify.service.NeighbourService;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class NeighbourResource {
	private NeighbourService neighboursService = new NeighbourService();
	
	@GET
	public List<Neighbour> getAllNeighbours(@PathParam("nodeId") long nodeId){
		return neighboursService.getAllNeighbours(nodeId);
	}
	
	@POST
	public Neighbour addNeighbour(@PathParam("nodeId") long nodeId, Neighbour neighbour){
		return neighboursService.addNeighbour(nodeId, neighbour);
	}
	
	@PUT
	@Path("/{neighbourId}")
	public Neighbour updateNeighbour(@PathParam("nodeId") long nodeId, @PathParam("neighbourId") long neighbourId, Neighbour neighbour){
		neighbour.setId(neighbourId);
		return neighboursService.updateNeighbour(nodeId, neighbour);
	}
	
	@DELETE
	@Path("/{neighbourId}")
	public void deleteNeighbour(@PathParam("nodeId") long nodeId, @PathParam("neighbourId") long neighbourId){
		neighboursService.removeNeighbour(nodeId, neighbourId);
	}
	
	@GET
	@Path("/{neighbourId}")
	public Neighbour getNeighbour(@PathParam("nodeId") long nodeId, @PathParam("neighbourId") long neighbourId){
		return neighboursService.getNeighbour(nodeId, neighbourId);
	}
}
