package it.polito.verefoo.test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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

import it.polito.verefoo.VerefooSerializer;
import it.polito.verefoo.jaxb.Elements;
import it.polito.verefoo.jaxb.FunctionalTypes;
import it.polito.verefoo.jaxb.NFV;
import it.polito.verefoo.jaxb.Node;
/**
 * 
 * This class tests the different topologies where NAT is allocated for both Atomic Predicates and Maximal Flows
 *
 */

public class TestNAT {
		
	private static String algo;
	
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
	
	private VerefooSerializer test(String file, String alg) throws Exception{
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
        VerefooSerializer test = new VerefooSerializer(root,alg);
        
        
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
	
	
	
	/**
	 * This test checks if the traffic flow is correctly set, by seeing the rules firewalls configure.
	 */
	@Test
	public void testNat01AP(){
		try {
			VerefooSerializer result = test( "./testfile/NAT/Nat01.xml","AP"); 
			assertTrue(result.isSat());
			List<Node> listFW = result.getNfv().getGraphs().getGraph().get(0).getNode().stream().filter(n -> n.getFunctionalType().equals(FunctionalTypes.FIREWALL)).collect(Collectors.toList());
			assertTrue(listFW.size() == 2);
			Node node1 = listFW.get(0);
			Node node2 = listFW.get(1);
			
			assertTrue(node1.getConfiguration().getFirewall().getElements().size() == 1);
			assertTrue(node2.getConfiguration().getFirewall().getElements().size() == 1);
			
			
			
			Elements element1 =node1.getConfiguration().getFirewall().getElements().get(0);
			Elements element2 =node2.getConfiguration().getFirewall().getElements().get(0);
			
			boolean firstOk = false;
			boolean secondOk = false;
			if((node1.getName().equals("20.0.0.3") && element1.getSource().equals("10.0.0.1")) || (node2.getName().equals("20.0.0.3") && element2.getSource().equals("10.0.0.1"))) 
				firstOk = true;
			if((node1.getName().equals("20.0.0.2") && element1.getSource().equals("20.0.0.1")) || (node2.getName().equals("20.0.0.2") && element2.getSource().equals("20.0.0.1"))) 
				secondOk = true;	
				
				
			assertTrue(firstOk && secondOk);
			
		} catch (Exception e) {
			fail(e.toString());
		}
		
		
	}
	
	/**
	 * This test checks if the traffic flow is correctly set, by seeing the rules firewalls configure.
	 */
	@Test
	public void testNat01MF(){
		try {
			VerefooSerializer result = test( "./testfile/NAT/Nat01.xml","MF"); 
			assertTrue(result.isSat());
			List<Node> listFW = result.getNfv().getGraphs().getGraph().get(0).getNode().stream().filter(n -> n.getFunctionalType().equals(FunctionalTypes.FIREWALL)).collect(Collectors.toList());
			assertTrue(listFW.size() == 2);
			Node node1 = listFW.get(0);
			Node node2 = listFW.get(1);
			
			assertTrue(node1.getConfiguration().getFirewall().getElements().size() == 1);
			assertTrue(node2.getConfiguration().getFirewall().getElements().size() == 1);
			
			
			
			Elements element1 =node1.getConfiguration().getFirewall().getElements().get(0);
			Elements element2 =node2.getConfiguration().getFirewall().getElements().get(0);
			
			boolean firstOk = false;
			boolean secondOk = false;
			if((node1.getName().equals("20.0.0.3") && element1.getSource().equals("10.0.0.1")) || (node2.getName().equals("20.0.0.3") && element2.getSource().equals("10.0.0.1"))) 
				firstOk = true;
			if((node1.getName().equals("20.0.0.2") && element1.getSource().equals("20.0.0.1")) || (node2.getName().equals("20.0.0.2") && element2.getSource().equals("20.0.0.1"))) 
				secondOk = true;	
				
				
			assertTrue(firstOk && secondOk);
			
		} catch (Exception e) {
			fail(e.toString());
		}
		
		
	}
	
	
	/**
	 * This test checks if the NAT status model work correctly.
	 */
	@Test
	public void testNat02AP(){
		try {
			VerefooSerializer result = test( "./testfile/NAT/Nat02.xml","AP"); 
			assertTrue(result.isSat());
			List<Node> listFW = result.getNfv().getGraphs().getGraph().get(0).getNode().stream().filter(n -> n.getFunctionalType().equals(FunctionalTypes.FIREWALL)).collect(Collectors.toList());
			assertTrue(listFW.size() == 2);
			Node node1 = listFW.get(0); // first firewall
			Node node2 = listFW.get(1); // second firewall
			
			assertTrue(node1.getConfiguration().getFirewall().getElements().size() == 2); // two elements (one firewall rules but in both directions)
			assertTrue(node2.getConfiguration().getFirewall().getElements().size() == 2);
			
			
			
			Elements element1 =node1.getConfiguration().getFirewall().getElements().get(0);
			Elements element2 =node2.getConfiguration().getFirewall().getElements().get(0);
			
			boolean firstOk = false;
			boolean secondOk = false;
			if((node1.getName().equals("20.0.0.3") && element1.getSource().equals("30.0.5.2")) || (node2.getName().equals("20.0.0.3") && element2.getSource().equals("30.0.5.2")))
				firstOk = true;
			if((node1.getName().equals("20.0.0.2") && element1.getSource().equals("30.0.5.2")) || (node2.getName().equals("20.0.0.2") && element2.getSource().equals("30.0.5.2"))) 
				secondOk = true;	
				
				
			assertTrue(firstOk && secondOk);
			
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
	/**
	 * This test checks if the NAT status model work correctly.
	 */
	@Test
	public void testNat02MF(){
		try {
			VerefooSerializer result = test( "./testfile/NAT/Nat02.xml","MF"); 
			assertTrue(result.isSat());
			List<Node> listFW = result.getNfv().getGraphs().getGraph().get(0).getNode().stream().filter(n -> n.getFunctionalType().equals(FunctionalTypes.FIREWALL)).collect(Collectors.toList());
			assertTrue(listFW.size() == 2);
			Node node1 = listFW.get(0);
			Node node2 = listFW.get(1);
			
			assertTrue(node1.getConfiguration().getFirewall().getElements().size() == 1);
			assertTrue(node2.getConfiguration().getFirewall().getElements().size() == 1);
			
			
			
			Elements element1 =node1.getConfiguration().getFirewall().getElements().get(0);
			Elements element2 =node2.getConfiguration().getFirewall().getElements().get(0);
			
			boolean firstOk = false;
			boolean secondOk = false;
			if((node1.getName().equals("20.0.0.3") && element1.getSource().equals("-1.0.-1.-1")) || (node2.getName().equals("20.0.0.3") && element2.getSource().equals("-1.0.-1.-1"))) 
				firstOk = true;
			if((node1.getName().equals("20.0.0.2") && element1.getSource().equals("-1.0.-1.-1")) || (node2.getName().equals("20.0.0.2") && element2.getSource().equals("-1.0.-1.-1"))) 
				secondOk = true;	
				
				
			assertTrue(firstOk && secondOk);
			
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
	/**
	 * This test checks if a firewall is allocated before the NAT, to block only an internal node, not all.
	 */
	@Test
	public void testNat03AP(){
		try {
			VerefooSerializer result = test( "./testfile/NAT/Nat03.xml","AP"); 
			assertTrue(result.isSat());
			List<Node> listFW = result.getNfv().getGraphs().getGraph().get(0).getNode().stream().filter(n -> n.getFunctionalType().equals(FunctionalTypes.FIREWALL)).collect(Collectors.toList());
			assertTrue(listFW.size() == 1);
			Node node1 = listFW.get(0);
			
			assertTrue(node1.getConfiguration().getFirewall().getElements().size() == 0);
			
				
			assertTrue(node1.getName().equals("20.0.0.4"));
			
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
	/**
	 * This test checks if a firewall is allocated before the NAT, to block only an internal node, not all.
	 */
	@Test
	public void testNat03MF(){
		try {
			VerefooSerializer result = test( "./testfile/NAT/Nat03.xml","MF"); 
			assertTrue(result.isSat());
			List<Node> listFW = result.getNfv().getGraphs().getGraph().get(0).getNode().stream().filter(n -> n.getFunctionalType().equals(FunctionalTypes.FIREWALL)).collect(Collectors.toList());
			assertTrue(listFW.size() == 1);
			Node node1 = listFW.get(0);
			
			assertTrue(node1.getConfiguration().getFirewall().getElements().size() == 0);
			
				
			assertTrue(node1.getName().equals("20.0.0.4"));
			
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
	/**
	 * This test checks if a firewall is allocated after the NAT, to block all the internal nodes.
	 */
	@Test
	public void testNat04AP(){
		try {
			VerefooSerializer result = test( "./testfile/NAT/Nat04.xml","AP"); 
			assertTrue(result.isSat());
			List<Node> listFW = result.getNfv().getGraphs().getGraph().get(0).getNode().stream().filter(n -> n.getFunctionalType().equals(FunctionalTypes.FIREWALL)).collect(Collectors.toList());
			assertTrue(listFW.size() == 1);
			Node node1 = listFW.get(0);
			
			assertTrue(node1.getConfiguration().getFirewall().getElements().size() == 0);
			
				
			assertTrue(node1.getName().equals("20.0.0.2"));
			
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
	/**
	 * This test checks if a firewall is allocated after the NAT, to block all the internal nodes.
	 */
	@Test
	public void testNat04MF(){
		try {
			VerefooSerializer result = test( "./testfile/NAT/Nat04.xml","MF"); 
			assertTrue(result.isSat());
			List<Node> listFW = result.getNfv().getGraphs().getGraph().get(0).getNode().stream().filter(n -> n.getFunctionalType().equals(FunctionalTypes.FIREWALL)).collect(Collectors.toList());
			assertTrue(listFW.size() == 1);
			Node node1 = listFW.get(0);
			
			assertTrue(node1.getConfiguration().getFirewall().getElements().size() == 0);
			
				
			assertTrue(node1.getName().equals("20.0.0.2"));
			
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
	/**
	 * This test checks if the result is UNSAT when the status is not present in the NAT for communication from outside.
	 */
	@Test
	public void testNat05AP(){
		try {
			VerefooSerializer result = test( "./testfile/NAT/Nat05.xml","AP"); 
			assertTrue(result.isSat()); // The result is SAT even if there is no communication form outside to the NAT
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
	/**
	 * This test checks if the result is UNSAT when the status is not present in the NAT for communication from outside.
	 */
	@Test
	public void testNat05MF(){
		try {
			VerefooSerializer result = test( "./testfile/NAT/Nat05.xml","MF"); 
			assertTrue(result.isSat()); // The result is SAT even if there is no communication form outside to the NAT
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
	/**
	 * This test checks a graph with multiple NATs.
	 */
	@Test
	public void testNat06AP(){
		try {
			VerefooSerializer result = test( "./testfile/NAT/Nat06.xml","AP"); 
			assertTrue(result.isSat());
			List<Node> listFW = result.getNfv().getGraphs().getGraph().get(0).getNode().stream().filter(n -> n.getFunctionalType().equals(FunctionalTypes.FIREWALL)).collect(Collectors.toList());
			assertTrue(listFW.size() == 3);
			Node node1 = listFW.get(0);
			Node node2 = listFW.get(1);
			Node node3 = listFW.get(2);
			
			assertTrue(node1.getConfiguration().getFirewall().getElements().size() == 1);
			assertTrue(node2.getConfiguration().getFirewall().getElements().size() == 1);
			assertTrue(node3.getConfiguration().getFirewall().getElements().size() == 1);
			
			
			Elements element1 =node1.getConfiguration().getFirewall().getElements().get(0);
			Elements element2 =node2.getConfiguration().getFirewall().getElements().get(0);
			Elements element3 =node3.getConfiguration().getFirewall().getElements().get(0);
			
			boolean firstOk = false;
			boolean secondOk = false;
			boolean thirdOk = false;
			if((node1.getName().equals("20.0.0.3") && element1.getSource().equals("10.0.0.1")) || (node2.getName().equals("20.0.0.3") && element2.getSource().equals("10.0.0.1")) || (node3.getName().equals("20.0.0.3") && element3.getSource().equals("10.0.0.1"))) 
				firstOk = true;
			if((node1.getName().equals("20.0.0.4") && element1.getSource().equals("20.0.0.1")) || (node2.getName().equals("20.0.0.4") && element2.getSource().equals("20.0.0.1")) || (node3.getName().equals("20.0.0.4") && element3.getSource().equals("20.0.0.1"))) 
				secondOk = true;	
			if((node1.getName().equals("20.0.0.2") && element1.getSource().equals("20.0.0.5")) || (node2.getName().equals("20.0.0.2") && element2.getSource().equals("20.0.0.5")) || (node3.getName().equals("20.0.0.2") && element3.getSource().equals("20.0.0.5"))) 
				thirdOk = true;
				
				
			assertTrue(firstOk && secondOk && thirdOk);
			
		} catch (Exception e) {
			fail(e.toString());
		}
		
		
	}
	
	/**
	 * This test checks a graph with multiple NATs.
	 */
	@Test
	public void testNat06MF(){
		try {
			VerefooSerializer result = test( "./testfile/NAT/Nat06.xml","MF"); 
			assertTrue(result.isSat());
			List<Node> listFW = result.getNfv().getGraphs().getGraph().get(0).getNode().stream().filter(n -> n.getFunctionalType().equals(FunctionalTypes.FIREWALL)).collect(Collectors.toList());
			assertTrue(listFW.size() == 3);
			Node node1 = listFW.get(0);
			Node node2 = listFW.get(1);
			Node node3 = listFW.get(2);
			
			assertTrue(node1.getConfiguration().getFirewall().getElements().size() == 1);
			assertTrue(node2.getConfiguration().getFirewall().getElements().size() == 1);
			assertTrue(node3.getConfiguration().getFirewall().getElements().size() == 1);
			
			
			Elements element1 =node1.getConfiguration().getFirewall().getElements().get(0);
			Elements element2 =node2.getConfiguration().getFirewall().getElements().get(0);
			Elements element3 =node3.getConfiguration().getFirewall().getElements().get(0);
			
			boolean firstOk = false;
			boolean secondOk = false;
			boolean thirdOk = false;
			if((node1.getName().equals("20.0.0.3") && element1.getSource().equals("10.0.0.1")) || (node2.getName().equals("20.0.0.3") && element2.getSource().equals("10.0.0.1")) || (node3.getName().equals("20.0.0.3") && element3.getSource().equals("10.0.0.1"))) 
				firstOk = true;
			if((node1.getName().equals("20.0.0.4") && element1.getSource().equals("20.0.0.1")) || (node2.getName().equals("20.0.0.4") && element2.getSource().equals("20.0.0.1")) || (node3.getName().equals("20.0.0.4") && element3.getSource().equals("20.0.0.1"))) 
				secondOk = true;	
			if((node1.getName().equals("20.0.0.2") && element1.getSource().equals("20.0.0.5")) || (node2.getName().equals("20.0.0.2") && element2.getSource().equals("20.0.0.5")) || (node3.getName().equals("20.0.0.2") && element3.getSource().equals("20.0.0.5"))) 
				thirdOk = true;
				
				
			assertTrue(firstOk && secondOk && thirdOk);
			
		} catch (Exception e) {
			fail(e.toString());
		}
		
		
	}
	
}
