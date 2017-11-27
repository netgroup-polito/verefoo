package it.polito.verifoo.rest.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.microsoft.z3.Context;
import com.microsoft.z3.DatatypeExpr;

import it.polito.verigraph.mcnet.components.NetContext;
import it.polito.verigraph.mcnet.components.Network;
import it.polito.verigraph.mcnet.components.NetworkObject;
import it.polito.verigraph.mcnet.components.Tuple;
import it.polito.verigraph.mcnet.netobjs.AclFirewall;
import it.polito.verigraph.mcnet.netobjs.Classifier;
import it.polito.verigraph.mcnet.netobjs.DumbNode;
import it.polito.verigraph.mcnet.netobjs.PolitoAntispam;
import it.polito.verigraph.mcnet.netobjs.PolitoCache;
import it.polito.verigraph.mcnet.netobjs.PolitoEndHost;
import it.polito.verigraph.mcnet.netobjs.PolitoFieldModifier;
import it.polito.verigraph.mcnet.netobjs.PolitoIDS;
import it.polito.verigraph.mcnet.netobjs.PolitoNat;
import it.polito.verifoo.rest.jaxb.FunctionalTypes;
import it.polito.verifoo.rest.jaxb.Node;
/**
 * This class generate a Map of a new network object and associated node.
 * @author Raffaele
 *
 */
public class NodeNetworkObject extends HashMap<Node, NetworkObject> implements java.util.function.Consumer<Node>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8001920385236985297L;
	private Logger logger = LogManager.getLogger("mylog");

	private Context ctx;
    private NetContext nctx;
    private Network net;
    /**
     * This class is an helper to generate network object
     * @param ctx Z3 Context
     * @param nctx NetworkContext
     * @param net Network
     */
    public NodeNetworkObject(Context ctx, NetContext nctx, Network net) {
		super();
		this.ctx = ctx;
		this.nctx = nctx;
		this.net = net;
	}

	/**
	 * Attach all network objects to the Network
	 */
	public void attachToNet(){
		this.forEach((n,netobjs)->net.attach(netobjs));
	}
	/**
	 * Generate Acl for firewall network objects
	 */
	public void generateAcl(){
		this.forEach(
				(n,netobjs)->{
					if(n.getFunctionalType().equals(FunctionalTypes.FIREWALL)){
						n.getConfiguration().getFirewall().getElements().forEach((e)->{
							ArrayList<Tuple<DatatypeExpr,DatatypeExpr>> acl = new ArrayList<Tuple<DatatypeExpr,DatatypeExpr>>();
							    Tuple<DatatypeExpr,DatatypeExpr> rule=new Tuple<DatatypeExpr,DatatypeExpr>(nctx.am.get(e.getSource()),nctx.am.get(e.getDestination()));
							    acl.add(rule);
							    System.out.println("Adding blocking rule " + acl);
							    ((AclFirewall)netobjs).addAcls(acl);
							    logger.debug("Added acl:"+ rule.toString() +" to "+n.getName());
						});
						
					}
				}
		);
	}
	
	/* (non-Javadoc)
	 * @see java.util.function.Consumer#accept(java.lang.Object)
	 * @param Node n
	 * @description This function process the node and generate a network object according to VNF type.
	 */
	@Override
	public void accept(Node n) {
		FunctionalTypes ftype=n.getFunctionalType();
		switch (ftype) {
			case FIREWALL:{
				this.put(n,new AclFirewall(ctx,new Object[]{nctx.nm.get(n.getName()),net,nctx}));
				break;
			}
			case FIELDMODIFIER:{					
				this.put(n,new PolitoFieldModifier(ctx,new Object[]{nctx.nm.get(n.getName()),net,nctx}));
				break;
			}
			case ENDHOST:{
				//TODO
				this.put(n,new PolitoEndHost(ctx,new Object[]{nctx.nm.get(n.getName()),net,nctx}));
				break;
			}
			case ANTISPAM:{
				//TODO IP are string, this doesn't work
				PolitoAntispam spam=new PolitoAntispam(ctx,new Object[]{nctx.nm.get(n.getName()),net,nctx});
				this.put(n,spam);
				int[] blacklist=n.getConfiguration().getAntispam().getSource().stream().mapToInt((s)->Integer.parseInt(s)).toArray();
				spam.installAntispam(blacklist);
				break;
			}
			case CACHE:{
				//TODO
				this.put(n,new PolitoCache(ctx,new Object[]{nctx.nm.get(n.getName()),net,nctx}));
				break;
			}
			case DPI:{
				//TODO notAllowed field is a string, this doesn't work
				PolitoIDS ids=new PolitoIDS(ctx,new Object[]{nctx.nm.get(n.getName()),net,nctx});
				this.put(n,ids);
				int[] blacklist=n.getConfiguration().getDpi().getNotAllowed().stream().mapToInt((s)->Integer.parseInt(s)).toArray();
				ids.installIDS(blacklist);
				break;
			}
			case MAILCLIENT:{
				//TODO
				this.put(n,new PolitoEndHost(ctx,new Object[]{nctx.nm.get(n.getName()),net,nctx}));
				break;
			}
			// TODO for PolitoMailClient is needed another parameter
			case MAILSERVER:{
				this.put(n,new PolitoEndHost(ctx,new Object[]{nctx.nm.get(n.getName()),net,nctx}));
				break;
			}
			case NAT:{					
				this.put(n,new PolitoNat(ctx,new Object[]{nctx.nm.get(n.getName()),net,nctx}));
				break;
			}
			case VPNACCESS:{					
				break;
			}
			case VPNEXIT:{					
				break;
			}
			case WEBCLIENT:{
				// TODO for PolitoWebClient is needed another parameter
				this.put(n,new PolitoEndHost(ctx,new Object[]{nctx.nm.get(n.getName()),net,nctx}));
				break;
			}
			case WEBSERVER:{
				this.put(n,new PolitoEndHost(ctx,new Object[]{nctx.nm.get(n.getName()),net,nctx}));
				break;
			}
			default:{
				System.err.println("Braiiinssssssssssss!");
				break;
			}
		}
	}
	
	
}
