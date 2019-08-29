package it.polito.verigraph.functions;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.DatatypeExpr;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Optimize;

import it.polito.verefoo.allocation.AllocationNode;
import it.polito.verefoo.graph.SecurityRequirement;
import it.polito.verigraph.solver.NetContext;

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
     * This method creates the forwarding rules for the forwarder.
     * Since it does not provide any filtering behaviour, the forwarders sends all the received packets.
     */
    public void forwarderSendRules (){
    	
    	for(SecurityRequirement sr : source.getRequirements().values()) {
    		constraints.add(ctx.mkEq(nctx.deny.apply(source.getZ3Name(), ctx.mkInt(sr.getIdRequirement())), ctx.mkFalse()));
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

