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
 * This class tests all the methods regarding firewall field creation and update
 *
 */

public class TestRestFirewallPolicy {
	final String target = "http://127.0.0.1:8085/verefoo/fwd/nodes";

	@Test 
	public void testCreateFirewall1() {
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
		
		String firewall="         		<firewall defaultAction=\"ALLOW\">\r\n" + 
				"					<elements>\r\n" + 
				"               		<action>DENY</action>\r\n" + 
				"                   	<source>192.168.56.-1</source>\r\n" + 
				"                   	<destination>192.168.57.4</destination>\r\n" + 
				"                   	<protocol>ANY</protocol>\r\n" + 
				"                   	<src_port>*</src_port>\r\n" + 
				"                		<dst_port>*</dst_port>\r\n" + 
				"            		</elements>\r\n" +
				"					<elements>\r\n" + 
				"               		<action>DENY</action>\r\n" + 
				"                   	<source>192.168.56.-1</source>\r\n" + 
				"                   	<destination>192.168.57.4</destination>\r\n" + 
				"                   	<protocol>ANY</protocol>\r\n" + 
				"                   	<src_port>*</src_port>\r\n" + 
				"                		<dst_port>*</dst_port>\r\n" + 
				"            		</elements>\r\n" + 
				"				</firewall>";
		
		Response res1 = ClientBuilder.newClient()
				.target(target)
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.post(Entity.entity(node,MediaType.APPLICATION_XML));
		Response res2 = ClientBuilder.newClient()
				.target(res1.getLocation()+"/configuration")
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.post(Entity.entity(firewall,MediaType.APPLICATION_XML));
		
		assertEquals(Status.CREATED.getStatusCode(), res2.getStatus());
		cleanUp();
		
	}
	
	@Test 
	public void testCreateFirewall2() {
		String node = "<node functional_type=\"FIREWALL\" name=\"10.0.0.3\">\r\n" + 
				"        	<neighbour name=\"30.0.0.1\"/>\r\n" + 
				"        	<configuration description=\"A simple description\" name=\"confA\">\r\n" + 
				"         		<firewall defaultAction=\"ALLOW\"/>\r\n" + 
				"       	</configuration>\r\n" + 
				"      </node>";
		
		String firewall="  		<firewall defaultAction=\"DENY\">\r\n" + 
				"					<elements>\r\n" + 
				"               		<action>DENY</action>\r\n" + 
				"                   	<source>192.168.56.-1</source>\r\n" + 
				"                   	<destination>192.168.57.4</destination>\r\n" + 
				"                   	<protocol>ANY</protocol>\r\n" + 
				"                   	<src_port>*</src_port>\r\n" + 
				"                		<dst_port>*</dst_port>\r\n" + 
				"            		</elements>\r\n" + 
				"				</firewall>";
		
		Response res1 = ClientBuilder.newClient()
				.target(target)
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.post(Entity.entity(node,MediaType.APPLICATION_XML));
		Response res2 = ClientBuilder.newClient()
				.target(res1.getLocation()+"/configuration")
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.post(Entity.entity(firewall,MediaType.APPLICATION_XML));
		
		assertEquals(Status.CONFLICT.getStatusCode(), res2.getStatus());
		cleanUp();
		
	}
//	@Test 
//	public void testCreateFirewall3() {
//		String node = "<node functional_type=\"FIREWALL\" name=\"10.0.0.3\">\r\n" + 
//				"        	<neighbour name=\"30.0.0.1\"/>\r\n" + 
//				"        	<configuration description=\"A simple description\" name=\"confA\">\r\n" + 
//				"         		<firewall defaultAction=\"ALLOW\"/>\r\n" + 
//				"       	</configuration>\r\n" + 
//				"      </node>";
//		
//		String firewall="  		<firewall defaultAction=\"ALLO\">\r\n" + 
//				"					<elements>\r\n" + 
//				"               		<action>DENY</action>\r\n" + 
//				"                   	<source>192.168.56.-1</source>\r\n" + 
//				"                   	<destination>192.168.57.4</destination>\r\n" + 
//				"                   	<protocol>ANY</protocol>\r\n" + 
//				"                   	<src_port>*</src_port>\r\n" + 
//				"                		<dst_port>*</dst_port>\r\n" + 
//				"            		</elements>\r\n" + 
//				"				</firewall>";
//		
//		Response res1 = ClientBuilder.newClient()
//				.target(target)
//				.request(MediaType.APPLICATION_XML)
//				.accept(MediaType.APPLICATION_XML)
//				.post(Entity.entity(node,MediaType.APPLICATION_XML));
//		Response res2 = ClientBuilder.newClient()
//				.target(res1.getLocation()+"/configuration")
//				.request(MediaType.APPLICATION_XML)
//				.accept(MediaType.APPLICATION_XML)
//				.post(Entity.entity(firewall,MediaType.APPLICATION_XML));
//		
//		assertEquals(Status.BAD_REQUEST.getStatusCode(), res2.getStatus());
//		cleanUp();
//		
//	}
	
	
	@Test 
	public void testCreateFirewall3() {
		
		String firewall="  		<firewall defaultAction=\"DENY\">\r\n" + 
				"					<elements>\r\n" + 
				"               		<action>DENY</action>\r\n" + 
				"                   	<source>192.168.56.-1</source>\r\n" + 
				"                   	<destination>192.168.57.4</destination>\r\n" + 
				"                   	<protocol>ANY</protocol>\r\n" + 
				"                   	<src_port>*</src_port>\r\n" + 
				"                		<dst_port>*</dst_port>\r\n" + 
				"            		</elements>\r\n" + 
				"				</firewall>";
		
		Response res = ClientBuilder.newClient()
				.target(target+"/4/configuration")
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.post(Entity.entity(firewall,MediaType.APPLICATION_XML));
		
		assertEquals(Status.BAD_REQUEST.getStatusCode(), res.getStatus());
		cleanUp();
		
	}
	
	@Test 
	public void testCreatePolicy1() {
		String node = "<node functional_type=\"FIREWALL\" name=\"10.0.0.3\">\r\n" + 
				"        	<neighbour name=\"30.0.0.1\"/>\r\n" + 
				"        	<configuration description=\"A simple description\" name=\"confA\">\r\n" + 
				"         		<firewall defaultAction=\"ALLOW\"/>\r\n" + 
				"       	</configuration>\r\n" + 
				"      </node>";
		
		String policy=
				"					<elements>\r\n" + 
				"                   	<source>192.168.56.-1</source>\r\n" + 
				"                   	<destination>192.168.57.4</destination>\r\n" + 
				"                   	<protocol>ANY</protocol>\r\n" + 
				"                   	<src_port>*</src_port>\r\n" + 
				"                		<dst_port>*</dst_port>\r\n" + 
				"            		</elements>" ; 
		
		Response res1 = ClientBuilder.newClient()
				.target(target)
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.post(Entity.entity(node,MediaType.APPLICATION_XML));
		Response res2 = ClientBuilder.newClient()
				.target(res1.getLocation()+"/configuration/firewall")
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.post(Entity.entity(policy,MediaType.APPLICATION_XML));
		
		assertEquals(Status.BAD_REQUEST.getStatusCode(), res2.getStatus());
		cleanUp();
		
	}
	
	
	@Test 
	public void testCreatePolicy2() {
		String node = "<node functional_type=\"FIREWALL\" name=\"10.0.0.3\">\r\n" + 
				"        	<neighbour name=\"30.0.0.1\"/>\r\n" + 
				"        	<configuration description=\"A simple description\" name=\"confA\">\r\n" + 
				"         		<firewall defaultAction=\"ALLOW\"/>\r\n" + 
				"       	</configuration>\r\n" + 
				"      </node>";
		
		String policy=
				"					<elements>\r\n" + 
				"               		<action>DENY</action>\r\n" + 
				"                   	<source>192.168.56.-1</source>\r\n" + 
				"                   	<destination>192.168.57.4</destination>\r\n" + 
				"                   	<src_port>*</src_port>\r\n" + 
				"                		<dst_port>*</dst_port>\r\n" + 
				"            		</elements>" ; 
		
		Response res1 = ClientBuilder.newClient()
				.target(target)
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.post(Entity.entity(node,MediaType.APPLICATION_XML));
		Response res2 = ClientBuilder.newClient()
				.target(res1.getLocation()+"/configuration/firewall")
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.post(Entity.entity(policy,MediaType.APPLICATION_XML));
		
		assertEquals(Status.BAD_REQUEST.getStatusCode(), res2.getStatus());
		cleanUp();
		
	}
	@Test 
	public void testCreatePolicy3() {
		String node = "<node functional_type=\"FIREWALL\" name=\"10.0.0.3\">\r\n" + 
				"        	<neighbour name=\"30.0.0.1\"/>\r\n" + 
				"        	<configuration description=\"A simple description\" name=\"confA\">\r\n" + 
				"         		<firewall defaultAction=\"ALLOW\"/>\r\n" + 
				"       	</configuration>\r\n" + 
				"      </node>";
		
		String policy=
				"					<elements>\r\n" + 
				"               		<action>DENY</action>\r\n" + 
				"                   	<source>192.168.56.-1</source>\r\n" +  
				"                   	<protocol>ANY</protocol>\r\n" + 
				"                   	<src_port>*</src_port>\r\n" + 
				"                		<dst_port>*</dst_port>\r\n" + 
				"            		</elements>" ; 
		
		Response res1 = ClientBuilder.newClient()
				.target(target)
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.post(Entity.entity(node,MediaType.APPLICATION_XML));
		Response res2 = ClientBuilder.newClient()
				.target(res1.getLocation()+"/configuration/firewall")
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.post(Entity.entity(policy,MediaType.APPLICATION_XML));
		
		assertEquals(Status.BAD_REQUEST.getStatusCode(), res2.getStatus());
		cleanUp();
		
	}
	
	@Test 
	public void testCreatePolicy4() {
		String node = "<node functional_type=\"FIREWALL\" name=\"10.0.0.3\">\r\n" + 
				"        	<neighbour name=\"30.0.0.1\"/>\r\n" + 
				"        	<configuration description=\"A simple description\" name=\"confA\">\r\n" + 
				"         		<firewall defaultAction=\"ALLOW\"/>\r\n" + 
				"       	</configuration>\r\n" + 
				"      </node>";
		
		String policy=
				"					<elements>\r\n" + 
				"               		<action>DENY</action>\r\n" + 
				"                   	<destination>192.168.57.4</destination>\r\n" + 
				"                   	<protocol>ANY</protocol>\r\n" + 
				"                   	<src_port>*</src_port>\r\n" + 
				"                		<dst_port>*</dst_port>\r\n" + 
				"            		</elements>" ; 
		
		Response res1 = ClientBuilder.newClient()
				.target(target)
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.post(Entity.entity(node,MediaType.APPLICATION_XML));
		Response res2 = ClientBuilder.newClient()
				.target(res1.getLocation()+"/configuration/firewall")
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.post(Entity.entity(policy,MediaType.APPLICATION_XML));
		
		assertEquals(Status.BAD_REQUEST.getStatusCode(), res2.getStatus());
		cleanUp();
		
	}
	
	@Test 
	public void testCreatePolicy5() {
		String node = "<node functional_type=\"FIREWALL\" name=\"10.0.0.3\">\r\n" + 
				"        	<neighbour name=\"30.0.0.1\"/>\r\n" + 
				"        	<configuration description=\"A simple description\" name=\"confA\">\r\n" + 
				"         		<firewall defaultAction=\"ALLOW\"/>\r\n" + 
				"       	</configuration>\r\n" + 
				"      </node>";
		
		String policy=
				"					<elements>\r\n" + 
				"               		<action>DENY</action>\r\n" + 
				"                   	<source>192.168.56.-1</source>\r\n" + 
				"                   	<destination>192.168.57.4</destination>\r\n" + 
				"                   	<protocol>ANY</protocol>\r\n" + 
				"                   	<src_port>*</src_port>\r\n" + 
				"                		<dst_port>*</dst_port>\r\n" + 
				"            		</elements>" ; 
		
		Response res1 = ClientBuilder.newClient()
				.target(target)
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.post(Entity.entity(node,MediaType.APPLICATION_XML));
		Response res2 = ClientBuilder.newClient()
				.target(res1.getLocation()+"/configuration/firewall")
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.post(Entity.entity(policy,MediaType.APPLICATION_XML));
		
		assertEquals(Status.CREATED.getStatusCode(), res2.getStatus());
		cleanUp();
		
	}
	
	@Test 
	public void testCreatePolicy6() {
		
		String policy=
				"					<elements>\r\n" + 
				"               		<action>DENY</action>\r\n" + 
				"                   	<source>192.168.56.-1</source>\r\n" + 
				"                   	<destination>192.168.57.4</destination>\r\n" + 
				"                   	<protocol>ANY</protocol>\r\n" + 
				"                   	<src_port>*</src_port>\r\n" + 
				"                		<dst_port>*</dst_port>\r\n" + 
				"            		</elements>" ; 
		
		Response res = ClientBuilder.newClient()
				.target(target+"/3/configuration/firewall")
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.post(Entity.entity(policy,MediaType.APPLICATION_XML));
		assertEquals(Status.BAD_REQUEST.getStatusCode(), res.getStatus());
		cleanUp();
	}
	
	@Test 
	public void testUpdatePolicy1() {
		String node = "<node functional_type=\"FIREWALL\" name=\"10.0.0.3\">\r\n" + 
				"        	<neighbour name=\"30.0.0.1\"/>\r\n" + 
				"        	<configuration description=\"A simple description\" name=\"confA\">\r\n" + 
				"         		<firewall defaultAction=\"ALLOW\"/>\r\n" + 
				"       	</configuration>\r\n" + 
				"      </node>";
		
		String policy1=
				"					<elements>\r\n" + 
				"               		<action>DENY</action>\r\n" + 
				"                   	<source>192.168.56.-1</source>\r\n" + 
				"                   	<destination>192.168.57.4</destination>\r\n" + 
				"                   	<protocol>ANY</protocol>\r\n" + 
				"                   	<src_port>*</src_port>\r\n" + 
				"                		<dst_port>*</dst_port>\r\n" + 
				"            		</elements>" ; 
		String policy2=
				"					<elements>\r\n" + 
				"               		<action>ALLOW</action>\r\n" + 
				"                   	<source>192.168.-1.-1</source>\r\n" + 
				"                   	<destination>192.168.57.4</destination>\r\n" + 
				"                   	<protocol>ANY</protocol>\r\n" + 
				"                   	<src_port>600</src_port>\r\n" + 
				"                		<dst_port>700</dst_port>\r\n" + 
				"            		</elements>" ; 
		
		Response res1 = ClientBuilder.newClient()
				.target(target)
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.post(Entity.entity(node,MediaType.APPLICATION_XML));
		Response res2 = ClientBuilder.newClient()
				.target(res1.getLocation()+"/configuration/firewall")
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.post(Entity.entity(policy1,MediaType.APPLICATION_XML));
		Response res3 = ClientBuilder.newClient()
				//.target(res1.getLocation()+"/configuration/firewall/1")
				.target(res2.getLocation())
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.put(Entity.entity(policy2,MediaType.APPLICATION_XML));
		
		assertEquals(Status.NO_CONTENT.getStatusCode(), res3.getStatus());
		cleanUp();
		
	}
	
	
	@Test 
	public void testUpdatePolicy2() {
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
				"            		</firewall>\r\n" +
				"       	</configuration>\r\n" + 
				"      </node>";
		
		String policy=
				"					<elements>\r\n" + 
				"               		<action>ALLOW</action>\r\n" + 
				"                   	<source>192.168.-1.-1</source>\r\n" + 
				"                   	<destination>192.168.57.4</destination>\r\n" + 
				"                   	<protocol>TCP</protocol>\r\n" + 
				"                   	<src_port>600</src_port>\r\n" + 
				"                		<dst_port>700</dst_port>\r\n" + 
				"            		</elements>" ; 
		
		Response res1 = ClientBuilder.newClient()
				.target(target)
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.post(Entity.entity(node,MediaType.APPLICATION_XML));
		Response res2 = ClientBuilder.newClient()
				.target(res1.getLocation()+"/configuration/firewall/1")
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.put(Entity.entity(policy,MediaType.APPLICATION_XML));
		
		assertEquals(Status.NO_CONTENT.getStatusCode(), res2.getStatus());
		cleanUp();
		
	}
	
	
	@Test 
	public void testUpdatePolicy3() {
		String node = "<node functional_type=\"FIREWALL\" name=\"10.0.0.3\">\r\n" + 
				"        	<neighbour name=\"30.0.0.1\"/>\r\n" + 
				"        	<configuration description=\"A simple description\" name=\"confA\">\r\n" + 
				"         		<firewall defaultAction=\"ALLOW\"/>\r\n" + 
				"       	</configuration>\r\n" + 
				"      </node>";
		
		String policy=
				"					<elements>\r\n" + 
				"               		<action>ALLOW</action>\r\n" + 
				"                   	<source>192.168.-1.-1</source>\r\n" + 
				"                   	<destination>192.168.57.4</destination>\r\n" + 
				"                   	<protocol>TCP</protocol>\r\n" + 
				"                   	<src_port>600</src_port>\r\n" + 
				"                		<dst_port>700</dst_port>\r\n" + 
				"            		</elements>" ; 
		
		Response res1 = ClientBuilder.newClient()
				.target(target)
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.post(Entity.entity(node,MediaType.APPLICATION_XML));
		Response res2 = ClientBuilder.newClient()
				.target(res1.getLocation()+"/configuration/firewall/1")
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.put(Entity.entity(policy,MediaType.APPLICATION_XML));
		
		assertEquals(Status.NOT_FOUND.getStatusCode(), res2.getStatus());
		cleanUp();
		
	}
	
	@Test 
	public void testUpdatePolicy4() {
		
		String policy=
				"					<elements>\r\n" + 
				"               		<action>ALLOW</action>\r\n" + 
				"                   	<source>192.168.-1.-1</source>\r\n" + 
				"                   	<destination>192.168.57.4</destination>\r\n" + 
				"                   	<protocol>TCP</protocol>\r\n" + 
				"                   	<src_port>600</src_port>\r\n" + 
				"                		<dst_port>700</dst_port>\r\n" + 
				"            		</elements>" ; 
		
		Response res2 = ClientBuilder.newClient()
				.target(target+"/4/configuration/firewall/1")
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.put(Entity.entity(policy,MediaType.APPLICATION_XML));
		
		assertEquals(Status.NOT_FOUND.getStatusCode(), res2.getStatus());
		cleanUp();
		
	}
	
	@Test 
	public void testUpdatePolicy5() {
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
				"            		</firewall>\r\n" +
				"       	</configuration>\r\n" + 
				"      </node>";
		
		String policy=
				"					<elements>\r\n" + 
				"                   	<source>192.168.-1.-1</source>\r\n" + 
				"                   	<destination>192.168.57.4</destination>\r\n" + 
				"                   	<protocol>TCP</protocol>\r\n" + 
				"                   	<src_port>600</src_port>\r\n" + 
				"                		<dst_port>700</dst_port>\r\n" + 
				"            		</elements>" ; 
		
		Response res1 = ClientBuilder.newClient()
				.target(target)
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.post(Entity.entity(node,MediaType.APPLICATION_XML));
		Response res2 = ClientBuilder.newClient()
				.target(res1.getLocation()+"/configuration/firewall/1")
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.put(Entity.entity(policy,MediaType.APPLICATION_XML));
		
		assertEquals(Status.BAD_REQUEST.getStatusCode(), res2.getStatus());
		cleanUp();
		
	}
	
	@Test 
	public void testUpdatePolicy6() {
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
				"            		</firewall>\r\n" +
				"       	</configuration>\r\n" + 
				"      </node>";
		
		String policy=
				"					<elements>\r\n" + 
				"               		<action>ALLOW</action>\r\n" + 
				"                   	<destination>192.168.57.4</destination>\r\n" + 
				"                   	<protocol>TCP</protocol>\r\n" + 
				"                   	<src_port>600</src_port>\r\n" + 
				"                		<dst_port>700</dst_port>\r\n" + 
				"            		</elements>" ; 
		
		Response res1 = ClientBuilder.newClient()
				.target(target)
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.post(Entity.entity(node,MediaType.APPLICATION_XML));
		Response res2 = ClientBuilder.newClient()
				.target(res1.getLocation()+"/configuration/firewall/1")
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.put(Entity.entity(policy,MediaType.APPLICATION_XML));
		
		assertEquals(Status.BAD_REQUEST.getStatusCode(), res2.getStatus());
		cleanUp();
		
	}
	
	@Test 
	public void testUpdatePolicy7() {
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
				"            		</firewall>\r\n" +
				"       	</configuration>\r\n" + 
				"      </node>";
		
		String policy=
				"					<elements>\r\n" + 
				"               		<action>ALLOW</action>\r\n" + 
				"                   	<source>192.168.-1.-1</source>\r\n" + 
				"                   	<protocol>TCP</protocol>\r\n" + 
				"                   	<src_port>600</src_port>\r\n" + 
				"                		<dst_port>700</dst_port>\r\n" + 
				"            		</elements>" ; 
		
		Response res1 = ClientBuilder.newClient()
				.target(target)
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.post(Entity.entity(node,MediaType.APPLICATION_XML));
		Response res2 = ClientBuilder.newClient()
				.target(res1.getLocation()+"/configuration/firewall/1")
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.put(Entity.entity(policy,MediaType.APPLICATION_XML));
		
		assertEquals(Status.BAD_REQUEST.getStatusCode(), res2.getStatus());
		cleanUp();
		
	}
	
	@Test 
	public void testUpdatePolicy8() {
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
				"            		</firewall>\r\n" +
				"       	</configuration>\r\n" + 
				"      </node>";
		
		String policy=
				"					<elements>\r\n" + 
				"               		<action>ALLOW</action>\r\n" + 
				"                   	<source>192.168.-1.-1</source>\r\n" + 
				"                   	<destination>192.168.57.4</destination>\r\n" + 
				"                   	<src_port>600</src_port>\r\n" + 
				"                		<dst_port>700</dst_port>\r\n" + 
				"            		</elements>" ; 
		
		Response res1 = ClientBuilder.newClient()
				.target(target)
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.post(Entity.entity(node,MediaType.APPLICATION_XML));
		Response res2 = ClientBuilder.newClient()
				.target(res1.getLocation()+"/configuration/firewall/1")
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.put(Entity.entity(policy,MediaType.APPLICATION_XML));
		
		assertEquals(Status.BAD_REQUEST.getStatusCode(), res2.getStatus());
		cleanUp();
		
	}
	
	@Test 
	public void testGetPolicy1() {
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
				"            		</firewall>\r\n" +
				"       	</configuration>\r\n" + 
				"      </node>";
		
		
		Response res1 = ClientBuilder.newClient()
				.target(target)
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.post(Entity.entity(node,MediaType.APPLICATION_XML));
		Response res2 = ClientBuilder.newClient()
				.target(res1.getLocation()+"/configuration/firewall/2")
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.get();
		
		assertEquals(Status.OK.getStatusCode(), res2.getStatus());
		cleanUp();
		
	}
	
	@Test 
	public void testGetPolicy2() {

		Response res = ClientBuilder.newClient()
				.target(target+"/6/configuration/firewall/1")
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.get();
		
		assertEquals(Status.NOT_FOUND.getStatusCode(), res.getStatus());
		cleanUp();
		
	}
	
	@Test 
	public void testDeletePolicy1() {
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
				"            		</firewall>\r\n" +
				"       	</configuration>\r\n" + 
				"      </node>";
		
		
		Response res1 = ClientBuilder.newClient()
				.target(target)
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.post(Entity.entity(node,MediaType.APPLICATION_XML));
		Response res2 = ClientBuilder.newClient()
				.target(res1.getLocation()+"/configuration/firewall/1")
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.delete();
		
		assertEquals(Status.NO_CONTENT.getStatusCode(), res2.getStatus());
		cleanUp();
		
	}
	
	@Test 
	public void testDeletePolicy2() {

		Response res = ClientBuilder.newClient()
				.target(target+"/6/configuration/firewall/1")
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.delete();
		
		assertEquals(Status.NOT_FOUND.getStatusCode(), res.getStatus());
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