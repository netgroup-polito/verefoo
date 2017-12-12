package it.polito.verifoo.rest.webservice;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
/**
 * This class wrap the output of a Processing Exception to the user
 * @see ProcessingException
 */
@Provider
public class ErrorMapper implements ExceptionMapper<Error> {

	@Override
	public Response toResponse(Error arg0) {
		return Response
                .status(Response.Status.SERVICE_UNAVAILABLE)
                .entity(arg0.getMessage())
                .type(MediaType.TEXT_PLAIN)
                .build();
	}


}
