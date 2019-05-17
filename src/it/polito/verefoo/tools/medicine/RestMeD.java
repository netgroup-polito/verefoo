package it.polito.verefoo.tools.medicine;

import java.net.MalformedURLException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.swagger.annotations.*;
import it.polito.verefoo.extra.BadGraphError;
import it.polito.verefoo.jaxb.ApplicationError;
import it.polito.verefoo.jaxb.EType;
import it.polito.verefoo.jaxb.Hosts;
import it.polito.verefoo.jaxb.NFV;

/**
 * 
 * This class implements the web service that deals with MeDICINE simulator
 *
 */
@Path("/simulation")
@Api("/simulation")
public class RestMeD {
		TopologyDB db = TopologyDB.getMedicineDB(); 
	    @POST
	    @ApiOperation(value = "Deploys the network model in the MeDICINE topology simulator", notes = "",
	    response=Integer.class)	    
	    @ApiResponses(value = {
	    	    		@ApiResponse(code = 200, message = "OK", response=NFV.class),
	    	    		@ApiResponse(code = 400, message = "Something wrong with the client request",response=ApplicationError.class),
	    	    		@ApiResponse(code = 415, message = "Invalid Media Type"),
	    	    		@ApiResponse(code = 500, message = "Something wrong with server", response=ApplicationError.class),
	    				@ApiResponse(code = 503, message = "Service temporarily unavailable")})
	    @Consumes(MediaType.APPLICATION_XML)
	    public Response put(@Context HttpServletRequest req,@ApiParam(value = "Network Schema", required = true) NFV root) throws MalformedURLException {
				if(root.getPropertyDefinition().getProperty().stream().filter(p->!p.isIsSat()).count() > 0)
					throw new BadGraphError("Properties are not satisfied",EType.INVALID_PROPERTY_DEFINITION);
				try{
					if(db.getResourceModel() == null){
						MedicineSimulator s = new MedicineSimulator(root);
						db.setResourceModel(s);	
						return Response.ok().build();
					}
					return Response.serverError().entity("Simulation already running").build();
				}catch(Exception e){
					return Response.serverError().entity("Something went wrong with the MeDICINE Simulation").build();
				}
	    }
	    @GET
	    @ApiOperation(value = "Gets the physical topology from the MeDICINE simulation", notes = "",
	    response=Hosts.class)	    
	    @ApiResponses(value = {
	    	    		@ApiResponse(code = 200, message = "OK", response=NFV.class),
	    	    		@ApiResponse(code = 400, message = "Something wrong with the client request",response=ApplicationError.class),
	    	    		@ApiResponse(code = 415, message = "Invalid Media Type"),
	    	    		@ApiResponse(code = 500, message = "Something wrong with server", response=ApplicationError.class),
	    				@ApiResponse(code = 503, message = "Service temporarily unavailable")})
	    @Produces(MediaType.APPLICATION_XML)
	    public Response get(){ 
	    	
			try {
				Hosts hosts = db.getResourceModel();
				if(hosts == null) return Response.serverError().entity("No simulation is running").build();
				return Response.ok(hosts, MediaType.APPLICATION_XML).build();
			} catch (ResourceModelException e) {
				return Response.serverError().entity("Error retrieving informations").build();
			}
	        
	    }
	    @DELETE
	    @ApiOperation(value = "Stops the MeDICINE simulation", notes = "")	    
	    @ApiResponses(value = {
	    	    		@ApiResponse(code = 200, message = "OK", response=NFV.class),
	    	    		@ApiResponse(code = 400, message = "Something wrong with the client request",response=ApplicationError.class),
	    	    		@ApiResponse(code = 415, message = "Invalid Media Type"),
	    	    		@ApiResponse(code = 500, message = "Something wrong with server", response=ApplicationError.class),
	    				@ApiResponse(code = 503, message = "Service temporarily unavailable")})
	    public Response delete(){
	    	try {
				db.removeResourceModel();
				return Response.ok().build();
			} catch (ResourceModelException e) {
				return Response.serverError().entity("Error removing information").build();
			}
	    	
	    }
}