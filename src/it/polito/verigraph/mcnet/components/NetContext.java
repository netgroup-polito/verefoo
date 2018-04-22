/*******************************************************************************
 * Copyright (c) 2017 Politecnico di Torino and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package it.polito.verigraph.mcnet.components;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Constructor;
import com.microsoft.z3.Context;
import com.microsoft.z3.DatatypeExpr;
import com.microsoft.z3.DatatypeSort;
import com.microsoft.z3.EnumSort;
import com.microsoft.z3.Expr;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.IntSort;
import com.microsoft.z3.Optimize;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Sort;
import com.microsoft.z3.Optimize.Handle;

import it.polito.verigraph.mcnet.netobjs.DumbNode;
import it.polito.verifoo.components.Host;

import it.polito.verifoo.components.Node;
import it.polito.verigraph.mcnet.components.Checker.Prop;
import it.polito.verigraph.mcnet.components.Core;
import it.polito.verigraph.mcnet.components.NetworkObject;

/**
 * Basic fields and other things required for model checking.
 *
 */
public class NetContext extends Core{


    public List<BoolExpr> constraints;
    public List<Tuple<BoolExpr, String>> softConstraints;
    public List<Tuple<BoolExpr, String>> softConstrAutoConf;
    public List<Tuple<BoolExpr, String>> softConstrAutoPlace;
    public List<Tuple<BoolExpr, String>> softConstrWildcard;
    List<Core> policies;

    public HashMap<String,NetworkObject> nm; //list of nodes, callable by node name
    public HashMap<String,DatatypeExpr> am; // list of addresses, callable by address name
    public HashMap<String,DatatypeExpr> ipm; // list of ip addresses, callable by name
    public HashMap<String,FuncDecl> pf;
    Context ctx;
    public EnumSort node;
	public DatatypeSort address;
    public FuncDecl src_port,dest_port,nodeHasAddr,addrToNode,send,recv;
    public DatatypeSort packet;
    

    public DatatypeSort ipAddress;
    public HashMap<String,FuncDecl> ip_functions;
    /*   Constants definition
        - used in the packet proto field */
    public final int HTTP_REQUEST    = 1;
    public final int HTTP_RESPONSE   = 2;
    public final int POP3_REQUEST    = 3;
    public final int POP3_RESPONSE   = 4;

   	public int latencyAll;
	public  BoolExpr ture;
    /**
     * Context for all of the rest that follows. Every network needs one of these
     * @param ctx
     * @param args
     */
    public NetContext(Context ctx,Object[]... args ){
        super(ctx,args);

    }

    @Override
    protected void init(Context ctx, Object[]... args) {
        this.ctx = ctx;
        nm = new HashMap<String,NetworkObject>(); //list of nodes, callable by node name
        am = new HashMap<String,DatatypeExpr>(); // list of addresses, callable by address name
        ipm= new HashMap<>();  // list of ip addresses, callable by name
        pf= new HashMap<String,FuncDecl>() ;
        ip_functions = new HashMap<String,FuncDecl>();
        constraints = new ArrayList<BoolExpr>();
        mkTypes((String[])args[0],(String[])args[1]);

        softConstraints = new ArrayList<>();
        softConstrAutoConf = new ArrayList<>();
        softConstrAutoPlace = new ArrayList<>();
        softConstrWildcard = new ArrayList<>(); 
        policies = new ArrayList<Core>();
        
        //variable true that is always true
        ture = ctx.mkBoolConst("ture");
        baseCondition();
        handles = new HashMap<String,Handle>();
		latencyAll= 0;
        
    }
    
    public int addLatency(int latencyAll){
    	this.latencyAll=this.latencyAll+latencyAll;
    	return latencyAll;
    }
    
    public HashMap<String,Handle> handles;
  
    
  
    
  

	public IntExpr bool_to_int(BoolExpr value) {
		IntExpr integer = ctx.mkIntConst("integer_" + value);


		constraints.add((ctx.mkImplies(value, ctx.mkEq(integer, ctx.mkInt(1)))));
		constraints.add((ctx.mkImplies(ctx.mkNot(value), ctx.mkEq(integer, ctx.mkInt(0)))));

		return integer;
	}
    /**
     * A policy is a collection of shared algorithms or functions used by multiple components
     * (for instance compression or DPI policies etc).
     * @param policy
     */
    public void AddPolicy (NetworkObject policy){
        policies.add(policy);
    }

    @Override
    protected void addConstraints(Optimize solver) {
        BoolExpr[] constr = new BoolExpr[constraints.size()];
        /*System.out.println("======NET CONTEXT HARD CONSTRAINTS====== ");
		constraints.forEach(c -> {
			System.out.println(c);
		});*/
        solver.Add(constraints.toArray(constr));
        for (Core policy : policies){
            policy.addConstraints(solver);
        }
        //System.out.println("======NET CONTEXT SOFT CONSTRAINTS====== ");
        //System.out.println("AutoConfiguration Constraints");
        for (Tuple<BoolExpr, String> t : softConstrAutoConf) {
        	//System.out.println(t._1 + "\n with value " + 1000 + ". Node is " + t._2);
			solver.AssertSoft(t._1, 1000, t._2);
		}
        //System.out.println("AutoPlacement Constraints");
        for (Tuple<BoolExpr, String> t : softConstrAutoPlace) {
        	//System.out.println(t._1 + "\n with value " + 100 + ". Node is " + t._2);
			solver.AssertSoft(t._1, 100, t._2);
		}
        //System.out.println("Soft Constraints");
        for (Tuple<BoolExpr, String> t : softConstraints) {
        	//System.out.println(t._1 + "\n with value " + 10 + ". Node is " + t._2);
			solver.AssertSoft(t._1, 10, t._2);
		}
      //System.out.println("Wildcards Constraints");
        for (Tuple<BoolExpr, String> t : softConstrWildcard) {
        	//System.out.println(t._1 + "\n with value " + 10 + ". Node is " + t._2);
			solver.AssertSoft(t._1, -10, t._2);
		}
        
    }

    public int[] getIpFromString(String ipString) {
    	int[] res = new int[4];
    	String[] decimalNotation = ipString.split("\\.");
    	if(decimalNotation.length > 4) throw new NumberFormatException();
    	int i = 0;
    	for(String s : decimalNotation){
    		res[i] = Integer.parseInt(s);
    		i++;
    	}
    	return res;
    }
    private void mkTypes (String[] nodes, String[] addresses){
        //Nodes in a network
        node = ctx.mkEnumSort("Node", nodes);

        for(int i=0;i<node.getConsts().length;i++){
            DatatypeExpr fd  = (DatatypeExpr)node.getConst(i);    
            DumbNode dn =new DumbNode(ctx,new Object[]{fd});

            nm.put(fd.toString().replace("|", ""),dn);
        }

        //Addresses for this network         
        String[] new_addr = new String[addresses.length+2];
        for(int k=0;k<addresses.length;k++)
            new_addr[k] = addresses[k];

        new_addr[new_addr.length-2] = "null";
        new_addr[new_addr.length-1] = "wildcard";
        //address = ctx.mkEnumSort("Address", new_addr);  
        String[] ipfieldNames = new String[]{"ipAddr_1","ipAddr_2","ipAddr_3","ipAddr_4"};
        Sort[] sort = new Sort[]{ctx.mkIntSort(),ctx.mkIntSort(),ctx.mkIntSort(),ctx.mkIntSort()};
        Constructor ipCon = ctx.mkConstructor("ip_constructor", "is_ip", ipfieldNames, sort, null);
        address = ctx.mkDatatypeSort("Address", new Constructor[] {ipCon});
        for(int i=0;i<ipfieldNames.length;i++){
        	ip_functions.put(ipfieldNames[i], address.getAccessors()[0][i]); // ip_functions to get ip's function declarations by name
        }
        
        for(int i=0;i<new_addr.length;i++){
            //DatatypeExpr fd  = (DatatypeExpr)address.getConst(i);
        	DatatypeExpr fd = (DatatypeExpr) ctx.mkConst(new_addr[i], address);
            //System.out.println(fd.toString().replace("|", ""));
            am.put(fd.toString().replace("|", ""),fd);
            try{
            	constraints.add(equalIpToIntArray(fd, getIpFromString(new_addr[i])));
            }catch(NumberFormatException e){
            	if(new_addr[i].equals("null")){
            		constraints.add(equalIpToIntArray(fd, getIpFromString("0.0.0.0")));
            	}
            	else if(new_addr[i].equals("wildcard")){
            		constraints.add(equalIpToIntArray(fd, getIpFromString("-1.-1.-1.-1")));
            	} else{
            		//251 is a prime number, to reduce collisions
            		constraints.add(equalIpToIntArray(fd, getIpFromString(
												            				(new_addr[i].hashCode()%251)+"."+
												            				(new_addr[i].hashCode()%251)+"."+ 
												            				(new_addr[i].hashCode()%251)+"."+ 
												            				(new_addr[i].hashCode()%251)           						
												            				)));
            	}
            	//System.out.println(new_addr[i] + " is not a valid ip address, using it as a label");
            }
        }
        
        // Type for packets, contains (some of these are currently represented as relations):
        // -   src: Source address
        // -   dest: Destination address
        // -   origin: Node where the data originated. (Node)
        // -   body: Packet contents. (Integer)
        // -   seq: Sequence number for packets. (Integer)
        // -   options: A representation for IP options. (Integer)

        String[] fieldNames = new String[]{
                "src","dest","inner_src","inner_dest","origin","orig_body","body","seq","proto","emailFrom","url","options","encrypted"};
        Sort[] srt = new Sort[]{
        		address,address,address,address,node,ctx.mkIntSort(),ctx.mkIntSort(),ctx.mkIntSort(),
                ctx.mkIntSort(),ctx.mkIntSort(),ctx.mkIntSort(),ctx.mkIntSort(),ctx.mkBoolSort()};
        Constructor packetcon = ctx.mkConstructor("packet", "is_packet", fieldNames, srt, null);
        packet = ctx.mkDatatypeSort("packet",  new Constructor[] {packetcon});


        for(int i=0;i<fieldNames.length;i++){
            pf.put(fieldNames[i], packet.getAccessors()[0][i]); // pf to get packet's function declarations by name
        }


        src_port = ctx.mkFuncDecl("sport", packet, ctx.mkIntSort());
        dest_port = ctx.mkFuncDecl("dport", packet, ctx.mkIntSort());

   
        nodeHasAddr = ctx.mkFuncDecl("nodeHasAddr", new Sort[]{node, address},ctx.mkBoolSort());


        // addrToNode: address -> node
        addrToNode = ctx.mkFuncDecl("addrToNode", address, node);


        // send: node -> node -> packet-> int-> bool
        send = ctx.mkFuncDecl("send", new Sort[]{ node, node, packet},ctx.mkBoolSort());


        // recv: node -> node -> packet-> int-> bool
        recv = ctx.mkFuncDecl("recv", new Sort[]{ node, node, packet},ctx.mkBoolSort());

    }

    /**
     * Set up base conditions for the network
     */
    private void baseCondition(){
        // Basic constraints for the overall model
        Expr n_0 = ctx.mkConst("ctx_base_n_0", node);
        Expr n_1 = ctx.mkConst("ctx_base_n_1", node);
        Expr n_2 = ctx.mkConst("ctx_base_n_2", node);
        Expr p_0 = ctx.mkConst("ctx_base_p_0", packet);
        Expr p_1 = ctx.mkConst("ctx_base_p_1", packet);
        //IntExpr t_0 = ctx.mkIntConst("ctx_base_t_0");
        //IntExpr t_1 = ctx.mkIntConst("ctx_base_t_1");

        // Constraint1 send(n_0, n_1, p_0, t_0) -> n_0 != n_1
        constraints.add(
                ctx.mkForall(new Expr[]{n_0, n_1, p_0},
                        ctx.mkImplies((BoolExpr)send.apply(n_0, n_1, p_0),ctx.mkNot(ctx.mkEq( n_0, n_1))),1,null,null,null,null));

        // Constraint2 recv(n_0, n_1, p_0, t_0) -> n_0 != n_1
        constraints.add(
                ctx.mkForall(new Expr[]{n_0, n_1, p_0},
                        ctx.mkImplies((BoolExpr)recv.apply(n_0, n_1, p_0),ctx.mkNot(ctx.mkEq( n_0, n_1))),1,null,null,null,null));

        // Constraint3 send(n_0, n_1, p_0, t_0) -> p_0.src != p_0.dest
        constraints.add(
                ctx.mkForall(new Expr[]{n_0, n_1, p_0},
                        ctx.mkImplies((BoolExpr)send.apply(n_0, n_1, p_0),
                                ctx.mkNot(ctx.mkEq(  pf.get("src").apply(p_0), pf.get("dest").apply(p_0)))),1,null,null,null,null));

        // Constraint4 recv(n_0, n_1, p_0, t_0) -> p_0.src != p_0.dest
        constraints.add(
                ctx.mkForall(new Expr[]{n_0, n_1, p_0 },
                        ctx.mkImplies((BoolExpr)recv.apply(n_0, n_1, p_0 ),
                                ctx.mkNot(ctx.mkEq(pf.get("src").apply(p_0),pf.get("dest").apply(p_0)))),1,null,null,null,null));

        // Constraint5 recv(n_0, n_1, p ) -> send(n_0, n_1, p, t_1) && t_1 < t_0
        constraints.add(
                ctx.mkForall(new Expr[]{n_0, n_1, p_0 },
                        ctx.mkImplies((BoolExpr)recv.apply(n_0, n_1, p_0 ),
                                        ctx.mkAnd((BoolExpr)send.apply(n_0, n_1, p_0)
                                                )),1,null,null,null,null));
        constraints.add(
                ctx.mkForall(new Expr[]{n_0, n_1, p_0 },
                        ctx.mkImplies(ctx.mkAnd((BoolExpr)send.apply(n_0, n_1, p_0)
                                                ),(BoolExpr)recv.apply(n_0, n_1, p_0 )),1,null,null,null,null));

        // Constraint6 send(n_0, n_1, p, t_0) -> p.src_port > 0 && p.dest_port < MAX_PORT
        constraints.add(
                ctx.mkForall(new Expr[]{n_0, n_1, p_0 },
                        ctx.mkImplies((BoolExpr)send.apply(n_0, n_1, p_0 ),
                                ctx.mkAnd( ctx.mkGe((IntExpr)src_port.apply(p_0),(IntExpr)ctx.mkInt(0)),
                                        ctx.mkLt((IntExpr)src_port.apply(p_0),(IntExpr) ctx.mkInt(MAX_PORT)))),1,null,null,null,null));

        // Constraint7 recv(n_0, n_1, p, t_0) -> p.src_port > 0 && p.dest_port < MAX_PORT
        constraints.add(
                ctx.mkForall(new Expr[]{n_0, n_1, p_0},
                        ctx.mkImplies((BoolExpr)recv.apply(n_0, n_1, p_0),
                                ctx.mkAnd( ctx.mkGe((IntExpr)dest_port.apply(p_0),(IntExpr)ctx.mkInt(0)),
                                        ctx.mkLt((IntExpr)dest_port.apply(p_0),(IntExpr) ctx.mkInt(MAX_PORT)))),1,null,null,null,null));


        // Extra constriants for supporting the VPN gateway
        constraints.add(
                ctx.mkForall(new Expr[]{n_0, n_1, p_0},
                        ctx.mkImplies(
                                ctx.mkAnd((BoolExpr)send.apply(n_0, n_1, p_0),
                                        ctx.mkNot(ctx.mkEq(this.pf.get("inner_src").apply(p_0), this.am.get("null")))),
                                ctx.mkNot(ctx.mkEq(this.pf.get("inner_src").apply(p_0), this.pf.get("inner_dest").apply(p_0)))),1,null,null,null,null));

        constraints.add(
                ctx.mkForall(new Expr[]{n_0, n_1, p_0},
                        ctx.mkImplies(
                                ctx.mkAnd((BoolExpr)send.apply(n_0, n_1, p_0),
                                        ctx.mkEq(this.pf.get("inner_src").apply(p_0), this.am.get("null"))),
                                ctx.mkEq(this.pf.get("inner_src").apply(p_0), this.pf.get("inner_dest").apply(p_0))),1,null,null,null,null));

        constraints.add(
                ctx.mkForall(new Expr[]{n_0, n_1, p_0},
                        ctx.mkImplies(
                                ctx.mkAnd((BoolExpr)send.apply(n_0, n_1, p_0),
                                        ctx.mkEq(this.pf.get("inner_dest").apply(p_0), this.am.get("null"))),
                                ctx.mkEq(this.pf.get("inner_src").apply(p_0), this.pf.get("inner_dest").apply(p_0))),1,null,null,null,null));

        constraints.add(
                ctx.mkForall(new Expr[]{n_0, n_1, p_0},
                        ctx.mkImplies(
                                ctx.mkAnd((BoolExpr)recv.apply(n_0, n_1, p_0),
                                        ctx.mkNot(ctx.mkEq(this.pf.get("inner_src").apply(p_0), this.am.get("null")))),
                                ctx.mkNot(ctx.mkEq(this.pf.get("inner_src").apply(p_0), this.pf.get("inner_dest").apply(p_0)))),1,null,null,null,null));

        constraints.add(
                ctx.mkForall(new Expr[]{n_0, n_1, p_0},
                        ctx.mkImplies(
                                ctx.mkAnd((BoolExpr)recv.apply(n_0, n_1, p_0),
                                        ctx.mkEq(this.pf.get("inner_src").apply(p_0), this.am.get("null"))),
                                ctx.mkEq(this.pf.get("inner_src").apply(p_0), this.pf.get("inner_dest").apply(p_0))),1,null,null,null,null));

        constraints.add(
                ctx.mkForall(new Expr[]{n_0, n_1, p_0},
                        ctx.mkImplies(
                                ctx.mkAnd((BoolExpr)recv.apply(n_0, n_1, p_0),
                                        ctx.mkEq(this.pf.get("inner_dest").apply(p_0), this.am.get("null"))),
                                ctx.mkEq(this.pf.get("inner_src").apply(p_0), this.pf.get("inner_dest").apply(p_0))),1,null,null,null,null));

        constraints.add(
                ctx.mkForall(new Expr[]{n_0, n_1, p_0, n_2, p_1},
                        ctx.mkImplies(
                                ctx.mkAnd(
                                        
                                        (BoolExpr)send.apply(n_0, n_1, p_0),
                                        (BoolExpr)this.pf.get("encrypted").apply(p_1),
                                        (BoolExpr)recv.apply(n_2, n_0, p_1),
                                        (BoolExpr)this.pf.get("encrypted").apply(p_0)),
                                ctx.mkAnd(
                                        ctx.mkEq(this.pf.get("inner_src").apply(p_1), this.pf.get("inner_src").apply(p_0)),
                                        ctx.mkEq(this.pf.get("inner_dest").apply(p_1), this.pf.get("inner_dest").apply(p_0)),
                                        ctx.mkEq(this.pf.get("origin").apply(p_1), this.pf.get("origin").apply(p_0)),
                                        ctx.mkEq(this.pf.get("orig_body").apply(p_1), this.pf.get("orig_body").apply(p_0)),
                                        ctx.mkEq(this.pf.get("body").apply(p_1), this.pf.get("body").apply(p_0)),
                                        ctx.mkEq(this.pf.get("seq").apply(p_1), this.pf.get("seq").apply(p_0)),
                                        ctx.mkEq(this.pf.get("proto").apply(p_1), this.pf.get("proto").apply(p_0)),
                                        ctx.mkEq(this.pf.get("emailFrom").apply(p_1), this.pf.get("emailFrom").apply(p_0)),
                                        ctx.mkEq(this.pf.get("url").apply(p_1), this.pf.get("url").apply(p_0)),
                                        ctx.mkEq(this.pf.get("options").apply(p_1), this.pf.get("options").apply(p_0)))),1,null,null,null,null)
                );
		constraints.add(ctx.mkForall(new Expr[]{n_0, n_1, p_0},
                                	ctx.mkImplies((BoolExpr)recv.apply(n_0, n_1, p_0),
                                				ctx.mkAnd( 
						                        		ctx.mkGe((IntExpr)ip_functions.get("ipAddr_1").apply(pf.get("src").apply(p_0)),(IntExpr)ctx.mkInt(0)),
						                                ctx.mkLe((IntExpr)ip_functions.get("ipAddr_1").apply(pf.get("src").apply(p_0)),(IntExpr) ctx.mkInt(255)),
						                        		ctx.mkGe((IntExpr)ip_functions.get("ipAddr_2").apply(pf.get("src").apply(p_0)),(IntExpr)ctx.mkInt(0)),
						                                ctx.mkLe((IntExpr)ip_functions.get("ipAddr_2").apply(pf.get("src").apply(p_0)),(IntExpr) ctx.mkInt(255)),
						                        		ctx.mkGe((IntExpr)ip_functions.get("ipAddr_3").apply(pf.get("src").apply(p_0)),(IntExpr)ctx.mkInt(0)),
						                                ctx.mkLe((IntExpr)ip_functions.get("ipAddr_3").apply(pf.get("src").apply(p_0)),(IntExpr) ctx.mkInt(255)),
						                        		ctx.mkGe((IntExpr)ip_functions.get("ipAddr_4").apply(pf.get("src").apply(p_0)),(IntExpr)ctx.mkInt(0)),
						                                ctx.mkLe((IntExpr)ip_functions.get("ipAddr_4").apply(pf.get("src").apply(p_0)),(IntExpr) ctx.mkInt(255)),
						                                ctx.mkGe((IntExpr)ip_functions.get("ipAddr_1").apply(pf.get("dest").apply(p_0)),(IntExpr)ctx.mkInt(0)),
						                                ctx.mkLe((IntExpr)ip_functions.get("ipAddr_1").apply(pf.get("dest").apply(p_0)),(IntExpr) ctx.mkInt(255)),
						                        		ctx.mkGe((IntExpr)ip_functions.get("ipAddr_2").apply(pf.get("dest").apply(p_0)),(IntExpr)ctx.mkInt(0)),
						                                ctx.mkLe((IntExpr)ip_functions.get("ipAddr_2").apply(pf.get("dest").apply(p_0)),(IntExpr) ctx.mkInt(255)),
						                        		ctx.mkGe((IntExpr)ip_functions.get("ipAddr_3").apply(pf.get("dest").apply(p_0)),(IntExpr)ctx.mkInt(0)),
						                                ctx.mkLe((IntExpr)ip_functions.get("ipAddr_3").apply(pf.get("dest").apply(p_0)),(IntExpr) ctx.mkInt(255)),
						                        		ctx.mkGe((IntExpr)ip_functions.get("ipAddr_4").apply(pf.get("dest").apply(p_0)),(IntExpr)ctx.mkInt(0)),
						                                ctx.mkLe((IntExpr)ip_functions.get("ipAddr_4").apply(pf.get("dest").apply(p_0)),(IntExpr) ctx.mkInt(255))
							                              )
                                				 )
                
        ,1,null,null,null,null));


    }
    
    /**
     * Two ip addresses are equals
     * @param p1
     * @param p2
     * @return
     */
    public BoolExpr equalIp(Expr ip1, Expr ip2){
        return ctx.mkAnd(new BoolExpr[]{
                ctx.mkOr(
                		ctx.mkEq(ip_functions.get("ipAddr_1").apply(ip1), ip_functions.get("ipAddr_1").apply(ip2)),
                		ctx.mkEq(ip_functions.get("ipAddr_1").apply(ip1), ip_functions.get("ipAddr_1").apply(am.get("wildcard"))),
                		ctx.mkEq(ip_functions.get("ipAddr_1").apply(ip2), ip_functions.get("ipAddr_1").apply(am.get("wildcard")))
                		),
                ctx.mkOr(
                		ctx.mkEq(ip_functions.get("ipAddr_2").apply(ip1), ip_functions.get("ipAddr_2").apply(ip2)),
                		ctx.mkEq(ip_functions.get("ipAddr_2").apply(ip1), ip_functions.get("ipAddr_2").apply(am.get("wildcard"))),
                		ctx.mkEq(ip_functions.get("ipAddr_2").apply(ip2), ip_functions.get("ipAddr_2").apply(am.get("wildcard")))
                		),
                ctx.mkOr(
                		ctx.mkEq(ip_functions.get("ipAddr_3").apply(ip1), ip_functions.get("ipAddr_3").apply(ip2)),
                		ctx.mkEq(ip_functions.get("ipAddr_3").apply(ip1), ip_functions.get("ipAddr_3").apply(am.get("wildcard"))),
                		ctx.mkEq(ip_functions.get("ipAddr_3").apply(ip2), ip_functions.get("ipAddr_3").apply(am.get("wildcard")))
                		),
                ctx.mkOr(
                		ctx.mkEq(ip_functions.get("ipAddr_4").apply(ip1), ip_functions.get("ipAddr_4").apply(ip2)),
                		ctx.mkEq(ip_functions.get("ipAddr_4").apply(ip1), ip_functions.get("ipAddr_4").apply(am.get("wildcard"))),
                		ctx.mkEq(ip_functions.get("ipAddr_4").apply(ip2), ip_functions.get("ipAddr_4").apply(am.get("wildcard")))
                		)});
    }

    public BoolExpr equalIpToIntArray(Expr ip_expr, int[] array){
        return ctx.mkAnd(new BoolExpr[]{
                ctx.mkEq(ip_functions.get("ipAddr_1").apply(ip_expr), ctx.mkInt(array[0])),
                ctx.mkEq(ip_functions.get("ipAddr_2").apply(ip_expr), ctx.mkInt(array[1])),
                ctx.mkEq(ip_functions.get("ipAddr_3").apply(ip_expr), ctx.mkInt(array[2])),
                ctx.mkEq(ip_functions.get("ipAddr_4").apply(ip_expr), ctx.mkInt(array[3]))});
    }
    /**
     * Two packets have equal headers
     * @param p1
     * @param p2
     * @return
     */
    public BoolExpr PacketsHeadersEqual(Expr p1, Expr p2){
        return ctx.mkAnd(new BoolExpr[]{
                ctx.mkEq(pf.get("src").apply(p1), pf.get("src").apply(p2)),
                ctx.mkEq(pf.get("dest").apply(p1), pf.get("dest").apply(p2)),
                ctx.mkEq(pf.get("origin").apply(p1), pf.get("origin").apply(p2)),
                ctx.mkEq(pf.get("seq").apply(p1), pf.get("seq").apply(p2)),
                ctx.mkEq(src_port.apply(p1),src_port.apply(p2)),
                ctx.mkEq(dest_port.apply(p1), dest_port.apply(p2)),
                ctx.mkEq(pf.get("options").apply(p1),pf.get("options").apply(p2))});
    }

    /**
     * Two packets have equal bodies
     * @param p1
     * @param p2
     * @return
     */
    public BoolExpr PacketContentEqual(Expr p1, Expr p2){
        return ctx.mkEq(pf.get("body").apply(p1), pf.get("body").apply(p2));
    }


    /* seems to be useless
     *
public Function failurePredicate (NetContext context)
{
    return (NetworkObject node) -> ctx.mkNot(context.failed (node.z3Node));

}*/

    public BoolExpr destAddrPredicate (Expr p, DatatypeExpr address){
        return  ctx.mkEq(pf.get("dest").apply(p),address);
    }

    public  BoolExpr srcAddrPredicate (Expr p, DatatypeExpr address){
        return  ctx.mkEq(pf.get("src").apply(p),address);
    }

}