/*******************************************************************************
 * Copyright (c) 2017 Politecnico di Torino and others.
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

import it.polito.verefoo.allocation.AllocationNode;
import it.polito.verefoo.extra.WildcardManager;
import it.polito.verefoo.utils.PortInterval;
import it.polito.verefoo.utils.Tuple;

/**
 * Basic fields and other things required for model checking.
 *
 */
public class NetContext {
	public Context ctx;
	public WildcardManager wildcardManager;
	public FuncDecl nodeHasAddr,addrToNode,send,recv,deny;
	private HashMap<String, AllocationNode> allocationNodes;
	
    public List<BoolExpr> constraints;
    public List<Tuple<BoolExpr, String>> softConstrAutoConf;
    public List<Tuple<BoolExpr, String>> softConstrAutoPlace;
    public List<Tuple<BoolExpr, String>> softConstrWildcard;
    public List<Tuple<BoolExpr, String>> softConstrProtoWildcard;
    public List<Tuple<BoolExpr, String>> softConstrPorts;

    public HashMap<String,DatatypeExpr> nodeMap; //list of nodes, callable by node name
    public HashMap<String,DatatypeExpr> addressMap; // list of addresses, callable by address name
    public HashMap<String,DatatypeExpr> portMap; // list of port range, callable by string

    
    public HashMap<String,FuncDecl> functionsMap;
    public HashMap<String,FuncDecl> portFunctionsMap;
    public HashMap<String,FuncDecl> ipFunctionsMap;    
    
    public EnumSort nodeType;
	public DatatypeSort addressType;
	public DatatypeSort portType;
    public DatatypeSort packetType;

    /*   Constants definition */
	public final int MAX_PORT = 65535;
    public final int HTTP_REQUEST    = 1;
    public final int HTTP_RESPONSE   = 2;
    public final int POP3_REQUEST    = 3;
    public final int POP3_RESPONSE   = 4;

    // weights
    public final int WPROTOWILDCARD   = 1000;
    public final int WIPWILDCARD   = -10;
    public final int WAUTOCONF   = 1;
    public final int WAUTOPLACEMENT   = 100000000;
    public final int WPORTS   = 1000;

 
    /**
     * Context for all of the rest that follows. Every network needs one of these
     * @param ctx
     * @param args
     */
    public NetContext(Context ctx, HashMap<String, AllocationNode> allocationNodes, Object[]... args){
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
     * Main Methods of NetContext class
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
     * @param srcp_ranges it is the array of source ports -> it can be a single port or a range of ports divided by a "-"
     * @param dstp_ranges it is the array of destination ports
     */
    private void mkTypes (String[] nodes, String[] addresses, String[] srcp_ranges, String[] dstp_ranges){
    	  
        //----------- Nodes in this network
        nodeType = ctx.mkEnumSort("Node", nodes);
        for(int i=0;i<nodeType.getConsts().length;i++){
            DatatypeExpr fd  = (DatatypeExpr)nodeType.getConst(i);   
            nodeMap.put(fd.toString().replace("|", ""),fd);
            AllocationNode n = allocationNodes.get(nodes[i]);
            n.setZ3Name(fd);
        }
        

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
    	/*for (AllocationNode an : allocationNodes.values()) {
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

		}*/
    }
    
   
    /**
     * This method converts a z3 boolean into a z3 integer
     * @param value it is the z3 boolean
     * @return the converted z3 integer
     */
    public IntExpr bool_to_int(BoolExpr value) {
		IntExpr integer = ctx.mkIntConst("integer_" + value);
		constraints.add((ctx.mkImplies(value, ctx.mkEq(integer, ctx.mkInt(1)))));
		constraints.add((ctx.mkImplies(ctx.mkNot(value), ctx.mkEq(integer, ctx.mkInt(0)))));
		return integer;
	}

    
    
    /**
     * Setter of the wildcard manager
     * @param wildcardManager it is the wildcard manager to set
     */
    public void setWildcardManager(WildcardManager wildcardManager) {
    	this.wildcardManager = wildcardManager;
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



	public BoolExpr equalPortRangeToPFRule(String srcPort, Expr rule) {
		Expr port_expr1 = portMap.get(srcPort);
		return ctx.mkOr(
        		ctx.mkAnd(
        						ctx.mkEq((IntExpr)portFunctionsMap.get("start").apply(port_expr1), (IntExpr)portFunctionsMap.get("start").apply(rule)),
        						ctx.mkEq((IntExpr)portFunctionsMap.get("end").apply(port_expr1), (IntExpr)portFunctionsMap.get("end").apply(rule))
        				),
        		ctx.mkEq(rule, portMap.get("null")),
        		ctx.mkEq(portMap.get(srcPort), portMap.get("null")));
	}



	public BoolExpr equalLv4ProtoToFwLv4Proto(int proto1, Expr proto2) {
		return ctx.mkOr(ctx.mkEq(ctx.mkInt(proto1), proto2),ctx.mkEq(proto2, ctx.mkInt(0)), ctx.mkEq(ctx.mkInt(proto1), ctx.mkInt(0)));
	}

}