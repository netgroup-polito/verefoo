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
import java.util.List;
import java.util.Map;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.DatatypeExpr;
import com.microsoft.z3.Expr;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Z3Exception;
import it.polito.verigraph.mcnet.components.Core;
import it.polito.verigraph.mcnet.components.NetContext;
import it.polito.verigraph.mcnet.components.NetworkObject;
import it.polito.verigraph.mcnet.components.Tuple;

/**Model for a network, encompasses routing and wiring
 *
 *
 */
public class Network extends Core{

    Context ctx;
    NetContext nctx;
    List<BoolExpr> constraints;
    public List<NetworkObject> elements;



    public Network(Context ctx,Object[]... args) {
        super(ctx, args);
    }

    @Override
    protected void init(Context ctx, Object[]... args) {
        this.ctx = ctx;
        this.nctx = (NetContext) args[0][0];
        constraints = new ArrayList<BoolExpr>();
        elements = new ArrayList<NetworkObject>();

    }

    /**Composes the network linking the configured network objects
     *
     * @param elements
     */
    public void attach (NetworkObject ... elements){
        for(NetworkObject el : elements)
            this.elements.add(el);
    }

    @Override
    protected void addConstraints(Solver solver) {
        try {
            BoolExpr[] constr = new BoolExpr[constraints.size()];
            solver.add(constraints.toArray(constr));
        } catch (Z3Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Specify host to address mapping.
     * Handles the case in which we have more than one address for a node
     * @param addrmap
     */
    public void setAddressMappings(ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>> addrmap){
        // Set address mapping for nodes.
        for (Tuple<NetworkObject,ArrayList<DatatypeExpr>> entry : addrmap) {
            NetworkObject node=entry._1;
            List<DatatypeExpr> addr=entry._2;
            Expr a_0 = ctx.mkConst(node+"_address_mapping_a_0",nctx.address);
            ArrayList<BoolExpr> or_clause = new ArrayList<BoolExpr>();

            // Constraint 1 addrToNode(foreach ad in addr) = node
            for (DatatypeExpr ad : addr){
                constraints.add(ctx.mkEq(nctx.addrToNode.apply(ad), node.getZ3Node()));
                or_clause.add(ctx.mkEq(a_0,ad));

                // System.out.println("Constraints mapping: " + (ctx.mkEq(nctx.addrToNode.apply(ad), node.getZ3Node())));

            }
            BoolExpr[] orClause = new BoolExpr[or_clause.size()];

            // Constraint 2nodeHasAddr(node, a_0) == Or(foreach ad in addr (a_0 == ad))
            // Note we need the iff here to make sure that we set nodeHasAddr to false
            // for other addresses.
            constraints.add(ctx.mkForall(new Expr[]{a_0},
                    ctx.mkEq(ctx.mkOr(or_clause.toArray(orClause)), nctx.nodeHasAddr.apply(node.getZ3Node(), a_0)),1,null,null,null,null));


        }
        //System.out.println("Constraints mapping: " + constraints);
    }



    /**
     * Don't forward packets addressed to node
     * @param node
     */
    public void saneSend(NetworkObject node){
        Expr n_0 = ctx.mkConst(node+"_saneSend_n_0", nctx.node);
        Expr p_0 = ctx.mkConst(node+"_saneSend_p_0", nctx.packet);
        IntExpr t_0 = ctx.mkIntConst(node+"_saneSend_t_0");
        // Constant: node
        //Constraint send(node, n_0, p, t_0) -> !nodeHasAddr(node, p.dest)
        constraints.add(
                ctx.mkForall(new Expr[]{n_0, p_0, t_0},
                        ctx.mkImplies((BoolExpr)nctx.send.apply(node.getZ3Node(),n_0, p_0, t_0),
                                ctx.mkNot((BoolExpr)nctx.nodeHasAddr.apply( node.getZ3Node(),
                                        nctx.pf.get("dest").apply(p_0)))),1,null,null,null,null));
    }

    /**
     * Node sends all traffic through gateway
     * @param node
     * @param gateway
     */
    public void setGateway (NetworkObject node, NetworkObject gateway){
        // SetGateway(self, node, gateway): All packets from node are sent through gateway
        Expr n_0 = ctx.mkConst(node+"_gateway_n_0", nctx.node);
        Expr p_0 = ctx.mkConst(node+"_gateway_p_0", nctx.packet);
        IntExpr t_0 = ctx.mkIntConst(node+"_gateway_t_0");

        //Constraint send(node, n_0, p_0, t_0) -> n_0 = gateway
        constraints.add(
                ctx.mkForall(new Expr[]{n_0, p_0, t_0},
                        ctx.mkImplies(
                                (BoolExpr)nctx.send.apply(node.getZ3Node(), n_0, p_0, t_0),
                                ctx.mkEq(n_0,gateway.getZ3Node())),1,null,null,null,null));

        //        constraints.add(ctx.mkForall(new Expr[]{n_0, p_0, t_0},
        //            ctx.mkImplies((BoolExpr)nctx.recv.apply(n_0, node.getZ3Node(),  p_0, t_0),
        //                                            ctx.mkEq(n_0,gateway.getZ3Node())),1,null,null,null,null));
    }

    /**
     * Assigns a specific routing table to a network object. Routing entries in the form: address -> node
     * @param node
     * @param routing_table
     */
    public void routingTable (NetworkObject node,ArrayList<Tuple<DatatypeExpr,NetworkObject>> routing_table){
        compositionPolicy(node,routing_table);
    }

    /**
     * Composition policies steer packets between middleboxes.
     * @param node
     * @param policy
     */
    public void compositionPolicy (NetworkObject node,ArrayList<Tuple<DatatypeExpr,NetworkObject>> policy){
        //Policy is of the form predicate -> node
        Expr p_0 = ctx.mkConst(node+"_composition_p_0", nctx.packet);
        Expr n_0 = ctx.mkConst(node+"_composition_n_0", nctx.node);
        Expr t_0 = ctx.mkIntConst(node+"_composition_t_0");

        HashMap<String,ArrayList<BoolExpr>> collected = new HashMap<String,ArrayList<BoolExpr>>();
        HashMap<String,NetworkObject> node_dict = new HashMap<String,NetworkObject>();
        BoolExpr predicates;
        for(int y=0;y<policy.size();y++){
            Tuple<DatatypeExpr,NetworkObject> tp = policy.get(y);
            if(collected.containsKey(""+tp._2)) collected.get(""+tp._2).add(nctx.destAddrPredicate(p_0,tp._1));
            else{
                ArrayList<BoolExpr> alb = new ArrayList<BoolExpr>();
                alb.add(nctx.destAddrPredicate(p_0,tp._1));
                collected.put(""+tp._2,alb);

            }
            node_dict.put(""+tp._2, tp._2);

        }
        // System.out.println("collected: " + collected);

        //Constraintforeach rtAddr,rtNode in rt( send(node, n_0, p_0, t_0) &&
        //Or(foreach rtAddr in rt destAddrPredicate(p_0,rtAddr)) -> n_0 == rtNode )
        for (Map.Entry<String,ArrayList<BoolExpr>> entry : collected.entrySet()) {
            BoolExpr[] pred = new BoolExpr[entry.getValue().size()];
            predicates = ctx.mkOr(entry.getValue().toArray(pred));

            constraints.add(ctx.mkForall(new Expr[]{n_0, p_0, t_0},
                    ctx.mkImplies(ctx.mkAnd((BoolExpr)nctx.send.apply(node.getZ3Node(), n_0, p_0, t_0), predicates),
                            ctx.mkEq(n_0, node_dict.get(entry.getKey()).getZ3Node())),1,null,null,null,null));
            /*System.out.println("cnstraints: " + (ctx.mkForall(new Expr[]{n_0, p_0, t_0},
                        ctx.mkImplies(ctx.mkAnd((BoolExpr)nctx.send.apply(node.getZ3Node(), n_0, p_0, t_0), predicates),
                                ctx.mkEq(n_0, node_dict.get(entry.getKey()).getZ3Node())),1,null,null,null,null)));*/
        }
        //System.out.println("constraints composition policy: " + constraints);

    }

    /**
     * Routing entries are in the form: address -> node. Also allows packet to be sent to another box for further processing
     * @param node
     * @param routing_table
     * @param shunt_node
     */
    public void routingTableShunt (NetworkObject node,ArrayList<Tuple<DatatypeExpr,NetworkObject>> routing_table,NetworkObject shunt_node){
        compositionPolicyShunt(node,routing_table,shunt_node);
    }

    /**
     * Composition policies steer packets between middleboxes.Policy is in the form: predicate -> node
     * @param node
     * @param routing_table
     * @param shunt_node
     */
    public void compositionPolicyShunt (NetworkObject node,ArrayList<Tuple<DatatypeExpr,NetworkObject>> routing_table,NetworkObject shunt_node){
        Expr p_0 = ctx.mkConst(node+"_composition_p_0", nctx.packet);
        Expr n_0 = ctx.mkConst(node+"_composition_n_0", nctx.node);
        Expr t_0 = ctx.mkIntConst(node+"_composition_t_0");

        HashMap<String,ArrayList<BoolExpr>> collected = new HashMap<String,ArrayList<BoolExpr>>();
        HashMap<String,NetworkObject> node_dict = new HashMap<String,NetworkObject>();
        BoolExpr predicates;
        for(int y=0;y<routing_table.size();y++){
            Tuple<DatatypeExpr,NetworkObject> tp = routing_table.get(y);
            if(collected.containsKey(""+tp._2)) collected.get(""+tp._2).add(nctx.destAddrPredicate(p_0,tp._1));
            else{
                ArrayList<BoolExpr> alb = new ArrayList<BoolExpr>();
                alb.add(nctx.destAddrPredicate(p_0,tp._1));
                collected.put(""+tp._2,alb);
            }
            node_dict.put(""+tp._2, tp._2);
        }

        //Constraintforeach rtAddr,rtNode in rt( send(node, n_0, p_0, t_0) &&
        //Or(foreach rtAddr in rt destAddrPredicate(p_0,rtAddr)) -> n_0 == rtNode )
        for (Map.Entry<String,ArrayList<BoolExpr>> entry : collected.entrySet()) {
            BoolExpr[] pred = new BoolExpr[entry.getValue().size()];
            predicates = ctx.mkOr(entry.getValue().toArray(pred));

            constraints.add(ctx.mkForall(new Expr[]{n_0, p_0, t_0},
                    ctx.mkImplies(ctx.mkAnd((BoolExpr)nctx.send.apply(node.getZ3Node(), n_0, p_0, t_0), predicates),
                            ctx.mkOr(ctx.mkEq(n_0, node_dict.get(entry.getKey()).getZ3Node()),ctx.mkEq(n_0, shunt_node.getZ3Node()))),1,null,null,null,null));
        }

    }

    //    public void SimpleIsolation (NetworkObject node, ArrayList<DatatypeExpr> addresses){
    //       Expr p = ctx.mkConst(node+"_s_p", nctx.packet);
    //       Expr n = ctx.mkConst(node+"_s_n", nctx.node);
    //       IntExpr t = ctx.mkInt(node+"_s_t");
    //
    //       BoolExpr[] a_pred= new BoolExpr[addresses.size()];
    //        for(int y=0;y<addresses.size();y++){
    //        DatatypeExpr de = addresses.get(y);
    //        a_pred[y] = ctx.mkOr(ctx.mkEq(nctx.pf.get("src").apply(p), de),ctx.mkEq(nctx.pf.get("dest").apply(p), de));
    //        }
    //
    //        constraints.add(
    //                ctx.mkForall(new Expr[]{p, n, t},
    //                  ctx.mkImplies((BoolExpr)nctx.recv.apply(n, node.getZ3Node(), p, t),
    //                            ctx.mkOr(a_pred)),1,null,null,null,null));
    //        constraints.add(
    //                ctx.mkForall(new Expr[]{p, n, t},
    //                  ctx.mkImplies((BoolExpr)nctx.send.apply(node.getZ3Node(), n, p, t),
    //                            ctx.mkOr(a_pred)),1,null,null,null,null));
    //    }


    /**
     * Set isolation constraints on a node.
     * Doesn't need to be set but useful when interfering policies are in play.
     * @param node
     * @param adjacencies
     *
     */
    public void SetIsolationConstraint ( NetworkObject node,  ArrayList<NetworkObject> adjacencies){

        Expr n_0 = ctx.mkConst(node+"_isolation_n_0", nctx.node);
        Expr p_0 = ctx.mkConst(node+"_isolation_p_0", nctx.packet);
        IntExpr t_0 = ctx.mkInt(node+"_isolation_t_0");

        BoolExpr[] adj = new BoolExpr[adjacencies.size()];
        for(int y=0;y<adjacencies.size();y++){
            NetworkObject no = adjacencies.get(y);
            adj[y] =  ctx.mkEq(n_0,no.getZ3Node());
        }
        BoolExpr clause = ctx.mkOr(adj);

        constraints.add(ctx.mkForall(new Expr[]{n_0, p_0, t_0},
                ctx.mkImplies((BoolExpr)nctx.send.apply(node.getZ3Node(), n_0, p_0, t_0),
                        clause),1,null,null,null,null));
        constraints.add(ctx.mkForall(new Expr[]{n_0, p_0, t_0},
                ctx.mkImplies((BoolExpr)nctx.recv.apply(n_0, node.getZ3Node(), p_0, t_0),
                        clause),1,null,null,null,null));
    }

    /**
     * Return all currently attached endhosts
     * @return NetworkObject
     */
    public List<String> EndHosts(){
        List<String> att_nos = new ArrayList<String>();
        for(NetworkObject el :elements){
            if(el.isEndHost){
                //System.out.println("el: "+el);
                att_nos.add(el.getZ3Node().toString());
            }
        }
        return att_nos;
    }
}
