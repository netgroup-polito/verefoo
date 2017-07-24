package it.polito.verigraph.validation;

import it.polito.verigraph.model.Configuration;
import it.polito.verigraph.model.Graph;
import it.polito.verigraph.model.Node;
import it.polito.verigraph.validation.exception.ValidationException;

public interface ValidationInterface {

    void validate(Graph graph, Node node, Configuration configuration) throws ValidationException;

}