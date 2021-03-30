package it.polito.verefoo.functions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.DatatypeExpr;
import com.microsoft.z3.Expr;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.Optimize;

import it.polito.verefoo.allocation.AllocationNode;
import it.polito.verefoo.extra.BadGraphError;
import it.polito.verefoo.extra.WildcardManager;
import it.polito.verefoo.graph.FlowPath;
import it.polito.verefoo.graph.Traffic;
import it.polito.verefoo.jaxb.ActionTypes;
import it.polito.verefoo.jaxb.EType;
import it.polito.verefoo.jaxb.FunctionalTypes;
import it.polito.verefoo.jaxb.Node;
import it.polito.verefoo.jaxb.PName;
import it.polito.verefoo.solver.NetContext;
import it.polito.verefoo.utils.PacketFilterRule;
import it.polito.verefoo.utils.Tuple;

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
	private BoolExpr blacklisting_z3;
	boolean blacklisting;
	boolean defaultActionSet;
	private String ipAddress;
	
	DatatypeExpr pf;
	Expr defaultAction;
	int nRules;
	Map<Integer, BoolExpr> notConfiguredConditions = new HashMap<>();
	Map<Integer, Expr> srcConditions = new HashMap<>();
	Map<Integer, Expr> dstConditions = new HashMap<>();
	Map<Integer, Expr> portSConditions = new HashMap<>();
	Map<Integer, Expr> portDConditions = new HashMap<>();
	Map<Integer, Expr> l4Conditions = new HashMap<>();
	

	/**
	 * Public constructor for the Packet Filter
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
		ipAddress = source.getNode().getName();
		constraints = new ArrayList<BoolExpr>();
   		rules = new ArrayList<>();
		isEndHost = false;

   		// true for blacklisting, false for whitelisting
   		// this is the default, but it can be changed
   		blacklisting_z3 = ctx.mkFalse();
   		blacklisting = false;
   		defaultActionSet = false;
   		
   		// function can be used or not, autoplace follows it
   		used = ctx.mkBoolConst(pf+"_used");
		autoplace = false; 
		
		//function can be autoConfigured is z3 must establish the firewall filtering policy
		autoConfigured = false;
	}

	/**
	 * This method allows to generate the filtering rules for a manually configured packet_filter
	 */
	public void manualConfiguration(){
		Node n = source.getNode();
		if(n.getFunctionalType().equals(FunctionalTypes.STATEFUL_FIREWALL)){
				n.getConfiguration().getStatefulFirewall().getElements().forEach((e)->{ 
					
						ArrayList<PacketFilterRule> rules_manual = new ArrayList<>();
						String src_port = e.getSrcPort()!=null? e.getSrcPort():"*";
						String dst_port = e.getDstPort()!=null? e.getDstPort():"*";
						boolean directional = e.isDirectional()!=null? e.isDirectional():true;
						int protocol = e.getProtocol()!=null? e.getProtocol().ordinal():0;
						boolean action;
						if(e.getAction() != null){
							action = (e.getAction().equals(ActionTypes.ALLOW)) ? true : false;
						}else{
							// if not specified the action of the rule is the opposite of the 
							// default behaviour otherwise the rule would not be necessary
							if(n.getConfiguration().getStatefulFirewall().getDefaultAction() == null){
								action = false;
							}else
							action = n.getConfiguration().getStatefulFirewall().getDefaultAction().equals(ActionTypes.ALLOW) ? false : true;
						}
						try{

							if(nctx.addressMap.get(e.getSource())!=null&&nctx.addressMap.get(e.getDestination())!=null){
									PacketFilterRule rule=new PacketFilterRule(nctx, ctx, action, nctx.addressMap.get(e.getSource()),nctx.addressMap.get(e.getDestination()),
																				src_port,dst_port, protocol, directional);
									rules_manual.add(rule);
							}else{ 
									PacketFilterRule rule=new PacketFilterRule(nctx, ctx, action, e.getSource(),e.getDestination(),
																				src_port,dst_port, protocol, directional);
									rules_manual.add(rule);
							}
						}catch(NumberFormatException ex){
							throw new BadGraphError(n.getName()+" has invalid configuration: "+ex.getMessage(), EType.INVALID_NODE_CONFIGURATION);
						}
						
						if(!autoConfigured){	// if not an auto_configuration packet_filter
							this.rules.addAll(rules_manual);
						}	
				});
		}
		
		
		if(!autoplace) {
			constraints.add(ctx.mkEq(used, ctx.mkTrue()));
		}
		
		nRules = rules.size();
		int i = 0;
		for(PacketFilterRule rule : rules) {
			Expr src = ctx.mkConst(pf + "_manual_src_"+i, nctx.addressType);
  			Expr dst = ctx.mkConst(pf + "_manual_dst_"+i, nctx.addressType);
  			Expr proto = ctx.mkConst(pf + "_manual_proto_"+i, ctx.mkIntSort());
  			Expr srcp = ctx.mkConst(pf + "_manual_srcp_"+i, nctx.portType);
  			Expr dstp = ctx.mkConst(pf + "_manual_dstp_"+i, nctx.portType);
 			
 	
 			constraints.add(ctx.mkEq(src, rule.getSource()));
 			constraints.add(ctx.mkEq(dst, rule.getDestination()));
 			constraints.add(ctx.mkEq((IntExpr)nctx.portFunctionsMap.get("start").apply(srcp),(IntExpr)rule.getStart_src_port()));
 			constraints.add(ctx.mkEq((IntExpr)nctx.portFunctionsMap.get("end").apply(srcp),(IntExpr)rule.getEnd_src_port()));
 			constraints.add(ctx.mkEq((IntExpr)nctx.portFunctionsMap.get("start").apply(dstp),(IntExpr)rule.getStart_dst_port()));
 			constraints.add(ctx.mkEq((IntExpr)nctx.portFunctionsMap.get("end").apply(dstp),(IntExpr)rule.getEnd_dst_port()));
 			constraints.add(ctx.mkEq(proto,rule.getProtocol()));
 			
 			srcConditions.put(i,  src);
 			dstConditions.put(i, dst);
 			portSConditions.put(i,  srcp);
 			portDConditions.put(i, dstp);
 			l4Conditions.put(i, proto) ;
 			i++;
		}
		
		
		/**
	     * This section allow the creation of Z3 variables for defaultAction and rules.
	     * behaviour variables is the combination of the auto-configured rules.
	    */
  		defaultAction = ctx.mkConst(pf + "_manual_default_action", ctx.mkBoolSort());
  		Expr ruleAction = ctx.mkConst(pf + "_manual_action", ctx.mkBoolSort());
  		
  		
  		constraints.add(ctx.mkEq(defaultAction, ctx.mkFalse()));
  		constraints.add(ctx.mkEq(ruleAction, ctx.mkTrue()));

  	
	
  		/**
  		 * This sections generates all the constraints to satisfy reachability and isolation requirements.
  		 */
  		
  		for(FlowPath sr : source.getFlows().values()) {
  		
  			generateManualSatifiabilityConstraint(sr);
  		}
		
	}



	/**
	 * This method generates the hard constraint to satisfy a requirement for an manually configured firewall.
	 * @param sr it is the security requirement
	 */
	private void generateManualSatifiabilityConstraint(FlowPath sr) {
		
		BoolExpr allowed1 = ctx.mkBoolConst(pf+"_allowed1"+sr.getIdFlow());
		BoolExpr allowed2 = ctx.mkBoolConst(pf+"_allowed2"+sr.getIdFlow());
		
	
		//Stateless part
		List<BoolExpr> listFirstC = new ArrayList<>();
		listFirstC.add(ctx.mkFalse());
		for(int i = 0; i < nRules; i++) {
			BoolExpr component1 = ctx.mkAnd(
						nctx.equalIpAddressToPFRule(sr.getCrossedTraffic(ipAddress).getIPSrc(), srcConditions.get(i)),
						nctx.equalIpAddressToPFRule(sr.getCrossedTraffic(ipAddress).getIPDst(), dstConditions.get(i)),
	 					nctx.equalPortRangeToPFRule(sr.getCrossedTraffic(ipAddress).getpSrc(), portSConditions.get(i)),
	 					nctx.equalPortRangeToPFRule(sr.getCrossedTraffic(ipAddress).getpDst(), portDConditions.get(i)),
	 					nctx.equalLv4ProtoToFwLv4Proto(sr.getCrossedTraffic(ipAddress).gettProto().ordinal(), l4Conditions.get(i))
	 					);
			listFirstC.add(component1);
		}
		
		BoolExpr[] tmp = new BoolExpr[listFirstC.size()];
		BoolExpr firstC = ctx.mkOr(listFirstC.toArray(tmp));
		
		constraints.add(ctx.mkImplies(firstC, allowed1));
		constraints.add(ctx.mkImplies(ctx.mkNot(firstC), ctx.mkNot(allowed1)));
		
		//Stateful part
		Traffic traffic = sr.getCrossedTraffic(source.getNode().getName());
		
		boolean statusFound = false;
		FlowPath status = null;
		for(FlowPath flow2 : source.getFlows().values()) {
			Traffic traffic2 = flow2.getCrossedTraffic(source.getNode().getName());
			if(traffic.getIPSrc().equals(traffic2.getIPDst()) && traffic.getIPDst().equals(traffic2.getIPSrc()) && traffic.getpSrc().equals(traffic2.getpDst())) {
				statusFound = true;
				status = flow2;
			}
		}
		
		if(statusFound) {

			List<BoolExpr> singleConstraints = new ArrayList<>();
			singleConstraints.add(ctx.mkTrue());
			for(AllocationNode node : status.getPath().getNodes()) {
				singleConstraints.add(ctx.mkImplies(node.getPlacedNF().getUsed(), ctx.mkEq( (BoolExpr)nctx.deny.apply(node.getZ3Name(), ctx.mkInt(status.getIdFlow())), ctx.mkFalse())));
				if(node.getNode().getName().equals(source.getNode().getName())) break;
			}
			
			BoolExpr[] arrayConstraints = new BoolExpr[singleConstraints.size()];
			BoolExpr andConstraint = ctx.mkAnd(singleConstraints.toArray(arrayConstraints));
			BoolExpr allowed1status = ctx.mkBoolConst(pf+"_allowed1"+status.getIdFlow());
			constraints.add(ctx.mkImplies(ctx.mkAnd(andConstraint, allowed1status), allowed2));
			constraints.add(ctx.mkImplies(ctx.mkOr(ctx.mkNot(andConstraint), ctx.mkNot(allowed1status)), ctx.mkNot(allowed2)));
		}else {
			constraints.add(ctx.mkNot(allowed2));
		}
		
		constraints.add(ctx.mkImplies(ctx.mkOr(allowed1, allowed2),  ctx.mkEq(nctx.deny.apply(source.getZ3Name(), ctx.mkInt(sr.getIdFlow())), ctx.mkFalse())));
		constraints.add(ctx.mkImplies(ctx.mkAnd(ctx.mkNot(allowed1), ctx.mkNot(allowed2)),  ctx.mkEq(nctx.deny.apply(source.getZ3Name(), ctx.mkInt(sr.getIdFlow())), ctx.mkTrue())));
		
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
