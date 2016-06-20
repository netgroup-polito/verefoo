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
import mcnet.netobjs.PolitoIDS;
import mcnet.netobjs.PolitoNat;
import mcnet.netobjs.PolitoWebClient;
import mcnet.netobjs.PolitoWebServer;
public class Test_1{
    public Checker check;
    public AclFirewall firewall;
    public PolitoWebServer webserver;
    public PolitoWebClient user2;
    public PolitoIDS dpi;
    public PolitoNat nat;
    public Test_1(Context ctx){
        NetContext nctx = new NetContext (ctx,new String[]{"webserver", "user2", "dpi"}, new String[]{"ip_webserver", "ip_user2", "ip_dpi"});
        Network net = new Network (ctx,new Object[]{nctx});
        webserver = new PolitoWebServer(ctx, new Object[]{nctx.nm.get("webserver"), net, nctx});
        user2 = new PolitoWebClient(ctx, new Object[]{nctx.nm.get("user2"), net, nctx});
        dpi = new PolitoIDS(ctx, new Object[]{nctx.nm.get("dpi"), net, nctx});
        ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>> adm = new ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>>();
        ArrayList<DatatypeExpr> al0 = new ArrayList<DatatypeExpr>();
        al0.add(nctx.am.get("ip_webserver"));
        adm.add(new Tuple<>((NetworkObject)webserver, al0));
        ArrayList<DatatypeExpr> al1 = new ArrayList<DatatypeExpr>();
        al1.add(nctx.am.get("ip_user2"));
        adm.add(new Tuple<>((NetworkObject)user2, al1));
        ArrayList<DatatypeExpr> al2 = new ArrayList<DatatypeExpr>();
        al2.add(nctx.am.get("ip_dpi"));
        adm.add(new Tuple<>((NetworkObject)dpi, al2));
        net.setAddressMappings(adm);
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt_webserver = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt_webserver.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_dpi"), dpi));
        rt_webserver.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_user2"), dpi));
        net.routingTable(webserver, rt_webserver);
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt_user2 = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt_user2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_dpi"), dpi));
        rt_user2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_webserver"), dpi));
        net.routingTable(user2, rt_user2);
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt_dpi = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt_dpi.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_webserver"), webserver));
        rt_dpi.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_user2"), user2));
        net.routingTable(dpi, rt_dpi);
        net.attach(webserver, user2, dpi);
        dpi.installIDS(new int[]{PolitoIDS.DROGA});
        check = new Checker(ctx,nctx,net);
    }
}

