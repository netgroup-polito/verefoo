package it.polito.verifoo.rest.common;

import it.polito.verifoo.rest.jaxb.EType;
/**
 * Exception thrown by the modules that implement a resource model.
 * @author Antonio
 *
 */
public class ResourceModelException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5755739983194819709L;

	public ResourceModelException() {
		super();
	}

	public ResourceModelException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	public ResourceModelException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public ResourceModelException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public ResourceModelException(String message) {
		super(message,null);
	}

}
