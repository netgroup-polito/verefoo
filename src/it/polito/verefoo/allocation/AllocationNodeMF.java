package it.polito.verefoo.allocation;

import java.util.*;

import com.microsoft.z3.DatatypeExpr;
import com.microsoft.z3.Optimize;

import it.polito.verefoo.functions.GenericFunction;
import it.polito.verefoo.graph.FlowPathAP;
import it.polito.verefoo.graph.FlowPathMF;
import it.polito.verefoo.graph.Predicate;
import it.polito.verefoo.jaxb.*;


/*
 * This class is an extension of the JAXB-Annotated Node class, this class is specific for Maximal Flows Algorithm.
 * It has additional features, like maps used to build forwarding rules and information about deployed Network Functions.
 * This class is for maximal flows algorithm
 */

public class AllocationNodeMF extends AllocationNode {

	
	/* Maximal flows */
	List<Predicate> forwardedPredicateList = new ArrayList<>();
	List<Predicate> droppedPredicateList = new ArrayList<>();
	private Map<Integer, FlowPathMF> crossingFlows = new HashMap<>();
	//<flowPathId, <maximalFlowId, predicate>>
	private Map<Integer, Map<Integer, Predicate>> mapFlowIdPredicatesInInput = new HashMap<>();
	
	/**
	 * Public constructor for the AllocationNodeMF class
	 * @param node It is the JAXB Node object.
	 */
	public AllocationNodeMF(Node node) {
		this.node = node;
		placedNF = null;
		typeNF = null;
		ipAddress = node.getName();
	}
	

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AllocationNodeMF other = (AllocationNodeMF) obj;
		if (ipAddress == null) {
			if (other.ipAddress != null)
				return false;
		} else if (!ipAddress.equals(other.ipAddress))
			return false;
		return true;
	}

	/**
	 * Set the Crossing Flows
	 * 
	 * @param the FlowPath for Maximal Flows
	 */
	public void addCrossingFlow(FlowPathMF sr) {
		crossingFlows.put(sr.getIdFlow(), sr);
	}
	
	/**
	 * Get the Crossing Flows that cross this node
	 * 
	 * @return the Map of FlowPath for Maximal Flows with integer mapping
	 */
	public Map<Integer, FlowPathMF> getCrossingFlows() {
		return crossingFlows;
	}

	/**
	 * Setter method for the map of requirements
	 * @param requirements the map of requirements
	 */
	public void setFlows(Map<Integer, FlowPathMF> requirements) {
		this.crossingFlows = requirements;
	}
	
	

	public List<Predicate> getForwardedPredicateList() {
		return forwardedPredicateList;
	}

	public void setForwardedPredicateList(List<Predicate> forwardedPredicateList) {
		this.forwardedPredicateList = forwardedPredicateList;
	}

	public List<Predicate> getDroppedPredicateList() {
		return droppedPredicateList;
	}

	public void setDroppedPredicateList(List<Predicate> droppedPredicateList) {
		this.droppedPredicateList = droppedPredicateList;
	}

	public void addPredicateInInput(int flowPathId, int maximalFlowId, Predicate predicate) {
		if(mapFlowIdPredicatesInInput.containsKey(flowPathId)) {
			Map<Integer, Predicate> predicateMap = mapFlowIdPredicatesInInput.get(flowPathId);
			predicateMap.put(maximalFlowId, predicate);
		} else {
			Map<Integer, Predicate> newMap = new HashMap<>();
			newMap.put(maximalFlowId, predicate);	
			mapFlowIdPredicatesInInput.put(flowPathId, newMap);
		}
	}
	
	public Map<Integer, Predicate> getPredicatesInInputForFlow(int flowId){
		if(mapFlowIdPredicatesInInput.containsKey(flowId))
			return mapFlowIdPredicatesInInput.get(flowId);
		return null;
	}
	
	public void addForwardedPredicate(Predicate predicate) {
		forwardedPredicateList.add(predicate);
	}
	
	public void addDroppedPredicate(Predicate predicate) {
		droppedPredicateList.add(predicate);
	}
}
