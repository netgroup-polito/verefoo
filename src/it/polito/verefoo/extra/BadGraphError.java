package it.polito.verefoo.extra;

import it.polito.verefoo.jaxb.EType;

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
	}

	public BadGraphError(String message, Throwable cause) {
		super(message, cause);
	}

	public BadGraphError(Throwable cause) {
		super(cause);
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

}
