package it.polito.verefoo.test.rest;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Test;

public class TestRestProperties {

	final String target = "http://127.0.0.1:8085/verefoo/adp/requirements";
	
	@Test
	public void testCreateProperty() {
		
		String property = "<Property graph=\"0\" name=\"IsolationProperty\" src=\"10.0.0.3\" dst=\"20.0.0.1\"/>";
		Response created = createRequirementsSet();		
		Response res = ClientBuilder.newClient()
				.target(created.getLocation() + "/properties")
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.post(Entity.entity(property,MediaType.APPLICATION_XML));
		assertEquals(Status.CREATED.getStatusCode(), res.getStatus());
		
	}
	
	
	@Test
	public void testUpdateProperty() {
		
		String property1 = "<Property graph=\"0\" name=\"IsolationProperty\" src=\"10.0.0.3\" dst=\"20.0.0.1\"/>";
		String property2 = "<Property graph=\"0\" name=\"ReachabilityProperty\" src=\"10.0.0.3\" dst=\"20.0.0.1\"/>";
		Response created = createRequirementsSet();		
		Response res1 = ClientBuilder.newClient()
				.target(created.getLocation() + "/properties")
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.post(Entity.entity(property1,MediaType.APPLICATION_XML));
		Response res2 = ClientBuilder.newClient()
				.target(res1.getLocation())
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.put(Entity.entity(property2,MediaType.APPLICATION_XML));
		assertEquals(Status.NO_CONTENT.getStatusCode(), res2.getStatus());
		
	}
	
	
	@Test
	public void testUpdateAbsentProperty() {
		
		String property = "<Property graph=\"0\" name=\"IsolationProperty\" src=\"10.0.0.3\" dst=\"20.0.0.1\"/>";
		Response created = createRequirementsSet();		
		Response res = ClientBuilder.newClient()
				.target(created.getLocation() + "/properties/100")
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.put(Entity.entity(property,MediaType.APPLICATION_XML));
		assertEquals(Status.NOT_FOUND.getStatusCode(), res.getStatus());
		
	}
	
	
	@Test
	public void testDeleteProperty() {
		
		String property = "<Property graph=\"0\" name=\"IsolationProperty\" src=\"10.0.0.3\" dst=\"20.0.0.1\"/>";
		Response created = createRequirementsSet();		
		Response res1 = ClientBuilder.newClient()
				.target(created.getLocation() + "/properties")
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.post(Entity.entity(property,MediaType.APPLICATION_XML));
		Response res2 = ClientBuilder.newClient()
				.target(res1.getLocation())
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.delete();
		assertEquals(Status.NO_CONTENT.getStatusCode(), res2.getStatus());
		
	}
	
	
	@Test
	public void testDeleteAbsentProperty() {
		
		Response created = createRequirementsSet();		
		Response res = ClientBuilder.newClient()
				.target(created.getLocation() + "propertiess/100")
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.delete();
		assertEquals(Status.NOT_FOUND.getStatusCode(), res.getStatus());
		
	}
	
	
	@Test
	public void testGetProperty() {

		Response created = createRequirementsSet();		
		Response res = ClientBuilder.newClient()
				.target(created.getLocation() + "/properties/1")
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.get();
		assertEquals(Status.OK.getStatusCode(), res.getStatus());
		
	}
	
	
	@Test
	public void testGetAbsentProperty() {

		Response created = createRequirementsSet();		
		Response res = ClientBuilder.newClient()
				.target(created.getLocation() + "/propertys/100")
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.get();
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
