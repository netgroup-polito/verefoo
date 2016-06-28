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
import mcnet.netobjs.PolitoIDS;
import mcnet.netobjs.PolitoWebServer;

// | PolitoEndHost | ---------- | PolitoIDS | ---------- | WebServer |


public class PolitoEndHostTest {

	public Checker check;
	public PolitoIDS politoIDS;
	public PolitoEndHost hostA;
	public PolitoWebServer server;
	
	public PolitoEndHostTest(Context ctx){
				
		NetContext nctx = new NetContext (ctx,new String[]{"hostA", "politoIDS", "server"},
						new String[]{"ip_hostA", "ip_politoIDS","ip_server"});
		
		Network net = new Network (ctx,new Object[]{nctx});
		
		hostA = new PolitoEndHost(ctx, new Object[]{nctx.nm.get("hostA"), net, nctx});
		server = new PolitoWebServer(ctx, new Object[]{nctx.nm.get("server"), net, nctx});
		politoIDS = new PolitoIDS(ctx, new Object[]{nctx.nm.get("politoIDS"), net, nctx});
		
		ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>> adm = new ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>>();
		ArrayList<DatatypeExpr> al1 = new ArrayList<DatatypeExpr>();
		ArrayList<DatatypeExpr> al2 = new ArrayList<DatatypeExpr>();
		ArrayList<DatatypeExpr> al3 = new ArrayList<DatatypeExpr>();
		
		al1.add(nctx.am.get("ip_hostA"));
		al2.add(nctx.am.get("ip_server"));
		al3.add(nctx.am.get("ip_politoIDS"));
		
		adm.add(new Tuple<>(hostA,al1));
		adm.add(new Tuple<>(server,al2));
		adm.add(new Tuple<>(politoIDS,al3));
		
		net.setAddressMappings(adm);
		
	    ArrayList<Tuple<DatatypeExpr,NetworkObject>> rtA = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
	    rtA.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_server"), politoIDS));
	    rtA.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_politoIDS"), politoIDS));
	    
	    ArrayList<Tuple<DatatypeExpr,NetworkObject>> rtIDS = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
	    rtIDS.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_hostA"), hostA));
	    rtIDS.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_server"), server));
	    
	    ArrayList<Tuple<DatatypeExpr,NetworkObject>> rtServer = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
	    rtServer.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_hostA"), politoIDS));
	    rtServer.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_politoIDS"), politoIDS));
	    
	    net.routingTable(hostA, rtA);
	    net.routingTable(server, rtServer);
	    net.routingTable(politoIDS, rtIDS);

	    net.attach(hostA, politoIDS, server);
	    
	    PacketModel pModel = new PacketModel();
	    pModel.setBody(String.valueOf("music").hashCode());
	    pModel.setIp_dest(nctx.am.get("ip_server"));
	    pModel.setProto(nctx.HTTP_REQUEST);
	    hostA.installEndHost(pModel);
	    
	    politoIDS.installIDS(new int[]{
	    								String.valueOf("drug").hashCode(), 
	    								String.valueOf("sex").hashCode(),
	    								String.valueOf("weapons").hashCode()
	    							  });
	    
	    check = new Checker(ctx, nctx, net);
	}
}
