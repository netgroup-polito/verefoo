/*******************************************************************************
 * Copyright (c) 2017 Politecnico di Torino and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package it.polito.neo4j.manager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.traversal.Paths;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.polito.neo4j.jaxb.Antispam;
import it.polito.neo4j.jaxb.Cache;
import it.polito.neo4j.jaxb.Configuration;
import it.polito.neo4j.jaxb.Dpi;
import it.polito.neo4j.jaxb.Elements;
import it.polito.neo4j.jaxb.Endhost;
import it.polito.neo4j.jaxb.Endpoint;
import it.polito.neo4j.jaxb.Fieldmodifier;
import it.polito.neo4j.jaxb.Firewall;
import it.polito.neo4j.jaxb.FunctionalTypes;
import it.polito.neo4j.jaxb.Graph;
import it.polito.neo4j.jaxb.Graphs;
import it.polito.neo4j.jaxb.Mailclient;
import it.polito.neo4j.jaxb.Mailserver;
import it.polito.neo4j.jaxb.Nat;
import it.polito.neo4j.jaxb.Neighbour;
import it.polito.neo4j.jaxb.ObjectFactory;
import it.polito.neo4j.jaxb.ProtocolTypes;
import it.polito.neo4j.jaxb.Vpnaccess;
import it.polito.neo4j.jaxb.Vpnexit;
import it.polito.neo4j.jaxb.Webclient;
import it.polito.neo4j.jaxb.Webserver;
import it.polito.neo4j.manager.Neo4jDBInteraction.NodeType;
import it.polito.verigraph.exception.DataNotFoundException;
import it.polito.verigraph.service.VerigraphLogger;
import it.polito.neo4j.exceptions.DuplicateNodeException;
import it.polito.neo4j.exceptions.MyInvalidIdException;
import it.polito.neo4j.exceptions.MyInvalidObjectException;
import it.polito.neo4j.exceptions.MyNotFoundException;


public class Neo4jLibrary implements Neo4jDBInteraction
{
    private static final int MAX_DEPTH = 50;


    private static final RelationshipType ElementRelationship = null;



    private GraphDatabaseFactory dbFactory;
    private GraphDatabaseService graphDB;
    private ObjectFactory obFactory;
    private static Neo4jLibrary neo4jLib = new Neo4jLibrary();
    public static VerigraphLogger vlogger = VerigraphLogger.getVerigraphlogger();

    private Neo4jLibrary()
    {
        try {
            FileReader r;
            Properties p = new Properties();
            String neo4j_path = System.getProperty("catalina.home");

            if(neo4j_path != null){
                r = new FileReader(new File(neo4j_path+File.separator+"/webapps/verigraph/server.properties"));

            }else{
                neo4j_path=System.getProperty("user.dir");
                r= new FileReader(new File(neo4j_path+File.separator+"/server.properties"));
            }

            p.load(r);
            String pathDB = (String) p.get("graphDBPath");
            dbFactory = new GraphDatabaseFactory();
            graphDB = dbFactory.newEmbeddedDatabase(new File(neo4j_path + "/neo4j"+File.separator+pathDB));
            VerigraphLogger.logger.info("Database location is "+ neo4j_path + "/neo4j"+File.separator+pathDB);
            registerShutdownHook(graphDB);
            obFactory = new ObjectFactory();

        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    //singleton class
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

    public void createGraphs(Graphs graphs) throws MyNotFoundException, MyInvalidIdException
    {
        for (Graph graph : graphs.getGraph())
        {
            createGraph(graph);
        }
    }

    public Graph createGraph(Graph graph) throws MyNotFoundException, MyInvalidIdException{
        Transaction tx = graphDB.beginTx();

        try
        {
            Node nffgRoot = graphDB.createNode(NodeType.Nffg);
            nffgRoot.setProperty("id", nffgRoot.getId());
            graph.setId(nffgRoot.getId());

            for(it.polito.neo4j.jaxb.Node nodo : graph.getNode()){
                Node newNode = createNode(nffgRoot, nodo, graph);
                nodo.setId(newNode.getId());
                it.polito.neo4j.jaxb.Configuration c=nodo.getConfiguration();
                Node newConf=createConfiguration(newNode, c);
                c.setId(newConf.getId());

            }

            //modify  the neighbours id inserting the relationship

            for(it.polito.neo4j.jaxb.Node nodo : graph.getNode()){
                Map<String, Neighbour> neighs = addNeighbours(nodo, graph);

                for(Neighbour neig : nodo.getNeighbour()){
                    Neighbour n = neighs.get(neig.getName());
                    neig.setId(n.getId());

                }

            }

            tx.success();

        }
        finally
        {
            tx.close();

        }
        return graph;
    }

    private Node createConfiguration(Node newNode, Configuration c) throws MyInvalidIdException {

        Node newConf=graphDB.createNode(NodeType.Configuration);
        newConf.setProperty("name", c.getName());
        newConf.setProperty("id", newConf.getId());
        c.setId(newConf.getId());
        if(c.getDescription()!=null)
            newConf.setProperty("description", c.getDescription());

        Relationship r=newConf.createRelationshipTo(newNode, RelationType.ConfigurationRelantionship);
        r.setProperty("id", newNode.getId());
        setConfiguration(newConf, c.getName().toUpperCase(), c);

        return newConf;

    }

    private void setConfiguration(Node newConf, String type, it.polito.neo4j.jaxb.Configuration c) throws MyInvalidIdException {

        switch(type){

        case "FIREWALL":{
            List<Elements> list=c.getFirewall().getElements();
            if(!list.isEmpty()){
                for(Elements e : list){
                    Node newElem=graphDB.createNode(NodeType.Firewall);
                    newElem.setProperty("source", e.getSource());
                    newElem.setProperty("destination", e.getDestination());

                    Relationship firewall=newElem.createRelationshipTo(newConf, RelationType.ElementRelationship);
                    firewall.setProperty("id", newConf.getId());
                }

            }else{
                vlogger.logger.info("Configuration firewall empty");

            }
            break;
        }

        case "ANTISPAM":{
            List<String> list=c.getAntispam().getSource();
            if(!list.isEmpty()){
                for(String s : list){
                    Node newElem=graphDB.createNode(NodeType.Antispam);
                    newElem.setProperty("source", s);
                    Relationship antispam=newElem.createRelationshipTo(newConf, RelationType.ElementRelationship);
                    antispam.setProperty("id", newConf.getId());
                }

            }else{
                vlogger.logger.info("Configuration Antispam empty");

            }
            break;

        }
        case "CACHE":{
            List<String> list=c.getCache().getResource();
            if(!list.isEmpty()){
                for(String s : list){
                    Node newElem=graphDB.createNode(NodeType.Cache);
                    newElem.setProperty("resource", s);
                    Relationship cache=newElem.createRelationshipTo(newConf, RelationType.ElementRelationship);
                    cache.setProperty("id", newConf.getId());
                }
            }else{
                vlogger.logger.info("Configuration Cache empty");

            }
            break;

        }
        case "DPI":{
            List<String> list=c.getDpi().getNotAllowed();
            if(!list.isEmpty()){
                for(String s : list){
                    Node newElem=graphDB.createNode(NodeType.DPI);
                    newElem.setProperty("notAllowed", s);
                    Relationship dpi=newElem.createRelationshipTo(newConf, RelationType.ElementRelationship);
                    dpi.setProperty("id", newConf.getId());
                }
            }else{
                vlogger.logger.info("Configuration Dpi empty");

            }
            break;
        }
        case "ENDHOST":{
            String destination=c.getEndhost().getDestination();
            String body=c.getEndhost().getBody();
            String email_from=c.getEndhost().getEmailFrom();
            ProtocolTypes protocol=c.getEndhost().getProtocol();
            String options=c.getEndhost().getOptions();
            String url=c.getEndhost().getUrl();
            BigInteger sequence=c.getEndhost().getSequence();
            if(destination!=null){
                Node dest=graphDB.createNode(NodeType.EndHost);
                dest.setProperty("destination", destination);
                Relationship d=dest.createRelationshipTo(newConf,  RelationType.ElementRelationship);
                d.setProperty("id", newConf.getId());
                d.setProperty("name", "destination");
            }
            if(body!=null){
                Node b=graphDB.createNode(NodeType.EndHost);
                b.setProperty("body", body);
                Relationship bo=b.createRelationshipTo(newConf,  RelationType.ElementRelationship);
                bo.setProperty("id", newConf.getId());
                bo.setProperty("name", "body");

            }
            if(email_from!=null){
                Node email=graphDB.createNode(NodeType.EndHost);
                email.setProperty("email_from", email_from);
                Relationship e_from=email.createRelationshipTo(newConf,  RelationType.ElementRelationship);
                e_from.setProperty("id", newConf.getId());
                e_from.setProperty("name", "email_from");
            }
            if(protocol!=null){
                String protocollo=c.getEndhost().getProtocol().value();
                Node proto=graphDB.createNode(NodeType.EndHost);
                proto.setProperty("protocol", protocollo);
                Relationship p=proto.createRelationshipTo(newConf,  RelationType.ElementRelationship);
                p.setProperty("id", newConf.getId());
                p.setProperty("name", "protocol");
            }
            if(options!=null){
                Node opt=graphDB.createNode(NodeType.EndHost);
                opt.setProperty("options", options);
                Relationship o=opt.createRelationshipTo(newConf,  RelationType.ElementRelationship);
                o.setProperty("id", newConf.getId());
                o.setProperty("name", "options");
            }
            if(url!=null){
                Node u=graphDB.createNode(NodeType.EndHost);
                u.setProperty("url", url);
                Relationship ur=u.createRelationshipTo(newConf,  RelationType.ElementRelationship);
                ur.setProperty("id", newConf.getId());
                ur.setProperty("name", "url");
            }
            if(sequence != null && !sequence.equals(BigInteger.ZERO)){
                Node seq=graphDB.createNode(NodeType.EndHost);
                seq.setProperty("sequence", sequence);
                Relationship seque=seq.createRelationshipTo(newConf,  RelationType.ElementRelationship);
                seque.setProperty("id", newConf.getId());
                seque.setProperty("name", "sequence");
            }

            break;
        }
        case "ENDPOINT":{
            vlogger.logger.info("It is an ENDPOINT");

            break;
        }
        case "FIELDMODIFIER":{
            vlogger.logger.info("It is a FIELMODIFIER");

            break;

        }

        case "MAILCLIENT":{
            String list=c.getMailclient().getMailserver();
            if(list!=null){
                Node newElem=graphDB.createNode(NodeType.Mailclient);
                newElem.setProperty("mailserver", list);
                Relationship mailclient=newElem.createRelationshipTo(newConf, RelationType.ElementRelationship);
                mailclient.setProperty("id", newConf.getId());
            }else{
                vlogger.logger.info("Configuration Mailclient empty");

            }

            break;
        }
        case "MAILSERVER":{
            vlogger.logger.info("It is a MAILSERVER");

            break;
        }
        case "NAT":{

            List<String> list=c.getNat().getSource();
            if(!list.isEmpty()){
                for(String s : list){
                    Node newElem=graphDB.createNode(NodeType.NAT);
                    newElem.setProperty("source", s);
                    Relationship nat=newElem.createRelationshipTo(newConf, RelationType.ElementRelationship);
                    nat.setProperty("id", newConf.getId());
                }
            }else{
                vlogger.logger.info("Configuration Nat empty");

            }
            break;
        }


        case "VPNACCESS":{
            String list=c.getVpnaccess().getVpnexit();
            if(list!=null){
                Node newElem=graphDB.createNode(NodeType.VPNAccess);
                newElem.setProperty("vpnexit", list);
                Relationship vpnaccess=newElem.createRelationshipTo(newConf, RelationType.ElementRelationship);
                vpnaccess.setProperty("id", newConf.getId());
            }else{
                vlogger.logger.info("Configuration Vpnaccess empty");

            }

            break;
        }
        case "VPNEXIT":{
            String list=c.getVpnexit().getVpnaccess();
            if(list!=null){
                Node newElem=graphDB.createNode(NodeType.VPNExit);
                newElem.setProperty("vpnaccess", list);
                Relationship vpnexit=newElem.createRelationshipTo(newConf, RelationType.ElementRelationship);
                vpnexit.setProperty("id", newConf.getId());
            }else{
                vlogger.logger.info("Configuration Vpnexit empty");

            }

            break;
        }
        case "WEBCLIENT":{
            String list=c.getWebclient().getNameWebServer();
            if(list!=null){
                Node newElem=graphDB.createNode(NodeType.Webclient);
                newElem.setProperty("webserver", list);
                Relationship webclient=newElem.createRelationshipTo(newConf, RelationType.ElementRelationship);
                webclient.setProperty("id", newConf.getId());
            }else{
                vlogger.logger.info("Configuration Webclient empty");

            }

            break;
        }
        case "WEBSERVER":{
            vlogger.logger.info("It's a webserver");
            break;
        }
        default:{
            throw new MyInvalidIdException("Element node "+type+" not valid");
        }
        }

    }

    private Node createNode(Node nffgRoot, it.polito.neo4j.jaxb.Node nodo, it.polito.neo4j.jaxb.Graph graph){
        Node newNode = graphDB.createNode(NodeType.Node);
        newNode.setProperty("name", nodo.getName());
        newNode.setProperty("functionalType", nodo.getFunctionalType().value());
        nodo.setId(newNode.getId());
        newNode.setProperty("id", newNode.getId());
        Relationship r=newNode.createRelationshipTo(nffgRoot, RelationType.OwnerRelationship);
        r.setProperty("id", graph.getId());

        return newNode;
    }

    private Map<String,Neighbour> addNeighbours(it.polito.neo4j.jaxb.Node nodo, Graph graph) throws MyNotFoundException{

        Node srcNode = graphDB.findNode(NodeType.Node, "id", nodo.getId());
        if(srcNode==null){
            throw new DataNotFoundException("Source node (in neighbour node) not found");
        }


        Map<String,Neighbour> neighbours = new HashMap<>();

        for(Neighbour neighbour : nodo.getNeighbour()){

            Node dstNode = findNodeByNameOfSpecificGraph(neighbour.getName(), graph.getId());

            if(dstNode == null)
                throw new DataNotFoundException("Destination node (in neighbour node) not found");

            Relationship rel = srcNode.createRelationshipTo(dstNode,RelationType.PathRelationship);

            neighbour.setId(dstNode.getId());
            rel.setProperty("id", neighbour.getId());
            neighbours.put((String)dstNode.getProperty("name"),neighbour);
        }
        return neighbours;
    }

    private Node findNodeByIdAndSpecificGraph(long nodoId, long graphId) throws MyNotFoundException {

        Node node=graphDB.findNode(NodeType.Node, "id", nodoId);
        Node n;

        if(node == null)
            throw new DataNotFoundException("There is no Node whose Id is '" + nodoId + "'");
        else{

            for(Relationship rel : node.getRelationships(RelationType.OwnerRelationship)){
                Node[] nodi = rel.getNodes();
                if(nodi[0].getId() == graphId || nodi[1].getId() == graphId){
                    if((long)rel.getProperty("id") == graphId)
                        return node;

                }

            }
        }
        return node;
    }

    private Node findNodeByNameOfSpecificGraph(String nodeName, long graphId){

        ResourceIterator<Node> nodes = graphDB.findNodes(NodeType.Node, "name", nodeName);
        while (nodes.hasNext()){
            Node n = nodes.next();
            for(Relationship rel : n.getRelationships(RelationType.OwnerRelationship)){
                Node[] nodi = rel.getNodes();
                if((nodi[0].getId() == graphId || nodi[1].getId()== graphId)){
                    if((long)rel.getProperty("id") == graphId)
                        return n;
                }
            }
        }
        return null;
    }

    public it.polito.neo4j.jaxb.Node createNode(it.polito.neo4j.jaxb.Node node, long graphId) throws MyNotFoundException, DuplicateNodeException, MyInvalidIdException{
        Node graph;
        Transaction tx = graphDB.beginTx();


        try{
            graph = findGraph(graphId);
            Graph myGraph = getGraph(graphId);
            if(DuplicateNode(node,myGraph))
                throw new DuplicateNodeException("This node is already present");
            Node newNode = createNode(graph,node,myGraph);
            node.setId(newNode.getId());
            it.polito.neo4j.jaxb.Configuration c=node.getConfiguration();
            Node newConf=createConfiguration(newNode, c);
            c.setId(newConf.getId());
            Map<String, Neighbour> neighs = addNeighbours(node, myGraph);
            for(Neighbour neig : node.getNeighbour()){
                Neighbour n = neighs.get(neig.getName());
                neig.setId(n.getId());
            }

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
            //NEVER BEEN HERE
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
            findGraph(id);//this method rises an exception if not found graph with Id id
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
                if((nodiRelation[0].getId()==graphId || nodiRelation[1].getId()==graphId))
                    if((long)rel.getProperty("id")==graphId)
                        nodi.add(nodo);
            }
        }
        return nodi;
    }

    private Node findGraph(long graphId) throws MyNotFoundException{
        Node graph;

        try{
            graph=graphDB.findNode(NodeType.Nffg, "id", graphId);
            if(graph==null){
                throw new DataNotFoundException("There is no Graph whose Id is '" + graphId + "'");

            }
        }
        catch(NotFoundException e){
            throw new MyNotFoundException("There is no Graph whose Id is '" + graphId + "'");
        }

        return graph;
    }

    private Node findNode(long nodeId) throws MyNotFoundException{
        Node node;

        try{
            node=graphDB.findNode(NodeType.Node, "id", nodeId);
            if(node==null)
                throw new DataNotFoundException("There is no Node whose Id is '" + nodeId + "'");
        }
        catch(NotFoundException e){
            throw new MyNotFoundException("There is no Node whose Id is '" + nodeId + "'");
        }

        return node;
    }


    private void addNeighbour(it.polito.neo4j.jaxb.Node src, Node dst, long neighbourId){
        Neighbour vicino = obFactory.createNeighbour();
        vicino.setId(neighbourId);
        vicino.setName((String) dst.getProperty("name"));
        src.getNeighbour().add(vicino);
    }

    private it.polito.neo4j.jaxb.Node retrieveNode(Node nodo){
        it.polito.neo4j.jaxb.Node n = obFactory.createNode();

        n.setId(nodo.getId());
        n.setName( nodo.getProperty("name").toString());
        n.setFunctionalType(it.polito.neo4j.jaxb.FunctionalTypes.valueOf((String) nodo.getProperty("functionalType")));

        //retrieve neighbours:
        Iterable<Relationship> links = nodo.getRelationships(RelationType.PathRelationship, Direction.OUTGOING);
        Iterator<Relationship> linksIt = links.iterator();
        while(linksIt.hasNext()){
            Relationship link = linksIt.next();
            Node endpoint = link.getEndNode();
            addNeighbour(n, endpoint, (long)link.getProperty("id"));
        }

        //retrieve configuration:
        it.polito.neo4j.jaxb.Configuration c=obFactory.createConfiguration();
        c=retrieveconfiguration(nodo);
        n.setConfiguration(c);

        return n;
    }

    private Configuration retrieveconfiguration(Node nodo) {
        it.polito.neo4j.jaxb.Configuration c=obFactory.createConfiguration();
        Relationship rel = nodo.getSingleRelationship(RelationType.ConfigurationRelantionship, Direction.BOTH);
        if(rel != null){
            Node conf;
            Node[] nodiRelation = rel.getNodes();
            String func_type;
            if((nodiRelation[0].getId()==nodo.getId() || nodiRelation[1].getId()==nodo.getId())){
                if((long)rel.getProperty("id") == nodo.getId()){
                    if(nodiRelation[0].hasLabel(NodeType.Configuration)){
                        conf=nodiRelation[0];
                    }else{
                        conf=nodiRelation[1];
                    }
                    func_type=conf.getProperty("name").toString();
                    c.setId((long)conf.getProperty("id"));
                    Object value=conf.getProperty("description", "null");
                    if(value!="null")
                        c.setDescription(conf.getProperty("description").toString());
                    c.setName(func_type);
                    switch(c.getName().toUpperCase()){

                    case "FIREWALL":{
                        Iterable<Relationship> confs= conf.getRelationships(RelationType.ElementRelationship);
                        Iterator<Relationship> confsIt = confs.iterator();
                        Firewall firewall=new Firewall();
                        List<Elements> element_list=new ArrayList<Elements>();
                        while(confsIt.hasNext()){
                            Relationship relConf = confsIt.next();
                            Node element = relConf.getStartNode();
                            if(element.hasLabel(NodeType.Firewall)){
                                Elements e=new Elements();
                                e.setDestination(element.getProperty("destination").toString());
                                e.setSource(element.getProperty("source").toString());
                                element_list.add(e);
                            }
                        }
                        firewall.getElements().addAll(element_list);
                        c.setFirewall(firewall);
                        break;

                    }

                    case "ANTISPAM":{
                        Iterable<Relationship> confs= conf.getRelationships(RelationType.ElementRelationship);
                        Iterator<Relationship> confsIt = confs.iterator();
                        Antispam antispam=new Antispam();
                        List<String> source=new ArrayList<String>();
                        while(confsIt.hasNext()){
                            Relationship relConf = confsIt.next();
                            Node element = relConf.getStartNode();
                            if(element.hasLabel(NodeType.Antispam)){
                                String s=element.getProperty("source").toString();
                                source.add(s);
                            }
                        }
                        antispam.getSource().addAll(source);
                        c.setAntispam(antispam);
                        break;
                    }

                    case "CACHE":{
                        Iterable<Relationship> confs= conf.getRelationships(RelationType.ElementRelationship);
                        Iterator<Relationship> confsIt = confs.iterator();
                        Cache cache=new Cache();
                        List<String> resource=new ArrayList<String>();
                        while(confsIt.hasNext()){
                            Relationship relConf = confsIt.next();
                            Node element = relConf.getStartNode();
                            if(element.hasLabel(NodeType.Cache)){
                                String s=element.getProperty("resource").toString();
                                resource.add(s);
                            }
                        }
                        cache.getResource().addAll(resource);
                        c.setCache(cache);
                        break;
                    }
                    case "DPI":{
                        Iterable<Relationship> confs= conf.getRelationships(RelationType.ElementRelationship);
                        Iterator<Relationship> confsIt = confs.iterator();
                        Dpi dpi=new Dpi();
                        List<String> notAllowed=new ArrayList<String>();
                        while(confsIt.hasNext()){
                            Relationship relConf = confsIt.next();
                            Node element = relConf.getStartNode();
                            if(element.hasLabel(NodeType.DPI)){
                                String s=element.getProperty("notAllowed").toString();
                                notAllowed.add(s);
                            }
                        }
                        dpi.getNotAllowed().addAll(notAllowed);
                        c.setDpi(dpi);
                        break;
                    }

                    case "ENDHOST":{
                        Iterable<Relationship> confs= conf.getRelationships(RelationType.ElementRelationship);
                        Iterator<Relationship> confsIt = confs.iterator();
                        Endhost endhost=new Endhost();
                        String destination=new String();
                        String body=new String();
                        String email_from=new String();
                        String protocol=new String();
                        String options=new String();
                        String url=new String();
                        BigInteger sequence=null;
                        while(confsIt.hasNext()){
                            Relationship relConf = confsIt.next();
                            Node element = relConf.getStartNode();
                            if(element.hasLabel(NodeType.EndHost)){
                                String type=relConf.getProperty("name").toString();
                                if(type.compareTo("destination")==0){
                                    destination=element.getProperty("destination").toString();
                                }else 
                                    if(type.compareTo("body")==0){
                                        body=element.getProperty("body").toString();
                                    }else
                                        if(type.compareTo("email_from")==0){
                                            email_from=element.getProperty("email_from").toString();
                                        }else
                                            if(type.compareTo("protocol")==0){
                                                protocol=element.getProperty("protocol").toString();
                                            }else
                                                if(type.compareTo("options")==0){
                                                    options=element.getProperty("options").toString();
                                                }else
                                                    if(type.compareTo("url")==0){
                                                        url=element.getProperty("url").toString();
                                                    }else
                                                        if(type.compareTo("sequence")==0){
                                                            sequence=new BigInteger((byte[]) element.getProperty("sequence"));
                                                        }

                            }
                        }

                        endhost.setBody(body);
                        endhost.setDestination(destination);
                        endhost.setEmailFrom(email_from);
                        endhost.setOptions(options);
                        endhost.setSequence(sequence);
                        endhost.setUrl(url);
                        if(!protocol.isEmpty())
                            endhost.setProtocol(ProtocolTypes.fromValue(protocol));
                        c.setEndhost(endhost);
                        break;
                    }

                    case "ENDPOINT":{
                        Endpoint endpoint=new Endpoint();
                        c.setEndpoint(endpoint);
                        break;
                    }

                    case "FIELDMODIFIER":{

                        Fieldmodifier field=new Fieldmodifier();
                        c.setFieldmodifier(field);
                        break;
                    }
                    case "MAILCLIENT":{
                        Iterable<Relationship> confs= conf.getRelationships(RelationType.ElementRelationship);
                        Iterator<Relationship> confsIt = confs.iterator();
                        Mailclient mailclient=new Mailclient();
                        String mailserver= new String();
                        while(confsIt.hasNext()){
                            Relationship relConf = confsIt.next();
                            Node element = relConf.getStartNode();
                            if(element.hasLabel(NodeType.Mailclient)){
                                mailserver=element.getProperty("mailserver").toString();
                            }
                        }
                        mailclient.setMailserver(mailserver);
                        c.setMailclient(mailclient);
                        break;
                    }

                    case "MAILSERVER":{

                        Mailserver mailserver=new Mailserver();
                        c.setMailserver(mailserver);
                        break;
                    }

                    case "NAT":{
                        Iterable<Relationship> confs= conf.getRelationships(RelationType.ElementRelationship);
                        Iterator<Relationship> confsIt = confs.iterator();
                        Nat nat=new Nat();
                        List<String> list=new ArrayList<String>();
                        while(confsIt.hasNext()){
                            Relationship relConf = confsIt.next();
                            Node element = relConf.getStartNode();
                            if(element.hasLabel(NodeType.NAT)){
                                String s=element.getProperty("source").toString();
                                list.add(s);
                            }
                        }
                        nat.getSource().addAll(list);
                        c.setNat(nat);
                        break;
                    }

                    case "VPNACCESS":{

                        Iterable<Relationship> confs= conf.getRelationships(RelationType.ElementRelationship);
                        Iterator<Relationship> confsIt = confs.iterator();
                        Vpnaccess vpnaccess=new Vpnaccess();
                        String vpnexit= new String();
                        while(confsIt.hasNext()){
                            Relationship relConf = confsIt.next();
                            Node element = relConf.getStartNode();
                            if(element.hasLabel(NodeType.VPNAccess)){
                                vpnexit=element.getProperty("vpnexit").toString();
                            }
                        }
                        vpnaccess.setVpnexit(vpnexit);
                        c.setVpnaccess(vpnaccess);
                        break;
                    }

                    case "VPNEXIT":{

                        Iterable<Relationship> confs= conf.getRelationships(RelationType.ElementRelationship);
                        Iterator<Relationship> confsIt = confs.iterator();
                        Vpnexit vpnexit=new Vpnexit();
                        String vpnaccess= new String();
                        while(confsIt.hasNext()){
                            Relationship relConf = confsIt.next();
                            Node element = relConf.getStartNode();
                            if(element.hasLabel(NodeType.VPNExit)){
                                vpnaccess=element.getProperty("vpnaccess").toString();
                            }
                        }
                        vpnexit.setVpnaccess(vpnaccess);
                        c.setVpnexit(vpnexit);
                        break;
                    }

                    case "WEBCLIENT":{
                        Iterable<Relationship> confs= conf.getRelationships(RelationType.ElementRelationship);
                        Iterator<Relationship> confsIt = confs.iterator();
                        Webclient webclient=new Webclient();
                        String webserver= new String();
                        while(confsIt.hasNext()){
                            Relationship relConf = confsIt.next();
                            Node element = relConf.getStartNode();
                            if(element.hasLabel(NodeType.Webclient)){
                                webserver=element.getProperty("webserver").toString();
                            }
                        }
                        webclient.setNameWebServer(webserver);
                        c.setWebclient(webclient);
                        break;
                    }

                    case "WEBSERVER":{

                        Webserver webserver=new Webserver();
                        c.setWebserver(webserver);
                        break;
                    }




                    }

                }
            }

        }else{
            vlogger.logger.info("Not valid functional type");

        }
        return c;
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

        deleteConfiguration(n);

        for (Relationship r : n.getRelationships())
        {
            r.getAllProperties().clear();
            r.delete();
        }

        n.delete();
    }

    private void deleteConfiguration(Node n) {
        //delete configuration
        Relationship rel = n.getSingleRelationship(RelationType.ConfigurationRelantionship, Direction.BOTH);
        if(rel != null){
            Node conf=rel.getStartNode();
            Iterable<Relationship> confs= conf.getRelationships(RelationType.ElementRelationship);
            Iterator<Relationship> confsIt = confs.iterator();
            while(confsIt.hasNext()){
                Relationship relConf = confsIt.next();
                Node element = relConf.getStartNode();
                element.getAllProperties().clear();
                relConf.getAllProperties().clear();
                relConf.delete();
                element.delete();
            }
            conf.getAllProperties().clear();
            conf.delete();
            rel.getAllProperties().clear();
            rel.delete();
        }

    }

    public void deleteNode(long graphId, long nodeId) throws MyNotFoundException{
        Node nodo;
        Transaction tx = graphDB.beginTx();

        try{
            findGraph(graphId);
            nodo = findNode(nodeId);
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

    public it.polito.neo4j.jaxb.Node getNode(long graphId, String nodeName) throws MyNotFoundException{
        Node nodo;
        Transaction tx = graphDB.beginTx();

        try{
            findGraph(graphId);
            nodo = findNodeByNameOfSpecificGraph(nodeName, graphId);
            if(nodo==null)
                return null;

            else{
                tx.success();
                return retrieveNode(nodo);
            }
        }
        catch(MyNotFoundException e){
            tx.close();
            throw new MyNotFoundException(e.getMessage());
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
            nodo = findNode(nodeId);
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
            nodosrc=findNode(nodeId);
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
            nodo = findNode(nodeId);
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
            nodo = findNeighbourNode(nodeId);
            if(nodo!=null){
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
            }
            //if we arrive at this point it means there is not neighbour ad id neghbourId
            tx.failure();
            return null;
        }
        finally{
            tx.close();
        }
    }

    private Node findNeighbourNode(long nodeId) throws MyNotFoundException {
        Node node;

        try{
            node=graphDB.findNode(NodeType.Node, "id",nodeId);

        }
        catch(NotFoundException e){
            throw new MyNotFoundException("There is no Node whose Id is '" + nodeId + "'");
        }

        return node;
    }

    public void deleteNeighbour(long graphId, long nodeId, long neighbourId) throws MyNotFoundException{
        Node nodo;
        Transaction tx = graphDB.beginTx();
        boolean trovato=false;

        try{
            findGraph(graphId);
            nodo = findNode(nodeId);
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

    public it.polito.neo4j.jaxb.Node updateNode(it.polito.neo4j.jaxb.Node node, long graphId, long nodeId) throws MyInvalidObjectException,MyNotFoundException, MyInvalidIdException{
        Node nodo;
        Transaction tx = graphDB.beginTx();
        it.polito.neo4j.jaxb.Node returnedNode;

        try{
            findGraph(graphId);
            nodo = findNode(nodeId);
            if(validNode(node,getGraph(graphId)) == false)
                throw new MyInvalidObjectException("Invalid node");
            nodo.setProperty("name", node.getName());
            nodo.setProperty("functionalType", node.getFunctionalType().value());
            for(Neighbour neigh : node.getNeighbour()){
                Neighbour n = updateNeighbour(nodo, neigh, graphId);
                neigh.setId(n.getId());
            }

            deleteConfiguration(nodo);
            createConfiguration(nodo, node.getConfiguration());
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
            //graphDB.getRelationshipById(neigh.getId());
            try {
                changeNeighbour(nodo, neigh, neigh.getId(), graphId);
            } catch (MyNotFoundException e) {
                e.printStackTrace();
            }
            return neigh;
        }
        catch(NotFoundException e){
            Relationship  rel = nodo.createRelationshipTo(dst, RelationType.PathRelationship);
            rel.setProperty("id", neigh.getId());
        }


        return neigh;
    }



    public Graph updateGraph(Graph graph, long graphId) throws MyNotFoundException,DuplicateNodeException,MyInvalidObjectException, MyInvalidIdException{
        Transaction tx = graphDB.beginTx();
        Node nodo;
        Node graph_old;

        try{
            graph_old = findGraph(graphId);
            for(Relationship rel: graph_old.getRelationships(RelationType.OwnerRelationship)){
                Node[] nodes = rel.getNodes();
                if(nodes[0].hasLabel(NodeType.Node))
                    deleteNode(nodes[0]);
                else
                    deleteNode(nodes[1]);
            }

            for(it.polito.neo4j.jaxb.Node tmpnodo : graph.getNode()){
                Node newNode = createNode(graph_old, tmpnodo, graph);
                tmpnodo.setId(newNode.getId());
                it.polito.neo4j.jaxb.Configuration c=tmpnodo.getConfiguration();
                Node newConf=createConfiguration(newNode, c);
                c.setId(newConf.getId());

            }

            //modify neighbours ids inserting ids relationship


            for(it.polito.neo4j.jaxb.Node tmpnodo : graph.getNode()){
                Map<String, Neighbour> neighs = addNeighbours(tmpnodo, graph);

                for(Neighbour neig : tmpnodo.getNeighbour()){
                    Neighbour n = neighs.get(neig.getName());
                    neig.setId(n.getId());

                }


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


    public it.polito.neo4j.jaxb.Node updateNeighbour(Neighbour neighbour, long graphId, long nodeId, long neighbourId) throws MyInvalidObjectException,MyNotFoundException{
        Node nodo;
        Transaction tx = graphDB.beginTx();
        it.polito.neo4j.jaxb.Node returnedNode;

        try{
            findGraph(graphId);
            nodo = findNode(nodeId);
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

        relationship.setProperty("id", neigh.getId());

    }

    public it.polito.neo4j.jaxb.Paths findAllPathsBetweenTwoNodes(long graphId, String srcName, String dstName, String direction) throws MyNotFoundException{
        Transaction tx = graphDB.beginTx();
        Set<String> pathPrinted = new HashSet<>();

        try{
            findGraph(graphId);
            Node src = findNodeByNameOfSpecificGraph(srcName, graphId);
            Node dst = findNodeByNameOfSpecificGraph(dstName, graphId);
            if(src == null ){
                tx.failure();
                throw new DataNotFoundException("Source node "+src+" not exists");
            }else
                if(dst == null){
                    tx.failure();
                    throw new DataNotFoundException("Destination node "+dst+" not exists");
                }
            PathFinder<Path> finder = GraphAlgoFactory.allSimplePaths(PathExpanders.forTypeAndDirection(RelationType.PathRelationship, Direction.valueOf(direction.toUpperCase())), MAX_DEPTH);

            for (Path p : finder.findAllPaths(src, dst))
            {

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

    public it.polito.neo4j.jaxb.Node getNodeByName(String name, long graphId) throws MyNotFoundException {
        Node nodo;
        Transaction tx = graphDB.beginTx();

        try{
            findGraph(graphId);

            nodo = findNodeByNameOfSpecificGraph(name, graphId);
            tx.success();
            if(nodo!=null)
                return retrieveNode(nodo);
            else
                return null;
        }
        catch(MyNotFoundException e){
            tx.close();
            throw new MyNotFoundException(e.getMessage());
        }
        finally{
            tx.close();
        }
    }

    public it.polito.neo4j.jaxb.Node getNodeById(long nodeId, long graphId) throws MyNotFoundException {
        Node nodo;
        Transaction tx = graphDB.beginTx();

        try{
            findGraph(graphId);

            nodo = findNodeByIdAndSpecificGraph(nodeId, graphId);
            tx.success();
            if(nodo==null){
                return null;
            }else
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

    public void checkGraph(long graphId) throws MyNotFoundException {

        Transaction tx = graphDB.beginTx();

        try{
            findGraph(graphId);


            tx.success();

        }
        catch(MyNotFoundException e){
            tx.close();
            throw new MyNotFoundException(e.getMessage());
        }
        finally{
            tx.close();
        }

    }

    public it.polito.neo4j.jaxb.Configuration updateConfiguration(long nodeId, long graphId, it.polito.neo4j.jaxb.Configuration nodeConfiguration) throws MyNotFoundException, MyInvalidIdException {
        Node nodo;
        Transaction tx = graphDB.beginTx();
        it.polito.neo4j.jaxb.Configuration returnedConf;

        try{
            findGraph(graphId);
            nodo=findNode(nodeId);
            deleteConfiguration(nodo);
            createConfiguration(nodo, nodeConfiguration);
            returnedConf = retrieveconfiguration(nodo);
            tx.success();
        }
        finally{
            tx.close();
        }
        return returnedConf;
    }


}