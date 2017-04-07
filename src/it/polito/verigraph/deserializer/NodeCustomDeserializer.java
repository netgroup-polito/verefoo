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
import it.polito.verigraph.model.Configuration;
import it.polito.verigraph.model.Neighbour;
import it.polito.verigraph.model.Node;

public class NodeCustomDeserializer extends JsonDeserializer<Node> {

	@Override
	public Node deserialize(JsonParser jp, DeserializationContext context) {

		try {
			JsonNode root = jp.getCodec().readTree(jp);
			JsonNode neighboursJson = root.get("neighbours");
			JsonNode configurationJson = root.get("configuration");

			String nodeName = root.get("name").asText();
			String functionalType = root.get("functional_type").asText();

			Node node = new Node();
			if(root.get("id") != null){
				long nodeId = root.get("id").asLong();
				node.setId(nodeId);
			}
			node.setName(nodeName);
			node.setFunctional_type(functionalType);

			if (configurationJson == null)
				node.setConfiguration(new Configuration(node.getName(), "", new ObjectMapper().createArrayNode()));
			else {
				Configuration conf = node.getConfiguration();
				conf.setId(node.getName());
				conf.setDescription("");
				conf.setConfiguration(configurationJson);
			}

			try {
				List<Neighbour> neighbourList = new ObjectMapper().readValue(	neighboursJson.toString(),
																				TypeFactory	.defaultInstance()
																							.constructCollectionType(	List.class,
																														Neighbour.class));
				Map<Long, Neighbour> neighbours = node.getNeighbours();

				long numberOfNeighbours = 0;
				for (Neighbour neighbour : neighbourList) {
					neighbours.put(++numberOfNeighbours, neighbour);
				}

				return node;
			}
			catch (JsonParseException e) {
				throw new BadRequestException("Invalid content for a node: " + e.getMessage());
			}
			catch (JsonMappingException e) {
				throw new BadRequestException("Invalid input json structure for a node: " + e.getMessage());
			}
		}
		catch (JsonProcessingException e) {
			throw new InternalServerErrorException("Error parsing a node: " + e.getMessage());
		}
		catch (IOException e) {
			throw new InternalServerErrorException("I/O error parsing a node: " + e.getMessage());
		}

	}

}
