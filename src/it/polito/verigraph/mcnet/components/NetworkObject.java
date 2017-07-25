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
import com.microsoft.z3.DatatypeExpr;

import it.polito.verigraph.mcnet.components.Core;

/** Represents a generic network object.
 *
 *
 */
public abstract class NetworkObject extends Core{

    public NetworkObject(Context ctx,Object[]... args) {
        super(ctx,args);
    }

    protected DatatypeExpr z3Node;
    protected boolean isEndHost;
    /**
     * Get a reference to the z3 node this class wraps around
     * @return
     */
    abstract public DatatypeExpr getZ3Node();

    public String toString(){
        return z3Node.toString();
    }

    //There is probably an error: z3Node.hashCode = 0 because AST.hashCode() has always hash=0
    /*public int hashCode(){
        return z3Node.hashCode();
}*/

    /**
     * A simple way to determine the set of endhosts
     * @return
     */
    public boolean isEndHost(){
        return isEndHost;
    }

    /**
     * Wrap methods to set policy
     * @param policy
     * @throws UnsupportedOperationException
     */
    void setPolicy (Object policy) throws UnsupportedOperationException{
        throw new UnsupportedOperationException();
    }
}