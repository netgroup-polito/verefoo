package it.polito.verefoo.graph;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.polito.verefoo.allocation.AllocationNode;
import it.polito.verefoo.jaxb.*;

public class FlowPathMF {

	SecurityRequirement requirement;
	int idFlow;
	List<AllocationNode> path;
	
	//<id of maximal flow, maximal flow>
	Map<Integer, MaximalFlow> maximalFlowsMap = new HashMap<>();
	
	public FlowPathMF(SecurityRequirement requirement, List<AllocationNode> path, int idFlow) {
		this.requirement = requirement;
		this.path = path;
		this.idFlow = idFlow;
		
		for(AllocationNode node: path) {
			node.addCrossingFlow(this);
		}
	}


	public SecurityRequirement getRequirement() {
		return requirement;
	}

	public void setRequirement(SecurityRequirement requirement) {
		this.requirement = requirement;
	}

	public int getIdFlow() {
		return idFlow;
	}

	public void setIdFlow(int idFlow) {
		this.idFlow = idFlow;
	}

	public List<AllocationNode> getPath() {
		return path;
	}

	public void setPath(List<AllocationNode> path) {
		this.path = path;
	}

	public void addMaximalFlow(int id, List<Predicate> predicateFlow) {
		MaximalFlow newFlow = new MaximalFlow(id, this, predicateFlow);
		maximalFlowsMap.put(id, newFlow);
	}

	public Map<Integer, MaximalFlow> getMaximalFlowsMap() {
		return maximalFlowsMap;
	}
}
