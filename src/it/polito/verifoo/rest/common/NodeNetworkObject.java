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

import it.polito.verifoo.rest.jaxb.EType;
import it.polito.verifoo.rest.jaxb.FunctionalTypes;
import it.polito.verifoo.rest.jaxb.NFV;
import it.polito.verifoo.rest.jaxb.Node;
import it.polito.verifoo.rest.jaxb.NodeConstraints.NodeMetrics;
import it.polito.verigraph.mcnet.components.NetContext;
import it.polito.verigraph.mcnet.components.Network;
import it.polito.verigraph.mcnet.components.NetworkObject;
import it.polito.verigraph.mcnet.components.Tuple;
import it.polito.verigraph.mcnet.netobjs.*;
import java.util.Optional;
/**
 * This class generates a Map of a new network object and associated node.
 * The network object are generated inside this class by extracting from the schema the type and by processing the configuration.
 * It also provide methods for Acl Attaching (for firewall object) and resource Attaching(for cache object)
 */
public class NodeNetworkObject extends HashMap<Node, NetworkObject>{
	private static final long serialVersionUID = 8001920385236985297L;
	private Logger logger = LogManager.getLogger("mylog");
	private Context ctx;
    private NetContext nctx;
	private AutoContext autoctx;
    private Network net;
	private int nRules;
	private List<NodeMetrics> nodeMetrics;
	/**
     * This class is an helper to generate network object
     * @param ctx Z3 Context
     * @param nctx NetworkContext
	 * @param autoctx 
     * @param net Network
     * @param nodes List of nodes from that we will generate NetworkObject
	 * @param nodeMetrics 
     */
    public NodeNetworkObject(Context ctx, NetContext nctx, AutoContext autoctx, Network net, 
    		List<it.polito.verifoo.rest.jaxb.Node> nodes, int nRules, List<NodeMetrics> nodeMetrics) {
		super();
		this.ctx = ctx;
		this.nctx = nctx;
		this.autoctx = autoctx;
		this.net = net;
		this.nRules = nRules;
		this.nodeMetrics = nodeMetrics;
		nodes.forEach(this::generateNetObj);
	}

	/**
	 * Attaches all network objects to the Network
	 */
	public void attachToNet(){
		this.forEach((n,netobjs)->net.attach(netobjs));
	}
	/**
	 * Generates Acl by processing the firewall configuration
	 * Please note that invalid configuration will result in a discarded firewall acl (we don't throw an exeption)
	 * @param fw 
	 */
	public void generateAcl(Node n, AclFirewall fw){
		if(n.getFunctionalType().equals(FunctionalTypes.FIREWALL)){
			n.getConfiguration().getFirewall().getElements().forEach((e)->{
				ArrayList<Tuple<DatatypeExpr,DatatypeExpr>> acl = new ArrayList<Tuple<DatatypeExpr,DatatypeExpr>>();
					if(nctx.am.get(e.getSource())!=null&&nctx.am.get(e.getDestination())!=null){
					    Tuple<DatatypeExpr,DatatypeExpr> rule=new Tuple<DatatypeExpr,DatatypeExpr>(nctx.am.get(e.getSource()),nctx.am.get(e.getDestination()));
					    acl.add(rule);
					    logger.debug("Adding blocking rule " + acl);
					}
				    fw.addAcls(acl);
				    //logger.debug("Added acl:"+ rule.toString() +" to "+n.getName());
				});
		}
		
	}
	/**
	 * Generates Cache internal node from cache resources configuration
	 */
	public void generateCache(Node n, PolitoCache c){
		if(n.getFunctionalType().equals(FunctionalTypes.CACHE)){
			List<String> resource = n.getConfiguration().getCache().getResource();
			NetworkObject[] internalNodes = {};
			internalNodes=this.entrySet().stream().filter(obj->resource.contains(obj.getKey().getName())).map(obj->obj.getValue()).collect(Collectors.toList()).toArray(internalNodes);
			/*for(NetworkObject no:internalNodes)
				logger.debug("Install cache with resources "+no.toString());*/
			if(internalNodes.length > 0)
				c.installCache(internalNodes);			
				
			}
	}
	
	/**
	 * Generates the vpn model
	 * @throws BadGraphError 
	 */
	public void generateVPN() throws BadGraphError {
		 long nVpnAccess = this.keySet().stream()
            	 .filter((n) -> n.getFunctionalType().equals(FunctionalTypes.VPNACCESS))
            	 .count();
        long nVpnExit = this.keySet().stream()
            	 .filter((n) -> n.getFunctionalType().equals(FunctionalTypes.VPNEXIT))
            	 .count();
        if(nVpnAccess > 0 || nVpnExit > 0) {
        	if(nVpnAccess != nVpnExit) throw new BadGraphError("VPN Access and Exit must be in equal number",EType.INVALID_VPN_CONFIGURATION);
        }
        List<Node> vpnAccessNodes = this.entrySet().stream()
           	 .filter((e) -> e.getKey().getFunctionalType().equals(FunctionalTypes.VPNACCESS))
           	 .map(e -> e.getKey())
           	 .collect(Collectors.toList());
        List<Node> vpnExitNodes = this.entrySet().stream()
              	 .filter((e) -> e.getKey().getFunctionalType().equals(FunctionalTypes.VPNEXIT))
               	 .map(e -> e.getKey())
              	 .collect(Collectors.toList());
        for(Node nA:vpnAccessNodes){
        	Node vpnExit = vpnExitNodes.stream()
        				.filter(nE -> nE.getName().equals(nA.getConfiguration().getVpnaccess().getVpnexit())).findFirst().orElse(null);
        	if(vpnExit == null) throw new BadGraphError("VPN not correctly configured",EType.INVALID_VPN_CONFIGURATION);
        	
        	((PolitoVpnExit) this.get(vpnExit)).vpnExitModel(nctx.am.get(nA.getName()), nctx.am.get(vpnExit.getName()));
        	((PolitoVpnAccess) this.get(nA)).vpnAccessModel(nctx.am.get(nA.getName()), nctx.am.get(vpnExit.getName()));
        }
		
	}
	
	/**
	 * @param Node n
	 * @throws BadGraphError 
	 * @description This function process the node and generate a network object according to functional type, 
	 * it also generate the configuration according to the type.
	 * @throws ProcessingException if it can't process the network object.
	 */
	public void generateNetObj(Node n){
			FunctionalTypes ftype;
			ftype=n.getFunctionalType();
			boolean optional = nodeMetrics.stream().filter( c -> c.getNode().equals(n.getName())).map(c -> c.isOptional()).findFirst().orElse(false);
			switch (ftype) {
				case FIREWALL:{
					if(n.getConfiguration().getFirewall()==null){
						throw new BadGraphError("You have specified a FIREWALL Type but you provide a configuration of another type",EType.INVALID_NODE_CONFIGURATION);
					}
					AclFirewall fw;
					if(n.getConfiguration().getFirewall().getElements().isEmpty()){
						if(optional){
							System.out.println("Autoplacement for " + n.getName());
							fw = new AclFirewall(ctx,new Object[]{nctx.nm.get(n.getName()),net,nctx,nRules, autoctx});
							autoctx.addOptionalNode(n, fw);
						}
						else{
							System.out.println("Autoconfiguration for " + n.getName());
							fw = new AclFirewall(ctx,new Object[]{nctx.nm.get(n.getName()),net,nctx,nRules});
						}
					}
					else{
						fw = new AclFirewall(ctx,new Object[]{nctx.nm.get(n.getName()),net,nctx});
						generateAcl(n, fw);
					}
					this.put(n, fw);
					break;
				}
				case FIELDMODIFIER:{	
					if(n.getConfiguration().getFieldmodifier()==null){
						throw new BadGraphError("You have specified a FIELDMODIFIER Type but you provide a configuration of another type",EType.INVALID_NODE_CONFIGURATION);
					}
					PolitoFieldModifier fm = new PolitoFieldModifier(ctx,new Object[]{nctx.nm.get(n.getName()),net,nctx});
					fm.installFieldModifier(Optional.ofNullable(null));
					// TODO: Field Modifier requires a packet model
					this.put(n,fm);
					break;
				}
				case ENDHOST:{
					if(n.getConfiguration().getEndhost()==null){
						throw new BadGraphError("You have specified a ENDHOST Type but you provide a configuration of another type",EType.INVALID_NODE_CONFIGURATION);
					}
					PolitoEndHost eh=new PolitoEndHost(ctx,new Object[]{nctx.nm.get(n.getName()),net,nctx});
					this.put(n,eh);
					eh.installEndHost(new PacketWrapper(n.getConfiguration().getEndhost(), nctx));
					break;
				}
				case ANTISPAM:{
					if(n.getConfiguration().getAntispam()==null){
						throw new BadGraphError("You have specified a ANTISPAM Type but you provide a configuration of another type",EType.INVALID_NODE_CONFIGURATION);
					}
					PolitoAntispam spam;
					if(n.getConfiguration().getAntispam().getSource().isEmpty()){
						if(optional){
							System.out.println("Autoplacement for " + n.getName());
							spam=new PolitoAntispam(ctx,new Object[]{nctx.nm.get(n.getName()),net,nctx,nRules,autoctx});
							autoctx.addOptionalNode(n, spam);
						}
						else{
							System.out.println("Autoconfiguration for " + n.getName());
							spam=new PolitoAntispam(ctx,new Object[]{nctx.nm.get(n.getName()),net,nctx,nRules});
						}
					}
					else{
						spam=new PolitoAntispam(ctx,new Object[]{nctx.nm.get(n.getName()),net,nctx});
						int[] blacklist=listToIntArguments(n.getConfiguration().getAntispam().getSource());
						spam.installAntispam(blacklist);
					}
					this.put(n, spam);					
					break;
				}
				case CACHE:{
					if(n.getConfiguration().getCache()==null){
						throw new BadGraphError("You have specified a CACHE Type but you provide a configuration of another type",EType.INVALID_NODE_CONFIGURATION);
					}
					PolitoCache c = new PolitoCache(ctx,new Object[]{nctx.nm.get(n.getName()),net,nctx});
					this.put(n, c);
					generateCache(n, c);
					break;
				}
				case DPI:{
					if(n.getConfiguration().getDpi()==null){
						throw new BadGraphError("You have specified a DPI Type but you provide a configuration of another type",EType.INVALID_NODE_CONFIGURATION);
					}
					PolitoIDS ids;
					if(n.getConfiguration().getDpi().getNotAllowed().isEmpty()){
						if(optional){
							System.out.println("Autoplacement for " + n.getName());
							ids=new PolitoIDS(ctx,new Object[]{nctx.nm.get(n.getName()),net,nctx,nRules,autoctx});
							autoctx.addOptionalNode(n, ids);
						}
						else{
							System.out.println("Autoconfiguration for " + n.getName());
							ids=new PolitoIDS(ctx,new Object[]{nctx.nm.get(n.getName()),net,nctx,nRules});
						}
						ids.installIDS(nRules);
					}
					else{
						ids=new PolitoIDS(ctx,new Object[]{nctx.nm.get(n.getName()),net,nctx});
						int[] blacklist=listToIntArguments(n.getConfiguration().getDpi().getNotAllowed());
						ids.installIDS(blacklist);
					}
					this.put(n,ids);
					break;
				}
				case MAILCLIENT:{
					if(n.getConfiguration().getMailclient()==null){
						throw new BadGraphError("You have specified a MAILCLIENT Type but you provide a configuration of another type",EType.INVALID_NODE_CONFIGURATION);
					}
					if(!(nctx.am.containsKey((n.getConfiguration().getMailclient().getMailserver())))) throw new BadGraphError("Mail server not present",EType.INVALID_NODE_CONFIGURATION);
					/*PolitoMailClient eh=new PolitoMailClient(ctx,new Object[]{nctx.nm.get(n.getName()),net,nctx,nctx.am.get(n.getConfiguration().getMailclient().getMailserver())});
					this.put(n,eh);*/
					PolitoEndHost eh=new PolitoEndHost(ctx,new Object[]{nctx.nm.get(n.getName()),net,nctx});
					PacketModel p = new PacketModel();
					this.put(n,eh);
					eh.installAsPOP3MailClient(nctx.am.get(n.getConfiguration().getMailclient().getMailserver()), p);
					break;
				}
				case MAILSERVER:{
					if(n.getConfiguration().getMailserver()==null){
						throw new BadGraphError("You have specified a MAILSERVER Type but you provide a configuration of another type",EType.INVALID_NODE_CONFIGURATION);
					}
					/*PolitoMailServer eh=new PolitoMailServer(ctx,new Object[]{nctx.nm.get(n.getName()),net,nctx});
					this.put(n,eh);*/
					PolitoEndHost eh=new PolitoEndHost(ctx,new Object[]{nctx.nm.get(n.getName()),net,nctx});
					PacketModel p = new PacketModel();
					this.put(n,eh);
					eh.installAsPOP3MailServer(p);
					break;
				}
				case NAT:{
					if(n.getConfiguration().getNat()==null){
						throw new BadGraphError("You have specified a NAT Type but you provide a configuration of another type",EType.INVALID_NODE_CONFIGURATION);
					}
					PolitoNat nat=new PolitoNat(ctx,new Object[]{nctx.nm.get(n.getName()),net,nctx});
					this.put(n,nat);
					ArrayList<DatatypeExpr> address = n.getConfiguration().getNat().getSource().stream()
							.map((s)->nctx.am.get(s))
							.filter(e -> e != null)
							.collect(Collectors.toCollection(ArrayList::new));
      				if(address.size() > 0){
						logger.debug("Added to nat " + n.getName() + " internal addresses "+address);
						nat.natModel(nctx.am.get(n.getName()));
						nat.setInternalAddress(address);		
					}
					break;
				}
				case VPNACCESS:{
					if(n.getConfiguration().getVpnaccess()==null){
						throw new BadGraphError("You have specified a VPNACCESS Type but you provide a configuration of another type",EType.INVALID_NODE_CONFIGURATION);
					}
					if(!(nctx.am.containsKey((n.getConfiguration().getVpnaccess().getVpnexit())))) throw new BadGraphError("VPN Exit not present",EType.INVALID_NODE_CONFIGURATION);
					PolitoVpnAccess vpn=new PolitoVpnAccess(ctx,new Object[]{nctx.nm.get(n.getName()),net,nctx});
					logger.debug("VPN Access: " +n.getName() + " with exit " + n.getConfiguration().getVpnaccess().getVpnexit());
					this.put(n,vpn);
					break;
				}
				case VPNEXIT:{
					if(n.getConfiguration().getVpnexit()==null){
						throw new BadGraphError("You have specified a VPNEXIT Type but you provide a configuration of another type",EType.INVALID_NODE_CONFIGURATION);
					}
					if(!(nctx.am.containsKey((n.getConfiguration().getVpnexit().getVpnaccess())))) throw new BadGraphError("VPN Access not present",EType.INVALID_NODE_CONFIGURATION);
					PolitoVpnExit vpn=new PolitoVpnExit(ctx,new Object[]{nctx.nm.get(n.getName()),net,nctx});
					logger.debug("VPN Exit: " +n.getName() + " with access " + n.getConfiguration().getVpnexit().getVpnaccess());
					this.put(n,vpn);
					break;
				}
				case WEBCLIENT:{
					if(n.getConfiguration().getWebclient()==null){
						throw new BadGraphError("You have specified a WEBCLIENT Type but you provide a configuration of another type",EType.INVALID_NODE_CONFIGURATION);
					}
					if(!(nctx.am.containsKey((n.getConfiguration().getWebclient().getNameWebServer())))) throw new BadGraphError("Web server not present",EType.INVALID_NODE_CONFIGURATION);
					/*PolitoWebClient eh=new PolitoWebClient(ctx,new Object[]{nctx.nm.get(n.getName()),net,nctx,nctx.am.get(n.getConfiguration().getWebclient().getNameWebServer())});
					this.put(n,eh);*/
					PolitoEndHost eh=new PolitoEndHost(ctx,new Object[]{nctx.nm.get(n.getName()),net,nctx});
					PacketModel p = new PacketModel();
					this.put(n,eh);
					eh.installAsWebClient(nctx.am.get(n.getConfiguration().getWebclient().getNameWebServer()), p);
					break;
				}
				case WEBSERVER:{
					if(n.getConfiguration().getWebserver()==null){
						throw new BadGraphError("You have specified a WEBSERVER Type but you provide a configuration of another type",EType.INVALID_NODE_CONFIGURATION);
					}
					/*PolitoWebServer eh=new PolitoWebServer(ctx,new Object[]{nctx.nm.get(n.getName()),net,nctx});
					this.put(n,eh);*/
					PolitoEndHost eh=new PolitoEndHost(ctx,new Object[]{nctx.nm.get(n.getName()),net,nctx});
					PacketModel p = new PacketModel();
					this.put(n,eh);
					eh.installAsWebServer(p);
					break;
				}
				default:{
					System.err.println("Braiiinssssssssssss!");
					throw new BadGraphError("Invalid Node Functional Type"+ftype,EType.INVALID_NODE_CONFIGURATION);
				}
			}
	}
	/**
	 * Transform a list of string in array of numbers
	 * @param arg List of String
	 * @return Array of int
	 */
	private int[] listToIntArguments(List<String> arg) {
        int[] o= new int[arg.size()];
        for(int i=0; i<arg.size(); i++){
            if(arg.get(i)!=null)
                o[i]= String.valueOf(arg.get(i)).hashCode();
        }
        return o;
    }
	
}
