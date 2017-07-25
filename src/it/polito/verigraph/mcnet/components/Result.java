/*******************************************************************************
 * Copyright (c) 2017 Politecnico di Torino and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package it.polito.verigraph.mcnet.components;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Model;

/**Data structure for the core of the response to a check request for data isolation property
 *
 */
public class Result {
    Context ctx;
    public Model model;
    public BoolExpr[] unsat_core;

    /**
     *
     * @param ctx
     * @param model
     */
    public Result(Context ctx, Model model){
        this.ctx = ctx;
        this.model = model;
    }

    /**
     *
     * @param ctx
     * @param unsat_core
     */
    public Result(Context ctx, BoolExpr[] unsat_core){
        this.ctx = ctx;
        this.unsat_core = unsat_core;
    }
}