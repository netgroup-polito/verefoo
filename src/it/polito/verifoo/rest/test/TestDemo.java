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
import it.polito.verifoo.rest.jaxb.FunctionalTypes;
import it.polito.verifoo.rest.jaxb.Graph;
import it.polito.verifoo.rest.jaxb.NFV;
import it.polito.verifoo.rest.jaxb.Node;
import it.polito.verifoo.rest.jaxb.PName;
import it.polito.verifoo.rest.jaxb.Property;
import it.polito.verigraph.mcnet.components.IsolationResult;
/**
 * 
 * This class runs some tests in order to check the correctness of the implementation of the isolation property 
 *
 */
public class TestDemo {

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
	
	private NFV test(String file, boolean sat) throws Exception{
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
        test.getResult().getPropertyDefinition().getProperty().forEach(p ->{
        	org.junit.Assert.assertEquals(sat, p.isIsSat());
        });
        return root;
	}
	
	@Test
	public void test1_Isolation(){
		try {
			//specify the ports in the property now impacts on the verification
			test( "./testfile/Demo/1_nfv1nodes7hostsSAT-FW.xml", true);
			test( "./testfile/Demo/1_nfv1nodes7hostsSAT-FWPorts.xml", true);
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	@Test
	public void test2_Reachability(){
		
		try {
			//specify a different flow in the property now impacts on the verification
			test( "./testfile/Demo/2_nfv5nodes7hostsSAT-WEB-PropertySpec.xml", true);
			test( "./testfile/Demo/2_nfv5nodes7hostsUNSAT-WEB-PropertySpec.xml", false);
		} catch (Exception e) {
			fail(e.toString());
		} 
	}
	@Test
	public void test3_FirmatoFW(){
		try {
			//it is now possible to have a complex firewall behaviour
			test( "./testfile/Demo/3_nfv3policies-Verification-SAT.xml", true);
			test( "./testfile/Demo/3_nfv3policies-Verification-UNSAT.xml", false);
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	@Test
	public void test4_FirmatoFW_Overlapping(){
		try {
			//conflict between overlapping rule gives always UNSAT
			test( "./testfile/Demo/4_nfv3policies-VerificationOrder-UNSAT.xml", false);
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	@Test
	public void test5_Wildcards() {
		try {
			//firewalls are now capable of undestanding the wildcards
			NFV root = test( "./testfile/Demo/5_nfv5nodes7hostsWildcard.xml", true); 			
		} catch (Exception e) {
			fail(e.toString());
		}		
	}

	@Test
	public void test6_SimpleSG() {
		try {
			//it is possible to specify a graph as a network service
			test( "./testfile/Demo/6_sg4nodes5host.xml", true);
		} catch (Exception e) {
			fail(e.toString());
		}		
	}
	
	@Test
	public void test7_SGOptimalPlacement() throws Exception {
        //minimize latency 
        NFV root = test("./testfile/Demo/7_sg5nodes3host.xml", true);
        List<String> n = root.getHosts().getHost().stream().filter(h-> h.getName().equals("host3")).findFirst().get().getNodeRef().stream().map(nr -> nr.getNode()).collect(Collectors.toList());
        org.junit.Assert.assertEquals(true, n.contains("node2"));
        org.junit.Assert.assertEquals(false, root.getHosts().getHost().stream().filter(h-> h.getName().equals("host2")).findFirst().get().isActive());
        //minimize the number of used servers
		root = test( "./testfile/Demo/7_nfv5nodes7hostsSAT-FullMeshNoFixedServer.xml", true); 
		org.junit.Assert.assertEquals(2, root.getHosts().getHost().stream().filter(h -> h.isActive()).count());
        return;
	}
	
	@Test
	public void test8_MultipleEndpoint() throws Exception {
		//it is possible to have multiple endpoints
        NFV root = test("./testfile/Demo/8_sg2clients3nodes3hostDiffEndpoints.xml", true);
        List<String> n1 = root.getHosts().getHost().stream().filter(h-> h.getName().equals("host1")).flatMap(h -> h.getNodeRef().stream()).map(nr -> nr.getNode()).collect(Collectors.toList()); 
        org.junit.Assert.assertEquals(true, n1.contains("node1"));
        List<String> n2 = root.getHosts().getHost().stream().filter(h-> h.getName().equals("host2")).flatMap(h -> h.getNodeRef().stream()).map(nr -> nr.getNode()).collect(Collectors.toList()); 
        org.junit.Assert.assertEquals(true, n2.contains("node3"));
		
        //2 clients and 2 servers in a limit case (see the XML for more info)
        root = test("./testfile/Demo/8_sg2clients3nodes2servers3host.xml", true);
        n1 = root.getHosts().getHost().stream().filter(h-> h.getName().equals("host1")).flatMap(h -> h.getNodeRef().stream()).map(nr -> nr.getNode()).collect(Collectors.toList()); 
        org.junit.Assert.assertEquals(true, n1.contains("node1") && n1.contains("node2") && n1.contains("node3"));
        
        return;
	}
	
	@Test
	public void test9_NewConstraints(){
		try {
			//the deployment is conditioned by a certain number of physical resources
			test( "./testfile/Demo/9_sg4nodes5hostSAT-NoConstraints.xml", true);
			test( "./testfile/Demo/9_sg4nodes3hostUNSAT-Bandwidth.xml", false);
			test( "./testfile/Demo/9_sg4nodes5hostUNSAT-CPU.xml", false);
			test( "./testfile/Demo/9_sg4nodes3hostUNSAT-MaxVNF.xml", false);
			test( "./testfile/Demo/9_sg4nodes5hostUNSAT-SupportedVNF.xml", false);
			test( "./testfile/Demo/9_sg4nodes5hostUNSAT-Memory.xml", false);
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
	@Test
	public void test10_AutoConfiguration(){
		try {
			test( "./testfile/Demo/10_nfv2nodes3policiesAutoConf-NoStrict.xml", true);
		} catch (Exception e) {
			fail(e.toString());
		}
	}
}
