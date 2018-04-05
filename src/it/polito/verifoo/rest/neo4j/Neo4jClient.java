package it.polito.verifoo.rest.neo4j;

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

import it.polito.verifoo.rest.jaxb.Graph;
import it.polito.verifoo.rest.jaxb.NFV;
import it.polito.verifoo.rest.jaxb.Neighbour;
import it.polito.verifoo.rest.jaxb.NodeConstraints.NodeMetrics;

public class Neo4jClient implements AutoCloseable{

    private final Driver driver;
	public Neo4jClient( String uri, String user, String password)
    {
        driver = GraphDatabase.driver( uri, AuthTokens.basic( user, password ) );
    }
    
    @Override
    public void close() throws Exception
    {
        driver.close();
    }

    public void storeGraph(NFV root){
    	 try ( Session session = driver.session() ){
    		 System.out.println("Session created");
    		 //clear all
    		 session.run("MATCH (n) DETACH DELETE n");
    		 System.out.println("Cleared all");
    	     Graph graph = root.getGraphs().getGraph().get(0);
             List<Map<String, Object>> maps = new ArrayList<>();
    	     for(it.polito.verifoo.rest.jaxb.Node node : graph.getNode()){
    	    	 NodeMetrics nodeMetr = root.getConstraints().getNodeConstraints().getNodeMetrics().stream()
    	    			 								.filter(nm -> nm.getNode().equals(node.getName()))
    	    			 								.findFirst().orElse(null);
                 
                 Map<String, Object> map = new HashMap<>();
                 map.put("name",node.getName());
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
             System.out.println("Nodes created");
             for(it.polito.verifoo.rest.jaxb.Node src : graph.getNode()){
         		for(Neighbour dst : src.getNeighbour()){
         			try (Transaction tx = session.beginTransaction())
                    {
         				query = "MATCH (a:Node {name: $src}) " +
                                "MATCH (b:Node {name: $dst}) " +
                                "MERGE (a)-[:CONNECTED_WITH]->(b)";
                        tx.run(query, parameters( "src", src.getName(), "dst", dst.getName() ));
                        tx.success();  // Mark this write as successful.
                    }
         		}
         	}
             System.out.println("Relatioships created");
         }   
    }

}
