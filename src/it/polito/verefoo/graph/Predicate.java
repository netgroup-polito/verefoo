package it.polito.verefoo.graph;

import java.util.ArrayList;
import java.util.List;

import it.polito.verefoo.jaxb.L4ProtocolTypes;


/**
 * This class represents a Predicate used to simultaneously model the five main fields of
 * the IP header, that are represented
 * by the quintuple {source IP, destination IP, source port, destination port,
 * protocol type}.
 *
 */
public class Predicate {
	List<IPAddress> IPSrcList = new ArrayList<>();  //in AND
	List<IPAddress> IPDstList = new ArrayList<>(); //in AND
	List<PortInterval> pSrcList = new ArrayList<>(); //in AND
	List<PortInterval> pDstList = new ArrayList<>(); //in AND
	List<L4ProtocolTypes> protoTypeList = new ArrayList<>(); //in OR
	
	public Predicate() {
	}
	
	/**
	 * This constructor simply populates the predicate of "this".
	 * 
	 * @param IPSrc    Is the IP source address
	 * @param negIPSrc Is the negation status of IP source address
	 * @param IPDst    Is the IP destination address
	 * @param negIPDst Is the negation status of IP destination address
	 * @param pSrc     Is the source port interval
	 * @param negPSrc  Is the negation status of source port interval
	 * @param pDst     Is the destination port interval
	 * @param negPDst  Is the negation status of destination port interval
	 * @param proto    Is the L4 protocol type used {TCP,UDP,ANY,OTHERS}
	 */
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
	
	/**
	 * This copy constructor simply populates the predicate of "this" from another
	 * predicate instance.
	 * 
	 * @param toCopy Is the a predicate instance to be copied
	 */
	public Predicate(Predicate toCopy) {
		IPSrcList = new ArrayList<>(toCopy.getIPSrcList());
		IPDstList = new ArrayList<>(toCopy.getIPDstList());
		pSrcList = new ArrayList<>(toCopy.getpSrcList());
		pDstList = new ArrayList<>(toCopy.getpDstList());
		protoTypeList = new ArrayList<>(toCopy.getProtoTypeList());
	}
	
	/**
	 * Getter method for the source IP address
	 * @return the source IP address
	 */
	public List<IPAddress> getIPSrcList() {
		return IPSrcList;
	}
	/**
	 * Setter method for the source IP address
	 * @param iPSrcList Is the source IP address
	 */
	public void setIPSrcList(List<IPAddress> iPSrcList) {
		IPSrcList = iPSrcList;
	}
	/**
	 * Getter method for the destination IP address
	 * @return the destination IP address
	 */
	public List<IPAddress> getIPDstList() {
		return IPDstList;
	}
	/**
	 * Setter method for the destination IP address
	 * @param iPDstList Is the destination IP address
	 */
	public void setIPDstList(List<IPAddress> iPDstList) {
		IPDstList = iPDstList;
	}
	/**
	 * Getter method for the source port interval
	 * @return the source port interval
	 */
	public List<PortInterval> getpSrcList() {
		return pSrcList;
	}
	/**
	 * Setter method for the source port interval
	 * @param pSrcList Is the source port interval
	 */
	public void setpSrcList(List<PortInterval> pSrcList) {
		this.pSrcList = pSrcList;
	}
	/**
	 * Getter method for the destination port interval
	 * @return the destination port interval
	 */
	public List<PortInterval> getpDstList() {
		return pDstList;
	}
	/**
	 * Setter method for the destination port interval
	 * @param pDstList Is the destination port interval
	 */
	public void setpDstList(List<PortInterval> pDstList) {
		this.pDstList = pDstList;
	}
	/**
	 * Getter method for the protocol type
	 * @return the protocol type
	 */
	public List<L4ProtocolTypes> getProtoTypeList() {
		return protoTypeList;
	}
	/**
	 * Setter method for the protocol type
	 * @param protoTypeList Is the L4 protocol type
	 */
	public void setProtoTypeList(List<L4ProtocolTypes> protoTypeList) {
		this.protoTypeList = protoTypeList;
	}
	/**
	 * Getter method for the source IP address list size
	 * @return the source IP address list size
	 */
	public int getIPSrcListSize() {
		return IPSrcList.size();
	}
	/**
	 * Getter method for the destination IP address list size
	 * @return the destination IP address list size
	 */
	public int getIPDstListSize() {
		return IPDstList.size();
	}
	
	/**
	 * Checks if "this" has source IP equal or included in the parameter 'ipList'.
	 * IPSrcList of "this" should be single not a negation, the check sould be done before calling
	 * the method.
	 * 
	 * @param ipList Contains IP address
	 * @return True if "this" source IP is included or equal to 'ipList', False
	 *         otherwise.
	 */
	public boolean hasIPSrcEqualOrIncludedIn(List<IPAddress> ipList) {
		IPAddress IPSrc = IPSrcList.get(0);
		if(IPSrc == null || IPSrc.isNeg()) return false;
		
		for(IPAddress ip: ipList) {
			if(IPSrc.isIncludedIn(ip))
				return true;
		}
		return false;
	}

	/**
	 * Checks if "this" has destination IP equal or included in the parameter
	 * 'ipList'.
	 * IPDstList of "this" should be single not a negation, the check should be done before calling
	 * the method.
	 * 
	 * @param ipList Contains IP address
	 * @return True if "this" destination IP is included or equal to 'ipList', False
	 *         otherwise.
	 */
	public boolean hasIPDstEqualOrIncludedIn(List<IPAddress> ipList) {
		IPAddress IPDst = IPDstList.get(0);
		if(IPDst == null || IPDst.isNeg()) return false;
		for(IPAddress ip: ipList) {
			if(IPDst.isIncludedIn(ip))
				return true;
		}
		return false;
	}
	
	/**
	 * Checks if "this" has source IP that is not included in the parameter 'ipList'.
	 * IPSrcList of "this" should be single not a negation, the check sould be done before calling
	 * the method.
	 * 
	 * @param ipList Contains IP address
	 * @return True if "this" source IP is not included in 'ipList', False
	 *         otherwise.
	 */
	public boolean hasIPSrcNotIncludedIn(List<IPAddress> ipList) {
		IPAddress IPSrc = IPSrcList.get(0);
		if(IPSrc == null || IPSrc.isNeg()) return false;
		for(IPAddress ip: ipList) {
			if(IPSrc.isIncludedIn(ip))
				return false;
		}
		return true;
	}
	
	/**
	 * Checks if "this" has destination IP that is not included in the parameter 'ipList'.
	 * IPDstList of "this" should be single not a negation, the check sould be done before calling
	 * the method.
	 * 
	 * @param ipList Contains IP address
	 * @return True if "this" destination IP is not included in 'ipList', False
	 *         otherwise.
	 */
	public boolean hasIPDstNotIncludedIn(List<IPAddress> ipList) {
		IPAddress IPDst = IPDstList.get(0);
		if(IPDst == null || IPDst.isNeg()) return false;
		for(IPAddress ip: ipList) {
			if(IPDst.isIncludedIn(ip))
				return false;
		}
		return true;
	}
	
	/**
	 * Checks if "this" has source IP that is equal to parameter 'ip'.
	 * IPSrcList of "this" should be single not a negation, the check sould be done before calling
	 * the method.
	 * 
	 * @param ip Contains IP address
	 * @return True if "this" source IP is equal to 'ip', False
	 *         otherwise.
	 */
	public boolean hasIPSrcEqual(IPAddress ip) {
		IPAddress IPSrc = IPSrcList.get(0);
		if(IPSrc == null || IPSrc.isNeg()) return false;
		return IPSrc.equals(ip);
	}
	
	/**
	 * Checks if "this" has destination IP that is equal to parameter 'ip'.
	 * IPDstList of "this" should be single not a negation, the check sould be done before calling
	 * the method.
	 * 
	 * @param ipList Contains IP address
	 * @return True if "this" destination IP is equal to 'ip', False
	 *         otherwise.
	 */
	public boolean hasIPDstEqual(IPAddress ip) {
		IPAddress IPDst = IPDstList.get(0);
		if(IPDst == null || IPDst.isNeg()) return false;
		return IPDst.equals(ip);
	}

	/**
	 * Get the fist IPAddress from the IPSrc list and convert it to string
	 * 
	 * @return the first IPAddress from the IPSrc list
	 */
	public String firstIPSrcToString() {
		IPAddress IPSrc = IPSrcList.get(0);
		return IPSrc.toString();
	}
	
	public String firstPSrcToString() {
		PortInterval pSrc = pSrcList.get(0);
		return pSrc.toString();
	}
	
	public String firstPDstToString() {
		PortInterval pDst = pDstList.get(0);
		return pDst.toString();
	}
	
	public L4ProtocolTypes firstProto() {
		return protoTypeList.get(0);
	}
	
	public IPAddress firstIPSrc() {
		IPAddress IPSrc = IPSrcList.get(0);
		return IPSrc;
	}
	
	public IPAddress firstIPDst() {
		IPAddress IPDst = IPDstList.get(0);
		return IPDst;
	}

	/**
	 * Get the fist IPAddress from the IPDst list and convert it to string
	 * 
	 * @return the first IPAddress from the IPDst list
	 */
	public String firstIPDstToString() {
		IPAddress IPDst = IPDstList.get(0);
		return IPDst.toString();
	}
	/**
	 * Checks if IPDst List contains only negations
	 * 
	 * @return True if IPDst List contains only IP negations, False otherwise.
	 */
	public boolean hasIPDstOnlyNegs() {
		for(IPAddress ip: IPDstList) {
			if(!ip.isNeg() && !ip.toString().equals("*"))
				return false;
		}
		return true;
	}
	/**
	 * Checks if IPSrc List contains only negations
	 * 
	 * @return True if IPSrc List contains only IP negations, False otherwise.
	 */
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
