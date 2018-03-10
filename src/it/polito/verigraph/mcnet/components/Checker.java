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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.Model;
import com.microsoft.z3.Optimize;
import com.microsoft.z3.Optimize.Handle;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;
import it.polito.verigraph.mcnet.components.IsolationResult;
import it.polito.verigraph.mcnet.components.NetContext;
import it.polito.verigraph.mcnet.components.Network;
import it.polito.verigraph.mcnet.components.NetworkObject;

/**
 * Various checks for specific properties in the network.
 *
 *
 */
public class Checker {

	Context ctx;
	Network net;
	NetContext nctx;
	Optimize solver;
	ArrayList<BoolExpr> constraints;
	public BoolExpr[] assertions;
	public Status result;
	public Model model;

	
	public enum Prop {
	    ISOLATION,REACHABILITY
	}
	public Checker(Context context, NetContext nctx, Network network) {
		this.ctx = context;
		this.net = network;
		this.nctx = nctx;
		this.solver = ctx.mkOptimize();
		this.constraints = new ArrayList<BoolExpr>();
		this.constraintList =new ArrayList<BoolExpr>();
	}

	/**
	 * Resets the constraints
	 *
	 */
	public void clearState() {
		// this.solver.reset();
		this.constraints = new ArrayList<BoolExpr>();
	}

	/**
	 * Checks whether the source provided can reach the destination
	 *
	 * @param src
	 * @param dest
	 * @return
	 */
	public IsolationResult checkIsolationProperty(NetworkObject src, NetworkObject dest) {
		assert (net.elements.contains(src));
		assert (net.elements.contains(dest));
		solver.Push();
		addConstraints();

		Expr p0 = ctx.mkConst("check_isolation_p0_" + src.getZ3Node() + "_" + dest.getZ3Node(), nctx.packet);
		Expr p1 = ctx.mkConst("check_isolation_p1_" + src.getZ3Node() + "_" + dest.getZ3Node(), nctx.packet);
		Expr n_0 = ctx.mkConst("check_isolation_n_0_" + src.getZ3Node() + "_" + dest.getZ3Node(), nctx.node);
		Expr n_1 = ctx.mkConst("check_isolation_n_1_" + src.getZ3Node() + "_" + dest.getZ3Node(), nctx.node);
		IntExpr t_0 = ctx.mkIntConst("check_isolation_t0_" + src.getZ3Node() + "_" + dest.getZ3Node());
		IntExpr t_1 = ctx.mkIntConst("check_isolation_t1_" + src.getZ3Node() + "_" + dest.getZ3Node());

		// Constraint1recv(n_0,destNode,p0,t_0)
		this.solver.Add((BoolExpr) nctx.recv.apply(n_0, dest.getZ3Node(), p0));

		// Constraint2send(srcNode,n_1,p1,t_1)
		this.solver.Add((BoolExpr) nctx.send.apply(src.getZ3Node(), n_1, p1));

		// Constraint3nodeHasAddr(srcNode,p1.srcAddr)
		this.solver.Add((BoolExpr) nctx.nodeHasAddr.apply(src.getZ3Node(), nctx.pf.get("src").apply(p1)));

		// Constraint4p1.origin == srcNode
		this.solver.Add(ctx.mkEq(nctx.pf.get("origin").apply(p1), src.getZ3Node()));

		// Constraint5nodeHasAddr(destNode,p1.destAddr)
		this.solver.Add((BoolExpr) nctx.nodeHasAddr.apply(dest.getZ3Node(), nctx.pf.get("dest").apply(p1)));

		// NON sembrano necessari
		// this.solver.add(z3.Or(this.ctx.nodeHasAddr(src.getZ3Node(),
		// this.ctx.packet.src(p0)),\
		// this.ctx.nodeHasAddr(n_0, this.ctx.packet.src(p0)),\
		// this.ctx.nodeHasAddr(n_1, this.ctx.packet.src(p0))))
		// this.solver.add(this.ctx.packet.dest(p1) == this.ctx.packet.dest(p0))

		// Constraint6p1.origin == p0.origin
		this.solver.Add(ctx.mkEq(nctx.pf.get("origin").apply(p1), nctx.pf.get("origin").apply(p0)));

		// Constraint7nodeHasAddr(destNode, p0.destAddr)
		this.solver.Add((BoolExpr) nctx.nodeHasAddr.apply(dest.getZ3Node(), nctx.pf.get("dest").apply(p0)));

		result = this.solver.Check();
		
		Handle temp = null;
		for (Entry<String, Handle> handle : nctx.handles.entrySet()) {
			temp = handle.getValue();
		}
		if(temp!=null) System.out.println(nctx.latencyAll-Integer.parseInt(temp.getValue()+""));
		if(temp!=null) System.out.println("Weight of falsified constraints:"+temp.getValue());
		model = null;
		// assertions = this.solver.getAssertions();
		// assertions = new BoolExpr [1] ;
		assertions = null;

		if (result == Status.SATISFIABLE) {
			model = this.solver.getModel();
		}
		this.solver.Pop();
		return new IsolationResult(ctx, result, p0, n_0, t_1, t_0, nctx, assertions, model);
	}
	
	public IsolationResult checkRealIsolationProperty(NetworkObject src, NetworkObject dest) {
		assert (net.elements.contains(src));
		assert (net.elements.contains(dest));
		//solver.Push();
		addConstraints();

		Expr p0 = ctx.mkConst("check_isolation_p0_" + src.getZ3Node() + "_" + dest.getZ3Node(), nctx.packet);
		Expr p1 = ctx.mkConst("check_isolation_p1_" + src.getZ3Node() + "_" + dest.getZ3Node(), nctx.packet);
		Expr n_0 = ctx.mkConst("check_isolation_n_0_" + src.getZ3Node() + "_" + dest.getZ3Node(), nctx.node);
		Expr n_1 = ctx.mkConst("check_isolation_n_1_" + src.getZ3Node() + "_" + dest.getZ3Node(), nctx.node);
		IntExpr t_0 = ctx.mkIntConst("check_isolation_t0_" + src.getZ3Node() + "_" + dest.getZ3Node());
		IntExpr t_1 = ctx.mkIntConst("check_isolation_t1_" + src.getZ3Node() + "_" + dest.getZ3Node());

	
		this.solver.Add(ctx.mkForall(new Expr[]{n_0, p0},
				ctx.mkImplies(ctx.mkAnd((BoolExpr) nctx.recv.apply(n_0, dest.getZ3Node(), p0)),
						ctx.mkAnd(ctx.mkNot(ctx.mkEq(src.getZ3Node(), nctx.pf.get("origin").apply(p0))))),1,null,null,null,null));
								
		// !(p1.origin == p0.origin)
		//this.solver.Add(ctx.mkAnd(ctx.mkNot(ctx.mkEq(nctx.pf.get("origin").apply(p1), nctx.pf.get("origin").apply(p0)))));
		// Constraint2 send(srcNode,n_1,p1,t_1)
		this.solver.Add((BoolExpr) nctx.send.apply(src.getZ3Node(), n_1, p1));

		// Constraint3 nodeHasAddr(srcNode,p1.srcAddr)
		//this.solver.Add((BoolExpr) nctx.nodeHasAddr.apply(src.getZ3Node(), nctx.pf.get("src").apply(p1)));

		// Constraint4 p1.origin == srcNode
		//this.solver.Add(ctx.mkEq(nctx.pf.get("origin").apply(p1), src.getZ3Node()));

		// Constraint5 nodeHasAddr(destNode,p1.destAddr)
		this.solver.Add((BoolExpr) nctx.nodeHasAddr.apply(dest.getZ3Node(), nctx.pf.get("dest").apply(p1)));

		// Constraint7nodeHasAddr(destNode, p0.destAddr)
		//this.solver.Add(((BoolExpr) nctx.nodeHasAddr.apply(dest.getZ3Node(), nctx.pf.get("dest").apply(p0))));

		result = this.solver.Check();
		Handle temp = null;
		for (Entry<String, Handle> handle : nctx.handles.entrySet()) {
			temp = handle.getValue();
		}
		if (temp!=null)System.out.println(temp.getValue());
		
		model = null;
		// assertions = this.solver.getAssertions();
		// assertions = new BoolExpr [1] ;
		assertions = null;
		if (result == Status.SATISFIABLE) {
			model = this.solver.getModel();
		}
		//this.solver.Pop();
		return new IsolationResult(ctx, result, p0, n_0, t_1, t_0, nctx, assertions, model);
	}
	private List<BoolExpr> constraintList;
	private void addIsolationProperty(NetworkObject src, NetworkObject dest) {

		Expr p0 = ctx.mkConst("check_isolation_p0_" + src.getZ3Node() + "_" + dest.getZ3Node(), nctx.packet);
		Expr p1 = ctx.mkConst("check_isolation_p1_" + src.getZ3Node() + "_" + dest.getZ3Node(), nctx.packet);
		Expr n_0 = ctx.mkConst("check_isolation_n_0_" + src.getZ3Node() + "_" + dest.getZ3Node(), nctx.node);
		Expr n_1 = ctx.mkConst("check_isolation_n_1_" + src.getZ3Node() + "_" + dest.getZ3Node(), nctx.node);
	
		
		
		constraintList.add(ctx.mkForall(new Expr[]{n_0, p0},
				ctx.mkImplies(ctx.mkAnd((BoolExpr) nctx.recv.apply(n_0, dest.getZ3Node(), p0)),
						ctx.mkAnd(ctx.mkNot(ctx.mkEq(src.getZ3Node(), nctx.pf.get("origin").apply(p0))))),1,null,null,null,null));
		constraintList.add((BoolExpr) nctx.send.apply(src.getZ3Node(), n_1, p1));
		constraintList.add((BoolExpr) nctx.nodeHasAddr.apply(dest.getZ3Node(), nctx.pf.get("dest").apply(p1)));

	}

	public void propertyAdd(NetworkObject src, NetworkObject dest, Prop property) {
		assert (net.elements.contains(src));
		assert (net.elements.contains(dest));
		
		switch (property) {
			case ISOLATION: 
					addIsolationProperty(src, dest);
					break;
			case REACHABILITY: 
					addReachabilityProperty(src, dest);
					break;
		}
		
		
	}
	private void addReachabilityProperty(NetworkObject src, NetworkObject dest) {
		
		Expr p0 = ctx.mkConst("check_reach_p0_" + src.getZ3Node() + "_" + dest.getZ3Node(), nctx.packet);
		Expr p1 = ctx.mkConst("check_reach_p1_" + src.getZ3Node() + "_" + dest.getZ3Node(), nctx.packet);
		Expr n_0 = ctx.mkConst("check_reach_n_0_" + src.getZ3Node() + "_" + dest.getZ3Node(), nctx.node);

		// Constraint1recv(n_0,destNode,p0,t_0)
		constraintList.add((BoolExpr) nctx.recv.apply(n_0, dest.getZ3Node(), p0));

		// Constraint4p1.origin == srcNode
		constraintList.add(ctx.mkEq(nctx.pf.get("origin").apply(p0), src.getZ3Node()));

		// Constraint7nodeHasAddr(destNode, p0.destAddr)
		constraintList.add((BoolExpr) nctx.nodeHasAddr.apply(dest.getZ3Node(), nctx.pf.get("dest").apply(p0)));
		
	}

	public IsolationResult propertyCheck(){
		solver.Push();
		addConstraints();
		
		result = this.solver.Check();
		
		Handle temp = null;
		for (Entry<String, Handle> handle : nctx.handles.entrySet()) {
			temp = handle.getValue();
		}
		if (temp!=null)System.out.println(temp.getValue());
		
		model = null;
		assertions = null;
		if (result == Status.SATISFIABLE) {
			model = this.solver.getModel();
		}
		solver.Pop();
		return new IsolationResult(ctx, result, null, null, null, null, nctx, assertions, model);
	}
	

	public void addConstraints() {
		net.addConstraints(solver);
		nctx.addConstraints(solver);
		for (NetworkObject el : net.elements)
			el.addConstraints(solver);
		for (BoolExpr boolExpr : constraintList) {
			this.solver.Add(boolExpr);
		}
	}

	public List<BoolExpr> getConstraints() {
		Optimize l = ctx.mkOptimize();
		nctx.addConstraints(l);
		net.addConstraints(l);
		for (NetworkObject el : net.elements)
			el.addConstraints(l);
		// return Arrays.asList(l.getAssertions());
		return Arrays.asList();
	}
}
