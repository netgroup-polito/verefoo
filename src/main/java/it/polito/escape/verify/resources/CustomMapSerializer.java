package it.polito.escape.verify.resources;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class CustomMapSerializer extends JsonSerializer<Map<?, ?>> {
    @Override
    public void serialize(final Map<?, ?> value, final JsonGenerator jgen, final SerializerProvider provider)
            throws IOException, JsonProcessingException {
        jgen.writeObject(value.values());
    }
}