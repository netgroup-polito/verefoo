/**
 * 
 */
package it.polito.verifoo.rest.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
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
public class TestPerformance {
	private long condTime = 0, checkTime = 0, totTime = 0;
	private static int N_SIMULATIONS = 100;
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
	
	private NFV init(NFV root) throws JAXBException, SAXException, FileNotFoundException{
		
        for(Graph g:root.getGraphs().getGraph()){
        	long beginVP=System.currentTimeMillis();
        	VerifooProxy test = new VerifooProxy(g, root.getHosts(), root.getConnections(),root.getConstraints());
        	long endVP=System.currentTimeMillis();
        	condTime += (endVP-beginVP);
            //System.out.println("Graph " + g.getId() + ": creating condition -> " + ((endVP-beginVP)) + "ms");
        	Property pd = root.getPropertyDefinition().getProperty().stream().filter(p->p.getGraph()==g.getId() && p.getName().equals(PName.ISOLATION_PROPERTY)).findFirst().orElse(null);
        	if(pd == null) break;
        	IsolationResult res=test.checkNFFGProperty(pd.getSrc(), pd.getDst());
        	long endCheck=System.currentTimeMillis();
        	checkTime += (endCheck-endVP);
            //System.out.println(g.getId() + ": checking property -> " + ((endCheck-endVP)) + "ms");
        	if(res.result != Status.UNSATISFIABLE)
        		new Translator(res.model.toString(),root).convert();
        	root.getPropertyDefinition().getProperty().stream().filter(p->p.getGraph()==g.getId()).findFirst().get().setIsSat(res.result!=Status.UNSATISFIABLE); 
        	long endT=System.currentTimeMillis();
            //System.out.println(g.getId() + ": translating model -> " + ((endT-endCheck)/1000) + "s");
        }
		return root;
		
	}
	
	private void test(NFV root, boolean sat) throws Exception{
        //System.out.println("===========FILE " + file + "===========");
		long beginAll=System.currentTimeMillis();
		NFV rootTest = init(root);
		long endAll=System.currentTimeMillis();
		totTime += (endAll-beginAll);
        //System.out.println("Total time -> " + ((endAll-beginAll)/1000) + "s");
		rootTest.getPropertyDefinition().getProperty().forEach(p ->{
        	org.junit.Assert.assertEquals(sat, p.isIsSat());
        });
        return;
	}
	@Test
	public void testPerformance(){
		try {
			List<String> files = new ArrayList<>();
			files.add("./testfile/Performance/bgGEANT.xml");
			files.add("./testfile/Performance/bgGEANTWithConstraints.xml");
			files.add("./testfile/Performance/sgGEANT.xml");
			files.add("./testfile/Performance/sgGEANTDiffEndpoints.xml");
			files.add("./testfile/Performance/sgGEANTDiffEndpointsWithConstraints.xml");
			for(String f : files){
				condTime = 0;
				checkTime = 0;
				totTime = 0;
				System.out.println("===========FILE " + f + "===========");
				// create a JAXBContext capable of handling the generated classes
				//long beginAll=System.currentTimeMillis();
		        JAXBContext jc = JAXBContext.newInstance( "it.polito.verifoo.rest.jaxb" );
		        // create an Unmarshaller
		        Unmarshaller u = jc.createUnmarshaller();
		        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI); 
		        Schema schema = sf.newSchema( new File( "./xsd/nfvSchema.xsd" )); 
		        u.setSchema(schema);
		        // unmarshal a document into a tree of Java content objects
		        NFV root = (NFV) u.unmarshal( new FileInputStream( f ) );
				//long endU=System.currentTimeMillis();
		        //System.out.println("Unmarshalling -> " + ((endU-beginAll)/1000) + "s");
				for(int i = 0; i < N_SIMULATIONS; i++){
					System.out.println("Simulation nr " + i);
					test( root, true);
				}
				System.out.println("AVG creating condition -> " + (condTime/N_SIMULATIONS) + "ms");
				System.out.println("AVG checking property -> " + (checkTime/N_SIMULATIONS) + "ms");
				System.out.println("AVG total time -> " + (totTime/N_SIMULATIONS) + "ms");
				System.out.println("=====================================");

			}
		} catch (Exception e) {
			fail(e.toString());
		}
	}
}
