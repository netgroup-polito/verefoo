package it.polito.verigraph.scalability.tests;

/**
 * <p/>  Custom test  <p/>
 *  | A | <------> | FW | <------> | FW | <------> | FW |<------> | B |
 */

import java.util.ArrayList;
import java.util.HashMap;

import com.microsoft.z3.Context;
import com.microsoft.z3.DatatypeExpr;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.Model;
import com.microsoft.z3.Status;
import com.microsoft.z3.Z3Exception;

import it.polito.verigraph.mcnet.components.Checker;
import it.polito.verigraph.mcnet.components.IsolationResult;
import it.polito.verigraph.mcnet.components.NetContext;
import it.polito.verigraph.mcnet.components.Network;
import it.polito.verigraph.mcnet.components.NetworkObject;
import it.polito.verigraph.mcnet.components.Tuple;
import it.polito.verigraph.mcnet.netobjs.PolitoEndHost;
import it.polito.verigraph.mcnet.netobjs.AclFirewall;

public class Test3Firewalls {

    public Checker check;
    public Context ctx;
    public PolitoEndHost a,b;
    public AclFirewall fw1, fw2, fw3;

    public  Test3Firewalls(){
        ctx = new Context();

        NetContext nctx = new NetContext (ctx,new String[]{"a", "b", "fw1","fw2","fw3"},
                                                new String[]{"ip_a", "ip_b", "ip_fw1","ip_fw2","ip_fw3"});
        Network net = new Network (ctx,new Object[]{nctx});

        a = new PolitoEndHost(ctx, new Object[]{nctx.nm.get("a"), net, nctx});
        b = new PolitoEndHost(ctx, new Object[]{nctx.nm.get("b"), net, nctx});
        fw1 = new AclFirewall(ctx, new Object[]{nctx.nm.get("fw1"), net, nctx});
        fw2 = new AclFirewall(ctx, new Object[]{nctx.nm.get("fw2"), net, nctx});
        fw3 = new AclFirewall(ctx, new Object[]{nctx.nm.get("fw3"), net, nctx});

        ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>> adm = new ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>>();
        ArrayList<DatatypeExpr> al1 = new ArrayList<DatatypeExpr>();
        ArrayList<DatatypeExpr> al2 = new ArrayList<DatatypeExpr>();
        ArrayList<DatatypeExpr> al3 = new ArrayList<DatatypeExpr>();
        ArrayList<DatatypeExpr> al4 = new ArrayList<DatatypeExpr>();
        ArrayList<DatatypeExpr> al5 = new ArrayList<DatatypeExpr>();
        al1.add(nctx.am.get("ip_a"));
        al2.add(nctx.am.get("ip_b"));
        al3.add(nctx.am.get("ip_fw1"));
        al4.add(nctx.am.get("ip_fw2"));
        al5.add(nctx.am.get("ip_fw3"));
        adm.add(new Tuple<>(a, al1));
        adm.add(new Tuple<>(b, al2));
        adm.add(new Tuple<>(fw1, al3));
        adm.add(new Tuple<>(fw2, al4));
        adm.add(new Tuple<>(fw3, al5));
        net.setAddressMappings(adm);

        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt1 = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt1.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_fw1"), fw1));
        rt1.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_fw2"), fw1));
        rt1.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_fw3"), fw1));
        rt1.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_b"), fw1));

        net.routingTable(a, rt1);

        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt2 = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_a"), fw3));
        rt2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_fw1"), fw3));
        rt2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_fw2"), fw3));
        rt2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_fw3"), fw3));

        net.routingTable(b, rt2);

        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt3 = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt3.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_a"),a));
        rt3.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_fw2"),fw2));
        rt3.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_fw3"),fw2));
        rt3.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_b"),fw2));

        net.routingTable(fw1, rt3);

        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt4 = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt4.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_a"),fw1));
        rt4.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_fw1"),fw1));
        rt4.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_fw3"),fw3));
        rt4.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_b"),fw3));

        net.routingTable(fw2, rt4);

        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt5 = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt5.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_a"),fw2));
        rt5.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_fw1"),fw2));
        rt5.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_fw2"),fw2));
        rt5.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_b"),b));

        net.routingTable(fw3, rt5);

        net.attach(a, b, fw1, fw2, fw3);

        //Configuring middleboxes
        ArrayList<Tuple<DatatypeExpr,DatatypeExpr>> acl = new ArrayList<Tuple<DatatypeExpr,DatatypeExpr>>();
        acl.add(new Tuple<DatatypeExpr,DatatypeExpr>(nctx.am.get("ip_a"),nctx.am.get("ip_fw1")));
        
        fw1.addAcls(acl);
        fw2.addAcls(acl);
        fw3.addAcls(acl);

        check = new Checker(ctx,nctx,net);
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
        Test3Firewalls model = new Test3Firewalls();
        model.resetZ3();
        
        IsolationResult ret =model.check.checkIsolationProperty(model.a,model.b);
        model.printVector(ret.assertions);
        if (ret.result == Status.UNSATISFIABLE){
           System.out.println("UNSAT"); // Nodes a and b are isolated
        }else{
            System.out.println("**************************************************");
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