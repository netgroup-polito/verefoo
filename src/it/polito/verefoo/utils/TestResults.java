package it.polito.verefoo.utils;

public class TestResults {
	private long atomicPredCompTime;
	private long atomicFlowsCompTime;
	private long fillMapTime;
	private long genPathTime;
	private int nAtomicPredicates;
	
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

	public long getFillMapTime() {
		return fillMapTime;
	}

	public void setFillMapTime(long fillMapTime) {
		this.fillMapTime = fillMapTime;
	}

	public long getGenPathTime() {
		return genPathTime;
	}

	public void setGenPathTime(long genPathTime) {
		this.genPathTime = genPathTime;
	}

	public int getnAtomicPredicates() {
		return nAtomicPredicates;
	}

	public void setnAtomicPredicates(int nAtomicPredicates) {
		this.nAtomicPredicates = nAtomicPredicates;
	}

}
