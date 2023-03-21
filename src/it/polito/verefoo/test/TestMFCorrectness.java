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
* e) Correct firewall position --> Not all
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
			//System.out.println("The number of firewall rules is :  "+ node1.getConfiguration().getFirewall().getElements().size() + node2.getConfiguration().getFirewall().getElements().size());
			assertTrue(node1.getConfiguration().getFirewall().getElements().size() == 2 || node1.getConfiguration().getFirewall().getElements().size() == 1 || node1.getConfiguration().getFirewall().getElements().size() == 0); // there is three solutions
			assertTrue(node2.getConfiguration().getFirewall().getElements().size() == 2 || node2.getConfiguration().getFirewall().getElements().size() == 1 || node2.getConfiguration().getFirewall().getElements().size() == 0); 
			//System.out.println("The firewall LOCATIONS is :  "+ node1.getName() + node2.getName());

			//Correctness Placement
			assertTrue(node1.getName().equals("1.0.0.1") || node1.getName().equals("1.0.0.2") || node1.getName().equals("1.0.0.3") || node1.getName().equals("1.0.0.6")); // different solutions possible
			assertTrue(node2.getName().equals("1.0.0.1") || node2.getName().equals("1.0.0.3") || node2.getName().equals("1.0.0.2") || node2.getName().equals("1.0.0.6"));
			
			//Correctness 4
			boolean correct1 = false;
			boolean correct2 = false;

			for(Node fw : listFW) {
				List<Elements> elements = fw.getConfiguration().getFirewall().getElements();
				if(fw.getName().equals("1.0.0.2")) {
					if((elements.get(0).getSource().equals("130.10.0.-1") && elements.get(0).getDestination().equals("40.40.42.-1")) ||
							(elements.get(0).getSource().equals("40.40.42.-1") && elements.get(0).getDestination().equals("130.10.0.-1")))
						correct1=true;
				}
				if(fw.getName().equals("1.0.0.6")){
					if((elements.get(0).getSource().equals("40.40.43.-1") && elements.get(0).getDestination().equals("130.10.0.3")) ||
							(elements.get(0).getSource().equals("130.10.0.3") && elements.get(0).getDestination().equals("40.40.43.-1")))
						correct2=true;
				}
				//Other alternative solution given by framework
				if(fw.getName().equals("1.0.0.1")) { // 0 firewall rules allocated
						correct1=true;
				}
				if(fw.getName().equals("1.0.0.3")){
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
	 * This test checks even if there is port conflicts DENY/ALLOW result is SAT in Maximal Flows
	 */
	@Test
	public void test3Sec8Correctness(){
		try {
			VerefooSerializer result = test("./testfile/RegressioneTestCases/Test3_8.xml"); 
			//Correctness 1
			assertTrue(result.isSat());
			

		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
/********************************************************************* Test4_X (Placement Tests) ********************************************************************/
// With Load Balancer
	
	/**
	 * This test checks if the algorithm always prefers lower number of firewall more than lower number of firewall rules. In Maximal Flows 
	 * the firewall is allocated before all servers, including servers 130.10.1.-1
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
			assertTrue(node1.getConfiguration().getFirewall().getElements().size() == 1); // firewall should have 1 rules
			
			assertTrue(node1.getName().equals("1.0.0.12")); // only one possible solution with minimum number of firewalls
			
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
	
	/**
	 * This test case have security requirements too generic for maximal flows, the result will be too generic.
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
			assertTrue(node1.getConfiguration().getFirewall().getElements().size() == 1); // firewall should have 1 rules
			
			assertTrue(node1.getName().equals("1.0.0.12")); // only one possible solution with minimum number of firewalls
			
			
			List<Elements> elements =node1.getConfiguration().getFirewall().getElements(); // verify that firewall rules didnot change 

			boolean correct1 = false;
			
				if(elements.get(0).getSource().equals("-1.-1.-1.-1") && elements.get(0).getDestination().equals("-1.-1.-1.-1")) {
					if(elements.get(0).getDstPort().equals("80"))
						correct1=true; 
				}

			
			assertTrue(correct1);
			
			
			
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
	
	/**
	 * This test checks if the framework is able to interpret servers combining (130.10.0.1 + 130.10.0.2 --> 130.10.0.-1) for maximal flows
	 * Maximal flows combines the servers while atomic predicates decomposes them
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
			assertTrue(node1.getConfiguration().getFirewall().getElements().size() == 2); // firewall should have 8 rules
			
			assertTrue(node1.getName().equals("1.0.0.12")); 
			
			List<Elements> elements =node1.getConfiguration().getFirewall().getElements();

			boolean correct1 = false;
			boolean correct2 = false;
			
			for(int i =0 ; i<2 ; i++) { 
				if(elements.get(i).getSource().equals("130.10.-1.-1") && elements.get(i).getDestination().equals("40.40.44.-1")) {
						correct1=true; // Maximal flows do not decompose 130.10.0.-1 and 130.10.1.-1 but combines them into 130.10.-1.-1 (unlike atomic predicates)
				}
				if(elements.get(i).getSource().equals("40.40.44.-1") && elements.get(i).getDestination().equals("130.10.-1.-1") ) {
						correct2=true; 
				}
			}
			
			assertTrue(correct1&&correct2);
			
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
	/**
	 * This test checks if the framework is able to interpret servers combining and several ports interpretation (TCP)
	 */
	@Test
	public void test4Sec7Correctness(){
		try {
			VerefooSerializer result = test("./testfile/RegressioneTestCases/Test4_7.xml"); 
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
			
			assertTrue(node1.getConfiguration().getFirewall().getElements().size() == 4 || 
					node1.getConfiguration().getFirewall().getElements().size() == 5); // firewall should have 4 or 5 rules (two solutions possible)
			
			assertTrue(node1.getName().equals("1.0.0.12")); 
			
			List<Elements> elements =node1.getConfiguration().getFirewall().getElements();

			boolean correct1 = false;
			boolean correct2 = false;
			
			for(int i =0 ; i<4 ; i++) { 
				
				if(elements.get(i).getSource().startsWith("40.40") && elements.get(i).getDestination().startsWith("130.10")
						&& elements.get(i).getDstPort().equals("80")) {
						correct1=true; 
				}
				if(elements.get(i).getSource().startsWith("40.40.41") && elements.get(i).getDestination().startsWith("130.10.1") 
						) {
						correct2=true; 
				}
			}
			
			assertTrue(correct1&&correct2);
			
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
	/**
	 * This test checks if the framework is able to interpret servers combining and several ports interpretation (TCP/UDP/ANY)
	 */
	@Test
	public void test4Sec8Correctness(){
		try {
			VerefooSerializer result = test("./testfile/RegressioneTestCases/Test4_8.xml"); 
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
			assertTrue(node1.getConfiguration().getFirewall().getElements().size() == 4); // firewall should have 8 rules
			
			assertTrue(node1.getName().equals("1.0.0.12")); 
			
			List<Elements> elements =node1.getConfiguration().getFirewall().getElements();

			boolean correct1 = false;
			boolean correct2 = false;
			
			for(int i =0 ; i<4 ; i++) { 
				if(elements.get(i).getSource().equals("40.40.41.-1") && elements.get(i).getDestination().equals("130.10.1.-1")
						&& elements.get(i).getDstPort().equals("80")) {
						correct1=true; 
				}
				if(elements.get(i).getSource().equals("40.40.42.-1") && elements.get(i).getDestination().equals("130.10.0.-1") 
						&& elements.get(i).getDstPort().equals("100") && elements.get(i).getProtocol().equals(L4ProtocolTypes.ANY)) {
						correct2=true; 
				}
			}	
			assertTrue(correct1&&correct2);
			
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
	 * This test checks if the correct placement of multiple firewalls behind NAT.
	 */
	@Test
	public void test5Sec2Correctness(){
		try {
			VerefooSerializer result = test("./testfile/RegressioneTestCases/Test5_2.xml"); 
			//Correctness 1
			assertTrue(result.isSat());
			//Correctness 2
			List<Node> listFW = result.getNfv().getGraphs().getGraph().get(0).getNode().stream().filter(n -> n.getFunctionalType().equals(FunctionalTypes.FIREWALL)).collect(Collectors.toList());
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
	 * 
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
	 * This test checks if the Maximal Flow performs well in large networks. 
	 * 
	 */
	@Test
	public void test5Sec4Correctness(){
		try {
			VerefooSerializer result = test("./testfile/RegressioneTestCases/Test5_4.xml"); 
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
			assertTrue(listFW.size() == 3); // 3 firewalls allocated
			
			
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
	/**
	 * This test checks if the correct placement of a central firewall in the topology (Maximal Flows produces less firewall rules than atomic predicates)
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
				 correct1=true; // Check if firewall allocated at 1.0.0.1 (two solutions possible one with one firewall rule other with no firewall rule)
			  }
			  if(fw.getName().equals("1.0.0.2") ) {
				 correct2=true; 
			  }
			  if(fw.getName().equals("1.0.0.3") ) {
				 correct3=true; 
			  }
			  if(fw.getName().equals("1.0.0.6") ) {
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
