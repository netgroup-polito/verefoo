package it.polito.verigraph.serializer;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import it.polito.verigraph.exception.InternalServerErrorException;

public class CustomMapSerializer extends JsonSerializer<Map<?, ?>> {
	@Override
	public void serialize(final Map<?, ?> value, final JsonGenerator jgen, final SerializerProvider provider) {
		try {
			jgen.writeObject(value.values());
		} catch (IOException e) {
			throw new InternalServerErrorException("I/O error serializing a map: " + e.getMessage());
		}
	}
}