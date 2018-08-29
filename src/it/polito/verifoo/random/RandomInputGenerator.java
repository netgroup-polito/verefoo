package it.polito.verifoo.random;

import java.util.Date;
import java.util.Random;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import it.polito.verifoo.rest.jaxb.*;
/**
 * Wraps the creation of a completely random input file for Verifoo
 * @author Antonio
 *
 */
public class RandomInputGenerator {
	Graphs graphs = new Graphs();
	Constraints constraints = null;
	PropertyDefinition properties = null;
	Hosts hosts = null;
	Connections connections = null;
	Random random = new Random(new Date().getTime());
	NFV randomInput = new NFV();
	/**
	 * Creates a totally random generated input for Verifoo
	 * @param maxClients the max number of clients in the service graph (min. 1)
	 * @param maxServers the max number of server in the service graph (min. 1)
	 * @param maxInternalNodes the max number of internal VNF
	 * @param maxProperty the max number of policies
	 * @param maxHosts the max number of hosts in the physical topology
	 */
    public RandomInputGenerator(int maxClients, int maxServers, int maxInternalNodes, int maxProperty, int maxHosts) {
    	RandomGraph serviceGraph = new RandomGraph(random, maxClients, maxServers, maxInternalNodes, maxProperty);
    	graphs.getGraph().add(serviceGraph.getGraph());
    	properties = serviceGraph.getProperties();
    	RandomConstraints randConstraints = new RandomConstraints(random, serviceGraph);
    	constraints = randConstraints.getConstraints();
    	RandomTopology topology = new RandomTopology(random, serviceGraph, maxHosts);
    	hosts = topology.getHosts();
    	connections = topology.getConnections();
    	
    	randomInput.setGraphs(graphs);
    	randomInput.setConstraints(constraints);
    	randomInput.setPropertyDefinition(properties);
    	randomInput.setHosts(hosts);
    	randomInput.setConnections(connections);
    	System.out.println("Nodes: " + graphs.getGraph().stream().flatMap(g -> g.getNode().stream()).count()
    						+" Hosts: " + hosts.getHost().size()
    						+" Connections: " + connections.getConnection().size());
    	/*JAXBContext jc;
         // create a JAXBContext capable of handling the generated classes
		try {
			 jc= JAXBContext.newInstance( "it.polito.verifoo.rest.jaxb" );
			 Marshaller m = jc.createMarshaller();
	         m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
	         m.setProperty( Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION,"./xsd/nfvSchema.xsd");
	         //m.marshal( properties, System.out );
	         //m.marshal( randomInput, System.out );
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
    }
    /**
     * 
     * @return the randomly generated input
     */
	public NFV getRandomInput() {
		return randomInput;
	}
 
}
