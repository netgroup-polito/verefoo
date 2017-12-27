package it.polito.verifoo.rest.webservice;

import java.net.MalformedURLException;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import it.polito.verifoo.rest.common.BadGraphError;
import it.polito.verifoo.rest.common.Translator;
import it.polito.verifoo.rest.jaxb.EType;
import it.polito.verifoo.rest.jaxb.NFV;


@Path("/converter")
@Api("/converter")
public class RestTranslator {
	    @POST
	    @ApiOperation(value = "Converts the output model of Verifoo", notes = "This API takes an XML file containing the output model of Verifoo and it converts it into an XML with the correct deployment.",
	    	    response=NFV.class)
	    
	    	    @ApiResponses(value = {
	    	    	    		@ApiResponse(code = 200, message = "OK"),
	    	    	    		@ApiResponse(code = 400, message = "Something wrong in Client"),
	    	    	    		@ApiResponse(code = 500, message = "Something wrong in Server")})
	    
	    @Consumes(MediaType.APPLICATION_XML)
		@Produces(MediaType.APPLICATION_XML)
	    public NFV put(NFV root) throws MalformedURLException {
	    	if(!root.getParsingString().isEmpty()){
	            new Translator(root.getParsingString(),root).convert();
	            root.setParsingString("");
				return root;
	    	}else{
	    		throw new BadGraphError("No string to parse is provided",EType.INVALID_PARSING_STRING);
	    	}
	    }
}