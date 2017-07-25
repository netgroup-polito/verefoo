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
 * Model of an anti-spam node
 *
 */
public class PolitoAntispam extends NetworkObject{

    List<BoolExpr> constraints;
    Context ctx;
    DatatypeExpr politoAntispam;
    Network net;
    NetContext nctx;
    FuncDecl isInBlacklist;

    public PolitoAntispam(Context ctx, Object[]... args) {
        super(ctx, args);
    }

    @Override
    protected void init(Context ctx, Object[]... args) {
        this.ctx = ctx;
        isEndHost=false;
        constraints = new ArrayList<BoolExpr>();
        z3Node = ((NetworkObject)args[0][0]).getZ3Node();
        politoAntispam = z3Node;
        net = (Network)args[0][1];
        nctx = (NetContext)args[0][2];
        //net.saneSend(this);
    }

    @Override
    public DatatypeExpr getZ3Node() {
        return politoAntispam;
    }

    @Override
    protected void addConstraints(Solver solver) {
        BoolExpr[] constr = new BoolExpr[constraints.size()];
        solver.add(constraints.toArray(constr));
    }

    public void installAntispam (int[] blackList){
        Expr n_0 = ctx.mkConst(politoAntispam+"_n_0", nctx.node);
        Expr n_1 = ctx.mkConst(politoAntispam+"_n_1", nctx.node);
        Expr p_0 = ctx.mkConst(politoAntispam+"_p_0", nctx.packet);
        IntExpr t_0 = ctx.mkIntConst(politoAntispam+"_t_0");
        IntExpr t_1 = ctx.mkIntConst(politoAntispam+"_t_1");
        IntExpr ef_0 = ctx.mkIntConst(politoAntispam+"_ef_0");

        isInBlacklist = ctx.mkFuncDecl(politoAntispam+"_isInBlacklist", ctx.mkIntSort(), ctx.mkBoolSort());
        BoolExpr[] blConstraint = new BoolExpr[blackList.length];
        if(blackList.length != 0){
            for (int i=0;i<blackList.length;i++)
                blConstraint[i]=(ctx.mkEq(ef_0,ctx.mkInt(blackList[i])));
            //Constraint1aif(isInBlackList(ef_0) == or(for bl in blacklist (ef_0==bl)) ? true : false
            constraints.add(ctx.mkForall(new Expr[]{ef_0}, ctx.mkIff((BoolExpr)isInBlacklist.apply(ef_0),ctx.mkOr(blConstraint)),1,null,null,null,null));
        }else{
            //Constraint1bisInblackList(ef_0) == false
            constraints.add(ctx.mkForall(new Expr[]{ef_0}, ctx.mkEq((BoolExpr)isInBlacklist.apply(ef_0), ctx.mkBool(false)),1,null,null,null,null));
        }

        //Constraint2 send(politoAntispam, n_0, p, t_0) && p.proto(POP3_RESP) ->
        //(exist  n_1,t_1 : (recv(n_1, politoAntispam, p, t_1) && t_1 < t_0)) && !isInBlackList(p.emailFrom)
        constraints.add( ctx.mkForall(new Expr[]{n_0, p_0, t_0},
                ctx.mkImplies(ctx.mkAnd((BoolExpr)nctx.send.apply(politoAntispam, n_0, p_0, t_0), ctx.mkEq(nctx.pf.get("proto").apply(p_0), ctx.mkInt(nctx.POP3_RESPONSE))),
                        ctx.mkAnd(ctx.mkExists(new Expr[]{n_1, t_1},
                                ctx.mkAnd((BoolExpr)nctx.recv.apply(n_1, politoAntispam, p_0, t_1), ctx.mkLt(t_1 , t_0)),1,null,null,null,null),
                                ctx.mkNot((BoolExpr)isInBlacklist.apply(nctx.pf.get("emailFrom").apply(p_0))))),1,null,null,null,null));

        //Constraint3 send(politoAntispam, n_0, p, t_0) && p.proto(POP3_REQ) ->
        //(exist n_1,t_1 : (recv(n_1, politoAntispam, p, t_1) && t_1 < t_0))
        constraints.add( ctx.mkForall(new Expr[]{n_0, p_0, t_0},
                ctx.mkImplies(ctx.mkAnd((BoolExpr)nctx.send.apply(politoAntispam, n_0, p_0, t_0), ctx.mkEq(nctx.pf.get("proto").apply(p_0), ctx.mkInt(nctx.POP3_REQUEST))),
                        ctx.mkAnd(ctx.mkExists(new Expr[]{n_1, t_1},
                                ctx.mkAnd((BoolExpr)nctx.recv.apply(n_1, politoAntispam, p_0, t_1),
                                        ctx.mkLt(t_1 , t_0)),1,null,null,null,null))),1,null,null,null,null));

        //Constraint4 send(politoAntispam, politoErrFunction, p, t_0) ->
        //            (exist n_1,t_1 : (recv(n_1, politoAntispam, p, t_1) && t_1 < t_0 && p.emailFrom ==1))
        constraints.add( ctx.mkForall(new Expr[]{n_0, p_0, t_0},
                ctx.mkImplies((BoolExpr)nctx.send.apply(politoAntispam, nctx.nm.get("politoErrFunction").getZ3Node(), p_0, t_0),
                        ctx.mkAnd(ctx.mkExists(new Expr[]{n_1, t_1},
                                ctx.mkAnd((BoolExpr)nctx.recv.apply(n_1, politoAntispam, p_0, t_1),
                                        ctx.mkLt(t_1 , t_0),
                                        ctx.mkEq(nctx.pf.get("emailFrom").apply(p_0),ctx.mkInt(1))),1,null,null,null,null))),1,null,null,null,null));

        //Constraint5 send(politoAntispam, n_0, p, t_0) -> p.proto == POP_REQ || p.protpo == POP_RESP
        constraints.add( ctx.mkForall(new Expr[]{n_0, p_0, t_0},
                ctx.mkImplies((BoolExpr)nctx.send.apply(politoAntispam, n_0, p_0, t_0),
                        ctx.mkOr(ctx.mkEq(nctx.pf.get("proto").apply(p_0), ctx.mkInt(nctx.POP3_REQUEST)),
                                ctx.mkEq(nctx.pf.get("proto").apply(p_0), ctx.mkInt(nctx.POP3_RESPONSE)))),1,null,null,null,null));

        //Constraint6 send(politoAntispam, n_0, p, t_0) -> nodeHasAddr(politoAntispam,p.src)
        constraints.add( ctx.mkForall(new Expr[]{n_0, p_0, t_0},
                ctx.mkImplies((BoolExpr)nctx.send.apply(politoAntispam, n_0, p_0, t_0),
                        ctx.mkNot((BoolExpr)nctx.nodeHasAddr.apply(politoAntispam,nctx.pf.get("src").apply(p_0)))),1,null,null,null,null));
    }
}