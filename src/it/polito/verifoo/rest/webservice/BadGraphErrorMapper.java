package it.polito.verifoo.rest.webservice;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import it.polito.verifoo.rest.common.BadGraphError;
import it.polito.verifoo.rest.jaxb.ApplicationError;
import it.polito.verifoo.rest.jaxb.EType;
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
