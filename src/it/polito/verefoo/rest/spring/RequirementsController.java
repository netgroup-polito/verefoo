package it.polito.verefoo.rest.spring;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

import io.swagger.annotations.*;
import io.swagger.v3.oas.annotations.Operation;
import it.polito.verefoo.jaxb.*;

@Controller
@RequestMapping(value = "/adp/requirements")
public class RequirementsController {

	ADPService service = new ADPService();

	@Autowired
	private HttpServletRequest request;

	/* Requirements Sets */

	/**
	 * @param requirementsSet it is the policy to create
	 * @return the created requirements set
	 */
	@Operation(tags = "version 1")
	@ApiOperation(value = "createRequirementSet", notes = "create a new requirements set", tags = "version 1")
	@RequestMapping(value = "", method = RequestMethod.POST)
	@ApiResponses(value = { @ApiResponse(code = 201, message = "Created"),
			@ApiResponse(code = 400, message = "Bad Request"), })
	public ResponseEntity<PropertyDefinition> createRequirementsSet(@RequestBody PropertyDefinition requirementsSet) {
		long pid = service.getNextRequirementsSetId();
		StringBuffer url = request.getRequestURL();
		PropertyDefinition created = service.createRequirementsSet(pid, requirementsSet);
		if (created != null) {
			String responseUrl;
			if (url.toString().endsWith("/"))
				responseUrl = url.toString() + pid;
			else
				responseUrl = url.toString() + "/" + pid;
			HttpHeaders responseHeaders = new HttpHeaders();
			try {
				responseHeaders.setLocation(new URI(responseUrl));
			} catch (URISyntaxException e) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bad request");
			}
			return new ResponseEntity<PropertyDefinition>(created, responseHeaders, HttpStatus.CREATED);
		} else
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bad request");
	}

	/**
	 * @param beforeInclusive it is the starting index
	 * @param afterInclusive  it is the ending index
	 * @return a collection of requirements sets
	 */
	@Operation(tags = "version 1")
	@ApiOperation(value = "getRequirementsSets", notes = "searches requirements sets", tags = "version 1")
	@RequestMapping(value = "", method = RequestMethod.GET)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 404, message = "Not Found"), })
	@ResponseBody
	public Collection<PropertyDefinition> getRequirementsSets(
			@RequestParam(name = "beforeInclusive", defaultValue = "1") int beforeInclusive,
			@RequestParam(name = "afterInclusive", defaultValue = "10") int afterInclusive) {
		Collection<PropertyDefinition> set = service.getRequirementsSets(beforeInclusive, afterInclusive);
		if (set.isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
		return set;
	}

	/**
	 * delete all the requirements sets
	 */
	@Operation(tags = "version 1")
	@ApiOperation(value = "deleteRequirementsSets", notes = "delete all the requirements sets", tags = "version 1")
	@RequestMapping(value = "", method = RequestMethod.DELETE)
	@ApiResponses(value = { @ApiResponse(code = 204, message = "No Content"),
			@ApiResponse(code = 404, message = "Not Found"), })
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	@ResponseBody
	public void deleteRequirementsSets() {
		boolean removed = service.deleteRequirementsSets();
		if (!removed)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
	}

	/* Requirements Set */

	/**
	 * @param rid             it is the id of the requirements set to update
	 * @param requirementsSet it is the new value of the requirements set
	 */
	@Operation(tags = "version 1")
	@ApiOperation(value = "updateRequirementsSet", notes = "update a single requirements set", tags = "version 1")
	@RequestMapping(value = "/{rid}", method = RequestMethod.PUT)
	@ApiResponses(value = { @ApiResponse(code = 204, message = "No Content"),
			@ApiResponse(code = 404, message = "Not Found"), })
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	@ResponseBody
	public void updateRequirementsSet(@PathVariable("rid") long rid, @RequestBody PropertyDefinition requirementsSet) {
		PropertyDefinition updated = service.updateRequirementsSet(rid, requirementsSet);
		if (updated == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
	}

	/**
	 * @param rid it is the id of the requirements set to retrieve
	 * @return the requested requirements set
	 */
	@Operation(tags = "version 1")
	@ApiOperation(value = "getRequirementsSet", notes = "retrieve a single requirements set", tags = "version 1")
	@RequestMapping(value = "/{rid}", method = RequestMethod.GET)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 404, message = "Not Found"), })
	@ResponseBody
	public PropertyDefinition getRequirementsSet(@PathVariable("rid") long rid) {
		PropertyDefinition set = service.getRequirementsSet(rid);
		if (set == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
		return set;
	}

	/**
	 * @param rid it is the id of the requirements set to delete
	 */
	@Operation(tags = "version 1")
	@ApiOperation(value = "deleteRequirementsSet", notes = "delete a single requirements set", tags = "version 1")
	@RequestMapping(value = "/{rid}", method = RequestMethod.DELETE)
	@ApiResponses(value = { @ApiResponse(code = 204, message = "No Content"),
			@ApiResponse(code = 404, message = "Not Found"), })
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	@ResponseBody
	public void deleteRequirementsSet(@PathVariable("rid") long rid) {
		PropertyDefinition deleted = service.deleteRequirementsSet(rid);
		if (deleted == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
	}

	/* Properties */

	/**
	 * @param rid      it is the id of the requirements set
	 * @param property it is the property to created
	 * @return the created property
	 */
	@Operation(tags = "version 1")
	@ApiOperation(value = "createProperty", notes = "create a new property", tags = "version 1")
	@RequestMapping(value = "/{rid}/properties", method = RequestMethod.POST)
	@ApiResponses(value = { @ApiResponse(code = 201, message = "Created"),
			@ApiResponse(code = 400, message = "Bad Request"), })
	public ResponseEntity<Property> createProperty(@PathVariable("rid") long rid, @RequestBody Property property) {
		Long idCreated = service.createProperty(rid, property);
		String url = request.getRequestURL().toString();
		if (idCreated != 0) {
			String responseUrl;
			if (url.toString().endsWith("/"))
				responseUrl = url.toString() + idCreated;
			else
				responseUrl = url.toString() + "/" + idCreated;
			HttpHeaders responseHeaders = new HttpHeaders();
			try {
				responseHeaders.setLocation(new URI(responseUrl));
			} catch (URISyntaxException e) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bad request");
			}
			return new ResponseEntity<Property>(property, responseHeaders, HttpStatus.CREATED);
		} else
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bad request");
	}

	/**
	 * @param rid      it is the id of the requirements set
	 * @param pid      it is the id of the property to update
	 * @param property it is the new value of the property
	 */
	@Operation(tags = "version 1")
	@ApiOperation(value = "updateProperty", notes = "update a single property", tags = "version 1")
	@RequestMapping(value = "/{rid}/properties/{pid}", method = RequestMethod.PUT)
	@ApiResponses(value = { @ApiResponse(code = 204, message = "No Content"),
			@ApiResponse(code = 404, message = "Not Found"), })
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	@ResponseBody
	public void updateProperty(@PathVariable("rid") long rid, @PathVariable("pid") long pid,
			@RequestBody Property property) {
		Property updated = service.updateProperty(rid, pid, property);
		if (updated == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
	}

	/**
	 * @param rid it is the id of the requirements
	 * @param pid it is the id of the property to retrieve
	 * @return the requested property
	 */
	@Operation(tags = "version 1")
	@ApiOperation(value = "getProperty", notes = "retrieve a single property", tags = "version 1")
	@RequestMapping(value = "/{rid}/properties/{pid}", method = RequestMethod.GET)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 404, message = "Not Found"), })
	@ResponseBody
	public Property getProperty(@PathVariable("rid") long rid, @PathVariable("pid") long pid) {
		Property property = service.getProperty(rid, pid);
		if (property == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
		return property;
	}

	/**
	 * @param rid it is the id of the requirements
	 * @param pid it is the id of the property to delete
	 */
	@Operation(tags = "version 1")
	@ApiOperation(value = "deleteProperty", notes = "delete a single property", tags = "version 1")
	@RequestMapping(value = "/{rid}/properties/{pid}", method = RequestMethod.DELETE)
	@ApiResponses(value = { @ApiResponse(code = 204, message = "No Content"),
			@ApiResponse(code = 404, message = "Not Found"), })
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	@ResponseBody
	public void deleteProperty(@PathVariable("rid") long rid, @PathVariable("pid") long pid) {
		Property property = service.deleteProperty(rid, pid);
		if (property == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
	}

}
