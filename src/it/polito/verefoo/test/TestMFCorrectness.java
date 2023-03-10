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
 * This class runs some tests in order to check the correctness of the auto-configuration module of Algorithm Maximal Flows
 *
 */
public class TestMFCorrectness {
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception { // run once before all tests to calculate once the results
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
	
	private VerefooSerializer test(String file) throws Exception{ // alg is the algorithm used
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
        
		VerefooSerializer test = new VerefooSerializer(root,"MF");
        
        
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
* Note: The tests of each file (TestX_X) are divided into 4 categories; 
* a) SAT - UNSAT  (Correctness1)
* b) Correct number of firewalls allocated (Correctness2)
* c) Correct number of firewall rules allocated (Correctness3) --> if possible
* d) Correct firewall rules allocated (Correctness4) --> if possible
* e) Correct firewall position --> Still not done
* /
/********************************************************************* Test1_X (Placement Tests)********************************************************************/
		
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
			assertTrue(node.getConfiguration().getFirewall().getElements().size() == 1); // firewall should have 1 allow rule and default deny
			
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
			assertTrue(node.getConfiguration().getFirewall().getElements().size() == 1); // firewall should have 1 allow rules and default deny
			
			List<Elements> elements =node.getConfiguration().getFirewall().getElements(); // verify that firewall rules didnot change
			assertTrue(elements.get(0).getSource().equals("-1.-1.-1.1") && elements.get(0).getDestination().equals("-1.-1.-1.1") &&
					elements.get(0).getSrcPort().equals("*") &&  elements.get(0).getDstPort().equals("80") );
			
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
/********************************************************************* Test2_X (Placement Tests) ********************************************************************/
	
	/**
	 * This test checks if Test2_1 result have the 3 Correctness
	 */
	@Test
	public void test2Sec1Correctness(){
		try {
			VerefooSerializer result = test("./testfile/RegressioneTestCases/Test2_1.xml"); 
			//Correctness 1
			assertTrue(result.isSat());
			//Correctness 2
			List<Node> listFW = result.getNfv().getGraphs().getGraph().get(0).getNode().stream().filter(n -> n.getFunctionalType().equals(FunctionalTypes.FIREWALL)).collect(Collectors.toList());
			assertTrue(listFW.size() == 1);
			//Correctness 3
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
			assertTrue(node.getConfiguration().getFirewall().getElements().size() == 1); // firewall should have 4 rules
			//Correctness 4
			List<Elements> elements =node.getConfiguration().getFirewall().getElements(); // verify that firewall rules didnot change (presuming order is the same)
			assertTrue(elements.get(0).getSource().equals("-1.-1.-1.-1") && elements.get(0).getDestination().equals("-1.-1.-1.-1") &&
					elements.get(0).getSrcPort().equals("*") &&  elements.get(0).getDstPort().equals("80") );
			
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
	/**
	 * This test checks if Test2_3 result have the 4 Correctness
	 */
	@Test
	public void test2Sec3Correctness(){
		try {
			VerefooSerializer result = test("./testfile/RegressioneTestCases/Test2_3.xml"); 
			//Correctness 1
			assertTrue(result.isSat());
			//Correctness 2
			List<Node> listFW = result.getNfv().getGraphs().getGraph().get(0).getNode().stream().filter(n -> n.getFunctionalType().equals(FunctionalTypes.FIREWALL)).collect(Collectors.toList());
			assertTrue(listFW.size() == 1);
			//Correctness 3
			Node node = listFW.get(0);
			assertTrue(node.getConfiguration().getFirewall().getElements().size() == 2); // firewall should have 2 rules
			//Correctness 4
			List<Elements> elements =node.getConfiguration().getFirewall().getElements(); // verify that firewall rules didnot change (presuming order is the same)
			assertTrue((elements.get(0).getSource().equals("130.10.0.-1") && elements.get(0).getDestination().equals("40.40.42.-1")) ||
					(elements.get(0).getSource().equals("40.40.42.-1") && elements.get(0).getDestination().equals("130.10.0.-1")) );
			
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
	/**
	 * This test checks if Test2_4 result have the 4 Correctness. Maximal Flows requires the security requirements to be more rigorous in order 
	 * to produce less ambiguous solution. This Test is specific to Maximal Flows and is not used in Atomic Predicates
	 */
	@Test
	public void test2Sec4Correctness(){
		try {
			VerefooSerializer result = test("./testfile/RegressioneTestCases/Test2_4.xml"); 
			//Correctness 1
			assertTrue(result.isSat());
			//Correctness 2
			List<Node> listFW = result.getNfv().getGraphs().getGraph().get(0).getNode().stream().filter(n -> n.getFunctionalType().equals(FunctionalTypes.FIREWALL)).collect(Collectors.toList());
			assertTrue(listFW.size() == 1); // one firewall is enough to satisfy the problem
			//Correctness 3
			Node node = listFW.get(0);
			assertTrue(node.getConfiguration().getFirewall().getElements().size() == 3); // firewall should have 2 rules
			//Correctness 4
			List<Elements> elements =node.getConfiguration().getFirewall().getElements(); 
			assertTrue((elements.get(0).getSource().equals("130.10.0.-1") && elements.get(0).getDestination().equals("40.40.-1.-1")) ||
					(elements.get(0).getSource().equals("40.40.-1.-1") && elements.get(0).getDestination().equals("130.10.0.-1")) );
			
			boolean correct1 = false;
			boolean correct2 = false;
			
			for(int i =0 ; i<3 ; i++) {
				if(elements.get(i).getSource().equals("40.40.-1.-1") && elements.get(i).getDestination().equals("130.10.0.-1") && elements.get(i).getAction().equals(ActionTypes.ALLOW)) {
					if(elements.get(i).getDstPort().equals("80"))
						correct1=true; // HTTP port 80 access Security Requirements satisfied
				}
				if(elements.get(i).getSource().equals("40.40.41.-1") && elements.get(i).getDestination().equals("130.10.0.-1") && elements.get(i).getAction().equals(ActionTypes.ALLOW)) {
					if(elements.get(i).getDstPort().equals("53"))
						correct2=true; // UDP port 53 access Security Requirements satisfied
				}
			}

			assertTrue(correct1 && correct2);
			
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
	/**
	 * This test checks if Test2_5 result have the 4 Correctness. Atomic Predicate produces Much more firewall rules than Maximal Flows,
	 * because it is more rigorous. While Maximal flows is more generic with its firewall rules
	 * In this test Atomic Predicates produce one firewall with 12 firewall rules while Maximal Flows produces only 2 firewall rules,
	 * but more generic
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
			assertTrue(node.getConfiguration().getFirewall().getElements().size() == 2); // firewall should have 2 rules
			//Correctness 4
			List<Elements> elements =node.getConfiguration().getFirewall().getElements(); 

			boolean correct1 = false;
			boolean correct2 = false;
			
			for(int i =0 ; i<2 ; i++) {
				if(elements.get(i).getSource().equals("-1.-1.-1.-1") && elements.get(i).getDestination().equals("-1.-1.-1.-1")) { // general rules since SR are not specific enough
					if(elements.get(i).getDstPort().equals("80"))
						correct1=true; // HTTP port 80 access Security Requirements satisfied from anywhere
				}
				if(elements.get(i).getSource().equals("-1.-1.-1.-1") && elements.get(i).getDestination().equals("-1.-1.-1.-1") ) {
					if(elements.get(i).getDstPort().equals("53"))
						correct2=true; // UDP port 53 access Security Requirements satisfied from anywhere
				}
			}
			
			assertTrue(correct1 && correct2);	
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
			assertTrue(node1.getName().equals("1.0.0.1"));
			assertTrue(node2.getName().equals("1.0.0.6"));
			
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
	/**
	 * This test checks if Test3_3 result have the 3 Correctness. This Test provide consistent result with MF but in-determinstic result with AP
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
			//Correctness 3
			Node node1 = listFW.get(0);
			Node node2 = listFW.get(1);
			assertTrue(node1.getConfiguration().getFirewall().getElements().size() == 2); // firewall should have 2 rules
			assertTrue(node2.getConfiguration().getFirewall().getElements().size() == 2); // firewall should have 2 rules
			
			//Correctness Placement
			assertTrue(node1.getName().equals("1.0.0.2") || node1.getName().equals("1.0.0.3"));
			assertTrue(node2.getName().equals("1.0.0.3") || node2.getName().equals("1.0.0.2"));
			
			//Correctness 4
			boolean correct1 = false;
			boolean correct2 = false;
			
			for(Node fw : listFW) {
				List<Elements> elements = fw.getConfiguration().getFirewall().getElements();
				if(fw.getName().equals("1.0.0.2")) {
					if((elements.get(0).getSource().equals("130.10.0.-1") && elements.get(0).getDestination().equals("40.40.42.-1")) ||
							(elements.get(0).getSource().equals("40.40.42.-1") && elements.get(0).getDestination().equals("130.10.0.-1")))
						correct1=true;
				}else{
					if((elements.get(0).getSource().equals("40.40.43.-1") && elements.get(0).getDestination().equals("130.10.0.3")) ||
							(elements.get(0).getSource().equals("130.10.0.3") && elements.get(0).getDestination().equals("40.40.43.-1")))
						correct2=true;
				}
			}
			
			assertTrue(correct1 && correct2);
			
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
	
	/**
	 * This test checks if Test3_4 result have the 4 Correctness. Test different isolation paths and different port/protocol reachability.
	 * 
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
	
	
/********************************************************************* Test4_X (Placement Tests) ********************************************************************/
// With Load Balancer
	
	
	
}
