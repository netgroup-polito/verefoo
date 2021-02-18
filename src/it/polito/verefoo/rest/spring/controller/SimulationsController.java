package it.polito.verefoo.rest.spring.controller;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.polito.verefoo.VerefooSerializer;
import it.polito.verefoo.jaxb.FunctionalTypes;
import it.polito.verefoo.jaxb.NFV;
import it.polito.verefoo.rest.spring.ResourceWrapperWithLinks;
import it.polito.verefoo.rest.spring.service.SimulationService;

@RestController
@RequestMapping(value = "/adp/simulations", consumes = { "application/xml", "application/json" }, produces = {
		"application/xml", "application/json" })
@ApiResponses(value = {
	@ApiResponse(responseCode = "400", description = "The provided resource is not compliant with the data model.")
})
public class SimulationsController {

	@Autowired
	private HttpServletRequest request;

	@Autowired
	SimulationService service;



	@Operation(tags = "version 1 - simulations", summary = "Run a simulation by passing the actual NFV", description = "This is an all-in-one service, since the relative data structures (like graphs) are created automatically and they will be retrievable with the pertinent APIs separately.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "")
	})

	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseEntity<Resources<Long>> runSimulationByNFV(@RequestBody NFV nfv, @RequestParam(value = "fid", required = false) List<FunctionalTypes> usableFunctionalTypes) {

		try {
			// the nfv is modified in place by VerefooSerializer
			new VerefooSerializer(nfv);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"The simulation request is semantically malformed. Cause: " + e.getMessage());
		}

		Long simulationId = service.createSimulationResult(nfv);

		String url = request.getRequestURL().toString();
		return ResponseEntity.status(HttpStatus.OK).body(
				// wrap the response with the hyperlinks
				new ResourceWrapperWithLinks<Long>()
						.addLink(url + "/" + simulationId, "self", RequestMethod.GET)
						.wrap(simulationId));
	}



	@Operation(tags = "version 1 - simulations", summary = "Run a simulation by passing references to data structures", description = "At the moment, neither network forwarding paths nor the parsing string can be passed; use the other simulation API instead.")
	@ApiResponses(value = { 
		@ApiResponse(responseCode = "200", description = ""),
		@ApiResponse(responseCode = "404", description = "At least one resource referenced by some parameter doesn't exist in the workspace.")
	})

	@RequestMapping(value = "/byParams", method = RequestMethod.POST)
	public ResponseEntity<Resources<Long>> runSimulationByParams(@RequestParam(value = "gid", required = true) Long gid,
			@RequestParam(value = "rid", required = false) Long rid,
			@RequestParam(value = "sid", required = false) Long sid,
			@RequestParam(value = "fid", required = false) List<FunctionalTypes> usableFunctionalTypes)  {

		NFV nfv = service.buildNFVFromParams(gid, rid, sid);

		try {
			// the nfv is modified in place by VerefooSerializer
			new VerefooSerializer(nfv);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"The simulation request is semantically malformed. Cause: " + e.getMessage());
		}

		Long simulationId = service.createSimulationResult(nfv, gid, rid, sid);

		String url = request.getRequestURL().toString();
		return ResponseEntity.status(HttpStatus.OK).body(
				// wrap the response with the hyperlinks
				new ResourceWrapperWithLinks<Long>()
						.addLink(url + "/" + simulationId, "self", RequestMethod.GET)
						.wrap(simulationId));

	}



	@Operation(tags = "version 1 - simulations", summary = "Get the result of a past simulation", description = "This API is not intended to run a new simulation.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = ""),
		@ApiResponse(responseCode = "404", description = "The requested simulation doesn't exist in the workspace.")
	})

	@RequestMapping(value = "/{smid}", method = RequestMethod.GET)
	public ResponseEntity<Resources<NFV>> getSimulationResult(@PathVariable("smid") Long smid) throws Exception {

		NFV result = service.getSimulationResult(smid);

		String url = request.getRequestURL().toString();
		return ResponseEntity.status(HttpStatus.OK).body(
				// wrap the response with the hyperlinks
				new ResourceWrapperWithLinks<NFV>()
						.addLink(url + "/" + smid, "self", RequestMethod.GET)
						.addLink(url, "collection", RequestMethod.POST)
						.wrap(result));
	}

}
