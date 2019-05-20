package it.polito.verefoo.extra;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import it.polito.verefoo.jaxb.*;

// Auxiliary class to generate  test cases for performance tests (used by TestPerformanceScalability)
public class TestCaseGenerator {

 
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
	
	Set<String> allIPs;
	
	
	public TestCaseGenerator(String name, int numberAllocationPlaces, int numberReachPolicies, int numberIsPolicies, String IPClient, String IPAllocationPlace, String IPServer) {
		this.name = name;
		IPC = IPClient;
		IPAP = IPAllocationPlace;
		IPS = IPServer;
		allIPs = null;
		
		this.numberAllocationPlaces=numberAllocationPlaces;
		this.numberReachPolicies = numberReachPolicies;
		this.numberIsPolicies = numberIsPolicies;
		nfv = generateNFV(numberAllocationPlaces, numberReachPolicies, numberIsPolicies, IPClient, IPAllocationPlace, IPServer);
	}
	
	
	public TestCaseGenerator(String name, int numberAllocationPlaces, int numberReachPolicies, int numberIsPolicies, int seed) {
		this.name = name;
		this.rand = new Random(seed); 

		this.numberAllocationPlaces=numberAllocationPlaces;
		this.numberReachPolicies = numberReachPolicies;
		this.numberIsPolicies = numberIsPolicies;
		allIPs = new HashSet<String>();
		nfv = generateNFV(numberAllocationPlaces, numberReachPolicies, numberIsPolicies, rand);
	}
	
	public NFV generateNew(){
		 countC = 1;
		 countAP = 1;
		 countS = 1;
		 countP = 1;
		return generateNFV(numberAllocationPlaces, numberReachPolicies, numberIsPolicies, IPC, IPAP, IPS);
	}

	public NFV changeIP(String IPClient, String IPAllocationPlace, String IPServer) {
		countC = 1;
		 countAP = 1;
		 countS = 1;
		 countP = 1;
		return generateNFV(numberAllocationPlaces, numberReachPolicies, numberIsPolicies, IPClient, IPAllocationPlace, IPServer);
	}
	
	
	public NFV changeIP(int seed) {
		this.rand = new Random(seed);
		allIPs = new HashSet<String>();
		return generateNFV(numberAllocationPlaces, numberReachPolicies, numberIsPolicies, rand);
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
	
	
	public NFV generateNFV(int numberAllocationPlaces, int numberReachPolicies, int numberIsPolicies, String IPClient, String IPAllocationPlace, String IPServer) {
		
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
		
		Node first = null;
		
		for(int i = 0; i < numberAllocationPlaces; i++) {
			Node ap = new Node();
			ap.setName(IPAllocationPlace + countAP);
			
			if(i != 0) {
				Neighbour prevNeigh = new Neighbour();
				prevNeigh.setName(IPAllocationPlace + (countAP -1));
				ap.getNeighbour().add(prevNeigh);
			}
			if(i != numberAllocationPlaces-1) {
				Neighbour nextNeigh = new Neighbour();
				nextNeigh.setName(IPAllocationPlace + (countAP +1));
				ap.getNeighbour().add(nextNeigh);
			}
			countAP++;
			
			if(i == 0) {
				first = ap;
			}
			else {
				if(i == numberAllocationPlaces - 1) {
					Neighbour servNeigh = new Neighbour();
					servNeigh.setName(IPServer + countS);
					ap.getNeighbour().add(servNeigh);
				}
				graph.getNode().add(ap);
			}
			
		}
		
		Node server = new Node();
		server.setFunctionalType(FunctionalTypes.WEBSERVER);
		server.setName(IPServer + countS);
		Neighbour prevServ = new Neighbour();
		prevServ.setName(IPAllocationPlace + numberAllocationPlaces);
		server.getNeighbour().add(prevServ);
		Configuration confS = new Configuration();
		confS.setName("confB");
		Webserver ws = new Webserver();
		ws.setName(server.getName());
		confS.setWebserver(ws);
		server.setConfiguration(confS);
		graph.getNode().add(server);
		
		for(int i = 0; i < numberReachPolicies; i++) {
			createPolicy(PName.REACHABILITY_PROPERTY, nfv, graph, first, IPClient, IPServer);
		}
		for(int i = 0; i < numberIsPolicies; i++) {
			createPolicy(PName.ISOLATION_PROPERTY, nfv, graph, first, IPClient, IPServer);
		}
		graph.getNode().add(first);
		nfv.getGraphs().getGraph().add(graph);
				
		return nfv;
	}

	private void createPolicy(PName type, NFV nfv, Graph graph, Node first, String IPClient, String IPServer) {
		
		Node client = new Node();
		client.setFunctionalType(FunctionalTypes.WEBCLIENT);
		client.setName(IPClient + countC);
		countC++;
		Neighbour nextC = new Neighbour();
		nextC.setName(first.getName());
		client.getNeighbour().add(nextC);
		Configuration confC = new Configuration();
		confC.setName("confA");
		Webclient wc = new Webclient();
		wc.setNameWebServer(IPServer + countS);
		confC.setWebclient(wc);
		client.setConfiguration(confC);
		graph.getNode().add(client);
		
		Neighbour clientNeigh = new Neighbour();
		clientNeigh.setName(client.getName());
		first.getNeighbour().add(clientNeigh);
		
		Property property = new Property();
		property.setName(type);
		property.setGraph((long) 0);
		property.setSrc(client.getName());
		property.setDst(IPServer + "1");
		nfv.getPropertyDefinition().getProperty().add(property);
	}

public NFV generateNFV(int numberAllocationPlaces, int numberReachPolicies, int numberIsPolicies, Random rand) {
		
		
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
		
		Node first = null;
		Node last = null;
		
		String prevAPIP = null, nextAPIP = null;
		
		String IPServer = createRandomIP();
		
		for(int i = 0; i < numberAllocationPlaces; i++) {
			Node ap = new Node();
			
			if(i==0) {
				String ip = createRandomIP();
				ap.setName(ip);
				nextAPIP = createRandomIP();
				Neighbour nextNeigh = new Neighbour();
				nextNeigh.setName(nextAPIP);
				ap.getNeighbour().add(nextNeigh);
				prevAPIP = ip;
			}
			
			if(i!=0) {
				ap.setName(nextAPIP);
				
				Neighbour prevNeigh = new Neighbour();
				prevNeigh.setName(prevAPIP);
				ap.getNeighbour().add(prevNeigh);
				prevAPIP = nextAPIP;
				
				if(i != numberAllocationPlaces-1) {
					nextAPIP = createRandomIP();
					Neighbour nextNeigh = new Neighbour();
					nextNeigh.setName(nextAPIP);
					ap.getNeighbour().add(nextNeigh);
				}
			}

			
			countAP++;
			
			if(i == 0) {
				first = ap;
			}
			else {
				if(i == numberAllocationPlaces - 1) {
					Neighbour servNeigh = new Neighbour();
					servNeigh.setName(IPServer);
					ap.getNeighbour().add(servNeigh);
				}
				graph.getNode().add(ap);
				last = ap;
			}
			
		}
		
		Node server = new Node();
		server.setFunctionalType(FunctionalTypes.WEBSERVER);
		server.setName(IPServer);
		Neighbour prevServ = new Neighbour();
		prevServ.setName(last.getName());
		server.getNeighbour().add(prevServ);
		Configuration confS = new Configuration();
		confS.setName("confB");
		Webserver ws = new Webserver();
		ws.setName(server.getName());
		confS.setWebserver(ws);
		server.setConfiguration(confS);
		graph.getNode().add(server);
		
		for(int i = 0; i < numberReachPolicies; i++) {
			createPolicy(PName.REACHABILITY_PROPERTY, nfv, graph, first, IPServer);
		}
		for(int i = 0; i < numberIsPolicies; i++) {
			createPolicy(PName.ISOLATION_PROPERTY, nfv, graph, first, IPServer);
		}
		graph.getNode().add(first);
		nfv.getGraphs().getGraph().add(graph);
			
		return nfv;
	}

	private void createPolicy(PName type, NFV nfv, Graph graph, Node first, String IPServer) {
		String IPClient = createRandomIP();
		
		Node client = new Node();
		client.setFunctionalType(FunctionalTypes.WEBCLIENT);
		client.setName(IPClient);
		countC++;
		Neighbour nextC = new Neighbour();
		nextC.setName(first.getName());
		client.getNeighbour().add(nextC);
		Configuration confC = new Configuration();
		confC.setName("confA");
		Webclient wc = new Webclient();
		wc.setNameWebServer(IPServer);
		confC.setWebclient(wc);
		client.setConfiguration(confC);
		graph.getNode().add(client);
		
		Neighbour clientNeigh = new Neighbour();
		clientNeigh.setName(client.getName());
		first.getNeighbour().add(clientNeigh);
		
		Property property = new Property();
		property.setName(type);
		property.setGraph((long) 0);
		property.setSrc(client.getName());
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
