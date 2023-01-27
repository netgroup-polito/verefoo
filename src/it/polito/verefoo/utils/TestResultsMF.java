package it.polito.verefoo.utils;

public class TestResultsMF extends TestResults {
	private long maximalFlowsCompTime;
	private long startMaxSMTtime;
	private long endMaxSMTtims;
	private int totalNumberGeneratedFlows;
	

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

	
}
