package it.polito.verifoo.rest.main;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import it.polito.verifoo.rest.jaxb.*;

public class ScalabilityTestGenerator {

	/* Set parameters for the test */
	static int numberAllocationPlaces = 5;
	static int numberReachPolicies = 3;
	static int numberIsPolicies = 2;
	static int numberPolicies = numberReachPolicies + numberIsPolicies;
	static String path = new String("./testfile/autogentest.xml");
	static String IPClient = new String("10.0.0.");
	static String IPAllocationPlace = new String("20.0.0.");
	static String IPServer = new String("30.0.0.");
	
	static int countC = 1;
	static int countAP = 1;
	static int countS = 1;
	static int countP = 1;
	
	
	public static void main(String[] args) {
		
		
		/* Creation of the test */
		NFV nfv = new NFV();
		Graphs graphs = new Graphs();
		PropertyDefinition pd = new PropertyDefinition();
		Constraints cnst = new Constraints();
		NodeConstraints nc = new NodeConstraints();
		LinkConstraints lc = new LinkConstraints();
		cnst.setNodeConstraints(nc);
		cnst.setLinkConstraints(lc);
		nfv.setGraphs(graphs);
		nfv.setPropertyDefinition(pd);
		nfv.setConstraints(cnst);
		Graph graph = new Graph();
		graph.setId((long) 0);
		
		Node first = null;
		
		for(int i = 0; i < numberAllocationPlaces; i++) {
			Node ap = new Node();
			ap.setName(IPAllocationPlace + countAP);
			
			if(i != 0) {
				Neighbour prevNeigh = new Neighbour();
				prevNeigh.setName(IPAllocationPlace + (countAP -1));
				ap.getNeighbour().add(prevNeigh);
			}
			if(i != numberAllocationPlaces-1) {
				Neighbour nextNeigh = new Neighbour();
				nextNeigh.setName(IPAllocationPlace + (countAP +1));
				ap.getNeighbour().add(nextNeigh);
			}
			countAP++;
			
			if(i == 0) {
				first = ap;
			}
			else {
				if(i == numberAllocationPlaces - 1) {
					Neighbour servNeigh = new Neighbour();
					servNeigh.setName(IPServer + countS);
					ap.getNeighbour().add(servNeigh);
				}
				graph.getNode().add(ap);
			}
			
		}
		
		Node server = new Node();
		server.setFunctionalType(FunctionalTypes.WEBSERVER);
		server.setName(IPServer + countS);
		Neighbour prevServ = new Neighbour();
		prevServ.setName(IPAllocationPlace + numberAllocationPlaces);
		server.getNeighbour().add(prevServ);
		Configuration confS = new Configuration();
		confS.setName("confB");
		Webserver ws = new Webserver();
		ws.setName(server.getName());
		confS.setWebserver(ws);
		server.setConfiguration(confS);
		graph.getNode().add(server);
		
		for(int i = 0; i < numberReachPolicies; i++) {
			createPolicy(PName.REACHABILITY_PROPERTY, nfv, graph, first);
		}
		for(int i = 0; i < numberIsPolicies; i++) {
			createPolicy(PName.ISOLATION_PROPERTY, nfv, graph, first);
		}
		graph.getNode().add(first);
		nfv.getGraphs().getGraph().add(graph);
		try {
			JAXBContext jc;
            jc= JAXBContext.newInstance( "it.polito.verifoo.rest.jaxb" );
			Marshaller m = jc.createMarshaller();
            m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
            m.setProperty( Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION,"./xsd/nfvSchema.xsd");
			OutputStream os = new FileOutputStream(path);
			m.marshal(nfv, os);
		} catch(JAXBException je ) {
            System.exit(1);
        }catch(FileNotFoundException e) {
        	System.exit(2);
        }
		

	}

	private static void createPolicy(PName type, NFV nfv, Graph graph, Node first) {
		
		Node client = new Node();
		client.setFunctionalType(FunctionalTypes.WEBCLIENT);
		client.setName(IPClient + countC);
		countC++;
		Neighbour nextC = new Neighbour();
		nextC.setName(first.getName());
		client.getNeighbour().add(nextC);
		Configuration confC = new Configuration();
		confC.setName("confA");
		Webclient wc = new Webclient();
		wc.setNameWebServer(IPServer + countS);
		confC.setWebclient(wc);
		client.setConfiguration(confC);
		graph.getNode().add(client);
		
		Neighbour clientNeigh = new Neighbour();
		clientNeigh.setName(client.getName());
		first.getNeighbour().add(clientNeigh);
		
		Property property = new Property();
		property.setName(type);
		property.setGraph((long) 0);
		property.setSrc(client.getName());
		property.setDst(IPServer + "1");
		nfv.getPropertyDefinition().getProperty().add(property);
	}

}
