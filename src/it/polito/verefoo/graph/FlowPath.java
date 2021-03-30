package it.polito.verefoo.graph;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.polito.verefoo.allocation.AllocationNode;
import it.polito.verefoo.jaxb.*;

public class FlowPath {
	SecurityRequirement requirement;
	int idFlow;
	private List<AllocationNode> path;
	//<id of atomic flow, atomic flow>
	Map<Integer, AtomicFlow> atomicFlowsMap = new HashMap<>();
	Map<Integer, AtomicFlow> atomicFlowsToDiscardMap = new HashMap<>();
	
	public FlowPath(SecurityRequirement requirement, List<AllocationNode> path, int idFlow) {
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
	
	public void addAtomicFlow(int id, List<Integer> atomicFlow) {
		AtomicFlow newFlow = new AtomicFlow(id, this, atomicFlow);
		atomicFlowsMap.put(id, newFlow);
	}
	
	public void addAtomicFlowToDiscard(int id, List<Integer> atomicFlow) {
		AtomicFlow newFlow = new AtomicFlow(id, this, atomicFlow);
		atomicFlowsToDiscardMap.put(id, newFlow);
		
	}

	public Map<Integer, AtomicFlow> getAtomicFlowsMap() {
		return atomicFlowsMap;
	}

	public Map<Integer, AtomicFlow> getAtomicFlowsToDiscardMap() {
		return atomicFlowsToDiscardMap;
	}
	
	
}
