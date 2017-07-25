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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import it.polito.neo4j.manager.Neo4jDBManager;
import it.polito.verigraph.exception.BadRequestException;
import it.polito.verigraph.exception.DataNotFoundException;
import it.polito.verigraph.exception.ForbiddenException;
import it.polito.verigraph.model.Graph;
import it.polito.verigraph.model.Neighbour;
import it.polito.verigraph.model.Node;

public class NeighbourService {

    private Neo4jDBManager manager=new Neo4jDBManager();


    public List<Neighbour> getAllNeighbours(long graphId, long nodeId) throws JsonProcessingException {
        if (graphId < 0) {
            throw new ForbiddenException("Illegal graph id: " + graphId);
        }
        if (nodeId < 0) {
            throw new ForbiddenException("Illegal node id: " + nodeId);
        }

        Map<Long, Neighbour> neighbours = manager.getNeighbours(graphId, nodeId);
        return new ArrayList<Neighbour>(neighbours.values());
    }

    public Neighbour getNeighbour(long graphId, long nodeId, long neighbourId) throws JsonProcessingException {
        if (graphId < 0) {
            throw new ForbiddenException("Illegal graph id: " + graphId);
        }
        if (nodeId < 0) {
            throw new ForbiddenException("Illegal node id: " + nodeId);
        }
        if (neighbourId < 0) {
            throw new ForbiddenException("Illegal neighbour id: " + neighbourId);
        }

        Neighbour neighbour=manager.getNeighbour(graphId, nodeId, neighbourId);
        if (neighbour == null) {
            throw new DataNotFoundException("Neighbour with id "+ neighbourId + " not found for node with id " + nodeId
                    + " in graph with id " + graphId);
        }
        return neighbour;
    }

    public Neighbour addNeighbour(long graphId, long nodeId, Neighbour neighbour) throws JsonParseException, JsonMappingException, JAXBException, IOException {
        if (graphId < 0) {
            throw new ForbiddenException("Illegal graph id: " + graphId);
        }
        if (nodeId < 0) {
            throw new ForbiddenException("Illegal node id: " + nodeId);
        }
        Graph graph = manager.getGraph(graphId);
        Node node=manager.getNodeById(nodeId, graphId);
        validateNeighbour(graph, node, neighbour);
        Neighbour out=manager.addNeighbours(graphId, nodeId, neighbour);
        validateNeighbour(graph, node, neighbour);
        return out;
    }

    public Neighbour updateNeighbour(long graphId, long nodeId, Neighbour neighbour) throws JAXBException, IOException {
        if (graphId < 0) {
            throw new ForbiddenException("Illegal graph id: " + graphId);
        }
        if (nodeId < 0) {
            throw new ForbiddenException("Illegal node id: " + nodeId);
        }
        if (neighbour.getId() < 0) {
            throw new ForbiddenException("Illegal neighbour id: " + nodeId);
        }
        Graph graph=manager.getGraph(graphId);
        Node node=manager.getNodeById(nodeId, graphId);
        if (node == null) {
            throw new DataNotFoundException("Node with id " + nodeId + " not found in graph with id " + graphId);
        }

        validateNeighbour(graph, node, neighbour);
        Neighbour n=manager.updateNeighbour(graphId, nodeId, neighbour);

        return n;
    }

    public Neighbour removeNeighbour(long graphId, long nodeId, long neighbourId) {
        if (graphId < 0) {
            throw new ForbiddenException("Illegal graph id: " + graphId);
        }
        if (nodeId < 0) {
            throw new ForbiddenException("Illegal node id: " + nodeId);
        }
        if (neighbourId < 0) {
            throw new ForbiddenException("Illegal neighbour id: " + nodeId);
        }

        Neighbour n=manager.deleteNeighbour(graphId, nodeId, neighbourId);
        return n;
    }

    public static void validateNeighbour(Graph graph, Node node, Neighbour neighbour) throws JsonProcessingException {
        if (graph == null)
            throw new BadRequestException("Neighbour validation failed: cannot validate null graph");
        if (node == null)
            throw new BadRequestException("Neighbour validation failed: cannot validate null node");
        if (neighbour == null)
            throw new BadRequestException("Neighbour validation failed: cannot validate null neighbour");

        if (neighbour.getName() == null)
            throw new BadRequestException("Neighbour validation failed: neighbour 'name' field cannot be null");
        if (neighbour.getName().equals(""))
            throw new BadRequestException("Neighbour validation failed: neighbour 'name' field cannot be an empty string");

        //Node nodeFound = graph.searchNodeByName(neighbour.getName());

        Node nodeFound=graph.searchNodeByName(neighbour.getName());
        if ((nodeFound == null) || (nodeFound.getName().equals(node.getName())))
            throw new BadRequestException("Neighbour validation failed: '"+ neighbour.getName()
            + "' is not a valid name for a neighbour of node '" + node.getName() + "'");

        Neighbour neighbourFound = node.searchNeighbourByName(neighbour.getName());
        if ((neighbourFound != null) && (neighbourFound.equals(neighbour) == false))
            throw new BadRequestException("Neighbour validation failed: node '"+ node.getName()
            + "' already has a neighbour named '" + neighbour.getName() + "'");
    }
}