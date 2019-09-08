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
import it.polito.verefoo.graph.TrafficFlow;
import it.polito.verefoo.jaxb.Property;
import it.polito.verigraph.solver.NetContext;

/**
 * NAT Model object
 *
 */
public class NAT extends GenericFunction {
	DatatypeExpr nat;
	List<String> private_addresses;
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
		used = ctx.mkTrue();
		private_addresses = source.getNode().getConfiguration().getNat().getSource().stream().collect(Collectors.toList());	
	}



	public void natConfiguration(DatatypeExpr natIp) {
		for(TrafficFlow tf : source.getRequirements().values()) {
    		//constraints.add(ctx.mkEq(nctx.deny.apply(source.getZ3Name(), ctx.mkInt(sr.getIdRequirement())), ctx.mkFalse()));
			Property property = tf.getCrossedTrafficFlow(source.getNode().getName());
			if(private_addresses.contains(property.getSrc())) {
				constraints.add(ctx.mkEq(nctx.deny.apply(source.getZ3Name(), ctx.mkInt(tf.getIdRequirement())), ctx.mkFalse()));
			} else {
				
				boolean statusFound = false;
				TrafficFlow status = null;
				for(TrafficFlow tf2 : source.getRequirements().values()) {
					Property property2 = tf2.getCrossedTrafficFlow(source.getNode().getName());
					if(property.getSrc().equals(property2.getDst()) && property.getDst().equals(property2.getSrc()) && property.getSrcPort().equals(property2.getDstPort())) {
						statusFound = true;
						status = tf2;
					}
				}
				
				if(statusFound) {

					List<BoolExpr> singleConstraints = new ArrayList<>();
					for(AllocationNode node : status.getPath().getNodes()) {
						singleConstraints.add(ctx.mkImplies(node.getPlacedNF().getUsed(), ctx.mkEq( (BoolExpr)nctx.deny.apply(node.getZ3Name(), ctx.mkInt(status.getIdRequirement())), ctx.mkFalse())));
						if(node.getNode().getName().equals(source.getNode().getName())) break;
					}
					
					BoolExpr[] arrayConstraints = new BoolExpr[singleConstraints.size()];
					BoolExpr andConstraint = ctx.mkAnd(singleConstraints.toArray(arrayConstraints));
					
					constraints.add(ctx.mkImplies(andConstraint, ctx.mkEq(nctx.deny.apply(source.getZ3Name(), ctx.mkInt(tf.getIdRequirement())), ctx.mkFalse())));
					constraints.add(ctx.mkImplies(ctx.mkNot(andConstraint), ctx.mkEq(nctx.deny.apply(source.getZ3Name(), ctx.mkInt(tf.getIdRequirement())), ctx.mkTrue())));
				}else {
					constraints.add(ctx.mkEq(nctx.deny.apply(source.getZ3Name(), ctx.mkInt(tf.getIdRequirement())), ctx.mkTrue()));
				}
				
				
			}
		
		}
	

	}


	/*public void setInternalAddress() {
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
	}*/

	
	@Override
	public void addContraints(Optimize solver) {
		BoolExpr[] constr = new BoolExpr[constraints.size()];
		solver.Add(constraints.toArray(constr));
	}
}
