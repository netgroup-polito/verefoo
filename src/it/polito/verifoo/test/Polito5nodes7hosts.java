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
import java.util.List;

import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.DatatypeExpr;

import it.polito.verifoo.components.Host;
import it.polito.verifoo.components.Node;
import it.polito.verifoo.components.RoutingTable;
import it.polito.verigraph.mcnet.components.Checker;
import it.polito.verigraph.mcnet.components.Checker.Prop;
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




/**
 * @author Giacomo Costantini
 * <p/>
 * Antispam test													<p/>
 *| CLIENT | --------- | ANTISPAM | --------- | MAIL SERVER |		<p/>
 *..........................|										<p/>
 *...................| ERR FUNCTION |								<p/>
 */
public class Polito5nodes7hosts {
	
	public Checker check;
	public AclFirewall x1;
	public AclFirewall x2;
	public AclFirewall x3;
	public AclFirewall x4;
	public AclFirewall x5;
	public PolitoEndHost a;
	public PolitoEndHost b;
	
	
	
	public  BoolExpr y1;
   	public  BoolExpr y2;
   	public  BoolExpr y3;
   	public  BoolExpr y4;
   	public  BoolExpr y5;
   	public  BoolExpr y6;
   	public  BoolExpr y7;
   	
   	
   	public  BoolExpr x11;
   	
   	public  BoolExpr x22;
   	public  BoolExpr x23;
   	public  BoolExpr x24;
   	public  BoolExpr x21;
   	
   	public  BoolExpr x31;
   	public  BoolExpr x32;
   	public  BoolExpr x33;
   	public  BoolExpr x34;
   	public  BoolExpr x35;
   	public  BoolExpr x36;
   	public  BoolExpr x37;
   	
   	public  BoolExpr x44;
   	public  BoolExpr x45;
   	public  BoolExpr x46;
   	public  BoolExpr x47;
   	
   	public  BoolExpr x57;
	
	public	Polito5nodes7hosts(Context ctx){
	
		
			NetContext nctx = new NetContext (ctx,
					new String[]{"a", "x1","x2","x3","x4","x5", "b"},
					new String[]{"ip_a", "ip_x1", "ip_x2", "ip_x3","ip_x4","ip_x5", "ip_b"});
			Network net = new Network (ctx,new Object[]{nctx});
			setConditions(ctx,nctx);
			
			x1 = new AclFirewall(ctx, new Object[]{nctx.nm.get("x1"), net, nctx});
			x2 = new AclFirewall(ctx, new Object[]{nctx.nm.get("x2"), net, nctx});
			x3 = new AclFirewall(ctx, new Object[]{nctx.nm.get("x3"), net, nctx});
			x4 = new AclFirewall(ctx, new Object[]{nctx.nm.get("x4"), net, nctx});
			x5 = new AclFirewall(ctx, new Object[]{nctx.nm.get("x5"), net, nctx});
			b = new PolitoEndHost(ctx, new Object[]{nctx.nm.get("b"), net, nctx});
			a = new PolitoEndHost(ctx, new Object[]{nctx.nm.get("a"), net, nctx});
		  
			ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>> adm = new ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>>();
			ArrayList<DatatypeExpr> al1 = new ArrayList<DatatypeExpr>();
  			ArrayList<DatatypeExpr> al2 = new ArrayList<DatatypeExpr>();
  			ArrayList<DatatypeExpr> al3 = new ArrayList<DatatypeExpr>();
  			ArrayList<DatatypeExpr> al5 = new ArrayList<DatatypeExpr>();
  			ArrayList<DatatypeExpr> al7 = new ArrayList<DatatypeExpr>();
  			ArrayList<DatatypeExpr> al8 = new ArrayList<DatatypeExpr>();
  			ArrayList<DatatypeExpr> al6 = new ArrayList<DatatypeExpr>();
  			
  			al1.add(nctx.am.get("ip_a"));
  			al2.add(nctx.am.get("ip_x1"));
  			al3.add(nctx.am.get("ip_b"));
  			al5.add(nctx.am.get("ip_x2"));
  			al6.add(nctx.am.get("ip_x3"));
  			al7.add(nctx.am.get("ip_x4"));
  			al8.add(nctx.am.get("ip_x5"));
			adm.add(new Tuple<>(a, al1));
		    adm.add(new Tuple<>(x1, al2));
		    adm.add(new Tuple<>(b, al3));
		    adm.add(new Tuple<>(x2, al5));
		    adm.add(new Tuple<>(x3, al6));
		    adm.add(new Tuple<>(x4, al7));
		    adm.add(new Tuple<>(x5, al8));

		    net.setAddressMappings(adm);

		    //for each Link from Client to Servers
		    ArrayList<RoutingTable> rtClient = new ArrayList<RoutingTable>();
		    //Client sends always to x1 (first middlebox in the graph)
			rtClient.add(new RoutingTable(nctx.am.get("ip_b"), x1, nctx.addLatency(a_y1), x11));
						
			ArrayList<RoutingTable> rtX1 = new ArrayList<RoutingTable>();
			rtX1.add(new RoutingTable(nctx.am.get("ip_b"), x2,nctx.addLatency(0),ctx.mkAnd(x11,x21)));
			rtX1.add(new RoutingTable(nctx.am.get("ip_b"), x2,nctx.addLatency(y1_y2),ctx.mkAnd(x11,x22)));
			rtX1.add(new RoutingTable(nctx.am.get("ip_b"), x2,nctx.addLatency(y1_y3),ctx.mkAnd(x11,x23)));
			rtX1.add(new RoutingTable(nctx.am.get("ip_b"), x2,nctx.addLatency(y1_y4),ctx.mkAnd(x11,x24)));
			
								
			ArrayList<RoutingTable> rtX2 = new ArrayList<RoutingTable>();
			rtX2.add(new RoutingTable(nctx.am.get("ip_b"), x3,nctx.addLatency(0),ctx.mkAnd(x21,x31)));
			rtX2.add(new RoutingTable(nctx.am.get("ip_b"), x3,nctx.addLatency(0),ctx.mkAnd(x22,x32)));
			rtX2.add(new RoutingTable(nctx.am.get("ip_b"), x3,nctx.addLatency(0),ctx.mkAnd(x23,x33)));
			rtX2.add(new RoutingTable(nctx.am.get("ip_b"), x3,nctx.addLatency(0),ctx.mkAnd(x24,x34)));

			rtX2.add(new RoutingTable(nctx.am.get("ip_b"), x3,nctx.addLatency(y1_y2),ctx.mkAnd(x21,x32)));
			rtX2.add(new RoutingTable(nctx.am.get("ip_b"), x3,nctx.addLatency(y2_y3),ctx.mkAnd(x23,x32)));
			rtX2.add(new RoutingTable(nctx.am.get("ip_b"), x3,nctx.addLatency(y2_y4),ctx.mkAnd(x24,x32)));
			
			rtX2.add(new RoutingTable(nctx.am.get("ip_b"), x3,nctx.addLatency(y1_y3),ctx.mkAnd(x21,x33)));
			rtX2.add(new RoutingTable(nctx.am.get("ip_b"), x3,nctx.addLatency(y2_y3),ctx.mkAnd(x22,x33)));
			rtX2.add(new RoutingTable(nctx.am.get("ip_b"), x3,nctx.addLatency(y3_y4),ctx.mkAnd(x24,x33)));
			
			rtX2.add(new RoutingTable(nctx.am.get("ip_b"), x3,nctx.addLatency(y1_y4),ctx.mkAnd(x21,x34)));
			rtX2.add(new RoutingTable(nctx.am.get("ip_b"), x3,nctx.addLatency(y2_y4),ctx.mkAnd(x22,x34)));
			rtX2.add(new RoutingTable(nctx.am.get("ip_b"), x3,nctx.addLatency(y3_y4),ctx.mkAnd(x23,x34)));
			
			rtX2.add(new RoutingTable(nctx.am.get("ip_b"), x3,nctx.addLatency(y3_y5),ctx.mkAnd(x23,x35)));
			rtX2.add(new RoutingTable(nctx.am.get("ip_b"), x3,nctx.addLatency(y4_y5),ctx.mkAnd(x24,x35)));

			rtX2.add(new RoutingTable(nctx.am.get("ip_b"), x3,nctx.addLatency(y2_y6),ctx.mkAnd(x22,x36)));
			rtX2.add(new RoutingTable(nctx.am.get("ip_b"), x3,nctx.addLatency(y4_y6),ctx.mkAnd(x24,x36)));
			
			rtX2.add(new RoutingTable(nctx.am.get("ip_b"), x3,nctx.addLatency(y4_y7),ctx.mkAnd(x24,x37)));



			ArrayList<RoutingTable> rtX3 = new ArrayList<RoutingTable>();
			rtX3.add(new RoutingTable(nctx.am.get("ip_b"), x4,nctx.addLatency(y1_y4),ctx.mkAnd(x31,x44)));
			
			rtX3.add(new RoutingTable(nctx.am.get("ip_b"), x4,nctx.addLatency(y2_y4),ctx.mkAnd(x32,x44)));
			rtX3.add(new RoutingTable(nctx.am.get("ip_b"), x4,nctx.addLatency(y2_y6),ctx.mkAnd(x32,x46)));
			
			rtX3.add(new RoutingTable(nctx.am.get("ip_b"), x4,nctx.addLatency(y3_y4),ctx.mkAnd(x33,x44)));
			rtX3.add(new RoutingTable(nctx.am.get("ip_b"), x4,nctx.addLatency(y3_y5),ctx.mkAnd(x33,x45)));
			
			rtX3.add(new RoutingTable(nctx.am.get("ip_b"), x4,nctx.addLatency(0),ctx.mkAnd(x34,x44)));
			rtX3.add(new RoutingTable(nctx.am.get("ip_b"), x4,nctx.addLatency(y4_y5),ctx.mkAnd(x34,x45)));
			rtX3.add(new RoutingTable(nctx.am.get("ip_b"), x4,nctx.addLatency(y4_y6),ctx.mkAnd(x34,x46)));
			rtX3.add(new RoutingTable(nctx.am.get("ip_b"), x4,nctx.addLatency(y4_y7),ctx.mkAnd(x34,x47)));
			
			rtX3.add(new RoutingTable(nctx.am.get("ip_b"), x4,nctx.addLatency(y4_y5),ctx.mkAnd(x35,x44)));
			rtX3.add(new RoutingTable(nctx.am.get("ip_b"), x4,nctx.addLatency(0),ctx.mkAnd(x35,x45)));
			rtX3.add(new RoutingTable(nctx.am.get("ip_b"), x4,nctx.addLatency(y5_y6),ctx.mkAnd(x35,x46)));
			rtX3.add(new RoutingTable(nctx.am.get("ip_b"), x4,nctx.addLatency(y5_y7),ctx.mkAnd(x35,x47)));
			
			rtX3.add(new RoutingTable(nctx.am.get("ip_b"), x4,nctx.addLatency(y4_y6),ctx.mkAnd(x36,x44)));
			rtX3.add(new RoutingTable(nctx.am.get("ip_b"), x4,nctx.addLatency(y5_y6),ctx.mkAnd(x36,x45)));
			rtX3.add(new RoutingTable(nctx.am.get("ip_b"), x4,nctx.addLatency(0),ctx.mkAnd(x36,x46)));
			rtX3.add(new RoutingTable(nctx.am.get("ip_b"), x4,nctx.addLatency(y6_y7),ctx.mkAnd(x36,x47)));
			
			rtX3.add(new RoutingTable(nctx.am.get("ip_b"), x4,nctx.addLatency(y4_y7),ctx.mkAnd(x37,x44)));
			rtX3.add(new RoutingTable(nctx.am.get("ip_b"), x4,nctx.addLatency(y5_y7),ctx.mkAnd(x37,x45)));
			rtX3.add(new RoutingTable(nctx.am.get("ip_b"), x4,nctx.addLatency(y6_y7),ctx.mkAnd(x37,x46)));
			rtX3.add(new RoutingTable(nctx.am.get("ip_b"), x4,nctx.addLatency(0),ctx.mkAnd(x37,x47)));
			
			
			ArrayList<RoutingTable> rtX4 = new ArrayList<RoutingTable>();
			rtX4.add(new RoutingTable(nctx.am.get("ip_b"), x5,nctx.addLatency(y4_y7),ctx.mkAnd(x44,x57)));
			rtX4.add(new RoutingTable(nctx.am.get("ip_b"), x5,nctx.addLatency(y5_y7),ctx.mkAnd(x45,x57)));
			rtX4.add(new RoutingTable(nctx.am.get("ip_b"), x5,nctx.addLatency(y6_y7),ctx.mkAnd(x46,x57)));
			rtX4.add(new RoutingTable(nctx.am.get("ip_b"), x5,nctx.addLatency(0),ctx.mkAnd(x47,x57)));
			
			
			
			ArrayList<RoutingTable> rtX5 = new ArrayList<RoutingTable>();
			rtX5.add(new RoutingTable(nctx.am.get("ip_b"), b,nctx.addLatency(y7_b),(x57)));
			
			ArrayList<RoutingTable> rtb = new ArrayList<RoutingTable>();
			
	    	net.routingOptimization(a, rtClient);
	    	net.routingOptimization(x1, rtX1);
	    	net.routingOptimization(x2, rtX2);
	    	net.routingOptimization(x3, rtX3);
	    	net.routingOptimization(x4, rtX4);
	    	net.routingOptimization(x5, rtX5);
	    	net.routingOptimization(b, rtb);
	    	
	    	ArrayList<Tuple<DatatypeExpr,DatatypeExpr>> acl = new ArrayList<Tuple<DatatypeExpr,DatatypeExpr>>();
	        x1.addAcls(acl);
	        x2.addAcls(acl);
	        x3.addAcls(acl);
	        acl.add(new Tuple<DatatypeExpr,DatatypeExpr>(nctx.am.get("ip_a"),nctx.am.get("ip_b")));
	        
		    net.attach(a, b, x1,x2,x3,x4,x5);
		    
		  
		    check = new Checker(ctx,nctx,net);
	}
	
	public int a_y1 = -1;

	public int y1_y2= -1;
	public int y1_y3= -1;
	public int y1_y4= -1000;
	
	public int y2_y3= -1;
	public int y2_y4= -1;
	public int y2_y6 =-1;
	
	public int y3_y5 =-1;
	public int y3_y4 =-1;

	public int y4_y5 =-1;
	public int y4_y6 =-1;
	public int y4_y7 =-1;

	public int y5_y6 =-1;
	public int y5_y7 =-1;

	public int y6_y7 =-1;
	
	public int y7_b= -1;
	

	 private void setConditions(Context ctx, NetContext nctx) {
		  	y1 = ctx.mkBoolConst("y1");
			y2 = ctx.mkBoolConst("y2");
			y3 = ctx.mkBoolConst("y3");
			y4 = ctx.mkBoolConst("y4");
			y5 = ctx.mkBoolConst("y5");
			y6 = ctx.mkBoolConst("y6");
			y7 = ctx.mkBoolConst("y7");
			
			nctx.softConstraints.add(new Tuple<BoolExpr, String>(ctx.mkNot(y1), "servers"));
			nctx.softConstraints.add(new Tuple<BoolExpr, String>(ctx.mkNot(y2), "servers"));
			nctx.softConstraints.add(new Tuple<BoolExpr, String>(ctx.mkNot(y3), "servers"));
			nctx.softConstraints.add(new Tuple<BoolExpr, String>(ctx.mkNot(y4), "servers"));
			nctx.softConstraints.add(new Tuple<BoolExpr, String>(ctx.mkNot(y5), "servers"));
			nctx.softConstraints.add(new Tuple<BoolExpr, String>(ctx.mkNot(y6), "servers"));
			nctx.softConstraints.add(new Tuple<BoolExpr, String>(ctx.mkNot(y7), "servers"));
			
		  
	    	int capacity_x1 = 10;
	    	int capacity_x2 = 10;
	    	int capacity_x3 = 10;
	    	int capacity_x4 = 10;
	    	int capacity_x5 = 10;
	    	
	    	int capacity_y1 = 100;
	    	int capacity_y2 = 100;
	    	int capacity_y3 = 100;
	    	int capacity_y4 = 100;
	    	int capacity_y5 = 100;
	    	int capacity_y6 = 100;
	    	int capacity_y7 = 100;
	    
	    	
			x11 = ctx.mkBoolConst("x11");

			x21 = ctx.mkBoolConst("x21");
			x22 = ctx.mkBoolConst("x22");
			x23 = ctx.mkBoolConst("x23");
			x24 = ctx.mkBoolConst("x24");
			
			x31 = ctx.mkBoolConst("x31");
			x32 = ctx.mkBoolConst("x32");
			x33 = ctx.mkBoolConst("x33");
			x34 = ctx.mkBoolConst("x34");
			x35 = ctx.mkBoolConst("x35");
			x36 = ctx.mkBoolConst("x36");
			x37 = ctx.mkBoolConst("x37");
			
			x44 = ctx.mkBoolConst("x44");
			x45 = ctx.mkBoolConst("x45");
			x46 = ctx.mkBoolConst("x46");
			x47 = ctx.mkBoolConst("x47");

			x57 = ctx.mkBoolConst("x57");

			y1 = ctx.mkBoolConst("y1");
			y2 = ctx.mkBoolConst("y2");
			y3 = ctx.mkBoolConst("y3");
			y4 = ctx.mkBoolConst("y4");
			y5 = ctx.mkBoolConst("y5");
			y6 = ctx.mkBoolConst("y6");
			y7 = ctx.mkBoolConst("y7");

			nctx.constraints.add(ctx.mkEq((
						nctx.bool_to_int(x11)
						), 
					ctx.mkInt(1)));
			nctx.constraints.add(ctx.mkEq(ctx.mkAdd(
					nctx.bool_to_int(x21),
					nctx.bool_to_int(x22),
					nctx.bool_to_int(x23),
					nctx.bool_to_int(x24)
					), 
			ctx.mkInt(1)));
			
			nctx.constraints.add(ctx.mkEq(ctx.mkAdd(
					nctx.bool_to_int(x31),
					nctx.bool_to_int(x32),
					nctx.bool_to_int(x33),
					nctx.bool_to_int(x34),
					nctx.bool_to_int(x35),
					nctx.bool_to_int(x36),
					nctx.bool_to_int(x37)
					), 
				ctx.mkInt(1)));
			
			nctx.constraints.add(ctx.mkEq(ctx.mkAdd(
					nctx.bool_to_int(x44),
					nctx.bool_to_int(x45),
					nctx.bool_to_int(x46),
					nctx.bool_to_int(x47)
					), 
				ctx.mkInt(1)));
			nctx.constraints.add(ctx.mkEq((
					nctx.bool_to_int(x57)
					), 
				ctx.mkInt(1)));
			
			nctx.constraints.add(ctx.mkOr(
					ctx.mkImplies(y1, x11),
					ctx.mkImplies(y1, x21),
					ctx.mkImplies(y1, x31)
					));
			
			nctx.constraints.add(ctx.mkOr(
					ctx.mkImplies(y2, x22),
					ctx.mkImplies(y2, x32)
					));
			
			nctx.constraints.add(ctx.mkOr(
					ctx.mkImplies(y3, x23),
					ctx.mkImplies(y3, x33)));
			
			nctx.constraints.add(ctx.mkOr(
					ctx.mkImplies(y4, x24),
					ctx.mkImplies(y4, x34),
					ctx.mkImplies(y4, x44)));
			
			nctx.constraints.add(ctx.mkOr(
					ctx.mkImplies(y5, x35),
					ctx.mkImplies(y5, x45)
					));
			
			
			nctx.constraints.add(ctx.mkOr(
					ctx.mkImplies(y6, x46),
					ctx.mkImplies(y6, x36)));
			
			
			nctx.constraints.add(ctx.mkOr(
					ctx.mkImplies(y7, x37),
					ctx.mkImplies(y7, x47),
					ctx.mkImplies(y7, x57)));
			
		
			ArithExpr leftSide = 
				ctx.mkAdd(
						ctx.mkMul(ctx.mkInt(capacity_x1), nctx.bool_to_int(x11)),
						ctx.mkMul(ctx.mkInt(capacity_x2), nctx.bool_to_int(x21)),
						ctx.mkMul(ctx.mkInt(capacity_x3), nctx.bool_to_int(x31))
						);
			nctx.constraints.add(ctx.mkLe(leftSide, ctx.mkMul(ctx.mkInt(capacity_y1), nctx.bool_to_int(y1))));
			
			leftSide = ctx.mkAdd(
					ctx.mkMul(ctx.mkInt(capacity_x2), nctx.bool_to_int(x22)),
					ctx.mkMul(ctx.mkInt(capacity_x3), nctx.bool_to_int(x32)));
			nctx.constraints.add(ctx.mkLe(leftSide, ctx.mkMul(ctx.mkInt(capacity_y2), nctx.bool_to_int(y2))));
			
			leftSide = ctx.mkAdd(
					ctx.mkMul(ctx.mkInt(capacity_x2), nctx.bool_to_int(x23)),
					ctx.mkMul(ctx.mkInt(capacity_x3), nctx.bool_to_int(x33))
					);
			nctx.constraints.add(ctx.mkLe(leftSide, ctx.mkMul(ctx.mkInt(capacity_y3), nctx.bool_to_int(y3))));
			
			leftSide = ctx.mkAdd(
					ctx.mkMul(ctx.mkInt(capacity_x2), nctx.bool_to_int(x24)),
					ctx.mkMul(ctx.mkInt(capacity_x3), nctx.bool_to_int(x34)),
					ctx.mkMul(ctx.mkInt(capacity_x4), nctx.bool_to_int(x44))
					);
			nctx.constraints.add(ctx.mkLe(leftSide, ctx.mkMul(ctx.mkInt(capacity_y4), nctx.bool_to_int(y4))));
			
			leftSide = ctx.mkAdd(
					ctx.mkMul(ctx.mkInt(capacity_x3), nctx.bool_to_int(x35)),
					ctx.mkMul(ctx.mkInt(capacity_x4), nctx.bool_to_int(x45))
					);
			nctx.constraints.add(ctx.mkLe(leftSide, ctx.mkMul(ctx.mkInt(capacity_y5), nctx.bool_to_int(y5))));
			
			leftSide = ctx.mkAdd(
					ctx.mkMul(ctx.mkInt(capacity_x3), nctx.bool_to_int(x36)),
					ctx.mkMul(ctx.mkInt(capacity_x4), nctx.bool_to_int(x46))
					);
			nctx.constraints.add(ctx.mkLe(leftSide, ctx.mkMul(ctx.mkInt(capacity_y6), nctx.bool_to_int(y6))));
			
			leftSide = ctx.mkAdd(
					ctx.mkMul(ctx.mkInt(capacity_x3), nctx.bool_to_int(x37)),
					ctx.mkMul(ctx.mkInt(capacity_x4), nctx.bool_to_int(x47)),
					ctx.mkMul(ctx.mkInt(capacity_x5), nctx.bool_to_int(x57))
					);
			nctx.constraints.add(ctx.mkLe(leftSide, ctx.mkMul(ctx.mkInt(capacity_y7), nctx.bool_to_int(y7))));
			
			nctx.softConstraints.add(new Tuple<BoolExpr, String>(ctx.mkNot(y1), "num_servers"));
			nctx.softConstraints.add(new Tuple<BoolExpr, String>(ctx.mkNot(y2), "num_servers"));
			nctx.softConstraints.add(new Tuple<BoolExpr, String>(ctx.mkNot(y3), "num_servers"));
			nctx.softConstraints.add(new Tuple<BoolExpr, String>(ctx.mkNot(y4), "num_servers"));
			nctx.softConstraints.add(new Tuple<BoolExpr, String>(ctx.mkNot(y5), "num_servers"));
			nctx.softConstraints.add(new Tuple<BoolExpr, String>(ctx.mkNot(y6), "num_servers"));
			nctx.softConstraints.add(new Tuple<BoolExpr, String>(ctx.mkNot(y7), "num_servers"));
	    	
	    	
		}
}
