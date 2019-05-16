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

	List<BoolExpr> constraints; 
	Context ctx;
	DatatypeExpr forwarder;
	NetContext nctx;
	private AllocationNode source;
	



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

	
	/**
	 * This method allows to create HARD constraints for a manually configured firewall, basing on its ACLs precedently computed
	 */
    public void forwarderSendRules (){
    	Expr p_0 = ctx.mkConst(forwarder+"_forwarder_send_p_0", nctx.packet);
    	Expr n_0 = ctx.mkConst(forwarder+"_firewall_send_n_0", nctx.node);
    	Expr n_1 = ctx.mkConst(forwarder+"_firewall_send_n_1", nctx.node);

    	
    	
    	/**
    	 * This section allows the creation of the following forwarding rules:
    	 * For each p_0, if recv(leftHop, fw, p_0) && acl_func.apply(p_0) -> for each rightHop, send(fw, rightHop, p_0)
    	 * In words:
    	 * For each packet, if the firewall has received this packet from a leftHop and its ACLs don't block the packet itself,
    	 * then the firewall must send the packet to ALL its nextHops towards its destination 
    	 */
    	
    	for(Map.Entry<AllocationNode, Set<AllocationNode>> entry : source.getLeftHops().entrySet()) {
  			
  			AllocationNode an = entry.getKey();
  			Expr e = an.getZ3Name();
  			BoolExpr recv= (BoolExpr) nctx.recv.apply(e, forwarder, p_0);
  			List<Expr> list = entry.getValue().stream().map(n -> n.getZ3Name()).collect(Collectors.toList());
  			List<Expr> sendNeighbours = list.stream().map(n -> (BoolExpr) nctx.send.apply(forwarder, n, p_0)).distinct().collect(Collectors.toList());
  			BoolExpr[] tmp3 = new BoolExpr[list.size()];
  			BoolExpr enumerateSend = ctx.mkAnd(sendNeighbours.toArray(tmp3));

  			constraints.add(ctx.mkForall(new Expr[] { p_0 },
							ctx.mkImplies((BoolExpr) recv,
							 enumerateSend), 
							1, null, null, null, null));
  			

  		}
    	
    	/**
    	 * This section allows the creation of the following forwarding rules:
    	 * For each p_0, for each rightHop, send(fw, rightHop, p_0) -> exist at least one leftHop that recv(leftHop, fw, p_0) && acl_func.apply(p_0) -> 
    	 * In words:
    	 * For each packet, if the firewall has sent this packet to a nextHop, that it means that:
    	 * 1) this packet has been received by at least one leftHop
    	 * 2) this packet isn't blocked by ACLs
    	 */
    	
    	
    	for(Map.Entry<AllocationNode, Set<AllocationNode>> entry : source.getRightHops().entrySet()){
  			AllocationNode an = entry.getKey();
  			Expr e = an.getZ3Name();
  			BoolExpr send = (BoolExpr) nctx.send.apply(forwarder, e, p_0);

  			List<Expr> list = entry.getValue().stream().map(n -> n.getZ3Name()).collect(Collectors.toList());
  			List<Expr> recvNeighbours = list.stream().map(n -> (BoolExpr) nctx.recv.apply(n, forwarder, p_0)).distinct().collect(Collectors.toList());
  			BoolExpr[] tmp2 = new BoolExpr[list.size()];
  	 		BoolExpr enumerateRecv = ctx.mkOr(recvNeighbours.toArray(tmp2));
  	 		
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

