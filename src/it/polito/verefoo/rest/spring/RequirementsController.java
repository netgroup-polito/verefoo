package it.polito.verefoo.rest.spring;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.polito.verefoo.jaxb.Property;
import it.polito.verefoo.jaxb.PropertyDefinition;

@RestController
@RequestMapping(value = "/adp/requirements", consumes = {"application/xml", "application/json"}, produces = {"application/xml", "application/json"})
public class RequirementsController {

	ADPService service = new ADPService();

	@Autowired
	private HttpServletRequest request;

	/* Requirements Sets */

	/**
	 * @param requirementsSet it is the policy to create
	 * @return the created requirements set
	 */
	@Operation(tags = "version 1 - requirements", summary = "Create a requirement set", description = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Created"),
			@ApiResponse(responseCode = "400", description = "The passed requirement set is semantically malformed. You can retry the operation or check the data."),
			@ApiResponse(responseCode = "404", description = "The referenced graph doesn't exist. You can first create a graph or refer to another one.") })
	public ResponseEntity<Resources<Void>> createRequirementsSet(@RequestBody PropertyDefinition requirementsSet) {
		// long pid = service.getNextRequirementsSetId();
		// StringBuffer url = request.getRequestURL();
		// PropertyDefinition created = service.createRequirementsSet(pid, requirementsSet);
		// if (created != null) {
		// 	String responseUrl;
		// 	if (url.toString().endsWith("/"))
		// 		responseUrl = url.toString() + pid;
		// 	else
		// 		responseUrl = url.toString() + "/" + pid;
		// 	HttpHeaders responseHeaders = new HttpHeaders();
		// 	try {
		// 		responseHeaders.setLocation(new URI(responseUrl));
		// 	} catch (URISyntaxException e) {
		// 		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bad request");
		// 	}
		// 	return new ResponseEntity<PropertyDefinition>(created, responseHeaders, HttpStatus.CREATED);
		// } else
		// 	throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bad request");
		String url = request.getRequestURL().toString();
		return ResponseEntity.status(HttpStatus.OK).body(
				// wrap the response with the hyperlinks
				new ResourceWrapperWithLinks<Void>()
						.addLink(url, "new", RequestMethod.POST)
						.addLink(url, "self", RequestMethod.GET)
						.addLink(url, "self", RequestMethod.DELETE)
						.wrap(null));
	}

	/**
	 * @param beforeInclusive it is the starting index
	 * @param afterInclusive  it is the ending index
	 * @return a collection of requirements sets
	 */
	@Operation(tags = "version 1 - requirements", summary = "Get all the requirement sets", description = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "404", description = "No requirement set has been found. You can retry the operation or create a requirement set.")
		})
	
	public ResponseEntity<Resources<PropertyDefinition>> getRequirementsSets() {
		// Collection<PropertyDefinition> set = service.getRequirementsSets(beforeInclusive, afterInclusive);
		// if (set.isEmpty())
		// 	throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
		// return set;
		PropertyDefinition propertyDefinitions = null;
		String url = request.getRequestURL().toString();
		return ResponseEntity.status(HttpStatus.OK).body(
				// wrap the response with the hyperlinks
				new ResourceWrapperWithLinks<PropertyDefinition>()
						.addLink(url, "new", RequestMethod.POST)
						.addLink(url, "self", RequestMethod.GET)
						.addLink(url, "self", RequestMethod.DELETE)
						.wrap(propertyDefinitions));
	}

	/**
	 * delete all the requirements sets
	 */
	@Operation(tags = "version 1 - requirements", summary = "Delete all the requirement sets", description = "Be careful before cleaning the whole workbench of requirements.")
	@RequestMapping(value = "", method = RequestMethod.DELETE)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "404", description = "No requirement set has been found. You can retry the operation or create a requirement set.")
		})
	
	public ResponseEntity<Resources<Void>> deleteRequirementsSets() {
		// boolean removed = service.deleteRequirementsSets();
		// if (!removed)
		// 	throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
		String url = request.getRequestURL().toString();
		return ResponseEntity.status(HttpStatus.OK).body(
				// wrap the response with the hyperlinks
				new ResourceWrapperWithLinks<Void>()
						.addLink(url, "new", RequestMethod.POST)
						.addLink(url, "self", RequestMethod.GET)
						.addLink(url, "self", RequestMethod.DELETE)
						.wrap(null));
	}

	/* Requirements Set */

	/**
	 * @param rid             it is the id of the requirements set to update
	 * @param requirementsSet it is the new value of the requirements set
	 */
	@Operation(tags = "version 1 - requirements", summary = "Update a requirement set", description = "")
	@RequestMapping(value = "/{rid}", method = RequestMethod.PUT)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "404", description = "The requirement set has not been found or the referenced graph doesn't exist. You can retry the operation or first create a requirement set or refer to another graph.")

		})
	
	public ResponseEntity<Resources<Void>> updateRequirementsSet(@PathVariable("rid") long rid, @RequestBody PropertyDefinition requirementsSet) {
		// PropertyDefinition updated = service.updateRequirementsSet(rid, requirementsSet);
		// if (updated == null)
		// 	throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
		String url = request.getRequestURL().substring(0, request.getRequestURL().lastIndexOf("/"));
		return ResponseEntity.status(HttpStatus.OK).body(
				// wrap the response with the hyperlinks
				new ResourceWrapperWithLinks<Void>()
						.addLink(url + "/" + rid, "self", RequestMethod.GET)
						.addLink(url + "/" + rid, "self", RequestMethod.PUT)
						.addLink(url + "/" + rid, "self", RequestMethod.DELETE)
						.addLink(url, "list", RequestMethod.GET)
						.wrap(null));
	}

	/**
	 * @param rid it is the id of the requirements set to retrieve
	 * @return the requested requirements set
	 */
	@Operation(tags = "version 1 - requirements", summary = "Get a requirement set", description = "")
	@RequestMapping(value = "/{rid}", method = RequestMethod.GET)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "404", description = "The requirement set has not been found. You can retry the operation or create a requirement set.")
		})
	
	public ResponseEntity<Resources<PropertyDefinition>> getRequirementsSet(@PathVariable("rid") long rid) {
		// PropertyDefinition set = service.getRequirementsSet(rid);
		// if (set == null)
		// 	throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
		// return set;
		PropertyDefinition propertyDefinition = null;
		String url = request.getRequestURL().substring(0, request.getRequestURL().lastIndexOf("/"));
		return ResponseEntity.status(HttpStatus.OK).body(
				// wrap the response with the hyperlinks
				new ResourceWrapperWithLinks<PropertyDefinition>()
						.addLink(url + "/" + rid, "self", RequestMethod.GET)
						.addLink(url + "/" + rid, "self", RequestMethod.PUT)
						.addLink(url + "/" + rid, "self", RequestMethod.DELETE)
						.addLink(url, "list", RequestMethod.GET)
						.wrap(propertyDefinition));
	}

	/**
	 * @param rid it is the id of the requirements set to delete
	 */
	@Operation(tags = "version 1 - requirements", summary = "Delete a requirement set", description = "")
	@RequestMapping(value = "/{rid}", method = RequestMethod.DELETE)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "204", description = "No Content"),
			@ApiResponse(responseCode = "404", description = "The requirement set has not been found. You can retry the operation or create a requirement set.")
		})
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	
	public ResponseEntity<Resources<Void>> deleteRequirementsSet(@PathVariable("rid") long rid) {
		// PropertyDefinition deleted = service.deleteRequirementsSet(rid);
		// if (deleted == null)
		// 	throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");

		String url = request.getRequestURL().substring(0, request.getRequestURL().lastIndexOf("/"));
		return ResponseEntity.status(HttpStatus.OK).body(
				// wrap the response with the hyperlinks
				new ResourceWrapperWithLinks<Void>()
						.addLink(url + "/" + rid, "self", RequestMethod.GET)
						.addLink(url + "/" + rid, "self", RequestMethod.PUT)
						.addLink(url + "/" + rid, "self", RequestMethod.DELETE)
						.addLink(url, "list", RequestMethod.GET)
						.wrap(null));
	}

	/* Properties */

	/**
	 * @param rid      it is the id of the requirements set
	 * @param property it is the property to created
	 * @return the created property
	 */
	@Operation(tags = "version 1 - requirements", summary = "Create another property in a requirement set", description = "")
	@RequestMapping(value = "/{rid}/properties", method = RequestMethod.POST)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Created"),
			@ApiResponse(responseCode = "400", description = "The passed property is semantically malformed. You can retry the operation or check the data."),
			@ApiResponse(responseCode = "404", description = "The requirement set has not been found or the referred graph doesn't exist. You can retry the operation or create/refer to another requirement set/graph.")
		})
	public ResponseEntity<Resources<Void>> createProperty(@PathVariable("rid") long rid, @RequestBody Property property) {
		// Long idCreated = service.createProperty(rid, property);
		// String url = request.getRequestURL().toString();
		// if (idCreated != 0) {
		// 	String responseUrl;
		// 	if (url.toString().endsWith("/"))
		// 		responseUrl = url.toString() + idCreated;
		// 	else
		// 		responseUrl = url.toString() + "/" + idCreated;
		// 	HttpHeaders responseHeaders = new HttpHeaders();
		// 	try {
		// 		responseHeaders.setLocation(new URI(responseUrl));
		// 	} catch (URISyntaxException e) {
		// 		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bad request");
		// 	}
		// 	return new ResponseEntity<Property>(property, responseHeaders, HttpStatus.CREATED);
		// } else
		// 	throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bad request");
		String url = request.getRequestURL()
			.substring(0, request.getRequestURL().lastIndexOf("/"))
			.substring(0, request.getRequestURL().lastIndexOf("/"));
		return ResponseEntity.status(HttpStatus.OK).body(
				// wrap the response with the hyperlinks
				new ResourceWrapperWithLinks<Void>()
						.addLink(url + "/" + rid + "/properties", "new", RequestMethod.POST)
						.addLink(url + "/" + rid, "list", RequestMethod.GET)
						.addLink(url, "list", RequestMethod.GET)
						.wrap(null));
	}

	/**
	 * @param rid      it is the id of the requirements set
	 * @param pid      it is the id of the property to update
	 * @param property it is the new value of the property
	 */
	@Operation(tags = "version 1 - requirements", summary = "Update a property in a requirement set", description = "")
	@RequestMapping(value = "/{rid}/properties/{pid}", method = RequestMethod.PUT)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "204", description = "No Content"),
			@ApiResponse(responseCode = "404", description = "The requirement set, the property or the referred graph has not been found. You can retry the operation or create/refer to a/another requirement set/property/graph.")
		})
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	
	public ResponseEntity<Resources<Void>> updateProperty(@PathVariable("rid") long rid, @PathVariable("pid") long pid,
			@RequestBody Property property) {
		// Property updated = service.updateProperty(rid, pid, property);
		// if (updated == null)
		// 	throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
		String url = request.getRequestURL()
			.substring(0, request.getRequestURL().lastIndexOf("/"))
			.substring(0, request.getRequestURL().lastIndexOf("/"))
			.substring(0, request.getRequestURL().lastIndexOf("/"));
		return ResponseEntity.status(HttpStatus.OK).body(
				// wrap the response with the hyperlinks
				new ResourceWrapperWithLinks<Void>()
						.addLink(url + "/" + rid + "/properties/" + rid, "self", RequestMethod.GET)
						.addLink(url + "/" + rid + "/properties/" + rid, "self", RequestMethod.PUT)
						.addLink(url + "/" + rid + "/properties/" + rid, "self", RequestMethod.DELETE)
						.addLink(url + "/" + rid + "/properties", "new", RequestMethod.POST)
						.addLink(url + "/" + rid, "list", RequestMethod.GET)
						.addLink(url, "list", RequestMethod.GET)
						.wrap(null));
	}

	/**
	 * @param rid it is the id of the requirements
	 * @param pid it is the id of the property to retrieve
	 * @return the requested property
	 */
	@Operation(tags = "version 1 - requirements", summary = "Get a property from a requirement set", description = "")
	@RequestMapping(value = "/{rid}/properties/{pid}", method = RequestMethod.GET)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "404", description = "The requirement set or the property has not been found. You can retry the operation or create a requirement set/property.")
		})
	
	public ResponseEntity<Resources<Property>> getProperty(@PathVariable("rid") long rid, @PathVariable("pid") long pid) {
		// Property property = service.getProperty(rid, pid);
		// if (property == null)
		// 	throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
		// return property;
		Property property = null;
		String url = request.getRequestURL()
			.substring(0, request.getRequestURL().lastIndexOf("/"))
			.substring(0, request.getRequestURL().lastIndexOf("/"))
			.substring(0, request.getRequestURL().lastIndexOf("/"));
		return ResponseEntity.status(HttpStatus.OK).body(
				// wrap the response with the hyperlinks
				new ResourceWrapperWithLinks<Property>()
						.addLink(url + "/" + rid + "/properties/" + rid, "self", RequestMethod.GET)
						.addLink(url + "/" + rid + "/properties/" + rid, "self", RequestMethod.PUT)
						.addLink(url + "/" + rid + "/properties/" + rid, "self", RequestMethod.DELETE)
						.addLink(url + "/" + rid + "/properties", "new", RequestMethod.POST)
						.addLink(url + "/" + rid, "list", RequestMethod.GET)
						.addLink(url, "list", RequestMethod.GET)
						.wrap(property));
	}

	/**
	 * @param rid it is the id of the requirements
	 * @param pid it is the id of the property to delete
	 */
	@Operation(tags = "version 1 - requirements", summary = "Delete a property from a requirement set", description = "")
	@RequestMapping(value = "/{rid}/properties/{pid}", method = RequestMethod.DELETE)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "404", description = "The requirement set or the property has not been found. You can retry the operation or create a requirement set/property.")
		})
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	
	public ResponseEntity<Resources<Void>> deleteProperty(@PathVariable("rid") long rid, @PathVariable("pid") long pid) {
		// Property property = service.deleteProperty(rid, pid);
		// if (property == null)
		// 	throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");

		String url = request.getRequestURL()
			.substring(0, request.getRequestURL().lastIndexOf("/"))
			.substring(0, request.getRequestURL().lastIndexOf("/"))
			.substring(0, request.getRequestURL().lastIndexOf("/"));
		return ResponseEntity.status(HttpStatus.OK).body(
				// wrap the response with the hyperlinks
				new ResourceWrapperWithLinks<Void>()
						.addLink(url + "/" + rid + "/properties", "new", RequestMethod.POST)
						.addLink(url + "/" + rid, "list", RequestMethod.GET)
						.addLink(url, "list", RequestMethod.GET)
						.wrap(null));
	}

}
