package it.polito.escape.verify.resources;

import java.io.IOException;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.polito.escape.verify.exception.InternalServerErrorException;
import it.polito.escape.verify.model.Configuration2;
import it.polito.escape.verify.model.Node;
import it.polito.escape.verify.service.NodeService;

public class Configuration2CustomDeserializer extends JsonDeserializer<Configuration2>{
	@Context
    UriInfo uriInfo;
	NodeService nodeService = new NodeService();
	
	@Override
	public Configuration2 deserialize(JsonParser jp, DeserializationContext ctxt)	throws IOException,
																			JsonProcessingException {
//		String[] paths = uriInfo.getPath().split("/");
//		long graphId = Long.parseLong(paths[1], 10);
//		long nodeId = Long.parseLong(paths[3], 10);
//		
//		Node node = nodeService.getNode(graphId, nodeId);
		try {
			JsonNode root = jp.getCodec().readTree(jp);
//			if(root==null)
//			return new Configuration2("", "",new ObjectMapper().createArrayNode());
			return new Configuration2("", "",root);
//			else{				
//				NodeService.validateNodeConfigurationAgainstSchemaFile(node, root);
//				Configuration2 conf = node.getConfiguration();
//				conf.setId(node.getName());
//				conf.setDescription("");
//				conf.setConfiguration(root);
//			}
		}
		catch (JsonProcessingException e) {
			throw new InternalServerErrorException("Error parsing configuration: " + e.getMessage());
		}
		catch (IOException e) {
			throw new InternalServerErrorException("I/O error parsing configuration: " + e.getMessage());
		}
		
//		return null;
	}

}
