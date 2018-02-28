package it.polito.verigraph.scalability.tests;

/**
 * <p/>  Custom test  <p/>
 *  | A | <------> | CACHE |<------> | B |
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.microsoft.z3.Context;
import com.microsoft.z3.DatatypeExpr;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.Model;
import com.microsoft.z3.Status;
import com.microsoft.z3.Z3Exception;

import it.polito.verigraph.mcnet.components.Checker;
import it.polito.verigraph.mcnet.components.Checker.Prop;
import it.polito.verigraph.mcnet.components.IsolationResult;
import it.polito.verigraph.mcnet.components.NetContext;
import it.polito.verigraph.mcnet.components.Network;
import it.polito.verigraph.mcnet.components.NetworkObject;
import it.polito.verigraph.mcnet.components.Tuple;
import it.polito.verigraph.mcnet.netobjs.PolitoEndHost;
import it.polito.verigraph.mcnet.netobjs.AclFirewall;

public class TestIsolation {

    public Checker check;
    public Context ctx;
    public PolitoEndHost a,b;
    public AclFirewall x;

    public  TestIsolation(){
        ctx = new Context();

        NetContext nctx = new NetContext (ctx,new String[]{"aC", "b","x"},
                                                new String[]{"ip_a", "ip_b", "ip_x"});
        Network net = new Network (ctx,new Object[]{nctx});

        a = new PolitoEndHost(ctx, new Object[]{nctx.nm.get("aC"), net, nctx});
        b = new PolitoEndHost(ctx, new Object[]{nctx.nm.get("b"), net, nctx});
        x = new AclFirewall(ctx, new Object[]{nctx.nm.get("x"), net, nctx});

        ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>> adm = new ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>>();
        ArrayList<DatatypeExpr> al1 = new ArrayList<DatatypeExpr>();
        ArrayList<DatatypeExpr> al2 = new ArrayList<DatatypeExpr>();
        ArrayList<DatatypeExpr> al3 = new ArrayList<DatatypeExpr>();
        al1.add(nctx.am.get("ip_a"));
        al2.add(nctx.am.get("ip_b"));
        al3.add(nctx.am.get("ip_x"));
        
        adm.add(new Tuple<>(a, al1));
        adm.add(new Tuple<>(b, al2));
        adm.add(new Tuple<>(x, al3));
        net.setAddressMappings(adm);

        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt1 = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt1.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_b"), x));
        net.routingTable(a, rt1);

        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt2 = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_a"), x));
        net.routingTable(b, rt2);
        
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rtX = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rtX.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_b"), b));
        rtX.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_a"), a));
        net.routingTable(x, rtX);

        net.attach(a, b,x);
        
        a.installEndHost(null);

        ArrayList<Tuple<DatatypeExpr,DatatypeExpr>> acl = new ArrayList<Tuple<DatatypeExpr,DatatypeExpr>>();
        //acl.add(new Tuple<DatatypeExpr,DatatypeExpr>(nctx.am.get("ip_x"),nctx.am.get("ip_b")));
        acl.add(new Tuple<DatatypeExpr,DatatypeExpr>(nctx.am.get("ip_a"),nctx.am.get("ip_b")));
        x.addAcls(acl);
        
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
        TestIsolation model = new TestIsolation();
        model.resetZ3();
        
        //model.check.propertyAdd(model.a, model.b, Prop.ISOLATION);
        model.check.propertyAdd(model.a, model.b, Prop.REACHABILITY);
        IsolationResult ret = model.check.propertyCheck();
        if (ret.result == Status.UNSATISFIABLE){
           System.out.println("UNSAT"); // Nodes a and b are isolated
        }else{
            System.out.println("SAT ");
            System.out.println(ret.model);
        }
    }
}