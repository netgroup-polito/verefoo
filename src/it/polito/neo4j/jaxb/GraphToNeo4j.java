package it.polito.neo4j.jaxb;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;

import java.io.File;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import it.polito.neo4j.jaxb.Graph;
import it.polito.neo4j.jaxb.Node;
import it.polito.neo4j.jaxb.Graphs;

public class GraphToNeo4j {	
	public static it.polito.neo4j.jaxb.Graph generateObject(it.polito.verigraph.model.Graph gr) {
		it.polito.neo4j.jaxb.Graph graph;
		graph=(new ObjectFactory()).createGraph();
		graph.setId(gr.getId());
		
		long i;
		List<it.polito.neo4j.jaxb.Node> nodes=new ArrayList<it.polito.neo4j.jaxb.Node>();
		//System.out.println("numero nodi:" + gr.getNodes().size());
		for(Map.Entry<Long, it.polito.verigraph.model.Node> c : gr.getNodes().entrySet()){
			it.polito.neo4j.jaxb.Node node=(new ObjectFactory()).createNode();
			node.setId(c.getValue().getId());
			node.setName(c.getValue().getName());			
			node.setFunctionalType(FunctionalTypes.fromValue(c.getValue().getFunctional_type().toUpperCase()));
			List<Neighbour> neighbours=new ArrayList<Neighbour>();
			for(Map.Entry<Long, it.polito.verigraph.model.Neighbour> a : c.getValue().getNeighbours().entrySet()){
				Neighbour neighbour=(new ObjectFactory()).createNeighbour();				
				neighbour.setId(a.getValue().getId());
				neighbour.setName(a.getValue().getName());
				neighbours.add(neighbour);		
		}
			node.getNeighbour().addAll(neighbours);
			nodes.add(node);
	}
		graph.getNode().addAll(nodes);
		return graph;
	}

public static Neighbour NeighbourToNeo4j(it.polito.verigraph.model.Neighbour n){
	Neighbour               neighbourRoot;
	neighbourRoot=(new ObjectFactory()).createNeighbour();
	neighbourRoot.setId(n.getId());
	neighbourRoot.setName(n.getName());
	return neighbourRoot;
	
}

public static Node NodeToNeo4j(it.polito.verigraph.model.Node n){
	Node              nodeRoot;
	nodeRoot=(new ObjectFactory()).createNode();
	nodeRoot.setId(n.getId());
	nodeRoot.setName(n.getName());
	nodeRoot.setFunctionalType(FunctionalTypes.fromValue(n.getFunctional_type().toUpperCase()));
	for(Map.Entry<Long, it.polito.verigraph.model.Neighbour> neighbour : n.getNeighbours().entrySet()){
		it.polito.verigraph.model.Neighbour neighb=neighbour.getValue();
		nodeRoot.getNeighbour().add(NeighbourToNeo4j(neighb));
	}
	return nodeRoot;
	
}
}

