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
import mcnet.netobjs.PacketModel;
import mcnet.netobjs.PolitoEndHost;
import mcnet.netobjs.PolitoFieldModifier;
import mcnet.netobjs.PolitoVpnAccess;
import mcnet.netobjs.PolitoVpnExit;
import mcnet.netobjs.PolitoWebServer;

/**
 * <p/>
 * VPN test													<p/>
 *| client | --------- | vpn access | --------- | field modifier |--------- | vpn exit | --------- | server |		<p/>
 */
public class PolitoVpnFailureTest {
	
	public Checker check;
	public PolitoVpnAccess politoVpnAccess;
	public PolitoFieldModifier politoFieldModifier;
	public PolitoVpnExit politoVpnExit;
	public PolitoEndHost politoClient;
	public PolitoWebServer politoServer;
	
	public	PolitoVpnFailureTest(Context ctx){
	
			NetContext nctx = new NetContext (ctx,
					new String[]{"politoClient", "politoVpnAccess", "politoFieldModifier", "politoVpnExit", "politoServer"},
					new String[]{"ip_client", "ip_vpnaccess", "ip_fieldmodifier", "ip_vpnexit", "ip_server"});
			
			Network net = new Network (ctx,new Object[]{nctx});
			
			politoClient = new PolitoEndHost(ctx, new Object[]{nctx.nm.get("politoClient"), net, nctx});
			politoVpnAccess = new PolitoVpnAccess(ctx, new Object[]{nctx.nm.get("politoVpnAccess"), net, nctx});
			politoFieldModifier = new PolitoFieldModifier(ctx, new Object[]{nctx.nm.get("politoFieldModifier"), net, nctx});
			politoVpnExit = new PolitoVpnExit(ctx, new Object[]{nctx.nm.get("politoVpnExit"), net, nctx});
			politoServer = new PolitoWebServer(ctx, new Object[]{nctx.nm.get("politoServer"), net, nctx});
		  
			ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>> adm = new ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>>();
			ArrayList<DatatypeExpr> al1 = new ArrayList<DatatypeExpr>();
  			ArrayList<DatatypeExpr> al2 = new ArrayList<DatatypeExpr>();
  			ArrayList<DatatypeExpr> al3 = new ArrayList<DatatypeExpr>();
  			ArrayList<DatatypeExpr> al4 = new ArrayList<DatatypeExpr>();
  			ArrayList<DatatypeExpr> al5 = new ArrayList<DatatypeExpr>();
  			al1.add(nctx.am.get("ip_client"));
  			al2.add(nctx.am.get("ip_vpnaccess"));
  			al3.add(nctx.am.get("ip_vpnexit"));
  			al4.add(nctx.am.get("ip_server"));
  			al5.add(nctx.am.get("ip_fieldmodifier"));
			adm.add(new Tuple<>(politoClient, al1));
		    adm.add(new Tuple<>(politoVpnAccess, al2));
		    adm.add(new Tuple<>(politoVpnExit, al3));
		    adm.add(new Tuple<>(politoServer, al4));
		    adm.add(new Tuple<>(politoFieldModifier, al5));

		    net.setAddressMappings(adm);
		    
			ArrayList<Tuple<DatatypeExpr,NetworkObject>> rtClient = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
	    	rtClient.add(new Tuple<>(nctx.am.get("ip_vpnaccess"), politoVpnAccess));
	    	rtClient.add(new Tuple<>(nctx.am.get("ip_vpnexit"), politoVpnAccess));
	    	rtClient.add(new Tuple<>(nctx.am.get("ip_server"), politoVpnAccess));
	    	rtClient.add(new Tuple<>(nctx.am.get("ip_fieldmodifier"), politoVpnAccess));
	    
	    	ArrayList<Tuple<DatatypeExpr,NetworkObject>> rtVpnAccess = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
	    	rtVpnAccess.add(new Tuple<>(nctx.am.get("ip_client"), politoClient));
	    	rtVpnAccess.add(new Tuple<>(nctx.am.get("ip_vpnexit"), politoFieldModifier));
	    	rtVpnAccess.add(new Tuple<>(nctx.am.get("ip_server"), politoFieldModifier));
	    	rtVpnAccess.add(new Tuple<>(nctx.am.get("ip_fieldmodifier"), politoFieldModifier));
	    	
	    	ArrayList<Tuple<DatatypeExpr,NetworkObject>> rtFieldModifier = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
	    	rtFieldModifier.add(new Tuple<>(nctx.am.get("ip_client"), politoVpnAccess));
	    	rtFieldModifier.add(new Tuple<>(nctx.am.get("ip_vpnaccess"), politoVpnAccess));
	    	rtFieldModifier.add(new Tuple<>(nctx.am.get("ip_vpnexit"), politoVpnExit));
	    	rtFieldModifier.add(new Tuple<>(nctx.am.get("ip_server"), politoVpnExit));
	    	
	    	ArrayList<Tuple<DatatypeExpr,NetworkObject>> rtVpnExit = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
	    	rtVpnExit.add(new Tuple<>(nctx.am.get("ip_vpnaccess"), politoFieldModifier));
	    	rtVpnExit.add(new Tuple<>(nctx.am.get("ip_client"), politoFieldModifier));
	    	rtVpnAccess.add(new Tuple<>(nctx.am.get("ip_server"), politoServer));
	    	rtVpnAccess.add(new Tuple<>(nctx.am.get("ip_fieldmodifier"), politoFieldModifier));
	    
	    	ArrayList<Tuple<DatatypeExpr,NetworkObject>> rtServ = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
	    	rtServ.add(new Tuple<>(nctx.am.get("ip_client"), politoVpnExit));
	    	rtServ.add(new Tuple<>(nctx.am.get("ip_vpnaccess"), politoVpnExit));
	    	rtServ.add(new Tuple<>(nctx.am.get("ip_vpnexit"), politoVpnExit));
	    	rtServ.add(new Tuple<>(nctx.am.get("ip_fieldmodifier"), politoVpnExit));
	    
	    	net.routingTable(politoClient, rtClient);
		    net.routingTable(politoVpnAccess, rtVpnAccess);
		    net.routingTable(politoFieldModifier, rtFieldModifier);
		    net.routingTable(politoServer, rtServ);
		    net.routingTable(politoVpnExit, rtVpnExit);
		    
		    net.attach(politoClient, politoVpnAccess, politoFieldModifier, politoVpnExit, politoServer);
		    
		    /* Client config */
		    PacketModel packet = new PacketModel();
		    packet.setProto(nctx.HTTP_REQUEST);
		    packet.setIp_dest(nctx.am.get("ip_server"));
		    politoClient.installEndHost(packet);
		    
		    /* VPN gateways config */
		    politoVpnAccess.vpnAccessModel(nctx.am.get("ip_vpnaccess"), nctx.am.get("ip_vpnexit"));
		    politoVpnExit.vpnAccessModel(nctx.am.get("ip_vpnaccess"), nctx.am.get("ip_vpnexit"));
		    
		    /* Field modifier setup */
		    politoFieldModifier.installFieldModifier();
		    
		    System.out.println(net.EndHosts());
		    check = new Checker(ctx,nctx,net);
	}
}