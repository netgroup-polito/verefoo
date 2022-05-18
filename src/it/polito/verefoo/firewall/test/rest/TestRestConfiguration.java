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
 * This class tests all the methods regarding configuration updates
 *
 */

public class TestRestConfiguration {
	final String target = "http://127.0.0.1:8085/verefoo/fwd/nodes";

	
	
	@Test 
	public void testUpdateConfiguration1() {

		
		String configuration= 
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
				"       	</configuration>";
		
		Response res2 = ClientBuilder.newClient()
				.target(target+"/50/configuration")
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.put(Entity.entity(configuration,MediaType.APPLICATION_XML));
		
		assertEquals(Status.NOT_FOUND.getStatusCode(), res2.getStatus());
		
	}	

	@Test 
	public void testUpdateConfiguration2() {
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
		
		String configuration= 
				"        	<configuration description=\"A complex description\" name=\"confB\">\r\n" + 
				"         		<firewall defaultAction=\"DENY\">\r\n" + 
				"					<elements>\r\n" + 
				"               		<action>ALLOW</action>\r\n" + 
				"                   	<source>192.168.56.-1</source>\r\n" + 
				"                   	<destination>192.168.57.4</destination>\r\n" + 
				"                   	<protocol>ANY</protocol>\r\n" + 
				"                   	<src_port>*</src_port>\r\n" + 
				"                		<dst_port>*</dst_port>\r\n" + 
				"            		</elements>\r\n" + 
				"				</firewall>\r\n"+
				"       	</configuration>";
		
		Response res1 = ClientBuilder.newClient()
				.target(target)
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.post(Entity.entity(node,MediaType.APPLICATION_XML));
		Response res2 = ClientBuilder.newClient()
				.target(res1.getLocation()+"/configuration")
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.put(Entity.entity(configuration,MediaType.APPLICATION_XML));
		
		assertEquals(Status.NO_CONTENT.getStatusCode(), res2.getStatus());
		cleanUp();
		
	}
	
	@Test 
	public void testUpdateConfiguration3() {
		String node = "<node functional_type=\"FIREWALL\" name=\"10.0.0.3\">\r\n" + 
				"        	<neighbour name=\"30.0.0.1\"/>\r\n" + 
				"        	<configuration description=\"A simple description\" name=\"confA\">\r\n" + 
				"       	</configuration>\r\n" + 
				"      </node>";
		
		String configuration= 
				"        	<configuration description=\"A complex description\" name=\"confB\">\r\n" + 
				"         		<firewall defaultAction=\"DENY\">\r\n" + 
				"					<elements>\r\n" + 
				"               		<action>ALLOW</action>\r\n" + 
				"                   	<source>192.168.56.-1</source>\r\n" + 
				"                   	<destination>192.168.57.4</destination>\r\n" + 
				"                   	<protocol>ANY</protocol>\r\n" + 
				"                   	<src_port>*</src_port>\r\n" + 
				"                		<dst_port>*</dst_port>\r\n" + 
				"            		</elements>\r\n" + 
				"				</firewall>\r\n"+
				"       	</configuration>";
		
		Response res1 = ClientBuilder.newClient()
				.target(target)
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.post(Entity.entity(node,MediaType.APPLICATION_XML));
		Response res2 = ClientBuilder.newClient()
				.target(res1.getLocation()+"/configuration")
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.put(Entity.entity(configuration,MediaType.APPLICATION_XML));
		
		assertEquals(Status.NO_CONTENT.getStatusCode(), res2.getStatus());
		cleanUp();
		
	}
	
	@Test 
	public void testUpdateConfiguration4() {
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
		
		String configuration= 
				"        	<configuration description=\"A complex description\" name=\"confB\"/>";
		
		Response res1 = ClientBuilder.newClient()
				.target(target)
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.post(Entity.entity(node,MediaType.APPLICATION_XML));
		Response res2 = ClientBuilder.newClient()
				.target(res1.getLocation()+"/configuration")
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.put(Entity.entity(configuration,MediaType.APPLICATION_XML));
		
		assertEquals(Status.NO_CONTENT.getStatusCode(), res2.getStatus());
		cleanUp();
		
	}
	
private void cleanUp() {
	ClientBuilder.newClient()
	 .target(target)
		.request(MediaType.APPLICATION_XML)
		.accept(MediaType.APPLICATION_XML)
		.delete();
}
}