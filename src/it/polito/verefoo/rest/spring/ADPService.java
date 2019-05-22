package it.polito.verefoo.rest.spring;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;

import it.polito.verefoo.jaxb.*;

public class ADPService {
	

	private ADPDatabase db = ADPDatabase.getDB();


	
	public ADPService() {
	}
	
	/* Graph Service Methods*/
	
	public long getNextGraphId() {
		return ADPDatabase.getNextGraphId();
	}
	
	public Graph createGraph(long id, Graph graph) {
		return db.createGraph(id, graph);
	}
	
	public Graphs getGraphs(int beforeInclusive, int afterInclusive) {
		Graphs graphs = new Graphs();
		List<Graph> list = graphs.getGraph();
		if(afterInclusive > beforeInclusive + 9) 
			afterInclusive = beforeInclusive + 9;
		list.addAll(db.getGraphs(beforeInclusive,afterInclusive));
		return graphs;
	}
	
	public Graphs deleteGraphs() {
		return db.deleteGraphs();
	}

	public Graph getGraph(long gid) {
		return db.getGraph(gid);
	}

	public Graph updateGraph(long gid, Graph graph) {
		return db.updateGraph(gid, graph);
	}

	public Graph deleteGraph(long gid) {
		return db.deleteGraph(gid);
	}
	

	public Node getNode(long gid, String nid) {
		return db.getNode(gid, nid);
	}


	public Node createNode(long gid, Node node, String nid) {
		return db.createNode(gid, node, nid);
	}

	public Node updateNode(long gid, String nid, Node node) {
		return db.updateNode(gid, nid, node);
	}

	public Node deleteNode(long gid, String nid) {
		return db.deleteNode(gid, nid);
	}
	
	public Neighbour addNeighbour(long gid, String nid, Neighbour neighbour) {
		return db.addNeighbour(gid, nid, neighbour);
	}

	public Neighbour deleteNeighbour(long gid, String nid, String neighbour) {
		return db.deleteNeighbour(gid, nid, neighbour);
	}
	
	public Configuration updateConfiguration(long gid, String nid, Configuration configuration) {
		return db.updateConfiguration(gid, nid, configuration);
	}

	public Configuration deleteConfiguration(long gid, String nid) {
		return db.deleteConfiguration(gid, nid);
	}
	
	public Constraints createConstraints(long gid, Constraints constraints) {
		return db.createConstraints(gid, constraints);
	}

	public Constraints getConstraints(long gid) {
		return db.getConstraints(gid);
	}

	public Constraints updateConstraints(long gid, Constraints constraints) {
		return db.updateConstraints(gid, constraints);
	}

	public Constraints deleteConstraints(long gid) {
		return db.deleteConstraints(gid);
	}


	
	/* Security Requirements */ 

	public long getNextRequirementsSetId() {
		return ADPDatabase.getNextRequirementsSetId();
	}

	public PropertyDefinition createRequirementsSet(long rid, PropertyDefinition requirementsSet) {
		return db.createRequirementsSet(rid, requirementsSet);
	}

	public Collection<PropertyDefinition> getRequirementsSets(int beforeInclusive, int afterInclusive) {
		return db.getRequirementsSet(beforeInclusive, afterInclusive);
	}

	public boolean deleteRequirementsSets() {
		return db.deleteRequirementsSet();
	}

	public PropertyDefinition updateRequirementsSet(long rid, PropertyDefinition requirementsSet) {
		return db.updateRequirementsSet(rid, requirementsSet);
	}

	public PropertyDefinition getRequirementsSet(long rid) {
		return db.getRequirementsSet(rid);
	}
	
	public PropertyDefinition deleteRequirementsSet(long rid) {
		return db.deleteRequirementsSet(rid);
	}

	public Property updateProperty(long rid, long pid, Property property) {
		return db.updateProperty(rid, pid, property);
	}

	public Long createProperty(long rid, Property property) {
		return db.createProperty(rid, property);
	}

	public Property getProperty(long rid, long pid) {
		return db.getProperty(rid, pid);
	}

	public Property deleteProperty(long rid, long pid) {
		return db.deleteProperty(rid, pid);
	}

	
	/* Substrate Networks */ 
	
	public long getNextSubstrateId() {
		return ADPDatabase.getNextSubstrateId();
	}

	public Hosts createSubstrate(long sid, Hosts substrate) {
		return db.createSubstrate(sid, substrate);
	}

	public Collection<Hosts> getSubstrates(int beforeInclusive, int afterInclusive) {
		return db.getSubstrates(beforeInclusive, afterInclusive);
	}

	public boolean deleteSubstrates() {
		return db.deleteSubstrates();
	}

	public Hosts updateSubstrate(long sid, Hosts substrate) {
		return db.updateSubstrate(sid, substrate);
	}

	public Hosts getSubstrate(long sid) {
		return db.getSubstrate(sid);
	}

	public Hosts deleteSubstrate(long sid) {
		return db.deleteSubstrate(sid);
	}

	public Host createHost(long sid, String hid, Host host) {
		return db.createHost(sid, hid, host);
	}

	public Host updateHost(long sid, String hid, Host host) {
		return db.updateHost(sid, hid, host);
	}

	public Host getHost(long sid, String hid) {
		return db.getHost(sid, hid);
	}

	public Host deleteHost(long sid, long hid) {
		return db.deleteHost(sid, hid);
	}

	public Connections createConnections(long sid, Connections connections) {
		return db.createConnections(sid, connections);
	}

	public Connections updateConnections(long sid, Connections connections) {
		return db.updateConnections(sid, connections);
	}

	public Connections getConnections(long sid) {
		return db.getConnections(sid);
	}

	public Connections deleteConnections(long sid) {
		return db.deleteConnections(sid);
	}

	/* Functions */
	
	public String createFunction(String function) {
		return db.createFunction(function);
	}

	public FunctionalTypes deleteFunction(String fid) {
		return db.deleteFunction(fid);
	}
	
	public boolean getFunction(String fid) {
		return db.getFunction(fid);
	}


	/* Simulations */
	
	public long getNextSimulationId() {
		return ADPDatabase.getNextSimulationId();
	}

	public NFV getSimulationResult(long smid) {
		return db.getSimulationResult(smid);
	}

	public void addSimulationResult(NFV nfv, long smid) {
		db.addSimulationResult(nfv, smid);
	}

	
}