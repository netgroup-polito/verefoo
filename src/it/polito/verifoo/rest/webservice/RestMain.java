package it.polito.verifoo.rest.webservice;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.appender.FileAppender;

import io.swagger.annotations.*;
import it.polito.verifoo.rest.common.LogReader;

/**
 * This class implements the web service that deals with the log request (present only for debugging purposes)
 *
 */
/*
@Path("/")
@Api(value = "/")

public class RestMain {		
	    @GET
	    @ApiOperation(value = "Get Verifoo Webservice Links", notes = "Get Links for Hateoas compliancy"
	    		)
   	    @ApiResponses(value = {
	    		@ApiResponse(code = 200, message = "OK"),
	    		})

		@Produces(MediaType.APPLICATION_XML)
	    public Links get(@Context ServletContext context){
	    	
			return null;
	    }
}*/