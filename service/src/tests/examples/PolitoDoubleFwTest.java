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
package tests.examples;


import java.util.ArrayList;

import com.microsoft.z3.Context;
import com.microsoft.z3.DatatypeExpr;

import mcnet.components.Checker;
import mcnet.components.NetContext;
import mcnet.components.Network;
import mcnet.components.NetworkObject;
import mcnet.components.Tuple;
import mcnet.netobjs.EndHost;
import mcnet.netobjs.PolitoNF;
/**
 * 
 * @author Giacomo Costantini
 * 
 * <p/>
 * Custom test										<p/>
 *    | A | ----| FW1 |----- | FW2 |----- | C |		<p/>
 *    .............|								<p/>
 *    | B | -------									<p/>
 *
 */
public class PolitoDoubleFwTest {
	
	public Checker check;
	public EndHost a,b,c;
	public PolitoNF fw1,fw2;

	public	PolitoDoubleFwTest(Context ctx){
		NetContext nctx = new NetContext (ctx,new String[]{"a", "b", "c", "fw1","fw2"},
												new String[]{"ip_a", "ip_b", "ip_c", "ip_fw1","ip_fw2"});
		Network net = new Network (ctx,new Object[]{nctx});
		
		a = new EndHost(ctx, new Object[]{nctx.nm.get("a"), net, nctx});
		b = new EndHost(ctx, new Object[]{nctx.nm.get("b"), net, nctx});
		c = new EndHost(ctx, new Object[]{nctx.nm.get("c"), net, nctx});
	    fw1 = new PolitoNF(ctx, new Object[]{nctx.nm.get("fw1"), net, nctx});
	    fw2 = new PolitoNF(ctx, new Object[]{nctx.nm.get("fw2"), net, nctx});
	    
	    ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>> adm = new ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>>();
		ArrayList<DatatypeExpr> al1 = new ArrayList<DatatypeExpr>();
		ArrayList<DatatypeExpr> al2 = new ArrayList<DatatypeExpr>();
		ArrayList<DatatypeExpr> al3 = new ArrayList<DatatypeExpr>();
		ArrayList<DatatypeExpr> al4 = new ArrayList<DatatypeExpr>();
		ArrayList<DatatypeExpr> al5 = new ArrayList<DatatypeExpr>();
		al1.add(nctx.am.get("ip_a"));
		al2.add(nctx.am.get("ip_b"));
		al3.add(nctx.am.get("ip_c"));
		al4.add(nctx.am.get("ip_fw1"));
		al5.add(nctx.am.get("ip_fw2"));
		adm.add(new Tuple<>(a, al1));
	    adm.add(new Tuple<>(b, al2));
	    adm.add(new Tuple<>(c, al3));
	    adm.add(new Tuple<>(fw1, al4));
	    adm.add(new Tuple<>(fw2, al5));		    
	    net.setAddressMappings(adm);

	    DatatypeExpr[] addresses = new DatatypeExpr[]{
	    		nctx.am.get("ip_a"),
	    		nctx.am.get("ip_b"),
	    		nctx.am.get("ip_c")
	    };
	    
	    ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt1 = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
	    for(DatatypeExpr dte : addresses){
	    	rt1.add(new Tuple<DatatypeExpr,NetworkObject>(dte, fw1));
	    }
	    
	    ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt2 = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
	    for(DatatypeExpr dte : addresses){
	    	rt2.add(new Tuple<DatatypeExpr,NetworkObject>(dte, fw2));
	    }
	    
	    net.routingTable(a, rt1);
	    net.routingTable(b, rt1);
	    net.routingTable(c, rt2);

//		    net.setGateway(a, nat);
//		    net.setGateway(b, nat);
//		    net.setGateway(c, fw);
	   
	    ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt3 = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
	    rt3.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_a"),a));
	    rt3.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_b"),b));
	    rt3.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_c"),fw2));

	    ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt4 = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
	    rt4.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_a"),fw1));
	    rt4.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_b"),fw1));
	    rt4.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_c"),c));

	    net.routingTable(fw1, rt3);
	    net.routingTable(fw2, rt4);
	    net.attach(a, b, c, fw1,fw2);
	    
	    fw1.politoNFRules(nctx.am.get("ip_a"),nctx.am.get("ip_c"));
	    fw2.politoNFRules(nctx.am.get("ip_b"),nctx.am.get("ip_c"));
	    
	    check = new Checker(ctx,nctx,net);
	}
}
