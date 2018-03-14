package it.polito.verifoo.rest.common;

import it.polito.verifoo.rest.jaxb.Hosts;

public interface ResourceModel {
	
	public Hosts getPhysicalTopology();
	public void setPhysicalTopology();
	
}
