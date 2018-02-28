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
import com.microsoft.z3.Optimize;
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
    int[] blacklist;
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
        net.saneSend(this);
    }

    @Override
    public DatatypeExpr getZ3Node() {
        return politoAntispam;
    }

    @Override
    protected void addConstraints(Optimize solver) {
        BoolExpr[] constr = new BoolExpr[constraints.size()];
        solver.Add(constraints.toArray(constr));
        blacklistConstraints(solver);
    }

    public void installAntispam (int[] blackList){
    	  Expr n_0 = ctx.mkConst(politoAntispam+"_n_0", nctx.node);
          Expr n_1 = ctx.mkConst(politoAntispam+"_n_1", nctx.node);
          Expr p_0 = ctx.mkConst(politoAntispam+"_p_0", nctx.packet);
          //IntExpr t_0 = ctx.mkIntConst(politoAntispam+"_t_0");
          //IntExpr t_1 = ctx.mkIntConst(politoAntispam+"_t_1");
         
          isInBlacklist = ctx.mkFuncDecl(politoAntispam+"_isInBlacklist", ctx.mkIntSort(), ctx.mkBoolSort());
          addBlackList(blackList);

          constraints.add(
                  ctx.mkForall(new Expr[]{n_0, p_0}, 
                          ctx.mkImplies(
                        		  ctx.mkAnd( (BoolExpr)nctx.send.apply(new Expr[]{ politoAntispam, n_0, p_0}),
                        				  		ctx.mkOr(ctx.mkEq(nctx.pf.get("proto").apply(p_0), ctx.mkInt(nctx.POP3_REQUEST))
                        				  				,ctx.mkEq(nctx.pf.get("proto").apply(p_0), ctx.mkInt(nctx.POP3_RESPONSE))
                        				  				 )),
                                  ctx.mkNot((BoolExpr)isInBlacklist.apply(nctx.pf.get("emailFrom").apply(p_0)))
                                          ),1,null,null,null,null));
          
          constraints.add( ctx.mkForall(new Expr[]{n_0, p_0},
                  ctx.mkImplies(ctx.mkAnd((BoolExpr)nctx.send.apply(politoAntispam, n_0, p_0)),
                          ctx.mkAnd(ctx.mkExists(new Expr[]{n_1},
                                  ctx.mkAnd((BoolExpr)nctx.recv.apply(n_1, politoAntispam, p_0)),1,null,null,null,null)
                                  )),1,null,null,null,null));
      
         
          
    }

    public void addBlackList(int[] list){
        this.blacklist = list;
    }

    //Constraint2 isInBlacklist(a_0) == or(foreach emailFrom in blacklist (a_0 == emailFrom))
    private void blacklistConstraints(Optimize  solver){
        if (blacklist.length == 0)
            return;
        Expr a_0 = ctx.mkConst(politoAntispam+"_blacklist_a_0", ctx.getIntSort());
        BoolExpr[] blacklist_map = new BoolExpr[blacklist.length];
        for(int y=0;y<blacklist.length;y++){
            blacklist_map[y] = ctx.mkOr(ctx.mkEq(a_0,ctx.mkInt(blacklist[y])));
        }

        solver.Add(ctx.mkForall(new Expr[]{a_0},
                ctx.mkEq( 
                        isInBlacklist.apply(a_0),
                        ctx.mkOr(blacklist_map)),1,null,null,null,null));
    }
}