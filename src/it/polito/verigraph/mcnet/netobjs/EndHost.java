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
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.Solver;
import it.polito.verigraph.mcnet.components.NetContext;
import it.polito.verigraph.mcnet.components.Network;
import it.polito.verigraph.mcnet.components.NetworkObject;
/**
 * End host network objects
 *
 *
 */
public class EndHost extends NetworkObject{

    List<BoolExpr> constraints;
    Context ctx;
    DatatypeExpr node;
    Network net;
    NetContext nctx;

    public EndHost(Context ctx, Object[]... args) {
        super(ctx, args);
    }

    @Override
    protected void init(Context ctx, Object[]... args) {
        this.ctx = ctx;
        isEndHost=true;
        constraints = new ArrayList<BoolExpr>();
        z3Node = ((NetworkObject)args[0][0]).getZ3Node();
        node = z3Node;
        net = (Network)args[0][1];
        nctx = (NetContext)args[0][2];
        endHostRules();
    }

    @Override
    public DatatypeExpr getZ3Node() {
        return node;
    }

    @Override
    protected void addConstraints(Solver solver) {
        BoolExpr[] constr = new BoolExpr[constraints.size()];
        solver.add(constraints.toArray(constr));
    }

    public void endHostRules (){
        Expr n_0 = ctx.mkConst("eh_"+node+"_n_0", nctx.node);
        IntExpr t_0 = ctx.mkIntConst("eh_"+node+"_t_0");
        Expr p_0 = ctx.mkConst("eh_"+node+"_p_0", nctx.packet);

        //Constraint1send(node, n_0, p, t_0) -> nodeHasAddr(node,p.src)
        constraints.add(
                ctx.mkForall(new Expr[]{n_0, p_0, t_0},
                        ctx.mkImplies(
                                (BoolExpr)nctx.send.apply(new Expr[]{ node, n_0, p_0, t_0}),
                                (BoolExpr)nctx.nodeHasAddr.apply(new Expr[]{node, nctx.pf.get("src").apply(p_0)})),1,null,null,null,null));
        //Constraint2send(node, n_0, p, t_0) -> p.origin == node
        constraints.add(
                ctx.mkForall(new Expr[]{n_0, p_0, t_0},
                        ctx.mkImplies(
                                (BoolExpr)nctx.send.apply(new Expr[]{ node, n_0, p_0, t_0}),
                                ctx.mkEq(nctx.pf.get("origin").apply(p_0),node)),1,null,null,null,null));
        //Constraint3      send(node, n_0, p, t_0) -> p.orig_body == p.body
        constraints.add(
                ctx.mkForall(new Expr[]{n_0, p_0, t_0},
                        ctx.mkImplies(
                                (BoolExpr)nctx.send.apply(new Expr[]{ node, n_0, p_0, t_0}),
                                ctx.mkEq(nctx.pf.get("orig_body").apply(p_0),nctx.pf.get("body").apply(p_0))),1,null,null,null,null));
        //Constraint4recv(n_0, node, p, t_0) -> nodeHasAddr(node,p.dest)
        constraints.add(
                ctx.mkForall(new Expr[]{n_0, p_0, t_0},
                        ctx.mkImplies(
                                (BoolExpr)nctx.recv.apply(new Expr[]{ n_0, node, p_0, t_0}),
                                (BoolExpr)nctx.nodeHasAddr.apply(new Expr[]{node, nctx.pf.get("dest").apply(p_0)})),1,null,null,null,null));

        //       Just a try: here we state that an endhost is not able to issue a HTTP response traffic
        //       See PolitoCache.py model for constants definition (2 means HTTP_RESPONSE, 1 means HTTP_REQUEST)
        //         constraints.add(ctx.mkForall(new Expr[]{n_0, p_0, t_0},
        //                 ctx.mkImplies((BoolExpr)nctx.send.apply(node, n_0, p_0, t_0),
        //                            ctx.mkEq(nctx.pf.get("proto").apply(p_0), ctx.mkInt(1))),1,null,null,null,null));
    }
}