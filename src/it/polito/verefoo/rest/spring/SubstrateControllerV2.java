package it.polito.verefoo.rest.spring;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.hateoas.Resources;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Schema;

import it.polito.verefoo.pojo.*;


@RestController
@RequestMapping(value = "/adp/v2/substrates", consumes = "application/json", produces = "application/json")
public class SubstrateControllerV2 {

        ADPService adpService = new ADPService();

        @Autowired
        private HttpServletRequest request;

        /**
         * @return All the substrates previously defined and stored
         */
        @Operation(summary = "Get all substrates", description = "Retrieve all the substrates previously defined and stored.", tags = "version 2")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Retrieved correctly", content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))),
                        @ApiResponse(responseCode = "500", description = "Bad request", content = @Content) })

        @RequestMapping(value = "", method = RequestMethod.GET)
        public ResponseEntity<Resources<List<Substrate>>> getSubstrates() {

                List<Substrate> substrates = null;
                Integer substrateId = 0;

                return ResponseEntity.status(HttpStatus.OK).body(
                                // wrap the response with the hyperlinks
                                new ResourceWrapperWithLinks<List<Substrate>>()
                                                .addLink("substrates/" + substrateId, "first", RequestMethod.GET)
                                                .addLink("substrates/" + substrateId, "first", RequestMethod.DELETE)
                                                .addLink("substrates/" + substrateId, "first", RequestMethod.PUT)
                                                .addLink("substrates", "new", RequestMethod.POST)
                                                .addLink("substrates", "self", RequestMethod.GET)
                                                .wrap(substrates));

        }

        /**
         * @param The substrate to create
         * @return The id of the substrate
         */
        @Operation(summary = "Create a substrate", description = "Create a new substrate, that is a physical network.", tags = "version 2")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Created correctly", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Integer.class), examples = @ExampleObject(value = "3", description = "The id that was automatically assigned to the passed substrate."))),
                        @ApiResponse(responseCode = "500", description = "Bad request", content = @Content) })

        @RequestMapping(value = "", method = RequestMethod.POST)
        public ResponseEntity<Resources<Integer>> createSubstrate(@RequestBody Substrate substrate) {

                Integer substrateId = 0;

                return ResponseEntity.status(HttpStatus.OK).body(
                                // wrap the response with the hyperlinks
                                new ResourceWrapperWithLinks<Integer>()
                                                .addLink("substrates/" + substrateId, "self", RequestMethod.GET)
                                                .addLink("substrates/" + substrateId, "self", RequestMethod.DELETE)
                                                .addLink("substrates/" + substrateId, "self", RequestMethod.PUT)
                                                .wrap(substrateId));

        }

        /**
         * @param The id of the substrate to retrieve
         * @return The wanted substrate
         */
        @Operation(summary = "Get a substrate", description = "Get the substrate, a.k.a. physical network, associated with a given id.", tags = "version 2")
        @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "Retrieved correctly", content = {
                        @Content(mediaType = "application/json", schema = @Schema(implementation = Substrate.class)) }),
                        @ApiResponse(responseCode = "400", description = "No substrate has been found with the given id.", content = @Content),
                        @ApiResponse(responseCode = "500", description = "Bad request", content = @Content) })

        @RequestMapping(value = "/{sid}", method = RequestMethod.GET)
        public ResponseEntity<Resources<Substrate>> getSubstrate(@PathVariable("sid") Integer substrateId) {

                Substrate substrate = null;

                // wrap the response with the hyperlinks
                return ResponseEntity.status(HttpStatus.OK).body(
                                // wrap the response with the hyperlinks
                                new ResourceWrapperWithLinks<Substrate>()
                                                .addLink("substrates/" + substrateId, "self", RequestMethod.DELETE)
                                                .addLink("substrates/" + substrateId, "self", RequestMethod.PUT)
                                                .wrap(substrate));
        }

        /**
         * @param The id of the substrate to retrieve
         * @return The wanted substrate
         */
        @Operation(summary = "Delete a substrate", description = "Delete the substrate, a.k.a. physical network, associated with a given id.", tags = "version 2")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Deleted correctly", content = @Content),
                        @ApiResponse(responseCode = "400", description = "No substrate has been found with the given id.", content = @Content),
                        @ApiResponse(responseCode = "500", description = "Bad request", content = @Content) })

        @RequestMapping(value = "/{sid}", method = RequestMethod.DELETE)
        public void deleteSubstrate(@PathVariable("sid") Integer substrateId) {

        }

        /**
         * @param The substrate with the new data
         * @param The id of the substrate to retrieve
         * @return The wanted substrate
         */
        @Operation(summary = "Update a substrate", description = "Update the substrate, a.k.a. physical network, with the id specified inside the substrate object itself.", tags = "version 2")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "204", description = "Updated correctly"),
                        @ApiResponse(responseCode = "400", description = "No substrate has been found with the given id.", content = @Content),
                        @ApiResponse(responseCode = "500", description = "Bad request", content = @Content) })

        @RequestMapping(value = "/{sid}", method = RequestMethod.PUT)
        // here the method returns the id just to have the possibility to send
        // hyperlinks
        public ResponseEntity<Resources<Integer>> updateSubstrate(@RequestBody Substrate substrate,
                        @PathVariable("sid") Integer substrateId) {

                return ResponseEntity.status(HttpStatus.OK).body(
                                // wrap the response with the hyperlinks
                                new ResourceWrapperWithLinks<Integer>()
                                                .addLink("substrates/" + substrateId, "self", RequestMethod.GET)
                                                .addLink("substrates/" + substrateId, "self", RequestMethod.DELETE)
                                                .addLink("substrates/" + substrateId, "self", RequestMethod.PUT)
                                                .wrap(substrateId));
                // wrap the response with the hyperlinks
        }

}