package it.polito.verefoo.rest.war;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import it.polito.verefoo.extra.BadGraphError;
import it.polito.verefoo.jaxb.ApplicationError;
/**
 * This class wrap the output of a BadGraphError to the user
 * @see ProcessingException
 */
@Provider
public class BadGraphErrorMapper implements ExceptionMapper<BadGraphError> {

	@Override
	public Response toResponse(BadGraphError arg0) {
		ApplicationError e=new ApplicationError();
		e.setType(arg0.getE());
		e.setMessage(arg0.getMessage());
		return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(e)
                .type(MediaType.APPLICATION_XML)
                .build();
	}


}
