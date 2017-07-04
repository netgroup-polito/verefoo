package it.polito.verigraph.deserializer;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

import it.polito.verigraph.exception.BadRequestException;
import it.polito.verigraph.exception.InternalServerErrorException;
import it.polito.verigraph.model.Graph;
import it.polito.verigraph.model.Node;


/**
 * The Class GraphCustomDeserializer is a custom deserializer for a Graph object
 */
public class GraphCustomDeserializer extends JsonDeserializer<Graph>{

	/* (non-Javadoc)
	 * @see com.fasterxml.jackson.databind.JsonDeserializer#deserialize(com.fasterxml.jackson.core.JsonParser, com.fasterxml.jackson.databind.DeserializationContext)
	 */
	@Override
	public Graph deserialize(JsonParser jp, DeserializationContext context){
		JsonNode root = null;
		try {
			root = jp.getCodec().readTree(jp);
		}
		catch (JsonProcessingException e) {
			throw new InternalServerErrorException("Error parsing a graph: " + e.getMessage());
		}
		catch (IOException e) {
			throw new InternalServerErrorException("I/O error parsing a graph: " + e.getMessage());
		} 
		
		JsonNode nodesJson = root.get("nodes");
		
		if(nodesJson == null)
			throw new BadRequestException("Invalid graph");
		
		List<Node> nodeList = null;
		try {
			nodeList = new ObjectMapper().readValue(nodesJson.toString(), TypeFactory.defaultInstance().constructCollectionType(List.class, Node.class));
		}
		catch (JsonParseException e) {
			throw new BadRequestException("Invalid content for a graph: " + e.getMessage());
		}
		catch (JsonMappingException e) {
			throw new BadRequestException("Invalid input json structure for a graph: " + e.getMessage());
		}
		catch (IOException e) {
			throw new InternalServerErrorException("I/O error parsing a graph: " + e.getMessage());
		}

		Graph graph = new Graph();
		if(root.get("id") != null){
			long graphId = root.get("id").asLong();
			graph.setId(graphId);
		}
		Map<Long, Node> nodes = graph.getNodes();
		
		long numberOfNodes = 0;
		for (Node node : nodeList){
			nodes.put(++numberOfNodes, node);
		}
		return graph;

	}

}
