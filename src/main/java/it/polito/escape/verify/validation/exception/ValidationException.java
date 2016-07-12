package it.polito.escape.verify.validation.exception;


public class ValidationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8251271078519549101L;
	
	private String message;
	
	public ValidationException(String message){
//		super(message);
		this.message = message;
	}
	
	@Override
	public String getMessage() {
		return this.message;
	}
}
