package it.polito.verefoo.functions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.DatatypeExpr;
import com.microsoft.z3.Expr;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.Optimize;
import com.microsoft.z3.SeqExpr;

import it.polito.verefoo.allocation.AllocationNode;
import it.polito.verefoo.graph.Flow;
import it.polito.verefoo.jaxb.Node;
import it.polito.verefoo.jaxb.WafElements;
import it.polito.verefoo.solver.NetContext;
import it.polito.verefoo.utils.PacketFilterRule;

/** Represents a Packet Filter with the associated Access Control List
 *
 */
public class WAF extends GenericFunction{

	FuncDecl filtering_function;
	ArrayList<PacketFilterRule> rules; 
	boolean autoConfigured;
	BoolExpr behaviour;
	FuncDecl rule_func;
	// blacklisting and defaultAction must match
	private BoolExpr blacklisting_z3;
	boolean blacklisting;
	boolean defaultActionSet;
	private String ipAddress;
	
	DatatypeExpr waf;
	Expr defaultAction;
	int nRules;
	Map<Integer, String> urls;
	Map<Integer, String> domains;
	
	

	/**
	 * Public constructor for the web application firewall
	 * @param source It is the Allocation Node on which the waf is put
	 * @param ctx It is the Z3 Context in which the model is generated
	 * @param nctx It is the NetContext object to which constraints are sent
	 */
	public WAF(AllocationNode source, Context ctx, NetContext nctx) {
		this.source = source;
		this.ctx = ctx;
		this.nctx = nctx;

		
		waf = source.getZ3Name();
		ipAddress = source.getNode().getName();
		constraints = new ArrayList<BoolExpr>();
   		urls = new HashMap<>();
   		domains = new HashMap<>();
		isEndHost = false;

   		// true for blacklisting, false for whitelisting
   		// this is the default, but it can be changed
   		defaultActionSet = false;
   		
   		// function can be used or not, autoplace follows it
   		used = ctx.mkBoolConst(waf+"_used");
		autoplace = false; 
		
		//function can be autoConfigured is z3 must establish the firewall filtering policy
		autoConfigured = false;
	}

	/**
	 * This method allows to generate the filtering rules for a manually configured WAF
	 */
	public void manualConfiguration(){
		
		constraints.add(ctx.mkEq(used, ctx.mkTrue()));
		
		Node n = source.getNode();
		
		int nRules = 0;
		for(WafElements rule : n.getConfiguration().getWebApplicationFirewall().getWafElements()) {
			String url = rule.getUrl() != null ? rule.getUrl() : "null";
			String domain = rule.getDomain() != null ? rule.getDomain() : "null";
			urls.put(nRules, url);
			domains.put(nRules, domain);
			nRules++;
		}


		
		/**
	     * This section allow the creation of Z3 variables for defaultAction and rules.
	     * behaviour variables is the combination of the auto-configured rules.
	    */
  		defaultAction = ctx.mkConst(waf + "_manual_default_action", ctx.mkBoolSort());
  		Expr ruleAction = ctx.mkConst(waf + "_manual_action", ctx.mkBoolSort());

  		constraints.add(ctx.mkEq(defaultAction, ctx.mkFalse()));
  		constraints.add(ctx.mkEq(ruleAction, ctx.mkTrue()));

	
  		/**
  		 * This sections generates all the constraints to satisfy reachability and isolation requirements.
  		 */
  		
  		for(Flow flow : source.getFlows().values()) {
  		
  			BoolExpr decision;
  			if(flow.getCrossedTraffic(ipAddress).getUrl().equals("null") && flow.getCrossedTraffic(ipAddress).getDomain().equals("null")) {
  				decision = blacklisting ? ctx.mkEq(nctx.deny.apply(source.getZ3Name(), ctx.mkInt(flow.getIdFlow())), ctx.mkFalse()) : ctx.mkEq(nctx.deny.apply(source.getZ3Name(), ctx.mkInt(flow.getIdFlow())), ctx.mkTrue()); 
  			}else {
  				String url = flow.getCrossedTraffic(ipAddress).getUrl();
  				String domain = flow.getCrossedTraffic(ipAddress).getDomain();
  				
  				List<BoolExpr> constraintConditions = new ArrayList<>();
  				BoolExpr finalConstraint;
  				if(domain.equals("null") && url.equals("null")){
  					finalConstraint = ctx.mkFalse();
  				} else if(!domain.equals("null") && url.equals("null")) {
  					finalConstraint = generateRulesCondition(domain, domains.values());
  				} else if(domain.equals("null") && !url.equals("null")) {
  					finalConstraint = generateRulesCondition(url, urls.values());
  				}else {
  					for(int i=0; i < nRules; i++) {
  						constraintConditions.add(ctx.mkAnd(generateRulesCondition(domain, domains.get(i)), generateRulesCondition(url, urls.get(i))));
  					}
  					BoolExpr[] tmp = new BoolExpr[constraintConditions.size()];
  					finalConstraint = ctx.mkOr(constraintConditions.toArray(tmp));
  				}
  				
  			
  				decision = blacklisting ?
  						ctx.mkEq(nctx.deny.apply(source.getZ3Name(), ctx.mkInt(flow.getIdFlow())), finalConstraint) :
  						ctx.mkEq(nctx.deny.apply(source.getZ3Name(), ctx.mkInt(flow.getIdFlow())), ctx.mkNot(finalConstraint));
  	
  			}
  			
  			constraints.add(decision);
  			
  			
  		}
		
	}
	
	
	private BoolExpr generateRulesCondition(String element, String toCompare) {
		
			int z3Element = element.hashCode();
			int z3ToCompare = toCompare.equals("null") ? element.hashCode() : toCompare.hashCode();
			return ctx.mkEq(ctx.mkInt(z3Element), ctx.mkInt(z3ToCompare));
		}
	
	
	private BoolExpr generateRulesCondition(String element, Collection<String> elementsList) {
		
		int z3Element = element.hashCode();
		
		List<BoolExpr> exprList = new ArrayList<>();
		for(String toCompare : elementsList) {
			int z3ToCompare = toCompare.equals("null") ? element.hashCode() : toCompare.hashCode();
			exprList.add(ctx.mkEq(ctx.mkInt(z3Element), ctx.mkInt(z3ToCompare)));
		}
		
		BoolExpr[] tmpArray = new BoolExpr[exprList.size()];
		return ctx.mkOr(exprList.toArray(tmpArray));
	}



	private BoolExpr generateRulesConditionWithString(String element, List<String> elementsList) {
		
		SeqExpr z3Element = ctx.mkString(element);
		
		List<BoolExpr> exprList = new ArrayList<>();
		for(String toCompare : elementsList) {
			SeqExpr z3ToCompare = ctx.mkString(toCompare);
			exprList.add(ctx.mkEq(z3Element, z3ToCompare));
		}
		
		BoolExpr[] tmpArray = new BoolExpr[exprList.size()];
		return ctx.mkOr(exprList.toArray(tmpArray));
	}

	/**
	 * This method allows to know if autoconfiguration feature is used
	 *@return the value of autoconfigured boolean variable
	 */
	public boolean isAutoconfigured() {
		return autoConfigured;
	}
    
   	/**
	 * This method allows to know if autoplacement feature is used
	 *@return the value of autoplace boolean variable
	 */
	public boolean isAutoplace() {
		return autoplace;
	}


	/**
	 * This method allows to know if blacklisting is used
	 *@return the value of blacklisting boolean variable
	 */
 	public boolean isBlacklisting() {
		return blacklisting;
	}

 	/**
	 * This method allows to set autoconfigured variable
	 *@param autoconfigured Value to set
	 */
	public void setAutoconfigured(boolean autoconfigured) {
		this.autoConfigured = autoconfigured;
	}
	
	/**
	 * This method allows to set autoplace variable
	 *@param autoplace Value to set
	 */
	public void setAutoplace(boolean autoplace) {
		this.autoplace = autoplace;
	}

	/**
	 * This method allows to set blacklisting variable
	 *@param blacklisting Value to set
	 */
	public void setBlacklisting(boolean blacklisting) {
		this.blacklisting = blacklisting;
		defaultActionSet = true;
	}
	
	/**
	 * This method allows to change the default behaviour of the packet_filter: blacklisting (true) or whitelisting (false)
	 * @param action The boolean is true for blacklisting, false for whitelisting.
	 */
	public void setDefaultAction(boolean action){
		blacklisting_z3 = action? ctx.mkTrue(): ctx.mkFalse();
		this.setBlacklisting(action);
	}

	
	/**
	 * This method allows to wrap the method which adds the constraints inside Z3 solver
	 * @param solver Istance of Z3 solver
	 */
	@Override
	public void addContraints(Optimize solver) {
		BoolExpr[] constr = new BoolExpr[constraints.size()];
	    solver.Add(constraints.toArray(constr));
	    //additionalConstraints(solver);
	}
	

}
