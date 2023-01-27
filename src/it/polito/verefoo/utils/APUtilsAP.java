package it.polito.verefoo.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import it.polito.verefoo.graph.IPAddress;
import it.polito.verefoo.graph.PortInterval;
import it.polito.verefoo.graph.Predicate;
import it.polito.verefoo.jaxb.L4ProtocolTypes;


/**
 * This class contains utilities used only by Atomic Predicates algorithm when calculating predicates.
 */

public class APUtilsAP extends APUtils {
	
	public APUtilsAP() {}
	
	List<Predicate> negDeniedRuleList;
	/**
	 * This function computes rules for firewall
	 * @param toAdd is the Allow rule to insert
	 * @param deniedList is the list of denied predicates
	 * @return List of Predicates allowed rules computed as allowed = rule-i AND !denied
	 */
	public List<Predicate> computeAllowedForRule(Predicate toAdd, List<Predicate> deniedList, boolean deniedListChanged){
		List<Predicate> retList = new ArrayList<>();
		List<Predicate> tmpList = new ArrayList<>();
		retList.add(toAdd);
		
		if(deniedList.isEmpty()) return retList;
		
		for(Predicate deniedRule: deniedList) {
			//compute !denied
			if(deniedListChanged)
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
	

}
