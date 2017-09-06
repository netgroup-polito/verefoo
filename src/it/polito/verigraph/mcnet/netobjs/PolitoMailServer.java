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
/** Mail server objects
 *
 *
 */
public class PolitoMailServer extends NetworkObject{

    List<BoolExpr> constraints;
    Context ctx;
    DatatypeExpr politoMailServer;
    Network net;
    NetContext nctx;
    FuncDecl isInBlacklist;

    public PolitoMailServer(Context ctx, Object[]... args) {
        super(ctx, args);
    }

    @Override
    protected void init(Context ctx, Object[]... args) {
        this.ctx = ctx;
        isEndHost=false;
        constraints = new ArrayList<BoolExpr>();
        z3Node = ((NetworkObject)args[0][0]).getZ3Node();
        politoMailServer = z3Node;
        net = (Network)args[0][1];
        nctx = (NetContext)args[0][2];
        mailServerRules();
        net.saneSend(this);
    }

    @Override
    public DatatypeExpr getZ3Node() {
        return politoMailServer;
    }

    @Override
    protected void addConstraints(Solver solver) {
        BoolExpr[] constr = new BoolExpr[constraints.size()];
        solver.add(constraints.toArray(constr));
    }

    private void mailServerRules (){
        Expr n_0 = ctx.mkConst(politoMailServer+"_n_0", nctx.node);
        Expr p_0 = ctx.mkConst(politoMailServer+"_p_0", nctx.packet);
        Expr p_1 = ctx.mkConst(politoMailServer+"_p_1", nctx.packet);
        IntExpr t_0 = ctx.mkIntConst(politoMailServer+"_t_0");
        IntExpr t_1 = ctx.mkIntConst(politoMailServer+"_t_1");

        //Constraint1 send(politoMailServer, n_0, p, t_0) -> nodeHasAddr(politoMailServer,p.src)
        constraints.add( ctx.mkForall(new Expr[]{n_0, p_0, t_0},
                ctx.mkImplies((BoolExpr)nctx.send.apply(politoMailServer, n_0, p_0, t_0),
                        (BoolExpr)nctx.nodeHasAddr.apply(politoMailServer,nctx.pf.get("src").apply(p_0))),1,null,null,null,null));

        //Constraint2 send(politoMailServer, n_0, p, t_0) -> p.origin == politoMailServer
        constraints.add( ctx.mkForall(new Expr[]{n_0, p_0, t_0},
                ctx.mkImplies((BoolExpr)nctx.send.apply(politoMailServer, n_0, p_0, t_0),
                        ctx.mkEq(nctx.pf.get("origin").apply(p_0),politoMailServer)),1,null,null,null,null));

        //Constraint3 send(politoMailServer, n_0, p, t_0) -> p.orig_body == p.body
        constraints.add( ctx.mkForall(new Expr[]{n_0, p_0, t_0},
                ctx.mkImplies((BoolExpr)nctx.send.apply(politoMailServer, n_0, p_0, t_0),
                        ctx.mkEq(nctx.pf.get("orig_body").apply(p_0),nctx.pf.get("body").apply(p_0))),1,null,null,null,null));

        //Constraint4 recv(n_0, politoMailServer, p, t_0) -> nodeHasAddr(politoMailServer,p.dest)
        constraints.add( ctx.mkForall(new Expr[]{n_0, p_0, t_0},
                ctx.mkImplies((BoolExpr)nctx.recv.apply(n_0,politoMailServer, p_0, t_0),
                        (BoolExpr)nctx.nodeHasAddr.apply(politoMailServer,nctx.pf.get("dest").apply(p_0))),1,null,null,null,null));

        //Constraint5 send(politoMailServer, n_0, p, t_0) -> p.proto == POP3_RESP && p.emailFrom == 1
        constraints.add( ctx.mkForall(new Expr[]{n_0, p_0, t_0},
                ctx.mkImplies((BoolExpr)nctx.send.apply(politoMailServer, n_0, p_0, t_0),
                        ctx.mkAnd(ctx.mkEq(nctx.pf.get("proto").apply(p_0), ctx.mkInt(nctx.POP3_RESPONSE)),
                                ctx.mkEq(nctx.pf.get("emailFrom").apply(p_0), ctx.mkInt(1)))),1,null,null,null,null));

        //Constraint6 send(politoMailServer, n_0, p, t_0)  ->
        //    (exist p_1, t_1 : (t_1 < t_0 && recv(n_0, politoMailServer, p_1, t_1) &&
        //    p_0.proto == POP3_RESP &&  p_1.proto == POP3_REQ && p_0.dest == p_1.src )

        constraints.add( ctx.mkForall(new Expr[]{n_0, p_0, t_0},
                ctx.mkImplies((BoolExpr)nctx.send.apply(politoMailServer, n_0, p_0, t_0),
                        ctx.mkExists(new Expr[]{p_1, t_1},
                                ctx.mkAnd(ctx.mkLt(t_1, t_0),
                                        (BoolExpr)nctx.recv.apply(n_0, politoMailServer, p_1, t_1),
                                        ctx.mkEq(nctx.pf.get("proto").apply(p_0), ctx.mkInt(nctx.POP3_RESPONSE)),
                                        ctx.mkEq(nctx.pf.get("proto").apply(p_1), ctx.mkInt(nctx.POP3_REQUEST)),
                                        ctx.mkEq(nctx.pf.get("dest").apply(p_0), nctx.pf.get("src").apply(p_1))),1,null,null,null,null)),1,null,null,null,null));

        //    constraints.add( ctx.mkForall(new Expr[]{n_0, p_0, t_0},
        //                    ctx.mkImplies((BoolExpr)nctx.send.apply(politoMailServer, n_0, p_0, t_0),
        //                    ctx.mkEq(nctx.pf.get("emailFrom").apply(p_0),ctx.mkInt(2))),1,null,null,null,null));
    }
}