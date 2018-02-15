package it.polito.verifoo.test;


import java.util.ArrayList;

import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.DatatypeExpr;

import it.polito.verifoo.components.RoutingTable;
import it.polito.verigraph.mcnet.components.Checker;
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


/*

  |nodeA|_______|node1|_______|node2|_______|nodeB|
			   	 |			       |
		   	     |			       |
		     |node3|___|node4|___|node5|

*/


public class Polito5nodes1hostSG {
	
	public Checker check;
	public AclFirewall x1;
	public AclFirewall x2;
	public AclFirewall x3;
	public AclFirewall x4;
	public AclFirewall x5;
	public PolitoEndHost a;
	public PolitoEndHost b;
	
	
	
	public  BoolExpr y1;
   	
   	
   	public  BoolExpr x11;
   	public  BoolExpr x21;
   	public  BoolExpr x31;
   	public  BoolExpr x41;
   	public  BoolExpr x51;
	
	public	Polito5nodes1hostSG(Context ctx){
	
		
			NetContext nctx = new NetContext (ctx,
					new String[]{"a", "x1","x2", "x3", "x4", "x5", "b"},
					new String[]{"ip_a", "ip_x1", "ip_x2", "ip_x3", "ip_x4", "ip_x5", "ip_b"});
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
			ArrayList<DatatypeExpr> alA = new ArrayList<DatatypeExpr>();
  			ArrayList<DatatypeExpr> alB = new ArrayList<DatatypeExpr>();
  			ArrayList<DatatypeExpr> al1 = new ArrayList<DatatypeExpr>();
  			ArrayList<DatatypeExpr> al2 = new ArrayList<DatatypeExpr>();
  			ArrayList<DatatypeExpr> al3 = new ArrayList<DatatypeExpr>();
  			ArrayList<DatatypeExpr> al4 = new ArrayList<DatatypeExpr>();
  			ArrayList<DatatypeExpr> al5 = new ArrayList<DatatypeExpr>();
  			
  			alA.add(nctx.am.get("ip_a"));
  			alB.add(nctx.am.get("ip_b"));
  			al1.add(nctx.am.get("ip_x1"));
  			al2.add(nctx.am.get("ip_x2"));
  			al3.add(nctx.am.get("ip_x3"));
  			al4.add(nctx.am.get("ip_x4"));
  			al5.add(nctx.am.get("ip_x5"));
			adm.add(new Tuple<>(a, alA));
		    adm.add(new Tuple<>(b, alB));
		    adm.add(new Tuple<>(x1, al1));
		    adm.add(new Tuple<>(x2, al2));
		    adm.add(new Tuple<>(x3, al3));
		    adm.add(new Tuple<>(x4, al4));
		    adm.add(new Tuple<>(x5, al5));
		    net.setAddressMappings(adm);

		    //for each Link from Client to Servers
		    ArrayList<RoutingTable> rtClient = new ArrayList<RoutingTable>();
		    //Client sends always to x1 (first middlebox in the graph)
			rtClient.add(new RoutingTable(nctx.am.get("ip_b"), x1, nctx.addLatency(a_y1), x11));
						
			ArrayList<RoutingTable> rtX1 = new ArrayList<RoutingTable>();
			rtX1.add(new RoutingTable(nctx.am.get("ip_b"), x2,nctx.addLatency(0),ctx.mkAnd(x11,x21)));
			rtX1.add(new RoutingTable(nctx.am.get("ip_b"), x3,nctx.addLatency(0),ctx.mkAnd(x11,x31)));
			
								
			ArrayList<RoutingTable> rtX2 = new ArrayList<RoutingTable>();
			rtX2.add(new RoutingTable(nctx.am.get("ip_b"), b,nctx.addLatency(y1_b),x21));

			ArrayList<RoutingTable> rtX3 = new ArrayList<RoutingTable>();
			rtX3.add(new RoutingTable(nctx.am.get("ip_b"), x4,nctx.addLatency(0),ctx.mkAnd(x31,x41)));
			
			ArrayList<RoutingTable> rtX4 = new ArrayList<RoutingTable>();
			rtX4.add(new RoutingTable(nctx.am.get("ip_b"), x5,nctx.addLatency(0),ctx.mkAnd(x41,x51)));
			
			ArrayList<RoutingTable> rtX5 = new ArrayList<RoutingTable>();
			rtX5.add(new RoutingTable(nctx.am.get("ip_b"), x2,nctx.addLatency(0),ctx.mkAnd(x51,x21)));
			
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
	        x4.addAcls(acl);
	        x5.addAcls(acl);
	        acl.add(new Tuple<DatatypeExpr,DatatypeExpr>(nctx.am.get("ip_c"),nctx.am.get("ip_d")));
	        
		    net.attach(a, b, x1,x2,x3,x4,x5);
		    
		  
		    check = new Checker(ctx,nctx,net);
	}
	
	public int a_y1 = -1;
	public int y1_b= -100;
	
	 private void setConditions(Context ctx, NetContext nctx) {
		  	y1 = ctx.mkBoolConst("y1");
			
			nctx.softConstraints.add(new Tuple<BoolExpr, String>(ctx.mkNot(y1), "servers"));
			
		  
	    	int capacity_x1 = 10;
	    	int capacity_x2 = 10;
	    	int capacity_x3 = 10;
	    	int capacity_x4 = 10;
	    	int capacity_x5 = 10;
	    	
	    	int capacity_y1 = 1000;
	    
	    	
			x11 = ctx.mkBoolConst("x11");
			x21 = ctx.mkBoolConst("x21");
			x31 = ctx.mkBoolConst("x31");
			x41 = ctx.mkBoolConst("x41");
			x51 = ctx.mkBoolConst("x51");

			y1 = ctx.mkBoolConst("y1");

			nctx.constraints.add(ctx.mkEq(nctx.bool_to_int(x11), ctx.mkInt(1)));
			nctx.constraints.add(ctx.mkEq(nctx.bool_to_int(x21), ctx.mkInt(1)));
			nctx.constraints.add(ctx.mkEq(nctx.bool_to_int(x31), ctx.mkInt(1)));
			nctx.constraints.add(ctx.mkEq(nctx.bool_to_int(x41), ctx.mkInt(1)));
			nctx.constraints.add(ctx.mkEq(nctx.bool_to_int(x51), ctx.mkInt(1)));
			
			nctx.constraints.add(ctx.mkOr(ctx.mkImplies(y1, x11),ctx.mkImplies(y1, x21),ctx.mkImplies(y1, x31),ctx.mkImplies(y1, x41),ctx.mkImplies(y1, x51)));
			
		
			ArithExpr leftSide = 
				ctx.mkAdd(ctx.mkMul(ctx.mkInt(capacity_x1), nctx.bool_to_int(x11)),
						ctx.mkMul(ctx.mkInt(capacity_x2), nctx.bool_to_int(x21)),
						ctx.mkMul(ctx.mkInt(capacity_x3), nctx.bool_to_int(x31)),
						ctx.mkMul(ctx.mkInt(capacity_x4), nctx.bool_to_int(x41)),
						ctx.mkMul(ctx.mkInt(capacity_x5), nctx.bool_to_int(x51))
						);
			nctx.constraints.add(ctx.mkLe(leftSide, ctx.mkMul(ctx.mkInt(capacity_y1), nctx.bool_to_int(y1))));
			nctx.softConstraints.add(new Tuple<BoolExpr, String>(ctx.mkNot(y1), "num_servers"));
	    	
	    	
		}
}
