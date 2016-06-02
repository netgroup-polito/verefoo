package it.polito.escape.verify.resources;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.type.TypeFactory;

import it.polito.escape.verify.model.Configuration;
import it.polito.escape.verify.model.ConfigurationObject;
import it.polito.escape.verify.model.Graph;
import it.polito.escape.verify.model.Neighbour;
import it.polito.escape.verify.model.Node;

public class NodeCustomDeserializer extends JsonDeserializer<Node>{

	@Override
	public Node deserialize(JsonParser jp, DeserializationContext context) throws IOException, JsonProcessingException {
		JsonNode root = jp.getCodec().readTree(jp);
		
		Node node = new Node();
		
		JsonNode neighboursJson = root.get("neighbours");
		
		String nodeName = root.get("name").asText();
		node.setName(nodeName);
		
		String functionalType = root.get("functional_type").asText();
		node.setFunctional_type(functionalType);
		
		JsonNode configurationJson = root.get("configuration");
		if (configurationJson == null)
			node.setConfiguration(new Configuration(node.getName(),"",new ArrayList<String>(), new ArrayList<ConfigurationObject>()));
		else{
			Configuration conf = node.getConfiguration();
			conf.setId(node.getName());
			conf.setDescription("");
			try{
				List<String> configurationList =
						new ObjectMapper().readValue(configurationJson.toString(), TypeFactory.defaultInstance().constructCollectionType(List.class, String.class));
//				for (String s : configurationList){
//					conf.getConfigurationList().add(s);
//				}
				conf.setConfigurationList(configurationList);
			}catch(JsonProcessingException e){
				
			}
			try{
				
				//Map<String, String> configurationMap = new ObjectMapper().readValue(configurationJson.toString(), new TypeReference<Map<String,String>>(){});
				List<ConfigurationObject> configurationMap =
						new ObjectMapper().readValue(configurationJson.toString(), TypeFactory.defaultInstance().constructCollectionType(List.class, ConfigurationObject.class));
//				Iterator<Entry<String, String>> iter = configurationMap.getMap().entrySet().iterator();
//				while(iter.hasNext()){
//					Entry<String, String> entry = iter.next();
//					conf.getConfigurationMap().put(entry.getKey(), entry.getValue());
//				}
				
				for (ConfigurationObject c : configurationMap){
					Iterator<Entry<String, String>> iter = c.getMap().entrySet().iterator();
					while(iter.hasNext()){
						Entry<String, String> entry = iter.next();
						System.out.printf("Key: " + entry.getKey() + " value: " + entry.getValue());
					}
					
				}
				conf.setConfigurationMap(configurationMap);
			}catch(JsonProcessingException e){
				
			}

			
		}
		//int nodeId = (Integer) ((IntNode) root.get("id")).numberValue();
		
		List<Neighbour> neighbourList =
				new ObjectMapper().readValue(neighboursJson.toString(), TypeFactory.defaultInstance().constructCollectionType(List.class, Neighbour.class));
	
		//node.setConfiguration(new Configuration(node.getName(),"",new ArrayList<String>()));
		//node.setId(nodeId);
		Map<Long, Neighbour> neighbours = node.getNeighbours();
		
		long numberOfNeighbours = 0;
		for (Neighbour neighbour : neighbourList){
			neighbours.put(++numberOfNeighbours, neighbour);
		}
		

		return node;
	}

}
