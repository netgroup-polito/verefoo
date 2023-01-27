package it.polito.verefoo.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.polito.verefoo.graph.IPAddress;
import it.polito.verefoo.graph.PortInterval;
import it.polito.verefoo.graph.Predicate;
import it.polito.verefoo.jaxb.L4ProtocolTypes;

/**
 * This class contains utilities used only by Maximal Flows algorithm when calculating predicates.
 */
public class APUtilsMF extends APUtils {
	
	public APUtilsMF() {}
	
	
	/* Compute rules for firewall */
	//toAdd is the ALLOW rule to insert, denied is the list of denied predicates
	//return allowed = rule-i AND !denied
	List<Predicate> negDeniedRuleList;
	public List<Predicate> computeAllowedForRule(Predicate toAdd, List<Predicate> deniedList){
		List<Predicate> retList = new ArrayList<>();
		List<Predicate> tmpList = new ArrayList<>();
		retList.add(toAdd);
		
		if(deniedList.isEmpty()) return retList;
		
		for(Predicate deniedRule: deniedList) {
			//compute !denied
			negDeniedRuleList = neg(deniedRule);
			for(Predicate p1: retList) {
				for(Predicate p2: negDeniedRuleList) {
					Predicate res = computeIntersection(p1, p2);
					if(res != null) {
						tmpList.add(res);
					}
				}
			}
			if(tmpList.isEmpty()) {
				//no intersection exists
				return new ArrayList<>();
			} else {
				retList = new ArrayList<>(tmpList);
				tmpList = new ArrayList<>();
			}
		}
		return retList;
	}
	

	public List<PortInterval> complexPortIntervalListInAndToOr(List<PortInterval> list){
		List<PortInterval> returnList = new ArrayList<>();
		PortInterval largestPI = null;
		List<PortInterval> otherIntervalList = new ArrayList<>();
		
		//Compute largest interval
		for(PortInterval pi: list) {
			if(!pi.isNeg())
				largestPI = pi;
			else otherIntervalList.add(pi);
		}
		
		if(otherIntervalList.size() == 0) {
			returnList.add(largestPI);
			return returnList;
		}
		
		//Sort by increasing min
		Collections.sort(otherIntervalList, new PortIntervalComparator());
		
		int index = 0;
		for(PortInterval pi: otherIntervalList) {
			if(index == 0) {
				//first port interval
				if(pi.getMin() > largestPI.getMin()) {
					returnList.add(new PortInterval(largestPI.getMin(), pi.getMin()-1, false));
				}
			}
			else {
				PortInterval previousPI = otherIntervalList.get(index-1);
				returnList.add(new PortInterval(previousPI.getMax()+1, pi.getMin()-1, false));
			}
			index++;
		}
		PortInterval last = otherIntervalList.get(index-1);
		if(last.getMax() < largestPI.getMax())
			returnList.add(new PortInterval(last.getMax()+1, largestPI.getMax(), false));
		
		//DEBUG: print list
//		System.out.println("Lista Port Interval size " + returnList.size());
//		for(PortInterval pi: returnList)
//			System.out.println(pi.toString());
//		System.out.println();
		//END DEBUG
		
		return returnList;
	}

}
