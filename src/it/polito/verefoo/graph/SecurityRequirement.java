package it.polito.verefoo.graph;

import java.util.HashMap;
import java.util.Map;

import it.polito.verefoo.jaxb.Property;

public class SecurityRequirement {
	
	Property originalProperty;
	int idRequirement;
	Map<String, Traffic> nodeTrafficMap;
	Map<Integer, Flow> flowsMap;

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
