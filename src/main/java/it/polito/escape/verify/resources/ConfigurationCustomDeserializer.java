package it.polito.escape.verify.resources;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.polito.escape.verify.model.Configuration;
import it.polito.escape.verify.model.ConfigurationObject;

public class ConfigurationCustomDeserializer extends JsonDeserializer<ConfigurationObject>{

	@Override
	public ConfigurationObject deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		JsonNode root = jp.getCodec().readTree(jp);
		
		Map<String, String> configurationMap = new ObjectMapper().readValue(root.toString(), new TypeReference<Map<String,String>>(){});
		
		ConfigurationObject confObj = new ConfigurationObject();
		confObj.setMap(configurationMap);
		
		return confObj;
	}

}
