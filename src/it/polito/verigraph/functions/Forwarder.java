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
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.DatatypeExpr;
import com.microsoft.z3.Expr;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.IntNum;
import com.microsoft.z3.Optimize;
import com.microsoft.z3.Sort;

import it.polito.verefoo.allocation.AllocationNode;
import it.polito.verefoo.extra.BadGraphError;
import it.polito.verefoo.jaxb.ActionTypes;
import it.polito.verefoo.jaxb.EType;
import it.polito.verefoo.jaxb.FunctionalTypes;
import it.polito.verefoo.jaxb.Node;
import it.polito.verigraph.extra.Quadruple;
import it.polito.verigraph.extra.Tuple;
import it.polito.verigraph.solver.NetContext;

/** Represents a Forwarder
 *
 */
public class Forwarder extends GenericFunction{
	DatatypeExpr forwarder;
	/**
	 * Public constructor for the AclFirewall
	 * @param source It is the Allocation Node on which the firewall is put
	 * @param ctx It is the Z3 Context in which the model is generated
	 * @param nctx It is the NetContext object to which contraints are sent
	 */
	public Forwarder(AllocationNode source, Context ctx, NetContext nctx) {
		forwarder = source.getZ3Name();
		this.source = source;
		this.ctx = ctx;
		this.nctx = nctx;
		constraints = new ArrayList<BoolExpr>();
		isEndHost = false;
	}

	
	// TODO comments about FOL formulas
    public void forwarderSendRules (){
    	Expr p_0 = ctx.mkConst(forwarder+"_forwarder_send_p_0", nctx.packetType);
    	// for each left hop
    	for(Map.Entry<AllocationNode, Set<AllocationNode>> entry : source.getLeftHops().entrySet()) {
    		BoolExpr enumerateSend = createAndSend(entry, p_0, forwarder);
    		BoolExpr recv= (BoolExpr) nctx.recv.apply(entry.getKey().getZ3Name(), forwarder, p_0);
  			constraints.add(ctx.mkForall(new Expr[] { p_0 },
							ctx.mkImplies((BoolExpr) recv,
							 enumerateSend), 
							1, null, null, null, null));
  		}
    	// for each right hop
    	for(Map.Entry<AllocationNode, Set<AllocationNode>> entry : source.getRightHops().entrySet()){
    		BoolExpr enumerateRecv = createOrRecv(entry, p_0, forwarder);
  			BoolExpr send = (BoolExpr) nctx.send.apply(forwarder, entry.getKey().getZ3Name(), p_0);
  	 		constraints.add(ctx.mkForall(new Expr[] {p_0 }, 
  	  					ctx.mkImplies((BoolExpr) send,
  	  									enumerateRecv
  	  										), 1, null, null, null, null)); 
  		}
    	
    }
    
 
	/**
	 * This method allows to wrap the methoch which adds the contraints inside Z3 solver
	 * @param solver Istance of Z3 solver
	 */
	@Override
	public void addContraints(Optimize solver) {
		BoolExpr[] constr = new BoolExpr[constraints.size()];
	    solver.Add(constraints.toArray(constr));
	}

  
}

