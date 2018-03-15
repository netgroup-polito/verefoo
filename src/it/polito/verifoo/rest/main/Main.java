package it.polito.verifoo.rest.main;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.List;
import java.util.stream.Collectors;

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
import com.microsoft.z3.Status;
import it.polito.verifoo.rest.jaxb.*;
import it.polito.verifoo.rest.medicine.MedicineSimulator;
import it.polito.verigraph.mcnet.components.IsolationResult;
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
                while(!exit){
                	try{
                		//System.out.println("New execution");
                    	r = new RandomInputGenerator(2, 2, 10, 1, 20);
                        NFV root = r.getRandomInput();
                        OutputStream out = new FileOutputStream("./testfile/Random/current.xml");
                     // create a Marshaller and marshal to output
                        Marshaller m = jc.createMarshaller();
                        m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
                        m.setProperty( Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION,"./xsd/nfvSchema.xsd");
                        m.marshal( root, out ); 
                        // unmarshal a document into a tree of Java content objects
                       
                        //NFV root = (NFV) u.unmarshal( new FileInputStream( "./testfile/ServiceGraphs/sg4nodes5host.xml" ) );/*
                        for(Graph g:root.getGraphs().getGraph()){
                        	System.out.println("Creating conditions");
                        	VerifooProxy test = new VerifooProxy(g, root.getHosts(), root.getConnections(),root.getConstraints());
                        	List<Property> prop = root.getPropertyDefinition().getProperty().stream().filter(p -> p.getGraph()==g.getId()).collect(Collectors.toList());
                        	System.out.println("Checking Property");
                        	IsolationResult res=test.checkNFFGProperty(prop);
                        	if(res.result != Status.UNSATISFIABLE){
                        		System.out.println("SAT");
                        		new Translator(res.model.toString(),root).convert();
                        		exit = true;
                        		// create a Marshaller and marshal to output
                                m = jc.createMarshaller();
                                m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
                                m.setProperty( Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION,"./xsd/nfvSchema.xsd");
                                m.marshal( root, System.out ); 
                        	}
                        	else{
                        		System.out.println("UNSAT");
                        	}
                        	root.getPropertyDefinition().getProperty().stream().filter(p->p.getGraph()==g.getId()).forEach(p -> p.setIsSat(res.result!=Status.UNSATISFIABLE)); 
                        }
                        //MedicineSimulator sim = new MedicineSimulator(root);
                        //sim.printAll();
                        //m.marshal( sim.getPhysicalTopology(), System.out );
                        //sim.stopSimulation();*/
                    } catch (BadGraphError | ArrayIndexOutOfBoundsException | FileNotFoundException e) {
            			//logger.error("Graph semantically incorrect");
            			//System.out.println("Graph semantically incorrect");
                    	//logger.error(e);
                    	if(r == null) exit = true;
                    }
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
