package it.polito.verigraph.functions;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.DatatypeExpr;
import com.microsoft.z3.Expr;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.Optimize;
import com.microsoft.z3.Quantifier;

import it.polito.verefoo.allocation.AllocationNode;
import it.polito.verefoo.graph.TrafficFlow;
import it.polito.verefoo.jaxb.Property;
import it.polito.verigraph.solver.NetContext;

/**
 * Load Balancer Model object
 *
 */
public class LoadBalancer extends GenericFunction {
	DatatypeExpr nat;
	List<String> serversIPAddresses;
	
	public LoadBalancer(AllocationNode source, Context ctx, NetContext nctx) {
		isEndHost = false;
		this.source = source;
		this.ctx = ctx;
		this.nctx = nctx;
		nat = source.getZ3Name();
		constraints = new ArrayList<BoolExpr>();
		used = ctx.mkTrue();
		serversIPAddresses = source.getNode().getConfiguration().getLoadbalancer().getPool().stream().collect(Collectors.toList());	
	}



	public void loadBalancerConfiguration(DatatypeExpr natIp) {
		
		
		
		
		for(TrafficFlow tf : source.getRequirements().values()) {
    		//constraints.add(ctx.mkEq(nctx.deny.apply(source.getZ3Name(), ctx.mkInt(sr.getIdRequirement())), ctx.mkFalse()));
			Property property = tf.getCrossedTrafficFlow(source.getNode().getName());
			if(!serversIPAddresses.contains(property.getSrc())) {
				constraints.add(ctx.mkEq(nctx.deny.apply(source.getZ3Name(), ctx.mkInt(tf.getIdRequirement())), ctx.mkFalse()));
			} else {
				
				boolean statusFound = false;
				TrafficFlow status = null;
				for(TrafficFlow tf2 : source.getRequirements().values()) {
					Property property2 = tf2.getCrossedTrafficFlow(source.getNode().getName());
					if(property.getSrc().equals(property2.getDst()) && property.getDst().equals(property2.getSrc()) && property.getSrcPort().equals(property2.getDstPort()) &&  property.getDstPort().equals(property2.getSrcPort())) {
						statusFound = true;
						status = tf2;
					}
				}
				
				if(statusFound) {

					List<BoolExpr> singleConstraints = new ArrayList<>();
					for(AllocationNode node : status.getPath().getNodes()) {
						singleConstraints.add(ctx.mkImplies(node.getPlacedNF().getUsed(), ctx.mkEq( (BoolExpr)nctx.deny.apply(node.getZ3Name(), ctx.mkInt(status.getIdRequirement())), ctx.mkFalse())));
						if(node.getNode().getName().equals(source.getNode().getName())) break;
					}
					
					BoolExpr[] arrayConstraints = new BoolExpr[singleConstraints.size()];
					BoolExpr andConstraint = ctx.mkAnd(singleConstraints.toArray(arrayConstraints));
					
					constraints.add(ctx.mkImplies(andConstraint, ctx.mkEq(nctx.deny.apply(source.getZ3Name(), ctx.mkInt(tf.getIdRequirement())), ctx.mkFalse())));
					constraints.add(ctx.mkImplies(ctx.mkNot(andConstraint), ctx.mkEq(nctx.deny.apply(source.getZ3Name(), ctx.mkInt(tf.getIdRequirement())), ctx.mkTrue())));
				}else {
					constraints.add(ctx.mkEq(nctx.deny.apply(source.getZ3Name(), ctx.mkInt(tf.getIdRequirement())), ctx.mkTrue()));
				}
				
				
			}
		
		}
	
	}

	
	@Override
	public void addContraints(Optimize solver) {
		BoolExpr[] constr = new BoolExpr[constraints.size()];
		solver.Add(constraints.toArray(constr));
	}
}
