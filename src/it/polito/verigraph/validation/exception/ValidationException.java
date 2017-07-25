/*******************************************************************************
 * Copyright (c) 2017 Politecnico di Torino and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package it.polito.verigraph.validation.exception;

public class ValidationException extends Exception {

    /**
     *
     */
    private static final long serialVersionUID = -8251271078519549101L;

    private String message;

    public ValidationException(String message){
        //super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}