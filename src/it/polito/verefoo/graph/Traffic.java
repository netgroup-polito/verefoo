package it.polito.verefoo.graph;

import it.polito.verefoo.jaxb.L4ProtocolTypes;
import it.polito.verefoo.jaxb.PName;
import it.polito.verefoo.jaxb.Property;

public class Traffic {
	
	//type of the requirement related to the traffic
	PName type;
	
	//IP 5-tuple - packet filtering level
	String IPSrc;
	String IPDst;
	String pSrc;
	String pDst;
	L4ProtocolTypes tProto;
	
	//Web-application level
	String url;
	String domain;
	
	//body
	String body;
	
	
	public Traffic(PName type, String iPSrc, String iPDst) {
		this.type = type;
		IPSrc = iPSrc;
		IPDst = iPDst;
		this.pSrc = "null";
		this.pDst = "null";
		this.tProto = L4ProtocolTypes.ANY;
		this.url = "null";
		this.domain = "null";
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
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	static public Traffic copyTraffic(Traffic original) {
		Traffic copy = new Traffic();
		copy.setType(original.getType());
		copy.setIPSrc(original.getIPSrc());
		copy.setIPDst(original.getIPDst());
		copy.setpSrc(original.getpSrc());
		copy.setpDst(original.getpDst());
		copy.settProto(original.gettProto());
		copy.setBody(original.getBody());
		copy.setDomain(original.getDomain());
		copy.setUrl(original.getUrl());
		return copy;
	}

}
