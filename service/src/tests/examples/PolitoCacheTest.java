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
import mcnet.netobjs.PolitoCache;

/**
 * @author Giacomo Costantini
 * <p/>
 * Custom test	<p/>
 * 
 * | A | <------> | CACHE | <------> | B |
 */
public class PolitoCacheTest {
	
	public Checker check;
	public EndHost a,b,c;
	public PolitoCache politoCache;

	public	PolitoCacheTest(Context ctx){
		NetContext nctx = new NetContext (ctx,new String[]{"a", "b", "politoCache"},
												new String[]{"ip_a", "ip_b", "ip_cache"});
		Network net = new Network (ctx,new Object[]{nctx});
		
		a = new EndHost(ctx, new Object[]{nctx.nm.get("a"), net, nctx});
		b = new EndHost(ctx, new Object[]{nctx.nm.get("b"), net, nctx});
		politoCache = new PolitoCache(ctx, new Object[]{nctx.nm.get("politoCache"), net, nctx});
	    
		
		ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>> adm = new ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>>();
		ArrayList<DatatypeExpr> al1 = new ArrayList<DatatypeExpr>();
		ArrayList<DatatypeExpr> al2 = new ArrayList<DatatypeExpr>();
		ArrayList<DatatypeExpr> al3 = new ArrayList<DatatypeExpr>();
		al1.add(nctx.am.get("ip_a"));
		al2.add(nctx.am.get("ip_b"));
		al3.add(nctx.am.get("ip_cache"));
		adm.add(new Tuple<>(a,al1));
	    adm.add(new Tuple<>(b, al2));
	    adm.add(new Tuple<>(politoCache, al3));
	    net.setAddressMappings(adm);
	
	    DatatypeExpr[] addresses = new DatatypeExpr[]{
	    		nctx.am.get("ip_a"),
	    		nctx.am.get("ip_b"),
	    };
	    
	    ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt1 = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
	    for(DatatypeExpr dte : addresses){
	    	rt1.add(new Tuple<DatatypeExpr,NetworkObject>(dte, politoCache));
	    }
	    
	    ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt2 = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
	    rt2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_a"),a));
	    rt2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_b"),b));

	    net.routingTable(a, rt1);
	    net.routingTable(b, rt1);
	    net.routingTable(politoCache, rt2);

	    net.attach(a, b, politoCache);
	    politoCache.installCache(new NetworkObject[]{nctx.nm.get("a")});
	    check = new Checker(ctx,nctx,net);
	}
}
