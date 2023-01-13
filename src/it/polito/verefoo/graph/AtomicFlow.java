package it.polito.verefoo.graph;

import java.util.ArrayList;
import java.util.List;

public class AtomicFlow {
	private int flowId;
	private FlowPathAP flowPathAP;
	private FlowPathMF flowPathMF;
	private List<Integer> atomicPredicateList = new ArrayList<>();
	
	public AtomicFlow(int flowId, FlowPathAP flowPath, List<Integer> atomicPredicateList) {
		super();
		this.flowId = flowId;
		this.flowPathAP = flowPath;
		this.atomicPredicateList = atomicPredicateList;
	}
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

	public FlowPathAP getFlowPathAP() {
		return flowPathAP;
	}

	public void setFlowPath(FlowPathAP flowPath) {
		this.flowPathAP = flowPath;
	}
	
	public FlowPathMF getFlowPath() {
		return flowPathMF;
	}

	public void setFlowPath(FlowPathMF flowPath) {
		this.flowPathMF = flowPath;
	}
	
	public List<Integer> getAtomicPredicateList() {
		return atomicPredicateList;
	}

	public void setAtomicPredicateList(List<Integer> atomicPredicateList) {
		this.atomicPredicateList = atomicPredicateList;
	}
	
	
}
