package it.polito.escape.verify.validation;

import it.polito.escape.verify.model.Configuration;
import it.polito.escape.verify.model.Graph;
import it.polito.escape.verify.model.Node;
import it.polito.escape.verify.validation.exception.ValidationException;

public interface ValidationInterface {
	
	void validate(Graph graph, Node node, Configuration configuration) throws ValidationException;

}
