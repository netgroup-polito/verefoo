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
 * WebClient
 */
public class PolitoWebClient extends NetworkObject{

    List<BoolExpr> constraints;
    Context ctx;
    DatatypeExpr politoWebClient;
    Network net;
    NetContext nctx;
    FuncDecl isInBlacklist;

    public PolitoWebClient(Context ctx, Object[]... args) {
        super(ctx, args);
    }

    @Override
    protected void init(Context ctx, Object[]... args) {
        this.ctx = ctx;
        isEndHost=true;
        constraints = new ArrayList<BoolExpr>();
        z3Node = ((NetworkObject)args[0][0]).getZ3Node();
        politoWebClient = z3Node;
        net = (Network)args[0][1];
        nctx = (NetContext)args[0][2];
        DatatypeExpr ipServer = (DatatypeExpr) args[0][3];
        webClientRules(ipServer);
        //net.saneSend(this);
    }

    @Override
    public DatatypeExpr getZ3Node() {
        return politoWebClient;
    }

    @Override
    protected void addConstraints(Solver solver) {
        //System.out.println("[MailClient] Installing rules.");
        BoolExpr[] constr = new BoolExpr[constraints.size()];
        solver.add(constraints.toArray(constr));
    }

    private void webClientRules (DatatypeExpr ipServer){
        Expr n_0 = ctx.mkConst("PolitoWebClient_"+politoWebClient+"_n_0", nctx.node);
        Expr p_0 = ctx.mkConst("PolitoWebClient_"+politoWebClient+"_p_0", nctx.packet);
        IntExpr t_0 = ctx.mkIntConst("PolitoWebClient_"+politoWebClient+"_t_0");

        //Constraint1send(politoWebClient, n_0, p, t_0) -> nodeHasAddr(politoWebClient,p.src)
        constraints.add( ctx.mkForall(new Expr[]{n_0, p_0, t_0},
                ctx.mkImplies((BoolExpr)nctx.send.apply(politoWebClient, n_0, p_0, t_0),
                        (BoolExpr)nctx.nodeHasAddr.apply(politoWebClient,nctx.pf.get("src").apply(p_0))),1,null,null,null,null));

        //Constraint2send(politoWebClient, n_0, p, t_0) -> p.origin == politoWebClient
        constraints.add( ctx.mkForall(new Expr[]{n_0, p_0, t_0},
                ctx.mkImplies((BoolExpr)nctx.send.apply(politoWebClient, n_0, p_0, t_0),
                        ctx.mkEq(nctx.pf.get("origin").apply(p_0),politoWebClient)),1,null,null,null,null));

        //Constraint3send(politoWebClient, n_0, p, t_0) -> p.orig_body == p.body
        constraints.add( ctx.mkForall(new Expr[]{n_0, p_0, t_0},
                ctx.mkImplies((BoolExpr)nctx.send.apply(politoWebClient, n_0, p_0, t_0),
                        ctx.mkEq(nctx.pf.get("orig_body").apply(p_0),nctx.pf.get("body").apply(p_0))),1,null,null,null,null));

        //Constraint4recv(n_0, politoWebClient, p, t_0) -> nodeHasAddr(politoWebClient,p.dest)
        constraints.add( ctx.mkForall(new Expr[]{n_0, p_0, t_0},
                ctx.mkImplies((BoolExpr)nctx.recv.apply(n_0,politoWebClient, p_0, t_0),
                        (BoolExpr)nctx.nodeHasAddr.apply(politoWebClient,nctx.pf.get("dest").apply(p_0))),1,null,null,null,null));


        //Constraint5This client is only able to produce HTTP requests
        //send(politoWebClient, n_0, p, t_0) -> p.proto == HTTP_REQ
        constraints.add( ctx.mkForall(new Expr[]{n_0, p_0, t_0},
                ctx.mkImplies((BoolExpr)nctx.send.apply(politoWebClient, n_0, p_0, t_0),
                        ctx.mkEq(nctx.pf.get("proto").apply(p_0), ctx.mkInt(nctx.HTTP_REQUEST))),1,null,null,null,null));

        //Constraint6send(politoWebClient, n_0, p, t_0) -> p.dest == ipServer
        constraints.add( ctx.mkForall(new Expr[]{n_0, p_0, t_0},
                ctx.mkImplies((BoolExpr)nctx.send.apply(politoWebClient, n_0, p_0, t_0),
                        ctx.mkEq(nctx.pf.get("dest").apply(p_0), ipServer)),1,null,null,null,null));

    }
}