package it.polito.verefoo.rest.spring;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.core.io.Resource;

import it.polito.verefoo.jaxb.Configuration;
import it.polito.verefoo.jaxb.Elements;
import it.polito.verefoo.jaxb.Firewall;
import it.polito.verefoo.jaxb.FunctionalTypes;
import it.polito.verefoo.jaxb.Graph;
import it.polito.verefoo.jaxb.Node;

public class FDWService {
	private FWDDatabase db = FWDDatabase.getDatabase();

	public FDWService() {

	}

	public long getNextNodeId() {
		// TODO Auto-generated method stub
		return db.getNextNodeId();
	}

	public List<Node> createNodesByGraph(long id, Graph graph) {
		List<Node> nodes;
		nodes = graph.getNode().stream().filter(x -> x.getFunctionalType().equals(FunctionalTypes.FIREWALL))
				.collect(Collectors.toList());
		for (int index = 0; index < nodes.size(); index++) {
			// id=service.getNextNodeId();
		}
		return null;
	}

	public Node createNode(Long nid, Node node) {
		// TODO Auto-generated method stub
		return db.createNode(nid, node);
	}

	public List<Node> getNodes(int beforeInclusive, int afterInclusive) {
		// TODO Auto-generated method stub
		return db.getNodes(beforeInclusive, afterInclusive);
	}

	public List<Node> deleteNodes() {
		// TODO Auto-generated method stub
		return db.deleteNodes();
	}

	public Node updateNode(long nid, Node node) {
		// TODO Auto-generated method stub
		return db.updateNode(nid, node);
	}

	public Node getNode(long nid) {
		// TODO Auto-generated method stub
		return db.getNode(nid);
	}

	public Node deleteNode(long nid) {
		// TODO Auto-generated method stub
		return db.deleteNode(nid);
	}

	public Configuration updateConfiguration(long nid, Configuration configuration) {
		// TODO Auto-generated method stub
		return db.updateConfiguration(nid, configuration);
	}

	public Firewall createFirewall(long nid, Firewall firewall) {
		// TODO Auto-generated method stub
		return db.createFirewall(nid, firewall);
	}

	public long getNextElementId(long nid) {
		// TODO Auto-generated method stub
		return db.getNextElementId();
	}

	public Elements createPolicy(long nid, long eid, Elements element) {
		// TODO Auto-generated method stub
		return db.createPolicy(nid, eid, element);
	}

	public Elements updatePolicy(long eid, long nid, Elements policy) {
		// TODO Auto-generated method stub
		return db.updatePolicy(nid, eid, policy);
	}

	public Elements getPolicy(long eid, long nid) {
		// TODO Auto-generated method stub
		return db.getPolicy(nid, eid);
	}

	public Elements deletePolicy(long eid, long nid) {
		// TODO Auto-generated method stub
		return db.deletePolicy(nid, eid);
	}
	
	public Resource loadFileAsResource(long nid,String type)  {
		 Resource resource = null;
		
		switch(type) 
        { 
		case "fortinet":
			resource = db.createFortinetFirewall(nid);
		break;
		case "ipfw":
			resource = db.createIPFWFirewall(nid);
			break;
		case "iptables":
			resource = db.createIptablesFirewall(nid);
			break;
		case "opnvswitch":
			resource = db.createOpnvswitchFirewall(nid);
			break;
		case "bpf_iptables":
			resource = db.createBPFFirewall(nid);
			break;
		default:
			return null;
		
        }
    
	return resource;
	}
	
	


}
