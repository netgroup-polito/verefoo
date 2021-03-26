package it.polito.verefoo.graph;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.polito.verefoo.allocation.AllocationNode;
import it.polito.verefoo.jaxb.*;

public class Flow {
	SecurityRequirement requirement;
	int idFlow;
	private List<AllocationNode> path;
	//<id of atomic flow, atomic flow>
	Map<Integer, List<Integer>> atomicFlowsMap = new HashMap<>();
	Map<Integer, List<Integer>> atomicFlowsToDiscardMap = new HashMap<>();
	
	public Flow(SecurityRequirement requirement, List<AllocationNode> path, int idFlow) {
		this.requirement = requirement;
		this.path = path;
		this.idFlow = idFlow;
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
	
	public void addAtomicFlow(int id, List<Integer> atomicFlow) {
		this.atomicFlowsMap.put(id, atomicFlow);
	}
	
	public void addAtomicFlowToDiscard(int id, List<Integer> atomicFlow) {
		this.atomicFlowsToDiscardMap.put(id, atomicFlow);
	}

	public Map<Integer, List<Integer>> getAtomicFlowsMap() {
		return atomicFlowsMap;
	}

	public Map<Integer, List<Integer>> getAtomicFlowsToDiscardMap() {
		return atomicFlowsToDiscardMap;
	}
	
	
}
