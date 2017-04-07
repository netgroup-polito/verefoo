package it.polito.verigraph.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;
import javax.xml.bind.JAXBException;

import it.polito.neo4j.jaxb.GraphToNeo4j;
import it.polito.neo4j.jaxb.Graphs;
import it.polito.neo4j.jaxb.ObjectFactory;
import it.polito.neo4j.manager.Neo4jDBManager;
import it.polito.neo4j.manager.Neo4jLibrary;
import it.polito.neo4j.service.Service;
import it.polito.neo4j.exceptions.DuplicateNodeException;
import it.polito.neo4j.exceptions.MyInvalidObjectException;
import it.polito.neo4j.exceptions.MyInvalidIdException;
import it.polito.neo4j.exceptions.MyNotFoundException;
import it.polito.verigraph.database.DatabaseClass;
import it.polito.verigraph.exception.DataNotFoundException;
import it.polito.verigraph.exception.ForbiddenException;
import it.polito.verigraph.model.Graph;
import it.polito.verigraph.model.Neighbour;
import it.polito.verigraph.model.Node;

public class GraphService {
	
	
	private Neo4jDBManager manager=new Neo4jDBManager(); 
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

	public Graph updateGraph(Graph graph) throws JAXBException {
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
		
	
		manager.updateGraph(graph);
		System.out.println("update graph ok");
		
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
		
		manager.deleteGraph(id);
		
		synchronized(this){
			return graphs.remove(id);
		}
	}

	public Graph addGraph(Graph graph) throws JAXBException {
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
	
		manager.addGraph(graph);
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
