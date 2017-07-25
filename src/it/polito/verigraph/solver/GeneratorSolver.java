/*******************************************************************************
 * Copyright (c) 2017 Politecnico di Torino and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package it.polito.verigraph.solver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.DatatypeExpr;
import com.microsoft.z3.Expr;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Sort;
import com.microsoft.z3.Status;
import com.microsoft.z3.Z3Exception;

import it.polito.neo4j.manager.Neo4jLibrary;
import it.polito.verigraph.exception.DataNotFoundException;
import it.polito.verigraph.mcnet.components.Checker;
import it.polito.verigraph.mcnet.components.IsolationResult;
import it.polito.verigraph.mcnet.components.NetContext;
import it.polito.verigraph.mcnet.components.Network;
import it.polito.verigraph.mcnet.components.NetworkObject;
import it.polito.verigraph.mcnet.components.Tuple;
import it.polito.verigraph.mcnet.netobjs.AclFirewall;
import it.polito.verigraph.mcnet.netobjs.EndHost;
import it.polito.verigraph.mcnet.netobjs.PacketModel;
import it.polito.verigraph.mcnet.netobjs.PolitoAntispam;
import it.polito.verigraph.mcnet.netobjs.PolitoCache;
import it.polito.verigraph.mcnet.netobjs.PolitoEndHost;
import it.polito.verigraph.mcnet.netobjs.PolitoFieldModifier;
import it.polito.verigraph.mcnet.netobjs.PolitoIDS;
import it.polito.verigraph.mcnet.netobjs.PolitoMailClient;
import it.polito.verigraph.mcnet.netobjs.PolitoMailServer;
import it.polito.verigraph.mcnet.netobjs.PolitoNat;
import it.polito.verigraph.mcnet.netobjs.PolitoVpnAccess;
import it.polito.verigraph.mcnet.netobjs.PolitoVpnExit;
import it.polito.verigraph.mcnet.netobjs.PolitoWebClient;
import it.polito.verigraph.mcnet.netobjs.PolitoWebServer;
import it.polito.verigraph.service.VerigraphLogger;

public class GeneratorSolver{
    Scenario scenario;
    Context ctx;
    Network net;
    NetContext nctx;
    List<BoolExpr> constraints = new ArrayList<BoolExpr>();
    public Checker check;
    Map<String, Object> mo=new HashMap<String, Object>();
    List<String> path=new ArrayList<String>();
    //private final static Logger LOGGER = Logger.getLogger(VerigraphLogger.class.getName());
    public static VerigraphLogger vlogger = VerigraphLogger.getVerigraphlogger();

    public List<String> getPaths(){
        if(path!=null)
            return path;
        else
            return null;
    }

    public GeneratorSolver(Scenario tmp, List<String> s){
        this.scenario=tmp;
        this.path=s;
    }

    public void resetZ3() throws Z3Exception{
        HashMap<String, String> cfg = new HashMap<String, String>();
        cfg.put("model", "true");
        ctx = new Context(cfg);
    }

    public String run(String src, String dst){
        IsolationResult result;
        result=check.checkIsolationProperty((NetworkObject)mo.get(src), (NetworkObject)mo.get(dst));
        String res=new String();

        if (result.result == Status.UNSATISFIABLE){
            res="UNSAT"; // Nodes a and b are isolated
        }else if(result.result == Status.SATISFIABLE){
            res= "SAT";
        }else if(result.result == Status.UNKNOWN){
            res= "UNKNOWN";
        }
        return res;
    }

    public void genSolver() {
        resetZ3();
        /*List<String> names=new ArrayList<String>();
            List<String> addresses=new ArrayList<String>();
            for(Map.Entry<String, Map<String, String>> a : scenario.chn.entrySet()){
            String name=a.getKey();
            Map<String, String> nodo=a.getValue();
            String address=nodo.get("address");
            names.add(name);
            addresses.add(address);
            }*/

        //String name_nctx=listToArguments(scenario.nodes_names);
        //String type_nctx=listToArguments(scenario.nodes_addresses);

        //String name_nctx=listToArguments(names);
        //String type_nctx=listToArguments(addresses);

        String[] name_nctx=listToStringArguments(scenario.nodes_names);
        String[] address_nctx=listToStringArguments(scenario.nodes_addresses);

        nctx = new NetContext (ctx, name_nctx, address_nctx);
        net = new Network (ctx,new Object[]{nctx});

        //classes creation mcnet.objects: Map<name_node, obj created>
        for(int i=0; i<scenario.nodes_names.size(); i++){
            if(scenario.nodes_names.get(i)!=null){
                //mo.put(scenario.nodes_names.get(i),setDevice(scenario.nodes_names.get(i), ctx, nctx, net));
                //setDevice(scenario.nodes_names.get(i), ctx, nctx, net, mo);
                setDevice(scenario.nodes_names.get(i));
            }
        }

        //setMapping
        ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>> adm = new ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>>();
        doMappings(adm, mo);
        //System.out.println("adm: " + adm);
        net.setAddressMappings(adm);
        //setRouting
        Map<String, List<Tuple<DatatypeExpr,NetworkObject>>> ro=new HashMap<String,List<Tuple<DatatypeExpr,NetworkObject>>>();
        doRouting(ro, mo);
        //configureDevice
        configureDevice();
        check = new Checker(ctx,nctx,net);
    }

    private void setDevice(String name) {
        Map<String, String> value=scenario.chn.get(name);
        String type=value.get("functional_type");

        if(type.compareTo("endhost")==0){
            PolitoEndHost endhost=new PolitoEndHost(ctx, new Object[]{nctx.nm.get(name), net, nctx});
            mo.put(name, endhost);
        }else if(type.compareTo("cache")==0){
            PolitoCache cache=new PolitoCache(ctx, new Object[] { nctx.nm.get(name), net, nctx });
            mo.put(name, cache);
        }else if(type.compareTo("antispam")==0){
            PolitoAntispam antispam=new PolitoAntispam(ctx, new Object[]{nctx.nm.get(name), net, nctx});
            mo.put(name, antispam);
        }else if(type.compareTo("fieldmodifier")==0){
            PolitoFieldModifier fieldmodifier=new PolitoFieldModifier(ctx, new Object[]{nctx.nm.get(name), net, nctx});
            mo.put(name, fieldmodifier);
        }else if(type.compareTo("mailclient")==0){
            String conf=(scenario.config_obj.get(name)).get("mailserver");
            PolitoMailClient mailclient=new PolitoMailClient(ctx, new Object[]{nctx.nm.get(name), net, nctx, nctx.am.get(conf)});
            mo.put(name, mailclient);
        }else if(type.compareTo("mailserver")==0){
            PolitoMailServer mailserver=new PolitoMailServer(ctx, new Object[]{nctx.nm.get(name), net, nctx});
            mo.put(name, mailserver);
        }else if(type.compareTo("nat")==0){
            PolitoNat nat=new PolitoNat(ctx, new Object[]{nctx.nm.get(name), net, nctx});
            mo.put(name, nat);
        }else if(type.compareTo("vpnaccess")==0){
            PolitoVpnAccess vpnaccess=new PolitoVpnAccess(ctx, new Object[]{nctx.nm.get(name), net, nctx});
            mo.put(name, vpnaccess);
        }else if(type.compareTo("vpnexit")==0){
            PolitoVpnExit vpnexit=new PolitoVpnExit(ctx, new Object[]{nctx.nm.get(name), net, nctx});
            mo.put(name, vpnexit);
        }else if(type.compareTo("webserver")==0){
            PolitoWebServer webserver=new PolitoWebServer(ctx, new Object[]{nctx.nm.get(name), net, nctx});
            mo.put(name, webserver);
        }else if(type.compareTo("webclient")==0){
            String conf=(scenario.config_obj.get(name)).get("webserver");
            PolitoWebClient webclient=new PolitoWebClient(ctx, new Object[]{nctx.nm.get(name), net, nctx, nctx.am.get(conf)});
            mo.put(name, webclient);
        }else if(type.compareTo("dpi")==0){
            PolitoIDS dpi=new PolitoIDS(ctx, new Object[]{nctx.nm.get(name), net, nctx});
            mo.put(name, dpi);
        }else if(type.compareTo("endpoint")==0){
            EndHost endpoint=new EndHost(ctx, new Object[]{nctx.nm.get(name), net, nctx});
            mo.put(name, endpoint);
        }else if(type.compareTo("firewall")==0){
            AclFirewall firewall=new AclFirewall(ctx, new Object[]{nctx.nm.get(name), net, nctx});
            mo.put(name, firewall);
        }
    }

    private void configureDevice() {
        vlogger.logger.info("Configuration service");
        //System.out.println("Configuration Device");
        for(Map.Entry<String,Object> cd : mo.entrySet()){
            String name=cd.getKey();
            String type=(scenario.chn.get(name)).get("functional_type");
            String address=(scenario.chn.get(name)).get("address");
            Object model=cd.getValue();
            if(model instanceof PolitoEndHost){
                Map<String, String> packet=scenario.config_obj.get(name);
                if(packet!=null){
                    PacketModel pModel = new PacketModel();
                    if(packet.get("body")!=null){
                        pModel.setBody(String.valueOf(packet.get("body")).hashCode());
                    }
                    if(packet.get("destination")!=null){
                        pModel.setIp_dest(nctx.am.get(packet.get("destination")));
                    }
                    if(packet.get("sequence")!=null){
                        pModel.setSeq(String.valueOf(packet.get("sequence")).hashCode());
                    }
                    if(packet.get("email_from")!=null){
                        pModel.setEmailFrom(String.valueOf(packet.get("email_from")).hashCode());
                    }
                    if(packet.get("url")!=null){
                        pModel.setUrl(String.valueOf(packet.get("url")).hashCode());
                    }
                    if(packet.get("options")!=null){
                        pModel.setOptions(String.valueOf(packet.get("options")).hashCode());
                    }
                    if(packet.get("protocol")!=null){
                        String proto=packet.get("protocol");
                        if(proto.compareTo("HTTP_REQUEST")==0)
                            pModel.setProto(nctx.HTTP_REQUEST);
                        else if(proto.compareTo("HTTP_RESPONSE")==0)
                            pModel.setProto(nctx.HTTP_RESPONSE);
                        else if(proto.compareTo("POP3_REQUEST")==0)
                            pModel.setProto(nctx.POP3_REQUEST);
                        else if(proto.compareTo("POP3_RESPONSE")==0)
                            pModel.setProto(nctx.POP3_RESPONSE);
                    }
                    ((PolitoEndHost)cd.getValue()).installEndHost(pModel);
                }
                else{
                    vlogger.logger.info("Configuration endhost "+name+"empty");
                }
            }else if(model instanceof PolitoCache){

                List<String> list_tmp=scenario.config_array.get(name);
                for(int i=0; i<list_tmp.size(); i++){
                    if(!scenario.nodes_addresses.contains(list_tmp.get(i)))
                        list_tmp.remove(i);
                }
                List<String> list=trimIp(list_tmp);
                if(list!=null){
                    PolitoCache cache=(PolitoCache)cd.getValue();
                    NetworkObject [] array_no=listToNetworkArguments(list);
                    ((PolitoCache)cd.getValue()).installCache(array_no);
                }
                else{
                    vlogger.logger.info("Cache "+name+" empty");
                }
            }else if(model instanceof PolitoAntispam){
                List<String> list_tmp=scenario.config_array.get(name);
                List<String> list=trimIp(list_tmp);
                if(list!=null){
                    PolitoAntispam antispam=(PolitoAntispam)cd.getValue();
                    int[] blackList=listToIntArguments(list);
                    ((PolitoAntispam)cd.getValue()).installAntispam(blackList);
                }
                else{
                    vlogger.logger.info("Antispam "+name+" empty");
                }
            }else if(model instanceof PolitoFieldModifier){
                ((PolitoFieldModifier)cd.getValue()).installFieldModifier();
            }else if(model instanceof PolitoMailClient){
                //the rules are inserted in the init method
                continue;
            }else if(model instanceof PolitoMailServer){
                //the rules are inserted in the init method
                continue;
            }else if(model instanceof PolitoNat){
                List<String> list=scenario.config_array.get(name);
                if(!list.isEmpty()){
                    ArrayList<DatatypeExpr> ia = new ArrayList<DatatypeExpr>();
                    for(String s : list){
                        if(scenario.nodes_addresses.contains(s)){
                            ia.add(nctx.am.get(s));
                        }
                    }
                    ((PolitoNat)cd.getValue()).natModel(nctx.am.get(address));
                    ((PolitoNat)cd.getValue()).setInternalAddress(ia);
                }
                else{
                    vlogger.logger.info("Nat "+name+" empty");
                }
            }else if(model instanceof PolitoVpnAccess){
                Map<String, String> vpnexit=scenario.config_obj.get(name);
                if(vpnexit!=null){
                    for(Map.Entry<String, String> a : vpnexit.entrySet())
                        ((PolitoVpnAccess)cd.getValue()).vpnAccessModel(nctx.am.get(address), nctx.am.get(a.getValue()));
                }
                else{
                    vlogger.logger.info("Vpnacces "+name+" empty");
                }
            }else if(model instanceof PolitoVpnExit){
                Map<String, String> vpnaccess=scenario.config_obj.get(name);
                if(vpnaccess!=null){
                    for(Map.Entry<String, String> a : vpnaccess.entrySet())
                        ((PolitoVpnExit)cd.getValue()).vpnExitModel(nctx.am.get(address), nctx.am.get(a.getValue()));
                }
                else{
                    vlogger.logger.info("Vpnexit "+name+" empty");
                }

            }else if(model instanceof PolitoWebServer){
                //the rules are inserted in the init method
                continue;

            }else if(model instanceof PolitoWebClient){
                //the rules are inserted in the init method
                continue;

            }else if(model instanceof PolitoIDS){
                List<String> list=scenario.config_array.get(name);
                if(!list.isEmpty()){
                    PolitoIDS dpi=(PolitoIDS)cd.getValue();
                    int[] blackList=listToIntArguments(list);
                    ((PolitoIDS)cd.getValue()).installIDS(blackList);
                }
                else{
                    vlogger.logger.info("Dpi "+name+" empty");
                }
            }else if(model instanceof EndHost){
                //the rules are inserted in the init method
                continue;
            }else if(model instanceof AclFirewall){
                Map<String, String> acls=scenario.config_obj.get(name);
                ArrayList<Tuple<DatatypeExpr,DatatypeExpr>> acl = new ArrayList<Tuple<DatatypeExpr,DatatypeExpr>>();

                if(!acls.isEmpty()){
                    for(Map.Entry<String, String> a : acls.entrySet()){
                        String dest=a.getKey();
                        String src=a.getValue();

                        if(scenario.nodes_addresses.contains("ip_"+dest) && scenario.nodes_addresses.contains("ip_"+src)){
                            acl.add(new Tuple<DatatypeExpr,DatatypeExpr>(nctx.am.get("ip_"+dest),nctx.am.get("ip_"+src)));

                        }
                    }
                    ((AclFirewall)cd.getValue()).addAcls(acl);

                }
                else{
                    vlogger.logger.info("Acls firewall "+name+" empty");
                }
            }
            else
                vlogger.logger.info("No configuration added");
        }
    }

    private List<String> trimIp(List<String> list) {
        List<String> result=new ArrayList<String>();
        for(String s : list){
            if(s.length()>3){
                if(s.substring(0, 3).compareTo("ip_")==0)
                    result.add(s.substring(3));
                else
                    result.add(s);
            }
        }
        return result;
    }

    private void doRouting(Map<String, List<Tuple<DatatypeExpr, NetworkObject>>> ro, Map<String, Object> mo) {
        for(String nodes : scenario.nodes_names){
            Object obj=mo.get(nodes);
            if(obj!=null){
                ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
                Map<String, String> route=scenario.routing.get(nodes);
                for(Map.Entry<String,String> r : route.entrySet()){
                    String dest=r.getKey();
                    NetworkObject next_hop=(NetworkObject)mo.get(r.getValue());
                    rt.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get(dest), next_hop));
                }
                net.routingTable((NetworkObject)obj, rt);
                net.attach((NetworkObject)obj);
                ro.put(nodes, rt);
            }
        }
    }

    private void doMappings(ArrayList<Tuple<NetworkObject, ArrayList<DatatypeExpr>>> adm, Map<String, Object> mo) {
        for(Map.Entry<String, Object> obj : mo.entrySet()){
            String name=obj.getKey().toString();
            Object model=obj.getValue();
            ArrayList<DatatypeExpr> al = new ArrayList<DatatypeExpr>();
            al.add(nctx.am.get((scenario.chn.get(name)).get("address")));
            adm.add(new Tuple<>((NetworkObject)(model),al));
        }
    }

    //function Arguments:
    private String listToArguments(List<String> nodes_names) {
        StringBuffer result=new StringBuffer();
        for(int i=0; i<nodes_names.size(); i++){
            if(i==nodes_names.size()-1){
                result.append("\"" + nodes_names.get(i) + "\"");
            }
            else{
                result.append("\"" + nodes_names.get(i) + "\"");
                result.append(", ");
            }
        }
        return result.toString();
    }
    private NetworkObject[] listToNetworkArguments(List<String> arg){
        NetworkObject[] o= new NetworkObject[arg.size()];
        for(int i=0; i<arg.size(); i++){
            if(arg.get(i)!=null)
                o[i]= nctx.nm.get(arg.get(i));
        }
        return o;
    }

    private String[] listToStringArguments(List<String> arg){
        String[] o= new String[arg.size()];
        for(int i=0; i<arg.size(); i++){
            if(arg.get(i)!=null)
                o[i]= arg.get(i);
        }
        return o;
    }

    private int[] listToIntArguments(List<String> arg) {
        int[] o= new int[arg.size()];
        for(int i=0; i<arg.size(); i++){
            if(arg.get(i)!=null)
                o[i]= String.valueOf(arg.get(i)).hashCode();
        }
        return o;
    }

}