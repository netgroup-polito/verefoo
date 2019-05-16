package it.polito.verefoo.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import it.polito.verefoo.jaxb.ApplicationError;
import it.polito.verefoo.jaxb.EType;
/**
 * 
 * This class tests the converter web service
 *
 */
public class TestRestConverter {
	private static String service="http://127.0.0.1:8080/verifoo/converter";
	//String service = System.getProperty("it.polito.rest.test.URL")+"/converter";
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void TestInvalidXMLRequest() {
		javax.ws.rs.core.Response res=ClientBuilder.newClient()
			.target(service)
			.request(MediaType.APPLICATION_XML)
			.accept(MediaType.APPLICATION_XML)
			.post(Entity.entity("thisisnoreallyanxmlfile",MediaType.APPLICATION_XML));
		assertEquals(Status.BAD_REQUEST.getStatusCode(),res.getStatus());
		assertTrue(res.readEntity(ApplicationError.class).getType().equals(EType.XML_VALIDATION_ERROR));
	}
	@Test
	public void TestValidConversionRequest() throws IOException {
		String xmlread=java.nio.file.Files.lines(Paths.get("./testfile/nfv5nodes7hostsSAT-WEBwithParsingString.xml")).collect(Collectors.joining("\n"));
		Response res = ClientBuilder.newClient()
				.target(service)
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.post(Entity.entity(xmlread,MediaType.APPLICATION_XML));
		assertEquals(Status.OK.getStatusCode(), res.getStatus());
		assertTrue(res.readEntity(String.class).contains("NodeRef"));

	}
	@Test
	public void TestValidVoidXMLRequest() {
		javax.ws.rs.core.Response res=ClientBuilder.newClient()
			.target(service)
			.request(MediaType.APPLICATION_XML)
			.accept(MediaType.APPLICATION_XML)
			.post(Entity.entity("<?xml version=\"1.0\" encoding=\"UTF-8\"?>",MediaType.APPLICATION_XML));
		assertEquals(Status.BAD_REQUEST.getStatusCode(),res.getStatus());	
		assertTrue(res.readEntity(ApplicationError.class).getType().equals(EType.XML_VALIDATION_ERROR));
	}
	@Test
	public void TestInvalidConversionRequest() throws IOException {
		String xmlread=java.nio.file.Files.lines(Paths.get("./testfile/nfv5nodes7hostsSAT-WEBwithInvParsingString.xml")).collect(Collectors.joining("\n"));
		Response res = ClientBuilder.newClient()
				.target(service)
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.post(Entity.entity(xmlread,MediaType.APPLICATION_XML));
		assertEquals(Status.OK.getStatusCode(), res.getStatus());
		assertFalse(res.readEntity(String.class).contains("NodeRef"));
	}
	@Test
	public void TestInvalidGetRequest() {
		javax.ws.rs.core.Response res=ClientBuilder.newClient()
			.target(service)
			.request(MediaType.APPLICATION_XML)
			.accept(MediaType.APPLICATION_XML)
			.get();
		assertEquals(Status.METHOD_NOT_ALLOWED.getStatusCode(),res.getStatus());
	}
	@Test
	public void TestInvalidPutRequest() {
		javax.ws.rs.core.Response res=ClientBuilder.newClient()
			.target(service)
			.request(MediaType.APPLICATION_XML)
			.accept(MediaType.APPLICATION_XML)
			.put(Entity.entity("<?xml version=\"1.0\" encoding=\"UTF-8\"?>",MediaType.APPLICATION_XML));
		assertEquals(Status.METHOD_NOT_ALLOWED.getStatusCode(),res.getStatus());
	}
	@Test
	public void TestInvalidDeleteRequest() {
		javax.ws.rs.core.Response res=ClientBuilder.newClient()
			.target(service)
			.request(MediaType.APPLICATION_XML)
			.accept(MediaType.APPLICATION_XML)
			.delete();
		assertEquals(Status.METHOD_NOT_ALLOWED.getStatusCode(),res.getStatus());
	}

	
}
