package it.polito.verefoo.functions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.DatatypeExpr;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.Optimize;

import it.polito.verefoo.allocation.AllocationNode;
import it.polito.verefoo.graph.FlowPath;
import it.polito.verefoo.graph.MaximalFlow;
import it.polito.verefoo.graph.Predicate;
import it.polito.verefoo.graph.Traffic;
import it.polito.verefoo.solver.NetContext;
import it.polito.verefoo.utils.APUtils;

/**
 * NAT Model object
 *
 */
public class NAT extends GenericFunction {
	DatatypeExpr nat;
	List<String> private_addresses;
	List<GenericFunction> private_node;
	FuncDecl private_addr_func;
	//APUtils aputils = new APUtils(); // not needed?
	
	/**
	 * Constructor method of the NAT class
	 * @param source it is the node where the NAT is installed
	 * @param ctx it is the z3 context
	 * @param nctx it is the NetContext object
	 */
	public NAT(AllocationNode source, Context ctx, NetContext nctx) {
		isEndHost = false;
		this.source = source;
		this.ctx = ctx;
		this.nctx = nctx;
		nat = source.getZ3Name();
		constraints = new ArrayList<BoolExpr>();
		//private_addr_func = ctx.mkFuncDecl(nat + "_nat_func", nctx.addressType, ctx.mkBoolSort()); 
		used = ctx.mkTrue();
		private_addresses = source.getNode().getConfiguration().getNat().getSource().stream().collect(Collectors.toList());	
	}



	/**
	 * Atomic Predicate Algorithm
	 * This method creates the hard constraints for the NAT configuration and status
	 * @param natIp
	 */
	public void natConfigurationAP() {	
		for(Map<Integer, Integer> flowMap : source.getMapFlowIdAtomicPredicatesInInput().values()) {
    		for(Integer traffic : flowMap.values()) {
    			constraints.add(ctx.mkEq(nctx.deny.apply(source.getZ3Name(), ctx.mkInt(traffic)), ctx.mkFalse()));
    		}	
    	}
	}

	/**
	 * Maximal Flows Algorithm
	 * This method creates the hard constraints for the NAT configuration and status
	 * @param natIp
	 */
	public void natConfigurationMF(DatatypeExpr natIp) {
		for(FlowPath flow : source.getCrossingFlows().values()) {
    		for(Entry<Integer, MaximalFlow> maximalFlowEntry: flow.getMaximalFlowsMap().entrySet()) {
    			constraints.add(ctx.mkEq(nctx.deny.apply(source.getZ3Name(), ctx.mkInt(maximalFlowEntry.getKey())), ctx.mkFalse()));
			}
		}
	}

	@Override
	public void addContraints(Optimize solver) {
		BoolExpr[] constr = new BoolExpr[constraints.size()];
		solver.Add(constraints.toArray(constr));
	}
}
