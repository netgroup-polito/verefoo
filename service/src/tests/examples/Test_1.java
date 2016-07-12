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
import mcnet.netobjs.PolitoVpnAccess;
import mcnet.netobjs.PolitoVpnExit;
import mcnet.netobjs.PolitoWebServer;
public class Test_1{
    public Checker check;
    public PolitoVpnExit vpnexit;
    public PolitoWebServer webserver;
    public PolitoEndHost client;
    public PolitoVpnAccess vpnaccess;
    public Test_1(Context ctx){
        NetContext nctx = new NetContext (ctx,new String[]{"vpnexit", "webserver", "client", "vpnaccess"}, new String[]{"ip_vpnexit", "ip_webserver", "ip_client", "ip_vpnaccess"});
        Network net = new Network (ctx,new Object[]{nctx});
        vpnexit = new PolitoVpnExit(ctx, new Object[]{nctx.nm.get("vpnexit"), net, nctx, nctx.am.get("ip_vpnaccess")});
        webserver = new PolitoWebServer(ctx, new Object[]{nctx.nm.get("webserver"), net, nctx});
        client = new PolitoEndHost(ctx, new Object[]{nctx.nm.get("client"), net, nctx});
        vpnaccess = new PolitoVpnAccess(ctx, new Object[]{nctx.nm.get("vpnaccess"), net, nctx, nctx.am.get("ip_vpnexit")});
        ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>> adm = new ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>>();
        ArrayList<DatatypeExpr> al0 = new ArrayList<DatatypeExpr>();
        al0.add(nctx.am.get("ip_vpnexit"));
        adm.add(new Tuple<>((NetworkObject)vpnexit, al0));
        ArrayList<DatatypeExpr> al1 = new ArrayList<DatatypeExpr>();
        al1.add(nctx.am.get("ip_webserver"));
        adm.add(new Tuple<>((NetworkObject)webserver, al1));
        ArrayList<DatatypeExpr> al2 = new ArrayList<DatatypeExpr>();
        al2.add(nctx.am.get("ip_client"));
        adm.add(new Tuple<>((NetworkObject)client, al2));
        ArrayList<DatatypeExpr> al3 = new ArrayList<DatatypeExpr>();
        al3.add(nctx.am.get("ip_vpnaccess"));
        adm.add(new Tuple<>((NetworkObject)vpnaccess, al3));
        net.setAddressMappings(adm);
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt_vpnexit = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt_vpnexit.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_webserver"), webserver));
        rt_vpnexit.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_client"), vpnaccess));
        rt_vpnexit.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_vpnaccess"), vpnaccess));
        net.routingTable(vpnexit, rt_vpnexit);
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt_webserver = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt_webserver.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_vpnexit"), vpnexit));
        rt_webserver.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_client"), vpnexit));
        rt_webserver.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_vpnaccess"), vpnexit));
        net.routingTable(webserver, rt_webserver);
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt_client = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt_client.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_vpnexit"), vpnaccess));
        rt_client.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_webserver"), vpnaccess));
        rt_client.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_vpnaccess"), vpnaccess));
        net.routingTable(client, rt_client);
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt_vpnaccess = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt_vpnaccess.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_vpnexit"), vpnexit));
        rt_vpnaccess.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_webserver"), vpnexit));
        rt_vpnaccess.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_client"), client));
        net.routingTable(vpnaccess, rt_vpnaccess);
        net.attach(vpnexit, webserver, client, vpnaccess);
        vpnexit.vpnAccessModel(nctx.am.get("ip_vpnaccess"), nctx.am.get("ip_vpnexit"));
        PacketModel pModel2 = new PacketModel();
        pModel2.setBody(String.valueOf("cats").hashCode());
        pModel2.setProto(nctx.HTTP_REQUEST);
        pModel2.setUrl(String.valueOf("www.facebook.com").hashCode());
        pModel2.setIp_dest(nctx.am.get("webserver"));
        client.installEndHost(pModel2);
        vpnaccess.vpnAccessModel(nctx.am.get("ip_vpnaccess"), nctx.am.get("ip_vpnexit"));
        check = new Checker(ctx,nctx,net);
    }
}

