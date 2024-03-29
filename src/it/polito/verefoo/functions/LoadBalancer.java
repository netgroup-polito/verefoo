package it.polito.verefoo.functions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.DatatypeExpr;
import com.microsoft.z3.Optimize;

import it.polito.verefoo.allocation.AllocationNodeAP;
import it.polito.verefoo.graph.FlowPathAP;
import it.polito.verefoo.solver.NetContextAP;
import it.polito.verefoo.allocation.AllocationNodeMF;
import it.polito.verefoo.graph.FlowPathMF;
import it.polito.verefoo.solver.NetContextMF;
import it.polito.verefoo.graph.MaximalFlow;

/**
 * Load Balancer Model object
 *
 */
public class LoadBalancer extends GenericFunction {
	DatatypeExpr nat;
	List<String> serversIPAddresses;
	
	/**
	 * Constructor method for the Load Balancer class specific to Atomic Predicates
	 * @param source it is the node where the load balancer functions is installed
	 * @param ctx it is the z3 context
	 * @param nctx it is the NetContext object
	 */
	public LoadBalancer(AllocationNodeAP source, Context ctx, NetContextAP nctx) {
		isEndHost = false;
		this.sourceAP = source;
		this.ctx = ctx;
		this.nctxAP = nctx;
		nat = source.getZ3Name();
		constraints = new ArrayList<BoolExpr>();
		used = ctx.mkTrue();
		serversIPAddresses = source.getNode().getConfiguration().getLoadbalancer().getPool().stream().collect(Collectors.toList());	
	}

	/**
	 * Constructor method for the Load Balancer class specific to Maximal Flows
	 * @param source it is the node where the load balancer functions is installed
	 * @param ctx it is the z3 context
	 * @param nctx it is the NetContext object
	 */
	public LoadBalancer(AllocationNodeMF source, Context ctx, NetContextMF nctx) {
		isEndHost = false;
		this.sourceMF = source;
		this.ctx = ctx;
		this.nctxMF = nctx;
		nat = source.getZ3Name();
		constraints = new ArrayList<BoolExpr>();
		used = ctx.mkTrue();
		serversIPAddresses = source.getNode().getConfiguration().getLoadbalancer().getPool().stream().collect(Collectors.toList());	
	}

	/**
	 * This method creates the z3 constraints specific to Atomic Predicates.
	 * @param lbIp it is the IP address of the load balancer
	 */
	public void loadBalancerConfigurationAP(DatatypeExpr lbIp) {
    	for(Map<Integer, Integer> flowMap : sourceAP.getMapFlowIdAtomicPredicatesInInput().values()) {
    		for(Integer traffic : flowMap.values()) {
    			constraints.add(ctx.mkEq(nctxAP.deny.apply(sourceAP.getZ3Name(), ctx.mkInt(traffic)), ctx.mkFalse()));
    		}
    		
    	}
	}

	/**
	 * This method creates the z3 constraints specific to Maximal Flows.
	 * @param lbIp it is the IP address of the load balancer
	 */
	public void loadBalancerConfigurationMF(DatatypeExpr lbIp) {
		for(FlowPathMF flow : sourceMF.getCrossingFlows().values()) {
    		for(Entry<Integer, MaximalFlow> maximalFlowEntry: flow.getMaximalFlowsMap().entrySet()) {
    			constraints.add(ctx.mkEq(nctxMF.deny.apply(sourceMF.getZ3Name(), ctx.mkInt(maximalFlowEntry.getKey())), ctx.mkFalse()));
    		}
    	}
	}

	/**
	 * This method allows to wrap the method which adds the constraints inside Z3 solver
	 * @param solver Instance of Z3 solver
	 */
	@Override
	public void addContraints(Optimize solver) {
		BoolExpr[] constr = new BoolExpr[constraints.size()];
		solver.Add(constraints.toArray(constr));
	}
}
