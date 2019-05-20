package it.polito.verefoo.extra;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import it.polito.verefoo.allocation.AllocationNode;
import it.polito.verefoo.jaxb.Node;
import it.polito.verigraph.extra.Quadruple;
import it.polito.verigraph.functions.PacketFilter;

/**
 * WildcardManager is the class which creates all the possible wildcards from every IP Address.
 * They are divided into 4 levels:
 * 1) -1.-1.-1.-1 -> /0 (includes all ip addresses)
 * 2) x.-1.-1.-1 -> /8 (A class)
 * 3) x.y.-1.-1 -> /16 (B class)
 * 4) x.y.z.-1 -> /24 (C class)
 */
public class WildcardManager {
	
	private HashMap<String, HashSet<String>> wildcardLevel1;
	private HashMap<String, HashSet<String>> wildcardLevel2;
	private HashMap<String, HashSet<String>> wildcardLevel3;
	private HashMap<String, HashSet<String>> wildcardLevel4;
	
	private boolean nodesWithIPAddresses;
	
	/**
	 * Public constructor of WildcardManager class
	 * It builds all the possible IP addresses with wildcards, dividing them in 4 levels.
	 * @param allocationNodes It's the map of nodes of the Allocation Graph.
	 */
	public WildcardManager(HashMap<String, AllocationNode> allocationNodes) {
		
		nodesWithIPAddresses = true;
		String addresses[] = {}; 
		addresses = allocationNodes.values().stream().map((n)->n.getNode().getName()).collect(Collectors.toCollection(ArrayList<String>::new)).toArray(addresses);
		
		wildcardLevel1 = new HashMap<String, HashSet<String>>();
		wildcardLevel2 = new HashMap<String, HashSet<String>>();
		wildcardLevel3 = new HashMap<String, HashSet<String>>();
		wildcardLevel4 = new HashMap<String, HashSet<String>>();
		
		
		for(String address : addresses) {
			
			String[] parts = address.split("\\.");
			
			if(parts.length != 4) {
				nodesWithIPAddresses = false;
				return;
			}
			
			if(wildcardLevel1.containsKey("-1.-1.-1.-1")) {
				wildcardLevel1.get("-1.-1.-1.-1").add(address);
			} else {
				HashSet<String> set = new HashSet<>();
				set.add(address);
				wildcardLevel1.put("-1.-1.-1.-1", set);
			}
			
			String l2 = parts[0] + ".-1.-1.-1";
			if(wildcardLevel1.containsKey(l2)) {
				wildcardLevel1.get(l2).add(address);
			} else {
				HashSet<String> set = new HashSet<>();
				set.add(address);
				wildcardLevel1.put(l2, set);
			}
			
			String l3 = parts[0] + "." + parts[1] + ".-1.-1";
			if(wildcardLevel1.containsKey(l3)) {
				wildcardLevel1.get(l3).add(address);
			} else {
				HashSet<String> set = new HashSet<>();
				set.add(address);
				wildcardLevel1.put(l3, set);
			}
			
			String l4 = parts[0] + "." + parts[1] + "." + parts[2] + ".-1";
			if(wildcardLevel1.containsKey(l4)) {
				wildcardLevel1.get(l4).add(address);
			} else {
				HashSet<String> set = new HashSet<>();
				set.add(address);
				wildcardLevel1.put(l4, set);
			}
			
		}
		
	}
	
	/**
	 * This method allows to understand if it's possible, starting from a set of string (toAggregate),
	 * combine them in an address range which doesn't include any string of another set (notAggregate)
	 * @param toAggregate It's the set of addresses to aggregate.
	 * @param notAggregate It's the set of addresses not to aggregate.
	 * @return true if it's possible to aggregate the addresses in toAggregate in a range which doesn't include the others in notAggregate
	 */
	public boolean areAggregable(Set<String> toAggregate, Set<String> notAggregate) {
		
		for(Map.Entry<String, HashSet<String>> entry : wildcardLevel1.entrySet()) {
			if(entry.getValue().containsAll(toAggregate) && Collections.disjoint(entry.getValue(), notAggregate)){
				return true;
			}
		}
		
		for(Map.Entry<String, HashSet<String>> entry : wildcardLevel2.entrySet()) {
			if(entry.getValue().containsAll(toAggregate) && Collections.disjoint(entry.getValue(), notAggregate)){
				return true;
			}
		}
			
		for(Map.Entry<String, HashSet<String>> entry : wildcardLevel3.entrySet()) {
			if(entry.getValue().containsAll(toAggregate) && Collections.disjoint(entry.getValue(), notAggregate)){
				return true;
			}
		}
			
		for(Map.Entry<String, HashSet<String>> entry : wildcardLevel4.entrySet()) {
			if(entry.getValue().containsAll(toAggregate) && Collections.disjoint(entry.getValue(), notAggregate)){
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * This method allows to understand if in the network nodes are assigned an IP address
	 * @return true if in the network nodes are assigned an IP address
	 */
	public boolean areNodesWithIPAddresses() {
		return nodesWithIPAddresses;
	}

}
