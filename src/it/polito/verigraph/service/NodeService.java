/*******************************************************************************
 * Copyright (c) 2017 Politecnico di Torino and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package it.polito.verigraph.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.InternalServerErrorException;
import javax.xml.bind.JAXBException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.main.JsonSchema;
import it.polito.neo4j.exceptions.MyInvalidIdException;
import it.polito.neo4j.exceptions.MyNotFoundException;
import it.polito.neo4j.manager.Neo4jDBManager;
import it.polito.verigraph.exception.BadRequestException;
import it.polito.verigraph.exception.DataNotFoundException;
import it.polito.verigraph.exception.ForbiddenException;
import it.polito.verigraph.model.Configuration;
import it.polito.verigraph.model.Graph;
import it.polito.verigraph.model.Neighbour;
import it.polito.verigraph.model.Node;

public class NodeService {

    private Neo4jDBManager manager=new Neo4jDBManager();

    public NodeService() {}

    public List<Node> getAllNodes(long graphId) throws JsonParseException, JsonMappingException, JAXBException, IOException, MyNotFoundException {
        if (graphId < 0) {
            throw new ForbiddenException("Illegal graph id: " + graphId);
        }

        /*il controllo sull'esistenza del grafo viene fatto all'interno della getNodes

         */

        Map<Long, Node> nodes =manager.getNodes(graphId);
        return new ArrayList<Node>(nodes.values());
    }

    public Node getNode(long graphId, long nodeId) throws JsonParseException, JsonMappingException, JAXBException, IOException, MyNotFoundException {
        if (graphId < 0) {
            throw new ForbiddenException("Illegal graph id: " + graphId);
        }
        if (nodeId < 0) {
            throw new ForbiddenException("Illegal node id: " + nodeId);
        }

        Node node=manager.getNodeById(nodeId, graphId);
        if (node == null) {
            throw new DataNotFoundException("Node with id " + nodeId + " not found in graph with id " + graphId);
        }
        return node;
    }

    public Node updateNode(long graphId, Node node) throws JAXBException, IOException, MyInvalidIdException {
        if (graphId < 0) {
            throw new ForbiddenException("Illegal graph id: " + graphId);
        }
        if (node.getId() < 0) {
            throw new ForbiddenException("Illegal node id: " + node.getId());
        }

        Graph graph=manager.getGraph(graphId);
        validateNode(graph, node);

        Node n=manager.updateNode(graphId, node, node.getId());
        validateNode(graph, n);

        return n;
    }

    public Node removeNode(long graphId, long nodeId) throws JsonParseException, JsonMappingException, JAXBException, IOException {
        if (graphId < 0) {
            throw new ForbiddenException("Illegal graph id: " + graphId);
        }
        if (nodeId < 0) {
            throw new ForbiddenException("Illegal node id: " + nodeId);
        }

        Graph graph=manager.getGraph(graphId);
        if (graph == null)
            throw new DataNotFoundException("Graph with id " + graphId + " not found");
        Node n=manager.deleteNode(graphId, nodeId);
        return n;
    }

    public Node addNode(long graphId, Node node) throws JsonParseException, JsonMappingException, JAXBException, IOException, MyInvalidIdException {
        if (graphId < 0) {
            throw new ForbiddenException("Illegal graph id: " + graphId);
        }

        Graph graph=manager.getGraph(graphId);
        validateNode(graph, node);
        Node n=manager.addNode(graphId, node);
        validateNode(graph, n);
        return n;
    }

    public Node searchByName(long graphId, String nodeName) throws JsonParseException, JsonMappingException, JAXBException, IOException {
        if (graphId < 0) {
            throw new ForbiddenException("Illegal graph id: " + graphId);
        }
        Graph graph = manager.getGraph(graphId);
        if (graph == null)
            throw new DataNotFoundException("Graph with id " + graphId + " not found");
        Map<Long, Node> nodes = graph.getNodes();
        for (Node node : nodes.values()) {
            if (node.getName().equals(nodeName))
                return node;
        }
        return null;
    }

    public static void validateNode(Graph graph, Node node) throws JsonProcessingException {
        if (graph == null)
            throw new BadRequestException("Node validation failed: cannot validate null graph");
        if (node == null)
            throw new BadRequestException("Node validation failed: cannot validate null node");

        if (node.getName() == null)
            throw new BadRequestException("Node validation failed: node 'name' field cannot be null");
        if (node.getFunctional_type() == null)
            throw new BadRequestException("Node validation failed: node 'functional_type' field cannot be null");

        if (node.getName().equals(""))
            throw new BadRequestException("Node validation failed: node 'name' field cannot be an empty string");
        if (node.getFunctional_type().equals(""))
            throw new BadRequestException("Node validation failed: node 'functional_type' field cannot be an empty string");

        Node nodeFound =graph.searchNodeByName(node.getName());
        if ((nodeFound != null) && (nodeFound.equals(node) == false))
            throw new BadRequestException("Node validation failed: graph already has a node named '"+ node.getName()
            + "'");
        Configuration configuration = node.getConfiguration();
        if (configuration != null) {
            JsonNode configurationJsonNode = configuration.getConfiguration();
            // validate configuration against schema file
            validateNodeConfigurationAgainstSchemaFile(node, configurationJsonNode);
            JsonValidationService jsonValidator = new JsonValidationService(graph, node);
            boolean hasCustomValidator = jsonValidator.validateNodeConfiguration();
            if (!hasCustomValidator) {
                jsonValidator.validateFieldsAgainstNodeNames(configurationJsonNode);
            }
        }

        // validate neighbours
        Map<Long, Neighbour> nodeNeighboursMap = node.getNeighbours();
        if (nodeNeighboursMap == null)
            throw new BadRequestException("Node validation failed: node 'neighbours' cannot be null");
        for (Neighbour neighbour : nodeNeighboursMap.values()) {
            NeighbourService.validateNeighbour(graph, node, neighbour);
        }
    }

    public static void validateNodeConfigurationAgainstSchemaFile(Node node, JsonNode configurationJson) {
        String schemaFileName = node.getFunctional_type() + ".json";

        File schemaFile = new File(System.getProperty("catalina.base") + "/webapps/verigraph/jsonschema/" + schemaFileName);

        if (!schemaFile.exists()) {
            //if no REST client, try gRPC application
            schemaFile = new File(System.getProperty("user.dir") + "/jsonschema/" + schemaFileName);

            if (!schemaFile.exists()) {
                throw new ForbiddenException("Functional type '"+ node.getFunctional_type()
                + "' is not supported! Please edit 'functional_type' field of node '"
                + node.getName() + "'");
            }
        }

        JsonSchema schemaNode = null;
        try {
            schemaNode = ValidationUtils.getSchemaNode(schemaFile);
        }
        catch (IOException e) {
            throw new InternalServerErrorException("Unable to load '" + schemaFileName + "' schema file");
        }
        catch (ProcessingException e) {
            throw new InternalServerErrorException("Unable to resolve '"+ schemaFileName
                    + "' schema file as a schema node");
        }

        try {
            ValidationUtils.validateJson(schemaNode, configurationJson);
        }
        catch (ProcessingException e) {
            throw new BadRequestException("Something went wrong trying to validate node '"+ node.getName()
            + "' with the following configuration: '" + configurationJson.toString()
            + "' against the json schema '" + schemaFile.getName() + "': "
            + e.getMessage());
        }

    }

    public Configuration addNodeConfiguration(long graphId, long nodeId, Configuration nodeConfiguration) throws IOException, MyInvalidIdException {
        if (graphId < 0) {
            throw new ForbiddenException("Illegal graph id: " + graphId);
        }
        if (nodeId < 0) {
            throw new ForbiddenException("Illegal node id: " + nodeId);
        }

        Node node=manager.getNodeById(nodeId, graphId);
        validateNodeConfigurationAgainstSchemaFile(node, nodeConfiguration.getConfiguration());
        Configuration newConf=manager.updateConfiguration(nodeId, graphId, nodeConfiguration, node);
        return newConf;
    }
}