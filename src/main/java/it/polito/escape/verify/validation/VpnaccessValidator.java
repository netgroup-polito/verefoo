package it.polito.escape.verify.validation;

import com.fasterxml.jackson.databind.JsonNode;

import it.polito.escape.verify.model.Configuration;
import it.polito.escape.verify.model.Graph;
import it.polito.escape.verify.model.Node;
import it.polito.escape.verify.validation.exception.ValidationException;

public class VpnaccessValidator implements ValidationInterface {

	@Override
	public void validate(Graph graph, Node node, Configuration configuration) throws ValidationException {
		JsonNode configurationNode = configuration.getConfiguration();

		if (!configurationNode.isArray())
			throw new ValidationException("configuration must be an array");

		for (JsonNode object : configurationNode) {
			JsonNode vpnexit = object.get("vpnexit");
			if (!vpnexit.isTextual())
				throw new ValidationException("value corresponding to the key 'vpnexit' must be a string");
			validateObject(graph, node, vpnexit.asText());
		}

	}

	private void validateObject(Graph g, Node node, String field) throws ValidationException {
		boolean isValid = false;
		for (Node n : g.getNodes().values()) {
			if ((n.getFunctional_type().equals("vpnexit")) && (n.getName().equals(field)))
				isValid = true;
		}

		if (!isValid)
			throw new ValidationException("'"	+ field + "' is not a valid value for the configuration of a type '"
											+ node.getFunctional_type()
											+ "'. Please use the name of an existing node of type 'vpnexit'.");

	}

}
