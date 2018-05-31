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
import java.util.List;

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

import it.polito.verifoo.rest.common.AutoContext;
import it.polito.verigraph.mcnet.components.AclFirewallRule;
import it.polito.verigraph.mcnet.components.NetContext;
import it.polito.verigraph.mcnet.components.Network;
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
	Network net;
	NetContext nctx;
	FuncDecl acl_func;
	FuncDecl rule_func;
	//FuncDecl acl_func_white;
	private boolean autoconf, autoplace;
	private AutoContext autoctx;
	
	
	public AclFirewall(Context ctx, Object[]... args) {
		super(ctx, args);
	}

	@Override
	protected void init(Context ctx, Object[]... args) {
		this.ctx = ctx;
		isEndHost=false;
   		constraints = new ArrayList<BoolExpr>();
   		acls = new ArrayList<>();
   		whiteAcls = new ArrayList<>();
   		z3Node = ((NetworkObject)args[0][0]).getZ3Node();
        fw = z3Node;
	    net = (Network)args[0][1];
	    nctx = (NetContext)args[0][2];
	    net.saneSend(this);
	    defaultAction = ((boolean)args[0][3])? ctx.mkTrue() : ctx.mkFalse();
	    if(args[0].length > 4 && ((Integer) args[0][4]) != 0){
	    	if(args[0].length > 5 && args[0][5] != null){
	    		used = ctx.mkBoolConst(fw+"_used");
				autoplace = true;
				autoctx = (AutoContext) args[0][5];
			}
			else{
				autoplace = false;
			}
			autoconf = true; 
			firewallSendRules((Integer) args[0][4]);
	    }
		else{
			autoplace = false;
			autoconf = false;
			firewallSendRules();
		}
	}
	

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
	
	@Override
	public DatatypeExpr getZ3Node() {
		return fw;
	}
	

	@Override
	protected void addConstraints(Optimize solver) {
			BoolExpr[] constr = new BoolExpr[constraints.size()];
		    solver.Add(constraints.toArray(constr));
		    aclConstraints(solver);
		    //aclNewConstraints(solver);
	}

    private void firewallSendRules (){
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
    	  constraints.add(
	            	ctx.mkForall(new Expr[]{n_0, p_0}, 
	    	            ctx.mkImplies(
	    	            	(BoolExpr)nctx.send.apply(new Expr[]{ fw, n_0, p_0}),
	    	            	
	    	            			ctx.mkAnd(
	    	            						ctx.mkExists(new Expr[]{n_1}, 
	    	            								nctx.recv.apply(n_1, fw, p_0),1,null,null,null,null), 
	    	            						(BoolExpr)acl_func.apply(p_0)
	    	            							    	            								
	    	            						/*((BoolExpr)acl_func_white.apply(nctx.pf.get("src").apply(p_0), 
							    	            								nctx.pf.get("dest").apply(p_0),
																				nctx.pf.get("src_port").apply(p_0), 
																				nctx.pf.get("dest_port").apply(p_0)))*/
	    	            							    	            						
	    	            						)),1,null,null,null,null));

    	  
    	  //Constraint2 obliges this VNF to send the packets that have been received
    	  constraints.add(
	            	ctx.mkForall(new Expr[]{n_0, p_0},
	            			ctx.mkImplies(	
	            					ctx.mkAnd( (BoolExpr)nctx.recv.apply(n_0, fw, p_0),
		            							(BoolExpr)acl_func.apply(p_0)
		            							/*((BoolExpr)acl_func_white.apply(nctx.pf.get("src").apply(p_0), 
							    	            								nctx.pf.get("dest").apply(p_0),
																				nctx.pf.get("src_port").apply(p_0), 
																				nctx.pf.get("dest_port").apply(p_0)))*/
	            							),
	            						ctx.mkAnd(ctx.mkExists(new Expr[]{n_1}, (BoolExpr)nctx.send.apply(new Expr[]{ fw, n_1, p_0}),1,null,null,null,null)
	            								//,ctx.mkNot((BoolExpr)acl_func.apply(nctx.pf.get("src").apply(p_0), nctx.pf.get("dest").apply(p_0)))
	            								)
	            						
	    	    	            	)
	            			,1,null,null,null,null));
    	  
    }
    
    private void firewallSendRules(Integer nRules) {
    	if(autoplace)
    		firewallSendRulesAutoPlacement(nRules);
    	else
    		firewallSendRulesAutoConf(nRules);
    }
    
    // for an autoplacement firewall
  	private void firewallSendRulesAutoPlacement(Integer nRules) {
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
  			Expr proto = ctx.mkConst(fw + "_auto_proto_"+i, ctx.mkIntSort());
  			Expr start_srcp = ctx.mkConst(fw + "_auto_start_srcp_"+i, ctx.mkIntSort());
  			Expr end_srcp = ctx.mkConst(fw + "_auto_end_srcp_"+i, ctx.mkIntSort());
  			Expr start_dstp = ctx.mkConst(fw + "_auto_start_dstp_"+i, ctx.mkIntSort());
  			Expr end_dstp = ctx.mkConst(fw + "_auto_end_dstp_"+i, ctx.mkIntSort());
  			IntExpr srcAuto1 = ctx.mkIntConst(fw + "_auto_src_ip_1_"+i);
  			IntExpr srcAuto2 = ctx.mkIntConst(fw + "_auto_src_ip_2_"+i);
  			IntExpr srcAuto3 = ctx.mkIntConst(fw + "_auto_src_ip_3_"+i);
  			IntExpr srcAuto4 = ctx.mkIntConst(fw + "_auto_src_ip_4_"+i);
  			IntExpr dstAuto1 = ctx.mkIntConst(fw + "_auto_dst_ip_1_"+i);
  			IntExpr dstAuto2 = ctx.mkIntConst(fw + "_auto_dst_ip_2_"+i);
  			IntExpr dstAuto3 = ctx.mkIntConst(fw + "_auto_dst_ip_3_"+i);
  			IntExpr dstAuto4 = ctx.mkIntConst(fw + "_auto_dst_ip_4_"+i);
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
 			//autoctx.softConstrAutoConf.add(new Tuple<BoolExpr, String>(ctx.mkEq( src, this.nctx.am.get("null")),"fw_auto_conf"));
 			//autoctx.softConstrAutoConf.add(new Tuple<BoolExpr, String>(ctx.mkEq( dst, this.nctx.am.get("null")),"fw_auto_conf"));
			autoctx.softConstrWildcard.add(new Tuple<BoolExpr, String>(ctx.mkEq((IntExpr)nctx.ip_functions.get("ipAddr_1").apply(nctx.am.get("wildcard")),srcAuto1), "fw_auto_conf"));
			autoctx.softConstrWildcard.add(new Tuple<BoolExpr, String>(ctx.mkEq((IntExpr)nctx.ip_functions.get("ipAddr_2").apply(nctx.am.get("wildcard")),srcAuto2), "fw_auto_conf"));
			autoctx.softConstrWildcard.add(new Tuple<BoolExpr, String>(ctx.mkEq((IntExpr)nctx.ip_functions.get("ipAddr_3").apply(nctx.am.get("wildcard")),srcAuto3), "fw_auto_conf"));
			autoctx.softConstrWildcard.add(new Tuple<BoolExpr, String>(ctx.mkEq((IntExpr)nctx.ip_functions.get("ipAddr_4").apply(nctx.am.get("wildcard")),srcAuto4), "fw_auto_conf"));
			autoctx.softConstrWildcard.add(new Tuple<BoolExpr, String>(ctx.mkEq((IntExpr)nctx.ip_functions.get("ipAddr_1").apply(nctx.am.get("wildcard")),dstAuto1), "fw_auto_conf"));
			autoctx.softConstrWildcard.add(new Tuple<BoolExpr, String>(ctx.mkEq((IntExpr)nctx.ip_functions.get("ipAddr_2").apply(nctx.am.get("wildcard")),dstAuto2), "fw_auto_conf"));
			autoctx.softConstrWildcard.add(new Tuple<BoolExpr, String>(ctx.mkEq((IntExpr)nctx.ip_functions.get("ipAddr_3").apply(nctx.am.get("wildcard")),dstAuto3), "fw_auto_conf"));
			autoctx.softConstrWildcard.add(new Tuple<BoolExpr, String>(ctx.mkEq((IntExpr)nctx.ip_functions.get("ipAddr_4").apply(nctx.am.get("wildcard")),dstAuto4), "fw_auto_conf"));
			autoctx.softConstrAutoConf.add(new Tuple<BoolExpr, String>(ctx.mkEq((IntExpr)nctx.ip_functions.get("ipAddr_1").apply(nctx.am.get("null")),srcAuto1), "fw_auto_conf"));
			autoctx.softConstrAutoConf.add(new Tuple<BoolExpr, String>(ctx.mkEq((IntExpr)nctx.ip_functions.get("ipAddr_2").apply(nctx.am.get("null")),srcAuto2), "fw_auto_conf"));
			autoctx.softConstrAutoConf.add(new Tuple<BoolExpr, String>(ctx.mkEq((IntExpr)nctx.ip_functions.get("ipAddr_3").apply(nctx.am.get("null")),srcAuto3), "fw_auto_conf"));
			autoctx.softConstrAutoConf.add(new Tuple<BoolExpr, String>(ctx.mkEq((IntExpr)nctx.ip_functions.get("ipAddr_4").apply(nctx.am.get("null")),srcAuto4), "fw_auto_conf"));
			autoctx.softConstrAutoConf.add(new Tuple<BoolExpr, String>(ctx.mkEq((IntExpr)nctx.ip_functions.get("ipAddr_1").apply(nctx.am.get("null")),dstAuto1), "fw_auto_conf"));
			autoctx.softConstrAutoConf.add(new Tuple<BoolExpr, String>(ctx.mkEq((IntExpr)nctx.ip_functions.get("ipAddr_2").apply(nctx.am.get("null")),dstAuto2), "fw_auto_conf"));
			autoctx.softConstrAutoConf.add(new Tuple<BoolExpr, String>(ctx.mkEq((IntExpr)nctx.ip_functions.get("ipAddr_3").apply(nctx.am.get("null")),dstAuto3), "fw_auto_conf"));
			autoctx.softConstrAutoConf.add(new Tuple<BoolExpr, String>(ctx.mkEq((IntExpr)nctx.ip_functions.get("ipAddr_4").apply(nctx.am.get("null")),dstAuto4), "fw_auto_conf"));
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

  	}
    
    // for an autoconfiguration firewall
 	private void firewallSendRulesAutoConf(Integer nRules) {
 		Expr p_0 = ctx.mkConst(fw + "_firewall_send_p_0", nctx.packet);
 		Expr n_0 = ctx.mkConst(fw + "_firewall_send_n_0", nctx.node);
 		Expr n_1 = ctx.mkConst(fw + "_firewall_send_n_1", nctx.node);
        acl_func = ctx.mkFuncDecl(fw + "_acl_func", new Sort[] { nctx.packet}, ctx.mkBoolSort());
        //acl_func_white = ctx.mkFuncDecl(fw + "_acl_func", new Sort[] { nctx.address, nctx.address , ctx.mkIntSort(), ctx.mkIntSort() }, ctx.mkBoolSort());
        List<BoolExpr> rules = new ArrayList<>();
 		for(int i = 0; i < nRules; i++){
 			Expr src = ctx.mkConst(fw + "_auto_src_"+i, nctx.address);
 			Expr dst = ctx.mkConst(fw + "_auto_dst_"+i, nctx.address);
 			Expr proto = ctx.mkConst(fw + "_auto_proto_"+i, ctx.mkIntSort());
 			Expr start_srcp = ctx.mkConst(fw + "_auto_start_srcp_"+i, ctx.mkIntSort());
 			Expr start_dstp = ctx.mkConst(fw + "_auto_start_dstp_"+i, ctx.mkIntSort());
 			Expr end_srcp = ctx.mkConst(fw + "_auto_end_srcp_"+i, ctx.mkIntSort());
 			Expr end_dstp = ctx.mkConst(fw + "_auto_end_dstp_"+i, ctx.mkIntSort());
 			IntExpr srcAuto1 = ctx.mkIntConst(fw + "_auto_src_ip_1_"+i);
 			IntExpr srcAuto2 = ctx.mkIntConst(fw + "_auto_src_ip_2_"+i);
 			IntExpr srcAuto3 = ctx.mkIntConst(fw + "_auto_src_ip_3_"+i);
 			IntExpr srcAuto4 = ctx.mkIntConst(fw + "_auto_src_ip_4_"+i);
 			IntExpr dstAuto1 = ctx.mkIntConst(fw + "_auto_dst_ip_1_"+i);
 			IntExpr dstAuto2 = ctx.mkIntConst(fw + "_auto_dst_ip_2_"+i);
 			IntExpr dstAuto3 = ctx.mkIntConst(fw + "_auto_dst_ip_3_"+i);
 			IntExpr dstAuto4 = ctx.mkIntConst(fw + "_auto_dst_ip_4_"+i);
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
 			constraints.add(ctx.mkAnd(
								ctx.mkGe((ArithExpr) start_srcp, ctx.mkInt(0)),
								ctx.mkLe((ArithExpr) start_srcp, ctx.mkInt(nctx.MAX_PORT)),
								ctx.mkGe((ArithExpr) end_srcp, ctx.mkInt(0)),
								ctx.mkLe((ArithExpr) end_srcp, ctx.mkInt(nctx.MAX_PORT)),
								ctx.mkLe((ArithExpr) start_srcp, (ArithExpr) end_srcp),								
								ctx.mkGe((ArithExpr) start_dstp, ctx.mkInt(0)),
								ctx.mkLe((ArithExpr) start_dstp, ctx.mkInt(nctx.MAX_PORT)),
								ctx.mkGe((ArithExpr) end_dstp, ctx.mkInt(0)),
								ctx.mkLe((ArithExpr) end_dstp, ctx.mkInt(nctx.MAX_PORT)),
								ctx.mkLe((ArithExpr) start_dstp, (ArithExpr) end_dstp)
						)
				);
 			//nctx.softConstrAutoConf.add(new Tuple<BoolExpr, String>(ctx.mkEq( src, this.nctx.am.get("null")),"fw_auto_conf"));
 			//nctx.softConstrAutoConf.add(new Tuple<BoolExpr, String>(ctx.mkEq( dst, this.nctx.am.get("null")),"fw_auto_conf"));
			nctx.softConstrWildcard.add(new Tuple<BoolExpr, String>(ctx.mkEq((IntExpr)nctx.ip_functions.get("ipAddr_1").apply(nctx.am.get("wildcard")),srcAuto1), "fw_auto_conf"));
			nctx.softConstrWildcard.add(new Tuple<BoolExpr, String>(ctx.mkEq((IntExpr)nctx.ip_functions.get("ipAddr_2").apply(nctx.am.get("wildcard")),srcAuto2), "fw_auto_conf"));
			nctx.softConstrWildcard.add(new Tuple<BoolExpr, String>(ctx.mkEq((IntExpr)nctx.ip_functions.get("ipAddr_3").apply(nctx.am.get("wildcard")),srcAuto3), "fw_auto_conf"));
			nctx.softConstrWildcard.add(new Tuple<BoolExpr, String>(ctx.mkEq((IntExpr)nctx.ip_functions.get("ipAddr_4").apply(nctx.am.get("wildcard")),srcAuto4), "fw_auto_conf"));
			nctx.softConstrWildcard.add(new Tuple<BoolExpr, String>(ctx.mkEq((IntExpr)nctx.ip_functions.get("ipAddr_1").apply(nctx.am.get("wildcard")),dstAuto1), "fw_auto_conf"));
			nctx.softConstrWildcard.add(new Tuple<BoolExpr, String>(ctx.mkEq((IntExpr)nctx.ip_functions.get("ipAddr_2").apply(nctx.am.get("wildcard")),dstAuto2), "fw_auto_conf"));
			nctx.softConstrWildcard.add(new Tuple<BoolExpr, String>(ctx.mkEq((IntExpr)nctx.ip_functions.get("ipAddr_3").apply(nctx.am.get("wildcard")),dstAuto3), "fw_auto_conf"));
			nctx.softConstrWildcard.add(new Tuple<BoolExpr, String>(ctx.mkEq((IntExpr)nctx.ip_functions.get("ipAddr_4").apply(nctx.am.get("wildcard")),dstAuto4), "fw_auto_conf"));
			nctx.softConstrAutoConf.add(new Tuple<BoolExpr, String>(ctx.mkEq((IntExpr)nctx.ip_functions.get("ipAddr_1").apply(nctx.am.get("null")),srcAuto1), "fw_auto_conf"));
			nctx.softConstrAutoConf.add(new Tuple<BoolExpr, String>(ctx.mkEq((IntExpr)nctx.ip_functions.get("ipAddr_2").apply(nctx.am.get("null")),srcAuto2), "fw_auto_conf"));
			nctx.softConstrAutoConf.add(new Tuple<BoolExpr, String>(ctx.mkEq((IntExpr)nctx.ip_functions.get("ipAddr_3").apply(nctx.am.get("null")),srcAuto3), "fw_auto_conf"));
			nctx.softConstrAutoConf.add(new Tuple<BoolExpr, String>(ctx.mkEq((IntExpr)nctx.ip_functions.get("ipAddr_4").apply(nctx.am.get("null")),srcAuto4), "fw_auto_conf"));
			nctx.softConstrAutoConf.add(new Tuple<BoolExpr, String>(ctx.mkEq((IntExpr)nctx.ip_functions.get("ipAddr_1").apply(nctx.am.get("null")),dstAuto1), "fw_auto_conf"));
			nctx.softConstrAutoConf.add(new Tuple<BoolExpr, String>(ctx.mkEq((IntExpr)nctx.ip_functions.get("ipAddr_2").apply(nctx.am.get("null")),dstAuto2), "fw_auto_conf"));
			nctx.softConstrAutoConf.add(new Tuple<BoolExpr, String>(ctx.mkEq((IntExpr)nctx.ip_functions.get("ipAddr_3").apply(nctx.am.get("null")),dstAuto3), "fw_auto_conf"));
			nctx.softConstrAutoConf.add(new Tuple<BoolExpr, String>(ctx.mkEq((IntExpr)nctx.ip_functions.get("ipAddr_4").apply(nctx.am.get("null")),dstAuto4), "fw_auto_conf"));
			nctx.softConstrAutoConf.add(new Tuple<BoolExpr, String>(ctx.mkEq( proto, ctx.mkInt(0)),"fw_auto_conf"));
			nctx.softConstrPorts.add(new Tuple<BoolExpr, String>(ctx.mkEq( start_srcp, ctx.mkInt(0)),"fw_auto_port"));
			nctx.softConstrPorts.add(new Tuple<BoolExpr, String>(ctx.mkEq( start_dstp, ctx.mkInt(0)),"fw_auto_port"));
			nctx.softConstrPorts.add(new Tuple<BoolExpr, String>(ctx.mkEq( end_srcp, ctx.mkInt(nctx.MAX_PORT)),"fw_auto_port"));
			nctx.softConstrPorts.add(new Tuple<BoolExpr, String>(ctx.mkEq( end_dstp, ctx.mkInt(nctx.MAX_PORT)),"fw_auto_port"));
			nctx.softConstrAutoConf.add(new Tuple<BoolExpr, String>(ctx.mkEq( ctx.mkSub((ArithExpr)start_srcp, (ArithExpr)end_srcp) , ctx.mkInt(0)),"fw_auto_port"));
			nctx.softConstrAutoConf.add(new Tuple<BoolExpr, String>(ctx.mkEq( ctx.mkSub((ArithExpr)start_dstp, (ArithExpr)end_dstp) , ctx.mkInt(0)),"fw_auto_port"));
 			rules.add(ctx.mkAnd(
 								//ctx.mkEq(nctx.pf.get("src").apply(p_0), src),
 								//ctx.mkEq(nctx.pf.get("dest").apply(p_0), dst)//,
			 					nctx.equalPacketIpToFwIpRule(nctx.pf.get("src").apply(p_0), src),
			 					nctx.equalPacketIpToFwIpRule(nctx.pf.get("dest").apply(p_0), dst),
			 					ctx.mkEq(nctx.pf.get("lv4proto").apply(p_0), proto),
			 					ctx.mkGe((IntExpr)nctx.pf.get("src_port").apply(p_0), (ArithExpr) start_srcp), ctx.mkLe((IntExpr)nctx.pf.get("src_port").apply(p_0), (ArithExpr) end_srcp),
			 					ctx.mkGe((IntExpr)nctx.pf.get("dest_port").apply(p_0), (ArithExpr) start_dstp),ctx.mkLe((IntExpr)nctx.pf.get("dest_port").apply(p_0), (ArithExpr) end_dstp)
			 					));
 		}
 		Expr defaultAction = ctx.mkConst(fw + "_auto_default_action", ctx.mkBoolSort());
 		Expr ruleAction = ctx.mkConst(fw + "_auto_action", ctx.mkBoolSort());
 		//System.out.println(rules);
 		BoolExpr[] tmp = new BoolExpr[rules.size()];
 		BoolExpr behaviour;
 		if(this.defaultAction.equals(ctx.mkTrue())){
 			behaviour = ctx.mkNot(
							  		ctx.mkOr(rules.toArray(tmp))		
							  	 );
 			constraints.add(ctx.mkEq(defaultAction, ctx.mkTrue()));
 			constraints.add(ctx.mkEq(ruleAction, ctx.mkFalse()));
 		}else{
 			behaviour = ctx.mkOr(rules.toArray(tmp));
 			constraints.add(ctx.mkEq(defaultAction, ctx.mkFalse()));
 			constraints.add(ctx.mkEq(ruleAction, ctx.mkTrue()));
 		}
 		//System.out.println("Behaviour: "+ behaviour);

		constraints.add(ctx.mkForall(new Expr[] { n_0, p_0 }, 
				ctx.mkImplies(
								(BoolExpr) nctx.send.apply(new Expr[] { fw, n_0, p_0 }),
								ctx.mkAnd(ctx.mkExists(new Expr[] { n_1 }, nctx.recv.apply(n_1, fw, p_0), 1, null, null, null, null),
										behaviour)), 1, null, null, null, null));;

 		BoolExpr[] tmp2 = new BoolExpr[rules.size()];
		constraints.add(ctx.mkForall(new Expr[] { n_0, p_0 },
										ctx.mkImplies(ctx.mkAnd((BoolExpr) nctx.recv.apply(n_0, fw, p_0),
												behaviour), ctx.mkAnd(ctx.mkExists(new Expr[] { n_1 }, (BoolExpr) nctx.send.apply(new Expr[] { fw, n_1, p_0 }), 1, null, null, null, null))), 
										1, null, null, null, null));
 	}
 	
 	private void aclConstraints(Optimize solver){
 		Expr p_0 = ctx.mkConst(fw+"_firewall_acl_p_0", nctx.packet);
 		Expr a_0 = ctx.mkConst(fw+"_rule_action_p_0", ctx.mkBoolSort());
 		Expr n_0 = ctx.mkConst(fw + "_firewall_send_n_0", nctx.node);
 		Expr n_1 = ctx.mkConst(fw + "_firewall_send_n_1", nctx.node);
 		System.out.println(fw+" default action: " + (defaultAction.equals(ctx.mkTrue())? "ALLOW":"DENY"));
    	if (acls.size() == 0){
    		//If the size of the ACL list is empty then by default acl_func must be false
    		 solver.Add(ctx.mkForall(new Expr[]{p_0},
						ctx.mkEq( 
								acl_func.apply(p_0), defaultAction),1,null,null,null,null));
    	}else{
    			BoolExpr[] rule_map = new BoolExpr[acls.size()];
    	        for(int y=0;y<acls.size();y++){
    	        	AclFirewallRule rule = acls.get(y);
    	        	System.out.println(fw + " rule: "+rule.getAction()+" from " + rule.getSource() + ":"+rule.getStart_src_port()+"-"+rule.getEnd_src_port() +
    	        											" to " + rule.getDestination()+":"+rule.getStart_dst_port()+"-"+rule.getEnd_dst_port());

    	        	rule_map[y] = rule.matchPacket(p_0);
					solver.Add(ctx.mkForall(new Expr[]{p_0},
											ctx.mkImplies(
														rule.matchPacket(p_0),
														ctx.mkAnd(ctx.mkEq(
																			acl_func.apply(p_0),
																			rule.getAction()
																			)
																	)
													  )
								, 1, null, null, null, null));
    	        }
    	        solver.Add( ctx.mkForall(new Expr[]{p_0},
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
    /*
    //Use blacklist and whitelist
 	private void aclConstraints(Optimize solver){
 		Expr p_0 = ctx.mkConst(fw+"_firewall_acl_p_1", nctx.packet);
    	if (acls.size() == 0){
    		//If the size of the ACL list is empty then by default acl_func must be false
    		 solver.Add(ctx.mkForall(new Expr[]{p_0},
						ctx.mkEq( 
								acl_func.apply(nctx.pf.get("src").apply(p_0), nctx.pf.get("dest").apply(p_0),
												nctx.pf.get("src_port").apply(p_0),nctx.pf.get("dest_port").apply(p_0)), ctx.mkFalse()),1,null,null,null,null));
    	}else{
    		 BoolExpr[] acl_map = new BoolExpr[acls.size()];
    	        for(int y=0;y<acls.size();y++){
    	        	Quattro<DatatypeExpr,DatatypeExpr,IntNum,IntNum> tp = acls.get(y);
    	        	System.out.println("Added rule: DROP from " + tp._1 + " to " + tp._2);
    	        	//acl_map[y] = ctx.mkOr(ctx.mkAnd(ctx.mkEq(a_0,tp._1),ctx.mkEq(a_1,tp._2)), ctx.mkAnd(ctx.mkEq(a_0,tp._2),ctx.mkEq(a_1,tp._1)));
    	        	acl_map[y] = ctx.mkOr(
    	        							ctx.mkAnd(nctx.equalPacketIpToFwIpRule(nctx.pf.get("src").apply(p_0),tp._1),nctx.equalPacketIpToFwIpRule(nctx.pf.get("dest").apply(p_0),tp._2),
    	        										//ctx.mkEq(nctx.pf.get("src").apply(p_0),tp._1),ctx.mkEq(nctx.pf.get("dest").apply(p_0),tp._2),
    	        										ctx.mkEq(nctx.pf.get("src_port").apply(p_0),tp._3),
    	        										ctx.mkEq(nctx.pf.get("dest_port").apply(p_0),tp._4)
    	        										)
    	        							);
    	        }
    	        //Constraint2		acl_func(a_0,a_1) == or(foreach ip1,ip2 in acl_map ((a_0 == ip1 && a_1 == ip2)||(a_0 == ip2 && a_1 == ip1)))
    	        solver.Add(ctx.mkForall(new Expr[]{p_0},
    	        						ctx.mkEq( 
    	        								acl_func.apply(nctx.pf.get("src").apply(p_0), nctx.pf.get("dest").apply(p_0), nctx.pf.get("src_port").apply(p_0), nctx.pf.get("dest_port").apply(p_0)),
    	        								ctx.mkOr(acl_map)),1,null,null,null,null));
    	    
    	}
    	if (whiteAcls.size() == 0){
       		//If the size of the ACL list is empty then by default acl_func must be false
       		 solver.Add(ctx.mkForall(new Expr[]{p_0},
    						ctx.mkEq( 
    								acl_func_white.apply(nctx.pf.get("src").apply(p_0), nctx.pf.get("dest").apply(p_0),
														nctx.pf.get("src_port").apply(p_0),nctx.pf.get("dest_port").apply(p_0)), 
    								ctx.mkTrue()),1,null,null,null,null));
       	}else{
       		BoolExpr[] acl_map_white = new BoolExpr[whiteAcls.size()];
               for(int y=0;y<whiteAcls.size();y++){
            	   Quattro<DatatypeExpr,DatatypeExpr,IntNum,IntNum> tp = whiteAcls.get(y);
               		acl_map_white[y] = ctx.mkOr(
											ctx.mkAnd(nctx.equalPacketIpToFwIpRule(nctx.pf.get("src").apply(p_0),tp._1),nctx.equalPacketIpToFwIpRule(nctx.pf.get("dest").apply(p_0),tp._2),
													//ctx.mkEq(nctx.pf.get("src").apply(p_0),tp._1),ctx.mkEq(nctx.pf.get("dest").apply(p_0),tp._2),
													ctx.mkEq(nctx.pf.get("src_port").apply(p_0),tp._3),
													ctx.mkEq(nctx.pf.get("dest_port").apply(p_0),tp._4)
													)
										);
               }
               //Constraint2		acl_func(a_0,a_1) == or(foreach ip1,ip2 in acl_map ((a_0 == ip1 && a_1 == ip2)||(a_0 == ip2 && a_1 == ip1)))
               solver.Add(ctx.mkForall(new Expr[]{p_0},
               						ctx.mkEq( 
               								acl_func_white.apply(nctx.pf.get("src").apply(p_0), nctx.pf.get("dest").apply(p_0), nctx.pf.get("src_port").apply(p_0), nctx.pf.get("dest_port").apply(p_0)),
               								ctx.mkOr(acl_map_white)),1,null,null,null,null));
       	}
            
       
    }*/
 	/*
 	 //Use only the blacklist
 	private void aclConstraints(Optimize solver){
   	 	Expr a_0 = ctx.mkConst(fw+"_firewall_acl_a_0", nctx.address);
        Expr a_1 = ctx.mkConst(fw+"_firewall_acl_a_1", nctx.address);
	   	if (acls.size() == 0){
	   		//If the size of the ACL list is empty then by default acl_func must be false
	   		 solver.Add(ctx.mkForall(new Expr[]{a_0, a_1},
							ctx.mkEq( 
									acl_func.apply(a_0, a_1), ctx.mkFalse()),1,null,null,null,null));
	   		 
	   	}else{
	   		 BoolExpr[] acl_map = new BoolExpr[acls.size()];
	   	        for(int y=0;y<acls.size();y++){
	   	        	Tuple<DatatypeExpr,DatatypeExpr> tp = acls.get(y);
	   	        	acl_map[y] = ctx.mkOr(ctx.mkAnd(ctx.mkEq(a_0,tp._1),ctx.mkEq(a_1,tp._2)), ctx.mkAnd(ctx.mkEq(a_0,tp._2),ctx.mkEq(a_1,tp._1)));
	   	        }
	   	        //Constraint2		acl_func(a_0,a_1) == or(foreach ip1,ip2 in acl_map ((a_0 == ip1 && a_1 == ip2)||(a_0 == ip2 && a_1 == ip1)))
	   	        solver.Add(ctx.mkForall(new Expr[]{a_0, a_1},
	   	        						ctx.mkEq( 
	   	        								acl_func.apply(a_0, a_1),
	   	        								ctx.mkOr(acl_map)),1,null,null,null,null));
	   	}
       //doing the same for the white list
       if (whiteAcls.size() == 0){
   		//If the size of the ACL list is empty then by default acl_func must be false
   		 solver.Add(ctx.mkForall(new Expr[]{a_0, a_1},
						ctx.mkEq( 
								acl_func_white.apply(a_0, a_1), ctx.mkTrue()),1,null,null,null,null));
	   	}else{
	   		BoolExpr[] acl_map_white = new BoolExpr[whiteAcls.size()];
	           for(int y=0;y<whiteAcls.size();y++){
	           	Tuple<DatatypeExpr,DatatypeExpr> tp = whiteAcls.get(y);
	           	acl_map_white[y] = ctx.mkOr(ctx.mkAnd(ctx.mkEq(a_0,tp._1),ctx.mkEq(a_1,tp._2)), ctx.mkAnd(ctx.mkEq(a_0,tp._2),ctx.mkEq(a_1,tp._1)));
	           }
	           //Constraint2		acl_func(a_0,a_1) == or(foreach ip1,ip2 in acl_map ((a_0 == ip1 && a_1 == ip2)||(a_0 == ip2 && a_1 == ip1)))
	           solver.Add(ctx.mkForall(new Expr[]{a_0, a_1},
	           						ctx.mkEq( 
	           								acl_func_white.apply(a_0, a_1),
	           								ctx.mkOr(acl_map_white)),1,null,null,null,null));
	   	}       
   }*/
}