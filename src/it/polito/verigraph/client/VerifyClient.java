/*******************************************************************************
 * Copyright (c) 2017 Politecnico di Torino and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package it.polito.verigraph.client;

import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.ResponseProcessingException;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.polito.verigraph.model.ErrorMessage;
import it.polito.verigraph.model.Graph;
import it.polito.verigraph.model.Neighbour;
import it.polito.verigraph.model.Node;
import it.polito.verigraph.model.Verification;

public class VerifyClient {

    private WebTarget baseTarget;
    private WebTarget graphsTarget;
    private WebTarget graphTarget;
    private WebTarget nodesTarget;
    private WebTarget nodeTarget;
    private WebTarget neighboursTarget;
    private WebTarget neighbourTarget;
    private WebTarget reachabilityTarget;
    private WebTarget isolationTarget;
    private WebTarget traversalTarget;

    public VerifyClient(String address) {
        
        Client client = ClientBuilder.newClient();
        this.baseTarget = client.target(address);
        this.graphsTarget = baseTarget.path("graphs");
        this.graphTarget = graphsTarget.path("/{graphId}");
        this.nodesTarget = graphTarget.path("/nodes");
        this.nodeTarget = nodesTarget.path("//{nodeId}");
        this.neighboursTarget = nodeTarget.path("/neighbours");
        this.neighbourTarget = neighboursTarget.path("/{neighbourId}");
        this.reachabilityTarget = graphTarget.path("/policy");
        this.isolationTarget = graphTarget.path("/policy");
        this.traversalTarget = graphTarget.path("/policy");
    }

    public void checkResponse(Response response) throws VerifyClientException {
        int status = response.getStatus();

        // 400
        if (status == Response.Status.BAD_REQUEST.getStatusCode()) {
            try {
                // String responseString = response.readEntity(String.class);
                // System.out.println(responseString);
                ErrorMessage errorMessage = response.readEntity(ErrorMessage.class);
                String message = errorMessage.getErrorMessage();
                throw new VerifyClientException("Bad request: " + message);
            }
            catch (ProcessingException e) {
                throw new VerifyClientException("the content of the message cannot be mapped to an entity of the 'ErrorMessage': "
                        + e.getMessage());
            }
            catch (IllegalStateException e) {
                throw new VerifyClientException("the entity is not backed by an input stream or the original entity input stream has already been consumed without buffering the entity data prior consuming: "
                        + e.getMessage());
            }
        }
        // 403
        if (status == Response.Status.FORBIDDEN.getStatusCode()) {
            try {
                ErrorMessage errorMessage = response.readEntity(ErrorMessage.class);
                String message = errorMessage.getErrorMessage();
                throw new VerifyClientException("Forbidden: " + message);
            }
            catch (ProcessingException e) {
                throw new VerifyClientException("the content of the message cannot be mapped to an entity of the 'ErrorMessage': "
                        + e.getMessage());
            }
            catch (IllegalStateException e) {
                throw new VerifyClientException("the entity is not backed by an input stream or the original entity input stream has already been consumed without buffering the entity data prior consuming: "
                        + e.getMessage());
            }
        }
        // 404
        if (status == Response.Status.NOT_FOUND.getStatusCode()) {
            try {
                ErrorMessage errorMessage = response.readEntity(ErrorMessage.class);
                String message = errorMessage.getErrorMessage();
                throw new VerifyClientException("Not found: " + message);
            }
            catch (ProcessingException e) {
                throw new VerifyClientException("the content of the message cannot be mapped to an entity of the 'ErrorMessage': "
                        + e.getMessage());
            }
            catch (IllegalStateException e) {
                throw new VerifyClientException("the 'Response' entity is not backed by an input stream or the original entity input stream has already been consumed without buffering the entity data prior consuming: "
                        + e.getMessage());
            }
        }
        // 500
        if (status == Response.Status.INTERNAL_SERVER_ERROR.getStatusCode()) {
            try {
                ErrorMessage errorMessage = response.readEntity(ErrorMessage.class);
                String message = errorMessage.getErrorMessage();
                throw new VerifyClientException("Internal server error: " + message);
            }
            catch (ProcessingException e) {
                throw new VerifyClientException("the content of the message cannot be mapped to an entity of the 'ErrorMessage': "
                        + e.getMessage());
            }
            catch (IllegalStateException e) {
                throw new VerifyClientException("the entity is not backed by an input stream or the original entity input stream has already been consumed without buffering the entity data prior consuming: "
                        + e.getMessage());
            }
        }
        if (status != Response.Status.ACCEPTED.getStatusCode() && status != Response.Status.CREATED.getStatusCode()
                && status != Response.Status.NO_CONTENT.getStatusCode() && status != Response.Status.OK.getStatusCode())
            throw new VerifyClientException("Unknown error");
    }

    public Response createGraph(Graph graph) throws VerifyClientException, ResponseProcessingException, ProcessingException {
        Response response = graphsTarget.request().post(Entity.json(graph));
        checkResponse(response);
        return response;
    }

    public Response createGraph(String graph) throws VerifyClientException, ResponseProcessingException, ProcessingException {
        Response response = graphsTarget.request().post(Entity.entity(graph, "application/json"));
        checkResponse(response);
        return response;
    }

    public Response retrieveGraph(long graphId) throws VerifyClientException, ProcessingException {
        Response response = graphTarget.resolveTemplate("graphId", graphId).request().get();
        checkResponse(response);
        return response;
    }

    public Response updateGraph(long graphId, Graph graph) throws VerifyClientException, ResponseProcessingException, ProcessingException {
        Response response = graphTarget.resolveTemplate("graphId", graphId).request().put(Entity.json(graph));
        checkResponse(response);
        return response;
    }

    public Response deleteGraph(long graphId) throws VerifyClientException, ResponseProcessingException, ProcessingException {
        Response response = graphTarget.resolveTemplate("graphId", graphId).request().delete();
        checkResponse(response);
        return response;
    }

    public Response createNode(long graphId, Node node) throws VerifyClientException, ResponseProcessingException, ProcessingException {
        Response response = nodesTarget.resolveTemplate("graphId", graphId).request().post(Entity.json(node));
        checkResponse(response);
        return response;
    }

    public Response retrieveNode(long graphId, long nodeId) throws VerifyClientException, ProcessingException {
        Response response = nodeTarget.resolveTemplate("graphId", graphId)
                .resolveTemplate("nodeId", nodeId)
                .request()
                .get();
        checkResponse(response);
        return response;
    }

    public Response updateNode(long graphId, long nodeId, Node node) throws VerifyClientException, ResponseProcessingException, ProcessingException {
        Response response = nodeTarget.resolveTemplate("graphId", graphId)
                .resolveTemplate("nodeId", nodeId)
                .request()
                .put(Entity.json(node));
        checkResponse(response);
        return response;
    }

    public Response deleteNode(long graphId, long nodeId) throws VerifyClientException, ResponseProcessingException, ProcessingException {
        Response response = nodeTarget.resolveTemplate("graphId", graphId)
                .resolveTemplate("nodeId", nodeId)
                .request()
                .delete();
        checkResponse(response);
        return response;
    }

    public Response createNeighbour(long graphId, long nodeId, Neighbour neighbour) throws VerifyClientException, ResponseProcessingException, ProcessingException {
        Response response = neighboursTarget.resolveTemplate("graphId", graphId)
                .resolveTemplate("nodeId", nodeId)
                .request()
                .post(Entity.json(neighbour));
        checkResponse(response);
        return response;
    }

    public Response retrieveNeighbour(long graphId, long nodeId, long neighbourId) throws VerifyClientException, ProcessingException {
        Response response = neighbourTarget.resolveTemplate("graphId", graphId)
                .resolveTemplate("nodeId", nodeId)
                .resolveTemplate("neighbourId", neighbourId)
                .request()
                .get();
        checkResponse(response);
        return response;
    }

    public Response updateNeighbour(long graphId, long nodeId, long neighbourId,
            Neighbour neighbour) throws VerifyClientException, ResponseProcessingException, ProcessingException {
        Response response = neighbourTarget.resolveTemplate("graphId", graphId)
                .resolveTemplate("nodeId", nodeId)
                .resolveTemplate("neighbourId", neighbourId)
                .request()
                .put(Entity.json(neighbour));
        checkResponse(response);
        return response;
    }

    public Response deleteNeighbour(long graphId, long nodeId, long neighbourId) throws VerifyClientException, ResponseProcessingException, ProcessingException {
        Response response = neighbourTarget.resolveTemplate("graphId", graphId)
                .resolveTemplate("nodeId", nodeId)
                .resolveTemplate("neighbourId", neighbourId)
                .request()
                .delete();
        checkResponse(response);
        return response;
    }

    public Verification getReachability(long graphId, String source, String destination) throws VerifyClientException, ProcessingException{
        Response response = reachabilityTarget.resolveTemplate("graphId", graphId)
                .queryParam("source", source)
                .queryParam("destination", destination)
                .queryParam("type", "reachability")
                .request()
                .get();
        checkResponse(response);
        try{
            Verification verification = response.readEntity(Verification.class);
            return verification;
        }
        catch (ProcessingException e) {
            throw new VerifyClientException("the content of the message cannot be mapped to an entity of the 'Verification': "
                    + e.getMessage());
        }
        catch (IllegalStateException e) {
            throw new VerifyClientException("the 'Verification' entity is not backed by an input stream or the original entity input stream has already been consumed without buffering the entity data prior consuming: "
                    + e.getMessage());
        }
    }

    public Verification getIsolation(long graphId, String source, String destination, String middlebox) throws VerifyClientException, ProcessingException{
        Response response = isolationTarget.resolveTemplate("graphId", graphId)
                .queryParam("source", source)
                .queryParam("destination", destination)
                .queryParam("middlebox", middlebox)
                .queryParam("type", "isolation")
                .request()
                .get();
        checkResponse(response);
        try{
            Verification verification = response.readEntity(Verification.class);
            return verification;
        }
        catch (ProcessingException e) {
            throw new VerifyClientException("the content of the message cannot be mapped to an entity of the 'Verification': "
                    + e.getMessage());
        }
        catch (IllegalStateException e) {
            throw new VerifyClientException("the 'Verification' entity is not backed by an input stream or the original entity input stream has already been consumed without buffering the entity data prior consuming: "
                    + e.getMessage());
        }
    }

    public Verification getTraversal(long graphId, String source, String destination, String middlebox) throws VerifyClientException, ProcessingException{
        Response response = traversalTarget.resolveTemplate("graphId", graphId)
                .queryParam("source", source)
                .queryParam("destination", destination)
                .queryParam("middlebox", middlebox)
                .queryParam("type", "traversal")
                .request()
                .get();
        checkResponse(response);
        try{
            Verification verification = response.readEntity(Verification.class);
            return verification;
        }
        catch (ProcessingException e) {
            throw new VerifyClientException("the content of the message cannot be mapped to an entity of the 'Verification': "
                    + e.getMessage());
        }
        catch (IllegalStateException e) {
            throw new VerifyClientException("the 'Verification' entity is not backed by an input stream or the original entity input stream has already been consumed without buffering the entity data prior consuming: "
                    + e.getMessage());
        }
    }

    @SuppressWarnings("unused")
    private static String deserializeString(File file) throws IOException {
        int len;
        char[] chr = new char[4096];
        final StringBuffer buffer = new StringBuffer();
        final FileReader reader = new FileReader(file);
        try {
            while ((len = reader.read(chr)) > 0) {
                buffer.append(chr, 0, len);
            }
        }
        finally {
            reader.close();
        }
        return buffer.toString();
    }

    public List<File> getFiles() {
        List<File> filesList = new ArrayList<File>();

        String folderString = System.getProperty("folder");
        File folder;
        if (folderString == null)
            folder = new File(System.getProperty("user.dir") + "/examples");
        else
            folder = new File(folderString);

        System.out.println("Folder set to " + folder.getAbsolutePath());

        File[] files = folder.listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".json");
            }
        });

        for (File f : files) {
            filesList.add(f);
        }

        return filesList;
    }

    public Graph addGraphFromFile(File file) throws JsonParseException, JsonMappingException, IOException, Exception {
        System.out.println("Parsing graph of file '" + file.getAbsolutePath() + "'...");
        Graph graph = new ObjectMapper().readValue(file, Graph.class);
        Response createGraphResponse = createGraph(graph);
        if (createGraphResponse.getStatus() != Status.CREATED.getStatusCode()) {
            throw new Exception("Creation of graph contained in file '"+ file.getAbsolutePath() + "' returned status "
                    + createGraphResponse.getStatus());
        }
        String responseString = createGraphResponse.readEntity(String.class);
        System.out.println("Response:");
        System.out.println(responseString);
        Graph response = new ObjectMapper().readValue(responseString, Graph.class);
        printGraph(response);
        return response;
    }

    public void printGraph(Graph graph) {
        System.out.println("Graph " + graph.getId());
        for (Node n : graph.getNodes().values()) {
            System.out.println("\tNode " + n.getId());
            System.out.println("\tName " + n.getName());
            System.out.println("\tFunctional type: " + n.getFunctional_type());
            for (Neighbour neighbour : n.getNeighbours().values()) {
                System.out.println("\t\tNeighbour " + neighbour.getId());
                System.out.println("\t\tName: " + neighbour.getName());
            }
        }
    }

    public Map<String, Graph> addGraphsFromFiles(List<File> files)throws JsonParseException, JsonMappingException, IOException,
    Exception {
        Map<String, Graph> graphs = new HashMap<String, Graph>();

        for (File f : files) {
            Graph graph = addGraphFromFile(f);
            graphs.put(f.getName(), graph);
        }

        for (Map.Entry<String, Graph> graph : graphs.entrySet()) {
            System.out.println(graph.getKey() + " -> graph " + graph.getValue().getId());
        }
        System.out.println("Graphs added");

        return graphs;
    }

    public static void main(String[] args) throws IOException, Exception {
        System.out.println("Adding graphs");

        VerifyClient verifyClient = new VerifyClient("http://localhost:8080/verigraph/api");

        List<File> files = verifyClient.getFiles();
        Map<String, Graph> graphs = verifyClient.addGraphsFromFiles(files);

        for (Graph g : graphs.values()) {
            Response response = verifyClient.retrieveGraph(g.getId());
            String responseString = response.readEntity(String.class);

            System.out.println("Response");
            System.out.println(responseString);
            Graph graph = new ObjectMapper().readValue(responseString, Graph.class);
            System.out.println("Read graph " + graph.getId());
            System.out.println(response.getStatus());
        }

        Graph graph = graphs.get("budapest_sat.json");
        System.out.println("graphId set to " + graph.getId());
        System.out.println("Getting reachability from 'user1' to 'websever' in 'budapest' graph (expecting SAT)...");
        Verification verification = verifyClient.getReachability(graph.getId(), "user1", "webserver");
        System.out.println(verification.getResult());

        graph = graphs.get("budapest_unsat.json");
        System.out.println("graphId set to " + graph.getId());
        System.out.println("Getting reachability from 'user1' to 'websever' in 'budapest' graph (expecting UNSAT)...");
        verification = verifyClient.getReachability(graph.getId(), "user1", "webserver");
        System.out.println(verification.getResult());

    }

}