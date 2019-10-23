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
public class TestCaseGeneratorBudapest {

 
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
	List<Node> allAPs;
	List<Node> allNATs;
	List<Node> allLBs;
	List<Tuple<String, Node>> lastAPs;
	
	
	public TestCaseGeneratorBudapest(String name, int numberAllocationPlaces, int numberReachPolicies, int numberIsPolicies, int numberNAT, int numberLB, int seed) {
		this.name = name;
		this.rand = new Random(seed); 


		allClients = new ArrayList<Node>();
		allServers = new ArrayList<Node>();
		allAPs = new ArrayList<Node>();
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
		allAPs = new ArrayList<Node>();
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
		for(int i = 0; i < 2; i++) {
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
		for(int i = 0; i < numberPolicies; i++) {
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
		
		
		//central AP
		Node central = new Node();
		String ipCentral = createRandomIP();
		central.setName(ipCentral);
	
		//creation of the others APs
		for(int i = 0; i < numberAllocationPlaces-1; i++) {
			String ip = createRandomIP();
			Node ap = new Node();
			ap.setName(ip);
			allAPs.add(ap);
		}
		
		//creation of all the NATs
		for(int i = 0; i < numberNAT; i++) {
			String ip = createRandomIP();
			Node nat = new Node();
			nat.setName(ip);
			Configuration confN = new Configuration();
			confN.setName("confN");
			Nat nt = new Nat();
			confN.setNat(nt);
			nat.setConfiguration(confN);
			allNATs.add(nat);
		}
		
		//creation of all the LBs
		for(int i = 0; i < numberLB; i++) {
			String ip = createRandomIP();
			Node loadBalancer = new Node();
			loadBalancer.setName(ip);
			Configuration confN = new Configuration();
			confN.setName("confN");
			Loadbalancer lb = new Loadbalancer();
			confN.setLoadbalancer(lb);
			loadBalancer.setConfiguration(confN);
			allLBs.add(loadBalancer);
		}
		
		//attach the APs
		int factorPAP = numberAllocationPlaces/numberPolicies;
		int resto = numberAllocationPlaces%numberPolicies;
		if(resto>0) factorPAP++;
		//if(factorPAP==0) factorPAP++;
		
		boolean createdAllAPs = false;
		int numAP = 0;
		int numC = 0;

		while(!createdAllAPs) {
			//System.out.println(numC);
			Node client = allClients.get(numC);
			Node prev = client;
			for(int j = 0; j < factorPAP; j++) {
				Node ap = allAPs.get(numAP);
				Neighbour nextNeigh = new Neighbour();
				nextNeigh.setName(ap.getName());
				prev.getNeighbour().add(nextNeigh);
				Neighbour prevNeigh = new Neighbour();
				prevNeigh.setName(prev.getName());
				ap.getNeighbour().add(prevNeigh);
				numAP++;
				prev = ap;
				if(numAP == (numberAllocationPlaces-1)){
					createdAllAPs = true;
					break;
				}
			}
			Tuple t = new Tuple(client.getName(), prev);
			lastAPs.add(t);
			numC++;
		}
		
		
		//attach the NATs
		int factorNAT = 0;
		boolean attachedAllNats = false;
		
		if(numberNAT!=0) factorNAT= numberPolicies/numberNAT;
		else attachedAllNats = true;
		if(factorNAT==0) factorNAT++;
		
		int numLastAP = 0;
		int fromAttach = 0;
		int numNAT = 0;

		while(!attachedAllNats) {
			Tuple t = lastAPs.get(numLastAP);
			Node lastAP = (Node) t._2;
			String client = (String) t._1;
			String prevString = client;
			Node prevNode = lastAP;
			for(int j = 0; j < factorNAT; j++) {
				Node nat = allNATs.get(numNAT);
				Neighbour nextNeigh = new Neighbour();
				nextNeigh.setName(nat.getName());
				prevNode.getNeighbour().add(nextNeigh);
				Neighbour prevNeigh = new Neighbour();
				prevNeigh.setName(prevNode.getName());
				nat.getNeighbour().add(prevNeigh);
				nat.getConfiguration().getNat().getSource().add(prevString);
				numNAT++;
				prevNode = nat;
				prevString = nat.getName();
				if(numNAT == numberNAT){
					attachedAllNats = true;
					break;
				}
			}
			fromAttach++;
			numLastAP++;
		}
		
		
		//attach the LBs
		for(int i = 0; i < numberLB; i++) {
			Node server = allServers.get(i);
			Node lb = allLBs.get(i);
			lb.getConfiguration().getLoadbalancer().getPool().add(server.getName());
			
			Neighbour nextNeigh = new Neighbour();
			nextNeigh.setName(server.getName());
			lb.getNeighbour().add(nextNeigh);
			Neighbour prevNeigh = new Neighbour();
			prevNeigh.setName(lb.getName());
			server.getNeighbour().add(prevNeigh);
			
			Neighbour nextNeigh2 = new Neighbour();
			nextNeigh.setName(lb.getName());
			central.getNeighbour().add(nextNeigh2);
			Neighbour prevNeigh2 = new Neighbour();
			prevNeigh.setName(central.getName());
			lb.getNeighbour().add(prevNeigh2);
		}
		
		for(int i = numberLB; i < 2; i++) {
			Node server = allServers.get(i);
			
			Neighbour nextNeigh = new Neighbour();
			nextNeigh.setName(server.getName());
			central.getNeighbour().add(nextNeigh);
			Neighbour prevNeigh = new Neighbour();
			prevNeigh.setName(central.getName());
			server.getNeighbour().add(prevNeigh);
		}
		
		
		//attach central node to NATs and APs
		for(int i = 0; i < numberNAT; i++) {
			Node node = allNATs.get(i);
		
			Neighbour nextNeigh = new Neighbour();
			nextNeigh.setName(central.getName());
			node.getNeighbour().add(nextNeigh);
			Neighbour prevNeigh = new Neighbour();
			prevNeigh.setName(node.getName());
			central.getNeighbour().add(prevNeigh);
		}
		
		int fA;
		//System.out.println(lastAPs.size());
		for(fA = fromAttach; fA < numberPolicies-1 && fA < lastAPs.size(); fA++) {
			//System.out.println(fA);
			Node node = lastAPs.get(fA)._2;
			
			Neighbour nextNeigh = new Neighbour();
			nextNeigh.setName(central.getName());
			node.getNeighbour().add(nextNeigh);
			Neighbour prevNeigh = new Neighbour();
			prevNeigh.setName(node.getName());
			central.getNeighbour().add(prevNeigh);
			
		}
		
		for(; fA < numberPolicies; fA++) {
			Node node = allClients.get(fA);
			
			Neighbour nextNeigh = new Neighbour();
			nextNeigh.setName(central.getName());
			node.getNeighbour().add(nextNeigh);
			Neighbour prevNeigh = new Neighbour();
			prevNeigh.setName(node.getName());
			central.getNeighbour().add(prevNeigh);
		}
	
		//add the nodes in the graph
		graph.getNode().addAll(allClients);
		graph.getNode().addAll(allServers);
		graph.getNode().addAll(allAPs);
		graph.getNode().addAll(allNATs);
		graph.getNode().addAll(allLBs);
		graph.getNode().add(central);

		//create the policies
		int numCP = 0;
		for(int i = 0; i < numberIsPolicies; i++) {
			createPolicy(PName.ISOLATION_PROPERTY, nfv, graph, allClients.get(numCP).getName(), allServers.get(0).getName());
			numCP++;
		}
		for(int i = 0; i < numberReachPolicies; i++) {
			createPolicy(PName.REACHABILITY_PROPERTY, nfv, graph, allClients.get(numCP).getName(), allServers.get(1).getName());
			numCP++;
		}

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
