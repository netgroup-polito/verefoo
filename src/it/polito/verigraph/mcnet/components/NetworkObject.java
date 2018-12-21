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
    protected BoolExpr used;
    protected boolean autoplace;
	public BoolExpr enumerateRecvP0;
	public BoolExpr enumerateSendP0;
	public BoolExpr enumerateRecvP1;
	public BoolExpr enumerateSendP1;
	public ArrayList<NetworkObject> neighbours;
	protected Map<Expr, Set<Expr>> nodesFrom = new HashMap<>();
	protected Map<Expr, Set<Expr>> nodesTo = new HashMap<>();
	protected Map<Expr, Set<Expr>> lastHops = new HashMap<>();
	protected Map<Expr, Set<Expr>> firstHops = new HashMap<>();
	
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
    
    public void addNodesFrom(NetworkObject prev, NetworkObject next) {
    	
	    if(nodesFrom.containsKey(prev.getZ3Node())) {
			nodesFrom.get(prev.getZ3Node()).add(next.getZ3Node());
		} else {
			Set<Expr> set = new HashSet<>();
			set.add(next.getZ3Node());
			nodesFrom.put(prev.getZ3Node(), set);
		}
	}
    
    public void addNodesTo(NetworkObject prev, NetworkObject next) {
    	
	    if(nodesTo.containsKey(next.getZ3Node())) {
			nodesTo.get(next.getZ3Node()).add(prev.getZ3Node());
		} else {
			Set<Expr> set = new HashSet<>();
			set.add(prev.getZ3Node());
			nodesTo.put(next.getZ3Node(), set);
		}
	}
    
    public void addFirstHop(NetworkObject dest, NetworkObject hop) {
    	
	    if(firstHops.containsKey(dest.getZ3Node())) {
			firstHops.get(dest.getZ3Node()).add(hop.getZ3Node());
		} else {
			Set<Expr> set = new HashSet<>();
			set.add(hop.getZ3Node());
			firstHops.put(dest.getZ3Node(), set);
		}
	}
    
  public void addLastHop(NetworkObject origin, NetworkObject hop) {
    	
	    if(lastHops.containsKey(origin.getZ3Node())) {
			lastHops.get(origin.getZ3Node()).add(hop.getZ3Node());
		} else {
			Set<Expr> set = new HashSet<>();
			set.add(hop.getZ3Node());
			lastHops.put(origin.getZ3Node(), set);
		}
	}
    
    public Map<Expr, Set<Expr>>  getNodesFrom(){
    	return nodesFrom;
    }
    
    public Map<Expr, Set<Expr>>  getNodesTo(){
    	return nodesTo;
    }
    
    
    public Map<Expr, Set<Expr>>  getFirstHops(){
    	return firstHops;
    }
    
    public Map<Expr, Set<Expr>>  getLastHops(){
    	return lastHops;
    }
    
    
}