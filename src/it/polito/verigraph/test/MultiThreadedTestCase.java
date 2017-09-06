/*******************************************************************************
 * Copyright (c) 2017 Politecnico di Torino and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package it.polito.verigraph.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.ws.rs.core.Response;

import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.polito.verigraph.client.VerifyClient;
import it.polito.verigraph.client.VerifyClientException;
import it.polito.verigraph.model.Configuration;
import it.polito.verigraph.model.Graph;
import it.polito.verigraph.model.Node;

public class MultiThreadedTestCase {

    private void testUpdateGraphStatus(final int threadCount) throws InterruptedException, ExecutionException, JsonParseException, JsonMappingException, IOException, VerifyClientException {
        final VerifyClient verifyClient = new VerifyClient("http://localhost:8080/verigraph/api");

        Response retrieveGraphResponse = verifyClient.retrieveGraph(1);
        String responseString = retrieveGraphResponse.readEntity(String.class);
        System.out.println(responseString);

        Graph graph = new ObjectMapper().readValue(responseString, Graph.class);

        UpdateGraph task = new UpdateGraph(verifyClient, 1, graph);

        List<MultiThreadedTestCase.UpdateGraph> tasks = Collections.nCopies(threadCount, task);
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        List<Future<Response>> futures = executorService.invokeAll(tasks);
        List<Integer> resultList = new ArrayList<Integer>(futures.size());

        // Check for exceptions
        for (Future<Response> future : futures) {
            // Throws an exception if an exception was thrown by the task.
            resultList.add(future.get().getStatus());
        }
        // Validate the dimensions
        Assert.assertEquals(threadCount, futures.size());

        List<Integer> expectedList = new ArrayList<Integer>(threadCount);
        for (int i = 1; i <= threadCount; i++) {
            expectedList.add(200);
        }
        // Validate expected results
        Assert.assertEquals(expectedList, resultList);
    }

    private void testUpdateGraph(final int threadCount) throws InterruptedException, ExecutionException, JsonParseException, JsonMappingException, IOException, VerifyClientException {
        final VerifyClient verifyClient = new VerifyClient("http://localhost:8080/verigraph/api");

        Response retrieveGraphResponse = verifyClient.retrieveGraph(2L);
        String responseString = retrieveGraphResponse.readEntity(String.class);
        System.out.println(responseString);

        Graph graph = new ObjectMapper().readValue(responseString, Graph.class);

        Node nodeToEdit = graph.getNodes().get(1L);
        nodeToEdit.setFunctional_type("endpoint");
        nodeToEdit.setConfiguration(new Configuration(nodeToEdit.getName(), "", new ObjectMapper().createArrayNode()));

        String graphAsString = new ObjectMapper().writeValueAsString(graph);
        System.out.println(graphAsString);

        UpdateGraph task = new UpdateGraph(verifyClient, 2, graph);

        List<MultiThreadedTestCase.UpdateGraph> tasks = Collections.nCopies(threadCount, task);
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        List<Future<Response>> futures = executorService.invokeAll(tasks);
        List<String> resultList = new ArrayList<String>(futures.size());

        // Check for exceptions
        for (Future<Response> future : futures) {
            // Throws an exception if an exception was thrown by the task.
            resultList.add(future.get().readEntity(String.class));
        }
        // Validate dimensions
        Assert.assertEquals(threadCount, futures.size());

        List<String> expectedList = new ArrayList<String>(threadCount);
        for (int i = 1; i <= threadCount; i++) {
            expectedList.add(graphAsString);
        }
        // Validate expected results
        Assert.assertEquals(expectedList, resultList);
    }

    private void testCreateGraphStatus(final int threadCount, Graph graph) throws InterruptedException, ExecutionException, JsonParseException, JsonMappingException, IOException {
        final VerifyClient verifyClient = new VerifyClient("http://localhost:8080/verigraph/api");

        CreateGraph task = new CreateGraph(verifyClient, graph);

        List<MultiThreadedTestCase.CreateGraph> tasks = Collections.nCopies(threadCount, task);
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        List<Future<Response>> futures = executorService.invokeAll(tasks);
        List<Integer> resultList = new ArrayList<Integer>(futures.size());

        // Check for exceptions
        for (Future<Response> future : futures) {
            // Throws an exception if an exception was thrown by the task.
            resultList.add(future.get().getStatus());
        }
        // Validate the IDs
        Assert.assertEquals(threadCount, futures.size());

        List<Integer> expectedList = new ArrayList<Integer>(threadCount);
        for (int i = 1; i <= threadCount; i++) {
            expectedList.add(201);
        }
        // Validate expected results
        Assert.assertEquals(expectedList, resultList);
    }

    private int randInt(int min, int max){
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return randomNum;
    }

    @Test
    public void updateGraphStatusCheck() throws InterruptedException, ExecutionException, JsonParseException, JsonMappingException, IOException, VerifyClientException {
        testUpdateGraphStatus(64);
    }

    @Test
    public void updateGraphResponseCheck() throws InterruptedException, ExecutionException, JsonParseException, JsonMappingException, IOException, VerifyClientException {
        testUpdateGraph(16);
    }

    @Test
    public void createGraphStatusCheck() throws JsonParseException, JsonMappingException, InterruptedException, ExecutionException, IOException {
        Graph graph = new Graph();
        testCreateGraphStatus(8, graph);
    }

    class UpdateGraph implements Callable<Response> {

        private VerifyClient verifyClient;

        private int graphId;

        private Graph graph;

        public UpdateGraph(VerifyClient verifyClient, int graphId, Graph graph) {
            this.graphId = graphId;
            this.graph = graph;
            this.verifyClient = verifyClient;
        }

        @Override
        public Response call() throws Exception {
            Thread.sleep(randInt(0,2000));
            Response updateGraphResponse = this.verifyClient.updateGraph(this.graphId, this.graph);
            return updateGraphResponse;
        }
    }

    class CreateGraph implements Callable<Response> {

        private VerifyClient verifyClient;

        private Graph graph;

        public CreateGraph(VerifyClient verifyClient, Graph graph) {
            this.graph = graph;
            this.verifyClient = verifyClient;
        }

        @Override
        public Response call() throws Exception {
            Thread.sleep(randInt(0,2000));
            return this.verifyClient.createGraph(this.graph);
        }

    }
}