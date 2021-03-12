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

// Auxiliary class to generate  test cases for performance tests (used by TestPerformanceScalabilityAtomicPredicates)
public class TestCaseGeneratorAtomicPredicates {
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
	
	Set<String> allIPs;
	List<Node> allClients;
	List<Node> allServers;
	List<Node> allAPs;
	List<Node> allNATs;
	List<Node> allFirewalls;
	List<Tuple<String, Node>> lastAPs;
	
	/* Atomic predicates new */
	int maxNATSrcs = 2;
	
	
	public TestCaseGeneratorAtomicPredicates(String name, int numberAllocationPlaces, int numberWebClients, int numberWebServers, 
			int numberReachPolicies, int numberIsPolicies, int numberNAT, int numberFirewall, int seed) {
		this.name = name;
		this.rand = new Random(seed); 

		allClients = new ArrayList<Node>();
		allServers = new ArrayList<Node>();
		allAPs = new ArrayList<Node>();
		allNATs = new ArrayList<Node>();
		allFirewalls = new ArrayList<Node>();
		lastAPs = new ArrayList<Tuple<String, Node>>();

		allIPs = new HashSet<String>();
		nfv = generateNFV(numberAllocationPlaces, numberWebClients, numberWebServers, numberReachPolicies, numberIsPolicies, numberNAT, numberFirewall, rand);
	}
	
	
	
	public NFV changeIP(int numberAllocationPlaces, int numberWebClients, int numberWebServers, int numberReachPolicies, int numberIsPolicies, 
			int numberNAT, int numberFirewall, int seed) {
		this.rand = new Random(seed);
		allClients = new ArrayList<Node>();
		allServers = new ArrayList<Node>();
		allAPs = new ArrayList<Node>();
		allNATs = new ArrayList<Node>();
		allFirewalls = new ArrayList<Node>();
		lastAPs = new ArrayList<Tuple<String, Node>>();

		allIPs = new HashSet<String>();
		return generateNFV(numberAllocationPlaces, numberWebClients, numberWebServers, numberReachPolicies, numberIsPolicies, numberNAT, numberFirewall, rand);
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
	
	
	public NFV generateNFV(int numberAllocationPlaces, int numberWebClients, int numberWebServers, int numberReachPolicies, int numberIsPolicies, int numberNAT, int numberFirewall, Random rand) {
		
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
		
		//creation of servers 
		for(int i = 0; i < numberWebServers; i++) {
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
		for(int i = 0; i < numberWebClients; i++) {
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
		System.out.println("Central node "+ ipCentral);
	
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
			nat.setFunctionalType(FunctionalTypes.NAT);
			confN.setName("confN");
			Nat nt = new Nat();
			confN.setNat(nt);
			nat.setConfiguration(confN);
			allNATs.add(nat);
		}
		
		//creation of all the firewalls
		for(int i = 0; i < numberFirewall; i++) {
			String ip = createRandomIP();
			Node firewall = new Node();
			firewall.setName(ip);
			Configuration confF = new Configuration();
			confF.setName("confF");
			Firewall fw = new Firewall();
			confF.setFirewall(fw);
			firewall.setConfiguration(confF);
			allFirewalls.add(firewall);
		}
		
		//attach one firewall to each client
		
		//attach the APs
		int factorPAP = numberAllocationPlaces/numberWebClients;
		int resto = numberAllocationPlaces%numberWebClients;
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
			Tuple<String, Node> t = new Tuple<String, Node>(client.getName(), prev);
			lastAPs.add(t);
			numC++;
		}
		
		for(int i=numC; i<numberWebClients; i++) {
			Node client = allClients.get(i);
			lastAPs.add(new Tuple<String, Node>(client.getName(), client));
		}
		
		//DEBUG:print tuple
		int index = 0;
		System.out.println("ALL CLIENTS");
		for(Node node: allClients) {
			System.out.println(index + " " + node.getName());
			index++;
		}
		System.out.println();
		System.out.println("ALL APS");
		for(Node node: allAPs) {
			System.out.println(index + " " + node.getName());
			index++;
		}
		System.out.println();
		System.out.println("ALL NATS");
		for(Node node: allNATs) {
			System.out.println(index + " " + node.getName());
			index++;
		}
		System.out.println();
		for(Tuple<String, Node> tuple: lastAPs) {
			System.out.println("CLIENT: " + tuple._1 + " -> " + tuple._2.getName());
		}
		System.out.println();
		//END DEBUG
		
		//attach the NATs
		//maxNATSrcs = number of clients to assign to each NAT
		int tupleIndex = 0;
		int remainingNAT = numberNAT;
		int n = 0;
		ArrayList<Tuple<String, Node>> newTupleList = new ArrayList<Tuple<String, Node>>();
		Node currentNAT;
		
		while(remainingNAT > 0) {
			for(Tuple<String, Node> tuple: lastAPs) {
				currentNAT = allNATs.get(numberNAT-remainingNAT);

				Neighbour neighForTuple = new Neighbour();
				Neighbour neighForNat = new Neighbour();
				neighForTuple.setName(currentNAT.getName());
				neighForNat.setName(tuple._2.getName());
				tuple._2.getNeighbour().add(neighForTuple);
				currentNAT.getNeighbour().add(neighForNat);
				//random selection if the client has to be added to NAT src (NOTE: NAT src list should contain at least one src)
				if(rand.nextBoolean() || currentNAT.getConfiguration().getNat().getSource().size() == 0) {
					currentNAT.getConfiguration().getNat().getSource().add(tuple._1);
				}

				if(n == 0) newTupleList.add(new Tuple<String, Node>(currentNAT.getName(), currentNAT));
				n++;
				if(n == maxNATSrcs) {
					remainingNAT--;
					if(remainingNAT == 0) break;
					n = 0;
				}
				tupleIndex++;
			}
			
			//tuple finished, but still NAT available
			if(remainingNAT > 0) {
				if(n != maxNATSrcs && n != 0) remainingNAT--;
				n=0;
				lastAPs = new ArrayList<Tuple<String, Node>>(newTupleList);
				newTupleList =  new ArrayList<Tuple<String, Node>>();
				tupleIndex = 0;
			} else {
				//NAT finished but there can be remaining tuple to process
				tupleIndex++;
				if(tupleIndex != lastAPs.size()) {
					for(int j = tupleIndex; j < lastAPs.size(); j++) {
						newTupleList.add(lastAPs.get(j));
					}
				}
				lastAPs = new ArrayList<Tuple<String, Node>>(newTupleList);
				newTupleList =  new ArrayList<Tuple<String, Node>>();
			}
		}
		
		//DEBUG
		for(Tuple<String, Node> tuple: lastAPs) {
			System.out.println("LAST AP: " + tuple._1 + " -> " + tuple._2.getName());
		}
		//END DEBUG
		
		
		//attach central node to NATs and APs
		
	
		//add the nodes in the graph
		graph.getNode().addAll(allClients);
		graph.getNode().addAll(allServers);
		graph.getNode().addAll(allAPs);
		graph.getNode().addAll(allNATs);
		//graph.getNode().addAll(allLBs);
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
