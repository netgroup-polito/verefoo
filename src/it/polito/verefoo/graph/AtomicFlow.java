package it.polito.verefoo.graph;

import java.util.ArrayList;
import java.util.List;

public class AtomicFlow {
	private int flowId;
	private FlowPath flowPath;
	private List<Integer> atomicPredicateList = new ArrayList<>();
	
	public AtomicFlow(int flowId, FlowPath flowPath, List<Integer> atomicPredicateList) {
		super();
		this.flowId = flowId;
		this.flowPath = flowPath;
		this.atomicPredicateList = atomicPredicateList;
	}

	public int getFlowId() {
		return flowId;
	}

	public void setFlowId(int flowId) {
		this.flowId = flowId;
	}

	public FlowPath getFlowPath() {
		return flowPath;
	}

	public void setFlowPath(FlowPath flowPath) {
		this.flowPath = flowPath;
	}

	public List<Integer> getAtomicPredicateList() {
		return atomicPredicateList;
	}

	public void setAtomicPredicateList(List<Integer> atomicPredicateList) {
		this.atomicPredicateList = atomicPredicateList;
	}
	
	
}
