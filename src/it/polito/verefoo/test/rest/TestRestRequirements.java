package it.polito.verefoo.test.rest;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Test;

public class TestRestRequirements {

	final String target = "http://127.0.0.1:8085/verefoo/adp/requirements";
	
	@Test
	public void testCreateRequirementsSet() {
		
		Response res = createRequirementsSet();	
		assertEquals(Status.CREATED.getStatusCode(), res.getStatus());
		
	}
	
	@Test
	public void testGetRequirementsSets() {
		
		for(int i = 0; i < 3; i++) createRequirementsSet();	
		Response res = ClientBuilder.newClient()
				.target(target)
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.get();
		assertEquals(Status.OK.getStatusCode(), res.getStatus());
		
	}
	
	@Test
	public void testGetAbsentRequirementsSets() {

		for(int i=0; i<3; i++) createRequirementsSet();	
		testDeleteAbsentRequirementsSets();
		Response res = ClientBuilder.newClient()
					.target(target)
					.request(MediaType.APPLICATION_XML)
					.accept(MediaType.APPLICATION_XML)
					.get();
		assertEquals(Status.NOT_FOUND.getStatusCode(), res.getStatus());
	}
	
	@Test
	public void testDeleteRequirementsSets() {
		
		for(int i=0; i<3; i++) createRequirementsSet();	
		Response res = ClientBuilder.newClient()
				.target(target)
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.delete();
		assertEquals(Status.NO_CONTENT.getStatusCode(), res.getStatus());
	}
	
	
	@Test
	public void testDeleteAbsentRequirementsSets() {
		
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
	public void testGetRequirementsSet() {

		Response created = createRequirementsSet();
		Response res = ClientBuilder.newClient()
					.target(created.getLocation())
					.request(MediaType.APPLICATION_XML)
					.accept(MediaType.APPLICATION_XML)
					.get();
		assertEquals(Status.OK.getStatusCode(), res.getStatus());
	}
	
	
	@Test
	public void testUpdateRequirementsSet() {

		Response created = createRequirementsSet();
		Response res = ClientBuilder.newClient()
					.target(created.getLocation())
					.request(MediaType.APPLICATION_XML)
					.accept(MediaType.APPLICATION_XML)
					.put(Entity.entity("<PropertyDefinition />",MediaType.APPLICATION_XML));
		assertEquals(Status.NO_CONTENT.getStatusCode(), res.getStatus());
	}
	
	
	@Test
	public void testDeleteGetRequirementsSet() {

		Response created = createRequirementsSet();
		Response res = ClientBuilder.newClient()
					.target(created.getLocation())
					.request(MediaType.APPLICATION_XML)
					.accept(MediaType.APPLICATION_XML)
					.delete();
		assertEquals(Status.NO_CONTENT.getStatusCode(), res.getStatus());
	}
	
	
	@Test
	public void testDeleteAbsentGetRequirementsSet() {

		Response created = createRequirementsSet();
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
	
	private Response createRequirementsSet() {
		String req = " <PropertyDefinition>\r\n" + 
				"		<Property graph=\"0\" name=\"IsolationProperty\" src=\"10.0.0.1\" dst=\"20.0.0.1\"/>\r\n" + 
				"		<Property graph=\"0\" name=\"IsolationProperty\" src=\"10.0.0.2\" dst=\"20.0.0.1\"/> 		 				\r\n" + 
				"  </PropertyDefinition>";
		
		return ClientBuilder.newClient()
					.target(target)
					.request(MediaType.APPLICATION_XML)
					.accept(MediaType.APPLICATION_XML)
					.post(Entity.entity(req,MediaType.APPLICATION_XML));
	}
	
}
