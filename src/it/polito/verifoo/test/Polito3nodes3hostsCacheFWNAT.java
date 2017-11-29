/*
 * Copyright 2016 Politecnico di Torino
 * Authors:
 * Project Supervisor and Contact: Riccardo Sisto (riccardo.sisto@polito.it)
 * 
 * This file is part of Verigraph.
 * 
 * Verigraph is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 * 
 * Verigraph is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public
 * License along with Verigraph.  If not, see
 * <http://www.gnu.org/licenses/>.
 */
package it.polito.verifoo.test;


import java.util.ArrayList;
import java.util.HashMap;

import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.DatatypeExpr;
import com.microsoft.z3.Status;
import com.microsoft.z3.Z3Exception;

import it.polito.verifoo.components.RoutingTable;
import it.polito.verigraph.mcnet.components.Checker;
import it.polito.verigraph.mcnet.components.Checker.Prop;
import it.polito.verigraph.mcnet.components.IsolationResult;
import it.polito.verigraph.mcnet.components.NetContext;
import it.polito.verigraph.mcnet.components.Network;
import it.polito.verigraph.mcnet.components.NetworkObject;
import it.polito.verigraph.mcnet.components.Quattro;
import it.polito.verigraph.mcnet.components.Tuple;
import it.polito.verigraph.mcnet.netobjs.AclFirewall;
import it.polito.verigraph.mcnet.netobjs.Classifier;
import it.polito.verigraph.mcnet.netobjs.PolitoAntispam;
import it.polito.verigraph.mcnet.netobjs.PolitoCache;
import it.polito.verigraph.mcnet.netobjs.PolitoEndHost;
import it.polito.verigraph.mcnet.netobjs.PolitoMailClient;
import it.polito.verigraph.mcnet.netobjs.PolitoMailServer;
import it.polito.verigraph.mcnet.netobjs.PolitoNat;
import it.polito.verigraph.mcnet.netobjs.PolitoWebClient;
import it.polito.verigraph.mcnet.netobjs.PolitoWebServer;




/**
 * @author Giacomo Costantini
 * <p/>
 * Antispam test													<p/>
 *| CLIENT | --------- | ANTISPAM | --------- | MAIL SERVER |		<p/>
 *..........................|										<p/>
 *...................| ERR FUNCTION |								<p/>
 */
public class Polito3nodes3hostsCacheFWNAT {
	
	public Checker check;
	public AclFirewall x1;
	public PolitoCache x2;
	public PolitoNat x3;
	public PolitoWebClient a;
	public PolitoWebServer b;
	
	
	
	public  BoolExpr y1;
   	public  BoolExpr y2;
   	public  BoolExpr y3;
   	
   	
   	public  BoolExpr x11;
   	public  BoolExpr x12;
   	public  BoolExpr x21;
   	public  BoolExpr x22;
   	public  BoolExpr x23;
   	public  BoolExpr x32;
   	public  BoolExpr x33;
   	public Context ctx;
   	
   	public void resetZ3() throws Z3Exception{
        HashMap<String, String> cfg = new HashMap<String, String>();
        cfg.put("model", "true");
         ctx = new Context(cfg);
    }
	public	Polito3nodes3hostsCacheFWNAT(){
			ctx = new Context();
		
			NetContext nctx = new NetContext (ctx,
					new String[]{"a", "fw","x2","x3", "b"},
					new String[]{"ip_a", "ip_x1", "ip_x2", "ip_x3", "ip_b"});
			Network net = new Network (ctx,new Object[]{nctx});
			setConditions(ctx,nctx);
			
			x1 = new AclFirewall(ctx, new Object[]{nctx.nm.get("fw"), net, nctx});
			x2 = new PolitoCache(ctx, new Object[]{nctx.nm.get("x2"), net, nctx});
			x3 = new PolitoNat(ctx, new Object[]{nctx.nm.get("x3"), net, nctx});
			b = new PolitoWebServer(ctx, new Object[]{nctx.nm.get("b"), net, nctx});
			a = new PolitoWebClient(ctx, new Object[]{nctx.nm.get("a"), net, nctx,nctx.am.get("ip_b")});
		  
			ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>> adm = new ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>>();
			ArrayList<DatatypeExpr> al1 = new ArrayList<DatatypeExpr>();
  			ArrayList<DatatypeExpr> al2 = new ArrayList<DatatypeExpr>();
  			ArrayList<DatatypeExpr> al3 = new ArrayList<DatatypeExpr>();
  			ArrayList<DatatypeExpr> al5 = new ArrayList<DatatypeExpr>();
  			ArrayList<DatatypeExpr> al6 = new ArrayList<DatatypeExpr>();
  			
  			al1.add(nctx.am.get("ip_a"));
  			al2.add(nctx.am.get("ip_x1"));
  			al3.add(nctx.am.get("ip_b"));
  			al5.add(nctx.am.get("ip_x2"));
  			al6.add(nctx.am.get("ip_x3"));
			adm.add(new Tuple<>(a, al1));
		    adm.add(new Tuple<>(x1, al2));
		    adm.add(new Tuple<>(b, al3));
		    adm.add(new Tuple<>(x2, al5));
		    adm.add(new Tuple<>(x3, al6));

		    net.setAddressMappings(adm);

		    //for each Link from Client to Servers
		    ArrayList<RoutingTable> rtClient = new ArrayList<RoutingTable>();
		    //Client sends always to x1 (first middlebox in the graph)
			rtClient.add(new RoutingTable(nctx.am.get("ip_b"), x1, nctx.addLatency(a_y1), x11));
			rtClient.add(new RoutingTable(nctx.am.get("ip_b"), x1, nctx.addLatency(a_y2), x12));
						
			ArrayList<RoutingTable> rtX1 = new ArrayList<RoutingTable>();
			rtX1.add(new RoutingTable(nctx.am.get("ip_b"), x2,nctx.addLatency(0),ctx.mkAnd(x11,x21)));
			rtX1.add(new RoutingTable(nctx.am.get("ip_b"), x2,nctx.addLatency(0),ctx.mkAnd(x12,x22)));
			
			rtX1.add(new RoutingTable(nctx.am.get("ip_b"), x2,nctx.addLatency(y1_y2),ctx.mkAnd(x11,x22)));
			rtX1.add(new RoutingTable(nctx.am.get("ip_b"), x2,nctx.addLatency(y1_y2),ctx.mkAnd(x12,x21)));

			rtX1.add(new RoutingTable(nctx.am.get("ip_b"), x2,nctx.addLatency(y1_y3),ctx.mkAnd(x11,x23)));
			rtX1.add(new RoutingTable(nctx.am.get("ip_b"), x2,nctx.addLatency(y2_y3),ctx.mkAnd(x12,x23)));
			
								
			ArrayList<RoutingTable> rtX2 = new ArrayList<RoutingTable>();
			rtX2.add(new RoutingTable(nctx.am.get("ip_b"), x3,nctx.addLatency(y1_y2),ctx.mkAnd(x21,x32)));
			rtX2.add(new RoutingTable(nctx.am.get("ip_b"), x3,nctx.addLatency(0),ctx.mkAnd(x22,x32)));
			rtX2.add(new RoutingTable(nctx.am.get("ip_b"), x3,nctx.addLatency(y2_y3),ctx.mkAnd(x23,x32)));

			rtX2.add(new RoutingTable(nctx.am.get("ip_b"), x3,nctx.addLatency(y1_y3),ctx.mkAnd(x21,x33)));
			rtX2.add(new RoutingTable(nctx.am.get("ip_b"), x3,nctx.addLatency(y2_y3),ctx.mkAnd(x22,x33)));
			rtX2.add(new RoutingTable(nctx.am.get("ip_b"), x3,nctx.addLatency(0),ctx.mkAnd(x23,x33)));

			ArrayList<RoutingTable> rtX3 = new ArrayList<RoutingTable>();
			rtX3.add(new RoutingTable(nctx.am.get("ip_b"), b,nctx.addLatency(y2_b),x32));
			rtX3.add(new RoutingTable(nctx.am.get("ip_b"), b,nctx.addLatency(y3_b),x33));
			
			ArrayList<RoutingTable> rtb = new ArrayList<RoutingTable>();
			
	    	net.routingOptimization(a, rtClient);
	    	net.routingOptimization(x1, rtX1);
	    	net.routingOptimization(x2, rtX2);
	    	net.routingOptimization(x3, rtX3);
	    	net.routingOptimization(b, rtb);
	    	
	    	
	    	net.attach(a, b, x1,x2,x3);
	    	
		    
	    	ArrayList<Tuple<DatatypeExpr,DatatypeExpr>> acl = new ArrayList<Tuple<DatatypeExpr,DatatypeExpr>>();
	    	x1.addAcls(acl);
	        //acl.add(new Tuple<DatatypeExpr,DatatypeExpr>(nctx.am.get("ip_a"),nctx.am.get("ip_b")));
	        
	        x2.installCache(new NetworkObject[]{nctx.nm.get("fw")});
	        ArrayList<DatatypeExpr> ia = new ArrayList<DatatypeExpr>();
		    ia.add(nctx.am.get("ip_a"));
		    x3.natModel(nctx.am.get("ip_x3"));
		    x3.setInternalAddress(ia);
		  
		    check = new Checker(ctx,nctx,net);
	}
	
	public int a_y1 = -1;
	public int a_y2 = -100;
	public int y1_y2= -1;
	public int y1_y3= -100;
	public int y2_y1= -1;
	public int y2_y3= -1;
	public int y3_b= -1;
	public int y2_b= -100;
	
	 private void setConditions(Context ctx, NetContext nctx) {
		  	y1 = ctx.mkBoolConst("y1");
			y2 = ctx.mkBoolConst("y2");
			y3 = ctx.mkBoolConst("y3");
			
			nctx.softConstraints.add(new Tuple<BoolExpr, String>(ctx.mkNot(y1), "servers"));
			nctx.softConstraints.add(new Tuple<BoolExpr, String>(ctx.mkNot(y2), "servers"));
			nctx.softConstraints.add(new Tuple<BoolExpr, String>(ctx.mkNot(y3), "servers"));
			
		  
	    	int capacity_x1 = 10;
	    	int capacity_x2 = 10;
	    	int capacity_x3 = 10;
	    	
	    	int capacity_y1 = 100;
	    	int capacity_y2 = 100;
	    	int capacity_y3 = 100;
	    
	    	
			x11 = ctx.mkBoolConst("x11");
			x12 = ctx.mkBoolConst("x12");
			x21 = ctx.mkBoolConst("x21");
			x22 = ctx.mkBoolConst("x22");
			x23 = ctx.mkBoolConst("x23");
			x32 = ctx.mkBoolConst("x32");
			x33 = ctx.mkBoolConst("x33");

			y1 = ctx.mkBoolConst("y1");
			y2 = ctx.mkBoolConst("y2");
			y3 = ctx.mkBoolConst("y3");

			nctx.constraints.add(ctx.mkEq(ctx.mkAdd(nctx.bool_to_int(x11),nctx.bool_to_int(x12)), ctx.mkInt(1)));
			nctx.constraints.add(ctx.mkEq(ctx.mkAdd(nctx.bool_to_int(x21),nctx.bool_to_int(x22),nctx.bool_to_int(x23)), ctx.mkInt(1)));
			nctx.constraints.add(ctx.mkEq(ctx.mkAdd(nctx.bool_to_int(x32),nctx.bool_to_int(x33)), ctx.mkInt(1)));
			
			nctx.constraints.add(ctx.mkOr(ctx.mkImplies(y1, x11),ctx.mkImplies(y1, x21)));
			nctx.constraints.add(ctx.mkOr(ctx.mkImplies(y2, x12),ctx.mkImplies(y2, x22),ctx.mkImplies(y2, x32)));
			nctx.constraints.add(ctx.mkOr(ctx.mkImplies(y3, x23),ctx.mkImplies(y3, x33)));
			
		
			ArithExpr leftSide = 
				ctx.mkAdd(ctx.mkMul(ctx.mkInt(capacity_x1), nctx.bool_to_int(x11)),
						ctx.mkMul(ctx.mkInt(capacity_x2), nctx.bool_to_int(x21))
						);
			nctx.constraints.add(ctx.mkLe(leftSide, ctx.mkMul(ctx.mkInt(capacity_y1), nctx.bool_to_int(y1))));
			
			leftSide = ctx.mkAdd(ctx.mkMul(ctx.mkInt(capacity_x1), nctx.bool_to_int(x12)),ctx.mkMul(ctx.mkInt(capacity_x2), nctx.bool_to_int(x22)),
					ctx.mkMul(ctx.mkInt(capacity_x3), nctx.bool_to_int(x32)));
			nctx.constraints.add(ctx.mkLe(leftSide, ctx.mkMul(ctx.mkInt(capacity_y2), nctx.bool_to_int(y2))));
			
			leftSide = ctx.mkAdd(
					
					ctx.mkMul(ctx.mkInt(capacity_x2), nctx.bool_to_int(x23)),
					ctx.mkMul(ctx.mkInt(capacity_x3), nctx.bool_to_int(x33)));
			nctx.constraints.add(ctx.mkLe(leftSide, ctx.mkMul(ctx.mkInt(capacity_y3), nctx.bool_to_int(y3))));
			
			nctx.softConstraints.add(new Tuple<BoolExpr, String>(ctx.mkNot(y1), "num_servers"));
			nctx.softConstraints.add(new Tuple<BoolExpr, String>(ctx.mkNot(y2), "num_servers"));
			nctx.softConstraints.add(new Tuple<BoolExpr, String>(ctx.mkNot(y3), "num_servers"));
	    	
	    	
		}
	 
	 public static void main(String[] args) throws Z3Exception
	    {
		 	Polito3nodes3hostsCacheFWNAT model = new Polito3nodes3hostsCacheFWNAT();
	        model.resetZ3();
	        
	        IsolationResult ret =model.check.checkIsolationProperty(model.a,model.b);
	        //model.check.propertyAdd(model.a, model.b, Prop.ISOLATION);
	        //IsolationResult ret= model.check.propertyCheck();
	        if (ret.result == Status.UNSATISFIABLE){
	           System.out.println("UNSAT"); // Nodes a and b are isolated
	        }else{
	            System.out.println("SAT ");
	            System.out.println(ret.model);
//	            System.out.print( "Model -> ");model.printModel(ret.model);
//	          System.out.println( "Violating packet -> " +ret.violating_packet);
//	          System.out.println("Last hop -> " +ret.last_hop);
//	          System.out.println("Last send_time -> " +ret.last_send_time);
//	          System.out.println( "Last recv_time -> " +ret.last_recv_time);
	        }
	    }
}
