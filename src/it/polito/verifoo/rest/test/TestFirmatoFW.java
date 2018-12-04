/**
 * 
 */
package it.polito.verifoo.rest.test;

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

import it.polito.verifoo.rest.common.BadGraphError;
import it.polito.verifoo.rest.common.Translator;
import it.polito.verifoo.rest.common.VerifooProxy;
import it.polito.verifoo.rest.common.VerifooSerializer;
import it.polito.verifoo.rest.jaxb.Elements;
import it.polito.verifoo.rest.jaxb.FunctionalTypes;
import it.polito.verifoo.rest.jaxb.Graph;
import it.polito.verifoo.rest.jaxb.NFV;
import it.polito.verifoo.rest.jaxb.Node;
import it.polito.verifoo.rest.jaxb.PName;
import it.polito.verifoo.rest.jaxb.Property;
import it.polito.verigraph.mcnet.components.IsolationResult;
/**
 * 
 * This class runs some tests in order to check the correctness of the new firewall model 
 *
 */
public class TestFirmatoFW {

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
        test.getResult().getPropertyDefinition().getProperty().forEach(p ->{
        	org.junit.Assert.assertEquals(sat, p.isIsSat());
        });
        return tmp;
	}
	
	@Test
	public void testFW_SAT(){
		try {
			test( "./testfile/FirmatoFW/nfv3policies-Verification-SAT.xml", true, FunctionalTypes.FIREWALL); //Working
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	@Test
	public void test2FW_SAT(){
		try {
			test( "./testfile/FirmatoFW/nfv2nodes3policiesAutoConf-NoStrict.xml", true, FunctionalTypes.FIREWALL); //Working
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	
	@Test
	public void testFW_UNSAT(){
		try {
			List<Node> autoNodes = test( "./testfile/FirmatoFW/nfv3policies-Verification-UNSAT.xml", false, FunctionalTypes.FIREWALL); //Working
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	@Test
	public void testPreProcessingAndMerging(){
		try {
			List<Node> autoNodes = test( "./testfile/FirmatoFW/Pre-Processing&Merging.xml", true, FunctionalTypes.FIREWALL); //Working
			assertTrue(autoNodes.size() == 1);
			List<Elements> e = autoNodes.get(0).getConfiguration().getFirewall().getElements();
			assertTrue(e.size() == 1);
			assertTrue(e.get(0).getSource().equals("-1.-1.-1.-1"));
		} catch (Exception e) {
			fail(e.toString());
		}
	}
}
