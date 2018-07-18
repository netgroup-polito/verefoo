package it.polito.verifoo.rest.main;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;
import it.polito.verifoo.rest.jaxb.*;
import it.polito.verifoo.random.RandomInputGenerator;
import it.polito.verifoo.rest.common.*;
/**
 * 
 * This is the main class only for testing the VerifooProxy
 *
 */
public class Main {
	public static void main(String[] args) throws MalformedURLException{
		System.setProperty("log4j.configuration", new File("resources", "log4j2.xml").toURI().toURL().toString());
        Logger logger = LogManager.getLogger("mylog");
		try {
			
				
				JAXBContext jc;
	                // create a JAXBContext capable of handling the generated classes
				synchronized(logger){
					jc= JAXBContext.newInstance( "it.polito.verifoo.rest.jaxb" );
				}
                // create an Unmarshaller
                Unmarshaller u = jc.createUnmarshaller();
                SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI); 
                Schema schema = sf.newSchema( new File( "./xsd/nfvSchema.xsd" )); 
                u.setSchema(schema);
                RandomInputGenerator r = null;
                boolean exit = false;
                int sat = 0; 
                long beginAll=System.currentTimeMillis();
                while(!exit){
                	try{
                		Marshaller m = jc.createMarshaller();
                        m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
                        m.setProperty( Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION,"./xsd/nfvSchema.xsd");
                		/*int maxClients = 1, maxServers = 1, maxInternalNodes = 10, maxProperty = 1, maxHosts = 20;
                    	r = new RandomInputGenerator(maxClients, maxServers, maxInternalNodes, maxProperty, maxHosts);
                        NFV root = r.getRandomInput();
                        OutputStream out = new FileOutputStream("./testfile/Random/current.xml");
                     // create a Marshaller and marshal to output
                         m.marshal( root, out ); 
                        
                        // unmarshal a document into a tree of Java content objects*/
                        //NFV root = (NFV) u.unmarshal( new FileInputStream(   "./testfile/FirmatoFW/Pre-Processing.xml"  ) );
                        
                        //root = (NFV) u.unmarshal( new FileInputStream( "./testfile/Random/current.xml" ) );
                        //NFV root = (NFV) u.unmarshal( new FileInputStream( "./testfile/Random/bug1.xml" ) );
                        VerifooSerializer test = new VerifooSerializer((NFV) u.unmarshal( new FileInputStream(   "./testfile/Performance/Refinement/refNoTopology-2FW4P.xml"   )));
                        m = jc.createMarshaller();
                        m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
                        m.setProperty( Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION,"./xsd/nfvSchema.xsd");
                        if(test.isSat()){
                        		System.out.println("SAT");
                        		sat++;
                        		if(sat > 0)
                        			exit = true;
                        		/*System.out.println("----------------------OUTPUT----------------------");
                                m.marshal( test.getResult(), System.out ); 
                        		System.out.println("--------------------------------------------------");*/
                    	}
                    	else{
                    		System.out.println("UNSAT");
                    		/*System.out.println("----------------------OUTPUT----------------------");
                            m.marshal( test.getResult(), System.out ); 
                    		System.out.println("--------------------------------------------------");*/
                    		if(r == null) exit = true;
                    	}
                        //MedicineSimulator sim = new MedicineSimulator(root);
                        //sim.printAll();
                        //m.marshal( sim.getPhysicalTopology(), System.out );
                        //sim.stopSimulation();*/
                    } catch (BadGraphError | FileNotFoundException e) {
            			//logger.error("Graph semantically incorrect");
            			//System.out.println("Graph semantically incorrect");
                    	logger.error(e);
                    	if(r == null) exit = true;
                    }
            		long endAll=System.currentTimeMillis();
            		System.out.print("time: " + (endAll-beginAll) + "ms;");
                }
            } catch( JAXBException je ) {
            	logger.error("Error while unmarshalling or marshalling");
                logger.error(je);
                System.exit(1);
            } catch( ClassCastException cce) {
            	logger.error("Wrong data type found in XML document");
            	logger.error(cce);
                System.exit(1);
            } catch (BadGraphError e) {
    			logger.error("Graph semantically incorrect");
            	logger.error(e);
                System.exit(1);
    		} catch (SAXException e) {
    			logger.error(e);
    			System.exit(1);
    		}
	}

}
