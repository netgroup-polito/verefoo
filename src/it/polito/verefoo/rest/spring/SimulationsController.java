package it.polito.verefoo.rest.spring;

import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlElement;

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

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Example;
import io.swagger.annotations.ExampleProperty;
import it.polito.verefoo.jaxb.*;
import it.polito.verefoo.VerefooSerializer;

import io.swagger.annotations.ApiParam;

@Controller
@RequestMapping(value = "/adp/simulations")
public class SimulationsController {

	ADPService service = new ADPService();
	
	@Autowired
	private HttpServletRequest request;
	
	

	/**
	 * @param nfv it is the NFV object on which the simulation must be performed
	 * @param alg it is the algorithm used to execute Verefoo, can be AP (Atomic Predicates) or MF (Maximal Flows)
	 * @return the result of the simulation
	 */
	@ApiOperation(value = "runSimulationByNFV", notes = "run a simulation providing a complete NFV",hidden=false)
	@RequestMapping(value = "", method = RequestMethod.POST, consumes = "application/xml")
	@ApiResponses(value = {
	    		@ApiResponse(code = 201, message = "Created"),
	    		@ApiResponse(code = 400, message = "Bad Request"),
	    		})
	public ResponseEntity<NFV> runSimulationByNFV(  @ApiParam(name="nfv",value = "example", required = true)
	
	/***, examples=@Example(value= {
	        @ExampleProperty(mediaType=MediaType.APPLICATION_XML, value = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + 
	                                		"<NFV xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"../xsd/nfvSchema.xsd\">\r\n" + 
	                                		"  <graphs>\r\n" + 
	                                		"    <graph id=\"0\">\r\n" + 
	                                		"     <node functional_type=\"WEBCLIENT\" name=\"10.0.0.1\">\r\n" + 
	                                		"        <neighbour name=\"30.0.0.1\"/>\r\n" + 
	                                		"        <configuration description=\"A simple description\" name=\"confA\">\r\n" + 
	                                		"          <webclient nameWebServer=\"20.0.0.1\"/>\r\n" + 
	                                		"        </configuration>\r\n" + 
	                                		"      </node>\r\n" + 
	                                		"      <node functional_type=\"WEBCLIENT\" name=\"10.0.0.2\">\r\n" + 
	                                		"        <neighbour name=\"30.0.0.1\"/>\r\n" + 
	                                		"        <configuration description=\"A simple description\" name=\"confA\">\r\n" + 
	                                		"          <webclient nameWebServer=\"20.0.0.1\"/>\r\n" + 
	                                		"        </configuration>\r\n" + 
	                                		"      </node>\r\n" + 
	                                		"      \r\n" + 
	                                		"      <node functional_type=\"FIREWALL\" name=\"30.0.0.1\">\r\n" + 
	                                		"        <neighbour name=\"10.0.0.1\"/>\r\n" + 
	                                		"        <neighbour name=\"10.0.0.2\"/>\r\n" + 
	                                		"		<neighbour name=\"20.0.0.1\"/>\r\n" + 
	                                		"        <configuration description=\"A simple description\" name=\"conf1\">\r\n" + 
	                                		"            <firewall defaultAction=\"ALLOW\" />\r\n" + 
	                                		"        </configuration>\r\n" + 
	                                		"      </node>\r\n" + 
	                                		"      <node functional_type=\"WEBSERVER\" name=\"20.0.0.1\">\r\n" + 
	                                		"        <neighbour name=\"30.0.0.1\"/>\r\n" + 
	                                		"        <configuration description=\"A simple description\" name=\"confB\">\r\n" + 
	                                		"          <webserver>\r\n" + 
	                                		"          	<name>b</name>\r\n" + 
	                                		"          </webserver>\r\n" + 
	                                		"        </configuration>\r\n" + 
	                                		"      </node>\r\n" + 
	                                		"    </graph>\r\n" + 
	                                		"  </graphs>\r\n" + 
	                                		"  <Constraints>\r\n" + 
	                                		"	  <NodeConstraints>\r\n" + 
	                                		"	  </NodeConstraints>\r\n" + 
	                                		"	  <LinkConstraints/>\r\n" + 
	                                		"  </Constraints>\r\n" + 
	                                		"  <PropertyDefinition>\r\n" + 
	                                		"		<Property graph=\"0\" name=\"IsolationProperty\" src=\"10.0.0.1\" dst=\"20.0.0.1\"/>\r\n" + 
	                                		"		<Property graph=\"0\" name=\"IsolationProperty\" src=\"10.0.0.2\" dst=\"20.0.0.1\"/> 		 				\r\n" + 
	                                		"  </PropertyDefinition>\r\n" + 
	                                		"  <ParsingString></ParsingString>\r\n" + 
	                                		"</NFV>")})) */ @RequestBody NFV nfv, @RequestParam(name = "Algorithm") String alg) {
		StringBuffer url = request.getRequestURL();
		VerefooSerializer test = null;
		try {
			test = new VerefooSerializer(nfv,alg);
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
    @ApiOperation(value = "runSimulationByParams", notes = "run a simulation by a set of parameters",hidden=true
	)
	@RequestMapping(value = "", method = RequestMethod.POST, consumes = "text/plain")
    @ApiResponses(value = {
    		@ApiResponse(code = 201, message = "Created"),
    		@ApiResponse(code = 400, message = "Bad Request"),
    		@ApiResponse(code = 404, message = "Not Found"),
    		})
    
	public ResponseEntity<NFV> runSimulationByParams(@RequestParam(value="gid", required = false) Long gid, @RequestParam(value="rid", required = false) Long rid, @RequestParam(value="sid", required = false) Long sid, @RequestParam(value="fid", required = false) List<String> fid, @RequestParam(name = "Algorithm") String alg)
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
			test = new VerefooSerializer(nfv,alg);
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
