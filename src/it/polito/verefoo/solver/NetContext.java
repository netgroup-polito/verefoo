/*******************************************************************************
 * Copyright (c) 2017 Politecnico di Torino and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package it.polito.verefoo.solver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Constructor;
import com.microsoft.z3.Context;
import com.microsoft.z3.DatatypeExpr;
import com.microsoft.z3.DatatypeSort;
import com.microsoft.z3.EnumSort;
import com.microsoft.z3.Expr;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.Optimize;
import com.microsoft.z3.Sort;

import it.polito.verefoo.allocation.AllocationNodeAP;
import it.polito.verefoo.allocation.AllocationNodeMF;
import it.polito.verefoo.extra.WildcardManager;
import it.polito.verefoo.graph.Predicate;
import it.polito.verefoo.utils.PortInterval;
import it.polito.verefoo.utils.Tuple;

/**
 * Basic fields and other things required for model checking.
 *
 */
abstract public class NetContext {
	public Context ctx;
	public WildcardManager wildcardManager;
	public FuncDecl nodeHasAddr,addrToNode,send,recv,deny;
	
    public List<BoolExpr> constraints;
    public List<Tuple<BoolExpr, String>> softConstrAutoConf;
    public List<Tuple<BoolExpr, String>> softConstrAutoPlace;
    public List<Tuple<BoolExpr, String>> softConstrMaintainStatePlacement;
    public List<Tuple<BoolExpr, String>> softConstrMaintainStateConfiguration;
    public List<Tuple<BoolExpr, String>> softConstrWildcard;
    public List<Tuple<BoolExpr, String>> softConstrProtoWildcard;
    public List<Tuple<BoolExpr, String>> softConstrPorts;

    public HashMap<String,DatatypeExpr> nodeMap; //list of nodes, callable by node name
    public HashMap<String,DatatypeExpr> addressMap; // list of addresses, callable by address name
    public HashMap<String,DatatypeExpr> portMap; // list of port range, callable by string

    
    public HashMap<String,FuncDecl> functionsMap;
    public HashMap<String,FuncDecl> portFunctionsMap;
    public HashMap<String,FuncDecl> ipFunctionsMap;    
    
    public EnumSort nodeType;
	public DatatypeSort addressType;
	public DatatypeSort portType;
    public DatatypeSort packetType;

    /*   Constants definition */
	public final int MAX_PORT = 65535;
    public final int HTTP_REQUEST    = 1;
    public final int HTTP_RESPONSE   = 2;
    public final int POP3_REQUEST    = 3;
    public final int POP3_RESPONSE   = 4;

    // weights
    public final int WPROTOWILDCARD   = 1000;
    public final int WIPWILDCARD   = -10;
    public final int WAUTOPLACEMENT   = 100000000;
    public final int WPORTS   = 1000;

 
    /**
     * Context for all of the rest that follows. Every network needs one of these
     * @param ctx
     * @param args
     */
    public NetContext(){
    }

    
    
    /*
     * Main Methods of NetContext class
     */
    
 
    /**
     * This method converts a z3 boolean into a z3 integer
     * @param value it is the z3 boolean
     * @return the converted z3 integer
     */
    public IntExpr bool_to_int(BoolExpr value) {
		IntExpr integer = ctx.mkIntConst("integer_" + value);
		constraints.add((ctx.mkImplies(value, ctx.mkEq(integer, ctx.mkInt(1)))));
		constraints.add((ctx.mkImplies(ctx.mkNot(value), ctx.mkEq(integer, ctx.mkInt(0)))));
		return integer;
	}

    
    
    /**
     * Setter of the wildcard manager
     * @param wildcardManager it is the wildcard manager to set
     */
    public void setWildcardManager(WildcardManager wildcardManager) {
    	this.wildcardManager = wildcardManager;
    }


}