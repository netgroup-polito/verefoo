package it.polito.verifoo.rest.medicine;

import it.polito.verifoo.rest.common.PhyResourceModel;
import it.polito.verifoo.rest.common.ResourceModelException;
import it.polito.verifoo.rest.jaxb.Hosts;
/**
 * Singleton to store the information about the physical topology. At the moment it handles only one 
 * topology at a time
 * @author Antonio
 *
 */
public class TopologyDB {
	private static TopologyDB db = null;
	private PhyResourceModel model = null;
	private TopologyDB() {}
	/**
	 * Retrieve the topology database needed to interact with this class
	 */
    public static synchronized TopologyDB getMedicineDB() {
        if (db == null) {
            db = new TopologyDB();
        }
        return db;
    }
    /**
     * Add a new resource model to the database 
     * @param s the new resource model
     */
	public void setResourceModel(PhyResourceModel m){
		System.out.println("New simulation created");
		model = m;
		return;
	}
	/**
	 * Get the hosts in the topology
	 * @throws ResourceModelException 
	 */
	public Hosts getResourceModel() throws ResourceModelException{
		if(model == null) return null;
		return model.getPhysicalTopology();
	}
	/**
	 * Remove the resource model from the database
	 * @throws ResourceModelException
	 */
	public void removeResourceModel() throws ResourceModelException {
		model.removePhysicalTopology();
	}
}
