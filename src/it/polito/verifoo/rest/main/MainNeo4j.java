package it.polito.verifoo.rest.main;

import java.io.File;
import java.io.FileInputStream;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.neo4j.driver.v1.*;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;

import static org.neo4j.driver.v1.Values.parameters;
import org.xml.sax.SAXException;

import it.polito.verifoo.rest.common.BadGraphError;
import it.polito.verifoo.rest.common.VerifooSerializer;
import it.polito.verifoo.rest.jaxb.NFV;
import it.polito.verifoo.rest.neo4j.Neo4jClient;
/**
 * 
 * This is the main class only for testing the the neo4j interaction
 *
 */
public class MainNeo4j{
    
	public static void main(String[] args) throws Exception {
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
                    // unmarshal a document into a tree of Java content objects*/
            NFV root = (NFV) u.unmarshal( new FileInputStream(   "./testfile/ServiceGraphs/sg2clients4nodes2servers3hostSAT_AtoB-FW.xml" ) );
            
            VerifooSerializer test = new VerifooSerializer(root);
            Neo4jClient client = new Neo4jClient("bolt://127.0.0.1:7687", "neo4j", "password");
            client.storeGraph(root);
            client.close();       
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
