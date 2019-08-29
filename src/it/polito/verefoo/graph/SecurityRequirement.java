package it.polito.verefoo.graph;

import it.polito.verefoo.jaxb.*;

public class SecurityRequirement {

	Property property;
	RequirementPath path;
	int idRequirement;
	
	/**
	 * Constructor class of the SecurityRequirement class
	 * It is an extension of the JAXB-annotated Property class
	 * @param property it is the JAXB property object
	 * @param path it is the path of nodes related to the property
	 * @param idRequirement
	 * it is the id of the requirement in VEREFOO
	 */
	public SecurityRequirement(Property property, RequirementPath path, int idRequirement) {
		super();
		this.property = property;
		this.path = path;
		this.idRequirement = idRequirement;
		this.property.setSrcPort((property == null || property.getSrcPort() == null) ? "null":property.getSrcPort());
		this.property.setDstPort((property == null || property.getDstPort() == null) ? "null":property.getDstPort());
		this.property.setLv4Proto((property == null || property.getLv4Proto() == null) ? L4ProtocolTypes.ANY:property.getLv4Proto());
	}

	
	/**
	 * Getter method for the property
	 * @return the property
	 */
	public Property getProperty() {
		return property;
	}

	/**
	 * Setter method for the property
	 * @param property it is the property
	 */
	public void setProperty(Property property) {
		this.property = property;
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
	
	
	
	
}
