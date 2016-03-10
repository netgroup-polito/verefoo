package it.polito.escape.verify.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import it.polito.escape.verify.database.DatabaseClass;
import it.polito.escape.verify.exception.BadRequestException;
import it.polito.escape.verify.exception.DataNotFoundException;
import it.polito.escape.verify.exception.ForbiddenException;
import it.polito.escape.verify.model.ErrorMessage;
import it.polito.escape.verify.model.Neighbour;
import it.polito.escape.verify.model.Node;

public class NeighbourService {
	private Map<Long, Node> nodes = DatabaseClass.getNodes();
	
	public List<Neighbour> getAllNeighbours(long nodeId){
		Node node = nodes.get(nodeId);
		if (node == null)
			throw new DataNotFoundException("Node with id " + nodeId + " not found");
		Map<Long, Neighbour> neighbours = node.getNeighbours();
		return new ArrayList<Neighbour>(neighbours.values());
	}
	
	public Neighbour getNeighbour(long nodeId, long neighbourId){		
		
		Node node = nodes.get(nodeId);
		if (node == null){
			throw new DataNotFoundException("Node with id " + nodeId + " not found");
		}
		Map<Long, Neighbour> neighbours = node.getNeighbours();
		Neighbour neighbour = neighbours.get(neighbourId);
		if (neighbour == null){
			throw new DataNotFoundException("Node with id " + nodeId + " doesn't have a neighbour with id " + neighbourId);
		}
		return neighbour;
	}
	
	public Neighbour addNeighbour(long nodeId, Neighbour neighbour){
		Node node = nodes.get(nodeId);
		if (node == null){
			throw new DataNotFoundException("Node with id " + nodeId + " not found");
		}
		Map<Long, Neighbour> neighbours = node.getNeighbours();
		if ( isValidNeighbour(neighbour) == false){
			throw new BadRequestException("Request neighbour is not valid: name is a required field");
		}
		synchronized(this){
			neighbour.setId(neighbours.size() + 1);
		}
		neighbours.put(neighbour.getId(), neighbour);
		return neighbour;
	}
	

	public Neighbour updateNeighbour(long nodeId, Neighbour neighbour){
		Node node = nodes.get(nodeId);
		if (node == null){
			throw new DataNotFoundException("Node with id " + nodeId + " not found");
		}
		Map<Long, Neighbour> neighbours = node.getNeighbours();
		if (neighbour.getId() <= 0){
			throw new ForbiddenException("Illegal node id: " + nodeId);
		}
		if ( isValidNeighbour(neighbour) == false){
			throw new BadRequestException("Request neighbour is not valid: name is a required field");
		}
		neighbours.put(neighbour.getId(), neighbour);
		return neighbour;
	}
	
	public Neighbour removeNeighbour(long nodeId, long neighbourId){
		Node node = nodes.get(nodeId);
		if (node == null){
			throw new DataNotFoundException("Node with id " + nodeId + " not found");
		}
		Map<Long, Neighbour> neighbours = node.getNeighbours();
		return neighbours.remove(neighbourId);
	}
	
	private boolean isValidNeighbour(Neighbour neighbour) {
		if (neighbour.getName() == null)
			return false;
		else
			return true;
	}
}
