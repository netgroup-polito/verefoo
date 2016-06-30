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
import mcnet.netobjs.PolitoMailClient;
import mcnet.netobjs.PolitoMailServer;
import mcnet.netobjs.PolitoNat;
public class Test_1{
    public Checker check;
    public AclFirewall firewall;
    public PolitoMailServer mailserver;
    public PolitoNat nat;
    public PolitoMailClient user1;
    public Test_1(Context ctx){
        NetContext nctx = new NetContext (ctx,new String[]{"firewall", "mailserver", "nat", "user1"}, new String[]{"ip_firewall", "ip_mailserver", "ip_nat", "ip_user1"});
        Network net = new Network (ctx,new Object[]{nctx});
        firewall = new AclFirewall(ctx, new Object[]{nctx.nm.get("firewall"), net, nctx});
        mailserver = new PolitoMailServer(ctx, new Object[]{nctx.nm.get("mailserver"), net, nctx});
        nat = new PolitoNat(ctx, new Object[]{nctx.nm.get("nat"), net, nctx});
        user1 = new PolitoMailClient(ctx, new Object[]{nctx.nm.get("user1"), net, nctx, nctx.am.get("ip_mailserver")});
        ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>> adm = new ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>>();
        ArrayList<DatatypeExpr> al0 = new ArrayList<DatatypeExpr>();
        al0.add(nctx.am.get("ip_firewall"));
        adm.add(new Tuple<>((NetworkObject)firewall, al0));
        ArrayList<DatatypeExpr> al1 = new ArrayList<DatatypeExpr>();
        al1.add(nctx.am.get("ip_mailserver"));
        adm.add(new Tuple<>((NetworkObject)mailserver, al1));
        ArrayList<DatatypeExpr> al2 = new ArrayList<DatatypeExpr>();
        al2.add(nctx.am.get("ip_nat"));
        adm.add(new Tuple<>((NetworkObject)nat, al2));
        ArrayList<DatatypeExpr> al3 = new ArrayList<DatatypeExpr>();
        al3.add(nctx.am.get("ip_user1"));
        adm.add(new Tuple<>((NetworkObject)user1, al3));
        net.setAddressMappings(adm);
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt_firewall = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt_firewall.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_user1"), nat));
        rt_firewall.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat"), nat));
        rt_firewall.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_mailserver"), mailserver));
        net.routingTable(firewall, rt_firewall);
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt_mailserver = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt_mailserver.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_firewall"), firewall));
        rt_mailserver.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_user1"), firewall));
        rt_mailserver.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat"), firewall));
        net.routingTable(mailserver, rt_mailserver);
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt_nat = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt_nat.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_firewall"), firewall));
        rt_nat.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_user1"), user1));
        rt_nat.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_mailserver"), firewall));
        net.routingTable(nat, rt_nat);
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt_user1 = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt_user1.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_firewall"), nat));
        rt_user1.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat"), nat));
        rt_user1.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_mailserver"), nat));
        net.routingTable(user1, rt_user1);
        net.attach(firewall, mailserver, nat, user1);
        ArrayList<DatatypeExpr> ia2 = new ArrayList<DatatypeExpr>();
        ia2.add(nctx.am.get("ip_user1"));
        nat.natModel(nctx.am.get("ip_nat"));
        nat.setInternalAddress(ia2);
        check = new Checker(ctx,nctx,net);
    }
}

