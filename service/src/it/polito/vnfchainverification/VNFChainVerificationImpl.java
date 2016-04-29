/*
 * Copyright 2016 Politecnico di Torino
 * Authors:
 * Project Supervisor and Contact: Riccardo Sisto (riccardo.sisto@polito.it)
 * 
 * This file is part of Verigraph.
 * 
 * Verigraph is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 * 
 * Verigraph is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public
 * License along with Verigraph.  If not, see
 * <http://www.gnu.org/licenses/>.
 */
package it.polito.vnfchainverification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import javax.jws.HandlerChain;
import javax.jws.WebService;

import com.microsoft.z3.Context;
import com.microsoft.z3.DatatypeExpr;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.Model;
import com.microsoft.z3.Status;
import com.microsoft.z3.Z3Exception;

import it.polito.vnfchainverification.VNF.RoutingTable.Entry;
import mcnet.components.Checker;
import mcnet.components.IsolationResult;
import mcnet.components.NetContext;
import mcnet.components.Network;
import mcnet.components.NetworkObject;
import mcnet.components.Tuple;
import mcnet.netobjs.AclFirewall;
import mcnet.netobjs.DumbNode;
import mcnet.netobjs.EndHost;
import mcnet.netobjs.PolitoAntispam;
import mcnet.netobjs.PolitoCache;
import mcnet.netobjs.PolitoErrFunction;
import mcnet.netobjs.PolitoMailClient;
import mcnet.netobjs.PolitoMailServer;
import mcnet.netobjs.PolitoNF;
import mcnet.netobjs.PolitoNat;
import mcnet.netobjs.PolitoWebClient;
import mcnet.netobjs.PolitoWebServer;


@WebService(endpointInterface = "it.polito.vnfchainverification.VNFChainVerification",
			name = "VNFChainVerification", 
			wsdlLocation = "META-INF/j-verigraph.wsdl",
			portName = "VNFChainVerificationPort",
			serviceName ="VNFChainVerificationService",
			targetNamespace = "http://www.example.org/checkisolation")
@HandlerChain(file = "META-INF/handler-chain.xml")
public class VNFChainVerificationImpl implements VNFChainVerification{
	public Checker check;
	Context ctx;

	@Override
	public boolean checkIsolationProperty(VNFName source, VNFName destination, List<VNF> vnf) throws WebServiceException_Exception,MalformedArgument_Exception,Z3Error_Exception{
		
		try{
			IsolationResult ret;   	
	    	resetZ3(); 	
	    	
	   		if(source==null||destination==null||vnf==null){
	   			throw new MalformedArgument_Exception("NULL arguments passed to the function checkIsolationProperty",new MalformedArgument());
	   		}
	   		if(vnf.size()<2){
	   			throw new MalformedArgument_Exception("Number of VNF passed must be more than 2 ",new MalformedArgument());
	   		}
	   		//boolean existsSrc = false;
	   		//boolean existsDst = false;

	   		
	    	List<String> vnfNames = new ArrayList<String>();
	    	List<String> vnfIPs = new ArrayList<String>();
	    	
	    	for(VNF v :vnf){
	    		if(v.getName()==null){
	    			throw new MalformedArgument_Exception("Every VNF must have a name",new MalformedArgument());
	    		}else if(v.getName().getId()==null){
	    			throw new MalformedArgument_Exception("Every VNFName must have an Id",new MalformedArgument());
	    		}
	    		vnfNames.add(v.getName().getId());
	    		
	    		if(v.getIPs()==null){
	    			throw new MalformedArgument_Exception("Every VNF must have at least an IP",new MalformedArgument());
	    		}else if(v.getIPs().isEmpty()){
	    			throw new MalformedArgument_Exception("Every VNF must have at least an IP",new MalformedArgument());
	    		}
	    		for(VNFIp ip : v.getIPs()){
		    		if(ip.getId()==null){
		    			throw new MalformedArgument_Exception("Every VNFIP must have an Id",new MalformedArgument());
		    		}
	    			vnfIPs.add(ip.getId());
	    		}
	    	}
	    	
	    	if(!vnfNames.contains(source.getId())||!vnfNames.contains(destination.getId())){
	   			throw new MalformedArgument_Exception("Source and destination must be included in VNF list",new MalformedArgument());
	   		}
	   		
	    	
	       	String [] names = new String[vnfNames.size()];
	    	String [] ips = new String[vnfIPs.size()];
	    	
	    	NetContext nctx = new NetContext (ctx, vnfNames.toArray(names),vnfIPs.toArray(ips));				
			Network net = new Network (ctx,new Object[]{nctx});
			
			TreeMap<String,NetworkObject> nobjs = new TreeMap<String,NetworkObject>();
			ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>> adm = new ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>>();
			ArrayList<DatatypeExpr> al;
			
			for(VNF v :vnf){
	    		Object vnftype =v.getAclFirewallOrDumbNodeOrEndHost();
	    		
	    		if(vnftype instanceof VNF.AclFirewall){
	       			AclFirewall node = new AclFirewall(ctx, new Object[]{nctx.nm.get(v.getName().getId()), net, nctx});
	    			nobjs.put(v.getName().getId(),node);
	    			al = new ArrayList<DatatypeExpr>();
	    			for(VNFIp ip : v.getIPs()){
	    				al.add(nctx.am.get(ip.getId()));
	    			} 
    				adm.add(new Tuple<>(node, al));
	    		
	    			ArrayList<Tuple<DatatypeExpr,DatatypeExpr>> acls = new ArrayList<Tuple<DatatypeExpr,DatatypeExpr>>();
	    			if(((VNF.AclFirewall)vnftype).getAcl()==null)
	    				throw new MalformedArgument_Exception("The AclFirewall network object must have an Acl list",new MalformedArgument());
	    			else if(((VNF.AclFirewall)vnftype).getAcl().isEmpty())
	    				throw new MalformedArgument_Exception("The Acl list of AclFirewall is empty",new MalformedArgument());
	    			
	    			List<VNF.AclFirewall.Acl> acl_jaxb =((VNF.AclFirewall)vnftype).getAcl();
	    			for(VNF.AclFirewall.Acl acl :acl_jaxb){
	    				if(acl.getIP1()==null||acl.getIP2()==null)
	    					throw new MalformedArgument_Exception("Each Acl must have two IP addresses",new MalformedArgument());
	    				else if(acl.getIP1().getId()==null||acl.getIP2().getId()==null)
	    					throw new MalformedArgument_Exception("Each Acl must have two IP elements with assigned address",new MalformedArgument());
	    				else if(!vnfIPs.contains(acl.getIP1().getId())||!vnfIPs.contains(acl.getIP2().getId()))
	    					throw new MalformedArgument_Exception("Each IP address of each Acl must match an IP of a given VNF",new MalformedArgument());
	    				acls.add(new Tuple<DatatypeExpr,DatatypeExpr>(nctx.am.get(acl.getIP1().getId()),nctx.am.get(acl.getIP2().getId())));
	    			}
	
	    			node.addAcls(acls);
	    		}
	    		else if(vnftype instanceof VNF.DumbNode){
	    			DumbNode node = new DumbNode(ctx, new Object[]{nctx.nm.get(v.getName().getId()), net, nctx});
	    			nobjs.put(v.getName().getId(),node);
	    			al = new ArrayList<DatatypeExpr>();
	    			for(VNFIp ip : v.getIPs()){
	    				al.add(nctx.am.get(ip.getId()));
	    			} 
    				adm.add(new Tuple<>(node, al));
	    		
	    		}
	    		else if(vnftype instanceof VNF.EndHost){
	     			EndHost node = new EndHost(ctx, new Object[]{nctx.nm.get(v.getName().getId()), net, nctx});
	    			nobjs.put(v.getName().getId(),node);
	    			al = new ArrayList<DatatypeExpr>();
	    			for(VNFIp ip : v.getIPs()){
	    				al.add(nctx.am.get(ip.getId()));
	    			} 
    				adm.add(new Tuple<>(node, al));
	    			
	    		}
	    		else if(vnftype instanceof VNF.PolitoAntispam){
	    			PolitoAntispam node = new PolitoAntispam(ctx, new Object[]{nctx.nm.get(v.getName().getId()), net, nctx});
	    			nobjs.put(v.getName().getId(),node);
	    			al = new ArrayList<DatatypeExpr>();
	    			for(VNFIp ip : v.getIPs()){
	    				al.add(nctx.am.get(ip.getId()));
	    			} 
    				adm.add(new Tuple<>(node, al));
    				if(((VNF.PolitoAntispam)vnftype).getBlacklist()==null)
	    				throw new MalformedArgument_Exception("The PolitoAntispam network object must have a blacklist",new MalformedArgument());
	    			else if(((VNF.PolitoAntispam)vnftype).getBlacklist().isEmpty())
	    				throw new MalformedArgument_Exception("The blacklist of PolitoAntispam is empty",new MalformedArgument());
	    			
    				List<Integer> bl = ((VNF.PolitoAntispam)vnftype).getBlacklist();
	    			int[] int_adapt = new int[bl.size()];
	    			for(int i =0;i<bl.size();i++){
	    				if(bl.get(i)==null)
	    					throw new MalformedArgument_Exception("Each Acl must have two IP addresses",new MalformedArgument());
	    				
	    				int_adapt[i] = bl.get(i);
	    			}
	    			node.installAntispam(int_adapt);
	    		}
	    		else if(vnftype instanceof VNF.PolitoCache){
	       			PolitoCache node = new PolitoCache(ctx, new Object[]{nctx.nm.get(v.getName().getId()), net, nctx});
	    			nobjs.put(v.getName().getId(),node);
	    			al = new ArrayList<DatatypeExpr>();
	    			for(VNFIp ip : v.getIPs()){
	    				al.add(nctx.am.get(ip.getId()));
	    			} 
    				adm.add(new Tuple<>(node, al));
	    		
    				if(((VNF.PolitoCache)vnftype).getInternalNodes()==null)
	    				throw new MalformedArgument_Exception("The Politocache network object must have internal nodes",new MalformedArgument());
	    			else if(((VNF.PolitoCache)vnftype).getInternalNodes().isEmpty())
	    				throw new MalformedArgument_Exception("The internal nodes list of PolitoCache is empty",new MalformedArgument());
	    		
    				List<VNFName> in = ((VNF.PolitoCache)vnftype).getInternalNodes();
	    			
	    			NetworkObject[] intnodes = new NetworkObject[in.size()]; 
	    			for(int i=0;i<in.size();i++){
	    				if(in.get(i).getId()==null)
	    					throw new MalformedArgument_Exception("Each PolitoCache internal node must have a name",new MalformedArgument());
	    				else if(!vnfNames.contains(in.get(i).getId()))
	    					throw new MalformedArgument_Exception("The name of each PolitoCache internal node must match the name of a given VNF",new MalformedArgument());

	    				intnodes[i] = nctx.nm.get(in.get(i).getId());
	    			}
	    			node.installCache(intnodes);
	    		}
	    		else if(vnftype instanceof VNF.PolitoErrFunction){
	    			PolitoErrFunction node = new PolitoErrFunction(ctx, new Object[]{nctx.nm.get(v.getName().getId()), net, nctx});
	    			nobjs.put(v.getName().getId(),node);
	    			al = new ArrayList<DatatypeExpr>();
	    			for(VNFIp ip : v.getIPs()){
	    				al.add(nctx.am.get(ip.getId()));
	    			} 
    				adm.add(new Tuple<>(node, al));
	    		 
	    		}
	    		else if(vnftype instanceof VNF.PolitoMailClient){
	        		PolitoMailClient node = new PolitoMailClient(ctx, new Object[]{nctx.nm.get(v.getName().getId()), net, nctx});
	    			nobjs.put(v.getName().getId(),node);
	    			al = new ArrayList<DatatypeExpr>();
	    			for(VNFIp ip : v.getIPs()){
	    				al.add(nctx.am.get(ip.getId()));
	    			} 
    				adm.add(new Tuple<>(node, al));
	    		
	    		}
	    		else if(vnftype instanceof VNF.PolitoMailServer){
	        		PolitoMailServer node = new PolitoMailServer(ctx, new Object[]{nctx.nm.get(v.getName().getId()), net, nctx});
	    			nobjs.put(v.getName().getId(),node);
	    			al = new ArrayList<DatatypeExpr>();
	    			for(VNFIp ip : v.getIPs()){
	    				al.add(nctx.am.get(ip.getId()));
	    			} 
    				adm.add(new Tuple<>(node, al));
	    		
	    		}
	    		else if(vnftype instanceof VNF.PolitoNat){
	    			PolitoNat node = new PolitoNat(ctx, new Object[]{nctx.nm.get(v.getName().getId()), net, nctx});
	    			nobjs.put(v.getName().getId(),node);
	    			al = new ArrayList<DatatypeExpr>();
	    			for(VNFIp ip : v.getIPs()){
	    				al.add(nctx.am.get(ip.getId()));
	    			} 
    				adm.add(new Tuple<>(node, al));
	    			
    				if(((VNF.PolitoNat)vnftype).getInternalIPs()==null)
	    				throw new MalformedArgument_Exception("The PolitoNat network object must have a list of internal IP addresses",new MalformedArgument());
	    			else if(((VNF.PolitoNat)vnftype).getInternalIPs().isEmpty())
	    				throw new MalformedArgument_Exception("The internal IP addresses list of PolitoNat is empty",new MalformedArgument());
	    			
	    			List<VNFIp> iip = ((VNF.PolitoNat)vnftype).getInternalIPs();
	    			
	    			ArrayList<DatatypeExpr> ia = new ArrayList<DatatypeExpr>();
	    			for(VNFIp  iaddr : iip){
	    				if(iaddr.getId()==null)
	    					throw new MalformedArgument_Exception("Each PolitoNat internal IP element must have an assigned address",new MalformedArgument());
	    				else if(!vnfIPs.contains(iaddr.getId()))
	    					throw new MalformedArgument_Exception("Each PolitoNat internal IP addresses must match an IP address of a given VNF",new MalformedArgument());
	      				ia.add(nctx.am.get(iaddr.getId()));
	    			}
	    			node.setInternalAddress(ia);
	    		}
	    		else if(vnftype instanceof VNF.PolitoNF){
	    			PolitoNF node = new PolitoNF(ctx, new Object[]{nctx.nm.get(v.getName().getId()), net, nctx});
	    			nobjs.put(v.getName().getId(),node);
	    			al = new ArrayList<DatatypeExpr>();
	    			for(VNFIp ip : v.getIPs()){
	    				al.add(nctx.am.get(ip.getId()));
	    			} 
    				adm.add(new Tuple<>(node, al));
	    			
    				if(((VNF.PolitoNF)vnftype).getNFRule()==null)
	    				throw new MalformedArgument_Exception("The PolitoNF network object must have a rules list",new MalformedArgument());
	    			else if(((VNF.PolitoNF)vnftype).getNFRule().isEmpty())
	    				throw new MalformedArgument_Exception("The rules list of PolitoNF is empty",new MalformedArgument());
	    			
	    			List<VNF.PolitoNF.NFRule> rules =((VNF.PolitoNF)vnftype).getNFRule();
	    			for(VNF.PolitoNF.NFRule  entry : rules){
	    				if(entry.getIP1()==null||entry.getIP2()==null)
	    					throw new MalformedArgument_Exception("Each rule must have two IP addresses",new MalformedArgument());
	    				else if(entry.getIP1().getId()==null||entry.getIP2().getId()==null)
	    					throw new MalformedArgument_Exception("Each rule must have two IP elements with assigned address",new MalformedArgument());
	    				else if(!vnfIPs.contains(entry.getIP1().getId())||!vnfIPs.contains(entry.getIP2().getId()))
	    					throw new MalformedArgument_Exception("Each IP address of each rule must match an IP of a given VNF",new MalformedArgument());
	    			
	    				node.politoNFRules(nctx.am.get(entry.getIP1().getId()),nctx.am.get(entry.getIP2().getId()));
	    			}
	
	    		}
	    		else if(vnftype instanceof VNF.PolitoWebClient){
	    			PolitoWebClient node = new PolitoWebClient(ctx, new Object[]{nctx.nm.get(v.getName().getId()), net, nctx,
	    					nctx.am.get(((VNF.PolitoWebClient)v.getAclFirewallOrDumbNodeOrEndHost()).getServerIP().getId())});
	    			nobjs.put(v.getName().getId(),node);
	    			al = new ArrayList<DatatypeExpr>();
	    			for(VNFIp ip : v.getIPs()){
	    				al.add(nctx.am.get(ip.getId()));
	    			} 
    				adm.add(new Tuple<>(node, al));
	    		
	    		}
	    		else if(vnftype instanceof VNF.PolitoWebServer){
	    			PolitoWebServer node = new PolitoWebServer(ctx, new Object[]{nctx.nm.get(v.getName().getId()), net, nctx});
	    			nobjs.put(v.getName().getId(),node);
	    			al = new ArrayList<DatatypeExpr>();
	    			for(VNFIp ip : v.getIPs()){
	    				al.add(nctx.am.get(ip.getId()));
	    			} 
    				adm.add(new Tuple<>(node, al));
	    		     			 
	    		}
	    	}
				
			net.setAddressMappings(adm);
		    for(VNF v :vnf){
				ArrayList<Tuple<DatatypeExpr,NetworkObject>> routingTable = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
				if(v.getRoutingTable()==null)
					throw new MalformedArgument_Exception("Each VNF must have a routing table",new MalformedArgument());
				if(v.getRoutingTable().getEntry()==null)
					throw new MalformedArgument_Exception("Each VNF routing table must have an entry list",new MalformedArgument());
				
				for(Entry r : v.getRoutingTable().getEntry() ){
					if(r.getIP()==null||r.getName()==null)
    					throw new MalformedArgument_Exception("Each entry of the routing table must a VPN IP address and a VNF name",new MalformedArgument());
    				else if(r.getIP().getId()==null||r.getName().getId()==null)
    					throw new MalformedArgument_Exception("Each entry must have an IP element with assigned address and a VNF name element with assigned name",new MalformedArgument());
    				else if(!vnfIPs.contains(r.getIP().getId())||!vnfNames.contains(r.getName().getId()))
    					throw new MalformedArgument_Exception("Each IP address and VNF name of each entry must match an IP and a name of a given VNF",new MalformedArgument());
    			
					routingTable.add(new Tuple<>(nctx.am.get(r.getIP().getId()),nobjs.get(r.getName().getId())));
	    		}
				
	    		net.routingTable(nobjs.get(v.getName().getId()),routingTable);
	    		net.attach(nobjs.get(v.getName().getId()));
	    	}
		   
		    check = new Checker(ctx,nctx,net);
		    ret = check.checkIsolationProperty(nobjs.get(source.getId()),nobjs.get(destination.getId()));
			
//			printVector(ret.assertions);
			if (ret.result == Status.UNSATISFIABLE){
//		 	   System.out.println("UNSAT"); // Nodes a and b are isolated
		 	   return false;
			}else{
//		 		System.out.println("SAT ");
		//		     		System.out.print( "Model -> "); p.printModel(ret.model);
		//		    	    System.out.println( "Violating packet -> " +ret.violating_packet);
		//		    	    System.out.println("Last hop -> " +ret.last_hop);
		//		    	    System.out.println("Last send_time -> " +ret.last_send_time);
		//		    	    System.out.println( "Last recv_time -> " +ret.last_recv_time);
		 		return true;
			}

		}catch(Z3Exception e1){
			e1.printStackTrace();
			Z3Error ze = new Z3Error();
			ze.setMessage("Server Z3 library error");
			throw new Z3Error_Exception("Server Z3 library error",ze,e1.getCause());
		}catch(RuntimeException e2){
			e2.printStackTrace();
			WebServiceException sge = new WebServiceException();
			sge.setMessage(e2.toString());
			throw new WebServiceException_Exception(e2.toString(),sge,e2.getCause());
		}

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


        
}
