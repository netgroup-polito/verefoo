/*******************************************************************************
 * Copyright (c) 2017 Politecnico di Torino and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package it.polito.verigraph.grpc.test;

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

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.runners.MethodSorters;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import it.polito.verigraph.grpc.GraphGrpc;
import it.polito.verigraph.grpc.NewGraph;
import it.polito.verigraph.grpc.NodeGrpc;
import it.polito.verigraph.client.VerifyClientException;
import it.polito.verigraph.grpc.client.Client;
import it.polito.verigraph.grpc.server.Service;

/**
 * Unit tests for gRPC project.
 * For testing concurrency on server side.
 */
@RunWith(JUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MultiThreadTest {
    private Service server;
    private Client client;

    @Before
    public void setUp() throws Exception {
        client = new Client("localhost" , 50051);
        server = new Service(50051);

        server.start();
    }

    @After
    public void tearDown() throws Exception {
        server.stop();
        client.shutdown();
    }

    private void testUpdateGraphStatus(final int threadCount) throws InterruptedException, ExecutionException, JsonParseException, JsonMappingException, IOException, VerifyClientException {
        GraphGrpc retrieveGraphResponse =client.getGraph(1);

        UpdateGraph task = new UpdateGraph(client, 1, retrieveGraphResponse);

        List<MultiThreadTest.UpdateGraph> tasks = Collections.nCopies(threadCount, task);
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        List<Future<NewGraph>> futures = executorService.invokeAll(tasks);
        List<Boolean> resultList = new ArrayList<Boolean>(futures.size());

        // Check for exceptions
        for (Future<NewGraph> future : futures) {
            // Throws an exception if an exception was thrown by the task.
            resultList.add(future.get().getSuccess());
        }
        // Validate the dimensions
        Assert.assertEquals(threadCount, futures.size());

        List<Boolean> expectedList = new ArrayList<Boolean>(threadCount);
        for (int i = 1; i <= threadCount; i++) {
            expectedList.add(true);
        }
        // Validate expected results
        Assert.assertEquals(expectedList, resultList);
    }

    private void testUpdateGraph(final int threadCount) throws Exception {
        GraphGrpc retrieveGraph = client.getGraph(2L);

        NodeGrpc nodeToEdit = Client.createNodeGrpc("client",
                "endpoint",
                null,
                Client.createConfigurationGrpc(null, null, "client", ""));

        GraphGrpc graphToUpdate = GraphGrpc.newBuilder(retrieveGraph).addNode(nodeToEdit).build();

        String graphAsString = graphToUpdate.toString();

        UpdateGraph task = new UpdateGraph(client, 2, graphToUpdate);

        List<MultiThreadTest.UpdateGraph> tasks = Collections.nCopies(threadCount, task);
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        List<Future<NewGraph>> futures = executorService.invokeAll(tasks);
        List<String> resultList = new ArrayList<String>(futures.size());

        // Check for exceptions
        for (Future<NewGraph> future : futures) {
            // Throws an exception if an exception was thrown by the task.
            GraphGrpc graphReceived = future.get().getGraph();
            NodeGrpc node = NodeGrpc.newBuilder(graphReceived.getNode(0)).setId(0).build();
            GraphGrpc graph = GraphGrpc.newBuilder(graphReceived).setNode(0, node).build();
            resultList.add(graph.toString());
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

    private void testCreateGraphStatus(final int threadCount) throws InterruptedException, ExecutionException, JsonParseException, JsonMappingException, IOException {

        GraphGrpc graph = GraphGrpc.newBuilder().build();

        CreateGraph task = new CreateGraph(client, graph);

        List<MultiThreadTest.CreateGraph> tasks = Collections.nCopies(threadCount, task);
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);

        List<Future<Boolean>> futures = executorService.invokeAll(tasks);
        List<Boolean> resultList = new ArrayList<Boolean>(futures.size());

        // Check for exceptions
        for (Future<Boolean> future : futures) {
            // Throws an exception if an exception was thrown by the task.
            resultList.add(future.get());
        }
        // Validate the IDs
        Assert.assertEquals(threadCount, futures.size());

        List<Boolean> expectedList = new ArrayList<Boolean>(threadCount);
        for (int i = 1; i <= threadCount; i++) {
            expectedList.add(true);
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
    public void updateGraphResponseCheck() throws Exception {
        testUpdateGraph(16);
    }

    @Test
    public void createGraphStatusCheck() throws JsonParseException, JsonMappingException, InterruptedException, ExecutionException, IOException {
        testCreateGraphStatus(8);
    }

    class UpdateGraph implements Callable<NewGraph> {

        private Client verifyClient;

        private int graphId;

        private GraphGrpc graph;

        public UpdateGraph(Client verifyClient, int graphId, GraphGrpc graph) {
            this.graphId = graphId;
            this.graph = graph;
            this.verifyClient = verifyClient;
        }

        @Override
        public NewGraph call() throws Exception {
            Thread.sleep(randInt(0,2000));
            return this.verifyClient.updateGraph(this.graphId, this.graph);
        }
    }

    class CreateGraph implements Callable<Boolean> {

        private Client verifyClient;

        private GraphGrpc graph;

        public CreateGraph(Client verifyClient, GraphGrpc graph) {
            this.graph = graph;
            this.verifyClient = verifyClient;
        }

        @Override
        public Boolean call() throws Exception {
            Thread.sleep(randInt(0,2000));
            return this.verifyClient.createGraph(this.graph).getSuccess();
        }

    }

}
