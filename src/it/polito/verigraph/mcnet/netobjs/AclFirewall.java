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
import it.polito.verigraph.mcnet.components.Tuple;

/** Represents a Firewall with the associated Access Control List
 *
 */
public class AclFirewall extends NetworkObject{

    List<BoolExpr> constraints;
    Context ctx;
    DatatypeExpr fw;
    ArrayList<Tuple<DatatypeExpr,DatatypeExpr>> acls;
    Network net;
    NetContext nctx;
    FuncDecl acl_func;

    public AclFirewall(Context ctx, Object[]... args) {
        super(ctx, args);
    }

    @Override
    protected void init(Context ctx, Object[]... args) {
        this.ctx = ctx;
        isEndHost=false;
        constraints = new ArrayList<BoolExpr>();
        acls = new ArrayList<Tuple<DatatypeExpr,DatatypeExpr>>();
        z3Node = ((NetworkObject)args[0][0]).getZ3Node();
        fw = z3Node;
        net = (Network)args[0][1];
        nctx = (NetContext)args[0][2];
        net.saneSend(this);
        firewallSendRules();

    }

    /**
     * Wrap add acls
     * @param policy
     */
    public void setPolicy(ArrayList<Tuple<DatatypeExpr, DatatypeExpr>> policy){
        addAcls(policy);
    }

    public void addAcls(ArrayList<Tuple<DatatypeExpr,DatatypeExpr>> acls){
        this.acls.addAll(acls);
    }

    @Override
    public DatatypeExpr getZ3Node() {
        return fw;
    }


    @Override
    protected void addConstraints(Solver solver) {
        BoolExpr[] constr = new BoolExpr[constraints.size()];
        solver.add(constraints.toArray(constr));
        aclConstraints(solver);
    }

    private void firewallSendRules (){
        Expr p_0 = ctx.mkConst(fw+"_firewall_send_p_0", nctx.packet);
        Expr n_0 = ctx.mkConst(fw+"_firewall_send_n_0", nctx.node);
        Expr n_1 = ctx.mkConst(fw+"_firewall_send_n_1", nctx.node);
        IntExpr t_0 = ctx.mkIntConst(fw+"_firewall_send_t_0");
        IntExpr t_1 = ctx.mkIntConst(fw+"_firewall_send_t_1");
        acl_func = ctx.mkFuncDecl(fw+"_acl_func", new Sort[]{nctx.address, nctx.address},ctx.mkBoolSort());

        //Constraint1send(fw, n_0, p, t_0)  -> (exist n_1,t_1 : (recv(n_1, fw, p, t_1) &&
        //    t_1 < t_0 && !acl_func(p.src,p.dest))
        constraints.add(
                ctx.mkForall(new Expr[]{n_0, p_0, t_0},
                        ctx.mkImplies(
                                (BoolExpr)nctx.send.apply(new Expr[]{ fw, n_0, p_0, t_0}),
                                ctx.mkExists(new Expr[]{t_1},
                                        ctx.mkAnd(ctx.mkLt(t_1,t_0),
                                                ctx.mkExists(new Expr[]{n_1},
                                                        nctx.recv.apply(n_1, fw, p_0, t_1),1,null,null,null,null),
                                                ctx.mkNot((BoolExpr)acl_func.apply(nctx.pf.get("src").apply(p_0), nctx.pf.get("dest").apply(p_0)))),1,null,null,null,null)),1,null,null,null,null));

    }

    private void aclConstraints(Solver solver){
        if (acls.size() == 0)
            return;
        Expr a_0 = ctx.mkConst(fw+"_firewall_acl_a_0", nctx.address);
        Expr a_1 = ctx.mkConst(fw+"_firewall_acl_a_1", nctx.address);
        BoolExpr[] acl_map = new BoolExpr[acls.size()];
        for(int y=0;y<acls.size();y++){
            Tuple<DatatypeExpr,DatatypeExpr> tp = acls.get(y);
            acl_map[y] = ctx.mkOr(ctx.mkAnd(ctx.mkEq(a_0,tp._1),ctx.mkEq(a_1,tp._2)), ctx.mkAnd(ctx.mkEq(a_0,tp._2),ctx.mkEq(a_1,tp._1)));
        }
        //Constraint2acl_func(a_0,a_1) == or(foreach ip1,ip2 in acl_map ((a_0 == ip1 && a_1 == ip2)||(a_0 == ip2 && a_1 == ip1)))
        solver.add(ctx.mkForall(new Expr[]{a_0, a_1},
                ctx.mkEq(
                        acl_func.apply(a_0, a_1),
                        ctx.mkOr(acl_map)),1,null,null,null,null));
    }
}