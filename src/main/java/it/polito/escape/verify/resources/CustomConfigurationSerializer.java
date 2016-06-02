package it.polito.escape.verify.resources;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import it.polito.escape.verify.model.Configuration;
import it.polito.escape.verify.model.ConfigurationObject;

public class CustomConfigurationSerializer extends JsonSerializer<Configuration> {
    @Override
    public void serialize(final Configuration conf, final JsonGenerator jgen, final SerializerProvider provider)
            throws IOException, JsonProcessingException {
        jgen.writeStartArray();
        if (conf.getConfigurationList().size() != 0){
        	System.out.println("Size is: " + conf.getConfigurationList().size());
	        for (String s : conf.getConfigurationList()){
	        	jgen.writeString(s);
	        }
        }
        else{
        	for (ConfigurationObject map : conf.getConfigurationMap()){
        		Iterator<Entry<String, String>> iter = map.getMap().entrySet().iterator();
            	while(iter.hasNext()){
            		Entry<String, String> entry = iter.next();
            		
            		jgen.writeStartObject();
            		jgen.writeStringField(entry.getKey(), entry.getValue());
            		jgen.writeEndObject();
            	}
        	}
        	
        }
        jgen.writeEndArray();
    }
}