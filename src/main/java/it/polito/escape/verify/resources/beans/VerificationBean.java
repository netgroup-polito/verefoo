package it.polito.escape.verify.resources.beans;

import javax.ws.rs.QueryParam;

public class VerificationBean {
	private @QueryParam("source") String source;
	private @QueryParam("destination") String destination;
	
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getDestination() {
		return destination;
	}
	public void setDestination(String destination) {
		this.destination = destination;
	}
	
	
}
