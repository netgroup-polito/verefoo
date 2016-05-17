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
import javax.xml.bind.Marshaller;

import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import it.polito.escape.verify.model.Graph;
import it.polito.escape.verify.model.Neighbour;
import it.polito.escape.verify.model.Node;

//@Provider
public class NodeMessageBodyWriter implements MessageBodyWriter<Node>{

	@Override
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return Node.class.isAssignableFrom(type);
	}

	@Override
	public long getSize(Node t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		// deprecated
		return -1;
	}

	@Override
	public void writeTo(Node node, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
					throws IOException, WebApplicationException {
		try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Node.class);
 
            // serialize the entity myBean to the entity output stream
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
            marshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);
			marshaller.marshal(node, entityStream);
			
			
//			JSONObject root = new JSONObject();
//			JSONArray nodes = new JSONArray();
//			for (Node n : graph.getNodes().values()){
//				JSONObject node = new JSONObject();
//				node.put("id", n.getId());
//				node.put("name", n.getName());
//				node.put("functional_type", n.getFunctional_type());
//				nodes.add(node);
//				JSONArray nodeNeighbours = new JSONArray();
//				for (Neighbour ne : n.getNeighbours().values()){
//					JSONObject neighbour = new JSONObject();
//					neighbour.put("id", ne.getId());
//					neighbour.put("name", ne.getName());
//					nodeNeighbours.add(neighbour);
//				}
//				node.put("neighbours", nodeNeighbours);
//			}
//			root.put("nodes", nodes);
//			entityStream.write(root.toJSONString().getBytes());
			
			
			
        } catch (JAXBException jaxbException) {
            throw new ProcessingException(
                "Error serializing a Node object to the output stream", jaxbException);
        }
		
	}

}
