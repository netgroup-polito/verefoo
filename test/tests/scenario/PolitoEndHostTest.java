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
package tests.scenario;

import java.util.ArrayList;

import com.microsoft.z3.Context;
import com.microsoft.z3.DatatypeExpr;

import it.polito.verigraph.mcnet.components.Checker;
import it.polito.verigraph.mcnet.components.NetContext;
import it.polito.verigraph.mcnet.components.Network;
import it.polito.verigraph.mcnet.components.NetworkObject;
import it.polito.verigraph.mcnet.components.Tuple;
import it.polito.verigraph.mcnet.netobjs.AclFirewall;
import it.polito.verigraph.mcnet.netobjs.PacketModel;
import it.polito.verigraph.mcnet.netobjs.PolitoEndHost;
import it.polito.verigraph.mcnet.netobjs.PolitoIDS;
import it.polito.verigraph.mcnet.netobjs.PolitoNat;
import it.polito.verigraph.mcnet.netobjs.PolitoWebServer;

// | PolitoEndHost | ---------- | PolitoIDS | ---------- | WebServer |


public class PolitoEndHostTest {

	public Checker check;
	public PolitoIDS politoIDS;
	public PolitoEndHost hostA;
	public PolitoWebServer server;
	
	public PolitoEndHostTest(Context ctx){
				
		NetContext nctx = new NetContext (ctx,new String[]{"hostA", "politoIDS", "server", "politoNat", "politoFw"},
						new String[]{"ip_hostA", "ip_politoIDS","ip_server", "ip_politoNat", "ip_politoFw"});
		
		
		Network net = new Network (ctx,new Object[]{nctx});
		hostA = new PolitoEndHost(ctx, new Object[]{nctx.nm.get("hostA"), net, nctx});
		server = new PolitoWebServer(ctx, new Object[]{nctx.nm.get("server"), net, nctx});
		politoIDS = new PolitoIDS(ctx, new Object[]{nctx.nm.get("politoIDS"), net, nctx});
		PolitoNat politoNat = new PolitoNat(ctx, new Object[]{nctx.nm.get("politoNat"), net, nctx});
		AclFirewall politoFw = new AclFirewall(ctx, new Object[]{nctx.nm.get("politoFw"), net, nctx});
		
		ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>> adm = new ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>>();
		ArrayList<DatatypeExpr> al1 = new ArrayList<DatatypeExpr>();
		ArrayList<DatatypeExpr> al2 = new ArrayList<DatatypeExpr>();
		ArrayList<DatatypeExpr> al3 = new ArrayList<DatatypeExpr>();
		ArrayList<DatatypeExpr> al5 = new ArrayList<DatatypeExpr>();
		ArrayList<DatatypeExpr> al6 = new ArrayList<DatatypeExpr>();
		
		
		al1.add(nctx.am.get("ip_hostA"));
		al2.add(nctx.am.get("ip_server"));
		al3.add(nctx.am.get("ip_politoIDS"));
		al5.add(nctx.am.get("ip_politoNat"));
		al6.add(nctx.am.get("ip_politoFw"));
		
		adm.add(new Tuple<>(hostA,al1));
		adm.add(new Tuple<>(server,al2));
		adm.add(new Tuple<>(politoIDS,al3));
		adm.add(new Tuple<>(politoNat, al5));
		adm.add(new Tuple<>(politoFw,al6));
		
		net.setAddressMappings(adm);
		
	    ArrayList<Tuple<DatatypeExpr,NetworkObject>> rtA = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
	    rtA.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_server"), politoNat));
	    rtA.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_politoIDS"), politoNat));
	    rtA.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_politoNat"), politoNat));
	    rtA.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_politoFw"), politoNat));
	    
	    ArrayList<Tuple<DatatypeExpr,NetworkObject>> rtIDS = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
	    rtIDS.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_hostA"), hostA));
	    rtIDS.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_server"), server));
	    rtIDS.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_politoNat"), politoNat));
	    rtIDS.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_politoFw"), politoFw));
	    
	    ArrayList<Tuple<DatatypeExpr,NetworkObject>> rtServer = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
	    rtServer.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_hostA"), politoFw));
	    rtServer.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_politoIDS"), politoFw));
	    rtServer.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_politoNat"), politoFw));
	    rtServer.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_politoFw"), politoFw));
	    
	    ArrayList<Tuple<DatatypeExpr,NetworkObject>> rtNat = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
	    rtNat.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_hostA"), hostA));
	    rtNat.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_politoFw"), politoIDS));
	    rtNat.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_server"), politoIDS));
	    rtNat.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_politoIDS"), politoIDS));
	    
	    ArrayList<Tuple<DatatypeExpr,NetworkObject>> rtFw = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
	    rtFw.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_hostA"), politoIDS));
	    rtFw.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_server"), server));
	    rtFw.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_politoNat"), politoIDS));
	    rtFw.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_politoIDS"), politoIDS));
	    
	    //Configuring routing tables of middleboxes
	    net.routingTable(politoFw, rtFw);
	    net.routingTable(politoNat, rtNat);
	    
	    net.routingTable(hostA, rtA);
	    net.routingTable(server, rtServer);
	    net.routingTable(politoIDS, rtIDS);

	    net.attach(hostA, politoIDS, server, politoFw, politoNat);
	    
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
	    
	    ArrayList<DatatypeExpr> ia = new ArrayList<DatatypeExpr>();
	    ia.add(nctx.am.get("ip_hostA"));
	  
	    ArrayList<Tuple<DatatypeExpr,DatatypeExpr>> acl = new ArrayList<Tuple<DatatypeExpr,DatatypeExpr>>();
	    acl.add(new Tuple<DatatypeExpr,DatatypeExpr>(nctx.am.get("ip_server"),nctx.am.get("ip_hostA")));
	    
	    politoNat.natModel(nctx.am.get("ip_politoNat"));
	    politoNat.setInternalAddress(ia);
	    
	    politoFw.addAcls(acl);
	
	    
	    check = new Checker(ctx, nctx, net);
	}
}
