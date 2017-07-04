package it.polito.verigraph.deserializer;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import javax.ws.rs.Consumes;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import it.polito.neo4j.jaxb.Paths;

@Provider
@Consumes(MediaType.APPLICATION_XML)
public class PathsMessageBodyReader implements MessageBodyReader<Paths>{

	@Override
	public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return type == Paths.class;
	}

	@Override
	public Paths readFrom(Class<Paths> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
					throws IOException, WebApplicationException {
		try {
	        JAXBContext jaxbContext = JAXBContext.newInstance(Paths.class);
	        Paths paths = (Paths) jaxbContext.createUnmarshaller()
	            .unmarshal(entityStream);
	        return paths;
	    } catch (JAXBException jaxbException) {
	        throw new ProcessingException("Error deserializing a Paths object.",
	            jaxbException);
	    }
	}

}
