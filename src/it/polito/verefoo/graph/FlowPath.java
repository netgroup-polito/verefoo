package it.polito.verefoo.graph;

import java.util.*;

import it.polito.verefoo.allocation.*;

public class FlowPath {
	
	private List<AllocationNode> nodes;
	
	/**
	 * Constructor of RequirementPath class
	 * @param nodes it is the list of nodes that must be crossed by the flow related to a requirement
	 */
	public FlowPath(List<AllocationNode> nodes) {
		this.nodes = nodes;
	}


	/**
	 * Getter method for the path of nodes
	 * @return the path of nodes
	 */
	public List<AllocationNode> getNodes() {
		return nodes;
	}

	/**
	 * Setter method for the path of nodes
	 * @param nodes it is the path of nodes
	 */
	public void setNodes(List<AllocationNode> nodes) {
		this.nodes = nodes;
	}
	
	

}
