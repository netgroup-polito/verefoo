package it.polito.verefoo.rest.spring;

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
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Schema;

import it.polito.verefoo.pojo.*;

@RestController
@RequestMapping(value = "/adp/v2/simulations", consumes = "application/json", produces = "application/json")
public class SimulationControllerV2 {

    ADPService adpService = new ADPService();

    @Autowired
    private HttpServletRequest request;

    /**
     * @param The simulation to perform
     * @return The report of the simulation, as soon as the process has finished, filled with the outcome of the processing
     */
    @Operation(summary = "Perform one or more simulations", description = "More than one simulation at a time can be launched, through an array of simulation elements. Each simulation has a graph and a set of network functions, constraints, requirements and substrates that are applied to the graph. The simulation's id and the graph's id are the same.", tags = "version 2")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All simulations terminated correctly", content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))),
            @ApiResponse(responseCode = "500", description = "Bad request", content = @Content) })

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseEntity<Resources<List<Simulation>>> performSimulations(@RequestBody /* Vector or List? */List<Simulation> simulations) {

        List<Simulation> processedSimulations = null;
        Integer simulationId = 0;

        return ResponseEntity.status(HttpStatus.OK).body(
                                // wrap the response with the hyperlinkss
                                new ResourceWrapperWithLinks<List<Simulation>>()
                                                .addLink("simulations/" + simulationId, "first", RequestMethod.GET)
                                                .addLink("simulations/" + simulationId, "first", RequestMethod.DELETE)
                                                .addLink("simulations/" + simulationId, "first", RequestMethod.PUT)
                                                .addLink("simulations", "new", RequestMethod.POST)
                                                .addLink("simulations", "self", RequestMethod.GET)
                                                .wrap(processedSimulations));

    }

    /**
     * @param The id of the past simulation to retrieve
     * @return The wanted simulation
     */
    @Operation(summary = "Get a past simulation", description = "Get the result of a past simulation through its id. The id of a simulation corresponds to the id of the graph upon which the simulation was evaluated. Not to be used to perform a simulation.", tags = "version 2")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retrieved correctly", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Simulation.class)) }),
            @ApiResponse(responseCode = "400", description = "No simulation has been found with the given id.", content = @Content),
            @ApiResponse(responseCode = "500", description = "Bad request", content = @Content) })

    @RequestMapping(value = "/{sid}", method = RequestMethod.GET)
    public ResponseEntity<Resources<Simulation>> getSimulation(@PathVariable("sid") Integer simulationId) {

        Simulation simulation = null;

        return ResponseEntity.status(HttpStatus.OK).body(
                                // wrap the response with the hyperlinkss
                                new ResourceWrapperWithLinks<Simulation>()
                                                .addLink("simulations/" + simulationId, "self", RequestMethod.GET)
                                                .addLink("simulations/" + simulationId, "self", RequestMethod.DELETE)
                                                .addLink("simulations/" + simulationId, "self", RequestMethod.PUT)
                                                .wrap(simulation));

    }

    /**
     * @param The id of the simulation report to delete
     */
    @Operation(summary = "Delete a past simulation", description = "Delete the simulation report associated with a given id. Not to be used to abort a simulation in processing.", tags = "version 2")
    @ApiResponses(value = { @ApiResponse(responseCode = "204", description = "Deleted correctly", content = @Content),
            @ApiResponse(responseCode = "400", description = "No simulation has been found with the given id.", content = @Content),
            @ApiResponse(responseCode = "500", description = "Bad request", content = @Content) })

    @RequestMapping(value = "/{sid}", method = RequestMethod.DELETE)
    public void deleteSimulation(@PathVariable("sid") Integer simulationId) {

    }
    
}
