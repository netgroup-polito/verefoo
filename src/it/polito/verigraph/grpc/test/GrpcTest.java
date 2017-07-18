package it.polito.verigraph.grpc.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.runners.MethodSorters;

import it.polito.verigraph.grpc.ConfigurationGrpc;
import it.polito.verigraph.grpc.GraphGrpc;
import it.polito.verigraph.grpc.NeighbourGrpc;
import it.polito.verigraph.grpc.NewGraph;
import it.polito.verigraph.grpc.NewNeighbour;
import it.polito.verigraph.grpc.NewNode;
import it.polito.verigraph.grpc.NodeGrpc;
import it.polito.verigraph.grpc.client.Client;
import it.polito.verigraph.grpc.server.Service;

@RunWith(JUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GrpcTest {
	private Service server;
	private Client client;

	@Before
	public void setUpBeforeClass() throws Exception {
		client = new Client("localhost" , 50051);
		server = new Service(50051);
		
		server.start();
	}

	@After
	public void tearDown() throws Exception {
		server.stop();
		client.shutdown();
	}

	// method for comparing two non-null strings    
	private void compareString(String rs, String ts, String meaning) {
		assertNotNull("NULL "+meaning, ts);
		assertEquals("Wrong "+meaning, rs, ts);		
	}

	@Test
	public final void test1Load() throws Exception{
		String funcType1 = "vpnaccess";
		String funcType2 = "vpnexit";

		// load an existing graph with 2 nodes
		Map<String,String> map = new HashMap<String,String>();
		map.put("vpnexit", "Node2");
		ConfigurationGrpc conf = Client.createConfigurationGrpc(map, null, null, null);
		NodeGrpc node1 = Client.createNodeGrpc("Node1", funcType1, null, conf);						
		List<NeighbourGrpc> neighbours = new ArrayList<NeighbourGrpc>();
		NeighbourGrpc nb = Client.createNeighbourGrpc("Node1");
		neighbours.add(nb);
		map.clear();
		map.put("vpnaccess", "Node1");
		conf = Client.createConfigurationGrpc(map, null, null, null);
		NodeGrpc node2 = Client.createNodeGrpc("Node2", funcType2, neighbours, conf);
		List<NodeGrpc> nodes = new ArrayList<NodeGrpc>();
		nodes.add(node1);			
		nodes.add(node2);			
		GraphGrpc graph = Client.createGraphGrpc(nodes);
		//createGraph
		graph = client.createGraph(graph).getGraph();

		//getGraph
		GraphGrpc retreivedGraph = client.getGraph(graph.getId());

		assertNotNull("NULL Graph ",retreivedGraph);
		assertEquals(graph.getId(), retreivedGraph.getId());

		// check the name of first node and the id
		compareString(retreivedGraph.getNodeList().get(0).getName(), graph.getNodeList().get(0).getName(), "node name");
		assertEquals(retreivedGraph.getNodeList().get(0).getId(), graph.getNodeList().get(0).getId());

		// check the name of second node and the id
		compareString(retreivedGraph.getNodeList().get(1).getName(), graph.getNodeList().get(1).getName(), "node name");
		assertEquals(retreivedGraph.getNodeList().get(1).getId(), graph.getNodeList().get(1).getId());
		
		//updateGraph
		GraphGrpc updatedGraph = GraphGrpc.newBuilder().build();
		NewGraph response = client.updateGraph(graph.getId(),updatedGraph);
		
		assertEquals(response.getSuccess(),true);
	}

	@Test
	public final void test2LoadWithError() throws Exception{		
		// try to load a graph with node without functionalType
		NodeGrpc node = null;
		try{
			node = Client.createNodeGrpc("Node1", null, null, null);
			fail( "createNodeGrpc didn't throw when I expected it to" );
		}
		catch(Exception ex){			
		}
		List<NodeGrpc> nodes = new ArrayList<NodeGrpc>();
		if(node != null)
			nodes.add(node);
		
		GraphGrpc graph = Client.createGraphGrpc(nodes);
		graph = client.createGraph(graph).getGraph();
		
		assertEquals(graph.getId(), 2);
		
		//getGraphs
		Iterator<GraphGrpc> graphs = client.getGraphs().iterator();

		assertEquals(graphs.next().getId(), 1);
		assertEquals(graphs.next(), graph);
		
		//deleteGraph
		boolean resp= client.deleteGraph(graph.getId());
		assertEquals(resp, true);
		
		List<GraphGrpc> listGraphs = client.getGraphs();

		assertEquals(listGraphs.size(), 1);
		assertEquals(listGraphs.get(0).getId(), 1);
	}
	
	@Test
	public void test3Node() throws Exception {
		NodeGrpc ufoundedGraph = NodeGrpc.newBuilder()
				.setErrorMessage("Node with id 1 not found in graph with id 1").build();

		// graph not found in the server
		NodeGrpc node = client.getNode(1, 1);//id not present

		assertEquals(ufoundedGraph, node);

		// graph found in the server, but first add it
		NodeGrpc addedNode = Client.createNodeGrpc("Node4", "firewall", null, null);
		NewNode response = client.createNode(addedNode, 1);
		addedNode = response.getNode();	
		node = client.getNode(1, 1);

		assertEquals(addedNode.getId(), node.getId());

		//updateNode
		NodeGrpc updatedNode = Client.createNodeGrpc("Node9", "endhost", null, null);
		response = client.updateNode(1, addedNode.getId(), updatedNode);

		assertEquals(response.getSuccess(),true);
		
		//configureNode
		//this configuration is valid only on endhost!
		Map<String,String> params = new HashMap<String,String>();
		params.put("url", "www.facebook.com");
		params.put("body", "word");
		params.put("destination","server");
		params.put("protocol", "HTTP_REQUEST");
		ConfigurationGrpc configuration = Client.createConfigurationGrpc(params, null, null, null);
		
		boolean status = client.configureNode(1, 1, configuration);
		
		assertEquals(status,true);
	}

	@Test
	public void test4Nodes() throws Exception {		
		// setup
		GraphGrpc graph = Client.createGraphGrpc(null);
		//createGraph
		graph = client.createGraph(graph).getGraph();
		
		List<NeighbourGrpc> neighbours = new ArrayList<NeighbourGrpc>();
		NeighbourGrpc nb = Client.createNeighbourGrpc("Node6");
		neighbours.add(nb);
		NodeGrpc n1 = Client.createNodeGrpc("Node6", "mailserver", null, null); 
		NodeGrpc n2 = Client.createNodeGrpc("Node9", "endhost", neighbours, null);
		Map<String,String> map = new HashMap<String,String>();
		map.put("mailserver", "Node6");
		ConfigurationGrpc conf = Client.createConfigurationGrpc(map, null, null, null);
		NodeGrpc n3 = Client.createNodeGrpc("Node10", "mailclient", null, conf);
		NodeGrpc n4 = Client.createNodeGrpc("Node11", "nat", null, null);

		NewNode nw1 = client.createNode(n1, graph.getId());
		NewNode nw2 = client.createNode(n2, graph.getId());
		NewNode nw3 = client.createNode(n3, graph.getId());
		NewNode nw4 = client.createNode(n4, graph.getId());
		assertEquals(nw1.getSuccess(),true);
		assertEquals(nw2.getSuccess(),true);
		assertEquals(nw3.getSuccess(),true);
		assertEquals(nw4.getSuccess(),true);
		n1 = NodeGrpc.newBuilder(n1).setId(nw1.getNode().getId()).build();		
		n2 = NodeGrpc.newBuilder(n2).setId(nw2.getNode().getId()).build();
		n3 = NodeGrpc.newBuilder(n3).setId(nw3.getNode().getId()).build();		
		n4 = NodeGrpc.newBuilder(n4).setId(nw4.getNode().getId()).build();
		
		// getNodes
		Iterator<NodeGrpc> nodes = client.getNodes(graph.getId()).iterator();

		assertEquals(nodes.next().getName(), n1.getName());
		assertEquals(nodes.next().getName(), n2.getName());
		assertEquals(nodes.next().getName(), n3.getName());
		assertEquals(nodes.next().getName(), n4.getName());
		
		//deleteNode
		client.deleteNode(graph.getId(), 1);
		// run
		nodes = client.getNodes(graph.getId()).iterator();

		assertEquals(nodes.next().getName(), n2.getName());
		assertEquals(nodes.next().getName(), n3.getName());
		assertEquals(nodes.next().getName(), n4.getName());
	}
	
	@Test
	public void test5Neighbours() throws Exception {
		NeighbourGrpc ufoundedNeighbour = NeighbourGrpc.newBuilder()
				.setErrorMessage("Neighbour with id 1 not found for node with id 1 in graph with id 1").build();;

		// Neighbour not found in the server
		NeighbourGrpc neighbour = client.getNeighbour(1, 1, 1);//id not present
		
		assertEquals(ufoundedNeighbour, neighbour);
		
		GraphGrpc graph = Client.createGraphGrpc(null);
		graph = client.createGraph(graph).getGraph();
		
		List<NeighbourGrpc> neighbours = new ArrayList<NeighbourGrpc>();
		NeighbourGrpc nb = Client.createNeighbourGrpc("Node1");
		neighbours.add(nb);
		NodeGrpc n1 = Client.createNodeGrpc("Node1", "antispam", null, null); 
		NodeGrpc n2 = Client.createNodeGrpc("Node2", "endhost", neighbours, null);
		NodeGrpc n3 = Client.createNodeGrpc("Node3", "endhost", null, null);
		NodeGrpc n4 = Client.createNodeGrpc("Node4", "endpoint", null, null);
		NodeGrpc n5 = Client.createNodeGrpc("Node5", "webserver", null, null);
		Map<String,String> map = new HashMap<String,String>();
		map.put("webserver", "Node5");
		ConfigurationGrpc conf = Client.createConfigurationGrpc(map, null, null, null);
		NodeGrpc n6 = Client.createNodeGrpc("Node6", "webclient", null, conf);
		NodeGrpc n7 = Client.createNodeGrpc("Node7", "cache", null, null);
		NodeGrpc n8 = Client.createNodeGrpc("Node8", "firewall", null, null);
		NodeGrpc n9 = Client.createNodeGrpc("Node9", "fieldmodifier", null, null);
		NodeGrpc n10 = Client.createNodeGrpc("Node10", "dpi", null, null);
		NewNode nw1 = client.createNode(n1, graph.getId());		
		NewNode nw2 = client.createNode(n2, graph.getId());
		NewNode nw3 = client.createNode(n3, graph.getId());
		NewNode nw4 = client.createNode(n4, graph.getId());
		NewNode nw5 = client.createNode(n5, graph.getId());
		NewNode nw6 = client.createNode(n6, graph.getId());
		NewNode nw7 = client.createNode(n7, graph.getId());
		NewNode nw8 = client.createNode(n8, graph.getId());
		NewNode nw9 = client.createNode(n9, graph.getId());
		NewNode nw10 = client.createNode(n10, graph.getId());
		assertEquals(nw1.getSuccess(),true);
		assertEquals(nw2.getSuccess(),true);
		assertEquals(nw3.getSuccess(),true);
		assertEquals(nw4.getSuccess(),true);
		assertEquals(nw5.getSuccess(),true);
		assertEquals(nw6.getSuccess(),true);
		assertEquals(nw7.getSuccess(),true);
		assertEquals(nw8.getSuccess(),true);
		assertEquals(nw9.getSuccess(),true);
		assertEquals(nw10.getSuccess(),true);

		// getNeighbour, but first add it
		NeighbourGrpc addedNeighbour = Client.createNeighbourGrpc("Node9");
		NewNeighbour response = client.createNeighbour(addedNeighbour, graph.getId(), nw1.getNode().getId());
		addedNeighbour = response.getNeighbour();
		neighbour = client.getNeighbour(graph.getId(), nw1.getNode().getId(), addedNeighbour.getId());

		assertEquals(addedNeighbour.getId(), neighbour.getId());

		//updateNeighbour
		NeighbourGrpc updatedNeighbour = Client.createNeighbourGrpc("Node10");
		
		response = client.updateNeighbour(graph.getId(), 1, addedNeighbour.getId(),updatedNeighbour);

		assertEquals(response.getSuccess(),true);
		assertEquals(response.getNeighbour().getName(),"Node10");
	}

	@Test
	public void test6Neighbours() throws Exception {
		// setup
		GraphGrpc graph = Client.createGraphGrpc(null);
		//createGraph
		graph = client.createGraph(graph).getGraph();

		NodeGrpc n1 = Client.createNodeGrpc("Node1", "antispam", null, null); 
		NodeGrpc n2 = Client.createNodeGrpc("Node2", "endhost", null, null);
		NodeGrpc n3 = Client.createNodeGrpc("Node3", "endhost", null, null);
		NodeGrpc n4 = Client.createNodeGrpc("Node4", "endpoint", null, null);
		NewNode nw1 = client.createNode(n1, graph.getId());		
		NewNode nw2 = client.createNode(n2, graph.getId());
		NewNode nw3 = client.createNode(n3, graph.getId());
		NewNode nw4 = client.createNode(n4, graph.getId());
		assertEquals(nw1.getSuccess(),true);
		assertEquals(nw2.getSuccess(),true);
		assertEquals(nw3.getSuccess(),true);
		assertEquals(nw4.getSuccess(),true);
		
		//createNeighbour
		NeighbourGrpc nn1 = Client.createNeighbourGrpc("Node2");
		NewNeighbour addedNeighbour1 = client.createNeighbour(nn1, graph.getId(), nw1.getNode().getId());
		assertEquals(addedNeighbour1.getSuccess(),true);
		NeighbourGrpc nn2 = Client.createNeighbourGrpc("Node3");
		NewNeighbour addedNeighbour2 = client.createNeighbour(nn2, graph.getId(), nw1.getNode().getId());
		assertEquals(addedNeighbour2.getSuccess(),true);
		NeighbourGrpc nn3 = Client.createNeighbourGrpc("Node4");
		NewNeighbour addedNeighbour3 = client.createNeighbour(nn3, graph.getId(), nw1.getNode().getId());
		assertEquals(addedNeighbour3.getSuccess(),true);
		
		nn1 = NeighbourGrpc.newBuilder(nn1).setId(1).build();		
		nn2 = NeighbourGrpc.newBuilder(nn2).setId(2).build();
		nn3 = NeighbourGrpc.newBuilder(nn3).setId(3).build();	
		// run
		Iterator<NeighbourGrpc> neighbours = client.getNeighbours(graph.getId(), nw1.getNode().getId()).iterator();

		assertEquals(neighbours.next(), nn1);
		assertEquals(neighbours.next(), nn2);
		assertEquals(neighbours.next(), nn3);
		
		//deleteNeighbour
		boolean succ = client.deleteNeighbour(graph.getId(), nw1.getNode().getId(), 1);
		assertEquals(succ, true);
		// run
		neighbours = client.getNeighbours(graph.getId(), nw1.getNode().getId()).iterator();

		assertEquals(neighbours.next(), nn2);
		assertEquals(neighbours.next(), nn3);
	}
}
