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
import java.util.stream.Collectors;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.DatatypeExpr;
import com.microsoft.z3.Expr;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.Optimize;
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
        net.saneSend(this);
        n_0 = ctx.mkConst("PolitoEndHost_"+politoEndHost+"_n_0", nctx.node);
        p_0 = ctx.mkConst("PolitoEndHost_"+politoEndHost+"_p_0", nctx.packet);

        Expr p_1 = ctx.mkConst("PolitoEndHost_"+politoEndHost+"_p_1", nctx.packet);
        neighbours = ((ArrayList<NetworkObject>) args[0][3]);
		List<Expr> recvNeighbours = neighbours.stream().map(n -> nctx.recv.apply(n.getZ3Node(), politoEndHost, p_0)).collect(Collectors.toList());
		BoolExpr[] tmp2 = new BoolExpr[recvNeighbours.size()];
		enumerateRecvP0 = ctx.mkOr(recvNeighbours.toArray(tmp2));
		List<Expr> sendNeighbours = neighbours.stream().map(n -> nctx.send.apply(politoEndHost, n.getZ3Node(), p_0)).collect(Collectors.toList());
		BoolExpr[] tmp3 = new BoolExpr[sendNeighbours.size()];
		enumerateSendP0 = ctx.mkOr(sendNeighbours.toArray(tmp3));
		List<Expr> recvNeighbours2 = neighbours.stream().map(n -> nctx.recv.apply(n.getZ3Node(), politoEndHost, p_1)).collect(Collectors.toList());
		BoolExpr[] tmp4 = new BoolExpr[recvNeighbours2.size()];
		enumerateRecvP1 = ctx.mkOr(recvNeighbours2.toArray(tmp4));
		List<Expr> sendNeighbours2 = neighbours.stream().map(n -> nctx.send.apply(politoEndHost, n.getZ3Node(), p_1)).collect(Collectors.toList());
		BoolExpr[] tmp5 = new BoolExpr[sendNeighbours2.size()];
		enumerateSendP1 = ctx.mkOr(sendNeighbours2.toArray(tmp5));
		
    }

    @Override
    protected void addConstraints(Optimize solver) {
        BoolExpr[] constr = new BoolExpr[constraints.size()];
        solver.Add(constraints.toArray(constr));
    }


    public  void installAsWebServer(PacketModel packet){
        installEndHost(packet);
        Expr p_1 = ctx.mkConst("PolitoEndHost_"+politoEndHost+"_p_1", nctx.packet);

        constraints.add( ctx.mkForall(new Expr[]{p_0},
                ctx.mkImplies(enumerateRecvP0,
                        ctx.mkEq(nctx.pf.get("proto").apply(p_0), ctx.mkInt(nctx.HTTP_REQUEST))
                        ),1,null,null,null,null));

        constraints.add( ctx.mkForall(new Expr[]{n_0, p_0},
                ctx.mkImplies(ctx.mkAnd((BoolExpr)nctx.send.apply(politoEndHost, n_0, p_0),ctx.mkEq(nctx.pf.get("proto").apply(p_0), ctx.mkInt(nctx.HTTP_RESPONSE))),
                        ctx.mkExists(new Expr[]{p_1},
                                ctx.mkAnd(
                                        ctx.mkEq(nctx.pf.get("url").apply(p_0), nctx.pf.get("url").apply(p_1)),
                                        (BoolExpr)nctx.recv.apply(n_0, politoEndHost, p_1),
                                        ctx.mkEq(nctx.pf.get("src").apply(p_1), nctx.pf.get("dest").apply(p_0))),
                                1,null,null,null,null)),1,null,null,null,null));
    }

    public  void installAsWebClient(DatatypeExpr ipServer,PacketModel packet){
        installEndHost(packet);
        Expr p_1 = ctx.mkConst("PolitoEndHost_"+politoEndHost+"_p_1", nctx.packet);
        constraints.add( ctx.mkForall(new Expr[]{n_0, p_0},
                ctx.mkImplies((BoolExpr)nctx.recv.apply(n_0,politoEndHost, p_0),
                        ctx.mkAnd( ctx.mkEq(nctx.pf.get("proto").apply(p_0), ctx.mkInt(nctx.HTTP_RESPONSE)),
                                ctx.mkExists(new Expr[]{p_1},
                                        ctx.mkAnd( (BoolExpr)nctx.send.apply(politoEndHost, n_0, p_1), ctx.mkEq(nctx.pf.get("src").apply(p_0), ipServer),
                                                ctx.mkEq(nctx.pf.get("dest").apply(p_1), nctx.pf.get("src").apply(p_0))),1,null,null,null,null)
                                )
                        ),1,null,null,null,null));

    }


    public  void installAsPOP3MailServer(PacketModel packet){
        installEndHost(packet);
        Expr p_1 = ctx.mkConst("PolitoEndHost_"+politoEndHost+"_p_1", nctx.packet);

        //constraint4 recv(n_0, politomailserver, p) -> nodehasaddr(politomailserver,p.dest)
        constraints.add( ctx.mkForall(new Expr[]{n_0, p_0},
                ctx.mkImplies(enumerateRecvP0,
                        ctx.mkEq(nctx.pf.get("proto").apply(p_0), ctx.mkInt(nctx.POP3_REQUEST))
                        ),1,null,null,null,null));

        //Constraint5 send(politoMailServer, n_0, p) -> p.proto == POP3_RESP
        constraints.add( ctx.mkForall(new Expr[]{n_0, p_0},
                ctx.mkImplies((BoolExpr)nctx.send.apply(politoEndHost, n_0, p_0),
                        ctx.mkAnd(ctx.mkEq(nctx.pf.get("proto").apply(p_0), ctx.mkInt(nctx.POP3_RESPONSE))
                                )),1,null,null,null,null));

        constraints.add( ctx.mkForall(new Expr[]{n_0, p_0},
                ctx.mkImplies((BoolExpr)nctx.send.apply(politoEndHost, n_0, p_0),
                        ctx.mkExists(new Expr[]{p_1},
                                ctx.mkAnd(
                                        (BoolExpr)nctx.recv.apply(n_0, politoEndHost, p_1),
                                        ctx.mkEq(nctx.pf.get("proto").apply(p_1), ctx.mkInt(nctx.POP3_REQUEST)),
                                        ctx.mkEq(nctx.pf.get("dest").apply(p_0), nctx.pf.get("src").apply(p_1))),1,null,null,null,null)),1,null,null,null,null));

    }
    public  void installAsPOP3MailClient(DatatypeExpr ipServer,PacketModel packet){
        installEndHost(packet);
        Expr p_1 = ctx.mkConst("PolitoEndHost_"+politoEndHost+"_p_1", nctx.packet);
        constraints.add( ctx.mkForall(new Expr[]{n_0, p_0},
                ctx.mkImplies((BoolExpr)nctx.recv.apply(n_0,politoEndHost, p_0),
                        ctx.mkAnd( ctx.mkEq(nctx.pf.get("proto").apply(p_0), ctx.mkInt(nctx.POP3_RESPONSE)),
                                ctx.mkExists(new Expr[]{n_0, p_1},
                                        ctx.mkAnd( (BoolExpr)nctx.send.apply(politoEndHost, n_0, p_1), ctx.mkEq(nctx.pf.get("src").apply(p_0), ipServer),
                                                ctx.mkEq(nctx.pf.get("dest").apply(p_1), nctx.pf.get("src").apply(p_0))),1,null,null,null,null)
                                )
                        ),1,null,null,null,null));/*
        constraints.add( ctx.mkForall(new Expr[]{n_0, p_0},
                ctx.mkImplies((BoolExpr)nctx.recv.apply(n_0,politoEndHost, p_0),
                        ctx.mkAnd( ctx.mkEq(nctx.pf.get("proto").apply(p_0), ctx.mkInt(nctx.POP3_RESPONSE)),
                                ctx.mkEq(nctx.pf.get("src").apply(p_0), ipServer))
                        ),1,null,null,null,null));*/
    }


    /*
     * Fields that can be configured -> "dest","body","seq","proto","emailFrom","url","options"
     */
    Expr n_0;
    Expr p_0;
    public void installEndHost (PacketModel packet){
        //System.out.println("Installing PolitoEndHost...");

        //IntExpr t_0 = ctx.mkIntConst("PolitoEndHost_"+politoEndHost+"_t_0");
        BoolExpr predicatesOnPktFields = ctx.mkTrue();
        if(packet!=null){
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
            /*if(packet.getProto() != null)
                predicatesOnPktFields = ctx.mkAnd(predicatesOnPktFields, ctx.mkEq(nctx.pf.get("lv4proto").apply(p_0), ctx.mkInt(packet.getL4proto())));*/
        }
        
        //Constraint1 send(politoEndHost, n_0, p) ->
        //p.origin == politoEndHost && p.orig_body == p.body && nodeHasAddr(politoEndHost,p.src)
        constraints.add( ctx.mkForall(new Expr[]{n_0, p_0},
                ctx.mkImplies((BoolExpr)nctx.send.apply(politoEndHost, n_0, p_0),
                        ctx.mkAnd(predicatesOnPktFields,
                                ctx.mkEq(nctx.pf.get("orig_body").apply(p_0),nctx.pf.get("body").apply(p_0)),
                                ctx.mkEq(nctx.pf.get("origin").apply(p_0),politoEndHost),
                                ctx.mkEq(nctx.pf.get("inner_src").apply(p_0),nctx.am.get("null")),
                                ctx.mkEq(nctx.pf.get("inner_dest").apply(p_0),nctx.am.get("null")),
                                ctx.mkEq(nctx.pf.get("encrypted").apply(p_0),ctx.mkFalse()),
                                (BoolExpr)nctx.nodeHasAddr.apply(politoEndHost,nctx.pf.get("src").apply(p_0))
                                )),1,null,null,null,null));

        //Constraint2 recv(n_0, politoEndHost, p) -> nodeHasAddr(politoEndHost,p.dest)
        constraints.add( ctx.mkForall(new Expr[]{p_0},
                ctx.mkImplies(enumerateRecvP0,
                        (BoolExpr)nctx.nodeHasAddr.apply(politoEndHost,nctx.pf.get("dest").apply(p_0))),1,null,null,null,null));

        //System.out.println("Done.");


        return;
    }

}