/**
 * 
 */
package it.polito.verefoo.test;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;



import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import it.polito.verefoo.VerefooSerializer;
import it.polito.verefoo.extra.Package1LoggingClass;
import it.polito.verefoo.extra.TestCaseGeneratorAtomicPredicatesVerigraph;
import it.polito.verefoo.jaxb.NFV;
import it.polito.verefoo.utils.TestResults;

/* Run some instances of TestCaseGeneratorAtomicPredicates. TestCaseGeneratorAtomicPredicates generates XML files for NFV, then this
 * class takes in input those files, for each of them runs Verefoo and print results and other statistics (time to complete, memory usage etc).
 * */
public class TestPerformanceScalabilityAtomicPredicatesVerigraph {
	
	public static void main(String[] args)  {	
		numberPR = 10;
		numberWC = 20;
		numberWS = 20;
		numberAP  = 20;
		numberNAT = 5;
		numberFW = 5;
		maxNATSrcs = 10;
		maxFWRules = 10;
		runs = 10;
		percReqWithPorts = 0.0; //from 0.0 to 1.0
		
		seed  = 66361;
		numberIPR  = numberPR/2;
		numberRPR = numberPR/2;
		numberPR = numberIPR + numberRPR;
		
		testScalabilityPerformance();
				
		System.out.println("TEST TERMINATI");
	}
	
	/* Variables to set if you want to automatically create the NFV */
	private static int runs;
	static String prefix = new String("Isol");
	String IPClient[] = new String[runs];
	String IPAllocationPlace[] = new String[runs];
	String IPServer[] = new String[runs];
	static int seed;
	static Random rand;

	static NFV root;
	static String pathfile;
	private static ch.qos.logback.classic.Logger logger;
	private static int numberAP; //number allocation places
	private static int numberWC; //number web clients
	private static int numberWS; //number web servers
	private static int numberIPR; //isolation property
	private static int numberRPR; //reachability property
	private static int numberPR;  //total number of requirements
	private static int numberNAT; //number of NATs
	private static int numberFW; //number of Firewalls
	private static int maxNATSrcs;
	private static int maxFWRules;
	private static double percReqWithPorts;
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

	private static NFV testCoarse(NFV root) throws Exception{
		long beginAll=System.currentTimeMillis();
		VerefooSerializer test = new VerefooSerializer(root);
		long endAll=System.currentTimeMillis();
		TestResults results = test.getTestTimeResults();
		
		long totalTime = endAll - beginAll;
		long atomicPredCompTime = results.getAtomicPredCompTime();
		long atomicFlowsCompTime = results.getAtomicFlowsCompTime();
		long maxSMTtime = endAll - results.getBeginMaxSMTTime();
		long numberOfFlows = results.getTotalFlows();
		
		String resString = new String("Total time " + totalTime +  "ms, atomicPredCompTime " 
				+ atomicPredCompTime +  "ms, atomicFlowsCompTime " 
				+ atomicFlowsCompTime + "ms, maxSMT time " + maxSMTtime + "ms "
				+ numberOfFlows + "flows;");
		
		System.out.println(resString);
		logger.info(totalTime + "\t" + atomicPredCompTime + "\t" + atomicFlowsCompTime + "\t" + maxSMTtime + "\t" + numberOfFlows + "\t" + results.getZ3Result() + "\t");
        return test.getResult();
	}
	
	
	@Test
	public static void testScalabilityPerformance(){
		    rand= new Random(seed);
		    pathfile = "NR"+numberPR+"WC"+numberWC+"WS"+numberWS+"AP"+numberAP+"NAT"+numberNAT+"FW"+numberFW+"NATS"
	        		+maxNATSrcs+"FWR"+maxFWRules+"PRP"+percReqWithPorts+"APLogs.log";
	        logger =  Package1LoggingClass.createLoggerFor(pathfile, "log/"+pathfile);

	        int[] seeds = new int[runs];
	        for(int m=0;m<runs;m++) { 
	        	seeds[m]=Math.abs(rand.nextInt()); 
	        }

	        /* Switch between automatic and manul configuration of the IP*/
	        int k=0, i=0;
	        try {
	        	List<TestCaseGeneratorAtomicPredicatesVerigraph> nfv = new ArrayList<>();
	        	nfv.add(new TestCaseGeneratorAtomicPredicatesVerigraph("Test case generator atomic predicates", numberAP, numberWC, numberWS, 
	        			numberRPR, numberIPR, numberNAT, numberFW, maxNATSrcs, maxFWRules, percReqWithPorts, 1));

	        	for(TestCaseGeneratorAtomicPredicatesVerigraph f : nfv){

	        		// create a JAXBContext capable of handling the generated classes
	        		//long beginAll=System.currentTimeMillis();
	        		JAXBContext jc = JAXBContext.newInstance( "it.polito.verefoo.jaxb" );
	        		// create an Unmarshaller
	        		Unmarshaller u = jc.createUnmarshaller();
	        		SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI); 
	        		Schema schema = sf.newSchema( new File( "./xsd/nfvSchema.xsd" )); 
	        		u.setSchema(schema);
	        		//unmarshal a document into a tree of Java content objects
	        		Marshaller m = jc.createMarshaller();
	        		m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
	        		m.setProperty( Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION,"./xsd/nfvSchema.xsd");
	        		//for debug purpose  
	        		//m.marshal(f.getNfv(), System.out ); 

	        		do{
	        			for(k = 0; k < runs; k++) {
	        				try {
	        					if(seeds[k] == 1820037872 && numberAP == 80 && numberPR == 50) continue;
	        					m = jc.createMarshaller();
	        					m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
	        					m.setProperty( Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION,"./xsd/nfvSchema.xsd");

	        					root = f.changeIP(numberAP, numberWC, numberWS, numberRPR, numberIPR, numberNAT, numberFW,
	        							maxNATSrcs, maxFWRules, percReqWithPorts, seeds[k]);

	        					//for debug purpose 
	        					//m.marshal( root, System.out );  
	        					i++;
	        					NFV resultNFV = testCoarse(root);
	        					// StringWriter stringWriter = new StringWriter();
	        					//m.marshal( resultNFV, System.out );
	        					//loggerModel.debug(stringWriter.toString());
	        				} catch (Exception e) {
	        					e.printStackTrace();
	        				}

	        			}
	        		}while(i<1);

	        	}
	        } catch (Exception e) {
	        	e.printStackTrace();
	        	fail(e.toString());
	        }
	}
}
