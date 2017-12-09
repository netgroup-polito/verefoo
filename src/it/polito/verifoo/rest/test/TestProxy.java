/**
 * 
 */
package it.polito.verifoo.rest.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.microsoft.z3.Status;

import it.polito.verifoo.rest.app.JniFinder;
import it.polito.verifoo.rest.common.BadGraphException;
import it.polito.verifoo.rest.common.Translator;
import it.polito.verifoo.rest.common.VerifooProxy;
import it.polito.verifoo.rest.jaxb.Graph;
import it.polito.verifoo.rest.jaxb.NFV;
import it.polito.verigraph.mcnet.components.IsolationResult;


public class TestProxy {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		String dir=System.getenv("TRAVIS_BUILD_DIR");
		if(dir != null && !dir.isEmpty()){
			JniFinder.extractZ3Lib(dir+"/WebContent//EB-INF/lib/jni/");
		}
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
        JAXBContext jc = JAXBContext.newInstance( "it.polito.verifoo.rest.jaxb" );
        // create an Unmarshaller
        Unmarshaller u = jc.createUnmarshaller();
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI); 
        Schema schema = sf.newSchema( new File( "./xsd/nfvInfo.xsd" )); 
        u.setSchema(schema);
        // unmarshal a document into a tree of Java content objects
        NFV root = (NFV) u.unmarshal( new FileInputStream( file ) );
        for(Graph g:root.getGraphs().getGraph()){
        	VerifooProxy test = new VerifooProxy(g, root.getHosts(), root.getConnections(),root.getCapacityDefinition());
        	IsolationResult res=test.checkNFFGProperty();
        	if(res.result != Status.UNSATISFIABLE)
        		new Translator(res.model.toString(),root).convert();
        	root.getPropertyDefinition().getProperty().stream().filter(p->p.getGraph()==g.getId()).findFirst().get().setIsSat(res.result!=Status.UNSATISFIABLE); 
        }
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
			test( "./testfile/nfv5nodes7hostsUNSAT-CACHE.xml", false); //Working
		} catch (Exception e) {
			fail(e.toString());
		} 
	}
	
	@Test
	public void testNAT_UNSAT() {
		try {
			test( "./testfile/nfv3nodes3hostsUNSAT-NAT.xml", false); //Working
		} catch (Exception e) {
			fail(e.toString());
		}		
	}
	@Test
	public void testANTISPAM_UNSAT() {
		try {
			test( "./testfile/nfv5nodes7hostsUNSAT-ANTISPAM.xml", false); //Working
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
	@Test(expected=BadGraphException.class)
	public void testNoMiddeleBoxes() throws Exception {
		test( "./testfile/nfv3nodes2hostsNoMIDDLEBOXES.xml", false); //Working
		fail("Exception not thrown");
		
	}
	@Test(expected=BadGraphException.class)
	public void testHostDisconnected() throws Exception {
		test( "./testfile/nfv3nodes3hostsHostsDisconnected.xml", false); //Working
		fail("Exception not thrown");
	}
	@Test(expected=BadGraphException.class)
	public void testWrongNodesConfiguration() throws Exception {
		test( "./testfile/nfv3nodes3hostsWrongNodesConfiguration.xml", false); //Working
		fail("Exception not thrown");
	}
}
