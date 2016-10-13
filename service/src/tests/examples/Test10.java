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
import mcnet.netobjs.AclFirewall;
import mcnet.netobjs.PolitoVpnExit;
import mcnet.netobjs.PolitoWebServer;
import mcnet.netobjs.PolitoMailServer;
import mcnet.netobjs.PolitoEndHost;
import mcnet.netobjs.EndHost;
import mcnet.netobjs.PolitoVpnAccess;
import mcnet.netobjs.PolitoCache;
import mcnet.netobjs.PolitoMailClient;
import mcnet.netobjs.PolitoFieldModifier;
import mcnet.netobjs.PolitoWebClient;
import mcnet.netobjs.PolitoNat;
import mcnet.netobjs.PolitoAntispam;
import mcnet.netobjs.PolitoIDS;
public class Test10{
    public Checker check;
    public PolitoNat nat9;
    public PolitoNat nat8;
    public PolitoNat nat3;
    public PolitoNat nat2;
    public PolitoNat nat1;
    public PolitoNat nat7;
    public PolitoNat nat6;
    public PolitoNat nat5;
    public PolitoNat nat4;
    public PolitoEndHost client;
    public PolitoWebServer server;
    public PolitoNat nat10;
    private void setDevices(Context ctx, NetContext nctx, Network net){
        nat9 = new PolitoNat(ctx, new Object[]{nctx.nm.get("nat9"), net, nctx});
        nat8 = new PolitoNat(ctx, new Object[]{nctx.nm.get("nat8"), net, nctx});
        nat3 = new PolitoNat(ctx, new Object[]{nctx.nm.get("nat3"), net, nctx});
        nat2 = new PolitoNat(ctx, new Object[]{nctx.nm.get("nat2"), net, nctx});
        nat1 = new PolitoNat(ctx, new Object[]{nctx.nm.get("nat1"), net, nctx});
        nat7 = new PolitoNat(ctx, new Object[]{nctx.nm.get("nat7"), net, nctx});
        nat6 = new PolitoNat(ctx, new Object[]{nctx.nm.get("nat6"), net, nctx});
        nat5 = new PolitoNat(ctx, new Object[]{nctx.nm.get("nat5"), net, nctx});
        nat4 = new PolitoNat(ctx, new Object[]{nctx.nm.get("nat4"), net, nctx});
        client = new PolitoEndHost(ctx, new Object[]{nctx.nm.get("client"), net, nctx});
        server = new PolitoWebServer(ctx, new Object[]{nctx.nm.get("server"), net, nctx});
        nat10 = new PolitoNat(ctx, new Object[]{nctx.nm.get("nat10"), net, nctx});
    }
    private void doMappings(NetContext nctx, ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>> adm){
        ArrayList<DatatypeExpr> al0 = new ArrayList<DatatypeExpr>();
        al0.add(nctx.am.get("ip_nat9"));
        adm.add(new Tuple<>((NetworkObject)nat9, al0));
        ArrayList<DatatypeExpr> al1 = new ArrayList<DatatypeExpr>();
        al1.add(nctx.am.get("ip_nat8"));
        adm.add(new Tuple<>((NetworkObject)nat8, al1));
        ArrayList<DatatypeExpr> al2 = new ArrayList<DatatypeExpr>();
        al2.add(nctx.am.get("ip_nat3"));
        adm.add(new Tuple<>((NetworkObject)nat3, al2));
        ArrayList<DatatypeExpr> al3 = new ArrayList<DatatypeExpr>();
        al3.add(nctx.am.get("ip_nat2"));
        adm.add(new Tuple<>((NetworkObject)nat2, al3));
        ArrayList<DatatypeExpr> al4 = new ArrayList<DatatypeExpr>();
        al4.add(nctx.am.get("ip_nat1"));
        adm.add(new Tuple<>((NetworkObject)nat1, al4));
        ArrayList<DatatypeExpr> al5 = new ArrayList<DatatypeExpr>();
        al5.add(nctx.am.get("ip_nat7"));
        adm.add(new Tuple<>((NetworkObject)nat7, al5));
        ArrayList<DatatypeExpr> al6 = new ArrayList<DatatypeExpr>();
        al6.add(nctx.am.get("ip_nat6"));
        adm.add(new Tuple<>((NetworkObject)nat6, al6));
        ArrayList<DatatypeExpr> al7 = new ArrayList<DatatypeExpr>();
        al7.add(nctx.am.get("ip_nat5"));
        adm.add(new Tuple<>((NetworkObject)nat5, al7));
        ArrayList<DatatypeExpr> al8 = new ArrayList<DatatypeExpr>();
        al8.add(nctx.am.get("ip_nat4"));
        adm.add(new Tuple<>((NetworkObject)nat4, al8));
        ArrayList<DatatypeExpr> al9 = new ArrayList<DatatypeExpr>();
        al9.add(nctx.am.get("ip_client"));
        adm.add(new Tuple<>((NetworkObject)client, al9));
        ArrayList<DatatypeExpr> al10 = new ArrayList<DatatypeExpr>();
        al10.add(nctx.am.get("ip_server"));
        adm.add(new Tuple<>((NetworkObject)server, al10));
        ArrayList<DatatypeExpr> al11 = new ArrayList<DatatypeExpr>();
        al11.add(nctx.am.get("ip_nat10"));
        adm.add(new Tuple<>((NetworkObject)nat10, al11));
    }
    private void setRoutingnat9(NetContext nctx, Network net, ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt_nat9){
        rt_nat9.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_server"), nat10));
        rt_nat9.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_client"), nat8));
        rt_nat9.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat10"), nat10));
        rt_nat9.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat8"), nat8));
        rt_nat9.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat6"), nat8));
        rt_nat9.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat7"), nat8));
        rt_nat9.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat4"), nat8));
        rt_nat9.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat5"), nat8));
        rt_nat9.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat2"), nat8));
        rt_nat9.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat3"), nat8));
        rt_nat9.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat1"), nat8));
        net.routingTable(nat9, rt_nat9);
    }
    private void configureDevicenat9(NetContext nctx) {
        ArrayList<DatatypeExpr> ia0 = new ArrayList<DatatypeExpr>();
        ia0.add(nctx.am.get("ip_client"));
        ia0.add(nctx.am.get("ip_nat1"));
        ia0.add(nctx.am.get("ip_nat2"));
        ia0.add(nctx.am.get("ip_nat3"));
        ia0.add(nctx.am.get("ip_nat4"));
        ia0.add(nctx.am.get("ip_nat5"));
        ia0.add(nctx.am.get("ip_nat6"));
        ia0.add(nctx.am.get("ip_nat7"));
        ia0.add(nctx.am.get("ip_nat8"));
        nat9.natModel(nctx.am.get("ip_nat9"));
        nat9.setInternalAddress(ia0);
    }
    private void setRoutingnat8(NetContext nctx, Network net, ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt_nat8){
        rt_nat8.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_client"), nat7));
        rt_nat8.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat10"), nat9));
        rt_nat8.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_server"), nat9));
        rt_nat8.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat9"), nat9));
        rt_nat8.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat6"), nat7));
        rt_nat8.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat7"), nat7));
        rt_nat8.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat4"), nat7));
        rt_nat8.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat5"), nat7));
        rt_nat8.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat2"), nat7));
        rt_nat8.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat3"), nat7));
        rt_nat8.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat1"), nat7));
        net.routingTable(nat8, rt_nat8);
    }
    private void configureDevicenat8(NetContext nctx) {
        ArrayList<DatatypeExpr> ia1 = new ArrayList<DatatypeExpr>();
        ia1.add(nctx.am.get("ip_client"));
        ia1.add(nctx.am.get("ip_nat1"));
        ia1.add(nctx.am.get("ip_nat2"));
        ia1.add(nctx.am.get("ip_nat3"));
        ia1.add(nctx.am.get("ip_nat4"));
        ia1.add(nctx.am.get("ip_nat5"));
        ia1.add(nctx.am.get("ip_nat6"));
        ia1.add(nctx.am.get("ip_nat7"));
        nat8.natModel(nctx.am.get("ip_nat8"));
        nat8.setInternalAddress(ia1);
    }
    private void setRoutingnat3(NetContext nctx, Network net, ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt_nat3){
        rt_nat3.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_server"), nat4));
        rt_nat3.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_client"), nat2));
        rt_nat3.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat10"), nat4));
        rt_nat3.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat8"), nat4));
        rt_nat3.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat9"), nat4));
        rt_nat3.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat6"), nat4));
        rt_nat3.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat7"), nat4));
        rt_nat3.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat4"), nat4));
        rt_nat3.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat5"), nat4));
        rt_nat3.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat2"), nat2));
        rt_nat3.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat1"), nat2));
        net.routingTable(nat3, rt_nat3);
    }
    private void configureDevicenat3(NetContext nctx) {
        ArrayList<DatatypeExpr> ia2 = new ArrayList<DatatypeExpr>();
        ia2.add(nctx.am.get("ip_client"));
        ia2.add(nctx.am.get("ip_nat1"));
        ia2.add(nctx.am.get("ip_nat2"));
        nat3.natModel(nctx.am.get("ip_nat3"));
        nat3.setInternalAddress(ia2);
    }
    private void setRoutingnat2(NetContext nctx, Network net, ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt_nat2){
        rt_nat2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_server"), nat3));
        rt_nat2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_client"), nat1));
        rt_nat2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat10"), nat3));
        rt_nat2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat8"), nat3));
        rt_nat2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat9"), nat3));
        rt_nat2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat6"), nat3));
        rt_nat2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat7"), nat3));
        rt_nat2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat4"), nat3));
        rt_nat2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat5"), nat3));
        rt_nat2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat3"), nat3));
        rt_nat2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat1"), nat1));
        net.routingTable(nat2, rt_nat2);
    }
    private void configureDevicenat2(NetContext nctx) {
        ArrayList<DatatypeExpr> ia3 = new ArrayList<DatatypeExpr>();
        ia3.add(nctx.am.get("ip_client"));
        ia3.add(nctx.am.get("ip_nat1"));
        nat2.natModel(nctx.am.get("ip_nat2"));
        nat2.setInternalAddress(ia3);
    }
    private void setRoutingnat1(NetContext nctx, Network net, ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt_nat1){
        rt_nat1.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_server"), nat2));
        rt_nat1.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_client"), client));
        rt_nat1.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat10"), nat2));
        rt_nat1.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat8"), nat2));
        rt_nat1.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat9"), nat2));
        rt_nat1.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat6"), nat2));
        rt_nat1.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat7"), nat2));
        rt_nat1.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat4"), nat2));
        rt_nat1.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat5"), nat2));
        rt_nat1.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat2"), nat2));
        rt_nat1.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat3"), nat2));
        net.routingTable(nat1, rt_nat1);
    }
    private void configureDevicenat1(NetContext nctx) {
        ArrayList<DatatypeExpr> ia4 = new ArrayList<DatatypeExpr>();
        ia4.add(nctx.am.get("ip_client"));
        nat1.natModel(nctx.am.get("ip_nat1"));
        nat1.setInternalAddress(ia4);
    }
    private void setRoutingnat7(NetContext nctx, Network net, ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt_nat7){
        rt_nat7.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_server"), nat8));
        rt_nat7.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_client"), nat6));
        rt_nat7.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat10"), nat8));
        rt_nat7.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat8"), nat8));
        rt_nat7.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat9"), nat8));
        rt_nat7.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat6"), nat6));
        rt_nat7.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat4"), nat6));
        rt_nat7.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat5"), nat6));
        rt_nat7.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat2"), nat6));
        rt_nat7.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat3"), nat6));
        rt_nat7.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat1"), nat6));
        net.routingTable(nat7, rt_nat7);
    }
    private void configureDevicenat7(NetContext nctx) {
        ArrayList<DatatypeExpr> ia5 = new ArrayList<DatatypeExpr>();
        ia5.add(nctx.am.get("ip_client"));
        ia5.add(nctx.am.get("ip_nat1"));
        ia5.add(nctx.am.get("ip_nat2"));
        ia5.add(nctx.am.get("ip_nat3"));
        ia5.add(nctx.am.get("ip_nat4"));
        ia5.add(nctx.am.get("ip_nat5"));
        ia5.add(nctx.am.get("ip_nat6"));
        nat7.natModel(nctx.am.get("ip_nat7"));
        nat7.setInternalAddress(ia5);
    }
    private void setRoutingnat6(NetContext nctx, Network net, ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt_nat6){
        rt_nat6.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_server"), nat7));
        rt_nat6.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_client"), nat5));
        rt_nat6.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat10"), nat7));
        rt_nat6.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat8"), nat7));
        rt_nat6.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat9"), nat7));
        rt_nat6.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat7"), nat7));
        rt_nat6.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat4"), nat5));
        rt_nat6.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat5"), nat5));
        rt_nat6.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat2"), nat5));
        rt_nat6.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat3"), nat5));
        rt_nat6.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat1"), nat5));
        net.routingTable(nat6, rt_nat6);
    }
    private void configureDevicenat6(NetContext nctx) {
        ArrayList<DatatypeExpr> ia6 = new ArrayList<DatatypeExpr>();
        ia6.add(nctx.am.get("ip_client"));
        ia6.add(nctx.am.get("ip_nat1"));
        ia6.add(nctx.am.get("ip_nat2"));
        ia6.add(nctx.am.get("ip_nat3"));
        ia6.add(nctx.am.get("ip_nat4"));
        ia6.add(nctx.am.get("ip_nat5"));
        nat6.natModel(nctx.am.get("ip_nat6"));
        nat6.setInternalAddress(ia6);
    }
    private void setRoutingnat5(NetContext nctx, Network net, ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt_nat5){
        rt_nat5.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_server"), nat6));
        rt_nat5.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_client"), nat4));
        rt_nat5.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat10"), nat6));
        rt_nat5.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat8"), nat6));
        rt_nat5.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat9"), nat6));
        rt_nat5.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat6"), nat6));
        rt_nat5.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat7"), nat6));
        rt_nat5.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat4"), nat4));
        rt_nat5.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat2"), nat4));
        rt_nat5.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat3"), nat4));
        rt_nat5.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat1"), nat4));
        net.routingTable(nat5, rt_nat5);
    }
    private void configureDevicenat5(NetContext nctx) {
        ArrayList<DatatypeExpr> ia7 = new ArrayList<DatatypeExpr>();
        ia7.add(nctx.am.get("ip_client"));
        ia7.add(nctx.am.get("ip_nat1"));
        ia7.add(nctx.am.get("ip_nat2"));
        ia7.add(nctx.am.get("ip_nat3"));
        ia7.add(nctx.am.get("ip_nat4"));
        nat5.natModel(nctx.am.get("ip_nat5"));
        nat5.setInternalAddress(ia7);
    }
    private void setRoutingnat4(NetContext nctx, Network net, ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt_nat4){
        rt_nat4.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_server"), nat5));
        rt_nat4.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_client"), nat3));
        rt_nat4.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat10"), nat5));
        rt_nat4.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat8"), nat5));
        rt_nat4.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat9"), nat5));
        rt_nat4.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat6"), nat5));
        rt_nat4.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat7"), nat5));
        rt_nat4.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat5"), nat5));
        rt_nat4.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat2"), nat3));
        rt_nat4.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat3"), nat3));
        rt_nat4.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat1"), nat3));
        net.routingTable(nat4, rt_nat4);
    }
    private void configureDevicenat4(NetContext nctx) {
        ArrayList<DatatypeExpr> ia8 = new ArrayList<DatatypeExpr>();
        ia8.add(nctx.am.get("ip_client"));
        ia8.add(nctx.am.get("ip_nat1"));
        ia8.add(nctx.am.get("ip_nat2"));
        ia8.add(nctx.am.get("ip_nat3"));
        nat4.natModel(nctx.am.get("ip_nat4"));
        nat4.setInternalAddress(ia8);
    }
    private void setRoutingclient(NetContext nctx, Network net, ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt_client){
        rt_client.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_server"), nat1));
        rt_client.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat10"), nat1));
        rt_client.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat8"), nat1));
        rt_client.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat9"), nat1));
        rt_client.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat6"), nat1));
        rt_client.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat7"), nat1));
        rt_client.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat4"), nat1));
        rt_client.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat5"), nat1));
        rt_client.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat2"), nat1));
        rt_client.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat3"), nat1));
        rt_client.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat1"), nat1));
        net.routingTable(client, rt_client);
    }
    private void configureDeviceclient(NetContext nctx) {
        PacketModel pModel9 = new PacketModel();
        pModel9.setBody(String.valueOf("word").hashCode());
        pModel9.setProto(nctx.HTTP_REQUEST);
        pModel9.setUrl(String.valueOf("www.facebook.com").hashCode());
        pModel9.setIp_dest(nctx.am.get("server"));
        client.installEndHost(pModel9);
    }
    private void setRoutingserver(NetContext nctx, Network net, ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt_server){
        rt_server.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_client"), nat10));
        rt_server.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat10"), nat10));
        rt_server.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat8"), nat10));
        rt_server.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat9"), nat10));
        rt_server.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat6"), nat10));
        rt_server.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat7"), nat10));
        rt_server.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat4"), nat10));
        rt_server.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat5"), nat10));
        rt_server.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat2"), nat10));
        rt_server.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat3"), nat10));
        rt_server.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat1"), nat10));
        net.routingTable(server, rt_server);
    }
    private void configureDeviceserver(NetContext nctx) {
    }
    private void setRoutingnat10(NetContext nctx, Network net, ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt_nat10){
        rt_nat10.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_server"), server));
        rt_nat10.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_client"), nat9));
        rt_nat10.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat8"), nat9));
        rt_nat10.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat9"), nat9));
        rt_nat10.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat6"), nat9));
        rt_nat10.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat7"), nat9));
        rt_nat10.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat4"), nat9));
        rt_nat10.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat5"), nat9));
        rt_nat10.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat2"), nat9));
        rt_nat10.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat3"), nat9));
        rt_nat10.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat1"), nat9));
        net.routingTable(nat10, rt_nat10);
    }
    private void configureDevicenat10(NetContext nctx) {
        ArrayList<DatatypeExpr> ia11 = new ArrayList<DatatypeExpr>();
        ia11.add(nctx.am.get("ip_client"));
        ia11.add(nctx.am.get("ip_nat1"));
        ia11.add(nctx.am.get("ip_nat2"));
        ia11.add(nctx.am.get("ip_nat3"));
        ia11.add(nctx.am.get("ip_nat4"));
        ia11.add(nctx.am.get("ip_nat5"));
        ia11.add(nctx.am.get("ip_nat6"));
        ia11.add(nctx.am.get("ip_nat7"));
        ia11.add(nctx.am.get("ip_nat8"));
        ia11.add(nctx.am.get("ip_nat9"));
        nat10.natModel(nctx.am.get("ip_nat10"));
        nat10.setInternalAddress(ia11);
    }
    public Test10(Context ctx){
        NetContext nctx = new NetContext (ctx,new String[]{"nat9", "nat8", "nat3", "nat2", "nat1", "nat7", "nat6", "nat5", "nat4", "client", "server", "nat10"}, new String[]{"ip_nat9", "ip_nat8", "ip_nat3", "ip_nat2", "ip_nat1", "ip_nat7", "ip_nat6", "ip_nat5", "ip_nat4", "ip_client", "ip_server", "ip_nat10"});
        Network net = new Network (ctx,new Object[]{nctx});
        setDevices(ctx, nctx, net);
        ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>> adm = new ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>>();
        doMappings(nctx, adm);
        net.setAddressMappings(adm);
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt_nat9 = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>(); 
        setRoutingnat9(nctx, net, rt_nat9);
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt_nat8 = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>(); 
        setRoutingnat8(nctx, net, rt_nat8);
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt_nat3 = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>(); 
        setRoutingnat3(nctx, net, rt_nat3);
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt_nat2 = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>(); 
        setRoutingnat2(nctx, net, rt_nat2);
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt_nat1 = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>(); 
        setRoutingnat1(nctx, net, rt_nat1);
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt_nat7 = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>(); 
        setRoutingnat7(nctx, net, rt_nat7);
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt_nat6 = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>(); 
        setRoutingnat6(nctx, net, rt_nat6);
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt_nat5 = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>(); 
        setRoutingnat5(nctx, net, rt_nat5);
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt_nat4 = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>(); 
        setRoutingnat4(nctx, net, rt_nat4);
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt_client = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>(); 
        setRoutingclient(nctx, net, rt_client);
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt_server = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>(); 
        setRoutingserver(nctx, net, rt_server);
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt_nat10 = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>(); 
        setRoutingnat10(nctx, net, rt_nat10);
        net.attach(nat9, nat8, nat3, nat2, nat1, nat7, nat6, nat5, nat4, client, server, nat10);
        configureDevicenat9(nctx);
        configureDevicenat8(nctx);
        configureDevicenat3(nctx);
        configureDevicenat2(nctx);
        configureDevicenat1(nctx);
        configureDevicenat7(nctx);
        configureDevicenat6(nctx);
        configureDevicenat5(nctx);
        configureDevicenat4(nctx);
        configureDeviceclient(nctx);
        configureDeviceserver(nctx);
        configureDevicenat10(nctx);
        check = new Checker(ctx,nctx,net);
    }
}

