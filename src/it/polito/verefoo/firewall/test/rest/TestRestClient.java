package it.polito.verefoo.firewall.test.rest;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Test;

/**
 * 
 * This class tests the connectivity of the client to the web server
 *
 */
public class TestRestClient {

	final String target = "http://127.0.0.1:8085/verefoo";

	@Test
	public void testdeploymentService() {
	
		Response res = ClientBuilder.newClient()
					.target(target)
					.request(MediaType.TEXT_PLAIN)
					.accept(MediaType.TEXT_PLAIN)
					.get();
		assertEquals(Status.OK.getStatusCode(), res.getStatus());
		

	}
	
}
