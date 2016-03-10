package it.polito.escape.verify.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.escape.verify.exception.BadRequestException;
import it.polito.escape.verify.exception.DataNotFoundException;
import it.polito.escape.verify.exception.ForbiddenException;
import it.polito.escape.verify.database.DatabaseClass;
import it.polito.escape.verify.model.Node;

public class NodeService {
	
	private Map<Long, Node> nodes = DatabaseClass.getNodes();
	
	public NodeService(){
		
	}
	
	public List<Node> getAllNodes(){
		return new ArrayList<Node>(nodes.values());
	}
	
	public Node getNode(long id){
		Node node = nodes.get(id);
		if (node == null){
			throw new DataNotFoundException("Node with id " + id + " not found");
		}
		return node;
	}
	
	public Node updateNode(Node node){
		if (node.getId() <= 0){
			throw new ForbiddenException("Illegal node id: " + node.getId());
		}
		
		Node localNode = nodes.get(node.getId());
		if (localNode == null){
			throw new DataNotFoundException("Node with id " + node.getId() + " not found");
		}
		
		if (!isValidNode(node))
			throw new BadRequestException("Request node is not valid: name and functional_type are required fields");
		
		nodes.put(node.getId(), node);
		
		return node;
	}
	
	public Node removeNode(long id){
		return nodes.remove(id);
	}

	public Node addNode(Node node) {
		if (isValidNode(node) == false)
			throw new BadRequestException("Node is not valid: name and functional_type are required fields");
		
		node.setId(DatabaseClass.getNumberOfNodes() + 1);
				
		nodes.put(node.getId(), node);
		return node;
	}
	
	public Node searchByName(String nodeName){
		for (Node node : nodes.values()){
			if (node.getName().equals(nodeName))
				return node;
		}
		return null;
	}
	
	public boolean isValidNode(Node node){
		if (node.getName() == null || node.getFunctional_type() == null)
			return false;
		else
			return true;
	}
}
