/*******************************************************************************
 * Copyright (c) 2017 Politecnico di Torino and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package it.polito.verigraph.extra;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Model;
import com.microsoft.z3.Status;

import it.polito.verigraph.solver.NetContext;


/**
 * Data structure for the response to check requests for isolation properties
 *
 */
public class VerificationResult {
    Context ctx;
    public NetContext nctx;
    public Status result;
    public Model model;
    public BoolExpr [] assertions;

    /**
     * TODO complete comments
     * @param ctx
     * @param result
     * @param nctx
     * @param assertions
     * @param model
     */
    public VerificationResult(Context ctx,Status result, NetContext nctx, BoolExpr[] assertions, Model model){
        this.ctx = ctx;
        this.result = result;
        this.model = model;
        this.nctx = nctx;
        this.assertions = assertions;
    }
}