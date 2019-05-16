package it.polito.verefoo.tools.neo4j;

import static org.neo4j.driver.v1.Values.parameters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.neo4j.driver.v1.AuthTokens;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.GraphDatabase;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.Transaction;

import it.polito.verefoo.jaxb.Graph;
import it.polito.verefoo.jaxb.NFV;
import it.polito.verefoo.jaxb.Neighbour;
import it.polito.verefoo.jaxb.NodeConstraints.NodeMetrics;
/**
 * This class interact with a Neo4j DB through the Cypher Query Language to store the service graph information
 * @author Antonio
 *
 */
public class Neo4jClient implements AutoCloseable{

    private final Driver driver;
    /**
     * Initialize the driver to interact with the neo4j server
     */
	public Neo4jClient( String uri, String user, String password)
    {
        driver = GraphDatabase.driver( uri, AuthTokens.basic( user, password ) );
    }
    /**
     * Close the connection with the neo4j server
     */
    @Override
    public void close() throws Exception
    {
        driver.close();
    }
    /**
     * Store the information about the service graph
     * @param root the NFV element received as input
     */
    public void storeGraph(NFV root){
    	 try ( Session session = driver.session() ){
    		 System.out.println("Session created");
    	     for(Graph graph : root.getGraphs().getGraph()){
    	    	 long id = graph.getId();
    	    	 
        		 //clear all
        		 session.run("MATCH (n:Node {graphID: "+ id +"}) DETACH DELETE n");
        		 session.run("MATCH (g:Graph {id: "+ id +"}) DETACH DELETE g");
        		 System.out.println("neo4j>> Cleared all previous overlapping data");
        		 //create new graph
        		 session.run("CREATE (g:Graph) SET g.id = " + id + " RETURN g");
        		 System.out.println("neo4j>> Graph " + id + " created");
    	    	 List<Map<String, Object>> maps = new ArrayList<>();
        	     for(it.polito.verefoo.jaxb.Node node : graph.getNode()){
        	    	 NodeMetrics nodeMetr = root.getConstraints().getNodeConstraints().getNodeMetrics().stream()
        	    			 								.filter(nm -> nm.getNode().equals(node.getName()))
        	    			 								.findFirst().orElse(null);
                     
                     Map<String, Object> map = new HashMap<>();
                     map.put("name",node.getName());
                     map.put("graphID",id);
                     if(nodeMetr != null){
                    	 map.put("cores", nodeMetr.getCores());
                    	 map.put("memory", nodeMetr.getMemory());
                    	 map.put("reqStorage", nodeMetr.getReqStorage());
                 		if(nodeMetr.getNrOfOperations() != null)
                 			map.put("nrOfOperations", nodeMetr.getNrOfOperations());
                 		if(nodeMetr.getMaxNodeLatency() != null)
                 			map.put("maxNodeLatency", nodeMetr.getMaxNodeLatency());
                     }
                     maps.add(map);
        	     }
        	     Map<String, Object> params = new HashMap<>();
                 params.put( "props", maps );
                 System.out.println("Parameters created: " + params);
                 String query = "UNWIND $props AS properties CREATE (n:Node) SET n = properties RETURN n";
                 try (Transaction tx = session.beginTransaction())
                 {
                     tx.run(query, params);
                     tx.success();  // Mark this write as successful.
                 }
                 System.out.println("neo4j>> Nodes created");
                 query = "MATCH (a:Node {graphID : " + id + "}) "+
                		 "MATCH (g:Graph {id : " + id + "}) "+
                		 "MERGE (g)-[:CONTAINS]->(a)";
                 try (Transaction tx = session.beginTransaction())
                 {
                     tx.run(query, params);
                     tx.success();  // Mark this write as successful.
                 }
                 System.out.println("neo4j>> Nodes associated with the graph");
                 for(it.polito.verefoo.jaxb.Node src : graph.getNode()){
             		for(Neighbour dst : src.getNeighbour()){
             			try (Transaction tx = session.beginTransaction())
                        {
             				query = "MATCH (a:Node {name: $src, graphID:" + id + "}) " +
                                    "MATCH (b:Node {name: $dst, graphID:" + id + "}) " +
                                    "MERGE (a)-[:CONNECTED_WITH]->(b)";
                            tx.run(query, parameters( "src", src.getName(), "dst", dst.getName() ));
                            tx.success();  // Mark this write as successful.
                        }
             		}
             	}
                 System.out.println("neo4j>> Relatioships created");
    	     }
             
         }   
    }

}
