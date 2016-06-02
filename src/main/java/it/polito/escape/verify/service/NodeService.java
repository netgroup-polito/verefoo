package it.polito.escape.verify.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.escape.verify.exception.BadRequestException;
import it.polito.escape.verify.exception.DataNotFoundException;
import it.polito.escape.verify.exception.ForbiddenException;
import it.polito.escape.verify.database.DatabaseClass;
import it.polito.escape.verify.model.Graph;
import it.polito.escape.verify.model.Node;

public class NodeService {
	
	private Map<Long, Graph> graphs = DatabaseClass.getGraphs();
	
	public NodeService(){
		
	}
	
	public List<Node> getAllNodes(long graphId){
		if (graphId <= 0){
			throw new ForbiddenException("Illegal graph id: " + graphId);
		}
		Graph graph = graphs.get(graphId);
		if (graph == null)
			throw new DataNotFoundException("Graph with id " + graphId + " not found");
		Map<Long, Node> nodes = graph.getNodes();
		return new ArrayList<Node>(nodes.values());
	}
	
	public Node getNode(long graphId, long nodeId){
		if (graphId <= 0){
			throw new ForbiddenException("Illegal graph id: " + graphId);
		}
		if (nodeId <= 0){
			throw new ForbiddenException("Illegal node id: " + nodeId);
		}
		Graph graph = graphs.get(graphId);
		if (graph == null)
			throw new DataNotFoundException("Graph with id " + graphId + " not found");
		Map<Long, Node> nodes = graph.getNodes();
		Node node = nodes.get(nodeId);
		if (node == null){
			throw new DataNotFoundException("Node with id " + nodeId + " not found in graph with id " + graphId);
		}
		return node;
	}
	
	public Node updateNode(long graphId, Node node){
		if (graphId <= 0){
			throw new ForbiddenException("Illegal graph id: " + graphId);
		}
		if (node.getId() <= 0){
			throw new ForbiddenException("Illegal node id: " + node.getId());
		}
		Graph graph = graphs.get(graphId);
		if (graph == null)
			throw new DataNotFoundException("Graph with id " + graphId + " not found");
		Map<Long, Node> nodes = graph.getNodes();			
		Node localNode = nodes.get(node.getId());
		if (localNode == null){
			throw new DataNotFoundException("Node with id " + node.getId() + " not found in graph with id " + graphId);
		}
		if (!isValidNode(graph, node))
			throw new BadRequestException("Given node is not valid!");
		
		nodes.put(node.getId(), node);
		
		return node;
	}
	
	public Node removeNode(long graphId, long nodeId){
		if (graphId <= 0){
			throw new ForbiddenException("Illegal graph id: " + graphId);
		}
		if (nodeId <= 0){
			throw new ForbiddenException("Illegal node id: " + nodeId);
		}
		Graph graph = graphs.get(graphId);
		if (graph == null)
			throw new DataNotFoundException("Graph with id " + graphId + " not found");
		Map<Long, Node> nodes = graph.getNodes();
		
		return nodes.remove(nodeId);
	}

	public Node addNode(long graphId, Node node) {
		if (graphId <= 0){
			throw new ForbiddenException("Illegal graph id: " + graphId);
		}
		Graph graph = graphs.get(graphId);
		if (graph == null)
			throw new DataNotFoundException("Graph with id " + graphId + " not found");
		Map<Long, Node> nodes = graph.getNodes();
		
		if (isValidNode(graph, node) == false)
			throw new BadRequestException("Given node is not valid!");
		
		synchronized(this){
			node.setId(DatabaseClass.getGraphNumberOfNodes(graphId) + 1);
		}
				
		nodes.put(node.getId(), node);
		return node;
	}
	
	public Node searchByName(long graphId, String nodeName){
		if (graphId <= 0){
			throw new ForbiddenException("Illegal graph id: " + graphId);
		}
		Graph graph = graphs.get(graphId);
		if (graph == null)
			throw new DataNotFoundException("Graph with id " + graphId + " not found");
		Map<Long, Node> nodes = graph.getNodes();
		
		for (Node node : nodes.values()){
			if (node.getName().equals(nodeName))
				return node;
		}
		return null;
	}
	
	public static boolean isValidNode(Graph graph, Node node){
		if (node.getName() == null || node.getFunctional_type() == null)
			return false;

		return true;
	}
}
