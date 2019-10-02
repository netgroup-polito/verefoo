package it.polito.verefoo.functions;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.DatatypeExpr;
import com.microsoft.z3.Optimize;

import it.polito.verefoo.allocation.AllocationNode;
import it.polito.verefoo.solver.NetContext;

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
		
	
	}

	
	@Override
	public void addContraints(Optimize solver) {
		BoolExpr[] constr = new BoolExpr[constraints.size()];
		solver.Add(constraints.toArray(constr));
	}
}
