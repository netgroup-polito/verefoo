package it.polito.verifoo.rest.common;

import it.polito.verifoo.rest.jaxb.Hosts;
/**
 * Interface for the physical resource model (i.e. how a host is characterized). 
 * All the classes that want to provide some way to store
 * this kind of information, must implement this interface.  
 * @author Antonio
 *
 */
public interface PhyResourceModel {
	
	public Hosts getPhysicalTopology();
	public void setPhysicalTopology();
	public void removePhysicalTopology();
	
}
