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
/**Cache Model
 *
 *
 */
public class PolitoCache extends NetworkObject{

    List<BoolExpr> constraints;
    Context ctx;
    DatatypeExpr politoCache;
    Network net;
    NetContext nctx;
    FuncDecl isInBlacklist;

    public PolitoCache(Context ctx, Object[]... args) {
        super(ctx, args);
    }

    @Override
    protected void init(Context ctx, Object[]... args) {
        this.ctx = ctx;
        isEndHost=false;
        constraints = new ArrayList<BoolExpr>();
        z3Node = ((NetworkObject)args[0][0]).getZ3Node();
        politoCache = z3Node;
        net = (Network)args[0][1];
        nctx = (NetContext)args[0][2];
        //net.saneSend(this);
    }

    @Override
    public DatatypeExpr getZ3Node() {
        return politoCache;
    }

    @Override
    protected void addConstraints(Solver solver) {
        BoolExpr[] constr = new BoolExpr[constraints.size()];
        solver.add(constraints.toArray(constr));
    }

    public void installCache (NetworkObject[] internalNodes){
        Expr n_0 = ctx.mkConst("politoCache_"+politoCache+"_n_0", nctx.node);
        Expr n_1 = ctx.mkConst("politoCache_"+politoCache+"_n_1", nctx.node);
        Expr n_2 = ctx.mkConst("politoCache_"+politoCache+"_n_2", nctx.node);

        Expr p_0 = ctx.mkConst("politoCache_"+politoCache+"_p_0", nctx.packet);
        Expr p_1 = ctx.mkConst("politoCache_"+politoCache+"_p_1", nctx.packet);
        Expr p_2 = ctx.mkConst("politoCache_"+politoCache+"_p_2", nctx.packet);

        IntExpr t_0 = ctx.mkIntConst("politoCache_"+politoCache+"_t_0");
        IntExpr t_1 = ctx.mkIntConst("politoCache_"+politoCache+"_t_1");
        IntExpr t_2 = ctx.mkIntConst("politoCache_"+politoCache+"_t_2");

        Expr a_0 = ctx.mkConst(politoCache+"politoCache_a_0", nctx.node);
        IntExpr u_0 = ctx.mkIntConst("politoCache_"+politoCache+"_u_0");

        FuncDecl isInternalNode = ctx.mkFuncDecl(politoCache+"_isInternalNode", nctx.node, ctx.mkBoolSort());
        FuncDecl isInCache = ctx.mkFuncDecl(politoCache+"_isInCache", new Sort[]{ctx.mkIntSort(),ctx.mkIntSort()}, ctx.mkBoolSort());

        assert(internalNodes.length!=0); //No internal nodes => Should never happen

        //Modeling the behavior of the isInternalNode() and isInCache() functions
        BoolExpr[] internalNodesConstraint = new BoolExpr[internalNodes.length];
        for(int w=0;w<internalNodesConstraint.length;w++)
            internalNodesConstraint[w]= (ctx.mkEq(a_0,internalNodes[w].getZ3Node()));

        //Constraint1 if(isInternalNode(a_0) == or(listadeinodiinterni) ? True : false
        constraints.add(
                ctx.mkForall(new Expr[]{a_0},
                        ctx.mkIff((BoolExpr)isInternalNode.apply(a_0), ctx.mkOr(internalNodesConstraint)),1,null,null,null,null));

        //    constraints.add(ctx.mkForall(new Expr[]{a_0}, ctx.mkEq(isInternalNode.apply(a_0),ctx.mkOr(internalNodesConstraint)),1,null,null,null,null));

        //    constraints.add(ctx.mkForall(new Expr[]{u_0, t_0},
        //                     ctx.mkITE(ctx.mkExists(new Expr[]{t_1, t_2, p_1, p_2, n_1, n_2},
        //                        ctx.mkAnd(ctx.mkLt(t_1, t_2),
        //                                ctx.mkLt(t_1, t_0),
        //                                ctx.mkLt(t_2, t_0),
        //                                (BoolExpr)nctx.recv.apply(n_1, politoCache, p_1, t_1),
        //                                (BoolExpr)nctx.recv.apply(n_2, politoCache, p_2, t_2),
        //                                ctx.mkEq(nctx.pf.get("proto").apply(p_1),ctx.mkInt(nctx.HTTP_REQUEST)),
        //                                ctx.mkEq(nctx.pf.get("proto").apply(p_2),ctx.mkInt(nctx.HTTP_RESPONSE)),
        //                                (BoolExpr)isInternalNode.apply(n_1),
        //                                ctx.mkNot((BoolExpr)isInternalNode.apply(n_2)),
        //                                ctx.mkEq(nctx.pf.get("url").apply(p_1),u_0),
        //                                ctx.mkEq(nctx.pf.get("url").apply(p_2),u_0)),1,null,null,null,null),
        //                     ctx.mkEq(isInCache.apply(u_0,t_0),ctx.mkBool(true)),ctx.mkEq(isInCache.apply(u_0,t_0),ctx.mkBool(false))),1,null,null,null,null));
        //

        //Constraint2 isInCache(u_0,t_0), exist t_1, t_2, p_1, p_2, n_1, n_2 :
        //    (   t_1< t_2 < t_0 && recv(n_1, politoCache, p_1, t_1) && recv(n_2, politoCache, p_2, t_2))) &&
        //        p_1.proto == HTTP_REQ &&  p_2.proto == HTTP_RESP &&
        //        isInternalNode(n_1) && !isInternalNode(n_2) &&
        //        p_1.url == u_0 &&  p_2.url == u_0 )
        constraints.add(
                ctx.mkForall(new Expr[]{u_0, t_0},
                        ctx.mkImplies((BoolExpr)isInCache.apply(u_0, t_0),
                                ctx.mkExists(new Expr[]{t_1,t_2,p_1,p_2,n_1, n_2},
                                        ctx.mkAnd(
                                                ctx.mkLt(t_1, t_2),
                                                ctx.mkLt(t_1, t_0),
                                                ctx.mkLt(t_2, t_0),
                                                (BoolExpr)nctx.recv.apply(n_1, politoCache, p_1, t_1),
                                                (BoolExpr)nctx.recv.apply(n_2, politoCache, p_2, t_2),
                                                ctx.mkEq(nctx.pf.get("proto").apply(p_1), ctx.mkInt(nctx.HTTP_REQUEST)),
                                                ctx.mkEq(nctx.pf.get("proto").apply(p_2), ctx.mkInt(nctx.HTTP_RESPONSE)),
                                                (BoolExpr)isInternalNode.apply(n_1),
                                                ctx.mkNot((BoolExpr)isInternalNode.apply(n_2)),
                                                ctx.mkEq(nctx.pf.get("url").apply(p_1), u_0),
                                                ctx.mkEq(nctx.pf.get("url").apply(p_2), u_0)),1,null,null,null,null)),1,null,null,null,null));
        //    //Always in cache
        //    constraints.add(ctx.mkForall(new Expr[]{u_0, t_0},ctx.mkEq(isInCache.apply(u_0,t_0),ctx.mkBool(true)),1,null,null,null,null));

        //Constraint3 Modeling the behavior of the cache
        //send(politoCache, n_0, p, t_0) && !isInternalNode(n_0) ->
        //    (exist t_1,n_1 :
        //        (t_1 < t_0 && recv(n_1, politoCache, p, t_1) &&
        //         p.proto == HTTP_REQ &&  !isInCache(p.url,t_0))
        constraints.add(
                ctx.mkForall(new Expr[]{n_0,p_0, t_0},
                        ctx.mkImplies(
                                ctx.mkAnd((BoolExpr)nctx.send.apply(politoCache,n_0,p_0,t_0),ctx.mkNot((BoolExpr)isInternalNode.apply(n_0))),
                                ctx.mkAnd(ctx.mkExists(new Expr[]{t_1, n_1},
                                        ctx.mkAnd(
                                                ctx.mkLt(t_1, t_0),
                                                (BoolExpr)isInternalNode.apply(n_1),
                                                (BoolExpr)nctx.recv.apply(n_1, politoCache, p_0, t_1)),1,null,null,null,null),
                                        ctx.mkEq(nctx.pf.get("proto").apply(p_0), ctx.mkInt(nctx.HTTP_REQUEST)),
                                        ctx.mkNot((BoolExpr)isInCache.apply(nctx.pf.get("url").apply(p_0), t_0)))),1,null,null,null,null));

        //Constraint4 send(politoCache, n_0, p, t_0) && isInternalNode(n_0) ->
        //    (exist p_1,t_1 :
        //        (t_1 < t_0 && recv(n_0, politoCache, p_1, t_1) &&
        //         p_1.proto == HTTP_REQ && p.proto == HTTP_RESP &&
        //         p_1.url == p.url &&  p.src == p_1.dest && p.dest==p_1.src
        //         && isInCache(p.url,t_0))
        constraints.add(
                ctx.mkForall(new Expr[]{n_0,p_0, t_0},
                        ctx.mkImplies(
                                ctx.mkAnd((BoolExpr)nctx.send.apply(politoCache,n_0,p_0,t_0),(BoolExpr)isInternalNode.apply(n_0)),
                                ctx.mkAnd(ctx.mkExists(new Expr[]{p_1, t_1},
                                        ctx.mkAnd(
                                                ctx.mkLt(t_1, t_0),
                                                (BoolExpr)nctx.recv.apply(n_0, politoCache, p_1, t_1),
                                                ctx.mkEq(nctx.pf.get("proto").apply(p_1), ctx.mkInt(nctx.HTTP_REQUEST)),
                                                ctx.mkEq(nctx.pf.get("url").apply(p_1), nctx.pf.get("url").apply(p_0))),1,null,null,null,null),
                                        ctx.mkEq(nctx.pf.get("proto").apply(p_0), ctx.mkInt(nctx.HTTP_RESPONSE)),
                                        ctx.mkEq(nctx.pf.get("src").apply(p_0), nctx.pf.get("dest").apply(p_1)),
                                        ctx.mkEq(nctx.pf.get("dest").apply(p_0), nctx.pf.get("src").apply(p_1)),
                                        (BoolExpr)isInCache.apply(nctx.pf.get("url").apply(p_0), t_0))),1,null,null,null,null));
    }
}