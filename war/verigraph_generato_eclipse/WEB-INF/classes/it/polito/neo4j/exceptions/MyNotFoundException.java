package it.polito.neo4j.exceptions;

public class MyNotFoundException extends Exception
{
	private static final long serialVersionUID = -1337751234736465663L;

	public MyNotFoundException(String message) 
	{
		super(message);
	}
}
