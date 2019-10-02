package it.polito.verefoo.functions;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.DatatypeExpr;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.Optimize;

import it.polito.verefoo.allocation.AllocationNode;
import it.polito.verefoo.graph.Flow;
import it.polito.verefoo.graph.Traffic;
import it.polito.verefoo.solver.NetContext;

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
		for(Flow flow : source.getFlows().values()) {
			Traffic traffic = flow.getCrossedTraffic(source.getNode().getName());
			if(private_addresses.contains(traffic.getIPSrc())) {
				constraints.add(ctx.mkEq(nctx.deny.apply(source.getZ3Name(), ctx.mkInt(flow.getIdFlow())), ctx.mkFalse()));
			} else {
				
				boolean statusFound = false;
				Flow status = null;
				for(Flow flow2 : source.getFlows().values()) {
					Traffic traffic2 = flow2.getCrossedTraffic(source.getNode().getName());
					if(traffic.getIPSrc().equals(traffic2.getIPDst()) && traffic.getIPDst().equals(traffic2.getIPSrc()) && traffic.getpSrc().equals(traffic2.getpDst())) {
						statusFound = true;
						status = flow2;
					}
				}
				
				if(statusFound) {

					List<BoolExpr> singleConstraints = new ArrayList<>();
					for(AllocationNode node : status.getPath().getNodes()) {
						singleConstraints.add(ctx.mkImplies(node.getPlacedNF().getUsed(), ctx.mkEq( (BoolExpr)nctx.deny.apply(node.getZ3Name(), ctx.mkInt(status.getIdFlow())), ctx.mkFalse())));
						if(node.getNode().getName().equals(source.getNode().getName())) break;
					}
					
					BoolExpr[] arrayConstraints = new BoolExpr[singleConstraints.size()];
					BoolExpr andConstraint = ctx.mkAnd(singleConstraints.toArray(arrayConstraints));
					
					constraints.add(ctx.mkImplies(andConstraint, ctx.mkEq(nctx.deny.apply(source.getZ3Name(), ctx.mkInt(flow.getIdFlow())), ctx.mkFalse())));
					constraints.add(ctx.mkImplies(ctx.mkNot(andConstraint), ctx.mkEq(nctx.deny.apply(source.getZ3Name(), ctx.mkInt(flow.getIdFlow())), ctx.mkTrue())));
				}else {
					constraints.add(ctx.mkEq(nctx.deny.apply(source.getZ3Name(), ctx.mkInt(flow.getIdFlow())), ctx.mkTrue()));
				}
				
				
			}
		
		}
	

	}

	
	@Override
	public void addContraints(Optimize solver) {
		BoolExpr[] constr = new BoolExpr[constraints.size()];
		solver.Add(constraints.toArray(constr));
	}
}
