package it.polito.verifoo.rest.common;

import java.util.ArrayList;
import java.util.HashMap;

import com.microsoft.z3.DatatypeExpr;

import it.polito.verifoo.rest.jaxb.Node;
import it.polito.verigraph.mcnet.components.NetContext;
import it.polito.verigraph.mcnet.components.NetworkObject;
import it.polito.verigraph.mcnet.components.Tuple;
/**
 * This class generate the Address Mapping List between nodename and nodeip.
 * @author Raffaele
 *
 */
public class AddressMapping extends ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>>  implements java.util.function.Consumer<Node> {

    /**
	 * 
	 */
	private static final long serialVersionUID = -4069135108727238094L;
	private HashMap<Node,NetworkObject> netobjs;
    private NetContext nctx;
	public AddressMapping(HashMap<Node, NetworkObject> netobjs, NetContext nctx) {
		super();
		this.netobjs = netobjs;
		this.nctx = nctx;
	}
	@Override
	public void accept(Node n) {
		ArrayList<DatatypeExpr> al = new ArrayList<DatatypeExpr>();
		al.add(nctx.am.get(n.getName()));
		this.add(new Tuple<>(netobjs.get(n), al));
	}

}
