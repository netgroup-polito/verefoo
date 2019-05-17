package it.polito.verefoo;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.microsoft.z3.Status;

import it.polito.verefoo.allocation.AllocationGraphGenerator;
import it.polito.verefoo.extra.BadGraphError;
import it.polito.verefoo.jaxb.Connection;
import it.polito.verefoo.jaxb.EType;
import it.polito.verefoo.jaxb.Graph;
import it.polito.verefoo.jaxb.Host;
import it.polito.verefoo.jaxb.NFV;
import it.polito.verefoo.jaxb.Node;
import it.polito.verefoo.jaxb.Path;
import it.polito.verefoo.jaxb.Property;
import it.polito.verefoo.tools.neo4j.Neo4jClient;
import it.polito.verefoo.translator.Translator;
import it.polito.verigraph.extra.VerificationResult;

/**
 * This class separates the Verifoo classes implementation from the actual input
 */
public class VerefooSerializer {
	private NFV nfv, result;
	private boolean sat = false;
	private Logger logger = LogManager.getLogger("model");
	private Logger loggerResult = LogManager.getLogger("result");
	private List<Node> removedNodes;
	/**
	 * Wraps all the Verifoo tasks, executing the z3 procedure for each graph in the NFV element
	 * @param root the NFV element received as input
	 */
	public VerefooSerializer(NFV root){
		this.nfv = root;
		removedNodes = new ArrayList<Node>();
		/*String neo4jURL, neo4jUsername, neo4jPassword;
		if(System.getProperty("it.polito.verifoo.rest.neo4j.neo4jURL") != null){
			neo4jURL = "bolt://"+System.getProperty("it.polito.verifoo.rest.neo4j.neo4jURL");
		}else{
			neo4jURL = "bolt://127.0.0.1:7687";
		}
		if(System.getProperty("it.polito.verifoo.rest.neo4j.neo4jUsername") != null){
			neo4jUsername = System.getProperty("it.polito.verifoo.rest.neo4j.neo4jUsername");
		}else{
			neo4jUsername = "neo4j";
		}
		if(System.getProperty("it.polito.verifoo.rest.neo4j.neo4jPassword") != null){
			neo4jPassword = System.getProperty("it.polito.verifoo.rest.neo4j.neo4jPassword");
			
		}else{
			neo4jPassword =  "password";
		}
		try{
			Neo4jClient client = new Neo4jClient(neo4jURL, neo4jUsername, neo4jPassword);
	        client.storeGraph(root);
	        client.close();
		}catch(Exception e){
			logger.debug("Neo4j deployment FAILED: " + e.getMessage());
			System.out.println("Neo4j deployment FAILED: " + e.getMessage());
		}*/
		AllocationGraphGenerator agg = new AllocationGraphGenerator(root);
		root = agg.getAllocationGraph();
		VerefooNormalizer norm = new VerefooNormalizer(root);
		root = norm.getRoot();
		

		try {
			JAXBContext jc = JAXBContext.newInstance( "it.polito.verefoo.jaxb" );
			Marshaller m = jc.createMarshaller();
	        m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
	        m.setProperty( Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION,"./xsd/nfvSchema.xsd");
			//logger.info("-----------------NORMALIZED INPUT-----------------");
			StringWriter stringWriter = new StringWriter();
			m.marshal( root, stringWriter); 
	        //logger.info(stringWriter.toString());
	        //logger.info("--------------------------------------------------");
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		try{
			if(root.getHosts() != null && (root.getConnections() == null || root.getConnections().getConnection().size() == 0)){
				logger.info("No connections found! Building full mesh topology...");
				createFullMesh(root);
				logger.info("Full mesh topology built");
				/*JAXBContext jc= JAXBContext.newInstance( "it.polito.verefoo.jaxb" );
				Marshaller m = jc.createMarshaller();
	            m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
	            m.setProperty( Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION,"./xsd/nfvSchema.xsd");
	            m.marshal( root.getConnections(), System.out ); */
			}
			List<Path> paths = null;
			if(root.getNetworkForwardingPaths() != null)
				paths = root.getNetworkForwardingPaths().getPath();
			for(Graph g:root.getGraphs().getGraph()){
	        	List<Property> prop = root.getPropertyDefinition().getProperty().stream().filter(p -> p.getGraph()==g.getId()).collect(Collectors.toList());
	        	if(prop.size() == 0)
					throw new BadGraphError("No property defined for the Graph "+g.getId(),EType.INVALID_PROPERTY_DEFINITION);
	        	VerefooProxy test = new VerefooProxy(g, root.getHosts(), root.getConnections(), root.getConstraints(), prop, paths);
	        	long beginAll=System.currentTimeMillis();
	        	VerificationResult res=test.checkNFFGProperty();
	        	long endAll=System.currentTimeMillis();
	        	loggerResult.debug("Only checker: " +(endAll-beginAll)+"ms");
	        	if(res.result != Status.UNSATISFIABLE&&res.result != Status.UNKNOWN){
	        		//System.out.println(res.model.toString());
	        		
	        		
	        		Translator t = new Translator(res.model.toString(),root, g, test.getAllocationNodes());
	        		t.setNormalizer(norm);
	        		result = t.convert();
	        		
	        		
	        		root = result;
	        		
	        		
	        		
	        		sat = true;
	        	}
	        	else{
	        		sat = false;
	        		result = root;
	        	}
	        	root.getPropertyDefinition().getProperty().stream().filter(p->p.getGraph()==g.getId()).forEach(p -> p.setIsSat(res.result!=Status.UNSATISFIABLE));
	        }
	    } catch (BadGraphError e) {
	    	logger.error("Graph semantically incorrect");
	    	logger.error(e);
	    	throw e;
	    }/*catch (JAXBException e) {
	    	logger.error(e);
		}*/
	}
	
	/**
	 * If no connections are declared in XML, a full mesh is created between the hosts
	 * @param root
	 */
	private void createFullMesh(NFV root){
		List<Host> hosts = root.getHosts().getHost();
		List<Connection> connections = root.getConnections().getConnection();
		for(Host h1 : hosts){
			for(Host h2 : hosts){
				if(h1.getName().equals(h2.getName())) continue;
				Connection c = new Connection();
				c.setSourceHost(h1.getName());
				c.setDestHost(h2.getName());
				c.setAvgLatency(1);
				connections.add(c);
			}
		}
	}
	
	/**
	 * @return the original NFV object given in the constructor
	 */
	public NFV getNfv() {
		return nfv;
	}

	/**
	 * @return the NFV object after the computation 
	 */
	public NFV getResult() {
		return result;
	}

	/**
	 * @return if the z3 model is sat
	 */
	public boolean isSat() {
		return sat;
	}

}
