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
import java.util.stream.Collectors;

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
public class TestCaseGeneratorPandaIterative {

 
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
	private int numberReachPolicies;
	
	Set<String> allIPs;
	List<Node> allClients;
	List<Node> allServers;
	
	List<Tuple<String, Node>> lastAPs;
	
	
	public TestCaseGeneratorPandaIterative(String name, int numberReachPolicies,  int seed) {
		this.name = name;
		this.rand = new Random(seed); 


		allClients = new ArrayList<Node>(); 
		allServers = new ArrayList<Node>();
	
		lastAPs = new ArrayList<Tuple<String, Node>>();

		allIPs = new HashSet<String>();
		nfv = generateNFV( numberReachPolicies,rand);
	}
	
	
	
	public NFV changeIP(int numberReachPolicies,   int seed) {
		this.rand = new Random(seed);
		allClients = new ArrayList<Node>();
		allServers = new ArrayList<Node>();
		lastAPs = new ArrayList<Tuple<String, Node>>();

		allIPs = new HashSet<String>();
		return generateNFV(numberReachPolicies,  rand);
	}
	
	
	
	private String createIP() {
		String ip;
		int first, second, third, forth;
		first = 1;
		if(first == 0) first++;
		second = 1;
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
	


	public NFV generateNFV( int numberReachPolicies, Random rand) {
		//numberReachPolicies  = rand.nextInt(1000);
		int numberClient = 1;
		int numberServer = 1;
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
		for(int i = 0; i < numberServer; i++) {
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
		for(int i = 0; i < numberClient; i++) {
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
		
		
		
			// load balancer
			String ip = createRandomIP();
			Node loadBalancer = new Node();
			loadBalancer.setFunctionalType(FunctionalTypes.LOADBALANCER);
			loadBalancer.setName(ip);
			Configuration confN = new Configuration();
			confN.setName("confN");
			Loadbalancer lb = new Loadbalancer();
			lb.getPool().addAll(allClients.stream().map(c->c.getName()).collect(Collectors.toList())); //forwarder
			confN.setLoadbalancer(lb);
			loadBalancer.setConfiguration(confN);
			
			//links client to LB
			for(int j = 0; j < numberServer; j++) { 
				Node server = allServers.get(j);
				Neighbour nextNeigh = new Neighbour();
				nextNeigh.setName(server.getName());
				loadBalancer.getNeighbour().add(nextNeigh);
				Neighbour prevNeigh = new Neighbour();
				prevNeigh.setName(loadBalancer.getName());
				server.getNeighbour().add(prevNeigh);	
			}
			
			
			// firewall
			ip = createRandomIP();
			Node firewall = new Node();
			firewall.setName(ip);
			Configuration confF = new Configuration();
			confF.setName("confF");
			Firewall fw =  new Firewall();
			firewall.setFunctionalType(FunctionalTypes.FIREWALL);
			fw.setDefaultAction(ActionTypes.ALLOW);
			
			for (int i = 0; i < numberReachPolicies; i++) {
				Elements element =  new Elements();
				element.setAction(ActionTypes.DENY);
				element.setSource("1.1.1.1");
				element.setDestination("1.1.1.1");
				fw.getElements().add(element);
			}
			
			
			confF.setFirewall(fw);
			firewall.setConfiguration(confF);
		
			//fw lb link
			Neighbour nextNeigh = new Neighbour();
			nextNeigh.setName(firewall.getName());
			loadBalancer.getNeighbour().add(nextNeigh);
			Neighbour prevNeigh = new Neighbour();
			prevNeigh.setName(loadBalancer.getName());
			firewall.getNeighbour().add(prevNeigh);	
			
			// dpi
			ip = createRandomIP();
			Node dpi = new Node();
			dpi.setName(ip);
			dpi.setFunctionalType(FunctionalTypes.DPI);
			Configuration confD = new Configuration();
			confD.setName("confD");
			Dpi d =  new Dpi();
			d.setDefaultAction(ActionTypes.ALLOW);
			confD.setDpi(d);
			dpi.setConfiguration(confD);
			
			//fw dpi link
			nextNeigh = new Neighbour();
			nextNeigh.setName(firewall.getName());
			dpi.getNeighbour().add(nextNeigh);
			prevNeigh = new Neighbour();
			prevNeigh.setName(dpi.getName());
			firewall.getNeighbour().add(prevNeigh);	
			
			
		/*
		 * // dpi2 ip = createRandomIP(); Node dpi2 = new Node();
		 * dpi2.setFunctionalType(FunctionalTypes.DPI); dpi2.setName(ip); Configuration
		 * confD2 = new Configuration(); confD2.setName("confD2"); Dpi d2 = new Dpi();
		 * d2.setDefaultAction(ActionTypes.ALLOW); confD2.setDpi(d2);
		 * dpi2.setConfiguration(confD2);
		 * 
		 * //dpi2 dpi link nextNeigh = new Neighbour();
		 * nextNeigh.setName(dpi2.getName()); dpi.getNeighbour().add(nextNeigh);
		 * prevNeigh = new Neighbour(); prevNeigh.setName(dpi.getName());
		 * dpi2.getNeighbour().add(prevNeigh);
		 * 
		 * 
		 * // firewall 2 ip = createRandomIP(); Node firewall2 = new Node();
		 * firewall2.setFunctionalType(FunctionalTypes.FIREWALL); firewall2.setName(ip);
		 * Configuration confF2 = new Configuration(); confF2.setName("confF2");
		 * Firewall fw2 = new Firewall(); fw2.setDefaultAction(ActionTypes.DENY);
		 * Elements element2 = new Elements(); element2.setAction(ActionTypes.ALLOW);
		 * element2.setSource("-1.-1.-1.-1"); element2.setDestination("-1.-1.-1.-1");
		 * fw2.getElements().add(element2); confF2.setFirewall(fw2);
		 * firewall2.setConfiguration(confF2);
		 * 
		 * //fw2 dpi2 link nextNeigh = new Neighbour();
		 * nextNeigh.setName(firewall2.getName()); dpi2.getNeighbour().add(nextNeigh);
		 * prevNeigh = new Neighbour(); prevNeigh.setName(dpi2.getName());
		 * firewall2.getNeighbour().add(prevNeigh);
		 * 
		 * // load balancer2 ip = createRandomIP(); Node loadBalancer2 = new Node();
		 * loadBalancer2.setFunctionalType(FunctionalTypes.LOADBALANCER);
		 * loadBalancer2.setName(ip); Configuration confN2 = new Configuration();
		 * confN2.setName("confN2"); Loadbalancer lb2 = new Loadbalancer();
		 * lb2.getPool().addAll(allServers.stream().map(c->c.getName()).collect(
		 * Collectors.toList())); //forwarder confN2.setLoadbalancer(lb2);
		 * loadBalancer2.setConfiguration(confN2);
		 * 
		 * //fw2 lb2 link nextNeigh = new Neighbour();
		 * nextNeigh.setName(firewall2.getName());
		 * loadBalancer2.getNeighbour().add(nextNeigh); prevNeigh = new Neighbour();
		 * prevNeigh.setName(loadBalancer2.getName());
		 * firewall2.getNeighbour().add(prevNeigh);
		 */
			
			//links server to LB2
			for(int j = 0; j < numberClient; j++) { 
				Node client = allClients.get(j);
				nextNeigh = new Neighbour();
				nextNeigh.setName(client.getName());
				dpi.getNeighbour().add(nextNeigh);
				prevNeigh = new Neighbour();
				prevNeigh.setName(dpi.getName());
				client.getNeighbour().add(prevNeigh);	
			}
		
		boolean createdAllAPs = false;
		int numAP = 0;
		int numC = 0;

		
		//add the nodes in the graph
		graph.getNode().addAll(allClients);
		graph.getNode().addAll(allServers);

		graph.getNode().add(loadBalancer);
		graph.getNode().add(dpi);
		graph.getNode().add(firewall);

		createPolicy(PName.REACHABILITY_PROPERTY, nfv, graph, allClients.get(0).getName(), allServers.get(0).getName());

		nfv.getGraphs().getGraph().add(graph);
			
		
		return nfv;
	}

	private void createPolicy(PName type, NFV nfv, Graph graph, String IPClient, String IPServer) {

		Property property = new Property();
		property.setName(type);
		property.setGraph((long) 0);
		property.setSrc(IPClient);
		property.setDst(IPServer);
		property.setBody("null");
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
