package it.polito.escape.verify.resources;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.ws.rs.Consumes;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.Provider;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import it.polito.escape.verify.database.DatabaseClass;
import it.polito.escape.verify.model.Configuration;
import it.polito.escape.verify.model.Graph;
import it.polito.escape.verify.model.Node;

@Provider
@Consumes(MediaType.APPLICATION_JSON)
public class ConfigurationMessageBodyReader implements MessageBodyReader<Configuration>{
	@Context
    UriInfo uriInfo;
	
	@Override
	public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return type == Configuration.class;
	}

	@Override
	public Configuration readFrom(Class<Configuration> type, Type genericType, Annotation[] annotations,
			MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
					throws IOException, WebApplicationException {
		JSONParser jsonParser = new JSONParser();
		InputStreamReader reader = new InputStreamReader(entityStream);
		try {
			JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
			JSONArray configurationArray = (JSONArray) jsonObject.get("configuration");
			if (configurationArray == null)
				throw new ProcessingException("Error deserializing a configuration object: configuration field not found");
			List<String> configurationFields = new ArrayList<String>();

			for (int i=0; i < configurationArray.size(); i++) {
				configurationFields.add((String)configurationArray.get(i));
			}

			System.out.println("Got following config: ");
			configurationFields.replaceAll(s -> "ip_" + s);
			Iterator<String> iter = configurationFields.iterator();
			while(iter.hasNext()){
				String field = iter.next();
				System.out.printf(field);
				if (iter.hasNext()) {
	                System.out.printf(", ");
	            }
			}

			
			String[] paths = uriInfo.getPath().split("/");
			long graphId = Long.parseLong(paths[1], 10);
			long nodeId = Long.parseLong(paths[3], 10);
			
			ConcurrentHashMap<Long, Graph> graphs = DatabaseClass.getGraphs();
			Graph graph = graphs.get(graphId);
			if (graph == null){
				throw new ProcessingException("Invalid graph id");
			}
			Map<Long, Node> nodes = graph.getNodes();
			Node node = nodes.get(nodeId);
			if (node == null){
				throw new ProcessingException("Invalid node id");
			}
			//Configuration config = new Configuration(node.getName(),"", configurationFields);
			node.getConfiguration().setConfigurationList(configurationFields);
			
			return node.getConfiguration();
		} catch (ParseException e) {
			e.printStackTrace();
			throw new ProcessingException("Error deserializing a Configuration object.", e);
		}
	}

}
