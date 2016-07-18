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
import mcnet.netobjs.PolitoIDS;
import mcnet.netobjs.PolitoNat;
import mcnet.netobjs.PolitoWebServer;
public class Test_1{
    public Checker check;
    public AclFirewall firewall;
    public PolitoWebServer webserver;
    public PolitoNat nat;
    public PolitoIDS dpi;
    public Test_1(Context ctx){
        NetContext nctx = new NetContext (ctx,new String[]{"firewall", "webserver", "nat", "dpi"}, new String[]{"ip_firewall", "ip_webserver", "ip_nat", "ip_dpi"});
        Network net = new Network (ctx,new Object[]{nctx});
        firewall = new AclFirewall(ctx, new Object[]{nctx.nm.get("firewall"), net, nctx});
        webserver = new PolitoWebServer(ctx, new Object[]{nctx.nm.get("webserver"), net, nctx});
        nat = new PolitoNat(ctx, new Object[]{nctx.nm.get("nat"), net, nctx});
        dpi = new PolitoIDS(ctx, new Object[]{nctx.nm.get("dpi"), net, nctx});
        ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>> adm = new ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>>();
        ArrayList<DatatypeExpr> al0 = new ArrayList<DatatypeExpr>();
        al0.add(nctx.am.get("ip_firewall"));
        adm.add(new Tuple<>((NetworkObject)firewall, al0));
        ArrayList<DatatypeExpr> al1 = new ArrayList<DatatypeExpr>();
        al1.add(nctx.am.get("ip_webserver"));
        adm.add(new Tuple<>((NetworkObject)webserver, al1));
        ArrayList<DatatypeExpr> al2 = new ArrayList<DatatypeExpr>();
        al2.add(nctx.am.get("ip_nat"));
        adm.add(new Tuple<>((NetworkObject)nat, al2));
        ArrayList<DatatypeExpr> al3 = new ArrayList<DatatypeExpr>();
        al3.add(nctx.am.get("ip_dpi"));
        adm.add(new Tuple<>((NetworkObject)dpi, al3));
        net.setAddressMappings(adm);
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt_firewall = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt_firewall.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_dpi"), nat));
        rt_firewall.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat"), nat));
        rt_firewall.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_webserver"), nat));
        net.routingTable(firewall, rt_firewall);
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt_webserver = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt_webserver.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_dpi"), dpi));
        rt_webserver.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat"), dpi));
        rt_webserver.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_firewall"), dpi));
        net.routingTable(webserver, rt_webserver);
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt_nat = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt_nat.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_dpi"), dpi));
        rt_nat.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_firewall"), firewall));
        rt_nat.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_webserver"), dpi));
        net.routingTable(nat, rt_nat);
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt_dpi = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt_dpi.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_webserver"), webserver));
        rt_dpi.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat"), nat));
        rt_dpi.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_firewall"), nat));
        net.routingTable(dpi, rt_dpi);
        net.attach(firewall, webserver, nat, dpi);
        ArrayList<DatatypeExpr> ia2 = new ArrayList<DatatypeExpr>();
        ia2.add(nctx.am.get("ip_user1"));
        ia2.add(nctx.am.get("ip_user2"));
        nat.natModel(nctx.am.get("ip_nat"));
        nat.setInternalAddress(ia2);
        dpi.installIDS(new int[]{String.valueOf("droga").hashCode()});
        check = new Checker(ctx,nctx,net);
    }
}


