package it.polito.verifoo.rest.webservice;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.xml.sax.SAXException;

import it.polito.verifoo.rest.common.BadNffgException;
import it.polito.verifoo.rest.common.VerifooProxy;
import it.polito.verifoo.rest.jaxb.NFV;


@Path("/rest")
public class RestFoo {
	    @POST
	    @Consumes(MediaType.APPLICATION_XML)
		@Produces(MediaType.TEXT_PLAIN)
	    public String put(String nfv) throws JAXBException, SAXException, BadNffgException {
            // create a JAXBContext capable of handling the generated classes
            JAXBContext jc;
			try {
				jc = JAXBContext.newInstance( "it.polito.verifoo.rest.jaxb" );
				// create an Unmarshaller
	            Unmarshaller u = jc.createUnmarshaller();
	            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI); 
	            
	            Schema schema = sf.newSchema( new URL("https://raw.githubusercontent.com/netgroup-polito/verifoo/rest-service/xsd/nfvInfo.xsd")); 
	            u.setSchema(schema);
	            // unmarshal a document into a tree of Java content objects
	            NFV root = (NFV) u.unmarshal(new ByteArrayInputStream(nfv.getBytes()));
				VerifooProxy test = new VerifooProxy(root.getNFFG(), root.getHosts(), root.getConnections(), root.getVNFCatalog());
				
	            return test.checkNFFGProperty();
	            
			} catch (JAXBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw e;
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw e;
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
            
	        
	    }

}