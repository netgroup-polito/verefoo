package it.polito.verifoo.rest.webservice;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
@Provider
public class ProcessingExceptionMapper implements ExceptionMapper<ProcessingException> {

	@Override
	public Response toResponse(ProcessingException arg0) {
		return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(arg0.getMessage())
                .type(MediaType.TEXT_PLAIN)
                .build();
	}


}
