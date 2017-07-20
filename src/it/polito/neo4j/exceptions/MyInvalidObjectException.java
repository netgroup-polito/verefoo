package it.polito.neo4j.exceptions;

public class MyInvalidObjectException extends Exception {

    private static final long serialVersionUID = 1L;

    public MyInvalidObjectException(String message){
        super(message);
    }
}
