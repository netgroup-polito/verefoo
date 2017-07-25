/*******************************************************************************
 * Copyright (c) 2017 Politecnico di Torino and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package it.polito.verigraph.service;

import java.io.IOException;
import java.util.List;
import javax.xml.bind.JAXBException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import it.polito.neo4j.manager.Neo4jDBManager;
import it.polito.neo4j.exceptions.MyInvalidIdException;
import it.polito.neo4j.exceptions.MyNotFoundException;
import it.polito.verigraph.exception.ForbiddenException;
import it.polito.verigraph.model.Graph;
import it.polito.verigraph.model.Node;

public class GraphService {

    private Neo4jDBManager manager= new Neo4jDBManager();
    public static VerigraphLogger vlogger = VerigraphLogger.getVerigraphlogger();

    public GraphService() {}

    public List<Graph> getAllGraphs() throws JsonProcessingException, MyNotFoundException {
        List<Graph> result;
        result=manager.getGraphs();
        for(Graph g : result){
            validateGraph(g);
        }
        return result;
    }

    public Graph getGraph(long id) throws JsonParseException, JsonMappingException, JAXBException, IOException {
        if (id < 0) {
            throw new ForbiddenException("Illegal graph id: " + id);
        }
        Graph localGraph=manager.getGraph(id);
        validateGraph(localGraph);
        return localGraph;
    }

    public Graph updateGraph(Graph graph) throws JAXBException, JsonParseException, JsonMappingException, IOException, MyInvalidIdException {
        if (graph.getId() < 0) {
            throw new ForbiddenException("Illegal graph id: " + graph.getId());
        }
        validateGraph(graph);
        Graph localGraph=new Graph();
        localGraph=manager.updateGraph(graph);
        vlogger.logger.info("Graph updated");
        validateGraph(localGraph);
        return localGraph;
    }


    public void removeGraph(long id) {

        if (id < 0) {
            throw new ForbiddenException("Illegal graph id: " + id);
        }
        manager.deleteGraph(id);
    }

    public Graph addGraph(Graph graph) throws JAXBException, JsonParseException, JsonMappingException, IOException, MyInvalidIdException {
        validateGraph(graph);
        Graph g=manager.addGraph(graph);
        validateGraph(g);
        return g;
    }

    public static void validateGraph(Graph graph) throws JsonProcessingException {
        for (Node node : graph.getNodes().values()) {
            NodeService.validateNode(graph, node);
        }
    }
}
