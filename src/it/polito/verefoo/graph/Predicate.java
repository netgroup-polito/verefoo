package it.polito.verefoo.graph;

import java.util.ArrayList;
import java.util.List;

import it.polito.verefoo.jaxb.L4ProtocolTypes;

public class Predicate {
	List<IPAddress> IPSrcList = new ArrayList<>();  //in AND
	List<IPAddress> IPDstList = new ArrayList<>(); //in AND
	List<PortInterval> pSrcList = new ArrayList<>(); //in AND
	List<PortInterval> pDstList = new ArrayList<>(); //in AND
	List<L4ProtocolTypes> protoTypeList = new ArrayList<>(); //in OR
	
	public Predicate() {
	}
	
	public Predicate(String IPSrc, boolean negIPSrc, String IPDst, boolean negIPDst,
			String pSrc, boolean negPSrc, String pDst, boolean negPDst, L4ProtocolTypes proto) {
		IPAddress src = new IPAddress(IPSrc, negIPSrc);
		IPAddress dst = new IPAddress(IPDst, negIPDst);
		PortInterval psrc = new PortInterval(pSrc, negPSrc);
		PortInterval pdst = new PortInterval(pDst, negPDst);
		
		IPSrcList.add(src);
		IPDstList.add(dst);
		pSrcList.add(psrc);
		pDstList.add(pdst);
		protoTypeList.add(proto);
	}
	
	//Copy constructor
	public Predicate(Predicate toCopy) {
		IPSrcList = new ArrayList<>(toCopy.getIPSrcList());
		IPDstList = new ArrayList<>(toCopy.getIPDstList());
		pSrcList = new ArrayList<>(toCopy.getpSrcList());
		pDstList = new ArrayList<>(toCopy.getpDstList());
		protoTypeList = new ArrayList<>(toCopy.getProtoTypeList());
	}
	
	public List<IPAddress> getIPSrcList() {
		return IPSrcList;
	}
	public void setIPSrcList(List<IPAddress> iPSrcList) {
		IPSrcList = iPSrcList;
	}
	public List<IPAddress> getIPDstList() {
		return IPDstList;
	}
	public void setIPDstList(List<IPAddress> iPDstList) {
		IPDstList = iPDstList;
	}
	public List<PortInterval> getpSrcList() {
		return pSrcList;
	}
	public void setpSrcList(List<PortInterval> pSrcList) {
		this.pSrcList = pSrcList;
	}
	public List<PortInterval> getpDstList() {
		return pDstList;
	}
	public void setpDstList(List<PortInterval> pDstList) {
		this.pDstList = pDstList;
	}
	public List<L4ProtocolTypes> getProtoTypeList() {
		return protoTypeList;
	}
	public void setProtoTypeList(List<L4ProtocolTypes> protoTypeList) {
		this.protoTypeList = protoTypeList;
	}
	public int getIPSrcListSize() {
		return IPSrcList.size();
	}
	public int getIPDstListSize() {
		return IPDstList.size();
	}
	
	//IPSrc should be single not neg (the check sould be done before calling the method)
	public boolean hasIPSrcEqualOrIncludedIn(List<IPAddress> ipList) {
		IPAddress IPSrc = IPSrcList.get(0);
		if(IPSrc == null || IPSrc.isNeg()) return false;
		
		for(IPAddress ip: ipList) {
			if(IPSrc.isIncludedIn(ip))
				return true;
		}
		return false;
	}

	//IPDst should be single not neg (the check sould be done before calling the method)
	public boolean hasIPDstEqualOrIncludedIn(List<IPAddress> ipList) {
		IPAddress IPDst = IPDstList.get(0);
		if(IPDst == null || IPDst.isNeg()) return false;
		for(IPAddress ip: ipList) {
			if(IPDst.isIncludedIn(ip))
				return true;
		}
		return false;
	}
	
	//IPSrc should be single not neg
	public boolean hasIPSrcNotIncludedIn(List<IPAddress> ipList) {
		IPAddress IPSrc = IPSrcList.get(0);
		if(IPSrc == null || IPSrc.isNeg()) return false;
		for(IPAddress ip: ipList) {
			if(IPSrc.isIncludedIn(ip))
				return false;
		}
		return true;
	}
	
	//IPDst should be single not neg
	public boolean hasIPDstNotIncludedIn(List<IPAddress> ipList) {
		IPAddress IPDst = IPDstList.get(0);
		if(IPDst == null || IPDst.isNeg()) return false;
		for(IPAddress ip: ipList) {
			if(IPDst.isIncludedIn(ip))
				return false;
		}
		return true;
	}
	
	//IPSrc should be single not neg
	public boolean hasIPSrcEqual(IPAddress ip) {
		IPAddress IPSrc = IPSrcList.get(0);
		if(IPSrc == null || IPSrc.isNeg()) return false;
		return IPSrc.equals(ip);
	}
	
	//IPDst should be single not neg
	public boolean hasIPDstEqual(IPAddress ip) {
		IPAddress IPDst = IPDstList.get(0);
		if(IPDst == null || IPDst.isNeg()) return false;
		return IPDst.equals(ip);
	}

	//Get the fist IPAddress from the IPSrc list and convert it to string
	public String firstIPSrcToString() {
		IPAddress IPSrc = IPSrcList.get(0);
		return IPSrc.toString();
	}
	
	//Get the fist IPAddress from the IPDst list and convert it to string
	public String firstIPDstToString() {
		IPAddress IPDst = IPDstList.get(0);
		return IPDst.toString();
	}
	
	public boolean hasIPDstOnlyNegs() {
		for(IPAddress ip: IPDstList) {
			if(!ip.isNeg() && !ip.toString().equals("*"))
				return false;
		}
		return true;
	}
	
	public boolean hasIPSrcOnlyNegs() {
		for(IPAddress ip: IPSrcList) {
			if(!ip.isNeg() && !ip.toString().equals("*"))
				return false;
		}
		return true;
	}
	
	//Just for DEBUG
	public void print() {
		System.out.print(": {");
		int i=0;
		for(IPAddress IPSrc: IPSrcList) {
			if(IPSrcList.size() > 1 && IPSrc.toString().equals("*")) continue;
			if(i!=0) System.out.print("AND");
			if(IPSrc.isNeg()) System.out.print("!");
			System.out.print(IPSrc.toString());
			i++;
		}
		i=0;
		System.out.print(", ");
		for(PortInterval pSrc: pSrcList) {
			if(pSrcList.size() > 1 && pSrc.toString().equals("*")) continue;
			if(i!=0) System.out.print("AND");
			if(pSrc.isNeg()) System.out.print("!");
			System.out.print(pSrc.toString());
			i++;
		}
		i=0;
		System.out.print(", ");
		for(IPAddress IPDst: IPDstList) {
			if(IPDstList.size() > 1 && IPDst.toString().equals("*")) continue;
			if(i!=0) System.out.print("AND");
			if(IPDst.isNeg()) System.out.print("!");
			System.out.print(IPDst.toString());
			i++;
		}
		i=0;
		System.out.print(", ");
		for(PortInterval pDst: pDstList) {
			if(pDstList.size() > 1 && pDst.toString().equals("*")) continue;
			if(i!=0) System.out.print("AND");
			if(pDst.isNeg()) System.out.print("!");
			System.out.print(pDst.toString());
			i++;
		}
		i=0;
		System.out.print(", ");
		for(L4ProtocolTypes proto: protoTypeList) {
			if(i!=0) System.out.print("-");
			System.out.print(proto);
			i++;
		}
		System.out.print("}\n");
	}
}
