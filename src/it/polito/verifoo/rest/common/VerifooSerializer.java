package it.polito.verifoo.rest.common;

import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.microsoft.z3.Status;

import it.polito.verifoo.rest.jaxb.Connection;
import it.polito.verifoo.rest.jaxb.EType;
import it.polito.verifoo.rest.jaxb.Graph;
import it.polito.verifoo.rest.jaxb.Host;
import it.polito.verifoo.rest.jaxb.NFV;
import it.polito.verifoo.rest.jaxb.Path;
import it.polito.verifoo.rest.jaxb.Property;
import it.polito.verifoo.rest.neo4j.Neo4jClient;
import it.polito.verigraph.mcnet.components.IsolationResult;

/**
 * This class separates the Verifoo classes implementation from the actual input
 *  * @author Antonio
 *
 */
public class VerifooSerializer {
	private NFV nfv, result;
	private boolean sat = false;
	private Logger logger = LogManager.getLogger("mylog");
	public VerifooSerializer(NFV root){
		this.nfv = root;
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
		VerifooNormalizer norm = new VerifooNormalizer(root);
		root = norm.getRoot();
		try {
			JAXBContext jc = JAXBContext.newInstance( "it.polito.verifoo.rest.jaxb" );
			Marshaller m = jc.createMarshaller();
	        m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
	        m.setProperty( Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION,"./xsd/nfvSchema.xsd");
			/*System.out.println("-----------------NORMALIZED INPUT-----------------");
	        m.marshal( root, System.out ); 
			System.out.println("--------------------------------------------------");*/
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try{
			if(root.getHosts() != null && (root.getConnections() == null || root.getConnections().getConnection().size() == 0)){
				createFullMesh(root);
				JAXBContext jc= JAXBContext.newInstance( "it.polito.verifoo.rest.jaxb" );
				Marshaller m = jc.createMarshaller();
	            m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
	            m.setProperty( Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION,"./xsd/nfvSchema.xsd");
	            m.marshal( root.getConnections(), System.out ); 
			}
			List<Path> paths = null;
			if(root.getNetworkForwardingPaths() != null)
				paths = root.getNetworkForwardingPaths().getPath();
			for(Graph g:root.getGraphs().getGraph()){
	        	List<Property> prop = root.getPropertyDefinition().getProperty().stream().filter(p -> p.getGraph()==g.getId()).collect(Collectors.toList());
	        	if(prop.size() == 0)
					throw new BadGraphError("No property defined for the Graph "+g.getId(),EType.INVALID_PROPERTY_DEFINITION);
	        	VerifooProxy test = new VerifooProxy(g, root.getHosts(), root.getConnections(), root.getConstraints(), prop, paths);
	        	IsolationResult res=test.checkNFFGProperty();
	        	if(res.result != Status.UNSATISFIABLE){
	        		Translator t = new Translator(res.model.toString(),root, g);
	        		t.setNormalizer(norm);
	        		//result = t.convert();
	        		root = result;
	        		sat = true;
	        	}
	        	else{
	        		sat = false;
	        		result = root;
	        	}
	        	//root.getPropertyDefinition().getProperty().stream().filter(p->p.getGraph()==g.getId()).forEach(p -> p.setIsSat(res.result!=Status.UNSATISFIABLE));
	        }
	    } catch (BadGraphError e) {
			//logger.error("Graph semantically incorrect");
			//System.out.println("Graph semantically incorrect");
	    	logger.error(e);
	    	throw e;
	    }catch (JAXBException e) {
	    	logger.error(e);
		}
	}
	
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
	 * @return the nfv object
	 */
	public NFV getNfv() {
		return nfv;
	}


	/**
	 * @return the result
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
