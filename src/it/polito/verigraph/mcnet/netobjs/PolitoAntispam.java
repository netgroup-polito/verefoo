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
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.DatatypeExpr;
import com.microsoft.z3.Expr;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.Optimize;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Sort;

import it.polito.verifoo.rest.common.AutoContext;
import it.polito.verigraph.mcnet.components.NetContext;
import it.polito.verigraph.mcnet.components.Network;
import it.polito.verigraph.mcnet.components.NetworkObject;
import it.polito.verigraph.mcnet.components.Tuple;

/**
 * Model of an anti-spam node
 *
 */
public class PolitoAntispam extends NetworkObject{

    List<BoolExpr> constraints;
    Context ctx;
    DatatypeExpr politoAntispam;
    Network net;
    NetContext nctx;
    FuncDecl isInBlacklist;
    int[] blacklist;
    int nRules;
	private boolean autoconf,autoplace;
	private AutoContext autoctx;
    public PolitoAntispam(Context ctx, Object[]... args) {
        super(ctx, args);
    }

    @Override
    protected void init(Context ctx, Object[]... args) {
        this.ctx = ctx;
        isEndHost=false;
        constraints = new ArrayList<BoolExpr>();
        z3Node = ((NetworkObject)args[0][0]).getZ3Node();
        politoAntispam = z3Node;
        net = (Network)args[0][1];
        nctx = (NetContext)args[0][2];
        net.saneSend(this);
        if(args[0].length > 3 && ((Integer) args[0][3]) != 0){
	    	if(args[0].length > 4 && args[0][4] != null){
	    		used = ctx.mkBoolConst(politoAntispam+"_used");
				autoplace = true;
				autoctx = (AutoContext) args[0][4];
			}
			else{
				autoplace = false;
			}
			autoconf = true; 
			nRules = (Integer) args[0][3];
			installAntispam(nRules);
	    }
		else{
			autoplace = false;
			autoconf = false;
		}
    }

    @Override
    public DatatypeExpr getZ3Node() {
        return politoAntispam;
    }

    @Override
    protected void addConstraints(Optimize solver) {
        BoolExpr[] constr = new BoolExpr[constraints.size()];
        solver.Add(constraints.toArray(constr));
        blacklistConstraints(solver);
    }

    public void installAntispam (int[] blackList){
    	  Expr n_0 = ctx.mkConst(politoAntispam+"_n_0", nctx.node);
          Expr n_1 = ctx.mkConst(politoAntispam+"_n_1", nctx.node);
          Expr p_0 = ctx.mkConst(politoAntispam+"_p_0", nctx.packet);
          //IntExpr t_0 = ctx.mkIntConst(politoAntispam+"_t_0");
          //IntExpr t_1 = ctx.mkIntConst(politoAntispam+"_t_1");
         
          isInBlacklist = ctx.mkFuncDecl(politoAntispam+"_isInBlacklist", ctx.mkIntSort(), ctx.mkBoolSort());
          addBlackList(blackList);
          constraints.add(
	            	ctx.mkForall(new Expr[]{n_0, p_0}, 
	    	            ctx.mkImplies(
	    	            		ctx.mkAnd( (BoolExpr)nctx.send.apply(politoAntispam, n_0, p_0),
                				  		ctx.mkOr(ctx.mkEq(nctx.pf.get("proto").apply(p_0), ctx.mkInt(nctx.POP3_REQUEST))
                				  				,ctx.mkEq(nctx.pf.get("proto").apply(p_0), ctx.mkInt(nctx.POP3_RESPONSE))
                				  				 )),
	    	            	
	    	            			ctx.mkAnd(
	    	            						ctx.mkExists(new Expr[]{n_1}, 
	    	            								nctx.recv.apply(n_1, politoAntispam, p_0),1,null,null,null,null), 
	    	            						ctx.mkNot((BoolExpr)isInBlacklist.apply(nctx.pf.get("emailFrom").apply(p_0))
	    	            								))),1,null,null,null,null));

  	  
	  	  //Constraint2 obliges this VNF to send the packets that have been received
	  	  constraints.add(
		            	ctx.mkForall(new Expr[]{n_0, p_0},
		            			ctx.mkImplies(	
		            					ctx.mkAnd( (BoolExpr)nctx.recv.apply(n_0, politoAntispam, p_0)
		            								,ctx.mkNot((BoolExpr)isInBlacklist.apply(nctx.pf.get("emailFrom").apply(p_0)))
		            							),
		            						ctx.mkAnd(ctx.mkExists(new Expr[]{n_1}, (BoolExpr)nctx.send.apply(new Expr[]{ politoAntispam, n_1, p_0}),1,null,null,null,null)
		            								)
		            						
		    	    	            	)
		            			,1,null,null,null,null));
  	  /* OLD constraints, they didn't work
          constraints.add(
                  ctx.mkForall(new Expr[]{n_0, p_0}, 
                          ctx.mkImplies(
                        		  ctx.mkAnd( (BoolExpr)nctx.send.apply(politoAntispam, n_0, p_0),
                        				  		ctx.mkOr(ctx.mkEq(nctx.pf.get("proto").apply(p_0), ctx.mkInt(nctx.POP3_REQUEST))
                        				  				,ctx.mkEq(nctx.pf.get("proto").apply(p_0), ctx.mkInt(nctx.POP3_RESPONSE))
                        				  				 )),
                                  ctx.mkNot((BoolExpr)isInBlacklist.apply(nctx.pf.get("emailFrom").apply(p_0)))
                                          ),1,null,null,null,null));
          
          constraints.add( ctx.mkForall(new Expr[]{n_0, p_0},
                  ctx.mkImplies(ctx.mkAnd((BoolExpr)nctx.send.apply(politoAntispam, n_0, p_0)),
                          ctx.mkAnd(ctx.mkExists(new Expr[]{n_1},
                                  ctx.mkAnd((BoolExpr)nctx.recv.apply(n_1, politoAntispam, p_0)),1,null,null,null,null)
                                  )),1,null,null,null,null));*/          
    }
    
    public void installAntispam (int nRules){
  	    Expr n_0 = ctx.mkConst(politoAntispam+"_n_0", nctx.node);
        Expr n_1 = ctx.mkConst(politoAntispam+"_n_1", nctx.node);
        Expr p_0 = ctx.mkConst(politoAntispam+"_p_0", nctx.packet);
        List<BoolExpr> rules = new ArrayList<>();
 		List<BoolExpr> implications1 = new ArrayList<BoolExpr>();
 		List<BoolExpr> implications2 = new ArrayList<BoolExpr>();
 		for(int i = 0; i < nRules; i++){
 			Expr emailFrom = ctx.mkConst(politoAntispam + "_auto_emailFrom_"+i, ctx.mkIntSort());
 			if(autoplace){
 	 			autoctx.softConstrAutoConf.add(new Tuple<BoolExpr, String>(ctx.mkEq( emailFrom, ctx.mkInt(0) ),"politoAntispam"));
				implications1.add(ctx.mkNot(ctx.mkEq( emailFrom, ctx.mkInt(0))));
				implications2.add(ctx.mkEq( emailFrom, ctx.mkInt(0)));
 			}else{
 	 			nctx.softConstrAutoConf.add(new Tuple<BoolExpr, String>(ctx.mkEq( emailFrom, ctx.mkInt(0) ),"politoAntispam"));
 			}
 			rules.add(
 						ctx.mkEq((IntExpr)nctx.pf.get("emailFrom").apply(p_0), emailFrom)
 					);
 		}
 		
 		isInBlacklist = ctx.mkFuncDecl(politoAntispam+"_isInBlacklist", ctx.mkIntSort(), ctx.mkBoolSort());
 		BoolExpr[] tmp = new BoolExpr[rules.size()];
  	  
 		this.constraints.add(
 				ctx.mkForall(new Expr[] { n_0, p_0 }, 
	 				ctx.mkImplies(
	 						ctx.mkAnd( (BoolExpr)nctx.send.apply(politoAntispam, n_0, p_0),
            				  		ctx.mkOr(ctx.mkEq(nctx.pf.get("proto").apply(p_0), ctx.mkInt(nctx.POP3_REQUEST))
            				  				,ctx.mkEq(nctx.pf.get("proto").apply(p_0), ctx.mkInt(nctx.POP3_RESPONSE))
            				  				 )),
			 				ctx.mkAnd(ctx.mkExists(new Expr[] { n_1 }, 
			 								nctx.recv.apply(n_1, politoAntispam, p_0), 1, null, null, null, null),
					 						  ctx.mkNot(
					 								  ctx.mkOr(
					 										  rules.toArray(tmp)
					 										  )		
					 								  ))), 1, null, null, null, null));
 		BoolExpr[] tmp2 = new BoolExpr[rules.size()];
 		constraints.add(
 				ctx.mkForall(new Expr[] { n_0, p_0 },
		 				ctx.mkImplies(
		 						ctx.mkAnd((BoolExpr) nctx.recv.apply(n_0, politoAntispam, p_0),
					 						ctx.mkNot(
					 									ctx.mkOr(
					 											rules.toArray(tmp2)
					 									   )
					 						)), 
		 						ctx.mkAnd(ctx.mkExists(new Expr[] { n_1 }, (BoolExpr) nctx.send.apply(new Expr[] { politoAntispam, n_1, p_0 }), 1, null, null, null, null))),
		 				1, null, null, null, null));
 		if(autoplace){
 			BoolExpr[] tmp3 = new BoolExpr[implications1.size()];
 			//System.out.println("Adding to antispam constraints: " + ctx.mkImplies(ctx.mkAnd(implications1.toArray(tmp3)), used));
 			constraints.add(     ctx.mkImplies(ctx.mkOr(implications1.toArray(tmp3)),used)    );

 	 		BoolExpr[] tmp4 = new BoolExpr[implications2.size()];
 			//System.out.println("Adding to antispam constraints: " + ctx.mkImplies(ctx.mkNot(used), ctx.mkAnd(implications2.toArray(tmp4))));
 			constraints.add(     ctx.mkImplies(ctx.mkNot(used), ctx.mkAnd(implications2.toArray(tmp4)))    );
 		}			
					 	      
  }
    public void addBlackList(int[] list){
        this.blacklist = list;
    }

    //Constraint2 isInBlacklist(a_0) == or(foreach emailFrom in blacklist (a_0 == emailFrom))
    private void blacklistConstraints(Optimize  solver){
        Expr a_0 = ctx.mkConst(politoAntispam+"_blacklist_a_0", ctx.getIntSort());

        if (blacklist == null || blacklist.length == 0){
        	//If the size of the blacklist is empty then by default isInBlacklist must be false
   		 	solver.Add(ctx.mkForall(new Expr[]{a_0},
							ctx.mkEq( 
									isInBlacklist.apply(a_0), ctx.mkFalse()),
						1,null,null,null,null));
        	return;
        }
        BoolExpr[] blacklist_map = new BoolExpr[blacklist.length];
        for(int y=0;y<blacklist.length;y++){
            blacklist_map[y] = ctx.mkOr(ctx.mkEq(a_0,ctx.mkInt(blacklist[y])));
        }
        solver.Add(ctx.mkForall(new Expr[]{a_0},
                ctx.mkEq( 
                        isInBlacklist.apply(a_0),
                        ctx.mkOr(blacklist_map)),1,null,null,null,null));
    }
}