package it.polito.verefoo.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.polito.verefoo.jaxb.Property;

public class SecurityRequirement {
	
	Property originalProperty;
	int idRequirement;
	Map<String, Traffic> nodeTrafficMap;
	Map<Integer, Flow> flowsMap;
	
	/* Atomic predicates */
	//Map <flow id, list of related atomic flows> 
	HashMap<Integer, List<List<Integer>>> atomicFlowsMap = new HashMap<>();
	HashMap<Integer, List<List<Integer>>> atomicFlowsToDiscardMap = new HashMap<>();
	
	public void addAtomicFlowsList(int flowId, List<List<Integer>> atomicFlowList, List<List<Integer>> atomicFlowToDiscardList) {
		atomicFlowsMap.put(flowId, atomicFlowList);
		atomicFlowsToDiscardMap.put(flowId, atomicFlowToDiscardList);
	}
	
	public List<List<Integer>> getAtomicFlowsForFlow(int flowId){
		return atomicFlowsMap.get(flowId);
	}
	
	public List<List<Integer>> getAtomicFlowsToDiscardForFlow(int flowId){
		return atomicFlowsToDiscardMap.get(flowId);
	}

	public SecurityRequirement(Property originalProperty, int idRequirement) {
		this.originalProperty = originalProperty;
		this.idRequirement = idRequirement;
		nodeTrafficMap = new HashMap<>();
		flowsMap = new HashMap<>();
	}

	public Property getOriginalProperty() {
		return originalProperty;
	}

	public void setOriginalProperty(Property originalProperty) {
		this.originalProperty = originalProperty;
	}

	public int getIdRequirement() {
		return idRequirement;
	}

	public void setIdRequirement(int idRequirement) {
		this.idRequirement = idRequirement;
	}

	public Map<String, Traffic> getNodeTrafficMap() {
		return nodeTrafficMap;
	}

	public void setNodeTrafficMap(Map<String, Traffic> nodeTrafficMap) {
		this.nodeTrafficMap = nodeTrafficMap;
	}

	public Map<Integer, Flow> getFlowsMap() {
		return flowsMap;
	}

	public void setFlowsMap(Map<Integer, Flow> flowsMap) {
		this.flowsMap = flowsMap;
	}
	
	
}
