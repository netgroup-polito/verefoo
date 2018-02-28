/**
 * 
 */
package it.polito.verifoo.rest.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
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

import it.polito.verifoo.rest.common.BadGraphError;
import it.polito.verifoo.rest.common.Translator;
import it.polito.verifoo.rest.common.VerifooProxy;
import it.polito.verifoo.rest.jaxb.Graph;
import it.polito.verifoo.rest.jaxb.NFV;
import it.polito.verifoo.rest.jaxb.PName;
import it.polito.verifoo.rest.jaxb.Property;
import it.polito.verigraph.mcnet.components.IsolationResult;
/**
 * 
 * This class runs some tests in order to check the correctness of the VerifooProxy class 
 *
 */
public class TestProxy {

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
	
	private void test(String file, boolean sat) throws Exception{
		// create a JAXBContext capable of handling the generated classes
        System.out.println("===========FILE " + file + "===========");
		long beginAll=System.currentTimeMillis();
        JAXBContext jc = JAXBContext.newInstance( "it.polito.verifoo.rest.jaxb" );
        // create an Unmarshaller
        Unmarshaller u = jc.createUnmarshaller();
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI); 
        Schema schema = sf.newSchema( new File( "./xsd/nfvSchema.xsd" )); 
        u.setSchema(schema);
        // unmarshal a document into a tree of Java content objects
        NFV root = (NFV) u.unmarshal( new FileInputStream( file ) );
		//long endU=System.currentTimeMillis();
        //System.out.println("Unmarshalling -> " + ((endU-beginAll)/1000) );
        for(Graph g:root.getGraphs().getGraph()){
        	long beginVP=System.currentTimeMillis();
        	VerifooProxy test = new VerifooProxy(g, root.getHosts(), root.getConnections(),root.getConstraints());
        	long endVP=System.currentTimeMillis();
            System.out.println("Graph " + g.getId() + ": creating condition -> " + ((endVP-beginVP)/1000) );
        	IsolationResult res=test.checkNFFGProperty(root.getPropertyDefinition());
        	long endCheck=System.currentTimeMillis();
            System.out.println(g.getId() + ": checking property -> " + ((endCheck-endVP)/1000) );
        	if(res.result != Status.UNSATISFIABLE)
        		new Translator(res.model.toString(),root).convert();
        	root.getPropertyDefinition().getProperty().stream().filter(p->p.getGraph()==g.getId()).forEach(p -> p.setIsSat(res.result!=Status.UNSATISFIABLE)); 
        	long endT=System.currentTimeMillis();
            System.out.println(g.getId() + ": translating model -> " + ((endT-endCheck)/1000) );
        }
		long endAll=System.currentTimeMillis();
        System.out.println("Total time -> " + ((endAll-beginAll)/1000) );
        root.getPropertyDefinition().getProperty().forEach(p ->{
        	org.junit.Assert.assertEquals(sat, p.isIsSat());
        });
        return;
	}
	
	@Test(expected=Exception.class)
	public void testBadClientConf() throws Exception {
		test( "./testfile/nfv5nodes7hostsUNSAT-WEB.xml", false); //Working
		fail("Exception not thrown");
	}
	@Test
	public void testFW_UNSAT(){
		try {
			test( "./testfile/nfv5nodes7hostsUNSAT-FW.xml", false); //Working
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	@Test
	public void testCACHE_UNSAT(){
		
		try {
			test( "./testfile/nfv5nodes7hostsSAT-CACHE.xml", true); //Working
		} catch (Exception e) {
			fail(e.toString());
		} 
	}
	
	@Test
	public void testNAT_UNSAT() {
		try {
			test( "./testfile/nfv3nodes3hostsSAT-NAT.xml", true); //Working
		} catch (Exception e) {
			fail(e.toString());
		}		
	}
	@Test
	public void testANTISPAM_UNSAT() {
		try {
			test( "./testfile/nfv5nodes7hostsSAT-ANTISPAM.xml", true); //Working
		} catch (Exception e) {
			fail(e.toString());
		}		
	}
	@Test
	public void testMAIL_SAT() {
		try {
			test( "./testfile/nfv3nodes3hostsSAT-MAIL.xml", true); //Working
		} catch (Exception e) {
			fail(e.toString());
		}		
	}
	@Test
	public void testAS_SAT() {
		try {
			test( "./testfile/AS.xml", true); //Working
		} catch (Exception e) {
			fail(e.toString());
		}		
	}
	@Test
	public void testBiggest_SAT() {
		try {
			test( "./testfile/Biggest.xml", true); //Working
		} catch (Exception e) {
			fail(e.toString());
		}		
	}
	@Test
	public void testGEANT_SAT() {
		try {
			test( "./testfile/GEANT.xml", true); //Working (
		} catch (Exception e) {
			fail(e.toString());
		}		
	}
	@Test
	public void testInternet_SAT() {
		try {
			test( "./testfile/Internet2.xml", true); //Working
		} catch (Exception e) {
			fail(e.toString());
		}		
	}
	@Test
	public void testUniv_SAT() {
		try {
			test( "./testfile/UNIV1.xml", true); //Working
		} catch (Exception e) {
			fail(e.toString());
		}	
	}
	@Test(expected=BadGraphError.class)
	public void testNoMiddeleBoxes() throws Exception {
		test( "./testfile/XmlWith2Host.xml", false); //Working
		fail("Exception not thrown");
		
	}
	@Test(expected=BadGraphError.class)
	public void testHostDisconnected() throws Exception {
		test( "./testfile/nfv3nodes3hostsHostsDisconnected.xml", false); //Working
		fail("Exception not thrown");
	}
	@Test(expected=BadGraphError.class)
	public void testWrongNodesConfiguration() throws Exception {
		test( "./testfile/XmlWith2Host2Node.xml", false); //Working
		fail("Exception not thrown");
	}
}
