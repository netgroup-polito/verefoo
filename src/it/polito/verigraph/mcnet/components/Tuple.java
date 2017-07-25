/*******************************************************************************
 * Copyright (c) 2017 Politecnico di Torino and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package it.polito.verigraph.mcnet.components;

/** A data structure which is an utility to make a generic couple of objects with different types in Java
 *
 */
public class Tuple<T, U> {
    public final T _1;
    public final U _2;


    public Tuple(T arg1,U arg2) {
        super();
        this._1 = arg1;
        this._2 = arg2;
    }

    public Tuple(){
        this._1 = null;
        this._2 = null;
    }

    @Override
    public String toString() {
        return String.format("(%s, %s)", _1, _2);
    }
}