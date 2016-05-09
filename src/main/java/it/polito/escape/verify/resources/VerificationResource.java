package it.polito.escape.verify.resources;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.BeanParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.polito.escape.verify.Entry;
import it.polito.escape.verify.client.Neo4jManagerClient;
import it.polito.escape.verify.model.ErrorMessage;
import it.polito.escape.verify.model.Neighbour;
import it.polito.escape.verify.model.Node;
import it.polito.escape.verify.resources.beans.VerificationBean;
import it.polito.escape.verify.service.NodeService;
import it.polito.nffg.neo4j.jaxb.Paths;

@Path("/chains")
@Api( value = "/chains", description = "Manage nodes" )
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
//@Produces(MediaType.TEXT_PLAIN)
public class VerificationResource {
	NodeService nodeService = new NodeService();
	
	@GET
	@ApiOperation(
    	    httpMethod = "GET",
    	    value = "Returns all the paths between a given source and destination",
    	    notes = "Returns possibly multiple paths",
    	    response = Paths.class)
	@ApiResponses(value = { @ApiResponse(code = 400, message = "Invalid request")})
	//@Produces(MediaType.TEXT_PLAIN)
	public Paths getChains(@BeanParam VerificationBean validationBean){		
		String source = validationBean.getSource() + "_" + nodeService.searchByName(validationBean.getSource()).getId();
		String destination = validationBean.getDestination() + "_" + nodeService.searchByName(validationBean.getDestination()).getId();
		//System.out.println("Source: " + source + ", destination: " + destination);
		if (source == null || destination == null){
			ErrorMessage errorMessage = new ErrorMessage("Bad request", 400, "http://localhost:8080/verify/api-docs/");
			Response response = Response.status(Status.BAD_REQUEST)
					.entity(errorMessage)
					.build();
			throw new WebApplicationException(response);
		}
		List<String> endpoints = new ArrayList<>();
		List<String> firewalls = new ArrayList<>();
		Map<String, List<Entry>> routingTable = new HashMap<>();
		
		for (Node node : nodeService.getAllNodes()){
			// if firewall
			if (node.getFunctional_type().equals("NF")){
				// add 2 connection points to RT
				routingTable.put(node.getName() + "_" + node.getId() + "_in", new ArrayList<Entry>());
				routingTable.put(node.getName() + "_" + node.getId() + "_out", new ArrayList<Entry>());
				// add node to firewalls
				firewalls.add(node.getName() + "_" + node.getId());
				//scan neighbours
				for (Neighbour neighbour : node.getNeighbours().values()){
					//check if neighbour is a firewall
					Node hop = nodeService.searchByName(neighbour.getName());
					// if neighbour is a firewall connect to its input port
					if (hop.getFunctional_type().equals("NF"))
						routingTable.get(node.getName() + "_" + node.getId() + "_out").add(new Entry("output", neighbour.getName() + "_" + hop.getId() + "_in"));
					else
						//connect normally to node
						routingTable.get(node.getName() + "_" + node.getId() + "_out").add(new Entry("output", neighbour.getName() + "_" + hop.getId()));
				}
			}
			// if endpoint
			else {
				// add endpoint to RT
				routingTable.put(node.getName() + "_" + node.getId(), new ArrayList<Entry>());
				// add to endpoints
				endpoints.add(node.getName() + "_" + node.getId());
				// scan neighbours
				for (Neighbour neighbour : node.getNeighbours().values()){
					//check if neighbour is a firewall
					Node hop = nodeService.searchByName(neighbour.getName());
					// if neighbour is a firewall connect to its input port
					if (hop.getFunctional_type().equals("NF"))
						routingTable.get(node.getName() + "_" + node.getId()).add(new Entry("output", neighbour.getName() + "_" + hop.getId() + "_in"));
					else
						//connect normally to node
						routingTable.get(node.getName() + "_" + node.getId()).add(new Entry("output", neighbour.getName() + "_" + hop.getId()));
				}
			}
			
		//end node scan	
		}
		System.out.println("Endpoints:");
		for (String endpoint : endpoints){
			System.out.println(endpoint);
		}
		System.out.println("Firewalls:");
		for (String firewall : firewalls){
			System.out.println(firewall);
		}
		System.out.println("Source: " + source);
		System.out.println("Destination: " + destination);
		for (String key: routingTable.keySet()){
			System.out.println("RT for node " + key);
			for (Entry entry : routingTable.get(key)){
				System.out.println(entry.getDirection() + " " + entry.getDestination());
			}
		}
		Neo4jManagerClient client = new Neo4jManagerClient(source, destination, endpoints, firewalls, routingTable);
		Paths paths = client.runClient();
		//String result = client.runClient();
		//System.out.println(result);
		//return result;
		return paths;
		
		/*StringBuffer sBuffer = new StringBuffer("");
		for (String path : paths.getPath()){
			sBuffer.append(path);
			System.out.println(path);
		}
		return sBuffer.toString();*/
		
	}

}
