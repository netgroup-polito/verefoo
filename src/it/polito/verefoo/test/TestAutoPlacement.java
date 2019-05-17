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
import it.polito.verefoo.jaxb.Elements;
import it.polito.verefoo.jaxb.FunctionalTypes;
import it.polito.verefoo.jaxb.Graph;
import it.polito.verefoo.jaxb.NFV;
import it.polito.verefoo.jaxb.Node;
import it.polito.verefoo.jaxb.PName;
import it.polito.verefoo.jaxb.Property;
import it.polito.verefoo.translator.Translator;
import it.polito.verigraph.extra.VerificationResult;
/**
 * 
 * This class runs some tests in order to check the correctness of the auto-placement module 
 *
 */
public class TestAutoPlacement {

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
	private boolean selectCond(Node n, FunctionalTypes ft){
        if(ft.equals(FunctionalTypes.FIREWALL)){
        	return (n.getConfiguration().getFirewall().getElements().size() > 0);
        }
        if(ft.equals(FunctionalTypes.DPI)){
        	return (n.getConfiguration().getDpi().getNotAllowed().size() > 0);
        }
        if(ft.equals(FunctionalTypes.ANTISPAM)){
        	return (n.getConfiguration().getAntispam().getSource().size() > 0);
        }
        return false;
	}
	private List<Node> test(String file, boolean sat, FunctionalTypes ft) throws Exception{
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
        // unmarshal a document into a tree of Java content objects
        NFV root = (NFV) u.unmarshal( new FileInputStream( file ) );
		//long endU=System.currentTimeMillis();
        //System.out.println("Unmarshalling -> " + ((endU-beginAll)/1000) );
        VerefooSerializer test = new VerefooSerializer(root);
        
        	
        tmp.addAll(
        		root.getGraphs().getGraph().stream().flatMap(g -> g.getNode().stream())
        		.filter(n -> n.getFunctionalType().equals(ft) && selectCond(n, ft))
        		.collect(Collectors.toList())
        		);
        if(test.isSat()){
        		System.out.println("SAT");
    	}
    	else{
    		System.out.println("UNSAT");
    	}
		long endAll=System.currentTimeMillis();
        System.out.println("Total time -> " + (endAll-beginAll)+"ms" );
        root.getPropertyDefinition().getProperty().forEach(p ->{
        	org.junit.Assert.assertEquals(sat, p.isIsSat());
        });
        return tmp;
	}
	
	@Test
	public void testAutoFW_RR(){
		try {
			List<Node> autoNodes = test( "./testfile/Autoconfiguration/nfv3nodes7hostsAutoPlace-FW-RR.xml", true, FunctionalTypes.FIREWALL); //Working
			assertTrue(autoNodes.size() == 0);
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	@Test
	public void testAutoFW_RI(){
		try {
			List<Node> autoNodes = test( "./testfile/Autoconfiguration/nfv3nodes7hostsAutoPlace-FW-RI.xml", true, FunctionalTypes.FIREWALL); //Working
			assertTrue(autoNodes.size() == 1);
			assertTrue(autoNodes.get(0).getName().equals("node1"));
			List<Elements> e = autoNodes.get(0).getConfiguration().getFirewall().getElements();
			assertTrue(e.size() == 1);
			assertTrue(e.get(0).getSource().equals("nodeC") && e.get(0).getDestination().equals("nodeB"));
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	@Test
	public void testAutoFW_IR(){
		try {
			List<Node> autoNodes = test( "./testfile/Autoconfiguration/nfv3nodes7hostsAutoPlace-FW-IR.xml", true, FunctionalTypes.FIREWALL); //Working
			assertTrue(autoNodes.size() == 1);
			assertTrue(autoNodes.get(0).getName().equals("node1"));
			List<Elements> e = autoNodes.get(0).getConfiguration().getFirewall().getElements();
			assertTrue(e.size() == 1);
			assertTrue(e.get(0).getSource().equals("nodeA") && e.get(0).getDestination().equals("nodeB"));
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	@Test
	public void testAutoFW_II(){
		try {
			List<Node> autoNodes = test( "./testfile/Autoconfiguration/nfv3nodes7hostsAutoPlace-FW-II.xml", true, FunctionalTypes.FIREWALL); //Working
			assertTrue(autoNodes.size() == 1);
			assertTrue(autoNodes.get(0).getName().equals("node3"));
			List<Elements> e = autoNodes.get(0).getConfiguration().getFirewall().getElements();
			assertTrue(e.size() == 1);
			assertTrue(e.get(0).getSource().equals("node2") && e.get(0).getDestination().equals("nodeB"));
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
	
	@Test
	public void testAutoDPI_RR(){
		try {
			List<Node> autoNodes = test( "./testfile/Autoconfiguration/nfv3nodes7hostsAutoPlace-DPI-RR.xml", true, FunctionalTypes.DPI); //Working
			assertTrue(autoNodes.size() == 0);
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	@Test
	public void testAutoDPI_RI(){
		try {
			List<Node> autoNodes = test( "./testfile/Autoconfiguration/nfv3nodes7hostsAutoPlace-DPI-RI.xml", true, FunctionalTypes.DPI); //Working
			assertTrue(autoNodes.size() == 1);
			List<String> e = autoNodes.get(0).getConfiguration().getDpi().getNotAllowed();
			assertTrue(e.size() == 1);
			assertTrue(e.get(0).equals("weapons"));
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	@Test
	public void testAutoDPI_IR(){
		try {
			List<Node> autoNodes = test( "./testfile/Autoconfiguration/nfv3nodes7hostsAutoPlace-DPI-IR.xml", true, FunctionalTypes.DPI); //Working
			assertTrue(autoNodes.size() == 1);
			List<String> e = autoNodes.get(0).getConfiguration().getDpi().getNotAllowed();
			assertTrue(e.size() == 1);
			assertTrue(e.get(0).equals("cats"));
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	@Test
	public void testAutoDPI_II(){
		try {
			List<Node> autoNodes = test( "./testfile/Autoconfiguration/nfv3nodes7hostsAutoPlace-DPI-II.xml", true, FunctionalTypes.DPI); //Working
			assertTrue(autoNodes.size() == 1);
			List<String> e = autoNodes.get(0).getConfiguration().getDpi().getNotAllowed();
			assertTrue(e.size() == 2);
			assertTrue( (e.get(0).equals("weapons") && e.get(1).equals("cats")) || (e.get(0).equals("cats") && e.get(1).equals("weapons")) );
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
	@Test
	public void testAutoAntispam_RR(){
		try {
			List<Node> autoNodes = test( "./testfile/Autoconfiguration/nfv3nodes7hostsAutoPlace-Antispam-RR.xml", true, FunctionalTypes.ANTISPAM); //Working
			assertTrue(autoNodes.size() == 0);
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	@Test
	public void testAutoAntispam_RI(){
		try {
			List<Node> autoNodes = test( "./testfile/Autoconfiguration/nfv3nodes7hostsAutoPlace-Antispam-RI.xml", true, FunctionalTypes.ANTISPAM); //Working
			assertTrue(autoNodes.size() == 1);
			List<String> e = autoNodes.get(0).getConfiguration().getAntispam().getSource();
			assertTrue(e.size() == 1);
			assertTrue(e.get(0).equals("2"));
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	@Test
	public void testAutoAntispam_IR(){
		try {
			List<Node> autoNodes = test( "./testfile/Autoconfiguration/nfv3nodes7hostsAutoPlace-Antispam-IR.xml", true, FunctionalTypes.ANTISPAM); //Working
			assertTrue(autoNodes.size() == 1);
			List<String> e = autoNodes.get(0).getConfiguration().getAntispam().getSource();
			assertTrue(e.size() == 1);
			assertTrue(e.get(0).equals("1"));
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	@Test
	public void testAutoAntispam_II(){
		try {
			List<Node> autoNodes = test( "./testfile/Autoconfiguration/nfv3nodes7hostsAutoPlace-Antispam-II.xml", true, FunctionalTypes.ANTISPAM); //Working
			assertTrue(autoNodes.size() == 1);
			List<String> e = autoNodes.get(0).getConfiguration().getAntispam().getSource();
			assertTrue(e.size() == 2);
			assertTrue((e.get(0).equals("1") && e.get(1).equals("2")) || (e.get(0).equals("2") && e.get(1).equals("1")));
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
	
}
