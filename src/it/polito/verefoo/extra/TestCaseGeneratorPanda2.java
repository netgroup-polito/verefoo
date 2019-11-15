package it.polito.verefoo.extra;


import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import it.polito.verefoo.jaxb.*;
import it.polito.verefoo.utils.Tuple;

// Auxiliary class to generate  test cases for performance tests (used by TestPerformanceScalability)
public class TestCaseGeneratorPanda2 {

 
	NFV nfv;
	String name;
	
	/*Additional variables */

	Random rand = null;
	

	NFV originalNFV;

	
	Set<String> allIPs;
	List<Node> allClients;
	List<Node> allServers;

	// type 0 for public 1 for private 2 for quarantine
	// numbers

	public TestCaseGeneratorPanda2(String name, int type, int numberPublic, int numberPrivate, int numberQuarantined, int seed) {
		this.name = name;
		this.rand = new Random(seed);  

		allClients = new ArrayList<Node>();
		allServers = new ArrayList<Node>();

		allIPs = new HashSet<String>();
		nfv = generateNFV(type, numberPublic, numberPrivate, numberQuarantined, rand);
	}
	
	
	
	public NFV changeIP(int type,int numberPublic,  int numberPrivate, int numberQuarantined, int seed) {
		this.rand = new Random(seed);
		allClients = new ArrayList<Node>();
		allServers = new ArrayList<Node>();
		
		allIPs = new HashSet<String>();
		return generateNFV(type, numberPublic, numberPrivate, numberQuarantined, rand);
	}
	
	
	
	private String createIP() {
		String ip;
		int first, second, third, forth;
		first = rand.nextInt(256);
		if(first == 0) first++;
		second = rand.nextInt(256);
		third = rand.nextInt(256);
		forth = rand.nextInt(256);
		ip = new String(first + "." + second + "." + third + "." + forth);
		if(rand.nextBoolean()) {
			if(rand.nextBoolean())
				ip = new String(first + "." + first + "." + first + "." + first);
			else {
				if(rand.nextBoolean())
					ip = new String(second + "." + second + "." + second + "." + second);
				else {
						ip = new String(third + "." + third + "." + third + "." + third);
				}
			}
		}
		return ip;
	}
	
	private String createRandomIP() {
		boolean notCreated = true;
		String ip = null;
		while(notCreated) {
			ip = createIP();
			if(!allIPs.contains(ip)) {
				notCreated = false;
				allIPs.add(ip);
			}
		}
		
		return ip;
		
	}
	


	public NFV generateNFV(int type, int numberPublic, int numberPrivate, int numberQuarantined, Random rand) {
	
		
		/* Creation of the test */
		
		NFV nfv = new NFV();
		Graphs graphs = new Graphs();
		PropertyDefinition pd = new PropertyDefinition();
		Constraints cnst = new Constraints();
		NodeConstraints nc = new NodeConstraints();
		LinkConstraints lc = new LinkConstraints();
		cnst.setNodeConstraints(nc);
		cnst.setLinkConstraints(lc);
		nfv.setGraphs(graphs);
		nfv.setPropertyDefinition(pd);
		nfv.setConstraints(cnst);
		Graph graph = new Graph();
		graph.setId((long) 0);
		
		
		//creation of the servers
		String IPServer = createRandomIP();
		Node server = new Node();
		server.setFunctionalType(FunctionalTypes.WEBSERVER);
		server.setName(IPServer);
		Configuration confS = new Configuration();
		confS.setName("confB");
		Webserver ws = new Webserver();
		ws.setName(server.getName());
		confS.setWebserver(ws);
		server.setConfiguration(confS);
		allServers.add(server);

		//creation of the client
		
		String IPClient = createRandomIP();
		Node client = new Node();
		client.setFunctionalType(FunctionalTypes.WEBCLIENT);
		client.setName(IPClient);
			
		Configuration confC = new Configuration();
		confC.setName("confA");
		Webclient wc = new Webclient();
		wc.setNameWebServer(server.getName());
		confC.setWebclient(wc);
		client.setConfiguration(confC);
		allClients.add(client);
		
		
		
		//creation of the firewall with all the rules
		Node fw = new Node();
		String ipCentral = createRandomIP();
		fw.setName(ipCentral);
		fw.setFunctionalType(FunctionalTypes.FIREWALL);
		Configuration confF = new Configuration();
		confF.setName("conf");
        Firewall sf = new Firewall();
		sf.setDefaultAction(ActionTypes.ALLOW);
		
		//type 0 : public, no DENY rules
		if(type == 0){
			numberPublic--;
		}
		
		
		//type 1 : private, a DENY rule for traffic from client (Internet)
		else if (type == 1) {
			Elements el = new Elements();
			el.setAction(ActionTypes.DENY);
			el.setSource(client.getName());
			el.setDestination(server.getName());
			sf.getElements().add(el);
			numberPrivate--;
		}
		
		//type 2 : quarantined, two DENY rules, one for each direction
		else if (type == 2) {
			Elements el = new Elements();
			el.setAction(ActionTypes.DENY);
			el.setSource(client.getName());
			el.setDestination(server.getName());
			
			sf.getElements().add(el);
			el = new Elements();
			el.setAction(ActionTypes.DENY);
			el.setDestination(client.getName());
			el.setSource(server.getName());
			
			sf.getElements().add(el);
			numberQuarantined--;
		}
	
		//create all the other rules
		for(int i = 0; i < numberPrivate; i++) {
			Elements el = new Elements();
			el.setAction(ActionTypes.DENY);
			el.setSource(createRandomIP());
			el.setDestination(createRandomIP());
			sf.getElements().add(el);
		}
		
		for(int i = 0; i < numberQuarantined; i++) {
			String ipv1 = createRandomIP();
			String ipv2 = createRandomIP();
			
			Elements el = new Elements();
			el.setAction(ActionTypes.DENY);
			el.setSource(ipv1);
			el.setDestination(ipv2);
			sf.getElements().add(el);
			el = new Elements();
			el.setAction(ActionTypes.DENY);
			el.setDestination(ipv1);
			el.setSource(ipv2);
			sf.getElements().add(el);
		}
		
		if(sf.getElements().isEmpty()) {
			//add at least one rule
			Elements el = new Elements();
			el.setAction(ActionTypes.DENY);
			el.setSource("5.5.5.5");
			el.setDestination("6.6.6.6");
			sf.getElements().add(el);
		}
		
		confF.setFirewall(sf);
		fw.setConfiguration(confF);
		
		//creation of the forwarder
		Node forwarder = new Node();
		String ipForw = createRandomIP();
		forwarder.setName(ipForw);
		forwarder.setFunctionalType(FunctionalTypes.FORWARDER);
		Configuration confN = new Configuration();
		confN.setName("conN");
		Forwarder frw = new Forwarder();
		confN.setForwarder(frw);
		forwarder.setConfiguration(confN);
		
		//attach client to firewall
		Neighbour nextNeigh = new Neighbour();
		nextNeigh.setName(fw.getName());
		client.getNeighbour().add(nextNeigh);
		Neighbour prevNeigh = new Neighbour();
		prevNeigh.setName(client.getName());
		fw.getNeighbour().add(prevNeigh);
		
		//attach firewall to forwarder
		nextNeigh = new Neighbour();
		nextNeigh.setName(forwarder.getName());
		fw.getNeighbour().add(nextNeigh);
		prevNeigh = new Neighbour();
		prevNeigh.setName(fw.getName());
		forwarder.getNeighbour().add(prevNeigh);
		
		//attach forwarder to server
		nextNeigh = new Neighbour();
		nextNeigh.setName(server.getName());
		forwarder.getNeighbour().add(nextNeigh);
		prevNeigh = new Neighbour();
		prevNeigh.setName(forwarder.getName());
		server.getNeighbour().add(prevNeigh);
		
		
		//add the nodes in the graph
		graph.getNode().add(client);
		graph.getNode().add(fw);
		graph.getNode().add(forwarder);
		graph.getNode().add(server);

		
		//create the policy

		createPolicy(PName.REACHABILITY_PROPERTY, nfv, graph, server.getName(), client.getName());
		
		nfv.getGraphs().getGraph().add(graph);
			
		
		return nfv;
	}

	private void createPolicy(PName type, NFV nfv, Graph graph, String IPClient, String IPServer) {

		Property property = new Property();
		property.setName(type);
		property.setGraph((long) 0);
		property.setSrc(IPClient);
		property.setDst(IPServer);
		nfv.getPropertyDefinition().getProperty().add(property);
	}
	
	
	public NFV getNfv() {
		return nfv;
	}

	public void setNfv(NFV nfv) {
		this.nfv = nfv;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}



}
