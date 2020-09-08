/**
 * 
 */
package it.polito.verefoo.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;



import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.microsoft.z3.Status;

import it.polito.verefoo.VerefooProxy;
import it.polito.verefoo.VerefooSerializer;
import it.polito.verefoo.extra.BadGraphError;
import it.polito.verefoo.extra.Package1LoggingClass;
import it.polito.verefoo.extra.TestCaseGeneratorAmsterdam;
import it.polito.verefoo.extra.TestCaseGeneratorVerigraph;
import it.polito.verefoo.jaxb.FunctionalTypes;
import it.polito.verefoo.jaxb.Graph;
import it.polito.verefoo.jaxb.Host;
import it.polito.verefoo.jaxb.NFV;
import it.polito.verefoo.jaxb.Path;
import it.polito.verefoo.jaxb.Property;
import it.polito.verefoo.jaxb.TypeOfHost;
import it.polito.verefoo.translator.Translator;
import it.polito.verefoo.utils.VerificationResult;
/**
 * 
 * This class runs some tests to collect some data about the performance of the tool 
 *
 */
public class TestPerformanceScalabilityVerigraph {
	

	//seed , numberAP, numberPR, runs
	public static void main(String[] args)  {
		//System.out.println(args.length);
		//if(args.length!=4) return;
		// 10,100,1000
		// isolation with 1000 rules 
		chainsize=108;
        numberAP  = 0;
        numberPR  = 1000;
        runs = 100;
        
        int[] sizes = {48, 48, 47, 48, 47, 49, 48, 48, 48, 48};
        int[] percDPIs =  {20,17,15,12,11,8,5,3,1};
        int[] percWafs =  {20,18,15,13,11,8,5,3,1};
        int[] percPfs =    {50,45,40,35,28,24,20,14,8};
        int[] percForws = {10,20,30,40,50,60,70,80,90};


		for (int k = 8; k >= 0; k--) {
			seed=1967;
			chainsize= sizes[k];
			percDPI = percDPIs[k];
			percWaf = percWafs[k];
			percPf = percPfs[k];
			percForw = percForws[k];
			totTime=0;
    		totTimeChecker=0;
			testScalabilityPerformance();
		}
		
        
        
	}
	
	/* Variables to set if you want to automatically create the NFV */
	private static int runs;
	static String prefix = new String("Isol");
	String IPClient[] = new String[runs];
	String IPAllocationPlace[] = new String[runs];
	String IPServer[] = new String[runs];
	static int seed = 1967;
	static Random rand;
	
	private static long condTime = 0;
	private static long checkTimeSAT = 0;
	private static long checkTimeUNSAT = 0;
	private static long totTime = 0;
	private static long maxCondTime = 0, maxCheckTimeSAT = 0, maxCheckTimeUNSAT = 0, maxTotTime = 0,minTotTime = 0;
	private  static int nSAT = 0, nUNSAT = 0, i = 0, err = 0, nrOfConditions = 0, maxNrOfConditions = 0;
	static NFV root;
	static String pathfile;
	private static ch.qos.logback.classic.Logger logger;
	private Logger loggerModel = LogManager.getLogger("model");
	private int newSeed;
	private static int numberAP;
	private static int numberPR;
	private static int chainsize;
	

	private static int percDPI;
	private static int percWaf;
	private static int percPf;
	private static int percForw;
	
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
	
	private static int totTimeChecker=0;
	private static NFV testCoarse(NFV root) throws Exception{
		long beginAll=System.currentTimeMillis();
		VerefooSerializer test = new VerefooSerializer(root);
		
		long endAll=System.currentTimeMillis();
		 if(test.isSat()){
			nSAT++;
			maxTotTime = maxTotTime<(endAll-beginAll)? (endAll-beginAll) : maxTotTime;
			minTotTime = minTotTime>(endAll-beginAll)? (endAll-beginAll) : minTotTime;
			logger.debug("time: " + (endAll-beginAll) + "ms;");
			totTime += (endAll-beginAll);
			totTimeChecker +=test.getTime(); 
			logger.debug("checker time: " + test.getTime() + "ms;");
		 }
	 	else{
	 		logger.debug("UNSAT");	
	 		totTimeChecker +=test.getTime(); 
			nUNSAT++;
	 	}
		
        return test.getResult();
	}
	

	private void setManuallyIP() {
		//same IP 
		IPClient[0]=  new String("1.1.1.");
		IPAllocationPlace[0] =  new String("2.2.2.");
		IPServer[0]= new String("3.3.3.");
		//different IP
		IPClient[1]=  new String("213.96.47.");
		IPAllocationPlace[1] =  new String("198.65.32.");
		IPServer[1]= new String("26.98.75.");
		//same numbers
		IPClient[2]=  new String("1.1.1.");
		IPAllocationPlace[2] =  new String("11.11.11.");
		IPServer[2]= new String("111.111.111.");
	}
	
	
	@Test
	public static void testScalabilityPerformance(){
		
		    rand= new Random(seed);
		    //pathfile =  "VerigraphRev1Test"+"_"+numberPR+"_"+runs+"_"+seed+"_"+"name.log";
		    pathfile =  "VerigraphRevision1" + chainsize+"_" + "percNoFilt"+ percForw + "_" + numberPR+"_"+runs+"_"+seed+"_"+"name.log";
	        logger =  Package1LoggingClass.createLoggerFor(pathfile, "log/"+pathfile);
		
		   Runtime rt = Runtime.getRuntime();
	        long totalMem = rt.totalMemory();
	        long maxMem = rt.maxMemory();
	        long freeMem = rt.freeMemory();
	        double megs = 1048576.0;

	        System.out.println ("Total Memory: " + totalMem + " (" + (totalMem/megs) + " MiB)");
	        System.out.println ("Max Memory:   " + maxMem + " (" + (maxMem/megs) + " MiB)");
	        System.out.println ("Free Memory:  " + freeMem + " (" + (freeMem/megs) + " MiB)");
		
	
	        int[] seeds = new int[runs];
	        
		  for(int m=0;m<runs;m++) { 
			  seeds[m]=Math.abs(rand.nextInt()); 
			}
		 
	        
	        /* Switch between automatic and manul configuration of the IP*/
		
		//setAutomaticallyIP();
		//setManuallyIP();
		int k=0;
		try {
			List<TestCaseGeneratorVerigraph> nfv = new ArrayList<>();
			
			 nfv.add(new TestCaseGeneratorVerigraph(prefix + numberAP + "AP" + numberPR + "PR", numberPR,  chainsize, 1, percDPI, percWaf, percPf, percForw ));
			
			
			
	
	
			for(TestCaseGeneratorVerigraph f : nfv){
				
				//System.out.println(" size ->  " + (f.getNfv().getGraphs().getGraph().get(0).getNode().size()) );
				//System.exit(0);
				
				condTime = 0;
				checkTimeSAT = 0;
				checkTimeUNSAT = 0;
				totTime = 0;
				maxCondTime = 0;
				maxCheckTimeSAT = 0;
				maxCheckTimeUNSAT = 0;
				maxTotTime = 0;
				minTotTime = Integer.MAX_VALUE;
				nSAT = 0;
				nUNSAT = 0;
				i = 0;
				err = 0;
				logger.info("===========FILE " + f.getName() + "===========");
					
				// create a JAXBContext capable of handling the generated classes
				//long beginAll=System.currentTimeMillis();
		        JAXBContext jc = JAXBContext.newInstance( "it.polito.verefoo.jaxb" );
		        // create an Unmarshaller
		        Unmarshaller u = jc.createUnmarshaller();
		        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI); 
		        Schema schema = sf.newSchema( new File( "./xsd/nfvSchema.xsd" )); 
		        u.setSchema(schema);
		        // unmarshal a document into a tree of Java content objects
		   
		        Marshaller m = jc.createMarshaller();
	            m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
	            m.setProperty( Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION,"./xsd/nfvSchema.xsd");
	            //for debug purpose  
                //m.marshal(f.getNfv(), System.out ); 
		        
		        do{
		        	for(k = 0; k < runs; k++) {
							try {
								
					             m = jc.createMarshaller();
					             m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
					             m.setProperty( Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION,"./xsd/nfvSchema.xsd");

					             //no random
					             //root = f.changeIP(IPClient[k], IPAllocationPlace[k], IPServer[k]);
					             //random
					           
					             root = f.changeIP(seeds[k]);
					             
					           
					             //random
					             logger.debug("Seed:" + seeds[k]);
					             System.out.println("Seed:" + seeds[k]);
					             
					             //for debug purpose 
								 //m.marshal( testCoarse(root), System.out );  
								 i++;
								 NFV resultNFV = testCoarse(root);
								 
								 //m.marshal( resultNFV, System.out );
								 //loggerModel.debug(stringWriter.toString());
							} catch (Exception e) {
								e.printStackTrace();
								err++;
							}
					
		        	}
				}while(i<1);
				
				logger.info("Simulations -> " + k + " / Errors -> " + err);
				//System.out.println("AVG Nr of Conditions -> " + (nrOfConditions/(i)) + " / MAX Nr Of Conditions -> " + maxNrOfConditions);
				//System.out.println("AVG creating condition -> " + (condTime/(i-err)) + "ms");
				//System.out.println("MAX creating condition -> " + (maxCondTime) + "ms");
				//logger.debug("AVG creating condition -> " + (condTime/(i-err)) + "ms");
				//logger.debug("MAX creating condition -> " + (maxCondTime) + "ms");
				if(nSAT > 0) {
					System.out.println("AVG total time -> " + (totTime/nSAT) + "ms");
					logger.info("AVG total time -> " + (totTime/nSAT) + "ms");
					logger.info("MAX total time -> " + (maxTotTime) + "ms");
					logger.info("MIN total time -> " + (minTotTime) + "ms");
				}
				
				//logger.debug("=====================================");
				logger.info(" total time checker -> " + (totTimeChecker) + "ms");
				logger.info(" size ->  " + (f.getNfv().getGraphs().getGraph().get(0).getNode().size()) + " VNFs");
				System.out.println(" size ->  " + (f.getNfv().getGraphs().getGraph().get(0).getNode().size()) + " VNFs");
				System.out.println(" total time checker -> " + (totTimeChecker) + "ms");


			}
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}
}
