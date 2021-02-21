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
import it.polito.verefoo.jaxb.Connections;
import it.polito.verefoo.jaxb.Host;
import it.polito.verefoo.jaxb.Hosts;
import it.polito.verefoo.rest.spring.ResourceWrapperWithLinks;
import it.polito.verefoo.rest.spring.service.SubstrateService;

@RestController
@RequestMapping(value = "/adp/substrates", produces = {"application/xml", "application/json" })
@ApiResponses(value = {
	@ApiResponse(responseCode = "400", description = "The provided resource is not compliant with the data model.")
})
public class SubstratesController {

	@Autowired
	SubstrateService service;

	@Autowired
	private HttpServletRequest request;


	

	@Operation(tags = "substrates", summary = "Create an empty substrate", description = "Create a new physical network")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "Created")
	})

	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseEntity<Resources<Long>> createSubstrate() {

		Long substrateId = service.createSubstrate();

		String url = request.getRequestURL().toString();
		return ResponseEntity.status(HttpStatus.CREATED).body(
                                // wrap the response with the hyperlinks
                                new ResourceWrapperWithLinks<Long>()
												.addLink(url + "/" + substrateId, "self", RequestMethod.GET)
												.addLink(url + "/" + substrateId + "/hosts", "subsection", RequestMethod.GET)
												.addLink(url + "/" + substrateId + "/connections", "subsection", RequestMethod.GET)
												.addLink(url, "collection", RequestMethod.GET)
                                                .addLink(url + "/" + substrateId, "self", RequestMethod.DELETE)
                                                .addLink(url, "collection", RequestMethod.POST)
												.wrap(substrateId));
	}


	
	@Operation(tags = "substrates", summary = "Get all substrates ids", description = "")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = ""),
		@ApiResponse(responseCode = "404", description = "No substrates have been found.")
	})

	@RequestMapping(value = "", method = RequestMethod.GET)
	public ResponseEntity<Resources<List<Long>>> getSubstrates() {

		List<Long> ids = service.getAllSubstrates();

		String url = request.getRequestURL().toString();
		return ResponseEntity.status(HttpStatus.OK).body(
                                // wrap the response with the hyperlinks
                                new ResourceWrapperWithLinks<List<Long>>()
                                                .addLink(url, "self", RequestMethod.GET)
                                                .addLink(url, "self", RequestMethod.DELETE)
												.addLink(url, "collection", RequestMethod.POST)
												.addLink(url + "/" + ids.get(0) + "/hosts", "subsection", RequestMethod.GET)
												.addLink(url + "/" + ids.get(0) + "/connections", "subsection", RequestMethod.GET)
                                                .wrap(ids));
	}



	@Operation(tags = "substrates", summary = "Delete all the substrates", description = "Be careful before cleaning the whole substrate workbench.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = ""),
		@ApiResponse(responseCode = "304", description = "No substrate exists at all in the workspace.")
	})

	@RequestMapping(value = "", method = RequestMethod.DELETE)
	public ResponseEntity<Resources<Void>> deleteSubstrates() {

		service.deleteAllSubstrates();

		String url = request.getRequestURL().toString();
		return ResponseEntity.status(HttpStatus.OK).body(
                                // wrap the response with the hyperlinks
                                new ResourceWrapperWithLinks<Void>()
                                                .addLink(url, "collection", RequestMethod.POST)
                                                .wrap(null));
	}


	
	@Operation(tags = "substrates", summary = "Delete a substrate", description = "The operation deletes all the hosts and connections of the substrate accordingly.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = ""),
		@ApiResponse(responseCode = "404", description = "The selected substrate doesn't exist at all.")
	})

	@RequestMapping(value = "/{sid}", method = RequestMethod.DELETE)
	public ResponseEntity<Resources<Void>> deleteSubstrate(@PathVariable("sid") Long sid) {

		service.deleteSubstrate(sid);

		String url = request.getRequestURL().toString();
		url = url.substring(0, url.lastIndexOf("/"));
		return ResponseEntity.status(HttpStatus.OK).body(
                                // wrap the response with the hyperlinks
                                new ResourceWrapperWithLinks<Void>()
                                                .addLink(url, "collection", RequestMethod.GET)
                                                .wrap(null));
	}



	@Operation(tags = "substrates", summary = "Add a collection of hosts in the substrate", description = "")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "Created"),
		@ApiResponse(responseCode = "404", description = "The substrate doesn't exist at all in the workspace.")
	})

	@RequestMapping(value = "/{sid}/hosts", consumes = { "application/xml", "application/json" }, method = RequestMethod.POST)
	public ResponseEntity<Resources<Void>> createHosts(@PathVariable("sid") Long sid, @RequestBody Hosts hosts) {

		service.createHosts(sid, hosts);

		String url = request.getRequestURL().toString();
		url = url.substring(0, url.lastIndexOf("/"));
		url = url.substring(0, url.lastIndexOf("/"));
		return ResponseEntity.status(HttpStatus.CREATED).body(
                                // wrap the response with the hyperlinks
								new ResourceWrapperWithLinks<Void>()
												.addLink(url + "/" + sid + "/connections", "collection", RequestMethod.POST)
                                                .addLink(url + "/" + sid + "/hosts", "self", RequestMethod.GET)
												.addLink(url + "/" + sid + "/hosts", "self", RequestMethod.DELETE)
												.addLink(url + "/" + sid + "/hosts", "collection", RequestMethod.POST)
												.addLink(url, "collection", RequestMethod.GET)
                                                .wrap(null));
	}


	

	@Operation(tags = "substrates", summary = "Get the collection of hosts in the substrate", description = "")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = ""),
		@ApiResponse(responseCode = "404", description = "The substrate doesn't exist at all in the workspace.")
	})

	@RequestMapping(value = "/{sid}/hosts", method = RequestMethod.GET)
	public ResponseEntity<Resources<Hosts>> getHosts(@PathVariable("sid") Long sid) {

		Hosts hosts = service.getHosts(sid);
		
		String url = request.getRequestURL().toString();
		url = url.substring(0, url.lastIndexOf("/"));
		url = url.substring(0, url.lastIndexOf("/"));
		return ResponseEntity.status(HttpStatus.OK).body(
                                // wrap the response with the hyperlinks
								new ResourceWrapperWithLinks<Hosts>()
												.addLink(url + "/" + sid + "/connections", "collection", RequestMethod.POST)
												.addLink(url + "/" + sid + "/hosts", "collection", RequestMethod.POST)
												.addLink(url + "/" + sid + "/hosts", "self", RequestMethod.DELETE)
												.addLink(url + "/" + sid + "/hosts", "self", RequestMethod.GET)
												.addLink(url, "collection", RequestMethod.GET)
                                                .wrap(hosts));
	}


	

	@Operation(tags = "substrates", summary = "Delete the list of hosts in the substrate", description = "The list is deleted only if no connections exists.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = ""),
		@ApiResponse(responseCode = "404", description = "The substrate doesn't exist at all in the workspace.")
	})

	@RequestMapping(value = "/{sid}/hosts", method = RequestMethod.DELETE)
	public ResponseEntity<Resources<Void>> deleteHosts(@PathVariable("sid") Long sid) {

		service.deleteHosts(sid);
		
		String url = request.getRequestURL().toString();
		url = url.substring(0, url.lastIndexOf("/"));
		url = url.substring(0, url.lastIndexOf("/"));
		return ResponseEntity.status(HttpStatus.OK).body(
                                // wrap the response with the hyperlinks
								new ResourceWrapperWithLinks<Void>()
												.addLink(url + "/" + sid + "/hosts", "collection", RequestMethod.POST)
												.addLink(url, "collection", RequestMethod.GET)
                                                .wrap(null));
	}


	

	@Operation(tags = "substrates", summary = "Update a host in a substrate", description = "")
	@ApiResponses(value = { 
		@ApiResponse(responseCode = "200", description = ""),
		@ApiResponse(responseCode = "404", description = "The host doesn't exist at all in the workspace."),
		@ApiResponse(responseCode = "424", description = "One referred node doesn't exist.")
	})

	@RequestMapping(value = "/{sid}/hosts/{hid}", consumes = { "application/xml", "application/json" }, method = RequestMethod.PUT)
	public ResponseEntity<Resources<Void>> updateHost(@PathVariable("sid") Long sid, @PathVariable("hid") String hid, @RequestBody Host host) {

		service.updateHost(sid, hid, host);

		String url = request.getRequestURL().toString();
		url = url.substring(0, url.lastIndexOf("/"));
		url = url.substring(0, url.lastIndexOf("/"));
		url = url.substring(0, url.lastIndexOf("/"));
		return ResponseEntity.status(HttpStatus.OK).body(
                                // wrap the response with the hyperlinks
								new ResourceWrapperWithLinks<Void>()
												.addLink(url + "/" + sid + "/connections", "collection", RequestMethod.GET)
												.addLink(url + "/" + sid + "/connections", "collection", RequestMethod.POST)
												.addLink(url + "/" + sid + "/hosts", "collection", RequestMethod.GET)
												.addLink(url + "/" + sid + "/hosts/" + hid, "self", RequestMethod.GET)
												.addLink(url + "/" + sid + "/hosts/" + hid, "self", RequestMethod.PUT)
												.addLink(url + "/" + sid + "/hosts/" + hid, "self", RequestMethod.DELETE)
												.addLink(url, "collection", RequestMethod.GET)
                                                .wrap(null));
	}


	

	@Operation(tags = "substrates", summary = "Get a host in a substrate", description = "")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = ""),
			@ApiResponse(responseCode = "404", description = "The host doesn't exist at all in the workspace.")
		})

	@RequestMapping(value = "/{sid}/hosts/{hid}", method = RequestMethod.GET)
	public ResponseEntity<Resources<Host>> getHost(@PathVariable("sid") Long sid, @PathVariable("hid") String hid) {

		Host host = service.getHost(sid, hid);

		String url = request.getRequestURL().toString();
		url = url.substring(0, url.lastIndexOf("/"));
		url = url.substring(0, url.lastIndexOf("/"));
		url = url.substring(0, url.lastIndexOf("/"));
		return ResponseEntity.status(HttpStatus.OK).body(
                                // wrap the response with the hyperlinks
                                new ResourceWrapperWithLinks<Host>()
												.addLink(url + "/" + sid + "/connections", "collection", RequestMethod.POST)
												.addLink(url + "/" + sid + "/hosts", "collection", RequestMethod.GET)
												.addLink(url + "/" + sid + "/hosts", "collection", RequestMethod.POST)
												.addLink(url + "/" + sid + "/hosts/" + hid, "self", RequestMethod.GET)
												.addLink(url + "/" + sid + "/hosts/" + hid, "self", RequestMethod.PUT)
												.addLink(url + "/" + sid + "/hosts/" + hid, "self", RequestMethod.DELETE)
												.addLink(url + "/" + sid + "/hosts", "collection", RequestMethod.DELETE)
												.addLink(url, "collection", RequestMethod.GET)
                                                .wrap(host));
	}


	

	@Operation(tags = "substrates", summary = "Delete a host", description = "The host is deleted only if it is not implied in any connection.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = ""),
		@ApiResponse(responseCode = "404", description = "The host doesn't exist at all in the workspace.")
	})

	@RequestMapping(value = "/{sid}/hosts/{hid}", method = RequestMethod.DELETE)
	public ResponseEntity<Resources<Void>> deleteHost(@PathVariable("sid") Long sid, @PathVariable("hid") String hid) {

		service.deleteHost(sid, hid);

		String url = request.getRequestURL().toString();
		url = url.substring(0, url.lastIndexOf("/"));
		url = url.substring(0, url.lastIndexOf("/"));
		url = url.substring(0, url.lastIndexOf("/"));
		return ResponseEntity.status(HttpStatus.OK).body(
                                // wrap the response with the hyperlinks
                                new ResourceWrapperWithLinks<Void>()
												.addLink(url + "/" + sid + "/connections", "collection", RequestMethod.POST)
												.addLink(url + "/" + sid + "/connections", "collection", RequestMethod.GET)
												.addLink(url + "/" + sid + "/hosts", "collection", RequestMethod.GET)
												.addLink(url + "/" + sid + "/hosts", "collection", RequestMethod.POST)
												.addLink(url + "/" + sid + "/hosts", "collection", RequestMethod.DELETE)
												.addLink(url, "collection", RequestMethod.GET)
                                                .wrap(null));
	}


	

	@Operation(tags = "substrates", summary = "Create a list of connections between hosts in the substrate", description = "The hosts must already exist. If some connections already exist, the new connections are just added; the duplicates are discarded.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Created"),
			@ApiResponse(responseCode = "424", description = "The connections cannot be established between the hosts because at least one of them doesn't exist in the workspace.")
		})
	
	@RequestMapping(value = "/{sid}/connections", consumes = { "application/xml", "application/json" }, method = RequestMethod.POST)
	public ResponseEntity<Resources<Void>> createConnections(@PathVariable("sid") long sid, @RequestBody Connections connections) {

		service.createConnections(sid, connections);

		String url = request.getRequestURL().toString();
		url = url.substring(0, url.lastIndexOf("/"));
		url = url.substring(0, url.lastIndexOf("/"));
		return ResponseEntity.status(HttpStatus.CREATED).body(
                                // wrap the response with the hyperlinks
                                new ResourceWrapperWithLinks<Void>()
												.addLink(url + "/" + sid + "/connections", "self", RequestMethod.GET)
												.addLink(url + "/" + sid + "/connections", "self", RequestMethod.PUT)
												.addLink(url + "/" + sid + "/connections", "self", RequestMethod.DELETE)
												.addLink(url + "/" + sid + "/connections", "collection", RequestMethod.POST)
												.addLink(url + "/" + sid + "/hosts", "collection", RequestMethod.GET)
												.addLink(url, "collection", RequestMethod.GET)
                                                .wrap(null));
	}


	

	@Operation(tags = "substrates", summary = "Change all the connections between hosts in a substrate.", description = "The old connections will be lost.")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = "OK"),
		@ApiResponse(responseCode = "404", description = "The substrate doesn't exist. You can create a new substrate or refer to another one."),
		@ApiResponse(responseCode = "424", description = "The connections cannot be established between the hosts because at least one of them doesn't exist in the workspace.")
	})

	@RequestMapping(value = "/{sid}/connections", consumes = { "application/xml", "application/json" }, method = RequestMethod.PUT)
	public ResponseEntity<Resources<Void>> updateConnections(@PathVariable("sid") Long sid, @RequestBody Connections connections) {

		service.updateConnections(sid, connections);

		String url = request.getRequestURL().toString();
		url = url.substring(0, url.lastIndexOf("/"));
		url = url.substring(0, url.lastIndexOf("/"));
		return ResponseEntity.status(HttpStatus.OK).body(
                                // wrap the response with the hyperlinks
                                new ResourceWrapperWithLinks<Void>()
												.addLink(url + "/" + sid + "/connections", "self", RequestMethod.GET)
												.addLink(url + "/" + sid + "/connections", "self", RequestMethod.PUT)
												.addLink(url + "/" + sid + "/connections", "self", RequestMethod.DELETE)
												.addLink(url + "/" + sid + "/connections", "collection", RequestMethod.POST)
												.addLink(url + "/" + sid + "/hosts", "collection", RequestMethod.GET)
												.addLink(url, "collection", RequestMethod.GET)
                                                .wrap(null));
	}


	
	@Operation(tags = "substrates", summary = "Get all the connections of a substrate", description = "")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = ""),
		@ApiResponse(responseCode = "404", description = "The substrate doesn't exist at all in the workspace.")
	})

	@RequestMapping(value = "/{sid}/connections", method = RequestMethod.GET)
	public ResponseEntity<Resources<Connections>> getConnections(@PathVariable("sid") Long sid) {

		Connections connections = service.getConnections(sid);

		String url = request.getRequestURL().toString();
		url = url.substring(0, url.lastIndexOf("/"));
		url = url.substring(0, url.lastIndexOf("/"));
		return ResponseEntity.status(HttpStatus.OK).body(
                                // wrap the response with the hyperlinks
                                new ResourceWrapperWithLinks<Connections>()
												.addLink(url + "/" + sid + "/connections", "self", RequestMethod.GET)
												.addLink(url + "/" + sid + "/connections", "self", RequestMethod.PUT)
												.addLink(url + "/" + sid + "/connections", "self", RequestMethod.DELETE)
												.addLink(url + "/" + sid + "/connections", "collection", RequestMethod.POST)
												.addLink(url + "/" + sid + "/hosts", "collection", RequestMethod.GET)
												.addLink(url, "collection", RequestMethod.GET)
                                                .wrap(connections));
	}


	
	@Operation(tags = "substrates", summary = "Delete all the connections of a substrate", description = "")
	@ApiResponses(value = {
		@ApiResponse(responseCode = "200", description = ""),
		@ApiResponse(responseCode = "404", description = "The substrate doesn't exist at all in the workspace.")
	})
	
	@RequestMapping(value = "/{sid}/connections", method = RequestMethod.DELETE)
	public ResponseEntity<Resources<Void>> deleteConnections(@PathVariable("sid") Long sid) {
		
		service.deleteConnections(sid);

		String url = request.getRequestURL().toString();
		url = url.substring(0, url.lastIndexOf("/"));
		url = url.substring(0, url.lastIndexOf("/"));
		return ResponseEntity.status(HttpStatus.OK).body(
                                // wrap the response with the hyperlinks
                                new ResourceWrapperWithLinks<Void>()
												.addLink(url + "/" + sid + "/connections", "collection", RequestMethod.POST)
												.addLink(url + "/" + sid + "/hosts", "collection", RequestMethod.GET)
												.addLink(url, "collection", RequestMethod.GET)
                                                .wrap(null));
	}

}
