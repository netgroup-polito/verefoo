package it.polito.verefoo.graph;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.polito.verefoo.allocation.AllocationNodeAP;
import it.polito.verefoo.allocation.AllocationNodeMF;
import it.polito.verefoo.jaxb.*;

/** Represents a Flow Path specific for Maximal Flows.
*
*
*/
public class FlowPathMF {

	SecurityRequirement requirement;
	int idFlow;
	List<AllocationNodeMF> path;
	
	//<id of maximal flow, maximal flow>
	Map<Integer, MaximalFlow> maximalFlowsMap = new HashMap<>();
	
	/**
	    * Public constructor of Flow Path specific to Maximal Flows.
	    * @param requirement
	    * @param path
	    * @param idFlow
	    */
	public FlowPathMF(SecurityRequirement requirement, List<AllocationNodeMF> path, int idFlow) {
		this.requirement = requirement;
		this.path = path;
		this.idFlow = idFlow;
		
		for(AllocationNodeMF node: path) {
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

	public List<AllocationNodeMF> getPath() {
		return path;
	}

	public void setPath(List<AllocationNodeMF> path) {
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
