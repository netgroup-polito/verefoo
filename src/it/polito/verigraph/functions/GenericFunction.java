/*******************************************************************************
 * Copyright (c) 2017 Politecnico di Torino and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package it.polito.verigraph.functions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.DatatypeExpr;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Optimize;

import it.polito.verefoo.allocation.AllocationNode;
import it.polito.verigraph.solver.NetContext;

/** Represents a generic network object.
 *
 *
 */
abstract public class GenericFunction {

    public GenericFunction() {
    }
    
    protected List<BoolExpr> constraints; 
    protected AllocationNode source;
    protected Context ctx;
    protected NetContext nctx;
    protected DatatypeExpr z3Node;
    protected boolean isEndHost;
    protected BoolExpr used;
    protected boolean autoplace;
	public BoolExpr enumerateRecvP0;
	public BoolExpr enumerateSendP0;
	public BoolExpr enumerateRecvP1;
	public BoolExpr enumerateSendP1;
	public ArrayList<GenericFunction> neighbours;
	
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
    
	
    protected BoolExpr createOrRecv(Entry<AllocationNode, Set<AllocationNode>> entry, Expr p_0, DatatypeExpr function) {
			List<Expr> list = entry.getValue().stream().map(n -> n.getZ3Name()).collect(Collectors.toList());
			List<Expr> recvNeighbours = list.stream().map(n -> (BoolExpr) nctx.recv.apply(n, function, p_0)).distinct().collect(Collectors.toList());
			BoolExpr[] tmp = new BoolExpr[list.size()];
			// TODO comments about FOL formulas, i.e., what is enumerateRecv
			// enumerateRecv = OR (recv(n,function,p_0) where n=leftNeighbours)
	 		BoolExpr enumerateRecv = ctx.mkOr(recvNeighbours.toArray(tmp));
	 		return enumerateRecv;
	}
	
    protected BoolExpr createAndSend(Entry<AllocationNode, Set<AllocationNode>> entry, Expr p_0, DatatypeExpr function) {
			List<Expr> list = entry.getValue().stream().map(n -> n.getZ3Name()).collect(Collectors.toList());
			List<Expr> sendNeighbours = list.stream().map(n -> (BoolExpr) nctx.send.apply(function, n, p_0)).distinct().collect(Collectors.toList());
			BoolExpr[] tmp = new BoolExpr[list.size()];
			BoolExpr enumerateSend = ctx.mkAnd(sendNeighbours.toArray(tmp));
			return enumerateSend;
	}
    
    
}