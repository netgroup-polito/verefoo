package it.polito.verefoo.utils;

public class TestResultsMF {
	private long maximalFlowsCompTime;
	private long startMaxSMTtime;
	private long endMaxSMTtims;
	private int totalNumberGeneratedFlows;
	
	private String z3Result;

	public TestResultsMF() {	
	}

	public long getMaximalFlowsCompTime() {
		return maximalFlowsCompTime;
	}

	public void setMaximalFlowsCompTime(long maximalFlowsCompTime) {
		this.maximalFlowsCompTime = maximalFlowsCompTime;
	}

	public long getStartMaxSMTtime() {
		return startMaxSMTtime;
	}

	public void setStartMaxSMTtime(long startMaxSMTtime) {
		this.startMaxSMTtime = startMaxSMTtime;
	}

	public long getEndMaxSMTtims() {
		return endMaxSMTtims;
	}

	public void setEndMaxSMTtims(long endMaxSMTtims) {
		this.endMaxSMTtims = endMaxSMTtims;
	}

	public int getTotalNumberGeneratedFlows() {
		return totalNumberGeneratedFlows;
	}

	public void setTotalNumberGeneratedFlows(int totalNumberGeneratedFlows) {
		this.totalNumberGeneratedFlows = totalNumberGeneratedFlows;
	}

	public String getZ3Result() {
		return z3Result;
	}

	public void setZ3Result(String z3Result) {
		this.z3Result = z3Result;
	}

	
}
