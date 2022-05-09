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

/** Represents a Packet Filter with the associated Access Control List
 *
 */
public class StatefulPacketFilter extends GenericFunction{

	FuncDecl filtering_function;
	boolean autoConfigured;
	BoolExpr behaviour;
	FuncDecl rule_func;
	// blacklisting and defaultAction must match
	boolean blacklisting;
	boolean defaultActionSet;
	DatatypeExpr pf;
	private BoolExpr whitelist;
	
	Map<Integer, Predicate> allowPredicates = new HashMap<>();
	Map<Integer, Predicate> denyPredicates = new HashMap<>();
	Map<Integer, Predicate> allowCondPredicates = new HashMap<>();
	Map<Integer, Predicate> allowCondInvPredicates = new HashMap<>();
	
	Map<Integer, List<Integer>> allowAtomicPredicates = new HashMap<>();
	List<Integer> denyAtomicPredicates = new ArrayList<>();
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
		isEndHost = false;

   		// true for blacklisting, false for whitelisting
   		// this is the default, but it can be changed
   		blacklisting = false;
   		defaultActionSet = false;
   		whitelist = ctx.mkBoolConst(pf+"_whitelist");
   		used = ctx.mkBoolConst(pf+"_used");
   		autoplace = false;
	}

	/**
	 * This method allows to generate the filtering rules for a manually configured packet_filter
	 */
	public void manualConfiguration(){
		
		if(!autoplace) constraints.add(ctx.mkEq(used, ctx.mkTrue()));
		
		Node n = source.getNode();
		if(n.getFunctionalType().equals(FunctionalTypes.STATEFUL_FIREWALL)){
			for(List<Integer> trafficList : allowAtomicPredicates.values()) {
				for(Integer traffic : trafficList) {
					constraints.add(ctx.mkEq((BoolExpr)nctx.deny.apply(source.getZ3Name(), ctx.mkInt(traffic)), ctx.mkFalse()));
				}		
			}
			for(Integer traffic : denyAtomicPredicates) {
				constraints.add(ctx.mkEq((BoolExpr)nctx.deny.apply(source.getZ3Name(), ctx.mkInt(traffic)), ctx.mkTrue()));
			}
			for(Map.Entry<Integer, List<Integer>> allowCondEntry : allowCondAtomicPredicates.entrySet()) {
				List<Integer> allowCondInvEntry = allowCondInvAtomicPredicates.get(allowCondEntry.getKey());
				for(Integer traffic : allowCondEntry.getValue()) {
					
				}
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
	
	
	public void addAllowPredicate(int index, Predicate predicate) {
		allowPredicates.put(index, predicate);
		List<Integer> apList = new ArrayList<>();
		allowAtomicPredicates.put(index, apList);
	}
	
	public void addDenyPredicate(int index, Predicate predicate) {
		denyPredicates.put(index, predicate);
		//List<Integer> apList = new ArrayList<>();
		//denyAtomicPredicates.put(index, apList);
	}
	
	public void addAllowCondPredicate(int index, Predicate predicate) {
		allowCondPredicates.put(index, predicate);
		List<Integer> apList = new ArrayList<>();
		allowCondAtomicPredicates.put(index, apList);
	}
	
	public void addAllowCondInvPredicate(int index, Predicate predicate) {
		allowCondInvPredicates.put(index, predicate);
		List<Integer> apList = new ArrayList<>();
		allowCondInvAtomicPredicates.put(index, apList);
	}
	
	public void addAllowAtomicPredicate(int index, Integer predicate) {
		allowAtomicPredicates.get(index).add(predicate);
	}
	
	public void addDenyAtomicPredicate(Integer predicate) {
		denyAtomicPredicates.add(predicate);
	}
	
	public void addAllowCondAtomicPredicate(int index, Integer predicate) {
		allowCondAtomicPredicates.get(index).add(predicate);
	}
	
	public void addAllowCondInvAtomicPredicate(int index, Integer predicate) {
		allowCondInvAtomicPredicates.get(index).add(predicate);
	}
	
	public Map<Integer, Predicate> getAllowPredicates() {
		return allowPredicates;
	}

	public Map<Integer, Predicate> getDenyPredicates() {
		return denyPredicates;
	}

	public Map<Integer, Predicate> getAllowCondPredicates() {
		return allowCondPredicates;
	}

	public Map<Integer, Predicate> getAllowCondInvPredicates() {
		return allowCondInvPredicates;
	}

	public Map<Integer, List<Integer>> getAllowAtomicPredicates() {
		return allowAtomicPredicates;
	}

	public List<Integer> getDenyAtomicPredicates() {
		return denyAtomicPredicates;
	}

	public Map<Integer, List<Integer>> getAllowCondAtomicPredicates() {
		return allowCondAtomicPredicates;
	}

	public Map<Integer, List<Integer>> getAllowCondInvAtomicPredicates() {
		return allowCondInvAtomicPredicates;
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
