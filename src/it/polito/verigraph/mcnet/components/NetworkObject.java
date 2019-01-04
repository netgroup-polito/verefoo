/*******************************************************************************
 * Copyright (c) 2017 Politecnico di Torino and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package it.polito.verigraph.mcnet.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.DatatypeExpr;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Optimize;

import it.polito.verigraph.mcnet.components.Core;

/** Represents a generic network object.
 *
 *
 */
abstract public class NetworkObject {

    public NetworkObject() {
    }

    protected DatatypeExpr z3Node;
    protected boolean isEndHost;
    protected BoolExpr used;
    protected boolean autoplace;
	public BoolExpr enumerateRecvP0;
	public BoolExpr enumerateSendP0;
	public BoolExpr enumerateRecvP1;
	public BoolExpr enumerateSendP1;
	public ArrayList<NetworkObject> neighbours;
	
    /**
     * Get a reference to the z3 node this class wraps around
     * @return
     */


    public String toString(){
        return z3Node.toString();
    }

    //There is probably an error: z3Node.hashCode = 0 because AST.hashCode() has always hash=0
    /*public int hashCode(){
        return z3Node.hashCode();
}*/
    public BoolExpr isUsed(){
    	return used;
    }

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
    public void setPolicy (Object policy) throws UnsupportedOperationException{
        throw new UnsupportedOperationException();
    }

	abstract public void addContraints(Optimize solver);
    
  
    
    
}