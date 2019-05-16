package it.polito.verefoo.tools.medicine;

import it.polito.verefoo.jaxb.*;

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
