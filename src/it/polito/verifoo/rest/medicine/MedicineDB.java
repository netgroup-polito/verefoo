package it.polito.verifoo.rest.medicine;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MedicineDB {

	HashMap<Integer,MedicineSimulator> simulations = new HashMap<>();
	AtomicInteger id = new AtomicInteger(-1);
	
	
	public int addSimulation(MedicineSimulator s){
		int newId = id.incrementAndGet();
		simulations.put(newId, s);
		return newId;
	}
	
	public MedicineSimulator getSimulation(int requestId){
		return simulations.get(requestId);
	}
	
	public void removeSimulation(int requestId) {
		MedicineSimulator s = simulations.remove(requestId);
		if(s == null) return;
		s.stopSimulation();
	}
}
