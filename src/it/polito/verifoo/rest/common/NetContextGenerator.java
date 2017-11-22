/**
 * 
 */
package it.polito.verifoo.rest.common;

import java.util.List;

import com.microsoft.z3.Context;

import it.polito.verifoo.rest.jaxb.Node;
import it.polito.verigraph.mcnet.components.NetContext;

/**
 * @author Raffaele
 *
 */
public final class NetContextGenerator{
	public static NetContext generate(Context ctx,List<Node> nodes){
		String[] nodesname=new String[nodes.size()];
		String[] nodesip=new String[nodes.size()];
	    for(int i = 0; i < nodes.size(); i++){
	    	nodesname[i] = new String(nodes.get(i).getName());
			nodesip[i] = new String(nodes.get(i).getIp());
	    }
	    return new NetContext(ctx,nodesname,nodesip);
	}
}
