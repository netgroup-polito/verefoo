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
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.polito.verefoo.jaxb.Property;
import it.polito.verefoo.jaxb.PropertyDefinition;
import it.polito.verefoo.rest.spring.ResourceWrapperWithLinks;
import it.polito.verefoo.rest.spring.service.RequirementService;

@RestController
@RequestMapping(value = "/adp/requirements", produces = {"application/xml", "application/json" })
@ApiResponses(value = {
	@ApiResponse(responseCode = "400", description = "The provided resource is not compliant with the data model.")
})
public class RequirementsController {

	@Autowired
	private HttpServletRequest request;

	@Autowired
	RequirementService service;


	@Operation(tags = "requirements", summary = "Get all the requirement sets", description = "")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = ""),
		@ApiResponse(responseCode = "404", description = "No requirement set has been found in the workspace.")
	})

	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseEntity<Resources<List<PropertyDefinition>>> getRequirementsSets() {

		List<PropertyDefinition> requirementsSets = service.getRequirementsSets();

		String url = request.getRequestURL().toString();
		return ResponseEntity.status(HttpStatus.OK).body(
				// wrap the response with the hyperlinks
				new ResourceWrapperWithLinks<List<PropertyDefinition>>().addLink(url, "collection", RequestMethod.POST)
						.addLink(url, "self", RequestMethod.GET).addLink(url, "self", RequestMethod.DELETE)
						.wrap(requirementsSets));
	}



	@Operation(tags = "requirements", summary = "Delete all the requirement sets", description = "Be careful before cleaning the whole workbench of requirements.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = ""),
		@ApiResponse(responseCode = "304", description = "No requirement set has been found.")
	})

	@RequestMapping(value = "", method = RequestMethod.DELETE)
	public ResponseEntity<Resources<Void>> deleteRequirementsSets() {

		service.deleteRequirementsSets();

		String url = request.getRequestURL().toString();
		return ResponseEntity.status(HttpStatus.OK).body(
				// wrap the response with the hyperlinks
				new ResourceWrapperWithLinks<Void>().addLink(url, "collection", RequestMethod.POST)
						.addLink(url, "self", RequestMethod.GET).addLink(url, "self", RequestMethod.DELETE).wrap(null));
	}



	@Operation(tags = "requirements", summary = "Create a requirement set", description = "")
	@ApiResponses(value = { 
		@ApiResponse(responseCode = "201", description = "Created"),
		@ApiResponse(responseCode = "424", description = "The referenced graph doesn't exist.") })
	
	@RequestMapping(value = "", consumes = { "application/xml", "application/json" }, method = RequestMethod.POST)
	public ResponseEntity<Resources<Long>> createRequirementsSet(@RequestBody PropertyDefinition requirementsSet) {

		Long requirementsSetId = service.createRequirementsSet(requirementsSet);

		String url = request.getRequestURL().toString();
		return ResponseEntity.status(HttpStatus.CREATED).body(
				// wrap the response with the hyperlinks
				new ResourceWrapperWithLinks<Long>().addLink(url, "collection", RequestMethod.POST)
						.addLink(url, "self", RequestMethod.GET).addLink(url, "self", RequestMethod.DELETE)
						.wrap(requirementsSetId));
	}



	@Operation(tags = "requirements", summary = "Update a requirement set", description = "")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "404", description = "The requirement set has not been found."),
		@ApiResponse(responseCode = "424", description = "The referenced graph doesn't exist."),
	})

	@RequestMapping(value = "/{rid}", consumes = { "application/xml", "application/json" }, method = RequestMethod.PUT)
	public ResponseEntity<Resources<Void>> updateRequirementsSet(@PathVariable("rid") Long rid,
			@RequestBody PropertyDefinition requirementsSet) {

		service.updateRequirementsSet(rid, requirementsSet);

		String url = request.getRequestURL().toString();
		url = url.substring(0, url.lastIndexOf("/"));
		return ResponseEntity.status(HttpStatus.OK).body(
				// wrap the response with the hyperlinks
				new ResourceWrapperWithLinks<Void>().addLink(url + "/" + rid, "self", RequestMethod.GET)
						.addLink(url + "/" + rid, "self", RequestMethod.PUT)
						.addLink(url + "/" + rid, "self", RequestMethod.DELETE).addLink(url, "collection", RequestMethod.GET)
						.wrap(null));
	}



	@Operation(tags = "requirements", summary = "Get a requirement set", description = "")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = ""),
		@ApiResponse(responseCode = "404", description = "The requirement doesn't exist at all in the workspace.")
	})

	@RequestMapping(value = "/{rid}", method = RequestMethod.GET)
	public ResponseEntity<Resources<PropertyDefinition>> getRequirementsSet(@PathVariable("rid") Long rid) {

		PropertyDefinition propertyDefinition = service.getRequirementsSet(rid);

		String url = request.getRequestURL().toString();
		url = url.substring(0, url.lastIndexOf("/"));
		return ResponseEntity.status(HttpStatus.OK).body(
				// wrap the response with the hyperlinks
				new ResourceWrapperWithLinks<PropertyDefinition>().addLink(url + "/" + rid, "self", RequestMethod.GET)
						.addLink(url + "/" + rid, "self", RequestMethod.PUT)
						.addLink(url + "/" + rid, "self", RequestMethod.DELETE).addLink(url, "collection", RequestMethod.GET)
						.wrap(propertyDefinition));
	}


	
	@Operation(tags = "requirements", summary = "Delete a requirement set", description = "")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = ""),
		@ApiResponse(responseCode = "404", description = "The requirement doesn't exist at all in the workspace.")
	})

	@RequestMapping(value = "/{rid}", method = RequestMethod.DELETE)
	public ResponseEntity<Resources<Void>> deleteRequirementsSet(@PathVariable("rid") Long rid) {

		service.deleteRequirementsSet(rid);

		String url = request.getRequestURL().toString();
		url = url.substring(0, url.lastIndexOf("/"));
		return ResponseEntity.status(HttpStatus.OK).body(
				// wrap the response with the hyperlinks
				new ResourceWrapperWithLinks<Void>().addLink(url + "/" + rid, "self", RequestMethod.GET)
						.addLink(url + "/" + rid, "self", RequestMethod.PUT)
						.addLink(url + "/" + rid, "self", RequestMethod.DELETE).addLink(url, "collection", RequestMethod.GET)
						.wrap(null));
	}


	
	@Operation(tags = "requirements", summary = "Create another property in a requirement set", description = "")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "Created"),
		@ApiResponse(responseCode = "404", description = "The requirements set has not been found."),
		@ApiResponse(responseCode = "424", description = "The referred graph doesn't exist.")
	})

	@RequestMapping(value = "/{rid}/properties", consumes = { "application/xml", "application/json" }, method = RequestMethod.POST)
	public ResponseEntity<Resources<Long>> createProperty(@PathVariable("rid") Long rid,
			@RequestBody Property property) {

		Long propertyId = service.createProperty(rid, property);

		String url = request.getRequestURL().toString();
		url = url.substring(0, url.lastIndexOf("/"));
		url = url.substring(0, url.lastIndexOf("/"));
		return ResponseEntity.status(HttpStatus.CREATED).body(
				// wrap the response with the hyperlinks
				new ResourceWrapperWithLinks<Long>().addLink(url + "/" + rid + "/properties", "collection", RequestMethod.POST)
						.addLink(url + "/" + rid, "collection", RequestMethod.GET).addLink(url, "collection", RequestMethod.GET)
						.wrap(propertyId));
	}



	@Operation(tags = "requirements", summary = "Update a property in a requirement set", description = "")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = ""),
		@ApiResponse(responseCode = "404", description = "The property has not been found."),
		@ApiResponse(responseCode = "424", description = "The referred graph has not been found.")
	})

	@RequestMapping(value = "/{rid}/properties/{pid}", consumes = { "application/xml", "application/json" }, method = RequestMethod.PUT)
	public ResponseEntity<Resources<Void>> updateProperty(@PathVariable("rid") Long rid, @PathVariable("pid") Long pid, @RequestBody Property property) {

		service.updateProperty(rid, pid, property);

		String url = request.getRequestURL().toString();
		url = url.substring(0, url.lastIndexOf("/"));
		url = url.substring(0, url.lastIndexOf("/"));
		return ResponseEntity.status(HttpStatus.OK).body(
				// wrap the response with the hyperlinks
				new ResourceWrapperWithLinks<Void>()
						.addLink(url + "/" + rid + "/properties/" + rid, "self", RequestMethod.GET)
						.addLink(url + "/" + rid + "/properties/" + rid, "self", RequestMethod.PUT)
						.addLink(url + "/" + rid + "/properties/" + rid, "self", RequestMethod.DELETE)
						.addLink(url + "/" + rid + "/properties", "collection", RequestMethod.POST)
						.addLink(url + "/" + rid, "collection", RequestMethod.GET).addLink(url, "collection", RequestMethod.GET)
						.wrap(null));
	}



	@Operation(tags = "requirements", summary = "Get a property from a requirement set", description = "")
	@ApiResponses(value = { 
		@ApiResponse(responseCode = "200", description = ""),
		@ApiResponse(responseCode = "404", description = "The property doesn't exist at all in the workspace.")
	})

	@RequestMapping(value = "/{rid}/properties/{pid}", method = RequestMethod.GET)
	public ResponseEntity<Resources<Property>> getProperty(@PathVariable("rid") Long rid,
			@PathVariable("pid") Long pid) {

		Property property = service.getProperty(rid, pid);

		String url = request.getRequestURL().toString();
		url = url.substring(0, url.lastIndexOf("/"));
		url = url.substring(0, url.lastIndexOf("/"));
		return ResponseEntity.status(HttpStatus.OK).body(
				// wrap the response with the hyperlinks
				new ResourceWrapperWithLinks<Property>()
						.addLink(url + "/" + rid + "/properties/" + rid, "self", RequestMethod.GET)
						.addLink(url + "/" + rid + "/properties/" + rid, "self", RequestMethod.PUT)
						.addLink(url + "/" + rid + "/properties/" + rid, "self", RequestMethod.DELETE)
						.addLink(url + "/" + rid + "/properties", "collection", RequestMethod.POST)
						.addLink(url + "/" + rid, "collection", RequestMethod.GET).addLink(url, "collection", RequestMethod.GET)
						.wrap(property));
	}


	
	@Operation(tags = "requirements", summary = "Delete a property from a requirement set", description = "")
	@ApiResponses(value = { 
		@ApiResponse(responseCode = "200", description = ""),
		@ApiResponse(responseCode = "404", description = "The property doesn't exist at all in the workspace.")
	})

	@RequestMapping(value = "/{rid}/properties/{pid}", method = RequestMethod.DELETE)
	public ResponseEntity<Resources<Void>> deleteProperty(@PathVariable("rid") Long rid,
			@PathVariable("pid") Long pid) {

		service.deleteProperty(rid, pid);

		String url = request.getRequestURL().toString();
		url = url.substring(0, url.lastIndexOf("/"));
		url = url.substring(0, url.lastIndexOf("/"));
		return ResponseEntity.status(HttpStatus.OK).body(
				// wrap the response with the hyperlinks
				new ResourceWrapperWithLinks<Void>().addLink(url + "/" + rid + "/properties", "collection", RequestMethod.POST)
						.addLink(url + "/" + rid, "collection", RequestMethod.GET).addLink(url, "collection", RequestMethod.GET)
						.wrap(null));
	}

}
