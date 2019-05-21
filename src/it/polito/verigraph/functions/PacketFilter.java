/*******************************************************************************
 * Copyright (c) 2017 Politecnico di Torino and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package it.polito.verigraph.functions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.DatatypeExpr;
import com.microsoft.z3.Expr;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.IntNum;
import com.microsoft.z3.Optimize;
import com.microsoft.z3.Sort;

import it.polito.verefoo.allocation.AllocationNode;
import it.polito.verefoo.extra.BadGraphError;
import it.polito.verefoo.jaxb.ActionTypes;
import it.polito.verefoo.jaxb.EType;
import it.polito.verefoo.jaxb.FunctionalTypes;
import it.polito.verefoo.jaxb.Node;
import it.polito.verigraph.extra.PacketFilterRule;
import it.polito.verigraph.extra.Quadruple;
import it.polito.verigraph.extra.Tuple;
import it.polito.verigraph.solver.NetContext;

/** Represents a Packet Filter with the associated Access Control List
 *
 */
public class PacketFilter extends GenericFunction{

	FuncDecl filtering_function;
	ArrayList<PacketFilterRule> rules; 
	boolean autoConfigured;
	BoolExpr behaviour;
	FuncDecl rule_func;
	// blacklisting and defaultAction must match
	private BoolExpr blacklisting_z3;
	boolean blacklisting;
	DatatypeExpr pf;
	
	

	/**
	 * Public constructor for the Packet Filter
	 * @param source It is the Allocation Node on which the packet_filter is put
	 * @param ctx It is the Z3 Context in which the model is generated
	 * @param nctx It is the NetContext object to which contraints are sent
	 */
	public PacketFilter(AllocationNode source, Context ctx, NetContext nctx) {
		this.source = source;
		this.ctx = ctx;
		this.nctx = nctx;
		
		pf = source.getZ3Name();
		constraints = new ArrayList<BoolExpr>();
   		rules = new ArrayList<>();
		isEndHost = false;

   		// true for blacklisting, false for whitelisting
   		// this is the default, but it can be changed
   		blacklisting_z3 = ctx.mkFalse();
   		blacklisting = false;
   		
   		// function can be used or not, autoplace follows it
   		used = ctx.mkBoolConst(pf+"_used");
		autoplace = true; 
		
		autoConfigured = true;
		
		// main z3 function
		filtering_function = ctx.mkFuncDecl(pf + "_filt_func", new Sort[] { nctx.packetType}, ctx.mkBoolSort());
	}

	

	/**
	 * This method allows to add the contraints inside Z3 solver
	 * @param solver Istance of Z3 solver
	 */
	
	// TODO improve some comments only here
	private void additionalConstraints(Optimize solver){
 		Expr p_0 = ctx.mkConst(pf+"_packet_filter_acl_p_0", nctx.packetType);
 		Expr n_0 = ctx.mkConst(pf+"_packet_filter_send_n_0", nctx.nodeType);

 		// if automatic configuration enabled, if is true
 		if (rules.size() == 0){
    		//If the size of the ACL list is empty then by default acl_func must be false
    		 solver.Add(ctx.mkForall(new Expr[]{p_0},
								ctx.mkEq(filtering_function.apply(p_0), blacklisting_z3)
						,1,null,null,null,null));
    	}else{
    			BoolExpr[] rule_map = new BoolExpr[rules.size()];
    	        for(int y=0;y<rules.size();y++){
    	        	PacketFilterRule rule = rules.get(y);
    	        	rule_map[y] = rule.matchPacket(p_0);
					solver.Add(ctx.mkForall(new Expr[]{p_0, n_0},
											// at this point we assume that the rules are conflict free
											ctx.mkImplies(ctx.mkAnd((BoolExpr) nctx.recv.apply(new Expr[] { n_0, pf, p_0 }),
																rule.matchPacket(p_0))
														,ctx.mkAnd(ctx.mkEq(
																			filtering_function.apply(p_0),
																			rule.getAction()
																			)
																	)
													  )
								, 1, null, null, null, null));
    	        }
    	        solver.Add(ctx.mkForall(new Expr[]{p_0, n_0},
										ctx.mkImplies(//no rule matches the packet -> the acl must allow the default behaviour
											ctx.mkAnd((BoolExpr) nctx.recv.apply(new Expr[] { n_0, pf, p_0 }),ctx.mkNot(ctx.mkOr(rule_map))),
											ctx.mkEq(
													filtering_function.apply(p_0),
													this.blacklisting_z3
													))
								, 1, null, null, null, null));

    	}
    }
	
	
	



	/**
	 * This method allows to generate the Acl for a manually configured packet_filter
	 */
	public void generateManualRules(){
		Node n = source.getNode();
		if(n.getFunctionalType().equals(FunctionalTypes.FIREWALL)){
				n.getConfiguration().getFirewall().getElements().forEach((e)->{
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
							if(n.getConfiguration().getFirewall().getDefaultAction() == null){
								action = false;
							}else
							action = n.getConfiguration().getFirewall().getDefaultAction().equals(ActionTypes.ALLOW) ? false : true;
						}
						try{

							if(nctx.addressMap.get(e.getSource())!=null&&nctx.addressMap.get(e.getDestination())!=null){
									PacketFilterRule rule=new PacketFilterRule(nctx, ctx, action, nctx.addressMap.get(e.getSource()),nctx.addressMap.get(e.getDestination()),
																				src_port,dst_port, protocol, directional);
									rules_manual.add(rule);
							}else{ // #TODO check if it enters here
									PacketFilterRule rule=new PacketFilterRule(nctx, ctx, action, e.getSource(),e.getDestination(),
																				src_port,dst_port, protocol, directional);
									rules_manual.add(rule);
							}
						}catch(NumberFormatException ex){
							throw new BadGraphError(n.getName()+" has invalid configuration: "+ex.getMessage(), EType.INVALID_NODE_CONFIGURATION);
						}
						
						if(!autoConfigured){	// if not an autoconfiguration packet_filter
							this.rules.addAll(rules_manual);
						}	
				});
		}
		
	}
    
    
	
	/**
	 * This method allows to create SOFT and HARD constraints for an auto-configured packet_filter
	 *@param nRules It is the number of MAXIMUM rules the packet_filter should try to configure
	 */
    public void automaticConfiguration(Integer nRules) {
    	Expr p_0 = ctx.mkConst(pf + "_packet_filter_send_p_0", nctx.packetType);

    	List<BoolExpr> automatic_rules = new ArrayList<>();
        List<BoolExpr> implications1 = new ArrayList<BoolExpr>();
  		List<BoolExpr> implications2 = new ArrayList<BoolExpr>();
  		
  		if(autoplace) {
  			// packet filter should not be used if possible
  			nctx.softConstrAutoPlace.add(new Tuple<BoolExpr, String>(ctx.mkNot(used), "fw_auto_conf"));
  		}
  		for(int i = 0; i < nRules; i++){
  			Expr src = ctx.mkConst(pf + "_auto_src_"+i, nctx.addressType);
  			Expr dst = ctx.mkConst(pf + "_auto_dst_"+i, nctx.addressType);
  			Expr proto = ctx.mkConst(pf + "_auto_proto_"+i, ctx.mkIntSort());
  			Expr srcp = ctx.mkConst(pf + "_auto_srcp_"+i, nctx.portType);
  			Expr dstp = ctx.mkConst(pf + "_auto_dstp_"+i, nctx.portType);
  			IntExpr srcAuto1 = ctx.mkIntConst(pf + "_auto_src_ip_1_"+i);
  			IntExpr srcAuto2 = ctx.mkIntConst(pf + "_auto_src_ip_2_"+i);
  			IntExpr srcAuto3 = ctx.mkIntConst(pf + "_auto_src_ip_3_"+i);
  			IntExpr srcAuto4 = ctx.mkIntConst(pf + "_auto_src_ip_4_"+i);
  			IntExpr dstAuto1 = ctx.mkIntConst(pf + "_auto_dst_ip_1_"+i);
  			IntExpr dstAuto2 = ctx.mkIntConst(pf + "_auto_dst_ip_2_"+i);
  			IntExpr dstAuto3 = ctx.mkIntConst(pf + "_auto_dst_ip_3_"+i);
  			IntExpr dstAuto4 = ctx.mkIntConst(pf + "_auto_dst_ip_4_"+i);
  			/**
  	    	 * This section allows the creation of the following hard constraints:
  	    	 * 1) if in a rule the source isn't NULL, then also the destination shouldn't be NULL
  	    	 * 2) if in a rule the source is NULL, then also the destination should be NULL
  	    	 * This is done only with autoplacement feature.
  	    	 * Observation: maybe we can introduce the REVERSE rules!
  	    	 */
  			if(autoplace) {
  				implications1.add(ctx.mkAnd(ctx.mkNot(ctx.mkEq( src, this.nctx.addressMap.get("null"))),
  						ctx.mkNot(ctx.mkEq( dst, this.nctx.addressMap.get("null")))));
  	  			implications2.add(ctx.mkAnd(ctx.mkEq( src, this.nctx.addressMap.get("null")),
  						ctx.mkEq( dst, this.nctx.addressMap.get("null"))));
  			}
  			/**
  	    	 * This section allows the creation of the following hard constraints:
  	    	 * Each component of source or destination ipAddress of the rule should be equal to the defined Z3 variable in this method.
  	    	 * It's used for a mapping useful only for Z3 model, not first-order logic behaviour of packet_filter.
  	    	 */
  			constraints.add(ctx.mkAnd(
						ctx.mkEq(nctx.ipFunctionsMap.get("ipAddr_1").apply(src), srcAuto1),
						ctx.mkEq(nctx.ipFunctionsMap.get("ipAddr_2").apply(src), srcAuto2),
						ctx.mkEq(nctx.ipFunctionsMap.get("ipAddr_3").apply(src), srcAuto3),
						ctx.mkEq(nctx.ipFunctionsMap.get("ipAddr_4").apply(src), srcAuto4),
						ctx.mkEq(nctx.ipFunctionsMap.get("ipAddr_1").apply(dst), dstAuto1),
						ctx.mkEq(nctx.ipFunctionsMap.get("ipAddr_2").apply(dst), dstAuto2),
						ctx.mkEq(nctx.ipFunctionsMap.get("ipAddr_3").apply(dst), dstAuto3),
						ctx.mkEq(nctx.ipFunctionsMap.get("ipAddr_4").apply(dst), dstAuto4)
						)
				); 
  			/**
  	    	 * This section allows the creation of the following soft constraints:
  	    	 * If possible, the source/destination IP address components should be equal to the wildcard.
  	    	 * This way the rule is able to manage more than one property at the same time.
  	    	 */
  			nctx.softConstrWildcard.add(new Tuple<BoolExpr, String>(ctx.mkEq((IntExpr)nctx.ipFunctionsMap.get("ipAddr_1").apply(nctx.addressMap.get("wildcard")),srcAuto1), "fw_auto_conf"));
  			nctx.softConstrWildcard.add(new Tuple<BoolExpr, String>(ctx.mkEq((IntExpr)nctx.ipFunctionsMap.get("ipAddr_2").apply(nctx.addressMap.get("wildcard")),srcAuto2), "fw_auto_conf"));
  			nctx.softConstrWildcard.add(new Tuple<BoolExpr, String>(ctx.mkEq((IntExpr)nctx.ipFunctionsMap.get("ipAddr_3").apply(nctx.addressMap.get("wildcard")),srcAuto3), "fw_auto_conf"));
  			nctx.softConstrWildcard.add(new Tuple<BoolExpr, String>(ctx.mkEq((IntExpr)nctx.ipFunctionsMap.get("ipAddr_4").apply(nctx.addressMap.get("wildcard")),srcAuto4), "fw_auto_conf"));
  			nctx.softConstrWildcard.add(new Tuple<BoolExpr, String>(ctx.mkEq((IntExpr)nctx.ipFunctionsMap.get("ipAddr_1").apply(nctx.addressMap.get("wildcard")),dstAuto1), "fw_auto_conf"));
  			nctx.softConstrWildcard.add(new Tuple<BoolExpr, String>(ctx.mkEq((IntExpr)nctx.ipFunctionsMap.get("ipAddr_2").apply(nctx.addressMap.get("wildcard")),dstAuto2), "fw_auto_conf"));
  			nctx.softConstrWildcard.add(new Tuple<BoolExpr, String>(ctx.mkEq((IntExpr)nctx.ipFunctionsMap.get("ipAddr_3").apply(nctx.addressMap.get("wildcard")),dstAuto3), "fw_auto_conf"));
  			nctx.softConstrWildcard.add(new Tuple<BoolExpr, String>(ctx.mkEq((IntExpr)nctx.ipFunctionsMap.get("ipAddr_4").apply(nctx.addressMap.get("wildcard")),dstAuto4), "fw_auto_conf"));
  			
   			/**
  	    	 * This section allows the creation of the following soft constraints:
  	    	 * If possible, the source/destination IP address components should be NULL at the same time.
  	    	 * This way the rule isn't generated.
  	    	 */
  			nctx.softConstrAutoConf.add(new Tuple<BoolExpr, String>(ctx.mkAnd(
 						ctx.mkEq(nctx.ipFunctionsMap.get("ipAddr_1").apply(nctx.addressMap.get("null")), srcAuto1),
 						ctx.mkEq(nctx.ipFunctionsMap.get("ipAddr_2").apply(nctx.addressMap.get("null")), srcAuto2),
 						ctx.mkEq(nctx.ipFunctionsMap.get("ipAddr_3").apply(nctx.addressMap.get("null")), srcAuto3),
 						ctx.mkEq(nctx.ipFunctionsMap.get("ipAddr_4").apply(nctx.addressMap.get("null")), srcAuto4),
 						ctx.mkEq(nctx.ipFunctionsMap.get("ipAddr_1").apply(nctx.addressMap.get("null")), dstAuto1),
 						ctx.mkEq(nctx.ipFunctionsMap.get("ipAddr_2").apply(nctx.addressMap.get("null")), dstAuto2),
 						ctx.mkEq(nctx.ipFunctionsMap.get("ipAddr_3").apply(nctx.addressMap.get("null")), dstAuto3),
 						ctx.mkEq(nctx.ipFunctionsMap.get("ipAddr_4").apply(nctx.addressMap.get("null")), dstAuto4)
 						), "fw_auto_conf"));
  			
  			/**
  	    	 * This section allows the creation of the following soft constraints:
  	    	 * 1) use wildcards for protocol field if possible
  	    	 * 2) use wildcards for source port if possible
  	    	 * 3) use wildcards for destination port if possible
  	    	 */
 			nctx.softConstrProtoWildcard.add(new Tuple<BoolExpr, String>(ctx.mkEq( proto, ctx.mkInt(0)),"fw_auto_conf"));
 			nctx.softConstrPorts.add(new Tuple<BoolExpr, String>(ctx.mkEq(srcp, nctx.portMap.get("null")),"fw_auto_conf"));
 			nctx.softConstrPorts.add(new Tuple<BoolExpr, String>(ctx.mkEq(dstp, nctx.portMap.get("null")),"fw_auto_conf"));
  			
 			automatic_rules.add(ctx.mkAnd(
		  						nctx.equalNodeNameToPFRule("src", p_0, src),
								nctx.equalNodeNameToPFRule("dest", p_0, dst),
 			 					nctx.equalPortRangeToRule(nctx.functionsMap.get("src_port").apply(p_0), srcp),
 			 					nctx.equalPortRangeToRule(nctx.functionsMap.get("dest_port").apply(p_0), dstp),
 			 					nctx.equalPacketLv4ProtoToFwPacketLv4Proto(nctx.functionsMap.get("lv4proto").apply(p_0), proto)
 			 					));
  		}
  		
  		/**
	     * This section allow the creation of Z3 variables for defaultAction and rules.
	     * behaviour variables is the combination of the auto-configured rules.
	    */
  		
  		Expr defaultAction = ctx.mkConst(pf + "_auto_default_action", ctx.mkBoolSort());
  		Expr ruleAction = ctx.mkConst(pf + "_auto_action", ctx.mkBoolSort());

  		BoolExpr[] tmp = new BoolExpr[automatic_rules.size()];
  		
  		if(this.blacklisting_z3.equals(ctx.mkTrue())){
  			behaviour = ctx.mkNot(ctx.mkOr(automatic_rules.toArray(tmp)));
  			constraints.add(ctx.mkEq(defaultAction, ctx.mkTrue()));
  			constraints.add(ctx.mkEq(ruleAction, ctx.mkFalse()));
  		}else{
  			behaviour = ctx.mkOr(automatic_rules.toArray(tmp));
  			constraints.add(ctx.mkEq(defaultAction, ctx.mkFalse()));
  			constraints.add(ctx.mkEq(ruleAction, ctx.mkTrue()));
  		}
  		
  		/**
	     * This section allows the insertion of previously created hard contraints in a list.
	     */
  		if(autoplace) {
  			BoolExpr[] implications_tmp = new BoolExpr[implications1.size()];
  			constraints.add(ctx.mkImplies(ctx.mkOr(implications1.toArray(implications_tmp)),used));

  	 		implications_tmp = new BoolExpr[implications2.size()];
  			constraints.add(ctx.mkImplies(ctx.mkNot(used), ctx.mkAnd(implications2.toArray(implications_tmp))));
  		}
  		
  		generateForwardingRules(p_0);
    }



	private void generateForwardingRules(Expr p_0) {
		// forwarding rules are created for each left hop
  		for(Map.Entry<AllocationNode, Set<AllocationNode>> entry : source.getLeftHops().entrySet()) {
  			BoolExpr enumerateSend = createAndSend(entry, p_0, pf); 
  			BoolExpr recv= (BoolExpr) nctx.recv.apply(entry.getKey().getZ3Name(), pf, p_0);
  			if(autoplace) {
  				/**
  		    	 * This section allows the creation of the following forwarding rules:
  		    	 * For each p_0, if recv(leftHop, fw, p_0) && behaviour && fw_is_used -> for each rightHop, send(fw, rightHop, p_0)
  		    	 * In words:
  		    	 * For each packet, if the packet_filter has received this packet from a leftHop, its rules don't block the packet itself and the packet_filter is used,
  		    	 * then the packet_filter must send the packet to ALL its nextHops towards its destination 
  		    	 */
  				constraints.add(ctx.mkForall(new Expr[] { p_0 },
							ctx.mkImplies(
										ctx.mkAnd((BoolExpr) recv,
										behaviour, used), ctx.mkAnd(enumerateSend)
										),1, null, null, null, null));
  				/**
  		    	 * This section allows the creation of the following forwarding rules:
  		    	 * For each p_0, if recv(leftHop, fw, p_0) && !fw_is_used -> for each rightHop, send(fw, rightHop, p_0)
  		    	 * In words:
  		    	 * For each packet, if the packet_filter has received this packet from a leftHop and the packet_filter isn' used,
  		    	 * then the packet_filter must send the packet to ALL its nextHops towards its destination because it is a forwarder.
  		    	 */
  				constraints.add(ctx.mkForall(new Expr[] { p_0 },
						ctx.mkImplies(ctx.mkAnd((BoolExpr) recv,
								ctx.mkNot(used)), ctx.mkAnd(enumerateSend)), 
						1, null, null, null, null));
  			}else {
  				
  				/**
  		    	 * This section allows the creation of the following forwarding rules:
  		    	 * For each p_0, if recv(leftHop, fw, p_0)  -> for each rightHop, send(fw, rightHop, p_0)
  		    	 * In words:
  		    	 * For each packet, if the packet_filter has received this packet from a leftHop and the rules don't block it.
  		    	 * then the packet_filter must send the packet to ALL its nextHops towards its destination.
  		    	 * NOTE: only without autoplacement
  		    	 */
  				constraints.add(ctx.mkForall(new Expr[] { p_0 },
							ctx.mkImplies(ctx.mkAnd((BoolExpr) recv,
									behaviour), ctx.mkAnd(enumerateSend)), 
							1, null, null, null, null));
  			}
  			
  		}
  		
  		// forwarding rules are created for each right hop
  		for(Map.Entry<AllocationNode, Set<AllocationNode>> entry : source.getRightHops().entrySet()){
  	 		BoolExpr enumerateRecv = createOrRecv(entry, p_0, pf);
  	 		BoolExpr send = (BoolExpr) nctx.send.apply(pf, entry.getKey().getZ3Name(), p_0);
  	 		if(autoplace) { 
  	 			/**
  		    	 * This section allows the creation of the following forwarding rules:
  		    	 * For each p_0, if send(fw, rightHop, p_0) && fw_is_used -> exist leftHop, recv(leftHop, fw, p_0) && behaviour 
  		    	 * In words:
  		    	 * For each packet, if the packet_filter has sent the packet and it is used, it means that:
  		    	 * 1) it has received the packet from a leftHop
  		    	 * 2) its rules don't block the packet
  		    	 */
  	 			
  	 			constraints.add(ctx.mkForall(new Expr[] {p_0 }, 
  	  					ctx.mkImplies(ctx.mkAnd((BoolExpr) send, used),
  	  									ctx.mkAnd(enumerateRecv,
  	  											behaviour)), 1, null, null, null, null));
  	 			
  	 			/**
  		    	 * This section allows the creation of the following forwarding rules:
  		    	 * For each p_0, if send(fw, rightHop, p_0) && !fw_is_used -> exist leftHop, recv(leftHop, fw, p_0)  
  		    	 * In words:
  		    	 * For each packet, if the packet_filter has sent the packet and it isn't used, it means that it simply has received the packet from a leftHop
  		    	 * Basically it behaves as a forwarder.
  		    	 */
  	 			
  	 			constraints.add(ctx.mkForall(new Expr[] {p_0 }, 
  	  					ctx.mkImplies(ctx.mkAnd((BoolExpr) send, ctx.mkNot(used)),
  	  									ctx.mkAnd(enumerateRecv)), 1, null, null, null, null));
  	 		} else {
  	 			
  	 			/**
  		    	 * This section allows the creation of the following forwarding rules:
  		    	 * For each p_0, if send(fw, rightHop, p_0) -> exist leftHop, recv(leftHop, fw, p_0) && behaviour 
  		    	 * In words:
  		    	 * For each packet, if the packet_filter has sent the packet, it means that:
  		    	 * 1) it has received the packet from a leftHop
  		    	 * 2) its rules don't block the packet
  		    	 * NOTE: only without autplacement
  		    	 */
  	 			
  	 			constraints.add(ctx.mkForall(new Expr[] {p_0 }, 
  	  					ctx.mkImplies((BoolExpr) send,
  	  									ctx.mkAnd(enumerateRecv,
  	  											behaviour)), 1, null, null, null, null));
  	 		}
  	 		
  		}
	}
	
	
	
    /**
	 * This method allows to create HARD constraints for a manually configured packet_filter, basing on its ACLs precedently computed
	 */
    public void manualConfiguration (){
    	Expr p_0 = ctx.mkConst(pf+"_packet_filter_send_p_0", nctx.packetType);
    	rule_func = ctx.mkFuncDecl(pf+"_rule_func", new Sort[]{nctx.packetType},ctx.mkBoolSort());

    	/**
    	 * This section allows the creation of the following forwarding rules:
    	 * For each p_0, if recv(leftHop, fw, p_0) && acl_func.apply(p_0) -> for each rightHop, send(fw, rightHop, p_0)
    	 * In words:
    	 * For each packet, if the packet_filter has received this packet from a leftHop and its ACLs don't block the packet itself,
    	 * then the packet_filter must send the packet to ALL its nextHops towards its destination 
    	 */
    	
    	for(Map.Entry<AllocationNode, Set<AllocationNode>> entry : source.getLeftHops().entrySet()) {
  			BoolExpr enumerateSend =createAndSend(entry, p_0, pf);
  			BoolExpr recv= (BoolExpr) nctx.recv.apply(entry.getKey().getZ3Name(), pf, p_0);
  			constraints.add(ctx.mkForall(new Expr[] { p_0 },
							ctx.mkImplies(ctx.mkAnd((BoolExpr) recv,
							(BoolExpr)filtering_function.apply(p_0)), ctx.mkAnd(enumerateSend)), 
							1, null, null, null, null));
  		}
    	
    	/**
    	 * This section allows the creation of the following forwarding rules:
    	 * For each p_0, for each rightHop, send(fw, rightHop, p_0) -> exist at least one leftHop that recv(leftHop, fw, p_0) && acl_func.apply(p_0) -> 
    	 * In words:
    	 * For each packet, if the packet_filter has sent this packet to a nextHop, that it means that:
    	 * 1) this packet has been received by at least one leftHop
    	 * 2) this packet isn't blocked by ACLs
    	 */
    	for(Map.Entry<AllocationNode, Set<AllocationNode>> entry : source.getRightHops().entrySet()){
  	 		BoolExpr enumerateRecv = createOrRecv(entry, p_0, pf);
  			BoolExpr send = (BoolExpr) nctx.send.apply(pf, entry.getKey().getZ3Name(), p_0);
  	 		constraints.add(ctx.mkForall(new Expr[] {p_0 }, 
  	  					ctx.mkImplies((BoolExpr) send,
  	  									ctx.mkAnd(enumerateRecv,
  	  										(BoolExpr)filtering_function.apply(p_0))), 1, null, null, null, null));
  		}
    	
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
	 * This method allows to wrap the methoch which adds the contraints inside Z3 solver
	 * @param solver Istance of Z3 solver
	 */
	@Override
	public void addContraints(Optimize solver) {
		BoolExpr[] constr = new BoolExpr[constraints.size()];
	    solver.Add(constraints.toArray(constr));
	    additionalConstraints(solver);
	}
}
