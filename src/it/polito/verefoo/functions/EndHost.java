package it.polito.verefoo.functions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.DatatypeExpr;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Optimize;

import it.polito.verefoo.graph.MaximalFlow;
import it.polito.verefoo.allocation.AllocationNodeAP;
import it.polito.verefoo.graph.FlowPathAP;
import it.polito.verefoo.solver.NetContextAP;
import it.polito.verefoo.allocation.AllocationNodeMF;
import it.polito.verefoo.graph.FlowPathMF;
import it.polito.verefoo.solver.NetContextMF;


/** Represents a EndHost functionality
*
*
*/

public class EndHost extends GenericFunction {

    List<BoolExpr> constraints = new ArrayList<BoolExpr>();
    Context ctx;
    DatatypeExpr politoEndHost;
    NetContextAP nctxAP;
    NetContextMF nctxMF;
    AllocationNodeAP sourceAP;
    AllocationNodeMF sourceMF;
    Expr n_0;
    Expr p_0;

    /**
     * Public constructor of EndHost class specific to Atomic Predicates
     * @param source it is the AllocationNode on which a client or server is installed
     * @param ctx it is the z3 Context variable
     * @param nctx it is the NetContext object storing the needed information about z3 IP address variables for Atomic Predicates
     */
    public EndHost(AllocationNodeAP source, Context ctx, NetContextAP nctx) { 
    	 this.sourceAP = source;
    	 this.ctx = ctx;
    	 this.nctxAP = nctx;
         this.isEndHost = true;
         politoEndHost = source.getZ3Name();
         n_0 = ctx.mkConst("PolitoEndHost_"+politoEndHost+"_n_0", nctx.nodeType);
         used = ctx.mkTrue();
 
    }

    /**
     * Public constructor of EndHost class specific to Maximal Flows
     * @param source it is the AllocationNode on which a client or server is installed
     * @param ctx it is the z3 Context variable
     * @param nctx it is the NetContext object storing the needed information about z3 IP address variables for Maximal Flows 
     */
    public EndHost(AllocationNodeMF source, Context ctx, NetContextMF nctx) { 
    	 this.sourceMF = source;
    	 this.ctx = ctx;
    	 this.nctxMF = nctx;
         this.isEndHost = true;
         politoEndHost = source.getZ3Name();
         n_0 = ctx.mkConst("PolitoEndHost_"+politoEndHost+"_n_0", nctx.nodeType);
         used = ctx.mkTrue();
 
    }

	/* (non-Javadoc)
	 * @see it.polito.verigraph.functions.GenericFunction#addContraints(com.microsoft.z3.Optimize)
	 * This method allows to add all the constraints in the z3 solver
	 */
	@Override
	public void addContraints(Optimize solver) {
		 BoolExpr[] constr = new BoolExpr[constraints.size()];
	        solver.Add(constraints.toArray(constr));
	}


    
    /**
     * This method sets some constraints about which packet can be configured
     * Fields that can be configured -> "dest","body","seq","proto","emailFrom","url","options"
     * @param packet it is the packet whose fields, if defined, must match with the z3 predicates
     */
    public void installEndHost (){
   
        return;
    }
    
    /**
     * Configuration for Endhost in Atomic Predicates by adding constraints
     */
    public void configureEndHostAP() {
    	for(Map<Integer, Integer> flowMap : sourceAP.getMapFlowIdAtomicPredicatesInInput().values()) {
    		for(Integer traffic : flowMap.values()) {
    			constraints.add(ctx.mkEq(nctxAP.deny.apply(sourceAP.getZ3Name(), ctx.mkInt(traffic)), ctx.mkFalse()));
    		}
    	}
    	constraints.add(ctx.mkEq(nctxAP.deny.apply(sourceAP.getZ3Name(), ctx.mkInt(-1)), ctx.mkFalse()));
    }

    /**
     * Configuration for Endhost in Maximal Flows by adding constraints
     */
    public void configureEndHostMF() {
    	for(FlowPathMF flow : sourceMF.getCrossingFlows().values()) {
    		for(Entry<Integer, MaximalFlow> maximalFlowEntry: flow.getMaximalFlowsMap().entrySet()) {
    			constraints.add(ctx.mkEq(nctxMF.deny.apply(sourceMF.getZ3Name(), ctx.mkInt(maximalFlowEntry.getKey())), ctx.mkFalse()));
    		}
    	}
    }

}