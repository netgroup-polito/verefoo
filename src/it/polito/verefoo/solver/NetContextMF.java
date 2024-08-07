/*******************************************************************************
 * Copyright (c) 2017-2024 Politecnico di Torino and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package it.polito.verefoo.solver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Constructor;
import com.microsoft.z3.Context;
import com.microsoft.z3.DatatypeExpr;
import com.microsoft.z3.DatatypeSort;
import com.microsoft.z3.EnumSort;
import com.microsoft.z3.Expr;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.Optimize;
import com.microsoft.z3.Sort;

import it.polito.verefoo.allocation.AllocationNodeAP;
import it.polito.verefoo.allocation.AllocationNodeMF;
import it.polito.verefoo.extra.WildcardManager;
import it.polito.verefoo.graph.Predicate;
import it.polito.verefoo.utils.PortInterval;
import it.polito.verefoo.utils.Tuple;

/**
 * Basic fields and other things required for model checking.
 *
 */
public class NetContextMF extends NetContext {

	private HashMap<String, AllocationNodeMF> allocationNodes;
	

    // weights
    public final int WAUTOCONF   = 1000;


 
    /**
     * Context for all of the rest that follows. Every network needs one of these
     * @param ctx
     * @param args
     */
    public NetContextMF(Context ctx, HashMap<String, AllocationNodeMF> allocationNodes, Object[]... args){
          nodeMap = new HashMap<String,DatatypeExpr>(); //list of nodes, callable by node name
          addressMap = new HashMap<String,DatatypeExpr>(); // list of addresses, callable by address name
          portMap = new HashMap<String,DatatypeExpr>();
          functionsMap= new HashMap<String,FuncDecl>() ;
          ipFunctionsMap = new HashMap<String,FuncDecl>();
          portFunctionsMap = new HashMap<String,FuncDecl>();
          constraints = new ArrayList<BoolExpr>();
          softConstrAutoConf = new ArrayList<>();
          softConstrAutoPlace = new ArrayList<>();
          softConstrWildcard = new ArrayList<>(); 
          softConstrProtoWildcard = new ArrayList<>(); 
          softConstrPorts = new ArrayList<>(); 

    	  this.ctx = ctx;
          this.allocationNodes = allocationNodes;
          mkTypes((String[])args[0],(String[])args[1], (String[])args[2], (String[])args[3]);
    }
    
    
    
    /*
     * Main Methods of NetContextMF class
     */

    
    /**
     * This methods adds hard and soft constraints inside the z3 solver
     * @param solver it is the z3 solver
     */
    protected void addConstraints(Optimize solver) {
    	setAddressMappings();
        BoolExpr[] constr = new BoolExpr[constraints.size()];
        solver.Add(constraints.toArray(constr));
        
        softConstrWildcard.forEach(t->solver.AssertSoft(t._1, WIPWILDCARD, t._2));
        softConstrProtoWildcard.forEach(t->solver.AssertSoft(t._1, WPROTOWILDCARD, t._2));
       
        
       int i = 1000;
       /* for(Tuple<BoolExpr,String> t : softConstrAutoConf) {
        	solver.AssertSoft(t._1, WAUTOCONF, t._2 + i);
        	i-=10;
        }*/
       softConstrAutoConf.forEach(t->solver.AssertSoft(t._1, WAUTOCONF, t._2));
        softConstrAutoPlace.forEach(t->solver.AssertSoft(t._1, WAUTOPLACEMENT, t._2));
        softConstrPorts.forEach(t->solver.AssertSoft(t._1, WPORTS, t._2));

    }
    
    /**
     * This method is in charge of creating the z3 types for ports, nodes, addresses and packets 
     * @param nodes it is the array of node names
     * @param addresses it is the array of IP addresses
     * @param srcp_ranges it is the array of source ports
     * @param dstp_ranges it is the array of destination ports
     */
    private void mkTypes (String[] nodes, String[] addresses, String[] srcp_ranges, String[] dstp_ranges){
    	 //----------- Port ranges for this network         
        String[] new_port_ranges = new String[srcp_ranges.length+dstp_ranges.length+1];
        for(int k=0;k<srcp_ranges.length;k++)
        	new_port_ranges[k] = srcp_ranges[k];
        for(int k=srcp_ranges.length;k<srcp_ranges.length+dstp_ranges.length;k++)
        	new_port_ranges[k] = dstp_ranges[k-srcp_ranges.length];
        
        // creating sort for port type
        new_port_ranges[new_port_ranges.length-1] = "null";
        String[] portRangeFieldNames = new String[]{"start","end"};
        // port type with two integers
        Sort[] sortPort = new Sort[]{ctx.mkIntSort(),ctx.mkIntSort()};
        Constructor portRangeCon = ctx.mkConstructor("port_range_constructor", "is_portRange", portRangeFieldNames, sortPort, null);
        portType = ctx.mkDatatypeSort("PortRange", new Constructor[] {portRangeCon});

        // port functions map filled 
        for(int i=0;i<portRangeFieldNames.length;i++){
        	portFunctionsMap.put(portRangeFieldNames[i], portType.getAccessors()[0][i]); // port_functions to get port's function declarations by name
        }
        
        
        for(int i=0;i<new_port_ranges.length;i++){
        	DatatypeExpr fd = (DatatypeExpr) ctx.mkConst(new_port_ranges[i], portType);
            portMap.put(fd.toString().replace("|", ""),fd);
            try{
            	constraints.add(equalPortRangeToInterval(fd, new PortInterval(new_port_ranges[i])));
            }catch(NumberFormatException e){
            	if(new_port_ranges[i].equals("null")){
            		constraints.add(equalPortRangeToInterval(fd,  new PortInterval("0-"+this.MAX_PORT)));
            	}else{
            		throw e;
            	}
            	
            }
        }
        
        //----------- Nodes in this network
        nodeType = ctx.mkEnumSort("Node", nodes);
        for(int i=0;i<nodeType.getConsts().length;i++){
            DatatypeExpr fd  = (DatatypeExpr)nodeType.getConst(i);   
            nodeMap.put(fd.toString().replace("|", ""),fd);
            AllocationNodeMF n = allocationNodes.get(nodes[i]);
            n.setZ3Name(fd);
        }
        
        

        //----------- Addresses for this network         
        String[] new_addr = new String[addresses.length+2];
        for(int k=0;k<addresses.length;k++)
            new_addr[k] = addresses[k];

        new_addr[new_addr.length-2] = "null";
        new_addr[new_addr.length-1] = "wildcard";

        String[] ipfieldNames = new String[]{"ipAddr_1","ipAddr_2","ipAddr_3","ipAddr_4"};
        Sort[] sort = new Sort[]{ctx.mkIntSort(),ctx.mkIntSort(),ctx.mkIntSort(),ctx.mkIntSort()};
        Constructor ipCon = ctx.mkConstructor("ip_constructor", "is_ip", ipfieldNames, sort, null);
        addressType = ctx.mkDatatypeSort("Address", new Constructor[] {ipCon});
        for(int i=0;i<ipfieldNames.length;i++){
        	ipFunctionsMap.put(ipfieldNames[i], addressType.getAccessors()[0][i]); // ip_functions to get ip's function declarations by name
        }
        

        for(int i=0;i<new_addr.length;i++){
        	DatatypeExpr fd = (DatatypeExpr) ctx.mkConst(new_addr[i], addressType);
        	 AllocationNodeMF n = allocationNodes.get(fd.toString().replace("|", ""));
             if(n != null) {
            	 n.setZ3Node(fd);
             }
            addressMap.put(fd.toString().replace("|", ""),fd);
            try{
            	constraints.add(equalIpToIntArray(fd, getIpFromString(new_addr[i])));
            }catch(NumberFormatException e){
            	if(new_addr[i].equals("null")){
            		constraints.add(equalIpToIntArray(fd, getIpFromString("0.0.0.0")));
            	}
            	else if(new_addr[i].equals("wildcard")){
            		constraints.add(equalIpToIntArray(fd, getIpFromString("-1.-1.-1.-1")));
            	} else{
            		//251 is a prime number, to reduce collisions
            		int symbolicAddr = Math.abs(new_addr[i].hashCode()%251);
            		constraints.add(equalIpToIntArray(fd, getIpFromString(symbolicAddr + "." + symbolicAddr + "." + symbolicAddr + "." + symbolicAddr)));
            	}
            	
            }
        }
        
        nodeHasAddr = ctx.mkFuncDecl("nodeHasAddr", new Sort[]{nodeType, addressType},ctx.mkBoolSort());
        // addrToNode: address -> node
        addrToNode = ctx.mkFuncDecl("addrToNode", addressType, nodeType);
        // recv: node -> node -> packet-> int-> bool
        deny = ctx.mkFuncDecl("deny", new Sort[]{ nodeType, ctx.mkIntSort()},ctx.mkBoolSort());
    }
    
    
    
    /*
     * Methods about comparison of packets (header and body)
     */
    
    /**
     * Two packets have equal headers
     * @param p1 it is the first packet
     * @param p2 it is the second packet
     * @return the corresponding z3 BoolExpr expression for the comparison
     */
    public BoolExpr PacketsHeadersEqual(Expr p1, Expr p2){
        return ctx.mkAnd(new BoolExpr[]{
        		equalIp(functionsMap.get("src").apply(p1), functionsMap.get("src").apply(p2)),
        		equalIp(functionsMap.get("dest").apply(p1), functionsMap.get("dest").apply(p2)),
                equalIp(functionsMap.get("origin").apply(p1), functionsMap.get("origin").apply(p2)),
                ctx.mkEq(functionsMap.get("seq").apply(p1), functionsMap.get("seq").apply(p2)),
                ctx.mkEq(functionsMap.get("lv4proto").apply(p1), functionsMap.get("lv4proto").apply(p2)),
                ctx.mkEq(functionsMap.get("src_port").apply(p1),functionsMap.get("src_port").apply(p2)),
                ctx.mkEq(functionsMap.get("dest_port").apply(p1), functionsMap.get("dest_port").apply(p2)),
                ctx.mkEq(functionsMap.get("options").apply(p1),functionsMap.get("options").apply(p2))});
    }
    
    /**
     * Two packets have equal bodies
     * @param p1 it is the first packet
     * @param p2 it is the second packet
     * @return the corresponding z3 BoolExpr expression for the comparison
     */
    public BoolExpr PacketContentEqual(Expr p1, Expr p2){
        return ctx.mkEq(functionsMap.get("body").apply(p1), functionsMap.get("body").apply(p2));
    }
 
    /*
     * Methods about comparison of IP Addresses and configuration of IP Addresses in Packet Filter rules
     */
      
    /**
     *#TODO jalol: organize formulas
     * This method compares two IP addresses which exploit wildcards
     * @param ip1 it is the first IP address
     * @param ip2 it is the second IP address
     * @return the corresponding z3 BoolExpr expression for the comparison
     */
    public BoolExpr equalIp(Expr ip1, Expr ip2){
    	return ctx.mkOr(new BoolExpr[] {
      		  ctx.mkAnd(ctx.mkEq(ipFunctionsMap.get("ipAddr_1").apply(ip1), ipFunctionsMap.get("ipAddr_1").apply(addressMap.get("wildcard"))),
      					ctx.mkEq(ipFunctionsMap.get("ipAddr_2").apply(ip1), ipFunctionsMap.get("ipAddr_2").apply(addressMap.get("wildcard"))),
      					ctx.mkEq(ipFunctionsMap.get("ipAddr_3").apply(ip1), ipFunctionsMap.get("ipAddr_3").apply(addressMap.get("wildcard"))),
      					ctx.mkEq(ipFunctionsMap.get("ipAddr_4").apply(ip1), ipFunctionsMap.get("ipAddr_4").apply(addressMap.get("wildcard")))),
      		  ctx.mkAnd(ctx.mkEq(ipFunctionsMap.get("ipAddr_1").apply(ip2), ipFunctionsMap.get("ipAddr_1").apply(addressMap.get("wildcard"))),
      					ctx.mkEq(ipFunctionsMap.get("ipAddr_2").apply(ip2), ipFunctionsMap.get("ipAddr_2").apply(addressMap.get("wildcard"))),
      					ctx.mkEq(ipFunctionsMap.get("ipAddr_3").apply(ip2), ipFunctionsMap.get("ipAddr_3").apply(addressMap.get("wildcard"))),
      					ctx.mkEq(ipFunctionsMap.get("ipAddr_4").apply(ip2), ipFunctionsMap.get("ipAddr_4").apply(addressMap.get("wildcard")))),
      		  ctx.mkAnd(ctx.mkEq(ipFunctionsMap.get("ipAddr_1").apply(ip2), ipFunctionsMap.get("ipAddr_1").apply(ip1)),
      		  			ctx.mkOr(ctx.mkAnd(ctx.mkEq(ipFunctionsMap.get("ipAddr_2").apply(ip1), ipFunctionsMap.get("ipAddr_2").apply(addressMap.get("wildcard"))),
  	    	    					ctx.mkEq(ipFunctionsMap.get("ipAddr_3").apply(ip1), ipFunctionsMap.get("ipAddr_3").apply(addressMap.get("wildcard"))),
  	    	    					ctx.mkEq(ipFunctionsMap.get("ipAddr_4").apply(ip1), ipFunctionsMap.get("ipAddr_4").apply(addressMap.get("wildcard")))),
      		  					ctx.mkAnd(ctx.mkEq(ipFunctionsMap.get("ipAddr_2").apply(ip2), ipFunctionsMap.get("ipAddr_2").apply(addressMap.get("wildcard"))),
  	    	    					ctx.mkEq(ipFunctionsMap.get("ipAddr_3").apply(ip2), ipFunctionsMap.get("ipAddr_3").apply(addressMap.get("wildcard"))),
  	    	    					ctx.mkEq(ipFunctionsMap.get("ipAddr_4").apply(ip2), ipFunctionsMap.get("ipAddr_4").apply(addressMap.get("wildcard")))))),
      		  ctx.mkAnd(ctx.mkEq(ipFunctionsMap.get("ipAddr_1").apply(ip2), ipFunctionsMap.get("ipAddr_1").apply(ip1)),
      		  			ctx.mkEq(ipFunctionsMap.get("ipAddr_2").apply(ip2), ipFunctionsMap.get("ipAddr_2").apply(ip1)),
      		  			ctx.mkOr(ctx.mkAnd(ctx.mkEq(ipFunctionsMap.get("ipAddr_3").apply(ip1), ipFunctionsMap.get("ipAddr_3").apply(addressMap.get("wildcard"))),
  	    	    					ctx.mkEq(ipFunctionsMap.get("ipAddr_4").apply(ip1), ipFunctionsMap.get("ipAddr_4").apply(addressMap.get("wildcard")))),
      		  					ctx.mkAnd(ctx.mkEq(ipFunctionsMap.get("ipAddr_3").apply(ip2), ipFunctionsMap.get("ipAddr_3").apply(addressMap.get("wildcard"))),
  	    	    					ctx.mkEq(ipFunctionsMap.get("ipAddr_4").apply(ip2), ipFunctionsMap.get("ipAddr_4").apply(addressMap.get("wildcard")))))),
      		  ctx.mkAnd(ctx.mkEq(ipFunctionsMap.get("ipAddr_1").apply(ip2), ipFunctionsMap.get("ipAddr_1").apply(ip1)),
    		  				ctx.mkEq(ipFunctionsMap.get("ipAddr_2").apply(ip2), ipFunctionsMap.get("ipAddr_2").apply(ip1)),
    		  				ctx.mkEq(ipFunctionsMap.get("ipAddr_3").apply(ip2), ipFunctionsMap.get("ipAddr_3").apply(ip1)),
    		  				ctx.mkOr(ctx.mkEq(ipFunctionsMap.get("ipAddr_4").apply(ip1), ipFunctionsMap.get("ipAddr_4").apply(addressMap.get("wildcard"))),
  	    	    					ctx.mkEq(ipFunctionsMap.get("ipAddr_4").apply(ip2), ipFunctionsMap.get("ipAddr_4").apply(addressMap.get("wildcard"))))),
      		ctx.mkAnd(ctx.mkEq(ipFunctionsMap.get("ipAddr_1").apply(ip2), ipFunctionsMap.get("ipAddr_1").apply(ip1)),
	  				ctx.mkEq(ipFunctionsMap.get("ipAddr_2").apply(ip2), ipFunctionsMap.get("ipAddr_2").apply(ip1)),
	  				ctx.mkEq(ipFunctionsMap.get("ipAddr_3").apply(ip2), ipFunctionsMap.get("ipAddr_3").apply(ip1)),
	  				ctx.mkEq(ipFunctionsMap.get("ipAddr_4").apply(ip2), ipFunctionsMap.get("ipAddr_4").apply(ip1)))
      	});
  
    }
    
    
    
    // #TODO Jalol: organize
    /**
     * This method allows to configure the IP address in a pcket filter rule
     * @param packet_ip it is the packet IP address
     * @param fwIpRule is is the rule IP address to configure
     * @return the corresponding z3 BoolExpr expression for the rule configuration
     */
    public BoolExpr equalPacketIpToPfIpRule(Expr packet_ip, Expr fwIpRule){
    	return ctx.mkOr(new BoolExpr[] {
    		  ctx.mkAnd(ctx.mkEq(ipFunctionsMap.get("ipAddr_1").apply(fwIpRule), ipFunctionsMap.get("ipAddr_1").apply(addressMap.get("wildcard"))),
    					ctx.mkEq(ipFunctionsMap.get("ipAddr_2").apply(fwIpRule), ipFunctionsMap.get("ipAddr_2").apply(addressMap.get("wildcard"))),
    					ctx.mkEq(ipFunctionsMap.get("ipAddr_3").apply(fwIpRule), ipFunctionsMap.get("ipAddr_3").apply(addressMap.get("wildcard"))),
    					ctx.mkEq(ipFunctionsMap.get("ipAddr_4").apply(fwIpRule), ipFunctionsMap.get("ipAddr_4").apply(addressMap.get("wildcard")))),
    		  ctx.mkAnd(ctx.mkEq(ipFunctionsMap.get("ipAddr_1").apply(packet_ip), ipFunctionsMap.get("ipAddr_1").apply(addressMap.get("wildcard"))),
    					ctx.mkEq(ipFunctionsMap.get("ipAddr_2").apply(packet_ip), ipFunctionsMap.get("ipAddr_2").apply(addressMap.get("wildcard"))),
    					ctx.mkEq(ipFunctionsMap.get("ipAddr_3").apply(packet_ip), ipFunctionsMap.get("ipAddr_3").apply(addressMap.get("wildcard"))),
    					ctx.mkEq(ipFunctionsMap.get("ipAddr_4").apply(packet_ip), ipFunctionsMap.get("ipAddr_4").apply(addressMap.get("wildcard")))),
    		  ctx.mkAnd(ctx.mkEq(ipFunctionsMap.get("ipAddr_1").apply(packet_ip), ipFunctionsMap.get("ipAddr_1").apply(fwIpRule)),
    		  			ctx.mkOr(ctx.mkAnd(ctx.mkEq(ipFunctionsMap.get("ipAddr_2").apply(fwIpRule), ipFunctionsMap.get("ipAddr_2").apply(addressMap.get("wildcard"))),
	    	    					ctx.mkEq(ipFunctionsMap.get("ipAddr_3").apply(fwIpRule), ipFunctionsMap.get("ipAddr_3").apply(addressMap.get("wildcard"))),
	    	    					ctx.mkEq(ipFunctionsMap.get("ipAddr_4").apply(fwIpRule), ipFunctionsMap.get("ipAddr_4").apply(addressMap.get("wildcard")))),
    		  					ctx.mkAnd(ctx.mkEq(ipFunctionsMap.get("ipAddr_2").apply(packet_ip), ipFunctionsMap.get("ipAddr_2").apply(addressMap.get("wildcard"))),
	    	    					ctx.mkEq(ipFunctionsMap.get("ipAddr_3").apply(packet_ip), ipFunctionsMap.get("ipAddr_3").apply(addressMap.get("wildcard"))),
	    	    					ctx.mkEq(ipFunctionsMap.get("ipAddr_4").apply(packet_ip), ipFunctionsMap.get("ipAddr_4").apply(addressMap.get("wildcard")))))),
    		  ctx.mkAnd(ctx.mkEq(ipFunctionsMap.get("ipAddr_1").apply(packet_ip), ipFunctionsMap.get("ipAddr_1").apply(fwIpRule)),
    		  			ctx.mkEq(ipFunctionsMap.get("ipAddr_2").apply(packet_ip), ipFunctionsMap.get("ipAddr_2").apply(fwIpRule)),
    		  			ctx.mkOr(ctx.mkAnd(ctx.mkEq(ipFunctionsMap.get("ipAddr_3").apply(fwIpRule), ipFunctionsMap.get("ipAddr_3").apply(addressMap.get("wildcard"))),
	    	    					ctx.mkEq(ipFunctionsMap.get("ipAddr_4").apply(fwIpRule), ipFunctionsMap.get("ipAddr_4").apply(addressMap.get("wildcard")))),
    		  					ctx.mkAnd(ctx.mkEq(ipFunctionsMap.get("ipAddr_3").apply(packet_ip), ipFunctionsMap.get("ipAddr_3").apply(addressMap.get("wildcard"))),
	    	    					ctx.mkEq(ipFunctionsMap.get("ipAddr_4").apply(packet_ip), ipFunctionsMap.get("ipAddr_4").apply(addressMap.get("wildcard")))))),
    		  ctx.mkAnd(ctx.mkEq(ipFunctionsMap.get("ipAddr_1").apply(packet_ip), ipFunctionsMap.get("ipAddr_1").apply(fwIpRule)),
  		  				ctx.mkEq(ipFunctionsMap.get("ipAddr_2").apply(packet_ip), ipFunctionsMap.get("ipAddr_2").apply(fwIpRule)),
  		  				ctx.mkEq(ipFunctionsMap.get("ipAddr_3").apply(packet_ip), ipFunctionsMap.get("ipAddr_3").apply(fwIpRule)),
  		  				ctx.mkOr(ctx.mkEq(ipFunctionsMap.get("ipAddr_4").apply(fwIpRule), ipFunctionsMap.get("ipAddr_4").apply(addressMap.get("wildcard"))),
	    	    					ctx.mkEq(ipFunctionsMap.get("ipAddr_4").apply(packet_ip), ipFunctionsMap.get("ipAddr_4").apply(addressMap.get("wildcard"))))),
    		  ctx.mkAnd(ctx.mkEq(ipFunctionsMap.get("ipAddr_1").apply(fwIpRule), ipFunctionsMap.get("ipAddr_1").apply(packet_ip)),
  	  				ctx.mkEq(ipFunctionsMap.get("ipAddr_2").apply(fwIpRule), ipFunctionsMap.get("ipAddr_2").apply(packet_ip)),
  	  				ctx.mkEq(ipFunctionsMap.get("ipAddr_3").apply(fwIpRule), ipFunctionsMap.get("ipAddr_3").apply(packet_ip)),
  	  				ctx.mkEq(ipFunctionsMap.get("ipAddr_4").apply(fwIpRule), ipFunctionsMap.get("ipAddr_4").apply(packet_ip)))

    	}); 
  
    }
    
    
    
    /**
     * This method compares four separate element in IP
     * @param ip_expr it is the z3 IP address variable
     * @param array it is an array with the four integer components
     * @return the corresponding z3 BoolExpr expression for the comparison
     */
    public BoolExpr equalIpToIntArray(Expr ip_expr, int[] array){
        return ctx.mkAnd(new BoolExpr[]{
                ctx.mkEq(ipFunctionsMap.get("ipAddr_1").apply(ip_expr), ctx.mkInt(array[0])),
                ctx.mkEq(ipFunctionsMap.get("ipAddr_2").apply(ip_expr), ctx.mkInt(array[1])),
                ctx.mkEq(ipFunctionsMap.get("ipAddr_3").apply(ip_expr), ctx.mkInt(array[2])),
                ctx.mkEq(ipFunctionsMap.get("ipAddr_4").apply(ip_expr), ctx.mkInt(array[3]))});
    }
    
    
    /**
     * This methods compare IP address in packet filter rule with given IP of the packet
     * @param index it is the index to retrieve the correct function in functionsMap
     * @param p_0 it is the z3 packet variable
     * @param rule it is the z3 packet filter rule
     * @return the corresponding z3 BoolExpr expression for the comparison
     */
    public BoolExpr equalNodeNameToPFRule(String index, Expr p_0, Expr rule) {
    	if(wildcardManager != null && wildcardManager.areNodesWithIPAddresses()) {
    		return equalPacketIpToPfIpRule(functionsMap.get(index).apply(p_0), rule);
    	}else {
    		return ctx.mkEq(functionsMap.get(index).apply(p_0), rule);
    	}
    }
    
    
    /**
     * This methods compares a node with the IP address
     * @param p it is the node
     * @param address it is the IP address
     * @return the corresponding z3 BoolExpr expression for the comparison
     */
    public  BoolExpr srcAddrPredicate (Expr p, DatatypeExpr address){
    	return equalIp(functionsMap.get("src").apply(p), address);
    }
    
    
    /**
     * This method creates a z3 IP address
     * @param ip it is the String representing the IP address
     * @return the z3 IP address variable
     */
    public DatatypeExpr createIpAddress(String ip){
    	DatatypeExpr fd = (DatatypeExpr) ctx.mkConst(ip, addressType);
    	try{
        	constraints.add(equalIpToIntArray(fd, getIpFromString(ip)));
        }catch(NumberFormatException e){
    		//251 is a prime number, to reduce collisions
    		int symbolicAddr = Math.abs(ip.hashCode()%251);
    		constraints.add(equalIpToIntArray(fd, getIpFromString(symbolicAddr + "." + symbolicAddr + "." + symbolicAddr + "." + symbolicAddr)));
        }
    	return fd;
    }
    
    
    /**
     * This method converts a string IP address into an array of four integer
     * @param ipString the string IP address 
     * @return the array of four integer
     */
    public int[] getIpFromString(String ipString) {
    	int[] res = new int[4];
    	String[] decimalNotation = ipString.split("\\.");
    	if(decimalNotation.length > 4) throw new NumberFormatException();
    	int i = 0;
    	for(String s : decimalNotation){
    		res[i] = Integer.parseInt(s);
    		i++;
    	}
    	return res;
    }

    
    /*
     * Methods about comparison of ports and configuration of ports in Packet Filter rules
     */
    
    
    /**
     * This methods compares a z3 Port expression with the PortInterval object
     * @param port_expr it is the z3 Port expression 
     * @param pi it is the the PortInterval object
     * @return the corresponding z3 BoolExpr expression for the comparison
     */
    public BoolExpr equalPortRangeToInterval(Expr port_expr, PortInterval pi){
        return ctx.mkAnd(new BoolExpr[]{
                ctx.mkEq(portFunctionsMap.get("start").apply(port_expr), ctx.mkInt(pi.getStart())),
                ctx.mkEq(portFunctionsMap.get("end").apply(port_expr), ctx.mkInt(pi.getEnd()))});
    }
 
    
    /**
     * This methods compares two z3 Port expressions 
     * @param port_expr1 it is the first z3 Port expression 
     * @param port_expr2 it is the second z3 Port expression 
     * @return the corresponding z3 BoolExpr expression for the comparison
     */
    public BoolExpr equalPortRangeToRange(Expr port_expr1, Expr port_expr2){
        return ctx.mkAnd(new BoolExpr[]{
                ctx.mkEq(portFunctionsMap.get("start").apply(port_expr1), portFunctionsMap.get("start").apply(port_expr2)),
                ctx.mkEq(portFunctionsMap.get("end").apply(port_expr1), portFunctionsMap.get("end").apply(port_expr2))});
    }
    
    
    
    /**
     * This method is used to configure the ports in a packet filter rule, which can exploits wildcards
     * @param port_expr1 it is the first z3 Port expression 
     * @param rule it is the second z3 Port expression, that is the packet filter rule
     * @return the corresponding z3 BoolExpr expression for the rule configuration
     */
    public BoolExpr equalPortRangeToRule(Expr port_expr1, Expr rule){
        return ctx.mkOr(
        		ctx.mkAnd(
        						ctx.mkEq((IntExpr)portFunctionsMap.get("start").apply(port_expr1), (IntExpr)portFunctionsMap.get("start").apply(rule)),
        						ctx.mkEq((IntExpr)portFunctionsMap.get("end").apply(port_expr1), (IntExpr)portFunctionsMap.get("end").apply(rule))
        				),
        		ctx.mkEq(rule, portMap.get("null")));
    }
    
    
    
    
    
    /*
     * Methods about comparison of L4 protocols and configuration of L4 protocols in Packet Filter rules
     */
    
    /**
     * This method is used to configure the L4 protocol in a packet filter rule
     * @param proto1 it is the packet L4 protocol
     * @param proto2 is the rule L4 protocol
     * @return the corresponding z3 BoolExpr expression for the rule configuration
     */
    public BoolExpr equalPacketLv4ProtoToFwPacketLv4Proto(Expr proto1, Expr proto2){
    	return ctx.mkOr(ctx.mkEq(proto1, proto2),ctx.mkEq(proto2, ctx.mkInt(0)));
    	
    }
    
    /*
     * Additional methods
     */
    
    /**
     * This methods maps each node to an address
     */
    public void setAddressMappings() {
    	for (AllocationNodeMF an : allocationNodes.values()) {
			Expr a_0 = ctx.mkConst(an.getZ3Name() + "_address_mapping_a_0", addressType);
			ArrayList<BoolExpr> or_clause = new ArrayList<BoolExpr>();
			// Constraint 1 addrToNode(foreach ad in addr) = node
				constraints.add(ctx.mkEq(addrToNode.apply(an.getZ3Node()), an.getZ3Name()));
				or_clause.add(ctx.mkEq(a_0, an.getZ3Node()));
			BoolExpr[] orClause = new BoolExpr[or_clause.size()];
			// Constraint 2nodeHasAddr(node, a_0) == Or(foreach ad in addr (a_0
			// == ad))
			// Note we need the iff here to make sure that we set nodeHasAddr to
			// false
			// for other addresses.
			constraints.add(ctx.mkForall(new Expr[] { a_0 },
					ctx.mkEq(ctx.mkOr(or_clause.toArray(orClause)), nodeHasAddr.apply(an.getZ3Name(), a_0)), 1,
					null, null, null, null));

		}
    }

    
 

    
    
    /* NEW */
	public BoolExpr equalIpAddressToPFRule(String IPAddress, Expr fwIpRule) {
		
		int[] parts = getIpFromString(IPAddress);
		return ctx.mkOr(new BoolExpr[] {
	    		  ctx.mkAnd(ctx.mkEq(ipFunctionsMap.get("ipAddr_1").apply(fwIpRule), ipFunctionsMap.get("ipAddr_1").apply(addressMap.get("wildcard"))),
	    					ctx.mkEq(ipFunctionsMap.get("ipAddr_2").apply(fwIpRule), ipFunctionsMap.get("ipAddr_2").apply(addressMap.get("wildcard"))),
	    					ctx.mkEq(ipFunctionsMap.get("ipAddr_3").apply(fwIpRule), ipFunctionsMap.get("ipAddr_3").apply(addressMap.get("wildcard"))),
	    					ctx.mkEq(ipFunctionsMap.get("ipAddr_4").apply(fwIpRule), ipFunctionsMap.get("ipAddr_4").apply(addressMap.get("wildcard")))),
	    		  
	    		  ctx.mkAnd(ctx.mkEq(ctx.mkInt(parts[0]), ipFunctionsMap.get("ipAddr_1").apply(fwIpRule)),
	    		  			ctx.mkEq(ipFunctionsMap.get("ipAddr_2").apply(fwIpRule), ipFunctionsMap.get("ipAddr_2").apply(addressMap.get("wildcard"))),
		    	    					ctx.mkEq(ipFunctionsMap.get("ipAddr_3").apply(fwIpRule), ipFunctionsMap.get("ipAddr_3").apply(addressMap.get("wildcard"))),
		    	    					ctx.mkEq(ipFunctionsMap.get("ipAddr_4").apply(fwIpRule), ipFunctionsMap.get("ipAddr_4").apply(addressMap.get("wildcard")))),
	    		  					
	    		  ctx.mkAnd(ctx.mkEq(ctx.mkInt(parts[0]), ipFunctionsMap.get("ipAddr_1").apply(fwIpRule)),
	    		  			ctx.mkEq(ctx.mkInt(parts[1]), ipFunctionsMap.get("ipAddr_2").apply(fwIpRule)),
	    		  			ctx.mkEq(ipFunctionsMap.get("ipAddr_3").apply(fwIpRule), ipFunctionsMap.get("ipAddr_3").apply(addressMap.get("wildcard"))),
		    	    					ctx.mkEq(ipFunctionsMap.get("ipAddr_4").apply(fwIpRule), ipFunctionsMap.get("ipAddr_4").apply(addressMap.get("wildcard")))),
	    		  					
	    		  ctx.mkAnd(ctx.mkEq(ctx.mkInt(parts[0]), ipFunctionsMap.get("ipAddr_1").apply(fwIpRule)),
	  		  				ctx.mkEq(ctx.mkInt(parts[1]), ipFunctionsMap.get("ipAddr_2").apply(fwIpRule)),
	  		  				ctx.mkEq(ctx.mkInt(parts[2]), ipFunctionsMap.get("ipAddr_3").apply(fwIpRule)),
	  		  				ctx.mkEq(ipFunctionsMap.get("ipAddr_4").apply(fwIpRule), ipFunctionsMap.get("ipAddr_4").apply(addressMap.get("wildcard")))),
		    	    					
	    		  ctx.mkAnd(ctx.mkEq(ipFunctionsMap.get("ipAddr_1").apply(fwIpRule), ctx.mkInt(parts[0])),
	  	  				ctx.mkEq(ipFunctionsMap.get("ipAddr_2").apply(fwIpRule), ctx.mkInt(parts[1])),
	  	  				ctx.mkEq(ipFunctionsMap.get("ipAddr_3").apply(fwIpRule), ctx.mkInt(parts[2])),
	  	  				ctx.mkEq(ipFunctionsMap.get("ipAddr_4").apply(fwIpRule), ctx.mkInt(parts[3])))

	    	}); 
		
	}
	
	
	//Given a tuple q and a fw rule, the function returns true if the rule condition completely includes the 5 values of the tuple
	public BoolExpr matchAll(Predicate predicate, Expr IPSrcP, Expr IPDstP, Expr PSrcP, Expr PDstP, Expr protoP) {
		int[] IPSrcQ = getIpFromString(predicate.firstIPSrcToString());
		int[] IPDstQ = getIpFromString(predicate.firstIPDstToString());
		
		return ctx.mkAnd(new BoolExpr[] {
				//Check IPSrc
				ctx.mkAnd(//(IP1q == IP1p || IP1p == *) AND (IP2q == IP2p || IP2p == *) AND (IP3q == IP3p || IP3p == *) AND (IP4q == IP4p || IP4p == *)
						ctx.mkOr(ctx.mkEq(ctx.mkInt(IPSrcQ[0]), ipFunctionsMap.get("ipAddr_1").apply(IPSrcP)), ctx.mkEq(ipFunctionsMap.get("ipAddr_1").apply(IPSrcP), ipFunctionsMap.get("ipAddr_1").apply(addressMap.get("wildcard")))),
						ctx.mkOr(ctx.mkEq(ctx.mkInt(IPSrcQ[1]), ipFunctionsMap.get("ipAddr_2").apply(IPSrcP)), ctx.mkEq(ipFunctionsMap.get("ipAddr_2").apply(IPSrcP), ipFunctionsMap.get("ipAddr_2").apply(addressMap.get("wildcard")))),
						ctx.mkOr(ctx.mkEq(ctx.mkInt(IPSrcQ[2]), ipFunctionsMap.get("ipAddr_3").apply(IPSrcP)), ctx.mkEq(ipFunctionsMap.get("ipAddr_3").apply(IPSrcP), ipFunctionsMap.get("ipAddr_3").apply(addressMap.get("wildcard")))),
						ctx.mkOr(ctx.mkEq(ctx.mkInt(IPSrcQ[3]), ipFunctionsMap.get("ipAddr_4").apply(IPSrcP)), ctx.mkEq(ipFunctionsMap.get("ipAddr_4").apply(IPSrcP), ipFunctionsMap.get("ipAddr_4").apply(addressMap.get("wildcard"))))
					),
				//Check IPdst
				ctx.mkAnd(//(IP1q == IP1p || IP1p == *) AND (IP2q == IP2p || IP2p == *) AND (IP3q == IP3p || IP3p == *) AND (IP4q == IP4p || IP4p == *)
						ctx.mkOr(ctx.mkEq(ctx.mkInt(IPDstQ[0]), ipFunctionsMap.get("ipAddr_1").apply(IPDstP)), ctx.mkEq(ipFunctionsMap.get("ipAddr_1").apply(IPDstP), ipFunctionsMap.get("ipAddr_1").apply(addressMap.get("wildcard")))),
						ctx.mkOr(ctx.mkEq(ctx.mkInt(IPDstQ[1]), ipFunctionsMap.get("ipAddr_2").apply(IPDstP)), ctx.mkEq(ipFunctionsMap.get("ipAddr_2").apply(IPDstP), ipFunctionsMap.get("ipAddr_2").apply(addressMap.get("wildcard")))),
						ctx.mkOr(ctx.mkEq(ctx.mkInt(IPDstQ[2]), ipFunctionsMap.get("ipAddr_3").apply(IPDstP)), ctx.mkEq(ipFunctionsMap.get("ipAddr_3").apply(IPDstP), ipFunctionsMap.get("ipAddr_3").apply(addressMap.get("wildcard")))),
						ctx.mkOr(ctx.mkEq(ctx.mkInt(IPDstQ[3]), ipFunctionsMap.get("ipAddr_4").apply(IPDstP)), ctx.mkEq(ipFunctionsMap.get("ipAddr_4").apply(IPDstP), ipFunctionsMap.get("ipAddr_4").apply(addressMap.get("wildcard"))))
					),	
				equalPortRangeToPFRule(predicate.firstPSrcToString(), PSrcP),
				equalPortRangeToPFRule(predicate.firstPDstToString(), PDstP),
				equalLv4ProtoToFwLv4Proto(predicate.firstProto().ordinal(), protoP)
		});
	}
	
	
	public BoolExpr matchNone(Predicate predicate, Expr IPSrcP, Expr IPDstP, Expr PSrcP, Expr PDstP, Expr protoP) {
		return ctx.mkNot(matchAll(predicate, IPSrcP, IPDstP, PSrcP, PDstP, protoP));
	}



	public BoolExpr equalPortRangeToPFRule(String srcPort, Expr rule) {
		Expr port_expr1 = !srcPort.toString().equals("*") ? portMap.get(srcPort) : portMap.get("null");
		return ctx.mkOr(
        		ctx.mkAnd(
        						ctx.mkEq((IntExpr)portFunctionsMap.get("start").apply(port_expr1), (IntExpr)portFunctionsMap.get("start").apply(rule)),
        						ctx.mkEq((IntExpr)portFunctionsMap.get("end").apply(port_expr1), (IntExpr)portFunctionsMap.get("end").apply(rule))
        				),
        		ctx.mkEq(rule, portMap.get("null")));
	}



	public BoolExpr equalLv4ProtoToFwLv4Proto(int proto1, Expr proto2) {
		return ctx.mkOr(ctx.mkEq(ctx.mkInt(proto1), proto2),ctx.mkEq(proto2, ctx.mkInt(0)), ctx.mkEq(ctx.mkInt(proto1), ctx.mkInt(0)));
	}

}