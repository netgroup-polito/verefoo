package it.polito.verefoo.rest.war;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
/**
 * This class pass the output of a Web Application Exception to the user (in other case the exception will be catched from RestExceptionMapper).
 */
@Provider
public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException>{

	@Override
	public Response toResponse(WebApplicationException arg0) {
		return arg0.getResponse();
	}

}
