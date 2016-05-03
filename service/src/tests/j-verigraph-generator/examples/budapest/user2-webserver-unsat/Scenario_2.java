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
import mcnet.netobjs.EndHost;
import mcnet.netobjs.PolitoNat;
public class Scenario_2{
    public Checker check;
    public AclFirewall firewall;
    public EndHost webserver;
    public EndHost user2;
    public PolitoNat dpi;
    public Scenario_2(Context ctx){
        NetContext nctx = new NetContext (ctx,new String[]{"firewall", "webserver", "user2", "dpi"}, new String[]{"ip_firewall", "ip_webserver", "ip_user2", "ip_nat"});
        Network net = new Network (ctx,new Object[]{nctx});
        firewall = new AclFirewall(ctx, new Object[]{nctx.nm.get("firewall"), net, nctx});
        webserver = new EndHost(ctx, new Object[]{nctx.nm.get("webserver"), net, nctx});
        user2 = new EndHost(ctx, new Object[]{nctx.nm.get("user2"), net, nctx});
        dpi = new PolitoNat(ctx, new Object[]{nctx.nm.get("dpi"), net, nctx});
        ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>> adm = new ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>>();
        ArrayList<DatatypeExpr> al0 = new ArrayList<DatatypeExpr>();
        al0.add(nctx.am.get("ip_firewall"));
        adm.add(new Tuple<>(firewall, al0));
        ArrayList<DatatypeExpr> al1 = new ArrayList<DatatypeExpr>();
        al1.add(nctx.am.get("ip_webserver"));
        adm.add(new Tuple<>(webserver, al1));
        ArrayList<DatatypeExpr> al2 = new ArrayList<DatatypeExpr>();
        al2.add(nctx.am.get("ip_user2"));
        adm.add(new Tuple<>(user2, al2));
        ArrayList<DatatypeExpr> al3 = new ArrayList<DatatypeExpr>();
        al3.add(nctx.am.get("ip_nat"));
        adm.add(new Tuple<>(dpi, al3));
        net.setAddressMappings(adm);
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt_firewall = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt_firewall.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_webserver"), webserver));
        rt_firewall.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat"), dpi));
        rt_firewall.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_user2"), dpi));
        net.routingTable(firewall, rt_firewall);
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt_webserver = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt_webserver.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_firewall"), firewall));
        rt_webserver.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat"), firewall));
        rt_webserver.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_user2"), firewall));
        net.routingTable(webserver, rt_webserver);
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt_user2 = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt_user2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_firewall"), dpi));
        rt_user2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat"), dpi));
        rt_user2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_webserver"), dpi));
        net.routingTable(user2, rt_user2);
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt_dpi = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt_dpi.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_firewall"), firewall));
        rt_dpi.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_webserver"), firewall));
        rt_dpi.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_user2"), user2));
        net.routingTable(dpi, rt_dpi);
        net.attach(firewall, webserver, user2, dpi);
        ArrayList<Tuple<DatatypeExpr,DatatypeExpr>> acl = new ArrayList<Tuple<DatatypeExpr,DatatypeExpr>>();
        acl.add(new Tuple<DatatypeExpr,DatatypeExpr>(nctx.am.get("ip_webserver"),nctx.am.get("ip_nat")));
        firewall.addAcls(acl);
        ArrayList<DatatypeExpr> ia = new ArrayList<DatatypeExpr>();
        ia.add(nctx.am.get("ip_user2"));
        dpi.setInternalAddress(ia);
        check = new Checker(ctx,nctx,net);
    }
}

