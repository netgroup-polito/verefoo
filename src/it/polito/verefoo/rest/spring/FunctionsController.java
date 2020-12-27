package it.polito.verefoo.rest.spring;

import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

import io.swagger.annotations.*;
import io.swagger.v3.oas.annotations.Operation;
import it.polito.verefoo.jaxb.*;


@Controller
@RequestMapping(value = "/adp/functions", consumes = {"application/xml", "application/json"}, produces = {"application/xml", "application/json"})
public class FunctionsController {

	ADPService service = new ADPService();
	
	@Autowired
	HttpServletRequest request;
	
	/**
	 * @param function it is the new function to created
	 * @return the created function
	 */
	@Operation(tags = "version 1")
	@ApiOperation(value = "createFunction", notes = "create a new function"
				)
	@RequestMapping(value = "", method = RequestMethod.POST)
	@ApiResponses(value = {
    		@ApiResponse(code = 201, message = "Created"),
    		@ApiResponse(code = 400, message = "Bad Request"),
    		})
	public ResponseEntity<FunctionalTypes> createFunction(@RequestBody FunctionalTypes functionalType) {
		StringBuffer url = request.getRequestURL();
    	FunctionalTypes created = FunctionalTypes.fromValue(service.createFunction(functionalType.name()));
    	if (created != null) {
    		String responseUrl;
    		if(url.toString().endsWith("/")) responseUrl = url.toString() + functionalType.name();
    		else responseUrl = url.toString() + "/" + functionalType.name();
    		HttpHeaders responseHeaders = new HttpHeaders();
    		try {
				responseHeaders.setLocation(new URI(responseUrl));
			} catch (URISyntaxException e) {
				throw new ResponseStatusException(
						  HttpStatus.BAD_REQUEST, "bad request"
						);
			}
        	return new ResponseEntity<FunctionalTypes>(created, responseHeaders, HttpStatus.CREATED);
    	} else
    		throw new ResponseStatusException(
					  HttpStatus.BAD_REQUEST, "bad request"
					);	
	}
	
	/**
	 * @param fid it is the name of the function to delete
	 */
	@Operation(tags = "version 1")
	@ApiOperation(value = "deleteFunction", notes = "delete an existing functio"
				)
	@RequestMapping(value = "/{fid}", method = RequestMethod.DELETE)
	@ApiResponses(value = {
    		@ApiResponse(code = 204, message = "No Content"),
    		@ApiResponse(code = 404, message = "Not Found"),
    		})
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
    @ResponseBody
	public void deleteFunction(@PathVariable("fid") FunctionalTypes functionalType) {
		
    	FunctionalTypes deleted = service.deleteFunction(functionalType.name());
    	if(deleted == null)
    		throw new ResponseStatusException(
					  HttpStatus.NOT_FOUND, "not found"
					);	
	}
	
	/**
	 * @param fid it is the name of the function to retrieve
	 * @return the requested function
	 */
	@Operation(tags = "version 1")
    @ApiOperation(value = "get Function", notes = "retrieve a function"
	)
	@RequestMapping(value = "/{fid}", method = RequestMethod.GET)
    @ApiResponses(value = {
    		@ApiResponse(code = 200, message = "OK"),
    		@ApiResponse(code = 404, message = "Not Found"),
    		})
	@ResponseBody
	String getFunction(@PathVariable("fid") FunctionalTypes functionalType) {
    	boolean found = service.getFunction(functionalType.name());
    	
    	if(!found)
    		throw new ResponseStatusException(
					  HttpStatus.NOT_FOUND, "not found"
					);	
    	return functionalType.name();
	}
}
