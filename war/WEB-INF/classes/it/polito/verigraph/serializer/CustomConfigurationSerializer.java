package it.polito.verigraph.serializer;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import it.polito.verigraph.exception.InternalServerErrorException;
import it.polito.verigraph.model.Configuration;

public class CustomConfigurationSerializer extends JsonSerializer<Configuration> {

	@Override
	public void serialize(Configuration conf, JsonGenerator jgen, SerializerProvider provider)
			throws IOException, JsonProcessingException {
		try {
			jgen.writeObject(conf.getConfiguration());
		} catch (IOException e) {
			throw new InternalServerErrorException("I/O error serializing a configuration object: " + e.getMessage());
		}

	}

}
