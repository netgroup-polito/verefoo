package it.polito.verefoo.graph;

import java.util.HashMap;
import java.util.Map;

import it.polito.verefoo.jaxb.*;

public class TrafficFlow {

	Property originalProperty;
	RequirementPath path;
	int idRequirement;
	Map<String, Property> functionFlowMap;
	
	/**
	 * Constructor class of the TrafficFlow class
	 * It is an extension of the JAXB-annotated Property class
	 * @param property it is the JAXB property object
	 * @param path it is the path of nodes related to the property
	 * @param idRequirement
	 * it is the id of the requirement in VEREFOO
	 */
	public TrafficFlow(Property property, RequirementPath path, int idRequirement) {
		super();
		this.originalProperty = property;
		this.path = path;
		this.idRequirement = idRequirement;
		this.originalProperty.setSrcPort((property == null || property.getSrcPort() == null) ? "null":property.getSrcPort());
		this.originalProperty.setDstPort((property == null || property.getDstPort() == null) ? "null":property.getDstPort());
		this.originalProperty.setLv4Proto((property == null || property.getLv4Proto() == null) ? L4ProtocolTypes.ANY:property.getLv4Proto());
		this.functionFlowMap = new HashMap<String, Property>();
	}

	
	/**
	 * Getter method for the property
	 * @return the property
	 */
	public Property getProperty() {
		return originalProperty;
	}

	/**
	 * Setter method for the property
	 * @param property it is the property
	 */
	public void setProperty(Property property) {
		this.originalProperty = property;
	}

	/**
	 * Getter method for the path of nodes
	 * @return the path of nodes
	 */
	public RequirementPath getPath() {
		return path;
	}

	/**
	 * Setter method for the path of nodes
	 * @param path it is the path of nodes
	 */
	public void setPath(RequirementPath path) {
		this.path = path;
	}

	/**
	 * Getter method for the id
	 * @return the id of the requirement
	 */
	public int getIdRequirement() {
		return idRequirement;
	}

	/**
	 * Setter method for the id
	 * @param idRequirement it is the id of the requirement
	 */
	public void setIdRequirement(int idRequirement) {
		this.idRequirement = idRequirement;
	}
	
	
	static public Property copyProperty(Property original) {
		Property copy = new Property();
		copy.setName(original.getName());
		copy.setGraph(original.getGraph());
		copy.setSrc(original.getSrc());
		copy.setDst(original.getDst());
		copy.setSrcPort(original.getSrcPort());
		copy.setDstPort(original.getDstPort());
		return copy;
	}


	public void addModifiedProperty(String name, Property p) {
		functionFlowMap.put(name, p);

	}
	
	public Property getCrossedTrafficFlow(String name) {
		if(functionFlowMap.containsKey(name)) 
			return functionFlowMap.get(name);
		else
			return originalProperty;
	}
	
	
	
	
}
