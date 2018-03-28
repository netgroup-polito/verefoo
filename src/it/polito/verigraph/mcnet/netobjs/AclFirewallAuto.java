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
import java.util.HashMap;
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
	private Integer nRules;
	public BoolExpr fwUsed;
	public List<BoolExpr> rules;
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
		fwUsed = ctx.mkBoolConst(fw+"_used");
		rules = new ArrayList<>();
		if(args[0].length > 3)
			nRules = (Integer) args[0][3];
		else
			nRules = 5;
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

	private void firewallSendRules() {
		Expr p_0 = ctx.mkConst(fw + "_firewall_send_p_0", nctx.packet);
		Expr n_0 = ctx.mkConst(fw + "_firewall_send_n_0", nctx.node);
		Expr n_1 = ctx.mkConst(fw + "_firewall_send_n_1", nctx.node);
		List<BoolExpr> implications1 = new ArrayList<>();
		List<BoolExpr> implications2 = new ArrayList<>();
		for(int i = 0; i < nRules; i++){
			Expr src = ctx.mkConst(fw + "_auto_src_"+i, nctx.address);
			Expr dst = ctx.mkConst(fw + "_auto_dst_"+i, nctx.address);
			//Expr proto = ctx.mkConst(fw + "_auto_proto_"+i, ctx.mkIntSort());
			//Expr srcp = ctx.mkConst(fw + "_auto_srcp_"+i, ctx.mkIntSort());
			//Expr dstp = ctx.mkConst(fw + "_auto_dstp_"+i, ctx.mkIntSort());
			nctx.softConstraints.add(new Tuple<BoolExpr, String>(ctx.mkEq( src, this.nctx.am.get("null")),"fw"));
			nctx.softConstraints.add(new Tuple<BoolExpr, String>(ctx.mkEq( dst, this.nctx.am.get("null")),"fw"));
			//nctx.softConstraints.add(new Tuple<BoolExpr, String>(ctx.mkEq( proto, ctx.mkInt(0)),"fw"));
			//nctx.softConstraints.add(new Tuple<BoolExpr, String>(ctx.mkEq( srcp, ctx.mkInt(0)),"fw"));
			//nctx.softConstraints.add(new Tuple<BoolExpr, String>(ctx.mkEq( dstp, ctx.mkInt(0)),"fw"));
			BoolExpr rule = ctx.mkAnd(
					ctx.mkEq(nctx.pf.get("src").apply(p_0), src),
					ctx.mkEq(nctx.pf.get("dest").apply(p_0), dst)
					//ctx.mkEq(nctx.pf.get("proto").apply(p_0), proto),
					//ctx.mkEq((IntExpr)nctx.src_port.apply(p_0), srcp),
					//ctx.mkEq((IntExpr)nctx.dest_port.apply(p_0), dstp)
					);
			rules.add(rule);
			
		}
		acl_func = ctx.mkFuncDecl(fw + "_acl_func", new Sort[] { nctx.address, nctx.address }, ctx.mkBoolSort());
		BoolExpr[] tmp = new BoolExpr[rules.size()];
		constraints.add(ctx.mkForall(new Expr[] { n_0, p_0 }, ctx.mkImplies(
				(BoolExpr) nctx.send.apply(new Expr[] { fw, n_0, p_0 }),
				ctx.mkAnd(ctx.mkExists(new Expr[] { n_1 }, nctx.recv.apply(n_1, fw, p_0), 1, null, null, null, null),
						  ctx.mkNot(
								  ctx.mkOr(
										  rules.toArray(tmp)
										  )		
								  ))), 1, null, null, null, null));
		BoolExpr[] tmp2 = new BoolExpr[rules.size()];
		constraints.add(ctx.mkForall(new Expr[] { n_0, p_0 },
				ctx.mkImplies(ctx.mkAnd((BoolExpr) nctx.recv.apply(n_0, fw, p_0),
						ctx.mkNot(
									ctx.mkOr(
											rules.toArray(tmp2)
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