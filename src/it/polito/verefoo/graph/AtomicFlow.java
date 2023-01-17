package it.polito.verefoo.graph;

import java.util.ArrayList;
import java.util.List;

/** Represents the Atomic flow.
*
*
*/
public class AtomicFlow {
	private int flowId;
	private FlowPathAP flowPathAP; // for Atomic Predicates
	private FlowPathMF flowPathMF; // for Maximal Flows
	private List<Integer> atomicPredicateList = new ArrayList<>();
	
	 /**
     * Public constructor of Atomic Flow specific to Atomic Predicates.
     * @param flowId
     * @param flowPath
     * @param atomicPredicateList
     */
	public AtomicFlow(int flowId, FlowPathAP flowPath, List<Integer> atomicPredicateList) {
		super();
		this.flowId = flowId;
		this.flowPathAP = flowPath;
		this.atomicPredicateList = atomicPredicateList;
	}
	 /**
     * Public constructor of Atomic Flow specific to Maximal Flows.
     * @param flowId
     * @param flowPath
     * @param atomicPredicateList
     */
	public AtomicFlow(int flowId, FlowPathMF flowPath, List<Integer> atomicPredicateList) {
		super();
		this.flowId = flowId;
		this.flowPathMF = flowPath;
		this.atomicPredicateList = atomicPredicateList;
	}
	
	public int getFlowId() {
		return flowId;
	}

	public void setFlowId(int flowId) {
		this.flowId = flowId;
	}

	public FlowPathAP getFlowPathAP() { // Atomic Predicate getter
		return flowPathAP;
	}

	public void setFlowPath(FlowPathAP flowPath) { // Atomic Predicate setter
		this.flowPathAP = flowPath;
	}
	
	public FlowPathMF getFlowPath() { // Maximal Flows getter
		return flowPathMF;
	}

	public void setFlowPath(FlowPathMF flowPath) { // Maximal Flows setter
		this.flowPathMF = flowPath;
	}
	
	public List<Integer> getAtomicPredicateList() {
		return atomicPredicateList;
	}

	public void setAtomicPredicateList(List<Integer> atomicPredicateList) {
		this.atomicPredicateList = atomicPredicateList;
	}
	
	
}
