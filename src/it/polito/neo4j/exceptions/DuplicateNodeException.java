package it.polito.neo4j.exceptions;

public class DuplicateNodeException extends Exception {

	private static final long serialVersionUID = 1L;

	public DuplicateNodeException(String message){
		super(message);
	}
}
