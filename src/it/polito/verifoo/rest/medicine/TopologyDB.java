package it.polito.verifoo.rest.medicine;

import it.polito.verifoo.rest.common.PhyResourceModel;

public class TopologyDB {
	private static TopologyDB db = null;
	private PhyResourceModel simulation = null;
	private TopologyDB() {}
	// Metodo della classe impiegato per accedere al singleton
    public static synchronized TopologyDB getMedicineDB() {
        if (db == null) {
            db = new TopologyDB();
        }
        return db;
    }
	public void setSimulation(PhyResourceModel s){
		System.out.println("New simulation created");
		simulation = s;
		return;
	}
	
	public PhyResourceModel getSimulation(){
		return simulation;
	}
	
	public void removeSimulation() {
		simulation.removePhysicalTopology();
	}
}
