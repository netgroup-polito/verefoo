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
 * This class tests all the methods regarding nodes creation and update
 *
 */

public class TestRestNodes {
	final String target = "http://127.0.0.1:8085/verefoo/fwd/nodes";
	
	
	

	@Test 
	public void testCreateNode1() {
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
		
		Response res = ClientBuilder.newClient()
				.target(target)
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.post(Entity.entity(node,MediaType.APPLICATION_XML));
		assertEquals(Status.CREATED.getStatusCode(), res.getStatus());
		
	}
	
	@Test 
	public void testCreateNode2() {
		String node = "<node functional_type=\"WEBCLIENT\" name=\"10.0.0.3\">\r\n" + 
				"        	<neighbour name=\"30.0.0.1\"/>\r\n" + 
				"        	<configuration description=\"A simple description\" name=\"confA\">\r\n" + 
				"       	</configuration>\r\n" + 
				"      </node>";
		
		Response res = ClientBuilder.newClient()
				.target(target)
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.post(Entity.entity(node,MediaType.APPLICATION_XML));
		assertEquals(Status.BAD_REQUEST.getStatusCode(), res.getStatus());
		
	}
	
	
//	@Test 
//	public void testCreateNode3() {
//		String node = "<node functional_type=\"WEBCLIENT\" name=\"10.0.0.3\">\r\n" + 
//				"        	<neighbour name=\"30.0.0.1\"/>\r\n" + 
//				"        	<configuration description=\"A simple description\" name=\"confA\">\r\n" + 
//				"       	</configuration>\r\n" + 
//				"      </node>";
//		
//		Response res = ClientBuilder.newClient()
//				.target(target)
//				.request(MediaType.APPLICATION_XML)
//				.accept(MediaType.APPLICATION_XML)
//				.post(Entity.entity(node,MediaType.APPLICATION_XML));
//		assertEquals(Status.BAD_REQUEST.getStatusCode(), res.getStatus());
//		//CONFLICT IMPOSSIBILE  DA RICREARE
//		
//	}
	
	
	@Test 
	public void testGetNodes1() {
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
		for(int i=0;i<5;i++) {
			 ClientBuilder.newClient()
				.target(target)
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.post(Entity.entity(node,MediaType.APPLICATION_XML));
			
		}

		 Response res =  ClientBuilder.newClient()
				 .target(target)
				 .queryParam("beforeInclusive", 1)
				 .queryParam("afterInclusive", 2)
					.request(MediaType.APPLICATION_XML)
					.accept(MediaType.APPLICATION_XML)
					.get();
		assertEquals(Status.OK.getStatusCode(), res.getStatus());
		
	}
	
	
	@Test 
	public void testGetNodes2() {
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
		
		 ClientBuilder.newClient()
				.target(target)
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.post(Entity.entity(node,MediaType.APPLICATION_XML));
		 Response res =  ClientBuilder.newClient()
				 .target(target)
				 .queryParam("beforeInclusive", 15)
				 .queryParam("afterInclusive", 11)
					.request(MediaType.APPLICATION_XML)
					.accept(MediaType.APPLICATION_XML)
					.get();
		assertEquals(Status.BAD_REQUEST.getStatusCode(), res.getStatus());
		
	}
	
	
	@Test 
	public void testGetNodes3() {
		 Response res =  ClientBuilder.newClient()
				 .target(target)
				 .queryParam("beforeInclusive", 20)
				 .queryParam("afterInclusive", 21)
					.request(MediaType.APPLICATION_XML)
					.accept(MediaType.APPLICATION_XML)
					.get();
		assertEquals(Status.NOT_FOUND.getStatusCode(), res.getStatus());
		
	}
	
	@Test 
	public void testGetNodes4() {
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
		
		 ClientBuilder.newClient()
				.target(target)
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.post(Entity.entity(node,MediaType.APPLICATION_XML));
		 Response res =  ClientBuilder.newClient()
				 .target(target)
				 .queryParam("beforeInclusive", 49)
				 .queryParam("afterInclusive", 50)
					.request(MediaType.APPLICATION_XML)
					.accept(MediaType.APPLICATION_XML)
					.get();
		assertEquals(Status.NOT_FOUND.getStatusCode(), res.getStatus());
		
	}
	
	@Test 
	public void testDeleteNodes1() {
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
		
		 ClientBuilder.newClient()
				.target(target)
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.post(Entity.entity(node,MediaType.APPLICATION_XML));
		 Response res =  ClientBuilder.newClient()
				 .target(target)
					.request(MediaType.APPLICATION_XML)
					.accept(MediaType.APPLICATION_XML)
					.delete();
		assertEquals(Status.NO_CONTENT.getStatusCode(), res.getStatus());
		
	}
	
	@Test 
public void testDeleteNodes2() {
		 Response res =  ClientBuilder.newClient()
				 .target(target)
					.request(MediaType.APPLICATION_XML)
					.accept(MediaType.APPLICATION_XML)
					.delete();
		assertEquals(Status.NOT_FOUND.getStatusCode(), res.getStatus());
		
	}
	
	@Test 
	public void testUpdateNode1() {
		String node1 = "<node functional_type=\"FIREWALL\" name=\"10.0.0.3\">\r\n" + 
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
		
		String node2 = "<node functional_type=\"FIREWALL\" name=\"10.0.0.3\">\r\n" + 
				"        	<neighbour name=\"30.0.1.1\"/>\r\n" + 
				"        	<configuration description=\"A simple description\" name=\"confA\">\r\n" + 
				"         		<firewall defaultAction=\"DENY\">\r\n" + 
				"					<elements>\r\n" + 
				"               		<action>ALLOW</action>\r\n" + 
				"                   	<source>192.168.56.96</source>\r\n" + 
				"                   	<destination>192.168.57.15</destination>\r\n" + 
				"                   	<protocol>ANY</protocol>\r\n" + 
				"                   	<src_port>*</src_port>\r\n" + 
				"                		<dst_port>*</dst_port>\r\n" + 
				"            		</elements>\r\n" + 
				"				</firewall>\r\n"+
				"       	</configuration>\r\n" + 
				"      </node>";
		
		Response res1 = ClientBuilder.newClient()
				.target(target)
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.post(Entity.entity(node1,MediaType.APPLICATION_XML));
		Response res2 = ClientBuilder.newClient()
				.target(res1.getLocation())
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.put(Entity.entity(node2,MediaType.APPLICATION_XML));
		assertEquals(Status.NO_CONTENT.getStatusCode(), res2.getStatus());
		cleanUp();
		
	}
	
	@Test 
	public void testUpdateNode2() {
		String node1 = "<node functional_type=\"FIREWALL\" name=\"10.0.0.3\">\r\n" + 
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
		
		Response res = ClientBuilder.newClient()
				.target(target+"/5")
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.put(Entity.entity(node1,MediaType.APPLICATION_XML));
	
		assertEquals(Status.NOT_FOUND.getStatusCode(), res.getStatus());
		cleanUp();
		
	}
	
	
	@Test 
	public void testUpdateNode3() {
		String node1 = "<node functional_type=\"FIREWALL\" name=\"10.0.0.3\">\r\n" + 
				"        	<neighbour name=\"30.0.0.1\"/>\r\n" + 
				"        	<configuration name=\"confB\" description=\"A simple description\"/>\r\n"+ 
				"      </node>";
		
		String node2 = "<node functional_type=\"FIREWALL\" name=\"10.0.0.3\">\r\n" + 
				"        	<neighbour name=\"30.0.1.1\"/>\r\n" + 
				"        	<configuration description=\"A simple description\" name=\"confA\">\r\n" + 
				"         		<firewall defaultAction=\"DENY\">\r\n" + 
				"					<elements>\r\n" + 
				"               		<action>ALLOW</action>\r\n" + 
				"                   	<source>192.168.56.96</source>\r\n" + 
				"                   	<destination>192.168.57.15</destination>\r\n" + 
				"                   	<protocol>ANY</protocol>\r\n" + 
				"                   	<src_port>*</src_port>\r\n" + 
				"                		<dst_port>*</dst_port>\r\n" + 
				"            		</elements>\r\n" + 
				"				</firewall>\r\n"+
				"       	</configuration>\r\n" + 
				"      </node>";
		
		Response res1 = ClientBuilder.newClient()
				.target(target)
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.post(Entity.entity(node1,MediaType.APPLICATION_XML));
		Response res2 = ClientBuilder.newClient()
				.target(res1.getLocation())
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.put(Entity.entity(node2,MediaType.APPLICATION_XML));
		assertEquals(Status.NO_CONTENT.getStatusCode(), res2.getStatus());
		cleanUp();
		
	}

	@Test 
	public void testUpdateNode4() {
		String node1 = "<node functional_type=\"FIREWALL\" name=\"10.0.0.3\">\r\n" + 
				"        	<neighbour name=\"30.0.1.1\"/>\r\n" + 
				"        	<configuration description=\"A simple description\" name=\"confA\">\r\n" + 
				"         		<firewall defaultAction=\"DENY\">\r\n" + 
				"					<elements>\r\n" + 
				"               		<action>ALLOW</action>\r\n" + 
				"                   	<source>192.168.56.96</source>\r\n" + 
				"                   	<destination>192.168.57.15</destination>\r\n" + 
				"                   	<protocol>ANY</protocol>\r\n" + 
				"                   	<src_port>*</src_port>\r\n" + 
				"                		<dst_port>*</dst_port>\r\n" + 
				"            		</elements>\r\n" + 
				"				</firewall>\r\n"+
				"       	</configuration>\r\n" + 
				"      </node>";
		
		String node2 = "<node functional_type=\"FIREWALL\" name=\"10.0.0.3\">\r\n" + 
				"        	<neighbour name=\"30.0.0.1\"/>\r\n" + 
				"        	<configuration name=\"confB\" description=\"A simple description\"/>\r\n"+
				"      </node>";
		
		Response res1 = ClientBuilder.newClient()
				.target(target)
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.post(Entity.entity(node1,MediaType.APPLICATION_XML));
		Response res2 = ClientBuilder.newClient()
				.target(res1.getLocation())
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.put(Entity.entity(node2,MediaType.APPLICATION_XML));
		assertEquals(Status.NO_CONTENT.getStatusCode(), res2.getStatus());
		cleanUp();
		
	}
	
	
	@Test 
	public void testUpdateNode5() {
		String node1 = "<node functional_type=\"FIREWALL\" name=\"10.0.0.3\">\r\n" + 
				"        	<neighbour name=\"30.0.0.1\"/>\r\n" + 
				"        	<configuration description=\"A simple description\" name=\"confA\">\r\n" + 
				"         		<firewall defaultAction=\"DENY\"/>\r\n"+
				"       	</configuration>\r\n" + 
				"      </node>";
		
		String node2 = "<node functional_type=\"FIREWALL\" name=\"10.0.0.3\">\r\n" + 
				"        	<neighbour name=\"30.0.1.1\"/>\r\n" + 
				"        	<configuration description=\"A simple description\" name=\"confA\">\r\n" + 
				"         		<firewall defaultAction=\"DENY\">\r\n" + 
				"					<elements>\r\n" + 
				"               		<action>ALLOW</action>\r\n" + 
				"                   	<source>192.168.56.96</source>\r\n" + 
				"                   	<destination>192.168.57.15</destination>\r\n" + 
				"                   	<protocol>ANY</protocol>\r\n" + 
				"                   	<src_port>*</src_port>\r\n" + 
				"                		<dst_port>*</dst_port>\r\n" + 
				"            		</elements>\r\n" + 
				"				</firewall>\r\n"+
				"       	</configuration>\r\n" + 
				"      </node>";
		
		Response res1 = ClientBuilder.newClient()
				.target(target)
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.post(Entity.entity(node1,MediaType.APPLICATION_XML));
		Response res2 = ClientBuilder.newClient()
				.target(res1.getLocation())
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.put(Entity.entity(node2,MediaType.APPLICATION_XML));
		assertEquals(Status.NO_CONTENT.getStatusCode(), res2.getStatus());
		cleanUp();
		
	}
	
	@Test 
	public void testUpdateNode6() {
		String node1 = "<node functional_type=\"FIREWALL\" name=\"10.0.0.3\">\r\n" + 
				"        	<neighbour name=\"30.0.1.1\"/>\r\n" + 
				"        	<configuration description=\"A simple description\" name=\"confA\">\r\n" + 
				"         		<firewall defaultAction=\"DENY\">\r\n" + 
				"					<elements>\r\n" + 
				"               		<action>ALLOW</action>\r\n" + 
				"                   	<source>192.168.56.96</source>\r\n" + 
				"                   	<destination>192.168.57.15</destination>\r\n" + 
				"                   	<protocol>ANY</protocol>\r\n" + 
				"                   	<src_port>*</src_port>\r\n" + 
				"                		<dst_port>*</dst_port>\r\n" + 
				"            		</elements>\r\n" + 
				"				</firewall>\r\n"+
				"       	</configuration>\r\n" + 
				"      </node>";
		
		String node2 = "<node functional_type=\"FIREWALL\" name=\"10.0.0.3\">\r\n" + 
				"        	<neighbour name=\"30.0.0.1\"/>\r\n" + 
				"        	<configuration description=\"A simple description\" name=\"confA\">\r\n" + 
				"         		<firewall defaultAction=\"DENY\"/>\r\n"+
				"       	</configuration>\r\n" + 
				"      </node>";
		
		Response res1 = ClientBuilder.newClient()
				.target(target)
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.post(Entity.entity(node1,MediaType.APPLICATION_XML));
		Response res2 = ClientBuilder.newClient()
				.target(res1.getLocation())
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.put(Entity.entity(node2,MediaType.APPLICATION_XML));
		assertEquals(Status.NO_CONTENT.getStatusCode(), res2.getStatus());
		cleanUp();
		
	}
	
	@Test 
	public void testGetNode1() {
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
		
		Response res1 = ClientBuilder.newClient()
				.target(target)
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.post(Entity.entity(node,MediaType.APPLICATION_XML));
		Response res2 = ClientBuilder.newClient()
				.target(res1.getLocation())
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.get();
		assertEquals(Status.OK.getStatusCode(), res2.getStatus());
		cleanUp();
		
	}
	
	
	@Test 
	public void testGetNode2() {

		Response res = ClientBuilder.newClient()
				.target(target +"/1")
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.get();
		assertEquals(Status.NOT_FOUND.getStatusCode(), res.getStatus());
		
	}
	
	
	
	@Test 
	public void testDeleteNode1() {
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
		
		Response res1 = ClientBuilder.newClient()
				.target(target)
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.post(Entity.entity(node,MediaType.APPLICATION_XML));
		Response res2 = ClientBuilder.newClient()
				.target(res1.getLocation())
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.delete();
		assertEquals(Status.NO_CONTENT.getStatusCode(), res2.getStatus());
		cleanUp();
		
	}
	
	
	@Test 
	public void testDeleteNode2() {

		Response res = ClientBuilder.newClient()
				.target(target +"/1")
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.delete();
		assertEquals(Status.NOT_FOUND.getStatusCode(), res.getStatus());
		
	}
	
	
	@Test 
	public void testCreateNodeFromNFV1() {
		String nfv = "<NFV xsi:noNamespaceSchemaLocation=\"./xsd/nfvSchema.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\r\n" + 
				"    <graphs>\r\n" + 
				"        <graph id=\"0\">\r\n" + 
				"            <node name=\"10.0.0.1\" functional_type=\"WEBCLIENT\">\r\n" + 
				"                <neighbour name=\"20.0.0.1\"/>\r\n" + 
				"                <configuration name=\"confA\" description=\"A simple description\">\r\n" + 
				"                    <webclient nameWebServer=\"30.0.5.2\"/>\r\n" + 
				"                </configuration>\r\n" + 
				"            </node>\r\n" + 
				"            <node name=\"10.0.0.2\" functional_type=\"WEBCLIENT\">\r\n" + 
				"                <neighbour name=\"20.0.0.2\"/>\r\n" + 
				"                <configuration name=\"conf1\" description=\"A simple description\">\r\n" + 
				"                    <webclient nameWebServer=\"30.0.5.2\"/>\r\n" + 
				"                </configuration>\r\n" + 
				"            </node>\r\n" + 
				"            <node name=\"10.0.1.3\" functional_type=\"WEBCLIENT\">\r\n" + 
				"                <neighbour name=\"20.0.0.3\"/>\r\n" + 
				"                <configuration name=\"conf1\" description=\"A simple description\">\r\n" + 
				"                    <webclient nameWebServer=\"30.0.5.2\"/>\r\n" + 
				"                </configuration>\r\n" + 
				"            </node>\r\n" + 
				"            <node name=\"10.0.0.4\" functional_type=\"WEBCLIENT\">\r\n" + 
				"                <neighbour name=\"20.0.0.4\"/>\r\n" + 
				"                <configuration name=\"conf1\" description=\"A simple description\">\r\n" + 
				"                    <webclient nameWebServer=\"30.0.5.2\"/>\r\n" + 
				"                </configuration>\r\n" + 
				"            </node>\r\n" + 
				"            <node name=\"10.0.0.5\" functional_type=\"WEBCLIENT\">\r\n" + 
				"                <neighbour name=\"20.0.0.5\"/>\r\n" + 
				"                <configuration name=\"conf1\" description=\"A simple description\">\r\n" + 
				"                    <webclient nameWebServer=\"30.0.5.2\"/>\r\n" + 
				"                </configuration>\r\n" + 
				"            </node>\r\n" + 
				"            <node name=\"20.0.0.1\" functional_type=\"FIREWALL\">\r\n" + 
				"                <neighbour name=\"10.0.0.1\"/>\r\n" + 
				"                <neighbour name=\"20.0.0.5\"/>\r\n" + 
				"                <configuration name=\"conf1\" description=\"b0: UC1\">\r\n" + 
				"                    <firewall defaultAction=\"ALLOW\"/>\r\n" + 
				"                </configuration>\r\n" + 
				"            </node>\r\n" + 
				"            <node name=\"20.0.0.2\" functional_type=\"FIREWALL\">\r\n" + 
				"                <neighbour name=\"10.0.0.2\"/>\r\n" + 
				"                <neighbour name=\"20.0.0.5\"/>\r\n" + 
				"                <configuration name=\"conf1\" description=\"b0: UC2\">\r\n" + 
				"                    <firewall defaultAction=\"DENY\"/>\r\n" + 
				"                </configuration>\r\n" + 
				"            </node>\r\n" + 
				"            <node name=\"20.0.0.3\" functional_type=\"FIREWALL\">\r\n" + 
				"                <neighbour name=\"10.0.1.3\"/>\r\n" + 
				"                <neighbour name=\"20.0.0.5\"/>\r\n" + 
				"                <configuration name=\"conf1\" description=\"b0: UC3\">\r\n" + 
				"                    <firewall defaultAction=\"ALLOW\">\r\n" + 
				"					<elements>\r\n" + 
				"                            <action>DENY</action>\r\n" + 
				"                            <source>192.168.56.-1</source>\r\n" + 
				"                            <destination>192.168.57.4</destination>\r\n" + 
				"                            <protocol>ANY</protocol>\r\n" + 
				"                            <src_port>*</src_port>\r\n" + 
				"                            <dst_port>*</dst_port>\r\n" + 
				"                        </elements>\r\n" + 
				"						 </firewall>\r\n" + 
				"                </configuration>\r\n" + 
				"            </node>\r\n" + 
				"            <node name=\"20.0.0.4\" functional_type=\"FIREWALL\">\r\n" + 
				"                <neighbour name=\"10.0.0.4\"/>\r\n" + 
				"                <neighbour name=\"20.0.0.2\"/>\r\n" + 
				"                <configuration name=\"conf1\" description=\"b0: UC4\">\r\n" + 
				"                    <firewall defaultAction=\"ALLOW\">\r\n" + 
				"					<elements>\r\n" + 
				"                            <action>DENY</action>\r\n" + 
				"                            <source>192.168.56.3</source>\r\n" + 
				"                            <destination>192.168.57.4</destination>\r\n" + 
				"                            <protocol>TCP</protocol>\r\n" + 
				"                            <src_port>*</src_port>\r\n" + 
				"                            <dst_port>8080</dst_port>\r\n" + 
				"							<directional>true</directional>\r\n" + 
				"                        </elements>\r\n" + 
				"						 </firewall>\r\n" + 
				"                </configuration>\r\n" + 
				"            </node>\r\n" + 
				"            <node name=\"20.0.0.5\" functional_type=\"FIREWALL\">\r\n" + 
				"                <neighbour name=\"10.0.0.5\"/>\r\n" + 
				"                <neighbour name=\"30.0.5.2\"/>\r\n" + 
				"                <configuration name=\"conf1\" description=\"b0: UC5\">\r\n" + 
				"                    <firewall defaultAction=\"ALLOW\">\r\n" + 
				"					<elements>\r\n" + 
				"                            <action>DENY</action>\r\n" + 
				"                            <source>192.168.56.3</source>\r\n" + 
				"                            <destination>192.168.57.4</destination>\r\n" + 
				"                            <protocol>TCP</protocol>\r\n" + 
				"                            <src_port>*</src_port>\r\n" + 
				"                            <dst_port>45-56</dst_port>\r\n" + 
				"							<priority>10</priority>\r\n" + 
				"						</elements>\r\n" + 
				"					<elements>\r\n" + 
				"                            <action>ALLOW</action>\r\n" + 
				"                            <source>192.168.56.3</source>\r\n" + 
				"                            <destination>192.168.57.4</destination>\r\n" + 
				"                            <protocol>TCP</protocol>\r\n" + 
				"                            <src_port>*</src_port>\r\n" + 
				"                            <dst_port>50</dst_port>\r\n" + 
				"							<priority>5</priority>\r\n" + 
				"                        </elements>\r\n" + 
				"						 </firewall>\r\n" + 
				"                </configuration>\r\n" + 
				"            </node>\r\n" + 
				"            <node name=\"30.0.5.2\" functional_type=\"WEBSERVER\">\r\n" + 
				"                <neighbour name=\"20.0.0.5\"/>\r\n" + 
				"                <configuration name=\"confB\" description=\"A simple description\">\r\n" + 
				"                    <webserver>\r\n" + 
				"                        <name>30.0.5.2</name>\r\n" + 
				"                    </webserver>\r\n" + 
				"                </configuration>\r\n" + 
				"            </node>\r\n" + 
				"        </graph>\r\n" + 
				"    </graphs>\r\n" + 
				"    <Constraints>\r\n" + 
				"        <NodeConstraints/>\r\n" + 
				"        <LinkConstraints/>\r\n" + 
				"    </Constraints>\r\n" + 
				"    <PropertyDefinition>\r\n" + 
				"        <Property name=\"IsolationProperty\" graph=\"0\" src=\"10.0.0.1\" dst=\"30.0.5.2\" lv4proto=\"ANY\" src_port=\"null\" dst_port=\"null\" isSat=\"true\"/>\r\n" + 
				"        <Property name=\"IsolationProperty\" graph=\"0\" src=\"10.0.0.2\" dst=\"30.0.5.2\" lv4proto=\"ANY\" src_port=\"null\" dst_port=\"null\" isSat=\"true\"/>\r\n" + 
				"        <Property name=\"ReachabilityProperty\" graph=\"0\" src=\"10.0.1.3\" dst=\"30.0.5.2\" lv4proto=\"ANY\" src_port=\"null\" dst_port=\"null\" isSat=\"true\"/>\r\n" + 
				"        <Property name=\"IsolationProperty\" graph=\"0\" src=\"10.0.0.4\" dst=\"30.0.5.2\" lv4proto=\"ANY\" src_port=\"null\" dst_port=\"null\" isSat=\"true\"/>\r\n" + 
				"        <Property name=\"IsolationProperty\" graph=\"0\" src=\"10.0.0.5\" dst=\"30.0.5.2\" lv4proto=\"ANY\" src_port=\"null\" dst_port=\"null\" isSat=\"true\"/>\r\n" + 
				"    </PropertyDefinition>\r\n" + 
				"    <ParsingString></ParsingString>\r\n" + 
				"</NFV>";
		
		Response res = ClientBuilder.newClient()
				.target(target+"/addnfv")
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.post(Entity.entity(nfv,MediaType.APPLICATION_XML));
		assertEquals(Status.OK.getStatusCode(), res.getStatus());
		cleanUp();
		
	}
	
	@Test 
	public void testCreateNodeFromNFV2() {
		String nfv = "<NFV xsi:noNamespaceSchemaLocation=\"./xsd/nfvSchema.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\r\n" + 
				"    <graphs>\r\n" + 
				"        <graph id=\"0\">\r\n" + 
				"            <node name=\"10.0.0.1\" functional_type=\"WEBCLIENT\">\r\n" + 
				"                <neighbour name=\"30.0.5.2\"/>\r\n" + 
				"                <configuration name=\"confA\" description=\"A simple description\">\r\n" + 
				"                    <webclient nameWebServer=\"30.0.5.2\"/>\r\n" + 
				"                </configuration>\r\n" + 
				"            </node>\r\n" + 
				"            <node name=\"30.0.5.2\" functional_type=\"WEBSERVER\">\r\n" + 
				"                <neighbour name=\"10.0.0.1\"/>\r\n" + 
				"                <configuration name=\"confB\" description=\"A simple description\">\r\n" + 
				"                    <webserver>\r\n" + 
				"                        <name>30.0.5.2</name>\r\n" + 
				"                    </webserver>\r\n" + 
				"                </configuration>\r\n" + 
				"            </node>\r\n" + 
				"        </graph>\r\n" + 
				"    </graphs>\r\n" + 
				"    <Constraints>\r\n" + 
				"        <NodeConstraints/>\r\n" + 
				"        <LinkConstraints/>\r\n" + 
				"    </Constraints>\r\n" + 
				"    <PropertyDefinition>\r\n" + 
				"        <Property name=\"IsolationProperty\" graph=\"0\" src=\"10.0.0.1\" dst=\"30.0.5.2\" lv4proto=\"ANY\" src_port=\"null\" dst_port=\"null\" isSat=\"true\"/>\r\n" + 
				"        <Property name=\"IsolationProperty\" graph=\"0\" src=\"10.0.0.2\" dst=\"30.0.5.2\" lv4proto=\"ANY\" src_port=\"null\" dst_port=\"null\" isSat=\"true\"/>\r\n" + 
				"        <Property name=\"ReachabilityProperty\" graph=\"0\" src=\"10.0.1.3\" dst=\"30.0.5.2\" lv4proto=\"ANY\" src_port=\"null\" dst_port=\"null\" isSat=\"true\"/>\r\n" + 
				"        <Property name=\"IsolationProperty\" graph=\"0\" src=\"10.0.0.4\" dst=\"30.0.5.2\" lv4proto=\"ANY\" src_port=\"null\" dst_port=\"null\" isSat=\"true\"/>\r\n" + 
				"        <Property name=\"IsolationProperty\" graph=\"0\" src=\"10.0.0.5\" dst=\"30.0.5.2\" lv4proto=\"ANY\" src_port=\"null\" dst_port=\"null\" isSat=\"true\"/>\r\n" + 
				"    </PropertyDefinition>\r\n" + 
				"    <ParsingString></ParsingString>\r\n" + 
				"</NFV>";
		
		Response res = ClientBuilder.newClient()
				.target(target+"/addnfv")
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.post(Entity.entity(nfv,MediaType.APPLICATION_XML));
		assertEquals(Status.BAD_REQUEST.getStatusCode(), res.getStatus());
		
	}
	
	@Test 
	public void testCreateNodeFromGraph1() {
		String graph ="        <graph id=\"0\">\r\n" + 
				"            <node name=\"10.0.0.1\" functional_type=\"WEBCLIENT\">\r\n" + 
				"                <neighbour name=\"20.0.0.1\"/>\r\n" + 
				"                <configuration name=\"confA\" description=\"A simple description\">\r\n" + 
				"                    <webclient nameWebServer=\"30.0.5.2\"/>\r\n" + 
				"                </configuration>\r\n" + 
				"            </node>\r\n" + 
				"            <node name=\"10.0.0.2\" functional_type=\"WEBCLIENT\">\r\n" + 
				"                <neighbour name=\"20.0.0.2\"/>\r\n" + 
				"                <configuration name=\"conf1\" description=\"A simple description\">\r\n" + 
				"                    <webclient nameWebServer=\"30.0.5.2\"/>\r\n" + 
				"                </configuration>\r\n" + 
				"            </node>\r\n" + 
				"            <node name=\"10.0.1.3\" functional_type=\"WEBCLIENT\">\r\n" + 
				"                <neighbour name=\"20.0.0.3\"/>\r\n" + 
				"                <configuration name=\"conf1\" description=\"A simple description\">\r\n" + 
				"                    <webclient nameWebServer=\"30.0.5.2\"/>\r\n" + 
				"                </configuration>\r\n" + 
				"            </node>\r\n" + 
				"            <node name=\"10.0.0.4\" functional_type=\"WEBCLIENT\">\r\n" + 
				"                <neighbour name=\"20.0.0.4\"/>\r\n" + 
				"                <configuration name=\"conf1\" description=\"A simple description\">\r\n" + 
				"                    <webclient nameWebServer=\"30.0.5.2\"/>\r\n" + 
				"                </configuration>\r\n" + 
				"            </node>\r\n" + 
				"            <node name=\"10.0.0.5\" functional_type=\"WEBCLIENT\">\r\n" + 
				"                <neighbour name=\"20.0.0.5\"/>\r\n" + 
				"                <configuration name=\"conf1\" description=\"A simple description\">\r\n" + 
				"                    <webclient nameWebServer=\"30.0.5.2\"/>\r\n" + 
				"                </configuration>\r\n" + 
				"            </node>\r\n" + 
				"            <node name=\"20.0.0.1\" functional_type=\"FIREWALL\">\r\n" + 
				"                <neighbour name=\"10.0.0.1\"/>\r\n" + 
				"                <neighbour name=\"20.0.0.5\"/>\r\n" + 
				"                <configuration name=\"conf1\" description=\"b0: UC1\">\r\n" + 
				"                    <firewall defaultAction=\"ALLOW\"/>\r\n" + 
				"                </configuration>\r\n" + 
				"            </node>\r\n" + 
				"            <node name=\"20.0.0.2\" functional_type=\"FIREWALL\">\r\n" + 
				"                <neighbour name=\"10.0.0.2\"/>\r\n" + 
				"                <neighbour name=\"20.0.0.5\"/>\r\n" + 
				"                <configuration name=\"conf1\" description=\"b0: UC2\">\r\n" + 
				"                    <firewall defaultAction=\"DENY\"/>\r\n" + 
				"                </configuration>\r\n" + 
				"            </node>\r\n" + 
				"            <node name=\"20.0.0.3\" functional_type=\"FIREWALL\">\r\n" + 
				"                <neighbour name=\"10.0.1.3\"/>\r\n" + 
				"                <neighbour name=\"20.0.0.5\"/>\r\n" + 
				"                <configuration name=\"conf1\" description=\"b0: UC3\">\r\n" + 
				"                    <firewall defaultAction=\"ALLOW\">\r\n" + 
				"					<elements>\r\n" + 
				"                            <action>DENY</action>\r\n" + 
				"                            <source>192.168.56.-1</source>\r\n" + 
				"                            <destination>192.168.57.4</destination>\r\n" + 
				"                            <protocol>ANY</protocol>\r\n" + 
				"                            <src_port>*</src_port>\r\n" + 
				"                            <dst_port>*</dst_port>\r\n" + 
				"                        </elements>\r\n" + 
				"						 </firewall>\r\n" + 
				"                </configuration>\r\n" + 
				"            </node>\r\n" + 
				"            <node name=\"20.0.0.4\" functional_type=\"FIREWALL\">\r\n" + 
				"                <neighbour name=\"10.0.0.4\"/>\r\n" + 
				"                <neighbour name=\"20.0.0.2\"/>\r\n" + 
				"                <configuration name=\"conf1\" description=\"b0: UC4\">\r\n" + 
				"                    <firewall defaultAction=\"ALLOW\">\r\n" + 
				"					<elements>\r\n" + 
				"                            <action>DENY</action>\r\n" + 
				"                            <source>192.168.56.3</source>\r\n" + 
				"                            <destination>192.168.57.4</destination>\r\n" + 
				"                            <protocol>TCP</protocol>\r\n" + 
				"                            <src_port>*</src_port>\r\n" + 
				"                            <dst_port>8080</dst_port>\r\n" + 
				"							<directional>true</directional>\r\n" + 
				"                        </elements>\r\n" + 
				"						 </firewall>\r\n" + 
				"                </configuration>\r\n" + 
				"            </node>\r\n" + 
				"            <node name=\"20.0.0.5\" functional_type=\"FIREWALL\">\r\n" + 
				"                <neighbour name=\"10.0.0.5\"/>\r\n" + 
				"                <neighbour name=\"30.0.5.2\"/>\r\n" + 
				"                <configuration name=\"conf1\" description=\"b0: UC5\">\r\n" + 
				"                    <firewall defaultAction=\"ALLOW\">\r\n" + 
				"					<elements>\r\n" + 
				"                            <action>DENY</action>\r\n" + 
				"                            <source>192.168.56.3</source>\r\n" + 
				"                            <destination>192.168.57.4</destination>\r\n" + 
				"                            <protocol>TCP</protocol>\r\n" + 
				"                            <src_port>*</src_port>\r\n" + 
				"                            <dst_port>45-56</dst_port>\r\n" + 
				"							<priority>10</priority>\r\n" + 
				"						</elements>\r\n" + 
				"					<elements>\r\n" + 
				"                            <action>ALLOW</action>\r\n" + 
				"                            <source>192.168.56.3</source>\r\n" + 
				"                            <destination>192.168.57.4</destination>\r\n" + 
				"                            <protocol>TCP</protocol>\r\n" + 
				"                            <src_port>*</src_port>\r\n" + 
				"                            <dst_port>50</dst_port>\r\n" + 
				"							<priority>5</priority>\r\n" + 
				"                        </elements>\r\n" + 
				"						 </firewall>\r\n" + 
				"                </configuration>\r\n" + 
				"            </node>\r\n" + 
				"            <node name=\"30.0.5.2\" functional_type=\"WEBSERVER\">\r\n" + 
				"                <neighbour name=\"20.0.0.5\"/>\r\n" + 
				"                <configuration name=\"confB\" description=\"A simple description\">\r\n" + 
				"                    <webserver>\r\n" + 
				"                        <name>30.0.5.2</name>\r\n" + 
				"                    </webserver>\r\n" + 
				"                </configuration>\r\n" + 
				"            </node>\r\n" + 
				"        </graph>";
		
		Response res = ClientBuilder.newClient()
				.target(target+"/addgraph")
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.post(Entity.entity(graph,MediaType.APPLICATION_XML));
		assertEquals(Status.OK.getStatusCode(), res.getStatus());
		cleanUp();
		
	}
	
	
	@Test 
	public void testCreateNodeFromGraph2() {
		String graph = 	"        <graph id=\"0\">\r\n" + 
				"            <node name=\"10.0.0.1\" functional_type=\"WEBCLIENT\">\r\n" + 
				"                <neighbour name=\"30.0.5.2\"/>\r\n" + 
				"                <configuration name=\"confA\" description=\"A simple description\">\r\n" + 
				"                    <webclient nameWebServer=\"30.0.5.2\"/>\r\n" + 
				"                </configuration>\r\n" + 
				"            </node>\r\n" + 
				"            <node name=\"30.0.5.2\" functional_type=\"WEBSERVER\">\r\n" + 
				"                <neighbour name=\"10.0.0.1\"/>\r\n" + 
				"                <configuration name=\"confB\" description=\"A simple description\">\r\n" + 
				"                    <webserver>\r\n" + 
				"                        <name>30.0.5.2</name>\r\n" + 
				"                    </webserver>\r\n" + 
				"                </configuration>\r\n" + 
				"            </node>\r\n" + 
				"        </graph>";
		
		Response res = ClientBuilder.newClient()
				.target(target+"/addgraph")
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.post(Entity.entity(graph,MediaType.APPLICATION_XML));
		assertEquals(Status.BAD_REQUEST.getStatusCode(), res.getStatus());
		
	}
	
	
	
	
private void cleanUp() {
	ClientBuilder.newClient()
	 .target(target)
		.request(MediaType.APPLICATION_XML)
		.accept(MediaType.APPLICATION_XML)
		.delete();
}
	
	
}
