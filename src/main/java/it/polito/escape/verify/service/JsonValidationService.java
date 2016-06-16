package it.polito.escape.verify.service;

import java.util.Iterator;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.JsonNode;

import it.polito.escape.verify.exception.BadRequestException;
import it.polito.escape.verify.model.Graph;
import it.polito.escape.verify.model.Node;

public class JsonValidationService {
	private Graph graph = new Graph();
	private Node node = new Node();

	public JsonValidationService() {

	}

	public JsonValidationService(Graph graph, Node node) {
		this.graph = graph;
		this.node = node;
	}

	public boolean validateFieldAgainstNodeNames(String value) {
		for (Node node : this.graph.getNodes().values()) {
			if (node.getName().equals(value))
				return true;
		}
		return false;
	}

	public void myValidation(JsonNode node) {
		if (node.isTextual()) {
			boolean isValid = validateFieldAgainstNodeNames(node.asText());
			if (!isValid) {
				System.out.println(node.asText() + " is not a valid string!");
				throw new BadRequestException("String '" + node.asText() + "' is not valid for the configuration of node '" + this.node.getName() + "'");
			}
		}
		if (node.isArray()) {
			for (JsonNode object : node) {
				myValidation(object);
			}
		}
		if (node.isObject()) {
			Iterator<Entry<String, JsonNode>> iter = node.fields();

			while (iter.hasNext()) {
				Entry<String, JsonNode> item = iter.next();
				myValidation(item.getValue());
			}
		}

	}
}
