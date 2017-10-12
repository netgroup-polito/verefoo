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
package it.polito.verigraph.scalability.tests;


import java.util.ArrayList;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.DatatypeExpr;

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




/**
 * @author Giacomo Costantini
 * <p/>
 * Antispam test													<p/>
 *| CLIENT | --------- | ANTISPAM | --------- | MAIL SERVER |		<p/>
 *..........................|										<p/>
 *...................| ERR FUNCTION |								<p/>
 */
public class PolitoMultipathTest {
	
	public Checker check;
	public AclFirewall fw;
	public PolitoCache x1;
	public PolitoCache x2;
	public PolitoCache x3;
	public PolitoEndHost a;
	public PolitoEndHost b;
	public Classifier classifier;
	
	public int classifier_y1 = 100;
	public int classifier_y2 = 5;
	public int a_classifier = 10;
	public int y1_y2= 30;
	public int y1_b= 40;
	public int y2_b= 50;
	
	
	public	PolitoMultipathTest(Context ctx){
	
			NetContext nctx = new NetContext (ctx,
					new String[]{"a", "x1","x2","x3", "b", "classifier"},
					new String[]{"ip_a", "ip_x1", "ip_x2", "ip_x3", "ip_b", "ip_classifier"});
			
			Network net = new Network (ctx,new Object[]{nctx});
			
			
			x1 = new PolitoCache(ctx, new Object[]{nctx.nm.get("x1"), net, nctx});
			x2 = new PolitoCache(ctx, new Object[]{nctx.nm.get("x2"), net, nctx});
			x3 = new PolitoCache(ctx, new Object[]{nctx.nm.get("x3"), net, nctx});
			b = new PolitoEndHost(ctx, new Object[]{nctx.nm.get("b"), net, nctx});
			classifier = new Classifier(ctx, new Object[]{nctx.nm.get("classifier"), net, nctx});
			a = new PolitoEndHost(ctx, new Object[]{nctx.nm.get("a"), net, nctx,b});
		  
			ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>> adm = new ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>>();
			ArrayList<DatatypeExpr> al1 = new ArrayList<DatatypeExpr>();
  			ArrayList<DatatypeExpr> al2 = new ArrayList<DatatypeExpr>();
  			ArrayList<DatatypeExpr> al3 = new ArrayList<DatatypeExpr>();
  			ArrayList<DatatypeExpr> al4 = new ArrayList<DatatypeExpr>();
  			ArrayList<DatatypeExpr> al5 = new ArrayList<DatatypeExpr>();
  			ArrayList<DatatypeExpr> al6 = new ArrayList<DatatypeExpr>();
  			
  			al1.add(nctx.am.get("ip_a"));
  			al2.add(nctx.am.get("ip_x1"));
  			al3.add(nctx.am.get("ip_b"));
  			al4.add(nctx.am.get("ip_classifier"));
  			al5.add(nctx.am.get("ip_x2"));
  			al6.add(nctx.am.get("ip_x3"));
			adm.add(new Tuple<>(a, al1));
		    adm.add(new Tuple<>(x1, al2));
		    adm.add(new Tuple<>(b, al3));
		    adm.add(new Tuple<>(classifier, al4));
		    adm.add(new Tuple<>(x2, al5));
		    adm.add(new Tuple<>(x3, al6));

		    net.setAddressMappings(adm);
		
		    
			ArrayList<Quattro<DatatypeExpr,NetworkObject,Integer,BoolExpr>> rtClassifier = new ArrayList<Quattro<DatatypeExpr,NetworkObject,Integer,BoolExpr>>();
			rtClassifier.add(new Quattro<>(nctx.am.get("ip_b"), x1,classifier_y1,nctx.x11));
			rtClassifier.add(new Quattro<>(nctx.am.get("ip_b"), x1,classifier_y2,nctx.x12));
			
			rtClassifier.add(new Quattro<>(nctx.am.get("ip_x1"), x1,classifier_y1,nctx.x11));
			rtClassifier.add(new Quattro<>(nctx.am.get("ip_x1"), x1,classifier_y2,nctx.x12));
			
			rtClassifier.add(new Quattro<>(nctx.am.get("ip_x2"), x1,classifier_y1,nctx.x11));
			rtClassifier.add(new Quattro<>(nctx.am.get("ip_x2"), x1,classifier_y2,nctx.x12));
			
			rtClassifier.add(new Quattro<>(nctx.am.get("ip_x3"), x1,classifier_y1,nctx.x11));
			rtClassifier.add(new Quattro<>(nctx.am.get("ip_x3"), x1,classifier_y2,nctx.x12));
			
			rtClassifier.add(new Quattro<>(nctx.am.get("ip_a"), a,a_classifier,nctx.ture));
			
			ArrayList<Quattro<DatatypeExpr,NetworkObject,Integer,BoolExpr>> rtClient = new ArrayList<Quattro<DatatypeExpr,NetworkObject,Integer,BoolExpr>>();
			rtClient.add(new Quattro<>(nctx.am.get("ip_b"), classifier,a_classifier, nctx.ture));
			rtClient.add(new Quattro<>(nctx.am.get("ip_x1"), classifier,a_classifier, nctx.ture));
			rtClient.add(new Quattro<>(nctx.am.get("ip_x2"), classifier,a_classifier, nctx.ture));
			rtClient.add(new Quattro<>(nctx.am.get("ip_x3"), classifier,a_classifier, nctx.ture));
			rtClient.add(new Quattro<>(nctx.am.get("ip_classifier"), classifier,a_classifier, nctx.ture));
			
			ArrayList<Quattro<DatatypeExpr,NetworkObject,Integer,BoolExpr>> rtX1 = new ArrayList<Quattro<DatatypeExpr,NetworkObject,Integer,BoolExpr>>();
			rtX1.add(new Quattro<>(nctx.am.get("ip_b"), x2,0,ctx.mkAnd(nctx.x11,nctx.x21)));
			rtX1.add(new Quattro<>(nctx.am.get("ip_b"), x2,y1_y2,ctx.mkAnd(nctx.x11,nctx.x22)));
			rtX1.add(new Quattro<>(nctx.am.get("ip_b"), x2,y1_y2,ctx.mkAnd(nctx.x12,nctx.x21)));
			rtX1.add(new Quattro<>(nctx.am.get("ip_b"), x2,0,ctx.mkAnd(nctx.x12,nctx.x22)));
			
			rtX1.add(new Quattro<>(nctx.am.get("ip_x2"), x2,0,ctx.mkAnd(nctx.x11,nctx.x21)));
			rtX1.add(new Quattro<>(nctx.am.get("ip_x2"), x2,y1_y2,ctx.mkAnd(nctx.x11,nctx.x22)));
			rtX1.add(new Quattro<>(nctx.am.get("ip_x2"), x2,y1_y2,ctx.mkAnd(nctx.x12,nctx.x21)));
			rtX1.add(new Quattro<>(nctx.am.get("ip_x2"), x2,0,ctx.mkAnd(nctx.x12,nctx.x22)));
			
			rtX1.add(new Quattro<>(nctx.am.get("ip_x3"), x2,0,ctx.mkAnd(nctx.x11,nctx.x21)));
			rtX1.add(new Quattro<>(nctx.am.get("ip_x3"), x2,y1_y2,ctx.mkAnd(nctx.x11,nctx.x22)));
			rtX1.add(new Quattro<>(nctx.am.get("ip_x3"), x2,y1_y2,ctx.mkAnd(nctx.x12,nctx.x21)));
			rtX1.add(new Quattro<>(nctx.am.get("ip_x3"), x2,0,ctx.mkAnd(nctx.x12,nctx.x22)));
			
			rtX1.add(new Quattro<>(nctx.am.get("ip_a"), classifier,classifier_y1,nctx.x11));
			rtX1.add(new Quattro<>(nctx.am.get("ip_a"), classifier,classifier_y2,nctx.x12));
			rtX1.add(new Quattro<>(nctx.am.get("ip_classifier"), classifier,classifier_y1,nctx.x11));
			rtX1.add(new Quattro<>(nctx.am.get("ip_classifier"), classifier,classifier_y2,nctx.x12));
			
			
			ArrayList<Quattro<DatatypeExpr,NetworkObject,Integer,BoolExpr>> rtX2 = new ArrayList<Quattro<DatatypeExpr,NetworkObject,Integer,BoolExpr>>();
			rtX2.add(new Quattro<>(nctx.am.get("ip_b"), x3,0,ctx.mkAnd(nctx.x21,nctx.x31)));
			rtX2.add(new Quattro<>(nctx.am.get("ip_b"), x3,y1_y2,ctx.mkAnd(nctx.x21,nctx.x32)));
			rtX2.add(new Quattro<>(nctx.am.get("ip_b"), x3,y1_y2,ctx.mkAnd(nctx.x22,nctx.x31)));
			rtX2.add(new Quattro<>(nctx.am.get("ip_b"), x3,0,ctx.mkAnd(nctx.x22,nctx.x32)));
			
			rtX2.add(new Quattro<>(nctx.am.get("ip_x3"), x3,0,ctx.mkAnd(nctx.x21,nctx.x31)));
			rtX2.add(new Quattro<>(nctx.am.get("ip_x3"), x3,y1_y2,ctx.mkAnd(nctx.x21,nctx.x32)));
			rtX2.add(new Quattro<>(nctx.am.get("ip_x3"), x3,y1_y2,ctx.mkAnd(nctx.x22,nctx.x31)));
			rtX2.add(new Quattro<>(nctx.am.get("ip_x3"), x3,0,ctx.mkAnd(nctx.x22,nctx.x32)));
			
			rtX2.add(new Quattro<>(nctx.am.get("ip_a"), x1,0,ctx.mkAnd(nctx.x21,nctx.x11)));
			rtX2.add(new Quattro<>(nctx.am.get("ip_a"), x1,y1_y2,ctx.mkAnd(nctx.x21,nctx.x12)));
			rtX2.add(new Quattro<>(nctx.am.get("ip_a"), x1,y1_y2,ctx.mkAnd(nctx.x22,nctx.x11)));
			rtX2.add(new Quattro<>(nctx.am.get("ip_a"), x1,0,ctx.mkAnd(nctx.x22,nctx.x12)));
			
			rtX2.add(new Quattro<>(nctx.am.get("ip_classifier"), x1,0,ctx.mkAnd(nctx.x21,nctx.x11)));
			rtX2.add(new Quattro<>(nctx.am.get("ip_classifier"), x1,y1_y2,ctx.mkAnd(nctx.x21,nctx.x12)));
			rtX2.add(new Quattro<>(nctx.am.get("ip_classifier"), x1,y1_y2,ctx.mkAnd(nctx.x22,nctx.x11)));
			rtX2.add(new Quattro<>(nctx.am.get("ip_classifier"), x1,0,ctx.mkAnd(nctx.x22,nctx.x12)));
			
			rtX2.add(new Quattro<>(nctx.am.get("ip_x1"), x1,0,ctx.mkAnd(nctx.x21,nctx.x11)));
			rtX2.add(new Quattro<>(nctx.am.get("ip_x1"), x1,y1_y2,ctx.mkAnd(nctx.x21,nctx.x12)));
			rtX2.add(new Quattro<>(nctx.am.get("ip_x1"), x1,y1_y2,ctx.mkAnd(nctx.x22,nctx.x11)));
			rtX2.add(new Quattro<>(nctx.am.get("ip_x1"), x1,0,ctx.mkAnd(nctx.x22,nctx.x12)));
			
			ArrayList<Quattro<DatatypeExpr,NetworkObject,Integer,BoolExpr>> rtX3 = new ArrayList<Quattro<DatatypeExpr,NetworkObject,Integer,BoolExpr>>();
			rtX3.add(new Quattro<>(nctx.am.get("ip_b"), b,y1_b,nctx.x31));
			rtX3.add(new Quattro<>(nctx.am.get("ip_b"), b,y2_b,nctx.x32));
			
			rtX3.add(new Quattro<>(nctx.am.get("ip_x2"), x2,y1_y2,ctx.mkAnd(nctx.x21,nctx.x32)));
			rtX3.add(new Quattro<>(nctx.am.get("ip_x2"), x2,y1_y2,ctx.mkAnd(nctx.x22,nctx.x31)));
			rtX3.add(new Quattro<>(nctx.am.get("ip_x2"), x2,0,ctx.mkAnd(nctx.x22,nctx.x32)));
			rtX3.add(new Quattro<>(nctx.am.get("ip_x2"), x2,0,ctx.mkAnd(nctx.x21,nctx.x31)));
			
			rtX3.add(new Quattro<>(nctx.am.get("ip_x1"), x2,y1_y2,ctx.mkAnd(nctx.x21,nctx.x32)));
			rtX3.add(new Quattro<>(nctx.am.get("ip_x1"), x2,y1_y2,ctx.mkAnd(nctx.x22,nctx.x31)));
			rtX3.add(new Quattro<>(nctx.am.get("ip_x1"), x2,0,ctx.mkAnd(nctx.x22,nctx.x32)));
			rtX3.add(new Quattro<>(nctx.am.get("ip_x1"), x2,0,ctx.mkAnd(nctx.x21,nctx.x31)));
			
			rtX3.add(new Quattro<>(nctx.am.get("ip_classifier"), x2,y1_y2,ctx.mkAnd(nctx.x21,nctx.x32)));
			rtX3.add(new Quattro<>(nctx.am.get("ip_classifier"), x2,y1_y2,ctx.mkAnd(nctx.x22,nctx.x31)));
			rtX3.add(new Quattro<>(nctx.am.get("ip_classifier"), x2,0,ctx.mkAnd(nctx.x22,nctx.x32)));
			rtX3.add(new Quattro<>(nctx.am.get("ip_classifier"), x2,0,ctx.mkAnd(nctx.x21,nctx.x31)));
			
			rtX3.add(new Quattro<>(nctx.am.get("ip_a"), x2,y1_y2,ctx.mkAnd(nctx.x21,nctx.x32)));
			rtX3.add(new Quattro<>(nctx.am.get("ip_a"), x2,y1_y2,ctx.mkAnd(nctx.x22,nctx.x31)));
			rtX3.add(new Quattro<>(nctx.am.get("ip_a"), x2,0,ctx.mkAnd(nctx.x22,nctx.x32)));
			rtX3.add(new Quattro<>(nctx.am.get("ip_a"), x2,0,ctx.mkAnd(nctx.x21,nctx.x31)));
			
			ArrayList<Quattro<DatatypeExpr,NetworkObject,Integer,BoolExpr>> rtb = new ArrayList<Quattro<DatatypeExpr,NetworkObject,Integer,BoolExpr>>();
			rtb.add(new Quattro<>(nctx.am.get("ip_b"), x3,y1_b,nctx.x31));
			rtb.add(new Quattro<>(nctx.am.get("ip_b"), x3,y2_b,nctx.x32));
			
			rtb.add(new Quattro<>(nctx.am.get("ip_x1"), x3,y1_b,nctx.x31));
			rtb.add(new Quattro<>(nctx.am.get("ip_x1"), x3,y2_b,nctx.x32));
			
			rtb.add(new Quattro<>(nctx.am.get("ip_x2"), x3,y1_b,nctx.x31));
			rtb.add(new Quattro<>(nctx.am.get("ip_x2"), x3,y2_b,nctx.x32));
			
			rtb.add(new Quattro<>(nctx.am.get("ip_x3"), x3,y1_b,nctx.x31));
			rtb.add(new Quattro<>(nctx.am.get("ip_x3"), x3,y2_b,nctx.x32));
			
			rtb.add(new Quattro<>(nctx.am.get("ip_classifier"), x3,y1_b,nctx.x31));
			rtb.add(new Quattro<>(nctx.am.get("ip_classifier"), x3,y2_b,nctx.x32));
			

	    	net.routingTable2(classifier, rtClassifier);
	    	net.routingTable2(a, rtClient);
	    	net.routingTable2(x1, rtX1);
	    	net.routingTable2(x2, rtX2);
	    	net.routingTable2(x3, rtX3);
	    	net.routingTable2(b, rtb);
	    	
		    
		    net.attach(a, b, classifier,x1,x2,x3);
		    
		  //Configuring middleboxes
	        x1.installCache(new NetworkObject[]{nctx.nm.get("classifier")});
	        x2.installCache(new NetworkObject[]{nctx.nm.get("x1")});
	        x3.installCache(new NetworkObject[]{nctx.nm.get("x2")});
		    
		    //x1.installAntispam(new int[]{1});
		    //x2.installAntispam(new int[]{1});
		    //x3.installAntispam(new int[]{1});
		    
		    //System.out.println(net.EndHosts());
		    check = new Checker(ctx,nctx,net);
	}
}
