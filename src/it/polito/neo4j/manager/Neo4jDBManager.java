/*******************************************************************************
 * Copyright (c) 2017 Politecnico di Torino and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package it.polito.neo4j.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.ws.rs.BadRequestException;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.xml.bind.JAXBException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import it.polito.neo4j.exceptions.DuplicateNodeException;
import it.polito.neo4j.exceptions.MyInvalidObjectException;
import it.polito.neo4j.exceptions.MyNotFoundException;
import it.polito.neo4j.jaxb.ObjectFactory;
import it.polito.neo4j.jaxb.Paths;
import it.polito.neo4j.translator.GraphToNeo4j;
import it.polito.neo4j.translator.Neo4jToGraph;
import it.polito.neo4j.exceptions.MyInvalidDirectionException;
import it.polito.neo4j.exceptions.MyInvalidIdException;
import it.polito.verigraph.exception.DataNotFoundException;
import it.polito.verigraph.model.Configuration;
import it.polito.verigraph.model.Graph;
import it.polito.verigraph.model.Neighbour;
import it.polito.verigraph.model.Node;
import it.polito.verigraph.service.VerigraphLogger;

public class Neo4jDBManager {

    public static Neo4jLibrary lib = Neo4jLibrary.getNeo4jLibrary();
    //static ObjectFactory obFactory = new ObjectFactory();

    public static VerigraphLogger vlogger = VerigraphLogger.getVerigraphlogger();


    public it.polito.verigraph.model.Graph updateGraph(it.polito.verigraph.model.Graph graph) throws JAXBException, JsonParseException, JsonMappingException, IOException, MyInvalidIdException{

        it.polito.neo4j.jaxb.Graph graph_xml=GraphToNeo4j.generateObject(graph);
        it.polito.neo4j.jaxb.Graph graphReturned;
        long graphId;

        try{
            graphId = graph.getId();
            graphReturned = lib.updateGraph(graph_xml, graphId);
            it.polito.verigraph.model.Graph g=Neo4jToGraph.generateGraph(graphReturned);
            return g;

        }
        catch(MyNotFoundException e1){
            vlogger.logger.info("error update 1");
            throw new NotFoundException();
        }

        catch(DuplicateNodeException e3){
            vlogger.logger.info("error update 2");
            throw new BadRequestException(e3.getMessage());
        }
        catch(MyInvalidObjectException e4){
            vlogger.logger.info("error update 3");
            throw new BadRequestException(e4.getMessage());
        }
    }

    public void deleteGraph(long id){

        try{
            lib.deleteGraph(id);
        }
        catch(MyNotFoundException e1){
            throw new NotFoundException();
        }
    }

    public it.polito.verigraph.model.Graph addGraph(it.polito.verigraph.model.Graph graph) throws JAXBException, JsonParseException, JsonMappingException, IOException, MyInvalidIdException {

        it.polito.neo4j.jaxb.Graph graph_xml=GraphToNeo4j.generateObject(graph);
        it.polito.neo4j.jaxb.Graph graphReturned;
        it.polito.neo4j.jaxb.Node node;

        try{
            graphReturned = lib.createGraph(graph_xml);
            it.polito.verigraph.model.Graph g=Neo4jToGraph.generateGraph(graphReturned);
            return g;

        }
        catch(MyNotFoundException e){
            e.printStackTrace();
            throw new NotFoundException();

        }
    }
    public it.polito.verigraph.model.Graph getGraph(long graphId) throws JAXBException, JsonParseException, JsonMappingException, IOException {

        it.polito.neo4j.jaxb.Graph graphReturned;

        try{
            graphReturned = lib.getGraph(graphId);

            it.polito.verigraph.model.Graph g=Neo4jToGraph.generateGraph(graphReturned);

            return g;

        }
        catch(MyNotFoundException e){
            e.printStackTrace();
            throw new NotFoundException();
        }

    }

    public List<it.polito.verigraph.model.Graph> getGraphs() throws JsonProcessingException, MyNotFoundException {

        List<it.polito.verigraph.model.Graph> graphsReturned=new ArrayList<it.polito.verigraph.model.Graph>();
        it.polito.neo4j.jaxb.Graphs graphs;

        graphs = lib.getGraphs();
        for(it.polito.neo4j.jaxb.Graph tmp : graphs.getGraph()){
            it.polito.verigraph.model.Graph g=Neo4jToGraph.generateGraph(tmp);
            graphsReturned.add(g);
        }

        return graphsReturned;

    }

    public it.polito.verigraph.model.Neighbour addNeighbours(long graphId, long nodeId, it.polito.verigraph.model.Neighbour neighbour) throws JsonProcessingException {

        it.polito.neo4j.jaxb.Neighbour neighbour_xml=GraphToNeo4j.NeighbourToNeo4j(neighbour);
        it.polito.neo4j.jaxb.Neighbour neighReturned;
        it.polito.verigraph.model.Neighbour n;
        try{
            neighReturned = lib.createNeighbour(neighbour_xml, graphId, nodeId);
            n=Neo4jToGraph.NeighbourToVerigraph(neighReturned);
            return n;
        }
        catch(MyNotFoundException e2){
            e2.printStackTrace();
            throw new NotFoundException();
        }
    }

    public Neighbour deleteNeighbour(long graphId, long nodeId, long neighbourId) {

        try{
            it.polito.neo4j.jaxb.Neighbour n=lib.getNeighbour(graphId, nodeId, neighbourId);
            if(n==null){
                throw new DataNotFoundException("Neighbour validation failed: '"+ neighbourId
                        + "' is not a valid id for a neighbour of node '" + nodeId + "'" + "' of graph '" + graphId + "'");
            }else{
                lib.deleteNeighbour(graphId, nodeId, neighbourId);
                return Neo4jToGraph.NeighbourToVerigraph(n);
            }
        }
        catch(MyNotFoundException e1){
            e1.printStackTrace();
            throw new NotFoundException();
        }catch (JsonProcessingException e) {
            throw new NotFoundException("jsonprocessing node");
        }

    }

    public Neighbour updateNeighbour(long graphId, long nodeId, it.polito.verigraph.model.Neighbour neighbour) throws JsonProcessingException {
        it.polito.neo4j.jaxb.Neighbour neighbour_xml=GraphToNeo4j.NeighbourToNeo4j(neighbour);
        it.polito.neo4j.jaxb.Node nodeReturned;
        Neighbour r;
        try{
            nodeReturned=lib.updateNeighbour(neighbour_xml, graphId, nodeId, neighbour_xml.getId());
            r=Neo4jToGraph.NeighbourToVerigraph(lib.getNeighbour(graphId, nodeReturned.getId(), neighbour_xml.getId()));
            return r;
        }
        catch(MyNotFoundException e2){
            throw new NotFoundException();
        } catch (MyInvalidObjectException e) {
            e.printStackTrace();
        }
        return null;

    }

    public it.polito.verigraph.model.Node addNode(long graphId, it.polito.verigraph.model.Node node) throws IOException, MyInvalidIdException {
        it.polito.neo4j.jaxb.Node node_xml=GraphToNeo4j.NodeToNeo4j(node);
        it.polito.neo4j.jaxb.Node nodeReturned;
        it.polito.verigraph.model.Node node_v=new it.polito.verigraph.model.Node();
        try
        {

            nodeReturned = lib.createNode(node_xml, graphId);
            node_v=Neo4jToGraph.NodeToVerigraph(nodeReturned);
        }
        catch(MyNotFoundException e1){
            e1.printStackTrace();
            throw new NotFoundException();
        }

        catch(DuplicateNodeException e3){
            e3.printStackTrace();
            throw new BadRequestException();
        }
        return node_v;
    }

    public Node updateNode(long graphId, Node node, long id) throws IOException, MyInvalidIdException {
        it.polito.neo4j.jaxb.Node node_xml=GraphToNeo4j.NodeToNeo4j(node);
        it.polito.neo4j.jaxb.Node nodeReturned;
        Node node_v;
        try{
            nodeReturned = lib.updateNode(node_xml, graphId, id);
            node_v=Neo4jToGraph.NodeToVerigraph(nodeReturned);
            return node_v;
        }
        catch(MyNotFoundException e2){
            e2.printStackTrace();
            throw new NotFoundException();
        }
        catch(MyInvalidObjectException e3){
            e3.printStackTrace();
            throw new BadRequestException(e3.getMessage());
        }

    }

    public Node deleteNode(long graphId, long nodeId) {

        try{
            it.polito.neo4j.jaxb.Node n=lib.getNodeById(nodeId, graphId);
            lib.deleteNode(graphId, nodeId);
            return Neo4jToGraph.NodeToVerigraph(n);
        }
        catch(MyNotFoundException e1){
            throw new NotFoundException("graph or node not found");
        } catch (JsonProcessingException e) {
            throw new NotFoundException("jsonprocessing node");
        }
    }

    public Paths getPath(long graphId, String source, String destination, String direction) throws MyInvalidDirectionException {
        it.polito.neo4j.jaxb.Paths paths=(new ObjectFactory()).createPaths();
        try{
            if(source == null || destination == null || direction == null)
                throw new DataNotFoundException("Missing query parameters");
            paths =  lib.findAllPathsBetweenTwoNodes(graphId, source, destination,direction);
        }
        catch(MyNotFoundException e1){
            e1.printStackTrace();
            throw new NotFoundException();

        }

        return paths;
    }

    public Map<Long, Node> getNodes(long graphId) throws JsonProcessingException {
        Map<Long, Node> nodes=new HashMap<Long, Node>();
        try
        {
            Set<it.polito.neo4j.jaxb.Node> set= lib.getNodes(graphId);
            nodes=Neo4jToGraph.NodesToVerigraph(set);
        }
        catch(MyNotFoundException e1){
            e1.printStackTrace();
            throw new NotFoundException();
        }
        return nodes;

    }

    public Map<Long, Neighbour> getNeighbours(long graphId, long nodeId) throws JsonProcessingException {
        Map<Long, Neighbour> neighbours=new HashMap<Long, Neighbour>();
        try
        {
            Set<it.polito.neo4j.jaxb.Neighbour> set= lib.getNeighbours(graphId, nodeId);
            neighbours=Neo4jToGraph.NeighboursToVerigraph(set);
            return neighbours;
        }
        catch(MyNotFoundException e1){
            e1.printStackTrace();
            throw new NotFoundException();
        }

    }

    public Node getNodeByName(long graphId, String name) throws JsonProcessingException {

        it.polito.verigraph.model.Node node;
        try
        {
            it.polito.neo4j.jaxb.Node set= lib.getNodeByName(name, graphId);
            if(set==null)
                return null;
            else{
                node=Neo4jToGraph.NodeToVerigraph(set);
                return node;
            }
        }
        catch(MyNotFoundException e1){
            e1.printStackTrace();
            throw new NotFoundException();
        }

    }

    public Node getNodeById(long nodeId, long graphId) throws JsonProcessingException {
        it.polito.verigraph.model.Node node;
        try
        {
            it.polito.neo4j.jaxb.Node set= lib.getNodeById(nodeId, graphId);
            if(set==null)
                return null;
            else{
                node=Neo4jToGraph.NodeToVerigraph(set);
                return node;
            }
        }
        catch(MyNotFoundException e1){
            e1.printStackTrace();
            throw new NotFoundException();
        }
    }

    public Neighbour getNeighbour(long graphId, long nodeId, long neighbourId) throws JsonProcessingException {
        try
        {
            it.polito.neo4j.jaxb.Neighbour set= lib.getNeighbour(graphId, nodeId, neighbourId);
            if(set!=null)
                return Neo4jToGraph.NeighbourToVerigraph(set);
            else
                return null;
        }
        catch(MyNotFoundException e1){
            e1.printStackTrace();
            throw new NotFoundException();
        }
    }

    public void checkGraph(long graphId) {

        try
        {
            lib.checkGraph(graphId);
        }
        catch(MyNotFoundException e1){
            e1.printStackTrace();
            throw new NotFoundException();
        }
    }

    public Configuration updateConfiguration(long nodeId, long graphId, Configuration nodeConfiguration, Node node) throws JsonParseException, JsonMappingException, IOException, MyInvalidIdException {

        try{

            it.polito.neo4j.jaxb.Configuration conf=GraphToNeo4j.ConfToNeo4j(nodeConfiguration, node);
            it.polito.neo4j.jaxb.Configuration c=lib.updateConfiguration(nodeId, graphId, conf);
            Configuration r=Neo4jToGraph.ConfToVerigraph(c);
            return r;
        }
        catch(MyNotFoundException e2){
            e2.printStackTrace();
            throw new NotFoundException();
        }
    }

}
