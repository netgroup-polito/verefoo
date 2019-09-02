package it.polito.verefoo.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.StringWriter;
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
import it.polito.verefoo.extra.TestCaseGenerator;
import it.polito.verefoo.jaxb.FunctionalTypes;
import it.polito.verefoo.jaxb.Graph;
import it.polito.verefoo.jaxb.Host;
import it.polito.verefoo.jaxb.NFV;
import it.polito.verefoo.jaxb.Path;
import it.polito.verefoo.jaxb.Property;
import it.polito.verefoo.jaxb.TypeOfHost;
import it.polito.verefoo.translator.Translator;
import it.polito.verigraph.extra.VerificationResult;
/**
 * 
 * This class runs some tests to collect some data about the performance of the tool 
 *
 */
public class TestPerformanceScalability {
	

	/* Variables to set if you want to automatically create the NFV */
	private static final int N = 100;
	String prefix = new String("Isol");
	String IPClient[] = new String[N];
	String IPAllocationPlace[] = new String[N];
	String IPServer[] = new String[N];
	int seed = 1967;
	Random rand = new Random(seed);
	
	private long condTime = 0, checkTimeSAT = 0, checkTimeUNSAT = 0, totTime = 0;
	private long maxCondTime = 0, maxCheckTimeSAT = 0, maxCheckTimeUNSAT = 0, maxTotTime = 0,minTotTime = 0;
	private int nSAT = 0, nUNSAT = 0, i = 0, err = 0, nrOfConditions = 0, maxNrOfConditions = 0;
	NFV root;
	String pathfile = "name.log" ;
	private ch.qos.logback.classic.Logger logger = Package1LoggingClass.createLoggerFor(pathfile, "log/name.log");
	private Logger loggerModel = LogManager.getLogger("model");
	private int newSeed;
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
	

	
	private NFV testCoarse(NFV root) throws Exception{
		long beginAll=System.currentTimeMillis();
		VerefooSerializer test = new VerefooSerializer(root);
		
		long endAll=System.currentTimeMillis();
		 if(test.isSat()){
			nSAT++;
			maxTotTime = maxTotTime<(endAll-beginAll)? (endAll-beginAll) : maxTotTime;
			minTotTime = minTotTime>(endAll-beginAll)? (endAll-beginAll) : minTotTime;
			logger.debug("time: " + (endAll-beginAll) + "ms;");
			totTime += (endAll-beginAll);
		 }
	 	else{
	 		logger.debug("UNSAT");	
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
	
	private void setAutomaticallyIP() {
		int first, second, third;
		for(int i = 0; i < N; i++) {
			first = rand.nextInt(256);
			if(first == 0) first++;
			second = rand.nextInt(256);
			third = rand.nextInt(256);
			IPClient[i] = new String(first + "." + second + "." + third + ".");
			if(rand.nextBoolean()) IPClient[i] = new String(first + "." + first + "." + first + ".");
			
			first = rand.nextInt(256);
			if(first == 0) first++;
			second = rand.nextInt(256);
			third = rand.nextInt(256);
			IPAllocationPlace[i] = new String(first + "." + second + "." + third + ".");
			if(rand.nextBoolean()) IPAllocationPlace[i] = new String(first + "." + first + "." + first + ".");
			
			first = rand.nextInt(256);
			if(first == 0) first++;
			second = rand.nextInt(256);
			third = rand.nextInt(256);
			IPServer[i] = new String(first + "." + second + "." + third + ".");
			if(rand.nextBoolean()) IPServer[i] = new String(first + "." + first + "." + first + ".");
		}
	}
	
	@Test
	public void testScalabilityPerformance(){
		
		   Runtime rt = Runtime.getRuntime();
	        long totalMem = rt.totalMemory();
	        long maxMem = rt.maxMemory();
	        long freeMem = rt.freeMemory();
	        double megs = 1048576.0;

	        System.out.println ("Total Memory: " + totalMem + " (" + (totalMem/megs) + " MiB)");
	        System.out.println ("Max Memory:   " + maxMem + " (" + (maxMem/megs) + " MiB)");
	        System.out.println ("Free Memory:  " + freeMem + " (" + (freeMem/megs) + " MiB)");
		
	
	        int[] seeds = {3263771	,
	        		16732555	,
	        		30691880	,
	        		82099074	,
	        		89512933	,
	        		125373162	,
	        		159691330	,
	        		164757118	,
	        		165306755	,
	        		167451038	,
	        		185698089	,
	        		189129162	,
	        		201382884	,
	        		204894282	,
	        		232245300	,
	        		283987185	,
	        		307047482	,
	        		316109782	,
	        		341073549	,
	        		352433015	,
	        		382144212	,
	        		427509305	,
	        		440400889	,
	        		482067466	,
	        		483226890	,
	        		490207606	,
	        		555910719	,
	        		613476386	,
	        		661038538	,
	        		661406783	,
	        		726097708	,
	        		749249898	,
	        		754831166	,
	        		779573373	,
	        		829252279	,
	        		835317888	,
	        		840806581	,
	        		896526641	,
	        		899966616	,
	        		918207226	,
	        		973218858	,
	        		990365427	,
	        		1016956473	,
	        		1018082824	,
	        		1023704156	,
	        		1112299723	,
	        		1154993508	,
	        		1160617290	,
	        		1163913733	,
	        		1227773091	};
		/*
		 * for(int m=0;m<N;m++) { seeds[m]=Math.abs(rand.nextInt()); }
		 */
	        
	        /* Switch between automatic and manul configuration of the IP*/
		
		//setAutomaticallyIP();
		//setManuallyIP();
		int k=0;
		try {
			List<TestCaseGenerator> nfv = new ArrayList<>();

			/* Scalability test for Allocation Places */
				//nfv.add(new TestCaseGenerator(prefix + 50 + "AP" + 10 + "PR", 50, 0, 10, seed));
				//nfv.add(new TestCaseGenerator(prefix + 50 + "AP" + 15 + "PR", 50, 0, 15, seed));
			
		
			 
			
			 for(int j = 15; j <= 15; j += 5) { //allocation places 
				 for(int i = 10; i <=80; i += 10) //policies //put 0,j for isolation, whereas j,0 for reachability
					 //no random 
					// nfv.add(new ScalabilityTestCase(prefix + i + "AP" + j + "PR",i, 0, j, IPClient[k], IPAllocationPlace[k], IPServer[k])); //random
			  nfv.add(new TestCaseGenerator(prefix + i + "AP" + j + "PR", i, 0, j, seed));
			  }
			
			
			
			
			/*Scalability test for Policy Rules */
			
			/*	for(int j = 10; j <= 80; j += 10) { //policies
					for(int i = 5; i <= 15; i += 5) //allocation places
						//put 0,j for isolation, whereas j,0 for reachability
							//no random
							nfv.add(new ScalabilityTestCase(prefix + i + "AP" + j + "PR", i, 0, j, IPClient[k], IPAllocationPlace[k], IPServer[k]));
							//random
							nfv.add(new ScalabilityTestCase(prefix + i + "AP" + j + "PR", i, 0, j, seed));
					}
			*/
	
	
			for(TestCaseGenerator f : nfv){
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
		        	for(k = 0; k < N; k++) {
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
								 StringWriter stringWriter = new StringWriter();
								 m.marshal( resultNFV, stringWriter );
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


			}
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}
}
