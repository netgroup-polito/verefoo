package it.polito.verigraph.functions;

import java.util.ArrayList;
import java.util.List;
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



	public void natConfiguration(DatatypeExpr natIp) {
		
		// to be changed

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
