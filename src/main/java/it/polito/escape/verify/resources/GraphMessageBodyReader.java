package it.polito.escape.verify.resources;

import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import it.polito.escape.verify.database.DatabaseClass;
import it.polito.escape.verify.model.Graph;
import it.polito.escape.verify.model.Neighbour;
import it.polito.escape.verify.model.Node;
import it.polito.nffg.neo4j.jaxb.Paths;

@Provider
@Consumes(MediaType.APPLICATION_JSON)
public class GraphMessageBodyReader implements MessageBodyReader<Graph>{

	@Override
	public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return type == Graph.class;
	}

	@Override
	public Graph readFrom(Class<Graph> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
					throws IOException, WebApplicationException {
//		try {
//	        JAXBContext jaxbContext = JAXBContext.newInstance(Paths.class);
//	        Graph graph = (Graph) jaxbContext.createUnmarshaller()
//	            .unmarshal(entityStream);
//	        return graph;
//	    } catch (JAXBException jaxbException) {
//	        throw new ProcessingException("Error deserializing a Graph object.",
//	            jaxbException);
//	    }
		
		JSONParser jsonParser = new JSONParser();
		InputStreamReader reader = new InputStreamReader(entityStream);
		try {
			Graph g = new Graph();
			g.setId(DatabaseClass.getNumberOfGraphs()+1);
			JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
			JSONArray nodes = (JSONArray) jsonObject.get("nodes");
			Iterator nodesIterator = nodes.iterator();
			int nodesCouter = 0;
			while (nodesIterator.hasNext()) {
				JSONObject node = (JSONObject) nodesIterator.next();
				Node n = new Node();
				n.setId(++nodesCouter);
				n.setName((String)node.get("name"));
				n.setFunctional_type((String)node.get("functional_type"));
				
				JSONArray neighbours = (JSONArray) node.get("neighbours");
				Iterator neighboursIterator = neighbours.iterator();
				int neighboursCounter = 0;
				while (neighboursIterator.hasNext()){
					JSONObject neighbour = (JSONObject) neighboursIterator.next();
					Neighbour ne = new Neighbour();
					ne.setId(++neighboursCounter);
					ne.setName((String)neighbour.get("name"));
					n.getNeighbours().put(ne.getId(), ne);
				}
				g.getNodes().put(n.getId(), n);
			}
			Map<Long, Graph> graphs = DatabaseClass.getGraphs();
			graphs.put(g.getId(), g);
			return g;
		} catch (ParseException e) {
			e.printStackTrace();
			throw new ProcessingException("Error deserializing a Graph object.", e);
		}
	}

}
