package it.polito.neo4j.manager;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.xml.bind.JAXBException;

import it.polito.neo4j.exceptions.DuplicateNodeException;
import it.polito.neo4j.exceptions.MyInvalidObjectException;
import it.polito.neo4j.exceptions.MyNotFoundException;
import it.polito.neo4j.jaxb.GraphToNeo4j;
import it.polito.neo4j.jaxb.ObjectFactory;
import it.polito.neo4j.jaxb.Paths;
import it.polito.neo4j.service.Service;
import it.polito.neo4j.exceptions.MyInvalidDirectionException;
import it.polito.neo4j.exceptions.MyInvalidIdException;
import it.polito.verigraph.model.Graph;
import it.polito.verigraph.model.Neighbour;
import it.polito.verigraph.model.Node;

public class Neo4jDBManager {
	
	public static Neo4jLibrary lib = Neo4jLibrary.getNeo4jLibrary();
	static Service service = new Service();
	//static ObjectFactory obFactory = new ObjectFactory();

	
	
	public void updateGraph(it.polito.verigraph.model.Graph graph) throws JAXBException{
			
		    it.polito.neo4j.jaxb.Graph graph_xml=GraphToNeo4j.generateObject(graph);
			it.polito.neo4j.jaxb.Graph graphReturned;
			long graphId;
			
			try{
				graphId = graph.getId();
				graphReturned = lib.updateGraph(graph_xml, graphId);
			}
			catch(MyNotFoundException e1){
				System.out.println("error update 1");
				throw new NotFoundException();
			}
			
			catch(DuplicateNodeException e3){
				System.out.println("error update 2");
				throw new BadRequestException(e3.getMessage());
			}
			catch(MyInvalidObjectException e4){
				System.out.println("error update 3");
				throw new BadRequestException(e4.getMessage());
			}
	}
	
	public void deleteGraph(long id){
		
				try{			
					lib.deleteGraph(id);
				}
				catch(MyNotFoundException e1){
					throw new NotFoundException();
				}		
	}

	public void addGraph(it.polito.verigraph.model.Graph graph) throws JAXBException {
		
			
				it.polito.neo4j.jaxb.Graph graph_xml=GraphToNeo4j.generateObject(graph);
				it.polito.neo4j.jaxb.Graph graphReturned;
				it.polito.neo4j.jaxb.Node node;
				
				/*System.out.println("grafo_xml id:" + graph_xml.getId());
				for(it.polito.neo4j.jaxb.Node nodes : graph_xml.getNode()){
					System.out.println("nodo:" + nodes.getName());
				}*/
			
								
				try{
					graphReturned = lib.createGraph(graph_xml);
					
				}
				catch(MyNotFoundException e){
					e.printStackTrace();
					System.exit(1);
					
				}
		
	}

	public void addNeighbours(long graphId, long nodeId, it.polito.verigraph.model.Neighbour neighbour) {
		// TODO Auto-generated method stub
		
		it.polito.neo4j.jaxb.Neighbour neighbour_xml=GraphToNeo4j.NeighbourToNeo4j(neighbour);
		it.polito.neo4j.jaxb.Neighbour neighReturned;
		try{
			neighReturned = lib.createNeighbour(neighbour_xml, graphId, nodeId);
		}		
		catch(MyNotFoundException e2){
			e2.printStackTrace();
			System.exit(1);
		}
	}

	public void deleteNeighbour(long graphId, long nodeId, long neighbourId) {
		// TODO Auto-generated method stub
		try{
			
			lib.deleteNeighbour(graphId, nodeId, neighbourId);
		}
		catch(MyNotFoundException e1){
			e1.printStackTrace();
			System.exit(1);
		}
		
		
	}

	public void updateNeighbour(long graphId, long nodeId, it.polito.verigraph.model.Neighbour neighbour) {
		// TODO Auto-generated method stub
		it.polito.neo4j.jaxb.Neighbour neighbour_xml=GraphToNeo4j.NeighbourToNeo4j(neighbour);
		it.polito.neo4j.jaxb.Node nodeReturned;
		try{
			nodeReturned=lib.updateNeighbour(neighbour_xml, graphId, nodeId, neighbour_xml.getId());
		}		
		catch(MyNotFoundException e2){
			throw new NotFoundException();
		} catch (MyInvalidObjectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	}

	public void addNode(long graphId, it.polito.verigraph.model.Node node) {
		// TODO Auto-generated method stub
		it.polito.neo4j.jaxb.Node node_xml=GraphToNeo4j.NodeToNeo4j(node);
		it.polito.neo4j.jaxb.Node nodeReturned;
		
		try
		{
			for(it.polito.neo4j.jaxb.Neighbour n :node_xml.getNeighbour()){
				System.out.println("vicini: " + n.getName());
				
			}
			
			nodeReturned = lib.createNode(node_xml, graphId);
		}
		catch(MyNotFoundException e1){
			e1.printStackTrace();
			System.exit(1);
		}
		
		catch(DuplicateNodeException e3){
			e3.printStackTrace();
			System.exit(1);
		}
		
	}

	public void updateNode(long graphId, Node node, long id) {
		// TODO Auto-generated method stub
		it.polito.neo4j.jaxb.Node node_xml=GraphToNeo4j.NodeToNeo4j(node);
		it.polito.neo4j.jaxb.Node nodeReturned;
		try{
			nodeReturned = lib.updateNode(node_xml, graphId, id);
		}
		catch(MyNotFoundException e2){
			e2.printStackTrace();
			System.exit(1);
		}
		catch(MyInvalidObjectException e3){
			e3.printStackTrace();
			System.exit(1);
		}
		
		
	}

	public void deleteNode(long graphId, long nodeId) {
		// TODO Auto-generated method stub
		
		try{
			lib.deleteNode(graphId, nodeId);
		}
		catch(MyNotFoundException e1){
			throw new NotFoundException("graph or node not found");
		}
	}
	
	

	public Paths getPath(long graphId, String source, String destination, String direction) throws MyInvalidDirectionException {
		// TODO Auto-generated method stub
		it.polito.neo4j.jaxb.Paths paths=(new ObjectFactory()).createPaths();
		try{
			if(source == null || destination == null || direction == null)
				throw new MyNotFoundException("Missing query parameters");
			service.checkValidDirection(direction);
			paths =  lib.findAllPathsBetweenTwoNodes(graphId, source, destination,direction);
		}
		catch(MyNotFoundException e1){
			e1.printStackTrace();
			System.exit(1);	
			return null;
		}
				
		return paths;
	}
}
