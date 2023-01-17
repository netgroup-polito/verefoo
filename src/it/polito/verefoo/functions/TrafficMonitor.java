package it.polito.verefoo.functions;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.DatatypeExpr;
import com.microsoft.z3.Optimize;

import it.polito.verefoo.allocation.AllocationNodeAP;
import it.polito.verefoo.graph.FlowPathAP;
import it.polito.verefoo.graph.MaximalFlow;
import it.polito.verefoo.solver.NetContextAP;
import it.polito.verefoo.allocation.AllocationNodeMF;
import it.polito.verefoo.graph.FlowPathMF;
import it.polito.verefoo.solver.NetContextMF;

/** Represents a Forwarder
 *
 */
public class TrafficMonitor extends GenericFunction{
	DatatypeExpr trafficMonitor;
	/**
	 * Public constructor for the Traffic Monitor specific to Atomic Predicates
	 * @param source It is the Allocation Node on which the forwarder is put
	 * @param ctx It is the Z3 Context in which the model is generated
	 * @param nctx It is the NetContext object to which constraints are sent
	 */
	public TrafficMonitor(AllocationNodeAP source, Context ctx, NetContextAP nctx) {
		trafficMonitor = source.getZ3Name();
		this.sourceAP = source;
		this.ctx = ctx;
		this.nctxAP = nctx;
		constraints = new ArrayList<BoolExpr>();
		isEndHost = false;
		used = ctx.mkTrue();
	}

	/**
	 * Public constructor for the Traffic Monitor specific to Maximal Flows
	 * @param source It is the Allocation Node on which the forwarder is put
	 * @param ctx It is the Z3 Context in which the model is generated
	 * @param nctx It is the NetContext object to which constraints are sent
	 */
	public TrafficMonitor(AllocationNodeMF source, Context ctx, NetContextMF nctx) {
		trafficMonitor = source.getZ3Name();
		this.sourceMF = source;
		this.ctx = ctx;
		this.nctxMF = nctx;
		constraints = new ArrayList<BoolExpr>();
		isEndHost = false;
		used = ctx.mkTrue();
	}


    /**
     * This method creates the forwarding rules for the traffic monitor specific to Atomic Predicates.
     * Since it does not provide any filtering behavior, the traffic monitor sends all the received packets.
     */
    public void trafficMonitorSendRulesAP (){
    	
    	for(Map<Integer, Integer> flowMap : sourceAP.getMapFlowIdAtomicPredicatesInInput().values()) {
    		for(Integer traffic : flowMap.values()) {
    			constraints.add(ctx.mkEq(nctxAP.deny.apply(sourceAP.getZ3Name(), ctx.mkInt(traffic)), ctx.mkFalse()));
    		}
    		
    	}
    	
    }
    
    /**
     * This method creates the forwarding rules for the traffic monitor specific to Maximal Flows.
     * Since it does not provide any filtering behaviour, the traffic monitor sends all the received packets.
     */
    public void trafficMonitorSendRulesMF (){
    	
    	for(FlowPathMF flow : sourceMF.getCrossingFlows().values()) {
    		for(Entry<Integer, MaximalFlow> maximalFlowEntry: flow.getMaximalFlowsMap().entrySet()) {
    			constraints.add(ctx.mkEq(nctxMF.deny.apply(sourceMF.getZ3Name(), ctx.mkInt(maximalFlowEntry.getKey())), ctx.mkFalse()));
    		}
    	}
    	
    }
 
	/**
	 * This method allows to wrap the method which adds the constraints inside Z3 solver
	 * @param solver instance of Z3 solver
	 */
	@Override
	public void addContraints(Optimize solver) {
		BoolExpr[] constr = new BoolExpr[constraints.size()];
	    solver.Add(constraints.toArray(constr));
	}

  
}

