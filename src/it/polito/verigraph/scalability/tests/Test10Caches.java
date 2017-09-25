package it.polito.verigraph.scalability.tests;

/**
 * <p/>  Custom test  <p/>
 *  | A | <------> | CACHE 1 | <------> | CACHE 2 | <------> | CACHE 3 | <------> | CACHE 4 | <------> | CACHE 5 | <------> | CACHE 6 | <------> | CACHE 7 | <------> | CACHE 8 | <------> | CACHE 9 | <------> | CACHE 0 |<------> | B |
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
import it.polito.verigraph.mcnet.netobjs.PolitoCache;

public class Test10Caches {

    public Checker check;
    public Context ctx;
    public PolitoEndHost a,b;
    public PolitoCache cache1, cache2, cache3,cache4,cache5,cache6,cache7,cache8,cache9,cache0;

    public  Test10Caches(){
        ctx = new Context();

        NetContext nctx = new NetContext (ctx,new String[]{"a", "b", "cache1","cache2","cache3", "cache4", "cache5", "cache6", "cache7", "cache8", "cache9", "cache0"},
                                                new String[]{"ip_a", "ip_b", "ip_cache1","ip_cache2","ip_cache3", "ip_cache4", "ip_cache5", "ip_cache6", "ip_cache7", "ip_cache8", "ip_cache9", "ip_cache0"});
        Network net = new Network (ctx,new Object[]{nctx});

        a = new PolitoEndHost(ctx, new Object[]{nctx.nm.get("a"), net, nctx});
        b = new PolitoEndHost(ctx, new Object[]{nctx.nm.get("b"), net, nctx});
        cache1 = new PolitoCache(ctx, new Object[]{nctx.nm.get("cache1"), net, nctx});
        cache2 = new PolitoCache(ctx, new Object[]{nctx.nm.get("cache2"), net, nctx});
        cache3 = new PolitoCache(ctx, new Object[]{nctx.nm.get("cache3"), net, nctx});
        cache4 = new PolitoCache(ctx, new Object[]{nctx.nm.get("cache4"), net, nctx});
        cache5 = new PolitoCache(ctx, new Object[]{nctx.nm.get("cache5"), net, nctx});
        cache6 = new PolitoCache(ctx, new Object[]{nctx.nm.get("cache6"), net, nctx});
        cache7 = new PolitoCache(ctx, new Object[]{nctx.nm.get("cache7"), net, nctx});
        cache8 = new PolitoCache(ctx, new Object[]{nctx.nm.get("cache8"), net, nctx});
        cache9 = new PolitoCache(ctx, new Object[]{nctx.nm.get("cache9"), net, nctx});
        cache0 = new PolitoCache(ctx, new Object[]{nctx.nm.get("cache0"), net, nctx});
        
        ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>> adm = new ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>>();
        ArrayList<DatatypeExpr> al1 = new ArrayList<DatatypeExpr>();
        ArrayList<DatatypeExpr> al2 = new ArrayList<DatatypeExpr>();
        ArrayList<DatatypeExpr> al3 = new ArrayList<DatatypeExpr>();
        ArrayList<DatatypeExpr> al4 = new ArrayList<DatatypeExpr>();
        ArrayList<DatatypeExpr> al5 = new ArrayList<DatatypeExpr>();
        ArrayList<DatatypeExpr> al6 = new ArrayList<DatatypeExpr>();
        ArrayList<DatatypeExpr> al65 = new ArrayList<DatatypeExpr>();
        ArrayList<DatatypeExpr> al66 = new ArrayList<DatatypeExpr>();
        ArrayList<DatatypeExpr> al67 = new ArrayList<DatatypeExpr>();
        ArrayList<DatatypeExpr> al68 = new ArrayList<DatatypeExpr>();
        ArrayList<DatatypeExpr> al69 = new ArrayList<DatatypeExpr>();
        ArrayList<DatatypeExpr> al60 = new ArrayList<DatatypeExpr>();
        al1.add(nctx.am.get("ip_a"));
        al2.add(nctx.am.get("ip_b"));
        al3.add(nctx.am.get("ip_cache1"));
        al4.add(nctx.am.get("ip_cache2"));
        al5.add(nctx.am.get("ip_cache3"));
        al6.add(nctx.am.get("ip_cache4"));
        al65.add(nctx.am.get("ip_cache5"));
        al66.add(nctx.am.get("ip_cache6"));
        al67.add(nctx.am.get("ip_cache7"));
        al68.add(nctx.am.get("ip_cache8"));
        al69.add(nctx.am.get("ip_cache9"));
        al60.add(nctx.am.get("ip_cache0"));
        adm.add(new Tuple<>(a, al1));
        adm.add(new Tuple<>(b, al2));
        adm.add(new Tuple<>(cache1, al3));
        adm.add(new Tuple<>(cache2, al4));
        adm.add(new Tuple<>(cache3, al5));
        adm.add(new Tuple<>(cache4, al6));
        adm.add(new Tuple<>(cache5, al65));
        adm.add(new Tuple<>(cache6, al66));
        adm.add(new Tuple<>(cache7, al67));
        adm.add(new Tuple<>(cache8, al68));
        adm.add(new Tuple<>(cache9, al69));
        adm.add(new Tuple<>(cache0, al60));
        net.setAddressMappings(adm);

        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt1 = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt1.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache1"), cache1));
        rt1.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache2"), cache1));
        rt1.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache3"), cache1));
        rt1.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache4"), cache1));
        rt1.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache5"), cache1));
        rt1.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache6"), cache1));
        rt1.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache7"), cache1));
        rt1.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache8"), cache1));
        rt1.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache9"), cache1));
        rt1.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache0"), cache1));
        rt1.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_b"), cache1));

        net.routingTable(a, rt1);

        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt2 = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_a"), cache0));
        rt2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache1"), cache0));
        rt2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache2"), cache0));
        rt2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache3"), cache0));
        rt2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache4"), cache0));
        rt2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache5"), cache0));
        rt2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache6"), cache0));
        rt2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache7"), cache0));
        rt2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache8"), cache0));
        rt2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache9"), cache0));
        rt2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache0"), cache0));

        net.routingTable(b, rt2);

        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt3 = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt3.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_a"),a));
        rt3.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache2"),cache2));
        rt3.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache3"),cache2));
        rt2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache4"), cache2));
        rt2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache5"), cache2));
        rt2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache6"), cache2));
        rt2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache7"), cache2));
        rt2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache8"), cache2));
        rt2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache9"), cache2));
        rt2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache0"), cache2));
        rt3.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_b"),cache2));

        net.routingTable(cache1, rt3);

        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt4 = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt4.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_a"),cache1));
        rt4.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache1"),cache1));
        rt4.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache3"),cache3));
        rt4.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache4"),cache3));
        rt4.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache5"),cache3));
        rt4.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache6"),cache3));
        rt4.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache7"),cache3));
        rt4.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache8"),cache3));
        rt4.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache9"), cache3));
        rt4.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache0"), cache3));
        rt4.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_b"),cache3));

        net.routingTable(cache2, rt4);

        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt5 = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt5.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_a"),cache2));
        rt5.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache1"),cache2));
        rt5.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache2"),cache2));
        rt5.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache4"), cache4));
        rt5.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache5"), cache4));
        rt5.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache6"), cache4));
        rt5.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache7"), cache4));
        rt5.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache8"), cache4));
        rt5.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache9"), cache4));
        rt5.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache0"), cache4));
        rt5.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_b"),cache4));

        net.routingTable(cache3, rt5);
        
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt6 = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt6.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_a"),cache3));
        rt6.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache1"),cache3));
        rt6.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache2"),cache3));
        rt6.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache3"), cache3));
        rt6.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache5"), cache5));
        rt6.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache6"), cache5));
        rt6.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache7"), cache5));
        rt6.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache8"), cache5));
        rt6.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache9"), cache5));
        rt6.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache0"), cache5));
        rt6.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_b"),cache5));

        net.routingTable(cache4, rt6);
        
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt7 = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt7.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_a"),cache4));
        rt7.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache1"),cache4));
        rt7.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache2"),cache4));
        rt7.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache3"), cache4));
        rt7.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache4"), cache4));
        rt7.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache6"), cache6));
        rt7.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache7"), cache6));
        rt7.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache8"), cache6));
        rt7.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache9"), cache6));
        rt7.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache0"), cache6));
        rt7.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_b"),cache6));

        net.routingTable(cache5, rt7);
        
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt8 = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt8.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_a"),cache5));
        rt8.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache1"),cache5));
        rt8.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache2"),cache5));
        rt8.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache3"), cache5));
        rt8.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache4"), cache5));
        rt8.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache5"), cache5));
        rt8.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache7"), cache7));
        rt8.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache8"), cache7));
        rt8.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache9"), cache7));
        rt8.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache0"), cache7));
        rt8.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_b"),cache7));

        net.routingTable(cache6, rt8);
        
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt9 = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt9.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_a"),cache6));
        rt9.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache1"),cache6));
        rt9.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache2"),cache6));
        rt9.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache3"), cache6));
        rt9.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache4"), cache6));
        rt9.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache5"), cache6));
        rt9.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache6"), cache6));
        rt9.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache8"), cache8));
        rt9.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache9"), cache8));
        rt9.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache0"), cache8));
        rt9.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_b"),cache8));

        net.routingTable(cache7, rt9);
        
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt10 = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt10.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_a"),cache7));
        rt10.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache1"),cache7));
        rt10.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache2"),cache7));
        rt10.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache3"), cache7));
        rt10.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache4"), cache7));
        rt10.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache5"), cache7));
        rt10.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache6"), cache7));
        rt10.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache7"), cache7));
        rt10.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache9"), cache9));
        rt10.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache0"), cache9));
        rt10.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_b"),cache9));

        net.routingTable(cache8, rt10);
        
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt11 = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt11.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_a"),cache8));
        rt11.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache1"),cache8));
        rt11.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache2"),cache8));
        rt11.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache3"), cache8));
        rt11.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache4"), cache8));
        rt11.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache5"), cache8));
        rt11.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache6"), cache8));
        rt11.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache7"), cache8));
        rt11.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache8"), cache8));
        rt11.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache0"), cache0));
        rt11.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_b"),cache0));

        net.routingTable(cache9, rt11);
        
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt12 = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt12.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_a"),cache9));
        rt12.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache1"),cache9));
        rt12.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache2"),cache9));
        rt12.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache3"), cache9));
        rt12.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache4"), cache9));
        rt12.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache5"), cache9));
        rt12.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache6"), cache9));
        rt12.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache7"), cache9));
        rt12.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache8"), cache9));
        rt12.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache9"), cache9));
        rt12.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_b"),b));

        net.routingTable(cache0, rt12);

        net.attach(a, b, cache1, cache2, cache3, cache4, cache5, cache6, cache7, cache8, cache9, cache0);

        //Configuring middleboxes
        cache1.installCache(new NetworkObject[]{nctx.nm.get("a")});
        cache2.installCache(new NetworkObject[]{nctx.nm.get("cache1")});
        cache3.installCache(new NetworkObject[]{nctx.nm.get("cache2")});
        cache4.installCache(new NetworkObject[]{nctx.nm.get("cache3")});
        cache5.installCache(new NetworkObject[]{nctx.nm.get("cache4")});
        cache6.installCache(new NetworkObject[]{nctx.nm.get("cache5")});
        cache7.installCache(new NetworkObject[]{nctx.nm.get("cache6")});
        cache8.installCache(new NetworkObject[]{nctx.nm.get("cache7")});
        cache9.installCache(new NetworkObject[]{nctx.nm.get("cache8")});
        cache0.installCache(new NetworkObject[]{nctx.nm.get("cache9")});

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
        Test10Caches model = new Test10Caches();
        model.resetZ3();
        
        IsolationResult ret =model.check.checkIsolationProperty(model.a,model.b);
        //model.printVector(ret.assertions);
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