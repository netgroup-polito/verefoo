/*******************************************************************************
 * Copyright (c) 2017 Politecnico di Torino and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package it.polito.verigraph.client;

public class VerifyClientException extends Exception{

    private static final long serialVersionUID = 4794956747093682854L;

    public VerifyClientException(String message){
        super(message);
    }

}