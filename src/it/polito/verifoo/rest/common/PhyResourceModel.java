package it.polito.verifoo.rest.common;

import it.polito.verifoo.rest.jaxb.Hosts;
/**
 * Interface to store and retrieve topological information.
 * @author Antonio
 *
 */
public interface PhyResourceModel {
	
	public Hosts getPhysicalTopology() throws ResourceModelException;
	public void setPhysicalTopology() throws ResourceModelException;
	public void removePhysicalTopology() throws ResourceModelException;
	
}
