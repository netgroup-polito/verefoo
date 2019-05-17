/*******************************************************************************
 * Copyright (c) 2017 Politecnico di Torino and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package it.polito.verigraph.solver;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.Model;
import com.microsoft.z3.Optimize;
import com.microsoft.z3.Optimize.Handle;
import com.microsoft.z3.Params;
import com.microsoft.z3.Status;

import it.polito.verefoo.allocation.AllocationNode;
import it.polito.verefoo.jaxb.Property;
import it.polito.verigraph.extra.VerificationResult;


/**
 * Various checks for specific properties in the network.
 *
 *
 */
	
public class Checker {
	public enum Prop {
	    ISOLATION,REACHABILITY
	}
	
	Context ctx;
	NetContext nctx;
	Optimize solver;
	public BoolExpr[] assertions={};
	public Status result;
	public Model model;
	private HashMap<String, AllocationNode> allocationNodes;
	private List<BoolExpr> constraintList;

	
	public Checker(Context context, NetContext nctx, HashMap<String,AllocationNode> allocationNodes) {
		this.ctx = context;
		this.nctx = nctx;
		this.allocationNodes = allocationNodes;
		this.solver = ctx.mkOptimize();
		this.constraintList =new ArrayList<BoolExpr>();
		
		// initial parameters
		Params p = ctx.mkParams();
		p.add("maxsat_engine", ctx.mkSymbol("wmax"));
		p.add("maxres.wmax", true  );
		solver.setParameters(p);
	}
	
	public void addConstraints() {
		allocationNodes.values().forEach(node->node.addConstraints(solver));
		constraintList.forEach(boolExpr->this.solver.Add(boolExpr));
		nctx.addConstraints(solver);
	}
	//#TODO comments in code, organize code
	private void addIsolationProperty(AllocationNode src, AllocationNode dest, int lv4proto, String src_port, String dst_port) {
		Expr p0 = ctx.mkConst("check_isolation_p0_" + src.getZ3Name() + "_" + dest.getZ3Name()+"_"+lv4proto+"_"+src_port+"_"+dst_port, nctx.packetType);
		Expr p1 = ctx.mkConst("check_isolation_p1_" + src.getZ3Name() + "_" + dest.getZ3Name()+"_"+lv4proto+"_"+src_port+"_"+dst_port, nctx.packetType);
		
		Set<AllocationNode> srcNeighbours = src.getFirstHops().get(dest);
		Set<AllocationNode> destNeighbours = dest.getLastHops().get(src);
		
		for(AllocationNode n : destNeighbours) {
			constraintList.add(ctx.mkForall(new Expr[]{p0},
					ctx.mkImplies(ctx.mkAnd((BoolExpr) nctx.recv.apply(n.getZ3Name(), dest.getZ3Name(), p0),
							(BoolExpr) nctx.nodeHasAddr.apply(dest.getZ3Name(), nctx.functionsMap.get("dest").apply(p0))
							),
							ctx.mkNot(ctx.mkAnd(ctx.mkEq(src.getZ3Name(), nctx.functionsMap.get("origin").apply(p0)), ctx.mkEq(nctx.functionsMap.get("src_port").apply(p0), nctx.portMap.get(src_port)),
									ctx.mkEq(nctx.functionsMap.get("dest_port").apply(p0), nctx.portMap.get(dst_port)), ctx.mkEq(nctx.functionsMap.get("lv4proto").apply(p0), ctx.mkInt(lv4proto))))),1,null,null,null,null));
		}
		
		
		List<Expr> sendNeighbours = srcNeighbours.stream().map(n ->  (BoolExpr) nctx.send.apply(src.getZ3Name(), n.getZ3Name(), p1)).distinct().collect(Collectors.toList());
		BoolExpr[] tmp2 = new BoolExpr[srcNeighbours.size()];

		BoolExpr enumerateSend = ctx.mkAnd(sendNeighbours.toArray(tmp2));
		constraintList.add(enumerateSend);
		constraintList.add((BoolExpr) nctx.nodeHasAddr.apply(dest.getZ3Name(), nctx.functionsMap.get("dest").apply(p1)));
		constraintList.add((BoolExpr) nctx.nodeHasAddr.apply(src.getZ3Name(), nctx.functionsMap.get("src").apply(p1)));
	}
	
	//#TODO comments in code, organize code
	public void addReachabilityProperty(AllocationNode src, AllocationNode dest, int lv4proto, String src_port, String dst_port) {
		Expr p0 = ctx.mkConst("check_reach_p0_" + src.getZ3Name() + "_" + dest.getZ3Name()+"_"+lv4proto+"_"+src_port+"_"+dst_port, nctx.packetType);
		Expr p1 = ctx.mkConst("check_reach_p1_" + src.getZ3Name() + "_" + dest.getZ3Name()+"_"+lv4proto+"_"+src_port+"_"+dst_port, nctx.packetType);
		// Constraint1recv(n_0,destNode,p0,t_0)
		Map<AllocationNode, Set<AllocationNode>> lastHops = dest.getLastHops();
		Set<AllocationNode> set = lastHops.get(src);
		
		List<Expr> recvNeighbours = set.stream().map(n -> (BoolExpr) nctx.recv.apply(n.getZ3Name(), dest.getZ3Name(), p0)).distinct().collect(Collectors.toList());
  		BoolExpr[] tmp = new BoolExpr[set.size()];
  	 	BoolExpr enumerateRecv = ctx.mkOr(recvNeighbours.toArray(tmp));
		constraintList.add(enumerateRecv);
		// Constraint send(srcNode,n_1,p0,t_0)
		Map<AllocationNode, Set<AllocationNode>> firstHops = src.getFirstHops();
		Set<AllocationNode> set2 = firstHops.get(dest);

		List<Expr> sendNeighbours = set2.stream().map(n ->  (BoolExpr) nctx.send.apply(src.getZ3Name(), n.getZ3Name(), p1)).distinct().collect(Collectors.toList());
		BoolExpr[] tmp2 = new BoolExpr[set2.size()];
  	 	BoolExpr enumerateSend = ctx.mkOr(sendNeighbours.toArray(tmp2));
		constraintList.add(enumerateSend);

		constraintList.add(ctx.mkEq(nctx.functionsMap.get("origin").apply(p0), src.getZ3Name()));
		constraintList.add((BoolExpr) nctx.nodeHasAddr.apply(src.getZ3Name(), nctx.functionsMap.get("src").apply(p1)));
		constraintList.add((BoolExpr) nctx.nodeHasAddr.apply(dest.getZ3Name(), nctx.functionsMap.get("dest").apply(p1)));
		constraintList.add((BoolExpr) nctx.nodeHasAddr.apply(dest.getZ3Name(), nctx.functionsMap.get("dest").apply(p0)));
	
		constraintList.add(ctx.mkForall(new Expr[]{p0},
				ctx.mkImplies(ctx.mkAnd(enumerateRecv),
						ctx.mkAnd(ctx.mkEq(nctx.functionsMap.get("lv4proto").apply(p0), (IntExpr)ctx.mkInt(lv4proto)),
								ctx.mkEq(nctx.functionsMap.get("src_port").apply(p0), nctx.portMap.get(src_port)),
									ctx.mkEq(nctx.functionsMap.get("dest_port").apply(p0), nctx.portMap.get(dst_port))
								)),1,null,null,null,null));
	
	}
	

	public void propertyAdd(AllocationNode source, AllocationNode dest, Prop property, Property otherConstr) {
		String src_port = (otherConstr == null || otherConstr.getSrcPort() == null) ? "null":otherConstr.getSrcPort();
		String dst_port = (otherConstr == null || otherConstr.getDstPort() == null) ? "null":otherConstr.getDstPort();
		int lv4proto = (otherConstr == null || otherConstr.getLv4Proto() == null) ? 0:otherConstr.getLv4Proto().ordinal();
		Expr p_0 = ctx.mkConst("check_prop_p0_" + source.getZ3Name() + "_" + dest.getZ3Name()+"_"+lv4proto+"_"+src_port+"_"+dst_port, nctx.packetType);
		
		switch (property) {
		case ISOLATION:
			addIsolationProperty(source, dest, lv4proto, src_port, dst_port);
			break;
		case REACHABILITY:
			addReachabilityProperty(source, dest, lv4proto, src_port, dst_port);
			break;
		}
	
		// #TODO check if OR or AND 
		List<Expr> sendNeighbours = source.getNode().getNeighbour().stream().map(n -> allocationNodes.get(n.getName())).map(n -> nctx.send.apply(source.getZ3Name(), n.getZ3Name(), p_0)).collect(Collectors.toList());
		BoolExpr[] tmp3 = new BoolExpr[sendNeighbours.size()];
		BoolExpr enumerateSendP0 = ctx.mkOr(sendNeighbours.toArray(tmp3));

		//#TODO formula explanation
		constraintList.add(ctx.mkForall(new Expr[]{p_0},
				ctx.mkImplies(ctx.mkAnd(enumerateSendP0),
						ctx.mkAnd(ctx.mkEq(nctx.functionsMap.get("lv4proto").apply(p_0), (IntExpr)ctx.mkInt(lv4proto)),
								ctx.mkEq(nctx.functionsMap.get("src_port").apply(p_0), nctx.portMap.get(src_port)),
									ctx.mkEq(nctx.functionsMap.get("dest_port").apply(p_0), nctx.portMap.get(dst_port))
								)),1,null,null,null,null));
		
	}

	public VerificationResult propertyCheck(){
		solver.Push();
		addConstraints();
		result = this.solver.Check(); 
		model = (result == Status.SATISFIABLE) ? this.solver.getModel() : null;
		logAssertions();
		solver.Pop();
		return new VerificationResult(ctx, result, nctx, assertions, model);
	}
	
	private void logAssertions()  {	
		// old versions of z3 did not provide solver.getAssertions() method
		// so if this is the case it has to be commented
		/*
		 * 
		Logger logger = LogManager.getLogger("assertions");
		StringWriter stringWriter = new StringWriter();
		assertions = solver.getAssertions();
		Arrays.asList(assertions).forEach(t-> stringWriter.append(t+"\n\n"));
		if(model!=null){
			logger.debug("---------- Assertions: "+assertions.length);	
		}*/
}

}


