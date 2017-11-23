package it.polito.verifoo.rest.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import it.polito.verigraph.mcnet.netobjs.PolitoIDS;
import it.polito.verigraph.mcnet.netobjs.PolitoNat;
import it.polito.verifoo.rest.jaxb.ConfigurationType;
import it.polito.verifoo.rest.jaxb.FName;
import it.polito.verifoo.rest.jaxb.Node;
import it.polito.verifoo.rest.jaxb.VNF;
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
    private List<VNF> vnfCat;
    /**
     * This class is an helper to generate network object
     * @param ctx Z3 Context
     * @param nctx NetworkContext
     * @param net Network
     * @param vnfCat List of all VNF (used for decide witch type of NetObj is associated to a node)
     */
    public NodeNetworkObject(Context ctx, NetContext nctx, Network net, List<VNF> vnfCat) {
		super();
		this.ctx = ctx;
		this.nctx = nctx;
		this.net = net;
		this.vnfCat = vnfCat;
	}
	/**
	 * @param n Node
	 * @return VNF for the node
	 */
	private VNF getVNF(Node n){
		return this.vnfCat.stream().filter(nf->nf.getName().equals(n.getVNF())).findFirst().get();
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
					VNF vnf=getVNF(n);
					if(vnf.getFunctionalType().equals(FName.FW)){
						vnf.getConfiguration().forEach((c)->{
							if(c.getName()!=null && c.getValue() !=null && !c.getName().isEmpty()&& !c.getValue().isEmpty()){
						    	ArrayList<Tuple<DatatypeExpr,DatatypeExpr>> acl = new ArrayList<Tuple<DatatypeExpr,DatatypeExpr>>();
								Tuple<DatatypeExpr,DatatypeExpr> rule=new Tuple<DatatypeExpr,DatatypeExpr>(nctx.am.get(c.getName()),nctx.am.get(c.getValue()));
								acl.add(rule);
								((AclFirewall)netobjs).addAcls(acl);
								logger.debug("Added acl:"+ rule.toString());
							}else{
								throw new IllegalArgumentException();
							} 
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
		FName ftype=getVNF(n).getFunctionalType(); 
		switch (ftype) {
			case FW:{
				this.put(n,new AclFirewall(ctx,new Object[]{nctx.nm.get(n.getName()),net,nctx}));
				break;
			}
			case CLASSIFIER:{					
				this.put(n,new Classifier(ctx,new Object[]{nctx.nm.get(n.getName()),net,nctx}));
				break;
			}
			case DUMB:{
				this.put(n,new DumbNode(ctx,new Object[]{nctx.nm.get(n.getName()),net,nctx}));
				break;
			}
			case ENDHOST:{
				//TODO
				this.put(n,new PolitoEndHost(ctx,new Object[]{nctx.nm.get(n.getName()),net,nctx}));
				break;
			}
			case SPAM:{
				PolitoAntispam spam=new PolitoAntispam(ctx,new Object[]{nctx.nm.get(n.getName()),net,nctx});
				this.put(n,spam);
				int[] blacklist=getVNF(n).getConfiguration().stream().map(ConfigurationType::getValue).mapToInt(s->Integer.parseInt(s)).toArray();
				spam.installAntispam(blacklist);
				break;
			}
			case CACHE:{
				//TODO
				this.put(n,new PolitoCache(ctx,new Object[]{nctx.nm.get(n.getName()),net,nctx}));
				break;
			}
			case IDS:{
				PolitoIDS ids=new PolitoIDS(ctx,new Object[]{nctx.nm.get(n.getName()),net,nctx});
				this.put(n,ids);
				int[] blacklist=getVNF(n).getConfiguration().stream().map(ConfigurationType::getValue).mapToInt(s->Integer.parseInt(s)).toArray();
				ids.installIDS(blacklist);
				break;
			}
			case MAIL_CLIENT:{
				this.put(n,new PolitoEndHost(ctx,new Object[]{nctx.nm.get(n.getName()),net,nctx}));
				break;
			}
			// TODO for PolitoMailClient is needed another parameter
			case MAIL_SERVER:{
				this.put(n,new PolitoEndHost(ctx,new Object[]{nctx.nm.get(n.getName()),net,nctx}));
				break;
			}
			case NAT:{					
				this.put(n,new PolitoNat(ctx,new Object[]{nctx.nm.get(n.getName()),net,nctx}));
				break;
			}
			case VPN:{					
				break;
			}
			case WEB_CLIENT:{
				// TODO for PolitoWebClient is needed another parameter
				this.put(n,new PolitoEndHost(ctx,new Object[]{nctx.nm.get(n.getName()),net,nctx}));
				break;
			}
			case WEB_SERVER:{
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
