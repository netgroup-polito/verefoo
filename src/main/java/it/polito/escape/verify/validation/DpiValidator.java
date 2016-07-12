package it.polito.escape.verify.validation;

import com.fasterxml.jackson.databind.JsonNode;

import it.polito.escape.verify.model.Configuration;
import it.polito.escape.verify.model.Graph;
import it.polito.escape.verify.model.Node;
import it.polito.escape.verify.validation.exception.ValidationException;

public class DpiValidator implements ValidationInterface {
	
	public DpiValidator(){
		
	}

	private void validateKey(String key) throws ValidationException {
		if (!key.matches("\\w+"))
			throw new ValidationException("'" + key + "' is not a valid configuration string for a 'dpi'");
	}

	@Override
	public void validate(Graph graph, Node node, Configuration configuration) throws ValidationException {
		
		JsonNode conf = configuration.getConfiguration();

		if (!conf.isArray()) {
			throw new ValidationException("Configuration of a 'dpi' must be an array");
		}
		for (JsonNode key : conf) {
			if (!key.isTextual())
				throw new ValidationException("Configuration of a 'dpi' must be an array of strings");
			validateKey(key.asText());
		}
	}

}
