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
	
	/**
	 * Getter method for the flow ID
	 * @return the flow ID
	 */
	public int getFlowId() {
		return flowId;
	}
	/**
	 * Setter method for flow ID
	 * @param flowId It is the flow ID integer.
	 */
	public void setFlowId(int flowId) {
		this.flowId = flowId;
	}
	/**
	 * Getter method for the flow path Atomic Predicate
	 * @return the flow Path AP
	 */
	public FlowPathAP getFlowPathAP() { // Atomic Predicate getter
		return flowPathAP;
	}
	/**
	 * Setter method for the path flow Atomic Predicates
	 * @param flowId It is the flow path AP object
	 */
	public void setFlowPath(FlowPathAP flowPath) { // Atomic Predicate setter
		this.flowPathAP = flowPath;
	}
	/**
	 * Getter method for the flow path Maximal Flows
	 * @return the flow path MF
	 */
	public FlowPathMF getFlowPath() { // Maximal Flows getter
		return flowPathMF;
	}
	/**
	 * Setter method for the path flow Maximal Flows
	 * @param flowId It is the flow path MF object
	 */
	public void setFlowPath(FlowPathMF flowPath) { // Maximal Flows setter
		this.flowPathMF = flowPath;
	}
	/**
	 * Getter method for the atomic predicates
	 * @return the list of atomic predicates
	 */
	public List<Integer> getAtomicPredicateList() {
		return atomicPredicateList;
	}
	/**
	 * Setter method for the atomic predicates list
	 * @return atomicPredicateList the integer list of atomic predicates
	 */
	public void setAtomicPredicateList(List<Integer> atomicPredicateList) {
		this.atomicPredicateList = atomicPredicateList;
	}
	
	
}
