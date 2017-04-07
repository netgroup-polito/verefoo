package neo4j;

import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.BadRequestException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.bind.ValidationEventLocator;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import it.polito.neo4j.exceptions.MyInvalidDirectionException;
import it.polito.neo4j.exceptions.MyNotFoundException;
import it.polito.neo4j.jaxb.FunctionalTypes;
import it.polito.neo4j.jaxb.Graph;
import it.polito.neo4j.jaxb.GraphToNeo4j;
import it.polito.neo4j.jaxb.Paths;
import it.polito.neo4j.manager.Neo4jDBManager;
import it.polito.neo4j.manager.Neo4jLibrary;
import it.polito.neo4j.service.Service;
import it.polito.neo4j.jaxb.Graphs;
import it.polito.neo4j.jaxb.ObjectFactory;
import it.polito.verigraph.service.GraphService;
import it.polito.verigraph.model.Neighbour;
import it.polito.verigraph.service.VerificationService;

public class test_old {

	public static void main(String[] args) throws MyNotFoundException, JAXBException, MyInvalidDirectionException {
		// TODO Auto-generated method stub
			

		Neo4jLibrary lib = Neo4jLibrary.getNeo4jLibrary();
		Service service = new Service();
		
		
		String filename = "testcases/test4.xml";
		it.polito.neo4j.jaxb.Graph graph = unmarshalFile(filename);
		Graph graphReturned;
		try{
			graphReturned = lib.createGraph(graph);			
		}
		catch(MyNotFoundException e){
			throw new BadRequestException();
		}
		
	}
	private static Graph unmarshalFile(String filename){
		it.polito.neo4j.jaxb.ObjectFactory objF = new it.polito.neo4j.jaxb.ObjectFactory();
		it.polito.neo4j.jaxb.Graphs graphs = objF.createGraphs();

		try {
            // create a JAXBContext capable of handling classes generated into
            // the primer.po package
            JAXBContext jc = JAXBContext.newInstance( "it.polito.neo4j.jaxb" );
            
            // create an Unmarshaller
            Unmarshaller u = jc.createUnmarshaller();

            SchemaFactory sf = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI);
            try {
                Schema schema = sf.newSchema(new File("schema/xml_components.xsd"));
                u.setSchema(schema);
                u.setEventHandler(
                    new ValidationEventHandler() {
                        // allow unmarshalling to continue even if there are errors
                        public boolean handleEvent(ValidationEvent ve) {
                            // ignore warnings
                            if (ve.getSeverity() != ValidationEvent.WARNING) {
                                ValidationEventLocator vel = ve.getLocator();
                                System.out.println("Line:Col[" + vel.getLineNumber() +
                                    ":" + vel.getColumnNumber() +
                                    "]:" + ve.getMessage());
                            }
                            return true;
                        }
                    }
                );
            } catch (org.xml.sax.SAXException se) {
                System.out.println("Unable to validate due to following error.");
                se.printStackTrace();
            }
            
            graphs = (it.polito.neo4j.jaxb.Graphs) u.unmarshal(new File(filename));
        } catch( UnmarshalException ue ) {
            // The JAXB specification does not mandate how the JAXB provider
            // must behave when attempting to unmarshal invalid XML data.  In
            // those cases, the JAXB provider is allowed to terminate the 
            // call to unmarshal with an UnmarshalException.
            System.out.println( "Caught UnmarshalException" );
        } catch( JAXBException je ) {
            je.printStackTrace();
        }
		return graphs.getGraph().get(0);
	}
}