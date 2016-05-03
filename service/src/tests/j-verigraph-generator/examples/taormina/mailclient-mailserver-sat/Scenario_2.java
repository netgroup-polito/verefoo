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
import mcnet.netobjs.PolitoAntispam;
import mcnet.netobjs.EndHost;
import mcnet.netobjs.PolitoNat;
public class Scenario_2{
    public Checker check;
    public AclFirewall firewall;
    public EndHost mailclient;
    public PolitoAntispam antispam;
    public EndHost mailserver;
    public PolitoNat nat;
    public Scenario_2(Context ctx){
        NetContext nctx = new NetContext (ctx,new String[]{"firewall", "mailclient", "antispam", "mailserver", "nat"}, new String[]{"ip_firewall", "ip_mailclient", "ip_antispam", "ip_mailserver", "ip_nat"});
        Network net = new Network (ctx,new Object[]{nctx});
        firewall = new AclFirewall(ctx, new Object[]{nctx.nm.get("firewall"), net, nctx});
        mailclient = new EndHost(ctx, new Object[]{nctx.nm.get("mailclient"), net, nctx});
        antispam = new PolitoAntispam(ctx, new Object[]{nctx.nm.get("antispam"), net, nctx});
        mailserver = new EndHost(ctx, new Object[]{nctx.nm.get("mailserver"), net, nctx});
        nat = new PolitoNat(ctx, new Object[]{nctx.nm.get("nat"), net, nctx});
        ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>> adm = new ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>>();
        ArrayList<DatatypeExpr> al0 = new ArrayList<DatatypeExpr>();
        al0.add(nctx.am.get("ip_firewall"));
        adm.add(new Tuple<>(firewall, al0));
        ArrayList<DatatypeExpr> al1 = new ArrayList<DatatypeExpr>();
        al1.add(nctx.am.get("ip_mailclient"));
        adm.add(new Tuple<>(mailclient, al1));
        ArrayList<DatatypeExpr> al2 = new ArrayList<DatatypeExpr>();
        al2.add(nctx.am.get("ip_antispam"));
        adm.add(new Tuple<>(antispam, al2));
        ArrayList<DatatypeExpr> al3 = new ArrayList<DatatypeExpr>();
        al3.add(nctx.am.get("ip_mailserver"));
        adm.add(new Tuple<>(mailserver, al3));
        ArrayList<DatatypeExpr> al4 = new ArrayList<DatatypeExpr>();
        al4.add(nctx.am.get("ip_nat"));
        adm.add(new Tuple<>(nat, al4));
        net.setAddressMappings(adm);
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt_firewall = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt_firewall.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_antispam"), nat));
        rt_firewall.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_mailclient"), nat));
        rt_firewall.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat"), nat));
        rt_firewall.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_mailserver"), mailserver));
        net.routingTable(firewall, rt_firewall);
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt_mailclient = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt_mailclient.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_firewall"), antispam));
        rt_mailclient.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_antispam"), antispam));
        rt_mailclient.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat"), antispam));
        rt_mailclient.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_mailserver"), antispam));
        net.routingTable(mailclient, rt_mailclient);
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt_antispam = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt_antispam.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_firewall"), nat));
        rt_antispam.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_mailclient"), mailclient));
        rt_antispam.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat"), nat));
        rt_antispam.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_mailserver"), nat));
        net.routingTable(antispam, rt_antispam);
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt_mailserver = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt_mailserver.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_firewall"), firewall));
        rt_mailserver.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_antispam"), firewall));
        rt_mailserver.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_mailclient"), firewall));
        rt_mailserver.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_nat"), firewall));
        net.routingTable(mailserver, rt_mailserver);
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt_nat = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt_nat.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_firewall"), firewall));
        rt_nat.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_antispam"), antispam));
        rt_nat.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_mailclient"), antispam));
        rt_nat.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_mailserver"), firewall));
        net.routingTable(nat, rt_nat);
        net.attach(firewall, mailclient, antispam, mailserver, nat);
        ArrayList<DatatypeExpr> ia = new ArrayList<DatatypeExpr>();
        ia.add(nctx.am.get("ip_mailclient"));
        ia.add(nctx.am.get("ip_antispam"));
        nat.setInternalAddress(ia);
        check = new Checker(ctx,nctx,net);
    }
}

