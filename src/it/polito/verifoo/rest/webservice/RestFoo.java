package it.polito.verifoo.rest.webservice;

import java.net.MalformedURLException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import com.microsoft.z3.Status;

import io.swagger.annotations.*;
import it.polito.verifoo.rest.common.BadGraphError;
import it.polito.verifoo.rest.common.Translator;
import it.polito.verifoo.rest.common.VerifooProxy;
import it.polito.verifoo.rest.jaxb.ApplicationError;
import it.polito.verifoo.rest.jaxb.Graph;
import it.polito.verifoo.rest.jaxb.NFV;
import it.polito.verigraph.mcnet.components.IsolationResult;

/**
 * 
 * This class implements the web service that deals with the deployment requests
 *
 */
@Path("/deployment")
@Api("/deployment")
public class RestFoo {
	    @POST
	    @ApiOperation(value = "Tests and Deploys a network model", notes = "This is the main API of the Verifoo Service. It provides, for each graph, the verification of the validity of the network model and the optimised deployment on the hosts.",
	    response=NFV.class)
	    
	    @ApiResponses(value = {
	    	    		@ApiResponse(code = 200, message = "OK", response=NFV.class),
	    	    		@ApiResponse(code = 400, message = "Something wrong with the client request",response=ApplicationError.class),
	    	    		@ApiResponse(code = 415, message = "Invalid Media Type"),
	    	    		@ApiResponse(code = 500, message = "Something wrong with server", response=ApplicationError.class),
	    				@ApiResponse(code = 503, message = "Service temporarily unavailable")})
	    @Consumes(MediaType.APPLICATION_XML)
		@Produces(MediaType.APPLICATION_XML)
	    public NFV put(@Context HttpServletRequest req,@ApiParam(value = "Network Schema", required = true) NFV root) throws MalformedURLException {
				String z3model = new String();
				for(Graph g:root.getGraphs().getGraph()){
	            	VerifooProxy test = new VerifooProxy(g, root.getHosts(), root.getConnections(), root.getCapacityDefinition());
	            	IsolationResult res=test.checkNFFGProperty();
	            	if(res.result != Status.UNSATISFIABLE){
	            		z3model=z3model.concat(res.model.toString());
	            	}
	            	root.getPropertyDefinition().getProperty().stream().filter(p->p.getGraph()==g.getId()).findFirst().get().setIsSat(res.result!=Status.UNSATISFIABLE);            
	            }
				if(!z3model.isEmpty()){
		            new Translator(z3model,root).convert();
					/*
					root.setParsingString(z3model);			
					Response res = ClientBuilder.newClient()
							.target(req.getRequestURL().toString().replace("/rest", ""))
							.path("/translate")
							.request(MediaType.APPLICATION_XML)
							.accept(MediaType.APPLICATION_XML)
							.post(Entity.entity(root,MediaType.APPLICATION_XML));
					if(res.getStatusInfo()==javax.ws.rs.core.Response.Status.OK){
							return res.readEntity(NFV.class);
					}else{
							throw new ProcessingException("Translator Error:"+res.readEntity(String.class));
					}*/
				}
				return root;
	    }

}