package it.polito.verefoo.rest.spring;

import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.ClientErrorException;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.polito.verefoo.jaxb.*;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;


@Controller
@RequestMapping(value = "/adp/graphs")
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
    @ApiOperation(value = "createGraph", notes = "create a new graph"
	)
	@RequestMapping(value = "", method = RequestMethod.POST)
    @ApiResponses(value = {
    		@ApiResponse(code = 201, message = "Created"),
    		@ApiResponse(code = 400, message = "Bad Request"),
    		})
	public ResponseEntity<Graph> createGraph(@RequestBody Graph graph) {
		long id = service.getNextGraphId();
    	StringBuffer url = request.getRequestURL();
    	Graph created = service.createGraph(id, graph);
    	if (created!=null) {
    		String responseUrl;
    		if(url.toString().endsWith("/")) responseUrl = url.toString() + id;
    		else responseUrl = url.toString() + "/" + id;
    		created.setId(id);
    		HttpHeaders responseHeaders = new HttpHeaders();
    		try {
				responseHeaders.setLocation(new URI(responseUrl));
			} catch (URISyntaxException e) {
				throw new ResponseStatusException(
						  HttpStatus.BAD_REQUEST, "bad request"
						);
			}
        	return new ResponseEntity<Graph>(created, responseHeaders, HttpStatus.CREATED);
    	} else
    		throw new ResponseStatusException(
					  HttpStatus.BAD_REQUEST, "bad request"
					);
	}
    
    
    
    /**
	 * @param beforeInclusive it is the starting index
	 * @param afterInclusive it is the ending index
	 * @return a collection of graph
	 */
    @ApiOperation(value = "getGraphs", notes = "searches graphs"
	)
    @RequestMapping(value = "", method = RequestMethod.GET)
    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "OK"),
    		@ApiResponse(code = 404, message = "Not Found"),
    		})
    @ResponseBody
	public Graphs getGraphs( @RequestParam(name="beforeInclusive", defaultValue = "1") int beforeInclusive,
			  @RequestParam(name="afterInclusive", defaultValue = "10") int afterInclusive
			  			 ) {
				Graphs graphs = service.getGraphs(beforeInclusive, afterInclusive);
				if(graphs.getGraph().isEmpty())
					throw new ResponseStatusException(
							  HttpStatus.NOT_FOUND, "not found"
							);
				return graphs;
	}
    
    
    /**
	 * 
	 */
    @ApiOperation(value = "deleteGraphs", notes = "delete all the graphs"
    		)
	@RequestMapping(value = "", method = RequestMethod.DELETE)
    @ApiResponses(value = {
    		@ApiResponse(code = 204, message = "No Content"),
    		@ApiResponse(code = 404, message = "Not Found"),
    		})
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @ResponseBody
	public void deleteGraphs() {
		Graphs graphs = service.deleteGraphs();
		if (graphs == null)
			throw new ResponseStatusException(
					  HttpStatus.NOT_FOUND, "not found"
					);
	}
	
	/* 
	 * Graph 
	 */
    
    /**
	 * @param gid it is the id of the graph to update
	 * @param graph it is the new graph
	 */
    @ApiOperation(value = "updateGraph", notes = "update single graph"
	)
	@RequestMapping(value = "/{gid}", method = RequestMethod.PUT)
    @ApiResponses(value = {
    		@ApiResponse(code = 204, message = "No Content"),
    		@ApiResponse(code = 404, message = "Not Found"),
    		})
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @ResponseBody
	public void updateGraph(@PathVariable("gid") long gid, @RequestBody Graph graph) {
		Graph updated = service.updateGraph(gid, graph);
		if (updated == null)
			throw new ResponseStatusException(
					  HttpStatus.NOT_FOUND, "not found"
					);
	}
    
    
    /**
	 * @param gid it is the id of the graph to retrieve
	 */
    @ApiOperation(value = "getGraph", notes = "retrieve single graph"
	)
    @RequestMapping(value = "/{gid}", method = RequestMethod.GET)
    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "OK"),
    		@ApiResponse(code = 404, message = "Not Found"),
    		})
    @ResponseBody
	public Graph getGraph(@PathVariable("gid") long gid) {
		Graph graph = service.getGraph(gid);
		if (graph==null)
			throw new ResponseStatusException(
					  HttpStatus.NOT_FOUND, "not found"
					);
		graph.setId(gid);
		return graph;
	}
    
    

	/**
	 * @param gid it is the id of the graph to delete
	 */
    @ApiOperation(value = "deleteGraph", notes = "delete single graph"
	)
	@RequestMapping(value = "/{gid}", method = RequestMethod.DELETE)
    @ApiResponses(value = {
    		@ApiResponse(code = 204, message = "No Content"),
    		@ApiResponse(code = 404, message = "Not Found"),
    		})
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @ResponseBody
	public void deleteGraph(@PathVariable("gid") long gid) {
		Graph graph = service.deleteGraph(gid);
		if (graph == null)
			throw new ResponseStatusException(
					  HttpStatus.NOT_FOUND, "not found"
					);
	}
	
	
	/*
	 *  Nodes
	 */ 
    
    /**
	 * @param gid it is the id of the graph
	 * @param nid it is the name of the node to create
	 * @param node it is the new node to create
	 * @return the created node
	 */
    @ApiOperation(value = "createNode", notes = "create a new node"
	)
	@RequestMapping(value = "/{gid}/nodes", method = RequestMethod.POST)
    @ApiResponses(value = {
    		@ApiResponse(code = 201, message = "Created"),
    		@ApiResponse(code = 400, message = "Bad Request"),
    		@ApiResponse(code = 409, message = "Conflict"),
    		})
    public ResponseEntity<Node> createNode(@PathVariable("gid") long gid, @RequestParam(name="nid") String nid, @RequestBody Node node) {
		if(nid == null)
			throw new ResponseStatusException(
					  HttpStatus.BAD_REQUEST, "bad request"
					);
		StringBuffer url = request.getRequestURL();
    	Node created = service.createNode(gid, node, nid);
    	if (created != null) {
    		if(created.getName() == null)
    			throw new ResponseStatusException(
  					  HttpStatus.CONFLICT, "conflict"
  					);
    		String responseUrl;
    		if(url.toString().endsWith("/")) responseUrl = url.toString() + nid;
    		else responseUrl = url.toString() + "/" + nid;
    		HttpHeaders responseHeaders = new HttpHeaders();
    		try {
				responseHeaders.setLocation(new URI(responseUrl));
			} catch (URISyntaxException e) {
				throw new ResponseStatusException(
						  HttpStatus.BAD_REQUEST, "bad request"
						);
			}
    		return new ResponseEntity<Node>(created, responseHeaders, HttpStatus.CREATED);
    	} else
    		throw new ResponseStatusException(
					  HttpStatus.BAD_REQUEST, "bad request"
					);
	}
    
    
    
    /* Node */

	/**
	 * @param gid it is the id of the graph
	 * @param nid it is the name of the node to update
	 * @param node it is the new value of the node
	 */
    @ApiOperation(value = "updateNode", notes = "update single node"
	)
	@RequestMapping(value = "/{gid}/nodes/{nid}", method = RequestMethod.PUT)
    @ApiResponses(value = {
    		@ApiResponse(code = 204, message = "No Content"),
    		@ApiResponse(code = 400, message = "Bad Request"),
    		@ApiResponse(code = 404, message = "Not Found"),
    		})
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @ResponseBody
	public void updateGraph(@PathVariable("gid") long gid, @PathVariable("nid") String nid, @RequestBody Node node) {
		Node updated = service.updateNode(gid, nid, node);
		if (updated == null)
			throw new ResponseStatusException(
					  HttpStatus.NOT_FOUND, "not found"
					);
		else if (updated.getName() == null)
			throw new ResponseStatusException(
					  HttpStatus.BAD_REQUEST, "bad request"
					);
	}
    
    
    /**
	 * @param gid it is the id of the graph
	 * @param nid it is the id of the node to retrieve
	 * @return the node with nid name
	 */
    @ApiOperation(value = "getNode", notes = "retrieve single node"
	)
	@RequestMapping(value = "/{gid}/nodes/{nid}", method = RequestMethod.GET)
    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "OK"),
    		@ApiResponse(code = 404, message = "Not Found"),
    		})
    @ResponseBody
	public Node getNode(@PathVariable("gid") long gid, @PathVariable("nid") String nid) {
		Node node = service.getNode(gid, nid);
		if (node == null)
			throw new ResponseStatusException(
					  HttpStatus.NOT_FOUND, "not found"
					);
		return node;
	}
    
    
    /**
	 * @param gid it is the id of the graph
	 * @param nid it is the name of the node to delete
	 */
    @ApiOperation(value = "deleteNode", notes = "delete single node"
	)
	@RequestMapping(value = "/{gid}/nodes/{nid}", method = RequestMethod.DELETE)
    @ApiResponses(value = {
    		@ApiResponse(code = 204, message = "No Content"),
    		@ApiResponse(code = 404, message = "Not Found"),
    		})
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @ResponseBody
	public void deleteNode(@PathVariable("gid") long gid, @PathVariable("nid") String nid) {
		Node node = service.deleteNode(gid, nid);
		if ( node == null)
			throw new ResponseStatusException(
					  HttpStatus.NOT_FOUND, "not found"
					);
	}

    
    
    /* 
     * Neighbours 
     */
	
	
	/**
	 * @param gid it is the id of the graph
	 * @param nid it is the name of the node
	 * @param neighbour it is the new neighbour to add to the node
	 */
    @ApiOperation(value = "addNeighbour", notes = "add a new neighbour"
	)
	@RequestMapping(value = "/{gid}/nodes/{nid}/neighbours", method = RequestMethod.POST)
	@ApiResponses(value = {
	    		@ApiResponse(code = 204, message = "No Content"),
	    		@ApiResponse(code = 404, message = "Not Found"),
	    		@ApiResponse(code = 409, message = "Conflict"),
	    		})
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @ResponseBody
	public void addNeighbour(@PathVariable("gid") long gid, @PathVariable("nid") String nid, @RequestBody Neighbour neighbour){
		Neighbour added = service.addNeighbour(gid, nid, neighbour);
		if(added == null)
			throw new ResponseStatusException(
					  HttpStatus.NOT_FOUND, "not found"
					);
		else if(added.getName() == null)
			throw new ResponseStatusException(
					  HttpStatus.CONFLICT, "conflict"
					);
	}
    
    
    
    /**
	 * @param gid it is the id of the graph
	 * @param nid it is the name of the node 
	 * @param neighbour it is the neighbour to remove from the node
	 */
    @ApiOperation(value = "deleteNeighbour", notes = "delete a neighbour"
	)
	@RequestMapping(value = "/{gid}/nodes/{nid}/neighbours", method = RequestMethod.DELETE)
	@ApiResponses(value = {
	    		@ApiResponse(code = 204, message = "No Content"),
	    		@ApiResponse(code = 400, message = "Bad Request"),
	    		@ApiResponse(code = 404, message = "Not Found")
	    		})
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @ResponseBody
	public void deleteNeighbour(@PathVariable("gid") long gid, @PathVariable("nid") String nid, @RequestParam(name="neighbour") String neighbour){
		
		if(neighbour == null)
			throw new ResponseStatusException(
					  HttpStatus.BAD_REQUEST, "bad request"
					);
		
		Neighbour removed = service.deleteNeighbour(gid, nid, neighbour);
		if(removed == null)
			throw new ResponseStatusException(
						 HttpStatus.NOT_FOUND, "not found"
					);
		else if(removed.getName() == null)
			throw new ResponseStatusException(
					  HttpStatus.BAD_REQUEST, "bad request"
					);
	}
	

	/* Configuration */
    
    /**
	 * @param gid it is the id of the graph
	 * @param nid it is the name of the node
	 * @param configuration it is the new configuration
	 */
    @ApiOperation(value = "updateConfiguration", notes = "update the configuration of a node"
	)
	@RequestMapping(value = "/{gid}/nodes/{nid}/configuration", method = RequestMethod.PUT)
	@ApiResponses(value = {
	    		@ApiResponse(code = 204, message = "No Content"),
	    		@ApiResponse(code = 404, message = "Not Found")
	    		})
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @ResponseBody
	public void updateConfiguration(@PathVariable("gid") long gid, @PathVariable("nid") String nid, @RequestBody Configuration configuration){
		
		Configuration updated = service.updateConfiguration(gid, nid, configuration);
		if(updated == null)
			throw new ResponseStatusException(
					 HttpStatus.NOT_FOUND, "not found"
				);
	}
    
    
    /**
	 * @param gid it is the id of the graph
	 * @param nid it is the name of the node whose configuration must be deleted
	 */

    @ApiOperation(value = "deleteConfiguration", notes = "delete the configuration of a node"
	)
	@RequestMapping(value = "/{gid}/nodes/{nid}/configuration", method = RequestMethod.DELETE)
	@ApiResponses(value = {
	    		@ApiResponse(code = 204, message = "No Content"),
	    		@ApiResponse(code = 404, message = "Not Found")
	    		})
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @ResponseBody
	public void deleteConfiguration(@PathVariable("gid") long gid, @PathVariable("nid") String nid){
		
		Configuration deleted = service.deleteConfiguration(gid, nid);
		if(deleted == null)
			throw new ResponseStatusException(
					 HttpStatus.NOT_FOUND, "not found"
				);
	}
	
	/* Constraints */
    
    /**
	 * @param gid it is the id of the graph
	 * @param constraints they are the constraints to add to the graph
	 * @return the created constraints 
	 */
	@ApiOperation(value = "createConstraints", notes = "create a set of constraints for a graph"
	)
	@RequestMapping(value = "/{gid}", method = RequestMethod.POST)
	@ApiResponses(value = {
	    	@ApiResponse(code = 201, message = "Created"),
	    	@ApiResponse(code = 400, message = "Bad Request"),
	    	@ApiResponse(code = 409, message = "Conflict"),
	    	})
	public ResponseEntity<Constraints> createConstraints(@PathVariable("gid") long gid, @RequestBody Constraints constraints) {
		if(constraints.getLinkConstraints().getLinkMetrics().isEmpty() && constraints.getNodeConstraints().getNodeMetrics().isEmpty())
			throw new ResponseStatusException(
					  HttpStatus.BAD_REQUEST, "bad request"
					);
		StringBuffer url = request.getRequestURL();
		String responseUrl;
		if(url.toString().endsWith("/")) responseUrl = url.toString() + "constraints";
		else responseUrl = url.toString() + "/constraints";
    	Constraints created = service.createConstraints(gid, constraints);
    	if (created != null) {
    		if(created.getLinkConstraints() == null && created.getNodeConstraints() == null)
    			throw new ResponseStatusException(
   					 HttpStatus.CONFLICT, "conflict"
   				);
    		HttpHeaders responseHeaders = new HttpHeaders();
    		try {
				responseHeaders.setLocation(new URI(responseUrl));
			} catch (URISyntaxException e) {
				throw new ResponseStatusException(
						  HttpStatus.BAD_REQUEST, "bad request"
						);
			}
    		return new ResponseEntity<Constraints>(created, responseHeaders, HttpStatus.CREATED);
    	} else
    		throw new ResponseStatusException(
					  HttpStatus.BAD_REQUEST, "bad request"
					);
	}
	
	
	
	/**
	 * @param gid it is the id of the graph
	 * @param constraints they are the new constraints to update
	 */
    @ApiOperation(value = "updateConstraints", notes = "update the constraints of a graph"
	)
	@RequestMapping(value = "/{gid}/constraints", method = RequestMethod.PUT)
    @ApiResponses(value = {
    		@ApiResponse(code = 204, message = "No Content"),
    		@ApiResponse(code = 400, message = "Bad Request"),
    		@ApiResponse(code = 404, message = "Not Found"),
    		})
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @ResponseBody
	public void updateConstraints(@PathVariable("gid") long gid, @RequestBody Constraints constraints) {
		if(constraints.getLinkConstraints().getLinkMetrics().isEmpty() && constraints.getNodeConstraints().getNodeMetrics().isEmpty())
			throw new ResponseStatusException(
					  HttpStatus.BAD_REQUEST, "bad request"
					);;
		Constraints updated = service.updateConstraints(gid, constraints);
		if (updated == null)
			throw new ResponseStatusException(
					 HttpStatus.NOT_FOUND, "not found"
				);
		else if (updated.getLinkConstraints().getLinkMetrics().isEmpty() && updated.getNodeConstraints().getNodeMetrics().isEmpty())
			throw new ResponseStatusException(
					  HttpStatus.BAD_REQUEST, "bad request"
					);
	}
    
    
	/**
	 * @param gid it is the id of the graph
	 * @return the constraints of the graph
	 */
    @ApiOperation(value = "getConstraints", notes = "retrieve the constraints of the graph"
	)
	@RequestMapping(value = "/{gid}/constraints", method = RequestMethod.GET)
    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "OK"),
    		@ApiResponse(code = 404, message = "Not Found"),
    		})
    @ResponseBody
	public Constraints getConstraints(@PathVariable("gid") long gid) {
		Constraints constraints = service.getConstraints(gid);
		if (constraints  == null)
			throw new ResponseStatusException(
					 HttpStatus.NOT_FOUND, "not found"
				);
		return constraints;
	}
	
    
    /**
	 * @param gid it is the id of the graph whose constraints must be removed
	 */
    @ApiOperation(value = "deleteConstraints", notes = "delete the constraints of a graph"
	)
	@RequestMapping(value = "/{gid}/constraints", method = RequestMethod.DELETE)
    @ApiResponses(value = {
    		@ApiResponse(code = 204, message = "No Content"),
    		@ApiResponse(code = 404, message = "Not Found"),
    		})
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    @ResponseBody
	public void deleteConstraints(@PathVariable("gid") long gid) {
		Constraints deleted = service.deleteConstraints(gid);
		if (deleted == null)
			throw new ResponseStatusException(
					 HttpStatus.NOT_FOUND, "not found"
				);
	}
	
	
}
