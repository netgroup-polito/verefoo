/**
 * 
 */
package it.polito.verefoo.extra;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.microsoft.z3.Context;

import it.polito.verefoo.allocation.AllocationNode;
import it.polito.verefoo.jaxb.EType;
import it.polito.verefoo.jaxb.Node;
import it.polito.verefoo.jaxb.Property;
import it.polito.verigraph.solver.NetContext;

/**
 * This class generates the nework context (the link between nodenames and node IPs in z3)
 */
public final class NetContextGenerator{
	/**
	 * This method generates the nework context (the link between nodenames and node IPs in z3)
	 * @param ctx Z3 Context
	 * @param nodes Node List
	 * @param properties the list of properties
	 * @param allocationNodes 
	 * @return NetContext
	 */
	public static NetContext generate(Context ctx,List<Node> nodes,List<Property> properties, HashMap<String, AllocationNode> allocationNodes){
		//the @ is used internally as a special symbol
		for(Node n : nodes){
			if(n.getName().contains("@"))
				throw new BadGraphError("Invalid node name "+ n.getName() + ", it can't contain @", EType.INVALID_SERVICE_GRAPH);
		}
		String[] nodesname={};
		nodesname=nodes.stream().map((n)->n.getName()).collect(Collectors.toCollection(ArrayList<String>::new)).toArray(nodesname);
		//suppose nodename=nodeip;
		String[] nodesip=nodesname;
		String[] src_portRange={};
		src_portRange=properties.stream().map(p -> p.getSrcPort()).filter(p -> p!=null).collect(Collectors.toCollection(ArrayList<String>::new)).toArray(src_portRange);
		String[] dst_portRange={};
		dst_portRange=properties.stream().map(p -> p.getDstPort()).filter(p -> p!=null).collect(Collectors.toCollection(ArrayList<String>::new)).toArray(dst_portRange);
	    return new NetContext(ctx,allocationNodes, nodesname,nodesip,src_portRange, dst_portRange);
	}
}
