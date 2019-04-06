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
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.DatatypeExpr;
import com.microsoft.z3.Expr;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.Optimize;
import com.microsoft.z3.Quantifier;

import it.polito.verifoo.rest.common.AllocationNode;
import it.polito.verigraph.mcnet.components.NetContext;
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
	NetContext nctx;
	FuncDecl private_addr_func;
	private AllocationNode source;

	public PolitoNat(AllocationNode source, Context ctx, NetContext nctx) {
		isEndHost = false;
		this.source = source;
		this.ctx = ctx;
		this.nctx = nctx;
		nat = source.getZ3Name();
		constraints = new ArrayList<BoolExpr>();
		private_addr_func = ctx.mkFuncDecl(nat + "_nat_func", nctx.address, ctx.mkBoolSort());
		// isEndHost = false;

		// net = (Network) args[0][1];
		// nctx = (NetContext) args[0][2];
		// private_addresses = new ArrayList<DatatypeExpr>();
		// net.saneSend(this);
	}

	/*
	 * private void addPrivateAdd(List<DatatypeExpr> address){
	 * private_addresses.addAll(address); }
	 */

	public List<DatatypeExpr> getPrivateAddress() {
		return private_addresses;
	}

	public void natModel(DatatypeExpr natIp) {
		setInternalAddress();
		Expr p_left_to_right_0 = ctx.mkConst("p_left_to_right_0", nctx.packet);
		Expr p_left_to_right_1 = ctx.mkConst("p_left_to_right_1", nctx.packet);
		Expr p_right_to_left_2 = ctx.mkConst("p_right_to_left_2", nctx.packet);
		Expr p_right_to_left_0 = ctx.mkConst("p_right_to_left_0", nctx.packet);
		Expr p_right_to_left_1 = ctx.mkConst("p_right_to_left_1", nctx.packet);
		Expr p_4 = ctx.mkConst("p_4", nctx.packet);
		Expr p_5 = ctx.mkConst("p_5", nctx.packet);

		/*
		 * IntExpr t_0 = ctx.mkIntConst("t_0"); IntExpr t_1 = ctx.mkIntConst("t_1");
		 * IntExpr t_2 = ctx.mkIntConst("t_2");
		 */

		// private_addr_func = ctx.mkFuncDecl("private_addr_func", nctx.address,
		// ctx.mkBoolSort());

		// Constraint1
		// "send(nat, x, p_0) && !private_addr_func(p_0.dest) ->
		// p_0.src == ip_politoNat &&
		// (exist y, p_1 :
		// (recv(y, nat, p_1)&&
		// private_addr_func(p1.src) &&
		// p_1.origin == p_0.origin &&
		// same for p_1.<dest,orig_body,body,seq,proto,emailFrom,url,options> ==
		// p_0.<...>) "

		System.out.print(natIp);
		Quantifier leftToRight = ctx.mkExists(new Expr[] { p_left_to_right_0 }, ctx.mkImplies(
				ctx.mkAnd(enumerateSend(p_left_to_right_0, source.getRightHops().keySet()),
						ctx.mkNot((BoolExpr) private_addr_func.apply(nctx.pf.get("dest").apply(p_left_to_right_0)))),
				ctx.mkAnd(ctx.mkEq(nctx.pf.get("src").apply(p_left_to_right_0), natIp),
						ctx.mkExists(new Expr[] { p_left_to_right_1 }, ctx.mkAnd(enumerateRecv(p_left_to_right_1, source.getLeftHops().keySet()),
								(BoolExpr) private_addr_func.apply(nctx.pf.get("src").apply(p_left_to_right_1)),
								ctx.mkEq(nctx.pf.get("origin").apply(p_left_to_right_1), nctx.pf.get("origin").apply(p_left_to_right_0)),
								ctx.mkEq(nctx.pf.get("encrypted").apply(p_left_to_right_1), nctx.pf.get("encrypted").apply(p_left_to_right_0)),
								ctx.mkEq(nctx.pf.get("inner_src").apply(p_left_to_right_1), nctx.pf.get("inner_src").apply(p_left_to_right_0)),
								ctx.mkEq(nctx.pf.get("inner_dest").apply(p_left_to_right_1), nctx.pf.get("inner_dest").apply(p_left_to_right_0)),
								ctx.mkEq(nctx.pf.get("dest").apply(p_left_to_right_1), nctx.pf.get("dest").apply(p_left_to_right_0)),
								ctx.mkEq(nctx.pf.get("orig_body").apply(p_left_to_right_1), nctx.pf.get("orig_body").apply(p_left_to_right_0)),
								ctx.mkEq(nctx.pf.get("body").apply(p_left_to_right_1), nctx.pf.get("body").apply(p_left_to_right_0)),
								ctx.mkEq(nctx.pf.get("seq").apply(p_left_to_right_1), nctx.pf.get("seq").apply(p_left_to_right_0)),
								ctx.mkEq(nctx.pf.get("lv4proto").apply(p_left_to_right_1), nctx.pf.get("lv4proto").apply(p_left_to_right_0)),
								ctx.mkEq(nctx.pf.get("proto").apply(p_left_to_right_1), nctx.pf.get("proto").apply(p_left_to_right_0)),
								ctx.mkEq(nctx.pf.get("src_port").apply(p_left_to_right_1), nctx.pf.get("src_port").apply(p_left_to_right_0)),
								ctx.mkEq(nctx.pf.get("dest_port").apply(p_left_to_right_1), nctx.pf.get("dest_port").apply(p_left_to_right_0)),
								ctx.mkEq(nctx.pf.get("emailFrom").apply(p_left_to_right_1), nctx.pf.get("emailFrom").apply(p_left_to_right_0)),
								ctx.mkEq(nctx.pf.get("url").apply(p_left_to_right_1), nctx.pf.get("url").apply(p_left_to_right_0)),
								ctx.mkEq(nctx.pf.get("options").apply(p_left_to_right_1), nctx.pf.get("options").apply(p_left_to_right_0))), 1,
								null, null, null, null))),
				1, null, null, null, null);
		 System.out.println("-------------Nat leftToRight: "+leftToRight);
		constraints.add(leftToRight);
		
		
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
		Quantifier rightToLeft = ctx.mkExists(new Expr[] { p_right_to_left_0 }, ctx.mkImplies(
				ctx.mkAnd(enumerateSend(p_right_to_left_0, source.getLeftHops().keySet()),
						(BoolExpr) private_addr_func.apply(nctx.pf.get("dest").apply(p_right_to_left_0))),
				ctx.mkAnd(
						ctx.mkNot((BoolExpr) private_addr_func.apply(nctx.pf.get("src").apply(p_right_to_left_0))), ctx.mkExists(
								new Expr[] { p_right_to_left_1 }, ctx.mkAnd(

										enumerateRecv(p_right_to_left_1, source.getRightHops().keySet()),
										ctx.mkNot((BoolExpr) private_addr_func.apply(nctx.pf.get("src").apply(p_right_to_left_1))),
										// we never did this! dest was never nat but it was the endpoint so this never works in reality
										ctx.mkEq(nctx.pf.get("dest").apply(p_right_to_left_1), natIp),
										ctx.mkEq(nctx.pf.get("src").apply(p_right_to_left_1), nctx.pf.get("src").apply(p_right_to_left_0)),
										ctx.mkEq(nctx.pf.get("origin").apply(p_right_to_left_0), nctx.pf.get("origin").apply(p_right_to_left_1)),
										ctx.mkEq(nctx.pf.get("inner_src").apply(p_right_to_left_1),nctx.pf.get("inner_src").apply(p_right_to_left_0)),
										ctx.mkEq(nctx.pf.get("inner_dest").apply(p_right_to_left_1),nctx.pf.get("inner_dest").apply(p_right_to_left_0)),
										ctx.mkEq(nctx.pf.get("orig_body").apply(p_right_to_left_1),nctx.pf.get("orig_body").apply(p_right_to_left_0)),
										ctx.mkEq(nctx.pf.get("body").apply(p_right_to_left_1), nctx.pf.get("body").apply(p_right_to_left_0)),
										ctx.mkEq(nctx.pf.get("seq").apply(p_right_to_left_1), nctx.pf.get("seq").apply(p_right_to_left_0)),
										ctx.mkEq(nctx.pf.get("lv4proto").apply(p_right_to_left_1),nctx.pf.get("lv4proto").apply(p_right_to_left_0)),
										ctx.mkEq(nctx.pf.get("proto").apply(p_right_to_left_1), nctx.pf.get("proto").apply(p_right_to_left_0)),
										ctx.mkEq(nctx.pf.get("src_port").apply(p_right_to_left_1),nctx.pf.get("src_port").apply(p_right_to_left_0)),
										ctx.mkEq(nctx.pf.get("dest_port").apply(p_right_to_left_1),nctx.pf.get("dest_port").apply(p_right_to_left_0)),
										ctx.mkEq(nctx.pf.get("emailFrom").apply(p_right_to_left_1),nctx.pf.get("emailFrom").apply(p_right_to_left_0)),
										ctx.mkEq(nctx.pf.get("url").apply(p_right_to_left_1), nctx.pf.get("url").apply(p_right_to_left_0)),
										ctx.mkEq(nctx.pf.get("encrypted").apply(p_right_to_left_1), nctx.pf.get("encrypted").apply(p_right_to_left_0)),
										ctx.mkEq(nctx.pf.get("options").apply(p_right_to_left_1), nctx.pf.get("options").apply(p_right_to_left_0)),
										ctx.mkExists(new Expr[] { p_right_to_left_2 }, ctx.mkAnd(
												enumerateRecv(p_right_to_left_2, source.getLeftHops().keySet()),
												(BoolExpr) private_addr_func.apply(nctx.pf.get("src").apply(p_right_to_left_2)),
												ctx.mkEq(nctx.pf.get("src").apply(p_right_to_left_1), nctx.pf.get("dest").apply(p_right_to_left_2)),
												ctx.mkEq(nctx.pf.get("src").apply(p_right_to_left_2),
														nctx.pf.get("dest").apply(p_right_to_left_0))),
												1, null, null, null, null)),
								1, null, null, null, null))),
				1, null, null, null, null);
		 System.out.println("-------------Nat rightToLeft: "+rightToLeft);
		 constraints.add(rightToLeft);
		// "src","dest","inner_src","inner_dest","origin","orig_body","body","seq", "lv4proto", "src_port", "dest_port", "proto", "emailFrom","url","options","encrypted"};
		 
		/* constraints.add(ctx.mkForall(
					new Expr[] { p_right_to_left_1 },  ctx.mkImplies(
							ctx.mkAnd(enumerateRecv(p_right_to_left_1, source.getRightHops().keySet()),
									ctx.mkNot((BoolExpr) private_addr_func.apply(nctx.pf.get("src").apply(p_right_to_left_1)))),
							ctx.mkNot(enumerateSend(p_right_to_left_1, source.getLeftHops().keySet()))
							)   ,1,null,null,null,null));*/
		 
		 
		// extra constraints to oblige the VNF to forward the received packets
		BoolExpr expr = ctx.mkImplies(
				ctx.mkAnd(enumerateRecv(p_5, source.getLeftHops().keySet()),
						(BoolExpr) private_addr_func.apply(nctx.pf.get("src").apply(p_5))),
				ctx.mkExists(new Expr[] { p_4 },
						ctx.mkAnd(enumerateSend(p_4, source.getRightHops().keySet()),
								ctx.mkNot((BoolExpr) private_addr_func.apply(nctx.pf.get("dest").apply(p_4))),
								ctx.mkEq(nctx.pf.get("src").apply(p_4), natIp),
								ctx.mkEq(nctx.pf.get("origin").apply(p_5), nctx.pf.get("origin").apply(p_4)),
								ctx.mkEq(nctx.pf.get("dest").apply(p_5), nctx.pf.get("dest").apply(p_4)),
								ctx.mkEq(nctx.pf.get("orig_body").apply(p_5), nctx.pf.get("orig_body").apply(p_4)),
								ctx.mkEq(nctx.pf.get("body").apply(p_5), nctx.pf.get("body").apply(p_4)),
								ctx.mkEq(nctx.pf.get("seq").apply(p_5), nctx.pf.get("seq").apply(p_4)),
								ctx.mkEq(nctx.pf.get("lv4proto").apply(p_5), nctx.pf.get("lv4proto").apply(p_4)),
								ctx.mkEq(nctx.pf.get("proto").apply(p_5), nctx.pf.get("proto").apply(p_4)),
								ctx.mkEq(nctx.pf.get("src_port").apply(p_5), nctx.pf.get("src_port").apply(p_4)),
								ctx.mkEq(nctx.pf.get("dest_port").apply(p_5), nctx.pf.get("dest_port").apply(p_4)),
								ctx.mkEq(nctx.pf.get("emailFrom").apply(p_5), nctx.pf.get("emailFrom").apply(p_4)),
								ctx.mkEq(nctx.pf.get("url").apply(p_5), nctx.pf.get("url").apply(p_4)),
								ctx.mkEq(nctx.pf.get("inner_src").apply(p_5), nctx.pf.get("inner_src").apply(p_4)),
								ctx.mkEq(nctx.pf.get("inner_dest").apply(p_5), nctx.pf.get("inner_dest").apply(p_4)),
								ctx.mkEq(nctx.pf.get("encrypted").apply(p_5), nctx.pf.get("encrypted").apply(p_4)),
								ctx.mkEq(nctx.pf.get("options").apply(p_5), nctx.pf.get("options").apply(p_4)))

						, 1, null, null, null, null)

		);
		 System.out.println("$$ "+expr);
		 constraints.add(ctx.mkForall(new Expr[] { p_5 }, expr, 1, null, null, null,null));

	}

	private BoolExpr enumerateRecv(Expr packet, Set<AllocationNode> hops) {
		List<Expr> list = hops.stream().map(n -> n.getZ3Name()).collect(Collectors.toList());
		List<Expr> recvNeighbours = list.stream().map(n -> (BoolExpr) nctx.recv.apply(n, nat, packet)).distinct()
				.collect(Collectors.toList());
		BoolExpr[] tmp3 = new BoolExpr[list.size()];
		BoolExpr enumerateRecv = ctx.mkOr(recvNeighbours.toArray(tmp3));
		return enumerateRecv;
	}

	private BoolExpr enumerateSend(Expr packet, Set<AllocationNode> hops) {
		List<Expr> list = hops.stream().map(n -> n.getZ3Name()).collect(Collectors.toList());
		List<Expr> sendNeighbours = list.stream().map(n -> (BoolExpr) nctx.send.apply(nat, n, packet)).distinct()
				.collect(Collectors.toList());
		BoolExpr[] tmp3 = new BoolExpr[list.size()];
		BoolExpr enumerateSend = ctx.mkAnd(sendNeighbours.toArray(tmp3));
		return enumerateSend;
	}

	public void setInternalAddress() {

		ArrayList<DatatypeExpr> address = source.getNode().getConfiguration().getNat().getSource().stream()
				.map((s) -> nctx.am.get(s)).filter(e -> e != null).collect(Collectors.toCollection(ArrayList::new));
		if (address.size() > 0) {

			List<BoolExpr> constr = new ArrayList<BoolExpr>();
			Expr n_0 = ctx.mkConst("nat_node", nctx.address);

			for (DatatypeExpr n : address) {
				constr.add(ctx.mkEq(n_0, n));
			}
			
			BoolExpr[] constrs = new BoolExpr[constr.size()];
			// Constraintprivate_addr_func(n_0) == or(n_0==n foreach internal address)
			constraints.add(ctx.mkForall(new Expr[] { n_0 },
					ctx.mkEq(private_addr_func.apply(n_0), ctx.mkOr(constr.toArray(constrs))), 1, null, null, null,
					null));
		}
	}

	/*
	 * public void setInternalAddress() { List<BoolExpr> constr = new
	 * ArrayList<BoolExpr>(); Expr n_0 = ctx.mkConst("nat_node", nctx.address);
	 * 
	 * for (AllocationNode n : source.getLeftHops().keySet()) {
	 * constr.add(ctx.mkEq(n_0, n.getZ3Node())); for (AllocationNode n1 :
	 * n.getLeftHops().keySet()) { constr.add(ctx.mkEq(n_0, n1.getZ3Node())); } }
	 * 
	 * 
	 * BoolExpr[] constrs = new BoolExpr[constr.size()]; //
	 * Constraintprivate_addr_func(n_0) == or(n_0==n foreach internal // address)
	 * Quantifier internalAddresses = ctx.mkForall(new Expr[] { n_0 },
	 * ctx.mkEq(private_addr_func.apply(n_0), ctx.mkOr(constr.toArray(constrs))), 1,
	 * null, null, null, null); constraints.add(internalAddresses); }
	 */

	@Override
	public void addContraints(Optimize solver) {
		BoolExpr[] constr = new BoolExpr[constraints.size()];
		solver.Add(constraints.toArray(constr));
	}
}
