package it.polito.verifoo.rest.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.ProcessingException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.microsoft.z3.Context;
import com.microsoft.z3.DatatypeExpr;

import it.polito.verifoo.rest.jaxb.FunctionalTypes;
import it.polito.verifoo.rest.jaxb.Node;
import it.polito.verigraph.mcnet.components.NetContext;
import it.polito.verigraph.mcnet.components.Network;
import it.polito.verigraph.mcnet.components.NetworkObject;
import it.polito.verigraph.mcnet.components.Tuple;
import it.polito.verigraph.mcnet.netobjs.AclFirewall;
import it.polito.verigraph.mcnet.netobjs.PolitoAntispam;
import it.polito.verigraph.mcnet.netobjs.PolitoCache;
import it.polito.verigraph.mcnet.netobjs.PolitoEndHost;
import it.polito.verigraph.mcnet.netobjs.PolitoFieldModifier;
import it.polito.verigraph.mcnet.netobjs.PolitoIDS;
import it.polito.verigraph.mcnet.netobjs.PolitoMailClient;
import it.polito.verigraph.mcnet.netobjs.PolitoMailServer;
import it.polito.verigraph.mcnet.netobjs.PolitoNat;
import it.polito.verigraph.mcnet.netobjs.PolitoWebClient;
import it.polito.verigraph.mcnet.netobjs.PolitoWebServer;
/**
 * This class generate a Map of a new network object and associated node.
 * @author Raffaele
 *
 */
public class NodeNetworkObject extends HashMap<Node, NetworkObject>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8001920385236985297L;
	private Logger logger = LogManager.getLogger("mylog");

	private Context ctx;
    private NetContext nctx;
    private Network net;
	private List<it.polito.verifoo.rest.jaxb.Node> nodes;
    /**
     * This class is an helper to generate network object
     * @param ctx Z3 Context
     * @param nctx NetworkContext
     * @param net Network
     * @param nodes 
     */
    public NodeNetworkObject(Context ctx, NetContext nctx, Network net, List<it.polito.verifoo.rest.jaxb.Node> nodes) {
		super();
		this.ctx = ctx;
		this.nctx = nctx;
		this.net = net;
		this.nodes=nodes;
		nodes.forEach(this::generateNetObj);
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
	/**
	 * Generate cachematerial for cache network objects
	 */
	public void generateCache(){
		this.forEach(
				(n,netobjs)->{
					if(n.getFunctionalType().equals(FunctionalTypes.CACHE)){
						PolitoCache c=(PolitoCache) netobjs;
						List<String> resource = n.getConfiguration().getCache().getResource();
						NetworkObject[] internalNodes = {};
						internalNodes=this.entrySet().stream().filter(obj->resource.contains(obj.getKey().getName())).map(obj->obj.getValue()).collect(Collectors.toList()).toArray(internalNodes);
						for(NetworkObject no:internalNodes)
							System.out.println("Install cache on "+no.toString());
						c.installCache(internalNodes);			
						
					}
				}
		);
	}
	/**
	 * @param Node n
	 * @description This function process the node and generate a network object according to VNF type.
	 */
	public void generateNetObj(Node n) {
		try {
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
					PolitoEndHost eh=new PolitoEndHost(ctx,new Object[]{nctx.nm.get(n.getName()),net,nctx});
					this.put(n,eh);
					eh.installEndHost(new PacketWrapper(n.getConfiguration().getEndhost(), nctx));
					break;
				}
				case ANTISPAM:{
					//TODO IP are string, this doesn't work
					PolitoAntispam spam=new PolitoAntispam(ctx,new Object[]{nctx.nm.get(n.getName()),net,nctx});
					this.put(n,spam);
					int[] blacklist=n.getConfiguration().getAntispam().getSource().stream().mapToInt((s)->s.hashCode()).toArray();
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
					int[] blacklist=n.getConfiguration().getDpi().getNotAllowed().stream().mapToInt((s)->s.hashCode()).toArray();
					ids.installIDS(blacklist);
					break;
				}
				case MAILCLIENT:{
					//TODO
					PolitoMailClient eh=new PolitoMailClient(ctx,new Object[]{nctx.nm.get(n.getName()),net,nctx,nctx.am.get(n.getConfiguration().getMailclient().getMailserver())});
					this.put(n,eh);
					break;
				}
				// TODO for PolitoMailClient is needed another parameter
				case MAILSERVER:{
					PolitoMailServer eh=new PolitoMailServer(ctx,new Object[]{nctx.nm.get(n.getName()),net,nctx});
					this.put(n,eh);
					break;
				}
				case NAT:{		
					PolitoNat nat=new PolitoNat(ctx,new Object[]{nctx.nm.get(n.getName()),net,nctx});
					this.put(n,nat);
					nat.setInternalAddress(n.getConfiguration().getNat().getSource().stream().map((s)->nctx.am.get(s)).collect(Collectors.toCollection(ArrayList::new)));							
					break;
				}
				case VPNACCESS:{					
					break;
				}
				case VPNEXIT:{					
					break;
				}
				case WEBCLIENT:{
					PolitoWebClient eh=new PolitoWebClient(ctx,new Object[]{nctx.nm.get(n.getName()),net,nctx,nctx.am.get(n.getConfiguration().getWebclient().getNameWebServer())});
					this.put(n,eh);
					break;
				}
				case WEBSERVER:{
					PolitoWebServer eh=new PolitoWebServer(ctx,new Object[]{nctx.nm.get(n.getName()),net,nctx});
					this.put(n,eh);
					break;
				}
				default:{
					System.err.println("Braiiinssssssssssss!");
					break;
				}
			}
		}catch (BadNffgException e) {
			throw new ProcessingException(e.getLocalizedMessage());
		}
		catch (NumberFormatException e) {			
			throw new ProcessingException("Cannot convert to int"+e.getLocalizedMessage());
		}
	}
	
	
}
