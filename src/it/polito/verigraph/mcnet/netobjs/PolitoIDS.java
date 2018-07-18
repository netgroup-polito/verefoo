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
import java.util.stream.Collectors;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.DatatypeExpr;
import com.microsoft.z3.Expr;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.Optimize;
import com.microsoft.z3.Solver;

import it.polito.verifoo.rest.common.AutoContext;
import it.polito.verigraph.mcnet.components.NetContext;
import it.polito.verigraph.mcnet.components.Network;
import it.polito.verigraph.mcnet.components.NetworkObject;
import it.polito.verigraph.mcnet.components.Tuple;

public class PolitoIDS extends NetworkObject {

    public static final int DROGA = 1; //no go
    public static final int GATTINI = 2;  //go

    Context ctx;
    List<BoolExpr> constraints = new ArrayList<BoolExpr>();
    DatatypeExpr politoIDS;
    Network net;
    NetContext nctx;
    FuncDecl isInBlacklist;
    int nRules;
	private boolean autoconf,autoplace;
	private AutoContext autoctx;


    public PolitoIDS(Context ctx, Object[]...args){
        super(ctx, args);
    }

    @Override
    public DatatypeExpr getZ3Node() {
        return politoIDS;
    }

    @Override
    protected void init(Context ctx, Object[]... args) {

        this.ctx = ctx;
        this.isEndHost = false;
        this.politoIDS = this.z3Node = ((NetworkObject)args[0][0]).getZ3Node();
        this.net = (Network)args[0][1];
        this.nctx = (NetContext)args[0][2];

        neighbours = ((ArrayList<NetworkObject>) args[0][3]);
        Expr p_0 = ctx.mkConst(politoIDS+"_p_0", nctx.packet);
   		List<Expr> recvNeighbours = neighbours.stream().map(n -> nctx.recv.apply(n.getZ3Node(), politoIDS, p_0)).collect(Collectors.toList());
   		BoolExpr[] tmp2 = new BoolExpr[recvNeighbours.size()];
   		enumerateRecvP0 = ctx.mkOr(recvNeighbours.toArray(tmp2));
   		List<Expr> sendNeighbours = neighbours.stream().map(n -> nctx.send.apply(politoIDS, n.getZ3Node(), p_0)).collect(Collectors.toList());
  		BoolExpr[] tmp3 = new BoolExpr[sendNeighbours.size()];
  		enumerateSendP0 = ctx.mkOr(sendNeighbours.toArray(tmp3));
   		
        if(args[0].length > 4 && ((Integer) args[0][4]) != 0){
	    	if(args[0].length > 5 && args[0][5] != null){
	    		used = ctx.mkBoolConst(politoIDS+"_used");
				autoplace = true;
				autoctx = (AutoContext) args[0][5];
			}
			else{
				autoplace = false;
			}
			autoconf = true; 
			nRules = (Integer) args[0][4];
	    }
		else{
			autoplace = false;
			autoconf = false;
		}
    }

    @Override
    protected void addConstraints(Optimize solver) {
        BoolExpr[] constr = new BoolExpr[constraints.size()];
        solver.Add(constraints.toArray(constr));

    }

    public void installIDS(int[] blackList){
        Expr p_0 = ctx.mkConst(politoIDS + "_p_0", nctx.packet);
        /*IntExpr t_0 = ctx.mkIntConst(politoIDS + "_t_0");
        IntExpr t_1 = ctx.mkIntConst(politoIDS + "_t_1");*/
        Expr b_0 = ctx.mkIntConst(politoIDS + "_b_0");

        isInBlacklist = ctx.mkFuncDecl(politoIDS + "_isInBlacklist", ctx.mkIntSort(), ctx.mkBoolSort());

        BoolExpr[] blConstraints = new BoolExpr[blackList.length];
        if(blackList.length != 0){

            for(int i = 0; i<blackList.length; i++)
                blConstraints[i] = ctx.mkEq(b_0, ctx.mkInt(blackList[i]));

            this.constraints.add(ctx.mkForall(new Expr[]{b_0},
                    ctx.mkIff((BoolExpr)isInBlacklist.apply(b_0), ctx.mkOr(blConstraints)),
                    1,
                    null, null, null, null));

        }/*else if(blackList==null){
            this.constraints.add(ctx.mkForall(new Expr[]{b_0},
                    ctx.mkEq(isInBlacklist.apply(b_0), ctx.mkBool(false)),
                    1,
                    null, null, null, null));
        }*/

        /*
        constraints.add(
            	ctx.mkForall(new Expr[]{n_0, p_0}, 
    	            ctx.mkImplies(
    	            		ctx.mkAnd( (BoolExpr)nctx.send.apply(politoIDS, n_0, p_0),
            				  		ctx.mkOr(ctx.mkEq(nctx.pf.get("proto").apply(p_0), ctx.mkInt(nctx.HTTP_RESPONSE))
            				  				,ctx.mkEq(nctx.pf.get("proto").apply(p_0), ctx.mkInt(nctx.HTTP_RESPONSE))
            				  				 )),
    	            	
    	            			ctx.mkAnd(
    	            						ctx.mkExists(new Expr[]{n_1}, 
    	            								nctx.recv.apply(n_1, politoIDS, p_0),1,null,null,null,null), 
    	            						ctx.mkNot((BoolExpr)isInBlacklist.apply(nctx.pf.get("body").apply(p_0))
    	            								))),1,null,null,null,null));

	  
  	  //Constraint2 obliges this VNF to send the packets that have been received
  	  constraints.add(
	            	ctx.mkForall(new Expr[]{n_0, p_0},
	            			ctx.mkImplies(	
	            					ctx.mkAnd( (BoolExpr)nctx.recv.apply(n_0, politoIDS, p_0)
	            								,ctx.mkNot((BoolExpr)isInBlacklist.apply(nctx.pf.get("body").apply(p_0)))
	            							),
	            						ctx.mkAnd(ctx.mkExists(new Expr[]{n_1}, (BoolExpr)nctx.send.apply(new Expr[]{ politoIDS, n_1, p_0}),1,null,null,null,null)
	            								)
	            						
	    	    	            	)
	            			,1,null,null,null,null));
        
        
  	/* OLD Constraints, they didn't work     */   
        //Constraint2 send(politoIDS, n_0, p, t_0) && (p.proto(HTTP_RESPONSE) || p.proto(HTTP_REQUEST)) ->
        //(exist  n_1,t_1 : (recv(n_1, politoIDS, p, t_1) && t_1 < t_0)) && !isInBlackList(p.body)
	
        this.constraints.add(ctx.mkForall(new Expr[]{p_0},
                ctx.mkImplies(ctx.mkAnd(enumerateSendP0),
                        ctx.mkAnd(enumerateRecvP0,
                                ctx.mkNot((BoolExpr)isInBlacklist.apply(nctx.pf.get("body").apply(p_0))))),
                1,
                null, null, null, null));

        this.constraints.add(ctx.mkForall(new Expr[]{p_0},
                ctx.mkImplies(ctx.mkAnd(enumerateRecvP0,
                						ctx.mkNot((BoolExpr)isInBlacklist.apply(nctx.pf.get("body").apply(p_0)))),
                				enumerateSendP0)
                ,1,null, null, null, null));

  	  
        /*this.constraints.add(ctx.mkForall(new Expr[]{n_0, p_0},
                ctx.mkImplies(ctx.mkAnd((BoolExpr)nctx.recv.apply(n_0, politoIDS,  p_0)),
                		ctx.mkAnd(
                				ctx.mkExists(new Expr[]{n_1},((BoolExpr)nctx.send.apply(politoIDS,n_1,p_0)),1,null, null, null, null)
                		,ctx.mkNot((BoolExpr)isInBlacklist.apply(nctx.pf.get("body").apply(p_0)))))
                		
                ,1,null, null, null, null));*/
    }
    
    // for an autoconfiguration IDS
    public void installIDS(int nRules){
        Expr n_0 = ctx.mkConst(politoIDS + "_n_0", nctx.node);
        Expr n_1 = ctx.mkConst(politoIDS + "_n_1", nctx.node);
        Expr p_0 = ctx.mkConst(politoIDS + "_p_0", nctx.packet);
        Expr b_0 = ctx.mkIntConst(politoIDS + "_b_0");
        List<BoolExpr> rules = new ArrayList<>();
 		List<BoolExpr> implications1 = new ArrayList<BoolExpr>();
 		List<BoolExpr> implications2 = new ArrayList<BoolExpr>();
 		for(int i = 0; i < nRules; i++){
 			Expr notAllowed = ctx.mkConst(politoIDS + "_auto_notAllowed_"+i, ctx.mkIntSort());
 			if(autoplace){
 	 			autoctx.softConstrAutoConf.add(new Tuple<BoolExpr, String>(ctx.mkEq( notAllowed, ctx.mkInt(0) ),"politoIDS"));
				implications1.add(ctx.mkNot(ctx.mkEq( notAllowed, ctx.mkInt(0))));
				implications2.add(ctx.mkEq( notAllowed, ctx.mkInt(0)));
 			}else{
 				nctx.softConstrAutoConf.add(new Tuple<BoolExpr, String>(ctx.mkEq( notAllowed, ctx.mkInt(0) ),"politoIDS"));
 			}
 			rules.add(
 						ctx.mkEq((IntExpr)nctx.pf.get("body").apply(p_0), notAllowed)
 					);
 		}
        isInBlacklist = ctx.mkFuncDecl(politoIDS + "_isInBlacklist", ctx.mkIntSort(), ctx.mkBoolSort());
        constraints.add(ctx.mkForall(new Expr[]{b_0},
                							ctx.mkEq((BoolExpr)isInBlacklist.apply(b_0), ctx.mkBool(false)),
                			1, null, null, null, null));
        BoolExpr[] tmp = new BoolExpr[rules.size()];
        //Constraint2 send(politoIDS, n_0, p, t_0) && (p.proto(HTTP_RESPONSE) || p.proto(HTTP_REQUEST)) ->
        //(exist  n_1 : (recv(n_1, politoIDS, p, t_1) )) && !isInBlackList(p.body)

        this.constraints.add(ctx.mkForall(new Expr[]{n_0, p_0},
                ctx.mkImplies(ctx.mkAnd((BoolExpr)nctx.send.apply(politoIDS, n_0, p_0)),
                        ctx.mkAnd(ctx.mkExists(new Expr[]{n_1},
                                				ctx.mkAnd((BoolExpr)nctx.recv.apply(n_1,politoIDS,p_0)),
                                				1, null, null, null, null),
                                ctx.mkNot(
                                		ctx.mkOr(
										  rules.toArray(tmp)
										  )	
                                		))), 1, null, null, null, null));

        this.constraints.add(ctx.mkForall(new Expr[]{n_0, p_0},
                ctx.mkImplies(ctx.mkAnd((BoolExpr)nctx.recv.apply(n_0, politoIDS,  p_0),
				                		ctx.mkNot(
				                				ctx.mkOr(
												  rules.toArray(tmp)
												  )	
				                				)),
                		ctx.mkExists(new Expr[]{n_1},((BoolExpr)nctx.send.apply(politoIDS,n_1,p_0)),1,null, null, null, null))
                ,1,null, null, null, null));
        if(autoplace){
 			BoolExpr[] tmp4 = new BoolExpr[implications1.size()];
 			//System.out.println("Adding to antispam constraints: " + ctx.mkImplies(ctx.mkAnd(implications1.toArray(tmp3)), used));
 			constraints.add(     ctx.mkImplies(ctx.mkOr(implications1.toArray(tmp4)),used)    );

 	 		BoolExpr[] tmp5 = new BoolExpr[implications2.size()];
 			//System.out.println("Adding to antispam constraints: " + ctx.mkImplies(ctx.mkNot(used), ctx.mkAnd(implications2.toArray(tmp4))));
 			constraints.add(     ctx.mkImplies(ctx.mkNot(used), ctx.mkAnd(implications2.toArray(tmp5)))    );
 			//Constraint3 set a constraint to decide if a firewall is being used
 	        this.constraints.add(
 	            	ctx.mkForall(new Expr[]{n_0, p_0},
 	            				ctx.mkImplies(	 enumerateRecvP0, used  )
 	            			,1,null,null,null,null));

        }	
    }

}
