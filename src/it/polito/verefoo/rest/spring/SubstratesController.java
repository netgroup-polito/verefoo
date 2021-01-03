package it.polito.verefoo.rest.spring;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import ch.qos.logback.classic.html.UrlCssBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import it.polito.verefoo.jaxb.*;
import it.polito.verefoo.rest.spring.service.SubstrateService;

@RestController
@RequestMapping(value = "/adp/substrates", consumes = { "application/xml", "application/json" }, produces = {
		"application/xml", "application/json" })
public class SubstratesController {

	@Autowired
	SubstrateService service;

	@Autowired
	private HttpServletRequest request;

	/* Substrates */

	// /**
	//  * @param substrate is the substrate network to create
	//  * @return the created substrate network
	//  */
	// @Operation(tags = "version 1 - substrates", summary = "createSubstrate", description = "create a new substrate network")
	// @RequestMapping(value = "", method = RequestMethod.POST)
	// @ApiResponses(value = { @ApiResponse(responseCode = "201", description = "Created"),
	// 		@ApiResponse(responseCode = "400", description = "Bad Request"), })
	// public List<Integer> createSubstrate(@RequestBody List<Host> hosts) {

	// 	StringBuffer url = request.getRequestURL();

	// 	List<Integer> ids = service.createSubstrate(hosts);

	// 	return ResponseEntity.status(HttpStatus.OK).body(
    //                             // wrap the response with the hyperlinks
    //                             new ResourceWrapperWithLinks<List<Integer>>()
    //                                             .addLink("substrates/" + substrateId, "first", RequestMethod.GET)
    //                                             .addLink("substrates/" + substrateId, "first", RequestMethod.DELETE)
    //                                             .addLink("substrates/" + substrateId, "first", RequestMethod.PUT)
    //                                             .addLink("substrates", "new", RequestMethod.POST)
    //                                             .addLink("substrates", "self", RequestMethod.GET)
    //                                             .wrap(substrates));

	// 	/* Boolean created = true;
	// 	if (created != null) {
	// 		String responseUrl;
	// 		if (url.toString().endsWith("/"))
	// 			responseUrl = url.toString() + sid;
	// 		else
	// 			responseUrl = url.toString() + "/" + sid;
	// 		HttpHeaders responseHeaders = new HttpHeaders();
	// 		try {
	// 			responseHeaders.setLocation(new URI(responseUrl));
	// 		} catch (URISyntaxException e) {
	// 			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bad request");
	// 		}
	// 		return new ResponseEntity<List<Host>>(created, responseHeaders, HttpStatus.CREATED);
	// 	} else
	// 		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bad request"); */
	// }

	/**
	 * @param substrate is the substrate network to create
	 * @return the created substrate network
	 */
	@Operation(tags = "version 1 - substrates", summary = "Create an empty substrate", description = "Create a new physical network")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Created"),
			@ApiResponse(responseCode = "500", description = "The server could not create a new substrate. You can do it over or retry later.")
		})

	@RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseEntity<Resources<Long>> createSubstrate() {

		Long substrateId = service.createSubstrate();

		String url = request.getRequestURL().toString();
		return ResponseEntity.status(HttpStatus.CREATED).body(
                                // wrap the response with the hyperlinks
                                new ResourceWrapperWithLinks<Long>()
												.addLink(url + "/" + substrateId, "self", RequestMethod.GET)
												.addLink(url + "/" + substrateId + "/hosts", "sub", RequestMethod.GET)
												.addLink(url + "/" + substrateId + "/connections", "sub", RequestMethod.GET)
												.addLink(url, "list", RequestMethod.GET)
                                                .addLink(url + "/" + substrateId, "self", RequestMethod.DELETE)
                                                .addLink(url, "new", RequestMethod.POST)
                                                .wrap(substrateId));

		/* Boolean created = true;
		if (created != null) {
			String responseUrl;
			if (url.toString().endsWith("/"))
				responseUrl = url.toString() + sid;
			else
				responseUrl = url.toString() + "/" + sid;
			HttpHeaders responseHeaders = new HttpHeaders();
			try {
				responseHeaders.setLocation(new URI(responseUrl));
			} catch (URISyntaxException e) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bad request");
			}
			return new ResponseEntity<List<Host>>(created, responseHeaders, HttpStatus.CREATED);
		} else
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bad request"); */
	}

	/**
	 * @param beforeInclusive it is the starting index
	 * @param afterInclusive  it is the ending index
	 * @return a collection of substrate network
	 */
	@Operation(tags = "version 1 - substrates", summary = "Get all substrates ids", description = "")
	@RequestMapping(value = "", method = RequestMethod.GET)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "404", description = "No substrates have been found. You can retry the operation or first create a substrate.")
		})

	public ResponseEntity<Resources<List<Integer>>> getSubstrates() {
		// List<Host> substrates = service.getSubstrates();
		// if (substrates.isEmpty())
		// 	throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
		// return substrates;
		List<Integer> ids = null;

		String url = request.getRequestURL().toString();
		return ResponseEntity.status(HttpStatus.OK).body(
                                // wrap the response with the hyperlinks
                                new ResourceWrapperWithLinks<List<Integer>>()
                                                .addLink(url, "self", RequestMethod.GET)
                                                .addLink(url, "self", RequestMethod.DELETE)
												.addLink(url, "new", RequestMethod.POST)
												.addLink(url + "/" + ids.get(0) + "/hosts", "first", RequestMethod.GET)
												.addLink(url + "/" + ids.get(0) + "/connections", "first", RequestMethod.GET)
                                                .wrap(ids));
	}

	/**
	 * delete all the substrate network
	 */
	@Operation(tags = "version 1 - substrates", summary = "Delete all the substrates", description = "Be careful before cleaning the whole substrate workbench.")
	@RequestMapping(value = "", method = RequestMethod.DELETE)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "404", description = "The substrates don't exist at all. You can retry the operation first create a substrate.")
		})

	public ResponseEntity<Resources<Void>> deleteSubstrates() {
		// boolean removed = service.deleteSubstrates();
		// if (!removed)
		// 	throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");

		String url = request.getRequestURL().toString();
		return ResponseEntity.status(HttpStatus.OK).body(
                                // wrap the response with the hyperlinks
                                new ResourceWrapperWithLinks<Void>()
                                                .addLink(url, "new", RequestMethod.POST)
                                                .wrap(null));
	}

	/* Substrate */

	// /**
	//  * @param sid       it is the id of the substrate network
	//  * @param substrate it is the new value of the substrate network
	//  */
	// @Operation(tags = "version 1 - substrates", summary = "updateSubstrate", description = "update a single substrate network")
	// @RequestMapping(value = "/{sid}", method = RequestMethod.PUT)
	// @ApiResponses(value = { @ApiResponse(responseCode = "204", description = "No Content"),
	// 		@ApiResponse(responseCode = "404", description = "Not Found"), })
	// @ResponseStatus(value = HttpStatus.NO_CONTENT)

	// public void updateSubstrate(@PathVariable("sid") long sid, @RequestBody Hosts substrate) {
	// 	Hosts updated = service.updateSubstrate(sid, substrate);
	// 	if (updated == null)
	// 		throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
	// }

	// /**
	//  * @param sid it is the id of the substrate network to retrieve
	//  * @return the requested substrate network
	//  */
	// @Operation(tags = "version 1 - substrates", summary = "getSubstrate", description = "retrieve a single substrate network")
	// @RequestMapping(value = "/{sid}", method = RequestMethod.GET)
	// @ApiResponses(value = { @ApiResponse(responseCode = "200", description = "OK"),
	// 		@ApiResponse(responseCode = "404", description = "Not Found"), })

	// public Hosts getSubstrate(@PathVariable("sid") long sid) {
	// 	Hosts substrate = service.getSubstrate(sid);
	// 	if (substrate == null)
	// 		throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
	// 	return substrate;
	// }

	/**
	 * @param sid it is the id of the substrate network to delete
	 */
	@Operation(tags = "version 1 - substrates", summary = "Delete a substrate", description = "The operation deletes all the hosts and connections of the substrates accordingly.")
	@RequestMapping(value = "/{sid}", method = RequestMethod.DELETE)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "404", description = "The selected substrate doesn't exist at all. You can eventually retry the operation or refer to another substrate.")
		})
	@ResponseStatus(value = HttpStatus.NO_CONTENT)

	public ResponseEntity<Resources<Void>> deleteSubstrate(@PathVariable("sid") long sid) {
		// service.deleteSubstrate(sid);
		// if (substrate == null)
		// 	throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");

		String url = request.getRequestURL().substring(0, request.getRequestURL().lastIndexOf("/"));
		return ResponseEntity.status(HttpStatus.OK).body(
                                // wrap the response with the hyperlinks
                                new ResourceWrapperWithLinks<Void>()
                                                .addLink(url, "list", RequestMethod.GET)
                                                .wrap(null));
	}

	/* Hosts */

	/**
	 * @param sid  it is the id of the substrate network
	 * @param hid  it is the name of the host to create
	 * @param host it is the host to create
	 * @return the created host
	 */
	@Operation(tags = "version 1 - substrates", summary = "Create a list of hosts in the substrate", description = "")
	@RequestMapping(value = "/{sid}/hosts", method = RequestMethod.POST)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Created"),
			@ApiResponse(responseCode = "400", description = "The passed hosts resource is semantically malformed. You can retry the operation or check the data."),
			@ApiResponse(responseCode = "404", description = "The substrate doesn't exist. You can first create a substrate or use an existing one.")
		})
	public ResponseEntity<Resources<Void>> createHosts(@PathVariable("sid") long sid,
			Hosts hosts) {
		// String hid = host.getName();
		// if (hid == null)
		// 	throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bad request");
		// String url = request.getRequestURL().toString();
		// Host created = service.createHost(sid, hid, host);
		// if (created != null) {
		// 	String responseUrl;
		// 	if (url.toString().endsWith("/"))
		// 		responseUrl = url.toString() + hid;
		// 	else
		// 		responseUrl = url.toString() + "/" + hid;
		// 	HttpHeaders responseHeaders = new HttpHeaders();
		// 	try {
		// 		responseHeaders.setLocation(new URI(responseUrl));
		// 	} catch (URISyntaxException e) {
		// 		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bad request");
		// 	}
		// 	return new ResponseEntity<Host>(created, responseHeaders, HttpStatus.CREATED);
		// } else
		// 	throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bad request");

		String url = request.getRequestURL()
			.substring(0, request.getRequestURL().lastIndexOf("/"))
			.substring(0, request.getRequestURL().lastIndexOf("/"));
		return ResponseEntity.status(HttpStatus.CREATED).body(
                                // wrap the response with the hyperlinks
								new ResourceWrapperWithLinks<Void>()
												.addLink(url + "/" + sid + "/connections", "new", RequestMethod.POST)
                                                .addLink(url + "/" + sid + "/hosts", "self", RequestMethod.GET)
												.addLink(url + "/" + sid + "/hosts", "self", RequestMethod.DELETE)
												.addLink(url + "/" + sid + "/hosts", "new", RequestMethod.POST)
												.addLink(url, "list", RequestMethod.GET)
                                                .wrap(null));
	}

	/**
	 * @param sid  it is the id of the substrate network
	 * @param hid  it is the name of the host to create
	 * @param host it is the host to create
	 * @return the created host
	 */
	@Operation(tags = "version 1 - substrates", summary = "Get the list of hosts in the substrate", description = "")
	@RequestMapping(value = "/{sid}/hosts", method = RequestMethod.GET)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "404", description = "The substrate doesn't exist. You can first create a substrate or use an existing one.")
		})
	public ResponseEntity<Resources<Hosts>> getHosts(@PathVariable("sid") long sid) {
		// String hid = host.getName();
		// if (hid == null)
		// 	throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bad request");
		// String url = request.getRequestURL().toString();
		// Host created = service.createHost(sid, hid, host);
		// if (created != null) {
		// 	String responseUrl;
		// 	if (url.toString().endsWith("/"))
		// 		responseUrl = url.toString() + hid;
		// 	else
		// 		responseUrl = url.toString() + "/" + hid;
		// 	HttpHeaders responseHeaders = new HttpHeaders();
		// 	try {
		// 		responseHeaders.setLocation(new URI(responseUrl));
		// 	} catch (URISyntaxException e) {
		// 		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bad request");
		// 	}
		// 	return new ResponseEntity<Host>(created, responseHeaders, HttpStatus.CREATED);
		// } else
		// 	throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bad request");
		Hosts hosts = null;
		
		String url = request.getRequestURL()
			.substring(0, request.getRequestURL().lastIndexOf("/"))
			.substring(0, request.getRequestURL().lastIndexOf("/"));
		return ResponseEntity.status(HttpStatus.OK).body(
                                // wrap the response with the hyperlinks
								new ResourceWrapperWithLinks<Hosts>()
												.addLink(url + "/" + sid + "/connections", "new", RequestMethod.POST)
												.addLink(url + "/" + sid + "/hosts", "new", RequestMethod.POST)
												.addLink(url + "/" + sid + "/hosts", "self", RequestMethod.DELETE)
												.addLink(url + "/" + sid + "/hosts", "self", RequestMethod.GET)
												.addLink(url, "list", RequestMethod.GET)
                                                .wrap(hosts));
	}

	/**
	 * @param sid  it is the id of the substrate network
	 * @param hid  it is the name of the host to create
	 * @param host it is the host to create
	 * @return the created host
	 */
	@Operation(tags = "version 1 - substrates", summary = "Delete the list of hosts in the substrate", description = "The list is deleted only if no connections exists.")
	@RequestMapping(value = "/{sid}/hosts", method = RequestMethod.DELETE)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "404", description = "The substrate doesn't exist. You can first create a substrate or use an existing one.")
		})
	public ResponseEntity<Resources<Void>> deleteHosts(@PathVariable("sid") long sid) {
		// String hid = host.getName();
		// if (hid == null)
		// 	throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bad request");
		// String url = request.getRequestURL().toString();
		// Host created = service.createHost(sid, hid, host);
		// if (created != null) {
		// 	String responseUrl;
		// 	if (url.toString().endsWith("/"))
		// 		responseUrl = url.toString() + hid;
		// 	else
		// 		responseUrl = url.toString() + "/" + hid;
		// 	HttpHeaders responseHeaders = new HttpHeaders();
		// 	try {
		// 		responseHeaders.setLocation(new URI(responseUrl));
		// 	} catch (URISyntaxException e) {
		// 		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bad request");
		// 	}
		// 	return new ResponseEntity<Host>(created, responseHeaders, HttpStatus.CREATED);
		// } else
		// 	throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bad request");
		
		String url = request.getRequestURL()
			.substring(0, request.getRequestURL().lastIndexOf("/"))
			.substring(0, request.getRequestURL().lastIndexOf("/"));
		return ResponseEntity.status(HttpStatus.OK).body(
                                // wrap the response with the hyperlinks
								new ResourceWrapperWithLinks<Void>()
												.addLink(url + "/" + sid + "/hosts", "new", RequestMethod.POST)
												.addLink(url, "list", RequestMethod.GET)
                                                .wrap(null));
	}

	/**
	 * @param sid  it is the id of the substrate network
	 * @param hid  it is the name of the host to update
	 * @param host it is the new value of the host
	 */
	@Operation(tags = "version 1 - substrates", summary = "Update a host in a substrate", description = "")
	@RequestMapping(value = "/{sid}/hosts/{hid}", method = RequestMethod.PUT)
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "404", description = "The host or the substrate doesn't exist. You can retry the operation or create/use other substrates or hosts."), })

	public ResponseEntity<Resources<Void>> updateHost(@PathVariable("sid") long sid, @PathVariable("hid") String hid, @RequestBody Host host) {
		// Host updated = service.updateHost(sid, hid, host);
		// if (updated == null)
		// 	throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");

		String url = request.getRequestURL().substring(0, request.getRequestURL().lastIndexOf("/")).substring(0, request.getRequestURL().lastIndexOf("/")).substring(0, request.getRequestURL().lastIndexOf("/"));
		return ResponseEntity.status(HttpStatus.OK).body(
                                // wrap the response with the hyperlinks
								new ResourceWrapperWithLinks<Void>()
												.addLink(url + "/" + sid + "/connections", "list", RequestMethod.GET)
												.addLink(url + "/" + sid + "/connections", "new", RequestMethod.POST)
												.addLink(url + "/" + sid + "/hosts", "list", RequestMethod.GET)
												.addLink(url + "/" + sid + "/hosts/" + hid, "self", RequestMethod.GET)
												.addLink(url + "/" + sid + "/hosts/" + hid, "self", RequestMethod.PUT)
												.addLink(url + "/" + sid + "/hosts/" + hid, "self", RequestMethod.DELETE)
												.addLink(url, "list", RequestMethod.GET)
                                                .wrap(null));
	}

	/**
	 * @param sid it is the id of the substrate network
	 * @param hid it is the name of the host to retrieve
	 * @return the request host
	 */
	@Operation(tags = "version 1 - substrates", summary = "Get a host in a substrate", description = "")
	@RequestMapping(value = "/{sid}/hosts/{hid}", method = RequestMethod.GET)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "404", description = "The host or the substrate doesn't exist. You can retry the operation or refer to other hosts/substrates.")
		})

	public ResponseEntity<Resources<Host>> getHost(@PathVariable("sid") long sid, @PathVariable("hid") String hid) {
		// Host host = service.getHost(sid, hid);
		// if (host == null)
		// 	throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
		// return host;
		Host host = null;

		String url = request.getRequestURL().substring(0, request.getRequestURL().lastIndexOf("/")).substring(0, request.getRequestURL().lastIndexOf("/")).substring(0, request.getRequestURL().lastIndexOf("/"));
		return ResponseEntity.status(HttpStatus.OK).body(
                                // wrap the response with the hyperlinks
                                new ResourceWrapperWithLinks<Host>()
												.addLink(url + "/" + sid + "/connections", "new", RequestMethod.POST)
												.addLink(url + "/" + sid + "/hosts", "list", RequestMethod.GET)
												.addLink(url + "/" + sid + "/hosts", "new", RequestMethod.POST)
												.addLink(url + "/" + sid + "/hosts/" + hid, "self", RequestMethod.GET)
												.addLink(url + "/" + sid + "/hosts/" + hid, "self", RequestMethod.PUT)
												.addLink(url + "/" + sid + "/hosts/" + hid, "self", RequestMethod.DELETE)
												.addLink(url + "/" + sid + "/hosts", "list", RequestMethod.DELETE)
												.addLink(url, "list", RequestMethod.GET)
                                                .wrap(host));
	}

	/**
	 * @param sid it is the id of the substrate network
	 * @param hid it is the name of the host to delete
	 */
	@Operation(tags = "version 1 - substrates", summary = "Delete a host", description = "The host is deleted only if it is not implied in any connection.")
	@RequestMapping(value = "/{sid}/hosts/{hid}", method = RequestMethod.DELETE)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "404", description = "The host doesn't exist at all. You can retry the operation or consider other hosts."),
			@ApiResponse(responseCode = "409", description = "The host cannot be deleted because it is implied in at least one connection. You can first delete the pertinent connections.")
		})
	@ResponseStatus(value = HttpStatus.NO_CONTENT)

	public ResponseEntity<Resources<Void>> deleteHost(@PathVariable("sid") long sid, @PathVariable("hid") long hid) {
		// Host host = service.deleteHost(sid, hid);
		// if (host == null)
		// 	throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
		String url = request.getRequestURL()
			.substring(0, request.getRequestURL().lastIndexOf("/"))
			.substring(0, request.getRequestURL().lastIndexOf("/"))
			.substring(0, request.getRequestURL().lastIndexOf("/"));
		return ResponseEntity.status(HttpStatus.OK).body(
                                // wrap the response with the hyperlinks
                                new ResourceWrapperWithLinks<Void>()
												.addLink(url + "/" + sid + "/connections", "new", RequestMethod.POST)
												.addLink(url + "/" + sid + "/connections", "list", RequestMethod.GET)
												.addLink(url + "/" + sid + "/hosts", "list", RequestMethod.GET)
												.addLink(url + "/" + sid + "/hosts", "new", RequestMethod.POST)
												.addLink(url + "/" + sid + "/hosts", "list", RequestMethod.DELETE)
												.addLink(url, "list", RequestMethod.GET)
                                                .wrap(null));
	}

	/* Connections */

	/**
	 * @param sid         it is the id of the substrate network
	 * @param connections they are the connections to add to the substrate network
	 * @return the created connections
	 */
	@Operation(tags = "version 1 - substrates", summary = "Create a list of connections between hosts in the substrate", description = "The hosts must already exist. If some connections already exist, the new connections are just added; the duplicates are discarded.")
	@RequestMapping(value = "/{sid}/connections", method = RequestMethod.POST)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Created"),
			@ApiResponse(responseCode = "400", description = "The passed connections resource is semantically malformed. You can retry the operation or check the data."),
			@ApiResponse(responseCode = "409", description = "The connections cannot be established between the existing hosts. You can check the hosts configuration or the connections.")
		})
	public ResponseEntity<Resources<Void>> createConnections(@PathVariable("sid") long sid,
			@RequestBody Connections connections) {
		// if (connections.getConnection().isEmpty())
		// 	throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bad request");
		// String url = request.getRequestURL().toString();
		// Connections created = service.createConnections(sid, connections);
		// if (created != null) {
		// 	if (created.getConnection().isEmpty())
		// 		throw new ResponseStatusException(HttpStatus.CONFLICT, "conflict");
		// 	String responseUrl;
		// 	if (url.toString().endsWith("/"))
		// 		responseUrl = url.toString() + "connections";
		// 	else
		// 		responseUrl = url.toString() + "/" + "connections";
		// 	HttpHeaders responseHeaders = new HttpHeaders();
		// 	try {
		// 		responseHeaders.setLocation(new URI(responseUrl));
		// 	} catch (URISyntaxException e) {
		// 		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bad request");
		// 	}
		// 	return new ResponseEntity<Connections>(created, responseHeaders, HttpStatus.CREATED);
		// } else
		// 	throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bad request");

		String url = request.getRequestURL()
				.substring(0, request.getRequestURL().lastIndexOf("/"))
				.substring(0, request.getRequestURL().lastIndexOf("/"));
		return ResponseEntity.status(HttpStatus.CREATED).body(
                                // wrap the response with the hyperlinks
                                new ResourceWrapperWithLinks<Void>()
												.addLink(url + "/" + sid + "/connections", "self", RequestMethod.GET)
												.addLink(url + "/" + sid + "/connections", "self", RequestMethod.PUT)
												.addLink(url + "/" + sid + "/connections", "self", RequestMethod.DELETE)
												.addLink(url + "/" + sid + "/connections", "new", RequestMethod.POST)
												.addLink(url + "/" + sid + "/hosts", "list", RequestMethod.GET)
												.addLink(url, "list", RequestMethod.GET)
                                                .wrap(null));
	}

	/**
	 * @param sid         it is the id of the substrate network
	 * @param connections it is the new value of the connections
	 */
	@Operation(tags = "version 1 - substrates", summary = "Update the connections of a substrate", description = "The new connections replace the existing ones in a shallow way.")
	@RequestMapping(value = "/{sid}/connections", method = RequestMethod.PUT)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "400", description = "The passed connections resource is semantically malformed. You can retry the operation or check the data."),
			@ApiResponse(responseCode = "404", description = "The substrate doesn't exist. You can create a new substrate or refer to another one."),
			@ApiResponse(responseCode = "409", description = "The connections cannot be established between the existing hosts. You can check the hosts configuration or the connections.")
		})

	public ResponseEntity<Resources<Void>> updateConnections(@PathVariable("sid") long sid, @RequestBody Connections connections) {
		// if (connections.getConnection().isEmpty())
		// 	throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bad request");
		// Connections updated = service.updateConnections(sid, connections);
		// if (updated == null)
		// 	throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
		// else if (updated.getConnection().isEmpty())
		// 	throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bad request");

		String url = request.getRequestURL()
				.substring(0, request.getRequestURL().lastIndexOf("/"))
				.substring(0, request.getRequestURL().lastIndexOf("/"));
		return ResponseEntity.status(HttpStatus.OK).body(
                                // wrap the response with the hyperlinks
                                new ResourceWrapperWithLinks<Void>()
												.addLink(url + "/" + sid + "/connections", "self", RequestMethod.GET)
												.addLink(url + "/" + sid + "/connections", "self", RequestMethod.PUT)
												.addLink(url + "/" + sid + "/connections", "self", RequestMethod.DELETE)
												.addLink(url + "/" + sid + "/connections", "new", RequestMethod.POST)
												.addLink(url + "/" + sid + "/hosts", "list", RequestMethod.GET)
												.addLink(url, "list", RequestMethod.GET)
                                                .wrap(null));
	}

	/**
	 * @param sid it is the id of the substrate network
	 * @return the connections of the substrate network
	 */
	@Operation(tags = "version 1 - substrates", summary = "Get all the connections of a substrate", description = "")
	@RequestMapping(value = "/{sid}/connections", method = RequestMethod.GET)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "404", description = "The substrate doesn't exist. You can create a new one or refer to another one.")
		})

	public ResponseEntity<Resources<Connections>> getConnections(@PathVariable("sid") long sid) {
		// Connections connections = service.getConnections(sid);
		// if (connections == null)
		// 	throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
		// return connections;

		Connections connections = null;

		String url = request.getRequestURL()
			.substring(0, request.getRequestURL().lastIndexOf("/"))
			.substring(0, request.getRequestURL().lastIndexOf("/"));
		return ResponseEntity.status(HttpStatus.OK).body(
                                // wrap the response with the hyperlinks
                                new ResourceWrapperWithLinks<Connections>()
												.addLink(url + "/" + sid + "/connections", "self", RequestMethod.GET)
												.addLink(url + "/" + sid + "/connections", "self", RequestMethod.PUT)
												.addLink(url + "/" + sid + "/connections", "self", RequestMethod.DELETE)
												.addLink(url + "/" + sid + "/connections", "new", RequestMethod.POST)
												.addLink(url + "/" + sid + "/hosts", "list", RequestMethod.GET)
												.addLink(url, "list", RequestMethod.GET)
                                                .wrap(connections));
	}

	/**
	 * @param sid it is the id of the substrate network whose connections must be
	 *            deleted
	 */
	@Operation(tags = "version 1 - substrates", summary = "Delete all the connections of a substrate", description = "")
	@RequestMapping(value = "/{sid}/connections", method = RequestMethod.DELETE)
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "404", description = "The substrate doesn't exist. You can retry the operation or refer to another substrate."), })

	public ResponseEntity<Resources<Void>> deleteConnections(@PathVariable("sid") long sid) {
		// Connections deleted = service.deleteConnections(sid);
		// if (deleted == null)
		// 	throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");

		String url = request.getRequestURL()
			.substring(0, request.getRequestURL().lastIndexOf("/"))
			.substring(0, request.getRequestURL().lastIndexOf("/"));
		return ResponseEntity.status(HttpStatus.OK).body(
                                // wrap the response with the hyperlinks
                                new ResourceWrapperWithLinks<Void>()
												.addLink(url + "/" + sid + "/connections", "new", RequestMethod.POST)
												.addLink(url + "/" + sid + "/hosts", "list", RequestMethod.GET)
												.addLink(url, "list", RequestMethod.GET)
                                                .wrap(null));
	}

}
