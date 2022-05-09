package it.polito.verefoo.functions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.DatatypeExpr;
import com.microsoft.z3.Optimize;

import it.polito.verefoo.allocation.AllocationNode;
import it.polito.verefoo.graph.FlowPath;
import it.polito.verefoo.solver.NetContext;

/**
 * Load Balancer Model object
 *
 */
public class LoadBalancer extends GenericFunction {
	DatatypeExpr nat;
	List<String> serversIPAddresses;
	
	/**
	 * Constructor method for the Load Balancer class
	 * @param source it is the node where the load balancer functions is installed
	 * @param ctx it is the z3 context
	 * @param nctx it is the NetContext object
	 */
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



	/**
	 * This method creates the z3 constraints.
	 * @param lbIp it is the IP address of the load balancer
	 */
	public void loadBalancerConfiguration(DatatypeExpr lbIp) {
		

    	for(Map<Integer, Integer> flowMap : source.getMapFlowIdAtomicPredicatesInInput().values()) {
    		for(Integer traffic : flowMap.values()) {
    			constraints.add(ctx.mkEq(nctx.deny.apply(source.getZ3Name(), ctx.mkInt(traffic)), ctx.mkFalse()));
    		}
    		
    	}
	}

	/**
	 * This method allows to wrap the method which adds the constraints inside Z3 solver
	 * @param solver Istance of Z3 solver
	 */
	@Override
	public void addContraints(Optimize solver) {
		BoolExpr[] constr = new BoolExpr[constraints.size()];
		solver.Add(constraints.toArray(constr));
	}
}
