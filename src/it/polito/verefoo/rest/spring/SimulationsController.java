package it.polito.verefoo.rest.spring;

import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

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
import org.springframework.web.server.ResponseStatusException;

import com.google.common.base.Optional;

import io.swagger.annotations.*;
import it.polito.verefoo.jaxb.*;
import it.polito.verefoo.VerefooSerializer;


@Controller
@RequestMapping(value = "/adp/simulations")
public class SimulationsController {

	ADPService service = new ADPService();
	
	@Autowired
	private HttpServletRequest request;
	
	/**
	 * @param nfv it is the NFV object on which the simulation must be performed
	 * @return the result of the simulation
	 */
	@ApiOperation(value = "runSimulationByNFV", notes = "run a simulation providing a complete NFV")
	@RequestMapping(value = "", method = RequestMethod.POST, consumes = "application/xml")
	@ApiResponses(value = {
	    		@ApiResponse(code = 201, message = "Created"),
	    		@ApiResponse(code = 400, message = "Bad Request"),
	    		})
	public ResponseEntity<NFV> runSimulationByNFV(@RequestBody NFV nfv) {
		StringBuffer url = request.getRequestURL();
		VerefooSerializer test = null;
		try {
			test = new VerefooSerializer(nfv);
		} catch (Exception e) {
			throw new ResponseStatusException(
					  HttpStatus.BAD_REQUEST, "bad request"
					);
		}
		
		long smid = service.getNextSimulationId();
		service.addSimulationResult(nfv, smid);
		String responseUrl;
		if(url.toString().endsWith("/")) responseUrl = url.toString() + smid;
		else responseUrl = url.toString() + "/" + smid;
		HttpHeaders responseHeaders = new HttpHeaders();
		try {
			responseHeaders.setLocation(new URI(responseUrl));
		} catch (URISyntaxException e) {
			throw new ResponseStatusException(
					  HttpStatus.BAD_REQUEST, "bad request"
					);
		}
    	return new ResponseEntity<NFV>(test.getResult(), responseHeaders, HttpStatus.CREATED);
	}
	

    /**
     * @param gid it is the id of the graph
     * @param rid it is the id of the requirements set
     * @param sid it is the id of the substrate network
     * @param fid it is a list of functions name
     * @return the simulation result
     */
    @ApiOperation(value = "runSimulationByParams", notes = "run a simulation by a set of parameters"
	)
	@RequestMapping(value = "", method = RequestMethod.POST)
    @ApiResponses(value = {
    		@ApiResponse(code = 201, message = "Created"),
    		@ApiResponse(code = 400, message = "Bad Request"),
    		@ApiResponse(code = 404, message = "Not Found"),
    		})
	public ResponseEntity<NFV> runSimulationByParams(@RequestParam(value="gid", required = false) Long gid, @RequestParam(value="rid", required = false) Long rid, @RequestParam(value="sid", required = false) Long sid, @RequestParam(value="fid", required = false) List<String> fid)
	{
    	StringBuffer url = request.getRequestURL();
		VerefooSerializer test = null;
		NFV nfv = new NFV();
		
		Graph graph = service.getGraph(gid);
		PropertyDefinition requirementsSet = service.getRequirementsSet(rid);
		Constraints constraints = service.getConstraints(gid);
		
		//For the moment, only allocation + distribution 
		
		if(graph == null || requirementsSet == null){
			throw new ResponseStatusException(
					  HttpStatus.NOT_FOUND, "not found"
					);
		}
		
		if(constraints == null) {
			constraints = new Constraints();
			constraints.setNodeConstraints(new NodeConstraints());
			constraints.setLinkConstraints(new LinkConstraints());
		}
		
		
		Graphs graphs = new Graphs();
		graphs.getGraph().add(graph);
		nfv.setGraphs(graphs);
		requirementsSet.getProperty().forEach(p -> p.setGraph(gid));
		nfv.setPropertyDefinition(requirementsSet);
		nfv.setConstraints(constraints);
		
		
		try {
			test = new VerefooSerializer(nfv);
		} catch (Exception e) {
			throw new ResponseStatusException(
					  HttpStatus.BAD_REQUEST, "bad request"
					);
		}
		
		long smid = service.getNextSimulationId();
		service.addSimulationResult(nfv, smid);
		String responseUrl;
		if(url.toString().endsWith("/")) responseUrl = url.toString() + smid;
		else responseUrl = url.toString() + "/" + smid;
		HttpHeaders responseHeaders = new HttpHeaders();
		try {
			responseHeaders.setLocation(new URI(responseUrl));
		} catch (URISyntaxException e) {
			throw new ResponseStatusException(
					  HttpStatus.BAD_REQUEST, "bad request"
					);
		}
    	return new ResponseEntity<NFV>(test.getResult(), responseHeaders, HttpStatus.CREATED);
		
	}

    
    
    /**
     * @param smid it is the id of the simulation result to retrieve
     * @return the simulation result
     */
    @ApiOperation(value = "getSimulationResult", notes = "get a simulation result by id")
	@RequestMapping(value = "/{smid}", method = RequestMethod.GET)
	@ApiResponses(value = {
    		@ApiResponse(code = 200, message = "OK"),
    		@ApiResponse(code = 404, message = "Not Found"),
    		})
    @ResponseBody
    public NFV getSimulationResult(@PathVariable("smid") long smid) {
		NFV result = service.getSimulationResult(smid);
		if(result == null)
			throw new ResponseStatusException(
					  HttpStatus.NOT_FOUND, "not found"
					);
		return result;
	}
	
	

}
