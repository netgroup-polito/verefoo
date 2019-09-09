package it.polito.verefoo;

import it.polito.verigraph.extra.Quadruple;
import it.polito.verigraph.functions.PacketFilter;
import it.polito.verefoo.allocation.AllocationNode;
import it.polito.verefoo.extra.WildcardManager;
import it.polito.verefoo.jaxb.*;

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

public class PacketFilterManager {

	private WildcardManager wildcardManager;
	private HashMap<String, Quadruple<PacketFilter, AllocationNode, Integer, Boolean>> autoconfPFproperties;
	private HashMap<String, List<Property>> pfPolicies;
	private HashMap<String, List<Property>> pfPoliciesSpecific;

	private List<Property> policies;

	/**
	 * Public constructor of FWAutoconfigurationManager class
	 * 
	 * @param wildcardManager It is an object of WildcardManager class
	 * @param policies        It is the list of reachability and isolation
	 *                        properties
	 * @param nodes           It is the list of nodes in the network
	 */
	public PacketFilterManager(WildcardManager wildcardManager, List<Property> policies) {
		this.wildcardManager = wildcardManager;
		this.policies = policies;
		autoconfPFproperties = new HashMap<String, Quadruple<PacketFilter, AllocationNode, Integer, Boolean>>();
		pfPolicies = new HashMap<String, List<Property>>();
		pfPoliciesSpecific = new HashMap<String, List<Property>>();
	}

	/**
	 * This method adds in a local map a firewall, associating it with the node in
	 * which it's deployed.
	 * 
	 * @param f The firewall to deploy
	 * @param n The node on which the firewall can be deployed
	 */
	public void addFirewall(PacketFilter f, AllocationNode n) {
		boolean autoplace;
		autoplace = true;
		if (!autoconfPFproperties.containsKey(n.getNode().getName())) {
			autoconfPFproperties.put(n.getNode().getName(),
					new Quadruple<PacketFilter, AllocationNode, Integer, Boolean>(f, n, new Integer(0),
							new Boolean(autoplace)));
			pfPolicies.put(n.getNode().getName(), new ArrayList<Property>());
			pfPoliciesSpecific.put(n.getNode().getName(), new ArrayList<Property>());
		}

	}

	/**
	 * This methods stores the information that if a firewall is present on the node
	 * specified as input, then it should manage a reachability/isolation polity
	 * between the source and destination specified.
	 * 
	 * @param node        It's the node on which a firewall may have been placed.
	 * @param source      It's the source of the policy.
	 * @param destination It's the destination of the policy.
	 */
	public void setPolicy(AllocationNode node, Node source, Node destination) {

		int newRules = 0;
		PacketFilter firewall = autoconfPFproperties.get(node.getIpAddress())._1;
		List<Property> allProperties = policies.stream()
				.filter(p -> p.getSrc().equals(source.getName()) && p.getDst().equals(destination.getName()))
				.collect(Collectors.toList());
		List<Property> interestedProperties = allProperties.stream().filter(p -> {
			boolean pruning = (p.getName().value().equals("IsolationProperty") && firewall.isBlacklisting())
					|| (p.getName().value().equals("ReachabilityProperty") && !firewall.isBlacklisting());
			// if the pruning must be disabled
			// return true;
			return pruning;
		}).collect(Collectors.toList());

		for (Property property : allProperties) {
			boolean found = pfPolicies.get(node.getIpAddress()).stream()
					.anyMatch(p -> p.getSrc().equals(property.getSrc()) && p.getDst().equals(property.getDst()));
			if (!found) {
				pfPolicies.get(node.getIpAddress()).add(property);
			}
		}

		for (Property property : interestedProperties) {
			boolean found = pfPoliciesSpecific.get(node.getIpAddress()).stream()
					.anyMatch(p -> p.getSrc().equals(property.getSrc()) && p.getDst().equals(property.getDst()));
			if (!found) {
				pfPoliciesSpecific.get(node.getIpAddress()).add(property);
				newRules++;
			}
		}
		autoconfPFproperties.get(node.getIpAddress())._3 += newRules;
	}

	/**
	 * This method implements a pruning+heuristics to minizime the number of rules
	 * which Z3Opt should evaluate. It combines rules only if the corresponding IP
	 * Addresses can be merged in a larger address range. WildcrdManager object in
	 * exploited in this method.
	 */
	public void minimizeRules() {

		if (wildcardManager.areNodesWithIPAddresses()) {
			for (Map.Entry<String, Quadruple<PacketFilter, AllocationNode, Integer, Boolean>> entry : autoconfPFproperties
					.entrySet()) {
				Quadruple<PacketFilter, AllocationNode, Integer, Boolean> value = entry.getValue();
				List<Property> interestedPolicies = pfPoliciesSpecific.get(value._2.getNode().getName());
				List<Property> allPolicies = pfPolicies.get(value._2.getNode().getName());
				if (!policies.isEmpty()) {
					List<String> destinations = policies.stream().map(p -> p.getDst()).distinct()
							.collect(Collectors.toList());
					for (String destination : destinations) {
						Set<String> interestedSRC = interestedPolicies.stream()
								.filter(p -> p.getDst().equals(destination)).map(p -> p.getSrc()).distinct()
								.collect(Collectors.toSet());
						Set<String> notInterestedSRC = allPolicies.stream().filter(p -> p.getDst().equals(destination))
								.map(p -> p.getSrc()).distinct().collect(Collectors.toSet());
						notInterestedSRC.removeAll(interestedSRC);
						if (!interestedSRC.isEmpty()
								&& wildcardManager.areAggregable(interestedSRC, notInterestedSRC)) {
							value._3 -= interestedSRC.size();
							value._3 += 1;
						}
					}
				}
				if (value._1.isAutoconfigured()) {
					if(value._2.getNode().getName().equals("1.0.0.12")) {
						value._1.automaticConfiguration(value._3);
						System.out.println(value._3);
					}
					else
						value._1.automaticConfiguration(1);

				} else {
					value._1.manualConfiguration();
				}

			}

		} else {

			for (Map.Entry<String, Quadruple<PacketFilter, AllocationNode, Integer, Boolean>> entry : autoconfPFproperties
					.entrySet()) {
				Quadruple<PacketFilter, AllocationNode, Integer, Boolean> value = entry.getValue();
				value._1.automaticConfiguration(value._3);
			}

		}

	}

	/**
	 * This method allows to know if, on a node, a firewall has been tentatively
	 * deployed.
	 * 
	 * @param n The node of interested
	 * @return true if on the input node node a firewall has been tentatively
	 *         deployed.
	 */
	public boolean firewallIsPresent(AllocationNode n) {
		return autoconfPFproperties.containsKey(n.getIpAddress());
	}

}
