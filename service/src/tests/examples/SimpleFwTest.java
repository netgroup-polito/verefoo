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
/**
 * @author Giacomo Costantini
 * <p/>
 * Simple ACL firewall test				<p/>
 * 
 *    | A | ----| FW1 |----- | C | 		<p/>
 *    .............|					<p/>
 *    | B | ------- 					<p/>
 *
 */
public class SimpleFwTest {
	
	public Checker check;
	public EndHost a,b,c;
	public AclFirewall fw;

	public	SimpleFwTest(Context ctx){
	
		NetContext nctx = new NetContext (ctx,new String[]{"a", "b", "c", "fw"},
												new String[]{"ip_a", "ip_b", "ip_c", "ip_f"});
		Network net = new Network (ctx,new Object[]{nctx});
		
		a = new EndHost(ctx, new Object[]{nctx.nm.get("a"), net, nctx});
		b = new EndHost(ctx, new Object[]{nctx.nm.get("b"), net, nctx});
		c = new EndHost(ctx, new Object[]{nctx.nm.get("c"), net, nctx});
	    fw = new AclFirewall(ctx, new Object[]{nctx.nm.get("fw"), net, nctx});
	    
	    ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>> adm = new ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>>();
		ArrayList<DatatypeExpr> al1 = new ArrayList<DatatypeExpr>();
		ArrayList<DatatypeExpr> al2 = new ArrayList<DatatypeExpr>();
		ArrayList<DatatypeExpr> al3 = new ArrayList<DatatypeExpr>();
		ArrayList<DatatypeExpr> al4 = new ArrayList<DatatypeExpr>();
		al1.add(nctx.am.get("ip_a"));
		al2.add(nctx.am.get("ip_b"));
		al3.add(nctx.am.get("ip_c"));
		al4.add(nctx.am.get("ip_f"));
		
	    adm.add(new Tuple<>(a, al1));
	    adm.add(new Tuple<>(b, al2));
	    adm.add(new Tuple<>(c, al3));
	    adm.add(new Tuple<>(fw, al4));

	    net.setAddressMappings(adm);
	    
	    DatatypeExpr[] addresses = new DatatypeExpr[]{
	    		nctx.am.get("ip_a"),
	    		nctx.am.get("ip_b"),
	    		nctx.am.get("ip_c"),
	    		nctx.am.get("ip_f")
	    };
	    
	    ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt1 = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
	    for(DatatypeExpr dte : addresses){
	    	rt1.add(new Tuple<DatatypeExpr,NetworkObject>(dte, fw));
	    }
	    
	    net.routingTable(a, rt1);
	    net.routingTable(b, rt1);
	    net.routingTable(c, rt1);

//		    net.setGateway(a, fw);
//		    net.setGateway(b, fw);
//		    net.setGateway(c, fw);
	    
	    ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt2 = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
	    rt2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_a"),a));
	    rt2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_b"),b));
	    rt2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_c"),c));

	    net.routingTable(fw, rt2);
	    
	    ArrayList<Tuple<DatatypeExpr,DatatypeExpr>> acl = new ArrayList<Tuple<DatatypeExpr,DatatypeExpr>>();
	    acl.add(new Tuple<DatatypeExpr,DatatypeExpr>(nctx.am.get("ip_a"),nctx.am.get("ip_c")));
	    acl.add(new Tuple<DatatypeExpr,DatatypeExpr>(nctx.am.get("ip_a"),nctx.am.get("ip_b")));
	    acl.add(new Tuple<DatatypeExpr,DatatypeExpr>(nctx.am.get("ip_b"),nctx.am.get("ip_c")));
	    
	    fw.addAcls(acl);
	    net.attach(a, b, c, fw);
	    check = new Checker(ctx,nctx,net);

}
}
