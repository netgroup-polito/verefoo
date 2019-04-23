package it.polito.verifoo.rest.autoconfiguration;

import it.polito.verigraph.mcnet.components.Quattro;
import it.polito.verigraph.mcnet.netobjs.AclFirewall;
import it.polito.verifoo.rest.common.AllocationNode;
import it.polito.verifoo.rest.jaxb.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class allow to manage the autoconfiguration of the packet filters.
 *
 */

public class FWAutoconfigurationManager {
	
	private WildcardManager wildcardManager;
	private HashMap<String, Quattro<AclFirewall, AllocationNode, Integer, Boolean>> autoconfFW;
	private HashMap<String, List<Property>> FWAllPolicies;
	private HashMap<String, List<Property>> FWInterestedPolicies;
	private List<Node> nodes;
	
	private List<Property> policies;
	
	/**
	 * Public constructor of FWAutoconfigurationManager class
	 * @param wildcardManager It is an object of WildcardManager class
	 * @param policies It is the list of reachability and isolation properties
	 * @param nodes It is the list of nodes in the network
	 */
	public FWAutoconfigurationManager(WildcardManager wildcardManager, List<Property> policies, List<Node> nodes){
		this.wildcardManager = wildcardManager;
		this.policies = policies;
		
		this.nodes = nodes;
		autoconfFW = new HashMap<String, Quattro<AclFirewall, AllocationNode, Integer, Boolean>>();
		FWAllPolicies = new HashMap<String, List<Property>>();
		FWInterestedPolicies = new HashMap<String, List<Property>>();
	}
	
	/**
	 * This method adds in a local map a firewall, associating it with the node in which it's deployed.
	 * @param f The firewall to deploy
	 * @param n The node on which the firewall can be deployed
	 */
	public void addFirewall(AclFirewall f, AllocationNode n) {
		
		boolean autoplace;
		
		autoplace = true;
		
		if(!autoconfFW.containsKey(n.getNode().getName())) {
			autoconfFW.put(n.getNode().getName(), new Quattro<AclFirewall, AllocationNode, Integer, Boolean>(f, n, new Integer(0), new Boolean(autoplace)));
			FWAllPolicies.put(n.getNode().getName(), new ArrayList<Property>());
			FWInterestedPolicies.put(n.getNode().getName(), new ArrayList<Property>());
		}
		

	}
	
	/**
	 * This methods stores the information that if a firewall is present on the node specified as input,
	 * then it should manage a reachability/isolation polity between the source and destination specified.
	 * @param node It's the node on which a firewall may have been placed.
	 * @param source It's the source of the policy.
	 * @param destination It's the destination of the policy.
	 */
	public void setPolicy(AllocationNode node, Node source, Node destination) {
	
		int newRules = 0;
		AclFirewall firewall = autoconfFW.get(node.getIpAddress())._1;
		List<Property> allProperties = policies.stream().filter(p -> p.getSrc().equals(source.getName()) && p.getDst().equals(destination.getName())).collect(Collectors.toList());		
		List<Property> interestedProperties = allProperties.stream().filter(p ->
		true).collect(Collectors.toList());
		for(Property property : allProperties) {
			boolean found = FWAllPolicies.get(node.getIpAddress()).stream().anyMatch(p -> p.getSrc().equals(property.getSrc()) && p.getDst().equals(property.getDst()));
			if(!found) {
				FWAllPolicies.get(node.getIpAddress()).add(property);
			}
		}
		//FWAllPolicies.get(firewall.getName()).addAll(allProperties);
		
		for(Property property : interestedProperties) {
			boolean found = FWInterestedPolicies.get(node.getIpAddress()).stream().anyMatch(p -> p.getSrc().equals(property.getSrc()) && p.getDst().equals(property.getDst()));
			if(!found) {
				FWInterestedPolicies.get(node.getIpAddress()).add(property);
				newRules++;
			}
		}
		//FWInterestedPolicies.get(firewall.getName()).addAll(interestedProperties);
		
		autoconfFW.get(node.getIpAddress())._3 += newRules;
		
	}
	
	/**
	 * This method implements a pruning+heuristics to minizime the number of rules which Z3Opt should evaluate.
	 * It combines rules only if the corresponding IP Addresses can be merged in a larger address range.
	 * WildcrdManager object in exploited in this method.
	 */
	public void minimizeRules() {
		
		if(wildcardManager.areNodesWithIPAddresses()) {
			for(Map.Entry<String, Quattro<AclFirewall, AllocationNode, Integer, Boolean>> entry : autoconfFW.entrySet()) {
				String key = entry.getKey();
				Quattro<AclFirewall, AllocationNode, Integer, Boolean> value = entry.getValue();
				//System.out.println(value._2.getIpAddress() + value._3);
				/*if(value._2.getNeighbour().size() == 2) {
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
				} */
				
					
				List<Property> interestedPolicies = FWInterestedPolicies.get(value._2.getNode().getName());
				List<Property> allPolicies = FWAllPolicies.get(value._2.getNode().getName());
				if(!policies.isEmpty()) {
					List<String> destinations = policies.stream().map(p -> p.getDst()).distinct().collect(Collectors.toList());
					for(String destination : destinations) {
						Set<String> interestedSRC = interestedPolicies.stream().filter(p -> p.getDst().equals(destination)).map(p -> p.getSrc()).distinct().collect(Collectors.toSet());
						Set<String> notInterestedSRC = allPolicies.stream().filter(p -> p.getDst().equals(destination)).map(p -> p.getSrc()).distinct().collect(Collectors.toSet());
						notInterestedSRC.removeAll(interestedSRC);
						if(!interestedSRC.isEmpty() && wildcardManager.areAggregable(interestedSRC, notInterestedSRC)) {
							value._3 -= interestedSRC.size();
							value._3 += 1;
						}
					}
				}
				//System.out.println(value._3);
				if(value._1.isAutoconfigured()) {
					value._1.firewallSendRules(value._3);
				} else {
					value._1.firewallSendRules();
				}
				
			}
			
		} else {
			
			for(Map.Entry<String, Quattro<AclFirewall, AllocationNode, Integer, Boolean>> entry : autoconfFW.entrySet()) {
				Quattro<AclFirewall, AllocationNode, Integer, Boolean> value = entry.getValue();
				value._1.firewallSendRules(value._3);
			}
			
		}
		
		
	}
	
	/**
	 * This method allows to know if, on a node, a firewall has been tentatively deployed.
	 * @param n The node of interested
	 * @return true if on the input node node a firewall has been tentatively deployed.
	 */
	public boolean firewallIsPresent(AllocationNode n) {
		return autoconfFW.containsKey(n.getIpAddress());
	}

}
