package it.polito.verefoo.functions;

import java.util.ArrayList;
import java.util.Map;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.DatatypeExpr;
import com.microsoft.z3.Optimize;

import it.polito.verefoo.allocation.AllocationNode;
import it.polito.verefoo.solver.NetContext;

/** Represents a Forwarder
 *
 */
public class TrafficMonitor extends GenericFunction{
	DatatypeExpr trafficMonitor;
	/**
	 * Public constructor for the Traffic Monitor
	 * @param source It is the Allocation Node on which the forwarder is put
	 * @param ctx It is the Z3 Context in which the model is generated
	 * @param nctx It is the NetContext object to which constraints are sent
	 */
	public TrafficMonitor(AllocationNode source, Context ctx, NetContext nctx) {
		trafficMonitor = source.getZ3Name();
		this.source = source;
		this.ctx = ctx;
		this.nctx = nctx;
		constraints = new ArrayList<BoolExpr>();
		isEndHost = false;
		used = ctx.mkTrue();
	}


    /**
     * This method creates the forwarding rules for the traffic monitor.
     * Since it does not provide any filtering behaviour, the traffic monitor sends all the received packets.
     */
    public void trafficMonitorSendRules (){
    	
    	for(Map<Integer, Integer> flowMap : source.getMapFlowIdAtomicPredicatesInInput().values()) {
    		for(Integer traffic : flowMap.values()) {
    			constraints.add(ctx.mkEq(nctx.deny.apply(source.getZ3Name(), ctx.mkInt(traffic)), ctx.mkFalse()));
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

