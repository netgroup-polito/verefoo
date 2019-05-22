package it.polito.verefoo.test.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Test;

public class TestRestSimulation {

	final String target = "http://127.0.0.1:8085/verefoo/adp/simulations";
	
	@Test
	public void testRunSimulationByNFV() {
		try {
			String nfv=java.nio.file.Files.lines(Paths.get("./testfile/FWCorrectness/FWCorrect03.xml")).collect(Collectors.joining("\n"));
			Response res = ClientBuilder.newClient()
					.target(target)
					.request(MediaType.APPLICATION_XML)
					.accept(MediaType.APPLICATION_XML)
					.post(Entity.entity(nfv,MediaType.APPLICATION_XML));
			assertEquals(Status.CREATED.getStatusCode(), res.getStatus());
		} catch (IOException e) {
			fail(e.toString());
		}
	}
	
	
	
	@Test
	public void testRunSimulationByParams() {
		
		String graph = "<graph id=\"0\">\r\n" + 
				"     <node functional_type=\"WEBCLIENT\" name=\"10.0.0.1\">\r\n" + 
				"        <neighbour name=\"30.0.0.1\"/>\r\n" + 
				"        <configuration description=\"A simple description\" name=\"confA\">\r\n" + 
				"          <webclient nameWebServer=\"20.0.0.1\"/>\r\n" + 
				"        </configuration>\r\n" + 
				"      </node>\r\n" + 
				"      <node functional_type=\"WEBCLIENT\" name=\"10.0.0.2\">\r\n" + 
				"        <neighbour name=\"30.0.0.1\"/>\r\n" + 
				"        <configuration description=\"A simple description\" name=\"confA\">\r\n" + 
				"          <webclient nameWebServer=\"20.0.0.1\"/>\r\n" + 
				"        </configuration>\r\n" + 
				"      </node>\r\n" + 
				"      \r\n" + 
				"      <node functional_type=\"FIREWALL\" name=\"30.0.0.1\">\r\n" + 
				"        <neighbour name=\"10.0.0.1\"/>\r\n" + 
				"        <neighbour name=\"10.0.0.2\"/>\r\n" + 
				"		<neighbour name=\"20.0.0.1\"/>\r\n" + 
				"        <configuration description=\"A simple description\" name=\"conf1\">\r\n" + 
				"            <firewall defaultAction=\"ALLOW\" />\r\n" + 
				"        </configuration>\r\n" + 
				"      </node>\r\n" + 
				"      <node functional_type=\"WEBSERVER\" name=\"20.0.0.1\">\r\n" + 
				"        <neighbour name=\"30.0.0.1\"/>\r\n" + 
				"        <configuration description=\"A simple description\" name=\"confB\">\r\n" + 
				"          <webserver>\r\n" + 
				"          	<name>b</name>\r\n" + 
				"          </webserver>\r\n" + 
				"        </configuration>\r\n" + 
				"      </node>\r\n" + 
				"    </graph>";
		
		Response respGraph = ClientBuilder.newClient()
					.target("http://127.0.0.1:8085/verefoo/adp/graphs")
					.request(MediaType.APPLICATION_XML)
					.accept(MediaType.APPLICATION_XML)
					.post(Entity.entity(graph,MediaType.APPLICATION_XML));
		
		
		String requirements = "<PropertyDefinition>\r\n" + 
				"		<Property graph=\"0\" name=\"IsolationProperty\" src=\"10.0.0.1\" dst=\"20.0.0.1\"/>\r\n" + 
				"		<Property graph=\"0\" name=\"IsolationProperty\" src=\"10.0.0.2\" dst=\"20.0.0.1\"/> 		 				\r\n" + 
				"  </PropertyDefinition>";
		
		Response respReq = ClientBuilder.newClient()
					.target("http://127.0.0.1:8085/verefoo/adp/requirements")
					.request(MediaType.APPLICATION_XML)
					.accept(MediaType.APPLICATION_XML)
					.post(Entity.entity(requirements,MediaType.APPLICATION_XML));
		
		String[] part1 = respGraph.getLocation().toString().split("/");
		String[] part2 = respReq.getLocation().toString().split("/");

		Response res = ClientBuilder.newClient()
				.target(target)
				.queryParam("gid", part1[part1.length-1])
				.queryParam("rid", part2[part2.length-1])
				.request(MediaType.TEXT_PLAIN)
				.accept(MediaType.APPLICATION_XML)
				.post(null);
		assertEquals(Status.CREATED.getStatusCode(), res.getStatus());
		
	}
	
	
	
	@Test
	public void testRunSimulationWithErrror() {

		String nfv="<NFV/>";
		Response res = ClientBuilder.newClient()
				.target(target)
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.post(Entity.entity(nfv,MediaType.APPLICATION_XML));
		assertEquals(Status.BAD_REQUEST.getStatusCode(), res.getStatus());

	}
	
	
	@Test
	public void testGetSimulationResult() {

		try {
			String nfv=java.nio.file.Files.lines(Paths.get("./testfile/FWCorrectness/FWCorrect03.xml")).collect(Collectors.joining("\n"));
			Response res1 = ClientBuilder.newClient()
					.target(target)
					.request(MediaType.APPLICATION_XML)
					.accept(MediaType.APPLICATION_XML)
					.post(Entity.entity(nfv,MediaType.APPLICATION_XML));
			assertEquals(Status.CREATED.getStatusCode(), res1.getStatus());
			Response res2 = ClientBuilder.newClient()
					.target(res1.getLocation())
					.request(MediaType.APPLICATION_XML)
					.accept(MediaType.APPLICATION_XML)
					.get();
			assertEquals(Status.OK.getStatusCode(), res2.getStatus());
		} catch (IOException e) {
			fail(e.toString());
		}

	}
	
	
	@Test
	public void testGetAbsentSimulationResult() {

		try {
			String nfv=java.nio.file.Files.lines(Paths.get("./testfile/FWCorrectness/FWCorrect03.xml")).collect(Collectors.joining("\n"));
			Response res1 = ClientBuilder.newClient()
					.target(target)
					.request(MediaType.APPLICATION_XML)
					.accept(MediaType.APPLICATION_XML)
					.post(Entity.entity(nfv,MediaType.APPLICATION_XML));
			assertEquals(Status.CREATED.getStatusCode(), res1.getStatus());
			Response res2 = ClientBuilder.newClient()
					.target(res1.getLocation() + "1")
					.request(MediaType.APPLICATION_XML)
					.accept(MediaType.APPLICATION_XML)
					.get();
			assertEquals(Status.NOT_FOUND.getStatusCode(), res2.getStatus());
		} catch (IOException e) {
			fail(e.toString());
		}

	}
	
	
	
}
