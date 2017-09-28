/*
* Copyright 2016 Politecnico di Torino
 * Authors:
 * Project Supervisor and Contact: Riccardo Sisto (riccardo.sisto@polito.it)
 * 
 * This file is part of Verigraph.
 * 
 * Verigraph is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 * 
 * Verigraph is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public
 * License along with Verigraph.  If not, see
 * <http://www.gnu.org/licenses/>.
 */
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
 * This is just a wrapper around z3 instances. The idea is that by using this we
 * perhaps need to have fewer (or no) ifs to deal with the case where we don't
 * instantiate an object for a node
 * 
 * @author Giacomo Costantini
 *
 */
public class Classifier extends NetworkObject {
	Context ctx;
	DatatypeExpr fw;
	ArrayList<Tuple<DatatypeExpr, DatatypeExpr>> acls;
	Network net;
	NetContext nctx;
	FuncDecl acl_func;
	List<BoolExpr> constraints;

	public Classifier(Context ctx, Object[]... args) {
		super(ctx, args);
	}

	@Override
	protected void init(Context ctx, Object[]... args) {
		this.ctx = ctx;
		isEndHost = false;
		constraints = new ArrayList<BoolExpr>();
		this.z3Node = ((NetworkObject) args[0][0]).getZ3Node();
		fw = z3Node;
		net = (Network) args[0][1];
		nctx = (NetContext) args[0][2];
		classifierSendRules();
	}

	private void classifierSendRules() {
		Expr p_0 = ctx.mkConst(fw + "_classifier_send_p_0", nctx.packet);
		Expr n_0 = ctx.mkConst(fw + "_classifier_send_n_0", nctx.node);
		Expr n_1 = ctx.mkConst(fw + "_classifier_send_n_1", nctx.node);
		// IntExpr t_0 = ctx.mkIntConst(fw+"_firewall_send_t_0");
		// IntExpr t_1 = ctx.mkIntConst(fw+"_firewall_send_t_1");
		acl_func = ctx.mkFuncDecl(fw + "_acl_func", new Sort[] { nctx.address, nctx.address }, ctx.mkBoolSort());

		// Constraint1 send(fw, n_0, p, t_0) -> (exist n_1,t_1 : (recv(n_1, fw,
		// p, t_1) &&
		// t_1 < t_0 && !acl_func(p.src,p.dest))
		constraints.add(ctx.mkForall(new Expr[] { n_0, p_0 }, ctx.mkImplies(
				(BoolExpr) nctx.send.apply(new Expr[] { fw, n_0, p_0 }), ctx.mkExists(new Expr[] { n_1 }, ctx.mkAnd(
						ctx.mkExists(new Expr[] { n_1 }, nctx.recv.apply(n_1, fw, p_0), 1, null, null, null, null)

				), 1, null, null, null, null)), 1, null, null, null, null));

	}

	@Override
	public DatatypeExpr getZ3Node() {
		return z3Node;
	}

	@Override
	protected void addConstraints(Optimize solver) {
		// TODO Auto-generated method stub

	}
}
