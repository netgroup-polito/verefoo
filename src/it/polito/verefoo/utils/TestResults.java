package it.polito.verefoo.utils;

public class TestResults {
	private long atomicPredCompTime;
	private long atomicFlowsCompTime;
	private long beginMaxSMTTime;
	private String z3Result;
	private long totalFlows;
	
	public TestResults() {	
	}

	public long getAtomicPredCompTime() {
		return atomicPredCompTime;
	}

	public void setAtomicPredCompTime(long atomicPredCompTime) {
		this.atomicPredCompTime = atomicPredCompTime;
	}

	public long getAtomicFlowsCompTime() {
		return atomicFlowsCompTime;
	}

	public void setAtomicFlowsCompTime(long atomicFlowsCompTime) {
		this.atomicFlowsCompTime = atomicFlowsCompTime;
	}

	public long getBeginMaxSMTTime() {
		return beginMaxSMTTime;
	}

	public void setBeginMaxSMTTime(long beginMaxSMTTime) {
		this.beginMaxSMTTime = beginMaxSMTTime;
	}

	public String getZ3Result() {
		return z3Result;
	}

	public void setZ3Result(String z3Result) {
		this.z3Result = z3Result;
	}
	
	public long getTotalFlows() {
		return totalFlows;
	}

	public void setTotalFlows(long totalFlows) {
		this.totalFlows = totalFlows;
	}
}
