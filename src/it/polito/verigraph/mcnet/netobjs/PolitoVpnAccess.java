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

public class PolitoVpnAccess extends NetworkObject {

    List<BoolExpr> constraints = new ArrayList<BoolExpr>();
    DatatypeExpr politoVpnAccess;
    FuncDecl private_addr_func;
    NetContext nctx;
    Context ctx;
    Network net;

    public PolitoVpnAccess(Context ctx, Object[]... args) {
        super(ctx, args);
    }

    @Override
    public DatatypeExpr getZ3Node() {
        return politoVpnAccess;
    }

    @Override
    protected void init(Context ctx, Object[]... args) {
        this.ctx = ctx;
        this.isEndHost = false;
        this.politoVpnAccess = this.z3Node = ((NetworkObject)args[0][0]).getZ3Node();
        this.net = (Network)args[0][1];
        this.nctx = (NetContext)args[0][2];
    }

    @Override
    protected void addConstraints(Solver solver) {
        BoolExpr[] constr = new BoolExpr[constraints.size()];
        solver.add(constraints.toArray(constr));
    }

    public void vpnAccessModel(DatatypeExpr vpnAccessIp, DatatypeExpr vpnExitIp) {
        Expr x = ctx.mkConst("vpn_x", nctx.node);
        Expr y = ctx.mkConst("vpn_y", nctx.node);

        Expr p_0 = ctx.mkConst("vpn_p_0", nctx.packet);
        Expr p_1 = ctx.mkConst("vpn_p_1", nctx.packet);

        IntExpr t_0 = ctx.mkIntConst("vpn_t_0");
        IntExpr t_1 = ctx.mkIntConst("vpn_t_1");

        private_addr_func = ctx.mkFuncDecl("vpn_private_addr_func", nctx.address, ctx.mkBoolSort());

        BoolExpr constraint1 = ctx.mkForall(new Expr[]{t_0, p_0, x},
                ctx.mkImplies(ctx.mkAnd(
                        (BoolExpr)nctx.send.apply(politoVpnAccess, x, p_0, t_0),
                        ctx.mkEq(nctx.pf.get("inner_src").apply(p_0), nctx.am.get("null"))),
                        ctx.mkAnd(
                                (BoolExpr)private_addr_func.apply(nctx.pf.get("dest").apply(p_0)),
                                ctx.mkNot((BoolExpr)nctx.pf.get("encrypted").apply(p_0)),
                                ctx.mkExists(new Expr[]{y, p_1, t_1},
                                        ctx.mkAnd((BoolExpr)nctx.recv.apply(y, politoVpnAccess, p_1, t_1),
                                                ctx.mkLt(t_1, t_0),
                                                (BoolExpr)nctx.pf.get("encrypted").apply(p_1),
                                                ctx.mkEq(nctx.pf.get("src").apply(p_1), vpnExitIp),
                                                ctx.mkEq(nctx.pf.get("dest").apply(p_1), vpnAccessIp),
                                                ctx.mkEq(nctx.pf.get("inner_src").apply(p_1), nctx.pf.get("src").apply(p_0)),
                                                ctx.mkEq(nctx.pf.get("inner_dest").apply(p_1), nctx.pf.get("dest").apply(p_0)),
                                                ctx.mkEq(nctx.pf.get("origin").apply(p_1), nctx.pf.get("origin").apply(p_0)),
                                                ctx.mkEq(nctx.pf.get("orig_body").apply(p_1), nctx.pf.get("orig_body").apply(p_0)),
                                                ctx.mkEq(nctx.pf.get("body").apply(p_1), nctx.pf.get("body").apply(p_0)),
                                                ctx.mkEq(nctx.pf.get("seq").apply(p_1), nctx.pf.get("seq").apply(p_0)),
                                                ctx.mkEq(nctx.pf.get("proto").apply(p_1), nctx.pf.get("proto").apply(p_0)),
                                                ctx.mkEq(nctx.pf.get("emailFrom").apply(p_1), nctx.pf.get("emailFrom").apply(p_0)),
                                                ctx.mkEq(nctx.pf.get("url").apply(p_1), nctx.pf.get("url").apply(p_0)),
                                                ctx.mkEq(nctx.pf.get("options").apply(p_1), nctx.pf.get("options").apply(p_0))), 1, null, null, null, null))),
                1,null,null,null,null);

        constraints.add(constraint1);

        BoolExpr constraint2 = ctx.mkForall(new Expr[]{t_0, p_0, x},
                ctx.mkImplies(ctx.mkAnd(
                        (BoolExpr)nctx.send.apply(politoVpnAccess, x, p_0, t_0),
                        ctx.mkNot(ctx.mkEq(nctx.pf.get("inner_src").apply(p_0), nctx.am.get("null")))),
                        ctx.mkAnd(
                                ctx.mkEq(nctx.pf.get("src").apply(p_0), vpnAccessIp),
                                ctx.mkEq(nctx.pf.get("dest").apply(p_0), vpnExitIp),
                                (BoolExpr)private_addr_func.apply(nctx.pf.get("inner_src").apply(p_0)),
                                ctx.mkNot(ctx.mkEq(nctx.pf.get("inner_dest").apply(p_0), vpnAccessIp)),
                                (BoolExpr)nctx.pf.get("encrypted").apply(p_0),
                                ctx.mkExists(new Expr[]{y, p_1, t_1},
                                        ctx.mkAnd((BoolExpr)nctx.recv.apply(y, politoVpnAccess, p_1, t_1),
                                                ctx.mkLt(t_1, t_0),
                                                ctx.mkNot((BoolExpr)nctx.pf.get("encrypted").apply(p_1)),
                                                ctx.mkEq(nctx.pf.get("src").apply(p_1), nctx.pf.get("inner_src").apply(p_0)),
                                                ctx.mkEq(nctx.pf.get("dest").apply(p_1), nctx.pf.get("inner_dest").apply(p_0)),
                                                ctx.mkEq(nctx.pf.get("inner_src").apply(p_1), nctx.am.get("null")),
                                                ctx.mkEq(nctx.pf.get("inner_dest").apply(p_1), nctx.am.get("null")),
                                                ctx.mkEq(nctx.pf.get("origin").apply(p_1), nctx.pf.get("origin").apply(p_0)),
                                                ctx.mkEq(nctx.pf.get("orig_body").apply(p_1), nctx.pf.get("orig_body").apply(p_0)),
                                                ctx.mkEq(nctx.pf.get("body").apply(p_1), nctx.pf.get("body").apply(p_0)),
                                                ctx.mkEq(nctx.pf.get("seq").apply(p_1), nctx.pf.get("seq").apply(p_0)),
                                                ctx.mkEq(nctx.pf.get("proto").apply(p_1), nctx.pf.get("proto").apply(p_0)),
                                                ctx.mkEq(nctx.pf.get("emailFrom").apply(p_1), nctx.pf.get("emailFrom").apply(p_0)),
                                                ctx.mkEq(nctx.pf.get("url").apply(p_1), nctx.pf.get("url").apply(p_0)),
                                                ctx.mkEq(nctx.pf.get("options").apply(p_1), nctx.pf.get("options").apply(p_0))), 1, null, null, null, null))),
                1,null,null,null,null);

        constraints.add(constraint2);
    }

    public void setInternalAddress(ArrayList<DatatypeExpr> internalAddress){
        List<BoolExpr> constr = new ArrayList<BoolExpr>();
        Expr n_0 = ctx.mkConst("vpn_node", nctx.address);

        for(DatatypeExpr n : internalAddress){
            constr.add(ctx.mkEq(n_0,n));
        }
        BoolExpr[] constrs = new BoolExpr[constr.size()];
        //Constraintprivate_addr_func(n_0) == or(n_0==n foreach internal address)
        constraints.add(ctx.mkForall(new Expr[]{n_0}, ctx.mkEq(private_addr_func.apply(n_0),ctx.mkOr(constr.toArray(constrs))),1,null,null,null,null));
    }

}