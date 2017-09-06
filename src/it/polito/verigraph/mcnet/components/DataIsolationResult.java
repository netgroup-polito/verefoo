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
import com.microsoft.z3.Expr;
import com.microsoft.z3.Model;
import com.microsoft.z3.Status;

import it.polito.verigraph.mcnet.components.NetContext;

/**Data structure for the response to a check request for data isolation property
 *
 */
public class DataIsolationResult {

    Context ctx;
    public NetContext nctx;
    public Status result;
    public Model model;
    public Expr violating_packet,last_hop,last_time,t_1;
    public BoolExpr [] assertions;

    public DataIsolationResult(Context ctx,Status result, Expr violating_packet, Expr last_hop, Expr last_time, NetContext nctx, BoolExpr[] assertions, Model model){
        this.ctx = ctx;
        this.result = result;
        this.violating_packet = violating_packet;
        this.last_hop = last_hop;
        this.model = model;
        this.last_time = last_time;
        this.assertions = assertions;
    }
}