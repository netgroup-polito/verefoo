package it.polito.verefoo.rest.spring;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

//import org.springframework.core.io.Resource;

import it.polito.verefoo.jaxb.Configuration;
import it.polito.verefoo.jaxb.Elements;
import it.polito.verefoo.jaxb.Firewall;
import it.polito.verefoo.jaxb.FunctionalTypes;
import it.polito.verefoo.jaxb.Graph;
import it.polito.verefoo.jaxb.NFV;
import it.polito.verefoo.jaxb.Node;

public class FDWService {
	private FWDDatabase db = FWDDatabase.getDatabase();

	public FDWService() {

	}

	public long getNextNodeId() {
		return db.getNextNodeId();
	}

	public List<Node> createNodesByGraph(long id, Graph graph) {
		List<Node> nodes;
		nodes = graph.getNode().stream().filter(x -> x.getFunctionalType().equals(FunctionalTypes.FIREWALL))
				.collect(Collectors.toList());
		for (int index = 0; index < nodes.size(); index++) {
		}
		return null;
	}

	public Node createNode(Long nid, Node node) {
		return db.createNode(nid, node);
	}

	public List<Node> getNodes(int beforeInclusive, int afterInclusive) {
		return db.getNodes(beforeInclusive, afterInclusive);
	}

	public List<Node> deleteNodes() {
		return db.deleteNodes();
	}

	public Node updateNode(long nid, Node node) {
		return db.updateNode(nid, node);
	}

	public Node getNode(long nid) {
		return db.getNode(nid);
	}

	public Node deleteNode(long nid) {
		return db.deleteNode(nid);
	}

	public Configuration updateConfiguration(long nid, Configuration configuration) {
		return db.updateConfiguration(nid, configuration);
	}

	public Firewall createFirewall(long nid, Firewall firewall) {
		return db.createFirewall(nid, firewall);
	}

	public long getNextElementId(long nid) {
		return db.getNextElementId();
	}

	public Elements createPolicy(long nid, long eid, Elements element) {
		return db.createPolicy(nid, eid, element);
	}

	public Elements updatePolicy(long eid, long nid, Elements policy) {
		return db.updatePolicy(nid, eid, policy);
	}

	public Elements getPolicy(long eid, long nid) {
		return db.getPolicy(nid, eid);
	}

	public Elements deletePolicy(long eid, long nid) {
		return db.deletePolicy(nid, eid);
	}
	
//	public Resource loadFileAsResource(long nid,String type)  {
//		 Resource resource = null;
//		
//		switch(type) 
//        { 
//		case "fortinet":
//			resource = db.createFortinetFirewall(nid);
//		break;
//		case "ipfw":
//			resource = db.createIPFWFirewall(nid);
//			break;
//		case "iptables":
//			resource = db.createIptablesFirewall(nid);
//			break;
//		case "opnvswitch":
//			resource = db.createOpnvswitchFirewall(nid);
//			break;
//		case "bpf_iptables":
//			resource = db.createBPFFirewall(nid);
//			break;
//		default:
//			return null;
//		
//        }
//    
//	return resource;
//	}

	public File getFile(long nid, String type) {
		 File resource = null;
			
		switch(type) 
       { 
		case "fortinet":
			resource = db.getFortinetFirewall(nid);
		break;
		case "ipfw":
			resource = db.getIPFWFirewall(nid);
			break;
		case "iptables":
			resource = db.getIptablesFirewall(nid);
			break;
		case "opnvswitch":
			resource = db.getOpnvswitchFirewall(nid);
			break;
		case "bpf_iptables":
			resource = db.getBPFFirewall(nid);
			break;
		default:
			return null;
		
       }
   
	return resource;
	}

	public String loadNFV(NFV nfv) {
		return db.loadNFV(nfv);
	}

	public String loadGraph(Graph g) {
		return db.loadGraph(g);
	}
	
	


}
