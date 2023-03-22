package it.polito.verefoo.firewall.test.rest;


import static org.junit.Assert.assertEquals;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.Test;

/**
 * 
 * This class tests the generation and the correctness of firewalls scripts
 *
 */

public class TestRestFirewallScript {
	final String target = "http://127.0.0.1:8085/verefoo/fwd/deploy";

	@Test 
	public void testGetFortinetConfiguration1() {		
		setUp();
		Response res2 = ClientBuilder.newClient()
				.target(target+"/getFortinet/1")
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_OCTET_STREAM)
				.get();
		
		assertEquals(Status.OK.getStatusCode(), res2.getStatus());
		cleanUp();
		
	}	
	
	@Test 
	public void testGetFortinetConfiguration2() {		
		Response res2 = ClientBuilder.newClient()
				.target(target+"/getFortinet/1")
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_OCTET_STREAM)
				.get();
		
		assertEquals(Status.NOT_FOUND.getStatusCode(), res2.getStatus());
		cleanUp();
		
	}
	
	@Test 
	public void testGetIPFWConfiguration1() {		
		setUp();
		Response res2 = ClientBuilder.newClient()
				.target(target+"/getIpfw/1")
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_OCTET_STREAM)
				.get();
		
		assertEquals(Status.OK.getStatusCode(), res2.getStatus());
		cleanUp();
		
	}	
	
	@Test 
	public void testGetIPFWConfiguration2() {		
		Response res2 = ClientBuilder.newClient()
				.target(target+"/getIpfw/1")
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_OCTET_STREAM)
				.get();
		
		assertEquals(Status.NOT_FOUND.getStatusCode(), res2.getStatus());
		cleanUp();
		
	}
	
	
	@Test 
	public void testGetIptablesConfiguration1() {		
		setUp();
		Response res2 = ClientBuilder.newClient()
				.target(target+"/getIptables/1")
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_OCTET_STREAM)
				.get();
		
		assertEquals(Status.OK.getStatusCode(), res2.getStatus());
		cleanUp();
		
	}	
	
	@Test 
	public void testGetIptablesConfiguration2() {		
		Response res2 = ClientBuilder.newClient()
				.target(target+"/getIptables/1")
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_OCTET_STREAM)
				.get();
		
		assertEquals(Status.NOT_FOUND.getStatusCode(), res2.getStatus());
		cleanUp();
		
	}
	
	@Test 
	public void testGetOpenvswitchConfiguration1() {		
		setUp();
		Response res2 = ClientBuilder.newClient()
				.target(target+"/getOpenVswitch/1")
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_OCTET_STREAM)
				.get();
		
		assertEquals(Status.OK.getStatusCode(), res2.getStatus());
		cleanUp();
		
	}	
	
	@Test 
	public void testGetOpenvswitchConfiguration2() {		
		Response res2 = ClientBuilder.newClient()
				.target(target+"/getOpenVswitch/1")
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_OCTET_STREAM)
				.get();
		
		assertEquals(Status.NOT_FOUND.getStatusCode(), res2.getStatus());
		cleanUp();
		
	}
	
	
	@Test 
	public void testGetBPFConfiguration1() {		
		setUp();
		Response res2 = ClientBuilder.newClient()
				.target(target+"/getBpfFirewall/1")
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_OCTET_STREAM)
				.get();
		
		assertEquals(Status.OK.getStatusCode(), res2.getStatus());
		cleanUp();
		
	}	
	
	@Test 
	public void testGetBPFConfiguration2() {		
		Response res2 = ClientBuilder.newClient()
				.target(target+"/getBpfFirewall/1")
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_OCTET_STREAM)
				.get();
		
		assertEquals(Status.NOT_FOUND.getStatusCode(), res2.getStatus());
		cleanUp();
		
	}

	
	
private void cleanUp() {
	ClientBuilder.newClient()
	.target("http://127.0.0.1:8085/verefoo/fwd/nodes")
		.request(MediaType.APPLICATION_XML)
		.accept(MediaType.APPLICATION_XML)
		.delete();
}

private Response setUp() {
	
	String node = "<node functional_type=\"FIREWALL\" name=\"10.0.0.3\">\r\n" + 
			"        	<neighbour name=\"30.0.0.1\"/>\r\n" + 
			"        	<configuration description=\"A simple description\" name=\"confA\">\r\n" + 
			"         		<firewall defaultAction=\"ALLOW\">\r\n" + 
			"					<elements>\r\n" + 
			"               		<action>DENY</action>\r\n" + 
			"                   	<source>192.168.56.-1</source>\r\n" + 
			"                   	<destination>192.168.57.4</destination>\r\n" + 
			"                   	<protocol>ANY</protocol>\r\n" + 
			"                   	<src_port>*</src_port>\r\n" + 
			"                		<dst_port>*</dst_port>\r\n" + 
			"            		</elements>\r\n" + 
			"				</firewall>\r\n"+
			"       	</configuration>\r\n" + 
			"      </node>";
	
	return ClientBuilder.newClient()
			.target("http://127.0.0.1:8085/verefoo/fwd/nodes")
			.request(MediaType.APPLICATION_XML)
			.accept(MediaType.APPLICATION_XML)
			.post(Entity.entity(node,MediaType.APPLICATION_XML));
}
}