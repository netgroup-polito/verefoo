package it.polito.escape.verify.service;

import java.util.ArrayList;
import java.util.Collection;
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
import it.polito.escape.verify.model.Graph;
import it.polito.escape.verify.model.Neighbour;
import it.polito.escape.verify.model.Node;

public class NeighbourService {
	private Map<Long, Graph> graphs = DatabaseClass.getGraphs();

	public List<Neighbour> getAllNeighbours(long graphId, long nodeId) {
		if (graphId <= 0) {
			throw new ForbiddenException("Illegal graph id: " + graphId);
		}
		if (nodeId <= 0) {
			throw new ForbiddenException("Illegal node id: " + nodeId);
		}
		Graph graph = graphs.get(graphId);
		if (graph == null)
			throw new DataNotFoundException("Graph with id " + graphId + " not found");
		Map<Long, Node> nodes = graph.getNodes();
		Node node = nodes.get(nodeId);
		if (node == null)
			throw new DataNotFoundException("Node with id " + nodeId + " not found in graph with id " + graphId);
		Map<Long, Neighbour> neighbours = node.getNeighbours();
		return new ArrayList<Neighbour>(neighbours.values());
	}

	public Neighbour getNeighbour(long graphId, long nodeId, long neighbourId) {
		if (graphId <= 0) {
			throw new ForbiddenException("Illegal graph id: " + graphId);
		}
		if (nodeId <= 0) {
			throw new ForbiddenException("Illegal node id: " + nodeId);
		}
		if (neighbourId <= 0) {
			throw new ForbiddenException("Illegal neighbour id: " + neighbourId);
		}
		Graph graph = graphs.get(graphId);
		if (graph == null)
			throw new DataNotFoundException("Graph with id " + graphId + " not found");
		Map<Long, Node> nodes = graph.getNodes();
		Node node = nodes.get(nodeId);
		if (node == null) {
			throw new DataNotFoundException("Node with id " + nodeId + " not found in graph with id " + graphId);
		}
		Map<Long, Neighbour> neighbours = node.getNeighbours();
		Neighbour neighbour = neighbours.get(neighbourId);
		if (neighbour == null) {
			throw new DataNotFoundException("Neighbour with id "+ neighbourId + " not found for node with id " + nodeId
											+ " in graph with id " + graphId);
		}
		return neighbour;
	}

	public Neighbour addNeighbour(long graphId, long nodeId, Neighbour neighbour) {
		if (graphId <= 0) {
			throw new ForbiddenException("Illegal graph id: " + graphId);
		}
		if (nodeId <= 0) {
			throw new ForbiddenException("Illegal node id: " + nodeId);
		}
		Graph graph = graphs.get(graphId);
		if (graph == null)
			throw new DataNotFoundException("Graph with id " + graphId + " not found");
		Map<Long, Node> nodes = graph.getNodes();
		Node node = nodes.get(nodeId);
		if (node == null) {
			throw new DataNotFoundException("Node with id " + nodeId + " not found in graph with id " + graphId);
		}
		Map<Long, Neighbour> neighbours = node.getNeighbours();
		
		validateNeighbour(graph, node, neighbour);
		
		synchronized (this) {
			neighbour.setId(neighbours.size() + 1);
		}
		neighbours.put(neighbour.getId(), neighbour);
		return neighbour;
	}

	public Neighbour updateNeighbour(long graphId, long nodeId, Neighbour neighbour) {
		if (graphId <= 0) {
			throw new ForbiddenException("Illegal graph id: " + graphId);
		}
		if (nodeId <= 0) {
			throw new ForbiddenException("Illegal node id: " + nodeId);
		}
		if (neighbour.getId() <= 0) {
			throw new ForbiddenException("Illegal neighbour id: " + nodeId);
		}
		Graph graph = graphs.get(graphId);
		if (graph == null)
			throw new DataNotFoundException("Graph with id " + graphId + " not found");
		Map<Long, Node> nodes = graph.getNodes();
		Node node = nodes.get(nodeId);
		if (node == null) {
			throw new DataNotFoundException("Node with id " + nodeId + " not found in graph with id " + graphId);
		}
		Map<Long, Neighbour> neighbours = node.getNeighbours();
		Neighbour currentNeighbour = neighbours.get(neighbour.getId());
		if (currentNeighbour == null) {
			throw new DataNotFoundException("Neighbour with id "+ neighbour.getId() + " not found for node with id "
											+ nodeId + " in graph with id " + graphId);
		}
		
		validateNeighbour(graph, node, neighbour);
		
		neighbours.put(neighbour.getId(), neighbour);
		return neighbour;
	}

	public Neighbour removeNeighbour(long graphId, long nodeId, long neighbourId) {
		if (graphId <= 0) {
			throw new ForbiddenException("Illegal graph id: " + graphId);
		}
		if (nodeId <= 0) {
			throw new ForbiddenException("Illegal node id: " + nodeId);
		}
		if (neighbourId <= 0) {
			throw new ForbiddenException("Illegal neighbour id: " + nodeId);
		}
		Graph graph = graphs.get(graphId);
		if (graph == null)
			throw new DataNotFoundException("Graph with id " + graphId + " not found");
		Map<Long, Node> nodes = graph.getNodes();
		Node node = nodes.get(nodeId);
		if (node == null) {
			throw new DataNotFoundException("Node with id " + nodeId + " not found in graph with id " + graphId);
		}
		Map<Long, Neighbour> neighbours = node.getNeighbours();
		return neighbours.remove(neighbourId);
	}

//	public static boolean isValidNeighbourAddition(Graph graph, Node node, Neighbour neighbour) {
//		if (neighbour.getName() == null)
//			return false;
//
//		Map<Long, Node> nodesMap = graph.getNodes();
//		if (nodesMap == null)
//			return false;
//
//		boolean isAcceptableNode = false;
//		Collection<Node> nodes = nodesMap.values();
//		if (nodes == null)
//			return false;
//		for (Node n : nodes) {
//			if ((n.getName().equals(neighbour.getName())) && (n.getName().equals(node.getName()) == false)) {
//				if (node.neighboursWithName(neighbour.getName()) < 1)
//					isAcceptableNode = true;
//			}
//		}
//		return isAcceptableNode;
//	}

//	public static boolean isValidNeighbourUpdate(Graph graph, Node node, Neighbour neighbour) {
//		if(graph==null || node == null || neighbour == null)
//			return false;
//		if (neighbour.getName() == null || neighbour.getName().equals(""))
//			return false;
//		Map<Long, Node> nodesMap = graph.getNodes();
//		if (nodesMap == null)
//			return false;
//
//		boolean isAcceptableNode = false;
//		Collection<Node> nodes = nodesMap.values();
//		if (nodes == null)
//			return false;
//		for (Node currentNode : nodes) {
//			if ((currentNode.getName().equals(neighbour.getName())) && (currentNode.getName().equals(node.getName()) == false)) {
//				if (node.neighboursWithName(neighbour.getName()) <= 1)
//					isAcceptableNode = true;
//			}
//		}
//		return isAcceptableNode;
//	}
	
	public static void validateNeighbour(Graph graph, Node node, Neighbour neighbour) {
		if (node == null)
			throw new BadRequestException("Neighbour validation failed: cannot validate null node");
		if (neighbour == null)
			throw new BadRequestException("Neighbour validation failed: cannot validate null neighbour");
		
		if (neighbour.getName() == null)
			throw new BadRequestException("Neighbour validation failed: neighbour 'name' field cannot be null");
		if (neighbour.getName().equals(""))
			throw new BadRequestException("Neighbour validation failed: neighbour 'name' field cannot be an empty string");
		
		Node nodeFound = graph.searchNodeByName(neighbour.getName());
		if ((nodeFound == null) || (nodeFound.getName().equals(node.getName())))
			throw new BadRequestException("Neighbour validation failed: '" + neighbour.getName() + "' is not a valid name for a neighbour of node '" + node.getName() + "'");
		
		Neighbour neighbourFound = node.searchNeighbourByName(neighbour.getName());
		if((neighbourFound != null) && (neighbourFound.equals(neighbour) == false) )
			throw new BadRequestException("Neighbour validation failed: node '" + node.getName() + "' already has a neighbour named '" + neighbour.getName() + "'");
	}
}
