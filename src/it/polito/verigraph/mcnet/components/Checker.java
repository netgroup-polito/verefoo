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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.Model;
import com.microsoft.z3.Optimize;
import com.microsoft.z3.Optimize.Handle;
import com.microsoft.z3.Params;
import com.microsoft.z3.Status;

import it.polito.verifoo.rest.common.AllocationNode;
import it.polito.verifoo.rest.common.NodeNetworkObject;
import it.polito.verifoo.rest.common.NFAllocationManager;
import it.polito.verifoo.rest.jaxb.Property;
import it.polito.verigraph.mcnet.components.IsolationResult;
import it.polito.verigraph.mcnet.components.NetContext;
import it.polito.verigraph.mcnet.components.NetworkObject;
import it.polito.verigraph.mcnet.netobjs.PolitoEndHost;


/**
 * Various checks for specific properties in the network.
 *
 *
 */
public class Checker {
	Context ctx;
	NetContext nctx;
	NodeNetworkObject netobjs;
	Optimize solver;
	ArrayList<BoolExpr> constraints;
	public BoolExpr[] assertions;
	public Status result;
	public Model model;
	private HashMap<String, AllocationNode> allocationNodes;
	

	
	public enum Prop {
	    ISOLATION,REACHABILITY
	}
	

	
	public Checker(Context context, NetContext nctx, HashMap<String,AllocationNode> allocationNodes, NFAllocationManager allocationManager) {
		this.ctx = context;
		
		this.nctx = nctx;
		this.allocationNodes = allocationNodes;
		//this.allocationManager = allocationManager;
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
	public IsolationResult checkIsolationProperty(AllocationNode src, AllocationNode dest) {

		solver.Push();
		addConstraints();

		Expr p0 = ctx.mkConst("check_isolation_p0_" + src.getZ3Name() + "_" + dest.getZ3Name(), nctx.packet);
		Expr p1 = ctx.mkConst("check_isolation_p1_" + src.getZ3Name() + "_" + dest.getZ3Name(), nctx.packet);
		Expr n_0 = ctx.mkConst("check_isolation_n_0_" + src.getZ3Name() + "_" + dest.getZ3Name(), nctx.node);
		Expr n_1 = ctx.mkConst("check_isolation_n_1_" + src.getZ3Name() + "_" + dest.getZ3Name(), nctx.node);
		IntExpr t_0 = ctx.mkIntConst("check_isolation_t0_" + src.getZ3Name() + "_" + dest.getZ3Name());
		IntExpr t_1 = ctx.mkIntConst("check_isolation_t1_" + src.getZ3Name() + "_" + dest.getZ3Name());

		// Constraint1recv(n_0,destNode,p0,t_0)
		this.solver.Add((BoolExpr) nctx.recv.apply(n_0, dest.getZ3Name(), p0));

		// Constraint2send(srcNode,n_1,p1,t_1)
		this.solver.Add((BoolExpr) nctx.send.apply(src.getZ3Name(), n_1, p1));

		// Constraint3nodeHasAddr(srcNode,p1.srcAddr)
		this.solver.Add((BoolExpr) nctx.nodeHasAddr.apply(src.getZ3Name(), nctx.pf.get("src").apply(p1)));

		// Constraint4p1.origin == srcNode
		this.solver.Add(ctx.mkEq(nctx.pf.get("origin").apply(p1), src.getZ3Name()));

		// Constraint5nodeHasAddr(destNode,p1.destAddr)
		this.solver.Add((BoolExpr) nctx.nodeHasAddr.apply(dest.getZ3Name(), nctx.pf.get("dest").apply(p1)));

		// NON sembrano necessari
		// this.solver.add(z3.Or(this.ctx.nodeHasAddr(src.getZ3Name(),
		// this.ctx.packet.src(p0)),\
		// this.ctx.nodeHasAddr(n_0, this.ctx.packet.src(p0)),\
		// this.ctx.nodeHasAddr(n_1, this.ctx.packet.src(p0))))
		// this.solver.add(this.ctx.packet.dest(p1) == this.ctx.packet.dest(p0))

		// Constraint6p1.origin == p0.origin
		this.solver.Add(ctx.mkEq(nctx.pf.get("origin").apply(p1), nctx.pf.get("origin").apply(p0)));

		// Constraint7nodeHasAddr(destNode, p0.destAddr)
		this.solver.Add((BoolExpr) nctx.nodeHasAddr.apply(dest.getZ3Name(), nctx.pf.get("dest").apply(p0)));

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
	
	public IsolationResult checkRealIsolationProperty(AllocationNode src, AllocationNode dest) {
		//solver.Push();
		addConstraints();

		Expr p0 = ctx.mkConst("check_isolation_p0_" + src.getZ3Name() + "_" + dest.getZ3Name(), nctx.packet);
		Expr p1 = ctx.mkConst("check_isolation_p1_" + src.getZ3Name() + "_" + dest.getZ3Name(), nctx.packet);
		Expr n_0 = ctx.mkConst("check_isolation_n_0_" + src.getZ3Name() + "_" + dest.getZ3Name(), nctx.node);
		Expr n_1 = ctx.mkConst("check_isolation_n_1_" + src.getZ3Name() + "_" + dest.getZ3Name(), nctx.node);
		IntExpr t_0 = ctx.mkIntConst("check_isolation_t0_" + src.getZ3Name() + "_" + dest.getZ3Name());
		IntExpr t_1 = ctx.mkIntConst("check_isolation_t1_" + src.getZ3Name() + "_" + dest.getZ3Name());

	
		this.solver.Add(ctx.mkForall(new Expr[]{n_0, p0},
				ctx.mkImplies(ctx.mkAnd((BoolExpr) nctx.recv.apply(n_0, dest.getZ3Name(), p0)),
						ctx.mkAnd(ctx.mkNot(ctx.mkEq(src.getZ3Name(), nctx.pf.get("origin").apply(p0))))),1,null,null,null,null));
								
		// !(p1.origin == p0.origin)
		//this.solver.Add(ctx.mkAnd(ctx.mkNot(ctx.mkEq(nctx.pf.get("origin").apply(p1), nctx.pf.get("origin").apply(p0)))));
		// Constraint2 send(srcNode,n_1,p1,t_1)
		this.solver.Add((BoolExpr) nctx.send.apply(src.getZ3Name(), n_1, p1));

		// Constraint3 nodeHasAddr(srcNode,p1.srcAddr)
		//this.solver.Add((BoolExpr) nctx.nodeHasAddr.apply(src.getZ3Name(), nctx.pf.get("src").apply(p1)));

		// Constraint4 p1.origin == srcNode
		//this.solver.Add(ctx.mkEq(nctx.pf.get("origin").apply(p1), src.getZ3Name()));

		// Constraint5 nodeHasAddr(destNode,p1.destAddr)
		this.solver.Add((BoolExpr) nctx.nodeHasAddr.apply(dest.getZ3Name(), nctx.pf.get("dest").apply(p1)));

		// Constraint7nodeHasAddr(destNode, p0.destAddr)
		//this.solver.Add(((BoolExpr) nctx.nodeHasAddr.apply(dest.getZ3Name(), nctx.pf.get("dest").apply(p0))));

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
	private void addIsolationProperty(AllocationNode src, AllocationNode dest, int lv4proto, String src_port, String dst_port) {

		Expr p0 = ctx.mkConst("check_isolation_p0_" + src.getZ3Name() + "_" + dest.getZ3Name()+"_"+lv4proto+"_"+src_port+"_"+dst_port, nctx.packet);
		Expr p1 = ctx.mkConst("check_isolation_p1_" + src.getZ3Name() + "_" + dest.getZ3Name()+"_"+lv4proto+"_"+src_port+"_"+dst_port, nctx.packet);
		
		List<AllocationNode> srcNeighbours = src.getNode().getNeighbour().stream().map(n -> allocationNodes.get(n.getName())).distinct().collect(Collectors.toList());
		List<AllocationNode> destNeighbours = dest.getNode().getNeighbour().stream().map(n -> allocationNodes.get(n.getName())).distinct().collect(Collectors.toList());
		
		for(AllocationNode n : destNeighbours) {
			constraintList.add(ctx.mkForall(new Expr[]{p0},
					ctx.mkImplies(ctx.mkAnd((BoolExpr) nctx.recv.apply(n.getZ3Name(), dest.getZ3Name(), p0)),
							ctx.mkAnd(ctx.mkNot(ctx.mkEq(src.getZ3Name(), nctx.pf.get("origin").apply(p0))))),1,null,null,null,null));
		}
		
		
		for(AllocationNode n : srcNeighbours) {
			constraintList.add((BoolExpr) nctx.send.apply(src.getZ3Name(), n.getZ3Name(), p1));
		}
		
		constraintList.add((BoolExpr) nctx.nodeHasAddr.apply(dest.getZ3Name(), nctx.pf.get("dest").apply(p1)));
		constraintList.add((BoolExpr) nctx.nodeHasAddr.apply(src.getZ3Name(), nctx.pf.get("src").apply(p1)));
		/*constraintList.add(ctx.mkForall(new Expr[]{n_0, p0},
		ctx.mkImplies(ctx.mkAnd((BoolExpr) nctx.recv.apply(n_0, dest.getZ3Name(), p0)),
				ctx.mkAnd(ctx.mkNot(ctx.mkEq(src.getZ3Name(), nctx.pf.get("origin").apply(p0))))),1,null,null,null,null));
		constraintList.add((BoolExpr) nctx.send.apply(src.getZ3Name(), n_1, p1)); */

	}
	
	
	
	public void propertyAdd(AllocationNode src, AllocationNode dest, Prop property) {
		
		switch (property) {
			case ISOLATION: 
					addIsolationProperty(src, dest, 0, "0-"+nctx.MAX_PORT, "0-"+nctx.MAX_PORT);
					break;
			case REACHABILITY: 
					addReachabilityProperty(src, dest, 0, "0-"+nctx.MAX_PORT, "0-"+nctx.MAX_PORT);
					break;
		}
		
		
	}

	public void propertyAdd(AllocationNode src, AllocationNode dest, Prop property, int lv4proto, String src_port, String dst_port) {
		
		switch (property) {
			case ISOLATION: 
					addIsolationProperty(src, dest, lv4proto, src_port, dst_port);
					break;
			case REACHABILITY: 
					addReachabilityProperty(src, dest, lv4proto, src_port, dst_port);
					break;
		}
		
		
	}
	public void propertyAdd(AllocationNode source, AllocationNode dest, Prop property, Property otherConstr) {
		
		String src_port = (otherConstr == null || otherConstr.getSrcPort() == null) ? "null":otherConstr.getSrcPort(),
				dst_port = (otherConstr == null || otherConstr.getDstPort() == null) ? "null":otherConstr.getDstPort();
		int lv4proto = (otherConstr == null || otherConstr.getLv4Proto() == null) ? 0:otherConstr.getLv4Proto().ordinal();
		propertyAdd(source, dest, property, lv4proto, src_port, dst_port);
		Expr p_0 = ctx.mkConst("check_prop_p0_" + source.getZ3Name() + "_" + dest.getZ3Name()+"_"+lv4proto+"_"+src_port+"_"+dst_port, nctx.packet);
		List<Expr> sendNeighbours = source.getNode().getNeighbour().stream().map(n -> allocationNodes.get(n.getName())).map(n -> nctx.send.apply(source.getZ3Name(), n.getZ3Name(), p_0)).collect(Collectors.toList());
		BoolExpr[] tmp3 = new BoolExpr[sendNeighbours.size()];
		BoolExpr enumerateSendP0 = ctx.mkOr(sendNeighbours.toArray(tmp3));


		/*constraintList.add(ctx.mkForall(new Expr[]{p_0},
				ctx.mkImplies(ctx.mkAnd(enumerateSendP0),
						ctx.mkAnd(ctx.mkEq(nctx.pf.get("lv4proto").apply(p_0), (IntExpr)ctx.mkInt(lv4proto)),
									ctx.mkEq(nctx.pf.get("src_port").apply(p_0), nctx.pm.get(src_port)),
									ctx.mkEq(nctx.pf.get("dest_port").apply(p_0), nctx.pm.get(dst_port)))),1,null,null,null,null));*/
	}
	
	public void addReachabilityProperty(AllocationNode src, AllocationNode dest, int lv4proto, String src_port, String dst_port) {
		
		Expr p0 = ctx.mkConst("check_reach_p0_" + src.getZ3Name() + "_" + dest.getZ3Name()+"_"+lv4proto+"_"+src_port+"_"+dst_port, nctx.packet);
		Expr p1 = ctx.mkConst("check_reach_p1_" + src.getZ3Name() + "_" + dest.getZ3Name()+"_"+lv4proto+"_"+src_port+"_"+dst_port, nctx.packet);
		Expr n_0 = ctx.mkConst("check_reach_n_0_" + src.getZ3Name() + "_" + dest.getZ3Name()+"_"+lv4proto+"_"+src_port+"_"+dst_port, nctx.node);
		Expr n_1 = ctx.mkConst("check_reach_n_1_" + src.getZ3Name() + "_" + dest.getZ3Name()+"_"+lv4proto+"_"+src_port+"_"+dst_port, nctx.node);

		// Constraint1recv(n_0,destNode,p0,t_0)
		Map<AllocationNode, Set<AllocationNode>> lastHops = dest.getLastHops();
		Set<AllocationNode> set = lastHops.get(src);
		
		//List<Expr> recvNeighbours = set.stream().map(n -> (BoolExpr) nctx.recv.apply(n, dest.getZ3Name(), p0)).distinct().collect(Collectors.toList());
		List<Expr> recvNeighbours = set.stream().map(n -> (BoolExpr) nctx.recv.apply(n.getZ3Name(), dest.getZ3Name(), p0)).distinct().collect(Collectors.toList());
  		BoolExpr[] tmp = new BoolExpr[set.size()];
  	 	BoolExpr enumerateRecv = ctx.mkOr(recvNeighbours.toArray(tmp));
		constraintList.add(enumerateRecv);

		// Constraint send(srcNode,n_1,p0,t_0)
		Map<AllocationNode, Set<AllocationNode>> firstHops = src.getFirstHops();
		Set<AllocationNode> set2 = firstHops.get(dest);
		//List<Expr> sendNeighbours = set2.stream().map(n -> (BoolExpr) nctx.send.apply(src.getZ3Name(), n, p1)).distinct().collect(Collectors.toList());
		List<Expr> sendNeighbours = set2.stream().map(n ->  (BoolExpr) nctx.send.apply(src.getZ3Name(), n.getZ3Name(), p1)).distinct().collect(Collectors.toList());
		BoolExpr[] tmp2 = new BoolExpr[set2.size()];
  	 	BoolExpr enumerateSend = ctx.mkOr(sendNeighbours.toArray(tmp2));
		constraintList.add(enumerateSend);
		//constraintList.add((BoolExpr) nctx.send.apply(src.getZ3Name(), n_1, p1));
		// Constraint4p1.origin == srcNode
		constraintList.add(ctx.mkEq(nctx.pf.get("origin").apply(p0), src.getZ3Name()));
		//constraintList.add(ctx.mkEq(nctx.pf.get("src").apply(p0), src.getZ3Node()));
		//constraintList.add(ctx.mkEq(nctx.pf.get("src").apply(p1), src.getZ3Node()));
		//constraintList.add(ctx.mkEq(nctx.pf.get("dest").apply(p0), dest.getZ3Node()));
		//constraintList.add(ctx.mkEq(nctx.pf.get("dest").apply(p1), dest.getZ3Node()));
		constraintList.add((BoolExpr) nctx.nodeHasAddr.apply(src.getZ3Name(), nctx.pf.get("src").apply(p1)));

		// Constraint7nodeHasAddr(destNode, p0.destAddr)
		constraintList.add((BoolExpr) nctx.nodeHasAddr.apply(dest.getZ3Name(), nctx.pf.get("dest").apply(p1)));
		constraintList.add((BoolExpr) nctx.nodeHasAddr.apply(src.getZ3Name(), nctx.pf.get("src").apply(p1)));
		constraintList.add((BoolExpr) nctx.nodeHasAddr.apply(dest.getZ3Name(), nctx.pf.get("dest").apply(p0)));
		//constraintList.add((BoolExpr) nctx.nodeHasAddr.apply(src.getZ3Name(), nctx.pf.get("src").apply(p0)));
	}

	public IsolationResult propertyCheck(){
		solver.Push();
		addConstraints();
		Params p = ctx.mkParams();
		p.add("maxsat_engine", ctx.mkSymbol("wmax")  );
		p.add("maxres.wmax", true  );

		
		
		solver.setParameters(p);
		result = this.solver.Check();
		
		
		model = null;
		//assertions = solver.getAssertions();
		//Arrays.asList(assertions).forEach(t-> System.out.println(t+"\n\n"));
		//System.out.println(Arrays.toString(assertions));
		if (result == Status.SATISFIABLE) {
			model = this.solver.getModel();
		}
		solver.Pop();
		return new IsolationResult(ctx, result, null, null, null, null, nctx, assertions, model);
	}
	

	public void addConstraints() {
	
		for (AllocationNode an: allocationNodes.values())
			an.addConstraints(solver);
		for (BoolExpr boolExpr : constraintList) {
			this.solver.Add(boolExpr);
		}
		nctx.addConstraints(solver);
	}

	public List<BoolExpr> getConstraints() {
		Optimize l = ctx.mkOptimize();
		nctx.addConstraints(l);
		for (AllocationNode an: allocationNodes.values())
			an.addConstraints(solver);
		// return Arrays.asList(l.getAssertions());
		return Arrays.asList();
	}
}


