package it.polito.neo4j.exceptions;

public class MyInvalidIdException extends Exception {

	private static final long serialVersionUID = 1L;

	public MyInvalidIdException(String message) 
	{
		super(message);
	}
}
