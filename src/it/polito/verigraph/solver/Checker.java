/*******************************************************************************
 * Copyright (c) 2017 Politecnico di Torino and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package it.polito.verigraph.solver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Model;
import com.microsoft.z3.Optimize;
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


	/**
	 * Public constructor of Checker class
	 * @param context it is the z3 context where assertions must be introduced into
	 * @param nctx it is the NetContext which stores basic z3 variables
	 * @param allocationNodes it is the map of allocation nodes of the Allocation Graph
	 */
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
		p.add("timeout", 3600000);
		solver.setParameters(p);
	}
	
	
	/**
	 * Thus method adds hard and soft constraints in the solver
	 */
	public void addConstraints() {
		allocationNodes.values().forEach(node->node.addConstraints(solver));
		constraintList.forEach(boolExpr->this.solver.Add(boolExpr));
		nctx.addConstraints(solver);
	}
	
	
	/**
	 * This method defines the z3 hard constraints for an isolation property (packet filter level)
	 * @param src it is the source node
	 * @param dest it is the destination node
	 * @param lv4proto it is the L4 protocol
	 * @param src_port it is the source protocol
	 * @param dst_port it is the destination protocol
	 */
	private void addIsolationProperty(AllocationNode src, AllocationNode dest, int lv4proto, String src_port, String dst_port) {
		Expr p0 = ctx.mkConst("check_isolation_p0_" + src.getZ3Name() + "_" + dest.getZ3Name()+"_"+lv4proto+"_"+src_port+"_"+dst_port, nctx.packetType);
		Expr p1 = ctx.mkConst("check_isolation_p1_" + src.getZ3Name() + "_" + dest.getZ3Name()+"_"+lv4proto+"_"+src_port+"_"+dst_port, nctx.packetType);
		
		Set<AllocationNode> srcNeighbours = src.getFirstHops().get(dest);
		Set<AllocationNode> destNeighbours = dest.getLastHops().get(src);
		
		
		/*
		 * Given the isolation property rule r,
		 * for each firstHop fp,
		 * exists p_1 : send(src, fp, p_1) && p_1.src = r.ip && p_1.dst = r.ip && 
		 * p_1.src_port = r.src_port && p_1.dst_port = r.dst_port && p_1.l4proto =r.l4proto
		 */
		List<Expr> sendNeighbours = srcNeighbours.stream().map(n ->  (BoolExpr) nctx.send.apply(src.getZ3Name(), n.getZ3Name(), p1)).distinct().collect(Collectors.toList());
		BoolExpr[] tmp2 = new BoolExpr[srcNeighbours.size()];
		BoolExpr enumerateSend = ctx.mkAnd(sendNeighbours.toArray(tmp2));
		constraintList.add(enumerateSend);
		constraintList.add((BoolExpr) nctx.nodeHasAddr.apply(dest.getZ3Name(), nctx.functionsMap.get("dest").apply(p1)));
		constraintList.add((BoolExpr) nctx.nodeHasAddr.apply(src.getZ3Name(), nctx.functionsMap.get("src").apply(p1)));
		constraintList.add(nctx.equalPortRangeToRange(nctx.functionsMap.get("src_port").apply(p1), nctx.portMap.get(src_port))
				); 
		constraintList.add(nctx.equalPortRangeToRange(nctx.functionsMap.get("dest_port").apply(p1), nctx.portMap.get(dst_port))
				); 
		constraintList.add(ctx.mkEq(nctx.functionsMap.get("lv4proto").apply(p1), ctx.mkInt(lv4proto))
				); 
		
		/*
		 * Given the isolation property rule r,
		 * for each lastHop lp,
		 * for each p_0,
		 * recv(lp, dst, p_0)  p_0.dst = dest.ip ---> not ( p_0.origin = r.src &&
		 * p_0.src_port = r.src_port && p_0.dst_port = r.dst_port && p_0.l4proto =r.l4proto)
		 */
		
		for(AllocationNode n : destNeighbours) {
			constraintList.add(ctx.mkForall(new Expr[]{p0},
					ctx.mkImplies(ctx.mkAnd((BoolExpr) nctx.recv.apply(n.getZ3Name(), dest.getZ3Name(), p0),
							(BoolExpr) nctx.nodeHasAddr.apply(dest.getZ3Name(), nctx.functionsMap.get("dest").apply(p0))
							),
							ctx.mkNot(ctx.mkAnd(ctx.mkEq(src.getZ3Name(), nctx.functionsMap.get("origin").apply(p0)),
									nctx.equalPortRangeToRange(nctx.functionsMap.get("src_port").apply(p0), nctx.portMap.get(src_port)),
									nctx.equalPortRangeToRange(nctx.functionsMap.get("dest_port").apply(p0), nctx.portMap.get(dst_port)),
									ctx.mkEq(nctx.functionsMap.get("lv4proto").apply(p0), ctx.mkInt(lv4proto))
									))),1,null,null,null,null));
		}
		
	
	}

	/**
	 * This method defines the z3 hard constraints for a reachability property (packet filter level)
	 * @param src it is the source node
	 * @param dest it is the destination node
	 * @param lv4proto it is the L4 protocol
	 * @param src_port it is the source protocol
	 * @param dst_port it is the destination protocol
	 */
	public void addReachabilityProperty(AllocationNode src, AllocationNode dest, int lv4proto, String src_port, String dst_port) {
		Expr p0 = ctx.mkConst("check_reach_p0_" + src.getZ3Name() + "_" + dest.getZ3Name()+"_"+lv4proto+"_"+src_port+"_"+dst_port, nctx.packetType);
		Expr p1 = ctx.mkConst("check_reach_p1_" + src.getZ3Name() + "_" + dest.getZ3Name()+"_"+lv4proto+"_"+src_port+"_"+dst_port, nctx.packetType);

		
		Map<AllocationNode, Set<AllocationNode>> firstHops = src.getFirstHops();
		Set<AllocationNode> set2 = firstHops.get(dest);

		/*
		 * Given the reachability property rule r,
		 * exists a firstHop fp, exists p_1 : 
		 * send(src, fp, p_1) && p_1.src = r.ip && p_1.dst = r.ip && 
		 * p_1.src_port = r.src_port && p_1.dst_port = r.dst_port && p_1.l4proto =r.l4proto
		 */
		List<Expr> sendNeighbours = set2.stream().map(n ->  (BoolExpr) nctx.send.apply(src.getZ3Name(), n.getZ3Name(), p1)).distinct().collect(Collectors.toList());
		BoolExpr[] tmp2 = new BoolExpr[set2.size()];
  	 	BoolExpr enumerateSend = ctx.mkOr(sendNeighbours.toArray(tmp2));
		constraintList.add(enumerateSend);
		constraintList.add((BoolExpr) nctx.nodeHasAddr.apply(src.getZ3Name(), nctx.functionsMap.get("src").apply(p1)));
		constraintList.add((BoolExpr) nctx.nodeHasAddr.apply(dest.getZ3Name(), nctx.functionsMap.get("dest").apply(p1)));
		constraintList.add(nctx.equalPortRangeToRange(nctx.functionsMap.get("src_port").apply(p1), nctx.portMap.get(src_port))
				);  	
		constraintList.add(nctx.equalPortRangeToRange(nctx.functionsMap.get("dest_port").apply(p1), nctx.portMap.get(dst_port))
				);  
		constraintList.add(ctx.mkEq(nctx.functionsMap.get("lv4proto").apply(p1), ctx.mkInt(lv4proto))
				);  
		
		/*
		 * Given the reachability property rule r,
		 * exists a lastHop lp, exists p_0:
		 * recv(lp, dst, p_0) && p_0.origin = r.src && p_0.dst = dest.ip &&
		 * p_0.src_port = r.src_port && p_0.dst_port = r.dst_port && p_0.l4proto =r.l4proto)
		 */
		Map<AllocationNode, Set<AllocationNode>> lastHops = dest.getLastHops();
		Set<AllocationNode> set = lastHops.get(src);
		
		List<Expr> recvNeighbours = set.stream().map(n -> (BoolExpr) nctx.recv.apply(n.getZ3Name(), dest.getZ3Name(), p0)).distinct().collect(Collectors.toList());
  		BoolExpr[] tmp = new BoolExpr[set.size()];
  	 	BoolExpr enumerateRecv = ctx.mkOr(recvNeighbours.toArray(tmp));
		constraintList.add(enumerateRecv);
		constraintList.add(ctx.mkEq(nctx.functionsMap.get("origin").apply(p0), src.getZ3Name())); 
		constraintList.add((BoolExpr) nctx.nodeHasAddr.apply(dest.getZ3Name(), nctx.functionsMap.get("dest").apply(p0)));
		constraintList.add(nctx.equalPortRangeToRange(nctx.functionsMap.get("src_port").apply(p0), nctx.portMap.get(src_port))
				);  
		constraintList.add(nctx.equalPortRangeToRange(nctx.functionsMap.get("dest_port").apply(p0), nctx.portMap.get(dst_port))
				); 
		constraintList.add(ctx.mkEq(nctx.functionsMap.get("lv4proto").apply(p0), ctx.mkInt(lv4proto))
				);  
	
	} 
	 

	
	/**
	 * This methods works as a wrapper, defining the correct values of the property elements before creating the constraints
	 * It invokes the correct method according to the property type (isolation, reachability)
	 * @param source it is the source node
	 * @param dest it is the destination node
	 * @param propertyType it is the type of property
	 * @param property it is the property
	 */
	public void propertyAdd(AllocationNode source, AllocationNode dest, Prop propertyType, Property property) {
		String src_port = (property == null || property.getSrcPort() == null) ? "null":property.getSrcPort();
		String dst_port = (property == null || property.getDstPort() == null) ? "null":property.getDstPort();
		int lv4proto = (property == null || property.getLv4Proto() == null) ? 0:property.getLv4Proto().ordinal();
		    
		switch (propertyType) {
		case ISOLATION:
			addIsolationProperty(source, dest, lv4proto, src_port, dst_port);
			break;
		case REACHABILITY:
			addReachabilityProperty(source, dest, lv4proto, src_port, dst_port);
			break;
		}

	}

	/**
	 * This method starts the z3 solver to solve the MaxSMT problem
	 * @return
	 */
	public VerificationResult propertyCheck(){
		solver.Push();
		addConstraints();
		result = this.solver.Check(); 
		model = (result == Status.SATISFIABLE) ? this.solver.getModel() : null;
		logAssertions();
		solver.Pop();
		return new VerificationResult(ctx, result, nctx, assertions, model);
	}
	
	/**
	 * This method prints the assertions of the z3 model in the log.
	 * old versions of z3 did not provide solver.getAssertions() method
	 * so if you want to use, it has to be commented
	 */
	private void logAssertions()  {	
		/*
		Logger logger = LogManager.getLogger("assertions");
		StringWriter stringWriter = new StringWriter();
		assertions = solver.getAssertions();
		Arrays.asList(assertions).forEach(t-> stringWriter.append(t+"\n\n"));
		if(model!=null){
			logger.debug("---------- Assertions: "+assertions.length);	
		}
		*/
	}

}


