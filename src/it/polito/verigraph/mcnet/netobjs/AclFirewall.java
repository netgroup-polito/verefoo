/*******************************************************************************
 * Copyright (c) 2017 Politecnico di Torino and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package it.polito.verigraph.mcnet.netobjs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
import com.microsoft.z3.IntNum;
import com.microsoft.z3.Optimize;
import com.microsoft.z3.Sort;

import it.polito.verifoo.rest.common.AllocationNode;
import it.polito.verifoo.rest.common.BadGraphError;
import it.polito.verifoo.rest.jaxb.ActionTypes;
import it.polito.verifoo.rest.jaxb.EType;
import it.polito.verifoo.rest.jaxb.FunctionalTypes;
import it.polito.verifoo.rest.jaxb.Node;
import it.polito.verigraph.mcnet.components.AclFirewallRule;
import it.polito.verigraph.mcnet.components.NetContext;
import it.polito.verigraph.mcnet.components.NetworkObject;
import it.polito.verigraph.mcnet.components.Quattro;
import it.polito.verigraph.mcnet.components.Tuple;

/** Represents a Firewall with the associated Access Control List
 *
 */
public class AclFirewall extends NetworkObject{

	List<BoolExpr> constraints; 
	Context ctx;
	DatatypeExpr fw;
	ArrayList<AclFirewallRule> acls;
	ArrayList<Quattro<DatatypeExpr,DatatypeExpr,IntNum,IntNum>> whiteAcls;
	private BoolExpr defaultAction;
	NetContext nctx;
	FuncDecl acl_func;
	FuncDecl rule_func;
	//FuncDecl acl_func_white;
	boolean autoconf;
	BoolExpr behaviour;
	private AllocationNode source;
	boolean blacklisting;
	boolean autoconfigured;


	public AclFirewall(AllocationNode source, Context ctx, NetContext nctx) {
		fw = source.getZ3Name();
		this.source = source;
		this.ctx = ctx;
		this.nctx = nctx;
		
		constraints = new ArrayList<BoolExpr>();
   		acls = new ArrayList<>();
   		whiteAcls = new ArrayList<>();
   		defaultAction = ctx.mkTrue();
   		used = ctx.mkBoolConst(fw+"_used");
   		
		autoplace = true;
		autoconfigured = true;
		isEndHost = false;
   		blacklisting = false;
	}


	
	/*protected void init(Context ctx, Object[]... args) {
		this.ctx = ctx;
		isEndHost=false;
   		constraints = new ArrayList<BoolExpr>();
   		acls = new ArrayList<>();
   		whiteAcls = new ArrayList<>();
   		z3Node = ((NetworkObject)args[0][0]).getZ3Node();
        fw = z3Node;
	    nctx = (NetContext)args[0][2];
	    
	    defaultAction = ((boolean)args[0][3])? ctx.mkTrue() : ctx.mkFalse();
	    neighbours = ((ArrayList<NetworkObject>) args[0][4]);
    	Expr p_0 = ctx.mkConst(fw+"_firewall_send_p_0", nctx.packet);
 		List<Expr> recvNeighbours = neighbours.stream().map(n -> nctx.recv.apply(n.getZ3Node(), fw, p_0)).collect(Collectors.toList());
 		BoolExpr[] tmp2 = new BoolExpr[recvNeighbours.size()];
 		enumerateRecvP0 = ctx.mkOr(recvNeighbours.toArray(tmp2));
 		List<Expr> sendNeighbours = neighbours.stream().map(n -> nctx.send.apply(fw, n.getZ3Node(), p_0)).collect(Collectors.toList());
		BoolExpr[] tmp3 = new BoolExpr[sendNeighbours.size()];
		enumerateSendP0 = ctx.mkOr(sendNeighbours.toArray(tmp3));
	    if(args[0].length > 5 && ((Integer) args[0][5]) != 0){
	    	if(args[0].length > 6 && args[0][6] != null){
	    		used = ctx.mkBoolConst(fw+"_used");
				autoplace = true;
				autoctx = (AutoContext) args[0][6];
			}
			else{
				autoplace = false;
			}
			autoconf = true; 
			//firewallSendRules((Integer) args[0][5]);
	    }
		else{
			autoplace = false;
			autoconf = false;
			firewallSendRules();
		}
	}*/
	

	/**
	 * Wrap add acls	
	 * @param policy
	 */
	public void setPolicy(ArrayList<Tuple<DatatypeExpr, DatatypeExpr>> policy){
		addAcls(policy);
	}
	
	public void setDefaultAction(boolean action){
		defaultAction = action? ctx.mkTrue(): ctx.mkFalse();
	}
	
	public void addAcls(ArrayList<Tuple<DatatypeExpr,DatatypeExpr>> acls){
		if(!autoconf){	// if not an autoconfiguration firewall
			for(Tuple<DatatypeExpr, DatatypeExpr> acl : acls){
				AclFirewallRule rule = new AclFirewallRule(nctx, ctx, false, acl._1, acl._2, "*", "*", 0, false);
				this.acls.add(rule);
			}
		}
			
	}
	
	public void addWhiteListAcls(ArrayList<Tuple<DatatypeExpr,DatatypeExpr>> acls){
		if(!autoconf){	// if not an autoconfiguration firewall
			for(Tuple<DatatypeExpr, DatatypeExpr> acl : acls){
				Quattro<DatatypeExpr, DatatypeExpr, IntNum, IntNum> rule = new Quattro<>(acl._1, acl._2, ctx.mkInt(0), ctx.mkInt(0));
				this.whiteAcls.add(rule);
			}
		}
	}
	
	public void addCompleteAcls(ArrayList<AclFirewallRule> acls){
		if(!autoconf){	// if not an autoconfiguration firewall
			this.acls.addAll(acls);
		}	
	}
	
	/*@Override
	public DatatypeExpr getZ3Node() {
		return fw;
	}*/
	
	public void generateAcl(){
		Node n = source.getNode();
		if(n.getFunctionalType().equals(FunctionalTypes.FIREWALL)){
				n.getConfiguration().getFirewall().getElements().forEach((e)->{
						ArrayList<AclFirewallRule> acl = new ArrayList<>();
						String src_port = e.getSrcPort()!=null? e.getSrcPort():"*";
						String dst_port = e.getDstPort()!=null? e.getDstPort():"*";
						boolean directional = e.isDirectional()!=null? e.isDirectional():true;
						int protocol = e.getProtocol()!=null? e.getProtocol().ordinal():0;
						boolean action;

						if(e.getAction() != null){
							if(e.getAction().equals(ActionTypes.ALLOW))
								action = true;
							else
								action = false;
						}else{
							//if not specified the action of the rule is the opposite of the default behaviour otherwise the rule would not be necessary
							if(n.getConfiguration().getFirewall().getDefaultAction() == null){
								action = false;
							}
							else if(n.getConfiguration().getFirewall().getDefaultAction().equals(ActionTypes.ALLOW))
								action = false;
							else
								action = true;
						}
						if(nctx.am.get(e.getSource())!=null&&nctx.am.get(e.getDestination())!=null){
							try{
								AclFirewallRule rule=new AclFirewallRule(nctx, ctx, action, nctx.am.get(e.getSource()),nctx.am.get(e.getDestination()),
																			src_port,dst_port, protocol, directional);
								acl.add(rule);
							   
							}catch(NumberFormatException ex){
								throw new BadGraphError(n.getName()+" has invalid configuration: "+ex.getMessage(), EType.INVALID_NODE_CONFIGURATION);
							}
							
						}else{
							try{
								AclFirewallRule rule=new AclFirewallRule(nctx, ctx, action, e.getSource(),e.getDestination(),
																			src_port,dst_port, protocol, directional);
								acl.add(rule);
							    
							}catch(NumberFormatException ex){
								throw new BadGraphError(n.getName()+" has invalid configuration: "+ex.getMessage(), EType.INVALID_NODE_CONFIGURATION);
							}
						}
						addCompleteAcls(acl);
				});
		}
		
	}
	
    public void firewallSendRules (){
    	Expr p_0 = ctx.mkConst(fw+"_firewall_send_p_0", nctx.packet);
    	Expr n_0 = ctx.mkConst(fw+"_firewall_send_n_0", nctx.node);
    	Expr n_1 = ctx.mkConst(fw+"_firewall_send_n_1", nctx.node);
    	//IntExpr t_0 = ctx.mkIntConst(fw+"_firewall_send_t_0");
    	//IntExpr t_1 = ctx.mkIntConst(fw+"_firewall_send_t_1");
    	acl_func = ctx.mkFuncDecl(fw+"_acl_func", new Sort[]{nctx.packet},ctx.mkBoolSort());
    	rule_func = ctx.mkFuncDecl(fw+"_rule_func", new Sort[]{nctx.packet},ctx.mkBoolSort());
    	//acl_func_white = ctx.mkFuncDecl(fw+"_acl_func_white", new Sort[]{nctx.address, nctx.address , ctx.mkIntSort(), ctx.mkIntSort()},ctx.mkBoolSort());
    	//Constraint1		send(fw, n_0, p, t_0)  -> (exist n_1,t_1 : (recv(n_1, fw, p, t_1) && 
    	//    				t_1 < t_0 && !acl_func(p.src,p.dest))
    	
    	for(Map.Entry<AllocationNode, Set<AllocationNode>> entry : source.getLeftHops().entrySet()) {
  			
  			AllocationNode an = entry.getKey();
  			Expr e = an.getZ3Name();
  			BoolExpr recv= (BoolExpr) nctx.recv.apply(e, fw, p_0);
  			List<Expr> list = entry.getValue().stream().map(n -> n.getZ3Name()).collect(Collectors.toList());
  			List<Expr> sendNeighbours = list.stream().map(n -> (BoolExpr) nctx.send.apply(fw, n, p_0)).distinct().collect(Collectors.toList());
  			BoolExpr[] tmp3 = new BoolExpr[list.size()];
  			BoolExpr enumerateSend = ctx.mkAnd(sendNeighbours.toArray(tmp3));
  			//System.out.println(recv);
  			//System.out.println(enumerateSend);
  			
  			constraints.add(ctx.mkForall(new Expr[] { p_0 },
							ctx.mkImplies(ctx.mkAnd((BoolExpr) recv,
							(BoolExpr)acl_func.apply(p_0)), ctx.mkAnd(enumerateSend)), 
							1, null, null, null, null));
  			

  		}
    	
    	for(Map.Entry<AllocationNode, Set<AllocationNode>> entry : source.getRightHops().entrySet()){
  			AllocationNode an = entry.getKey();
  			Expr e = an.getZ3Name();
  			BoolExpr send = (BoolExpr) nctx.send.apply(fw, e, p_0);

  			List<Expr> list = entry.getValue().stream().map(n -> n.getZ3Name()).collect(Collectors.toList());
  			List<Expr> recvNeighbours = list.stream().map(n -> (BoolExpr) nctx.recv.apply(n, fw, p_0)).distinct().collect(Collectors.toList());
  			BoolExpr[] tmp2 = new BoolExpr[list.size()];
  	 		BoolExpr enumerateRecv = ctx.mkOr(recvNeighbours.toArray(tmp2));
  	 		
  	 		constraints.add(ctx.mkForall(new Expr[] {p_0 }, 
  	  					ctx.mkImplies((BoolExpr) send,
  	  									ctx.mkAnd(enumerateRecv,
  	  										(BoolExpr)acl_func.apply(p_0))), 1, null, null, null, null));
  	 		
  	 		
  		}
    	
    	
    	
    	
    	
 		/*List<Expr> recvNeighbours = neighbours.stream().map(n -> nctx.recv.apply(n.getZ3Node(), fw, p_0)).collect(Collectors.toList());
 		BoolExpr[] tmp2 = new BoolExpr[recvNeighbours.size()];
 		BoolExpr enumerateRecv = ctx.mkOr(recvNeighbours.toArray(tmp2));
 		List<Expr> sendNeighbours = neighbours.stream().map(n -> nctx.send.apply(fw, n.getZ3Node(), p_0)).collect(Collectors.toList());
		BoolExpr[] tmp3 = new BoolExpr[sendNeighbours.size()];
		BoolExpr enumerateSend = ctx.mkOr(sendNeighbours.toArray(tmp3));
    	  constraints.add(
	            	ctx.mkForall(new Expr[]{n_0, p_0}, 
	    	            ctx.mkImplies(
	    	            		enumerateSend,
	    	            	
	    	            			ctx.mkAnd(
	    	            						enumerateRecv, 
	    	            						(BoolExpr)acl_func.apply(p_0)
	    	            							    	            								
	    	            						((BoolExpr)acl_func_white.apply(nctx.pf.get("src").apply(p_0), 
							    	            								nctx.pf.get("dest").apply(p_0),
																				nctx.pf.get("src_port").apply(p_0), 
																				nctx.pf.get("dest_port").apply(p_0)))
	    	            							    	            						
	    	            						)),1,null,null,null,null));

    	  
    	  //Constraint2 obliges this VNF to send the packets that have been received
    	  constraints.add(
	            	ctx.mkForall(new Expr[]{p_0},
	            			ctx.mkImplies(	
	            					ctx.mkAnd( enumerateRecv,
		            							(BoolExpr)acl_func.apply(p_0)
		            							((BoolExpr)acl_func_white.apply(nctx.pf.get("src").apply(p_0), 
							    	            								nctx.pf.get("dest").apply(p_0),
																				nctx.pf.get("src_port").apply(p_0), 
																				nctx.pf.get("dest_port").apply(p_0)))
	            							),
	            						ctx.mkAnd(
	            								enumerateSend
	            								//,ctx.mkNot((BoolExpr)acl_func.apply(nctx.pf.get("src").apply(p_0), nctx.pf.get("dest").apply(p_0)))
	            								)
	            						
	    	    	            	)
	            			,1,null,null,null,null));*/
    	  
    }
    
    public void firewallSendRules(Integer nRules) {
    	
    	Expr p_0 = ctx.mkConst(fw + "_firewall_send_p_0", nctx.packet);
        acl_func = ctx.mkFuncDecl(fw + "_acl_func", new Sort[] { nctx.packet}, ctx.mkBoolSort());
        //acl_func_white = ctx.mkFuncDecl(fw + "_acl_func", new Sort[] { nctx.address, nctx.address , ctx.mkIntSort(), ctx.mkIntSort() }, ctx.mkBoolSort());
        List<BoolExpr> rules = new ArrayList<>();
        List<BoolExpr> implications1 = new ArrayList<BoolExpr>();
  		List<BoolExpr> implications2 = new ArrayList<BoolExpr>();
  		
  		if(autoplace) {
  			nctx.softConstrAutoPlace.add(new Tuple<BoolExpr, String>(ctx.mkNot(used), "fw_auto_conf"));
  		}
  		
  		//System.out.println(nRules + " " + fw);
  		for(int i = 0; i < nRules; i++){
  			Expr src = ctx.mkConst(fw + "_auto_src_"+i, nctx.address);
  			Expr dst = ctx.mkConst(fw + "_auto_dst_"+i, nctx.address);
  			Expr proto = ctx.mkConst(fw + "_auto_proto_"+i, ctx.mkIntSort());
  			Expr srcp = ctx.mkConst(fw + "_auto_srcp_"+i, nctx.port_range);
  			Expr dstp = ctx.mkConst(fw + "_auto_dstp_"+i, nctx.port_range);
  			IntExpr srcAuto1 = ctx.mkIntConst(fw + "_auto_src_ip_1_"+i);
  			IntExpr srcAuto2 = ctx.mkIntConst(fw + "_auto_src_ip_2_"+i);
  			IntExpr srcAuto3 = ctx.mkIntConst(fw + "_auto_src_ip_3_"+i);
  			IntExpr srcAuto4 = ctx.mkIntConst(fw + "_auto_src_ip_4_"+i);
  			IntExpr dstAuto1 = ctx.mkIntConst(fw + "_auto_dst_ip_1_"+i);
  			IntExpr dstAuto2 = ctx.mkIntConst(fw + "_auto_dst_ip_2_"+i);
  			IntExpr dstAuto3 = ctx.mkIntConst(fw + "_auto_dst_ip_3_"+i);
  			IntExpr dstAuto4 = ctx.mkIntConst(fw + "_auto_dst_ip_4_"+i);
  			if(autoplace) {
  				implications1.add(ctx.mkAnd(ctx.mkNot(ctx.mkEq( src, this.nctx.am.get("null"))),
  						ctx.mkNot(ctx.mkEq( dst, this.nctx.am.get("null")))));
  	  			implications2.add(ctx.mkAnd(ctx.mkEq( src, this.nctx.am.get("null")),
  						ctx.mkEq( dst, this.nctx.am.get("null"))));
  			}
  			
  			constraints.add(ctx.mkAnd(
						ctx.mkEq(nctx.ip_functions.get("ipAddr_1").apply(src), srcAuto1),
						ctx.mkEq(nctx.ip_functions.get("ipAddr_2").apply(src), srcAuto2),
						ctx.mkEq(nctx.ip_functions.get("ipAddr_3").apply(src), srcAuto3),
						ctx.mkEq(nctx.ip_functions.get("ipAddr_4").apply(src), srcAuto4),
						ctx.mkEq(nctx.ip_functions.get("ipAddr_1").apply(dst), dstAuto1),
						ctx.mkEq(nctx.ip_functions.get("ipAddr_2").apply(dst), dstAuto2),
						ctx.mkEq(nctx.ip_functions.get("ipAddr_3").apply(dst), dstAuto3),
						ctx.mkEq(nctx.ip_functions.get("ipAddr_4").apply(dst), dstAuto4)
						)
				); 
  			
  			nctx.softConstrWildcard.add(new Tuple<BoolExpr, String>(ctx.mkEq((IntExpr)nctx.ip_functions.get("ipAddr_1").apply(nctx.am.get("wildcard")),srcAuto1), "fw_auto_conf"));
  			nctx.softConstrWildcard.add(new Tuple<BoolExpr, String>(ctx.mkEq((IntExpr)nctx.ip_functions.get("ipAddr_2").apply(nctx.am.get("wildcard")),srcAuto2), "fw_auto_conf"));
  			nctx.softConstrWildcard.add(new Tuple<BoolExpr, String>(ctx.mkEq((IntExpr)nctx.ip_functions.get("ipAddr_3").apply(nctx.am.get("wildcard")),srcAuto3), "fw_auto_conf"));
  			nctx.softConstrWildcard.add(new Tuple<BoolExpr, String>(ctx.mkEq((IntExpr)nctx.ip_functions.get("ipAddr_4").apply(nctx.am.get("wildcard")),srcAuto4), "fw_auto_conf"));
  			nctx.softConstrWildcard.add(new Tuple<BoolExpr, String>(ctx.mkEq((IntExpr)nctx.ip_functions.get("ipAddr_1").apply(nctx.am.get("wildcard")),dstAuto1), "fw_auto_conf"));
  			nctx.softConstrWildcard.add(new Tuple<BoolExpr, String>(ctx.mkEq((IntExpr)nctx.ip_functions.get("ipAddr_2").apply(nctx.am.get("wildcard")),dstAuto2), "fw_auto_conf"));
  			nctx.softConstrWildcard.add(new Tuple<BoolExpr, String>(ctx.mkEq((IntExpr)nctx.ip_functions.get("ipAddr_3").apply(nctx.am.get("wildcard")),dstAuto3), "fw_auto_conf"));
  			nctx.softConstrWildcard.add(new Tuple<BoolExpr, String>(ctx.mkEq((IntExpr)nctx.ip_functions.get("ipAddr_4").apply(nctx.am.get("wildcard")),dstAuto4), "fw_auto_conf"));
  			
  			/*constraints.add(ctx.mkNot(
					ctx.mkEq(nctx.ip_functions.get("ipAddr_4").apply(nctx.am.get("wildcard")), srcAuto4)	
			)); */
  			
  			nctx.softConstrAutoConf.add(new Tuple<BoolExpr, String>(ctx.mkAnd(
 						ctx.mkEq(nctx.ip_functions.get("ipAddr_1").apply(nctx.am.get("null")), srcAuto1),
 						ctx.mkEq(nctx.ip_functions.get("ipAddr_2").apply(nctx.am.get("null")), srcAuto2),
 						ctx.mkEq(nctx.ip_functions.get("ipAddr_3").apply(nctx.am.get("null")), srcAuto3),
 						ctx.mkEq(nctx.ip_functions.get("ipAddr_4").apply(nctx.am.get("null")), srcAuto4),
 						ctx.mkEq(nctx.ip_functions.get("ipAddr_1").apply(nctx.am.get("null")), dstAuto1),
 						ctx.mkEq(nctx.ip_functions.get("ipAddr_2").apply(nctx.am.get("null")), dstAuto2),
 						ctx.mkEq(nctx.ip_functions.get("ipAddr_3").apply(nctx.am.get("null")), dstAuto3),
 						ctx.mkEq(nctx.ip_functions.get("ipAddr_4").apply(nctx.am.get("null")), dstAuto4)
 						), "fw_auto_conf"));
  			
 			nctx.softConstrProtoWildcard.add(new Tuple<BoolExpr, String>(ctx.mkEq( proto, ctx.mkInt(0)),"fw_auto_conf"));
 			nctx.softConstrPorts.add(new Tuple<BoolExpr, String>(ctx.mkEq(srcp, nctx.pm.get("null")),"fw_auto_port"));
 			nctx.softConstrPorts.add(new Tuple<BoolExpr, String>(ctx.mkEq(dstp, nctx.pm.get("null")),"fw_auto_port"));
  			rules.add(ctx.mkAnd(
		  						nctx.equalNodeNameToFWRule("src", p_0, src),
								nctx.equalNodeNameToFWRule("dest", p_0, dst),
  								//nctx.equalPacketLv4ProtoToFwPacketLv4Proto(nctx.pf.get("lv4proto").apply(p_0), proto),
 			 					ctx.mkEq(nctx.pf.get("lv4proto").apply(p_0), proto),
 			 					ctx.mkEq(nctx.pf.get("src_port").apply(p_0), srcp),
 			 					ctx.mkEq(nctx.pf.get("dest_port").apply(p_0), dstp)
 			 					));
  		}
  		
  		Expr defaultAction = ctx.mkConst(fw + "_auto_default_action", ctx.mkBoolSort());
  		Expr ruleAction = ctx.mkConst(fw + "_auto_action", ctx.mkBoolSort());

  		BoolExpr[] tmp = new BoolExpr[rules.size()];
  		
  		if(this.defaultAction.equals(ctx.mkTrue())){
  			behaviour = ctx.mkNot(ctx.mkOr(rules.toArray(tmp)));
  			constraints.add(ctx.mkEq(defaultAction, ctx.mkTrue()));
  			constraints.add(ctx.mkEq(ruleAction, ctx.mkFalse()));
  		}else{
  			behaviour = ctx.mkOr(rules.toArray(tmp));
  			constraints.add(ctx.mkEq(defaultAction, ctx.mkFalse()));
  			constraints.add(ctx.mkEq(ruleAction, ctx.mkTrue()));
  		}
  		
  		
  		for(Map.Entry<AllocationNode, Set<AllocationNode>> entry : source.getLeftHops().entrySet()) {
  			
  			AllocationNode an = entry.getKey();
  			Expr e = an.getZ3Name();
  			BoolExpr recv= (BoolExpr) nctx.recv.apply(e, fw, p_0);
  			List<Expr> list = entry.getValue().stream().map(n -> n.getZ3Name()).collect(Collectors.toList());
  			List<Expr> sendNeighbours = list.stream().map(n -> (BoolExpr) nctx.send.apply(fw, n, p_0)).distinct().collect(Collectors.toList());
  			BoolExpr[] tmp3 = new BoolExpr[list.size()];
  			BoolExpr enumerateSend = ctx.mkAnd(sendNeighbours.toArray(tmp3));
  			//System.out.println(recv);
  			//System.out.println(enumerateSend);
  			if(autoplace) {
  				
  				constraints.add(ctx.mkForall(new Expr[] { p_0 },
							ctx.mkImplies(ctx.mkAnd((BoolExpr) recv,
									behaviour, used), ctx.mkAnd(enumerateSend)), 
							1, null, null, null, null));
  				constraints.add(ctx.mkForall(new Expr[] { p_0 },
						ctx.mkImplies(ctx.mkAnd((BoolExpr) recv,
								ctx.mkNot(used)), ctx.mkAnd(enumerateSend)), 
						1, null, null, null, null));
  			}else {
  				constraints.add(ctx.mkForall(new Expr[] { p_0 },
							ctx.mkImplies(ctx.mkAnd((BoolExpr) recv,
									behaviour), ctx.mkAnd(enumerateSend)), 
							1, null, null, null, null));
  			}
  			
  		}
  		
  		for(Map.Entry<AllocationNode, Set<AllocationNode>> entry : source.getRightHops().entrySet()){
  			AllocationNode an = entry.getKey();
  			Expr e = an.getZ3Name();
  			BoolExpr send = (BoolExpr) nctx.send.apply(fw, e, p_0);

  			List<Expr> list = entry.getValue().stream().map(n -> n.getZ3Name()).collect(Collectors.toList());
  			
  			
  			List<Expr> recvNeighbours = list.stream().map(n -> (BoolExpr) nctx.recv.apply(n, fw, p_0)).distinct().collect(Collectors.toList());
  			
  			
  			BoolExpr[] tmp2 = new BoolExpr[list.size()];
  	 		BoolExpr enumerateRecv = ctx.mkOr(recvNeighbours.toArray(tmp2));
  	 		if(autoplace) {
  	 			constraints.add(ctx.mkForall(new Expr[] {p_0 }, 
  	  					ctx.mkImplies(ctx.mkAnd((BoolExpr) send, used),
  	  									ctx.mkAnd(enumerateRecv,
  	  											behaviour)), 1, null, null, null, null));
  	 			constraints.add(ctx.mkForall(new Expr[] {p_0 }, 
  	  					ctx.mkImplies(ctx.mkAnd((BoolExpr) send, ctx.mkNot(used)),
  	  									ctx.mkAnd(enumerateRecv)), 1, null, null, null, null));
  	 		} else {
  	 			constraints.add(ctx.mkForall(new Expr[] {p_0 }, 
  	  					ctx.mkImplies((BoolExpr) send,
  	  									ctx.mkAnd(enumerateRecv,
  	  											behaviour)), 1, null, null, null, null));
  	 		}
  	 		
  		}
  		
  		if(autoplace) {
  			BoolExpr[] tmp3 = new BoolExpr[implications1.size()];
  			//System.out.println("Adding to fw constraints: " + ctx.mkImplies(ctx.mkAnd(implications1.toArray(tmp3)), used));
  			constraints.add(ctx.mkImplies(ctx.mkOr(implications1.toArray(tmp3)),used));

  	 		BoolExpr[] tmp4 = new BoolExpr[implications2.size()];
  			//System.out.println("Adding to fw constraints: " + ctx.mkImplies(ctx.mkNot(used), ctx.mkAnd(implications2.toArray(tmp4))));
  			constraints.add(ctx.mkImplies(ctx.mkNot(used), ctx.mkAnd(implications2.toArray(tmp4))));
  		}
  		
    	
    }
    
   
    
     		
  
 	public boolean isBlacklisting() {
		return blacklisting;
	}



	public void setBlacklisting(boolean blacklisting) {
		this.blacklisting = blacklisting;
	}



	private void aclConstraints(Optimize solver){
 		Expr p_0 = ctx.mkConst(fw+"_firewall_acl_p_0", nctx.packet);
 		Expr a_0 = ctx.mkConst(fw+"_rule_action_p_0", ctx.mkBoolSort());
 		Expr n_0 = ctx.mkConst(fw + "_firewall_send_n_0", nctx.node);
 		Expr n_1 = ctx.mkConst(fw + "_firewall_send_n_1", nctx.node);
 		//System.out.println("Firewall " +fw+" -> default action: " + (defaultAction.equals(ctx.mkTrue())? "ALLOW":"DENY"));
    	if (acls.size() == 0){
    		//If the size of the ACL list is empty then by default acl_func must be false
    		 solver.Add(ctx.mkForall(new Expr[]{p_0},
						ctx.mkEq( 
								acl_func.apply(p_0), defaultAction),1,null,null,null,null));
    	}else{
    			List<IntExpr> rules = new ArrayList<>();
    			BoolExpr[] rule_map = new BoolExpr[acls.size()];
    	        for(int y=0;y<acls.size();y++){
    	        	AclFirewallRule rule = acls.get(y);
    	        	/*System.out.println(fw + " rule: "+rule.getAction()+" from " + rule.getSource() + ":"+rule.getSrc_port() +
    	        											" to " + rule.getDestination()+":"+rule.getDst_port());*/
    	        	rule_map[y] = rule.matchPacket(p_0);
					solver.Add(ctx.mkForall(new Expr[]{p_0, n_0},
											// at this point we assume that the rules are conflict free
											ctx.mkImplies(ctx.mkAnd((BoolExpr) nctx.recv.apply(new Expr[] { n_0, fw, p_0 }),
																rule.matchPacket(p_0))
														,ctx.mkAnd(ctx.mkEq(
																			acl_func.apply(p_0),
																			rule.getAction()
																			)
																	)
													  )
								, 1, null, null, null, null));
    	        }
    	        solver.Add(ctx.mkForall(new Expr[]{p_0, n_0},
										ctx.mkImplies(
											//no rule matches the packet -> the acl must allow the default behaviour
											ctx.mkAnd((BoolExpr) nctx.recv.apply(new Expr[] { n_0, fw, p_0 }),
													ctx.mkNot(ctx.mkOr(rule_map))
													),
											ctx.mkEq(
													acl_func.apply(p_0),
													this.defaultAction
													))
								, 1, null, null, null, null));

    	}
    }



	@Override
	public void addContraints(Optimize solver) {
		BoolExpr[] constr = new BoolExpr[constraints.size()];
	    solver.Add(constraints.toArray(constr));
	    aclConstraints(solver);
	}


	public boolean isAutoplace() {
		return autoplace;
	}


	public void setAutoplace(boolean autoplace) {
		this.autoplace = autoplace;
	}
	
	public boolean isAutoconfigured() {
		return autoconfigured;
	}


	public void setAutoconfigured(boolean autoconfigured) {
		this.autoconfigured = autoconfigured;
	}
 	
    

}






// for an autoplacement firewall, to avoid further performance issues it is advisable to run this feature only with a BASIC autoconfiguration
	/*private void firewallSendRulesAutoPlacementOLD(Integer nRules) {
		Expr p_0 = ctx.mkConst(fw + "_firewall_send_p_0", nctx.packet);
		Expr n_0 = ctx.mkConst(fw + "_firewall_send_n_0", nctx.node);
		Expr n_1 = ctx.mkConst(fw + "_firewall_send_n_1", nctx.node);
     acl_func = ctx.mkFuncDecl(fw + "_acl_func", new Sort[] { nctx.packet}, ctx.mkBoolSort());
     //acl_func_white = ctx.mkFuncDecl(fw + "_acl_func", new Sort[] { nctx.address, nctx.address , ctx.mkIntSort(), ctx.mkIntSort() }, ctx.mkBoolSort());
     List<BoolExpr> rules = new ArrayList<>();
		List<BoolExpr> implications1 = new ArrayList<BoolExpr>();
		List<BoolExpr> implications2 = new ArrayList<BoolExpr>();
		for(int i = 0; i < nRules; i++){
			Expr src = ctx.mkConst(fw + "_auto_src_"+i, nctx.address);
			Expr dst = ctx.mkConst(fw + "_auto_dst_"+i, nctx.address);
			autoctx.softConstrAutoConf.add(new Tuple<BoolExpr, String>(ctx.mkEq( src, this.nctx.am.get("null")),"fw_auto_conf"));
			autoctx.softConstrAutoConf.add(new Tuple<BoolExpr, String>(ctx.mkEq( dst, this.nctx.am.get("null")),"fw_auto_conf"));
			implications1.add(ctx.mkAnd(ctx.mkNot(ctx.mkEq( src, this.nctx.am.get("null"))),
									ctx.mkNot(ctx.mkEq( dst, this.nctx.am.get("null")))));
		implications2.add(ctx.mkAnd(ctx.mkEq( src, this.nctx.am.get("null")),
									ctx.mkEq( dst, this.nctx.am.get("null"))));
			rules.add(ctx.mkAnd(
								ctx.mkEq(nctx.pf.get("src").apply(p_0), src),
								ctx.mkEq(nctx.pf.get("dest").apply(p_0), dst)
								));
		}

		BoolExpr[] tmp = new BoolExpr[rules.size()];
		constraints.add(ctx.mkForall(new Expr[] { n_0, p_0 }, 
				ctx.mkImplies(
								(BoolExpr) nctx.send.apply(new Expr[] { fw, n_0, p_0 }),
								ctx.mkAnd(ctx.mkExists(new Expr[] { n_1 }, nctx.recv.apply(n_1, fw, p_0), 1, null, null, null, null),
										ctx.mkNot(
												  ctx.mkOr(
														  rules.toArray(tmp)
														  )		
												  ))), 1, null, null, null, null));;

		BoolExpr[] tmp2 = new BoolExpr[rules.size()];
		constraints.add(ctx.mkForall(new Expr[] { n_0, p_0 },
										ctx.mkImplies(ctx.mkAnd((BoolExpr) nctx.recv.apply(n_0, fw, p_0),
												ctx.mkNot(
															ctx.mkOr(
																	rules.toArray(tmp2)
															   )
												)), ctx.mkAnd(ctx.mkExists(new Expr[] { n_1 }, (BoolExpr) nctx.send.apply(new Expr[] { fw, n_1, p_0 }), 1, null, null, null, null))), 
										1, null, null, null, null));

	BoolExpr[] tmp3 = new BoolExpr[implications1.size()];
	//System.out.println("Adding to fw constraints: " + ctx.mkImplies(ctx.mkAnd(implications1.toArray(tmp3)), used));
	constraints.add(     ctx.mkImplies(ctx.mkOr(implications1.toArray(tmp3)),used)    );

		BoolExpr[] tmp4 = new BoolExpr[implications2.size()];
	//System.out.println("Adding to fw constraints: " + ctx.mkImplies(ctx.mkNot(used), ctx.mkAnd(implications2.toArray(tmp4))));
	constraints.add(     ctx.mkImplies(ctx.mkNot(used), ctx.mkAnd(implications2.toArray(tmp4)))    );

	}*/