package it.polito.escape.verify.database;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import it.polito.escape.verify.model.Graph;
import it.polito.escape.verify.model.Neighbour;
import it.polito.escape.verify.model.Node;

public class DatabaseClass {
	
	private static ConcurrentHashMap<Long, Graph> graphs = new ConcurrentHashMap<>();
	
	public static ConcurrentHashMap<Long, Graph> getGraphs() {
		Graph graph = new Graph();
		Node node = new Node();
		node.setId(1);
		node.setName("matteo");
		node.setFunctional_type("endpoint");
		Neighbour neighbour = new Neighbour();
		neighbour.setId(1);
		neighbour.setName("ciccone");
		node.getNeighbours().put(1L, neighbour);
		graph.getNodes().put(1L, node);
		graphs.put(1L, graph);
		
		return graphs;
	}
	public synchronized static int getNumberOfGraphs(){
		return graphs.size();
	}
	public synchronized static int getGraphNumberOfNodes(long graphId) {
		Graph graph = graphs.get(graphId);
		if (graph == null)
			return 0;
		Map<Long, Node> nodes = graph.getNodes();
		if (nodes == null)
			return 0;
		return nodes.size();
	}
}
