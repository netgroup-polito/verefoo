package it.polito.verefoo.utils;

 public class TestResults {

	private String z3Result;
	private long atomicPredCompTime;
	private long atomicFlowsCompTime;
	private long beginMaxSMTTime;
	private long totalFlows;
	private long maximalFlowsCompTime;
	private long startMaxSMTtime;
	private long endMaxSMTtims;
	private int totalNumberGeneratedFlows;
	
	public TestResults() {	
	}

	/*******************************************************************ATOMIC PREDICATE***************************************************************************/
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
	
	public long getTotalFlows() {
		return totalFlows;
	}

	public void setTotalFlows(long totalFlows) {
		this.totalFlows = totalFlows;
	}
	
/******************************************************************MAXIMAL FLOWS*******************************************************************************/
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
