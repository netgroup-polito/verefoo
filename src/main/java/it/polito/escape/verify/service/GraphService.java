package it.polito.escape.verify.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.escape.verify.exception.BadRequestException;
import it.polito.escape.verify.exception.DataNotFoundException;
import it.polito.escape.verify.exception.ForbiddenException;
import it.polito.escape.verify.database.DatabaseClass;
import it.polito.escape.verify.model.Graph;
import it.polito.escape.verify.model.Neighbour;
import it.polito.escape.verify.model.Node;

public class GraphService {
	
	private Map<Long, Graph> graphs = DatabaseClass.getGraphs();
	
	public GraphService(){
		
	}
	
	public List<Graph> getAllGraphs(){
		return new ArrayList<Graph>(graphs.values());
	}
	
	public Graph getGraph(long id){
		if (id <= 0){
			throw new ForbiddenException("Illegal graph id: " + id);
		}
		Graph graph = graphs.get(id);
		if (graph == null){
			throw new DataNotFoundException("Graph with id " + id + " not found");
		}
		return graph;
	}
	
	public Graph updateGraph(Graph graph){
		if (graph.getId() <= 0){
			throw new ForbiddenException("Illegal graph id: " + graph.getId());
		}		
		Graph localGraph = graphs.get(graph.getId());
		if (localGraph == null){
			throw new DataNotFoundException("Graph with id " + graph.getId() + " not found");
		}
		if (!isValidGraph(graph))
			throw new BadRequestException("Given graph is not valid!");
		
		graphs.put(graph.getId(), graph);
		
		return graph;
	}
	
	public Graph removeGraph(long id){
		if (id <= 0){
			throw new ForbiddenException("Illegal graph id: " + id);
		}
		return graphs.remove(id);
	}

	public Graph addGraph(Graph graph) {
		if (isValidGraph(graph) == false)
			throw new BadRequestException("Given graph is not valid!");
		
		synchronized(this){
			graph.setId(DatabaseClass.getNumberOfGraphs() + 1);
		}
		int numberOfNodes = 0;
		for (Node node : graph.getNodes().values()){
			synchronized(this){
				node.setId(++numberOfNodes);
			}
			int numberOfNodeNeighbours = 0;
			for (Neighbour neighbour : node.getNeighbours().values()){
				synchronized(this){
					neighbour.setId(++numberOfNodeNeighbours);
				}
			}
		}
		graphs.put(graph.getId(), graph);
		return graph;
	}
	
	
	public static boolean isValidGraph(Graph graph){
		for (Node node : graph.getNodes().values()){
			if (NodeService.isValidNode(node) == false)
				return false;
		}
		return true;
	}
}
