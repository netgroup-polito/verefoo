package it.polito.verifoo.rest.webservice;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import it.polito.verifoo.rest.jaxb.ApplicationError;
import it.polito.verifoo.rest.jaxb.EType;
/**
 * This class wrap the output of an InvalidXMLException to the user
 * @see ProcessingException
 */
@Provider
public class InvalidXMLExceptionMapper implements ExceptionMapper<InvalidXMLException> {

	@Override
	public Response toResponse(InvalidXMLException arg0) {
		ApplicationError e=new ApplicationError();
		e.setType(EType.XML_VALIDATION_ERROR);
		e.setMessage(arg0.getMessage());
		return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(e)
                .type(MediaType.APPLICATION_XML)
                .build();
	}


}
