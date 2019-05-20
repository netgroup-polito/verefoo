package it.polito.verefoo.allocation;

import java.util.*;

import com.microsoft.z3.DatatypeExpr;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Optimize;

import it.polito.verefoo.jaxb.*;
import it.polito.verigraph.functions.GenericFunction;


/*
 * This class is an extension of the JAXB-Annotated Node class.
 * It has additional features, like maps used to build forwarding rules and information about deployed Network Functions.
 */

public class AllocationNode {

	private Node node;
	private GenericFunction placedNF;
	private FunctionalTypes typeNF;
	private String ipAddress;
	private DatatypeExpr z3Name;
	private DatatypeExpr z3Node;
	
	/*
	 * These maps are used to store:
	 * 1) leftHop towards a destination
	 * 2) rightHop (i.e. nextHop) towards a destination
	 * 3) the last hop for a destination
	 * 4) the first hop for a destination
	 */
	private Map<AllocationNode, Set<AllocationNode>> leftHops = new HashMap<>();
	private Map<AllocationNode, Set<AllocationNode>> rightHops = new HashMap<>();
	private Map<AllocationNode, Set<AllocationNode>> lastHops = new HashMap<>();
	private Map<AllocationNode, Set<AllocationNode>> firstHops = new HashMap<>();
	
	/**
	 * Public constructor for the AllocationNode class
	 * @param node It is the JAXB Node object.
	 */
	public AllocationNode(Node node) {
		this.node = node;
		placedNF = null;
		typeNF = null;
		ipAddress = node.getName();
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
	 * Getter method for the leftHops map
	 * @return the leftHops map
	 */
	public Map<AllocationNode, Set<AllocationNode>> getLeftHops() {
		return leftHops;
	}

	/**
	 * Setter method for the leftHops map
	 * @param leftHops It is a Map<AllocationNode, Set<AllocationNode>
	 */
	public void setLeftHops(Map<AllocationNode, Set<AllocationNode>> leftHops) {
		this.leftHops = leftHops;
	}

	/**
	 * Getter method for the rightHops map
	 * @return the rightHops map
	 */
	public Map<AllocationNode, Set<AllocationNode>> getRightHops() {
		return rightHops;
	}

	/**
	 * Setter method for the rightHops map
	 * @param rightHops It is a Map<AllocationNode, Set<AllocationNode>
	 */
	public void setRightHops(Map<AllocationNode, Set<AllocationNode>> rightHops) {
		this.rightHops = rightHops;
	}

	/**
	 * Getter method for the lastHops map
	 * @return the lastHops map
	 */
	public Map<AllocationNode, Set<AllocationNode>> getLastHops() {
		return lastHops;
	}

	/**
	 * Setter method for the lastHops map
	 * @param lastHops It is a Map<AllocationNode, Set<AllocationNode>
	 */
	public void setLastHops(Map<AllocationNode, Set<AllocationNode>> lastHops) {
		this.lastHops = lastHops;
	}

	/**
	 * Getter method for the firstHops map
	 * @return the firstHops map
	 */
	public Map<AllocationNode, Set<AllocationNode>> getFirstHops() {
		return firstHops;
	}

	/**
	 * Setter method for the firstHops map
	 * @param firstHops It is a Map<AllocationNode, Set<AllocationNode>
	 */
	public void setFirstHops(Map<AllocationNode, Set<AllocationNode>> firstHops) {
		this.firstHops = firstHops;
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

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AllocationNode other = (AllocationNode) obj;
		if (ipAddress == null) {
			if (other.ipAddress != null)
				return false;
		} else if (!ipAddress.equals(other.ipAddress))
			return false;
		return true;
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
