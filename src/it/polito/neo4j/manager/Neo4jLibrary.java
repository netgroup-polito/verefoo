
package it.polito.neo4j.manager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.neo4j.graphalgo.GraphAlgoFactory;
import org.neo4j.graphalgo.PathFinder;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.NotFoundException;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.PathExpanders;
import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.traversal.Paths;

import it.polito.neo4j.jaxb.FunctionalTypes;
import it.polito.neo4j.jaxb.Graph;
import it.polito.neo4j.jaxb.Graphs;
import it.polito.neo4j.jaxb.Neighbour;
import it.polito.neo4j.jaxb.ObjectFactory;
import it.polito.neo4j.jaxb.Reachability;
import it.polito.neo4j.exceptions.DuplicateNodeException;
import it.polito.neo4j.exceptions.MyInvalidObjectException;
import it.polito.neo4j.exceptions.MyNotFoundException;


public class Neo4jLibrary implements Neo4jDBInteraction
{
	private static final int MAX_DEPTH = 50;


	private GraphDatabaseFactory dbFactory;
	private GraphDatabaseService graphDB;
	private ObjectFactory obFactory;
	private static Neo4jLibrary neo4jLib = new Neo4jLibrary();
		
	private Neo4jLibrary()
	{
		String neo4jDeploymentFolder =  System.getProperty("catalina.home") + "/webapps/verigraph";
		//String neo4jDeploymentFolder =  "C:/Users/Cristina/Documents";		
		Properties p = new Properties();
		FileReader r;
		try {
			r = new FileReader(new File("C:/Users/Cristina/git/verigraph/server.properties"));
			p.load(r);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	    String pathDB = (String) p.get("graphDBPath");
		dbFactory = new GraphDatabaseFactory();
		graphDB = dbFactory.newEmbeddedDatabase(new File(neo4jDeploymentFolder+"/"+pathDB));		
		registerShutdownHook(graphDB);
		obFactory = new ObjectFactory();
	}

//	singleton class
	public static Neo4jLibrary getNeo4jLibrary(){
		return neo4jLib;
	}

	private static void registerShutdownHook(final GraphDatabaseService graphDB)
	{
	    // Registers a shutdown hook for the Neo4j instance so that it shuts down
		// nicely when the VM exits (even if you "Ctrl-C" the running application).
	    Runtime.getRuntime().addShutdownHook(new Thread()
	    {
	        @Override
	        public void run()
	        {
	            graphDB.shutdown();
	        }
	    });
	}

	public void createGraphs(Graphs graphs) throws MyNotFoundException
	{
		for (Graph graph : graphs.getGraph())
		{
			createGraph(graph);
		}
	}

	public Graph createGraph(Graph graph) throws MyNotFoundException{
		Transaction tx = graphDB.beginTx();

		try
		{	
			Node nffgRoot = graphDB.createNode(NodeType.Nffg);
			//nffgRoot.setProperty("name", "graph");
			nffgRoot.setProperty("id", graph.getId());
			
			//System.out.println("graph modificato:" + nffgRoot.getProperty("id").toString());
			//System.out.println("nffg " + nffgRoot.getId());
			
			//graph.setId(nffgRoot.getId());	
			
			
			for(it.polito.neo4j.jaxb.Node nodo : graph.getNode()){
				Node newNode = createNode(nffgRoot, nodo, graph);
				//nodo.setId(newNode.getId());
				System.out.println("id_nodo_nuovo:" + newNode.getProperty("id"));
			}
			
			
			
			for(it.polito.neo4j.jaxb.Node nodo : graph.getNode()){
				Map<String, Neighbour> neighs = addNeighbours(nodo, graph);
				
				/*for(Neighbour neig : nodo.getNeighbour()){
					Neighbour n = neighs.get(neig.getName());					
					neig.setId(n.getId());
				}*/
			}
			tx.success();
		}
		finally
		{
			tx.close();
		}
		return graph;
	}

	private Node createNode(Node nffgRoot, it.polito.neo4j.jaxb.Node nodo, it.polito.neo4j.jaxb.Graph graph){
		Node newNode = graphDB.createNode(NodeType.Node);
		newNode.setProperty("name", nodo.getName());
		newNode.setProperty("functionalType", nodo.getFunctionalType().value());
		newNode.setProperty("id", nodo.getId());
		Relationship r=newNode.createRelationshipTo(nffgRoot, RelationType.OwnerRelationship);
		r.setProperty("id", graph.getId());
		System.out.println("IdRelOwner:" + r.getProperty("id"));
		System.out.println("IdNuovoNodo:" + newNode.getProperty("id"));
		
		return newNode;
	}

	private Map<String,Neighbour> addNeighbours(it.polito.neo4j.jaxb.Node nodo, Graph graph) throws MyNotFoundException{
		
		
		//Node srcNode = graphDB.findNode(NodeType.Node, "id", nodo.getId());				
		//Node srcNode = graphDB.getNodeById(nodo.getId());
		//Node srcNode=findNodeByNameAndId(nodo,graph);
		Node srcNode=findNodeByNameOfSpecificGraph(nodo.getName(), graph.getId());
				
		Map<String,Neighbour> neighbours = new HashMap<>();

		for(Neighbour neighbour : nodo.getNeighbour()){
			
			Node dstNode = findNodeByNameOfSpecificGraph(neighbour.getName(), graph.getId());
			
			if(dstNode == null)
				throw new MyNotFoundException("Destination node not found");
		
			Relationship rel = srcNode.createRelationshipTo(dstNode,RelationType.PathRelationship);
			
			//rel.setProperty("id", rel.getId());
			//neighbour.setId(rel.getId());
			
			System.out.println("neighbourId: " + neighbour.getId());
			rel.setProperty("id", neighbour.getId());
			neighbours.put((String)dstNode.getProperty("name"),neighbour);
		}
		return neighbours;
	}

	private Node findNodeByIdAndSpecificGraph(long nodoId, Graph graph) throws MyNotFoundException {
		// TODO Auto-generated method stub
		ResourceIterator<Node> nodes=graphDB.findNodes(NodeType.Node, "id", nodoId);
		while (nodes.hasNext()){
			Node n = nodes.next();			
				
					for(Relationship rel : n.getRelationships(RelationType.OwnerRelationship)){
						Node[] nodi = rel.getNodes();
						//System.out.println("nome relazione: " + nodi[0].getProperty("name"));
						//System.out.println("nome relazione: " + nodi[1].getProperty("name"));
						//System.out.println("nodi:" + nodi[0].getId() + "\ngraphID:" + graphId);
						if((long)nodi[0].getProperty("id") == graph.getId() || (long)nodi[1].getProperty("id") == graph.getId()){
							if((long)rel.getProperty("id") == graph.getId())
								return n;
						}
						
					}					
				
			}
		return null;
	}

	private Node findNodeByNameOfSpecificGraph(String nodeName, long graphId){
		
		ResourceIterator<Node> nodes = graphDB.findNodes(NodeType.Node, "name", nodeName);
		while (nodes.hasNext()){
			Node n = nodes.next();
			//System.out.println("nome nodo" + n.getProperty("name"));
			for(Relationship rel : n.getRelationships(RelationType.OwnerRelationship)){
				Node[] nodi = rel.getNodes();
				if((long)nodi[0].getProperty("id") == graphId || (long)nodi[1].getProperty("id") == graphId){
					System.out.println("rel:" + rel.getId());
					
					if((long)rel.getProperty("id") == graphId)
						return n;
				}
			}
		}
		return null;
	}

	public it.polito.neo4j.jaxb.Node createNode(it.polito.neo4j.jaxb.Node node, long graphId) throws MyNotFoundException, DuplicateNodeException{
		Node graph;
		Transaction tx = graphDB.beginTx();
		System.out.println("nodo da creare: " + node.getName() + " id nodo: " + node.getId());
		
		try{
			graph = findGraph(graphId);
			System.out.println("grafo trovato id: " + (long)graph.getProperty("id"));
			Graph myGraph = getGraph(graphId);
			if(DuplicateNode(node,myGraph))
				throw new DuplicateNodeException("This node is already present");
			Node newNode = createNode(graph,node,myGraph);
			//node.setId(newNode.getId());
			//System.out.println("newnode:" + newNode.getId());
			Map<String, Neighbour> neighs = addNeighbours(node, myGraph);
			
			
			tx.success();
		}
		finally{
			tx.close();
		}
		return node;
	}

	private boolean DuplicateNode(it.polito.neo4j.jaxb.Node node, Graph myGraph) {
		for(it.polito.neo4j.jaxb.Node n : myGraph.getNode()){
			if(n.getName().compareTo(node.getName()) == 0)
				return true;
		}
		return false;
	}

	public Graphs getGraphs(){
		Graphs graphs = obFactory.createGraphs();
		Transaction tx = graphDB.beginTx();

		try{
			ResourceIterator<Node> graphNodes = graphDB.findNodes(NodeType.Nffg);
			while(graphNodes.hasNext()){
				Node currentGraph = graphNodes.next();
				Graph g = getGraph(currentGraph.getId());
				graphs.getGraph().add(g);
			}
			tx.success();
		}
		catch(MyNotFoundException e){
//			NEVER BEEN HERE
		}
		finally{
			tx.close();
		}
		return graphs;
	}

	public Graph getGraph(long id) throws MyNotFoundException{
		Graph graph = obFactory.createGraph();
		Transaction tx = graphDB.beginTx();

		try
		{
			findGraph(id); 		//this method rises an exception if not found graph with Id id
			graph.setId(id);
			Set<Node> nodes = retrieveNodesOfSpecificGraph(id);
			for(Node nodo : nodes){
				graph.getNode().add(retrieveNode(nodo));
			}
			tx.success();
		}
		catch(MyNotFoundException e){
			tx.close();
			throw new MyNotFoundException(e.getMessage());
		}
		finally{
			tx.close();
		}
		return graph;
	}

	private Set<Node> retrieveNodesOfSpecificGraph(long graphId){
		Set<Node> nodi = new HashSet<>();
		Node nodo;
		ResourceIterator<Node> nodes = graphDB.findNodes(NodeType.Node);
		while (nodes.hasNext())
		{
			nodo = nodes.next();
			Relationship rel = nodo.getSingleRelationship(RelationType.OwnerRelationship, Direction.BOTH);
			if(rel != null){
				Node[] nodiRelation = rel.getNodes();
				if((long)nodiRelation[0].getProperty("id")==graphId || (long)nodiRelation[1].getProperty("id")==graphId)
					nodi.add(nodo);
			}
		}
		return nodi;
	}

	private Node findGraph(long graphId) throws MyNotFoundException{
		Node graph;

		try{
			graph=graphDB.findNode(NodeType.Nffg, "id", graphId);
			if(graph==null)
				throw new MyNotFoundException("There is no Graph whose Id is '" + graphId + "'");
		}
		catch(NotFoundException e){
			throw new MyNotFoundException("There is no Graph whose Id is '" + graphId + "'");
		}

		return graph;
	}

	private Node findNode(long nodoId, long graphId) throws MyNotFoundException{
		Node n;

		try{
			ResourceIterator<Node> nodes=graphDB.findNodes(NodeType.Node, "id", nodoId);
			while (nodes.hasNext()){
				n = nodes.next();			
					
						for(Relationship rel : n.getRelationships(RelationType.OwnerRelationship)){
							Node[] nodi = rel.getNodes();						
							if((long)nodi[0].getProperty("id") == graphId || (long)nodi[1].getProperty("id") == graphId){
								if((long)rel.getProperty("id") == graphId)
									return n;
							}
							
						}		
					
				}
			return null;
		}
		catch(NotFoundException e){
			throw new MyNotFoundException("There is no Node whose Id is '" + nodoId + "'");
		}		
		
	}

	private void addNeighbour(it.polito.neo4j.jaxb.Node src, Node dst, long neighbourId){
		Neighbour vicino = obFactory.createNeighbour();
		vicino.setId(neighbourId);
		vicino.setName((String) dst.getProperty("name"));
		src.getNeighbour().add(vicino);
	}

	private it.polito.neo4j.jaxb.Node retrieveNode(Node nodo){
		it.polito.neo4j.jaxb.Node n = obFactory.createNode();
		n.setId((long)nodo.getProperty("id"));
		n.setName((String) nodo.getProperty("name"));
		n.setFunctionalType(it.polito.neo4j.jaxb.FunctionalTypes.valueOf((String) nodo.getProperty("functionalType")));

		Iterable<Relationship> links = nodo.getRelationships(Direction.OUTGOING, RelationType.PathRelationship);
		Iterator<Relationship> linksIt = links.iterator();
		while(linksIt.hasNext()){
			Relationship link = linksIt.next();
			Node endpoint = link.getEndNode();
			addNeighbour(n, endpoint, (long)link.getProperty("id"));
		}
		return n;
	}

	public void deleteGraph(long id) throws MyNotFoundException{
		Node graph;
		Transaction tx = graphDB.beginTx();

		try{
			graph = findGraph(id);
			for(Relationship rel: graph.getRelationships(RelationType.OwnerRelationship)){
				Node[] nodes = rel.getNodes();
				if(nodes[0].hasLabel(NodeType.Node))
					deleteNode(nodes[0]);
				else
					deleteNode(nodes[1]);
			}
			deleteNode(graph);
			tx.success();
		}
		finally{
			tx.close();
		}
	}

	private void deleteNode(Node n)
	{
		n.getAllProperties().clear();

		for (Relationship r : n.getRelationships())
		{
			r.getAllProperties().clear();
			r.delete();
		}

		n.delete();
	}

	public void deleteNode(long graphId, long nodeId) throws MyNotFoundException{
		Node nodo;
		Transaction tx = graphDB.beginTx();

		try{
			findGraph(graphId);
			nodo = findNode(nodeId, graphId);
			if(nodo==null){
				tx.failure();
				throw new MyNotFoundException("There is no Node whose id is '" + nodeId+"'");
			}
			
				
			deleteNode(nodo);
			tx.success();
		}
		finally{
			tx.close();
		}
	}

	public it.polito.neo4j.jaxb.Node getNode(long graphId, long nodeId) throws MyNotFoundException{
		Node nodo;
		Transaction tx = graphDB.beginTx();

		try{
			findGraph(graphId);
			nodo = findNode(nodeId, graphId);
			tx.success();
			return retrieveNode(nodo);
		}
		catch(MyNotFoundException e){
			tx.close();
			throw new MyNotFoundException(e.getMessage());
		}
		finally{
			tx.close();
		}
	}

	public Set<it.polito.neo4j.jaxb.Node> getNodes(long graphId) throws MyNotFoundException{
		Node graph;
		Transaction tx = graphDB.beginTx();
		Set<it.polito.neo4j.jaxb.Node> set = new HashSet<>();

		try{
			graph = findGraph(graphId);
			for(Relationship rel : graph.getRelationships(RelationType.OwnerRelationship)){
				Node[] nodes = rel.getNodes();
				it.polito.neo4j.jaxb.Node nodeAdded=null;
				for(Label l: nodes[0].getLabels()){
					if(l.name().compareTo(NodeType.Nffg.name())==0){
						nodeAdded = retrieveNode(nodes[1]);
					}else{
						nodeAdded = retrieveNode(nodes[0]);
					}
				}
				set.add(nodeAdded);
			}
			tx.success();
		}
		finally{
			tx.close();
		}
		return set;
	}

	public Neighbour createNeighbour(Neighbour neighbour, long graphId, long nodeId) throws MyNotFoundException{
		Node nodosrc,nododst;
		Transaction tx = graphDB.beginTx();

		try{
			findGraph(graphId);
			nodosrc = findNode(nodeId, graphId);
			nododst = findNodeByNameOfSpecificGraph(neighbour.getName(), graphId);
			if(nododst == null){
				tx.failure();
				throw new MyNotFoundException("There is no Node whose name is '" + neighbour.getName() + "'");
			}
			Relationship rel = nodosrc.createRelationshipTo(nododst,RelationType.PathRelationship);			
			rel.setProperty("id", neighbour.getId());
			tx.success();
		}
		finally{
			tx.close();
		}
		return neighbour;
	}

	public Set<Neighbour> getNeighbours(long graphId, long nodeId) throws MyNotFoundException{
		Node nodo;
		Transaction tx = graphDB.beginTx();
		Set<Neighbour> set = new HashSet<>();

		try{
			findGraph(graphId);
			nodo = findNode(nodeId, graphId);
			for(Relationship rel : nodo.getRelationships(Direction.OUTGOING,RelationType.PathRelationship)){
				Neighbour newNeigh = obFactory.createNeighbour();
				Node endpoint = rel.getEndNode();
				newNeigh.setName((String)endpoint.getProperty("name"));
				newNeigh.setId((long)rel.getProperty("id"));
				set.add(newNeigh);
			}
			tx.success();
			return set;
		}
		finally{
			tx.close();
		}
	}

	public Neighbour getNeighbour(long graphId, long nodeId, long neighbourId) throws MyNotFoundException{
		Node nodo;
		Transaction tx = graphDB.beginTx();
		Neighbour newNeigh = obFactory.createNeighbour();

		try{
			findGraph(graphId);
			nodo = findNode(nodeId, graphId);
			for(Relationship rel : nodo.getRelationships(Direction.OUTGOING, RelationType.PathRelationship)){
				if((long)rel.getProperty("id") == neighbourId){
					Node endpoint = rel.getEndNode();
					newNeigh.setName((String)endpoint.getProperty("name"));
					newNeigh.setId((long)rel.getProperty("id"));
					tx.success();
					tx.close();
					return newNeigh;
				}
			}
			//if we arrive at this point it means there is not neighbour ad id neghbourId
			tx.failure();
			throw new MyNotFoundException("There is no Neighbour whose Id is '" + neighbourId + "'");
		}
		finally{
			tx.close();
		}
	}

	public void deleteNeighbour(long graphId, long nodeId, long neighbourId) throws MyNotFoundException{
		Node nodo;
		Transaction tx = graphDB.beginTx();
		boolean trovato=false;

		try{
			findGraph(graphId);
			nodo = findNode(nodeId, graphId);
			for(Relationship rel : nodo.getRelationships(Direction.OUTGOING, RelationType.PathRelationship)){
				if((long)rel.getProperty("id") == neighbourId){
					rel.delete();
					trovato=true;
					break;
				}
			}
			if(!trovato){
				tx.close();
				throw new MyNotFoundException("There is no Neighbour whose Id is '" + neighbourId + "'");
			}
			tx.success();
		}
		finally{
			tx.close();
		}
	}

	public it.polito.neo4j.jaxb.Node updateNode(it.polito.neo4j.jaxb.Node node, long graphId, long nodeId) throws MyInvalidObjectException,MyNotFoundException{
		Node nodo;
		Transaction tx = graphDB.beginTx();
		it.polito.neo4j.jaxb.Node returnedNode;

		try{
			findGraph(graphId);
			nodo = findNode(nodeId, graphId);
			if(validNode(node,getGraph(graphId)) == false)
				throw new MyInvalidObjectException("Invalid node");
			nodo.setProperty("name", node.getName());
			nodo.setProperty("functionalType", node.getFunctionalType().value());
			for(Neighbour neigh : node.getNeighbour()){
				Neighbour n = updateNeighbour(nodo, neigh, graphId);
				//neigh.setId(n.getId());
			}
			returnedNode = retrieveNode(nodo);
			tx.success();
		}
		finally{
			tx.close();
		}
		return returnedNode;
	}

	private boolean validNode(it.polito.neo4j.jaxb.Node node, Graph graph) {
		int cntNeighbour = 0;
		for(it.polito.neo4j.jaxb.Node n : graph.getNode()){
			if(node.getId().longValue() != n.getId().longValue() && node.getName().compareTo(n.getName()) == 0)
				return false;
			if(node.getId() != n.getId()){
				for(Neighbour neigh : node.getNeighbour()){
					if(neigh.getName().compareTo(n.getName()) == 0)
						cntNeighbour++;
				}
			}
			else{
				for(Neighbour neigh : node.getNeighbour()){
					if(neigh.getName().compareTo(node.getName()) == 0)
						return false;
				}
			}
		}
		if(cntNeighbour == node.getNeighbour().size())
			return true;
		else
			return false;
	}

	private Neighbour updateNeighbour(Node nodo, Neighbour neigh, long graphId){
		Node dst = findNodeByNameOfSpecificGraph(neigh.getName(), graphId);

		try{
			boolean trovato = false;
			Iterable<Relationship> rels = nodo.getRelationships(Direction.OUTGOING, RelationType.PathRelationship);
			Iterator<Relationship> relIt = rels.iterator();
			if(neigh.getId() != null){
				while(relIt.hasNext()){
					Relationship r = relIt.next();
					if((long)r.getProperty("id") == neigh.getId()){
						trovato = true;
						break;
					}
				}
			}
			if(!trovato)
				throw new NotFoundException();
//			graphDB.getRelationshipById(neigh.getId());
			try {
				changeNeighbour(nodo, neigh, neigh.getId(), graphId);
			} catch (MyNotFoundException e) {
				e.printStackTrace();
			}
			return neigh;
		}
		catch(NotFoundException e){
			Relationship  rel = nodo.createRelationshipTo(dst, RelationType.PathRelationship);
			//rel.setProperty("id", rel.getId());
			//neigh.setId(rel.getId());			
			rel.setProperty("id", neigh.getId());
		}


		return neigh;
	}

	public Graph updateGraph(Graph graph, long graphId) throws MyNotFoundException,DuplicateNodeException,MyInvalidObjectException{
		Transaction tx = graphDB.beginTx();
		it.polito.neo4j.jaxb.Node newNode;
		Node nodo;

		try{
			findGraph(graphId);
			if(ValidGraph(graph) == false)
				throw new MyInvalidObjectException("Invalid graph");
			for(it.polito.neo4j.jaxb.Node n : graph.getNode()){
				try {
					try{
						getNode(graphId, n.getId()); //if there is not exception it means that this node exists
						newNode = updateNode(n, graphId, n.getId());
					}
					catch (MyNotFoundException e){
						newNode = createNode(n, graphId);
					}
				}
				finally{}
				nodo = findNode(newNode.getId(), graphId);
				n = retrieveNode(nodo);
			}
			tx.success();
		}
		finally{
			tx.close();
		}
		return graph;
	}

	private boolean ValidGraph(Graph graph) {
		if (duplicateNodesInsideGraph(graph))
			return false;
		for(it.polito.neo4j.jaxb.Node n :graph.getNode()){
			int cnt = 0;
			for(Neighbour neig : n.getNeighbour()){
				if(neig.getName().compareTo(n.getName()) == 0)
					return false;
				for(it.polito.neo4j.jaxb.Node n2 : graph.getNode()){
					if(neig.getName().compareTo(n2.getName()) == 0){
						cnt++;
						break;
					}
				}
			}
			if(cnt != n.getNeighbour().size())
				return false;
		}
		return true;
	}

	private boolean duplicateNodesInsideGraph(Graph graph) {
		for(it.polito.neo4j.jaxb.Node n :graph.getNode()){
			for(it.polito.neo4j.jaxb.Node n2 : graph.getNode()){
				if(n!=n2  && n.getName().compareTo(n2.getName()) == 0)
					return true;
			}
		}
		return false;
	}

	//public void updateNeighbour(Neighbour neighbour, long graphId, long nodeId, long neighbourId) throws MyInvalidObjectException,MyNotFoundException{
	public it.polito.neo4j.jaxb.Node updateNeighbour(Neighbour neighbour, long graphId, long nodeId, long neighbourId) throws MyInvalidObjectException,MyNotFoundException{
		Node nodo;
		Transaction tx = graphDB.beginTx();
		it.polito.neo4j.jaxb.Node returnedNode;

		try{
			findGraph(graphId);
			nodo = findNode(nodeId, graphId);
			if(validNeighbour(neighbour,getGraph(graphId),getNode(graphId, nodeId)) ==  false)
				throw new MyInvalidObjectException("Invalid neighbour");
			changeNeighbour(nodo,neighbour,neighbourId,graphId);
			returnedNode = retrieveNode(nodo);
			tx.success();
		}
		finally{
			tx.close();
		}
		//return;
		return returnedNode;
	}

	private boolean validNeighbour(Neighbour neighbour, Graph graph, it.polito.neo4j.jaxb.Node node) {
		if(neighbour.getName().compareTo(node.getName()) == 0)
			return false;
		for(it.polito.neo4j.jaxb.Node n : graph.getNode()){
			if(neighbour.getName().compareTo(n.getName()) ==  0)
				return true;
		}
		return false;
	}

	private void changeNeighbour(Node nodo, Neighbour neigh, long neighbourId, long graphId) throws MyNotFoundException{
		boolean trovato = false;
		Relationship rel = null;
		Iterable<Relationship> rels = nodo.getRelationships(Direction.OUTGOING, RelationType.PathRelationship);
		Iterator<Relationship> relIt = rels.iterator();
		while(relIt.hasNext()){
			Relationship r = relIt.next();
			if((long)r.getProperty("id") == neighbourId){
				trovato=true;
				rel=r;
				break;
			}
		}
		if(!trovato)
			throw new MyNotFoundException("There is no Relationship with id '" + neigh.getId() + "'");
		rel.delete();
		Node dst = findNodeByNameOfSpecificGraph(neigh.getName(), graphId);
		if(dst == null)
			throw new MyNotFoundException("There is no Node whose name is '" + neigh.getName() + "'");
		Relationship relationship = nodo.createRelationshipTo(dst, RelationType.PathRelationship);
		//la relazione deve avere sempre id uguale all'id del neighbour dst
		//relationship.setProperty("id", neighbourId);
		relationship.setProperty("id", neigh.getId());
		
	}

	public it.polito.neo4j.jaxb.Paths findAllPathsBetweenTwoNodes(long graphId, String srcName, String dstName, String direction) throws MyNotFoundException{
		Transaction tx = graphDB.beginTx();
		Set<String> pathPrinted = new HashSet<>();

		try{
			findGraph(graphId);		
			Node src = findNodeByNameOfSpecificGraph(srcName, graphId);
			Node dst = findNodeByNameOfSpecificGraph(dstName, graphId);
			if(src == null || dst == null){
				tx.failure();
				throw new MyNotFoundException("Source node and/or destination node are not exist");
			}
			PathFinder<Path> finder = GraphAlgoFactory.allSimplePaths(PathExpanders.forTypeAndDirection(RelationType.PathRelationship, Direction.valueOf(direction.toUpperCase())), MAX_DEPTH);
			//PathFinder<Path> finder = GraphAlgoFactory.allPaths(PathExpanders.forTypeAndDirection(RelationType.PathRelationship, Direction.valueOf(direction.toUpperCase())), MAX_DEPTH);

		    for (Path p : finder.findAllPaths(src, dst))
		    {
		    	//System.out.println("path iterator" + Paths.simplePathToString(p, "name"));
		    	pathPrinted.add(Paths.simplePathToString(p, "name"));
		    }
		}
		finally{
			tx.close();
		}

		it.polito.neo4j.jaxb.Paths paths = obFactory.createPaths();
		paths.setSource(srcName);
		paths.setDestination(dstName);
		paths.setDirection(direction);
		if(pathPrinted.isEmpty())
			paths.setMessage("No available paths");
		else
			paths.getPath().addAll(pathPrinted);
		return paths;
	}

	public Reachability checkReachability(long graphId, String srcName, String dstName, String direction) throws MyNotFoundException{
		Transaction tx = graphDB.beginTx();
		boolean trovato = false;

		try{
			findGraph(graphId);
			Node src = findNodeByNameOfSpecificGraph(srcName, graphId);
			Node dst = findNodeByNameOfSpecificGraph(dstName, graphId);
			if(src == null || dst == null){
				tx.failure();
				throw new MyNotFoundException("Source node and/or destination node are not exist");
			}
			PathFinder<Path> finder = GraphAlgoFactory.allSimplePaths(PathExpanders.forTypeAndDirection(RelationType.PathRelationship, Direction.valueOf(direction.toUpperCase())), MAX_DEPTH);

		    for (Path p : finder.findAllPaths(src, dst))
		    {
		    	trovato = true;
		    	break;
		    }
		}
		finally{
			tx.close();
		}

		Reachability reach = obFactory.createReachability();
		reach.setSource(srcName);
		reach.setDestination(dstName);
		reach.setDirection(direction);
		reach.setResult(trovato);
		return reach;
	}
}