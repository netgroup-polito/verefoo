package it.polito.escape.verify.test;

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

import it.polito.escape.verify.client.VerifyClient;
import it.polito.escape.verify.model.Graph;

public class MultiThreadedTestCase {

	private void testUpdateGraphStatus(final int threadCount) throws InterruptedException, ExecutionException, JsonParseException, JsonMappingException, IOException {
		final VerifyClient verifyClient = new VerifyClient("http://localhost:8080/verify/api");
		Response retrieveGraphResponse = verifyClient.retrieveGraph(1);
		String responseString = retrieveGraphResponse.readEntity(String.class);
		System.out.println(responseString);
		Graph graph = new ObjectMapper().readValue(responseString, Graph.class);
		UpdateGraphTask task = new UpdateGraphTask(verifyClient, 1, graph);
		List<MultiThreadedTestCase.UpdateGraphTask> tasks = Collections.nCopies(threadCount, task);
		ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
		List<Future<Integer>> futures = executorService.invokeAll(tasks);
		List<Integer> resultList = new ArrayList<Integer>(futures.size());
		// Check for exceptions
		for (Future<Integer> future : futures) {
			// Throws an exception if an exception was thrown by the task.
			resultList.add(future.get());
		}
		// Validate the IDs
		Assert.assertEquals(threadCount, futures.size());
		List<Integer> expectedList = new ArrayList<Integer>(threadCount);
		for (int i = 1; i <= threadCount; i++) {
			expectedList.add(200);
		}
		Collections.sort(resultList);
		Assert.assertEquals(expectedList, resultList);
	}
	
	private void testUpdateGraph(final int threadCount) throws InterruptedException, ExecutionException, JsonParseException, JsonMappingException, IOException {
		final VerifyClient verifyClient = new VerifyClient("http://localhost:8080/verify/api");
		Response retrieveGraphResponse = verifyClient.retrieveGraph(1);
		String responseString = retrieveGraphResponse.readEntity(String.class);
		System.out.println(responseString);
		Graph graph = new ObjectMapper().readValue(responseString, Graph.class);
		graph.getNodes().get(1L).setFunctional_type("firewall");
		String graphAsString = new ObjectMapper().writeValueAsString(graph);
		UpdateGraph task = new UpdateGraph(verifyClient, 1, graph);
		List<MultiThreadedTestCase.UpdateGraph> tasks = Collections.nCopies(threadCount, task);
		ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
		List<Future<String>> futures = executorService.invokeAll(tasks);
		List<String> resultList = new ArrayList<String>(futures.size());
		// Check for exceptions
		for (Future<String> future : futures) {
			// Throws an exception if an exception was thrown by the task.
			resultList.add(future.get());
		}
		// Validate the IDs
		Assert.assertEquals(threadCount, futures.size());
		List<String> expectedList = new ArrayList<String>(threadCount);
		for (int i = 1; i <= threadCount; i++) {
			expectedList.add(graphAsString);
		}

		Assert.assertEquals(expectedList, resultList);
	}
	
	private void testCreateGraphStatus(final int threadCount, Graph graph) throws InterruptedException, ExecutionException, JsonParseException, JsonMappingException, IOException {
		final VerifyClient verifyClient = new VerifyClient("http://localhost:8080/verify/api");

		CreateGraphTask task = new CreateGraphTask(verifyClient, graph);
		
		List<MultiThreadedTestCase.CreateGraphTask> tasks = Collections.nCopies(threadCount, task);
		ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
		List<Future<Integer>> futures = executorService.invokeAll(tasks);
		List<Integer> resultList = new ArrayList<Integer>(futures.size());
		// Check for exceptions
		for (Future<Integer> future : futures) {
			// Throws an exception if an exception was thrown by the task.
			resultList.add(future.get());
		}
		// Validate the IDs
		Assert.assertEquals(threadCount, futures.size());
		List<Integer> expectedList = new ArrayList<Integer>(threadCount);
		for (int i = 1; i <= threadCount; i++) {
			expectedList.add(201);
		}
		Collections.sort(resultList);
		Assert.assertEquals(expectedList, resultList);
	}
	
	private int randInt(int min, int max){
		Random rand = new Random();
	    int randomNum = rand.nextInt((max - min) + 1) + min;
	    return randomNum;
	}

	@Test
	public void updateGraphStatusCheck() throws InterruptedException, ExecutionException, JsonParseException, JsonMappingException, IOException {
		testUpdateGraphStatus(64);
	}
	
	@Test
	public void updateGraphResponseCheck() throws InterruptedException, ExecutionException, JsonParseException, JsonMappingException, IOException {
		testUpdateGraph(16);
	}
	
	@Test
	public void createGraphStatusCheck() throws JsonParseException, JsonMappingException, InterruptedException, ExecutionException, IOException {
		Graph graph = new Graph();
		testCreateGraphStatus(8, graph);
	}

	class UpdateGraphTask implements Callable<Integer> {

		private VerifyClient	verifyClient;

		private int				graphId;

		private Graph			graph;

		public UpdateGraphTask(VerifyClient verifyClient, int graphId, Graph graph) {
			this.graphId = graphId;
			this.graph = graph;
			this.verifyClient = verifyClient;
		}

		@Override
		public Integer call() throws Exception {
			Thread.sleep(randInt(0,2000));
			return this.verifyClient.updateGraph(this.graphId, this.graph).getStatus();
		}
	}
	
	class UpdateGraph implements Callable<String> {

		private VerifyClient	verifyClient;

		private int				graphId;

		private Graph			graph;

		public UpdateGraph(VerifyClient verifyClient, int graphId, Graph graph) {
			this.graphId = graphId;
			this.graph = graph;
			this.verifyClient = verifyClient;
		}

		@Override
		public String call() throws Exception {
			Thread.sleep(randInt(0,2000));
			Response updateGraphResponse = this.verifyClient.updateGraph(this.graphId, this.graph);
			String updatedGraph = updateGraphResponse.readEntity(String.class);
			return updatedGraph;
		}
	}
	
	class CreateGraphTask implements Callable<Integer> {

		private VerifyClient	verifyClient;

		private Graph			graph;

		public CreateGraphTask(VerifyClient verifyClient, Graph graph) {
			this.graph = graph;
			this.verifyClient = verifyClient;
		}

		@Override
		public Integer call() throws Exception {
			Thread.sleep(randInt(0,2000));
			return this.verifyClient.createGraph(this.graph).getStatus();
		}
		
	}
}