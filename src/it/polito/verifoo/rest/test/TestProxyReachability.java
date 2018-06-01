/**
 * 
 */
package it.polito.verifoo.rest.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
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

import it.polito.verifoo.rest.common.BadGraphError;
import it.polito.verifoo.rest.common.Translator;
import it.polito.verifoo.rest.common.VerifooProxy;
import it.polito.verifoo.rest.common.VerifooSerializer;
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
public class TestProxyReachability {

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
        VerifooSerializer test = new VerifooSerializer(root);
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
        return;
	}
	
	@Test
	public void testNoHost_SAT(){
		try {
			test( "./testfile/nfv3nodes-NoHosts.xml", false); //Working
		} catch (Exception e) {
			fail(e.toString());
		}
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
	public void testDPI_UNSAT() {
		try {
			test( "./testfile/nfv3nodes3hostsUNSAT-DPI.xml", false);
		} catch (Exception e) {
			fail(e.toString());
		}		
	}
	@Test
	public void testCACHE_SAT(){
		
		try {
			test( "./testfile/nfv5nodes7hostsSAT-CACHE.xml", true); //Working
		} catch (Exception e) {
			fail(e.toString());
		} 
	}
	
	@Test
	public void testNAT_SAT() {
		try {
			test( "./testfile/nfv3nodes3hostsSAT-NAT.xml", true); //Working
		} catch (Exception e) {
			fail(e.toString());
		}		
	}
	//@Test
	public void testANTISPAM_SAT() {
		try {
			test( "./testfile/nfv5nodes7hostsSAT-ANTISPAM.xml", true); //Working
		} catch (Exception e) {
			fail(e.toString());
		}		
	}
	@Test
	public void testANTISPAM_UNSAT() {
		try {
			test( "./testfile/nfv3nodes3hostsUNSAT-ANTISPAM.xml", false); //Working
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
	public void testWEB_SAT() {
		try {
			test( "./testfile/nfv5nodes7hostsSAT-WEB.xml", true); //Working
		} catch (Exception e) {
			fail(e.toString());
		}		
	}
	@Test
	public void testMAIL_PropertySpec_SAT() {
		try {
			test( "./testfile/nfv5nodes7hostsSAT-MAIL-PropertySpec.xml", true); //Working
		} catch (Exception e) {
			fail(e.toString());
		}		
	}
	@Test
	public void testWEB_PropertySpec_SAT() {
		try {
			test( "./testfile/nfv5nodes7hostsSAT-WEB-PropertySpec.xml", true); //Working
		} catch (Exception e) {
			fail(e.toString());
		}		
	}
	@Test
	public void testMAIL_PropertySpec_UNSAT() {
		try {
			test( "./testfile/nfv5nodes7hostsUNSAT-MAIL-PropertySpec.xml", false); //Working
		} catch (Exception e) {
			fail(e.toString());
		}		
	}
	@Test
	public void testWEB_PropertySpec_UNSAT() {
		try {
			test( "./testfile/nfv5nodes7hostsUNSAT-WEB-PropertySpec.xml", false); //Working
		} catch (Exception e) {
			fail(e.toString());
		}		
	}
	@Test
	public void testFullMesh() {
		try {
			test( "./testfile/nfv5nodes7hostsSAT-FullMesh.xml", true); //Working
		} catch (Exception e) {
			fail(e.toString());
		}		
	}
	@Test
	public void testNoFixedServer() {
		try {
			test( "./testfile/nfv5nodes7hostsSAT-NoFixedServer.xml", true); //Working
		} catch (Exception e) {
			fail(e.toString());
		}		
	}
	@Test
	public void testFullMeshNoFixedServer() {
		try {
			test( "./testfile/nfv5nodes7hostsSAT-FullMeshNoFixedServer.xml", true); //Working
		} catch (Exception e) {
			fail(e.toString());
		}		
	}
	@Test
	public void testPaths() {
		try {
			test( "./testfile/nfv3nodes3hosts2Paths-SAT.xml", true); //Working
		} catch (Exception e) {
			fail(e.toString());
		}		
	}
}
