package it.polito.verefoo.graph;

import java.util.ArrayList;
import java.util.List;

import it.polito.verefoo.utils.APUtils;


public class SimplePredicate {
	List<String> IPSrcList = new ArrayList<>();		//in AND
	boolean negIPSrc;
	List<String> IPDstList = new ArrayList<>();
	boolean negIPDst;
	List<String> pSrcList = new ArrayList<>();
	boolean negPSrc;
	List<String> pDstList = new ArrayList<>();
	boolean negPDst;
	APUtils aputils;
	
	public SimplePredicate(APUtils aputils, String IPSrc, boolean negIPSrc, String IPDst, boolean negIPDst,
			String pSrc, boolean negPSrc, String pDst, boolean negPDst) {
		super();
		IPSrcList.add(IPSrc);
		this.negIPSrc = negIPSrc;
		IPDstList.add(IPDst);
		this.negIPDst = negIPDst;
		pSrcList.add(pSrc);
		this.negPSrc = negPSrc;
		pDstList.add(pDst);
		this.negPDst = negPDst;
		this.aputils = aputils;
	}
	
	public SimplePredicate() {
		
	}	

	public List<String> getIPSrcList() {
		return IPSrcList;
	}

	public void setIPSrcList(List<String> iPSrcList) {
		IPSrcList = iPSrcList;
	}

	public boolean isNegIPSrc() {
		return negIPSrc;
	}

	public void setNegIPSrc(boolean negIPSrc) {
		this.negIPSrc = negIPSrc;
	}

	public List<String> getIPDstList() {
		return IPDstList;
	}

	public void setIPDstList(List<String> iPDstList) {
		IPDstList = iPDstList;
	}

	public boolean isNegIPDst() {
		return negIPDst;
	}

	public void setNegIPDst(boolean negIPDst) {
		this.negIPDst = negIPDst;
	}

	public List<String> getpSrcList() {
		return pSrcList;
	}

	public void setpSrcList(List<String> pSrcList) {
		this.pSrcList = pSrcList;
	}

	public boolean isNegPSrc() {
		return negPSrc;
	}

	public void setNegPSrc(boolean negPSrc) {
		this.negPSrc = negPSrc;
	}

	public List<String> getpDstList() {
		return pDstList;
	}

	public void setpDstList(List<String> pDstList) {
		this.pDstList = pDstList;
	}

	public boolean isNegPDst() {
		return negPDst;
	}

	public void setNegPDst(boolean negPDst) {
		this.negPDst = negPDst;
	}

	@Override
	public String toString() {
		
		System.out.print(": {");
		int i=0;
		for(String IPSrc: IPSrcList) {
			if(i!=0) System.out.print("AND");
			if(negIPSrc) System.out.print("!");
			System.out.print(IPSrc);
			i++;
		}
		i=0;
		System.out.print(", ");
		for(String pSrc: pSrcList) {
			if(i!=0) System.out.print("AND");
			if(negPSrc) System.out.print("!");
			System.out.print(pSrc);
			i++;
		}
		i=0;
		System.out.print(", ");
		for(String IPDst: IPDstList) {
			if(i!=0) System.out.print("AND");
			if(negIPDst) System.out.print("!");
			System.out.print(IPDst);
			i++;
		}
		i=0;
		System.out.print(", ");
		for(String pDst: pDstList) {
			if(i!=0) System.out.print("AND");
			if(negPDst) System.out.print("!");
			System.out.print(pDst);
			i++;
		}
		System.out.print("}");
		return  "";
		
//		return "SimplePredicate [IPSrcList=" + IPSrcList + ", negIPSrc=" + negIPSrc + ", IPDstList=" + IPDstList
//				+ ", negIPDst=" + negIPDst + ", pSrcList=" + pSrcList + ", negPSrc=" + negPSrc + ", pDstList="
//				+ pDstList + ", negPDst=" + negPDst + "]";
	}
	
	
	
	
}
