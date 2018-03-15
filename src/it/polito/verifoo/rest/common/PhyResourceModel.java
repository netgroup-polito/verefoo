package it.polito.verifoo.rest.common;

import it.polito.verifoo.rest.jaxb.Hosts;

public interface PhyResourceModel {
	
	public Hosts getPhysicalTopology();
	public void setPhysicalTopology();
	public void removePhysicalTopology();
	
}
