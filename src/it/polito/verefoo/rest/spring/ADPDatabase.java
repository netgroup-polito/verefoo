package it.polito.verefoo.rest.spring;

import java.util.concurrent.ConcurrentHashMap;
import it.polito.verefoo.jaxb.*;
import java.util.*;

public class ADPDatabase {
	private static ADPDatabase db = new ADPDatabase();
	private static long lastGraphID = 0;
	private static long lastRequirementsSetID = 0;
	private static long lastSubstrateID = 0;
	private static long lastSimulationID = 0;

	private ConcurrentHashMap<Long, ConcurrentHashMap<String, Node>> nodeByGraphId;
	private ConcurrentHashMap<Long, Constraints> constraintsByGraphId;
	private ConcurrentHashMap<Long, ConcurrentHashMap<Long, Property>> propertyByRequirementsSetId;
	private ConcurrentHashMap<Long, ConcurrentHashMap<String, Host>> hostBySubstrateId;
	private ConcurrentHashMap<Long, Connections> connectionsBySubstrateId;
	private ConcurrentHashMap<String, FunctionalTypes> networkFunctionMap;
	private ConcurrentHashMap<Long, NFV> simulationResultsMap;
	
	/**
	 * Constructor of the ADP Database class
	 */
	ADPDatabase() {
		nodeByGraphId = new ConcurrentHashMap<Long, ConcurrentHashMap<String, Node>>();
		constraintsByGraphId = new ConcurrentHashMap<Long, Constraints>();
		propertyByRequirementsSetId = new ConcurrentHashMap<Long, ConcurrentHashMap<Long, Property>>();
		hostBySubstrateId = new ConcurrentHashMap<Long, ConcurrentHashMap<String, Host>>();
		connectionsBySubstrateId = new ConcurrentHashMap<Long, Connections>();
		networkFunctionMap = new ConcurrentHashMap<String, FunctionalTypes>();
		simulationResultsMap = new ConcurrentHashMap<Long, NFV>();
	}
	
	/**
	 * @return the singleton instance of the database
	 */
	public static ADPDatabase getDB() {
		return db;
	}
	
	/**
	 * @return a new unique value for the graphId
	 */
	public static synchronized long getNextGraphId() {
		return ++lastGraphID;
	}
	
	
	/**
	 * @return a new unique value for the policyId
	 */
	public static synchronized long getNextRequirementsSetId() {
		return ++lastRequirementsSetID;
	}
	
	/**
	 * @return a new unique value for the substrateId
	 */
	public static synchronized long getNextSubstrateId() {
		return ++lastSubstrateID;
	}
	
	/**
	 * @return a new unique value for the simulationId
	 */
	public static long getNextSimulationId() {
		return ++lastSimulationID;
	}

	
	/* Graph Database */

	/**
	 * @param id it is the id of the graph
	 * @param graph it is the Graph element
	 * @return the created Graph element
	 */
	public Graph createGraph(long id, Graph graph) {
		
		ConcurrentHashMap<String, Node> nodesMap = new ConcurrentHashMap<String, Node>();
		List<Node> nodesList = graph.getNode();
		for(Node node : nodesList){
			nodesMap.put(node.getName(), node);
		}
		
		if(nodeByGraphId.putIfAbsent(id, nodesMap)==null) {
			return graph;
		}
		else 
			return null;
	}
	
	/**
	 * @param beforeInclusive it is the starting index of the list
	 * @param afterInclusive it is the ending index of the list
	 * @return a collection of retrieved graphs
	 */
	public Collection<Graph> getGraphs(int beforeInclusive, int afterInclusive) {
		
		List<Graph> list = new ArrayList<>();
		for(long i = beforeInclusive; i <= afterInclusive; i++){
			Graph graph = new Graph();
			graph.setId(i);
			ConcurrentHashMap<String, Node> nodesMap = nodeByGraphId.get(i);
			if(nodesMap != null){
				graph.getNode().addAll(nodesMap.values());
				list.add(graph);
			}
		}
		return list;
	}
	

	/**
	 * @return an empty Graphs element in case of success, null otherwise
	 */
	public synchronized Graphs deleteGraphs() {
		if(nodeByGraphId.isEmpty() && constraintsByGraphId.isEmpty())
			return null;
		nodeByGraphId.clear();
		constraintsByGraphId.clear();
		return new Graphs();
	}
	

	/**
	 * @param gid it is the id of the graph to retrieve
	 * @return the graph if it is exists, otherwise null
	 */
	public Graph getGraph(long gid) {
		
		Graph graph = new Graph();
		ConcurrentHashMap<String, Node> nodesMap = nodeByGraphId.get(gid);
		
		if(nodesMap == null)
			return null;
		
		graph.setId(gid);
		graph.getNode().addAll(nodesMap.values());
		return graph;
		
	}

	
	/**
	 * @param gid it is the id of the graph to update
	 * @param graph it is the new Graph element to store
	 * @return the updated Graph is an older one existed, otherwise null
	 */
	public synchronized Graph updateGraph(long gid, Graph graph) {
		
		ConcurrentHashMap<String, Node> nodesMap = new ConcurrentHashMap<String, Node>();
		for(Node node : graph.getNode()) {
			nodesMap.put(node.getName(), node);
		}
		
		ConcurrentHashMap<String, Node> result = nodeByGraphId.computeIfPresent(gid, (k,old) -> nodesMap);
		if(result == null){
			return null;
		}
		constraintsByGraphId.remove(gid);
		return graph;
		
	}

	
	/**
	 * @param gid it is the id of the graph to delete
	 * @return the deleted Graph if it existed, otherwise null
	 */
	public synchronized Graph deleteGraph(long gid) {
		
		Graph graph = new Graph();
		graph.setId(gid);
		
		ConcurrentHashMap<String, Node> nodesMap = nodeByGraphId.remove(gid);
		if(nodesMap == null)
			return null;
		constraintsByGraphId.remove(gid);
		graph.getNode().addAll(nodesMap.values());
		return graph;
	}

	
	

	
	
	/**
	 * @param gid it is the id of the graph
	 * @return the list of nodes associated to the graph if it exists, otherwise null
	 */
	public Collection<Node> getNodes(long gid) {
		ConcurrentHashMap<String, Node> map = nodeByGraphId.get(gid);
		if(map == null)
			return null;
		return map.values();
	}

	
	
	/**
	 * @param gid it is the id of the graph 
	 * @param nid it is the name of the node to retrieve
	 * @return the Node element if it exists, otherwise null
	 */
	public Node getNode(long gid, String nid) {
		ConcurrentHashMap<String, Node> map = nodeByGraphId.get(gid);
		if(map == null)
			return null;
		return map.get(nid);
	}

	
	
	/**
	 * @param gid it is the id of the graph 
	 * @param node it is the node to create
	 * @param nid it is the name of the node to create
	 * @return the created Node element in case of success, null if the graph doesn't exist, an empty Node element if the input node contains as a neighbour a non-existent node
	 */
	public synchronized Node createNode(long gid, Node node, String nid) {
		ConcurrentHashMap<String, Node> map = nodeByGraphId.get(gid);
		if(map == null)
			return null;
		
		
		for(Neighbour n : node.getNeighbour()){
			if(!map.containsKey(n.getName())) {
				Node empty = new Node();
				empty.setName(null);
				return empty;
			}
		}
		
		node.setName(nid);
		
		if(map.putIfAbsent(nid, node) == null) {
	
			for(Neighbour n : node.getNeighbour()){
				String neighName = n.getName();
				Node neighNode = map.get(neighName);
				Neighbour newNeigh = new Neighbour();
				newNeigh.setName(node.getName());
				neighNode.getNeighbour().add(newNeigh);
			}
			return node;
		}
		else {
			Node empty = new Node();
			empty.setName(null);
			return empty;
		}
			
	}

	
	/**
	 * @param gid it is the id of the graph
	 * @param nid it is the name of the node to update
	 * @param node is the the new Node element
	 * @return the updated Node element in case of success, null if the graph doesn't exist, an empty Node element if the input node contains as a neighbour a non-existent node
	 */
	public synchronized Node updateNode(long gid, String nid, Node node) {
		ConcurrentHashMap<String, Node> map = nodeByGraphId.get(gid);
		if(map == null || !map.containsKey(nid))
			return null;
		
		for(Neighbour n : node.getNeighbour()){
			if(!map.containsKey(n.getName())) {
				Node empty = new Node();
				empty.setName(null);
				return empty;
			}
		}
		
		for(Neighbour n : map.get(nid).getNeighbour()){
			String neighName = n.getName();
			Node neighNode = map.get(neighName);
			neighNode.getNeighbour().removeIf(neigh -> neigh.getName().compareTo(nid) == 0);
		}
		
		for(Neighbour n : node.getNeighbour()){
			String neighName = n.getName();
			Node neighNode = map.get(neighName);
			Neighbour newNeigh = new Neighbour();
			newNeigh.setName(node.getName());
			neighNode.getNeighbour().add(newNeigh);
		}
		
		map.remove(nid);
		map.put(node.getName(), node);
		return node;		
	}
	

	
	/**
	 * @param gid it is the id of the graph
	 * @param nid it is the name of the node to delete
	 * @return the deleted Node element in case of success, otherwise null
	 */
	public synchronized Node deleteNode(long gid, String nid) {
		
		ConcurrentHashMap<String, Node> map = nodeByGraphId.get(gid);
		if(map == null)
			return null;
		Node node = map.remove(nid);
		if(node == null)
			return null;
		
		for(Neighbour n : node.getNeighbour()){
			String neighName = n.getName();
			Node neighNode = map.get(neighName);
			neighNode.getNeighbour().removeIf(neigh -> neigh.getName().compareTo(nid) == 0);
		}
		
		Constraints c = constraintsByGraphId.get(gid);
		if(c != null){
			c.getNodeConstraints().getNodeMetrics().removeIf(nm -> nm.getNode().equals(nid));
			c.getLinkConstraints().getLinkMetrics().removeIf(nl -> nl.getSrc().equals(nid) || nl.getDst().equals(nid));
		}
		
		return node;
	}
	
	
	/**
	 * @param gid it is the id of the graph
	 * @param nid it is the name of the node to which a neighbour must be added
	 * @param neighbour it is the Neighbour element to add
	 * @return the added Neighbour in case of success, otherwise null
	 */
	public synchronized Neighbour addNeighbour(long gid, String nid, Neighbour neighbour) {
		
		ConcurrentHashMap<String, Node> map = nodeByGraphId.get(gid);
		if(map == null)
			return null;
		
		Node node = map.get(nid);
		Node nodeNeigh = map.get(neighbour.getName());
		
		if(node == null || nodeNeigh == null)
			return null;		
		
		for(Neighbour neigh : node.getNeighbour()){
			if(neigh.getName().equals(neighbour.getName())){
				Neighbour newneigh = new Neighbour();
				newneigh.setName(null);
				return newneigh;
			}
		}
	

		node.getNeighbour().add(neighbour);
		Neighbour neighToAdd = new Neighbour();
		neighToAdd.setName(nid);
		nodeNeigh.getNeighbour().add(neighToAdd);
		
		
		return neighbour;
	}
	
	
	
	/**
	 * @param gid it is the id of the graph
	 * @param nid it is the name of the node from which a neighbour must be deleted
	 * @param neighbour it is the name of the neighbour to delete
	 * @return the deleted Neighbour in case of success, otherwise null
	 */
	public synchronized Neighbour deleteNeighbour(long gid, String nid, String neighbour) {
		
		ConcurrentHashMap<String, Node> map = nodeByGraphId.get(gid);
		if(map == null)
			return null;
		
		Node node = map.get(nid);
		Node nodeNeigh = map.get(neighbour);
		
		if(node == null || nodeNeigh == null)
			return null;

		boolean badRequest = true;
		for(Neighbour neigh : node.getNeighbour()){
			if(neigh.getName().equals(neighbour)){
				badRequest = false;
			}
		}
		
		if(badRequest){
			Neighbour newneigh = new Neighbour();
			newneigh.setName(null);
			return newneigh;
		}
		
		
		node.getNeighbour().removeIf(n -> n.getName().equals(neighbour));
		nodeNeigh.getNeighbour().removeIf(n -> n.getName().equals(node.getName()));
		
		Neighbour neighToRemove = new Neighbour();
		neighToRemove.setName(neighbour);
		return neighToRemove;
	}
	
	
	/**
	 * @param gid it is the id of the graph
	 * @param nid it is the name of the node whose configuration must be updated
	 * @param configuration it is the new configuration to set
	 * @return the updated Configuration element in case of success, otherwise null
	 */
	public Configuration updateConfiguration(long gid, String nid, Configuration configuration) {

		ConcurrentHashMap<String, Node> map = nodeByGraphId.get(gid);
		if(map == null)
			return null;
		Node node = map.get(nid);
		if(node == null)
			return null;
		node.setConfiguration(configuration);
		return configuration;
		
	}
	
	
	/**
	 * @param gid it is the id of the graph
	 * @param nid it is the name of the node whose configuration must be deleted
	 * @return the deleted Configuration element in case of success, otherwise null
	 */
	public Configuration deleteConfiguration(long gid, String nid) {
		
		ConcurrentHashMap<String, Node> map = nodeByGraphId.get(gid);
		if(map == null)
			return null;
		Node node = map.get(nid);
		if(node == null)
			return null;
		node.setConfiguration(null);
		
		return new Configuration();
	}
	
	
	/**
	 * @param gid it is the id of the graph
	 * @param constraints it is the set of constraints to created
	 * @return the created Constraints element in case of success, null for bad request, an empty Constraints element if constraints already existed
	 */
	public synchronized Constraints createConstraints(long gid, Constraints constraints) {
		
		ConcurrentHashMap<String, Node> nodesMap = nodeByGraphId.get(gid);
		if(nodesMap == null)
			return null;
		
		
		boolean badRequest = false;
		for(NodeConstraints.NodeMetrics nm : constraints.getNodeConstraints().getNodeMetrics()){
			if(!nodesMap.containsKey(nm.getNode())){
				badRequest = true;
			}
		}
		
		for(LinkConstraints.LinkMetrics lm : constraints.getLinkConstraints().getLinkMetrics()){
			if(!nodesMap.containsKey(lm.getSrc()) || !nodesMap.containsKey(lm.getDst())){
				badRequest = true;
			}
		}
		
		if(badRequest){
			return null;
		}
		
		if(constraintsByGraphId.putIfAbsent(gid, constraints) == null){
			return constraints;
		} else {
			Constraints empty = new Constraints();
			return empty;
		}

	}

	
	/**
	 * @param gid it is the id of the graph
	 * @return the constraints of the graph it they exist, otherwise null
	 */
	public Constraints getConstraints(long gid) {
		return constraintsByGraphId.get(gid);
	}

	
	/**
	 * @param gid it is the id of the graph 
	 * @param constraints it is the set of the new constraints 
	 * @return the created Constraints element in case of success, null if not found, an empty Constraints element for a bad request
	 */
	public synchronized Constraints updateConstraints(long gid, Constraints constraints) {
		ConcurrentHashMap<String, Node> nodesMap = nodeByGraphId.get(gid);
		if(nodesMap == null)
			return null;
		
		boolean badRequest = false;
		for(NodeConstraints.NodeMetrics nm : constraints.getNodeConstraints().getNodeMetrics()){
			if(!nodesMap.containsKey(nm.getNode())){
				badRequest = true;
			}
		}
		

		for(LinkConstraints.LinkMetrics lm : constraints.getLinkConstraints().getLinkMetrics()){
			if(!nodesMap.containsKey(lm.getSrc()) || !nodesMap.containsKey(lm.getDst())){
				badRequest = true;
			}
		}
		
		if(badRequest){
			Constraints empty = new Constraints();
			return empty;
		}
		
		if(constraintsByGraphId.replace(gid, constraints) != null){
			return constraints;
		} else {
			return null;
		}
	}

	
	/**
	 * @param gid it is the id of the graph
	 * @return the deleted Constraints element if it existed, otherwise null
	 */
	public Constraints deleteConstraints(long gid) {
		return constraintsByGraphId.remove(gid);
	}

	
	/* Policy Database */

	
	/**
	 * @param rid it is the id of the requirements set
	 * @param requirementsSet it is the requirements set to create
	 * @return the created PropertyDefinition element in case of success, otherwise null
	 */
	public PropertyDefinition createRequirementsSet(long rid, PropertyDefinition requirementsSet) {
		
		List<Property> rules = requirementsSet.getProperty();
		ConcurrentHashMap<Long, Property> rulesMap = new ConcurrentHashMap<Long, Property>();
		long i = 0;
		for(Property rule : rules) {
			rulesMap.put(++i, rule);
		}
		
		if(propertyByRequirementsSetId.putIfAbsent(rid, rulesMap) == null) {
			return requirementsSet;
		}
		else 
			return null;
	}

	/**
	 * @param beforeInclusive it is the starting index of the list
	 * @param afterInclusive it is the ending index of the list
	 * @return a collection of requirements sets
	 */
	public Collection<PropertyDefinition> getRequirementsSet(int beforeInclusive, int afterInclusive) {
		
		Collection<PropertyDefinition> list = new ArrayList<>();
		
		for(long i = beforeInclusive; i <= afterInclusive; i++){
			PropertyDefinition p = new PropertyDefinition();
			ConcurrentHashMap<Long, Property> rulesMap = propertyByRequirementsSetId.get(i);
			
			if(rulesMap != null) {
				p.getProperty().addAll(rulesMap.values());
				list.add(p);
			}

		}
		
		return list;
	}

	
	/**
	 * @return true if at least one requirements set was deleted, otherwise false
	 */
	public synchronized boolean deleteRequirementsSet() {
		
		if(propertyByRequirementsSetId.isEmpty())
			return false;
		
		propertyByRequirementsSetId.clear();
		lastRequirementsSetID = 0;
		
		return true;
	}

	
	/**
	 * @param rid it is the id of the requirements set
	 * @param requirementsSet it is the requirements set to update
	 * @return the updated PropertyDefinition element in case of success, otherwise null
	 */
	public PropertyDefinition updateRequirementsSet(long rid, PropertyDefinition requirementsSet) {

		ConcurrentHashMap<Long, Property> rulesMap = new ConcurrentHashMap<Long, Property>();
		long i = 0;
		for(Property rule : requirementsSet.getProperty()) {
			rulesMap.put(++i, rule);
		}
		
		ConcurrentHashMap<Long, Property> result = propertyByRequirementsSetId.computeIfPresent(rid, (k, old) -> rulesMap);
		if(result == null)
			return null;
		
		return requirementsSet;
	}

	
	/**
	 * @param rid it is the id of the requirements set to retrieve
	 * @return the PropertyDefinition element if it exists, otherwise null
	 */
	public PropertyDefinition getRequirementsSet(long rid) {
		
		PropertyDefinition policy = new PropertyDefinition();
		ConcurrentHashMap<Long, Property> rulesMap = propertyByRequirementsSetId.get(rid);
		
		if(rulesMap == null)
			return null;

		policy.getProperty().addAll(rulesMap.values());
		return policy;
	}

	

	/**
	 * @param rid it is the id of the requirements set to delete
	 * @return the deleted PropertyDefinition element if existed, otherwise null
	 */
	public PropertyDefinition deleteRequirementsSet(long rid) {
		
		PropertyDefinition policy = new PropertyDefinition();

		ConcurrentHashMap<Long, Property> rulesMap = propertyByRequirementsSetId.remove(rid);
		if(rulesMap == null)
			return null;
		policy.getProperty().addAll(rulesMap.values());
		return policy;
	}

	
	/**
	 * @param rid it is the id of the requirements set
	 * @param property it is the property to create
	 * @return the created Property element
	 */
	public synchronized Long createProperty(long rid, Property rule) {
		
		ConcurrentHashMap<Long, Property> rulesMap = propertyByRequirementsSetId.get(rid);
		if(rulesMap == null)
			return new Long(0);
		Long pid = new Long(rulesMap.size());
		rulesMap.put(++pid, rule);
		return pid;
		
	}
	
	
	/**
	 * @param rid it is the id of the requirements set
	 * @param pid it is the id of the property to update
	 * @param property it is the new rule 
	 * @return the updated Property element if existed, otherwise null
	 */
	public synchronized Property updateProperty(long rid, long pid, Property property) {
		
		ConcurrentHashMap<Long, Property> rulesMap = propertyByRequirementsSetId.get(rid);
		if(rulesMap == null)
			return null;
		if(rulesMap.replace(pid, property) != null)
			return property;
		else 
			return null;
	}

	
	
	/**
	 * @param rid it is the id of the requirements set
	 * @param pid it is the id of the property to retrieve
	 * @return the Property element if exists, otherwise null
	 */
	public Property getProperty(long rid, long pid) {
		
		ConcurrentHashMap<Long, Property> rulesMap = propertyByRequirementsSetId.get(rid);
		if(rulesMap == null)
			return null;
		
		return rulesMap.get(pid);
	}
	

	/**
	 * @param rid it is the id of the requirements set
	 * @param pid it is the id of the property to deleted
	 * @return the deleted Property element if existed, otherwise null
	 */
	public Property deleteProperty(long rid, long pid) {
		
		ConcurrentHashMap<Long, Property> rulesMap = propertyByRequirementsSetId.get(rid);
		if(rulesMap == null)
			return null;
		
		return rulesMap.remove(pid);
	}
	
	/* Substrate networks database */

	
	/**
	 * @param sid it is the id of the substrate network to create
	 * @param substrate it is the substrate network to create
	 * @return the created Hosts element in case of success, otherwise null
	 */
	public Hosts createSubstrate(long sid, Hosts substrate) {
		List<Host> hosts = substrate.getHost();
		ConcurrentHashMap<String, Host> hostsMap = new ConcurrentHashMap<String, Host>();
		for(Host host : hosts) {
			hostsMap.put(host.getName(), host);
		}
		
		if(hostBySubstrateId.putIfAbsent(sid, hostsMap) == null) {
			return substrate;
		}
		else 
			return null;
	}

	
	/**
	 * @param beforeInclusive it is the starting index of the list
	 * @param afterInclusive it is the ending index of the list
	 * @return a collection of substrate networks
	 */
	public Collection<Hosts> getSubstrates(int beforeInclusive, int afterInclusive) {

		Collection<Hosts> list = new ArrayList<>();
		
		for(long i = beforeInclusive; i <= afterInclusive; i++){
			Hosts h = new Hosts();
			ConcurrentHashMap<String, Host> hostsMap = hostBySubstrateId.get(i);
			
			if(hostsMap != null) {
				h.getHost().addAll(hostsMap.values());
				list.add(h);
			}

		}
		
		return list;
	}

	
	/**
	 * @return true if at least a substrate network was deleted, otherwise null
	 */
	public synchronized boolean deleteSubstrates() {
		
		if(hostBySubstrateId.isEmpty())
			return false;
		
		hostBySubstrateId.clear();
		connectionsBySubstrateId.clear();
		lastSubstrateID = 0;
		
		return true;
	}

	
	/**
	 * @param sid it is the id of the substrate network to update
	 * @param substrate it is the new substrate network
	 * @return the updated Hosts element in case of success, otherwise null
	 */
	public synchronized Hosts updateSubstrate(long sid, Hosts substrate) {
		
		ConcurrentHashMap<String, Host> hostsMap = new ConcurrentHashMap<String, Host> ();
	
		for(Host h : substrate.getHost()) {
			hostsMap.put(h.getName(), h);
		}
		
		ConcurrentHashMap<String, Host> result = hostBySubstrateId.computeIfPresent(sid, (k, old) -> hostsMap);
		if(result == null)
			return null;
		
		connectionsBySubstrateId.remove(sid);
		return substrate;
	}

	
	/**
	 * @param sid it is the id of the substrate network to retrieve
	 * @return the Hosts element if it exists, otherwise null
	 */
	public Hosts getSubstrate(long sid) {
		
		Hosts hosts = new Hosts();
		ConcurrentHashMap<String, Host> hostsMap = hostBySubstrateId.get(sid);
		
		if(hostsMap == null)
			return null;

		hosts.getHost().addAll(hostsMap.values());
		return hosts;
	}

	
	/**
	 * @param sid it is the id of the substrate network to delete
	 * @return the deleted Hosts element if it existed, otherwise null
	 */
	public synchronized Hosts deleteSubstrate(long sid) {
		
		Hosts hosts = new Hosts();

		ConcurrentHashMap<String, Host> hostsMap = hostBySubstrateId.remove(sid);
		if(hostsMap == null)
			return null;
		hosts.getHost().addAll(hostsMap.values());
		connectionsBySubstrateId.remove(sid);
		return hosts;
	}
	

	/**
	 * @param sid it is the id of the substrate network
	 * @param hid it is the name of the host to create
	 * @param host it is the host to create
	 * @return the created Host element in case of success, 
	 */
	public Host createHost(long sid, String hid, Host host) {
		ConcurrentHashMap<String, Host> hostsMap = hostBySubstrateId.remove(sid);
		if(hostsMap == null)
			return null;
		host.setName(hid);		
		if(hostsMap.putIfAbsent(hid, host) == null)
			return host;
		else {
			Host empty = new Host();
			empty.setName(null);
			return empty;
		}
	}

	/**
	 * @param sid it is the id of the substrate network
	 * @param hid it is the id of the host to update
	 * @param host it is the new host
	 * @return return the updated Host element in case of success, otherwise null
	 */
	public Host updateHost(long sid, String hid, Host host) {
		
		ConcurrentHashMap<String, Host> hostsMap = hostBySubstrateId.remove(sid);
		if(hostsMap  == null)
			return null;
		host.setName(hid);
		if(hostsMap .replace(hid, host) != null)
			return host;
		else 
			return null;
	}

	
	/**
	 * @param sid it is the id of the substrate network
	 * @param hid it is the id of the host to retrieve
	 * @return return the Host element in case of success, otherwise null
	 */
	public Host getHost(long sid, String hid) {
		
		ConcurrentHashMap<String, Host> hostsMap = hostBySubstrateId.remove(sid);
		if(hostsMap == null)
			return null;
		
		return hostsMap.get(hid);
	}

	
	/**
	 * @param sid it is the id of the substrate network
	 * @param hid it is the id of the host to delete
	 * @return return the Host element in case of success, otherwise null
	 */
	public synchronized Host deleteHost(long sid, long hid) {
		
		ConcurrentHashMap<String, Host> hostsMap = hostBySubstrateId.remove(sid);
		if(hostsMap == null)
			return null;
		
		Connections connections = connectionsBySubstrateId.get(sid);
		connections.getConnection().removeIf(c -> c.getSourceHost().equals(hid) || c.getDestHost().equals(hid));
		
		return hostsMap.remove(hid);
	}
	
	
	/**
	 * @param sid it is the id of the substrate network
	 * @param connections they are the connections to insert to the substrate network
	 * @return the created Connections object in case of success, null for bad request, an empty Connection object if connections for the substrate already existed
	 */
	public synchronized Connections createConnections(long sid, Connections connections) {
		ConcurrentHashMap<String, Host> hostsMap = hostBySubstrateId.get(sid);
		if(hostsMap == null)
			return null;
		
		boolean badRequest = false;
		for(Connection c : connections.getConnection()){
			if(!hostsMap.contains(c.getSourceHost()) || !hostsMap.contains(c.getDestHost())){
				badRequest = true;
			}
		}
		
		if(badRequest){
			return null;
		}
		
		if(connectionsBySubstrateId.putIfAbsent(sid, connections) == null){
			return connections;
		} else {
			Connections empty = new Connections();
			return empty;
		}
	}

	
	/**
	 * @param sid it is the id of the substrate network
	 * @param connections they are the new connections to update
	 * @return the updated Connections in case of success, null for not found, an empty Connections object for bad request
	 */
	public synchronized Connections updateConnections(long sid, Connections connections) {
		ConcurrentHashMap<String, Host> hostsMap = hostBySubstrateId.get(sid);
		if(hostsMap == null)
			return null;
		
		boolean badRequest = false;
		for(Connection c : connections.getConnection()){
			if(!hostsMap.contains(c.getSourceHost()) || !hostsMap.contains(c.getDestHost())){
				badRequest = true;
			}
		}
		

		if(badRequest){
			Connections empty = new Connections();
			return empty;
		}
		
		if(connectionsBySubstrateId.replace(sid, connections) != null){
			return connections;
		} else {
			return null;
		}
	}

	
	/**
	 * @param sid it is the id of the substrate network
	 * @return the Connections object is present, otherwise null
	 */
	public Connections getConnections(long sid) {
		return connectionsBySubstrateId.get(sid);
	}

	
	/**
	 * @param sid it is the id of the substrate network
	 * @return the removed Connections object is present, otherwise null
	 */
	public Connections deleteConnections(long sid) {
		return connectionsBySubstrateId.remove(sid);
	}
	
	
	
	/* Functions */

	
	/**
	 * @param function it is the function to create
	 * @return the created FunctionalTypes element in case of success, otherwise null
	 */
	public String createFunction(String function) {
		FunctionalTypes ft =  null;
		
		try{
			ft = FunctionalTypes.fromValue(function);
		} catch(IllegalArgumentException e) {
			return null;
		}
		

		if(ft != null &&networkFunctionMap.putIfAbsent(function, FunctionalTypes.FIREWALL) == null){
			return function;
		} else {
			return null;
		}
	}

	/**
	 * @param fid it is the id of the function to delete 
	 * @return the deleted FunctionalTypes element in case of success, otherwise null
	 */
	public FunctionalTypes deleteFunction(String fid) {
		
		return  networkFunctionMap.remove(fid);
		
	}

	
	/**
	 * @param fid it is the id of the function to retrieve
	 * @return the FunctionalTypes element if it exists, otherwise null
	 */
	public boolean getFunction(String fid) {
		return networkFunctionMap.containsKey(fid);
	}

	
	/* Simulations Database */
	
	/**
	 * @param smid it is the id of the simulation
	 * @return the NFV (simulation result) 
	 */
	public NFV getSimulationResult(long smid) {
		return simulationResultsMap.get(smid);
	}

	public void addSimulationResult(NFV nfv, long smid) {
		simulationResultsMap.put(smid, nfv);
	}


	
}