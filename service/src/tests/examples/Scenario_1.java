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
import mcnet.netobjs.PolitoNat;

public class Scenario_1{
    public Checker check;
    public AclFirewall firewall;
    public EndHost webserver;
    public PolitoNat nat;
    public EndHost user1;
    public Scenario_1(Context ctx){
        NetContext nctx = new NetContext (ctx,new String[]{"firewall", "webserver", "nat", "user1"}, new String[]{"ip_firewall", "ip_webserver", "ip_nat", "ip_user1"});
        Network net = new Network (ctx,new Object[]{nctx});
        firewall = new AclFirewall(ctx, new Object[]{nctx.nm.get("firewall"), net, nctx});
        webserver = new EndHost(ctx, new Object[]{nctx.nm.get("webserver"), net, nctx});
        nat = new PolitoNat(ctx, new Object[]{nctx.nm.get("nat"), net, nctx});
        user1 = new EndHost(ctx, new Object[]{nctx.nm.get("user1"), net, nctx});
        ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>> adm = new ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>>();
        ArrayList<DatatypeExpr> al0 = new ArrayList<DatatypeExpr>();
        al0.add(nctx.am.get("ip_firewall"));
        adm.add(new Tuple<>(firewall, al0));
        ArrayList<DatatypeExpr> al1 = new ArrayList<DatatypeExpr>();
        al1.add(nctx.am.get("ip_webserver"));
        adm.add(new Tuple<>(webserver, al1));
        ArrayList<DatatypeExpr> al2 = new ArrayList<DatatypeExpr>();
        al2.add(nctx.am.get("ip_nat"));
        adm.add(new Tuple<>(nat, al2));
        ArrayList<DatatypeExpr> al3 = new ArrayList<DatatypeExpr>();
        al3.add(nctx.am.get("ip_user1"));
        adm.add(new Tuple<>(user1, al3));
        net.setAddressMappings(adm);
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt_firewall = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt_firewall.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_webserver"), webserver));
        rt_firewall.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_user1"), nat));
        rt_firewall.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat"), nat));
        net.routingTable(firewall, rt_firewall);
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt_webserver = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt_webserver.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_firewall"), firewall));
        rt_webserver.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_user1"), firewall));
        rt_webserver.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat"), firewall));
        net.routingTable(webserver, rt_webserver);
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt_nat = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt_nat.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_firewall"), firewall));
        rt_nat.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_user1"), user1));
        rt_nat.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_webserver"), firewall));
        net.routingTable(nat, rt_nat);
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt_user1 = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt_user1.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_firewall"), nat));
        rt_user1.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat"), nat));
        rt_user1.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_webserver"), nat));
        net.routingTable(user1, rt_user1);
        net.attach(firewall, webserver, nat, user1);
        ArrayList<DatatypeExpr> ia = new ArrayList<DatatypeExpr>();
        ia.add(nctx.am.get("ip_user1"));
        nat.setInternalAddress(ia);
        check = new Checker(ctx,nctx,net);
    }
}

