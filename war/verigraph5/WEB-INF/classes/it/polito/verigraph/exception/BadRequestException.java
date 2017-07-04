package it.polito.verigraph.exception;

public class BadRequestException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -548472179073745084L;

	public BadRequestException(String message) {
		super(message);
	}

}
