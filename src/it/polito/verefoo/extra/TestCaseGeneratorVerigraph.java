package it.polito.verefoo.extra;


import java.util.HashSet;
import java.util.Random;
import java.util.Set;


import it.polito.verefoo.jaxb.*;

// Auxiliary class to generate  test cases for performance tests (used by TestPerformanceScalability)
public class TestCaseGeneratorVerigraph {

 
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
	private int chainSize;
	private int numberOfRules;
	
	
	private static int totTimeChecker=0;
	
	private int percDPI;
	private int percWaf;
	private int percPf;
	private int percForw;
	
	public TestCaseGeneratorVerigraph(String name, int numberRules, int sizeChain, int seed, int percDPI, int percWaf, int percPf, int percForw) {
		this.name = name;
		this.rand = new Random(seed); 
		this.percDPI = percDPI;
		this.percWaf = percWaf;
		this.percPf = percPf;
		this.percForw = percForw;
		chainSize = sizeChain;  
		numberOfRules =numberRules;

		allIPs = new HashSet<String>();
		nfv = generateNFV(numberRules, sizeChain, rand);
	}
	

	
	public NFV changeIP(int seed) {
		this.rand = new Random(seed);
		allIPs = new HashSet<String>();
		return generateNFV(numberOfRules, chainSize, rand);
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
	
	
	public NFV generateNFV(int numberRules, int sizeChain, Random rand) {
		
		chainSize = sizeChain; //10 
		numberOfRules =numberRules;
		
		int numberDPI = chainSize*percDPI/100; //15%   
		int numberWaf = chainSize*percWaf/100;      //15%
		int numberPf = chainSize*percPf/100;       //40%
		int numberForwarder = chainSize*percForw/100;//30%
		
		
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
		
		
		String ipClient = createRandomIP();
		String ipServer = createRandomIP();
		
		//client
		Node client = new Node();
		client.setFunctionalType(FunctionalTypes.WEBCLIENT);
		client.setName(ipClient);
		Configuration confC = new Configuration();
		confC.setName("confA");
		Webclient wc = new Webclient();
		wc.setNameWebServer(ipServer);
		confC.setWebclient(wc);
		client.setConfiguration(confC);
		graph.getNode().add(client);
		
		
		// one nat
		String ipNat = createRandomIP();
		Node nat = new Node();
		nat.setFunctionalType(FunctionalTypes.NAT);
		nat.setName(ipNat);
		Configuration confN = new Configuration();
		confN.setName("confN");
		Nat nt = new Nat();
		nt.getSource().add(ipClient);
		confN.setNat(nt);
		nat.setConfiguration(confN);
		Neighbour prevC = new Neighbour();
		prevC.setName(ipClient);
		nat.getNeighbour().add(prevC);
		Neighbour nextN = new Neighbour();
		nextN.setName(ipNat);
		client.getNeighbour().add(nextN);
		graph.getNode().add(nat);
		
		
		Node prev = nat;
		//add firewalls
		for (int i = 0; i < numberPf; i++) {
			String ipF = createRandomIP();
			Node firewall = new Node();
			firewall.setName(ipF);
			Configuration confF = new Configuration();
			confF.setName("confF");
			Firewall fw =  new Firewall();
			firewall.setFunctionalType(FunctionalTypes.FIREWALL);
			fw.setDefaultAction(ActionTypes.ALLOW);
			for (int j = 0; j < numberOfRules; j++) {
				Elements element =  new Elements();
				element.setAction(ActionTypes.DENY);
				element.setSource(createRandomIP());
				element.setDestination(createRandomIP());
				fw.getElements().add(element);
			}
			confF.setFirewall(fw);
			firewall.setConfiguration(confF);
			
			//links
			Neighbour prevN = new Neighbour();
			prevN.setName(prev.getName());
			firewall.getNeighbour().add(prevN);
			Neighbour nextF = new Neighbour();
			nextF.setName(ipF);
			prev.getNeighbour().add(nextF);
			graph.getNode().add(firewall);
			
			prev=firewall;
		}
		
		//add DPIs
		for (int i = 0; i < numberDPI; i++) {
			String ipD = createRandomIP();
			Node dpi = new Node();
			dpi.setName(ipD);
			dpi.setFunctionalType(FunctionalTypes.DPI);
			Configuration confD = new Configuration();
			confD.setName("confD");
			Dpi d =  new Dpi();
			d.setDefaultAction(ActionTypes.ALLOW);
			for (int j = 0; j < numberOfRules; j++) {
				DpiElements element = new DpiElements();
				element.setAction(ActionTypes.DENY);
				element.setCondition("video");
				d.getDpiElements().add(element);
			}
			confD.setDpi(d);
			dpi.setConfiguration(confD);
			
			//links
			Neighbour prevN = new Neighbour();
			prevN.setName(prev.getName());
			dpi.getNeighbour().add(prevN);
			Neighbour nextF = new Neighbour();
			nextF.setName(ipD);
			prev.getNeighbour().add(nextF);
			graph.getNode().add(dpi);
			
			prev=dpi;
		}
		
		
		//add WAFss
		for (int i = 0; i < numberWaf; i++) {
			String ipW = createRandomIP();
			Node waf = new Node();
			waf.setName(ipW);
			waf.setFunctionalType(FunctionalTypes.WEB_APPLICATION_FIREWALL);
			Configuration confW = new Configuration();
			confW.setName("confW");
			WebApplicationFirewall w =  new WebApplicationFirewall();
			w.setDefaultAction(ActionTypes.ALLOW);
			for (int j = 0; j < numberOfRules; j++) {
				WafElements element = new WafElements();
				element.setAction(ActionTypes.DENY);
				element.setDomain("youtube.com");
				element.setUrl("youtube.com/32nfj3");
				w.getWafElements().add(element);
			}
			confW.setWebApplicationFirewall(w);
			waf.setConfiguration(confW);
					
			//links
			Neighbour prevN = new Neighbour();
			prevN.setName(prev.getName());
			waf.getNeighbour().add(prevN);
			Neighbour nextF = new Neighbour();
			nextF.setName(ipW);
			prev.getNeighbour().add(nextF);
			graph.getNode().add(waf);
					
			prev=waf;
		}
		
		
		// add forwarders
		for (int i = 0; i < numberForwarder; i++) {
			Node forwarder = new Node();
			String forwarderIP = createRandomIP();
			forwarder.setName(forwarderIP);
			forwarder.setFunctionalType(FunctionalTypes.FORWARDER);
			
			
			//links
			Neighbour prevN = new Neighbour();
			prevN.setName(prev.getName());
			forwarder.getNeighbour().add(prevN);
			Neighbour nextF = new Neighbour();
			nextF.setName(forwarderIP);
			prev.getNeighbour().add(nextF);
			
			graph.getNode().add(forwarder);
			prev=forwarder;
		}
		
				
		// one LB
		String ipL = createRandomIP();
		Node loadBalancer = new Node();
		loadBalancer.setFunctionalType(FunctionalTypes.LOADBALANCER);
		loadBalancer.setName(ipL);
		confN = new Configuration();
		confN.setName("confN");
		Loadbalancer lb = new Loadbalancer();
		lb.getPool().add(ipServer);
		confN.setLoadbalancer(lb);
		loadBalancer.setConfiguration(confN);
		//links
		Neighbour prevN = new Neighbour();
		prevN.setName(prev.getName());
		loadBalancer.getNeighbour().add(prevN);
		Neighbour nextF = new Neighbour();
		nextF.setName(ipL);
		prev.getNeighbour().add(nextF);
		
		graph.getNode().add(loadBalancer);
		prev=loadBalancer;
		
		
		
		//add server
		Node server = new Node();
		server.setFunctionalType(FunctionalTypes.WEBSERVER);
		server.setName(ipServer);
		Configuration confS = new Configuration();
		confS.setName("confB");
		Webserver ws = new Webserver();
		ws.setName(server.getName());
		confS.setWebserver(ws);
		server.setConfiguration(confS);
		//links
		prevN = new Neighbour();
		prevN.setName(prev.getName());
		server.getNeighbour().add(prevN);
		nextF = new Neighbour();
		nextF.setName(ipServer);
		prev.getNeighbour().add(nextF);
				
		graph.getNode().add(server);
		prev=server;
		
		//isolation
		createPolicy(PName.REACHABILITY_PROPERTY, nfv, ipClient, ipServer);
		
		
		nfv.getGraphs().getGraph().add(graph);
				
		return nfv;
	}

	private void createPolicy(PName type, NFV nfv, String IPClient, String IPServer) {
	
		
		Property property = new Property();
		property.setName(type);
		property.setGraph((long) 0);
		property.setSrc(IPClient);
		property.setDst(IPServer);
		property.setBody("search");
		HTTPDefinition hd = new HTTPDefinition();
		hd.setDomain("google.com");
		hd.setUrl("google.com/image");
		property.setHTTPDefinition(hd);
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
