package it.polito.verefoo.rest.war;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import it.polito.verefoo.jaxb.ApplicationError;
import it.polito.verefoo.jaxb.EType;
/**
 * This class wrap the output of a Generic Exception to the user
 */
@Provider
public class RestExceptionMapper implements ExceptionMapper<Exception>{
   	@Override
	public Response toResponse(Exception ex) {
		ApplicationError e=new ApplicationError();
		e.setType(EType.INTERNAL_SERVER_ERROR);
		e.setMessage("Check the log on the server for more information");
		ex.printStackTrace();
		return Response
                .status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(e)
                .type(MediaType.APPLICATION_XML)
                .build();
	}
}
