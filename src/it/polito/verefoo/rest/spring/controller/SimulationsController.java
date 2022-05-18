package it.polito.verefoo.rest.spring.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.polito.verefoo.VerefooSerializer;
import it.polito.verefoo.extra.BadGraphError;
import it.polito.verefoo.jaxb.FunctionalTypes;
import it.polito.verefoo.jaxb.NFV;
import it.polito.verefoo.rest.spring.ResourceWrapperWithLinks;
import it.polito.verefoo.rest.spring.service.SimulationService;


@RestController
@RequestMapping(value = "/adp/simulations", produces = {"application/xml", "application/json" })
@ApiResponses(value = {
	@ApiResponse(responseCode = "400", description = "The provided resource is not compliant with the data model.")
})
public class SimulationsController {

	@Autowired
	private HttpServletRequest request;

	@Autowired
	SimulationService service;



	@Operation(tags = "simulations", summary = "Run a simulation by passing the actual NFV", description = "This is an all-in-one service, since the relative data structures (like graphs) are created automatically and they will be retrievable with the pertinent APIs separately.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "")
	})

	@RequestMapping(value = "", consumes = { "application/xml", "application/json" }, method = RequestMethod.POST)
	public ResponseEntity<Resources<NFV>> runSimulationByNFV(@RequestBody NFV nfv, @RequestParam(value = "fid", required = false) List<FunctionalTypes> usableFunctionalTypes) {
		try {
			// the nfv is modified in place by VerefooSerializer
			new VerefooSerializer(nfv);
		} catch (BadGraphError e) {
			throw verefooCoreExceptionBuilder(e);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}

		//Long simulationId = service.createSimulationResult(nfv);

		String url = request.getRequestURL().toString();
		return ResponseEntity.status(HttpStatus.OK).body(
				// wrap the response with the hyperlinks
				new ResourceWrapperWithLinks<NFV>()
				.addLink(url + "/" + 1, "self", RequestMethod.GET)
				.addLink(url, "collection", RequestMethod.POST)
				.wrap(nfv));
	}



	@Operation(tags = "simulations", summary = "Run a simulation by passing references to data structures", description = "At the moment, neither network forwarding paths nor the parsing string can be passed; use the other simulation API instead.")
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
		} catch (BadGraphError e) {
			throw verefooCoreExceptionBuilder(e);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
		}

		Long simulationId = service.createSimulationResult(nfv, gid, rid, sid);

		String url = request.getRequestURL().toString();
		return ResponseEntity.status(HttpStatus.OK).body(
				// wrap the response with the hyperlinks
				new ResourceWrapperWithLinks<Long>()
						.addLink(url + "/" + simulationId, "self", RequestMethod.GET)
						.wrap(simulationId));

	}


	@CrossOrigin
	@Operation(tags = "simulations", summary = "Get the result of a past simulation", description = "This API is not intended to run a new simulation.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = ""),
		@ApiResponse(responseCode = "404", description = "The requested simulation doesn't exist in the workspace.")
	})

	@RequestMapping(value = "/{smid}", method = RequestMethod.GET)
	public ResponseEntity<Resources<NFV>> getSimulationResult(@PathVariable("smid") Long smid) {

		NFV result = service.getSimulationResult(smid);

		String url = request.getRequestURL().toString();
		url = url.substring(0, url.lastIndexOf("/"));


		return ResponseEntity.status(HttpStatus.OK)
				.body(
				// wrap the response with the hyperlinks
				new ResourceWrapperWithLinks<NFV>()
						.addLink(url + "/" + smid, "self", RequestMethod.GET)
						.addLink(url, "collection", RequestMethod.POST)
						.wrap(result));
	}


	@Operation(tags = "simulations", summary = "Get the result of a past simulation", description = "This API is not intended to run a new simulation.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = ""),
		@ApiResponse(responseCode = "404", description = "No simulation results currently exist in the workspace. You can start by performing a new simulation.")
	})

	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseEntity<Resources<List<NFV>>> getSimulationResults() {


		List<NFV> results = service.getSimulationResults();

		String url = request.getRequestURL().toString();
		return ResponseEntity.status(HttpStatus.OK).body(
				// wrap the response with the hyperlinks
				new ResourceWrapperWithLinks<List<NFV>>()
						.addLink(url, "collection", RequestMethod.POST)
						.wrap(results));
	}



	@Operation(tags = "simulations", summary = "Delete the result of a past simulation", description = "")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = ""),
		@ApiResponse(responseCode = "404", description = "The requested simulation doesn't exist in the workspace.")
	})

	@RequestMapping(value = "/{smid}", method = RequestMethod.DELETE)
	public ResponseEntity<Resources<Void>> deleteSimulationResult(@PathVariable("smid") Long smid) {

		service.deleteSimulationResult(smid);

		String url = request.getRequestURL().toString();
		url = url.substring(0, url.lastIndexOf("/"));
		return ResponseEntity.status(HttpStatus.OK).body(
				// wrap the response with the hyperlinks
				new ResourceWrapperWithLinks<Void>()
						.addLink(url, "collection", RequestMethod.POST)
						.wrap(null));
	}


	@Operation(tags = "simulations", summary = "Delete all the results of past simulations", description = "")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = ""),
		@ApiResponse(responseCode = "304", description = "No simulation results are in the workspace at all.")
	})

	@RequestMapping(value = "", method = RequestMethod.DELETE)
	public ResponseEntity<Resources<Void>> deleteSimulationResults() {

		service.deleteSimulationResults();

		String url = request.getRequestURL().toString();
		url = url.substring(0, url.lastIndexOf("/"));
		return ResponseEntity.status(HttpStatus.OK).body(
				// wrap the response with the hyperlinks
				new ResourceWrapperWithLinks<Void>()
						.addLink(url, "collection", RequestMethod.POST)
						.wrap(null));
	}


	/**
	 * Beware that with some HTTP status codes (like {@literal INTERNAL_SERVER_ERROR} ) the descriptive message is
	 * automatically neglected by HTTP parsers
	 * @param badGraphError
	 * @return an exception that should be thrown (the exception is returned, but not thrown by this method)
	 */
	private ResponseStatusException verefooCoreExceptionBuilder(BadGraphError badGraphError) {
		HttpStatus returnStatus;
		if (badGraphError.getE() != null) {
			switch (badGraphError.getE()) {
			case INVALID_NODE_CONFIGURATION:
			case INVALID_PARSING_STRING:
			case INVALID_PROPERTY_DEFINITION:
			case INVALID_SERVICE_GRAPH:
			case INVALID_VPN_CONFIGURATION:
			case NO_MIDDLE_HOST_DEFINED:
				returnStatus = HttpStatus.BAD_REQUEST;
				break;
			case INVALID_PHY_SERVER_CLIENT_CONF:
			case INVALID_SERVER_CLIENT_CONF:
				returnStatus = HttpStatus.FORBIDDEN;
				break;
			case XML_VALIDATION_ERROR:
				returnStatus = HttpStatus.UNPROCESSABLE_ENTITY;
				break;
			case PHY_CLIENT_SERVER_NOT_CONNECTED:
				returnStatus = HttpStatus.SERVICE_UNAVAILABLE;
				break;
			case INTERNAL_SERVER_ERROR:
			default:
				returnStatus = HttpStatus.INTERNAL_SERVER_ERROR;
				break;
			}
		} else {
			returnStatus = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		
		String returnMessage = "";
		if (badGraphError.getE() != null) {
			returnMessage += "Type of error: " + badGraphError.getE() + ". ";
		}
		returnMessage += "Cause: " + badGraphError.getMessage();
	
		return new ResponseStatusException(returnStatus, returnMessage);
	}

}
