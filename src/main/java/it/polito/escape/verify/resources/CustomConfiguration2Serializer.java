package it.polito.escape.verify.resources;

import java.io.IOException;
import java.util.Arrays;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import it.polito.escape.verify.exception.InternalServerErrorException;
import it.polito.escape.verify.model.Configuration2;

public class CustomConfiguration2Serializer extends JsonSerializer<Configuration2>{

	@Override
	public void serialize(	Configuration2 conf, JsonGenerator jgen,
							SerializerProvider provider) throws IOException, JsonProcessingException {
		try {
//			byte[] bytes = conf.getConfiguration().getBytes();
//			String decodedString = new String(bytes);
//			System.out.println("About to return the following configuration");
//			System.out.println(decodedString);
//			jgen.writeRawUTF8String(bytes, 0, bytes.length);
//			jgen.writeString(conf.getConfiguration());
			jgen.writeObject(conf.getConfiguration());
		}
		catch (IOException e) {
			throw new InternalServerErrorException("I/O error serializing a configuration object: " + e.getMessage());
		}
		
	}

}
