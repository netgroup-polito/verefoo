package neo4j;

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.BadRequestException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import it.polito.neo4j.exceptions.MyInvalidDirectionException;
import it.polito.neo4j.exceptions.MyNotFoundException;
import it.polito.neo4j.jaxb.FunctionalTypes;
import it.polito.neo4j.jaxb.Graph;
import it.polito.neo4j.jaxb.GraphToNeo4j;
import it.polito.neo4j.jaxb.Paths;
import it.polito.neo4j.manager.Neo4jDBManager;
import it.polito.neo4j.manager.Neo4jLibrary;
import it.polito.neo4j.service.Service;
import it.polito.neo4j.jaxb.Graphs;
import it.polito.neo4j.jaxb.ObjectFactory;
import it.polito.verigraph.service.GraphService;
import it.polito.verigraph.model.Neighbour;
import it.polito.verigraph.service.VerificationService;

public class test {

	public static void main(String[] args) throws MyNotFoundException, JAXBException, MyInvalidDirectionException {
		// TODO Auto-generated method stub
			
		Neo4jDBManager d=new Neo4jDBManager();
		System.out.println(new File("server.properties").getAbsolutePath());
		
		// Creazione primo grafo
		
		it.polito.verigraph.model.Graph g=new it.polito.verigraph.model.Graph();
		long i=2;
		g.setId(i);
		
		it.polito.verigraph.model.Node e=new it.polito.verigraph.model.Node();
		e.setName("host1");
		e.setId(i);
		e.setFunctional_type("Endpoint");
		Neighbour ne=new Neighbour();
		ne.setId(1);
		ne.setName("webserver");
		Map<Long, Neighbour> neighbo=new HashMap<Long, Neighbour>();
		neighbo.put((long) 1, ne);
		e.setNeighbours(neighbo);
		
		
		it.polito.verigraph.model.Node f=new it.polito.verigraph.model.Node();
		f.setName("webserver");
		f.setId(i+1);
		f.setFunctional_type("Endpoint");
		Neighbour nf=new Neighbour();
		nf.setName("pippo");
		nf.setId(2);
		Map<Long, Neighbour> neighbours=new HashMap<Long, Neighbour>();
		neighbours.put((long) 1, nf);
		f.setNeighbours(neighbours);
		
		Map<Long, it.polito.verigraph.model.Node> nodes=new HashMap<Long, it.polito.verigraph.model.Node>();
		nodes.put(i, e);
		nodes.put(i+1, f);		
		g.setNodes(nodes);
		
		List<Neighbour> nei=new ArrayList<Neighbour>();		
		d.addGraph(g);
		
		
		//ricerca Paths
		/*Paths path=new Paths();
		path=d.getPath(2, "host1", "webserver", "OUTGOING");
		System.out.println(path.getPath().toString());
		List<String> x=extractPath(path.getPath().toString());
		for(String c : x)
		System.out.println("path sanitized " + extractPath(path.getPath().toString()));*/
		
		/*aggiunta nodo*/
		/*
		it.polito.verigraph.model.Node h=new it.polito.verigraph.model.Node();
		h.setName("user1");
		h.setId(i+2);
		h.setFunctional_type("Endpoint");
		
		d.addNode(2, h);
		
		//System.out.println("elimina nodo 2: ");
		//d.deleteNode(0, 2);
		
		/*aggiunta neighbour*/
		/*
		Neighbour nh=new Neighbour();
		nh.setId(1);
		nh.setName("user1");
		d.addNeighbours((long)2, (long)3, nh);*/
		
	
		
		//Creazione secondo grafo
		/*
		it.polito.verigraph.model.Graph g=new it.polito.verigraph.model.Graph();
		long i=4;
		g.setId(i);
		
		it.polito.verigraph.model.Node e=new it.polito.verigraph.model.Node();
		e.setName("user1");
		e.setId(i);
		e.setFunctional_type("Endpoint");
		Neighbour ne=new Neighbour();
		ne.setId(1);
		ne.setName("server");
		Map<Long, Neighbour> neighbo=new HashMap<Long, Neighbour>();
		neighbo.put((long) 1, ne);
		e.setNeighbours(neighbo);
		
		it.polito.verigraph.model.Node f=new it.polito.verigraph.model.Node();
		f.setName("server");
		f.setId(i+1);
		f.setFunctional_type("Endpoint");
		Neighbour nf=new Neighbour();
		nf.setName("user1");
		nf.setId(2);
		Map<Long, Neighbour> neighbours=new HashMap<Long, Neighbour>();
		neighbours.put((long) 1, nf);
		f.setNeighbours(neighbours);
		
		Map<Long, it.polito.verigraph.model.Node> nodes=new HashMap<Long, it.polito.verigraph.model.Node>();
		nodes.put(i, e);
		nodes.put(i+1, f);		
		g.setNodes(nodes);
		
		d.addGraph(g);*/
		
		//aggiunta nodo
		/*it.polito.verigraph.model.Node e=new it.polito.verigraph.model.Node();
		e.setName("pqqqq");
		e.setId(46);
		e.setFunctional_type("Endpoint");
		Neighbour ne=new Neighbour();
		ne.setId(1);
		ne.setName("pqqqq");
		Map<Long, Neighbour> neighbo=new HashMap<Long, Neighbour>();
		neighbo.put((long) 1, ne);
		e.setNeighbours(neighbo);*/
		
	//	System.out.println(e.getNeighbours().get((long)1).getName());

		
		//d.addNode((long)4, e);
		//d.deleteNode((long)4, );
		//d.updateNode((long)4, e, (long)4);
		//d.updateGraph(g);
		//d.deleteGraph((long)2);
		//d.addNeighbours((long)4, (long)4,ne );
		Graph b=d.lib.getGraph((long)2);
		System.out.println(b.getId() );
		
	}
	public static List<String> extractPath(String path) {
		List<String> newPath = new ArrayList<String>();
		// find all nodes, i.e. all names between parentheses
		Matcher m = Pattern.compile("\\(([^)]+)\\)").matcher(path);
		while (m.find()) {
			String node = m.group(1);
			System.out.println("node:" + node);
			newPath.add(node);
			
		}
		return newPath;

	}

	
}
		
