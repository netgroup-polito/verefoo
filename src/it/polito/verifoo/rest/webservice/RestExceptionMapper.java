package it.polito.verifoo.rest.webservice;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
/**
 * This class wrap the output of a Generic Exception to the user
 */
public class RestExceptionMapper implements ExceptionMapper<Exception>{
   	@Override
	public Response toResponse(Exception ex) {
   		return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(ex.getMessage())
                .type(MediaType.TEXT_PLAIN)
                .build();
	}
}
