package it.polito.verifoo.rest.webservice;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;

import javax.ws.rs.Consumes;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.ws.WebServiceException;

import org.xml.sax.SAXException;

import it.polito.verifoo.rest.jaxb.NFV;
/**
 * This class provide the validation of XML schema in input and in output for NFV object.
 */
@Provider
@Produces(MediaType.APPLICATION_XML)
@Consumes(MediaType.APPLICATION_XML)
public class XMLProvider implements MessageBodyReader<Object>, MessageBodyWriter<Object>{

	@Override
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type == NFV.class;
	}

    public void writeTo(Object object, Class<?> type, Type genericType,
            Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, Object> httpHeaders,
            OutputStream entityStream) throws IOException,
            WebApplicationException {
            try {
                Marshaller m = JAXBContext.newInstance( "it.polito.verifoo.rest.jaxb").createMarshaller();
                m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
                m.setProperty( Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION,"https://raw.githubusercontent.com/netgroup-polito/verifoo/rest-service/xsd/nfvInfo.xsd");
        		try {
        			SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);    
        			Schema schema = sf.newSchema( new URL("https://raw.githubusercontent.com/netgroup-polito/verifoo/rest-service/xsd/nfvInfo.xsd"));
        			m.setSchema(schema);
        		} catch (MalformedURLException e) {
        			e.printStackTrace();
        		} catch (SAXException e) {
        			e.printStackTrace();
        		}
                m.marshal(object, entityStream);
            } catch(JAXBException e) {
            	e.printStackTrace();
            	throw new ProcessingException("Error serializing XML:"+e.toString());  
            }
    }

    @Override
    public boolean isReadable(Class<?> type, Type genericType,
                              Annotation[] annotations, MediaType mediaType) {
        return type == NFV.class;
    }
	@Override
    public NFV readFrom(Class<Object> type, Type genericType,
            Annotation[] annotations, MediaType mediaType,
            MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
            throws IOException, WebApplicationException {
		try {
            Unmarshaller u = JAXBContext.newInstance( "it.polito.verifoo.rest.jaxb").createUnmarshaller();
        	try {
    			SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);    
    			Schema schema = sf.newSchema( new URL("https://raw.githubusercontent.com/netgroup-polito/verifoo/rest-service/xsd/nfvInfo.xsd"));
    			u.setSchema(schema);
    		} catch (MalformedURLException e) {
    			e.printStackTrace();
    		} catch (SAXException e) {
    			e.printStackTrace();
    		}
            return (NFV)u.unmarshal(entityStream);
        } catch(JAXBException e) {
        	e.printStackTrace();
        	throw new ProcessingException("Error deserializing XML:"+e.toString());
        }
	}


}
