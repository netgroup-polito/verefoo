package it.polito.neo4j.exceptions;

public class MyInvalidDirectionException extends Exception {

    private static final long serialVersionUID = 1L;

    public MyInvalidDirectionException(String message) 
    {
        super(message);
    }
}
