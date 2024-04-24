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
import it.polito.verefoo.utils.PortInterval;
import it.polito.verefoo.utils.Tuple;

/**
 * Basic fields and other things required for model checking.
 *
 */
public class NetContextAP extends NetContext {

	private HashMap<String, AllocationNodeAP> allocationNodes;
	
    // weights
    public final int WAUTOCONF   = 1;
    //REACT_VEREFOO
    public final int WMAINTAINSTATECONF = WAUTOCONF / 2;
    public final int WMAINTAINSTATEPLACEMENT = WAUTOPLACEMENT/10;

 
    /**
     * Context for all of the rest that follows. Every network needs one of these
     * @param ctx
     * @param args
     */
    public NetContextAP(Context ctx, HashMap<String, AllocationNodeAP> allocationNodes, Object[]... args){
          nodeMap = new HashMap<String,DatatypeExpr>(); //list of nodes, callable by node name
          addressMap = new HashMap<String,DatatypeExpr>(); // list of addresses, callable by address name
          portMap = new HashMap<String,DatatypeExpr>();
          functionsMap= new HashMap<String,FuncDecl>() ;
          ipFunctionsMap = new HashMap<String,FuncDecl>();
          portFunctionsMap = new HashMap<String,FuncDecl>();
          constraints = new ArrayList<BoolExpr>();
          softConstrAutoConf = new ArrayList<>();
          softConstrAutoPlace = new ArrayList<>();
          softConstrMaintainStatePlacement = new ArrayList<>();
          softConstrMaintainStateConfiguration = new ArrayList<>();
          softConstrWildcard = new ArrayList<>(); 
          softConstrProtoWildcard = new ArrayList<>(); 
          softConstrPorts = new ArrayList<>();

    	  this.ctx = ctx;
          this.allocationNodes = allocationNodes;
          mkTypes((String[])args[0],(String[])args[1], (String[])args[2], (String[])args[3]);
    }

    
    
    /*
     * Main Methods of NetContextAP class
     */

    
    /**
     * This methods adds hard and soft constraints inside the z3 solver
     * @param solver it is the z3 solver
     */
    protected void addConstraints(Optimize solver) {
        BoolExpr[] constr = new BoolExpr[constraints.size()];
        solver.Add(constraints.toArray(constr));
        softConstrAutoConf.forEach(t->solver.AssertSoft(t._1, WAUTOCONF, t._2));
        softConstrAutoPlace.forEach(t->solver.AssertSoft(t._1, WAUTOPLACEMENT, t._2));
        softConstrMaintainStatePlacement.forEach(t->solver.AssertSoft(t._1, WMAINTAINSTATEPLACEMENT, t._2));
        softConstrMaintainStateConfiguration.forEach(t->solver.AssertSoft(t._1, WMAINTAINSTATECONF, t._2));
    }
    
    /**
     * This method is in charge of creating the z3 types for ports, nodes, addresses and packets 
     * @param nodes it is the array of node names
     * @param addresses it is the array of IP addresses
     * @param srcp_ranges it is the array of source ports -> it can be a single port or a range of ports divided by a "-"
     * @param dstp_ranges it is the array of destination ports
     */
    private void mkTypes (String[] nodes, String[] addresses, String[] srcp_ranges, String[] dstp_ranges){
    	  
        //----------- Nodes in this network
        nodeType = ctx.mkEnumSort("Node", nodes);
        for(int i=0;i<nodeType.getConsts().length;i++){
            DatatypeExpr fd  = (DatatypeExpr)nodeType.getConst(i);   
            nodeMap.put(fd.toString().replace("|", ""),fd);
            AllocationNodeAP n = allocationNodes.get(nodes[i]);
            n.setZ3Name(fd);
        }

      //----------- Deny predicate
        deny = ctx.mkFuncDecl("deny", new Sort[]{ nodeType, ctx.mkIntSort()},ctx.mkBoolSort());
    }
    
 


}