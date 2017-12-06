/**
 * 
 */
package it.polito.verifoo.rest.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

import it.polito.verifoo.rest.jaxb.NFV;


public class TestRestClient {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}
	final String target = "http://127.0.0.1:8080/verifoo";
	//final String target = "http://deploymentfoo.eu-de.mybluemix.net";
	@Test
	public void testdeploymentService() {
		try {
			String xmlread=java.nio.file.Files.lines(Paths.get("./testfile/nfv3nodes3hostsSAT-MAIL.xml")).collect(Collectors.joining("\n"));
			//System.out.println(xmlread);
			Response res = ClientBuilder.newClient()
					.target(target)
					.path("/deployment")
					.request(MediaType.APPLICATION_XML)
					.accept(MediaType.APPLICATION_XML)
					.post(Entity.entity(xmlread,MediaType.APPLICATION_XML));
			assertEquals(Status.OK.getStatusCode(), res.getStatus());
		} catch (IOException e) {
			fail(e.toString());
		}
	}
	@Test
	public void testTranslatorOnly() {
		try {
			String xmlread=java.nio.file.Files.lines(Paths.get("./testfile/nfv5nodes7hostsSAT-WEBwithParsingString.xml")).collect(Collectors.joining("\n"));
			//System.out.println(xmlread);
			Response res = ClientBuilder.newClient()
					.target(target)
					.path("/converter")
					.request(MediaType.APPLICATION_XML)
					.accept(MediaType.APPLICATION_XML)
					.post(Entity.entity(xmlread,MediaType.APPLICATION_XML));
			assertEquals(Status.OK.getStatusCode(),res.getStatus());
		} catch (IOException e) {
			fail(e.toString());
		}
	}
	@Test
	public void testBadGraph() {
		try {
			String xmlread=java.nio.file.Files.lines(Paths.get("./testfile/nfv5nodes7hostsUNSAT-WEB.xml")).collect(Collectors.joining("\n"));
			//System.out.println(xmlread);
			Response res = ClientBuilder.newClient()
					.target(target)
					.path("/deployment")
					.request(MediaType.APPLICATION_XML)
					.accept(MediaType.APPLICATION_XML)
					.post(Entity.entity(xmlread,MediaType.APPLICATION_XML));
			assertEquals(Status.BAD_REQUEST.getStatusCode(),res.getStatus());
		} catch (IOException e) {
			fail(e.toString());
		}
	}
	@Test
	public void testBadRequest() {
		try {
			String xmlread=java.nio.file.Files.lines(Paths.get("./testfile/nfvNoXml.txt")).collect(Collectors.joining("\n"));
			//System.out.println(xmlread);
			Response res = ClientBuilder.newClient()
					.target(target)
					.path("/deployment")
					.request(MediaType.APPLICATION_XML)
					.accept(MediaType.APPLICATION_XML)
					.post(Entity.entity(xmlread,MediaType.APPLICATION_XML));
			assertEquals(Status.BAD_REQUEST.getStatusCode(),res.getStatus());
		} catch (IOException e) {
			fail(e.toString());
		}
	}
}
