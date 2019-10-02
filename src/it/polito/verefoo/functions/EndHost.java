package it.polito.verefoo.functions;

import java.util.ArrayList;
import java.util.List;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.DatatypeExpr;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Optimize;

import it.polito.verefoo.allocation.AllocationNode;
import it.polito.verefoo.graph.Flow;
import it.polito.verefoo.solver.NetContext;
import it.polito.verefoo.utils.PacketModel;

public class EndHost extends GenericFunction {

    List<BoolExpr> constraints = new ArrayList<BoolExpr>();
    Context ctx;
    DatatypeExpr politoEndHost;
    NetContext nctx;
    PacketModel packet;
    AllocationNode source;
    Expr n_0;
    Expr p_0;

    /**
     * Public constructor of EndHost class
     * @param source it is the AllocationNode on which a client or server is installed
     * @param ctx it is the z3 Context variable
     * @param nctx it is the NetContext object storing the needed information about z3 IP address variables
     */
    public EndHost(AllocationNode source, Context ctx, NetContext nctx) { 
    	 this.source = source;
    	 this.ctx = ctx;
    	 this.nctx = nctx;
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
    public void installEndHost (PacketModel packet){
   
        return;
    }
    
    public void configureEndHost() {
    	for(Flow flow : source.getFlows().values()) {
    		constraints.add(ctx.mkEq(nctx.deny.apply(source.getZ3Name(), ctx.mkInt(flow.getIdFlow())), ctx.mkFalse()));
    	}
    }



}