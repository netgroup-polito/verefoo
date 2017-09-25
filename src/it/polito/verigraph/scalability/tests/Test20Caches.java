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

public class Test20Caches {

    public Checker check;
    public Context ctx;
    public PolitoEndHost a,b;
    public PolitoCache cache1, cache2, cache3,cache4,cache5,cache6,cache7,cache8,cache9,cache0;
    public PolitoCache cache11, cache21, cache31,cache41,cache51,cache61,cache71,cache81,cache91,cache01;

    public  Test20Caches(){
        ctx = new Context();

        NetContext nctx = new NetContext (ctx,new String[]{"a", "b", "cache1","cache2","cache3", "cache4", "cache5", "cache6", "cache7", "cache8", "cache9", "cache0"
        		,"cache01", "cache11","cache21","cache31", "cache41", "cache51", "cache61", "cache71", "cache81", "cache91"},
                                                new String[]{"ip_a", "ip_b", "ip_cache1","ip_cache2","ip_cache3", "ip_cache4", "ip_cache5", "ip_cache6", "ip_cache7", "ip_cache8", "ip_cache9", "ip_cache0"
                                                		, "ip_cache01", "ip_cache11","ip_cache21","ip_cache31", "ip_cache41", "ip_cache51", "ip_cache61", "ip_cache71", "ip_cache81", "ip_cache91"});
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
        
        cache11 = new PolitoCache(ctx, new Object[]{nctx.nm.get("cache11"), net, nctx});
        cache21 = new PolitoCache(ctx, new Object[]{nctx.nm.get("cache21"), net, nctx});
        cache31 = new PolitoCache(ctx, new Object[]{nctx.nm.get("cache31"), net, nctx});
        cache41 = new PolitoCache(ctx, new Object[]{nctx.nm.get("cache41"), net, nctx});
        cache51 = new PolitoCache(ctx, new Object[]{nctx.nm.get("cache51"), net, nctx});
        cache61 = new PolitoCache(ctx, new Object[]{nctx.nm.get("cache61"), net, nctx});
        cache71 = new PolitoCache(ctx, new Object[]{nctx.nm.get("cache71"), net, nctx});
        cache81 = new PolitoCache(ctx, new Object[]{nctx.nm.get("cache81"), net, nctx});
        cache91 = new PolitoCache(ctx, new Object[]{nctx.nm.get("cache91"), net, nctx});
        cache01 = new PolitoCache(ctx, new Object[]{nctx.nm.get("cache01"), net, nctx});
        
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
        ArrayList<DatatypeExpr> al01 = new ArrayList<DatatypeExpr>();
        ArrayList<DatatypeExpr> al11 = new ArrayList<DatatypeExpr>();
        ArrayList<DatatypeExpr> al21 = new ArrayList<DatatypeExpr>();
        ArrayList<DatatypeExpr> al31 = new ArrayList<DatatypeExpr>();
        ArrayList<DatatypeExpr> al41 = new ArrayList<DatatypeExpr>();
        ArrayList<DatatypeExpr> al51 = new ArrayList<DatatypeExpr>();
        ArrayList<DatatypeExpr> al61 = new ArrayList<DatatypeExpr>();
        ArrayList<DatatypeExpr> al71 = new ArrayList<DatatypeExpr>();
        ArrayList<DatatypeExpr> al81 = new ArrayList<DatatypeExpr>();
        ArrayList<DatatypeExpr> al91 = new ArrayList<DatatypeExpr>();
        
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
        
        al11.add(nctx.am.get("ip_cache11"));
        al21.add(nctx.am.get("ip_cache21"));
        al31.add(nctx.am.get("ip_cache31"));
        al41.add(nctx.am.get("ip_cache41"));
        al51.add(nctx.am.get("ip_cache51"));
        al61.add(nctx.am.get("ip_cache61"));
        al71.add(nctx.am.get("ip_cache71"));
        al81.add(nctx.am.get("ip_cache81"));
        al91.add(nctx.am.get("ip_cache91"));
        al01.add(nctx.am.get("ip_cache01"));
        
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
        
        adm.add(new Tuple<>(cache11, al11));
        adm.add(new Tuple<>(cache21, al21));
        adm.add(new Tuple<>(cache31, al31));
        adm.add(new Tuple<>(cache41, al41));
        adm.add(new Tuple<>(cache51, al51));
        adm.add(new Tuple<>(cache61, al61));
        adm.add(new Tuple<>(cache71, al71));
        adm.add(new Tuple<>(cache81, al81));
        adm.add(new Tuple<>(cache91, al91));
        adm.add(new Tuple<>(cache01, al01));
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
        
        rt1.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache01"), cache1));
        rt1.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache11"), cache1));
        rt1.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache21"), cache1));
        rt1.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache31"), cache1));
        rt1.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache41"), cache1));
        rt1.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache51"), cache1));
        rt1.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache61"), cache1));
        rt1.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache71"), cache1));
        rt1.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache81"), cache1));
        rt1.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache91"), cache1));
        rt1.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_b"), cache1));
        net.routingTable(a, rt1);

        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt2 = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_a"), cache91));
        rt2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache1"), cache91));
        rt2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache2"), cache91));
        rt2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache3"), cache91));
        rt2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache4"), cache91));
        rt2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache5"), cache91));
        rt2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache6"), cache91));
        rt2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache7"), cache91));
        rt2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache8"), cache91));
        rt2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache9"), cache91));
        rt2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache0"), cache91));
        
        rt2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache11"), cache91));
        rt2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache21"), cache91));
        rt2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache31"), cache91));
        rt2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache41"), cache91));
        rt2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache51"), cache91));
        rt2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache61"), cache91));
        rt2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache71"), cache91));
        rt2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache81"), cache91));
        rt2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache91"), cache91));
        rt2.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache01"), cache91));
        net.routingTable(b, rt2);

        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt3 = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt3.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_a"),a));
        rt3.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache2"),cache2));
        rt3.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache3"),cache2));
        rt3.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache4"), cache2));
        rt3.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache5"), cache2));
        rt3.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache6"), cache2));
        rt3.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache7"), cache2));
        rt3.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache8"), cache2));
        rt3.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache9"), cache2));
        rt3.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache0"), cache2));
        
        rt3.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache11"),cache2));
        rt3.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache21"),cache2));
        rt3.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache31"),cache2));
        rt3.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache41"), cache2));
        rt3.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache51"), cache2));
        rt3.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache61"), cache2));
        rt3.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache71"), cache2));
        rt3.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache81"), cache2));
        rt3.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache91"), cache2));
        rt3.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache01"), cache2));
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
        
        rt4.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache11"),cache3));
        rt4.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache21"),cache3));
        rt4.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache31"),cache3));
        rt4.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache41"),cache3));
        rt4.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache51"),cache3));
        rt4.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache61"),cache3));
        rt4.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache71"),cache3));
        rt4.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache81"),cache3));
        rt4.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache91"), cache3));
        rt4.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache01"), cache3));
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
        
        rt5.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache11"),cache4));
        rt5.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache21"),cache4));
        rt5.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache31"),cache4));
        rt5.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache41"), cache4));
        rt5.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache51"), cache4));
        rt5.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache61"), cache4));
        rt5.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache71"), cache4));
        rt5.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache81"), cache4));
        rt5.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache91"), cache4));
        rt5.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache01"), cache4));
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
        
        rt6.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache11"),cache5));
        rt6.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache21"),cache5));
        rt6.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache31"), cache5));
        rt6.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache41"), cache5));
        rt6.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache51"), cache5));
        rt6.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache61"), cache5));
        rt6.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache71"), cache5));
        rt6.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache81"), cache5));
        rt6.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache91"), cache5));
        rt6.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache01"), cache5));
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
        
        rt7.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache11"),cache6));
        rt7.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache21"),cache6));
        rt7.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache31"), cache6));
        rt7.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache41"), cache6));
        rt7.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache51"), cache6));
        rt7.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache61"), cache6));
        rt7.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache71"), cache6));
        rt7.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache81"), cache6));
        rt7.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache91"), cache6));
        rt7.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache01"), cache6));
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
        
        rt8.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache11"),cache7));
        rt8.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache21"),cache7));
        rt8.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache31"), cache7));
        rt8.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache41"), cache7));
        rt8.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache51"), cache7));
        rt8.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache61"), cache7));
        rt8.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache71"), cache7));
        rt8.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache81"), cache7));
        rt8.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache91"), cache7));
        rt8.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache01"), cache7));
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
        
        rt9.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache11"),cache8));
        rt9.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache21"),cache8));
        rt9.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache31"), cache8));
        rt9.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache41"), cache8));
        rt9.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache51"), cache8));
        rt9.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache61"), cache8));
        rt9.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache71"), cache8));
        rt9.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache81"), cache8));
        rt9.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache91"), cache8));
        rt9.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache01"), cache8));
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
        
        rt10.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache11"),cache9));
        rt10.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache21"),cache9));
        rt10.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache31"), cache9));
        rt10.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache41"), cache9));
        rt10.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache51"), cache9));
        rt10.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache61"), cache9));
        rt10.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache71"), cache9));
        rt10.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache81"), cache9));
        rt10.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache91"), cache9));
        rt10.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache01"), cache9));
        rt10.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_b"),cache9));
        net.routingTable(cache8, rt10);
        
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt11 = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt11.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_a"),cache0));
        rt11.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache1"),cache8));
        rt11.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache2"),cache8));
        rt11.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache3"), cache8));
        rt11.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache4"), cache8));
        rt11.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache5"), cache8));
        rt11.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache6"), cache8));
        rt11.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache7"), cache8));
        rt11.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache8"), cache8));
        rt11.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache0"), cache0));
        
        rt11.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache1"),cache0));
        rt11.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache2"),cache0));
        rt11.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache3"), cache0));
        rt11.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache4"), cache0));
        rt11.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache5"), cache0));
        rt11.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache6"), cache0));
        rt11.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache7"), cache0));
        rt11.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache8"), cache0));
        rt11.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache9"), cache0));
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
        
        rt12.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache11"), cache01));
        rt12.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache21"), cache01));
        rt12.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache31"), cache01));
        rt12.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache41"), cache01));
        rt12.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache51"), cache01));
        rt12.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache61"), cache01));
        rt12.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache71"), cache01));
        rt12.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache81"), cache01));
        rt12.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache91"), cache01));
        rt12.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_b"),cache01));
        net.routingTable(cache0, rt12);
        
        /////////////////////////////////fix
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt01 = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt01.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_a"),cache0));
        rt01.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache1"),cache0));
        rt01.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache2"),cache0));
        rt01.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache3"), cache0));
        rt01.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache4"), cache0));
        rt01.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache5"), cache0));
        rt01.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache6"), cache0));
        rt01.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache7"), cache0));
        rt01.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache8"), cache0));
        rt01.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache9"), cache0));
        rt01.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache0"), cache0));
        
        rt01.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache11"), cache11));
        rt01.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache21"), cache11));
        rt01.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache31"), cache11));
        rt01.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache41"), cache11));
        rt01.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache51"), cache11));
        rt01.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache61"), cache11));
        rt01.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache71"), cache11));
        rt01.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache81"), cache11));
        rt01.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache91"), cache11));
        rt01.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_b"),cache11));
        net.routingTable(cache01, rt01);
        
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt111 = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt111.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_a"),cache01));
        rt111.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache1"),cache01));
        rt111.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache2"),cache01));
        rt111.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache3"), cache01));
        rt111.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache4"), cache01));
        rt111.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache5"), cache01));
        rt111.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache6"), cache01));
        rt111.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache7"), cache01));
        rt111.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache8"), cache01));
        rt111.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache9"), cache01));
        rt111.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache0"), cache01));
        rt111.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache01"), cache01));

        rt111.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache21"), cache21));
        rt111.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache31"), cache21));
        rt111.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache41"), cache21));
        rt111.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache51"), cache21));
        rt111.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache61"), cache21));
        rt111.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache71"), cache21));
        rt111.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache81"), cache21));
        rt111.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache91"), cache21));
        rt111.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_b"),cache21));
        net.routingTable(cache11, rt111);
        
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt21 = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt21.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_a"),cache11));
        rt21.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache1"),cache11));
        rt21.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache2"),cache11));
        rt21.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache3"), cache11));
        rt21.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache4"), cache11));
        rt21.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache5"), cache11));
        rt21.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache6"), cache11));
        rt21.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache7"), cache11));
        rt21.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache8"), cache11));
        rt21.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache9"), cache11));
        rt21.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache0"), cache11));
        rt21.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache01"), cache11));
        rt21.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache11"), cache11));
        
        rt21.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache31"), cache31));
        rt21.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache41"), cache31));
        rt21.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache51"), cache31));
        rt21.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache61"), cache31));
        rt21.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache71"), cache31));
        rt21.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache81"), cache31));
        rt21.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache91"), cache31));
        rt21.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_b"),cache31));
        net.routingTable(cache21, rt21);
        
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt31 = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt31.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_a"),cache21));
        rt31.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache1"),cache21));
        rt31.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache2"),cache21));
        rt31.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache3"), cache21));
        rt31.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache4"), cache21));
        rt31.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache5"), cache21));
        rt31.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache6"), cache21));
        rt31.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache7"), cache21));
        rt31.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache8"), cache21));
        rt31.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache9"), cache21));
        rt31.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache0"), cache21));
        rt31.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache01"), cache21));
        rt31.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache11"), cache21));
        rt31.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache21"), cache21));

        rt31.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache41"), cache41));
        rt31.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache51"), cache41));
        rt31.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache61"), cache41));
        rt31.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache71"), cache41));
        rt31.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache81"), cache41));
        rt31.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache91"), cache41));
        rt31.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_b"),cache41));
        net.routingTable(cache31, rt31);
        
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt41 = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt41.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_a"),cache31));
        rt41.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache1"),cache31));
        rt41.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache2"),cache31));
        rt41.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache3"), cache31));
        rt41.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache4"), cache31));
        rt41.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache5"), cache31));
        rt41.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache6"), cache31));
        rt41.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache7"), cache31));
        rt41.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache8"), cache31));
        rt41.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache9"), cache31));
        rt41.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache0"), cache31));
        rt41.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache01"), cache31));
        rt41.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache11"), cache31));
        rt41.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache21"), cache31));
        rt41.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache31"), cache31));

        rt41.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache51"), cache51));
        rt41.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache61"), cache51));
        rt41.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache71"), cache51));
        rt41.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache81"), cache51));
        rt41.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache91"), cache51));
        rt41.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_b"),cache51));
        net.routingTable(cache41, rt41);
        
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt51 = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt51.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_a"),cache41));
        rt51.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache1"),cache41));
        rt51.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache2"),cache41));
        rt51.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache3"), cache41));
        rt51.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache4"), cache41));
        rt51.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache5"), cache41));
        rt51.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache6"), cache41));
        rt51.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache7"), cache41));
        rt51.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache8"), cache41));
        rt51.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache9"), cache41));
        rt51.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache0"), cache41));
        rt51.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache01"), cache41));
        rt51.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache11"), cache41));
        rt51.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache21"), cache41));
        rt51.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache31"), cache41));
        rt51.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache41"), cache41));
        
        rt51.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache61"), cache61));
        rt51.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache71"), cache61));
        rt51.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache81"), cache61));
        rt51.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache91"), cache61));
        rt51.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_b"),cache61));
        net.routingTable(cache51, rt51);
        
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt61 = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt61.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_a"),cache51));
        rt61.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache1"),cache51));
        rt61.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache2"),cache51));
        rt61.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache3"), cache51));
        rt61.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache4"), cache51));
        rt61.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache5"), cache51));
        rt61.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache6"), cache51));
        rt61.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache7"), cache51));
        rt61.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache8"), cache51));
        rt61.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache9"), cache51));
        rt61.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache0"), cache51));
        rt61.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache01"), cache51));
        rt61.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache11"), cache51));
        rt61.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache21"), cache51));
        rt61.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache31"), cache51));
        rt61.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache41"), cache51));
        rt61.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache51"), cache51));

        rt61.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache71"), cache71));
        rt61.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache81"), cache71));
        rt61.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache91"), cache71));
        rt61.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_b"),cache71));
        net.routingTable(cache61, rt61);
        
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt71 = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt71.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_a"),cache61));
        rt71.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache1"),cache61));
        rt71.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache2"),cache61));
        rt71.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache3"), cache61));
        rt71.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache4"), cache61));
        rt71.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache5"), cache61));
        rt71.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache6"), cache61));
        rt71.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache7"), cache61));
        rt71.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache8"), cache61));
        rt71.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache9"), cache61));
        rt71.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache0"), cache61));
        rt71.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache01"), cache61));
        rt71.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache11"), cache61));
        rt71.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache21"), cache61));
        rt71.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache31"), cache61));
        rt71.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache41"), cache61));
        rt71.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache51"), cache61));
        rt71.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache61"), cache61));

        rt71.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache81"), cache81));
        rt71.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache91"), cache81));
        rt71.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_b"),cache81));
        net.routingTable(cache71, rt71);
        
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt81 = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt81.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_a"),cache71));
        rt81.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache1"),cache71));
        rt81.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache2"),cache71));
        rt81.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache3"), cache71));
        rt81.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache4"), cache71));
        rt81.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache5"), cache71));
        rt81.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache6"), cache71));
        rt81.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache7"), cache71));
        rt81.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache8"), cache71));
        rt81.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache9"), cache71));
        rt81.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache0"), cache71));
        rt81.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache01"), cache71));
        rt81.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache11"), cache71));
        rt81.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache21"), cache71));
        rt81.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache31"), cache71));
        rt81.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache41"), cache71));
        rt81.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache51"), cache71));
        rt81.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache61"), cache71));
        rt81.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache71"), cache71));

        rt81.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache91"), cache91));
        rt81.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_b"),cache91));
        net.routingTable(cache81, rt81);
        
        ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt91 = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
        rt91.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_a"),cache81));
        rt91.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache1"),cache81));
        rt91.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache2"),cache81));
        rt91.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache3"), cache81));
        rt91.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache4"), cache81));
        rt91.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache5"), cache81));
        rt91.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache6"), cache81));
        rt91.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache7"), cache81));
        rt91.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache8"), cache81));
        rt91.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache9"), cache81));
        rt91.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache0"), cache81));
        rt91.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache01"), cache81));
        rt91.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache11"), cache81));
        rt91.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache21"), cache81));
        rt91.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache31"), cache81));
        rt91.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache41"), cache81));
        rt91.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache51"), cache81));
        rt91.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache61"), cache81));
        rt91.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache71"), cache81));
        rt91.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_cache81"), cache81));

        rt91.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get("ip_b"),b));
        net.routingTable(cache91, rt91);
        
        ///////////////////////////////////////////////////////////////////
        net.attach(a, b, cache1, cache2, cache3, cache4, cache5, cache6, cache7, cache8, cache9, cache0,
        		cache11, cache21, cache31, cache41, cache51, cache61, cache71, cache81, cache91, cache01);

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
        cache01.installCache(new NetworkObject[]{nctx.nm.get("cache0")});
        cache21.installCache(new NetworkObject[]{nctx.nm.get("cache11")});
        cache31.installCache(new NetworkObject[]{nctx.nm.get("cache21")});
        cache41.installCache(new NetworkObject[]{nctx.nm.get("cache31")});
        cache51.installCache(new NetworkObject[]{nctx.nm.get("cache41")});
        cache61.installCache(new NetworkObject[]{nctx.nm.get("cache51")});
        cache71.installCache(new NetworkObject[]{nctx.nm.get("cache61")});
        cache81.installCache(new NetworkObject[]{nctx.nm.get("cache71")});
        cache91.installCache(new NetworkObject[]{nctx.nm.get("cache81")});

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
        Test20Caches model = new Test20Caches();
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