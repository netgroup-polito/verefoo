package it.polito.verefoo.functions;

import java.util.ArrayList;
import java.util.List;

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
	int nUrls;
	int nDomains;
	List<String> urls;
	List<String> domains;
	
	

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
   		urls = new ArrayList<>();
   		domains = new ArrayList<>();
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
		
		for(WafElements rule : n.getConfiguration().getWebApplicationFirewall().getWafElements()) {
			if(rule.getUrl() != null) urls.add(rule.getUrl());
			else if(rule.getDomain() != null) domains.add(rule.getDomain());
		}
		
		nUrls = urls.size();
		nDomains = domains.size();
		
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
  				
  				BoolExpr firstPart = url.equals("null") ? ctx.mkFalse() : generateRulesCondition(url, urls);
  				BoolExpr secondPart = domain.equals("null") ? ctx.mkFalse() : generateRulesCondition(domain, domains);
  				
  				decision = blacklisting ?
  						ctx.mkEq(nctx.deny.apply(source.getZ3Name(), ctx.mkInt(flow.getIdFlow())), ctx.mkOr(firstPart, secondPart)) :
  						ctx.mkEq(nctx.deny.apply(source.getZ3Name(), ctx.mkInt(flow.getIdFlow())), ctx.mkNot(ctx.mkOr(firstPart, secondPart)));
  	
  			}
  			
  			constraints.add(decision);
  			
  			
  		}
		
	}



	private BoolExpr generateRulesCondition(String element, List<String> elementsList) {
		
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