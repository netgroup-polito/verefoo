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
public class TestCaseGeneratorBudapest2 {

 
	NFV nfv;
	String name;
	
	/*Additional variables */
	int countC = 1;
	int countAP = 1;
	int countS = 1;
	int countP = 1;
	Random rand = null;
	
	String IPC;
	String IPAP;
	String IPS;
	 NFV originalNFV;
	private int numberAllocationPlaces;
	private int numberReachPolicies;
	private int numberIsPolicies;
	private int numberNAT;
	private int numberLB;
	
	Set<String> allIPs;
	List<Node> allClients;
	List<Node> allServers;
	List<Node> allAPS;
	List<Node> allAPC;
	List<Node> allNATs;
	List<Node> allLBs;
	List<Tuple<String, Node>> lastAPs;
	
	
	public TestCaseGeneratorBudapest2(String name, int numberAllocationPlaces, int numberReachPolicies, int numberIsPolicies, int numberNAT, int numberLB, int seed) {
		this.name = name;
		this.rand = new Random(seed); 


		allClients = new ArrayList<Node>();
		allServers = new ArrayList<Node>();
		allAPS = new ArrayList<Node>();
		allAPC = new ArrayList<Node>();
		allNATs = new ArrayList<Node>();
		allLBs = new ArrayList<Node>();
		lastAPs = new ArrayList<Tuple<String, Node>>();

		allIPs = new HashSet<String>();
		nfv = generateNFV(numberAllocationPlaces, numberReachPolicies, numberIsPolicies, numberNAT, numberLB, rand);
	}
	
	
	
	public NFV changeIP(int numberAllocationPlaces, int numberReachPolicies, int numberIsPolicies, int numberNAT, int numberLB, int seed) {
		this.rand = new Random(seed);
		allClients = new ArrayList<Node>();
		allServers = new ArrayList<Node>();
		allAPS = new ArrayList<Node>();
		allAPC = new ArrayList<Node>();
		allNATs = new ArrayList<Node>();
		allLBs = new ArrayList<Node>();
		lastAPs = new ArrayList<Tuple<String, Node>>();

		allIPs = new HashSet<String>();
		return generateNFV(numberAllocationPlaces, numberReachPolicies, numberIsPolicies, numberNAT, numberLB, rand);
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
	


	public NFV generateNFV(int numberAllocationPlaces, int numberReachPolicies, int numberIsPolicies, int numberNAT, int numberLB, Random rand) {
		
		int numberPolicies = numberReachPolicies + numberIsPolicies;
		
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
		for(int i = 0; i < numberAllocationPlaces/2; i++) {
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
		}
		
		String firstIPServer = allServers.get(0).getName();
		
		//creation of the clients
		for(int i = 0; i < numberAllocationPlaces/2; i++) {
			String IPClient = createRandomIP();
			Node client = new Node();
			client.setFunctionalType(FunctionalTypes.WEBCLIENT);
			client.setName(IPClient);
			
			Configuration confC = new Configuration();
			confC.setName("confA");
			Webclient wc = new Webclient();
			wc.setNameWebServer(firstIPServer);
			confC.setWebclient(wc);
			client.setConfiguration(confC);
			allClients.add(client);
		}
		
		
		//central forwarder
		Node central = new Node();
		String ipCentral = createRandomIP();
		central.setName(ipCentral);
		central.setFunctionalType(FunctionalTypes.FORWARDER);
	
		
		//creation of the APs attached to servers
		for(int i = 0; i < numberAllocationPlaces/2; i++) {
			String ip = createRandomIP();
			Node ap = new Node();
			ap.setName(ip);
			allAPS.add(ap);
		}
		
		
		//creation of the APs attached to the clients
				for(int i = 0; i < numberAllocationPlaces/2; i++) {
					String ip = createRandomIP();
					Node ap = new Node();
					ap.setName(ip);
					allAPC.add(ap);
				}
		
		
		
		//attach everything
		for(int i = 0; i < numberAllocationPlaces/2; i++) {
			Node c = allClients.get(i);
			Node s = allServers.get(i);
			Node apC = allAPC.get(i);
			Node apS = allAPS.get(i);
			
			//attach the forwarder to the APC
			Neighbour nextNeigh = new Neighbour();
			nextNeigh.setName(central.getName());
			apC.getNeighbour().add(nextNeigh);
			Neighbour prevNeigh = new Neighbour();
			prevNeigh.setName(apC.getName());
			central.getNeighbour().add(prevNeigh);
			
			//attach the forwarder to the APS
			Neighbour nextNeigh2 = new Neighbour();
			nextNeigh2.setName(central.getName());
			apS.getNeighbour().add(nextNeigh2);
			Neighbour prevNeigh2 = new Neighbour();
			prevNeigh2.setName(apS.getName());
			central.getNeighbour().add(prevNeigh2);
			
			//attach the client to the APC
			Neighbour nextNeigh3 = new Neighbour();
			nextNeigh3.setName(apC.getName());
			c.getNeighbour().add(nextNeigh3);
			Neighbour prevNeigh3 = new Neighbour();
			prevNeigh3.setName(c.getName());
			apC.getNeighbour().add(prevNeigh3);
			
			//attach the server to the APS
			Neighbour nextNeigh4 = new Neighbour();
			nextNeigh4.setName(apS.getName());
			s.getNeighbour().add(nextNeigh4);
			Neighbour prevNeigh4 = new Neighbour();
			prevNeigh4.setName(s.getName());
			apS.getNeighbour().add(prevNeigh4);
			
			
			
		}
				
				

		for(int i = 0; i < numberPolicies/2; i++) {
			createPolicy(PName.ISOLATION_PROPERTY, nfv, graph, allClients.get(i).getName(), allServers.get(i).getName());
			createPolicy(PName.REACHABILITY_PROPERTY, nfv, graph, allServers.get(i).getName(), allClients.get(i).getName());
		}


		graph.getNode().addAll(allClients);
		graph.getNode().addAll(allServers);
		graph.getNode().addAll(allAPC);
		graph.getNode().addAll(allAPS);
		graph.getNode().add(central);
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

	public String getIPC() {
		return IPC;
	}

	public void setIPC(String iPC) {
		IPC = iPC;
	}

	public String getIPAP() {
		return IPAP;
	}

	public void setIPAP(String iPAP) {
		IPAP = iPAP;
	}

	public String getIPS() {
		return IPS;
	}

	public void setIPS(String iPS) {
		IPS = iPS;
	}

}
