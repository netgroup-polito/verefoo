package it.polito.verifoo.rest.common;

import java.util.*;

import com.microsoft.z3.DatatypeExpr;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Optimize;

import it.polito.verifoo.rest.jaxb.*;
import it.polito.verigraph.mcnet.components.NetworkObject;

public class AllocationNode {

	private Node node;
	//private Map<FunctionalTypes, NetworkObject> placeableVNF;
	private NetworkObject placedNF;
	private FunctionalTypes typeNF;
	private String ipAddress;
	private List<Property> interestedProperties;
	private DatatypeExpr z3Name;
	private DatatypeExpr z3Node;
	
	
	private Map<AllocationNode, Set<AllocationNode>> leftHops = new HashMap<>();
	private Map<AllocationNode, Set<AllocationNode>> rightHops = new HashMap<>();
	private Map<AllocationNode, Set<AllocationNode>> lastHops = new HashMap<>();
	private Map<AllocationNode, Set<AllocationNode>> firstHops = new HashMap<>();
	
	
	public AllocationNode(Node node) {
		this.node = node;
		placedNF = null;
		typeNF = null;
		ipAddress = node.getName();
	}
	
	
	public DatatypeExpr getZ3Node() {
		return z3Node;
	}

	public void setZ3Node(DatatypeExpr z3Node) {
		this.z3Node = z3Node;
	}

	
	
	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public Map<AllocationNode, Set<AllocationNode>> getLeftHops() {
		return leftHops;
	}

	public void setLeftHops(Map<AllocationNode, Set<AllocationNode>> leftHops) {
		this.leftHops = leftHops;
	}

	public Map<AllocationNode, Set<AllocationNode>> getRightHops() {
		return rightHops;
	}

	public void setRightHops(Map<AllocationNode, Set<AllocationNode>> rightHops) {
		this.rightHops = rightHops;
	}

	public Map<AllocationNode, Set<AllocationNode>> getLastHops() {
		return lastHops;
	}

	public void setLastHops(Map<AllocationNode, Set<AllocationNode>> lastHops) {
		this.lastHops = lastHops;
	}

	public Map<AllocationNode, Set<AllocationNode>> getFirstHops() {
		return firstHops;
	}

	public void setFirstHops(Map<AllocationNode, Set<AllocationNode>> firstHops) {
		this.firstHops = firstHops;
	}


	public Node getNode() {
		return node;
	}
	
	public void setNode(Node node) {
		this.node = node;
	}
	

	public NetworkObject getPlacedNF() {
		return placedNF;
	}

	public void setPlacedNF(NetworkObject placedNF) {
		this.placedNF = placedNF;
	}

	public FunctionalTypes getTypeNF() {
		return typeNF;
	}

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

	public DatatypeExpr getZ3Name() {
		return z3Name;
	}

	public void setZ3Name(DatatypeExpr z3Name) {
		this.z3Name = z3Name;
	}

	public void addConstraints(Optimize solver) {
		if(placedNF != null) {
			placedNF.addContraints(solver);
		}
	}
	
	
}
