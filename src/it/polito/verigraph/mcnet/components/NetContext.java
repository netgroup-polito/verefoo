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
    List<Core> policies;

    public HashMap<String,NetworkObject> nm; //list of nodes, callable by node name
    public HashMap<String,DatatypeExpr> am; // list of addresses, callable by address name
    public HashMap<String,FuncDecl> pf;
    Context ctx;
    public EnumSort node,address;
    public FuncDecl src_port,dest_port,nodeHasAddr,addrToNode,send,recv;
    public DatatypeSort packet;

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
        pf= new HashMap<String,FuncDecl>() ;

        mkTypes((String[])args[0],(String[])args[1]);

        constraints = new ArrayList<BoolExpr>();
        softConstraints = new ArrayList<>();
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
        solver.Add(constraints.toArray(constr));
        for (Core policy : policies){
            policy.addConstraints(solver);
        }
        for (Tuple<BoolExpr, String> t : softConstraints) {
			solver.AssertSoft(t._1, 100, t._2);
		}  
    }

    private void mkTypes (String[] nodes, String[] addresses){
        //Nodes in a network
        node = ctx.mkEnumSort("Node", nodes);

        for(int i=0;i<node.getConsts().length;i++){
            DatatypeExpr fd  = (DatatypeExpr)node.getConst(i);    
            DumbNode dn =new DumbNode(ctx,new Object[]{fd});

            nm.put(fd.toString(),dn);
        }

        //Addresses for this network
        String[] new_addr = new String[addresses.length+1];
        for(int k=0;k<addresses.length;k++)
            new_addr[k] = addresses[k];

        new_addr[new_addr.length-1] = "null";
        address = ctx.mkEnumSort("Address", new_addr);
        for(int i=0;i<address.getConsts().length;i++){
            DatatypeExpr fd  = (DatatypeExpr)address.getConst(i);


            am.put(fd.toString(),fd);
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