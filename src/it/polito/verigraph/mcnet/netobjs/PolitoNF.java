/*******************************************************************************
 * Copyright (c) 2017 Politecnico di Torino and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package it.polito.verigraph.mcnet.netobjs;

import java.util.ArrayList;
import java.util.List;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.DatatypeExpr;
import com.microsoft.z3.Expr;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Sort;

import it.polito.verigraph.mcnet.components.NetContext;
import it.polito.verigraph.mcnet.components.Network;
import it.polito.verigraph.mcnet.components.NetworkObject;

/** First example of custom network function: a simple filter
 *
 *
 */
public class PolitoNF extends NetworkObject{

    List<BoolExpr> constraints;
    Context ctx;
    DatatypeExpr politoNF;
    Network net;
    NetContext nctx;
    FuncDecl isInBlacklist;


    public PolitoNF(Context ctx, Object[]... args) {
        super(ctx, args);
    }

    @Override
    protected void init(Context ctx, Object[]... args) {
        this.ctx = ctx;
        isEndHost=false;
        constraints = new ArrayList<BoolExpr>();
        z3Node = ((NetworkObject)args[0][0]).getZ3Node();
        politoNF = z3Node;
        net = (Network)args[0][1];
        nctx = (NetContext)args[0][2];
        //net.saneSend(this);
    }

    public void politoNFRules (DatatypeExpr ipA,DatatypeExpr ipB){
        //    System.out.println("[PolitoNf] Installing rules");
        Expr n_0 = ctx.mkConst("politoNF_"+politoNF+"_n_0", nctx.node);
        Expr n_1 = ctx.mkConst("politoNF_"+politoNF+"_n_1", nctx.node);
        Expr p_0 = ctx.mkConst("politoNF_"+politoNF+"_p_0", nctx.packet);
        IntExpr t_0 = ctx.mkIntConst("politoNF_"+politoNF+"_t_0");
        IntExpr t_1 = ctx.mkIntConst("politoNF_"+politoNF+"_t_1");
        Expr a_0 = ctx.mkConst(politoNF+"_politoNF_a_0", nctx.address);
        Expr a_1 = ctx.mkConst(politoNF+"_politoNF_a_1", nctx.address);

        FuncDecl myFunction = ctx.mkFuncDecl(politoNF+"_myFunction", new Sort[]{nctx.address,nctx.address}, ctx.mkBoolSort());

        BoolExpr myConstraint = ctx.mkForall(new Expr[]{n_0, p_0, t_0},
                ctx.mkImplies((BoolExpr)nctx.send.apply(politoNF, n_0, p_0, t_0),
                        ctx.mkExists(new Expr[]{n_1, t_1},
                                ctx.mkAnd((BoolExpr)nctx.recv.apply(n_1, politoNF, p_0, t_1),
                                        ctx.mkLt(t_1 , t_0),
                                        (BoolExpr)myFunction.apply(nctx.pf.get("src").apply(p_0), nctx.pf.get("dest").apply(p_0))),1,null,null,null,null)),1,null,null,null,null);

        BoolExpr funcConstraint = ctx.mkOr(ctx.mkAnd(ctx.mkEq(a_0, ipA), ctx.mkEq(a_1, ipB)), ctx.mkAnd(ctx.mkEq(a_0,ipB), ctx.mkEq(a_1,ipA)));

        // Constraint1myFunction(a_0,a_1) == ((a_0 == ipA && a_1 == ipB) || (a_0 == ipB && a_1 == ipA))
        constraints.add(
                ctx.mkForall(new Expr[]{a_0,a_1},
                        ctx.mkEq(myFunction.apply(a_0, a_1), funcConstraint),1,null,null,null,null));

        //Constraint2send(politoNF, n_0, p, t_0)  ->
        //(exist n_1,t_1 : (t_1 < t_0 && recv(n_1, politoNF, p, t_1) && myFunction(p.src,p.dest))
        constraints.add(myConstraint);

    }

    @Override
    protected void addConstraints(Solver solver) {
        BoolExpr[] constr = new BoolExpr[constraints.size()];
        solver.add(constraints.toArray(constr));
    }

    @Override
    public DatatypeExpr getZ3Node() {
        return politoNF;
    }

}