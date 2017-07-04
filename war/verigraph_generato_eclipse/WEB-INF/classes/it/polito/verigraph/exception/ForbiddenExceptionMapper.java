package it.polito.verigraph.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import it.polito.verigraph.model.ErrorMessage;

@Provider
public class ForbiddenExceptionMapper implements ExceptionMapper<ForbiddenException> {

	@Override
	public Response toResponse(ForbiddenException exception) {
		ErrorMessage errorMessage = new ErrorMessage(	exception.getMessage(),
														403,
														"http://localhost:8080/verigraph/api-docs/");
		return Response.status(Status.FORBIDDEN).entity(errorMessage).build();
	}

}
