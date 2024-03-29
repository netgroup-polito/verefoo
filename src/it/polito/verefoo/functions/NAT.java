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

import it.polito.verefoo.allocation.AllocationNodeAP;
import it.polito.verefoo.graph.FlowPathAP;
import it.polito.verefoo.graph.MaximalFlow;
import it.polito.verefoo.graph.Predicate;
import it.polito.verefoo.graph.Traffic;
import it.polito.verefoo.solver.NetContextAP;
import it.polito.verefoo.utils.APUtilsAP;
import it.polito.verefoo.allocation.AllocationNodeMF;
import it.polito.verefoo.graph.FlowPathMF;
import it.polito.verefoo.solver.NetContextMF;
import it.polito.verefoo.utils.APUtilsMF;
/**
 * NAT Model object
 *
 */
public class NAT extends GenericFunction {
	DatatypeExpr nat;
	List<String> private_addresses;
	List<GenericFunction> private_node;
	FuncDecl private_addr_func;
	
	/**
	 * Constructor method of the NAT class specific to Atomic Predicates
	 * @param source it is the node where the NAT is installed
	 * @param ctx it is the z3 context
	 * @param nctx it is the NetContext object
	 */
	public NAT(AllocationNodeAP source, Context ctx, NetContextAP nctx) {
		isEndHost = false;
		this.sourceAP = source;
		this.ctx = ctx;
		this.nctxAP = nctx;
		nat = source.getZ3Name();
		constraints = new ArrayList<BoolExpr>();
		used = ctx.mkTrue();
		private_addresses = source.getNode().getConfiguration().getNat().getSource().stream().collect(Collectors.toList());	
	}

	/**
	 * Constructor method of the NAT class specific to Maximal Flows
	 * @param source it is the node where the NAT is installed
	 * @param ctx it is the z3 context
	 * @param nctx it is the NetContext object
	 */
	public NAT(AllocationNodeMF source, Context ctx, NetContextMF nctx) {
		isEndHost = false;
		this.sourceMF = source;
		this.ctx = ctx;
		this.nctxMF = nctx;
		nat = source.getZ3Name();
		constraints = new ArrayList<BoolExpr>();
		used = ctx.mkTrue();
		private_addresses = source.getNode().getConfiguration().getNat().getSource().stream().collect(Collectors.toList());	
	}

	/**
	 * This method creates the hard constraints for the NAT configuration specific to Atomic Predicates.
	 * @param natIp
	 */
	public void natConfigurationAP() {	
		for(Map<Integer, Integer> flowMap : sourceAP.getMapFlowIdAtomicPredicatesInInput().values()) {
    		for(Integer traffic : flowMap.values()) {
    			constraints.add(ctx.mkEq(nctxAP.deny.apply(sourceAP.getZ3Name(), ctx.mkInt(traffic)), ctx.mkFalse()));
    		}	
    	}
	}

	/**
	 * This method creates the hard constraints for the NAT configuration specific to Maximal Flows
	 * @param natIp
	 */
	public void natConfigurationMF(DatatypeExpr natIp) {
		for(FlowPathMF flow : sourceMF.getCrossingFlows().values()) {
    		for(Entry<Integer, MaximalFlow> maximalFlowEntry: flow.getMaximalFlowsMap().entrySet()) {
    			constraints.add(ctx.mkEq(nctxMF.deny.apply(sourceMF.getZ3Name(), ctx.mkInt(maximalFlowEntry.getKey())), ctx.mkFalse()));
			}
		}
	}

	@Override
	public void addContraints(Optimize solver) {
		BoolExpr[] constr = new BoolExpr[constraints.size()];
		solver.Add(constraints.toArray(constr));
	}
}
