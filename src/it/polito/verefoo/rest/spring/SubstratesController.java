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
@RequestMapping(value = "/adp/substrates")
public class SubstratesController {

	ADPService service = new ADPService();

	@Autowired
	private HttpServletRequest request;

	/* Substrates */

	/**
	 * @param substrate is the substrate network to create
	 * @return the created substrate network
	 */
	@Operation(tags = "version 1")
	@ApiOperation(value = "createSubstrate", notes = "create a new substrate network", tags = "version 1")
	@RequestMapping(value = "", method = RequestMethod.POST)
	@ApiResponses(value = { @ApiResponse(code = 201, message = "Created"),
			@ApiResponse(code = 400, message = "Bad Request"), })
	public ResponseEntity<Hosts> createSubstrate(@RequestBody Hosts substrate) {
		long sid = service.getNextSubstrateId();
		StringBuffer url = request.getRequestURL();
		Hosts created = service.createSubstrate(sid, substrate);
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
			return new ResponseEntity<Hosts>(created, responseHeaders, HttpStatus.CREATED);
		} else
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bad request");
	}

	/**
	 * @param beforeInclusive it is the starting index
	 * @param afterInclusive  it is the ending index
	 * @return a collection of substrate network
	 */
	@Operation(tags = "version 1")
	@ApiOperation(value = "getSubstrates", notes = "searches subtrate networks", tags = "version 1")
	@RequestMapping(value = "", method = RequestMethod.GET)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 404, message = "Not Found"), })
	@ResponseBody
	public Collection<Hosts> getSubstrates(
			@RequestParam(name = "beforeInclusive", defaultValue = "1") int beforeInclusive,
			@RequestParam(name = "afterInclusive", defaultValue = "10") int afterInclusive) {
		Collection<Hosts> substrates = service.getSubstrates(beforeInclusive, afterInclusive);
		if (substrates.isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
		return substrates;
	}

	/**
	 * delete all the substrate network
	 */
	@Operation(tags = "version 1")
	@ApiOperation(value = "deleteSubstrates", notes = "delete all the substrate networks", tags = "version 1")
	@RequestMapping(value = "", method = RequestMethod.DELETE)
	@ApiResponses(value = { @ApiResponse(code = 204, message = "No Content"),
			@ApiResponse(code = 404, message = "Not Found"), })
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	@ResponseBody
	public void deleteSubstrates() {
		boolean removed = service.deleteSubstrates();
		if (!removed)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
	}

	/* Substrate */

	/**
	 * @param sid       it is the id of the substrate network
	 * @param substrate it is the new value of the substrate network
	 */
	@Operation(tags = "version 1")
	@ApiOperation(value = "updateSubstrate", notes = "update a single substrate network", tags = "version 1")
	@RequestMapping(value = "/substrates/{sid}", method = RequestMethod.PUT)
	@ApiResponses(value = { @ApiResponse(code = 204, message = "No Content"),
			@ApiResponse(code = 404, message = "Not Found"), })
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	@ResponseBody
	public void updateSubstrate(@PathVariable("sid") long sid, @RequestBody Hosts substrate) {
		Hosts updated = service.updateSubstrate(sid, substrate);
		if (updated == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
	}

	/**
	 * @param sid it is the id of the substrate network to retrieve
	 * @return the requested substrate network
	 */
	@Operation(tags = "version 1")
	@ApiOperation(value = "getSubstrate", notes = "retrieve a single substrate network", tags = "version 1")
	@RequestMapping(value = "/substrates/{sid}", method = RequestMethod.GET)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 404, message = "Not Found"), })
	@ResponseBody
	public Hosts getSubstrate(@PathVariable("sid") long sid) {
		Hosts substrate = service.getSubstrate(sid);
		if (substrate == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
		return substrate;
	}

	/**
	 * @param sid it is the id of the substrate network to delete
	 */
	@Operation(tags = "version 1")
	@ApiOperation(value = "deleteSubstrate", notes = "delete a single substrate", tags = "version 1")
	@RequestMapping(value = "/substrates/{sid}", method = RequestMethod.DELETE)
	@ApiResponses(value = { @ApiResponse(code = 204, message = "No Content"),
			@ApiResponse(code = 404, message = "Not Found"), })
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	@ResponseBody
	public void deleteSubstrate(@PathVariable("sid") long sid) {
		Hosts substrate = service.deleteSubstrate(sid);
		if (substrate == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
	}

	/* Hosts */

	/**
	 * @param sid  it is the id of the substrate network
	 * @param hid  it is the name of the host to create
	 * @param host it is the host to create
	 * @return the created host
	 */
	@Operation(tags = "version 1")
	@ApiOperation(value = "createHost", notes = "create a new host", tags = "version 1")
	@RequestMapping(value = "/substrates/{sid}/hosts", method = RequestMethod.POST)
	@ApiResponses(value = { @ApiResponse(code = 201, message = "Created"),
			@ApiResponse(code = 400, message = "Bad Request"), })
	public ResponseEntity<Host> createHost(@PathVariable("sid") long sid, @RequestParam(name = "hid") String hid,
			Host host) {
		if (hid == null)
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bad request");
		String url = request.getRequestURL().toString();
		Host created = service.createHost(sid, hid, host);
		if (created != null) {
			String responseUrl;
			if (url.toString().endsWith("/"))
				responseUrl = url.toString() + hid;
			else
				responseUrl = url.toString() + "/" + hid;
			HttpHeaders responseHeaders = new HttpHeaders();
			try {
				responseHeaders.setLocation(new URI(responseUrl));
			} catch (URISyntaxException e) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bad request");
			}
			return new ResponseEntity<Host>(created, responseHeaders, HttpStatus.CREATED);
		} else
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bad request");
	}

	/**
	 * @param sid  it is the id of the substrate network
	 * @param hid  it is the name of the host to update
	 * @param host it is the new value of the host
	 */
	@Operation(tags = "version 1")
	@ApiOperation(value = "updateHost", notes = "update a single host", tags = "version 1")
	@RequestMapping(value = "/substrates/{sid}/hosts/{hid}", method = RequestMethod.PUT)
	@ApiResponses(value = { @ApiResponse(code = 204, message = "No Content"),
			@ApiResponse(code = 404, message = "Not Found"), })
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	@ResponseBody
	public void updateRule(@PathVariable("sid") long sid, @PathVariable("hid") String hid, @RequestBody Host host) {
		Host updated = service.updateHost(sid, hid, host);
		if (updated == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
	}

	/**
	 * @param sid it is the id of the substrate network
	 * @param hid it is the name of the host to retrieve
	 * @return the request host
	 */
	@Operation(tags = "version 1")
	@ApiOperation(value = "getHost", notes = "retrieve a single host", tags = "version 1")
	@RequestMapping(value = "/substrates/{sid}/hosts/{hid}", method = RequestMethod.GET)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 404, message = "Not Found"), })
	@ResponseBody
	public Host getHost(@PathVariable("sid") long sid, @PathVariable("hid") String hid) {
		Host host = service.getHost(sid, hid);
		if (host == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
		return host;
	}

	/**
	 * @param sid it is the id of the substrate network
	 * @param hid it is the name of the host to delete
	 */
	@Operation(tags = "version 1")
	@ApiOperation(value = "deleteHost", notes = "delete a single host", tags = "version 1")
	@RequestMapping(value = "/substrates/{sid}/hosts/{hid}", method = RequestMethod.DELETE)
	@ApiResponses(value = { @ApiResponse(code = 204, message = "No Content"),
			@ApiResponse(code = 404, message = "Not Found"), })
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	@ResponseBody
	public void deleteHost(@PathVariable("sid") long sid, @PathVariable("hid") long hid) {
		Host host = service.deleteHost(sid, hid);
		if (host == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
	}

	/* Connections */

	/**
	 * @param sid         it is the id of the substrate network
	 * @param connections they are the connections to add to the substrate network
	 * @return the created connections
	 */
	@Operation(tags = "version 1")
	@ApiOperation(value = "createConnections", notes = "create a set of connections for a substrate network", tags = "version 1")
	@RequestMapping(value = "/substrates/{sid}", method = RequestMethod.POST)
	@ApiResponses(value = { @ApiResponse(code = 201, message = "Created"),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 409, message = "Conflict"), })
	public ResponseEntity<Connections> createConnections(@PathVariable("sid") long sid,
			@RequestBody Connections connections) {
		if (connections.getConnection().isEmpty())
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bad request");
		String url = request.getRequestURL().toString();
		Connections created = service.createConnections(sid, connections);
		if (created != null) {
			if (created.getConnection().isEmpty())
				throw new ResponseStatusException(HttpStatus.CONFLICT, "conflict");
			String responseUrl;
			if (url.toString().endsWith("/"))
				responseUrl = url.toString() + "connections";
			else
				responseUrl = url.toString() + "/" + "connections";
			HttpHeaders responseHeaders = new HttpHeaders();
			try {
				responseHeaders.setLocation(new URI(responseUrl));
			} catch (URISyntaxException e) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bad request");
			}
			return new ResponseEntity<Connections>(created, responseHeaders, HttpStatus.CREATED);
		} else
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bad request");
	}

	/**
	 * @param sid         it is the id of the substrate network
	 * @param connections it is the new value of the connections
	 */
	@Operation(tags = "version 1")
	@ApiOperation(value = "updateConnections", notes = "update the connections of a substrate network", tags = "version 1")
	@RequestMapping(value = "/substrates/{sid}/connections", method = RequestMethod.PUT)
	@ApiResponses(value = { @ApiResponse(code = 204, message = "No Content"),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 404, message = "Not Found"), })
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	@ResponseBody
	public void updateConnections(@PathVariable("sid") long sid, @RequestBody Connections connections) {
		if (connections.getConnection().isEmpty())
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bad request");
		Connections updated = service.updateConnections(sid, connections);
		if (updated == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
		else if (updated.getConnection().isEmpty())
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bad request");
	}

	/**
	 * @param sid it is the id of the substrate network
	 * @return the connections of the substrate network
	 */
	@Operation(tags = "version 1")
	@ApiOperation(value = "getConnections", notes = "retrieve the connections of a substrate network", tags = "version 1")
	@RequestMapping(value = "/substrates/{sid}/connections", method = RequestMethod.GET)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 404, message = "Not Found"), })
	@ResponseBody
	public Connections getConnections(@PathVariable("sid") long sid) {
		Connections connections = service.getConnections(sid);
		if (connections == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
		return connections;
	}

	/**
	 * @param sid it is the id of the substrate network whose connections must be
	 *            deleted
	 */
	@Operation(tags = "version 1")
	@ApiOperation(value = "deleteConnections", notes = "delete the connections of a substrate network", tags = "version 1")
	@RequestMapping(value = "/substrates/{sid}/connections", method = RequestMethod.DELETE)
	@ApiResponses(value = { @ApiResponse(code = 204, message = "No Content"),
			@ApiResponse(code = 404, message = "Not Found"), })
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	@ResponseBody
	public void deleteConnections(@PathVariable("sid") long sid) {
		Connections deleted = service.deleteConnections(sid);
		if (deleted == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
	}

}
