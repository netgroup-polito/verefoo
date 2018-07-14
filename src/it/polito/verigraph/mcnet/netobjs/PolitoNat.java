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
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.Optimize;
import com.microsoft.z3.Solver;
import it.polito.verigraph.mcnet.components.NetContext;
import it.polito.verigraph.mcnet.components.Network;
import it.polito.verigraph.mcnet.components.NetworkObject;

/**
 * NAT Model object
 *
 */
public class PolitoNat extends NetworkObject {
	List<BoolExpr> constraints;
	Context ctx;
	DatatypeExpr nat;
	List<DatatypeExpr> private_addresses;
	List<NetworkObject> private_node;
	Network net;
	NetContext nctx;
	FuncDecl private_addr_func;
	private ArrayList<NetworkObject> neighbours;

	public PolitoNat(Context ctx, Object[]... args) {
		super(ctx, args);
	}

	@Override
	protected void init(Context ctx, Object[]... args) {
		this.ctx = ctx;
		isEndHost = false;
		constraints = new ArrayList<BoolExpr>();
		z3Node = ((NetworkObject) args[0][0]).getZ3Node();
		nat = z3Node;
		net = (Network) args[0][1];
		nctx = (NetContext) args[0][2];
        neighbours = ((ArrayList<NetworkObject>) args[0][3]);
		private_addresses = new ArrayList<DatatypeExpr>();
		private_node = new ArrayList<NetworkObject>();
		net.saneSend(this);
	}

	@Override
	public DatatypeExpr getZ3Node() {
		return nat;
	}

	@Override
	protected void addConstraints(Optimize solver) {
		BoolExpr[] constr = new BoolExpr[constraints.size()];
		solver.Add(constraints.toArray(constr));
	}

	/*
	 * private void addPrivateAdd(List<DatatypeExpr> address){
	 * private_addresses.addAll(address); }
	 */

	public List<DatatypeExpr> getPrivateAddress() {
		return private_addresses;
	}

	public void natModel(DatatypeExpr natIp) {
		
        Expr p_0 = ctx.mkConst("p_0", nctx.packet);
        Expr p_1 = ctx.mkConst("p_1", nctx.packet);
        Expr p_2 = ctx.mkConst("p_2", nctx.packet);
        Expr p_3 = ctx.mkConst("p_3", nctx.packet);
        Expr p_4 = ctx.mkConst("p_4", nctx.packet);

		/*
		 * IntExpr t_0 = ctx.mkIntConst("t_0"); IntExpr t_1 =
		 * ctx.mkIntConst("t_1"); IntExpr t_2 = ctx.mkIntConst("t_2");
		 */

		// private_addr_func = ctx.mkFuncDecl("private_addr_func", nctx.address,
		// ctx.mkBoolSort());
		private_addr_func = ctx.mkFuncDecl(nat + "_nat_func", nctx.address, ctx.mkBoolSort());

 		List<Expr> sendNeighbours = neighbours.stream().map(n -> nctx.send.apply(nat, n.getZ3Node(), p_0)).collect(Collectors.toList());
		BoolExpr[] tmp2 = new BoolExpr[sendNeighbours.size()];
		BoolExpr enumerateSendP0 = ctx.mkOr(sendNeighbours.toArray(tmp2));
 		List<Expr> recvNeighbours = neighbours.stream().map(n -> nctx.recv.apply(n.getZ3Node(), nat, p_1)).collect(Collectors.toList());
 		BoolExpr[] tmp3 = new BoolExpr[recvNeighbours.size()];
 		BoolExpr enumerateRecvP1 = ctx.mkOr(recvNeighbours.toArray(tmp3));
		List<Expr> recvNeighbours2 = neighbours.stream().map(n -> nctx.recv.apply(n.getZ3Node(), nat, p_2)).collect(Collectors.toList());
 		BoolExpr[] tmp4 = new BoolExpr[recvNeighbours2.size()];
 		BoolExpr enumerateRecvP2 = ctx.mkOr(recvNeighbours2.toArray(tmp2));
 		
		// Constraint1
		// "send(nat, x, p_0) && !private_addr_func(p_0.dest) ->
		// p_0.src == ip_politoNat &&
		// (exist y, p_1 :
		// (recv(y, nat, p_1)&&
		// private_addr_func(p1.src) &&
		// p_1.origin == p_0.origin &&
		// same for p_1.<dest,orig_body,body,seq,proto,emailFrom,url,options> ==
		// p_0.<...>) "
		constraints.add(ctx.mkForall(new Expr[] { p_0 }, ctx.mkImplies(
				ctx.mkAnd(enumerateSendP0,
						ctx.mkNot((BoolExpr) private_addr_func.apply(nctx.pf.get("dest").apply(p_0)))),
				ctx.mkAnd(ctx.mkEq(nctx.pf.get("src").apply(p_0), natIp),
						ctx.mkExists(new Expr[] { p_1 }, ctx.mkAnd(enumerateRecvP1,
								(BoolExpr) private_addr_func.apply(nctx.pf.get("src").apply(p_1)),
								ctx.mkEq(nctx.pf.get("origin").apply(p_1), nctx.pf.get("origin").apply(p_0)),
								ctx.mkEq(nctx.pf.get("inner_src").apply(p_1), nctx.pf.get("inner_src").apply(p_0)),
								ctx.mkEq(nctx.pf.get("inner_dest").apply(p_1), nctx.pf.get("inner_dest").apply(p_0)),
								ctx.mkEq(nctx.pf.get("dest").apply(p_1), nctx.pf.get("dest").apply(p_0)),
								ctx.mkEq(nctx.pf.get("orig_body").apply(p_1), nctx.pf.get("orig_body").apply(p_0)),
								ctx.mkEq(nctx.pf.get("body").apply(p_1), nctx.pf.get("body").apply(p_0)),
								ctx.mkEq(nctx.pf.get("seq").apply(p_1), nctx.pf.get("seq").apply(p_0)),
								ctx.mkEq(nctx.pf.get("lv4proto").apply(p_1), nctx.pf.get("lv4proto").apply(p_0)),
								ctx.mkEq(nctx.pf.get("proto").apply(p_1), nctx.pf.get("proto").apply(p_0)),
								ctx.mkEq(nctx.pf.get("src_port").apply(p_1), nctx.pf.get("src_port").apply(p_0)),
								ctx.mkEq(nctx.pf.get("dest_port").apply(p_1), nctx.pf.get("dest_port").apply(p_0)),
								ctx.mkEq(nctx.pf.get("emailFrom").apply(p_1), nctx.pf.get("emailFrom").apply(p_0)),
								ctx.mkEq(nctx.pf.get("url").apply(p_1), nctx.pf.get("url").apply(p_0)),
								ctx.mkEq(nctx.pf.get("options").apply(p_1), nctx.pf.get("options").apply(p_0))), 1,
								null, null, null, null))),
				1, null, null, null, null));

		// Constraint2
		// send(nat, x, p_0) && private_addr_func(p_0.dest) ->
		// !private_addr_func(p_0.src) &&
		// (exist y, p_1 :
		// (recv(y, nat, p_1) &&
		// !private_addr_func(p1.src) &&
		// p_1.dest == ip_politoNat &&
		// p_1.origin == p_0.origin &&
		// same for p_1.<src,orig_body,body,seq,proto,emailFrom,url,options> ==
		// p_0.<...>)
		constraints.add(ctx.mkForall(new Expr[] { p_0 },
				ctx.mkImplies(
						ctx.mkAnd(enumerateSendP0,
								(BoolExpr) private_addr_func.apply(nctx.pf.get("dest").apply(p_0))),
						ctx.mkAnd(ctx.mkNot((BoolExpr) private_addr_func.apply(nctx.pf.get("src").apply(p_0))),
								ctx.mkExists(new Expr[] { p_1 }, ctx.mkAnd(

										enumerateRecvP1,
										ctx.mkNot((BoolExpr) private_addr_func.apply(nctx.pf.get("src").apply(p_1))),
										ctx.mkEq(nctx.pf.get("dest").apply(p_1), natIp),
										ctx.mkEq(nctx.pf.get("src").apply(p_1), nctx.pf.get("src").apply(p_0)),
										ctx.mkEq(nctx.pf.get("origin").apply(p_0), nctx.pf.get("origin").apply(p_1)),
										ctx.mkEq(nctx.pf.get("inner_src").apply(p_1),
												nctx.pf.get("inner_src").apply(p_0)),
										ctx.mkEq(nctx.pf.get("inner_dest").apply(p_1),
												nctx.pf.get("inner_dest").apply(p_0)),
										ctx.mkEq(nctx.pf.get("orig_body").apply(p_1),
												nctx.pf.get("orig_body").apply(p_0)),
										ctx.mkEq(nctx.pf.get("body").apply(p_1), nctx.pf.get("body").apply(p_0)),
										ctx.mkEq(nctx.pf.get("seq").apply(p_1), nctx.pf.get("seq").apply(p_0)),
										ctx.mkEq(nctx.pf.get("lv4proto").apply(p_1), nctx.pf.get("lv4proto").apply(p_0)),
										ctx.mkEq(nctx.pf.get("proto").apply(p_1), nctx.pf.get("proto").apply(p_0)),
										ctx.mkEq(nctx.pf.get("src_port").apply(p_1), nctx.pf.get("src_port").apply(p_0)),
										ctx.mkEq(nctx.pf.get("dest_port").apply(p_1), nctx.pf.get("dest_port").apply(p_0)),
										ctx.mkEq(nctx.pf.get("emailFrom").apply(p_1),
												nctx.pf.get("emailFrom").apply(p_0)),
										ctx.mkEq(nctx.pf.get("url").apply(p_1), nctx.pf.get("url").apply(p_0)),
										ctx.mkEq(nctx.pf.get("options").apply(p_1), nctx.pf.get("options").apply(p_0)),
										ctx.mkExists(new Expr[] { p_2 }, ctx.mkAnd(

												enumerateRecvP2,
												(BoolExpr) private_addr_func.apply(nctx.pf.get("src").apply(p_2)),
												ctx.mkEq(nctx.pf.get("src").apply(p_1), nctx.pf.get("dest").apply(p_2)),
												ctx.mkEq(nctx.pf.get("src").apply(p_2),
														nctx.pf.get("dest").apply(p_0))),
												1, null, null, null, null)),
										1, null, null, null, null))),
				1, null, null, null, null));

 		List<Expr> sendNeighbours2 = neighbours.stream().map(n -> nctx.send.apply(nat, n.getZ3Node(), p_4)).collect(Collectors.toList());
		BoolExpr[] tmp5 = new BoolExpr[sendNeighbours2.size()];
		BoolExpr enumerateSendP4 = ctx.mkOr(sendNeighbours2.toArray(tmp5));
 		List<Expr> recvNeighbours3 = neighbours.stream().map(n -> nctx.recv.apply(n.getZ3Node(), nat, p_3)).collect(Collectors.toList());
 		BoolExpr[] tmp6 = new BoolExpr[recvNeighbours3.size()];
 		BoolExpr enumerateRecvP3 = ctx.mkOr(recvNeighbours3.toArray(tmp6));
 		
		// extra constraints to oblige the VNF to forward the received packets
		BoolExpr expr = ctx.mkImplies(
				ctx.mkAnd(enumerateRecvP3,
						(BoolExpr) private_addr_func.apply(nctx.pf.get("src").apply(p_3))),
				ctx.mkExists(new Expr[] { p_4 },
						ctx.mkAnd(enumerateSendP4,
								ctx.mkNot((BoolExpr) private_addr_func.apply(nctx.pf.get("dest").apply(p_4))),
								ctx.mkEq(nctx.pf.get("src").apply(p_4), natIp),
								ctx.mkEq(nctx.pf.get("origin").apply(p_3), nctx.pf.get("origin").apply(p_4)),
								ctx.mkEq(nctx.pf.get("dest").apply(p_3), nctx.pf.get("dest").apply(p_4)),
								ctx.mkEq(nctx.pf.get("orig_body").apply(p_3), nctx.pf.get("orig_body").apply(p_4)),
								ctx.mkEq(nctx.pf.get("body").apply(p_3), nctx.pf.get("body").apply(p_4)),
								ctx.mkEq(nctx.pf.get("seq").apply(p_3), nctx.pf.get("seq").apply(p_4)),
								ctx.mkEq(nctx.pf.get("lv4proto").apply(p_3), nctx.pf.get("lv4proto").apply(p_4)),
								ctx.mkEq(nctx.pf.get("proto").apply(p_3), nctx.pf.get("proto").apply(p_4)),
								ctx.mkEq(nctx.pf.get("src_port").apply(p_3), nctx.pf.get("src_port").apply(p_4)),
								ctx.mkEq(nctx.pf.get("dest_port").apply(p_3), nctx.pf.get("dest_port").apply(p_4)),
								ctx.mkEq(nctx.pf.get("emailFrom").apply(p_3), nctx.pf.get("emailFrom").apply(p_4)),
								ctx.mkEq(nctx.pf.get("url").apply(p_3), nctx.pf.get("url").apply(p_4)),
								ctx.mkEq(nctx.pf.get("inner_src").apply(p_3), nctx.pf.get("inner_src").apply(p_4)),
								ctx.mkEq(nctx.pf.get("inner_dest").apply(p_3), nctx.pf.get("inner_dest").apply(p_4)),
								ctx.mkEq(nctx.pf.get("url").apply(p_3), nctx.pf.get("url").apply(p_4)),
								ctx.mkEq(nctx.pf.get("encrypted").apply(p_3), nctx.pf.get("encrypted").apply(p_4)),
								ctx.mkEq(nctx.pf.get("options").apply(p_3), nctx.pf.get("options").apply(p_4)))

						, 1, null, null, null, null)

		);
		constraints.add(ctx.mkForall(new Expr[] { p_3 }, expr, 1, null, null, null, null));

	}

	public void setInternalAddress(ArrayList<DatatypeExpr> internalAddress) {
		List<BoolExpr> constr = new ArrayList<BoolExpr>();
		Expr n_0 = ctx.mkConst("nat_node", nctx.address);

		for (DatatypeExpr n : internalAddress) {
			constr.add(ctx.mkEq(n_0, n));
		}
		BoolExpr[] constrs = new BoolExpr[constr.size()];
		// Constraintprivate_addr_func(n_0) == or(n_0==n foreach internal
		// address)
		constraints.add(ctx.mkForall(new Expr[] { n_0 },
				ctx.mkEq(private_addr_func.apply(n_0), ctx.mkOr(constr.toArray(constrs))), 1, null, null, null, null));
	}
}
