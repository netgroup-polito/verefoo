package it.polito.verifoo.rest.neo4j;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
//import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import it.polito.verifoo.rest.jaxb.Graph;
import it.polito.verifoo.rest.jaxb.NFV;
import it.polito.verifoo.rest.jaxb.Neighbour;
import it.polito.verifoo.rest.jaxb.NodeConstraints.NodeMetrics;

public class Neo4jServer {
	private static enum RelTypes implements RelationshipType
	{
		CONNECTED_WITH, CONTAINS
	}
	GraphDatabaseService graphDb;
	public Neo4jServer( String pathToFile)
    {
    	graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( new File(pathToFile) );
    	registerShutdownHook( graphDb );
    }
    private static void registerShutdownHook( final GraphDatabaseService graphDb )
    {
        // Registers a shutdown hook for the Neo4j instance so that it
        // shuts down nicely when the VM exits (even if you "Ctrl-C" the
        // running application).
        Runtime.getRuntime().addShutdownHook( new Thread()
        {
            @Override
            public void run()
            {
                graphDb.shutdown();
            }
        } );
    }
    
    
    public void storeGraph(NFV root){
    	Map<String, Node> nodes = new HashMap<>();
    	//Relationship relationship;
    	Graph graph = root.getGraphs().getGraph().get(0);
    	//creates new nodes in the neo4j database
    	for(it.polito.verifoo.rest.jaxb.Node n : graph.getNode()){
    		nodes.put(n.getName(), graphDb.createNode());
    	}
    	//set property for the nodes
    	for(NodeMetrics n : root.getConstraints().getNodeConstraints().getNodeMetrics()){
    		Node node = nodes.get(n.getNode());
    		if(node == null) continue;
    		node.setProperty("cores", n.getCores());
    		node.setProperty("memory", n.getMemory());
    		node.setProperty("reqStorage", n.getReqStorage());
    		if(n.getNrOfOperations() != null)
        		node.setProperty("nrOfOperations", n.getNrOfOperations());
    		if(n.getMaxNodeLatency() != null)
        		node.setProperty("maxNodeLatency", n.getMaxNodeLatency());
    	}
    	//set connections between nodes
    	for(it.polito.verifoo.rest.jaxb.Node n : graph.getNode()){
    		Node source = nodes.get(n.getName());
    		for(Neighbour neighbour : n.getNeighbour()){
    			Node dest = nodes.get(neighbour.getName());
    	    	//relationship = 
    	    			source.createRelationshipTo( dest, RelTypes.CONNECTED_WITH );
    		}
    	}
    }
}
