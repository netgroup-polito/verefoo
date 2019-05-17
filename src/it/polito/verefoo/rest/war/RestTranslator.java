package it.polito.verefoo.rest.war;

import java.net.MalformedURLException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.polito.verefoo.extra.BadGraphError;
import it.polito.verefoo.jaxb.ApplicationError;
import it.polito.verefoo.jaxb.EType;
import it.polito.verefoo.jaxb.NFV;
import it.polito.verefoo.translator.Translator;

/**
 * 
 * This class implements the web service that deals with the converter requests
 *
 */
@Path("/converter")
@Api("/converter")
public class RestTranslator {
	    @POST
	    @ApiOperation(value = "Converts the output model of Verifoo", notes = "This API takes an XML file containing the output model of Verifoo and it converts it into an XML with the correct deployment.",
	    	    response=NFV.class)
	    
	    @ApiResponses(value = {
	    		@ApiResponse(code = 200, message = "OK", response=NFV.class),
	    		@ApiResponse(code = 400, message = "Something wrong with the client request",response=ApplicationError.class),
	    		@ApiResponse(code = 415, message = "Invalid Media Type"),
	    		@ApiResponse(code = 500, message = "Something wrong with server", response=ApplicationError.class),
				@ApiResponse(code = 503, message = "Service temporarily unavailable")})
	    
	    @Consumes(MediaType.APPLICATION_XML)
		@Produces(MediaType.APPLICATION_XML)
	    public NFV put(@ApiParam(value = "Complete or Tiny Response")@DefaultValue("true")@QueryParam("complete") Boolean complete,@ApiParam(value = "Network Schema with parsing string", required = true)NFV root) throws MalformedURLException {
	    	if(root.getParsingString()!=null && !root.getParsingString().isEmpty()){
	    		root.getGraphs().getGraph().forEach(g -> {
		            new Translator(root.getParsingString(),root,g).convert(); 
	    		});
	            root.setParsingString("");
	            if(complete!=true) {
					root.getHosts().getHost().removeIf((h)->!h.isActive());
					root.getConnections().getConnection().removeIf((c)->{
						return !(
							root.getHosts().getHost().stream().filter((h)->h.getName().equals(c.getSourceHost())).findFirst().isPresent()
							&&
							root.getHosts().getHost().stream().filter((h)->h.getName().equals(c.getDestHost())).findFirst().isPresent()
						);
					});						
				}
				return root;
	    	}else{
	    		throw new BadGraphError("No string to parse is provided",EType.INVALID_PARSING_STRING);
	    	}
	    }
}