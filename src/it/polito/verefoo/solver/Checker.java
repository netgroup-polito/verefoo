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
import java.util.Map;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Model;
import com.microsoft.z3.Optimize;
import com.microsoft.z3.Params;
import com.microsoft.z3.Status;

import it.polito.verefoo.allocation.AllocationNodeAP;
import it.polito.verefoo.allocation.AllocationNodeMF;
import it.polito.verefoo.graph.AtomicFlow;
import it.polito.verefoo.graph.FlowPathAP;
import it.polito.verefoo.graph.FlowPathMF;
import it.polito.verefoo.graph.MaximalFlow;
import it.polito.verefoo.graph.SecurityRequirement;
import it.polito.verefoo.utils.VerificationResult;


/**
 * Various checks for specific properties in the network.
 *
 *
 */
	
public class Checker {
	public enum Prop {
	    ISOLATION,REACHABILITY,COMPLETE_REACHABILITY
	}
	
	Context ctx;
	NetContextAP nctxAP;
	NetContextMF nctxMF;
	Optimize solver;
	public BoolExpr[] assertions={};
	public Status result;
	public Model model;
	private HashMap<String, AllocationNodeAP> allocationNodesAP;
	private HashMap<String, AllocationNodeMF> allocationNodesMF;
	private List<BoolExpr> constraintList;
	private long timeChecker;


	/**
	 * Public constructor of Checker class
	 * @param context it is the z3 context where assertions must be introduced into
	 * @param nctx it is the NetContext which stores basic z3 variables
	 * @param allocationNodes it is the map of allocation nodes of the Allocation Graph
	 */
	public Checker(Context context, NetContextAP nctx, HashMap<String,AllocationNodeAP> allocationNodes) {
		this.ctx = context;
		this.nctxAP = nctx;
		this.allocationNodesAP = allocationNodes;
		this.solver = ctx.mkOptimize();
		this.constraintList =new ArrayList<BoolExpr>();
		
		// initial parameters
		Params p = ctx.mkParams();
		p.add("maxsat_engine", ctx.mkSymbol("wmax"));
		p.add("maxres.wmax", true  );
		p.add("timeout", 1800000);
		solver.setParameters(p);
	}
	
	/**
	 * Public constructor of Checker class
	 * @param context it is the z3 context where assertions must be introduced into
	 * @param nctx it is the NetContext which stores basic z3 variables
	 * @param allocationNodes it is the map of allocation nodes of the Allocation Graph
	 */
	public Checker(Context context, NetContextMF nctx, HashMap<String,AllocationNodeMF> allocationNodes) {
		this.ctx = context;
		this.nctxMF = nctx;
		this.allocationNodesMF = allocationNodes;
		this.solver = ctx.mkOptimize();
		this.constraintList =new ArrayList<BoolExpr>();
		
		// initial parameters
		Params p = ctx.mkParams();
		p.add("maxsat_engine", ctx.mkSymbol("wmax"));
		p.add("maxres.wmax", true  );
		p.add("timeout", 1800000);
		solver.setParameters(p);
	}
	
	
/***************************************************************Atomic Predicates Methods*********************************************************************************************/
	
	/**
	 * Thus method adds hard and soft constraints in the solver
	 */
	public void addConstraintsAP() {
		allocationNodesAP.values().forEach(node->node.addConstraints(solver));
		constraintList.forEach(boolExpr->this.solver.Add(boolExpr));
		nctxAP.addConstraints(solver);
	}
		
	/**
	 * This method starts the z3 solver to solve the MaxSMT problem
	 * @return
	 */
	public VerificationResult propertyCheckAP(){
		solver.Push();
		addConstraintsAP();
		  long startTime = System.currentTimeMillis();

		result = this.solver.Check(); 
		long stopTime = System.currentTimeMillis();
	    long elapsedTime = stopTime - startTime;
	     timeChecker = elapsedTime;
	     System.out.println("single checker time " +timeChecker);
		model = (result == Status.SATISFIABLE) ? this.solver.getModel() : null;
		logAssertions();
		solver.Pop();
		return new VerificationResult(ctx, result, nctxAP, assertions, model);
	}

	/**
	 * This method is invoked by VerefooProxy to generate the z3 constraints for each security requirement
	 * @param sr It is the requirement that must be modeled in z3 language
	 * @param propType It is the type of the security requirement
	 */
	public void createRequirementConstraintsAP(SecurityRequirement sr, Prop propType) {
		
		switch (propType) {
			case ISOLATION:
				createIsolationConstraintsAP(sr);
				break;
			case REACHABILITY:
				createReachabilityConstraintAP(sr);
				break;
			case COMPLETE_REACHABILITY:
				createCompleteReachabilityConstraintAP(sr);
				break;
		}
	}
	
	/**
	 * Atomic Predicate Algorithm
	 * This method generates the constraints for a reachability requirement
	 * @param sr It is the requirement that must be modeled in z3 language
	 * @param propType It is the type of the security requirement
	 */
	private void createReachabilityConstraintAP(SecurityRequirement sr) {
		
		List<BoolExpr> pathConstraints = new ArrayList<>();
		Map<Integer, FlowPathAP> allFlows = sr.getFlowsMapAP();
		
		for(FlowPathAP flowPath : allFlows.values()) {
			for(AtomicFlow flow : flowPath.getAtomicFlowsMap().values()) {
				List<BoolExpr> singleConstraints = new ArrayList<>();
				
				for(AllocationNodeAP node : flowPath.getPath()) {
					int traffic;
					if(node.getAtomicPredicatesInInputForFlow(flowPath.getIdFlow()) == null)
						traffic = -1;
					else
						traffic = node.getAtomicPredicatesInInputForFlow(flowPath.getIdFlow()).get(flow.getFlowId());
					singleConstraints.add(ctx.mkImplies(node.getPlacedNF().getUsed(), ctx.mkNot((BoolExpr) nctxAP.deny.apply(node.getZ3Name(), ctx.mkInt(traffic)))));
				}
				

				BoolExpr[] arrayConstraints = new BoolExpr[singleConstraints.size()];
				BoolExpr finalConstraint = ctx.mkAnd(singleConstraints.toArray(arrayConstraints));
				pathConstraints.add(finalConstraint);
			}
		}
		
	
		BoolExpr[] arrayConstraints = new BoolExpr[pathConstraints.size()];
		BoolExpr finalConstraint = ctx.mkOr(pathConstraints.toArray(arrayConstraints));
		constraintList.add(finalConstraint);
	}
	
	/**
	 * Atomic Predicate Algorithm
	 * This method generates the constraints for an Complete Reachability requirement
	 * @param sr It is the requirement that must be modeled in z3 language
	 */
	private void createCompleteReachabilityConstraintAP(SecurityRequirement sr) {
		
		Map<Integer, FlowPathAP> allFlows = sr.getFlowsMapAP();
		List<BoolExpr> pathConstraints = new ArrayList<>();
		
		for(FlowPathAP flowPath : allFlows.values()) {
			List<BoolExpr> atomicFlowConstraintsInsideFlowPath = new ArrayList<>();
			for(Map.Entry<Integer, AtomicFlow> atomicFlowEntry: flowPath.getAtomicFlowsMap().entrySet()) {
				
				List<BoolExpr> singleConstraints = new ArrayList<>();
				for(AllocationNodeAP node : flowPath.getPath()) {
					int traffic;
					if(node.getAtomicPredicatesInInputForFlow(flowPath.getIdFlow()) == null)
						traffic = -1;
					else
						traffic = node.getAtomicPredicatesInInputForFlow(flowPath.getIdFlow()).get(atomicFlowEntry.getValue().getFlowId());
					singleConstraints.add(ctx.mkImplies(node.getPlacedNF().getUsed(), ctx.mkEq( (BoolExpr)nctxAP.deny.apply(node.getZ3Name(), ctx.mkInt(traffic)), ctx.mkFalse())));
				}
				
				BoolExpr[] arrayConstraints = new BoolExpr[singleConstraints.size()];
				BoolExpr maximalFlowConstraint = ctx.mkAnd(singleConstraints.toArray(arrayConstraints));
				atomicFlowConstraintsInsideFlowPath.add(maximalFlowConstraint);
			}
			
			BoolExpr[] tmp = new BoolExpr[atomicFlowConstraintsInsideFlowPath.size()];
			BoolExpr pathConstraint = ctx.mkAnd(atomicFlowConstraintsInsideFlowPath.toArray(tmp));
			pathConstraints.add(pathConstraint);
		}
	
		BoolExpr[] arrayConstraints = new BoolExpr[pathConstraints.size()];
		BoolExpr finalConstraint = ctx.mkOr(pathConstraints.toArray(arrayConstraints));
		constraintList.add(finalConstraint);
	}
	
	/**
	 * Atomic Predicate Algorithm
	 * This method generates the constraints for an isolation requirement
	 * @param sr It is the requirement that must be modeled in z3 language
	 */
	private void createIsolationConstraintsAP(SecurityRequirement sr) {
		
		List<BoolExpr> pathConstraints = new ArrayList<>();
		Map<Integer, FlowPathAP> allFlows = sr.getFlowsMapAP();
		
		for(FlowPathAP flowPath : allFlows.values()) {
			for(AtomicFlow flow : flowPath.getAtomicFlowsMap().values()) {
				List<BoolExpr> singleConstraints = new ArrayList<>();
				
				for(AllocationNodeAP node : flowPath.getPath()) {
					int traffic;
					if(node.getAtomicPredicatesInInputForFlow(flowPath.getIdFlow()) == null)
						traffic = -1;
					else
						traffic = node.getAtomicPredicatesInInputForFlow(flowPath.getIdFlow()).get(flow.getFlowId());
					singleConstraints.add(ctx.mkAnd(node.getPlacedNF().getUsed(), (BoolExpr) nctxAP.deny.apply(node.getZ3Name(), ctx.mkInt(traffic))));
				}
				
				BoolExpr[] arrayConstraints = new BoolExpr[singleConstraints.size()];
				BoolExpr finalConstraint = ctx.mkOr(singleConstraints.toArray(arrayConstraints));
				pathConstraints.add(finalConstraint);
			}
			
		}
		
		
		BoolExpr[] arrayConstraints = new BoolExpr[pathConstraints.size()];
		BoolExpr finalConstraint = ctx.mkAnd(pathConstraints.toArray(arrayConstraints));
		constraintList.add(finalConstraint);
	}
	
	public long getTimeChecker() {
		return timeChecker;
	}


	public void setTimeChecker(long timeChecker) {
		this.timeChecker = timeChecker;
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

/***********************************************************************Maximal Flows Methods*****************************************************************************************/
	
	/**
	 * Thus method adds hard and soft constraints in the solver
	 */
	public void addConstraintsMF() {
		allocationNodesMF.values().forEach(node->node.addConstraints(solver));
		constraintList.forEach(boolExpr->this.solver.Add(boolExpr));
		nctxMF.addConstraints(solver);
	}
	
	/**
	 * This method starts the z3 solver to solve the MaxSMT problem
	 * @return
	 */
	public VerificationResult propertyCheckMF(){
		solver.Push();
		addConstraintsMF();
		  long startTime = System.currentTimeMillis();

		result = this.solver.Check(); 
		long stopTime = System.currentTimeMillis();
	    long elapsedTime = stopTime - startTime;
	     timeChecker = elapsedTime;
	     System.out.println("single checker time " +timeChecker);
		model = (result == Status.SATISFIABLE) ? this.solver.getModel() : null;
		logAssertions();
		solver.Pop();
		return new VerificationResult(ctx, result, nctxMF, assertions, model);
	}	
	
	/**
	 * This method is invoked by VerefooProxy to generate the z3 constraints for each security requirement
	 * @param sr It is the requirement that must be modeled in z3 language
	 * @param propType It is the type of the security requirement
	 */
	public void createRequirementConstraintsMF(SecurityRequirement sr, Prop propType) {
		
		switch (propType) {
			case ISOLATION:
				createIsolationConstraintsMF(sr);
				break;
			case REACHABILITY:
				createReachabilityConstraintMF(sr);
				break;
			case COMPLETE_REACHABILITY:
				createCompleteReachabilityConstraintMF(sr);
				break;
		}
	}
	

	/**
	 * Maximal flow algorithm
	 * This method generates the constraints for a reachability requirement
	 * @param sr It is the requirement that must be modeled in z3 language
	 * @param propType It is the type of the security requirement
	 */
	private void createReachabilityConstraintMF(SecurityRequirement sr) {
		
		List<BoolExpr> pathConstraints = new ArrayList<>();
		Map<Integer, FlowPathMF> allFlows = sr.getFlowsMapMF();
		
		for(FlowPathMF flow : allFlows.values()) {
			for(Map.Entry<Integer, MaximalFlow> maximalFlowEntry: flow.getMaximalFlowsMap().entrySet()) {
				
				List<BoolExpr> singleConstraints = new ArrayList<>();
				for(AllocationNodeMF node : flow.getPath()) {
					singleConstraints.add(ctx.mkImplies(node.getPlacedNF().getUsed(), ctx.mkEq( (BoolExpr)nctxMF.deny.apply(node.getZ3Name(), ctx.mkInt(maximalFlowEntry.getKey())), ctx.mkFalse())));
				}
				
				BoolExpr[] arrayConstraints = new BoolExpr[singleConstraints.size()];
				BoolExpr finalConstraint = ctx.mkAnd(singleConstraints.toArray(arrayConstraints));
				pathConstraints.add(finalConstraint);
			}
		}
		
	
		BoolExpr[] arrayConstraints = new BoolExpr[pathConstraints.size()];
		BoolExpr finalConstraint = ctx.mkOr(pathConstraints.toArray(arrayConstraints));
		constraintList.add(finalConstraint);
	}
	
	/**
	 * Maximal flow algorithm
	 * This method generates the constraints for a Complete Reachability requirement
	 * @param sr It is the requirement that must be modeled in z3 language
	 */
	private void createCompleteReachabilityConstraintMF(SecurityRequirement sr) {
		
		Map<Integer, FlowPathMF> allFlows = sr.getFlowsMapMF();
		List<BoolExpr> pathConstraints = new ArrayList<>();
		
		for(FlowPathMF flow : allFlows.values()) {
			List<BoolExpr> maximalFlowConstraintsInsideFlowPath = new ArrayList<>();
			for(Map.Entry<Integer, MaximalFlow> maximalFlowEntry: flow.getMaximalFlowsMap().entrySet()) {
				
				List<BoolExpr> singleConstraints = new ArrayList<>();
				for(AllocationNodeMF node : flow.getPath()) {
					singleConstraints.add(ctx.mkImplies(node.getPlacedNF().getUsed(), ctx.mkEq( (BoolExpr)nctxMF.deny.apply(node.getZ3Name(), ctx.mkInt(maximalFlowEntry.getKey())), ctx.mkFalse())));
				}
				
				BoolExpr[] arrayConstraints = new BoolExpr[singleConstraints.size()];
				BoolExpr maximalFlowConstraint = ctx.mkAnd(singleConstraints.toArray(arrayConstraints));
				maximalFlowConstraintsInsideFlowPath.add(maximalFlowConstraint);
			}
			
			BoolExpr[] tmp = new BoolExpr[maximalFlowConstraintsInsideFlowPath.size()];
			BoolExpr pathConstraint = ctx.mkAnd(maximalFlowConstraintsInsideFlowPath.toArray(tmp));
			pathConstraints.add(pathConstraint);
		}
	
		BoolExpr[] arrayConstraints = new BoolExpr[pathConstraints.size()];
		BoolExpr finalConstraint = ctx.mkOr(pathConstraints.toArray(arrayConstraints));
		constraintList.add(finalConstraint);
	}
	
	/**
	 * Maximal Flow Algorithm
	 * This method generates the constraints for an isolation requirement
	 * @param sr It is the requirement that must be modeled in z3 language
	 */
	private void createIsolationConstraintsMF(SecurityRequirement sr) {
		
		List<BoolExpr> pathConstraints = new ArrayList<>();
		Map<Integer, FlowPathMF> allFlows = sr.getFlowsMapMF();
		
		for(FlowPathMF flow : allFlows.values()) {
			for(Map.Entry<Integer, MaximalFlow> maximalFlowEntry: flow.getMaximalFlowsMap().entrySet()) {
				
				List<BoolExpr> singleConstraints = new ArrayList<>();
				
				for(AllocationNodeMF node : flow.getPath()) {
					singleConstraints.add(ctx.mkAnd(node.getPlacedNF().getUsed(), (BoolExpr) nctxMF.deny.apply(node.getZ3Name(), ctx.mkInt(maximalFlowEntry.getKey()))));
				}
				
				BoolExpr[] arrayConstraints = new BoolExpr[singleConstraints.size()];
				BoolExpr finalConstraint = ctx.mkOr(singleConstraints.toArray(arrayConstraints));
				pathConstraints.add(finalConstraint);
			}
		}
		
		
		BoolExpr[] arrayConstraints = new BoolExpr[pathConstraints.size()];
		BoolExpr finalConstraint = ctx.mkAnd(pathConstraints.toArray(arrayConstraints));
		constraintList.add(finalConstraint);
	}

}


