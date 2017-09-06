/*******************************************************************************
 * Copyright (c) 2017 Politecnico di Torino and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package it.polito.verigraph.validation;

import com.fasterxml.jackson.databind.JsonNode;

import it.polito.verigraph.model.Configuration;
import it.polito.verigraph.model.Graph;
import it.polito.verigraph.model.Node;
import it.polito.verigraph.validation.exception.ValidationException;

public class DpiValidator implements ValidationInterface {

    public DpiValidator(){

    }

    private void validateKey(String key) throws ValidationException {
        if (!key.matches("\\w+"))
            throw new ValidationException("'" + key + "' is not a valid configuration string for a 'dpi'");
    }

    @Override
    public void validate(Graph graph, Node node, Configuration configuration) throws ValidationException {

        JsonNode conf = configuration.getConfiguration();

        if (!conf.isArray()) {
            throw new ValidationException("Configuration of a 'dpi' must be an array");
        }
        for (JsonNode key : conf) {
            if (!key.isTextual())
                throw new ValidationException("Configuration of a 'dpi' must be an array of strings");
            validateKey(key.asText());
        }
    }

}