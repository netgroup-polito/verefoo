/**
 * 
 */
package it.polito.verefoo.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
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
 * This class runs some tests in order to check the correctness of the auto-configuration module
 *
 */
public class TestFirewallCorrectness {

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
        VerefooSerializer test = new VerefooSerializer(root);
        
        
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
	 * This test checks if a manually configured firewall correctly blocks all the packets.
	 */
	@Test
	public void testFWCorrectness01(){
		try {
			VerefooSerializer result = test( "./testfile/FWCorrectness/FWCorrect01.xml"); 
			assertTrue(result.isSat());
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
	/**
	 * This test checks if a manually configured firewall correctly - with a wrong configuration - is not able to block the packets. 
	 * It should be UNSAT
	 */
	@Test
	public void testFWCorrectness02(){
		try {
			VerefooSerializer result = test( "./testfile/FWCorrectness/FWCorrect02.xml"); 
			assertTrue(!result.isSat());
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
	
	/**
	 * This test checks if an auto-configured firewall configures only a rule which allows only a communication.
	 */
	@Test
	public void testFWCorrectness03(){
		try {
			VerefooSerializer result = test( "./testfile/FWCorrectness/FWCorrect03.xml"); 
			assertTrue(result.isSat());
			List<Node> listFW = result.getNfv().getGraphs().getGraph().get(0).getNode().stream().filter(n -> n.getFunctionalType().equals(FunctionalTypes.FIREWALL)).collect(Collectors.toList());
			assertTrue(listFW.size() == 1);
			Node node = listFW.get(0);
		
			assertTrue(node.getConfiguration().getFirewall().getElements().size() == 1);
			Elements element =node.getConfiguration().getFirewall().getElements().get(0);
			assertTrue(element.getSource().equals("10.0.0.1"));
			assertTrue(element.getDestination().equals("20.0.0.1"));
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
	
	/**
	 * This test checks if an auto-configured firewall configures only a rule which blocks traffic coming ONLY from 10.0.0.1
	 */
	@Test
	public void testFWCorrectness04(){
		try {
			VerefooSerializer result = test( "./testfile/FWCorrectness/FWCorrect04.xml"); 
			assertTrue(result.isSat());
			List<Node> listFW = result.getNfv().getGraphs().getGraph().get(0).getNode().stream().filter(n -> n.getFunctionalType().equals(FunctionalTypes.FIREWALL)).collect(Collectors.toList());
			assertTrue(listFW.size() == 1);
			Node node = listFW.get(0);
		
			assertTrue(node.getConfiguration().getFirewall().getElements().size() == 1);
			Elements element =node.getConfiguration().getFirewall().getElements().get(0);
			assertTrue(element.getSource().equals("10.0.0.1"));
			assertTrue(element.getDestination().equals("20.0.0.1"));
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
	
	/**
	 * Test about policies from clients and from server at the same time
	 * is test checks if an auto-configured firewall configures only a rule which blocks traffic coming ONLY coming from 10.0.0.*
	 */
	@Test
	public void testFWCorrectness05(){
		try {
			VerefooSerializer result = test( "./testfile/FWCorrectness/FWCorrect05.xml"); 
			assertTrue(result.isSat());
			List<Node> listFW = result.getNfv().getGraphs().getGraph().get(0).getNode().stream().filter(n -> n.getFunctionalType().equals(FunctionalTypes.FIREWALL)).collect(Collectors.toList());
			assertTrue(listFW.size() == 1);
			Node node = listFW.get(0);
		
			assertTrue(node.getConfiguration().getFirewall().getElements().size() == 1);
			Elements element =node.getConfiguration().getFirewall().getElements().get(0);
			assertTrue(
					(element.getSource().equals("-1.-1.-1.-1") && element.getDestination().equals("20.-1.-1.-1")) ||
					(element.getSource().equals("10.0.0.-1") && element.getDestination().equals("20.0.0.1"))
					);
			
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
	
	/**
	 * Test about two alternative paths
	 * Only a firewall configures an ALLOW rule, the other is simply in DENY mode
	 */
	@Test
	public void testFWCorrectness06(){
		try {
			VerefooSerializer result = test( "./testfile/FWCorrectness/FWCorrect06.xml"); 
			assertTrue(result.isSat());
			List<Node> listFW = result.getNfv().getGraphs().getGraph().get(0).getNode().stream().filter(n -> n.getFunctionalType().equals(FunctionalTypes.FIREWALL)).collect(Collectors.toList());
			assertTrue(listFW.size() == 2);
			
			boolean correct1 = false;
			boolean correct2 = false;
		
			for(Node fw : listFW) {
				List<Elements> elements = fw.getConfiguration().getFirewall().getElements();
				if(elements.size() == 0) {
					correct1 = true;
				} else if(elements.size() == 1) {
					Elements element = elements.get(0);
					if(
						(element.getSource().equals("-1.-1.-1.-1") && element.getDestination().equals("10.-1.-1.-1")) ||
						(element.getSource().equals("20.0.0.1") && element.getDestination().equals("10.0.0.1"))
						) {
						correct2 = true;
					}
				}
			}
		
			assertTrue(correct1 && correct2);
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
	
	
	/**
	 * Test about placement
	 * There are two paths, but now firewall must be placed
	 */
	@Test
	public void testFWCorrectness07(){
		try {
			VerefooSerializer result = test( "./testfile/FWCorrectness/FWCorrect07.xml"); 
			assertTrue(result.isSat());
			List<Node> listFW = result.getNfv().getGraphs().getGraph().get(0).getNode().stream().filter(n -> n.getFunctionalType().equals(FunctionalTypes.FIREWALL)).collect(Collectors.toList());
			assertTrue(listFW.size() == 0);
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
	/**
	 * Test about removal of firewall
	 * Only a firewall is removed, because it was optional
	 */
	@Test
	public void testFWCorrectness08(){
		try {
			VerefooSerializer result = test( "./testfile/FWCorrectness/FWCorrect08.xml"); 
			assertTrue(result.isSat());
			List<Node> listFW = result.getNfv().getGraphs().getGraph().get(0).getNode().stream().filter(n -> n.getFunctionalType().equals(FunctionalTypes.FIREWALL)).collect(Collectors.toList());
			assertTrue(listFW.size() == 2);
			
			boolean correct1 = false;
			boolean correct2 = false;
			boolean correct3 = true;
			
			for(Node fw : listFW) {
				if(fw.getName().equals("30.0.0.1")) {
					correct1 = true;
				}
				if(fw.getName().equals("30.0.0.3")) {
					correct2 = true;
				}
				if(fw.getName().equals("30.0.0.2")) {
					correct3 = false;
				}
			}
		
			assertTrue(correct1 && correct2 && correct3);
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
	
	
	/**
	 * Test about removal of firewall
	 * All the firewalls are removed, except one, and a client becomes directly connected to the server
	 */
	@Test
	public void testFWCorrectness09(){
		try {
			VerefooSerializer result = test( "./testfile/FWCorrectness/FWCorrect09.xml"); 
			assertTrue(result.isSat());
			List<Node> listFW = result.getNfv().getGraphs().getGraph().get(0).getNode().stream().filter(n -> n.getFunctionalType().equals(FunctionalTypes.FIREWALL)).collect(Collectors.toList());
			assertTrue(listFW.size() == 1);
			
			boolean correct = false;
			
			for(Node fw : listFW) {
				if(fw.getName().equals("30.0.0.2")) {
					correct = true;
				}
			}
		
			assertTrue(correct);
			
			Node client = result.getNfv().getGraphs().getGraph().get(0).getNode().stream().filter(n -> n.getName().equals("10.0.0.1")).findAny().orElse(null);
			assertTrue(client != null);
			assertTrue(client.getNeighbour().get(0).getName().equals("20.0.0.1"));
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
	
}
