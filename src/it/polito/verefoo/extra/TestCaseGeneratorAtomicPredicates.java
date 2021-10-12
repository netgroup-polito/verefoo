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
		
	public TestCaseGeneratorAtomicPredicates(String name, int numberAllocationPlaces, int numberWebClients, int numberWebServers, 
			int numberReachPolicies, int numberIsPolicies, int numberNAT, int numberFirewall, int maxNATSrcs, int maxFWRules, 
			double percReqWithPorts, int seed) {
		this.name = name;
		this.rand = new Random(seed); 

		allClients = new ArrayList<Node>();
		allServers = new ArrayList<Node>();
		allAPs = new ArrayList<Node>();
		allNATs = new ArrayList<Node>();
		allFirewalls = new ArrayList<Node>();
		lastAPs = new ArrayList<Tuple<String, Node>>();

		allIPs = new HashSet<String>();
		nfv = generateNFV(numberAllocationPlaces, numberWebClients, numberWebServers, numberReachPolicies, numberIsPolicies, numberNAT, numberFirewall, 
				maxNATSrcs, maxFWRules, percReqWithPorts, rand);
	}
	
	
	public NFV changeIP(int numberAllocationPlaces, int numberWebClients, int numberWebServers, int numberReachPolicies, int numberIsPolicies, 
			int numberNAT, int numberFirewall, int maxNATSrcs, int maxFWRules, double percReqWithPorts, int seed) {
		this.rand = new Random(seed);
		allClients = new ArrayList<Node>();
		allServers = new ArrayList<Node>();
		allAPs = new ArrayList<Node>();
		allNATs = new ArrayList<Node>();
		allFirewalls = new ArrayList<Node>();
		lastAPs = new ArrayList<Tuple<String, Node>>();

		allIPs = new HashSet<String>();
		return generateNFV(numberAllocationPlaces, numberWebClients, numberWebServers, numberReachPolicies, numberIsPolicies, numberNAT, numberFirewall,
				maxNATSrcs, maxFWRules, percReqWithPorts, rand);
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
	
	
	public NFV generateNFV(int numberAllocationPlaces, int numberWebClients, int numberWebServers, int numberReachPolicies, int numberIsPolicies, 
			int numberNAT, int numberFirewall, int maxNATSrcs, int maxFWRules, double percReqWithPorts, Random rand) {
		
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
	
		//creation of the others APs
		for(int i = 0; i < numberAllocationPlaces; i++) {
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
			firewall.setFunctionalType(FunctionalTypes.FIREWALL);
			firewall.setName(ip);
			Configuration confF = new Configuration();
			confF.setName("confF");
			Firewall fw = new Firewall();
			confF.setFirewall(fw);
			firewall.setConfiguration(confF);
			allFirewalls.add(firewall);
		}
				
		//attach the APs
		int factorPAP = numberAllocationPlaces/numberWebClients;
		int resto = numberAllocationPlaces%numberWebClients;
		if(resto>0) factorPAP++;
		//if(factorPAP==0) factorPAP++;
		
		boolean createdAllAPs = false;
		int numAP = 0;
		int numC = 0;

		while(!createdAllAPs) {
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
				if(numAP == (numberAllocationPlaces)){
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
		
		//DEBUG
//		System.out.println("TUPLE before attaching firewalls");
//		for(Tuple<String, Node> tuple: lastAPs) {
//			System.out.println("TUPLE before fw: " + tuple._1 + " -> " + tuple._2.getName());
//		}
//		System.out.println();
		//END DEBUG
		
		//attach firewall to client chain (NOTE: some can ramain)
		int remainingFirewall = numberFirewall;
		List<Tuple<String, Node>> tmpTupleList = new ArrayList<Tuple<String, Node>>();
		for(Tuple<String, Node> tuple: lastAPs) {
			if(remainingFirewall > 0) {
				Node currentFirewall = allFirewalls.get(numberFirewall-remainingFirewall);
				Neighbour neighForTuple = new Neighbour();
				Neighbour neighForFirewall = new Neighbour();
				neighForTuple.setName(currentFirewall.getName());
				neighForFirewall.setName(tuple._2.getName());
				tuple._2.getNeighbour().add(neighForTuple);
				currentFirewall.getNeighbour().add(neighForFirewall);
				tmpTupleList.add(new Tuple<String, Node>(tuple._1, currentFirewall));
				remainingFirewall--;
			} else
				tmpTupleList.add(new Tuple<String, Node>(tuple._1, tuple._2));
		}
		lastAPs = new ArrayList<Tuple<String, Node>>(tmpTupleList);
		
		//DEBUG:print tuple
//		int index = 0;
//		System.out.println("ALL CLIENTS");
//		for(Node node: allClients) {
//			System.out.println(index + " " + node.getName());
//			index++;
//		}
//		System.out.println();
//		System.out.println("ALL FIREWALLS");
//		for(Node node: allFirewalls) {
//			System.out.println(index + " " + node.getName());
//			index++;
//		}
//		System.out.println();
//		System.out.println("ALL APS");
//		for(Node node: allAPs) {
//			System.out.println(index + " " + node.getName());
//			index++;
//		}
//		System.out.println();
//		System.out.println("ALL NATS");
//		for(Node node: allNATs) {
//			System.out.println(index + " " + node.getName());
//			index++;
//		}
//		System.out.println();
//		System.out.println("Tuple before applying NAT");
//		for(Tuple<String, Node> tuple: lastAPs) {
//			System.out.println("TUPLE: " + tuple._1 + " -> " + tuple._2.getName());
//		}
//		System.out.println();
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
				//if(rand.nextBoolean() || currentNAT.getConfiguration().getNat().getSource().size() == 0) {
					currentNAT.getConfiguration().getNat().getSource().add(tuple._1);
				//}

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
//		System.out.println("Tuple before applying last firewalls");
//		for(Tuple<String, Node> tuple: lastAPs) {
//			System.out.println("LAST AP: " + tuple._1 + " -> " + tuple._2.getName());
//		}
		//END DEBUG
		
		//attach remaining firewalls
		tmpTupleList = new ArrayList<Tuple<String, Node>>();
		while(remainingFirewall > 0) {
			for(Tuple<String, Node> tuple: lastAPs) {
				if(remainingFirewall > 0) {
					Node currentFirewall = allFirewalls.get(numberFirewall-remainingFirewall);
					Neighbour neighForTuple = new Neighbour();
					Neighbour neighForFirewall = new Neighbour();
					neighForTuple.setName(currentFirewall.getName());
					neighForFirewall.setName(tuple._2.getName());
					tuple._2.getNeighbour().add(neighForTuple);
					currentFirewall.getNeighbour().add(neighForFirewall);
					tmpTupleList.add(new Tuple<String, Node>(currentFirewall.getName(), currentFirewall));
					remainingFirewall--;
				} else
					tmpTupleList.add(new Tuple<String, Node>(tuple._1, tuple._2));
			}
			lastAPs = new ArrayList<Tuple<String, Node>>(tmpTupleList);
			tmpTupleList = new ArrayList<Tuple<String, Node>>();
		}

		//DEBUG
//		System.out.println("\nTuple after applying last firewalls");
//		for(Tuple<String, Node> tuple: lastAPs) {
//			System.out.println("LAST AP: " + tuple._1 + " -> " + tuple._2.getName());
//		}
		//END DEBUG
		
		//attach all chains to central node
		for(Tuple<String, Node> tuple: lastAPs) {
			Neighbour neighForTuple = new Neighbour();
			Neighbour neighForCentralNode  = new Neighbour();
			neighForTuple.setName(central.getName());
			neighForCentralNode.setName(tuple._2.getName());
			tuple._2.getNeighbour().add(neighForTuple);
			central.getNeighbour().add(neighForCentralNode);
		}
		
		//attach all servers to central node
		for(Node server: allServers) {
			Neighbour neighForServer = new Neighbour();
			Neighbour neighForCentralNode  = new Neighbour();
			neighForServer.setName(central.getName());
			neighForCentralNode.setName(server.getName());
			server.getNeighbour().add(neighForServer);
			central.getNeighbour().add(neighForCentralNode);
		}
		

		//create the policies
		int numberIPWithPorts = (int) (numberIsPolicies * percReqWithPorts);
		for(int i = 0; i < numberIsPolicies; i++) {
			String srcNode = "", dstNode = "", srcPort = "*", dstPort = "*";
//			if(rand.nextBoolean())
//				srcNode = allClients.get(rand.nextInt(allClients.size())).getName();
//			else srcNode = allServers.get(rand.nextInt(allServers.size())).getName();
			srcNode = allClients.get(rand.nextInt(allClients.size())).getName();
//			if(rand.nextBoolean())
//				dstNode = allClients.get(rand.nextInt(allClients.size())).getName();
//			else dstNode = allServers.get(rand.nextInt(allServers.size())).getName();
			dstNode = allServers.get(rand.nextInt(allServers.size())).getName();
			if(numberIPWithPorts > 0) {
				srcPort = String.valueOf(rand.nextInt(65535));
//				if(rand.nextBoolean())
//					srcPort = String.valueOf(rand.nextInt(65535));
//				else dstPort = String.valueOf(rand.nextInt(65535));
				numberIPWithPorts--;
			}
			
			boolean alreadyInserted = false;
			for(Property prop: nfv.getPropertyDefinition().getProperty()) {
				if(prop.getSrc().equals(srcNode) && prop.getDst().equals(dstNode)) {
					alreadyInserted = true;
					break;
				}
			}
			if(!alreadyInserted && !srcNode.equals(dstNode))
				createPolicy(PName.ISOLATION_PROPERTY, nfv, graph, srcNode, dstNode, srcPort, dstPort);
			else i--;
		}
		
		int numberRPWithPorts = (int) (numberReachPolicies * percReqWithPorts);
		for(int i = 0; i < numberReachPolicies; i++) {
			String srcNode = "", dstNode = "", srcPort = "*", dstPort = "*";
			if(rand.nextBoolean())
				srcNode = allClients.get(rand.nextInt(allClients.size())).getName();
			else srcNode = allServers.get(rand.nextInt(allServers.size())).getName();
			//srcNode = allClients.get(rand.nextInt(allClients.size())).getName();
			if(rand.nextBoolean())
				dstNode = allClients.get(rand.nextInt(allClients.size())).getName();
			else dstNode = allServers.get(rand.nextInt(allServers.size())).getName();
			//dstNode = allServers.get(rand.nextInt(allServers.size())).getName();
			if(numberRPWithPorts > 0) {
				srcPort = String.valueOf(rand.nextInt(65535));
//				if(rand.nextBoolean())
//					srcPort = String.valueOf(rand.nextInt(65535));
//				else dstPort = String.valueOf(rand.nextInt(65535));
				numberRPWithPorts--;
			}
			
			//control if the policy is not ready inserted
			boolean alreadyInserted = false;
			for(Property prop: nfv.getPropertyDefinition().getProperty()) {
				if(prop.getSrc().equals(srcNode) && prop.getDst().equals(dstNode)) {
					alreadyInserted = true;
					break;
				}
			}
			
			if(!alreadyInserted && !srcNode.equals(dstNode))
				createPolicy(PName.COMPLETE_REACHABILITY_PROPERTY, nfv, graph, srcNode, dstNode, srcPort, dstPort);
			else i--;
		}

		
		//generate firewall rules
		//NOTE: rule for the firewall is randomly selected: set0=allClients, set1=allServers, set2=allNAT
		int nRules;
		for(Node firewall: allFirewalls) {
			//introducing some firewalls with zero rules, only with default action
			if(rand.nextBoolean())
				nRules =  maxFWRules; //rand.nextInt(maxFWRules);
			else nRules = 0;
			
			//default action set to oallow
			firewall.getConfiguration().getFirewall().setDefaultAction(ActionTypes.ALLOW);

			for(int i=0; i<nRules; i++) {
				String srcNode = ""; String dstNode = "";
				
				switch(rand.nextInt(2)) {
				case 0: 
					srcNode = allClients.get(rand.nextInt(allClients.size())).getName(); break;
				case 1: 
					srcNode = allServers.get(rand.nextInt(allServers.size())).getName(); break;	
				}
				switch(rand.nextInt(2)) {
				case 0: 
					dstNode = allClients.get(rand.nextInt(allClients.size())).getName(); break;
				case 1: 
					dstNode = allServers.get(rand.nextInt(allServers.size())).getName(); break;	
				}
					
				//check that no reachability requirements match this DENY rule
				boolean reqExists = false;
				for(Property prop: nfv.getPropertyDefinition().getProperty()) {
					if(prop.getSrc().equals(srcNode) && prop.getDst().equals(dstNode) && prop.getName().equals(PName.REACHABILITY_PROPERTY)) {
						reqExists = true;
						break;
					}
				}
				
				if(!reqExists) {
					Elements rule = new Elements();
					rule.setAction(ActionTypes.DENY);
					rule.setSource(srcNode);
					rule.setDestination(dstNode);
					rule.setSrcPort("*");
					rule.setDstPort("*");
					rule.setProtocol(L4ProtocolTypes.ANY);
					firewall.getConfiguration().getFirewall().getElements().add(rule);
				} else i--;
			}
		}
		
		//Create loop in the network: take couples of NAT and link them
		//START CREATE LOOP
//		boolean found;
//		for(int i=0; i<2; i++) {
//			found = false;
//			Node node1 = allNATs.get(rand.nextInt(allNATs.size()));
//			Node node2 = allNATs.get(rand.nextInt(allNATs.size()));
//			
//			if(!node1.getName().equals(node2.getName())) {
//				//Check if the two node are already neighbours
//				for(Neighbour tmpNeig: node1.getNeighbour()) {
//					if(tmpNeig.getName().equals(node2.getName())) {
//						found = true;
//						break;
//					}
//				}
//				if(!found) {
//					Neighbour neig1 = new Neighbour();
//					Neighbour neig2  = new Neighbour();
//					neig1.setName(node2.getName());
//					neig2.setName(node1.getName());
//					node1.getNeighbour().add(neig1);
//					node2.getNeighbour().add(neig2);
//				} else i--;
//					
//			}else i--; //repeat iteration
//		}
		//END CREATE LOOP

		//add the nodes in the graph
		graph.getNode().addAll(allClients);
		graph.getNode().addAll(allServers);
		graph.getNode().addAll(allAPs);
		graph.getNode().addAll(allNATs);
		graph.getNode().addAll(allFirewalls);
		graph.getNode().add(central);
		nfv.getGraphs().getGraph().add(graph);
			
		
		return nfv;
	}

	private void createPolicy(PName type, NFV nfv, Graph graph, String IPClient, String IPServer, String srcPort, String dstPort) {

		Property property = new Property();
		property.setName(type);
		property.setGraph((long) 0);
		property.setSrc(IPClient);
		property.setDst(IPServer);
		property.setSrcPort(srcPort);
		property.setDstPort(dstPort);
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
