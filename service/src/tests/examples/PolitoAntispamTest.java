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
import mcnet.netobjs.PolitoAntispam;
import mcnet.netobjs.PolitoErrFunction;
import mcnet.netobjs.PolitoMailClient;
import mcnet.netobjs.PolitoMailServer;

/**
 * @author Giacomo Costantini
 * <p/>
 * Antispam test													<p/>
 *| CLIENT | --------- | ANTISPAM | --------- | MAIL SERVER |		<p/>
 *..........................|										<p/>
 *...................| ERR FUNCTION |								<p/>
 */
public class PolitoAntispamTest {
	
	public Checker check;
	public AclFirewall fw;
	public PolitoAntispam politoAntispam;
	public PolitoMailClient politoMailClient;
	public PolitoMailServer politoMailServer;
	public PolitoErrFunction politoErrFunction;
	
	public	PolitoAntispamTest(Context ctx){
	
			NetContext nctx = new NetContext (ctx,
					new String[]{"politoMailClient", "politoAntispam", "politoMailServer", "politoErrFunction"},
					new String[]{"ip_client", "ip_antispam", "ip_mailServer", "ip_errFunction"});
			
			Network net = new Network (ctx,new Object[]{nctx});
			
			politoMailClient = new PolitoMailClient(ctx, new Object[]{nctx.nm.get("politoMailClient"), net, nctx});
			politoAntispam = new PolitoAntispam(ctx, new Object[]{nctx.nm.get("politoAntispam"), net, nctx});
			politoMailServer = new PolitoMailServer(ctx, new Object[]{nctx.nm.get("politoMailServer"), net, nctx});
			politoErrFunction = new PolitoErrFunction(ctx, new Object[]{nctx.nm.get("politoErrFunction"), net, nctx});
		  
			ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>> adm = new ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>>();
			ArrayList<DatatypeExpr> al1 = new ArrayList<DatatypeExpr>();
  			ArrayList<DatatypeExpr> al2 = new ArrayList<DatatypeExpr>();
  			ArrayList<DatatypeExpr> al3 = new ArrayList<DatatypeExpr>();
  			ArrayList<DatatypeExpr> al4 = new ArrayList<DatatypeExpr>();
  			al1.add(nctx.am.get("ip_client"));
  			al2.add(nctx.am.get("ip_antispam"));
  			al3.add(nctx.am.get("ip_mailServer"));
  			al4.add(nctx.am.get("ip_errFunction"));
			adm.add(new Tuple<>(politoMailClient, al1));
		    adm.add(new Tuple<>(politoAntispam, al2));
		    adm.add(new Tuple<>(politoMailServer, al3));
		    adm.add(new Tuple<>(politoErrFunction, al4));

		    net.setAddressMappings(adm);
		
		    
			ArrayList<Tuple<DatatypeExpr,NetworkObject>> rtClient = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
	    	rtClient.add(new Tuple<>(nctx.am.get("ip_mailServer"), politoAntispam));
	    	rtClient.add(new Tuple<>(nctx.am.get("ip_errFunction"), politoErrFunction));
	    
	    	ArrayList<Tuple<DatatypeExpr,NetworkObject>> rtAnti = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
	    	rtAnti.add(new Tuple<>(nctx.am.get("ip_mailServer"), politoMailServer));
	    	rtAnti.add(new Tuple<>(nctx.am.get("ip_client"), politoMailClient));
	    	rtAnti.add(new Tuple<>(nctx.am.get("ip_errFunction"), politoErrFunction));
	    
	    	ArrayList<Tuple<DatatypeExpr,NetworkObject>> rtServ = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
	    	rtServ.add(new Tuple<>(nctx.am.get("ip_client"), politoAntispam));
	    
//	    	ArrayList<Tuple<DatatypeExpr,NetworkObject>> rtErr = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
//	    	rtErr.add(new Tuple(nctx.am.get("ip_errFunction"), politoErrFunction));	    	
//	        net.routingTable(politoAntispam, rtErr);

	    	net.routingTable(politoMailClient, rtClient);
		    net.routingTable(politoAntispam, rtAnti);
		    net.routingTable(politoMailServer, rtServ);
		    
		    net.attach(politoMailClient, politoAntispam, politoMailServer, politoErrFunction);
		    
		    System.out.println(net.EndHosts());
		    politoAntispam.installAntispam(new int[]{1});
		    check = new Checker(ctx,nctx,net);
	}
}
