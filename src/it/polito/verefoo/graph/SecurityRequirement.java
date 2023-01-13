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
	Map<Integer, FlowPathAP> flowsMapAP;
	Map<Integer, FlowPathMF> flowsMapMF;
	
	public SecurityRequirement(Property originalProperty, int idRequirement) {
		this.originalProperty = originalProperty;
		this.idRequirement = idRequirement;
		nodeTrafficMap = new HashMap<>();
		flowsMapAP = new HashMap<>();
		flowsMapMF = new HashMap<>();
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

	public Map<Integer, FlowPathAP> getFlowsMapAP() {
		return flowsMapAP;
	}

	public void setFlowsMapAP(Map<Integer, FlowPathAP> flowsMap) {
		this.flowsMapAP = flowsMap;
	}
	
	public Map<Integer, FlowPathMF> getFlowsMapMF() {
		return flowsMapMF;
	}

	public void setFlowsMapMF(Map<Integer, FlowPathMF> flowsMap) {
		this.flowsMapMF = flowsMap;
	}
	
}
