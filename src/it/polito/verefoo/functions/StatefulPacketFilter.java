package it.polito.verefoo.functions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.DatatypeExpr;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.Optimize;

import it.polito.verefoo.allocation.AllocationNode;
import it.polito.verefoo.graph.Predicate;
import it.polito.verefoo.jaxb.FunctionalTypes;
import it.polito.verefoo.jaxb.Node;
import it.polito.verefoo.solver.NetContext;
import it.polito.verefoo.utils.PacketFilterRule;

/** Represents a Packet Filter with the associated Access Control List
 *
 */
public class StatefulPacketFilter extends GenericFunction{

	FuncDecl filtering_function;
	ArrayList<PacketFilterRule> rules; 
	boolean autoConfigured;
	BoolExpr behaviour;
	FuncDecl rule_func;
	// blacklisting and defaultAction must match
	boolean blacklisting;
	boolean defaultActionSet;
	DatatypeExpr pf;
	private BoolExpr whitelist;
	
	int aIndex, dIndex, acIndex;
	Map<Integer, Predicate> allowPredicates = new HashMap<>();
	Map<Integer, Predicate> denyPredicates = new HashMap<>();
	Map<Integer, Predicate> allowCondPredicates = new HashMap<>();
	Map<Integer, Predicate> allowCondInvPredicates = new HashMap<>();
	
	Map<Integer, List<Integer>> allowAtomicPredicates = new HashMap<>();
	Map<Integer, List<Integer>> denyAtomicPredicates = new HashMap<>();
	Map<Integer, List<Integer>> allowCondAtomicPredicates = new HashMap<>();
	Map<Integer, List<Integer>> allowCondInvAtomicPredicates = new HashMap<>();
	
	
	

	/**
	 * Public constructor for the Stateful Packet Filter
	 * @param source It is the Allocation Node on which the packet filter is put
	 * @param ctx It is the Z3 Context in which the model is generated
	 * @param nctx It is the NetContext object to which constraints are sent
	 * @param wildcardManager 
	 */
	public StatefulPacketFilter(AllocationNode source, Context ctx, NetContext nctx) {
		this.source = source;
		this.ctx = ctx;
		this.nctx = nctx;
		
		pf = source.getZ3Name();
		constraints = new ArrayList<BoolExpr>();
   		rules = new ArrayList<>();
		isEndHost = false;

   		// true for blacklisting, false for whitelisting
   		// this is the default, but it can be changed
   		blacklisting = false;
   		defaultActionSet = false;
   		whitelist = ctx.mkBoolConst(pf+"_whitelist");
   		aIndex = 0;
   		dIndex = 0;
   		acIndex = 0;
	}

	/**
	 * This method allows to generate the filtering rules for a manually configured packet_filter
	 */
	public void manualConfiguration(){
		
		if(!autoplace) constraints.add(ctx.mkEq(used, ctx.mkTrue()));
		
		Node n = source.getNode();
		if(n.getFunctionalType().equals(FunctionalTypes.FIREWALL)){
			System.out.println("Allowed");
			for(Integer traffic : source.getForwardBehaviourList()) {
				System.out.println(traffic);
				constraints.add(ctx.mkEq((BoolExpr)nctx.deny.apply(source.getZ3Name(), ctx.mkInt(traffic)), ctx.mkFalse()));
			}
			System.out.println("Dropped");
			for(Integer traffic : source.getDroppedList()) {
				System.out.println(traffic);
				constraints.add(ctx.mkEq((BoolExpr)nctx.deny.apply(source.getZ3Name(), ctx.mkInt(traffic)), ctx.mkTrue()));
			}
		}
	}
    
    
	/**
	 * This method allows to know if blacklisting is used
	 *@return the value of blacklisting boolean variable
	 */
 	public boolean isBlacklisting() {
		return blacklisting;
	}

	
	/**
	 * This method allows to change the default behaviour of the packet_filter: blacklisting (true) or whitelisting (false)
	 * @param action The boolean is true for blacklisting, false for whitelisting.
	 */
	public void setDefaultAction(boolean action){
		this.blacklisting = action;
		defaultActionSet = true;
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
