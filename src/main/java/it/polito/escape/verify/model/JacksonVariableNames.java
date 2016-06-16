package it.polito.escape.verify.model;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonVariableNames {

    static final String JSON = "{\n"
            + "  \"a\": {\n"
            + "    \"value\": \"1\"\n"
            + "  },\n"
            + "  \"b\": {\n"
            + "    \"value\": \"2\"\n"
            + "  },\n"
            + "  \"c\": {\n"
            + "    \"value\": \"3\"\n"
            + "  }\n"
            + "}";

    static class Value {
        private final String value;

        @JsonCreator
        Value(@JsonProperty("value") final String value) {this.value = value;}

        @Override
        public String toString() {
            return "Value{" +
                    "value='" + value + '\'' +
                    '}';
        }
    }

    static class Values {
        private final Map<String, Value> values = new HashMap<>();

        @JsonAnySetter
        public void setValue(final String property, final Value value) {
            values.put(property, value);
        }

        @Override
        public String toString() {
            return "Values{" +
                    "values=" + values +
                    '}';
        }
    }
    
	static String readFile(String path, Charset encoding) throws IOException {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
	}

    public static void main(String[] args) throws IOException {
        String filename = "C:/Program Files/Java/apache-tomcat-8.0.30/shared/test.json";
        String content = readFile(filename, Charset.defaultCharset());
        System.out.println(content);
    	
    	final ObjectMapper mapper = new ObjectMapper();
//        System.out.println(mapper.readValue(JSON, Values.class));
        System.out.println(mapper.readValue(content, Values.class));

    }
}