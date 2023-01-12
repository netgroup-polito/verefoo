package it.polito.verefoo.graph;

import java.util.ArrayList;
import java.util.List;

public class MaximalFlow {
	private int flowId;
	private FlowPath flowPath;
	private List<Predicate> predicateList = new ArrayList<>();
	
	public MaximalFlow(int flowId, FlowPath flowPath, List<Predicate> predicateList) {
		super();
		this.flowId = flowId;
		this.flowPath = flowPath;
		this.predicateList = predicateList;
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

	public List<Predicate> getPredicateList() {
		return predicateList;
	}

	public void setPredicateList(List<Predicate> predicateList) {
		this.predicateList = predicateList;
	}
	
	
}
