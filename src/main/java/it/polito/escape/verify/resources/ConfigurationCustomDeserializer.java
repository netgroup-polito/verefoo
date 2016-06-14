package it.polito.escape.verify.resources;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.polito.escape.verify.exception.BadRequestException;
import it.polito.escape.verify.exception.InternalServerErrorException;
import it.polito.escape.verify.model.ConfigurationObject;


/**
 * The Class ConfigurationCustomDeserializer is a custom deserializer for a ConfigurationObject, i.e. a configuration map
 */
public class ConfigurationCustomDeserializer extends JsonDeserializer<ConfigurationObject>{

	/* (non-Javadoc)
	 * @see com.fasterxml.jackson.databind.JsonDeserializer#deserialize(com.fasterxml.jackson.core.JsonParser, com.fasterxml.jackson.databind.DeserializationContext)
	 */
	@Override
	public ConfigurationObject deserialize(JsonParser jp, DeserializationContext ctxt) {
		try {
			JsonNode root = jp.getCodec().readTree(jp);
			try {
				Map<String, String> configurationMap = new ObjectMapper().readValue(root.toString(), new TypeReference<Map<String,String>>(){});
				ConfigurationObject confObj = new ConfigurationObject();
				confObj.setMap(configurationMap);
				
				return confObj;
			}
			catch (JsonParseException e) {
				throw new BadRequestException("Invalid content for a configuration map: " + e.getMessage());
			}
			catch (JsonMappingException e) {
				throw new BadRequestException("Invalid input json structure for a configuration map: " + e.getMessage());
			}
		}
		catch (JsonProcessingException e) {
			throw new InternalServerErrorException("Error parsing a configuration map: " + e.getMessage());
		}
		catch (IOException e) {
			throw new InternalServerErrorException("I/O error parsing a configuration map: " + e.getMessage());
		}
	
	}

}
