package it.polito.verefoo.rest.spring.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.polito.verefoo.jaxb.Configuration;
import it.polito.verefoo.jaxb.Constraints;
import it.polito.verefoo.jaxb.Graph;
import it.polito.verefoo.jaxb.Graphs;
import it.polito.verefoo.jaxb.Neighbour;
import it.polito.verefoo.jaxb.Node;
import it.polito.verefoo.rest.spring.ResourceWrapperWithLinks;
import it.polito.verefoo.rest.spring.service.GraphService;

@RestController
@RequestMapping(value = "/adp/graphs", produces = {"application/xml", "application/json" })
@ApiResponses(value = {
	@ApiResponse(responseCode = "400", description = "The provided resource is not compliant with the data model.")
})
public class GraphsController {

	@Autowired
	GraphService service;

	@Autowired
	private HttpServletRequest request;



	@Operation(tags = "graphs", summary = "Create a graph", description = "The graph's id in the request, if provided, is neglected; instead, the system automatically generates, stores the id for all the graphs and puts them in the response.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "Created"),
	})

	@RequestMapping(value = "", consumes = { "application/xml", "application/json" }, method = RequestMethod.POST)
	public ResponseEntity<Resources<List<Long>>> createGraphs(@RequestBody Graphs graphs) {

		List<Long> graphIds = service.createGraphs(graphs);

		String url = request.getRequestURL().toString();
		return ResponseEntity.status(HttpStatus.CREATED).body(
				// wrap the response with the hyperlinks
				new ResourceWrapperWithLinks<List<Long>>()
						.addLink(url + "/" + graphIds.get(0), "first", RequestMethod.GET)
						.addLink(url + "/" + graphIds.get(0), "first", RequestMethod.PUT)
						.addLink(url, "collection", RequestMethod.GET)
						.addLink(url, "collection", RequestMethod.DELETE)
						.addLink(url, "collection", RequestMethod.POST)
						.addLink(url + "/" + graphIds.get(0), "first", RequestMethod.DELETE).wrap(graphIds));
	}



	@Operation(tags = "graphs", summary = "Get all the graphs", description = "")
	@ApiResponses(value = { 
		@ApiResponse(responseCode = "200", description = ""),
		@ApiResponse(responseCode = "404", description = "No graphs currently exist in the workspace. You can start by creating a new graph") })

	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseEntity<Resources<Graphs>> getGraphs() {

		Graphs graphs = service.getGraphs();

		String url = request.getRequestURL().toString();
		return ResponseEntity.status(HttpStatus.OK).body(
				// wrap the response with the hyperlinks
				new ResourceWrapperWithLinks<Graphs>().addLink(url, "collection", RequestMethod.POST)
						.addLink(url, "self", RequestMethod.DELETE).addLink(url, "self", RequestMethod.GET)
						.wrap(graphs));
	}



	@Operation(tags = "graphs", summary = "Delete all the graphs", description = "Be careful before cleaning the whole workbench of graphs.")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = ""),
			@ApiResponse(responseCode = "304", description = "No graph exists at all in the workspace."),
			@ApiResponse(responseCode = "424", description = "No graph has been deleted because at least one of them is referred by other resources; you must first delete the interested resources.") })

	@RequestMapping(value = "", method = RequestMethod.DELETE)
	public ResponseEntity<Resources<Void>> deleteGraphs() {

		service.deleteGraphs();

		String url = request.getRequestURL().toString();
		return ResponseEntity.status(HttpStatus.OK).body(
				// wrap the response with the hyperlinks
				new ResourceWrapperWithLinks<Void>().addLink(url, "collection", RequestMethod.POST).wrap(null));
	}



	@Operation(tags = "graphs", summary = "Update a graph", description = "It's advisable to explicitly perform a get of the modified resource through the pertinent API, since some ids may have been changed.")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = ""),
			@ApiResponse(responseCode = "404", description = "The graph or one of its sub-resources doesn't exist. Check the ids of the submitted resources."), })

	@RequestMapping(value = "/{gid}", consumes = { "application/xml", "application/json" }, method = RequestMethod.PUT)
	public ResponseEntity<Resources<Void>> updateGraph(@PathVariable("gid") Long gid, @RequestBody Graph graph) {

		service.updateGraph(gid, graph);

		String url = request.getRequestURL().toString();
		url = url.substring(0, url.lastIndexOf("/"));
		return ResponseEntity.status(HttpStatus.OK).body(
				// wrap the response with the hyperlinks
				new ResourceWrapperWithLinks<Void>().addLink(url + "/" + gid, "self", RequestMethod.GET)
						.addLink(url, "collection", RequestMethod.POST).addLink(url + "/" + gid, "self", RequestMethod.DELETE)
						.addLink(url + "/" + gid, "self", RequestMethod.PUT).wrap(null));
	}



	@Operation(tags = "graphs", summary = "Get a graph", description = "")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = ""),
			@ApiResponse(responseCode = "404", description = "The graph doesn't exist in the workspace."), })

	@RequestMapping(value = "/{gid}", method = RequestMethod.GET)
	public ResponseEntity<Resources<Graph>> getGraph(@PathVariable("gid") long gid) throws Exception {

		Graph graph = service.getGraph(gid);

		String url = request.getRequestURL().toString();
		url = url.substring(0, url.lastIndexOf("/"));
		return ResponseEntity.status(HttpStatus.OK).body(
				// wrap the response with the hyperlinks
				new ResourceWrapperWithLinks<Graph>()
						.addLink(url + "/" + gid, "self", RequestMethod.GET)
						.addLink(url, "collection", RequestMethod.POST)
						.addLink(url + "/" + gid, "self", RequestMethod.DELETE)
						.addLink(url + "/" + gid, "self", RequestMethod.PUT).wrap(graph));
	}


	
	@Operation(tags = "graphs", summary = "Delete a graph", description = "All the nested resources are deleted accordingly. In case the graph is referred by other resources, the operation is not performed and the workspace is not modified.")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = ""),
			@ApiResponse(responseCode = "404", description = "The graph doesn't exist at all in the workspace."),
			@ApiResponse(responseCode = "409", description = "The graph could not be deleted because it is referenced by other resources; you must first delete the interested resources.")})

	@RequestMapping(value = "/{gid}", method = RequestMethod.DELETE)
	public ResponseEntity<Resources<Void>> deleteGraph(@PathVariable("gid") Long gid) {

		service.deleteGraph(gid);

		String url = request.getRequestURL().toString();
		url = url.substring(0, url.lastIndexOf("/"));
		return ResponseEntity.status(HttpStatus.OK).body(
				// wrap the response with the hyperlinks
				new ResourceWrapperWithLinks<Void>().addLink(url, "collection", RequestMethod.POST)
						.addLink(url, "collection", RequestMethod.GET).wrap(null));
	}



	@Operation(tags = "graphs", summary = "Create a node in a graph", description = "The neighbours and the configuration are considered too eventually.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "Created"),
		@ApiResponse(responseCode = "404", description = "The graph doesn't exist at all.")
	})

	@RequestMapping(value = "/{gid}/nodes", consumes = { "application/xml", "application/json" }, method = RequestMethod.POST)
	public ResponseEntity<Resources<Long>> createNode(@PathVariable("gid") Long gid, @RequestBody Node node) {

		Long nodeId = service.createNode(gid, node);

		String url = request.getRequestURL().toString();
		url = url.substring(0, url.lastIndexOf("/"));
		url = url.substring(0, url.lastIndexOf("/"));
		return ResponseEntity.status(HttpStatus.CREATED).body(
				// wrap the response with the hyperlinks
				new ResourceWrapperWithLinks<Long>().addLink(url + "/" + gid + "/nodes", "collection", RequestMethod.POST)
						.addLink(url + "/" + gid + "/nodes" + "/" + nodeId, "self", RequestMethod.DELETE)
						.addLink(url + "/" + gid + "/nodes" + "/" + nodeId, "self", RequestMethod.PUT)
						.addLink(url + "/" + gid + "/nodes" + "/" + nodeId, "self", RequestMethod.GET)
						.addLink(url, "collection", RequestMethod.GET).wrap(nodeId));
	}


	
	@Operation(tags = "graphs", summary = "Update a node in a graph", description = "All the nested resources are replaced in a shallow way, besides the neighbours; that field is totally neglected.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = ""),
		@ApiResponse(responseCode = "404", description = "The graph or the node doesn't exist at all.")
	})

	@RequestMapping(value = "/{gid}/nodes/{nid}", consumes = { "application/xml", "application/json" }, method = RequestMethod.PUT)
	public ResponseEntity<Resources<Void>> updateNode(@PathVariable("gid") Long gid, @PathVariable("nid") Long nid, @RequestBody Node node) {

		service.updateNode(gid, nid, node);

		String url = request.getRequestURL().toString();
		url = url.substring(0, url.lastIndexOf("/"));
		url = url.substring(0, url.lastIndexOf("/"));
		return ResponseEntity.status(HttpStatus.OK).body(
				// wrap the response with the hyperlinks
				new ResourceWrapperWithLinks<Void>().addLink(url + "/" + gid + "/nodes", "collection", RequestMethod.POST)
						.addLink(url + "/" + gid + "/nodes" + "/" + nid, "self", RequestMethod.DELETE)
						.addLink(url + "/" + gid + "/nodes" + "/" + nid, "self", RequestMethod.PUT)
						.addLink(url + "/" + gid + "/nodes" + "/" + nid, "self", RequestMethod.GET)
						.addLink(url + "/" + gid + "/nodes", "collection", RequestMethod.GET)
						.addLink(url + "/" + gid, "collection", RequestMethod.GET).addLink(url, "collection", RequestMethod.GET)
						.wrap(null));
	}


	
	@Operation(tags = "graphs", summary = "Get a node", description = "")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = ""),
		@ApiResponse(responseCode = "404", description = "The graph or the node doesn't exist at all.")
	})

	@RequestMapping(value = "/{gid}/nodes/{nid}", method = RequestMethod.GET)
	public ResponseEntity<Resources<Node>> getNode(@PathVariable("gid") Long gid, @PathVariable("nid") Long nid) {

		Node node = service.getNode(gid, nid);

		String url = request.getRequestURL().toString();
		url = url.substring(0, url.lastIndexOf("/"));
		url = url.substring(0, url.lastIndexOf("/"));
		return ResponseEntity.status(HttpStatus.OK).body(
				// wrap the response with the hyperlinks
				new ResourceWrapperWithLinks<Node>()
						.addLink(url + "/" + gid + "/nodes", "collection", RequestMethod.POST)
						.addLink(url + "/" + gid + "/nodes" + "/" + nid, "self", RequestMethod.DELETE)
						.addLink(url + "/" + gid + "/nodes" + "/" + nid, "self", RequestMethod.PUT)
						.addLink(url + "/" + gid + "/nodes" + "/" + nid, "self", RequestMethod.GET)
						.addLink(url + "/" + gid + "/nodes", "collection", RequestMethod.GET)
						.addLink(url + "/" + gid, "collection", RequestMethod.GET).addLink(url, "collection", RequestMethod.GET)
						.wrap(node));
	}

	

	@Operation(tags = "graphs", summary = "Delete a node", description = "")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = ""),
		@ApiResponse(responseCode = "404", description = "The graph or the node doesn't exist at all."),
		@ApiResponse(responseCode = "409", description = "The node could not have been deleted, because it is referred by other resources; first delete the interested resources.")
	})

	@RequestMapping(value = "/{gid}/nodes/{nid}", method = RequestMethod.DELETE)
	public ResponseEntity<Resources<Void>> deleteNode(@PathVariable("gid") Long gid, @PathVariable("nid") Long nid) {

		service.deleteNode(gid, nid);

		String url = request.getRequestURL().toString();
		url = url.substring(0, url.lastIndexOf("/"));
		url = url.substring(0, url.lastIndexOf("/"));
		return ResponseEntity.status(HttpStatus.OK).body(
				// wrap the response with the hyperlinks
				new ResourceWrapperWithLinks<Void>().addLink(url + "/" + gid + "/nodes", "collection", RequestMethod.POST)
						.addLink(url + "/" + gid + "/nodes", "collection", RequestMethod.GET)
						.addLink(url + "/" + gid, "collection", RequestMethod.GET).addLink(url, "collection", RequestMethod.GET)
						.wrap(null));
	}



	@Operation(tags = "graphs", summary = "Add a neighbour to a node", description = "add a new neighbour")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "Created"),
		@ApiResponse(responseCode = "404", description = "The graph or the node doesn't exist at all.")
	})

	@RequestMapping(value = "/{gid}/nodes/{nid}/neighbours", consumes = { "application/xml", "application/json" }, method = RequestMethod.POST)
	public ResponseEntity<Resources<Long>> createNeighbour(@PathVariable("gid") Long gid, @PathVariable("nid") Long nid, @RequestBody Neighbour neighbour) {

		Long neighbourId = service.createNeighbour(gid, nid, neighbour);

		String url = request.getRequestURL().toString();
		url = url.substring(0, url.lastIndexOf("/"));
		url = url.substring(0, url.lastIndexOf("/"));
		url = url.substring(0, url.lastIndexOf("/"));
		return ResponseEntity.status(HttpStatus.CREATED).body(
				// wrap the response with the hyperlinks
				new ResourceWrapperWithLinks<Long>()
						.addLink(url + "/" + gid + "/nodes" + "/" + nid + "/neighbours", "collection", RequestMethod.POST)
						.addLink(url + "/" + gid + "/nodes" + "/" + nid + "/neighbours/" + neighbourId, "self", RequestMethod.DELETE)
						.addLink(url + "/" + gid + "/nodes" + "/" + nid, "self", RequestMethod.GET)
						.addLink(url + "/" + gid + "/nodes", "collection", RequestMethod.GET)
						.addLink(url + "/" + gid, "collection", RequestMethod.GET)
						.addLink(url, "collection", RequestMethod.GET)
						.wrap(neighbourId));
	}



	@Operation(tags = "graphs", summary = "Delete a neighbour of a node", description = "delete a neighbour")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = ""),
		@ApiResponse(responseCode = "404", description = "The graph, the node or its neighbour doesn't exist at all.")
	})

	@RequestMapping(value = "/{gid}/nodes/{noid}/neighbours/{neid}", method = RequestMethod.DELETE)
	public ResponseEntity<Resources<Void>> deleteNeighbour(@PathVariable("gid") Long gid, @PathVariable("noid") Long noid, @PathVariable("neid") Long neid) {

		service.deleteNeighbour(gid, noid, neid);

		String url = request.getRequestURL().toString();
		url = url.substring(0, url.lastIndexOf("/"));
		url = url.substring(0, url.lastIndexOf("/"));
		url = url.substring(0, url.lastIndexOf("/"));
		return ResponseEntity.status(HttpStatus.OK).body(
				// wrap the response with the hyperlinks
				new ResourceWrapperWithLinks<Void>()
						.addLink(url + "/" + gid + "/nodes" + "/" + noid + "/neighbours", "collection", RequestMethod.POST)
						.addLink(url + "/" + gid + "/nodes" + "/" + noid, "self", RequestMethod.GET)
						.addLink(url + "/" + gid + "/nodes", "collection", RequestMethod.GET)
						.addLink(url + "/" + gid, "collection", RequestMethod.GET)
						.addLink(url, "collection", RequestMethod.GET)
						.wrap(null));
	}



	@Operation(tags = "graphs", summary = "Get the configuration of a node", description = "")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = ""),
		@ApiResponse(responseCode = "404", description = "The graph or the node doesn't exist at all.")
	})

	@RequestMapping(value = "/{gid}/nodes/{nid}/configuration", method = RequestMethod.GET)
	public ResponseEntity<Resources<Configuration>> getConfiguration(@PathVariable("gid") Long gid, @PathVariable("nid") Long nid) {

		Configuration configuration = service.getConfiguration(gid, nid);

		String url = request.getRequestURL().toString();
		url = url.substring(0, url.lastIndexOf("/"));
		url = url.substring(0, url.lastIndexOf("/"));
		url = url.substring(0, url.lastIndexOf("/"));
		return ResponseEntity.status(HttpStatus.OK).body(
				// wrap the response with the hyperlinks
				new ResourceWrapperWithLinks<Configuration>()
						.addLink(url + "/" + gid + "/nodes" + "/" + nid + "/configuration", "self", RequestMethod.PUT)
						.addLink(url + "/" + gid + "/nodes" + "/" + nid + "/configuration", "self", RequestMethod.GET)
						.addLink(url + "/" + gid + "/nodes" + "/" + nid, "self", RequestMethod.GET)
						.addLink(url + "/" + gid + "/nodes", "collection", RequestMethod.GET)
						.addLink(url + "/" + gid, "collection", RequestMethod.GET)
						.addLink(url, "collection", RequestMethod.GET)
						.wrap(configuration));
	}



	@Operation(tags = "graphs", summary = "Update the configuration of a node", description = "")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = ""),
		@ApiResponse(responseCode = "404", description = "The graph or the node doesn't exist at all.")
	})

	@RequestMapping(value = "/{gid}/nodes/{nid}/configuration/{cid}", consumes = { "application/xml", "application/json" }, method = RequestMethod.PUT)
	public ResponseEntity<Resources<Void>> updateConfiguration(@PathVariable("gid") Long gid, @PathVariable("nid") Long nid, @PathVariable("cid") Long cid, @RequestBody Configuration configuration) {

		service.updateConfiguration(gid, nid, cid, configuration);

		String url = request.getRequestURL().toString();
		url = url.substring(0, url.lastIndexOf("/"));
		url = url.substring(0, url.lastIndexOf("/"));
		url = url.substring(0, url.lastIndexOf("/"));
		return ResponseEntity.status(HttpStatus.OK).body(
				// wrap the response with the hyperlinks
				new ResourceWrapperWithLinks<Void>()
						.addLink(url + "/" + gid + "/nodes" + "/" + nid + "/configuration/" + cid, "self", RequestMethod.PUT)
						.addLink(url + "/" + gid + "/nodes" + "/" + nid + "/configuration", "self", RequestMethod.GET)
						.addLink(url + "/" + gid + "/nodes" + "/" + nid, "self", RequestMethod.GET)
						.addLink(url + "/" + gid + "/nodes", "collection", RequestMethod.GET)
						.addLink(url + "/" + gid, "collection", RequestMethod.GET)
						.addLink(url, "collection", RequestMethod.GET)
						.wrap(null));
	}


	
	@Operation(tags = "graphs", summary = "Create a set of constraints in a graph", description = "If the constraints already exists, the new ones replace them in a shallow way.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Created"),
			@ApiResponse(responseCode = "404", description = "The graph doesn't exist at all.")
		})

	@RequestMapping(value = "/{gid}/constraints", consumes = { "application/xml", "application/json" }, method = RequestMethod.POST)
	public ResponseEntity<Resources<Void>> createConstraints(@PathVariable("gid") Long gid, @RequestBody Constraints constraints) {
		
		service.createConstraints(gid, constraints);

		String url = request.getRequestURL().toString();
		url = url.substring(0, url.lastIndexOf("/"));
		return ResponseEntity.status(HttpStatus.CREATED).body(
				// wrap the response with the hyperlinks
				new ResourceWrapperWithLinks<Void>()
						.addLink(url + "/" + gid + "/constraints", "collection", RequestMethod.POST)
						.addLink(url + "/" + gid + "/constraints", "self", RequestMethod.PUT)
						.addLink(url + "/" + gid + "/constraints", "self", RequestMethod.GET)
						.addLink(url + "/" + gid + "/constraints", "self", RequestMethod.DELETE)
						.addLink(url + "/" + gid, "collection", RequestMethod.GET)
						.addLink(url, "collection", RequestMethod.GET)
						.wrap(null));
	}


	

	@Operation(tags = "graphs", summary = "Update constraints of a graph", description = "update the constraints of a graph")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = ""),
			@ApiResponse(responseCode = "404", description = "The graph doesn't exist at all.")
		})

	@RequestMapping(value = "/{gid}/constraints", consumes = { "application/xml", "application/json" }, method = RequestMethod.PUT)
	public ResponseEntity<Resources<Void>> updateConstraints(@PathVariable("gid") Long gid, @RequestBody Constraints constraints) {
		
		service.updateConstraints(gid, constraints);

		String url = request.getRequestURL().toString();
		url = url.substring(0, url.lastIndexOf("/"));
		return ResponseEntity.status(HttpStatus.OK).body(
				// wrap the response with the hyperlinks
				new ResourceWrapperWithLinks<Void>()
						.addLink(url + "/" + gid + "/constraints", "collection", RequestMethod.POST)
						.addLink(url + "/" + gid + "/constraints", "self", RequestMethod.PUT)
						.addLink(url + "/" + gid + "/constraints", "self", RequestMethod.GET)
						.addLink(url + "/" + gid + "/constraints", "self", RequestMethod.DELETE)
						.addLink(url + "/" + gid, "collection", RequestMethod.GET)
						.addLink(url, "collection", RequestMethod.GET)
						.wrap(null));
	}



	@Operation(tags = "graphs", summary = "Get the constraints of a graph", description = "")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = ""),
			@ApiResponse(responseCode = "404", description = "The graph doesn't exist at all.")
		})

	@RequestMapping(value = "/{gid}/constraints", method = RequestMethod.GET)
	public ResponseEntity<Resources<Constraints>> getConstraints(@PathVariable("gid") Long gid) {

		Constraints constraints = service.getConstraints(gid);

		String url = request.getRequestURL().toString();
		url = url.substring(0, url.lastIndexOf("/"));
		return ResponseEntity.status(HttpStatus.OK).body(
				// wrap the response with the hyperlinks
				new ResourceWrapperWithLinks<Constraints>()
						.addLink(url + "/" + gid + "/constraints", "collection", RequestMethod.POST)
						.addLink(url + "/" + gid + "/constraints", "self", RequestMethod.PUT)
						.addLink(url + "/" + gid + "/constraints", "self", RequestMethod.GET)
						.addLink(url + "/" + gid + "/constraints", "self", RequestMethod.DELETE)
						.addLink(url + "/" + gid, "collection", RequestMethod.GET)
						.addLink(url, "collection", RequestMethod.GET)
						.wrap(constraints));
	}



	@Operation(tags = "graphs", summary = "Delete all the constraints of a graph", description = "")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = ""),
		@ApiResponse(responseCode = "404", description = "The graph doesn't exist at all.")
	})

	@RequestMapping(value = "/{gid}/constraints", method = RequestMethod.DELETE)
	public ResponseEntity<Resources<Void>> deleteConstraints(@PathVariable("gid") Long gid) {
		
		service.deleteConstraints(gid);

		String url = request.getRequestURL().toString();
		url = url.substring(0, url.lastIndexOf("/"));
		return ResponseEntity.status(HttpStatus.OK).body(
				// wrap the response with the hyperlinks
				new ResourceWrapperWithLinks<Void>()
						.addLink(url + "/" + gid + "/constraints", "collection", RequestMethod.POST)
						.addLink(url + "/" + gid + "/constraints", "self", RequestMethod.PUT)
						.addLink(url + "/" + gid + "/constraints", "self", RequestMethod.GET)
						.addLink(url + "/" + gid + "/constraints", "self", RequestMethod.DELETE)
						.addLink(url + "/" + gid, "collection", RequestMethod.GET)
						.addLink(url, "collection", RequestMethod.GET)
						.wrap(null));
	}

}
