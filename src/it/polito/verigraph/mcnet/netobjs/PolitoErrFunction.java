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

import it.polito.verigraph.mcnet.components.NetContext;
import it.polito.verigraph.mcnet.components.Network;
import it.polito.verigraph.mcnet.components.NetworkObject;
/**Error Function objects
 *
 *
 */
public class PolitoErrFunction extends NetworkObject{

    List<BoolExpr> constraints;
    Context ctx;
    DatatypeExpr politoErrFunction;
    Network net;
    NetContext nctx;
    FuncDecl isInBlacklist;

    public PolitoErrFunction(Context ctx, Object[]... args) {
        super(ctx, args);
    }

    @Override
    protected void init(Context ctx, Object[]... args) {
        this.ctx = ctx;
        isEndHost=true;
        constraints = new ArrayList<BoolExpr>();
        z3Node = ((NetworkObject)args[0][0]).getZ3Node();
        politoErrFunction = z3Node;
        net = (Network)args[0][1];
        nctx = (NetContext)args[0][2];
        errFunctionRules();
        //net.saneSend(this);
    }

    @Override
    public DatatypeExpr getZ3Node() {
        return politoErrFunction;
    }

    @Override
    protected void addConstraints(Solver solver) {
        //System.out.println("[ERR FUNC] Installing rules.");
        BoolExpr[] constr = new BoolExpr[constraints.size()];
        solver.add(constraints.toArray(constr));
    }

    private void errFunctionRules (){
        Expr n_0 = ctx.mkConst("PolitoErrFunction_"+politoErrFunction+"_n_0", nctx.node);
        Expr p_0 = ctx.mkConst("PolitoErrFunction_"+politoErrFunction+"_p_0", nctx.packet);
        IntExpr t_0 = ctx.mkIntConst("PolitoErrFunction_"+politoErrFunction+"_t_0");

        //     constraints.add( ctx.mkForall(new Expr[]{n_0, p_0, t_0},
        //     ctx.mkImplies((BoolExpr)nctx.send.apply(politoErrFunction, n_0, p_0, t_0),
        //                 (BoolExpr)nctx.nodeHasAddr.apply(politoErrFunction, nctx.pf.get("src").apply(p_0))),1,null,null,null,null));
        //     constraints.add( ctx.mkForall(new Expr[]{n_0, p_0, t_0},
        //     ctx.mkImplies((BoolExpr)nctx.send.apply(politoErrFunction, n_0, p_0, t_0),
        //                 ctx.mkEq(nctx.pf.get("origin").apply(p_0), politoErrFunction)),1,null,null,null,null));
        //
        //     constraints.add( ctx.mkForall(new Expr[]{n_0, p_0, t_0},
        //     ctx.mkImplies((BoolExpr)nctx.send.apply(politoErrFunction, n_0, p_0, t_0),
        //                    ctx.mkEq(nctx.pf.get("orig_body").apply(p_0),nctx.pf.get("body").apply(p_0))),1,null,null,null,null));


        //            Constraint1 We want the ErrFunction not to send out any packet
        //    send(politoErrFunction, n_0, p, t_0) -> 1 == 2
        constraints.add( ctx.mkForall(new Expr[]{n_0, p_0, t_0},
                ctx.mkImplies((BoolExpr)nctx.send.apply(politoErrFunction, n_0, p_0, t_0),
                        ctx.mkEq(ctx.mkInt(1),ctx.mkInt(2))),1,null,null,null,null));

        //            constraints.add( ctx.mkForall(new Expr[]{n_0, p_0, t_0},
        //            ctx.mkImplies((BoolExpr)nctx.send.apply(n_0, politoErrFunction, p_0, t_0),
        //                            (BoolExpr)nctx.nodeHasAddr.apply(politoErrFunction, nctx.pf.get("dest").apply(p_0))),1,null,null,null,null));

    }
}

