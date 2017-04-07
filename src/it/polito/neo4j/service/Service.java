package it.polito.neo4j.service;

import it.polito.neo4j.exceptions.MyInvalidDirectionException;
import it.polito.neo4j.exceptions.MyInvalidIdException;

public class Service {

	public long convertStringId(String id) throws MyInvalidIdException{
		if(id.matches("[0-9]+")){
			return Long.parseLong(id);
		}
		else
			throw new MyInvalidIdException("Invalid id");
	}
	
	public void checkValidDirection(String direction) throws MyInvalidDirectionException{
		if(direction.toUpperCase().compareTo("BOTH") != 0 && direction.toUpperCase().compareTo("OUTGOING") != 0)
			throw new MyInvalidDirectionException("Invalid direction");
	}
}
