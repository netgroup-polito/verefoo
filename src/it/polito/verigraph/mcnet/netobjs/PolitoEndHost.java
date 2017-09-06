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
import it.polito.verigraph.mcnet.netobjs.PacketModel;

public class PolitoEndHost extends NetworkObject {

    List<BoolExpr> constraints = new ArrayList<BoolExpr>();
    Context ctx;
    DatatypeExpr politoEndHost;
    Network net;
    NetContext nctx;

    public PolitoEndHost(Context ctx, Object[]... args) {
        super(ctx, args);
    }

    @Override
    public DatatypeExpr getZ3Node() {
        return politoEndHost;
    }

    @Override
    protected void init(Context ctx, Object[]... args) {
        this.ctx = ctx;
        this.isEndHost = true;
        this.politoEndHost = this.z3Node = ((NetworkObject)args[0][0]).getZ3Node();
        this.net = (Network)args[0][1];
        this.nctx = (NetContext)args[0][2];
    }

    @Override
    protected void addConstraints(Solver solver) {
        BoolExpr[] constr = new BoolExpr[constraints.size()];
        solver.add(constraints.toArray(constr));
    }

    /*
     * Fields that can be configured -> "dest","body","seq","proto","emailFrom","url","options"
     */
    public void installEndHost (PacketModel packet){
        //System.out.println("Installing PolitoEndHost...");
        Expr n_0 = ctx.mkConst("PolitoEndHost_"+politoEndHost+"_n_0", nctx.node);
        Expr p_0 = ctx.mkConst("PolitoEndHost_"+politoEndHost+"_p_0", nctx.packet);
        IntExpr t_0 = ctx.mkIntConst("PolitoEndHost_"+politoEndHost+"_t_0");
        BoolExpr predicatesOnPktFields = ctx.mkTrue();

        if(packet.getIp_dest() != null)
            predicatesOnPktFields = ctx.mkAnd(predicatesOnPktFields, ctx.mkEq(nctx.pf.get("dest").apply(p_0), packet.getIp_dest()));
        if(packet.getBody() != null)
            predicatesOnPktFields = ctx.mkAnd(predicatesOnPktFields, ctx.mkEq(nctx.pf.get("body").apply(p_0), ctx.mkInt(packet.getBody())));
        if(packet.getEmailFrom() != null)
            predicatesOnPktFields = ctx.mkAnd(predicatesOnPktFields, ctx.mkEq(nctx.pf.get("emailFrom").apply(p_0), ctx.mkInt(packet.getEmailFrom())));
        if(packet.getOptions() != null)
            predicatesOnPktFields = ctx.mkAnd(predicatesOnPktFields, ctx.mkEq(nctx.pf.get("options").apply(p_0), ctx.mkInt(packet.getOptions())));
        if(packet.getProto() != null)
            predicatesOnPktFields = ctx.mkAnd(predicatesOnPktFields, ctx.mkEq(nctx.pf.get("proto").apply(p_0), ctx.mkInt(packet.getProto())));
        if(packet.getSeq() != null)
            predicatesOnPktFields = ctx.mkAnd(predicatesOnPktFields, ctx.mkEq(nctx.pf.get("seq").apply(p_0), ctx.mkInt(packet.getSeq())));
        if(packet.getUrl() != null)
            predicatesOnPktFields = ctx.mkAnd(predicatesOnPktFields, ctx.mkEq(nctx.pf.get("url").apply(p_0), ctx.mkInt(packet.getUrl())));

        //Constraint1 send(politoWebClient, n_0, p, t_0) -> p.origin == politoWebClient && p.orig_body == p.body && nodeHasAddr(politoWebClient,p.src)
        constraints.add( ctx.mkForall(new Expr[]{n_0, p_0, t_0},
                ctx.mkImplies((BoolExpr)nctx.send.apply(politoEndHost, n_0, p_0, t_0),
                        ctx.mkAnd(predicatesOnPktFields,
                                ctx.mkEq(nctx.pf.get("orig_body").apply(p_0),nctx.pf.get("body").apply(p_0)),
                                ctx.mkEq(nctx.pf.get("origin").apply(p_0),politoEndHost),
                                (BoolExpr)nctx.nodeHasAddr.apply(politoEndHost,nctx.pf.get("src").apply(p_0)))),1,null,null,null,null));

        //Constraint2 recv(n_0, politoWebClient, p, t_0) -> nodeHasAddr(politoWebClient,p.dest)
        constraints.add( ctx.mkForall(new Expr[]{n_0, p_0, t_0},
                ctx.mkImplies((BoolExpr)nctx.recv.apply(n_0,politoEndHost, p_0, t_0),
                        (BoolExpr)nctx.nodeHasAddr.apply(politoEndHost,nctx.pf.get("dest").apply(p_0))),1,null,null,null,null));

        //System.out.println("Done.");


        return;
    }

}
