package it.polito.escape.verify.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.InternalServerErrorException;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.main.JsonSchema;

import it.polito.escape.verify.database.DatabaseClass;
import it.polito.escape.verify.exception.BadRequestException;
import it.polito.escape.verify.exception.DataNotFoundException;
import it.polito.escape.verify.exception.ForbiddenException;
import it.polito.escape.verify.model.Configuration;
import it.polito.escape.verify.model.Graph;
import it.polito.escape.verify.model.Neighbour;
import it.polito.escape.verify.model.Node;

public class NodeService {

	private Map<Long, Graph> graphs = DatabaseClass.getInstance().getGraphs();

	public NodeService() {

	}

	public List<Node> getAllNodes(long graphId) {
		if (graphId <= 0) {
			throw new ForbiddenException("Illegal graph id: " + graphId);
		}
		Graph graph = graphs.get(graphId);
		if (graph == null)
			throw new DataNotFoundException("Graph with id " + graphId + " not found");
		Map<Long, Node> nodes = graph.getNodes();
		return new ArrayList<Node>(nodes.values());
	}

	public Node getNode(long graphId, long nodeId) {
		if (graphId <= 0) {
			throw new ForbiddenException("Illegal graph id: " + graphId);
		}
		if (nodeId <= 0) {
			throw new ForbiddenException("Illegal node id: " + nodeId);
		}
		Graph graph = graphs.get(graphId);
		if (graph == null)
			throw new DataNotFoundException("Graph with id " + graphId + " not found");
		Map<Long, Node> nodes = graph.getNodes();
		Node node = nodes.get(nodeId);
		if (node == null) {
			throw new DataNotFoundException("Node with id " + nodeId + " not found in graph with id " + graphId);
		}
		return node;
	}

	public Node updateNode(long graphId, Node node) {
		if (graphId <= 0) {
			throw new ForbiddenException("Illegal graph id: " + graphId);
		}
		if (node.getId() <= 0) {
			throw new ForbiddenException("Illegal node id: " + node.getId());
		}
		Graph graph = graphs.get(graphId);
		if (graph == null)
			throw new DataNotFoundException("Graph with id " + graphId + " not found");
		Map<Long, Node> nodes = graph.getNodes();
		Node localNode = nodes.get(node.getId());
		if (localNode == null) {
			throw new DataNotFoundException("Node with id " + node.getId() + " not found in graph with id " + graphId);
		}

		Graph graphCopy = new Graph();
		graphCopy.setId(graph.getId());
		graphCopy.setNodes(new HashMap<Long, Node>(graph.getNodes()));
		graphCopy.getNodes().remove(node.getId());

		// int numberOfNeighbours = 0;
		// for(Neighbour neighbour : node.getNeighbours().values()){
		// neighbour.setId(++numberOfNeighbours);
		// }

		for (Map.Entry<Long, Neighbour> neighbourEntry : node.getNeighbours().entrySet()) {
			neighbourEntry.getValue().setId(neighbourEntry.getKey());
		}

		validateNode(graphCopy, node);

		synchronized (this) {
			nodes.put(node.getId(), node);
			DatabaseClass.persistDatabase();
			return node;
		}
	}

	public Node removeNode(long graphId, long nodeId) {
		if (graphId <= 0) {
			throw new ForbiddenException("Illegal graph id: " + graphId);
		}
		if (nodeId <= 0) {
			throw new ForbiddenException("Illegal node id: " + nodeId);
		}
		Graph graph = graphs.get(graphId);
		if (graph == null)
			throw new DataNotFoundException("Graph with id " + graphId + " not found");
		Map<Long, Node> nodes = graph.getNodes();

		synchronized (this) {
			return nodes.remove(nodeId);
		}
	}

	public Node addNode(long graphId, Node node) {
		if (graphId <= 0) {
			throw new ForbiddenException("Illegal graph id: " + graphId);
		}
		Graph graph = graphs.get(graphId);
		if (graph == null)
			throw new DataNotFoundException("Graph with id " + graphId + " not found");
		Map<Long, Node> nodes = graph.getNodes();

		validateNode(graph, node);

		synchronized (this) {
			node.setId(DatabaseClass.getInstance().getGraphNumberOfNodes(graphId) + 1);
		}

		// int numberOfNeighbours = 0;

		for (Map.Entry<Long, Neighbour> neighbourEntry : node.getNeighbours().entrySet()) {
			neighbourEntry.getValue().setId(neighbourEntry.getKey());
		}

		// for (Neighbour neighbour : node.getNeighbours().values()) {
		// neighbour.setId(++numberOfNeighbours);
		// }

		synchronized (this) {
			nodes.put(node.getId(), node);
			DatabaseClass.persistDatabase();
			return node;
		}
	}

	public Node searchByName(long graphId, String nodeName) {
		if (graphId <= 0) {
			throw new ForbiddenException("Illegal graph id: " + graphId);
		}
		Graph graph = graphs.get(graphId);
		if (graph == null)
			throw new DataNotFoundException("Graph with id " + graphId + " not found");
		Map<Long, Node> nodes = graph.getNodes();

		for (Node node : nodes.values()) {
			if (node.getName().equals(nodeName))
				return node;
		}
		return null;
	}

	public static void validateNode(Graph graph, Node node) {
		if (graph == null)
			throw new BadRequestException("Node validation failed: cannot validate null graph");
		if (node == null)
			throw new BadRequestException("Node validation failed: cannot validate null node");

		if (node.getName() == null)
			throw new BadRequestException("Node validation failed: node 'name' field cannot be null");
		if (node.getFunctional_type() == null)
			throw new BadRequestException("Node validation failed: node 'functional_type' field cannot be null");

		if (node.getName().equals(""))
			throw new BadRequestException("Node validation failed: node 'name' field cannot be an empty string");
		if (node.getFunctional_type().equals(""))
			throw new BadRequestException("Node validation failed: node 'functional_type' field cannot be an empty string");

		Node nodeFound = graph.searchNodeByName(node.getName());
		if ((nodeFound != null) && (nodeFound.equals(node) == false))
			throw new BadRequestException("Node validation failed: graph already has a node named '"	+ node.getName()
											+ "'");
		Configuration configuration = node.getConfiguration();
		if (configuration != null) {
			JsonNode configurationJsonNode = configuration.getConfiguration();
			// validate configuration against schema file
			validateNodeConfigurationAgainstSchemaFile(node, configurationJsonNode);
			JsonValidationService jsonValidator = new JsonValidationService(graph, node);
			boolean hasCustomValidator = jsonValidator.validateNodeConfiguration();
			if (!hasCustomValidator) {
				jsonValidator.validateFieldsAgainstNodeNames(configurationJsonNode);
			}
		}

		// validate neighbours
		Map<Long, Neighbour> nodeNeighboursMap = node.getNeighbours();
		if (nodeNeighboursMap == null)
			throw new BadRequestException("Node validation failed: node 'neighbours' cannot be null");
		for (Neighbour neighbour : nodeNeighboursMap.values()) {
			NeighbourService.validateNeighbour(graph, node, neighbour);
		}
	}

	public static void validateNodeConfigurationAgainstSchemaFile(Node node, JsonNode configurationJson) {
		String schemaFileName = node.getFunctional_type() + ".json";

		File schemaFile = new File(System.getProperty("catalina.base") + "/webapps/verify/json/" + schemaFileName);

		if (!schemaFile.exists()) {
			throw new ForbiddenException("Functional type '"	+ node.getFunctional_type()
											+ "' is not supported! Please edit 'functional_type' field of node '"
											+ node.getName() + "'");
		}

		JsonSchema schemaNode = null;
		try {
			schemaNode = ValidationUtils.getSchemaNode(schemaFile);
		}
		catch (IOException e) {
			throw new InternalServerErrorException("Unable to load '" + schemaFileName + "' schema file");
		}
		catch (ProcessingException e) {
			throw new InternalServerErrorException("Unable to resolve '"	+ schemaFileName
													+ "' schema file as a schema node");
		}

		try {
			ValidationUtils.validateJson(schemaNode, configurationJson);
		}
		catch (ProcessingException e) {
			throw new BadRequestException("Something went wrong trying to validate node '"	+ node.getName()
											+ "' with the following configuration: '" + configurationJson.toString()
											+ "' against the json schema '" + schemaFile.getName() + "': "
											+ e.getMessage());

		}

	}
}
