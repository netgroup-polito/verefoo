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
public class SimulationsController {

	@Autowired
	private HttpServletRequest request;

	@Autowired
	SimulationService service;

	/**
	 * @param nfv it is the NFV object on which the simulation must be performed
	 * @return the result of the simulation
	 */
	@Operation(tags = "version 1 - simulations", summary = "Run a simulation by passing the actual NFV", description = "This is an all-in-one service, since the relative data structures (like graphs) are created automatically and they will be retrievable with the pertinent APIs separately.")
	@RequestMapping(value = "", method = RequestMethod.POST)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "400", description = "The NFV or the requirement set is semantically malformed. You can retry the operation or check the data.")
		})
	public ResponseEntity<Resources<Long>> runSimulationByNFV(
			@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "nfv example", required = true)

			/***
			 * , examples=@Example(value= {
			 * 
			 * @ExampleProperty(mediaType=MediaType.APPLICATION_XML, value = "<?xml
			 *                                                       version=\"1.0\"
			 *                                                       encoding=\"UTF-8\"?>\r\n"
			 *                                                       + "<NFV
			 *                                                       xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"
			 *                                                       xsi:noNamespaceSchemaLocation=\"../xsd/nfvSchema.xsd\">\r\n"
			 *                                                       + " <graphs>\r\n" + "
			 *                                                       <graph id=\"0\">\r\n" +
			 *                                                       " <node
			 *                                                       functional_type=\"WEBCLIENT\"
			 *                                                       name=\"10.0.0.1\">\r\n"
			 *                                                       + " <neighbour
			 *                                                       name=\"30.0.0.1\"/>\r\n"
			 *                                                       + " <configuration
			 *                                                       description=\"A simple
			 *                                                       description\"
			 *                                                       name=\"confA\">\r\n" +
			 *                                                       " <webclient
			 *                                                       nameWebServer=\"20.0.0.1\"/>\r\n"
			 *                                                       + "
			 *                                                       </configuration>\r\n" +
			 *                                                       " </node>\r\n" + "
			 *                                                       <node
			 *                                                       functional_type=\"WEBCLIENT\"
			 *                                                       name=\"10.0.0.2\">\r\n"
			 *                                                       + " <neighbour
			 *                                                       name=\"30.0.0.1\"/>\r\n"
			 *                                                       + " <configuration
			 *                                                       description=\"A simple
			 *                                                       description\"
			 *                                                       name=\"confA\">\r\n" +
			 *                                                       " <webclient
			 *                                                       nameWebServer=\"20.0.0.1\"/>\r\n"
			 *                                                       + "
			 *                                                       </configuration>\r\n" +
			 *                                                       " </node>\r\n" + "
			 *                                                       \r\n" + " <node
			 *                                                       functional_type=\"FIREWALL\"
			 *                                                       name=\"30.0.0.1\">\r\n"
			 *                                                       + " <neighbour
			 *                                                       name=\"10.0.0.1\"/>\r\n"
			 *                                                       + " <neighbour
			 *                                                       name=\"10.0.0.2\"/>\r\n"
			 *                                                       + " <neighbour
			 *                                                       name=\"20.0.0.1\"/>\r\n"
			 *                                                       + " <configuration
			 *                                                       description=\"A simple
			 *                                                       description\"
			 *                                                       name=\"conf1\">\r\n" +
			 *                                                       " <firewall
			 *                                                       defaultAction=\"ALLOW\"
			 *                                                       />\r\n" + "
			 *                                                       </configuration>\r\n" +
			 *                                                       " </node>\r\n" + "
			 *                                                       <node
			 *                                                       functional_type=\"WEBSERVER\"
			 *                                                       name=\"20.0.0.1\">\r\n"
			 *                                                       + " <neighbour
			 *                                                       name=\"30.0.0.1\"/>\r\n"
			 *                                                       + " <configuration
			 *                                                       description=\"A simple
			 *                                                       description\"
			 *                                                       name=\"confB\">\r\n" +
			 *                                                       " <webserver>\r\n" + "
			 *                                                       <name>b</name>\r\n" + "
			 *                                                       </webserver>\r\n" + "
			 *                                                       </configuration>\r\n" +
			 *                                                       " </node>\r\n" + "
			 *                                                       </graph>\r\n" + "
			 *                                                       </graphs>\r\n" + "
			 *                                                       <Constraints>\r\n" + "
			 *                                                       <NodeConstraints>\r\n"
			 *                                                       + "
			 *                                                       </NodeConstraints>\r\n"
			 *                                                       + "
			 *                                                       <LinkConstraints/>\r\n"
			 *                                                       + " </Constraints>\r\n"
			 *                                                       + "
			 *                                                       <PropertyDefinition>\r\n"
			 *                                                       + " <Property
			 *                                                       graph=\"0\"
			 *                                                       name=\"IsolationProperty\"
			 *                                                       src=\"10.0.0.1\"
			 *                                                       dst=\"20.0.0.1\"/>\r\n"
			 *                                                       + " <Property
			 *                                                       graph=\"0\"
			 *                                                       name=\"IsolationProperty\"
			 *                                                       src=\"10.0.0.2\"
			 *                                                       dst=\"20.0.0.1\"/>
			 *                                                       \r\n" + "
			 *                                                       </PropertyDefinition>\r\n"
			 *                                                       + "
			 *                                                       <ParsingString></ParsingString>\r\n"
			 *                                                       + "</NFV>")}))
			 */
			@RequestBody NFV nfv, @RequestParam(value = "usableNetworkFunctions", required = false) List<FunctionalTypes> usableFunctionalTypes) {

		try {
			// the nfv is modified in place by VerefooSerializer
			new VerefooSerializer(nfv);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"The simulation request is semantically malformed. Cause: " + e.getMessage());
		}

		// long smid = service.getNextSimulationId();
		// service.addSimulationResult(nfv, smid);
		// String responseUrl;
		// if (url.toString().endsWith("/"))
		// 	responseUrl = url.toString() + smid;
		// else
		// 	responseUrl = url.toString() + "/" + smid;
		// HttpHeaders responseHeaders = new HttpHeaders();
		// try {
		// 	responseHeaders.setLocation(new URI(responseUrl));
		// } catch (URISyntaxException e) {
		// 	throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bad request");
		// }
		// return new ResponseEntity<NFV>(test.getResult(), responseHeaders, HttpStatus.CREATED);

		Long simulationId = service.createSimulationResult(nfv);

		String url = request.getRequestURL().toString();
		return ResponseEntity.status(HttpStatus.OK).body(
				// wrap the response with the hyperlinks
				new ResourceWrapperWithLinks<Long>()
						.addLink(url + "/" + simulationId, "self", RequestMethod.GET)
						.wrap(simulationId));
	}

	/**
	 * @param gid                   it is the id of the graph
	 * @param rid                   it is the id of the requirements set
	 * @param sid                   it is the id of the substrate network
	 * @param usableFunctionalTypes it is a list of functions name
	 * @return the simulation result
	 * @throws Exception
	 */
	@Operation(tags = "version 1 - simulations", summary = "Run a simulation by passing references to data structures", description = "At the moment, neither network forwarding paths nor the parsing string can be passed; use the other simulation API instead.")
	@RequestMapping(value = "/byParams", method = RequestMethod.POST)
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "400", description = "One of the parameters is semantically malformed. You can retry the operation or check the data."),
			@ApiResponse(responseCode = "404", description = "At least one resource referenced by some parameter has not been found. You can retry the operation or check that the resources actually exist.") })

	public ResponseEntity<Resources<Long>> runSimulationByParams(@RequestParam(value = "gid", required = true) Long gid,
			@RequestParam(value = "rid", required = false) Long rid,
			@RequestParam(value = "sid", required = false) Long sid
	/*
	 * This parameter is not used yet: @RequestParam(value = "fid", required =
	 * false) List<FunctionalTypes> fid
	 */) throws Exception {

		NFV nfv = service.buildNFVFromParams(gid, sid, rid);
		// from now on, the controller behaves like the other simulation API

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

	/**
	 * @param smid it is the id of the simulation result to retrieve
	 * @return the simulation result
	 * @throws Exception
	 */
	@Operation(tags = "version 1 - simulations", summary = "Get the result of a past simulation", description = "This API is not intended to run a new simulation.")
	@RequestMapping(value = "/{smid}", method = RequestMethod.GET)
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "404", description = "The requested simulation has never been run. You can retry the operation or run a simulation first.") })

	public ResponseEntity<Resources<NFV>> getSimulationResult(@PathVariable("smid") long smid) throws Exception {

		NFV result = service.getSimulationResult(smid);

		String url = request.getRequestURL().toString();
		return ResponseEntity.status(HttpStatus.OK).body(
				// wrap the response with the hyperlinks
				new ResourceWrapperWithLinks<NFV>()
						.addLink(url + "/" + smid, "self", RequestMethod.GET)
						.addLink(url, "new", RequestMethod.POST)
						.wrap(result));
	}

}
