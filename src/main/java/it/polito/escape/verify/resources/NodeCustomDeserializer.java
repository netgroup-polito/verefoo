package it.polito.escape.verify.resources;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.type.TypeFactory;

import it.polito.escape.verify.model.Graph;
import it.polito.escape.verify.model.Neighbour;
import it.polito.escape.verify.model.Node;

public class NodeCustomDeserializer extends JsonDeserializer<Node>{

	@Override
	public Node deserialize(JsonParser jp, DeserializationContext context) throws IOException, JsonProcessingException {
		JsonNode root = jp.getCodec().readTree(jp);
		
		JsonNode neighboursJson = root.get("neighbours");
		String nodeName = root.get("name").asText();
		String functionalType = root.get("functional_type").asText();
		//int nodeId = (Integer) ((IntNode) root.get("id")).numberValue();
		
		List<Neighbour> neighbourList =
				new ObjectMapper().readValue(neighboursJson.toString(), TypeFactory.defaultInstance().constructCollectionType(List.class, Neighbour.class));
		
		Node node = new Node();
		node.setName(nodeName);
		node.setFunctional_type(functionalType);
		//node.setId(nodeId);
		Map<Long, Neighbour> neighbours = node.getNeighbours();
		
		long numberOfNeighbours = 0;
		for (Neighbour neighbour : neighbourList){
			neighbours.put(++numberOfNeighbours, neighbour);
		}

		return node;
	}

}
