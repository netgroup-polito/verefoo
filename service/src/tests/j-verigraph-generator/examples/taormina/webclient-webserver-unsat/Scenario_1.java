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
import mcnet.netobjs.EndHost;
public class Scenario_1{
    public Checker check;
    public AclFirewall firewall;
    public EndHost webserver;
    public PolitoCache cache;
    public PolitoNat nat;
    public EndHost webclient;
    public Scenario_1(Context ctx){
        NetContext nctx = new NetContext (ctx,new String[]{"firewall", "webserver", "cache", "nat", "webclient"}, new String[]{"ip_firewall", "ip_webserver", "ip_cache", "ip_nat", "ip_webclient"});
        Network net = new Network (ctx,new Object[]{nctx});
        firewall = new AclFirewall(ctx, new Object[]{nctx.nm.get("firewall"), net, nctx});
        webserver = new EndHost(ctx, new Object[]{nctx.nm.get("webserver"), net, nctx});
        cache = new PolitoCache(ctx, new Object[]{nctx.nm.get("cache"), net, nctx});
        nat = new PolitoNat(ctx, new Object[]{nctx.nm.get("nat"), net, nctx});
        webclient = new EndHost(ctx, new Object[]{nctx.nm.get("webclient"), net, nctx});
        ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>> adm = new ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>>();
        ArrayList<DatatypeExpr> al0 = new ArrayList<DatatypeExpr>();
        al0.add(nctx.am.get("ip_firewall"));
        adm.add(new Tuple<>(firewall, al0));
        ArrayList<DatatypeExpr> al1 = new ArrayList<DatatypeExpr>();
        al1.add(nctx.am.get("ip_webserver"));
        adm.add(new Tuple<>(webserver, al1));
        ArrayList<DatatypeExpr> al2 = new ArrayList<DatatypeExpr>();
        al2.add(nctx.am.get("ip_cache"));
        adm.add(new Tuple<>(cache, al2));
        ArrayList<DatatypeExpr> al3 = new ArrayList<DatatypeExpr>();
        al3.add(nctx.am.get("ip_nat"));
        adm.add(new Tuple<>(nat, al3));
        ArrayList<DatatypeExpr> al4 = new ArrayList<DatatypeExpr>();
        al4.add(nctx.am.get("ip_webclient"));
        adm.add(new Tuple<>(webclient, al4));
        net.setAddressMappings(adm);
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt_firewall = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt_firewall.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache"), nat));
        rt_firewall.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_webserver"), webserver));
        rt_firewall.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat"), nat));
        rt_firewall.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_webclient"), nat));
        net.routingTable(firewall, rt_firewall);
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt_webserver = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt_webserver.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache"), firewall));
        rt_webserver.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_firewall"), firewall));
        rt_webserver.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat"), firewall));
        rt_webserver.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_webclient"), firewall));
        net.routingTable(webserver, rt_webserver);
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt_cache = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt_cache.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_firewall"), nat));
        rt_cache.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_webclient"), webclient));
        rt_cache.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_webserver"), nat));
        rt_cache.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat"), nat));
        net.routingTable(cache, rt_cache);
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt_nat = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt_nat.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache"), cache));
        rt_nat.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_firewall"), firewall));
        rt_nat.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_webclient"), cache));
        rt_nat.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_webserver"), firewall));
        net.routingTable(nat, rt_nat);
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt_webclient = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt_webclient.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache"), cache));
        rt_webclient.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_firewall"), cache));
        rt_webclient.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat"), cache));
        rt_webclient.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_webserver"), cache));
        net.routingTable(webclient, rt_webclient);
        net.attach(firewall, webserver, cache, nat, webclient);
        ArrayList<Tuple<DatatypeExpr,DatatypeExpr>> acl = new ArrayList<Tuple<DatatypeExpr,DatatypeExpr>>();
        acl.add(new Tuple<DatatypeExpr,DatatypeExpr>(nctx.am.get("ip_webserver"),nctx.am.get("ip_nat")));
        firewall.addAcls(acl);
        ArrayList<DatatypeExpr> ia = new ArrayList<DatatypeExpr>();
        ia.add(nctx.am.get("ip_cache"));
        nat.setInternalAddress(ia);
        check = new Checker(ctx,nctx,net);
    }
}

