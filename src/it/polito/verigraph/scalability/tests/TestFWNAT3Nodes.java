package it.polito.verigraph.scalability.tests;

/**
 * <p/>  Custom test  <p/>
 *  | A | <------> | CACHE | <------> | CACHE |<------> | B |
 */

import java.util.ArrayList;
import java.util.HashMap;


import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.DatatypeExpr;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.Model;
import com.microsoft.z3.Status;
import com.microsoft.z3.Z3Exception;

import it.polito.verifoo.components.RoutingTable;
import it.polito.verigraph.mcnet.components.Checker;
import it.polito.verigraph.mcnet.components.Checker.Prop;
import it.polito.verigraph.mcnet.components.IsolationResult;
import it.polito.verigraph.mcnet.components.NetContext;
import it.polito.verigraph.mcnet.components.Network;
import it.polito.verigraph.mcnet.components.NetworkObject;
import it.polito.verigraph.mcnet.components.Quattro;
import it.polito.verigraph.mcnet.components.Tuple;
import it.polito.verigraph.mcnet.netobjs.PolitoEndHost;
import it.polito.verigraph.mcnet.netobjs.PolitoNat;
import it.polito.verigraph.mcnet.netobjs.AclFirewall;
import it.polito.verigraph.mcnet.netobjs.AclFirewallAuto;
import it.polito.verigraph.mcnet.netobjs.PolitoCache;

public class TestFWNAT3Nodes {

    public Checker check;
    public Context ctx;
    public PolitoEndHost a,b,c;
    public AclFirewallAuto fw1,fw2;
    public PolitoNat nat;

    public  TestFWNAT3Nodes(){
     /*   ctx = new Context();

        NetContext nctx  = new NetContext (ctx,new String[]{"a", "b","c", "nat","fw2","fw1"},
                                                new String[]{"ip_a", "ip_b", "ip_c","ip_nat", "ip_fw2", "ip_fw1"});
        Network net = new Network (ctx,new Object[]{nctx});

        a = new PolitoEndHost(ctx, new Object[]{nctx.nm.get("a"), net, nctx});
        b = new PolitoEndHost(ctx, new Object[]{nctx.nm.get("b"), net, nctx});
        c = new PolitoEndHost(ctx, new Object[]{nctx.nm.get("c"), net, nctx});
        nat = new PolitoNat(ctx, new Object[]{nctx.nm.get("nat"), net, nctx});
        fw1= new AclFirewallAuto(ctx, new Object[]{nctx.nm.get("fw1"), net, nctx});
        fw2= new AclFirewallAuto(ctx, new Object[]{nctx.nm.get("fw2"), net, nctx});

        ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>> adm = new ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>>();
        ArrayList<DatatypeExpr> al1 = new ArrayList<DatatypeExpr>();
        ArrayList<DatatypeExpr> al2 = new ArrayList<DatatypeExpr>();
        ArrayList<DatatypeExpr> al3 = new ArrayList<DatatypeExpr>();
        ArrayList<DatatypeExpr> al4 = new ArrayList<DatatypeExpr>();
        ArrayList<DatatypeExpr> al5 = new ArrayList<DatatypeExpr>();
        ArrayList<DatatypeExpr> al6 = new ArrayList<DatatypeExpr>();
        al1.add(nctx.am.get("ip_a"));
        al2.add(nctx.am.get("ip_b"));
        al6.add(nctx.am.get("ip_c"));
        al3.add(nctx.am.get("ip_nat"));
        al4.add(nctx.am.get("ip_fw2"));
        al5.add(nctx.am.get("ip_fw1"));
        adm.add(new Tuple<>(a, al1));
        adm.add(new Tuple<>(b, al2));
        adm.add(new Tuple<>(c, al6));
        adm.add(new Tuple<>(nat, al3));
        adm.add(new Tuple<>(fw2, al4));
        adm.add(new Tuple<>(fw1, al5));
        net.setAddressMappings(adm);

        
        ArrayList<RoutingTable> rta = new ArrayList<RoutingTable>();
        rta.add(new RoutingTable(nctx.am.get("ip_b"), fw1,NetContext.a_fw1,nctx.y1));
        rta.add(new RoutingTable(nctx.am.get("ip_b"), nat,NetContext.a_nat,ctx.mkNot(nctx.y1)));
        net.routingTable2(a, rta);
        
        ArrayList<RoutingTable> rtfw1 = new ArrayList<RoutingTable>();
        rtfw1.add(new RoutingTable(nctx.am.get("ip_b"), nat,NetContext.fw1_nat,nctx.y1));
        rtfw1.add(new RoutingTable(nctx.am.get("ip_b"), fw1,NetContext.fw1_nat,ctx.mkNot(nctx.y1)));
        net.routingTable2(fw1, rtfw1);

        ArrayList<RoutingTable> rtnat = new ArrayList<RoutingTable>();
        rtnat.add(new RoutingTable(nctx.am.get("ip_b"), fw2,NetContext.nat_fw2,nctx.y2));
        rtnat.add(new RoutingTable(nctx.am.get("ip_b"), b,NetContext.nat_b,ctx.mkNot(nctx.y2)));
        net.routingTable2(nat, rtnat);
        
        ArrayList<RoutingTable> rtfw2 = new ArrayList<RoutingTable>();
        rtfw2.add(new RoutingTable(nctx.am.get("ip_b"), b,NetContext.fw2_b,nctx.y2));
        rtfw2.add(new RoutingTable(nctx.am.get("ip_b"), fw2,NetContext.fw2_b,ctx.mkNot(nctx.y2)));
        net.routingTable2(fw2, rtfw2);       
        
        ArrayList<RoutingTable> rtc = new ArrayList<RoutingTable>();
        rtc.add(new RoutingTable(nctx.am.get("ip_b"), fw1,NetContext.c_fw1,nctx.y1));
        rtc.add(new RoutingTable(nctx.am.get("ip_b"), nat,NetContext.c_nat,ctx.mkNot(nctx.y1))); 
        net.routingTable2(c, rtc); 
        
        ArrayList<RoutingTable> rtb = new ArrayList<RoutingTable>();
        net.routingTable2(b, rtb); 
        
        net.attach(a, b,c, nat,fw2,fw1);
        
        ArrayList<DatatypeExpr> ia = new ArrayList<DatatypeExpr>();
	    ia.add(nctx.am.get("ip_a"));
	    ia.add(nctx.am.get("ip_c"));
	   // ia.add(nctx.am.get("ip_fw1"));
	    nat.natModel(nctx.am.get("ip_nat"));
	    nat.setInternalAddress(ia);
	    
	    ArrayList<Tuple<DatatypeExpr,DatatypeExpr>> acl = new ArrayList<Tuple<DatatypeExpr,DatatypeExpr>>();
        fw2.addAcls(acl);
        
        ArrayList<Tuple<DatatypeExpr,DatatypeExpr>> acl2 = new ArrayList<Tuple<DatatypeExpr,DatatypeExpr>>();
        fw1.addAcls(acl2);
        
        check = new Checker(ctx,nctx,net);*/
}
    
    public void resetZ3() throws Z3Exception{
        HashMap<String, String> cfg = new HashMap<String, String>();
        cfg.put("model", "true");
         ctx = new Context(cfg);
    }
    
    public void printVector (Object[] array){
        int i=0;
        System.out.println( "*** Printing vector ***");
        for (Object a : array){
            i+=1;
            System.out.println( "#"+i);
            System.out.println(a);
            System.out.println(  "*** "+ i+ " elements printed! ***");
        }
    }
    
    public void printModel (Model model) throws Z3Exception{
        for (FuncDecl d : model.getFuncDecls()){
            System.out.println(d.getName() +" = "+ d.toString());
              System.out.println("");
        }
    }


    public static void main(String[] args) throws Z3Exception
    {
        TestFWNAT3Nodes model = new TestFWNAT3Nodes();
        model.resetZ3();
        
        //IsolationResult ret =model.check.checkRealIsolationProperty(model.a,model.b);
        model.check.propertyAdd(model.a, model.b, Prop.ISOLATION);
        model.check.propertyAdd(model.c, model.b, Prop.ISOLATION);
        IsolationResult ret= model.check.propertyCheck();
        if (ret.result == Status.UNSATISFIABLE){
           System.out.println("UNSAT"); // Nodes a and b are isolated
        }else{
            System.out.println("SAT ");
            System.out.println(ret.model);
//            System.out.print( "Model -> ");model.printModel(ret.model);
//          System.out.println( "Violating packet -> " +ret.violating_packet);
//          System.out.println("Last hop -> " +ret.last_hop);
//          System.out.println("Last send_time -> " +ret.last_send_time);
//          System.out.println( "Last recv_time -> " +ret.last_recv_time);
        }
    }
}