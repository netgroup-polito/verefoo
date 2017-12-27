package it.polito.verifoo.rest.test;

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
import org.junit.runner.RunWith;

import com.anarsoft.vmlens.concurrent.junit.ConcurrentTestRunner;
import com.anarsoft.vmlens.concurrent.junit.ThreadCount;
@RunWith(ConcurrentTestRunner.class)

public class TestRestConcurrency {
    private final static int THREAD_COUNT = 5;
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
    @ThreadCount(THREAD_COUNT)
	public void TestConcurrentRequest() throws IOException {
		//String service="http://127.0.0.1:8080/verifoo/deployment";
		String service = System.getProperty("it.polito.rest.test.URL")+"/deployment";
		String xmlread=java.nio.file.Files.lines(Paths.get("./testfile/nfv3nodes3hostsSAT-MAIL.xml")).collect(Collectors.joining("\n"));
		javax.ws.rs.core.Response res=ClientBuilder.newClient()
			.target(service)
			.request(MediaType.APPLICATION_XML)
			.accept(MediaType.APPLICATION_XML)
			.post(Entity.entity(xmlread, MediaType.APPLICATION_XML));
		assertEquals(Status.OK.getStatusCode(),res.getStatus());
		assertTrue(res.readEntity(String.class).contains("isSat=\"true\""));
	}
	@Test
    @ThreadCount(THREAD_COUNT)
	public void TestConcurrentLog() {
		String service=System.getProperty("it.polito.rest.test.URL")+"/log";
		javax.ws.rs.core.Response res=ClientBuilder.newClient()
				.target(service)
				.request(MediaType.TEXT_HTML)
				.accept(MediaType.TEXT_HTML)
				.get();
			assertEquals(Status.OK.getStatusCode(),res.getStatus());
	}
	@Test
    @ThreadCount(THREAD_COUNT)
	public void TestConcurrentConverter() throws IOException {
		String service = System.getProperty("it.polito.rest.test.URL")+"/converter";
		String xmlread=java.nio.file.Files.lines(Paths.get("./testfile/nfv5nodes7hostsSAT-WEBwithParsingString.xml")).collect(Collectors.joining("\n"));
		Response res = ClientBuilder.newClient()
				.target(service)
				.request(MediaType.APPLICATION_XML)
				.accept(MediaType.APPLICATION_XML)
				.post(Entity.entity(xmlread,MediaType.APPLICATION_XML));
		assertEquals(Status.OK.getStatusCode(), res.getStatus());
		assertTrue(res.readEntity(String.class).contains("NodeRef"));
	}

}
