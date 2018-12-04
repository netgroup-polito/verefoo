package it.polito.verifoo.rest.autoconfiguration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import it.polito.verifoo.rest.jaxb.Node;
import it.polito.verigraph.mcnet.components.Quattro;
import it.polito.verigraph.mcnet.netobjs.AclFirewall;

public class WildcardManager {
	
	private HashMap<String, HashSet<String>> wildcardLevel1;
	private HashMap<String, HashSet<String>> wildcardLevel2;
	private HashMap<String, HashSet<String>> wildcardLevel3;
	private HashMap<String, HashSet<String>> wildcardLevel4;
	
	private boolean nodesWithIPAddresses;
	
	
	public WildcardManager(List<Node> nodes) {
		
		nodesWithIPAddresses = true;
		String addresses[] = {}; 
		addresses = nodes.stream().map((n)->n.getName()).collect(Collectors.toCollection(ArrayList<String>::new)).toArray(addresses);
		
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
	
	public boolean areNodesWithIPAddresses() {
		return nodesWithIPAddresses;
	}

}
