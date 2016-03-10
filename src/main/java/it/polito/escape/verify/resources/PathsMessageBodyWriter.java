package it.polito.escape.verify.resources;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Consumes;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import it.polito.nffg.neo4j.jaxb.Paths;

//@Provider
@Consumes(MediaType.APPLICATION_XML)
@Produces(MediaType.APPLICATION_JSON)
public class PathsMessageBodyWriter implements MessageBodyWriter<Paths>{

	@Override
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return Paths.class.isAssignableFrom(type);
	}

	@Override
	public long getSize(Paths t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		// deprecated
		return -1;
	}

	@Override
	public void writeTo(Paths paths, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
					throws IOException, WebApplicationException {
		try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Paths.class);
 
            // serialize the entity myBean to the entity output stream
            jaxbContext.createMarshaller().marshal(paths, entityStream);
        } catch (JAXBException jaxbException) {
            throw new ProcessingException(
                "Error serializing a Paths object to the output stream", jaxbException);
        }
    }
		
	

}
