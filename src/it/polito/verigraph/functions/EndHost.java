/*******************************************************************************
 * Copyright (c) 2017 Politecnico di Torino and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package it.polito.verigraph.functions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.DatatypeExpr;
import com.microsoft.z3.Expr;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.Optimize;
import com.microsoft.z3.Solver;

import it.polito.verefoo.allocation.AllocationNode;
import it.polito.verigraph.extra.PacketModel;
import it.polito.verigraph.solver.NetContext;

public class EndHost extends GenericFunction {

    List<BoolExpr> constraints = new ArrayList<BoolExpr>();
    Context ctx;
    DatatypeExpr politoEndHost;
    NetContext nctx;
    PacketModel packet;
    AllocationNode source;

    // TODO comments need to be updated
    public EndHost(AllocationNode source, Context ctx, NetContext nctx) { 
    	 this.source = source;
    	 this.ctx = ctx;
    	 this.nctx = nctx;
         this.isEndHost = true;
         politoEndHost = source.getZ3Name();
         n_0 = ctx.mkConst("PolitoEndHost_"+politoEndHost+"_n_0", nctx.nodeType);
         p_0 = ctx.mkConst("PolitoEndHost_"+politoEndHost+"_p_0", nctx.packetType);
    }


	@Override
	public void addContraints(Optimize solver) {
		 BoolExpr[] constr = new BoolExpr[constraints.size()];
	        solver.Add(constraints.toArray(constr));
	}

    /*
     * Fields that can be configured -> "dest","body","seq","proto","emailFrom","url","options"
     */
    Expr n_0;
    Expr p_0;
    public void installEndHost (PacketModel packet){
        BoolExpr predicatesOnPktFields = ctx.mkTrue();
        if(packet!=null){
        	if(packet.getIp_dest() != null)
                predicatesOnPktFields = ctx.mkAnd(predicatesOnPktFields, ctx.mkEq(nctx.functionsMap.get("dest").apply(p_0), packet.getIp_dest()));
            if(packet.getBody() != null)
                predicatesOnPktFields = ctx.mkAnd(predicatesOnPktFields, ctx.mkEq(nctx.functionsMap.get("body").apply(p_0), ctx.mkInt(packet.getBody())));
            if(packet.getEmailFrom() != null)
                predicatesOnPktFields = ctx.mkAnd(predicatesOnPktFields, ctx.mkEq(nctx.functionsMap.get("emailFrom").apply(p_0), ctx.mkInt(packet.getEmailFrom())));
            if(packet.getOptions() != null)
                predicatesOnPktFields = ctx.mkAnd(predicatesOnPktFields, ctx.mkEq(nctx.functionsMap.get("options").apply(p_0), ctx.mkInt(packet.getOptions())));
            if(packet.getProto() != null)
                predicatesOnPktFields = ctx.mkAnd(predicatesOnPktFields, ctx.mkEq(nctx.functionsMap.get("proto").apply(p_0), ctx.mkInt(packet.getProto())));
            if(packet.getSeq() != null)
                predicatesOnPktFields = ctx.mkAnd(predicatesOnPktFields, ctx.mkEq(nctx.functionsMap.get("seq").apply(p_0), ctx.mkInt(packet.getSeq())));
            if(packet.getUrl() != null)
                predicatesOnPktFields = ctx.mkAnd(predicatesOnPktFields, ctx.mkEq(nctx.functionsMap.get("url").apply(p_0), ctx.mkInt(packet.getUrl())));
        }
        
        
        //Constraint send(politoEndHost, n_0, p) ->
        //p.origin == politoEndHost && p.orig_body == p.body && nodeHasAddr(politoEndHost,p.src)
        constraints.add( ctx.mkForall(new Expr[]{n_0, p_0},
                ctx.mkImplies((BoolExpr)nctx.send.apply(politoEndHost, n_0, p_0),
                        ctx.mkAnd(predicatesOnPktFields,
                                ctx.mkEq(nctx.functionsMap.get("orig_body").apply(p_0),nctx.functionsMap.get("body").apply(p_0)),
                                ctx.mkEq(nctx.functionsMap.get("origin").apply(p_0),politoEndHost),
                                ctx.mkEq(nctx.functionsMap.get("inner_src").apply(p_0),nctx.addressMap.get("null")),
                                ctx.mkEq(nctx.functionsMap.get("inner_dest").apply(p_0),nctx.addressMap.get("null")),
                                ctx.mkEq(nctx.functionsMap.get("encrypted").apply(p_0),ctx.mkFalse()),
                                ctx.mkEq(nctx.functionsMap.get("encrypted").apply(p_0),ctx.mkFalse())
                                ,(BoolExpr)nctx.nodeHasAddr.apply(politoEndHost,nctx.functionsMap.get("src").apply(p_0))
                                )),1,null,null,null,null));

        return;
    }





}