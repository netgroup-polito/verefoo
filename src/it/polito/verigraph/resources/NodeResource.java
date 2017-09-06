/*******************************************************************************
 * Copyright (c) 2017 Politecnico di Torino and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package it.polito.verigraph.resources;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import javax.xml.bind.JAXBException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.polito.neo4j.exceptions.MyInvalidIdException;
import it.polito.neo4j.exceptions.MyNotFoundException;
import it.polito.neo4j.manager.Neo4jDBManager;
import it.polito.verigraph.exception.BadRequestException;
import it.polito.verigraph.exception.ForbiddenException;
import it.polito.verigraph.model.Configuration;
import it.polito.verigraph.model.ErrorMessage;
import it.polito.verigraph.model.Graph;
import it.polito.verigraph.model.Neighbour;
import it.polito.verigraph.model.Node;
import it.polito.verigraph.service.GraphService;
import it.polito.verigraph.service.NodeService;

@Api( hidden= true, value = "", description = "Manage nodes" )
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class NodeResource {

    NodeService nodeService = new NodeService();

    @GET
    @ApiOperation(
            httpMethod = "GET",
            value = "Returns all nodes of a given graph",
            notes = "Returns an array of nodes belonging to a given graph",
            response = Node.class,
            responseContainer = "List")
    @ApiResponses(value = { @ApiResponse(code = 403, message = "Invalid graph id", response = ErrorMessage.class),
            @ApiResponse(code = 404, message = "Graph not found", response = ErrorMessage.class),
            @ApiResponse(code = 500, message = "Internal server error", response = ErrorMessage.class),
            @ApiResponse(code = 200, message = "All the nodes have been returned in the message body", response = Node.class, responseContainer = "List") })
    public List<Node> getNodes(@ApiParam(value = "Graph id", required = true) @PathParam("graphId") long graphId) throws JsonParseException, JsonMappingException, JAXBException, IOException, MyNotFoundException{
        return nodeService.getAllNodes(graphId);
    }

    @POST
    @ApiOperation(
            httpMethod = "POST",
            value = "Creates a node in a given graph",
            notes = "Creates a single node for a given graph",
            response = Response.class)
    @ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid node supplied", response = ErrorMessage.class),
            @ApiResponse(code = 403, message = "Invalid graph id", response = ErrorMessage.class),
            @ApiResponse(code = 404, message = "Graph not found", response = ErrorMessage.class),
            @ApiResponse(code = 500, message = "Internal server error", response = ErrorMessage.class),
            @ApiResponse(code = 201, message = "Node successfully created", response = Node.class)})
    public Response addNode(
            @ApiParam(value = "Graph id", required = true) @PathParam("graphId") long graphId,
            @ApiParam(value = "New node object", required = true) Node node,
            @Context UriInfo uriInfo) throws JsonParseException, JsonMappingException, JAXBException, IOException, MyInvalidIdException {
        Node newNode = nodeService.addNode(graphId, node);
        String newId = String.valueOf(newNode.getId());
        URI uri = uriInfo.getAbsolutePathBuilder().path(newId).build();
        return Response.created(uri)
                .entity(newNode)
                .build();
    }

    @GET
    @Path("{nodeId}")
    @ApiOperation(
            httpMethod = "GET",
            value = "Returns a node of a given graph",
            notes = "Returns a single node of a given graph",
            response = Node.class)
    @ApiResponses(value = { @ApiResponse(code = 403, message = "Invalid graph and/or node id", response = ErrorMessage.class),
            @ApiResponse(code = 404, message = "Graph and/or node not found", response = ErrorMessage.class),
            @ApiResponse(code = 500, message = "Internal server error", response = ErrorMessage.class),
            @ApiResponse(code = 200, message = "The requested node has been returned in the message body", response = Node.class)})
    public Node getNode(
            @ApiParam(value = "Graph id", required = true) @PathParam("graphId") long graphId,
            @ApiParam(value = "Node id", required = true) @PathParam("nodeId") long nodeId,
            @Context UriInfo uriInfo) throws JsonParseException, JsonMappingException, JAXBException, IOException, MyNotFoundException{
        Node node = nodeService.getNode(graphId, nodeId);
        node.addLink(getUriForSelf(uriInfo, graphId, node), "self");
        node.addLink(getUriForNeighbours(uriInfo, graphId, node), "neighbours");
        return node;
    }

    @PUT
    @Path("{nodeId}/configuration")
    @ApiOperation(
            httpMethod = "PUT",
            value = "Adds/edits a configuration to a node of a given graph",
            notes = "Configures a node. Once all the nodes of a graph have been configured a given policy can be verified for the graph (e.g. 'reachability' between two nodes).")
    @ApiResponses(value = { @ApiResponse(code = 403, message = "Invalid graph and/or node id", response = ErrorMessage.class),
            @ApiResponse(code = 404, message = "Graph and/or node not found", response = ErrorMessage.class),
            @ApiResponse(code = 500, message = "Internal server error", response = ErrorMessage.class),
            @ApiResponse(code = 200, message = "Configuration updated for the requested node", response=Configuration.class)})
    public Configuration addNodeConfiguration(
            @ApiParam(value = "Graph id", required = true) @PathParam("graphId") long graphId,
            @ApiParam(value = "Node id", required = true) @PathParam("nodeId") long nodeId,
            @ApiParam(value = "Node configuration", required = true) Configuration nodeConfiguration,
            @Context UriInfo uriInfo) throws JsonParseException, JsonMappingException, JAXBException, IOException, MyNotFoundException, MyInvalidIdException{

        Configuration conf=nodeService.addNodeConfiguration(graphId, nodeId, nodeConfiguration);
        return conf;

    }


    @PUT
    @Path("{nodeId}")
    @ApiOperation(
            httpMethod = "PUT",
            value = "Edits a node of a given graph",
            notes = "Edits a single node of a given graph",
            response = Node.class)
    @ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid node object", response = ErrorMessage.class),
            @ApiResponse(code = 403, message = "Invalid graph and/or node id", response = ErrorMessage.class),
            @ApiResponse(code = 404, message = "Graph and/or node not found", response = ErrorMessage.class),
            @ApiResponse(code = 500, message = "Internal server error", response = ErrorMessage.class),
            @ApiResponse(code = 200, message = "Node edited successfully", response = Node.class)})
    public Node updateNode(
            @ApiParam(value = "Graph id", required = true) @PathParam("graphId") long graphId,
            @ApiParam(value = "Node id", required = true) @PathParam("nodeId") long nodeId,
            @ApiParam(value = "Updated node object", required = true) Node node) throws JAXBException, IOException, MyInvalidIdException{
        node.setId(nodeId);
        return nodeService.updateNode(graphId, node);
    }

    @DELETE
    @Path("{nodeId}")
    @ApiOperation(
            httpMethod = "DELETE",
            value = "Deletes a node of a given graph",
            notes = "Deletes a single node of a given graph")
    @ApiResponses(value = { @ApiResponse(code = 403, message = "Invalid graph and/or node id", response = ErrorMessage.class),
            @ApiResponse(code = 500, message = "Internal server error", response = ErrorMessage.class),
            @ApiResponse(code = 204, message = "Node successfully deleted")})
    public void deleteNode(
            @ApiParam(value = "Graph id", required = true) @PathParam("graphId") long graphId,
            @ApiParam(value = "Node id", required = true) @PathParam("nodeId") long nodeId) throws JsonParseException, JsonMappingException, JAXBException, IOException{
        nodeService.removeNode(graphId, nodeId);
    }

    private String getUriForSelf(UriInfo uriInfo, long graphId, Node node) {
        String uri = uriInfo.getBaseUriBuilder()
                //.path(NodeResource.class)
                .path(GraphResource.class)
                .path(GraphResource.class, "getNodeResource")
                .resolveTemplate("graphId", graphId)
                .path(Long.toString(node.getId()))
                .build()
                .toString();
        return uri;
    }

    private String getUriForNeighbours(UriInfo uriInfo, long graphId, Node node) {
        String uri = uriInfo.getBaseUriBuilder()
                .path(GraphResource.class)
                .path(GraphResource.class, "getNodeResource")
                .resolveTemplate("graphId", graphId)
                .path(Long.toString(node.getId()))
                .path("neighbours")
                .build()
                .toString();
        //     .path(NodeResource.class)
        // .path(NodeResource.class, "getNeighbourResource")
        // .path(NeighbourResource.class)
        // .resolveTemplate("nodeId", node.getId())
        // .build()
        // .toString();
        return uri;
    }

    @Path("{nodeId}/neighbours")
    public NeighbourResource getNeighbourResource(){
        return new NeighbourResource();
    }
}