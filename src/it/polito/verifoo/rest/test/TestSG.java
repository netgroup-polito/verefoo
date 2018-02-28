/**
 * 
 */
package it.polito.verifoo.rest.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

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

import it.polito.verifoo.rest.common.BadGraphError;
import it.polito.verifoo.rest.common.Translator;
import it.polito.verifoo.rest.common.VerifooProxy;
import it.polito.verifoo.rest.jaxb.Graph;
import it.polito.verifoo.rest.jaxb.NFV;
import it.polito.verifoo.rest.jaxb.NodeRefType;
import it.polito.verifoo.rest.jaxb.PName;
import it.polito.verifoo.rest.jaxb.Property;
import it.polito.verigraph.mcnet.components.IsolationResult;
/**
 * 
 * This class runs some tests in order to check the correctness of the VerifooProxy class 
 *
 */
public class TestSG {

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
	
	private NFV init(String file) throws JAXBException, SAXException, FileNotFoundException{
		// create a JAXBContext capable of handling the generated classes
        JAXBContext jc = JAXBContext.newInstance( "it.polito.verifoo.rest.jaxb" );
        // create an Unmarshaller
        Unmarshaller u = jc.createUnmarshaller();
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI); 
        Schema schema = sf.newSchema( new File( "./xsd/nfvSchema.xsd" )); 
        u.setSchema(schema);
        // unmarshal a document into a tree of Java content objects
        NFV root = (NFV) u.unmarshal( new FileInputStream( file ) );
        for(Graph g:root.getGraphs().getGraph()){
        	VerifooProxy test = new VerifooProxy(g, root.getHosts(), root.getConnections(),root.getConstraints());
        	IsolationResult res=test.checkNFFGProperty(root.getPropertyDefinition());
        	if(res.result != Status.UNSATISFIABLE)
        		new Translator(res.model.toString(),root).convert();
        	root.getPropertyDefinition().getProperty().stream().filter(p->p.getGraph()==g.getId()).forEach(p -> p.setIsSat(res.result!=Status.UNSATISFIABLE)); 
        }
		return root;
		
	}
	
	private void test(String file, boolean sat) throws Exception{
		NFV root = init(file);
        root.getPropertyDefinition().getProperty().forEach(p ->{
        	org.junit.Assert.assertEquals(sat, p.isIsSat());
        });
        return;
	}
	@Test
	public void testBasicPlacement(){
		try {
			test( "./testfile/ServiceGraphs/sg5nodes1host.xml", true); //Working
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	@Test
	public void testModel() throws Exception {
        NFV root = init("./testfile/ServiceGraphs/sg4nodes5hostConnection.xml");
        root.getPropertyDefinition().getProperty().forEach(p ->{
        	org.junit.Assert.assertEquals(true, p.isIsSat());
        });
        List<String> n = root.getHosts().getHost().stream().filter(h-> h.getName().equals("host1") || h.getName().equals("host2")).flatMap(h -> h.getNodeRef().stream()).map(nr -> nr.getNode()).collect(Collectors.toList()); 
        org.junit.Assert.assertEquals(true, n.contains("node1") && n.contains("node2") && n.contains("node3"));
        return;
	}
	@Test
	public void testOptimalPlacement() throws Exception {
        //minimize latency
        NFV root = init("./testfile/ServiceGraphs/sg4nodes5host.xml");
        root.getPropertyDefinition().getProperty().forEach(p ->{
        	org.junit.Assert.assertEquals(true, p.isIsSat());
        });
        List<String> n = root.getHosts().getHost().stream().filter(h-> h.getName().equals("host5")).findFirst().get().getNodeRef().stream().map(nr -> nr.getNode()).collect(Collectors.toList());
        org.junit.Assert.assertEquals(true, n.contains("node2"));
        //minimize latency and number of servers used
        root = init("./testfile/ServiceGraphs/sg5nodes3host.xml");
        root.getPropertyDefinition().getProperty().forEach(p ->{
        	org.junit.Assert.assertEquals(true, p.isIsSat());
        });
        n = root.getHosts().getHost().stream().filter(h-> h.getName().equals("host3")).findFirst().get().getNodeRef().stream().map(nr -> nr.getNode()).collect(Collectors.toList());
        org.junit.Assert.assertEquals(true, n.contains("node2"));
        org.junit.Assert.assertEquals(false, root.getHosts().getHost().stream().filter(h-> h.getName().equals("host2")).findFirst().get().isActive());
        return;
	}
	
	@Test
	public void testMultipleEndpointWithSamePlacement() throws Exception {
		//2 clients
        NFV root = init("./testfile/ServiceGraphs/sg2clients3nodes3host.xml");
        root.getPropertyDefinition().getProperty().forEach(p ->{
        	org.junit.Assert.assertEquals(true, p.isIsSat());
        });
        List<String> n = root.getHosts().getHost().stream().filter(h-> h.getName().equals("host1")).flatMap(h -> h.getNodeRef().stream()).map(nr -> nr.getNode()).collect(Collectors.toList()); 
        org.junit.Assert.assertEquals(true, n.contains("node1") && n.contains("node3"));

		//2 servers
        root = init("./testfile/ServiceGraphs/sg3nodes2servers3host.xml");
        root.getPropertyDefinition().getProperty().forEach(p ->{
        	org.junit.Assert.assertEquals(true, p.isIsSat());
        });
        n = root.getHosts().getHost().stream().filter(h-> h.getName().equals("host3")).flatMap(h -> h.getNodeRef().stream()).map(nr -> nr.getNode()).collect(Collectors.toList()); 
        org.junit.Assert.assertEquals(true, n.contains("node2") && n.contains("node3"));

		//2 clients and 2 servers
        root = init("./testfile/ServiceGraphs/sg2clients4nodes2servers3host.xml");
        root.getPropertyDefinition().getProperty().forEach(p ->{
        	org.junit.Assert.assertEquals(true, p.isIsSat());
        });
        List<String> n1 = root.getHosts().getHost().stream().filter(h-> h.getName().equals("host1")).flatMap(h -> h.getNodeRef().stream()).map(nr -> nr.getNode()).collect(Collectors.toList()); 
        org.junit.Assert.assertEquals(true, n1.contains("node1") && n1.contains("node3"));
        List<String> n2 = root.getHosts().getHost().stream().filter(h-> h.getName().equals("host3")).flatMap(h -> h.getNodeRef().stream()).map(nr -> nr.getNode()).collect(Collectors.toList()); 
        org.junit.Assert.assertEquals(true, n2.contains("node2") && n2.contains("node4"));
        
		//2 clients and 2 servers in a limit case (see the XML for more info)
        root = init("./testfile/ServiceGraphs/sg2clients3nodes2servers3host.xml");
        root.getPropertyDefinition().getProperty().forEach(p ->{
        	org.junit.Assert.assertEquals(true, p.isIsSat());
        });
        n1 = root.getHosts().getHost().stream().filter(h-> h.getName().equals("host1")).flatMap(h -> h.getNodeRef().stream()).map(nr -> nr.getNode()).collect(Collectors.toList()); 
        org.junit.Assert.assertEquals(true, n1.contains("node1") && n1.contains("node2") && n1.contains("node3"));
        return;
	}
	
	@Test
	public void testMultipleEndpointWithDifferentPlacement() throws Exception {
		//2 clients
        NFV root = init("./testfile/ServiceGraphs/sg2clients3nodes3hostDiffEndpoints.xml");
        root.getPropertyDefinition().getProperty().forEach(p ->{
        	org.junit.Assert.assertEquals(true, p.isIsSat());
        });
        List<String> n1 = root.getHosts().getHost().stream().filter(h-> h.getName().equals("host1")).flatMap(h -> h.getNodeRef().stream()).map(nr -> nr.getNode()).collect(Collectors.toList()); 
        org.junit.Assert.assertEquals(true, n1.contains("node1"));
        List<String> n2 = root.getHosts().getHost().stream().filter(h-> h.getName().equals("host2")).flatMap(h -> h.getNodeRef().stream()).map(nr -> nr.getNode()).collect(Collectors.toList()); 
        org.junit.Assert.assertEquals(true, n2.contains("node3"));

		//2 servers
        root = init("./testfile/ServiceGraphs/sg3nodes2servers3hostDiffEndpoints.xml");
        root.getPropertyDefinition().getProperty().forEach(p ->{
        	org.junit.Assert.assertEquals(true, p.isIsSat());
        });
        n1 = root.getHosts().getHost().stream().filter(h-> h.getName().equals("host2")).flatMap(h -> h.getNodeRef().stream()).map(nr -> nr.getNode()).collect(Collectors.toList()); 
        org.junit.Assert.assertEquals(true, n1.contains("node3"));
        n2 = root.getHosts().getHost().stream().filter(h-> h.getName().equals("host3")).flatMap(h -> h.getNodeRef().stream()).map(nr -> nr.getNode()).collect(Collectors.toList()); 
        org.junit.Assert.assertEquals(true, n2.contains("node2"));
        
		//2 clients and 2 servers
        root = init("./testfile/ServiceGraphs/sg2clients4nodes2servers3hostDiffEndpoints.xml");
        root.getPropertyDefinition().getProperty().forEach(p ->{
        	org.junit.Assert.assertEquals(true, p.isIsSat());
        });
        n1 = root.getHosts().getHost().stream().filter(h-> h.getName().equals("host1")).flatMap(h -> h.getNodeRef().stream()).map(nr -> nr.getNode()).collect(Collectors.toList()); 
        org.junit.Assert.assertEquals(true, n1.contains("node1"));
        n2 = root.getHosts().getHost().stream().filter(h-> h.getName().equals("host2")).flatMap(h -> h.getNodeRef().stream()).map(nr -> nr.getNode()).collect(Collectors.toList()); 
        org.junit.Assert.assertEquals(true, n2.contains("node3") && n2.contains("node4"));
        n1 = root.getHosts().getHost().stream().filter(h-> h.getName().equals("host3")).flatMap(h -> h.getNodeRef().stream()).map(nr -> nr.getNode()).collect(Collectors.toList()); 
        org.junit.Assert.assertEquals(true, n1.contains("node2"));
        return;
	}
	
	@Test
	public void testNewConstraints(){
		try {
			test( "./testfile/ServiceGraphs/sg4nodes5hostSAT-NoConstraints.xml", true);
			test( "./testfile/ServiceGraphs/sg4nodes3hostUNSAT-Bandwidth.xml", false);
			test( "./testfile/ServiceGraphs/sg4nodes5hostUNSAT-CPU.xml", false);
			test( "./testfile/ServiceGraphs/sg4nodes3hostUNSAT-MaxVNF.xml", false);
			test( "./testfile/ServiceGraphs/sg4nodes5hostUNSAT-SupportedVNF.xml", false);
			test( "./testfile/ServiceGraphs/sg4nodes5hostUNSAT-Memory.xml", false);
		} catch (Exception e) {
			fail(e.toString());
		}
	}
	@Test
	public void testReachPropBetweenDiffEndpoints(){
		try {
			test( "./testfile/ServiceGraphs/sg2clients4nodes2servers3hostSAT_AtoB-FW.xml", true);
			test( "./testfile/ServiceGraphs/sg2clients4nodes2servers3hostSAT_AtoD-FW.xml", true);
			test( "./testfile/ServiceGraphs/sg2clients4nodes2servers3hostSAT_CtoB-FW.xml", true);
			test( "./testfile/ServiceGraphs/sg2clients4nodes2servers3hostSAT_CtoD-FW.xml", true);
			test( "./testfile/ServiceGraphs/sg2clients4nodes2servers3hostUNSAT_AtoB-FW.xml", false);
			test( "./testfile/ServiceGraphs/sg2clients4nodes2servers3hostUNSAT_AtoD-FW.xml", false);
			test( "./testfile/ServiceGraphs/sg2clients4nodes2servers3hostUNSAT_CtoB-FW.xml", false);
			test( "./testfile/ServiceGraphs/sg2clients4nodes2servers3hostUNSAT_CtoD-FW.xml", false);
		} catch (Exception e) {
			fail(e.toString());
		}
	}
}
