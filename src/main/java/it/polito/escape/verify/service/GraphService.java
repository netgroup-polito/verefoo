package it.polito.escape.verify.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.escape.verify.database.DatabaseClass;
import it.polito.escape.verify.exception.DataNotFoundException;
import it.polito.escape.verify.exception.ForbiddenException;
import it.polito.escape.verify.model.Graph;
import it.polito.escape.verify.model.Neighbour;
import it.polito.escape.verify.model.Node;

public class GraphService {

	private Map<Long, Graph> graphs = DatabaseClass.getInstance().getGraphs();

	public GraphService() {

	}

	public List<Graph> getAllGraphs() {
		return new ArrayList<Graph>(graphs.values());
	}

	public Graph getGraph(long id) {
		if (id <= 0) {
			throw new ForbiddenException("Illegal graph id: " + id);
		}
		Graph graph = graphs.get(id);
		if (graph == null) {
			throw new DataNotFoundException("Graph with id " + id + " not found");
		}
		return graph;
	}

	public Graph updateGraph(Graph graph) {
		if (graph.getId() <= 0) {
			throw new ForbiddenException("Illegal graph id: " + graph.getId());
		}
		Graph localGraph = graphs.get(graph.getId());
		if (localGraph == null) {
			throw new DataNotFoundException("Graph with id " + graph.getId() + " not found");
		}
		
		validateGraph(graph);
		
//		int numberOfNodes = 0;
//		for (Node node : graph.getNodes().values()) {
//
//			node.setId(++numberOfNodes);
//
//			int numberOfNodeNeighbours = 0;
//			for (Neighbour neighbour : node.getNeighbours().values()) {
//				neighbour.setId(++numberOfNodeNeighbours);
//			}
//		}
		
		for (Map.Entry<Long, Node> nodeEntry : graph.getNodes().entrySet()){
			nodeEntry.getValue().setId(nodeEntry.getKey());
			
			for (Map.Entry<Long, Neighbour> neighbourEntry : nodeEntry.getValue().getNeighbours().entrySet()){
				neighbourEntry.getValue().setId(neighbourEntry.getKey());
			}
		}
		
		synchronized(this){
			graphs.put(graph.getId(), graph);
			DatabaseClass.persistDatabase();
			return graph;
		}
	}

	public Graph removeGraph(long id) {
		if (id <= 0) {
			throw new ForbiddenException("Illegal graph id: " + id);
		}
		synchronized(this){
			return graphs.remove(id);
		}
	}

	public Graph addGraph(Graph graph) {
		validateGraph(graph);
		
		synchronized (this) {
			graph.setId(DatabaseClass.getInstance().getNumberOfGraphs() + 1);
		}
//		int numberOfNodes = 0;
//		for (Node node : graph.getNodes().values()) {
//
//			node.setId(++numberOfNodes);
//
//			int numberOfNodeNeighbours = 0;
//			for (Neighbour neighbour : node.getNeighbours().values()) {
//				neighbour.setId(++numberOfNodeNeighbours);
//			}
//		}
		
		for (Map.Entry<Long, Node> nodeEntry : graph.getNodes().entrySet()){
			nodeEntry.getValue().setId(nodeEntry.getKey());
			
			for (Map.Entry<Long, Neighbour> neighbourEntry : nodeEntry.getValue().getNeighbours().entrySet()){
				neighbourEntry.getValue().setId(neighbourEntry.getKey());
			}
		}
		
		synchronized(this){
			graphs.put(graph.getId(), graph);
			DatabaseClass.persistDatabase();
			return graph;
		}
	}

	public static void validateGraph(Graph graph) {
		for (Node node : graph.getNodes().values()) {
			NodeService.validateNode(graph, node);
		}
	}
}
