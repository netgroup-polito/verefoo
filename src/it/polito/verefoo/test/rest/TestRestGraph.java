package it.polito.verefoo.test.rest;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Test;

public class TestRestGraph {

	final String target = "http://127.0.0.1:8085/verefoo/adp/graphs";

	@Test
	public void testCreateGraph() {
		
		Response res = createGraph();	
		assertEquals(Status.CREATED.getStatusCode(), res.getStatus());
		
	}
	
	@Test
	public void testCreateBadGraph() {
		
		Response res = createBadGraph();	
		assertEquals(Status.BAD_REQUEST.getStatusCode(), res.getStatus());
		
	}
	
	
	@Test
	public void testDeleteGraphs() {
		
		for(int i=0; i<3; i++) createGraph();	
		Response res = ClientBuilder.newClient()
				.target(target)
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.delete();
		assertEquals(Status.NO_CONTENT.getStatusCode(), res.getStatus());
	}
	
	
	@Test
	public void testDeleteAbsentGraphs() {
		
		ClientBuilder.newClient()
				.target(target)
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.delete();
		Response res = ClientBuilder.newClient()
				.target(target)
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.delete();
		assertEquals(Status.NOT_FOUND.getStatusCode(), res.getStatus());
	}
	
	
	@Test
	public void testGetAbsentGraphs() {

		for(int i=0; i<3; i++) createGraph();	
		testDeleteAbsentGraphs();
		Response res = ClientBuilder.newClient()
					.target(target)
					.request(MediaType.APPLICATION_XML)
					.accept(MediaType.APPLICATION_XML)
					.get();
		assertEquals(Status.NOT_FOUND.getStatusCode(), res.getStatus());
	}
	
	
	
	@Test
	public void testGetGraph() {

		Response created = createGraph();
		Response res = ClientBuilder.newClient()
					.target(created.getLocation())
					.request(MediaType.APPLICATION_XML)
					.accept(MediaType.APPLICATION_XML)
					.get();
		assertEquals(Status.OK.getStatusCode(), res.getStatus());
	}
	
	
	@Test
	public void testUpdateGraph() {

		Response created = createGraph();
		Response res = ClientBuilder.newClient()
					.target(created.getLocation())
					.request(MediaType.APPLICATION_XML)
					.accept(MediaType.APPLICATION_XML)
					.put(Entity.entity("<graph></graph>",MediaType.APPLICATION_XML));
		assertEquals(Status.NO_CONTENT.getStatusCode(), res.getStatus());
	}
	
	
	@Test
	public void testDeleteGraph() {

		Response created = createGraph();
		Response res = ClientBuilder.newClient()
					.target(created.getLocation())
					.request(MediaType.APPLICATION_XML)
					.accept(MediaType.APPLICATION_XML)
					.delete();
		assertEquals(Status.NO_CONTENT.getStatusCode(), res.getStatus());
	}
	
	
	@Test
	public void testDeleteAbsentGraph() {

		Response created = createGraph();
		Response res = ClientBuilder.newClient()
					.target(created.getLocation())
					.request(MediaType.APPLICATION_XML)
					.accept(MediaType.APPLICATION_XML)
					.delete();
		res = ClientBuilder.newClient()
				.target(created.getLocation())
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.delete();
		assertEquals(Status.NOT_FOUND.getStatusCode(), res.getStatus());
	}
	
	
	
	private Response createGraph() {
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
		
		return ClientBuilder.newClient()
					.target(target)
					.request(MediaType.APPLICATION_XML)
					.accept(MediaType.APPLICATION_XML)
					.post(Entity.entity(graph,MediaType.APPLICATION_XML));
	}
	
	
	private Response createBadGraph() {
		String graph = "<graph id=\"0\">\r\n" + 
				"     <node functional_type=\"WEBCLIENT\" name=\"10.0.0.1\">\r\n" + 
				"        <neighbour name=\"30.0.0.1\"/>\r\n" + 
				"        <configuration description=\"A simple description\" name=\"confA\">\r\n" + 
				"          <webclient nameWebServer=\"20.0.0.1\"/>\r\n" + 
				"        </configuration>\r\n" + 
				"      </node>\r\n" + 
				"      <node functional_type=\"WEBCLIENT\" name=\"10.0.0.2\">\r\n" + 
				"        <neighbour name=\"30.0.0.1\"/>\r\n" + 
				"      \r\n" + 
				"      <node functional_type=\"WEBSERVER\" name=\"20.0.0.1\">\r\n" + 
				"        <neighbour name=\"30.0.0.1\"/>\r\n" + 
				"        <configuration description=\"A simple description\" name=\"confB\">\r\n" + 
				"          <webserver>\r\n" + 
				"          	<name>b</name>\r\n" + 
				"          </webserver>\r\n" + 
				"        </configuration>\r\n" + 
				"      </node>\r\n" + 
				"    </graph>";
		
		return ClientBuilder.newClient()
					.target(target)
					.request(MediaType.APPLICATION_XML)
					.accept(MediaType.APPLICATION_XML)
					.post(Entity.entity(graph,MediaType.APPLICATION_XML));
	}
}
