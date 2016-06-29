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
import mcnet.netobjs.AclFirewall;
import mcnet.netobjs.EndHost;
import mcnet.netobjs.PolitoCache;
import mcnet.netobjs.PolitoNat;
import mcnet.netobjs.PolitoWebClient;
import mcnet.netobjs.PolitoWebServer;
/**
 * @author Giacomo Costantini
 * <p/>
 * Cache - Nat - Fw     test												<p/>
 * 
 *    | HOST_A | ----| CACHE |----- | NAT | ----| FW |----- | HOST_B |		<p/>
 *    ...................|													<p/>
 *    | HOST_C | --------													<p/>
 *
 */
public class PolitoCacheNatFwTest {
	
	public Checker check;
	public EndHost a;
	public PolitoCache politoCache;
	public PolitoWebClient hostA;
	public PolitoWebServer hostB,hostC;
	public PolitoWebServer server;
	public PolitoNat politoNat;
	public AclFirewall politoFw;
	
	public	PolitoCacheNatFwTest(Context ctx){
	
		NetContext nctx = new NetContext (ctx,new String[]{"hostA", "hostC", "politoCache","politoNat","politoFw","hostB"},
												new String[]{"ip_hostA", "ip_hostC", "ip_politoCache","ip_politoNat","ip_politoFw","ip_hostB"});
		Network net = new Network (ctx,new Object[]{nctx});
		
		hostA = new PolitoWebClient(ctx, new Object[]{nctx.nm.get("hostA"), net, nctx,nctx.am.get("ip_hostB")});
		hostB = new PolitoWebServer(ctx, new Object[]{nctx.nm.get("hostB"), net, nctx});
		hostC = new PolitoWebServer(ctx, new Object[]{nctx.nm.get("hostC"), net, nctx});
		politoCache = new PolitoCache(ctx, new Object[]{nctx.nm.get("politoCache"), net, nctx});
		politoNat = new PolitoNat(ctx, new Object[]{nctx.nm.get("politoNat"), net, nctx});
		politoFw = new AclFirewall(ctx, new Object[]{nctx.nm.get("politoFw"), net, nctx});
	    
		ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>> adm = new ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>>();
		ArrayList<DatatypeExpr> al1 = new ArrayList<DatatypeExpr>();
		ArrayList<DatatypeExpr> al2 = new ArrayList<DatatypeExpr>();
		ArrayList<DatatypeExpr> al3 = new ArrayList<DatatypeExpr>();
		ArrayList<DatatypeExpr> al4 = new ArrayList<DatatypeExpr>();
		ArrayList<DatatypeExpr> al5 = new ArrayList<DatatypeExpr>();
		ArrayList<DatatypeExpr> al6 = new ArrayList<DatatypeExpr>();
		
		al1.add(nctx.am.get("ip_hostA"));
		al2.add(nctx.am.get("ip_hostB"));
		al3.add(nctx.am.get("ip_hostC"));
		al4.add(nctx.am.get("ip_politoCache"));
		al5.add(nctx.am.get("ip_politoNat"));
		al6.add(nctx.am.get("ip_politoFw"));
		
	    adm.add(new Tuple<>(hostA, al1));
	    adm.add(new Tuple<>(hostB, al2));
	    adm.add(new Tuple<>(hostC, al3));
	    adm.add(new Tuple<>(politoCache, al4));
	    adm.add(new Tuple<>(politoNat, al5));
	    adm.add(new Tuple<>(politoFw,al6));
	    
	    net.setAddressMappings(adm);
	    
	    ArrayList<Tuple<DatatypeExpr,NetworkObject>> rtA = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
	    rtA.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_hostB"), politoCache));
	    rtA.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_hostC"), politoCache));
	    rtA.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_politoCache"), politoCache));
	    rtA.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_politoNat"), politoCache));
	    rtA.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_politoFw"), politoCache));
	    
	    ArrayList<Tuple<DatatypeExpr,NetworkObject>> rtB = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
	    rtB.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_hostA"), politoFw));
	    rtB.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_hostC"), politoFw));
	    rtB.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_politoCache"), politoFw));
	    rtB.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_politoNat"), politoFw));
	    rtB.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_politoFw"), politoFw));
	    
	    ArrayList<Tuple<DatatypeExpr,NetworkObject>> rtC = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
	    rtC.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_hostB"), politoCache));
	    rtC.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_hostA"), politoCache));
	    rtC.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_politoCache"), politoCache));
	    rtC.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_politoNat"), politoCache));
	    rtC.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_politoFw"), politoCache));
	    
	    ArrayList<Tuple<DatatypeExpr,NetworkObject>> rtCache = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
	    rtCache.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_hostA"), hostA));
	    rtCache.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_hostB"), politoNat));
	    rtCache.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_hostC"), hostC));
	    rtCache.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_politoNat"), politoNat));
	    rtCache.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_politoFw"), politoNat));
	    
	    ArrayList<Tuple<DatatypeExpr,NetworkObject>> rtNat = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
	    rtNat.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_hostA"), politoCache));
	    rtNat.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_hostB"), politoFw));
	    rtNat.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_hostC"), politoCache));
	    rtNat.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_politoCache"), politoCache));
	    rtNat.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_politoFw"), politoFw));
	    
	    ArrayList<Tuple<DatatypeExpr,NetworkObject>> rtFw = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
	    rtFw.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_hostB"), hostB));
	    rtFw.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_hostA"), politoNat));
	    rtFw.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_hostC"), politoNat));
	    rtFw.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_politoNat"), politoNat));
	    rtFw.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_politoCache"), politoNat));
	    
	    //Configuring routing tables of middleboxes
	    net.routingTable(politoFw, rtFw);
	    net.routingTable(politoNat, rtNat);
	    net.routingTable(politoCache, rtCache);
	    net.routingTable(hostA, rtA);
	    net.routingTable(hostC, rtC);
	    net.routingTable(hostB, rtB);
	    
	    //Attaching nodes to network
	    net.attach(hostA, hostB, hostC, politoCache, politoNat, politoFw);

	    ArrayList<DatatypeExpr> ia = new ArrayList<DatatypeExpr>();
	    ia.add(nctx.am.get("ip_hostA"));
	    ia.add(nctx.am.get("ip_hostC"));
	    ArrayList<Tuple<DatatypeExpr,DatatypeExpr>> acl = new ArrayList<Tuple<DatatypeExpr,DatatypeExpr>>();
	    acl.add(new Tuple<DatatypeExpr,DatatypeExpr>(nctx.am.get("ip_politoNat"),nctx.am.get("ip_hostB")));
	    
	    //Configuring middleboxes
	    politoNat.natModel(nctx.am.get("ip_politoNat"));
	    politoNat.setInternalAddress(ia);
	    politoCache.installCache(new NetworkObject[]{hostA});
	    politoFw.addAcls(acl);
	
	    check = new Checker(ctx,nctx,net);

	}
}
