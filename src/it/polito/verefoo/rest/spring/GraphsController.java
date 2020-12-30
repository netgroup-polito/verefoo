package it.polito.verefoo.rest.spring;

import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import it.polito.verefoo.jaxb.Configuration;
import it.polito.verefoo.jaxb.Constraints;
import it.polito.verefoo.jaxb.Graph;
import it.polito.verefoo.jaxb.Graphs;
import it.polito.verefoo.jaxb.Neighbour;
import it.polito.verefoo.jaxb.Node;

@RestController
@RequestMapping(value = "/adp/graphs", consumes = { "application/xml", "application/json" }, produces = {
		"application/xml", "application/json" })
public class GraphsController {

	ADPService service = new ADPService();

	@Autowired
	private HttpServletRequest request;

	/*
	 * Graphs
	 */

	/**
	 * @param graph it is the graph to store
	 * @return the created graph
	 */
	@Operation(tags = "version 1 - graphs", summary = "Create a graph", description = "")
	@ApiResponses(value = { 
		@ApiResponse(responseCode = "201", description = "Created"),
		@ApiResponse(responseCode = "400", description = "The provided graph is semantically malformed. You can check it and retry the operation accordingly."),
	})

	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseEntity<Resources<Integer>> createGraph(@RequestBody Graph graph) {
		// long id = service.getNextGraphId();
		// StringBuffer url = request.getRequestURL();
		// Graph created = service.createGraph(id, graph);
		// if (created != null) {
		// String responseUrl;
		// if(url.toString().endsWith("/")) responseUrl = url.toString() + id;
		// else responseUrl = url.toString() + "/" + id;
		// created.setId(id);
		// HttpHeaders responseHeaders = new HttpHeaders();
		// try {
		// responseHeaders.setLocation(new URI(responseUrl));
		// } catch (URISyntaxException e) {
		// throw new ResponseStatusException(
		// HttpStatus.BAD_REQUEST, "bad request"
		// );
		// }
		// return new ResponseEntity<Graph>(created, responseHeaders,
		// HttpStatus.CREATED);
		// } else
		// throw new ResponseStatusException(
		// HttpStatus.BAD_REQUEST, "bad request"
		// );
		Integer graphId = 0;
		String url = request.getRequestURL().toString();
		return ResponseEntity.status(HttpStatus.CREATED).body(
				// wrap the response with the hyperlinks
				new ResourceWrapperWithLinks<Integer>()
						.addLink(url + "/" + graphId, "self", RequestMethod.GET)
						.addLink(url + "/" + graphId, "self", RequestMethod.PUT)
						.addLink(url, "list", RequestMethod.GET)
						.addLink(url, "list", RequestMethod.DELETE)
						.addLink(url, "new", RequestMethod.POST)
						.addLink(url + "/" + graphId, "self", RequestMethod.DELETE)
						.wrap(graphId));
	}

	/**
	 * @param beforeInclusive it is the starting index
	 * @param afterInclusive  it is the ending index
	 * @return a collection of graph
	 */
	@Operation(tags = "version 1 - graphs", summary = "Get all the graphs", description = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "404", description = "No graphs have been defined so far. You can start by creating a new graph")
	 })

	public ResponseEntity<Resources<Graphs>> getGraphs() {
		// Graphs graphs = service.getGraphs();
		Graphs graphs = null;
		// if(graphs.getGraph().isEmpty())
		// throw new ResponseStatusException(
		// HttpStatus.NOT_FOUND, "not found"
		// );
		// return graphs;
		String url = request.getRequestURL().toString();
		return ResponseEntity.status(HttpStatus.CREATED).body(
				// wrap the response with the hyperlinks
				new ResourceWrapperWithLinks<Graphs>()
						.addLink(url, "new", RequestMethod.POST)
						.addLink(url, "self", RequestMethod.DELETE)
						.addLink(url, "self", RequestMethod.GET)
						.wrap(graphs));
	}

	/**
	 * 
	 */
	@Operation(tags = "version 1 - graphs", summary = "Delete all the graphs", description = "Be careful before cleaning the whole workbench of graphs.")
	@RequestMapping(value = "", method = RequestMethod.DELETE)
	@ApiResponses(value = { @ApiResponse(responseCode = "204", description = "No Content"),
			@ApiResponse(responseCode = "404", description = "The graph doesn't exist at all. You can retry the operation or refer to another graph."),
			@ApiResponse(responseCode = "409", description = "No graph has been deleted because at least one of them is referred by a requirement; you can first delete the interested requirements.") })
	@ResponseStatus(value = HttpStatus.NO_CONTENT)

	public ResponseEntity<Resources<Void>> deleteGraphs() {
		// Graphs graphs = service.deleteGraphs();
		// if (graphs == null)
		// throw new ResponseStatusException(
		// HttpStatus.NOT_FOUND, "not found"
		// );
		String url = request.getRequestURL().toString();
		return ResponseEntity.status(HttpStatus.CREATED).body(
				// wrap the response with the hyperlinks
				new ResourceWrapperWithLinks<Void>().addLink(url, "new", RequestMethod.POST).wrap(null));
	}

	/*
	 * Graph
	 */

	/**
	 * @param gid   it is the id of the graph to update
	 * @param graph it is the new graph
	 */
	@Operation(tags = "version 1 - graphs", summary = "Update a graph", description = "All the nested resources are replaced with the new ones in a shallow way.")
	@RequestMapping(value = "/{gid}", method = RequestMethod.PUT)
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "404", description = "The graph doesn't exist. You can retry the operation or create the graph instead."), })
	@ResponseStatus(value = HttpStatus.NO_CONTENT)

	public ResponseEntity<Resources<Void>> updateGraph(@PathVariable("gid") long gid, @RequestBody Graph graph) {
		// Graph updated = service.updateGraph(gid, graph);
		// if (updated == null)
		// throw new ResponseStatusException(
		// HttpStatus.NOT_FOUND, "not found"
		// );
		String url = request.getRequestURL().toString().substring(0, request.getRequestURL().lastIndexOf("/"));
		return ResponseEntity.status(HttpStatus.OK).body(
				// wrap the response with the hyperlinks
				new ResourceWrapperWithLinks<Void>().addLink(url + "/" + gid, "self", RequestMethod.GET)
						.addLink(url, "new", RequestMethod.POST).addLink(url + "/" + gid, "self", RequestMethod.DELETE)
						.addLink(url + "/" + gid, "self", RequestMethod.PUT).wrap(null));
	}

	/**
	 * @param gid it is the id of the graph to retrieve
	 */
	@Operation(tags = "version 1 - graphs", summary = "Get a graph", description = "")
	@RequestMapping(value = "/{gid}", method = RequestMethod.GET)
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "404", description = "The graph doesn't exist. You can retry the operation or refer to another graph."), })

	public ResponseEntity<Resources<Graph>> getGraph(@PathVariable("gid") long gid) {
		// Graph graph = service.getGraph(gid);
		// if (graph==null)
		// throw new ResponseStatusException(
		// HttpStatus.NOT_FOUND, "not found"
		// );
		// graph.setId(gid);
		// return graph;
		Graph graph = null;
		String url = request.getRequestURL().toString().substring(0, request.getRequestURL().lastIndexOf("/"));
		return ResponseEntity.status(HttpStatus.OK).body(
				// wrap the response with the hyperlinks
				new ResourceWrapperWithLinks<Graph>()
						.addLink(url + "/" + gid, "self", RequestMethod.GET)
						.addLink(url, "new", RequestMethod.POST)
						.addLink(url + "/" + gid, "self", RequestMethod.DELETE)
						.addLink(url + "/" + gid, "self", RequestMethod.PUT).wrap(graph));
	}

	/**
	 * @param gid it is the id of the graph to delete
	 */
	@Operation(tags = "version 1 - graphs", summary = "Delete a graph", description = "All the nested resources are deleted accordingly. In case the graph is referred by a requirement resource, the operation is not performed.")
	@RequestMapping(value = "/{gid}", method = RequestMethod.DELETE)
	@ApiResponses(value = { @ApiResponse(responseCode = "204", description = "No Content"),
			@ApiResponse(responseCode = "404", description = "The graph doesn't exist at all. You can retry the operation or refer to another graph."),
			@ApiResponse(responseCode = "409", description = "The graph could not be deleted because it is referenced by a requirement resource; you can first delete the interested requirement.")})
	@ResponseStatus(value = HttpStatus.NO_CONTENT)

	public ResponseEntity<Resources<Void>> deleteGraph(@PathVariable("gid") long gid) {
		// Graph graph = service.deleteGraph(gid);
		// if (graph == null)
		// throw new ResponseStatusException(
		// HttpStatus.NOT_FOUND, "not found"
		// );
		String url = request.getRequestURL().substring(0, request.getRequestURL().lastIndexOf("/"));
		return ResponseEntity.status(HttpStatus.OK).body(
				// wrap the response with the hyperlinks
				new ResourceWrapperWithLinks<Void>().addLink(url, "new", RequestMethod.POST)
						.addLink(url, "list", RequestMethod.GET).wrap(null));
	}

	/*
	 * Nodes
	 */

	/**
	 * @param gid  it is the id of the graph
	 * @param nid  it is the name of the node to create
	 * @param node it is the new node to create
	 * @return the created node
	 */
	@Operation(tags = "version 1 - graphs", summary = "Create a node in a graph", description = "The neighbours are considered too.")
	@RequestMapping(value = "/{gid}/nodes", method = RequestMethod.POST)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Created"),
			@ApiResponse(responseCode = "400", description = "The node is semantically malformed. You can retry the operation or check the node."),
			@ApiResponse(responseCode = "404", description = "The graph doesn't exist at all. You can retry the operation or refer to another graph.")
		})
	public ResponseEntity<Resources<Integer>> createNode(@PathVariable("gid") long gid,
			@RequestParam(name = "nid") String nid, @RequestBody Node node) {
		// if(nid == null)
		// throw new ResponseStatusException(
		// HttpStatus.BAD_REQUEST, "bad request"
		// );
		// StringBuffer url = request.getRequestURL();
		// Node created = service.createNode(gid, node, nid);
		// if (created != null) {
		// if(created.getName() == null)
		// throw new ResponseStatusException(
		// HttpStatus.CONFLICT, "conflict"
		// );
		// String responseUrl;
		// if(url.toString().endsWith("/")) responseUrl = url.toString() + nid;
		// else responseUrl = url.toString() + "/" + nid;
		// HttpHeaders responseHeaders = new HttpHeaders();
		// try {
		// responseHeaders.setLocation(new URI(responseUrl));
		// } catch (URISyntaxException e) {
		// throw new ResponseStatusException(
		// HttpStatus.BAD_REQUEST, "bad request"
		// );
		// }
		// return new ResponseEntity<Node>(created, responseHeaders,
		// HttpStatus.CREATED);
		// } else
		// throw new ResponseStatusException(
		// HttpStatus.BAD_REQUEST, "bad request"
		// );
		Integer nodeId = 0;
		String url = request.getRequestURL().substring(0, request.getRequestURL().lastIndexOf("/")).substring(0,
				request.getRequestURL().lastIndexOf("/"));
		return ResponseEntity.status(HttpStatus.CREATED).body(
				// wrap the response with the hyperlinks
				new ResourceWrapperWithLinks<Integer>().addLink(url + "/" + gid + "/nodes", "new", RequestMethod.POST)
						.addLink(url + "/" + gid + "/nodes" + "/" + nodeId, "self", RequestMethod.DELETE)
						.addLink(url + "/" + gid + "/nodes" + "/" + nodeId, "self", RequestMethod.PUT)
						.addLink(url + "/" + gid + "/nodes" + "/" + nodeId, "self", RequestMethod.GET)
						.addLink(url, "list", RequestMethod.GET).wrap(nodeId));
	}

	/* Node */

	/**
	 * @param gid  it is the id of the graph
	 * @param nid  it is the name of the node to update
	 * @param node it is the new value of the node
	 */
	@Operation(tags = "version 1 - graphs", summary = "Update a node in a graph", description = "All the nested resources are replaced in a shallow way, besides the neighbours; that field is totally neglected.")
	@RequestMapping(value = "/{gid}/nodes/{nid}", method = RequestMethod.PUT)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "400", description = "The node is semantically malformed. You can retry the operation or check the node."),
			@ApiResponse(responseCode = "404", description = "The graph or the node doesn't exist at all. You can retry the operation or refer to another graph/node.")
		})
	@ResponseStatus(value = HttpStatus.NO_CONTENT)

	public ResponseEntity<Resources<Void>> updateNode(@PathVariable("gid") long gid, @PathVariable("nid") String nid,
			@RequestBody Node node) {
		// Node updated = service.updateNode(gid, nid, node);
		// if (updated == null)
		// throw new ResponseStatusException(
		// HttpStatus.NOT_FOUND, "not found"
		// );
		// else if (updated.getName() == null)
		// throw new ResponseStatusException(
		// HttpStatus.BAD_REQUEST, "bad request"
		// );

		String url = request.getRequestURL().substring(0, request.getRequestURL().lastIndexOf("/"))
				.substring(0, request.getRequestURL().lastIndexOf("/"))
				.substring(0, request.getRequestURL().lastIndexOf("/"));
		return ResponseEntity.status(HttpStatus.CREATED).body(
				// wrap the response with the hyperlinks
				new ResourceWrapperWithLinks<Void>().addLink(url + "/" + gid + "/nodes", "new", RequestMethod.POST)
						.addLink(url + "/" + gid + "/nodes" + "/" + nid, "self", RequestMethod.DELETE)
						.addLink(url + "/" + gid + "/nodes" + "/" + nid, "self", RequestMethod.PUT)
						.addLink(url + "/" + gid + "/nodes" + "/" + nid, "self", RequestMethod.GET)
						.addLink(url + "/" + gid + "/nodes", "list", RequestMethod.GET)
						.addLink(url + "/" + gid, "list", RequestMethod.GET).addLink(url, "list", RequestMethod.GET)
						.wrap(null));
	}

	/**
	 * @param gid it is the id of the graph
	 * @param nid it is the id of the node to retrieve
	 * @return the node with nid name
	 */
	@Operation(tags = "version 1 - graphs", summary = "Get a node", description = "")
	@RequestMapping(value = "/{gid}/nodes/{nid}", method = RequestMethod.GET)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "404", description = "The graph or the node doesn't exist at all. You can retry the operation or refer to another graph/node.")
		})

	public ResponseEntity<Resources<Node>> getNode(@PathVariable("gid") long gid, @PathVariable("nid") String nid) {
		// Node node = service.getNode(gid, nid);
		// if (node == null)
		// throw new ResponseStatusException(
		// HttpStatus.NOT_FOUND, "not found"
		// );
		// return node;

		Node node = null;
		String url = request.getRequestURL().substring(0, request.getRequestURL().lastIndexOf("/"))
				.substring(0, request.getRequestURL().lastIndexOf("/"))
				.substring(0, request.getRequestURL().lastIndexOf("/"));
		return ResponseEntity.status(HttpStatus.CREATED).body(
				// wrap the response with the hyperlinks
				new ResourceWrapperWithLinks<Node>()
						.addLink(url + "/" + gid + "/nodes", "new", RequestMethod.POST)
						.addLink(url + "/" + gid + "/nodes" + "/" + nid, "self", RequestMethod.DELETE)
						.addLink(url + "/" + gid + "/nodes" + "/" + nid, "self", RequestMethod.PUT)
						.addLink(url + "/" + gid + "/nodes" + "/" + nid, "self", RequestMethod.GET)
						.addLink(url + "/" + gid + "/nodes", "list", RequestMethod.GET)
						.addLink(url + "/" + gid, "list", RequestMethod.GET).addLink(url, "list", RequestMethod.GET)
						.wrap(node));
	}

	/**
	 * @param gid it is the id of the graph
	 * @param nid it is the name of the node to delete
	 */
	@Operation(tags = "version 1 - graphs", summary = "Delete a node", description = "")
	@RequestMapping(value = "/{gid}/nodes/{nid}", method = RequestMethod.DELETE)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "204", description = "No Content"),
			@ApiResponse(responseCode = "404", description = "The graph or the node doesn't exist at all. You can retry the operation or refer to another graph/node.")
			@ApiResponse(responseCode = "409", description = "The node could not have been deleted, because it is linked to other nodes; first delete the interested connections.")
		})
	@ResponseStatus(value = HttpStatus.NO_CONTENT)

	public ResponseEntity<Resources<Void>> deleteNode(@PathVariable("gid") long gid, @PathVariable("nid") String nid) {
		// Node node = service.deleteNode(gid, nid);
		// if ( node == null)
		// throw new ResponseStatusException(
		// HttpStatus.NOT_FOUND, "not found"
		// );

		String url = request.getRequestURL().substring(0, request.getRequestURL().lastIndexOf("/"))
				.substring(0, request.getRequestURL().lastIndexOf("/"))
				.substring(0, request.getRequestURL().lastIndexOf("/"));
		return ResponseEntity.status(HttpStatus.CREATED).body(
				// wrap the response with the hyperlinks
				new ResourceWrapperWithLinks<Void>().addLink(url + "/" + gid + "/nodes", "new", RequestMethod.POST)
						.addLink(url + "/" + gid + "/nodes", "list", RequestMethod.GET)
						.addLink(url + "/" + gid, "list", RequestMethod.GET).addLink(url, "list", RequestMethod.GET)
						.wrap(null));
	}

	/*
	 * Neighbours
	 */

	/**
	 * @param gid       it is the id of the graph
	 * @param nid       it is the name of the node
	 * @param neighbour it is the new neighbour to add to the node
	 */
	@Operation(tags = "version 1 - graphs", summary = "Add a neighbour to a node", description = "add a new neighbour")
	@RequestMapping(value = "/{gid}/nodes/{nid}/neighbours", method = RequestMethod.POST)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "204", description = "No Content"),
			@ApiResponse(responseCode = "404", description = "The graph or the node doesn't exist at all. You can retry the operation or refer to another graph/node."),
			@ApiResponse(responseCode = "409", description = "")
		})
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	public ResponseEntity<Resources<Void>> addNeighbour(@PathVariable("gid") long gid, @PathVariable("nid") String nid,
			@RequestBody Neighbour neighbour) {
		// Neighbour added = service.addNeighbour(gid, nid, neighbour);
		// if(added == null)
		// throw new ResponseStatusException(
		// HttpStatus.NOT_FOUND, "not found"
		// );
		// else if(added.getName() == null)
		// throw new ResponseStatusException(
		// HttpStatus.CONFLICT, "conflict"
		// );
		String url = request.getRequestURL().substring(0, request.getRequestURL().lastIndexOf("/"))
				.substring(0, request.getRequestURL().lastIndexOf("/"))
				.substring(0, request.getRequestURL().lastIndexOf("/"))
				.substring(0, request.getRequestURL().lastIndexOf("/"));
		return ResponseEntity.status(HttpStatus.CREATED).body(
				// wrap the response with the hyperlinks
				new ResourceWrapperWithLinks<Void>()
						.addLink(url + "/" + gid + "/nodes" + "/" + nid + "/neighbours", "new", RequestMethod.POST)
						.addLink(url + "/" + gid + "/nodes" + "/" + nid + "/neighbours" + "?neighbour=" + neighbour.getName(), "self", RequestMethod.DELETE)
						.addLink(url + "/" + gid + "/nodes" + "/" + nid, "self", RequestMethod.GET)
						.addLink(url + "/" + gid + "/nodes", "list", RequestMethod.GET)
						.addLink(url + "/" + gid, "list", RequestMethod.GET)
						.addLink(url, "list", RequestMethod.GET)
						.wrap(null));
	}

	/**
	 * @param gid       it is the id of the graph
	 * @param nid       it is the name of the node
	 * @param neighbour it is the neighbour to remove from the node
	 */
	@Operation(tags = "version 1 - graphs", summary = "Delete a neighbour of a node", description = "delete a neighbour")
	@RequestMapping(value = "/{gid}/nodes/{nid}/neighbours", method = RequestMethod.DELETE)
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "No Content"),
		@ApiResponse(responseCode = "404", description = "The graph, the initial node or the incident node doesn't exist at all. You can retry the operation or refer to another graph/node.")
	})


	public ResponseEntity<Resources<Void>> deleteNeighbour(@PathVariable("gid") long gid, @PathVariable("nid") String nid,
			@RequestParam(name = "neighbour") String neighbour) {

		// if (neighbour == null)
		// 	throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bad request");

		// Neighbour removed = service.deleteNeighbour(gid, nid, neighbour);
		// if (removed == null)
		// 	throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
		// else if (removed.getName() == null)
		// 	throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bad request");

		String url = request.getRequestURL().substring(0, request.getRequestURL().lastIndexOf("/"))
				.substring(0, request.getRequestURL().lastIndexOf("/"))
				.substring(0, request.getRequestURL().lastIndexOf("/"))
				.substring(0, request.getRequestURL().lastIndexOf("/"));
		return ResponseEntity.status(HttpStatus.CREATED).body(
				// wrap the response with the hyperlinks
				new ResourceWrapperWithLinks<Void>()
						.addLink(url + "/" + gid + "/nodes" + "/" + nid + "/neighbours", "new", RequestMethod.POST)
						.addLink(url + "/" + gid + "/nodes" + "/" + nid, "self", RequestMethod.GET)
						.addLink(url + "/" + gid + "/nodes", "list", RequestMethod.GET)
						.addLink(url + "/" + gid, "list", RequestMethod.GET)
						.addLink(url, "list", RequestMethod.GET)
						.wrap(null));
	}

	/* Configuration */

	/**
	 * @param gid it is the id of the graph
	 * @param nid it is the name of the node
	 */
	@Operation(tags = "version 1 - graphs", summary = "Get the configuration of a node", description = "")
	@RequestMapping(value = "/{gid}/nodes/{nid}/configuration", method = RequestMethod.GET)
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "No Content"),
		@ApiResponse(responseCode = "404", description = "The graph, the node or the configuration doesn't exist at all. You can retry the operation or refer to another graph/node.")
	})

	public ResponseEntity<Resources<Configuration>> getConfiguration(@PathVariable("gid") long gid, @PathVariable("nid") String nid) {

		// Configuration configuration = service.getConfiguration(gid, nid);
		// if (configuration == null)
		// 	throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
		// return configuration;
		Configuration configuration = null;
		String url = request.getRequestURL().substring(0, request.getRequestURL().lastIndexOf("/"))
				.substring(0, request.getRequestURL().lastIndexOf("/"))
				.substring(0, request.getRequestURL().lastIndexOf("/"))
				.substring(0, request.getRequestURL().lastIndexOf("/"));
		return ResponseEntity.status(HttpStatus.CREATED).body(
				// wrap the response with the hyperlinks
				new ResourceWrapperWithLinks<Configuration>()
						.addLink(url + "/" + gid + "/nodes" + "/" + nid + "/configuration", "self", RequestMethod.PUT)
						.addLink(url + "/" + gid + "/nodes" + "/" + nid + "/configuration", "self", RequestMethod.GET)
						.addLink(url + "/" + gid + "/nodes" + "/" + nid, "self", RequestMethod.GET)
						.addLink(url + "/" + gid + "/nodes", "list", RequestMethod.GET)
						.addLink(url + "/" + gid, "list", RequestMethod.GET)
						.addLink(url, "list", RequestMethod.GET)
						.wrap(configuration));
	}

	/**
	 * @param gid           it is the id of the graph
	 * @param nid           it is the name of the node
	 * @param configuration it is the new configuration
	 */
	@Operation(tags = "version 1 - graphs", summary = "Update the configuration of a node", description = "")
	@RequestMapping(value = "/{gid}/nodes/{nid}/configuration", method = RequestMethod.PUT)
	@ApiResponses(value = {
		@ApiResponse(responseCode = "204", description = "No Content"),
		@ApiResponse(responseCode = "404", description = "The graph, the node or the configuration doesn't exist at all. You can retry the operation or refer to another graph/node.")
	})
	@ResponseStatus(value = HttpStatus.NO_CONTENT)

	public ResponseEntity<Resources<Void>> updateConfiguration(@PathVariable("gid") long gid, @PathVariable("nid") String nid,
			@RequestBody Configuration configuration) {

		// Configuration updated = service.updateConfiguration(gid, nid, configuration);
		// if (updated == null)
		// 	throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
		String url = request.getRequestURL().substring(0, request.getRequestURL().lastIndexOf("/"))
				.substring(0, request.getRequestURL().lastIndexOf("/"))
				.substring(0, request.getRequestURL().lastIndexOf("/"))
				.substring(0, request.getRequestURL().lastIndexOf("/"));
		return ResponseEntity.status(HttpStatus.OK).body(
				// wrap the response with the hyperlinks
				new ResourceWrapperWithLinks<Void>()
						.addLink(url + "/" + gid + "/nodes" + "/" + nid + "/configuration", "self", RequestMethod.PUT)
						.addLink(url + "/" + gid + "/nodes" + "/" + nid + "/configuration", "self", RequestMethod.GET)
						.addLink(url + "/" + gid + "/nodes" + "/" + nid, "self", RequestMethod.GET)
						.addLink(url + "/" + gid + "/nodes", "list", RequestMethod.GET)
						.addLink(url + "/" + gid, "list", RequestMethod.GET)
						.addLink(url, "list", RequestMethod.GET)
						.wrap(null));
	}

	// /**
	//  * @param gid it is the id of the graph
	//  * @param nid it is the name of the node whose configuration must be deleted
	//  */
	// @Operation(tags = "version 1 - graphs", summary = "deleteConfiguration", description = "delete the configuration of a node")
	// @RequestMapping(value = "/{gid}/nodes/{nid}/configuration", method = RequestMethod.DELETE)
	// @ApiResponses(value = { @ApiResponse(responseCode = "204", description = "No Content"),
	// 		@ApiResponse(responseCode = "404", description = "Not Found") })
	// @ResponseStatus(value = HttpStatus.NO_CONTENT)

	// public void deleteConfiguration(@PathVariable("gid") long gid, @PathVariable("nid") String nid) {

	// 	Configuration deleted = service.deleteConfiguration(gid, nid);
	// 	if (deleted == null)
	// 		throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
	// }

	/* Constraints */

	/**
	 * @param gid         it is the id of the graph
	 * @param constraints they are the constraints to add to the graph
	 * @return the created constraints
	 */
	@Operation(tags = "version 1 - graphs", summary = "Create a set of constraints in a graph", description = "If the constraints already exists, the new ones replace them in a shallow way.")
	@RequestMapping(value = "/{gid}/constraints", method = RequestMethod.POST)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Created"),
			@ApiResponse(responseCode = "400", description = "The passed constraints are semantically malformed. You can retry the operation or check them."),
			@ApiResponse(responseCode = "404", description = "The graph doesn't exist at all. You can retry the operation or refer to another graph.")
		})
	public ResponseEntity<Resources<Void>> createConstraints(@PathVariable("gid") long gid, @RequestBody Constraints constraints) {
		// if(constraints.getLinkConstraints().getLinkMetrics().isEmpty() &&
		// constraints.getNodeConstraints().getNodeMetrics().isEmpty())
		// throw new ResponseStatusException(
		// HttpStatus.BAD_REQUEST, "bad request"
		// );
		// StringBuffer url = request.getRequestURL();
		// String responseUrl;
		// if(url.toString().endsWith("/")) responseUrl = url.toString() +
		// "constraints";
		// else responseUrl = url.toString() + "/constraints";
		// Constraints created = service.createConstraints(gid, constraints);
		// if (created != null) {
		// if(created.getLinkConstraints() == null && created.getNodeConstraints() ==
		// null)
		// throw new ResponseStatusException(
		// HttpStatus.CONFLICT, "conflict"
		// );
		// HttpHeaders responseHeaders = new HttpHeaders();
		// try {
		// responseHeaders.setLocation(new URI(responseUrl));
		// } catch (URISyntaxException e) {
		// throw new ResponseStatusException(
		// HttpStatus.BAD_REQUEST, "bad request"
		// );
		// }
		// return new ResponseEntity<Constraints>(created, responseHeaders,
		// HttpStatus.CREATED);
		// } else
		// throw new ResponseStatusException(
		// HttpStatus.BAD_REQUEST, "bad request"
		// );
		String url = request.getRequestURL().substring(0, request.getRequestURL().lastIndexOf("/"))
				.substring(0, request.getRequestURL().lastIndexOf("/"));
		return ResponseEntity.status(HttpStatus.OK).body(
				// wrap the response with the hyperlinks
				new ResourceWrapperWithLinks<Void>()
						.addLink(url + "/" + gid + "/constraints", "new", RequestMethod.POST)
						.addLink(url + "/" + gid + "/constraints", "self", RequestMethod.PUT)
						.addLink(url + "/" + gid + "/constraints", "self", RequestMethod.GET)
						.addLink(url + "/" + gid + "/constraints", "self", RequestMethod.DELETE)
						.addLink(url + "/" + gid, "list", RequestMethod.GET)
						.addLink(url, "list", RequestMethod.GET)
						.wrap(null));
	}

	/**
	 * @param gid         it is the id of the graph
	 * @param constraints they are the new constraints to update
	 */
	@Operation(tags = "version 1 - graphs", summary = "Update constraints of a graph", description = "update the constraints of a graph")
	@RequestMapping(value = "/{gid}/constraints", method = RequestMethod.PUT)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "400", description = "The passed constraints are semantically malformed. You can retry the operation or check them."),
			@ApiResponse(responseCode = "404", description = "The graph doesn't exist at all. You can retry the operation or refer to another graph.")
		})
	@ResponseStatus(value = HttpStatus.NO_CONTENT)

	public ResponseEntity<Resources<Void>> updateConstraints(@PathVariable("gid") long gid, @RequestBody Constraints constraints) {
		// if (constraints.getLinkConstraints().getLinkMetrics().isEmpty()
		// 		&& constraints.getNodeConstraints().getNodeMetrics().isEmpty())
		// 	throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bad request");
		// ;
		// Constraints updated = service.updateConstraints(gid, constraints);
		// if (updated == null)
		// 	throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
		// else if (updated.getLinkConstraints().getLinkMetrics().isEmpty()
		// 		&& updated.getNodeConstraints().getNodeMetrics().isEmpty())
		// 	throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bad request");
		String url = request.getRequestURL().substring(0, request.getRequestURL().lastIndexOf("/"))
				.substring(0, request.getRequestURL().lastIndexOf("/"));
		return ResponseEntity.status(HttpStatus.OK).body(
				// wrap the response with the hyperlinks
				new ResourceWrapperWithLinks<Void>()
						.addLink(url + "/" + gid + "/constraints", "new", RequestMethod.POST)
						.addLink(url + "/" + gid + "/constraints", "self", RequestMethod.PUT)
						.addLink(url + "/" + gid + "/constraints", "self", RequestMethod.GET)
						.addLink(url + "/" + gid + "/constraints", "self", RequestMethod.DELETE)
						.addLink(url + "/" + gid, "list", RequestMethod.GET)
						.addLink(url, "list", RequestMethod.GET)
						.wrap(null));
	}

	/**
	 * @param gid it is the id of the graph
	 * @return the constraints of the graph
	 */
	@Operation(tags = "version 1 - graphs", summary = "Get the constraints of a graph", description = "")
	@RequestMapping(value = "/{gid}/constraints", method = RequestMethod.GET)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "404", description = "The graph doesn't exist at all. You can retry the operation or refer to another graph.")
		})

	public ResponseEntity<Resources<Constraints>> getConstraints(@PathVariable("gid") long gid) {
		// Constraints constraints = service.getConstraints(gid);
		// if (constraints == null)
		// 	throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
		// return constraints;
		Constraints constraints = null;
		String url = request.getRequestURL().substring(0, request.getRequestURL().lastIndexOf("/"))
				.substring(0, request.getRequestURL().lastIndexOf("/"));
		return ResponseEntity.status(HttpStatus.OK).body(
				// wrap the response with the hyperlinks
				new ResourceWrapperWithLinks<Constraints>()
						.addLink(url + "/" + gid + "/constraints", "new", RequestMethod.POST)
						.addLink(url + "/" + gid + "/constraints", "self", RequestMethod.PUT)
						.addLink(url + "/" + gid + "/constraints", "self", RequestMethod.GET)
						.addLink(url + "/" + gid + "/constraints", "self", RequestMethod.DELETE)
						.addLink(url + "/" + gid, "list", RequestMethod.GET)
						.addLink(url, "list", RequestMethod.GET)
						.wrap(constraints));
	}

	/**
	 * @param gid it is the id of the graph whose constraints must be removed
	 */
	@Operation(tags = "version 1 - graphs", summary = "Delete all the constraints of a graph", description = "")
	@RequestMapping(value = "/{gid}/constraints", method = RequestMethod.DELETE)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "404", description = "The graph or the constraint set doesn't exist at all. You can retry the operation or refer to another graph.")

		})

	public ResponseEntity<Resources<Void>> deleteConstraints(@PathVariable("gid") long gid) {
		// Constraints deleted = service.deleteConstraints(gid);
		// if (deleted == null)
		// 	throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
		String url = request.getRequestURL().substring(0, request.getRequestURL().lastIndexOf("/"))
				.substring(0, request.getRequestURL().lastIndexOf("/"));
		return ResponseEntity.status(HttpStatus.OK).body(
				// wrap the response with the hyperlinks
				new ResourceWrapperWithLinks<Void>()
						.addLink(url + "/" + gid + "/constraints", "new", RequestMethod.POST)
						.addLink(url + "/" + gid + "/constraints", "self", RequestMethod.PUT)
						.addLink(url + "/" + gid + "/constraints", "self", RequestMethod.GET)
						.addLink(url + "/" + gid + "/constraints", "self", RequestMethod.DELETE)
						.addLink(url + "/" + gid, "list", RequestMethod.GET)
						.addLink(url, "list", RequestMethod.GET)
						.wrap(null));
	}

}
