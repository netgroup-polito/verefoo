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
 * This class runs some tests in order to check the correctness of the auto-configuration module of Algorithm Atomic Predicates
 *
 */
public class TestAPCorrectness {
	
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
        
		VerefooSerializer test = new VerefooSerializer(root,"AP");
        
        
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
// The Test files are found in /testfile/RegressioneTestCases.
/**
 * Note: The tests of each file are divided into 4 categories, each category defines the expected result; 
 * a) SAT - UNSAT  (Correctness1)
 * b) Correct number of firewalls allocated (Correctness2)
 * c) Correct number of firewall rules allocated (Correctness3) --> if possible
 * d) Correct firewall rules allocated (Correctness4) --> if possible
 * e) Correct firewall position --> Not all
 * /
/********************************************************************* Test1_X (Placement Tests) ********************************************************************/
	
	/**
	 * This test checks if Test1_1 result have the expected SAT status
	 */
	@Test
	public void test1Sec1Correctness1(){
		try {
			VerefooSerializer result = test( "./testfile/RegressioneTestCases/Test1_1.xml"); 
			assertTrue(result.isSat());
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
	/**
	 * This test checks if Test1_1 result have the expected number of firewalls
	 */
	@Test
	public void test1Sec1Correctness2(){
		try {
			VerefooSerializer result = test("./testfile/RegressioneTestCases/Test1_1.xml"); 
			assertTrue(result.isSat());
			List<Node> listFW = result.getNfv().getGraphs().getGraph().get(0).getNode().stream().filter(n -> n.getFunctionalType().equals(FunctionalTypes.FIREWALL)).collect(Collectors.toList());
			assertTrue(listFW.size() == 0);
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
	/**
	 * This test checks if Test1_2 result have the expected SAT status
	 */
	@Test
	public void test1Sec2Correctness1(){
		try {
			VerefooSerializer result = test( "./testfile/RegressioneTestCases/Test1_2.xml"); 
			assertTrue(result.isSat());
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
	/**
	 * This test checks if Test1_2 result have the expected number of firewalls
	 */
	@Test
	public void test1Sec2Correctness2(){
		try {
			VerefooSerializer result = test("./testfile/RegressioneTestCases/Test1_2.xml"); 
			assertTrue(result.isSat());
			List<Node> listFW = result.getNfv().getGraphs().getGraph().get(0).getNode().stream().filter(n -> n.getFunctionalType().equals(FunctionalTypes.FIREWALL)).collect(Collectors.toList());
			assertTrue(listFW.size() == 1);
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
	/**
	 * This test checks if Test1_2 result have the expected number of firewall rules
	 */
	@Test
	public void test1Sec2Correctness3(){
		try {
			VerefooSerializer result = test("./testfile/RegressioneTestCases/Test1_2.xml"); 
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
	 * This test checks if Test1_3 result have the expected SAT status
	 */
	@Test
	public void test1Sec3Correctness1(){
		try {
			VerefooSerializer result = test( "./testfile/RegressioneTestCases/Test1_3.xml"); 
			assertTrue(result.isSat());
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
	/**
	 * This test checks if Test1_3 result have the expected number of firewalls
	 */
	@Test
	public void test1Sec3Correctness2(){
		try {
			VerefooSerializer result = test("./testfile/RegressioneTestCases/Test1_3.xml"); 
			assertTrue(result.isSat());
			List<Node> listFW = result.getNfv().getGraphs().getGraph().get(0).getNode().stream().filter(n -> n.getFunctionalType().equals(FunctionalTypes.FIREWALL)).collect(Collectors.toList());
			assertTrue(listFW.size() == 1);
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
	/**
	 * This test checks if Test1_3 result have the expected number of firewall rules
	 */
	@Test
	public void test1Sec3Correctness3(){
		try {
			VerefooSerializer result = test("./testfile/RegressioneTestCases/Test1_3.xml"); 
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
	 * This test checks if Test1_4 result have the expected SAT status
	 */
	@Test
	public void test1Sec4Correctness1(){
		try {
			VerefooSerializer result = test( "./testfile/RegressioneTestCases/Test1_4.xml"); 
			assertTrue(result.isSat());
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
	/**
	 * This test checks if Test1_4 result have the expected number of firewalls
	 */
	@Test
	public void test1Sec4Correctness2(){
		try {
			VerefooSerializer result = test("./testfile/RegressioneTestCases/Test1_4.xml"); 
			assertTrue(result.isSat());
			List<Node> listFW = result.getNfv().getGraphs().getGraph().get(0).getNode().stream().filter(n -> n.getFunctionalType().equals(FunctionalTypes.FIREWALL)).collect(Collectors.toList());
			assertTrue(listFW.size() == 1);
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
	/**
	 * This test checks if Test1_4 result have the expected number of firewall rules
	 */
	@Test
	public void test1Sec4Correctness3(){
		try {
			VerefooSerializer result = test("./testfile/RegressioneTestCases/Test1_4.xml"); 
			assertTrue(result.isSat());
			List<Node> listFW = result.getNfv().getGraphs().getGraph().get(0).getNode().stream().filter(n -> n.getFunctionalType().equals(FunctionalTypes.FIREWALL)).collect(Collectors.toList());
			assertTrue(listFW.size() == 1);
			
			Node node = listFW.get(0);
			assertTrue(node.getConfiguration().getFirewall().getElements().size() == 4); // firewall should have 4 deny rules and default allow
			
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
	/**
	 * This test checks if Test1_4 result have the expected firewall rules
	 */
	@Test
	public void test1Sec4Correctness4(){
		try {
			VerefooSerializer result = test("./testfile/RegressioneTestCases/Test1_4.xml"); 
			assertTrue(result.isSat());
			List<Node> listFW = result.getNfv().getGraphs().getGraph().get(0).getNode().stream().filter(n -> n.getFunctionalType().equals(FunctionalTypes.FIREWALL)).collect(Collectors.toList());
			assertTrue(listFW.size() == 1);
			
			Node node = listFW.get(0);
			assertTrue(node.getConfiguration().getFirewall().getElements().size() == 4); // firewall should have 4 deny rules and default allow
			
			boolean correct1=false;
			boolean correct2=false;
			
			for(int i =0 ; i <4 ; i ++) {
			List<Elements> elements =node.getConfiguration().getFirewall().getElements(); // verify that firewall rules didnot change (presuming order is the same)
			if(elements.get(i).getSource().startsWith("130.10.0") && elements.get(i).getDestination().startsWith("40.40.41") )
				correct1=true;;
			if(elements.get(i).getSource().startsWith("40.40.41") && elements.get(i).getDestination().startsWith("130.10.0") )
				correct2=true;
			}
			
			assertTrue(correct1&correct2);
			
			
			
		} catch (Exception e) {
			fail(e.toString());
		}
	}
		
/********************************************************************* Test2_X (Placement Tests) ********************************************************************/
	
	/**
	 * This test checks if Test2_1 result satisfy the 3 Correctness
	 */
	@Test
	public void test2Sec1Correctness(){
		try {
			VerefooSerializer result = test("./testfile/RegressioneTestCases/Test2_1.xml"); 
			// Correctness 1
			assertTrue(result.isSat());
			// Correctness 2
			List<Node> listFW = result.getNfv().getGraphs().getGraph().get(0).getNode().stream().filter(n -> n.getFunctionalType().equals(FunctionalTypes.FIREWALL)).collect(Collectors.toList());
			assertTrue(listFW.size() == 1);
			// Correctness 3
			Node node = listFW.get(0);
			assertTrue(node.getConfiguration().getFirewall().getElements().size() == 0); // firewall should have 0 rules
			
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
	
	/**
	 * This test checks if Test2_2 result have the 4 Correctness
	 */
	@Test
	public void test2Sec2Correctness(){
		try {
			VerefooSerializer result = test("./testfile/RegressioneTestCases/Test2_2.xml"); 
			//Correctness 1
			assertTrue(result.isSat());
			//Correctness 2
			List<Node> listFW = result.getNfv().getGraphs().getGraph().get(0).getNode().stream().filter(n -> n.getFunctionalType().equals(FunctionalTypes.FIREWALL)).collect(Collectors.toList());
			assertTrue(listFW.size() == 1);
			//Correctness 3
			Node node = listFW.get(0);
			assertTrue(node.getConfiguration().getFirewall().getElements().size() == 4); // firewall should have 4 rules
			//Correctness 4
			List<Elements> elements =node.getConfiguration().getFirewall().getElements(); // verify that firewall rules didnot change (presuming order is the same)
			
			boolean correct1=false;
			boolean correct2=false;
			
			for(int i =0 ; i <4 ; i ++) {
			if(elements.get(i).getSource().startsWith("130.10.0") && elements.get(i).getDestination().startsWith("40.40.42") &&
					elements.get(i).getSrcPort().equals("0-65535") &&  elements.get(i).getDstPort().equals("80") )
				correct1=true;
			if(elements.get(i).getSource().startsWith("40.40.42") && elements.get(i).getDestination().startsWith("130.10.0") &&
					elements.get(i).getSrcPort().equals("0-65535") &&  elements.get(i).getDstPort().equals("80") )
			correct2=true;
			}
			
			assertTrue(correct1&&correct2);
			
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
	/**
	 * This test checks if Test2_5 result have the 4 Correctness. Atomic Predicate produces Much more firewall rules than Maximal Flows,
	 * because it is more rigorous.
	 * In this test Atomic Predicates produce one firewall with 12 firewall rules while Maximal Flows produces only 2 firewall rules,
	 * but more generic.
	 */
	@Test
	public void test2Sec5Correctness(){
		try {
			VerefooSerializer result = test("./testfile/RegressioneTestCases/Test2_5.xml"); 
			//Correctness 1
			assertTrue(result.isSat());
			//Correctness 2
			List<Node> listFW = result.getNfv().getGraphs().getGraph().get(0).getNode().stream().filter(n -> n.getFunctionalType().equals(FunctionalTypes.FIREWALL)).collect(Collectors.toList());
			assertTrue(listFW.size() == 1);
			//Correctness 3
			Node node = listFW.get(0);
			assertTrue(node.getConfiguration().getFirewall().getElements().size() == 12); // firewall should have 12 rules
			//Correctness 4
			List<Elements> elements =node.getConfiguration().getFirewall().getElements(); // verify that firewall rules didnot change 

			boolean correct1 = false;
			boolean correct2 = false;
			boolean correct3 = false;
			
			for(int i =0 ; i<12 ; i++) {
				if(elements.get(i).getSource().equals("40.40.43.-1") && elements.get(i).getDestination().equals("130.10.0.2")) {
					if(elements.get(i).getDstPort().equals("80"))
						correct1=true; // HTTP port 80 access Security Requirements satisfied for 40.40.43.-1
				}
				if(elements.get(i).getSource().equals("40.40.41.-1") && elements.get(i).getDestination().equals("130.10.0.2") ) {
					if(elements.get(i).getDstPort().equals("53"))
						correct2=true; // UDP port 53 access Security Requirements satisfied for 40.40.41.-1
				}
				if(elements.get(i).getSource().equals("40.40.42.-1") && elements.get(i).getDestination().equals("130.10.0.2") ) {
					if(elements.get(i).getDstPort().equals("80"))
						correct3=true; // HTTP port 80 access Security Requirements satisfied for 40.40.42.-1
				}
			}
			
			assertTrue(correct1 && correct2 && correct3);
			
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
/********************************************************************* Test3_X (Placement Tests) ********************************************************************/
	
	/**
	 * This test checks if Test3_1 result have the 3 Correctness. Test if in large network the firewall is placed closest to the central forwarder
	 */
	@Test
	public void test3Sec1Correctness(){
		try {
			VerefooSerializer result = test("./testfile/RegressioneTestCases/Test3_1.xml"); 
			//Correctness 1
			assertTrue(result.isSat());
			//Correctness 2
			List<Node> listFW = result.getNfv().getGraphs().getGraph().get(0).getNode().stream().filter(n -> n.getFunctionalType().equals(FunctionalTypes.FIREWALL)).collect(Collectors.toList());
			assertTrue(listFW.size() == 2);
			//Correctness 3
			Node node1 = listFW.get(0);
			Node node2 = listFW.get(1);
			assertTrue(node1.getConfiguration().getFirewall().getElements().size() == 0); // firewall should have 0 rules
			assertTrue(node2.getConfiguration().getFirewall().getElements().size() == 0); // firewall should have 0 rules
			
			//Correctness Placement
			assertTrue(node1.getName().equals("1.0.0.2") || node1.getName().equals("1.0.0.3") || node1.getName().equals("1.0.0.1") || node1.getName().equals("1.0.0.6"));
			assertTrue(node2.getName().equals("1.0.0.3") || node2.getName().equals("1.0.0.2") || node2.getName().equals("1.0.0.1") || node2.getName().equals("1.0.0.6"));
			
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
	/**
	 * This test checks if Test3_2 result have the 4 Correctness. This test is used to make sure firewall allocation doesn't create path conflict
	 */
	@Test
	public void test3Sec2Correctness(){
		try {
			VerefooSerializer result = test("./testfile/RegressioneTestCases/Test3_2.xml"); 
			//Correctness 1
			assertTrue(result.isSat());
			//Correctness 2
			List<Node> listFW = result.getNfv().getGraphs().getGraph().get(0).getNode().stream().filter(n -> n.getFunctionalType().equals(FunctionalTypes.FIREWALL)).collect(Collectors.toList());
			assertTrue(listFW.size() == 2);
			//Correctness 3
			 Node node1 = listFW.get(0);
			 Node node2 = listFW.get(1);
			 
			//Correctness Placement
			assertTrue(node1.getName().equals("1.0.0.2") || node1.getName().equals("1.0.0.7") || node1.getName().equals("1.0.0.1") );
			assertTrue(node2.getName().equals("1.0.0.7") || node2.getName().equals("1.0.0.2") || node1.getName().equals("1.0.0.1") );
			
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
	
	/**
	 * This test checks if Test3_3 result have the 3 Correctness. This Test provide consistent result with MF but in-determinstic result with AP
	 * All solutions offered by Atomic Predicates are equivalent
	 */
	@Test
	public void test3Sec3Correctness(){
		try {
			VerefooSerializer result = test("./testfile/RegressioneTestCases/Test3_3.xml"); 
			//Correctness 1
			assertTrue(result.isSat());
			//Correctness 2
			List<Node> listFW = result.getNfv().getGraphs().getGraph().get(0).getNode().stream().filter(n -> n.getFunctionalType().equals(FunctionalTypes.FIREWALL)).collect(Collectors.toList());
			assertTrue(listFW.size() == 2);
			//Correctness of different solutions
			Node node1 = listFW.get(0);
			Node node2 = listFW.get(1);
			
			//Correctness Placement
			assertTrue(node1.getName().equals("1.0.0.1") || node1.getName().equals("1.0.0.2") || node1.getName().equals("1.0.0.3") || node1.getName().equals("1.0.0.6") || node1.getName().equals("1.0.0.7") );
			assertTrue(node2.getName().equals("1.0.0.1") || node2.getName().equals("1.0.0.2") || node2.getName().equals("1.0.0.3") || node2.getName().equals("1.0.0.6") || node2.getName().equals("1.0.0.7") );
			
			
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
	/**
	 * This test checks if Test3_4 result have the 4 Correctness. Test different isolation paths and different port/protocol reachability.
	 * In this case Atomic Predicates offers two correct solutions
	 */
	@Test
	public void test3Sec4Correctness(){
		try {
			VerefooSerializer result = test("./testfile/RegressioneTestCases/Test3_4.xml"); 
			//Correctness 1
			assertTrue(result.isSat());
			//Correctness 2
			List<Node> listFW = result.getNfv().getGraphs().getGraph().get(0).getNode().stream().filter(n -> n.getFunctionalType().equals(FunctionalTypes.FIREWALL)).collect(Collectors.toList());
			assertTrue(listFW.size() == 2);
			
			// In this case, Atomic Predicates have multiple results but all correct. The only difference with the results is the allocation place
			// This behavior is due to the network topology deign
			Node node1 = listFW.get(0);
			Node node2 = listFW.get(1);
			// This test case has 4 solutions, test all the possible solutions
			assertTrue(node1.getName().equals("1.0.0.1") || node1.getName().equals("1.0.0.2") || node1.getName().equals("1.0.0.3") || node1.getName().equals("1.0.0.7") || node1.getName().equals("1.0.0.6"));
			assertTrue(node2.getName().equals("1.0.0.1") || node2.getName().equals("1.0.0.2") || node2.getName().equals("1.0.0.3") || node2.getName().equals("1.0.0.7") || node2.getName().equals("1.0.0.6"));
			
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
	/**
	 * This test checks if Test3_5 result have the 2 Correctness. 
	 */
	@Test
	public void test3Sec5Correctness(){
		try {
			VerefooSerializer result = test("./testfile/RegressioneTestCases/Test3_5.xml"); 
			//Correctness 1
			assertTrue(result.isSat());
			//Correctness 2
			List<Node> listFW = result.getNfv().getGraphs().getGraph().get(0).getNode().stream().filter(n -> n.getFunctionalType().equals(FunctionalTypes.FIREWALL)).collect(Collectors.toList());
			assertTrue(listFW.size() == 2);
			

		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
	/**
	 * This test checks if having conflict in Security Requirements generate UNSAT result
	 */
	@Test
	public void test3Sec6Correctness(){
		try {
			VerefooSerializer result = test("./testfile/RegressioneTestCases/Test3_6.xml"); 
			//Correctness 1
			assertTrue(!result.isSat());
			

		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
	/**
	 * This test checks if having only reachability requirements no firewalls are allocated 
	 */
	@Test
	public void test3Sec7Correctness(){
		try {
			VerefooSerializer result = test("./testfile/RegressioneTestCases/Test3_7.xml"); 
			//Correctness 1
			assertTrue(result.isSat());
			//Correctness 2
			List<Node> listFW = result.getNfv().getGraphs().getGraph().get(0).getNode().stream().filter(n -> n.getFunctionalType().equals(FunctionalTypes.FIREWALL)).collect(Collectors.toList());
			assertTrue(listFW.size() == 0);
			

		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
	
	/**
	 * This test checks if there is port conflicts DENY/ALLOW result is UNSAT in Atomic Predicates 
	 */
	@Test
	public void test3Sec8Correctness(){
		try {
			VerefooSerializer result = test("./testfile/RegressioneTestCases/Test3_8.xml"); 
			//Correctness 1
			assertTrue(!result.isSat());
			

		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
/********************************************************************* Test4_X (Placement Tests) ********************************************************************/
// With Load Balancer
	
	/**
	 * This test checks if the algorithm always prefers lower number of firewall more than lower number of firewall rules. In atomic Predicates
	 * the firewall is allocated before only some servers 130.10.0.-1 (Atomic Predicates assumes default allow to 130.10.1.-1 if not specified in Scurity Requirements)
	 */
	@Test
	public void test4Sec1Correctness(){
		try {
			VerefooSerializer result = test("./testfile/RegressioneTestCases/Test4_1.xml"); 
			//Correctness 1
			assertTrue(result.isSat());
			//Correctness 2
			List<Node> listFW = result.getNfv().getGraphs().getGraph().get(0).getNode().stream().filter(n -> 
			{ 
			if(n.getFunctionalType() != null && n.getFunctionalType().equals(FunctionalTypes.FIREWALL) )
				return true;
			else 
				return false;

			} ).collect(Collectors.toList());
			
			assertTrue(listFW.size() == 1);
			
			Node node1 = listFW.get(0);
			assertTrue(node1.getConfiguration().getFirewall().getElements().size() == 2); // firewall should have 2 rules
			
			assertTrue(node1.getName().equals("1.0.0.3")); // only one possible solution with minimum number of firewalls
			
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
	/**
	 * This test checks if the algorithm always prefers lower number of firewall more than lower number of firewall rules.
	 */
	@Test
	public void test4Sec2Correctness(){
		try {
			VerefooSerializer result = test("./testfile/RegressioneTestCases/Test4_2.xml"); 
			//Correctness 1
			assertTrue(result.isSat());
			//Correctness 2
			List<Node> listFW = result.getNfv().getGraphs().getGraph().get(0).getNode().stream().filter(n -> 
			{ 
			if(n.getFunctionalType() != null && n.getFunctionalType().equals(FunctionalTypes.FIREWALL) )
				return true;
			else 
				return false;

			} ).collect(Collectors.toList());
			
			assertTrue(listFW.size() == 1);
			
			Node node1 = listFW.get(0);
			assertTrue(node1.getConfiguration().getFirewall().getElements().size() == 12); // firewall should have 12 rules
			
			assertTrue(node1.getName().equals("1.0.0.12")); // only one possible solution with minimum number of firewalls
			
			List<Elements> elements =node1.getConfiguration().getFirewall().getElements(); // verify that firewall rules didnot change 

			boolean correct1 = false;
			boolean correct2 = false;
			boolean correct3 = false;
			
			for(int i =0 ; i<12 ; i++) {
				if(elements.get(i).getSource().equals("40.40.43.-1") && elements.get(i).getDestination().equals("130.10.0.2")) {
					if(elements.get(i).getDstPort().equals("80"))
						correct1=true; // HTTP port 80 access Security Requirements satisfied for 40.40.43.-1
				}
				if(elements.get(i).getSource().equals("40.40.41.-1") && elements.get(i).getDestination().equals("130.10.0.2") ) {
					if(elements.get(i).getDstPort().equals("80"))
						correct2=true; // TCP port 80 access Security Requirements satisfied for 40.40.41.-1
				}
				if(elements.get(i).getSource().equals("40.40.42.-1") && elements.get(i).getDestination().equals("130.10.1.2") ) {
					if(elements.get(i).getDstPort().equals("80"))
						correct3=true; // HTTP port 80 access Security Requirements satisfied for 40.40.42.-1
				}
			}
			
			assertTrue(correct1&&correct2&&correct3);
			
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
	/**
	 * This test checks if the framework is able to interpret servers combining (130.10.0.1 + 130.10.0.2 --> 130.10.0.-1) for atomic predicates
	 */
	@Test
	public void test4Sec3Correctness(){
		try {
			VerefooSerializer result = test("./testfile/RegressioneTestCases/Test4_3.xml"); 
			//Correctness 1
			assertTrue(result.isSat());
			//Correctness 2
			List<Node> listFW = result.getNfv().getGraphs().getGraph().get(0).getNode().stream().filter(n -> 
			{ 
			if(n.getFunctionalType() != null && n.getFunctionalType().equals(FunctionalTypes.FIREWALL) )
				return true;
			else 
				return false;

			} ).collect(Collectors.toList());
			
			assertTrue(listFW.size() == 1);
			
			Node node1 = listFW.get(0);
			assertTrue(node1.getConfiguration().getFirewall().getElements().size() == 8); // firewall should have 8 rules
			
			assertTrue(node1.getName().equals("1.0.0.12")); 
			
			List<Elements> elements =node1.getConfiguration().getFirewall().getElements();

			boolean correct1 = false;
			boolean correct2 = false;
			boolean correct3 = false;
			boolean correct4 = false;
			
			for(int i =0 ; i<8 ; i++) { 
				if(elements.get(i).getSource().equals("40.40.44.-1") && elements.get(i).getDestination().equals("130.10.0.1")) {
						correct1=true; // framework successfully interpreted 130.10.0.-1 into 130.10.0.1
				}
				if(elements.get(i).getSource().equals("40.40.44.-1") && elements.get(i).getDestination().equals("130.10.0.2") ) {
						correct2=true; // framework successfully interpreted 130.10.0.-1 into 130.10.0.2
				}
				if(elements.get(i).getSource().equals("40.40.44.-1") && elements.get(i).getDestination().equals("130.10.1.1") ) {
						correct3=true; // framework successfully interpreted 130.10.1.-1 into 130.10.1.1
				}
				if(elements.get(i).getSource().equals("40.40.44.-1") && elements.get(i).getDestination().equals("130.10.1.2") ) {
					correct4=true; // framework successfully interpreted 130.10.1.-1 into 130.10.1.2
			}
			}
			
			assertTrue(correct1&&correct2&&correct3&&correct4);
			
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
	/**
	 * This test checks if the framework correctly analyzes different ports requirements (Test Multiple TCP Ports Trasnaltion)
	 */
	@Test
	public void test4Sec4Correctness(){
		try {
			VerefooSerializer result = test("./testfile/RegressioneTestCases/Test4_4.xml"); 
			
			//Correctness 1
			assertTrue(result.isSat());
			//Correctness 2
			List<Node> listFW = result.getNfv().getGraphs().getGraph().get(0).getNode().stream().filter(n -> 
			{ 
			if(n.getFunctionalType() != null && n.getFunctionalType().equals(FunctionalTypes.FIREWALL) )
				return true;
			else 
				return false;

			} ).collect(Collectors.toList());
			
			assertTrue(listFW.size() == 1);
			
			Node node1 = listFW.get(0);

			assertTrue(node1.getConfiguration().getFirewall().getElements().size() == 16); // firewall should have 16 rules
			
			assertTrue(node1.getName().equals("1.0.0.12")); 
			

		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
	/**
	 * This test checks if the framework correctly analyzes different ports requirements and protocols (Test UDP/ports translation)
	 */
	@Test
	public void test4Sec5Correctness(){
		try {
			VerefooSerializer result = test("./testfile/RegressioneTestCases/Test4_5.xml"); 
			
			//Correctness 1
			assertTrue(result.isSat());
			//Correctness 2
			List<Node> listFW = result.getNfv().getGraphs().getGraph().get(0).getNode().stream().filter(n -> 
			{ 
			if(n.getFunctionalType() != null && n.getFunctionalType().equals(FunctionalTypes.FIREWALL) )
				return true;
			else 
				return false;

			} ).collect(Collectors.toList());
			
			assertTrue(listFW.size() == 1);
			
			Node node1 = listFW.get(0);
			assertTrue(node1.getConfiguration().getFirewall().getElements().size() == 20);
			
			assertTrue(node1.getName().equals("1.0.0.12")); 
			
			List<Elements> elements =node1.getConfiguration().getFirewall().getElements();
/*			//To test rules
			boolean correct1 = false;
			boolean correct2 = false;
			boolean correct3 = false;
			boolean correct4 = false;
			
			for(int i =0 ; i<20 ; i++) { 
				if(elements.get(i).getSource().equals("40.40.41.-1") && elements.get(i).getDestination().equals("130.10.1.2") 
						&& elements.get(i).getDstPort().equals("80") && elements.get(i).getProtocol().equals(L4ProtocolTypes.UDP)) {
						correct1=true; // created rule with port 80 and UDP
				}
				if(elements.get(i).getSource().equals("40.40.41.-1") && elements.get(i).getDestination().equals("130.10.1.2") 
						&& elements.get(i).getDstPort().equals("500") && elements.get(i).getProtocol().equals(L4ProtocolTypes.UDP)) {
						correct2=true; // created rule at port 500 / UDP
				}
				if(elements.get(i).getSource().equals("40.40.41.-1") && elements.get(i).getDestination().equals("130.10.1.2") 
						&& elements.get(i).getDstPort().equals("443") && elements.get(i).getProtocol().equals(L4ProtocolTypes.UDP)) {
						correct3=true; // created rule at port 443 / UDP
				}
				if(elements.get(i).getSource().equals("40.40.42.-1") && elements.get(i).getDestination().equals("130.10.0.1") 
						&& elements.get(i).getDstPort().equals("100") && elements.get(i).getProtocol().equals(L4ProtocolTypes.UDP)) {
					correct4=true; // created rule at port 100 / UDP
			}
			}*/
			
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
	/**
	 * This test checks if the framework correctly analyzes different ports requirements and protocols (Test ANY/ports translation)
	 */
	@Test
	public void test4Sec6Correctness(){
		try {
			VerefooSerializer result = test("./testfile/RegressioneTestCases/Test4_6.xml"); 
			
			//Correctness 1
			assertTrue(result.isSat());
			//Correctness 2
			List<Node> listFW = result.getNfv().getGraphs().getGraph().get(0).getNode().stream().filter(n -> 
			{ 
			if(n.getFunctionalType() != null && n.getFunctionalType().equals(FunctionalTypes.FIREWALL) )
				return true;
			else 
				return false;

			} ).collect(Collectors.toList());
			
			assertTrue(listFW.size() == 1);
			
			Node node1 = listFW.get(0);
			assertTrue(node1.getConfiguration().getFirewall().getElements().size() == 12);
			
			assertTrue(node1.getName().equals("1.0.0.12")); 
			
			List<Elements> elements =node1.getConfiguration().getFirewall().getElements();

			boolean correct1 = false;
			boolean correct2 = false;
			boolean correct3= false;
			
			for(int i =0 ; i<12 ; i++) { 
				if(elements.get(i).getDstPort().equals("100") && elements.get(i).getProtocol().equals(L4ProtocolTypes.UDP)
						|| elements.get(i).getDstPort().equals("100") && elements.get(i).getProtocol().equals(L4ProtocolTypes.TCP)) {
						correct1=true; // ANY successfully decomposed to UDP/TCP
				}
				if(elements.get(i).getDstPort().equals("100") && elements.get(i).getProtocol().equals(L4ProtocolTypes.OTHER)) {
						correct1=true; // ANY successfully decomposed to UDP/Other
				}
				if(elements.get(i).getDstPort().equals("80") && elements.get(i).getProtocol().equals(L4ProtocolTypes.TCP)
			|| elements.get(i).getDstPort().equals("80") && elements.get(i).getProtocol().equals(L4ProtocolTypes.UDP)) {
						correct2=true; // ANY successfully decomposed to TCP
				}
				
			if(elements.get(i).getDstPort().equals("500") && elements.get(i).getProtocol().equals(L4ProtocolTypes.TCP)
			|| elements.get(i).getDstPort().equals("500") && elements.get(i).getProtocol().equals(L4ProtocolTypes.UDP)) {
						correct3=true; // ANY successfully decomposed to TCP / UDP
				}
			}
			
			assertTrue(correct1&&correct2&&correct3);
			
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
/********************************************************************* Test5_X (Placement Tests) ********************************************************************/
// With Nat (large topology)
	
	/**
	 * This test checks if the algorithm decomposes the topology into 3 halves by allocating two firewalls
	 */
	@Test
	public void test5Sec1Correctness(){
		try {
			VerefooSerializer result = test("./testfile/RegressioneTestCases/Test5_1.xml"); 
			//Correctness 1
			assertTrue(result.isSat());
			//Correctness 2
			List<Node> listFW = result.getNfv().getGraphs().getGraph().get(0).getNode().stream().filter(n -> n.getFunctionalType().equals(FunctionalTypes.FIREWALL)).collect(Collectors.toList());
			assertTrue(listFW.size() == 2); // two firewalls allocated
			
			Node node1 = listFW.get(0);
			Node node2 = listFW.get(1);

			assertTrue(node1.getName().equals("1.0.0.10") || node1.getName().equals("1.0.0.17")); 
			assertTrue(node2.getName().equals("1.0.0.10") || node2.getName().equals("1.0.0.17")); 
			
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
	/**
	 * This test checks if the correct placement of multiple firewalls behind different NATs. 
	 */
	@Test
	public void test5Sec2Correctness(){
		try {
			VerefooSerializer result = test("./testfile/RegressioneTestCases/Test5_2.xml"); 
			//Correctness 1
			assertTrue(result.isSat());
			//Correctness 2
			//Correctness 2
			List<Node> listFW = result.getNfv().getGraphs().getGraph().get(0).getNode().stream().filter(n -> 
			{ 
			if(n.getFunctionalType() != null && n.getFunctionalType().equals(FunctionalTypes.FIREWALL) )
				return true;
			else 
				return false;

			} ).collect(Collectors.toList());
			assertTrue(listFW.size() == 6); // six firewalls allocated
			
			boolean correct1 = false;
			boolean correct2 = false;
			boolean correct3= false;
			boolean correct4= false;
			
			for(Node fw : listFW) {
				if(fw.getName().equals("1.0.0.21") && (fw.getNeighbour().get(0).getName().equals("192.168.5.-1")
						|| fw.getNeighbour().get(1).getName().equals("192.168.5.-1"))  ) {
					correct1 = true; // firewall correctly allocated behind NAT
				}
				if(fw.getName().equals("1.0.0.16") && (fw.getNeighbour().get(0).getName().equals("192.168.3.-1")
						|| fw.getNeighbour().get(1).getName().equals("192.168.3.-1"))) {
					correct2 = true; // firewall correctly allocated behind NAT
				}
				if(fw.getName().equals("1.0.0.9") && (fw.getNeighbour().get(0).getName().equals("192.168.1.-1")
						|| fw.getNeighbour().get(1).getName().equals("192.168.1.-1"))) {
					correct3 = true; // firewall correctly allocated behind NAT
				}
				if(fw.getName().equals("1.0.0.4") && (fw.getNeighbour().get(0).getName().equals("33.33.33.3")
						|| fw.getNeighbour().get(1).getName().equals("33.33.33.1"))) {
					correct4 = true; // firewall correctly allocated to block 40.40.-1.-1
				}
			}

			assertTrue(correct1&&correct2&&correct3&&correct4);
			
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
	/**
	 * This test checks if the correct placement of multiple firewalls in a ramified network with multiple ports/protocols requirements. 
	 * This test takes a little a bit of time to execute (around 30 seconds)
	 */
	@Test
	public void test5Sec3Correctness(){
		try {
			VerefooSerializer result = test("./testfile/RegressioneTestCases/Test5_3.xml"); 
			//Correctness 1
			assertTrue(result.isSat());
			//Correctness 2
			List<Node> listFW = result.getNfv().getGraphs().getGraph().get(0).getNode().stream().filter(n -> 
			{ 
			if(n.getFunctionalType() != null && n.getFunctionalType().equals(FunctionalTypes.FIREWALL) )
				return true;
			else 
				return false;

			} ).collect(Collectors.toList());
			
			assertTrue(listFW.size() == 2); // two firewalls allocated
			
			
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
	/**
	 * This test checks if the correct placement of a central firewall in the topology
	 */
	@Test
	public void test5Sec5Correctness(){
		try {
			VerefooSerializer result = test("./testfile/RegressioneTestCases/Test5_5.xml"); 
			//Correctness 1
			assertTrue(result.isSat());
			//Correctness 2
			List<Node> listFW = result.getNfv().getGraphs().getGraph().get(0).getNode().stream().filter(n -> 
			{ 
			if(n.getFunctionalType() != null && n.getFunctionalType().equals(FunctionalTypes.FIREWALL) )
				return true;
			else 
				return false;

			} ).collect(Collectors.toList());
			
			assertTrue(listFW.size() == 1); // One central firewalls allocated
			
			assertTrue(listFW.get(0).getName().equals("1.0.0.10"));
			
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
	/**
	 * This test checks if the correct configuration of firewall rules is done having NAT IP as firewall rule
	 */
	@Test
	public void test5Sec6Correctness(){
		try {
			VerefooSerializer result = test("./testfile/RegressioneTestCases/Test5_6.xml"); 
			//Correctness 1
			assertTrue(result.isSat());
			//Correctness 2
			List<Node> listFW = result.getNfv().getGraphs().getGraph().get(0).getNode().stream().filter(n -> 
			{ 
			if(n.getFunctionalType() != null && n.getFunctionalType().equals(FunctionalTypes.FIREWALL) )
				return true;
			else 
				return false;

			} ).collect(Collectors.toList());
			
			assertTrue(listFW.size() == 5); // five firewalls allocated
			
			
			boolean correct1 = false;

			
			for(Node fw : listFW) {
				for(Elements ele: fw.getConfiguration().getFirewall().getElements()) {
					if(ele.getSource().startsWith("220.220"))
						correct1=true; // firewalls allocated firewall rule with source IP the IP of the NAT correctly
				}
			}

			assertTrue(correct1);
			
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
/********************************************************************Firewall Removal Tests and wrong Manual Configuration**********************************************************************/
	
	/**
	 * This test checks if the predefined firewalls are changed according to security requirements
	 */
	@Test
	public void test6Sec1Correctness(){
		try {
			VerefooSerializer result = test("./testfile/RegressioneTestCases/Test6_1.xml"); 
			//Correctness 1
			assertTrue(result.isSat());
			//Correctness 2
			List<Node> listFW = result.getNfv().getGraphs().getGraph().get(0).getNode().stream().filter(n -> 
			{ 
			if(n.getFunctionalType() != null && n.getFunctionalType().equals(FunctionalTypes.FIREWALL) )
				return true;
			else 
				return false;

			} ).collect(Collectors.toList());
			
			assertTrue(listFW.size() == 2);
			
			
			boolean correct1 = false;
			boolean correct2 = false;
			
			for(Node fw : listFW) {
			  if(fw.getConfiguration().getFirewall().getElements().size() !=0 ) {
				 correct1=true; 
			  }
			  if(fw.getConfiguration().getFirewall().getElements().size() !=0 ) {
				 correct2=true; 
			  }
			}

			assertTrue(correct1&&correct2);
			
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
	/**
	 * This test checks if the predefined firewalls are changed according to security requirements and predfined firewalls that are 
	 * defined outside the scope of security requirements remain un-altered
	 */
	@Test
	public void test6Sec2Correctness(){
		try {
			VerefooSerializer result = test("./testfile/RegressioneTestCases/Test6_2.xml"); 
			//Correctness 1
			assertTrue(result.isSat());
			//Correctness 2
			List<Node> listFW = result.getNfv().getGraphs().getGraph().get(0).getNode().stream().filter(n -> 
			{ 
			if(n.getFunctionalType() != null && n.getFunctionalType().equals(FunctionalTypes.FIREWALL) )
				return true;
			else 
				return false;

			} ).collect(Collectors.toList());
			
			assertTrue(listFW.size() == 3);
			
			
			boolean correct1 = false;
			
			for(Node fw : listFW) {
					if(fw.getName().equals("1.0.0.4")) {
						if(fw.getConfiguration().getFirewall().getElements().size() == 0) {
							correct1=true; // make sure that the firewall is not altered (outside scope of security requirements)
						}
					}
			  }
			

			assertTrue(correct1);
			
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
	/**
	 * This test checks if the predefined firewalls are changed according to security requirements and predfined firewalls that are 
	 * defined outside the scope of security requirements remain un-altered
	 */
	@Test
	public void test7Sec1Correctness(){
		try {
			VerefooSerializer result = test("./testfile/RegressioneTestCases/Test7_1.xml"); 
			//Correctness 1
			assertTrue(result.isSat());
			//Correctness 2
			List<Node> listFW = result.getNfv().getGraphs().getGraph().get(0).getNode().stream().filter(n -> 
			{ 
			if(n.getFunctionalType() != null && n.getFunctionalType().equals(FunctionalTypes.FIREWALL) )
				return true;
			else 
				return false;

			} ).collect(Collectors.toList());
			
			assertTrue(listFW.size() == 4);
			
			
			boolean correct1 = false;
			boolean correct2 = false;
			boolean correct3 = false;
			boolean correct4 = false;
			
			for(Node fw : listFW) {
			  if(fw.getName().equals("1.0.0.1") ) {
				  if(fw.getConfiguration().getFirewall().getElements().size() !=0)
				 correct1=true; 
			  }
			  if(fw.getName().equals("1.0.0.2") ) {
				  if(fw.getConfiguration().getFirewall().getElements().size() !=0)
				 correct2=true; 
			  }
			  if(fw.getName().equals("1.0.0.3") ) {
				  if(fw.getConfiguration().getFirewall().getElements().size() !=0)
				 correct3=true; 
			  }
			  if(fw.getName().equals("1.0.0.6") ) {
				  if(fw.getConfiguration().getFirewall().getElements().size() ==0)
				 correct4=true; 
			  }
			}

			assertTrue(correct1&&correct2&&correct3&&correct4);
			
			
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
	/**
	 * This test checks if the predefined firewalls are removed if constraints are optional (there placement was optional)
	 */
	@Test
	public void test7Sec2Correctness(){
		try {
			VerefooSerializer result = test("./testfile/RegressioneTestCases/Test7_2.xml"); 
			//Correctness 1
			assertTrue(result.isSat());
			//Correctness 2
			List<Node> listFW = result.getNfv().getGraphs().getGraph().get(0).getNode().stream().filter(n -> 
			{ 
			if(n.getFunctionalType() != null && n.getFunctionalType().equals(FunctionalTypes.FIREWALL) )
				return true;
			else 
				return false;

			} ).collect(Collectors.toList());
			
			assertTrue(listFW.size() == 2); // verify that 2 firewalls were removed
			
			
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
	/**
	 * This test checks if the predefined firewalls are removed if constraints are optional (there placement was optional)
	 */
	@Test
	public void test7Sec3Correctness(){
		try {
			VerefooSerializer result = test("./testfile/RegressioneTestCases/Test7_3.xml"); 
			//Correctness 1
			assertTrue(result.isSat());
			//Correctness 2
			List<Node> listFW = result.getNfv().getGraphs().getGraph().get(0).getNode().stream().filter(n -> 
			{ 
			if(n.getFunctionalType() != null && n.getFunctionalType().equals(FunctionalTypes.FIREWALL) )
				return true;
			else 
				return false;

			} ).collect(Collectors.toList());
			
			assertTrue(listFW.size() == 2); // verify that 2 firewalls were removed
			
			
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
	/**
	 * This test checks if the Security requirement ports present a conflict the problem should return UNSAT
	 */
	@Test
	public void test8Sec1Correctness(){
		try {
			VerefooSerializer result = test("./testfile/RegressioneTestCases/Test8_1.xml"); 
			//Correctness 1
			assertTrue(!result.isSat());
			
			
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
	/**
	 * This test checks if the optional firewalls are removed and not optional ones are preserved
	 */
	@Test
	public void test8Sec2Correctness(){
		try {
			VerefooSerializer result = test("./testfile/RegressioneTestCases/Test8_2.xml"); 
			//Correctness 1
			assertTrue(result.isSat());
			
			//Correctness 2
			List<Node> listFW = result.getNfv().getGraphs().getGraph().get(0).getNode().stream().filter(n -> 
			{ 
			if(n.getFunctionalType() != null && n.getFunctionalType().equals(FunctionalTypes.FIREWALL) )
				return true;
			else 
				return false;

			} ).collect(Collectors.toList());
			
			assertTrue(listFW.size() == 3); // verify that 2 firewalls were removed
			
			
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
	/**
	 * This test checks if the optional firewalls are removed and not optional ones are preserved
	 */
	@Test
	public void test9Sec1Correctness(){
		try {
			VerefooSerializer result = test("./testfile/RegressioneTestCases/Test9_1.xml"); 
			//Correctness 1
			assertTrue(result.isSat());
			
			//Correctness 2
			List<Node> listFW = result.getNfv().getGraphs().getGraph().get(0).getNode().stream().filter(n -> 
			{ 
			if(n.getFunctionalType() != null && n.getFunctionalType().equals(FunctionalTypes.FIREWALL) )
				return true;
			else 
				return false;

			} ).collect(Collectors.toList());
			
			assertTrue(listFW.size() == 4); 
			
			
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
	
}