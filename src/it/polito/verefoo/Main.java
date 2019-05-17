package it.polito.verefoo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.StringWriter;
import java.net.MalformedURLException;
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
import org.xml.sax.SAXException;

import it.polito.verefoo.*;
import it.polito.verefoo.extra.BadGraphError;
import it.polito.verefoo.jaxb.*;

/**
 * This is the main class only for testing the Verifoo execution
 *
 */
public class Main {
	public static void main(String[] args) throws MalformedURLException{
		System.setProperty("log4j.configuration", new File("resources", "log4j2.xml").toURI().toURL().toString());
        Logger loggerInfo = LogManager.getLogger(Main.class);
        Logger loggerResult = LogManager.getLogger("result");
		try {
				JAXBContext jc;
	            // create a JAXBContext capable of handling the generated classes
				jc= JAXBContext.newInstance( "it.polito.verefoo.jaxb" );
                // create an Unmarshaller
                Unmarshaller u = jc.createUnmarshaller();
                SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI); 
                Schema schema = sf.newSchema( new File( "./xsd/nfvSchema.xsd" )); 
                u.setSchema(schema);
                
                boolean exit = false;
                int sat = 0; 
                
                long beginAll = System.currentTimeMillis();
                while(!exit){
                	try{
                		Marshaller m = jc.createMarshaller();
                        m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
                        m.setProperty( Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION,"./xsd/nfvSchema.xsd");
                        VerefooSerializer test = new VerefooSerializer((NFV) u.unmarshal( new FileInputStream( "./testfile/FWCorrectness/FWCorrect01.xml" )));
                        m = jc.createMarshaller();
                        m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
                        m.setProperty( Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION,"./xsd/nfvSchema.xsd");
                        if(test.isSat()){
                        		loggerResult.info("SAT");
                        		sat++;
                        		if(sat > 0) {
									exit = true;
								}
                        		loggerResult.info("----------------------OUTPUT----------------------");
                        		StringWriter stringWriter = new StringWriter();
                        		m.marshal( test.getResult(), stringWriter ); 
                                loggerResult.info(stringWriter.toString());
                                loggerResult.info("--------------------------------------------------");
                    	}
                    	else{
                    		loggerResult.info("UNSAT");
                    		loggerResult.info("----------------------OUTPUT----------------------");
                    		StringWriter stringWriter = new StringWriter();
                            m.marshal( test.getResult(), stringWriter ); 
                            loggerResult.info(stringWriter.toString());
                            loggerResult.info("--------------------------------------------------");
                    		System.exit(1);
                    	}
                        //MedicineSimulator sim = new MedicineSimulator(root);
                        //sim.printAll();
                        //m.marshal( sim.getPhysicalTopology(), System.out );
                        //sim.stopSimulation();*/
                    } catch (BadGraphError | FileNotFoundException e) {
            			loggerInfo.error("Graph semantically incorrect");
                    	loggerInfo.error(e);
                    	System.exit(1);
                    }
            		long endAll=System.currentTimeMillis();
            		loggerResult.info("time: " + (endAll-beginAll) + "ms;");
                }
            } catch( JAXBException je ) {
            	loggerInfo.error("Error while unmarshalling or marshalling");
            	loggerInfo.error(je);
                System.exit(1);
            } catch( ClassCastException cce) {
            	loggerInfo.error("Wrong data type found in XML document");
            	loggerInfo.error(cce);
                System.exit(1);
            } catch (BadGraphError e) {
            	loggerInfo.error("Graph semantically incorrect");
    			loggerInfo.error(e);
                System.exit(1);
    		} catch (SAXException e) {
    			loggerInfo.error(e);
    			System.exit(1);
    		}
	}

}
