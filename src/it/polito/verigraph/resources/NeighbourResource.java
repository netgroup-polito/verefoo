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
import javax.xml.bind.JAXBException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.polito.verigraph.model.ErrorMessage;
import it.polito.verigraph.model.Neighbour;
import it.polito.verigraph.service.NeighbourService;

//@Path("/")
@Api( hidden= true, value = "", description = "Manage neighbours" )
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class NeighbourResource {
    private NeighbourService neighboursService = new NeighbourService();

    @GET
    @ApiOperation(
            httpMethod = "GET",
            value = "Returns all neighbours of a given node belonging to a given graph",
            notes = "Returns an array of neighbours of a given node belonging to a given graph",
            response = Neighbour.class,
            responseContainer = "List")
    @ApiResponses(value = { @ApiResponse(code = 403, message = "Invalid graph and/or node id", response=ErrorMessage.class),
            @ApiResponse(code = 404, message = "Graph and/or node not found", response=ErrorMessage.class),
            @ApiResponse(code = 500, message = "Internal server error", response = ErrorMessage.class),
            @ApiResponse(code = 200, message = "All the neighbours have been returned in the message body", response=Neighbour.class, responseContainer="List")})
    public List<Neighbour> getAllNeighbours(
            @ApiParam(value = "Graph id", required = true) @PathParam("graphId") long graphId,
            @ApiParam(value = "Node id", required = true) @PathParam("nodeId") long nodeId) throws JsonProcessingException{
        return neighboursService.getAllNeighbours(graphId, nodeId);
    }

    @POST
    @ApiOperation(
            httpMethod = "POST",
            value = "Adds a neighbour to a given node belonging to a given graph",
            notes = "Adds single neighbour to a given node belonging to a given graph",
            response = Neighbour.class)
    @ApiResponses(value = { @ApiResponse(code = 403, message = "Invalid graph and/or node id", response=ErrorMessage.class),
            @ApiResponse(code = 404, message = "Graph and/or node not found", response=ErrorMessage.class),
            @ApiResponse(code = 400, message = "Invalid neighbour object", response=ErrorMessage.class),
            @ApiResponse(code = 500, message = "Internal server error", response = ErrorMessage.class),
            @ApiResponse(code = 201, message = "Neighbour successfully created", response=Neighbour.class)})
    public Response addNeighbour(
            @ApiParam(value = "Graph id", required = true) @PathParam("graphId") long graphId,
            @ApiParam(value = "Node id", required = true) @PathParam("nodeId") long nodeId,
            @ApiParam(value = "New neighbour object. Neighbour name must refer to the name of an existing node of the same graph", required = true) Neighbour neighbour,
            @Context UriInfo uriInfo) throws JsonParseException, JsonMappingException, JAXBException, IOException{
        Neighbour newNeighbour = neighboursService.addNeighbour(graphId, nodeId, neighbour);
        String newId = String.valueOf(newNeighbour.getId());
        URI uri = uriInfo.getAbsolutePathBuilder().path(newId).build();
        return Response.created(uri)
                .entity(newNeighbour)
                .build();
    }

    @PUT
    @Path("{neighbourId}")
    @ApiOperation(
            httpMethod = "PUT",
            value = "Edits a neighbour of a given node belonging to a given graph",
            notes = "Edits a single neighbour of a given node belonging to a given graph",
            response = Neighbour.class)
    @ApiResponses(value = { @ApiResponse(code = 403, message = "Invalid graph and/or node and/or neighbour id", response=ErrorMessage.class),
            @ApiResponse(code = 404, message = "Graph and/or node and /or neighbour not found", response=ErrorMessage.class),
            @ApiResponse(code = 400, message = "Invalid neighbour object", response=ErrorMessage.class),
            @ApiResponse(code = 500, message = "Internal server error", response = ErrorMessage.class),
            @ApiResponse(code = 200, message = "Neighbour edited successfully", response=Neighbour.class)})
    public Neighbour updateNeighbour(
            @ApiParam(value = "Graph id", required = true) @PathParam("graphId") long graphId,
            @ApiParam(value = "Node id", required = true) @PathParam("nodeId") long nodeId,
            @ApiParam(value = "Neighbour id", required = true) @PathParam("neighbourId") long neighbourId,
            @ApiParam(value = "Updated neighbour object. Neighbour name must refer to the name of an existing node of the same graph", required = true) Neighbour neighbour) throws JAXBException, IOException{
        neighbour.setId(neighbourId);
        return neighboursService.updateNeighbour(graphId, nodeId, neighbour);
    }

    @DELETE
    @Path("{neighbourId}")
    @ApiOperation(
            httpMethod = "DELETE",
            value = "Removes a neighbour from a given node belonging to a given graph",
            notes = "Deletes a single neighbour of a given node belonging to a given graph",
            response = Neighbour.class)
    @ApiResponses(value = { @ApiResponse(code = 403, message = "Invalid graph and/or node and/or neighbour id", response=ErrorMessage.class),
            @ApiResponse(code = 404, message = "Graph and/or node not found", response=ErrorMessage.class),
            @ApiResponse(code = 500, message = "Internal server error", response = ErrorMessage.class),
            @ApiResponse(code = 204, message = "Node successfully deleted")})
    public void deleteNeighbour(
            @ApiParam(value = "Graph id", required = true) @PathParam("graphId") long graphId,
            @ApiParam(value = "Node id", required = true) @PathParam("nodeId") long nodeId,
            @ApiParam(value = "Neighbour id", required = true) @PathParam("neighbourId") long neighbourId){
        neighboursService.removeNeighbour(graphId, nodeId, neighbourId);
    }

    @GET
    @Path("{neighbourId}")
    @ApiOperation(
            httpMethod = "GET",
            value = "Returns a neighbour of a given node belonging to a given graph",
            notes = "Returns a single neighbour of a given node belonging to a given graph",
            response = Neighbour.class)
    @ApiResponses(value = { @ApiResponse(code = 403, message = "Invalid graph and/or node and/or neighbour id", response=ErrorMessage.class),
            @ApiResponse(code = 404, message = "Graph and/or node and /or neighbour not found", response=ErrorMessage.class),
            @ApiResponse(code = 500, message = "Internal server error", response = ErrorMessage.class),
            @ApiResponse(code = 200, message = "The requested neighbour has been returned in the message body", response=Neighbour.class)})
    public Neighbour getNeighbour(
            @ApiParam(value = "Graph id", required = true) @PathParam("graphId") long graphId,
            @ApiParam(value = "Node id", required = true) @PathParam("nodeId") long nodeId,
            @ApiParam(value = "Neighbour id", required = true) @PathParam("neighbourId") long neighbourId) throws JsonProcessingException{
        return neighboursService.getNeighbour(graphId, nodeId, neighbourId);
    }
}
