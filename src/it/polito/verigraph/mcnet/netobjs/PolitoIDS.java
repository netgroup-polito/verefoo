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

public class PolitoIDS extends NetworkObject {

    public static final int DROGA = 1; //no go
    public static final int GATTINI = 2;  //go

    Context ctx;
    List<BoolExpr> constraints = new ArrayList<BoolExpr>();
    DatatypeExpr politoIDS;
    Network net;
    NetContext nctx;
    FuncDecl isInBlacklist;


    public PolitoIDS(Context ctx, Object[]...args){
        super(ctx, args);
    }

    @Override
    public DatatypeExpr getZ3Node() {
        return politoIDS;
    }

    @Override
    protected void init(Context ctx, Object[]... args) {

        this.ctx = ctx;
        this.isEndHost = false;
        this.politoIDS = this.z3Node = ((NetworkObject)args[0][0]).getZ3Node();
        this.net = (Network)args[0][1];
        this.nctx = (NetContext)args[0][2];

    }

    @Override
    protected void addConstraints(Solver solver) {
        BoolExpr[] constr = new BoolExpr[constraints.size()];
        solver.add(constraints.toArray(constr));

    }

    public void installIDS(int[] blackList){
        Expr n_0 = ctx.mkConst(politoIDS + "_n_0", nctx.node);
        Expr n_1 = ctx.mkConst(politoIDS + "_n_1", nctx.node);
        Expr p_0 = ctx.mkConst(politoIDS + "_p_0", nctx.packet);
        IntExpr t_0 = ctx.mkIntConst(politoIDS + "_t_0");
        IntExpr t_1 = ctx.mkIntConst(politoIDS + "_t_1");
        Expr b_0 = ctx.mkIntConst(politoIDS + "_b_0");

        isInBlacklist = ctx.mkFuncDecl(politoIDS + "_isInBlacklist", ctx.mkIntSort(), ctx.mkBoolSort());


        BoolExpr[] blConstraints = new BoolExpr[blackList.length];
        if(blackList.length != 0){

            for(int i = 0; i<blackList.length; i++)
                blConstraints[i] = ctx.mkEq(b_0, ctx.mkInt(blackList[i]));

            this.constraints.add(ctx.mkForall(new Expr[]{b_0},
                    ctx.mkIff((BoolExpr)isInBlacklist.apply(b_0), ctx.mkOr(blConstraints)),
                    1,
                    null, null, null, null));

        }else{
            this.constraints.add(ctx.mkForall(new Expr[]{b_0},
                    ctx.mkEq(isInBlacklist.apply(b_0), ctx.mkBool(false)),
                    1,
                    null, null, null, null));
        }

        //Constraint2 send(politoIDS, n_0, p, t_0) && (p.proto(HTTP_RESPONSE) || p.proto(HTTP_REQUEST)) ->
        //(exist  n_1,t_1 : (recv(n_1, politoIDS, p, t_1) && t_1 < t_0)) && !isInBlackList(p.body)

        this.constraints.add(ctx.mkForall(new Expr[]{n_0, p_0, t_0},
                ctx.mkImplies(ctx.mkAnd((BoolExpr)nctx.send.apply(politoIDS, n_0, p_0, t_0), ctx.mkOr(ctx.mkEq(nctx.pf.get("proto").apply(p_0), ctx.mkInt(nctx.HTTP_RESPONSE)), ctx.mkEq(nctx.pf.get("proto").apply(p_0), ctx.mkInt(nctx.HTTP_REQUEST)))),
                        ctx.mkAnd(ctx.mkExists(new Expr[]{n_1,t_1},
                                ctx.mkAnd((BoolExpr)nctx.recv.apply(n_1,politoIDS,p_0,t_1),ctx.mkLt(t_1, t_0)),
                                1,
                                null, null, null, null),
                                ctx.mkNot((BoolExpr)isInBlacklist.apply(nctx.pf.get("body").apply(p_0))))),
                1,
                null, null, null, null));

        //Constraint3 send(politoIDS, n_0, p, t_0) && p.proto(HTTP_REQUEST) ->
        //(exist n_1,t_1 : (recv(n_1, politoIDS, p, t_1) && t_1 < t_0)) Constraint not needed anymore (included in contr. 2)
        /* this.constraints.add(ctx.mkForall(new Expr[]{n_0, p_0, t_0},
              ctx.mkImplies(ctx.mkAnd((BoolExpr)nctx.send.apply(politoIDS, n_0, p_0, t_0), ctx.mkEq(nctx.pf.get("proto").apply(p_0), ctx.mkInt(nctx.HTTP_REQUEST))),
              ctx.mkAnd(ctx.mkExists(new Expr[]{n_1,t_1},
                 ctx.mkAnd((BoolExpr)nctx.recv.apply(n_1,politoIDS,p_0,t_1),ctx.mkLt(t_1, t_0)),
                 1,
                 null, null, null, null))),
              1,
              null, null, null, null));
         */

        //Constraint5send(politoIDS, n_0, p, t_0) -> p.proto == HTTP_REQ || p.protpo == HTTP_RESP

        this.constraints.add(ctx.mkForall(new Expr[]{n_0, p_0, t_0},
                ctx.mkImplies((BoolExpr)nctx.send.apply(politoIDS, n_0, p_0, t_0),
                        ctx.mkOr(ctx.mkEq(nctx.pf.get("proto").apply(p_0), ctx.mkInt(nctx.HTTP_REQUEST)),
                                ctx.mkEq(nctx.pf.get("proto").apply(p_0), ctx.mkInt(nctx.HTTP_RESPONSE)))),
                1,
                null,null,null,null));

        //Constraint6send(politoIDS, n_0, p, t_0) -> nodeHasAddr(politoIDS,p.src)

        this.constraints.add(ctx.mkForall(new Expr[]{n_0, p_0, t_0},
                ctx.mkImplies((BoolExpr)nctx.send.apply(politoIDS, n_0, p_0, t_0),
                        ctx.mkNot((BoolExpr)nctx.nodeHasAddr.apply(politoIDS,nctx.pf.get("src").apply(p_0)))),
                1,
                null,null,null,null));



    }

}
