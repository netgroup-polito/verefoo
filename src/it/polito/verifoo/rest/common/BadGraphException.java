package it.polito.verifoo.rest.common;
/**
 * 
 * This class represents the main exception that will be thrown by the VerifooProxy Class
 *
 */
public class BadGraphException extends Exception {

	public BadGraphException() {
		super();
	}

	public BadGraphException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	public BadGraphException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public BadGraphException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public BadGraphException(String message) {
		super(message,null);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 7802813559016997900L;

}
