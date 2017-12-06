package it.polito.verifoo.rest.main;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
import com.microsoft.z3.Context;
import com.microsoft.z3.Status;
import it.polito.verifoo.rest.jaxb.*;
import it.polito.verigraph.mcnet.components.IsolationResult;
import it.polito.verifoo.rest.common.*;

public class Main {
	Context ctx;
	public static void main(String[] args) throws MalformedURLException{
		System.setProperty("log4j.configuration", new File("resources", "log4j2.xml").toURI().toURL().toString());
        Logger logger = LogManager.getLogger("mylog"); 

		// TODO Auto-generated method stub
		try {
            // create a JAXBContext capable of handling the generated classes
            JAXBContext jc = JAXBContext.newInstance( "it.polito.verifoo.rest.jaxb" );
            // create an Unmarshaller
            Unmarshaller u = jc.createUnmarshaller();
            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI); 
            Schema schema = sf.newSchema( new File( "./xsd/nfvInfo.xsd" )); 
            u.setSchema(schema);
            // unmarshal a document into a tree of Java content objects
            //NFV root = (NFV) u.unmarshal( new FileInputStream( "./xsd/nfvInfo.xml" ) );
            NFV root = (NFV) u.unmarshal( new FileInputStream( "./testfile/GEANT.xml" ) );
           
            for(Graph g:root.getGraphs().getGraph()){
            	VerifooProxy test = new VerifooProxy(g, root.getHosts(), root.getConnections(),root.getCapacityDefinition());
            	IsolationResult res=test.checkNFFGProperty();
            	if(res.result != Status.UNSATISFIABLE)
            		new Translator(res.model.toString(),root).convert();
            	root.getPropertyDefinition().getProperty().stream().filter(p->p.getGraph()==g.getId()).findFirst().get().setIsSat(res.result!=Status.UNSATISFIABLE); 
            }
            // create a Marshaller and marshal to output
            Marshaller m = jc.createMarshaller();
            m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
            m.setProperty( Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION,"./xsd/nfvInfo.xsd");
            //m.marshal( root, System.out ); 
        } catch( JAXBException je ) {
        	logger.error("Error while unmarshalling or marshalling");
            logger.error(je);
            System.exit(1);
        } catch( IOException ioe ) {
            logger.error(ioe);
            System.exit(1);
        } catch( ClassCastException cce) {
        	logger.error("Wrong data type found in XML document");
        	logger.error(cce);
            System.exit(1);
        } catch (BadNffgException e) {
			logger.error("Graph semantically incorrect");
        	logger.error(e);
            System.exit(1);
		} catch (SAXException e) {
			logger.error(e);
			System.exit(1);
		}
	}

}
