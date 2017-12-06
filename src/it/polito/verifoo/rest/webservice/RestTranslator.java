package it.polito.verifoo.rest.webservice;

import java.net.MalformedURLException;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import it.polito.verifoo.rest.common.Translator;
import it.polito.verifoo.rest.jaxb.NFV;


@Path("/converter")
public class RestTranslator {
	    @POST
	    @Consumes(MediaType.APPLICATION_XML)
		@Produces(MediaType.APPLICATION_XML)
	    public NFV put(NFV root) throws MalformedURLException {
	    	if(!root.getParsingString().isEmpty()){
	            new Translator(root.getParsingString(),root).convert();
	            root.setParsingString("");
				return root;
	    	}else{
	    		throw new ProcessingException("No string to parse is provided");
	    	}
	    }
}