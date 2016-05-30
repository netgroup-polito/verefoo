package it.polito.escape.verify.resources;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.type.TypeFactory;

import it.polito.escape.verify.database.DatabaseClass;
import it.polito.escape.verify.model.Graph;
import it.polito.escape.verify.model.Neighbour;
import it.polito.escape.verify.model.Node;
import it.polito.escape.verify.service.GraphService;


public class GraphCustomDeserializer extends JsonDeserializer<Graph>{

	@Override
	public Graph deserialize(JsonParser jp, DeserializationContext context)
			throws IOException, JsonProcessingException {
		JsonNode root = jp.getCodec().readTree(jp); 
		
		JsonNode nodesJson = root.get("nodes");
		
		List<Node> nodeList =
				new ObjectMapper().readValue(nodesJson.toString(), TypeFactory.defaultInstance().constructCollectionType(List.class, Node.class));
		for (Node node : nodeList){
			System.out.println("Node name: " + node.getName());
		}
		Graph graph = new Graph();
		Map<Long, Node> nodes = graph.getNodes();
		
		long numberOfNodes = 0;
		for (Node node : nodeList){
			nodes.put(++numberOfNodes, node);
		}
		return graph;

	}

}
