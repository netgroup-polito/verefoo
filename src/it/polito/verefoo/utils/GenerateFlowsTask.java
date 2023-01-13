package it.polito.verefoo.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import it.polito.verefoo.allocation.AllocationNodeAP;
import it.polito.verefoo.graph.FlowPathAP;
import it.polito.verefoo.allocation.AllocationNodeMF;
import it.polito.verefoo.graph.FlowPathMF;
import it.polito.verefoo.graph.Predicate;
import it.polito.verefoo.graph.SecurityRequirement;
import it.polito.verefoo.jaxb.FunctionalTypes;
import it.polito.verefoo.jaxb.L4ProtocolTypes;
import it.polito.verefoo.jaxb.Node;
import it.polito.verefoo.jaxb.Property;

public class GenerateFlowsTask implements Runnable {

	SecurityRequirement requirement;
	HashMap<Integer, Predicate> networkAtomicPredicates;
	APUtilsAP aputils;
	HashMap<String, Node> transformersNode;
	AtomicInteger atomicId;
	
	public GenerateFlowsTask(SecurityRequirement requirement, HashMap<Integer, Predicate> networkAtomicPredicates, 
			APUtilsAP aputils, HashMap<String, Node> transformersNode, AtomicInteger atomicId) {
		this.requirement = requirement;
		this.networkAtomicPredicates = networkAtomicPredicates;
		this.aputils = aputils;
		this.transformersNode = transformersNode;
		this.atomicId = atomicId;
	}
	
	
	@Override
	public void run() {
		Property prop = requirement.getOriginalProperty();
		//System.out.println("\nSource predicates for requirement {"+prop.getSrc()+","+prop.getSrcPort()+","+prop.getDst()+","+prop.getDstPort()+","+prop.getLv4Proto()+"}");
		String pSrc = prop.getSrcPort() != null &&  !prop.getSrcPort().equals("null") ? prop.getSrcPort() : "*";
		//get all atomic predicates that match IPSrc and PSrc
		Predicate srcPredicate = new Predicate(prop.getSrc(), false, "*", false, pSrc, false, "*", false, L4ProtocolTypes.ANY);
		List<Integer> srcPredicateList = new ArrayList<>();
		for(HashMap.Entry<Integer, Predicate> apEntry: networkAtomicPredicates.entrySet()) {
			Predicate intersectionPredicate = aputils.computeIntersection(apEntry.getValue(), srcPredicate);
			if(intersectionPredicate != null && aputils.APCompare(intersectionPredicate, apEntry.getValue())
					&& !apEntry.getValue().hasIPDstOnlyNegs()) {
				//System.out.print(apEntry.getKey() + " "); apEntry.getValue().print();
				srcPredicateList.add(apEntry.getKey());
			}
		}
		
		//System.out.println("Destination predicates");
		List<Integer> dstPredicateList = new ArrayList<>();
		String pDst = prop.getDstPort() != null &&  !prop.getDstPort().equals("null") ? prop.getDstPort() : "*";
		Predicate dstPredicate = new Predicate("*", false, prop.getDst(), false, "*", false, pDst, false, prop.getLv4Proto());
		//get all atomic predicates that match IPDst and PDst and prototype
		for(HashMap.Entry<Integer, Predicate> apEntry: networkAtomicPredicates.entrySet()) {
			Predicate intersectionPredicate = aputils.computeIntersection(apEntry.getValue(), dstPredicate);
			if(intersectionPredicate != null && aputils.APCompare(intersectionPredicate, apEntry.getValue())) {
				//System.out.print(apEntry.getKey() + " "); apEntry.getValue().print();
				dstPredicateList.add(apEntry.getKey());
			}
		}
		
		//Generate atomic flows
		for(FlowPathAP flow: requirement.getFlowsMapAP().values()) {
			List<AllocationNodeAP> path = flow.getPath();
			List<List<Integer>> resultList = new ArrayList<>();
			List<List<Integer>> resultListToDiscard = new ArrayList<>();
			//now we have the requirement, the path and the list of source predicates -> call recursive function
			int nodeIndex = 0;
			for(Integer ap: srcPredicateList) {
				List<Integer> currentList = new ArrayList<>();
				recursiveGenerateAtomicPath(nodeIndex, requirement, path, ap, dstPredicateList, resultList, resultListToDiscard, currentList);
			}
			
			for(List<Integer> atomicFlow: resultList) {
				flow.addAtomicFlow(atomicId.incrementAndGet(), atomicFlow);
			}
			for(List<Integer> atomicFlowToDiscard: resultListToDiscard)
				flow.addAtomicFlowToDiscard(atomicId.incrementAndGet(), atomicFlowToDiscard);
		}
		
	}
	
	private void recursiveGenerateAtomicPath(int nodeIndex, SecurityRequirement sr, List<AllocationNodeAP> path, int ap, List<Integer> dstPredicateList, List<List<Integer>> atomicFlowsList, List<List<Integer>> atomicFlowsListToDiscard, List<Integer> currentList) {
		AllocationNodeAP currentNode = path.get(nodeIndex);
		Predicate currentPredicate = networkAtomicPredicates.get(ap);
		Predicate currentNodeDestPredicate = new Predicate("*", false, currentNode.getIpAddress(), false, "*", false, "*", false, L4ProtocolTypes.ANY);
		
		if(nodeIndex == path.size() -1) {
			//last node of the path
			if(dstPredicateList.contains(ap)) {
				//ALL OK, new atomic flow found
				atomicFlowsList.add(currentList);
				return;
			} else {
				//Discard path
				currentList.add(ap);
				atomicFlowsListToDiscard.add(currentList);
				return;
			}
		}
		
		Predicate intersectionPredicate = aputils.computeIntersection(currentPredicate, currentNodeDestPredicate);
		if(intersectionPredicate != null && aputils.APCompare(intersectionPredicate, currentPredicate)
				&& (currentNode.getTransformationMap().isEmpty()  //not NAT
						|| (!currentNode.getTransformationMap().containsKey(ap)))) { //is NAT but does not transform the predicate
			//Discard path: destination reached without reaching destination of the path
			currentList.add(ap);
			atomicFlowsListToDiscard.add(currentList);
			return;
		}
		
		//Apply transformation and filtering rules
		if(transformersNode.containsKey(currentNode.getIpAddress()) && transformersNode.get(currentNode.getIpAddress()).getFunctionalType().equals(FunctionalTypes.NAT)) { //NAT
			if(currentNode.getTransformationMap().containsKey(ap)) {
				for(Integer newAp: currentNode.getTransformationMap().get(ap)) {
					List<Integer> newCurrentList = new ArrayList<>(currentList);
					newCurrentList.add(newAp);
					recursiveGenerateAtomicPath(nodeIndex+1, sr, path, newAp, dstPredicateList, atomicFlowsList, atomicFlowsListToDiscard, newCurrentList);
				}
			} else {
				//simple forwarding
				List<Integer> newCurrentList = new ArrayList<>(currentList);
				newCurrentList.add(ap);
				recursiveGenerateAtomicPath(nodeIndex+1, sr, path, ap, dstPredicateList, atomicFlowsList, atomicFlowsListToDiscard, newCurrentList);
			}
		}
		else { //normal node
			List<Integer> newCurrentList = new ArrayList<>(currentList);
			newCurrentList.add(ap);
			recursiveGenerateAtomicPath(nodeIndex+1, sr, path, ap, dstPredicateList, atomicFlowsList, atomicFlowsListToDiscard, newCurrentList);
		}	
	}

}
