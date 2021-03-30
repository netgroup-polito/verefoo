package it.polito.verefoo.allocation;

import java.util.*;

import com.microsoft.z3.DatatypeExpr;
import com.microsoft.z3.Optimize;

import it.polito.verefoo.functions.GenericFunction;
import it.polito.verefoo.graph.FlowPath;
import it.polito.verefoo.graph.Predicate;
import it.polito.verefoo.jaxb.*;


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
	
	/* Atomic predicates */
	HashMap<Integer, List<Integer>> transformationsMap = new HashMap<>();
	List<Predicate> forwardBehaviourPredicateList = new ArrayList<>();
	List<Integer> forwardBehaviourList = new ArrayList<>();
	List<Integer> droppedList = new ArrayList<>();
	private Map<Integer, FlowPath> crossingFlows = new HashMap<>();
	//<flowPathId, <atomicFlowId, atomicPredicate>>
	private Map<Integer, Map<Integer, Integer>> mapFlowIdAtomicPredicatesInInput = new HashMap<>();
	
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

	public void addCrossingFlow(FlowPath sr) {
		crossingFlows.put(sr.getIdFlow(), sr);
	}
	
	//return flows that cross this node
	public Map<Integer, FlowPath> getCrossingFlows() {
		return crossingFlows;
	}

	/**
	 * Setter method for the map of requirements
	 * @param requirements the map of requirements
	 */
	public void setFlows(Map<Integer, FlowPath> requirements) {
		this.crossingFlows = requirements;
	}
	
	public HashMap<Integer, List<Integer>> getTransformationMap(){
		return transformationsMap;
	}
	
	public void setForwardBehaviourPredicateList(List<Predicate> list) {
		this.forwardBehaviourPredicateList = list;
	}
	
	public List<Predicate> getForwardBehaviourPredicateList(){
		return forwardBehaviourPredicateList;
	}
	
	public void setForwardBehaviourList(List<Integer> list) {
		forwardBehaviourList = list;
	}
	
	public List<Integer> getForwardBehaviourList(){
		return forwardBehaviourList;
	}
	
	public List<Integer> getDroppedList() {
		return droppedList;
	}

	public void setDroppedList(List<Integer> droppedList) {
		this.droppedList = droppedList;
	}

	public void addAtomicPredicateInInput(int flowPathId, int atomicFlowId, int ap) {
		if(mapFlowIdAtomicPredicatesInInput.containsKey(flowPathId)) {
			Map<Integer, Integer> atomicPredicateMap = mapFlowIdAtomicPredicatesInInput.get(flowPathId);
			atomicPredicateMap.put(atomicFlowId, ap);
		} else {
			Map<Integer, Integer> newMap = new HashMap<>();
			newMap.put(atomicFlowId, ap);	
			mapFlowIdAtomicPredicatesInInput.put(flowPathId, newMap);
		}
	}
	
	public Map<Integer, Integer> getAtomicPredicatesInInputForFlow(int flowId){
		if(mapFlowIdAtomicPredicatesInInput.containsKey(flowId))
			return mapFlowIdAtomicPredicatesInInput.get(flowId);
		return null;
	}
	
	public void addForwardingPredicate(int ap) {
		forwardBehaviourList.add(ap);
	}
	
	public void addDroppedPredicate(int ap) {
		droppedList.add(ap);
	}

	public Map<Integer, Map<Integer, Integer>> getMapFlowIdAtomicPredicatesInInput() {
		return mapFlowIdAtomicPredicatesInInput;
	}

	public void setMapFlowIdAtomicPredicatesInInput(Map<Integer, Map<Integer, Integer>> mapFlowIdAtomicPredicatesInInput) {
		this.mapFlowIdAtomicPredicatesInInput = mapFlowIdAtomicPredicatesInInput;
	}
	
	
}
