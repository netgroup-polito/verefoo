package it.polito.verifoo.rest.webservice;

import java.io.File;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;



@Path("/")
public class RestDoc {
	    @GET
	    public Response get(@Context ServletContext context){
	    	 File file = new File(context.getRealPath("/WEB-INF/classes/VerifooDocs.pdf"));
	    	 Response response = null;
    	    if (file.exists()) {
    	      ResponseBuilder builder = Response.ok(file).type("application/pdf");
    	      builder.header("Content-Disposition", "filename=" + file.getName());
    	      response = builder.build();
    	    } else {
    	      response = Response.status(404).
    	              entity("FILE NOT FOUND: ").
    	              type("text/plain").
    	              build();
    	    }	    	      
    	    return response;
	    }
}
