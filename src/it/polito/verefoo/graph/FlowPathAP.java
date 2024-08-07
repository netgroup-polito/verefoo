package it.polito.verefoo.graph;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.polito.verefoo.allocation.AllocationNodeAP;
import it.polito.verefoo.allocation.AllocationNodeMF;
import it.polito.verefoo.jaxb.*;

/** Represents a Flow Path specific for Atomic Predicates.
*
*
*/
public class FlowPathAP {
	
	SecurityRequirement requirement;
	int idFlow;
	private List<AllocationNodeAP> path;
	//<id of atomic flow, atomic flow>
	Map<Integer, AtomicFlow> atomicFlowsMap = new HashMap<>();
	Map<Integer, AtomicFlow> atomicFlowsToDiscardMap = new HashMap<>();
	
	/**
    * Public constructor of Flow Path specific to Atomic Predicates.
    * @param requirement
    * @param path
    * @param idFlow
    */
	public FlowPathAP(SecurityRequirement requirement, List<AllocationNodeAP> path, int idFlow) {
		this.requirement = requirement;
		this.path = path;
		this.idFlow = idFlow;
		
		for(AllocationNodeAP node: path) {
			node.addCrossingFlow(this);
		}
	}
	/**
	 * Getter method for security requirement
	 * @return the security requirement
	 */
	public SecurityRequirement getRequirement() {
		return requirement;
	}
	/**
	 * Setter method for the security requirement
	 * @param requirement It is the security requirement.
	 */
	public void setRequirement(SecurityRequirement requirement) {
		this.requirement = requirement;
	}

	public int getIdFlow() {
		return idFlow;
	}

	public void setIdFlow(int idFlow) {
		this.idFlow = idFlow;
	}

	public List<AllocationNodeAP> getPath() {
		return path;
	}

	public void setPath(List<AllocationNodeAP> path) {
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
