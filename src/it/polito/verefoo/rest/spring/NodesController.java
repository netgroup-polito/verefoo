package it.polito.verefoo.rest.spring;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.polito.verefoo.jaxb.ActionTypes;
import it.polito.verefoo.jaxb.Configuration;
import it.polito.verefoo.jaxb.Elements;
import it.polito.verefoo.jaxb.Firewall;
import it.polito.verefoo.jaxb.Graph;
import it.polito.verefoo.jaxb.NFV;
import it.polito.verefoo.jaxb.Node;

@Controller
@RequestMapping(value = "/fwd/nodes")
public class NodesController {

	FDWService service = new FDWService();
	@Autowired
	private HttpServletRequest request;
	
	//nfv
	
	@ApiOperation(value = "createNodesFromNFV", notes = "create a set of nodes from a NFV")
	@RequestMapping(value = "/addnfv", method = RequestMethod.POST)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 400, message = "Bad Request"), })
	@ResponseStatus(value = HttpStatus.OK)
	public void createNodesNFV(@RequestBody NFV nfv, HttpServletResponse response) {
		String created = service.loadNFV(nfv);
		StringBuffer url = request.getRequestURL();
		if (created != null) {
			String responseUrl;
			if (url.toString().endsWith("/"))
				responseUrl = url.toString().substring(0, url.toString().length()-new String("/addnfv/").length());
				//responseUrl = url.toString().substring(0, url.toString().length()-8);
			else
				responseUrl = url.toString().substring(0, url.toString().length()-new String("/addnfv").length());
				//responseUrl = url.toString().substring(0, url.toString().length()-7);
			try {
				response.sendRedirect(responseUrl+"?afterInclusive="+created.split("-")[1]+"&beforeInclusive="+created.split("-")[0]);
			} catch (IOException  e) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bad request");
			}
		} else
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bad request");
	}

	//graph
	
	
	@ApiOperation(value = "createNodesFromGraph", notes = "create a set of nodes from a graph")
	@RequestMapping(value = "/addgraph", method = RequestMethod.POST)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 400, message = "Bad Request"), })
	@ResponseStatus(value = HttpStatus.OK)
	public void createNodesGraph(@RequestBody Graph g, HttpServletResponse response) {
		String created = service.loadGraph(g);
		StringBuffer url = request.getRequestURL();
		if (created != null) {
			String responseUrl;
			if (url.toString().endsWith("/"))
				responseUrl = url.toString().substring(0, url.toString().length()-new String("/addgraph/").length());
			else
				responseUrl = url.toString().substring(0, url.toString().length()-new String("/addgraph").length());
			try {
				response.sendRedirect(responseUrl+"?afterInclusive="+created.split("-")[1]+"&beforeInclusive="+created.split("-")[0]);
			} catch (IOException  e) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bad request");
			}
		} else
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bad request");
	}
	
	//node

	@ApiOperation(value = "createNode", notes = "create a new node")
	@RequestMapping(value = "", method = RequestMethod.POST)
	@ApiResponses(value = { @ApiResponse(code = 201, message = "Created"),
			@ApiResponse(code = 400, message = "Bad Request"),@ApiResponse(code = 409, message = "Conflict"), })
	public ResponseEntity<Node> createNode(@RequestBody Node node) {
		long nid = service.getNextNodeId();
		StringBuffer url = request.getRequestURL();
		Node created = service.createNode(nid, node);
		if (created != null) {
			if (created.getName() == null)
				throw new ResponseStatusException(HttpStatus.CONFLICT, "conflict");
			String responseUrl;
			if (url.toString().endsWith("/"))
				responseUrl = url.toString() + nid;
			else
				responseUrl = url.toString() + "/" + nid;
			HttpHeaders responseHeaders = new HttpHeaders();
			try {
				responseHeaders.setLocation(new URI(responseUrl));
			} catch (URISyntaxException e) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bad request");
			}
			return new ResponseEntity<Node>(created, responseHeaders, HttpStatus.CREATED);
		} else
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bad request");
	}

	@ApiOperation(value = "getNodes", notes = "searches nodes")
	@RequestMapping(value = "", method = RequestMethod.GET)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 404, message = "Not Found"), @ApiResponse(code = 400, message = "Bad Request"),})
	@ResponseBody
	public List<Node> getNodes(@RequestParam(name = "beforeInclusive", defaultValue = "1") int beforeInclusive,
			@RequestParam(name = "afterInclusive", defaultValue = "10") int afterInclusive) {
		List<Node> nodes = service.getNodes(beforeInclusive, afterInclusive);
		if (nodes == null)
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bad request");
		if (nodes.isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
		return nodes;
	}

	@ApiOperation(value = "deleteNodes", notes = "delete all the nodes")
	@RequestMapping(value = "", method = RequestMethod.DELETE)
	@ApiResponses(value = { @ApiResponse(code = 204, message = "No Content"),
			 @ApiResponse(code = 404, message = "Not Found"), })
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	@ResponseBody
	public void deleteNodes() {
		List<Node> nodes = service.deleteNodes();
		if (nodes == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
	}
	// node

	@ApiOperation(value = "updateNode", notes = "update single node")
	@RequestMapping(value = "/{nid}", method = RequestMethod.PUT)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 404, message = "Not Found"), })
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	@ResponseBody
	public void updateNode(@PathVariable("nid") long nid, @RequestBody Node node) {
		Node updated = service.updateNode(nid, node);
		if (updated == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
	}

	@ApiOperation(value = "getNode", notes = "retrieve single node")
	@RequestMapping(value = "/{nid}", method = RequestMethod.GET)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 404, message = "Not Found"), })
	@ResponseBody
	public Node getNode(@PathVariable("nid") long nid) {
		Node node = service.getNode(nid);
		if (node == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
		node.setId(nid);
		return node;
	}

	@ApiOperation(value = "deleteNode", notes = "delete single node")
	@RequestMapping(value = "/{nid}", method = RequestMethod.DELETE)
	@ApiResponses(value = { @ApiResponse(code = 204, message = "No Content"),
			@ApiResponse(code = 404, message = "Not Found"), })
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	@ResponseBody
	public void deleteNode(@PathVariable("nid") long nid) {
		Node node = service.deleteNode(nid);
		if (node == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
	}

	// configuration

	@ApiOperation(value = "updateConfiguration", notes = "update the configuration of a node")
	@RequestMapping(value = "/{nid}/configuration", method = RequestMethod.PUT)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 404, message = "Not Found") })
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	@ResponseBody
	public void updateConfiguration(@PathVariable("nid") long nid, @RequestBody Configuration configuration) {

		Configuration updated = service.updateConfiguration(nid, configuration);
		if (updated == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
	}

	// firewall
	@ApiOperation(value = "createPolicies", notes = "create a set of policies for a node")
	@RequestMapping(value = "/{nid}/configuration", method = RequestMethod.POST)
	@ApiResponses(value = { @ApiResponse(code = 201, message = "Created"),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 409, message = "Conflict"), })
	public ResponseEntity<Firewall> createPolicies(@PathVariable("nid") long nid, @RequestBody Firewall firewall) {

		if (!(firewall.getDefaultAction().equals(ActionTypes.ALLOW))
				&& !(firewall.getDefaultAction().equals(ActionTypes.DENY)))
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bad request");
		StringBuffer url = request.getRequestURL();
		String responseUrl;
		if (url.toString().endsWith("/"))
			responseUrl = url.toString() + "firewall";
		else
			responseUrl = url.toString() + "/firewall";
		Firewall created = service.createFirewall(nid, firewall);
		if (created != null) {
			if (created.getDefaultAction() == null)
				throw new ResponseStatusException(HttpStatus.CONFLICT, "conflict");
			HttpHeaders responseHeaders = new HttpHeaders();
			try {
				responseHeaders.setLocation(new URI(responseUrl));
			} catch (URISyntaxException e) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bad request");
			}
			return new ResponseEntity<Firewall>(created, responseHeaders, HttpStatus.CREATED);
		} else
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bad request");
	}

	@ApiOperation(value = "createPolicy", notes = "create a policy for a node")
	@RequestMapping(value = "/{nid}/configuration/firewall", method = RequestMethod.POST)
	@ApiResponses(value = { @ApiResponse(code = 201, message = "Created"),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 409, message = "Conflict"), })
	public ResponseEntity<Elements> createPolicy(@PathVariable("nid") long nid, @RequestBody Elements element) {

		if (element.getAction() == null || element.getProtocol() == null || element.getDestination() == null
				|| element.getSource() == null)
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bad request");
		long eid = service.getNextElementId(nid);
		StringBuffer url = request.getRequestURL();
		String responseUrl;
		if (url.toString().endsWith("/"))
			responseUrl = url.toString() + eid;
		else
			responseUrl = url.toString() + "/" + eid;
		Elements created = service.createPolicy(nid, eid, element);
		if (created != null) {
			if (created.getAction() == null)
				throw new ResponseStatusException(HttpStatus.CONFLICT, "conflict");
			HttpHeaders responseHeaders = new HttpHeaders();
			try {
				responseHeaders.setLocation(new URI(responseUrl));
			} catch (URISyntaxException e) {
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bad request");
			}
			return new ResponseEntity<Elements>(created, responseHeaders, HttpStatus.CREATED);
		} else
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bad request");
	}

	@ApiOperation(value = "updatePolicy", notes = "update single policy of a node")
	@RequestMapping(value = "/{nid}/configuration/firewall/{eid}", method = RequestMethod.PUT)
	@ApiResponses(value = { @ApiResponse(code = 204, message = "No Content"),
			@ApiResponse(code = 400, message = "Bad Request"), @ApiResponse(code = 404, message = "Not Found"),@ApiResponse(code = 409, message = "Conflict"), })
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	@ResponseBody
	public void updatePolicy(@PathVariable("eid") long eid, @PathVariable("nid") long nid,
			@RequestBody Elements policy) {
		if (policy.getAction() == null || policy.getProtocol() == null || policy.getDestination() == null
				|| policy.getSource() == null)
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "bad request");
		Elements updated = service.updatePolicy(eid, nid, policy);
		if (updated == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
		else if(updated.getAction()==null) 
			throw new ResponseStatusException(HttpStatus.CONFLICT, "conflict");
		
	}

	@ApiOperation(value = "getPolicy", notes = "retrieve single policy")
	@RequestMapping(value = "/{nid}/configuration/firewall/{eid}", method = RequestMethod.GET)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK"),
			@ApiResponse(code = 404, message = "Not Found"), })
	@ResponseBody
	public Elements getPolicy(@PathVariable("eid") long eid, @PathVariable("nid") long nid) {
		Elements policy = service.getPolicy(eid, nid);
		if (policy == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
		return policy;
	}

	@ApiOperation(value = "deletePolicy", notes = "delete single policy of a node")
	@RequestMapping(value = "/{nid}/configuration/firewall/{eid}", method = RequestMethod.DELETE)
	@ApiResponses(value = { @ApiResponse(code = 204, message = "No Content"),
			@ApiResponse(code = 404, message = "Not Found"), })
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	@ResponseBody
	public void deletePolicy(@PathVariable("eid") long eid, @PathVariable("nid") long nid) {
		Elements policy = service.deletePolicy(eid, nid);
		if (policy == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "not found");
	}
}
