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
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.Optimize;
import com.microsoft.z3.Quantifier;

import it.polito.verefoo.allocation.AllocationNode;
import it.polito.verigraph.solver.NetContext;

/**
 * NAT Model object
 *
 */
public class NAT extends GenericFunction {
	DatatypeExpr nat;
	List<DatatypeExpr> private_addresses;
	List<GenericFunction> private_node;
	FuncDecl private_addr_func;

	
	public NAT(AllocationNode source, Context ctx, NetContext nctx) {
		isEndHost = false;
		this.source = source;
		this.ctx = ctx;
		this.nctx = nctx;
		nat = source.getZ3Name();
		constraints = new ArrayList<BoolExpr>();
		private_addr_func = ctx.mkFuncDecl(nat + "_nat_func", nctx.addressType, ctx.mkBoolSort()); 
	}


	// #TODO correct the formulas
	public void natConfiguration(DatatypeExpr natIp) {
		setInternalAddress();
		Expr p_left_to_right_0 = ctx.mkConst("p_left_to_right_0", nctx.packetType);
		Expr p_left_to_right_1 = ctx.mkConst("p_left_to_right_1", nctx.packetType);
		Expr p_right_to_left_2 = ctx.mkConst("p_right_to_left_2", nctx.packetType);
		Expr p_right_to_left_0 = ctx.mkConst("p_right_to_left_0", nctx.packetType);
		Expr p_right_to_left_1 = ctx.mkConst("p_right_to_left_1", nctx.packetType);
		Expr p_4 = ctx.mkConst("p_4", nctx.packetType);
		Expr p_5 = ctx.mkConst("p_5", nctx.packetType);
		
		// Constraint1
		// "send(nat, x, p_0) && !private_addr_func(p_0.dest) ->
		// p_0.src == ip_politoNat &&
		// (exist y, p_1 :
		// (recv(y, nat, p_1)&&
		// private_addr_func(p1.src) &&
		// p_1.origin == p_0.origin &&
		// same for p_1.<dest,orig_body,body,seq,proto,emailFrom,url,options> ==
		// p_0.<...>) "

		Quantifier leftToRight = ctx.mkForall(new Expr[] { p_left_to_right_0 }, ctx.mkImplies(
				ctx.mkAnd(enumerateSend(p_left_to_right_0, source.getRightHops().keySet()),
						ctx.mkNot((BoolExpr) private_addr_func.apply(nctx.functionsMap.get("dest").apply(p_left_to_right_0)))),
				ctx.mkAnd(ctx.mkEq(nctx.functionsMap.get("src").apply(p_left_to_right_0), natIp),
						ctx.mkExists(new Expr[] { p_left_to_right_1 }, ctx.mkAnd(enumerateRecv(p_left_to_right_1, source.getLeftHops().keySet()),
								(BoolExpr) private_addr_func.apply(nctx.functionsMap.get("src").apply(p_left_to_right_1)),
								ctx.mkEq(nctx.functionsMap.get("origin").apply(p_left_to_right_1), nctx.functionsMap.get("origin").apply(p_left_to_right_0)),
								ctx.mkEq(nctx.functionsMap.get("encrypted").apply(p_left_to_right_1), nctx.functionsMap.get("encrypted").apply(p_left_to_right_0)),
								ctx.mkEq(nctx.functionsMap.get("inner_src").apply(p_left_to_right_1), nctx.functionsMap.get("inner_src").apply(p_left_to_right_0)),
								ctx.mkEq(nctx.functionsMap.get("inner_dest").apply(p_left_to_right_1), nctx.functionsMap.get("inner_dest").apply(p_left_to_right_0)),
								ctx.mkEq(nctx.functionsMap.get("dest").apply(p_left_to_right_1), nctx.functionsMap.get("dest").apply(p_left_to_right_0)),
								ctx.mkEq(nctx.functionsMap.get("orig_body").apply(p_left_to_right_1), nctx.functionsMap.get("orig_body").apply(p_left_to_right_0)),
								ctx.mkEq(nctx.functionsMap.get("body").apply(p_left_to_right_1), nctx.functionsMap.get("body").apply(p_left_to_right_0)),
								ctx.mkEq(nctx.functionsMap.get("seq").apply(p_left_to_right_1), nctx.functionsMap.get("seq").apply(p_left_to_right_0)),
								ctx.mkEq(nctx.functionsMap.get("lv4proto").apply(p_left_to_right_1), nctx.functionsMap.get("lv4proto").apply(p_left_to_right_0)),
								ctx.mkEq(nctx.functionsMap.get("proto").apply(p_left_to_right_1), nctx.functionsMap.get("proto").apply(p_left_to_right_0)),
								ctx.mkEq(nctx.functionsMap.get("src_port").apply(p_left_to_right_1), nctx.functionsMap.get("src_port").apply(p_left_to_right_0)),
								ctx.mkEq(nctx.functionsMap.get("dest_port").apply(p_left_to_right_1), nctx.functionsMap.get("dest_port").apply(p_left_to_right_0)),
								ctx.mkEq(nctx.functionsMap.get("emailFrom").apply(p_left_to_right_1), nctx.functionsMap.get("emailFrom").apply(p_left_to_right_0)),
								ctx.mkEq(nctx.functionsMap.get("url").apply(p_left_to_right_1), nctx.functionsMap.get("url").apply(p_left_to_right_0)),
								ctx.mkEq(nctx.functionsMap.get("options").apply(p_left_to_right_1), nctx.functionsMap.get("options").apply(p_left_to_right_0))), 1,
								null, null, null, null))),
				1, null, null, null, null);
		// System.out.println("-------------Nat leftToRight: "+leftToRight);
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
		Quantifier rightToLeft = ctx.mkForall(new Expr[] { p_right_to_left_0 }, ctx.mkImplies(
				ctx.mkAnd(enumerateSend(p_right_to_left_0, source.getLeftHops().keySet()),
						(BoolExpr) private_addr_func.apply(nctx.functionsMap.get("dest").apply(p_right_to_left_0))),
				ctx.mkAnd(
						ctx.mkNot((BoolExpr) private_addr_func.apply(nctx.functionsMap.get("src").apply(p_right_to_left_0))), ctx.mkExists(
								new Expr[] { p_right_to_left_1 }, ctx.mkAnd(

										enumerateRecv(p_right_to_left_1, source.getRightHops().keySet()),
										ctx.mkNot((BoolExpr) private_addr_func.apply(nctx.functionsMap.get("src").apply(p_right_to_left_1))),
										// we never did this! dest was never nat but it was the endpoint so this never works in reality
										ctx.mkEq(nctx.functionsMap.get("dest").apply(p_right_to_left_1), natIp),
										ctx.mkEq(nctx.functionsMap.get("src").apply(p_right_to_left_1), nctx.functionsMap.get("src").apply(p_right_to_left_0)),
										ctx.mkEq(nctx.functionsMap.get("origin").apply(p_right_to_left_0), nctx.functionsMap.get("origin").apply(p_right_to_left_1)),
										ctx.mkEq(nctx.functionsMap.get("inner_src").apply(p_right_to_left_1),nctx.functionsMap.get("inner_src").apply(p_right_to_left_0)),
										ctx.mkEq(nctx.functionsMap.get("inner_dest").apply(p_right_to_left_1),nctx.functionsMap.get("inner_dest").apply(p_right_to_left_0)),
										ctx.mkEq(nctx.functionsMap.get("orig_body").apply(p_right_to_left_1),nctx.functionsMap.get("orig_body").apply(p_right_to_left_0)),
										ctx.mkEq(nctx.functionsMap.get("body").apply(p_right_to_left_1), nctx.functionsMap.get("body").apply(p_right_to_left_0)),
										ctx.mkEq(nctx.functionsMap.get("seq").apply(p_right_to_left_1), nctx.functionsMap.get("seq").apply(p_right_to_left_0)),
										ctx.mkEq(nctx.functionsMap.get("lv4proto").apply(p_right_to_left_1),nctx.functionsMap.get("lv4proto").apply(p_right_to_left_0)),
										ctx.mkEq(nctx.functionsMap.get("proto").apply(p_right_to_left_1), nctx.functionsMap.get("proto").apply(p_right_to_left_0)),
										ctx.mkEq(nctx.functionsMap.get("src_port").apply(p_right_to_left_1),nctx.functionsMap.get("src_port").apply(p_right_to_left_0)),
										ctx.mkEq(nctx.functionsMap.get("dest_port").apply(p_right_to_left_1),nctx.functionsMap.get("dest_port").apply(p_right_to_left_0)),
										ctx.mkEq(nctx.functionsMap.get("emailFrom").apply(p_right_to_left_1),nctx.functionsMap.get("emailFrom").apply(p_right_to_left_0)),
										ctx.mkEq(nctx.functionsMap.get("url").apply(p_right_to_left_1), nctx.functionsMap.get("url").apply(p_right_to_left_0)),
										ctx.mkEq(nctx.functionsMap.get("encrypted").apply(p_right_to_left_1), nctx.functionsMap.get("encrypted").apply(p_right_to_left_0)),
										ctx.mkEq(nctx.functionsMap.get("options").apply(p_right_to_left_1), nctx.functionsMap.get("options").apply(p_right_to_left_0)),
										ctx.mkExists(new Expr[] { p_right_to_left_2 }, ctx.mkAnd(
												enumerateRecv(p_right_to_left_2, source.getLeftHops().keySet()),
												(BoolExpr) private_addr_func.apply(nctx.functionsMap.get("src").apply(p_right_to_left_2)),
												ctx.mkEq(nctx.functionsMap.get("src").apply(p_right_to_left_1), nctx.functionsMap.get("dest").apply(p_right_to_left_2)),
												ctx.mkEq(nctx.functionsMap.get("src").apply(p_right_to_left_2),
														nctx.functionsMap.get("dest").apply(p_right_to_left_0))),
												1, null, null, null, null)),
								1, null, null, null, null))),
				1, null, null, null, null);
		 //System.out.println("-------------Nat rightToLeft: "+rightToLeft);
		 //constraints.add(rightToLeft);
		// "src","dest","inner_src","inner_dest","origin","orig_body","body","seq", "lv4proto", "src_port", "dest_port", "proto", "emailFrom","url","options","encrypted"};
		 
	
		
		// extra constraints to oblige the VNF to forward the received packets
		BoolExpr expr = ctx.mkImplies(
				ctx.mkAnd(enumerateRecv(p_5, source.getLeftHops().keySet()),
						(BoolExpr) private_addr_func.apply(nctx.functionsMap.get("src").apply(p_5))),
				ctx.mkExists(new Expr[] { p_4 },
						ctx.mkAnd(enumerateSend(p_4, source.getRightHops().keySet()),
								ctx.mkNot((BoolExpr) private_addr_func.apply(nctx.functionsMap.get("dest").apply(p_4))),
								ctx.mkEq(nctx.functionsMap.get("src").apply(p_4), natIp),
								ctx.mkEq(nctx.functionsMap.get("origin").apply(p_5), nctx.functionsMap.get("origin").apply(p_4)),
								ctx.mkEq(nctx.functionsMap.get("dest").apply(p_5), nctx.functionsMap.get("dest").apply(p_4)),
								ctx.mkEq(nctx.functionsMap.get("orig_body").apply(p_5), nctx.functionsMap.get("orig_body").apply(p_4)),
								ctx.mkEq(nctx.functionsMap.get("body").apply(p_5), nctx.functionsMap.get("body").apply(p_4)),
								ctx.mkEq(nctx.functionsMap.get("seq").apply(p_5), nctx.functionsMap.get("seq").apply(p_4)),
								ctx.mkEq(nctx.functionsMap.get("lv4proto").apply(p_5), nctx.functionsMap.get("lv4proto").apply(p_4)),
								ctx.mkEq(nctx.functionsMap.get("proto").apply(p_5), nctx.functionsMap.get("proto").apply(p_4)),
								ctx.mkEq(nctx.functionsMap.get("src_port").apply(p_5), nctx.functionsMap.get("src_port").apply(p_4)),
								ctx.mkEq(nctx.functionsMap.get("dest_port").apply(p_5), nctx.functionsMap.get("dest_port").apply(p_4)),
								ctx.mkEq(nctx.functionsMap.get("emailFrom").apply(p_5), nctx.functionsMap.get("emailFrom").apply(p_4)),
								ctx.mkEq(nctx.functionsMap.get("url").apply(p_5), nctx.functionsMap.get("url").apply(p_4)),
								ctx.mkEq(nctx.functionsMap.get("inner_src").apply(p_5), nctx.functionsMap.get("inner_src").apply(p_4)),
								ctx.mkEq(nctx.functionsMap.get("inner_dest").apply(p_5), nctx.functionsMap.get("inner_dest").apply(p_4)),
								ctx.mkEq(nctx.functionsMap.get("encrypted").apply(p_5), nctx.functionsMap.get("encrypted").apply(p_4)),
								ctx.mkEq(nctx.functionsMap.get("options").apply(p_5), nctx.functionsMap.get("options").apply(p_4)))

						, 1, null, null, null, null)

		);
		
		 //System.out.println("$$ "+expr);
		 constraints.add(ctx.mkForall(new Expr[] { p_5 }, expr, 1, null, null, null,null));
			expr =  ctx.mkImplies(
					ctx.mkAnd(enumerateRecv(p_5, source.getLeftHops().keySet())),
					ctx.mkExists(new Expr[] { p_4 },
							ctx.mkAnd(enumerateSend(p_4, source.getRightHops().keySet()),
									ctx.mkEq(nctx.functionsMap.get("src").apply(p_4), natIp),
									ctx.mkEq(nctx.functionsMap.get("origin").apply(p_5), nctx.functionsMap.get("origin").apply(p_4)),
									ctx.mkEq(nctx.functionsMap.get("dest").apply(p_5), nctx.functionsMap.get("dest").apply(p_4)),
									ctx.mkEq(nctx.functionsMap.get("orig_body").apply(p_5), nctx.functionsMap.get("orig_body").apply(p_4)),
									ctx.mkEq(nctx.functionsMap.get("body").apply(p_5), nctx.functionsMap.get("body").apply(p_4)),
									ctx.mkEq(nctx.functionsMap.get("seq").apply(p_5), nctx.functionsMap.get("seq").apply(p_4)),
									ctx.mkEq(nctx.functionsMap.get("lv4proto").apply(p_5), nctx.functionsMap.get("lv4proto").apply(p_4)),
									ctx.mkEq(nctx.functionsMap.get("proto").apply(p_5), nctx.functionsMap.get("proto").apply(p_4)),
									ctx.mkEq(nctx.functionsMap.get("src_port").apply(p_5), nctx.functionsMap.get("src_port").apply(p_4)),
									ctx.mkEq(nctx.functionsMap.get("dest_port").apply(p_5), nctx.functionsMap.get("dest_port").apply(p_4)),
									ctx.mkEq(nctx.functionsMap.get("emailFrom").apply(p_5), nctx.functionsMap.get("emailFrom").apply(p_4)),
									ctx.mkEq(nctx.functionsMap.get("url").apply(p_5), nctx.functionsMap.get("url").apply(p_4)),
									ctx.mkEq(nctx.functionsMap.get("inner_src").apply(p_5), nctx.functionsMap.get("inner_src").apply(p_4)),
									ctx.mkEq(nctx.functionsMap.get("inner_dest").apply(p_5), nctx.functionsMap.get("inner_dest").apply(p_4)),
									ctx.mkEq(nctx.functionsMap.get("encrypted").apply(p_5), nctx.functionsMap.get("encrypted").apply(p_4)),
									ctx.mkEq(nctx.functionsMap.get("options").apply(p_5), nctx.functionsMap.get("options").apply(p_4)))
							, 1, null, null, null, null)

			);
		 ///constraints.add(ctx.mkForall(new Expr[] { p_5 }, expr, 1, null, null, null,null));

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
				.map((s) -> nctx.addressMap.get(s)).filter(e -> e != null).collect(Collectors.toCollection(ArrayList::new));
		if (address.size() > 0) {
			List<BoolExpr> constr = new ArrayList<BoolExpr>();
			Expr n_0 = ctx.mkConst("nat_node", nctx.addressType);
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

	
	@Override
	public void addContraints(Optimize solver) {
		BoolExpr[] constr = new BoolExpr[constraints.size()];
		solver.Add(constraints.toArray(constr));
	}
}
