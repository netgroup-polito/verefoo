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
 * Load Balancer Model object
 *
 */
public class LoadBalancer extends GenericFunction {
	DatatypeExpr nat;
	List<String> serversIPAddresses;
	
	public LoadBalancer(AllocationNode source, Context ctx, NetContext nctx) {
		isEndHost = false;
		this.source = source;
		this.ctx = ctx;
		this.nctx = nctx;
		nat = source.getZ3Name();
		constraints = new ArrayList<BoolExpr>();
		used = ctx.mkTrue();
		serversIPAddresses = source.getNode().getConfiguration().getLoadbalancer().getPool().stream().collect(Collectors.toList());	
	}



	public void loadBalancerConfiguration(DatatypeExpr natIp) {
		
		for(TrafficFlow tf : source.getRequirements().values()) {
    	
			if(serversIPAddresses.contains(tf.getProperty().getSrc()) || serversIPAddresses.contains(tf.getProperty().getDst())) {
				constraints.add(ctx.mkEq(nctx.deny.apply(source.getZ3Name(), ctx.mkInt(tf.getIdRequirement())), ctx.mkFalse()));
			} else {
				constraints.add(ctx.mkEq(nctx.deny.apply(source.getZ3Name(), ctx.mkInt(tf.getIdRequirement())), ctx.mkTrue()));
			}
		
		}
	
	}

	
	@Override
	public void addContraints(Optimize solver) {
		BoolExpr[] constr = new BoolExpr[constraints.size()];
		solver.Add(constraints.toArray(constr));
	}
}
