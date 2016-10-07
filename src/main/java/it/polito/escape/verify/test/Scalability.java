package it.polito.escape.verify.test;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import it.polito.escape.verify.client.VerifyClient;
import it.polito.escape.verify.client.VerifyClientException;
import it.polito.escape.verify.model.Configuration;
import it.polito.escape.verify.model.Graph;
import it.polito.escape.verify.model.Neighbour;
import it.polito.escape.verify.model.Node;
import it.polito.escape.verify.model.Verification;

public class Scalability {
	
	private VerifyClient client = new VerifyClient("http://localhost:8080/verify/api");

	public static void main(String[] args) throws VerifyClientException {
		Scalability s = new Scalability();
		
		reachabilityTest(s,50);                                                                                                                                                                            
		reachabilityTest(s,100);                                                                                                                                                                           
		reachabilityTest(s,150);                                                                                                                                                                           
		reachabilityTest(s,200);                                                                                                                                                                           
		reachabilityTest(s,250);                                                                                                                                                                           
		reachabilityTest(s,300);                                                                                                                                                                           
		reachabilityTest(s,350);                                                                                                                                                                           
		reachabilityTest(s,400);                                                                                                                                                                           
		reachabilityTest(s,450);                                                                                                                                                                           
		reachabilityTest(s,500);                                                                                                                                                                           
		reachabilityTest(s,550);                                                                                                                                                                           
		reachabilityTest(s,600);                                                                                                                                                                           
		reachabilityTest(s,650);                                                                                                                                                                           
		reachabilityTest(s,700);                                                                                                                                                                           
		reachabilityTest(s,750);                                                                                                                                                                           
		reachabilityTest(s,800);                                                                                                                                                                           
		reachabilityTest(s,850);                                                                                                                                                                           
		reachabilityTest(s,900);                                                                                                                                                                           
		reachabilityTest(s,950);
		reachabilityTest(s,1000);
/*		
		isolationTest(s,50);                                                                                                                                                                            
		isolationTest(s,100);                                                                                                                                                                           
		isolationTest(s,150);                                                                                                                                                                           
		isolationTest(s,200);                                                                                                                                                                           
		isolationTest(s,250);                                                                                                                                                                           
		isolationTest(s,300);                                                                                                                                                                           
		isolationTest(s,350);                                                                                                                                                                           
		isolationTest(s,400);                                                                                                                                                                           
		isolationTest(s,450);                                                                                                                                                                           
		isolationTest(s,500);                                                                                                                                                                           
		isolationTest(s,550);                                                                                                                                                                           
		isolationTest(s,600);                                                                                                                                                                           
		isolationTest(s,650);                                                                                                                                                                           
		isolationTest(s,700);                                                                                                                                                                           
		isolationTest(s,750);                                                                                                                                                                           
		isolationTest(s,800);                                                                                                                                                                           
		isolationTest(s,850);                                                                                                                                                                           
		isolationTest(s,900);                                                                                                                                                                           
		isolationTest(s,950);
		isolationTest(s,1000);
		
		traversalTest(s,50);                                                                                                                                                                            
		traversalTest(s,100);                                                                                                                                                                           
		traversalTest(s,150);                                                                                                                                                                           
		traversalTest(s,200);                                                                                                                                                                           
		traversalTest(s,250);                                                                                                                                                                           
		traversalTest(s,300);                                                                                                                                                                           
		traversalTest(s,350);                                                                                                                                                                           
		traversalTest(s,400);                                                                                                                                                                           
		traversalTest(s,450);                                                                                                                                                                           
		traversalTest(s,500);                                                                                                                                                                           
		traversalTest(s,550);                                                                                                                                                                           
		traversalTest(s,600);                                                                                                                                                                           
		traversalTest(s,650);                                                                                                                                                                           
		traversalTest(s,700);                                                                                                                                                                           
		traversalTest(s,750);                                                                                                                                                                           
		traversalTest(s,800);                                                                                                                                                                           
		traversalTest(s,850);                                                                                                                                                                           
		traversalTest(s,900);                                                                                                                                                                           
		traversalTest(s,950);
		traversalTest(s,1000);*/
	}

	private static void reachabilityTest(Scalability s, int n) throws VerifyClientException {
		System.out.printf("Reachability test with " + n + " NATs: ");
		printTimestamp();
		Graph graph = generateNatScenario(n);
		Graph createdGraph = s.client.createGraph(graph).readEntity(Graph.class);
		Verification result = s.client.getReachability(createdGraph.getId(), "client", "server");
		System.out.println("Test returned " + result.getResult());
		System.out.printf("Finished reachability test with " + n + " NATs: ");
		printTimestamp();
		System.out.println();
	}
	
	private static void isolationTest(Scalability s, int n) throws VerifyClientException {
		System.out.printf("Isolation test with " + n + " NATs: ");
		printTimestamp();
		Graph graph = generateNatScenario(n);
		Graph createdGraph = s.client.createGraph(graph).readEntity(Graph.class);
		Verification result = s.client.getIsolation(createdGraph.getId(), "client", "server", "nat1");
		System.out.println("Test returned " + result.getResult());
		System.out.printf("Finished isolation test with " + n + " NATs: ");
		printTimestamp();
		System.out.println();
	}
	
	private static void traversalTest(Scalability s, int n) throws VerifyClientException {
		System.out.printf("Traversal test with " + n + " NATs: ");
		printTimestamp();
		Graph graph = generateNatScenario(n);
		Graph createdGraph = s.client.createGraph(graph).readEntity(Graph.class);
		Verification result = s.client.getIsolation(createdGraph.getId(), "client", "server", "nat1");
		System.out.println("Test returned " + result.getResult());
		System.out.printf("Finished traversal test with " + n + " NATs: ");
		printTimestamp();
		System.out.println();
	}

	private static void printTimestamp() {
		java.util.Date date= new java.util.Date();
		System.out.println(new Timestamp(date.getTime()));		
	}

	private static Graph generateNatScenario(int n) {
		List<Node> nodes = new ArrayList<Node>();
		
		Node client = new Node();
		client.setName("client");
		client.setFunctional_type("endhost");
		ArrayNode clientConfigArray = new ObjectMapper().createArrayNode();
		JsonNode clientConfig = new ObjectMapper().createObjectNode();
		((ObjectNode)clientConfig).put("url", "www.facebook.com");
		((ObjectNode)clientConfig).put("body", "word");
		((ObjectNode)clientConfig).put("destination","server");
		((ObjectNode)clientConfig).put("protocol", "HTTP_REQUEST");
		clientConfigArray.add(clientConfig);
		client.setConfiguration(new Configuration(client.getName(),"", clientConfigArray));
		
		Map<Long, Neighbour> clientNeighbours = new HashMap<Long, Neighbour>();
		clientNeighbours.put(1L, new Neighbour(1L, "nat1"));
		client.setNeighbours(clientNeighbours );
		//add client to list
		nodes.add(client);
		
		for(int i=0; i< n;i++){
			Node nat = new Node();
			nat.setId(i+1);
			nat.setName("nat" + (i+1));
			nat.setFunctional_type("nat");
			ArrayNode configArray = new ObjectMapper().createArrayNode();
			
			Map<Long, Neighbour> natNeighbours = new HashMap<Long, Neighbour>();
			
			//set left neighbour for each node except the first 
			if(nat.getId() != 1){
				natNeighbours.put(1L, new Neighbour(1L, "nat" + i));
				configArray.add("client");
			}
			//first nat: set only client as neighbour and natted node
			else{
				natNeighbours.put(1L, new Neighbour(1L, "client"));
				configArray.add("client");
			}
			//set right neighbour for each node except the last 
			if(nat.getId() != n){
				natNeighbours.put(2L, new Neighbour(1L, "nat" + (i+2)));
			}
			//last nat: set server as neighbour
			else{
				natNeighbours.put(2L, new Neighbour(1L, "server"));
			}
						
			nat.setNeighbours(natNeighbours);
			nat.setConfiguration(new Configuration(nat.getName(),"", configArray));
			
			//add nat to list
			nodes.add(nat);
		}
		
		Node server = new Node();
		server.setName("server");
		server.setFunctional_type("webserver");
		ArrayNode serverConfigArray = new ObjectMapper().createArrayNode();
		server.setConfiguration(new Configuration(server.getName(),"", serverConfigArray));
		
		Map<Long, Neighbour> serverNeighbours = new HashMap<Long, Neighbour>();
		serverNeighbours.put(1L, new Neighbour(1L, "nat" + (n)));
		server.setNeighbours(serverNeighbours );
		
		//add server to list
		nodes.add(server);
		
		//create graph
		Graph g = new Graph();
		Map<Long, Node> graphNodes = new HashMap<Long, Node>();
		long index = 1L;
		for (Node node : nodes){
			graphNodes.put(index, node);
			index++;
		}
		g.setNodes(graphNodes);
		
		return g;
	}
	
	private static Graph generateFirewallScenario(int n) {
		List<Node> nodes = new ArrayList<Node>();
		
		Node client = new Node();
		client.setName("client");
		client.setFunctional_type("endhost");
		ArrayNode clientConfigArray = new ObjectMapper().createArrayNode();
		JsonNode clientConfig = new ObjectMapper().createObjectNode();
		((ObjectNode)clientConfig).put("url", "www.facebook.com");
		((ObjectNode)clientConfig).put("body", "word");
		((ObjectNode)clientConfig).put("destination","server");
		((ObjectNode)clientConfig).put("protocol", "HTTP_REQUEST");
		clientConfigArray.add(clientConfig);
		client.setConfiguration(new Configuration(client.getName(),"", clientConfigArray));
		
		Map<Long, Neighbour> clientNeighbours = new HashMap<Long, Neighbour>();
		clientNeighbours.put(1L, new Neighbour(1L, "firewall1"));
		client.setNeighbours(clientNeighbours );
		//add client to list
		nodes.add(client);
		
		for(int i=0; i< n;i++){
			Node firewall = new Node();
			firewall.setId(i+1);
			firewall.setName("firewall" + (i+1));
			firewall.setFunctional_type("firewall");
			ArrayNode configArray = new ObjectMapper().createArrayNode();
			
			Map<Long, Neighbour> natNeighbours = new HashMap<Long, Neighbour>();
			
			//set left neighbour for each node except the first 
			if(firewall.getId() != 1){
				natNeighbours.put(1L, new Neighbour(1L, "firewall" + i));
			}
			//first firewall: set only client as neighbour and natted node
			else{
				natNeighbours.put(1L, new Neighbour(1L, "client"));
			}
			//set right neighbour for each node except the last 
			if(firewall.getId() != n){
				natNeighbours.put(2L, new Neighbour(1L, "firewall" + (i+2)));
			}
			//last firewall: set server as neighbour
			else{
				natNeighbours.put(2L, new Neighbour(1L, "server"));
			}
						
			firewall.setNeighbours(natNeighbours);
			firewall.setConfiguration(new Configuration(firewall.getName(),"", configArray));
			
			//add nat to list
			nodes.add(firewall);
		}
		
		Node server = new Node();
		server.setName("server");
		server.setFunctional_type("webserver");
		ArrayNode serverConfigArray = new ObjectMapper().createArrayNode();
		server.setConfiguration(new Configuration(server.getName(),"", serverConfigArray));
		
		Map<Long, Neighbour> serverNeighbours = new HashMap<Long, Neighbour>();
		serverNeighbours.put(1L, new Neighbour(1L, "firewall" + (n)));
		server.setNeighbours(serverNeighbours );
		
		//add server to list
		nodes.add(server);
		
		//create graph
		Graph g = new Graph();
		Map<Long, Node> graphNodes = new HashMap<Long, Node>();
		long index = 1L;
		for (Node node : nodes){
			graphNodes.put(index, node);
			index++;
		}
		g.setNodes(graphNodes);
		
		return g;
	}
	
	private static Graph generateDpiScenario(int n) {
		List<Node> nodes = new ArrayList<Node>();
		
		Node client = new Node();
		client.setName("client");
		client.setFunctional_type("endhost");
		ArrayNode clientConfigArray = new ObjectMapper().createArrayNode();
		JsonNode clientConfig = new ObjectMapper().createObjectNode();
		((ObjectNode)clientConfig).put("url", "www.facebook.com");
		((ObjectNode)clientConfig).put("body", "word");
		((ObjectNode)clientConfig).put("destination","server");
		((ObjectNode)clientConfig).put("protocol", "HTTP_REQUEST");
		clientConfigArray.add(clientConfig);
		client.setConfiguration(new Configuration(client.getName(),"", clientConfigArray));
		
		Map<Long, Neighbour> clientNeighbours = new HashMap<Long, Neighbour>();
		clientNeighbours.put(1L, new Neighbour(1L, "dpi1"));
		client.setNeighbours(clientNeighbours );
		//add client to list
		nodes.add(client);
		
		for(int i=0; i< n;i++){
			Node dpi = new Node();
			dpi.setId(i+1);
			dpi.setName("dpi" + (i+1));
			dpi.setFunctional_type("dpi");
			ArrayNode configArray = new ObjectMapper().createArrayNode();
			configArray.add("drug");
			
			Map<Long, Neighbour> natNeighbours = new HashMap<Long, Neighbour>();
			
			//set left neighbour for each node except the first 
			if(dpi.getId() != 1){
				natNeighbours.put(1L, new Neighbour(1L, "dpi" + i));
			}
			//first firewall: set only client as neighbour and natted node
			else{
				natNeighbours.put(1L, new Neighbour(1L, "client"));
			}
			//set right neighbour for each node except the last 
			if(dpi.getId() != n){
				natNeighbours.put(2L, new Neighbour(1L, "dpi" + (i+2)));
			}
			//last firewall: set server as neighbour
			else{
				natNeighbours.put(2L, new Neighbour(1L, "server"));
			}
						
			dpi.setNeighbours(natNeighbours);
			dpi.setConfiguration(new Configuration(dpi.getName(),"", configArray));
			
			//add nat to list
			nodes.add(dpi);
		}
		
		Node server = new Node();
		server.setName("server");
		server.setFunctional_type("webserver");
		ArrayNode serverConfigArray = new ObjectMapper().createArrayNode();
		server.setConfiguration(new Configuration(server.getName(),"", serverConfigArray));
		
		Map<Long, Neighbour> serverNeighbours = new HashMap<Long, Neighbour>();
		serverNeighbours.put(1L, new Neighbour(1L, "dpi" + (n)));
		server.setNeighbours(serverNeighbours );
		
		//add server to list
		nodes.add(server);
		
		//create graph
		Graph g = new Graph();
		Map<Long, Node> graphNodes = new HashMap<Long, Node>();
		long index = 1L;
		for (Node node : nodes){
			graphNodes.put(index, node);
			index++;
		}
		g.setNodes(graphNodes);
		
		return g;
	}

}
