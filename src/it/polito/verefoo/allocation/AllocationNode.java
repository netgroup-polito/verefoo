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
 * Represent a Generic Allocation Node
 */

abstract public class AllocationNode {

	protected Node node;
	protected GenericFunction placedNF;
	protected FunctionalTypes typeNF;
	protected String ipAddress;
	protected DatatypeExpr z3Name;
	protected DatatypeExpr z3Node;
	
	/**
	 * Public constructor for the AllocationNode class
	 */
	public AllocationNode() {
	}
	
	/**
	 * Getter method for the Node object
	 * @return the Node object
	 */
	public Node getNode() {
		return node;
	}
	
	/**
	 * Setter method for the Node object
	 * @param node It is the Node object
	 */
	public void setNode(Node node) {
		this.node = node;
	}
	
	/**
	 * Getter method for the Z3 Node expression
	 * @return the Z3 Node Expression
	 */
	public DatatypeExpr getZ3Node() {
		return z3Node;
	}

	/**
	 * Setter method for the Z3 Node expression
	 * @param z3Node It is the Z3Node expression.
	 */
	public void setZ3Node(DatatypeExpr z3Node) {
		this.z3Node = z3Node;
	}
	
	/**
	 * Getter method for the Z3 name expression
	 * @return the Z3 name Expression
	 */
	public DatatypeExpr getZ3Name() {
		return z3Name;
	}

	/**
	 * Setter method for the Z3 name expression
	 * @param z3Name It is the Z3Name expression.
	 */
	public void setZ3Name(DatatypeExpr z3Name) {
		this.z3Name = z3Name;
	}

	/**
	 * Getter method for the Ip Address
	 * @return the ipAddress
	 */
	public String getIpAddress() {
		return ipAddress;
	}

	/**
	 * Setter method for the ipAddress
	 * @param ipAddress It is the ipAddress
	 */
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}


	/**
	 * Getter method for the placed Network Function
	 * @return the placed Network Function
	 */
	public GenericFunction getPlacedNF() {
		return placedNF;
	}

	/**
	 * Setter method for the Network Object instantiated on the node
	 * @param placedNF It is the instantiated network function.
	 */
	public void setPlacedNF(GenericFunction placedNF) {
		this.placedNF = placedNF;
	}

	/**
	 * Getter method for type of the placed Network Function
	 * @return the FunctionalTypes of the placed Network Function
	 */
	public FunctionalTypes getTypeNF() {
		return typeNF;
	}

	/**
	 * Setter method for the type of the Network Object instantiated on the node
	 * @param typeNF It is the FunctionalTypes of the instantiated network function.
	 */
	public void setTypeNF(FunctionalTypes typeNF) {
		this.typeNF = typeNF;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ipAddress == null) ? 0 : ipAddress.hashCode());
		return result;
	}

	/**
	 * Method to add constraints to Z3 Solver.
	 * @param solver It is the instance of Z3 solver.
	 */
	public void addConstraints(Optimize solver) {
		if(placedNF != null) {
			placedNF.addContraints(solver);
		}
	}
	
	
}
