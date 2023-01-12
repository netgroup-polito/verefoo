package it.polito.verefoo.functions;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.DatatypeExpr;
import com.microsoft.z3.Optimize;

import it.polito.verefoo.allocation.AllocationNodeAP;
import it.polito.verefoo.solver.NetContextAP;
import it.polito.verefoo.graph.FlowPathAP;
import it.polito.verefoo.allocation.AllocationNodeMF;
import it.polito.verefoo.solver.NetContextMF;
import it.polito.verefoo.graph.FlowPathMF;
import it.polito.verefoo.graph.MaximalFlow;

/** Represents a Forwarder
 *
 */
public class Forwarder extends GenericFunction{
	DatatypeExpr forwarder;
	/**
	 * Public constructor for the Forwarder
	 * @param source It is the Allocation Node on which the forwarder is put
	 * @param ctx It is the Z3 Context in which the model is generated
	 * @param nctx It is the NetContext object to which constraints are sent
	 */
	public Forwarder(AllocationNode source, Context ctx, NetContext nctx) {
		forwarder = source.getZ3Name();
		this.source = source;
		this.ctx = ctx;
		this.nctx = nctx;
		constraints = new ArrayList<BoolExpr>();
		isEndHost = false;
		used = ctx.mkTrue();
	}


    /**
	 * Atomic Predicate algorithm.
     * This method creates the forwarding rules for the forwarder.
     * Since it does not provide any filtering behaviour, the forwarders sends all the received packets.
     * deny(forwarder, t) = false
     */
    public void forwarderSendRulesAP (){
    	
    	for(Map<Integer, Integer> flowMap : source.getMapFlowIdAtomicPredicatesInInput().values()) {
    		for(Integer traffic : flowMap.values()) {
    			constraints.add(ctx.mkEq(nctx.deny.apply(source.getZ3Name(), ctx.mkInt(traffic)), ctx.mkFalse()));
    		}
    		
    	}
    	
    }
    
    /**
	 * Maximal Flow algorithm.
     * This method creates the forwarding rules for the forwarder.
     * Since it does not provide any filtering behaviour, the forwarders sends all the received packets.
     * deny(forwarder, t) = false
     */
    public void forwarderSendRulesMF (){
    	for(FlowPath flow : source.getCrossingFlows().values()) {
    		for(Entry<Integer, MaximalFlow> maximalFlowEntry: flow.getMaximalFlowsMap().entrySet()) {
    			constraints.add(ctx.mkEq(nctx.deny.apply(source.getZ3Name(), ctx.mkInt(maximalFlowEntry.getKey())), ctx.mkFalse()));
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

