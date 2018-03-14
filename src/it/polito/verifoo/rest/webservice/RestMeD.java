package it.polito.verifoo.rest.webservice;

import java.net.MalformedURLException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.swagger.annotations.*;
import it.polito.verifoo.rest.common.BadGraphError;
import it.polito.verifoo.rest.jaxb.ApplicationError;
import it.polito.verifoo.rest.jaxb.EType;
import it.polito.verifoo.rest.jaxb.Hosts;
import it.polito.verifoo.rest.jaxb.NFV;
import it.polito.verifoo.rest.medicine.MedicineDB;
import it.polito.verifoo.rest.medicine.MedicineSimulator;

/**
 * 
 * This class implements the web service that deals with MeDICINE simulator
 *
 */
@Path("/simulation")
@Api("/simulation")
public class RestMeD {
		MedicineDB db = new MedicineDB();
		
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
		@Produces(MediaType.APPLICATION_XML)
	    public Response put(@Context HttpServletRequest req,@ApiParam(value = "Network Schema", required = true) NFV root) throws MalformedURLException {
				if(root.getPropertyDefinition().getProperty().stream().filter(p->!p.isIsSat()).count() > 0)
					throw new BadGraphError("Properties are not satisfied",EType.INVALID_PROPERTY_DEFINITION);
				try{
					MedicineSimulator s = new MedicineSimulator(root);
					int id = db.addSimulation(s);	
					return Response.ok(id).build();
				}catch(Exception e){
					return Response.serverError().entity("Something went wrong with the MeDICINE Simulation").build();
				}
	    }
	    //for retrive the topology infos
	    @GET
	    @Path("/{id}")
	    @ApiOperation(value = "Gets the physical topology from the MeDICINE simulation for the specified id", notes = "",
	    response=Hosts.class)	    
	    @ApiResponses(value = {
	    	    		@ApiResponse(code = 200, message = "OK", response=NFV.class),
	    	    		@ApiResponse(code = 400, message = "Something wrong with the client request",response=ApplicationError.class),
	    	    		@ApiResponse(code = 415, message = "Invalid Media Type"),
	    	    		@ApiResponse(code = 500, message = "Something wrong with server", response=ApplicationError.class),
	    				@ApiResponse(code = 503, message = "Service temporarily unavailable")})
	    @Produces(MediaType.APPLICATION_XML)
	    public Response get(@PathParam("id") String id) {
	        if(id == null || id.trim().length() == 0) {
	            return Response.serverError().entity("ID cannot be blank").build();
	        }
	        MedicineSimulator s = db.getSimulation(Integer.parseInt(id));
	        if(s == null) {
	            return Response.status(Response.Status.NOT_FOUND).entity("Entity not found for ID: " + id).build();
	        }
	        Hosts hosts = s.getPhysicalTopology();
	        return Response.ok(hosts, MediaType.APPLICATION_XML).build();
	    }
	    @DELETE
	    @Path("/{id}")
	    @ApiOperation(value = "Stops the MeDICINE simulation for the specified id", notes = "")	    
	    @ApiResponses(value = {
	    	    		@ApiResponse(code = 200, message = "OK", response=NFV.class),
	    	    		@ApiResponse(code = 400, message = "Something wrong with the client request",response=ApplicationError.class),
	    	    		@ApiResponse(code = 415, message = "Invalid Media Type"),
	    	    		@ApiResponse(code = 500, message = "Something wrong with server", response=ApplicationError.class),
	    				@ApiResponse(code = 503, message = "Service temporarily unavailable")})
	    public void delete(@PathParam("id")int id){
	       db.removeSimulation(id);
	    }
}