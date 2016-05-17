package it.polito.escape.verify.resources;

import java.net.URI;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.polito.escape.verify.model.Graph;
import it.polito.escape.verify.model.Node;
import it.polito.escape.verify.service.GraphService;

@Path("/graphs")
@Api(value = "/graphs", description = "Manage graphs")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class GraphResource {
	GraphService graphService = new GraphService();

	@GET
	@ApiOperation(httpMethod = "GET", value = "Returns all the graphs", notes = "Returns multiple graphs", response = Graph.class, responseContainer = "List")
	public List<Graph> getGraphs() {
		return graphService.getAllGraphs();
	}

	@POST
	@ApiOperation(httpMethod = "POST", value = "Creates a graph", notes = "A single graph can be created", response = Response.class)
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid graph supplied") })
	public Response addGraph(@ApiParam(value = "New graph object", required = true) Graph graph, @Context UriInfo uriInfo) {	
		Graph newGraph = graphService.addGraph(graph);
		String newId = String.valueOf(newGraph.getId());
		URI uri = uriInfo.getAbsolutePathBuilder().path(newId).build();
		return Response.created(uri).entity(newGraph).build();
	}

	@GET
	@Path("/{graphId}")
	@ApiOperation(httpMethod = "GET", value = "Returns a graph", notes = "A single graph can be returned", response = Graph.class)
	@ApiResponses(value = { @ApiResponse(code = 403, message = "Invalid graph id"),
			@ApiResponse(code = 404, message = "Graph not found") })
	public Graph getGraph(@ApiParam(value = "Graph id", required = true) @PathParam("graphId") long graphId,
			@Context UriInfo uriInfo) {
		Graph graph = graphService.getGraph(graphId);
		graph.addLink(getUriForSelf(uriInfo, graph), "self");
		graph.addLink(getUriForNodes(uriInfo, graph), "nodes");
		return graph;
	}

	@PUT
	@Path("/{graphId}")
	@ApiOperation(httpMethod = "PUT", value = "Edits a graph", notes = "A single graph can be edited", response = Graph.class)
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid graph object"),
			@ApiResponse(code = 403, message = "Invalid graph id"),
			@ApiResponse(code = 404, message = "Graph not found") })
	public Graph updateGraph(@ApiParam(value = "Graph id", required = true) @PathParam("graphId") long id,
			@ApiParam(value = "Updated graph object", required = true) Graph graph) {
		graph.setId(id);
		return graphService.updateGraph(graph);
	}

	@DELETE
	@Path("/{graphId}")
	@ApiOperation(httpMethod = "DELETE", value = "Deletes a graph", notes = "A single graph can be deleted")
	@ApiResponses(value = { @ApiResponse(code = 403, message = "Invalid graph id") })
	public void deleteGraph(@ApiParam(value = "Graph id", required = true) @PathParam("graphId") long id) {
		graphService.removeGraph(id);
	}

	private String getUriForSelf(UriInfo uriInfo, Graph graph) {
		String uri = uriInfo.getBaseUriBuilder()
				.path(GraphResource.class)
				.path(Long.toString(graph.getId()))
				.build()
				.toString();
		return uri;
	}

	private String getUriForNodes(UriInfo uriInfo, Graph graph) {
		String uri = uriInfo.getBaseUriBuilder()
				.path(GraphResource.class)
				.path(GraphResource.class, "getNodeResource")
				//.path(NodeResource.class)
				.resolveTemplate("graphId", graph.getId())
				.build().
				toString();
		return uri;
	}

	@Path("/{graphId}/nodes")
	public NodeResource getNodeResource() {
		return new NodeResource();
	}
}
