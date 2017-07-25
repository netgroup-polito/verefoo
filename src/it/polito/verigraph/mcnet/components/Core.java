/*******************************************************************************
 * Copyright (c) 2017 Politecnico di Torino and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package it.polito.verigraph.mcnet.components;

import com.microsoft.z3.Context;
import com.microsoft.z3.Solver;

/**Core component for everything that matters
 *
 *
 */
public abstract class Core{

    final int MAX_PORT = 512;

    /**
     * Base class for all objects in the modeling framework
     * @param ctx
     * @param args
     */
    public Core(Context ctx, Object[]... args){ // Object[]... -> The nearest way to implement variable length argument lists
        //in Java, in the most generic way.
        init(ctx,args);
    }
    /**
     * Override _init for any constructor initialization. Avoids having to explicitly call super.__init__ every Time.class
     * @param ctx
     * @param args
     */
    abstract protected void init(Context ctx,Object[]... args);

    /**
     * Add constraints to solver
     * @param solver
     */
    abstract protected void addConstraints(Solver solver);
}