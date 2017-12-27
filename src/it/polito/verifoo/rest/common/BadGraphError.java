package it.polito.verifoo.rest.common;

import it.polito.verifoo.rest.jaxb.EType;

/**
 * 
 * This class represents the main exception that will be thrown by the VerifooProxy Class
 *
 */
public class BadGraphError extends RuntimeException {
	private EType e;
	public BadGraphError() {
		super();
	}

	public BadGraphError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	public BadGraphError(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public BadGraphError(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public BadGraphError(String message) {
		super(message,null);
	}
	public BadGraphError(String message,EType e) {
		super(message,null);
		this.e=e;
	}

	
	public EType getE() {
		return e;
	}

	public void setE(EType e) {
		this.e = e;
	}


	/**
	 * 
	 */
	private static final long serialVersionUID = 7802813559016997900L;

}
