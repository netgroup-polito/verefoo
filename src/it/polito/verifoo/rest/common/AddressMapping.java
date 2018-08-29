package it.polito.verifoo.rest.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.microsoft.z3.DatatypeExpr;

import it.polito.verifoo.rest.jaxb.Node;
import it.polito.verigraph.mcnet.components.NetContext;
import it.polito.verigraph.mcnet.components.Network;
import it.polito.verigraph.mcnet.components.NetworkObject;
import it.polito.verigraph.mcnet.components.Tuple;
/**
 * This class generates the data structure to correlate node name and node ip.
 */
public class AddressMapping extends ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>>{

    /**
	 * This class is used to generate an address mapping between z3 ip addresses and correlated network objects
	 */
	private static final long serialVersionUID = -4069135108727238094L;
	private HashMap<Node,NetworkObject> netobjs;
    private NetContext nctx;
	private Network net;
	/**
	 * Costructor of the class
	 * @param netobjs A map of node and associated NetworkObject
	 * @param nctx The z3 Net Context
	 * @param net The z3 Network
	 */
	public AddressMapping(HashMap<Node, NetworkObject> netobjs, NetContext nctx, Network net) {
		super();
		this.netobjs = netobjs;
		this.nctx = nctx;
		this.net=net;
	}
	/**
	 * For each node of the list it generates a tuple formed by a netobj and its addresses
	 * @param nodes
	 */
	public void setAddressMappings(List<Node> nodes) {
		nodes.forEach((n)->{
			ArrayList<DatatypeExpr> al = new ArrayList<DatatypeExpr>();
			al.add(nctx.am.get(n.getName()));
			this.add(new Tuple<>(netobjs.get(n), al));
		});
		net.setAddressMappings(this);
	}

}
