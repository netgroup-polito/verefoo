package it.polito.verifoo.rest.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestRestConvConcurr {

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
	public void test() throws IOException {
		Concurrency c=new Concurrency("./testfile/nfv3nodes3hostsSAT-MAIL.xml","./testfile/nfv3nodes3hostsSAT-MAIL.xml", "./testfile/nfv3nodes3hostsSAT-MAIL.xml","./testfile/nfv3nodes3hostsSAT-MAIL.xml");
		c.runConcurrent(5);
		for(int j=0;j<4;j++){
			for(int i=0;i<5;i++){
				String file1="result"+i+"-"+j+".xml";
				assertTrue(java.nio.file.Files.lines(Paths.get(file1)).collect(Collectors.joining("\n")).contains("NodeRef"));
				java.nio.file.Files.delete(Paths.get(file1));
			}
		}
	}

}
