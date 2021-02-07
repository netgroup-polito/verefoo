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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.polito.verefoo.jaxb.Property;
import it.polito.verefoo.jaxb.PropertyDefinition;
import it.polito.verefoo.rest.spring.ResourceWrapperWithLinks;
import it.polito.verefoo.rest.spring.service.RequirementService;

@RestController
@RequestMapping(value = "/adp/requirements", consumes = { "application/xml", "application/json" }, produces = {
		"application/xml", "application/json" })
public class RequirementsController {

	@Autowired
	private HttpServletRequest request;

	@Autowired
	RequirementService service;

	/* Requirements Sets */

	/**
	 * @param beforeInclusive it is the starting index
	 * @param afterInclusive  it is the ending index
	 * @return a collection of requirements sets
	 */
	@Operation(tags = "version 1 - requirements", summary = "Get all the requirement sets", description = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "404", description = "No requirement set has been found. You can retry the operation or create a requirement set.") })

	public ResponseEntity<Resources<List<PropertyDefinition>>> getRequirementsSets() {

		List<PropertyDefinition> requirementsSets = service.getRequirementsSets();

		String url = request.getRequestURL().toString();
		return ResponseEntity.status(HttpStatus.OK).body(
				// wrap the response with the hyperlinks
				new ResourceWrapperWithLinks<List<PropertyDefinition>>().addLink(url, "new", RequestMethod.POST)
						.addLink(url, "self", RequestMethod.GET).addLink(url, "self", RequestMethod.DELETE)
						.wrap(requirementsSets));
	}

	/**
	 * delete all the requirements sets
	 */
	@Operation(tags = "version 1 - requirements", summary = "Delete all the requirement sets", description = "Be careful before cleaning the whole workbench of requirements.")
	@RequestMapping(value = "", method = RequestMethod.DELETE)
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "404", description = "No requirement set has been found. You can retry the operation or create a requirement set.") })

	public ResponseEntity<Resources<Void>> deleteRequirementsSets() {

		service.deleteRequirementsSets();

		String url = request.getRequestURL().toString();
		return ResponseEntity.status(HttpStatus.OK).body(
				// wrap the response with the hyperlinks
				new ResourceWrapperWithLinks<Void>().addLink(url, "new", RequestMethod.POST)
						.addLink(url, "self", RequestMethod.GET).addLink(url, "self", RequestMethod.DELETE).wrap(null));
	}

	/* Requirements Set */

	/**
	 * @param requirementsSet it is the policy to create
	 * @return the created requirements set
	 */
	@Operation(tags = "version 1 - requirements", summary = "Create a requirement set", description = "")
	@RequestMapping(value = "", method = RequestMethod.POST)
	@ApiResponses(value = { @ApiResponse(responseCode = "201", description = "Created"),
			@ApiResponse(responseCode = "400", description = "The passed requirement set is semantically malformed. You can retry the operation or check the data."),
			@ApiResponse(responseCode = "404", description = "The referenced graph doesn't exist. You can first create a graph or refer to another one.") })
	public ResponseEntity<Resources<Long>> createRequirementsSet(@RequestBody PropertyDefinition requirementsSet) {

		Long requirementsSetId = service.createRequirementsSet(requirementsSet);

		String url = request.getRequestURL().toString();
		return ResponseEntity.status(HttpStatus.CREATED).body(
				// wrap the response with the hyperlinks
				new ResourceWrapperWithLinks<Long>().addLink(url, "new", RequestMethod.POST)
						.addLink(url, "self", RequestMethod.GET).addLink(url, "self", RequestMethod.DELETE)
						.wrap(requirementsSetId));
	}

	/**
	 * @param rid             it is the id of the requirements set to update
	 * @param requirementsSet it is the new value of the requirements set
	 */
	@Operation(tags = "version 1 - requirements", summary = "Update a requirement set", description = "")
	@RequestMapping(value = "/{rid}", method = RequestMethod.PUT)
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "404", description = "The requirement set has not been found or the referenced graph doesn't exist. You can retry the operation or first create a requirement set or refer to another graph.")

	})

	public ResponseEntity<Resources<Void>> updateRequirementsSet(@PathVariable("rid") Long rid,
			@RequestBody PropertyDefinition requirementsSet) {

		service.updateRequirementsSet(rid, requirementsSet);

		String url = request.getRequestURL().substring(0, request.getRequestURL().lastIndexOf("/"));
		return ResponseEntity.status(HttpStatus.OK).body(
				// wrap the response with the hyperlinks
				new ResourceWrapperWithLinks<Void>().addLink(url + "/" + rid, "self", RequestMethod.GET)
						.addLink(url + "/" + rid, "self", RequestMethod.PUT)
						.addLink(url + "/" + rid, "self", RequestMethod.DELETE).addLink(url, "list", RequestMethod.GET)
						.wrap(null));
	}

	/**
	 * @param rid it is the id of the requirements set to retrieve
	 * @return the requested requirements set
	 */
	@Operation(tags = "version 1 - requirements", summary = "Get a requirement set", description = "")
	@RequestMapping(value = "/{rid}", method = RequestMethod.GET)
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "404", description = "The requirement set has not been found. You can retry the operation or create a requirement set.") })

	public ResponseEntity<Resources<PropertyDefinition>> getRequirementsSet(@PathVariable("rid") Long rid) {

		PropertyDefinition propertyDefinition = service.getRequirementsSet(rid);

		String url = request.getRequestURL().substring(0, request.getRequestURL().lastIndexOf("/"));
		return ResponseEntity.status(HttpStatus.OK).body(
				// wrap the response with the hyperlinks
				new ResourceWrapperWithLinks<PropertyDefinition>().addLink(url + "/" + rid, "self", RequestMethod.GET)
						.addLink(url + "/" + rid, "self", RequestMethod.PUT)
						.addLink(url + "/" + rid, "self", RequestMethod.DELETE).addLink(url, "list", RequestMethod.GET)
						.wrap(propertyDefinition));
	}

	/**
	 * @param rid it is the id of the requirements set to delete
	 */
	@Operation(tags = "version 1 - requirements", summary = "Delete a requirement set", description = "")
	@RequestMapping(value = "/{rid}", method = RequestMethod.DELETE)
	@ApiResponses(value = { @ApiResponse(responseCode = "204", description = "No Content"),
			@ApiResponse(responseCode = "404", description = "The requirement set has not been found. You can retry the operation or create a requirement set.") })
	@ResponseStatus(value = HttpStatus.NO_CONTENT)

	public ResponseEntity<Resources<Void>> deleteRequirementsSet(@PathVariable("rid") Long rid) {

		service.deleteRequirementsSet(rid);

		String url = request.getRequestURL().substring(0, request.getRequestURL().lastIndexOf("/"));
		return ResponseEntity.status(HttpStatus.OK).body(
				// wrap the response with the hyperlinks
				new ResourceWrapperWithLinks<Void>().addLink(url + "/" + rid, "self", RequestMethod.GET)
						.addLink(url + "/" + rid, "self", RequestMethod.PUT)
						.addLink(url + "/" + rid, "self", RequestMethod.DELETE).addLink(url, "list", RequestMethod.GET)
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
	@ApiResponses(value = { @ApiResponse(responseCode = "201", description = "Created"),
			@ApiResponse(responseCode = "400", description = "The passed property is semantically malformed. You can retry the operation or check the data."),
			@ApiResponse(responseCode = "404", description = "The requirement set has not been found or the referred graph doesn't exist. You can retry the operation or create/refer to another requirement set/graph.") })
	public ResponseEntity<Resources<Long>> createProperty(@PathVariable("rid") Long rid,
			@RequestBody Property property) {

		Long propertyId = service.createProperty(rid, property);

		String url = request.getRequestURL().substring(0, request.getRequestURL().lastIndexOf("/")).substring(0,
				request.getRequestURL().lastIndexOf("/"));
		return ResponseEntity.status(HttpStatus.CREATED).body(
				// wrap the response with the hyperlinks
				new ResourceWrapperWithLinks<Long>().addLink(url + "/" + rid + "/properties", "new", RequestMethod.POST)
						.addLink(url + "/" + rid, "list", RequestMethod.GET).addLink(url, "list", RequestMethod.GET)
						.wrap(propertyId));
	}

	/**
	 * @param rid      it is the id of the requirements set
	 * @param pid      it is the id of the property to update
	 * @param property it is the new value of the property
	 */
	@Operation(tags = "version 1 - requirements", summary = "Update a property in a requirement set", description = "")
	@RequestMapping(value = "/{rid}/properties/{pid}", method = RequestMethod.PUT)
	@ApiResponses(value = { @ApiResponse(responseCode = "204", description = "No Content"),
			@ApiResponse(responseCode = "404", description = "The requirement set, the property or the referred graph has not been found. You can retry the operation or create/refer to a/another requirement set/property/graph.") })

	public ResponseEntity<Resources<Void>> updateProperty(@PathVariable("rid") Long rid, @PathVariable("pid") Long pid, @RequestBody Property property) {

		service.updateProperty(rid, pid, property);

		String url = request.getRequestURL().substring(0, request.getRequestURL().lastIndexOf("/"))
				.substring(0, request.getRequestURL().lastIndexOf("/"))
				.substring(0, request.getRequestURL().lastIndexOf("/"));
		return ResponseEntity.status(HttpStatus.OK).body(
				// wrap the response with the hyperlinks
				new ResourceWrapperWithLinks<Void>()
						.addLink(url + "/" + rid + "/properties/" + rid, "self", RequestMethod.GET)
						.addLink(url + "/" + rid + "/properties/" + rid, "self", RequestMethod.PUT)
						.addLink(url + "/" + rid + "/properties/" + rid, "self", RequestMethod.DELETE)
						.addLink(url + "/" + rid + "/properties", "new", RequestMethod.POST)
						.addLink(url + "/" + rid, "list", RequestMethod.GET).addLink(url, "list", RequestMethod.GET)
						.wrap(null));
	}

	/**
	 * @param rid it is the id of the requirements
	 * @param pid it is the id of the property to retrieve
	 * @return the requested property
	 */
	@Operation(tags = "version 1 - requirements", summary = "Get a property from a requirement set", description = "")
	@RequestMapping(value = "/{rid}/properties/{pid}", method = RequestMethod.GET)
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "404", description = "The requirement set or the property has not been found. You can retry the operation or create a requirement set/property.") })

	public ResponseEntity<Resources<Property>> getProperty(@PathVariable("rid") Long rid,
			@PathVariable("pid") Long pid) {

		Property property = service.getProperty(rid, pid);

		String url = request.getRequestURL().substring(0, request.getRequestURL().lastIndexOf("/"))
				.substring(0, request.getRequestURL().lastIndexOf("/"))
				.substring(0, request.getRequestURL().lastIndexOf("/"));
		return ResponseEntity.status(HttpStatus.OK).body(
				// wrap the response with the hyperlinks
				new ResourceWrapperWithLinks<Property>()
						.addLink(url + "/" + rid + "/properties/" + rid, "self", RequestMethod.GET)
						.addLink(url + "/" + rid + "/properties/" + rid, "self", RequestMethod.PUT)
						.addLink(url + "/" + rid + "/properties/" + rid, "self", RequestMethod.DELETE)
						.addLink(url + "/" + rid + "/properties", "new", RequestMethod.POST)
						.addLink(url + "/" + rid, "list", RequestMethod.GET).addLink(url, "list", RequestMethod.GET)
						.wrap(property));
	}

	/**
	 * @param rid it is the id of the requirements
	 * @param pid it is the id of the property to delete
	 */
	@Operation(tags = "version 1 - requirements", summary = "Delete a property from a requirement set", description = "")
	@RequestMapping(value = "/{rid}/properties/{pid}", method = RequestMethod.DELETE)
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "404", description = "The requirement set or the property has not been found. You can retry the operation or create a requirement set/property.") })
	@ResponseStatus(value = HttpStatus.NO_CONTENT)

	public ResponseEntity<Resources<Void>> deleteProperty(@PathVariable("rid") Long rid,
			@PathVariable("pid") Long pid) {

		service.deleteProperty(rid, pid);

		String url = request.getRequestURL().substring(0, request.getRequestURL().lastIndexOf("/"))
				.substring(0, request.getRequestURL().lastIndexOf("/"))
				.substring(0, request.getRequestURL().lastIndexOf("/"));
		return ResponseEntity.status(HttpStatus.OK).body(
				// wrap the response with the hyperlinks
				new ResourceWrapperWithLinks<Void>().addLink(url + "/" + rid + "/properties", "new", RequestMethod.POST)
						.addLink(url + "/" + rid, "list", RequestMethod.GET).addLink(url, "list", RequestMethod.GET)
						.wrap(null));
	}

}
