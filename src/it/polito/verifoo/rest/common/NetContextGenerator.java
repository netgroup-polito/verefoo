/**
 * 
 */
package it.polito.verifoo.rest.common;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.microsoft.z3.Context;

import it.polito.verifoo.rest.jaxb.Node;
import it.polito.verigraph.mcnet.components.NetContext;

/**
 * This class generate the nework context (the link between nodenames and node ip in z3)
 */
public final class NetContextGenerator{
	public static NetContext generate(Context ctx,List<Node> nodes){
		String[] nodesname={};
		nodesname=nodes.stream().map((n)->n.getName()).collect(Collectors.toCollection(ArrayList<String>::new)).toArray(nodesname);
		//suppose nodename=nodeip;
		String[] nodesip=nodesname;
	    return new NetContext(ctx,nodesname,nodesip);
	}
}
