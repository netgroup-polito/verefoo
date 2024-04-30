/**
 * 
 */
package it.polito.verefoo.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import com.microsoft.z3.Status;

import it.polito.verefoo.VerefooProxy;
import it.polito.verefoo.VerefooSerializer;
import it.polito.verefoo.extra.BadGraphError;
import it.polito.verefoo.jaxb.*;
import it.polito.verefoo.translator.Translator;
import it.polito.verefoo.utils.VerificationResult;
/**
 * 
 * This class runs some tests in order to check the correctness of the auto-configuration module of Algorithm Atomic Predicates with the REACT version enabled.
 * 
 */
public class TestREACTCorrectness {
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception { // run once before all tests to compute results then check them
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
	
	private VerefooSerializer test(String file) throws Exception{ 
		List<Node> tmp = new ArrayList<>();
		// create a JAXBContext capable of handling the generated classes
        System.out.println("===========FILE " + file + "===========");
		long beginAll=System.currentTimeMillis();
        JAXBContext jc = JAXBContext.newInstance( "it.polito.verefoo.jaxb" );
        // create an Unmarshaller
        Unmarshaller u = jc.createUnmarshaller();
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI); 
        Schema schema = sf.newSchema( new File( "./xsd/nfvSchema.xsd" )); 
        u.setSchema(schema);
        NFV root = (NFV) u.unmarshal( new FileInputStream( file ) );
        
		VerefooSerializer test = new VerefooSerializer(root,"AP",true);
        
        
        if(test.isSat()){
        		System.out.println("SAT");
    	}
    	else{
    		System.out.println("UNSAT");
    	}
		long endAll=System.currentTimeMillis();
        System.out.println("Total time -> " + (endAll-beginAll)+"ms" );

        return test;
	}
/********************************************************************** Tests **********************************************************************/
// The Test files are found in /testfile/REACT.
/**
 * Note: In each file there is at least one previous NSF that has to be modified. The tests performed for each file are considering 3 aspects
 * a) SAT - UNSAT
 * b) Correct number of firewalls allocated
 * c) Correct number of firewall rules allocated 
 *
 * Test1 = simple solution with 3 endpoints connected by a FORWARDER. Reach/Isol are swapped and: current FW has to be removed, a new one has to be instantiated (zero rules).
 * 
 * Test2 = 5 endpoints connected by multiple FORWARDER. An Isol is swapped into Reach: current FW has to be reconfigured, with additional rules to support the situation.
 * 
 * Test3 = more complex scenario with NATs and different rules.
 * /
	
	/**
	 * This test checks if Test1 result have the expected SAT status
	 */
	@Test
	public void test1_a(){
		try {
			VerefooSerializer result = test( "./testfile/REACT/Test1.xml"); 
			assertTrue(result.isSat());
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
	/**
	 * This test checks if Test1 result have the expected number of firewalls
	 */
	@Test
	public void test1_b(){
		try {
			VerefooSerializer result = test("./testfile/REACT/Test1.xml"); 
			assertTrue(result.isSat());
			List<Node> listFW = result.getNfv().getGraphs().getGraph().get(0).getNode().stream().filter(n -> n.getFunctionalType().equals(FunctionalTypes.FIREWALL)).collect(Collectors.toList());
			assertTrue(listFW.size() == 1);
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
	/**
	 * This test checks if Test1 result have the expected number of firewalls
	 */
	@Test
	public void test1_c(){
		try {
			VerefooSerializer result = test("./testfile/REACT/Test1.xml"); 
			assertTrue(result.isSat());
			List<Node> listFW = result.getNfv().getGraphs().getGraph().get(0).getNode().stream().filter(n -> n.getFunctionalType().equals(FunctionalTypes.FIREWALL)).collect(Collectors.toList());
			assertTrue(listFW.size() == 1);
			
			Node node = listFW.get(0);
			assertTrue(node.getConfiguration().getFirewall().getElements().size() == 0); // firewall should have 0 rules and default deny
		} catch (Exception e) {
			fail(e.toString());
		}
	}

	/**
	 * This test checks if Test2 result have the expected SAT status
	 */
	@Test
	public void test2_a(){
		try {
			VerefooSerializer result = test( "./testfile/REACT/Test2.xml"); 
			assertTrue(result.isSat());
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
	/**
	 * This test checks if Test2 result have the expected number of firewalls
	 */
	@Test
	public void test2_b(){
		try {
			VerefooSerializer result = test("./testfile/REACT/Test2.xml"); 
			assertTrue(result.isSat());
			List<Node> listFW = result.getNfv().getGraphs().getGraph().get(0).getNode().stream().filter(n -> n.getFunctionalType().equals(FunctionalTypes.FIREWALL)).collect(Collectors.toList());
			assertTrue(listFW.size() == 1);
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
	/**
	 * This test checks if Test2 result have the expected number of firewalls
	 */
	@Test
	public void test2_c(){
		try {
			VerefooSerializer result = test("./testfile/REACT/Test2.xml"); 
			assertTrue(result.isSat());
			List<Node> listFW = result.getNfv().getGraphs().getGraph().get(0).getNode().stream().filter(n -> n.getFunctionalType().equals(FunctionalTypes.FIREWALL)).collect(Collectors.toList());
			assertTrue(listFW.size() == 1);
			
			Node node = listFW.get(0);
			assertTrue(node.getConfiguration().getFirewall().getElements().size() == 4); // firewall should have 4 rules
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
	/**
	 * This test checks if Test3 result have the expected SAT status
	 */
	@Test
	public void test3_a(){
		try {
			VerefooSerializer result = test( "./testfile/REACT/Test3.xml"); 
			assertTrue(result.isSat());
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
	/**
	 * This test checks if Test3 result have the expected number of firewalls
	 */
	@Test
	public void test3_b(){
		try {
			VerefooSerializer result = test("./testfile/REACT/Test3.xml"); 
			assertTrue(result.isSat());
			List<Node> listFW = result.getNfv().getGraphs().getGraph().get(0).getNode().stream().filter(n -> n.getFunctionalType().equals(FunctionalTypes.FIREWALL)).collect(Collectors.toList());
			assertTrue(listFW.size() == 3);
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
	/**
	 * This test checks if Test3 result have the expected number of firewalls
	 */
	@Test
	public void test3_c(){
		try {
			VerefooSerializer result = test("./testfile/REACT/Test3.xml"); 
			assertTrue(result.isSat());
			List<Node> listFW = result.getNfv().getGraphs().getGraph().get(0).getNode().stream().filter(n -> n.getFunctionalType().equals(FunctionalTypes.FIREWALL)).collect(Collectors.toList());
			assertTrue(listFW.size() == 3);
			
			int acc = 0;
			Node node = listFW.get(0);
			acc += node.getConfiguration().getFirewall().getElements().size();
			node = listFW.get(1);
			acc += node.getConfiguration().getFirewall().getElements().size();
			node = listFW.get(2);
			acc += node.getConfiguration().getFirewall().getElements().size();
			
			assertTrue(acc == 4); // 3 firewalls; one with zero rules and others with 2 rules each
			
			
		} catch (Exception e) {
			fail(e.toString());
		}
	}	
}