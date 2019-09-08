package it.polito.verigraph.functions;

import java.util.ArrayList;
import java.util.List;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.DatatypeExpr;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Optimize;

import it.polito.verefoo.allocation.AllocationNode;
import it.polito.verefoo.graph.TrafficFlow;
import it.polito.verigraph.extra.PacketModel;
import it.polito.verigraph.solver.NetContext;

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
         p_0 = ctx.mkConst("PolitoEndHost_"+politoEndHost+"_p_0", nctx.packetType);
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
    	
        BoolExpr predicatesOnPktFields = ctx.mkTrue();
        
        /*
         * If the packet has some configured elements, they must match the corresponding packet elements.
         * predicatesOnPktFields is the AND of these correspondences 
         */
        /*if(packet!=null){
        	if(packet.getIp_dest() != null)
                predicatesOnPktFields = ctx.mkAnd(predicatesOnPktFields, ctx.mkEq(nctx.functionsMap.get("dest").apply(p_0), packet.getIp_dest()));
            if(packet.getBody() != null)
                predicatesOnPktFields = ctx.mkAnd(predicatesOnPktFields, ctx.mkEq(nctx.functionsMap.get("body").apply(p_0), ctx.mkInt(packet.getBody())));
            if(packet.getEmailFrom() != null)
                predicatesOnPktFields = ctx.mkAnd(predicatesOnPktFields, ctx.mkEq(nctx.functionsMap.get("emailFrom").apply(p_0), ctx.mkInt(packet.getEmailFrom())));
            if(packet.getOptions() != null)
                predicatesOnPktFields = ctx.mkAnd(predicatesOnPktFields, ctx.mkEq(nctx.functionsMap.get("options").apply(p_0), ctx.mkInt(packet.getOptions())));
            if(packet.getProto() != null)
                predicatesOnPktFields = ctx.mkAnd(predicatesOnPktFields, ctx.mkEq(nctx.functionsMap.get("proto").apply(p_0), ctx.mkInt(packet.getProto())));
            if(packet.getSeq() != null)
                predicatesOnPktFields = ctx.mkAnd(predicatesOnPktFields, ctx.mkEq(nctx.functionsMap.get("seq").apply(p_0), ctx.mkInt(packet.getSeq())));
            if(packet.getUrl() != null)
                predicatesOnPktFields = ctx.mkAnd(predicatesOnPktFields, ctx.mkEq(nctx.functionsMap.get("url").apply(p_0), ctx.mkInt(packet.getUrl())));
        }
        
        
        /*
         * Constraint send(politoEndHost, n_0, p_0) -> predicatesOnPktFields &&
         * p_0.origin == politoEndHost && p_0.orig_body == p_0.body && 
         * p_0.inner_src == null && p_0.inner_dest == null &&
         * p_0.encrypted == null && nodeHasAddr(politoEndHost,p.src)
         * (no encapsulated packet inside)
         
        constraints.add( ctx.mkForall(new Expr[]{n_0, p_0},
                ctx.mkImplies((BoolExpr)nctx.send.apply(politoEndHost, n_0, p_0),
                        ctx.mkAnd(predicatesOnPktFields,
                                ctx.mkEq(nctx.functionsMap.get("orig_body").apply(p_0),nctx.functionsMap.get("body").apply(p_0)),
                                ctx.mkEq(nctx.functionsMap.get("origin").apply(p_0),politoEndHost),
                                ctx.mkEq(nctx.functionsMap.get("inner_src").apply(p_0),nctx.addressMap.get("null")),
                                ctx.mkEq(nctx.functionsMap.get("inner_dest").apply(p_0),nctx.addressMap.get("null")),
                                ctx.mkEq(nctx.functionsMap.get("encrypted").apply(p_0),ctx.mkFalse()),
                                (BoolExpr)nctx.nodeHasAddr.apply(politoEndHost,nctx.functionsMap.get("src").apply(p_0))
                                )),1,null,null,null,null));*/

        return;
    }
    
    public void configureEndHost() {
    	for(TrafficFlow sr : source.getRequirements().values()) {
    		constraints.add(ctx.mkEq(nctx.deny.apply(source.getZ3Name(), ctx.mkInt(sr.getIdRequirement())), ctx.mkFalse()));
    	}
    }



}