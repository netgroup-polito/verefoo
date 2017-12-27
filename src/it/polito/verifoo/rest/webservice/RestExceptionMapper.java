package it.polito.verifoo.rest.webservice;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import it.polito.verifoo.rest.jaxb.ApplicationError;
import it.polito.verifoo.rest.jaxb.EType;
/**
 * This class wrap the output of a Generic Exception to the user
 */
public class RestExceptionMapper implements ExceptionMapper<Exception>{
   	@Override
	public Response toResponse(Exception ex) {
		ApplicationError e=new ApplicationError();
		e.setType(EType.INTERNAL_SERVER_ERROR);
		e.setMessage("Check the log on the server for more information");
		return Response
                .status(Response.Status.SERVICE_UNAVAILABLE)
                .entity(ex.getMessage())
                .type(MediaType.APPLICATION_XML)
                .build();
	}
}
