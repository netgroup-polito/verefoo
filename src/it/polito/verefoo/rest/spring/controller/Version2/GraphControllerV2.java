package it.polito.verefoo.rest.spring.controller.Version2;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Schema;

import it.polito.verefoo.pojo.*;
import it.polito.verefoo.rest.spring.ResourceWrapperWithLinks;

@RestController
@RequestMapping(value = "/adp/v2/graphs", consumes = "application/json", produces = "application/json")
public class GraphControllerV2 {

        @Autowired
        private HttpServletRequest request;

        /**
         * @return All the graphs previously defined and stored
         */
        @Operation(summary = "Get all graphs", description = "Retrieve all the graphs previously defined and stored.", tags = "version 2")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Retrieved correctly", content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))),
                        @ApiResponse(responseCode = "500", description = "Bad request", content = @Content) })

        @RequestMapping(value = "", method = RequestMethod.GET)
        public ResponseEntity<Resources<List<Graph>>> getGraphs() {

                List<Graph> graphs = null;
                Integer graphId = 0;

                return ResponseEntity.status(HttpStatus.OK).body(
                                // wrap the response with the hyperlinks
                                new ResourceWrapperWithLinks<List<Graph>>()
                                                .addLink("graphs/" + graphId, "first", RequestMethod.GET)
                                                .addLink("graphs/" + graphId, "first", RequestMethod.DELETE)
                                                .addLink("graphs/" + graphId, "first", RequestMethod.PUT)
                                                .addLink("graphs", "new", RequestMethod.POST)
                                                .addLink("graphs", "self", RequestMethod.GET)
                                                .wrap(graphs));

        }

        /**
         * @param The graph to create
         * @return The id of the graph
         */
        @Operation(summary = "Create a graph", description = "Create a new graph which describes a logical topology.", tags = "version 2")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Created correctly", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Integer.class), examples = @ExampleObject(value = "3", description = "The id that was automatically assigned to the passed graph."))),
                        @ApiResponse(responseCode = "500", description = "Bad request", content = @Content) })

        @RequestMapping(value = "", method = RequestMethod.POST)
        public ResponseEntity<Resources<Integer>> createGraph(@RequestBody Graph graph) {

                Integer graphId = 0;

                return ResponseEntity.status(HttpStatus.OK).body(
                                // wrap the response with the hyperlinks
                                new ResourceWrapperWithLinks<Integer>()
                                                .addLink("graphs/" + graphId, "self", RequestMethod.GET)
                                                .addLink("graphs/" + graphId, "self", RequestMethod.DELETE)
                                                .addLink("graphs/" + graphId, "self", RequestMethod.PUT).wrap(graphId));

        }

        /**
         * @param The id of the graph to retrieve
         * @return The wanted graph
         */
        @Operation(summary = "Get a graph", description = "Get the graph, a.k.a. logical topology, associated with a given id.", tags = "version 2")
        @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Retrieved correctly", content = {
                        @Content(mediaType = "application/json", schema = @Schema(implementation = Graph.class)) }),
                        @ApiResponse(responseCode = "400", description = "No graph has been found with the given id.", content = @Content),
                        @ApiResponse(responseCode = "500", description = "Bad request", content = @Content) })

        @RequestMapping(value = "/{gid}", method = RequestMethod.GET)
        public ResponseEntity<Resources<Graph>> getGraph(@PathVariable("gid") Integer graphId) {

                Graph graph = null;

                return ResponseEntity.status(HttpStatus.OK).body(
                                // wrap the response with the hyperlinkss
                                new ResourceWrapperWithLinks<Graph>()
                                                .addLink("graphs/" + graphId, "self", RequestMethod.GET)
                                                .addLink("graphs/" + graphId, "self", RequestMethod.DELETE)
                                                .addLink("graphs/" + graphId, "self", RequestMethod.PUT).wrap(graph));

        }

        /**
         * @param The id of the graph to delete
         */
        @Operation(summary = "Delete a graph", description = "Delete the graph, a.k.a. logical topology, associated with a given id.", tags = "version 2")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Deleted correctly", content = @Content),
                        @ApiResponse(responseCode = "400", description = "No graph has been found with the given id.", content = @Content),
                        @ApiResponse(responseCode = "500", description = "Bad request", content = @Content) })

        @RequestMapping(value = "/{gid}", method = RequestMethod.DELETE)
        public void deleteGraph(@PathVariable("gid") Integer graphId) {

        }

        /**
         * @param The graph with the new data
         * @param The id of the graph to retrieve
         */
        @Operation(summary = "Update a graph", description = "Update the graph, a.k.a. logical topology, with the id specified inside the graph object itself.", tags = "version 2")
        @ApiResponses(value = { @ApiResponse(responseCode = "204", description = "Updated correctly"),
                        @ApiResponse(responseCode = "400", description = "No graph has been found with the given id."),
                        @ApiResponse(responseCode = "500", description = "Bad request") })

        @RequestMapping(value = "/{gid}", method = RequestMethod.PUT)
        public void updateGraph(@RequestBody Graph graph, @PathVariable("gid") Integer graphId) {

        }

}
