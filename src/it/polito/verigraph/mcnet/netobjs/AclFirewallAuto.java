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
import com.microsoft.z3.Sort;

import it.polito.verigraph.mcnet.components.NetContext;
import it.polito.verigraph.mcnet.components.Network;
import it.polito.verigraph.mcnet.components.NetworkObject;
import it.polito.verigraph.mcnet.components.Tuple;

/**
 * Represents a Firewall with the associated Access Control List
 *
 */
public class AclFirewallAuto extends NetworkObject {

	List<BoolExpr> constraints;
	Context ctx;
	DatatypeExpr fw;
	ArrayList<Tuple<DatatypeExpr, DatatypeExpr>> acls;
	Network net;
	NetContext nctx;
	FuncDecl acl_func;

	public AclFirewallAuto(Context ctx, Object[]... args) {
		super(ctx, args);
	}

	@Override
	protected void init(Context ctx, Object[]... args) {
		this.ctx = ctx;
		isEndHost = false;
		constraints = new ArrayList<BoolExpr>();
		acls = new ArrayList<Tuple<DatatypeExpr, DatatypeExpr>>();
		z3Node = ((NetworkObject) args[0][0]).getZ3Node();
		fw = z3Node;
		net = (Network) args[0][1];
		nctx = (NetContext) args[0][2];
		net.saneSend(this);
		firewallSendRules();
	}

	/**
	 * Wrap add acls
	 * 
	 * @param policy
	 */
	public void setPolicy(ArrayList<Tuple<DatatypeExpr, DatatypeExpr>> policy) {
		addAcls(policy);
	}

	public void addAcls(ArrayList<Tuple<DatatypeExpr, DatatypeExpr>> acls) {
		this.acls.addAll(acls);
	}

	@Override
	public DatatypeExpr getZ3Node() {
		return fw;
	}

	@Override
	protected void addConstraints(Optimize solver) {
		BoolExpr[] constr = new BoolExpr[constraints.size()];
		solver.Add(constraints.toArray(constr));
		aclConstraints(solver);
	}

	///minimize number of rules
	// cost of the fw1 100$    fw2 50$
	private void firewallSendRules() {
		Expr p_0 = ctx.mkConst(fw + "_firewall_send_p_0", nctx.packet);
		Expr n_0 = ctx.mkConst(fw + "_firewall_send_n_0", nctx.node);
		Expr n_1 = ctx.mkConst(fw + "_firewall_send_n_1", nctx.node);

		Expr src_1 = ctx.mkConst(fw + "_src_1", nctx.address);
		Expr dst_1 = ctx.mkConst(fw + "_dst_1", nctx.address);
		Expr proto_1 = ctx.mkConst(fw + "_proto_1", ctx.mkIntSort());
		Expr srcp_1 = ctx.mkConst(fw + "_srcp_1", ctx.mkIntSort());
		Expr dstp_1 = ctx.mkConst(fw + "_dstp_1", ctx.mkIntSort());
		
		Expr src_2 = ctx.mkConst(fw + "_src_2", nctx.address);
		Expr dst_2 = ctx.mkConst(fw + "_dst_2", nctx.address);
		Expr proto_2 = ctx.mkConst(fw + "_proto_2", ctx.mkIntSort());
		Expr srcp_2 = ctx.mkConst(fw + "_srcp_2", ctx.mkIntSort());
		Expr dstp_2 = ctx.mkConst(fw + "_dstp_2", ctx.mkIntSort());
		
		
		Expr src_3 = ctx.mkConst(fw + "_src_3", nctx.address);
		Expr dst_3 = ctx.mkConst(fw + "_dst_3", nctx.address);
		Expr proto_3 = ctx.mkConst(fw + "_proto_3", ctx.mkIntSort());
		Expr srcp_3 = ctx.mkConst(fw + "_srcp_3", ctx.mkIntSort());
		Expr dstp_3 = ctx.mkConst(fw + "_dstp_3", ctx.mkIntSort());
		
		Expr src_4 = ctx.mkConst(fw + "_src_4", nctx.address);
		Expr dst_4 = ctx.mkConst(fw + "_dst_4", nctx.address);
		Expr proto_4 = ctx.mkConst(fw + "_proto_4", ctx.mkIntSort());
		Expr srcp_4 = ctx.mkConst(fw + "_srcp_4", ctx.mkIntSort());
		Expr dstp_4 = ctx.mkConst(fw + "_dstp_4", ctx.mkIntSort());
		
		Expr src_5 = ctx.mkConst(fw + "_src_5", nctx.address);
		Expr dst_5 = ctx.mkConst(fw + "_dst_5", nctx.address);
		Expr proto_5 = ctx.mkConst(fw + "_proto_5", ctx.mkIntSort());
		Expr srcp_5 = ctx.mkConst(fw + "_srcp_5", ctx.mkIntSort());
		Expr dstp_5 = ctx.mkConst(fw + "_dstp_5", ctx.mkIntSort());
		
		//5 rule table max
		
		
		
		
		nctx.softConstraints.add(new Tuple<BoolExpr, String>(ctx.mkEq( src_1, this.nctx.am.get("null")),"fw"));
		nctx.softConstraints.add(new Tuple<BoolExpr, String>(ctx.mkEq( src_2, this.nctx.am.get("null")),"fw"));
		nctx.softConstraints.add(new Tuple<BoolExpr, String>(ctx.mkEq( src_3, this.nctx.am.get("null")),"fw"));
		nctx.softConstraints.add(new Tuple<BoolExpr, String>(ctx.mkEq( src_4, this.nctx.am.get("null")),"fw"));
		nctx.softConstraints.add(new Tuple<BoolExpr, String>(ctx.mkEq( src_5, this.nctx.am.get("null")),"fw"));
		
		nctx.softConstraints.add(new Tuple<BoolExpr, String>(ctx.mkEq( dst_1, this.nctx.am.get("null")),"fw"));
		nctx.softConstraints.add(new Tuple<BoolExpr, String>(ctx.mkEq( dst_2, this.nctx.am.get("null")),"fw"));
		nctx.softConstraints.add(new Tuple<BoolExpr, String>(ctx.mkEq( dst_3, this.nctx.am.get("null")),"fw"));
		nctx.softConstraints.add(new Tuple<BoolExpr, String>(ctx.mkEq( dst_4, this.nctx.am.get("null")),"fw"));
		nctx.softConstraints.add(new Tuple<BoolExpr, String>(ctx.mkEq( dst_5, this.nctx.am.get("null")),"fw"));
		
		
		
		nctx.softConstraints.add(new Tuple<BoolExpr, String>(ctx.mkEq( proto_1, ctx.mkInt(0)),"fw"));
		nctx.softConstraints.add(new Tuple<BoolExpr, String>(ctx.mkEq( proto_2, ctx.mkInt(0)),"fw"));
		nctx.softConstraints.add(new Tuple<BoolExpr, String>(ctx.mkEq( proto_3, ctx.mkInt(0)),"fw"));
		nctx.softConstraints.add(new Tuple<BoolExpr, String>(ctx.mkEq( proto_4, ctx.mkInt(0)),"fw"));
		nctx.softConstraints.add(new Tuple<BoolExpr, String>(ctx.mkEq( proto_5, ctx.mkInt(0)),"fw"));
		
		nctx.softConstraints.add(new Tuple<BoolExpr, String>(ctx.mkEq( srcp_1, ctx.mkInt(0)),"fw"));
		nctx.softConstraints.add(new Tuple<BoolExpr, String>(ctx.mkEq( srcp_2, ctx.mkInt(0)),"fw"));
		nctx.softConstraints.add(new Tuple<BoolExpr, String>(ctx.mkEq( srcp_3, ctx.mkInt(0)),"fw"));
		nctx.softConstraints.add(new Tuple<BoolExpr, String>(ctx.mkEq( srcp_4, ctx.mkInt(0)),"fw"));
		nctx.softConstraints.add(new Tuple<BoolExpr, String>(ctx.mkEq( srcp_5, ctx.mkInt(0)),"fw"));
		
		nctx.softConstraints.add(new Tuple<BoolExpr, String>(ctx.mkEq( dstp_1, ctx.mkInt(0)),"fw"));
		nctx.softConstraints.add(new Tuple<BoolExpr, String>(ctx.mkEq( dstp_2, ctx.mkInt(0)),"fw"));
		nctx.softConstraints.add(new Tuple<BoolExpr, String>(ctx.mkEq( dstp_3, ctx.mkInt(0)),"fw"));
		nctx.softConstraints.add(new Tuple<BoolExpr, String>(ctx.mkEq( dstp_4, ctx.mkInt(0)),"fw"));
		nctx.softConstraints.add(new Tuple<BoolExpr, String>(ctx.mkEq( dstp_5, ctx.mkInt(0)),"fw"));
		
		
		
		
		acl_func = ctx.mkFuncDecl(fw + "_acl_func", new Sort[] { nctx.address, nctx.address }, ctx.mkBoolSort());
		
		constraints.add(ctx.mkForall(new Expr[] { n_0, p_0 }, ctx.mkImplies(
				(BoolExpr) nctx.send.apply(new Expr[] { fw, n_0, p_0 }),
				ctx.mkAnd(ctx.mkExists(new Expr[] { n_1 }, nctx.recv.apply(n_1, fw, p_0), 1, null, null, null, null),
						ctx.mkNot(ctx.mkOr(
											ctx.mkAnd(
													ctx.mkEq(nctx.pf.get("src").apply(p_0), src_1),
													ctx.mkEq(nctx.pf.get("dest").apply(p_0), dst_1)
													,ctx.mkEq(nctx.pf.get("proto").apply(p_0), proto_1)
													,ctx.mkEq((IntExpr)nctx.src_port.apply(p_0), srcp_1),
													ctx.mkEq((IntExpr)nctx.dest_port.apply(p_0), dstp_1)
													)
											,ctx.mkAnd(
													ctx.mkEq(nctx.pf.get("src").apply(p_0), src_2),
													ctx.mkEq(nctx.pf.get("dest").apply(p_0), dst_2)
													,ctx.mkEq(nctx.pf.get("proto").apply(p_0), proto_2)
													,ctx.mkEq((IntExpr)nctx.src_port.apply(p_0), srcp_2),
													ctx.mkEq((IntExpr)nctx.dest_port.apply(p_0), dstp_2)
													)
											,ctx.mkAnd(
													ctx.mkEq(nctx.pf.get("src").apply(p_0), src_3),
													ctx.mkEq(nctx.pf.get("dest").apply(p_0), dst_3)
													,ctx.mkEq(nctx.pf.get("proto").apply(p_0), proto_3)
													,ctx.mkEq((IntExpr)nctx.src_port.apply(p_0), srcp_3),
													ctx.mkEq((IntExpr)nctx.dest_port.apply(p_0), dstp_3)
													)
											,ctx.mkAnd(
													ctx.mkEq(nctx.pf.get("src").apply(p_0), src_4),
													ctx.mkEq(nctx.pf.get("dest").apply(p_0), dst_4)
													,ctx.mkEq(nctx.pf.get("proto").apply(p_0), proto_4)
													,ctx.mkEq((IntExpr)nctx.src_port.apply(p_0), srcp_4),
													ctx.mkEq((IntExpr)nctx.dest_port.apply(p_0), dstp_4)
													)
											,ctx.mkAnd(
													ctx.mkEq(nctx.pf.get("src").apply(p_0), src_5),
													ctx.mkEq(nctx.pf.get("dest").apply(p_0), dst_5)
													,ctx.mkEq(nctx.pf.get("proto").apply(p_0), proto_5)
													,ctx.mkEq((IntExpr)nctx.src_port.apply(p_0), srcp_5),
													ctx.mkEq((IntExpr)nctx.dest_port.apply(p_0), dstp_5)
													)
										   )
								
						))), 1, null, null, null, null));

		constraints.add(ctx.mkForall(new Expr[] { n_0, p_0 },
				ctx.mkImplies(ctx.mkAnd((BoolExpr) nctx.recv.apply(n_0, fw, p_0),
						ctx.mkNot(
									ctx.mkOr(
										ctx.mkAnd(
												ctx.mkEq(nctx.pf.get("src").apply(p_0), src_1),
												ctx.mkEq(nctx.pf.get("dest").apply(p_0), dst_1)
												,ctx.mkEq(nctx.pf.get("proto").apply(p_0), proto_1)
												,ctx.mkEq((IntExpr)nctx.src_port.apply(p_0), srcp_1),
												ctx.mkEq((IntExpr)nctx.dest_port.apply(p_0), dstp_1)
												)
										,ctx.mkAnd(
												ctx.mkEq(nctx.pf.get("src").apply(p_0), src_2),
												ctx.mkEq(nctx.pf.get("dest").apply(p_0), dst_2)
												,ctx.mkEq(nctx.pf.get("proto").apply(p_0), proto_2)
												,ctx.mkEq((IntExpr)nctx.src_port.apply(p_0), srcp_2),
												ctx.mkEq((IntExpr)nctx.dest_port.apply(p_0), dstp_2)
												)
										,ctx.mkAnd(
												ctx.mkEq(nctx.pf.get("src").apply(p_0), src_3),
												ctx.mkEq(nctx.pf.get("dest").apply(p_0), dst_3)
												,ctx.mkEq(nctx.pf.get("proto").apply(p_0), proto_3)
												,ctx.mkEq((IntExpr)nctx.src_port.apply(p_0), srcp_3),
												ctx.mkEq((IntExpr)nctx.dest_port.apply(p_0), dstp_3)
												)
										,ctx.mkAnd(
												ctx.mkEq(nctx.pf.get("src").apply(p_0), src_4),
												ctx.mkEq(nctx.pf.get("dest").apply(p_0), dst_4)
												,ctx.mkEq(nctx.pf.get("proto").apply(p_0), proto_4)
												,ctx.mkEq((IntExpr)nctx.src_port.apply(p_0), srcp_4),
												ctx.mkEq((IntExpr)nctx.dest_port.apply(p_0), dstp_4)
												)
										,ctx.mkAnd(
												ctx.mkEq(nctx.pf.get("src").apply(p_0), src_5),
												ctx.mkEq(nctx.pf.get("dest").apply(p_0), dst_5)
												,ctx.mkEq(nctx.pf.get("proto").apply(p_0), proto_5)
												,ctx.mkEq((IntExpr)nctx.src_port.apply(p_0), srcp_5),
												ctx.mkEq((IntExpr)nctx.dest_port.apply(p_0), dstp_5)
												)
									   )
						)), ctx.mkAnd(ctx.mkExists(new Expr[] { n_1 }, (BoolExpr) nctx.send.apply(new Expr[] { fw, n_1, p_0 }), 1, null, null, null, null))), 1, null, null, null, null));

	}

	private void aclConstraints(Optimize solver) {
		if (acls.size() == 0)
			return;
		Expr a_0 = ctx.mkConst(fw + "_firewall_acl_a_0", nctx.address);
		Expr a_1 = ctx.mkConst(fw + "_firewall_acl_a_1", nctx.address);

		BoolExpr[] acl_map = new BoolExpr[acls.size()];
		for (int y = 0; y < acls.size(); y++) {
			Tuple<DatatypeExpr, DatatypeExpr> tp = acls.get(y);
			acl_map[y] = ctx.mkOr(ctx.mkAnd(ctx.mkEq(a_0, tp._1), ctx.mkEq(a_1, tp._2)),
					ctx.mkAnd(ctx.mkEq(a_0, tp._2), ctx.mkEq(a_1, tp._1)));
		}
		// Constraint2 acl_func(a_0,a_1) == or(foreach ip1,ip2 in acl_map ((a_0
		// == ip1 && a_1 == ip2)||(a_0 == ip2 && a_1 == ip1)))
		solver.Add(ctx.mkForall(new Expr[] { a_0, a_1 }, ctx.mkEq(acl_func.apply(a_0, a_1), ctx.mkOr(acl_map)), 1, null,
				null, null, null));
	}
}