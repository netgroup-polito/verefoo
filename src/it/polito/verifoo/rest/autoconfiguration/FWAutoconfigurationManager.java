package it.polito.verifoo.rest.autoconfiguration;

import it.polito.verigraph.mcnet.components.Quattro;
import it.polito.verigraph.mcnet.netobjs.AclFirewall;

import it.polito.verifoo.rest.jaxb.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;



public class FWAutoconfigurationManager {
	
	private WildcardManager wildcardManager;
	private HashMap<String, Quattro<AclFirewall, Node, Integer, Boolean>> autoconfFW;
	private HashMap<String, List<Property>> FWAllPolicies;
	private HashMap<String, List<Property>> FWInterestedPolicies;
	private List<Node> nodes;
	
	private List<Property> policies;
	
	public FWAutoconfigurationManager(WildcardManager wildcardManager, List<Property> policies, List<Node> nodes){
		this.wildcardManager = wildcardManager;
		this.policies = policies;
		
		this.nodes = nodes;
		autoconfFW = new HashMap<String, Quattro<AclFirewall, Node, Integer, Boolean>>();
		FWAllPolicies = new HashMap<String, List<Property>>();
		FWInterestedPolicies = new HashMap<String, List<Property>>();
	}
	
	public void addFirewall(AclFirewall f, Node n) {
		
		boolean autoplace;
		
		if(n.getConfiguration() == null) {
			autoplace = true;
		} else {
			autoplace = false;
		}
		
		autoconfFW.put(n.getName(), new Quattro<AclFirewall, Node, Integer, Boolean>(f, n, new Integer(0), new Boolean(autoplace)));
		FWAllPolicies.put(n.getName(), new ArrayList<Property>());
		FWInterestedPolicies.put(n.getName(), new ArrayList<Property>());

	}
	
	public void setPolicy(Node firewall, Node source, Node destination) {

		List<Property> allProperties = policies.stream().filter(p -> p.getSrc().equals(source.getName()) && p.getDst().equals(destination.getName())).collect(Collectors.toList());		
		List<Property> interestedProperties = allProperties.stream().filter(p ->
				( p.getName().value().equals("IsolationProperty") && firewall.getConfiguration().getFirewall().getDefaultAction() == ActionTypes.ALLOW ) 
				|| (p.getName().value().equals("ReachabilityProperty") && firewall.getConfiguration().getFirewall().getDefaultAction() == ActionTypes.DENY)).collect(Collectors.toList());
		
		FWAllPolicies.get(firewall.getName()).addAll(allProperties);
		FWInterestedPolicies.get(firewall.getName()).addAll(interestedProperties);
		autoconfFW.get(firewall.getName())._3 += interestedProperties.size();
		
	}
	
	public void minimizeRules() {
		
		if(wildcardManager.areNodesWithIPAddresses()) {
			for(Map.Entry<String, Quattro<AclFirewall, Node, Integer, Boolean>> entry : autoconfFW.entrySet()) {
				String key = entry.getKey();
				Quattro<AclFirewall, Node, Integer, Boolean> value = entry.getValue();

				if(value._2.getNeighbour().size() == 2) {
					List<Neighbour> neighbours = value._2.getNeighbour();
					

					Node n0 = nodes.stream().filter(n -> n.getName().equals(neighbours.get(0).getName())).findFirst().get();
					Node n1 = nodes.stream().filter(n -> n.getName().equals(neighbours.get(1).getName())).findFirst().get();
				
					if((n0.getFunctionalType() == FunctionalTypes.FIREWALL && n1.getFunctionalType() == FunctionalTypes.FIREWALL) ||
							(n0.getFunctionalType() == FunctionalTypes.FIREWALL && (n1.getFunctionalType() == FunctionalTypes.WEBSERVER || n1.getFunctionalType() == FunctionalTypes.MAILSERVER)) ||
							(n1.getFunctionalType() == FunctionalTypes.FIREWALL && (n0.getFunctionalType() == FunctionalTypes.WEBSERVER || n0.getFunctionalType() == FunctionalTypes.MAILSERVER))) {
						value._3 = 0;
						value._1.firewallSendRules(value._3);
						continue;
					}
				} 
				
					
				List<Property> interestedPolicies = FWInterestedPolicies.get(value._2.getName());
				List<Property> allPolicies = FWAllPolicies.get(value._2.getName());
				if(!policies.isEmpty()) {
					List<String> destinations = policies.stream().map(p -> p.getDst()).distinct().collect(Collectors.toList());
					for(String destination : destinations) {
						Set<String> interestedSRC = interestedPolicies.stream().filter(p -> p.getDst() == destination).map(p -> p.getSrc()).distinct().collect(Collectors.toSet());
						Set<String> notInterestedSRC = allPolicies.stream().filter(p -> p.getDst() != destination).map(p -> p.getSrc()).distinct().collect(Collectors.toSet());
						if(wildcardManager.areAggregable(interestedSRC, notInterestedSRC)) {
							value._3 -= interestedSRC.size();
							value._3 += 1;
						}
					}
				}
				
				value._1.firewallSendRules(value._3); //creation of Z3 formulas for autoconfiguration firewall
			}
			
		} else {
			
			for(Map.Entry<String, Quattro<AclFirewall, Node, Integer, Boolean>> entry : autoconfFW.entrySet()) {
				Quattro<AclFirewall, Node, Integer, Boolean> value = entry.getValue();
				value._1.firewallSendRules(value._3);
			}
			
		}
		
		
	}
	

}
