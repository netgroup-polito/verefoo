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
/**
 *  MailClient objects
 *
 */
public class PolitoMailClient extends NetworkObject{

    List<BoolExpr> constraints;
    Context ctx;
    DatatypeExpr politoMailClient;
    Network net;
    NetContext nctx;
    FuncDecl isInBlacklist;

    public PolitoMailClient(Context ctx, Object[]... args) {
        super(ctx, args);
    }

    @Override
    protected void init(Context ctx, Object[]... args) {
        this.ctx = ctx;
        isEndHost=true;
        constraints = new ArrayList<BoolExpr>();
        z3Node = ((NetworkObject)args[0][0]).getZ3Node();
        politoMailClient = z3Node;
        net = (Network)args[0][1];
        nctx = (NetContext)args[0][2];
        DatatypeExpr ipServer = (DatatypeExpr) args[0][3];
        mailClientRules(ipServer);
        //net.saneSend(this);
    }

    @Override
    public DatatypeExpr getZ3Node() {
        return politoMailClient;
    }

    @Override
    protected void addConstraints(Solver solver) {
        //System.out.println("[MailClient] Installing rules.");
        BoolExpr[] constr = new BoolExpr[constraints.size()];
        solver.add(constraints.toArray(constr));
    }

    private void mailClientRules (DatatypeExpr ipServer){
        Expr n_0 = ctx.mkConst("PolitoMailClient_"+politoMailClient+"_n_0", nctx.node);
        Expr p_0 = ctx.mkConst("PolitoMailClient_"+politoMailClient+"_p_0", nctx.packet);
        IntExpr t_0 = ctx.mkIntConst("PolitoMailClient_"+politoMailClient+"_t_0");

        //Constraint1 send(politoMailClient, n_0, p, t_0) -> nodeHasAddr(politoMailClient,p.src)
        constraints.add( ctx.mkForall(new Expr[]{n_0, p_0, t_0},
                ctx.mkImplies((BoolExpr)nctx.send.apply(politoMailClient, n_0, p_0, t_0),
                        (BoolExpr)nctx.nodeHasAddr.apply(politoMailClient,nctx.pf.get("src").apply(p_0))),1,null,null,null,null));

        //Constraint2 send(politoMailClient, n_0, p, t_0) -> p.origin == politoMailClient
        constraints.add( ctx.mkForall(new Expr[]{n_0, p_0, t_0},
                ctx.mkImplies((BoolExpr)nctx.send.apply(politoMailClient, n_0, p_0, t_0),
                        ctx.mkEq(nctx.pf.get("origin").apply(p_0),politoMailClient)),1,null,null,null,null));

        //Constraint3 send(politoMailClient, n_0, p, t_0) -> p.orig_body == p.body
        constraints.add( ctx.mkForall(new Expr[]{n_0, p_0, t_0},
                ctx.mkImplies((BoolExpr)nctx.send.apply(politoMailClient, n_0, p_0, t_0),
                        ctx.mkEq(nctx.pf.get("orig_body").apply(p_0),nctx.pf.get("body").apply(p_0))),1,null,null,null,null));

        //Constraint4 recv(n_0, politoMailClient, p, t_0) -> nodeHasAddr(politoMailClient,p.dest)
        constraints.add( ctx.mkForall(new Expr[]{n_0, p_0, t_0},
                ctx.mkImplies((BoolExpr)nctx.recv.apply(n_0,politoMailClient, p_0, t_0),
                        (BoolExpr)nctx.nodeHasAddr.apply(politoMailClient,nctx.pf.get("dest").apply(p_0))),1,null,null,null,null));

        //Constraint5 This client is only able to produce POP3 requests
        //send(politoMailClient, n_0, p, t_0) -> p.proto == POP3_REQ
        constraints.add( ctx.mkForall(new Expr[]{n_0, p_0, t_0},
                ctx.mkImplies((BoolExpr)nctx.send.apply(politoMailClient, n_0, p_0, t_0),
                        ctx.mkEq(nctx.pf.get("proto").apply(p_0), ctx.mkInt(nctx.POP3_REQUEST))),1,null,null,null,null));

        //Constraint6 send(politoMailClient, n_0, p, t_0) -> p.dest == ip_mailServer
        constraints.add( ctx.mkForall(new Expr[]{n_0, p_0, t_0},
                ctx.mkImplies((BoolExpr)nctx.send.apply(politoMailClient, n_0, p_0, t_0),
                        ctx.mkEq(nctx.pf.get("dest").apply(p_0), ipServer)),1,null,null,null,null));
    }
}