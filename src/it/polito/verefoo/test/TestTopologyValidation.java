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
import it.polito.verefoo.extra.TestCaseGeneratorBudapest;
import it.polito.verefoo.jaxb.FunctionalTypes;
import it.polito.verefoo.jaxb.Graph;
import it.polito.verefoo.jaxb.Host;
import it.polito.verefoo.jaxb.NFV;
import it.polito.verefoo.jaxb.PName;
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
public class TestTopologyValidation {

	public static void main(String[] args)  {

		String path = "./testfile/NetworkTopology/Internet2.xml";
		//seed = 95485;
		//seed = 1413;
		seed = 6789;
		Random random = new Random(seed);
		String prefix = "1.0.0.";
		int min = 1;
		int max = 18;
		
		int numberRequirements = 50;
		
		try {
			JAXBContext jc;
			jc = JAXBContext.newInstance("it.polito.verefoo.jaxb");
			Unmarshaller u = jc.createUnmarshaller();
			SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = sf.newSchema(new File("./xsd/nfvSchema.xsd"));
			u.setSchema(schema);

			try {
				Marshaller m = jc.createMarshaller();
				m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
				m.setProperty(Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION, "./xsd/nfvSchema.xsd");
				NFV testCase = (NFV) u.unmarshal(new FileInputStream(path));
				testCase.getPropertyDefinition().getProperty().clear();
				
				int i = 0;
				while(i < numberRequirements) {
					
					int first = random.nextInt(max - min + 1) + min;
					int second = random.nextInt(max - min + 1) + min;
					while(second == first) {
						second = random.nextInt(max - min + 1) + min;
					}
					String srcIP = new String(prefix + first);
					String dstIP = new String(prefix + second);
					
					boolean toAdd = true;
					for(Property p : testCase.getPropertyDefinition().getProperty()) {
						if(p.getSrc().equals(srcIP) && p.getDst().equals(dstIP)) {
							toAdd = false;
						}
					}
					
					if(toAdd) {
						PName type = (i < numberRequirements-20) ? PName.ISOLATION_PROPERTY : PName.REACHABILITY_PROPERTY;
						Property property = new Property();
						property.setName(type);
						property.setGraph((long) 0);
						property.setSrc(srcIP);
						property.setDst(dstIP);
						testCase.getPropertyDefinition().getProperty().add(property);
						i++;
					}
					
					
				}
				
				
					testCoarse(testCase);

				
				
				
			} catch (BadGraphError | FileNotFoundException e) {
				logger.info("Graph semantically incorrect");
				System.exit(1);
			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (JAXBException je) {
			logger.info("Error while unmarshalling or marshalling");
			System.exit(1);
		} catch (ClassCastException cce) {
			logger.info("Wrong data type found in XML document");
			System.exit(1);
		} catch (BadGraphError e) {
			logger.info("Graph semantically incorrect");
			System.exit(1);
		} catch (SAXException e) {
			System.exit(1);
		}
	
	
		
		
		

        
	}
	
	/* Variables to set if you want to automatically create the NFV */
	private static int runs;
	static String prefix = new String("Isol");
	String IPClient[] = new String[runs];
	String IPAllocationPlace[] = new String[runs];
	String IPServer[] = new String[runs];
	static int seed;
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
	private static int numberIPR;
	private static int numberRPR;
	private static int numberPR;
	private static int numberNAT;
	private static int numberLB;
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
		
		JAXBContext jc;
		jc = JAXBContext.newInstance("it.polito.verefoo.jaxb");
		Marshaller m = jc.createMarshaller();
        m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
        m.setProperty( Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION,"./xsd/nfvSchema.xsd");
        
       m.marshal( test.getNfv(), System.out );
		
		long endAll=System.currentTimeMillis();
		 if(test.isSat()){
			nSAT++;
			maxTotTime = maxTotTime<(endAll-beginAll)? (endAll-beginAll) : maxTotTime;
			minTotTime = minTotTime>(endAll-beginAll)? (endAll-beginAll) : minTotTime;
			System.out.println("time: " + (endAll-beginAll) + "ms;");
			totTime += (endAll-beginAll);
		 }
	 	else{
	 		//logger.debug("UNSAT");	

	 	}
		
        return test.getResult();
	}
	

	
}
