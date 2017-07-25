/*******************************************************************************
 * Copyright (c) 2017 Politecnico di Torino and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package it.polito.verigraph.mcnet.netobjs;

import com.microsoft.z3.Context;
import com.microsoft.z3.DatatypeExpr;
import com.microsoft.z3.Solver;
import it.polito.verigraph.mcnet.components.NetworkObject;

/**
 * This is just a wrapper around z3 instances. The idea is that by using this we perhaps need to have
 * fewer (or no) ifs to deal with the case where we don't instantiate an object for a node
 *
 */
public class DumbNode extends NetworkObject {
    public DumbNode(Context ctx, Object[]... args){
        super(ctx,args);
    }

    @Override
    protected void addConstraints(Solver solver) {
        return;
    }

    @Override
    protected void init(Context ctx, Object[]... args) {
        isEndHost=true;
        this.z3Node = (DatatypeExpr)args[0][0];
    }
    @Override
    public DatatypeExpr getZ3Node() {
        return z3Node;
    }
}