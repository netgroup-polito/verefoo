package it.polito.verefoo.test.rest;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Test;


public class TestRestNeighbours {

	
	final String target = "http://127.0.0.1:8085/verefoo/adp/graphs";

	@Test
	public void testAddNeighbour() {
		
		String neigh = " <neighbour name=\"10.0.0.2\"/>";
		Response created = createGraph();		
		Response res = ClientBuilder.newClient()
				.target(created.getLocation() + "/nodes/10.0.0.1/neighbours")
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.post(Entity.entity(neigh,MediaType.APPLICATION_XML));
		assertEquals(Status.NO_CONTENT.getStatusCode(), res.getStatus());
		
	}
	
	
	@Test
	public void testAddNotExistentNeighbour() {
		
		String neigh = " <neighbour name=\"10.0.0.88\"/>";
		Response created = createGraph();		
		Response res = ClientBuilder.newClient()
				.target(created.getLocation() + "/nodes/10.0.0.1/neighbours")
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.post(Entity.entity(neigh,MediaType.APPLICATION_XML));
		assertEquals(Status.NOT_FOUND.getStatusCode(), res.getStatus());
		
	}
	
	
	@Test
	public void testAddConflictNeighbour() {
		
		String neigh = " <neighbour name=\"30.0.0.1\"/>";
		Response created = createGraph();		
		Response res = ClientBuilder.newClient()
				.target(created.getLocation() + "/nodes/10.0.0.1/neighbours")
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.post(Entity.entity(neigh,MediaType.APPLICATION_XML));
		assertEquals(Status.CONFLICT.getStatusCode(), res.getStatus());
		
	}
	
	
	@Test
	public void testDeleteNeighbour() {
		
		Response created = createGraph();		
		Response res = ClientBuilder.newClient()
				.target(created.getLocation() + "/nodes/10.0.0.1/neighbours")
				.queryParam("neighbour", "30.0.0.1")
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.delete();
		assertEquals(Status.NO_CONTENT.getStatusCode(), res.getStatus());
		
	}
	
	@Test
	public void testDeleteNotExistentNeighbour() {
		
		Response created = createGraph();		
		Response res = ClientBuilder.newClient()
				.target(created.getLocation() + "/nodes/10.0.0.1/neighbours")
				.queryParam("neighbour", "30.0.0.8")
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
}
