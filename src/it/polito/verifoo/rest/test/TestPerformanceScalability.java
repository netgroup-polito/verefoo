/**
 * 
 */
package it.polito.verifoo.rest.test;

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

import it.polito.verifoo.rest.common.BadGraphError;
import it.polito.verifoo.rest.common.Translator;
import it.polito.verifoo.rest.common.VerifooProxy;
import it.polito.verifoo.rest.common.VerifooSerializer;
import it.polito.verifoo.rest.jaxb.FunctionalTypes;
import it.polito.verifoo.rest.jaxb.Graph;
import it.polito.verifoo.rest.jaxb.Host;
import it.polito.verifoo.rest.jaxb.NFV;
import it.polito.verifoo.rest.jaxb.Path;
import it.polito.verifoo.rest.jaxb.Property;
import it.polito.verifoo.rest.jaxb.TypeOfHost;
import it.polito.verifoo.rest.main.ScalabilityTestCase;
import it.polito.verigraph.mcnet.components.IsolationResult;
/**
 * 
 * This class runs some tests to collect some data about the performance of the tool 
 *
 */
public class TestPerformanceScalability {
	

	/* Variables to set if you want to automatically create the NFV */
	private static final int N = 3;
	String prefix = new String("Isol");
	String IPClient[] = new String[N];
	String IPAllocationPlace[] = new String[N];
	String IPServer[] = new String[N];
	
	private long condTime = 0, checkTimeSAT = 0, checkTimeUNSAT = 0, totTime = 0;
	private long maxCondTime = 0, maxCheckTimeSAT = 0, maxCheckTimeUNSAT = 0, maxTotTime = 0;
	private int nSAT = 0, nUNSAT = 0, i = 0, err = 0, nrOfConditions = 0, maxNrOfConditions = 0;
	NFV root;
	private Logger logger = LogManager.getLogger("result");
	private Logger loggerModel = LogManager.getLogger("model");
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
		VerifooSerializer test = new VerifooSerializer(root);
		
		long endAll=System.currentTimeMillis();
		 if(test.isSat()){
			nSAT++;
			maxTotTime = maxTotTime<(endAll-beginAll)? (endAll-beginAll) : maxTotTime;
			logger.info("time: " + (endAll-beginAll) + "ms;");
			totTime += (endAll-beginAll);
		 }
	 	else{
	 		logger.info("UNSAT");	
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
//		Random rand = new Random(System.currentTimeMillis());
		Random rand = new Random(13423420);
		int first, second, third;
		for(int i = 0; i < N; i++) {
			first = rand.nextInt(256);
			if(first == 0) first++;
			second = rand.nextInt(256);
			third = rand.nextInt(256);
			IPClient[i] = new String(first + "." + second + "." + third + ".");
			
			first = rand.nextInt(256);
			if(first == 0) first++;
			second = rand.nextInt(256);
			third = rand.nextInt(256);
			IPAllocationPlace[i] = new String(first + "." + second + "." + third + ".");
			
			first = rand.nextInt(256);
			if(first == 0) first++;
			second = rand.nextInt(256);
			third = rand.nextInt(256);
			IPServer[i] = new String(first + "." + second + "." + third + ".");
		}
	}
	
	@Test
	public void testScalabilityPerformance(){
		
		/* Switch between automatic and manul configuration of the IP*/
		
		setAutomaticallyIP();
		//setManuallyIP();
		
		try {
			List<ScalabilityTestCase> nfv = new ArrayList<>();
			
			/* Scalability test for Allocation Places */
			for(int k = 0; k < N; k++) {
				for(int i = 10; i <= 80; i += 10) { //allocation places
					for(int j = 5; j <= 15; j += 5) //policies
						//put 0,j for isolation, whereas j,0 for reachability
						nfv.add(new ScalabilityTestCase(prefix + i + "AP" + j + "PR", i, 0, j, IPClient[k], IPAllocationPlace[k], IPServer[k]));
				}
			}
			
			
			/*Scalability test for Policy Rules */
			for(int k = 0; k < N; k++) {
				for(int j = 10; j <= 80; j += 10) { //policies
					for(int i = 5; i <= 15; i += 5) //allocation places
						//put 0,j for isolation, whereas j,0 for reachability
						nfv.add(new ScalabilityTestCase(prefix + i + "AP" + j + "PR", i, 0, j, IPClient[k], IPAllocationPlace[k], IPServer[k]));
				}
			}
	
			
			for(ScalabilityTestCase f : nfv){
				condTime = 0;
				checkTimeSAT = 0;
				checkTimeUNSAT = 0;
				totTime = 0;
				maxCondTime = 0;
				maxCheckTimeSAT = 0;
				maxCheckTimeUNSAT = 0;
				maxTotTime = 0;
				nSAT = 0;
				nUNSAT = 0;
				i = 0;
				err = 0;
				logger.debug("===========FILE " + f.getName() + "===========");
				logger.debug("Client: "+ f.getIPC() +" AllocationPlace: "+ f.getIPAP() + " IPServer: "+ f.getIPS());	
				// create a JAXBContext capable of handling the generated classes
				//long beginAll=System.currentTimeMillis();
		        JAXBContext jc = JAXBContext.newInstance( "it.polito.verifoo.rest.jaxb" );
		        // create an Unmarshaller
		        Unmarshaller u = jc.createUnmarshaller();
		        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI); 
		        Schema schema = sf.newSchema( new File( "./xsd/nfvSchema.xsd" )); 
		        u.setSchema(schema);
		        // unmarshal a document into a tree of Java content objects
		        root = f.getNfv();
		   
		        Marshaller m = jc.createMarshaller();
	            m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
	            m.setProperty( Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION,"./xsd/nfvSchema.xsd");
	            //for debug purpose  
                //m.marshal(f.getNfv(), System.out ); 
		        
		        do{
		        	
							try {
					             m = jc.createMarshaller();
					             m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
					             m.setProperty( Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION,"./xsd/nfvSchema.xsd");
								 root = f.getNfv();
								 //for debug purpose 
								 //m.marshal( testCoarse(root), System.out );  
								 i++;
								 NFV resultNFV = testCoarse(root);
								 StringWriter stringWriter = new StringWriter();
								 m.marshal( resultNFV, stringWriter );
								 loggerModel.debug(stringWriter.toString());
							} catch (Exception e) {
								e.printStackTrace();
								err++;
							}
					

				}while(i<5);
				
				logger.debug("Simulations -> " + i + " / Errors -> " + err);
				//System.out.println("AVG Nr of Conditions -> " + (nrOfConditions/(i)) + " / MAX Nr Of Conditions -> " + maxNrOfConditions);
				//System.out.println("AVG creating condition -> " + (condTime/(i-err)) + "ms");
				//System.out.println("MAX creating condition -> " + (maxCondTime) + "ms");
				//logger.debug("AVG creating condition -> " + (condTime/(i-err)) + "ms");
				//logger.debug("MAX creating condition -> " + (maxCondTime) + "ms");
				if(nSAT > 0) {
					logger.debug("AVG total time -> " + (totTime/nSAT) + "ms");
					logger.debug("MAX total time -> " + (maxTotTime) + "ms");
				}
				
				logger.debug("=====================================");


			}
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.toString());
		}
	}
}
