package it.polito.escape.verify.exception;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import it.polito.escape.verify.model.ErrorMessage;

@Provider
public class DataNotFoundExceptionMapper implements ExceptionMapper<DataNotFoundException> {

	@Override
	public Response toResponse(DataNotFoundException exception) {
		ErrorMessage errorMessage = new ErrorMessage(	exception.getMessage(),
														404,
														"http://localhost:8080/verify/api-docs/");
		return Response.status(Status.NOT_FOUND).entity(errorMessage).build();
	}

}
