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
import java.util.Arrays;
import java.util.List;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.Model;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;
import it.polito.verigraph.mcnet.components.IsolationResult;
import it.polito.verigraph.mcnet.components.NetContext;
import it.polito.verigraph.mcnet.components.Network;
import it.polito.verigraph.mcnet.components.NetworkObject;

/**Various checks for specific properties in the network.
 *
 *
 */
public class Checker {

    Context ctx;
    Network net;
    NetContext nctx;
    Solver solver;
    ArrayList<BoolExpr> constraints;
    public BoolExpr [] assertions;
    public Status result;
    public Model model;


    public Checker(Context context,NetContext nctx,Network network){
        this.ctx = context;
        this.net = network;
        this.nctx = nctx;
        this.solver = ctx.mkSolver();
        this.constraints = new ArrayList<BoolExpr>();
    }

    /**Resets the constraints
     *
     */
    public void clearState (){
        this.solver.reset();
        this.constraints = new ArrayList<BoolExpr>();
    }

    /**Checks whether the source provided can reach the destination
     *
     * @param src
     * @param dest
     * @return
     */
    public IsolationResult checkIsolationProperty (NetworkObject src, NetworkObject dest){
        assert(net.elements.contains(src));
        assert(net.elements.contains(dest));
        solver.push ();
        addConstraints();


        Expr p0 = ctx.mkConst("check_isolation_p0_"+src.getZ3Node()+"_"+dest.getZ3Node(), nctx.packet);
        Expr p1 = ctx.mkConst("check_isolation_p1_"+src.getZ3Node()+"_"+dest.getZ3Node(), nctx.packet);
        Expr n_0 =ctx.mkConst("check_isolation_n_0_"+src.getZ3Node()+"_"+dest.getZ3Node(), nctx.node);
        Expr n_1 =ctx.mkConst("check_isolation_n_1_"+src.getZ3Node()+"_"+dest.getZ3Node(), nctx.node);
        IntExpr t_0 = ctx.mkIntConst("check_isolation_t0_"+src.getZ3Node()+"_"+dest.getZ3Node());
        IntExpr t_1 = ctx.mkIntConst("check_isolation_t1_"+src.getZ3Node()+"_"+dest.getZ3Node());

        //        Constraint1recv(n_0,destNode,p0,t_0)
        this.solver.add((BoolExpr)nctx.recv.apply(n_0, dest.getZ3Node(), p0, t_0));

        //        Constraint2send(srcNode,n_1,p1,t_1)
        this.solver.add((BoolExpr)nctx.send.apply(src.getZ3Node(), n_1, p1, t_1));

        //        Constraint3nodeHasAddr(srcNode,p1.srcAddr)
        this.solver.add((BoolExpr)nctx.nodeHasAddr.apply(src.getZ3Node(), nctx.pf.get("src").apply(p1)));


        //        Constraint4p1.origin == srcNode
        this.solver.add(ctx.mkEq(nctx.pf.get("origin").apply(p1), src.getZ3Node()));

        //        Constraint5nodeHasAddr(destNode,p1.destAddr)
        this.solver.add((BoolExpr)nctx.nodeHasAddr.apply(dest.getZ3Node(), nctx.pf.get("dest").apply(p1)));

        //NON sembrano necessari
        //         this.solver.add(z3.Or(this.ctx.nodeHasAddr(src.getZ3Node(), this.ctx.packet.src(p0)),\
        //                               this.ctx.nodeHasAddr(n_0, this.ctx.packet.src(p0)),\
        //                               this.ctx.nodeHasAddr(n_1, this.ctx.packet.src(p0))))
        //this.solver.add(this.ctx.packet.dest(p1) == this.ctx.packet.dest(p0))

        //        Constraint6p1.origin ==  p0.origin
        this.solver.add(ctx.mkEq(nctx.pf.get("origin").apply(p1),nctx.pf.get("origin").apply(p0)));

        //        Constraint7nodeHasAddr(destNode, p0.destAddr)
        this.solver.add((BoolExpr)nctx.nodeHasAddr.apply(dest.getZ3Node(), nctx.pf.get("dest").apply(p0)));

        result = this.solver.check();
        model = null;
        assertions = this.solver.getAssertions();
        if (result == Status.SATISFIABLE){
            model = this.solver.getModel();
        }
        this.solver.pop();
        return new IsolationResult(ctx,result, p0, n_0, t_1, t_0, nctx, assertions, model);
    }



    /*public IsolationResult CheckIsolationFlowProperty (NetworkObject src, NetworkObject dest){
assert(net.elements.contains(src));
    assert(net.elements.contains(dest));
    solver.push ();
    addConstraints();

    Expr p = ctx.mkConst("check_isolation_p_"+src.getZ3Node()+"_"+dest.getZ3Node(), nctx.packet);
    Expr n_0 =ctx.mkConst("check_isolation_n_0_"+src.getZ3Node()+"_"+dest.getZ3Node(), nctx.node);
    Expr n_1 =ctx.mkConst("check_isolation_n_1_"+src.getZ3Node()+"_"+dest.getZ3Node(), nctx.node);
    Expr n_2 =ctx.mkConst("check_isolation_n_1_"+src.getZ3Node()+"_"+dest.getZ3Node(), nctx.node);

    IntExpr t_0 = ctx.mkIntConst("check_isolation_t0_"+src.getZ3Node()+"_"+dest.getZ3Node());
    IntExpr t_1 = ctx.mkIntConst("check_isolation_t1_"+src.getZ3Node()+"_"+dest.getZ3Node());
    IntExpr t_2 = ctx.mkIntConst("check_isolation_t2_"+src.getZ3Node()+"_"+dest.getZ3Node());

//        Constraint1recv(n_0,destNode,p,t_0)
    this.solver.add((BoolExpr)nctx.recv.apply(n_0, dest.getZ3Node(), p, t_0));

//        Constraint2send(srcNode,n_1,p1,t_1)
    this.solver.add((BoolExpr)nctx.send.apply(src.getZ3Node(), n_1, p, t_1));

//        Constraint3nodeHasAddr(srcNode,p.srcAddr)
    this.solver.add((BoolExpr)nctx.nodeHasAddr.apply(src.getZ3Node(), nctx.pf.get("src").apply(p)));

//        Constraint4p.origin == srcNode
    this.solver.add(ctx.mkEq(nctx.pf.get("origin").apply(p), src.getZ3Node()));


    Expr p_2 = ctx.mkConst("check_isolation_p_flow_"+src.getZ3Node()+"_"+dest.getZ3Node(), nctx.packet);

    //    Constraint5there does not exist p_2, n_2, t_2 :
//     send(destNode,n_2,p_2,t_2) &&
//         p_2.srcAddr == p. destAddr &&
//         p_2.srcPort == p.destPort &&
//         p_2.destPort == p.srcPort &&
//         p_2.destt == p.src &&
//         t_2 < t_0
    this.solver.add(ctx.mkNot(ctx.mkExists(new Expr[]{p_2,n_2,t_2},
    ctx.mkAnd(
    (BoolExpr)nctx.send.apply(dest.getZ3Node(), n_2, p_2, t_2),
    ctx.mkEq(nctx.pf.get("src").apply(p_2), nctx.pf.get("dest").apply(p)),
    ctx.mkEq(nctx.src_port.apply(p_2), nctx.dest_port.apply(p)),
    ctx.mkEq(nctx.dest_port.apply(p_2), nctx.src_port.apply(p)),
    ctx.mkEq(nctx.pf.get("dest").apply(p_2), nctx.pf.get("src").apply(p)),
    ctx.mkLt(t_2,t_0)),1,null,null,null,null)));
    //System.out.println((ctx.mkNot(ctx.mkExists(new Expr[]{p_2,n_2,t_2},
ctx.mkAnd(
(BoolExpr)nctx.send.apply(dest.getZ3Node(), n_2, p_2, t_2),
ctx.mkEq(nctx.pf.get("src").apply(p_2), nctx.pf.get("dest").apply(p)),
ctx.mkEq(nctx.src_port.apply(p_2), nctx.dest_port.apply(p)),
ctx.mkEq(nctx.dest_port.apply(p_2), nctx.src_port.apply(p)),
ctx.mkEq(nctx.pf.get("dest").apply(p_2), nctx.pf.get("src").apply(p)),
ctx.mkLt(t_2,t_0)),1,null,null,null,null))));

    result = this.solver.check();
    model = null;
    assertions = this.solver.getAssertions();
    if (result == Status.SATISFIABLE){
    model = this.solver.getModel();
    }
    this.solver.pop();
    return new IsolationResult(ctx,result, p, n_0, t_1, t_0, nctx, assertions, model);
}



    public IsolationResult  CheckNodeTraversalProperty (NetworkObject src, NetworkObject dest, NetworkObject node){
assert(net.elements.contains(src));
    assert(net.elements.contains(dest));
    solver.push ();
    addConstraints();

    Expr p = ctx.mkConst("check_isolation_p_"+src.getZ3Node()+"_"+dest.getZ3Node(), nctx.packet);

    Expr n_0 =ctx.mkConst("check_isolation_n_0_"+src.getZ3Node()+"_"+dest.getZ3Node(), nctx.node);
    Expr n_1 =ctx.mkConst("check_isolation_n_1_"+src.getZ3Node()+"_"+dest.getZ3Node(), nctx.node);
    Expr n_2 =ctx.mkConst("check_isolation_n_1_"+src.getZ3Node()+"_"+dest.getZ3Node(), nctx.node);

    IntExpr t_0 = ctx.mkIntConst("check_isolation_t0_"+src.getZ3Node()+"_"+dest.getZ3Node());
    IntExpr t_1 = ctx.mkIntConst("check_isolation_t1_"+src.getZ3Node()+"_"+dest.getZ3Node());
    IntExpr t_2 = ctx.mkIntConst("check_isolation_t2_"+src.getZ3Node()+"_"+dest.getZ3Node());

//        Constraint1recv(n_0,destNode,p,t_0)
    this.solver.add((BoolExpr)nctx.recv.apply(n_0, dest.getZ3Node(), p, t_0));

//        Constraint2send(srcNode,n_1,p1,t_1)
    this.solver.add((BoolExpr)nctx.send.apply(src.getZ3Node(), n_1, p, t_1));

//        Constraint3nodeHasAddr(srcNode,p.srcAddr)
    this.solver.add((BoolExpr)nctx.nodeHasAddr.apply(src.getZ3Node(), nctx.pf.get("src").apply(p)));

//        Constraint4p.origin == srcNode
    this.solver.add(ctx.mkEq(nctx.pf.get("origin").apply(p), src.getZ3Node()));


//Constraint5there does not exist n_2, t_2 : recv(n_2,node,p,t_2) && t_2 < t_0
    this.solver.add(ctx.mkNot(ctx.mkExists(new Expr[]{n_2,t_2},
    ctx.mkAnd(
    (BoolExpr)nctx.recv.apply(n_2, node.getZ3Node(), p, t_2),
    ctx.mkLt(t_2,t_0)),1,null,null,null,null)));

//Constraint 6there does not exist n_2, t_2 : send(node,n_2,p,t_2) && t_2 < t_0
    this.solver.add(ctx.mkNot(ctx.mkExists(new Expr[]{n_2,t_2},
ctx.mkAnd(
(BoolExpr)nctx.send.apply(node.getZ3Node(), n_2, p, t_2),
ctx.mkLt(t_2,t_0)),1,null,null,null,null)));


    result = this.solver.check();
    model = null;
    assertions = this.solver.getAssertions();
    if (result == Status.SATISFIABLE){
    model = this.solver.getModel();
    }
    this.solver.pop();
    return new IsolationResult(ctx,result, p, n_0, t_1, t_0, nctx, assertions, model);

    }

    public IsolationResult CheckLinkTraversalProperty (NetworkObject src, NetworkObject dest, NetworkObject le0, NetworkObject le1){
assert(net.elements.contains(src));
    assert(net.elements.contains(dest));
    solver.push ();
    addConstraints();

    Expr p = ctx.mkConst("check_isolation_p_"+src.getZ3Node()+"_"+dest.getZ3Node(), nctx.packet);

    Expr n_0 =ctx.mkConst("check_isolation_n_0_"+src.getZ3Node()+"_"+dest.getZ3Node(), nctx.node);
    Expr n_1 =ctx.mkConst("check_isolation_n_1_"+src.getZ3Node()+"_"+dest.getZ3Node(), nctx.node);

    IntExpr t_0 = ctx.mkIntConst("check_isolation_t0_"+src.getZ3Node()+"_"+dest.getZ3Node());
    IntExpr t_1 = ctx.mkIntConst("check_isolation_t1_"+src.getZ3Node()+"_"+dest.getZ3Node());
    IntExpr t_2 = ctx.mkIntConst("check_isolation_t2_"+src.getZ3Node()+"_"+dest.getZ3Node());

//        Constraint1recv(n_0,destNode,p,t_0)
    this.solver.add((BoolExpr)nctx.recv.apply(n_0, dest.getZ3Node(), p, t_0));

//        Constraint2send(srcNode,n_1,p,t_1)
    this.solver.add((BoolExpr)nctx.send.apply(src.getZ3Node(), n_1, p, t_1));

//        Constraint3nodeHasAddr(srcNode,p.srcAddr)
    this.solver.add((BoolExpr)nctx.nodeHasAddr.apply(src.getZ3Node(), nctx.pf.get("src").apply(p)));

//        Constraint4p.origin == srcNode
    this.solver.add(ctx.mkEq(nctx.pf.get("origin").apply(p), src.getZ3Node()));

//Constraint5∃ t_1, t_2 :
    //    send(linkNode0,linkNode1,p,t_1) &&
//    recv(linkNode0,linkNode1,p,t_2) &&
//    t_1 < t_0 &&
//    t_2 < t_0
    this.solver.add(ctx.mkExists(new Expr[]{t_1,t_2},
    ctx.mkAnd(
    (BoolExpr)nctx.send.apply(le0.getZ3Node(), le1.getZ3Node(), p, t_1),
    (BoolExpr)nctx.recv.apply(le0.getZ3Node(), le1.getZ3Node(), p, t_2),
    ctx.mkLt(t_1,t_0),
    ctx.mkLt(t_2,t_0)),1,null,null,null,null));


    result = this.solver.check();
    model = null;
    assertions = this.solver.getAssertions();
    if (result == Status.SATISFIABLE){
    model = this.solver.getModel();
    }
    this.solver.pop();
    return new IsolationResult(ctx,result, p, n_0, t_1, t_0, nctx, assertions, model);

    }

   public Result CheckDataIsolationPropertyCore (NetworkObject src, NetworkObject dest){
   assert(net.elements.contains(src));
    assert(net.elements.contains(dest));
    List<BoolExpr> constr = new ArrayList(this.getConstraints());

    Expr p = ctx.mkConst("check_isolation_p_"+src.getZ3Node()+"_"+dest.getZ3Node(), nctx.packet);

    Expr n_0 =ctx.mkConst("check_isolation_n_0_"+src.getZ3Node()+"_"+dest.getZ3Node(), nctx.node);

    IntExpr t = ctx.mkIntConst("check_isolation_t_"+src.getZ3Node()+"_"+dest.getZ3Node());

//        Constraint1recv(n_0,destNode,p,t)
    constr.add((BoolExpr)nctx.recv.apply(n_0, dest.getZ3Node(), p, t));


//        Constraint2p.origin == srcNode
    this.solver.add(ctx.mkEq(nctx.pf.get("origin").apply(p), src.getZ3Node()));

    this.solver.push();

    //        Constraint3for each constraint( n -> constraint)
    ArrayList<BoolExpr> names =new ArrayList<BoolExpr>();
    for(BoolExpr con : constr){
    BoolExpr n = ctx.mkBoolConst(""+con);
    names.add(n);
    this.solver.add(ctx.mkImplies(n, con));
    }

    BoolExpr[] nam = new BoolExpr[names.size()];
    result = this.solver.check(names.toArray(nam));
    Result ret =null;

    if (result == Status.SATISFIABLE){
    System.out.println("SAT");
    ret = new Result(ctx,this.solver.getModel());
    }else if(result == Status.UNSATISFIABLE){
    System.out.println("unsat");
    ret = new Result(ctx,this.solver.getUnsatCore());
    }
    this.solver.pop();
    return ret;
   }

    public DataIsolationResult CheckDataIsolationProperty(NetworkObject src, NetworkObject dest){
    assert(net.elements.contains(src));
    assert(net.elements.contains(dest));
    solver.push ();
    addConstraints();

    Expr p = ctx.mkConst("check_isolation_p_"+src.getZ3Node()+"_"+dest.getZ3Node(), nctx.packet);

    Expr n_0 =ctx.mkConst("check_isolation_n_0_"+src.getZ3Node()+"_"+dest.getZ3Node(), nctx.node);
    IntExpr t = ctx.mkIntConst("check_isolation_t_"+src.getZ3Node()+"_"+dest.getZ3Node());

//        Constraint1recv(n_0,destNode,p,t)
    this.solver.add((BoolExpr)nctx.recv.apply(n_0, dest.getZ3Node(), p, t));

//        Constraint2p.origin == srcNode
    this.solver.add(ctx.mkEq(nctx.pf.get("origin").apply(p), src.getZ3Node()));

    result = this.solver.check();
    model = null;
    assertions = this.solver.getAssertions();
    if (result == Status.SATISFIABLE){
    model = this.solver.getModel();
    }
    this.solver.pop();
    return new DataIsolationResult(ctx,result, p, n_0, t, nctx, assertions, model);
    }
     */



    public void addConstraints(){
        nctx.addConstraints(solver);
        net.addConstraints(solver);
        for (NetworkObject el : net.elements)
            el.addConstraints(solver);
    }


    public List<BoolExpr> getConstraints(){
        Solver l = ctx.mkSolver();
        nctx.addConstraints(l);
        net.addConstraints(l);
        for (NetworkObject el : net.elements)
            el.addConstraints(l);
        return Arrays.asList(l.getAssertions());
    }
}
