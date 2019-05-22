package it.polito.verefoo.test.rest;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Test;

public class TestRestFunctions {

	
	final String target = "http://127.0.0.1:8085/verefoo/adp/functions";

	@Test
	public void testCreateFirewall() {
		
		String function = "FIREWALL";
		Response res = ClientBuilder.newClient()
				.target(target)
				.request(MediaType.TEXT_PLAIN)
				.accept(MediaType.TEXT_PLAIN)
				.post(Entity.entity(function,MediaType.TEXT_PLAIN));
		if(Status.BAD_REQUEST.getStatusCode() == res.getStatus()) {
			ClientBuilder.newClient()
					.target(target + "/FIREWALL")
					.request(MediaType.TEXT_PLAIN)
					.accept(MediaType.TEXT_PLAIN)
					.delete();
			res = ClientBuilder.newClient()
					.target(target)
					.request(MediaType.TEXT_PLAIN)
					.accept(MediaType.TEXT_PLAIN)
					.post(Entity.entity(function,MediaType.TEXT_PLAIN));
			assertEquals(Status.CREATED.getStatusCode(), res.getStatus());
		}
		
	}
	
	

	@Test
	public void testCreateAntiSpam() {
		
		String function = "ANTISPAM";
		Response res = ClientBuilder.newClient()
				.target(target)
				.request(MediaType.TEXT_PLAIN)
				.accept(MediaType.TEXT_PLAIN)
				.post(Entity.entity(function,MediaType.TEXT_PLAIN));
		if(Status.BAD_REQUEST.getStatusCode() == res.getStatus()) {
			ClientBuilder.newClient()
					.target(target + "/ANTISPAM")
					.request(MediaType.TEXT_PLAIN)
					.accept(MediaType.TEXT_PLAIN)
					.delete();
			res = ClientBuilder.newClient()
					.target(target)
					.request(MediaType.TEXT_PLAIN)
					.accept(MediaType.TEXT_PLAIN)
					.post(Entity.entity(function,MediaType.TEXT_PLAIN));
			assertEquals(Status.CREATED.getStatusCode(), res.getStatus());
		}
		
	}
	
	
	@Test
	public void testCreateInvalidFunction() {
		
		String function = "WEB_TRAFFIC_MONITOR";
		Response res = ClientBuilder.newClient()
				.target(target)
				.request(MediaType.TEXT_PLAIN)
				.accept(MediaType.TEXT_PLAIN)
				.accept(MediaType.APPLICATION_XML)
				.post(Entity.entity(function,MediaType.TEXT_PLAIN));
		assertEquals(Status.BAD_REQUEST.getStatusCode(), res.getStatus());
	}
	
	
	@Test
	public void testDeleteFirewall() {
		
		String function = "FIREWALL";
		ClientBuilder.newClient()
				.target(target)
				.request(MediaType.TEXT_PLAIN)
				.accept(MediaType.TEXT_PLAIN)
				.post(Entity.entity(function,MediaType.TEXT_PLAIN));
		Response res = ClientBuilder.newClient()
				.target(target + "/" + function)
				.request(MediaType.TEXT_PLAIN)
				.accept(MediaType.TEXT_PLAIN)
				.accept(MediaType.APPLICATION_XML)
				.delete();
		assertEquals(Status.NO_CONTENT.getStatusCode(), res.getStatus());
		
	}
	
	@Test
	public void testDeleteInvalidFunction() {
		
		String function = "WEB_TRAFFIC_MONITOR";
		Response res = ClientBuilder.newClient()
				.target(target + "/" + function)
				.request(MediaType.TEXT_PLAIN)
				.accept(MediaType.TEXT_PLAIN)
				.accept(MediaType.APPLICATION_XML)
				.delete();
		assertEquals(Status.NOT_FOUND.getStatusCode(), res.getStatus());
		
	}
	
	
	@Test
	public void testGetFirewall() {
		
		String function = "FIREWALL";
		ClientBuilder.newClient()
				.target(target)
				.request(MediaType.TEXT_PLAIN)
				.accept(MediaType.TEXT_PLAIN)
				.post(Entity.entity(function,MediaType.TEXT_PLAIN));
		Response res = ClientBuilder.newClient()
				.target(target + "/" + function)
				.request(MediaType.TEXT_PLAIN)
				.accept(MediaType.TEXT_PLAIN)
				.accept(MediaType.APPLICATION_XML)
				.get();
		assertEquals(Status.OK.getStatusCode(), res.getStatus());
		
	}
	
	@Test
	public void testGetInvalidFunction() {
		
		String function = "WEB_TRAFFIC_MONITOR";
		Response res = ClientBuilder.newClient()
				.target(target + "/" + function)
				.request(MediaType.TEXT_PLAIN)
				.accept(MediaType.TEXT_PLAIN)
				.accept(MediaType.APPLICATION_XML)
				.get();
		assertEquals(Status.NOT_FOUND.getStatusCode(), res.getStatus());
		
	}
	
}
