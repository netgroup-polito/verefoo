package it.polito.verefoo.rest.war;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import it.polito.verefoo.jaxb.ApplicationError;
import it.polito.verefoo.jaxb.EType;
/**
 * This class wrap the output of an Error to the user
 * @see ProcessingException
 */
@Provider
public class ErrorMapper implements ExceptionMapper<Error> {

	@Override
	public Response toResponse(Error arg0) {
		ApplicationError e=new ApplicationError();
		e.setType(EType.INTERNAL_SERVER_ERROR);
		e.setMessage("Check the log on the server for more information");
		arg0.printStackTrace();
		return Response
                .status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(e)
                .type(MediaType.APPLICATION_XML)
                .build();
	}


}
