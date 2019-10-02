package it.polito.verefoo.graph;

import it.polito.verefoo.jaxb.L4ProtocolTypes;
import it.polito.verefoo.jaxb.PName;
import it.polito.verefoo.jaxb.Property;

public class Traffic {
	
	PName type;
	String IPSrc;
	String IPDst;
	String pSrc;
	String pDst;
	L4ProtocolTypes tProto;
	
	public Traffic(PName type, String iPSrc, String iPDst, String pSrc, String pDst, L4ProtocolTypes tProto) {
		this.type = type;
		IPSrc = iPSrc;
		IPDst = iPDst;
		this.pSrc = pSrc;
		this.pDst = pDst;
		this.tProto = tProto;
	}

	public Traffic() {
	}

	public PName getType() {
		return type;
	}

	public void setType(PName type) {
		this.type = type;
	}

	public String getIPSrc() {
		return IPSrc;
	}

	public void setIPSrc(String iPSrc) {
		IPSrc = iPSrc;
	}

	public String getIPDst() {
		return IPDst;
	}

	public void setIPDst(String iPDst) {
		IPDst = iPDst;
	}

	public String getpSrc() {
		return pSrc;
	}

	public void setpSrc(String pSrc) {
		this.pSrc = pSrc;
	}

	public String getpDst() {
		return pDst;
	}

	public void setpDst(String pDst) {
		this.pDst = pDst;
	}

	public L4ProtocolTypes gettProto() {
		return tProto;
	}

	public void settProto(L4ProtocolTypes tProto) {
		this.tProto = tProto;
	}
	
	
	static public Traffic copyTraffic(Traffic original) {
		Traffic copy = new Traffic();
		copy.setType(original.getType());
		copy.setIPSrc(original.getIPSrc());
		copy.setIPDst(original.getIPDst());
		copy.setpSrc(original.getpSrc());
		copy.setpDst(original.getpDst());
		copy.settProto(original.gettProto());
		return copy;
	}

}
