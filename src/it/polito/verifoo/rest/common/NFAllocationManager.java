package it.polito.verifoo.rest.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.microsoft.z3.Context;

import it.polito.verifoo.rest.autoconfiguration.FWAutoconfigurationManager;
import it.polito.verifoo.rest.jaxb.ActionTypes;
import it.polito.verifoo.rest.jaxb.FunctionalTypes;
import it.polito.verifoo.rest.jaxb.Node;
import it.polito.verifoo.rest.jaxb.PName;
import it.polito.verifoo.rest.jaxb.Property;
import it.polito.verifoo.rest.jaxb.NodeConstraints.NodeMetrics;
import it.polito.verigraph.mcnet.components.NetContext;
import it.polito.verigraph.mcnet.components.NetworkObject;
import it.polito.verigraph.mcnet.netobjs.AclFirewall;
import it.polito.verigraph.mcnet.netobjs.PolitoEndHost;
import it.polito.verigraph.mcnet.netobjs.PolitoNat;

public class NFAllocationManager {
	private Context ctx;
    private NetContext nctx;
    private FWAutoconfigurationManager FWmanager;
	private int nIsolationProp, nReachabilityProp;
	private List<NodeMetrics> nodeMetrics;
	private List<Property> properties;
	private HashMap<String, AllocationNode> nodes;
	
	 public NFAllocationManager(Context ctx, NetContext nctx, 
	    		HashMap<String, AllocationNode> allocationNodes, List<NodeMetrics> nodeMetrics, List<Property> properties, FWAutoconfigurationManager FWmanager) {
			super();
			this.ctx = ctx;
			this.nctx = nctx;
			this.nodeMetrics = nodeMetrics;
			this.properties = properties;
			this.FWmanager = FWmanager;
			nIsolationProp = (int) properties.stream().filter(p -> p.getName().equals(PName.ISOLATION_PROPERTY)).count();
			nReachabilityProp = (int) properties.stream().filter(p -> p.getName().equals(PName.REACHABILITY_PROPERTY)).count();
			this.nodes = allocationNodes;
		}

	 
	 /* 
	  * This method is invoked by VerifooProxy to instanciate the Network Functions which have been specified in the input, processing the input nodes with a proper Configuration (i.e. node which isn't empty). For the moment it's able to instanciate web client/server and firewall. 
	  * Each other NF can be added here following the same pattern (legacy class: NodeNetworkObject)
	  */
	public void instanciateDefineNF() {
		
		nodes.values().forEach(allocationNode -> {
			Node node = allocationNode.getNode();
			if(node.getFunctionalType() != null) {
				
				if(node.getFunctionalType() == FunctionalTypes.WEBCLIENT) {
					PolitoEndHost client = new PolitoEndHost(allocationNode, ctx, nctx);
					PacketWrapper p = new PacketWrapper(node.getConfiguration().getEndhost(), nctx);
					Property prop =  properties.stream().filter(pr -> pr.getSrc().equals(node.getName())).findFirst().orElse(null);
					if(prop != null){ p.setProperties(prop, nctx);}
					//AllocationNode server = nodes.values().stream().filter(n -> n.getIpAddress().equals(node.getConfiguration().getWebclient().getNameWebServer())).findFirst().orElse(null);
					
					client.installEndHost(p);
					allocationNode.setPlacedNF(client);
					allocationNode.setTypeNF(FunctionalTypes.WEBCLIENT);
				}
			
			
				else if(node.getFunctionalType() == FunctionalTypes.WEBSERVER) {
					PolitoEndHost server = new PolitoEndHost(allocationNode, ctx, nctx);
					PacketWrapper p = new PacketWrapper(node.getConfiguration().getEndhost(), nctx);
					Property prop =  properties.stream().filter(pr -> pr.getDst().equals(node.getName())).findFirst().orElse(null);
					if(prop != null){ p.setProperties(prop, nctx);}
					
					server.installEndHost(p);
					allocationNode.setPlacedNF(server);
					allocationNode.setTypeNF(FunctionalTypes.WEBSERVER);
					;
				}
				
				else if(node.getFunctionalType() == FunctionalTypes.FIREWALL) {
					AclFirewall firewall = new AclFirewall(allocationNode, ctx, nctx);
					firewall.setAutoplace(false);
					
					if(node.getConfiguration().getFirewall().getDefaultAction() != null) {
						if(node.getConfiguration().getFirewall().getDefaultAction().equals(ActionTypes.ALLOW)) {
							firewall.setDefaultAction(true);
						}else {
							firewall.setDefaultAction(false);
						}
					}
					
					if(!node.getConfiguration().getFirewall().getElements().isEmpty()) {
						firewall.setAutoconfigured(false);
						firewall.generateAcl();
					} 
					
					FWmanager.addFirewall(firewall,  allocationNode);
					allocationNode.setPlacedNF(firewall);
					allocationNode.setTypeNF(FunctionalTypes.FIREWALL);
				}
				
				else if(node.getFunctionalType() == FunctionalTypes.NAT) {
					PolitoNat nat = new PolitoNat(allocationNode, ctx, nctx);
					allocationNode.setPlacedNF(nat);
					allocationNode.setTypeNF(FunctionalTypes.NAT);
				}
			}
			
		});
	}

	
	/*
	 * This method is invoked in the recursive visit of the graph inside Verifoo Proxy. It feature an heuristic algorithm to select which NF place on the node. 
	 * The heuristic will be completed in the future, for the moment it considers just one type of NF per time.
	 */
	public void NFchoice(AllocationNode source, AllocationNode origin, AllocationNode finalDest) {
		if(source.getTypeNF() == null || source.getTypeNF().equals(FunctionalTypes.FIREWALL)) {
			boolean interested = properties.stream().anyMatch(p -> p.getSrc().equals(origin.getIpAddress()) && p.getDst().equals(finalDest.getIpAddress()));
			if(interested) {
				if(!FWmanager.firewallIsPresent(source)) {
					AclFirewall firewall = new AclFirewall(source, ctx, nctx);
					FWmanager.addFirewall(firewall, source);
					source.setPlacedNF(firewall);
					source.setTypeNF(FunctionalTypes.FIREWALL);
				}
				
				FWmanager.setPolicy(source, origin.getNode(), finalDest.getNode());
				
			}
		}
	
	}
	
	/*
	 * this method is invoked in Verifoo Proxy before the creation of the Checker but after the recursive visit of the graph. 
	 * It allows to create hard and soft contraints for each Network Function placed inside a node.
	 */
	
	public void NFinstall() {
		
		nodes.values().forEach(allocationNode -> {
			Node node = allocationNode.getNode();
			FunctionalTypes type = allocationNode.getTypeNF();
			NetworkObject no = allocationNode.getPlacedNF();
				
			if(type.equals(FunctionalTypes.WEBCLIENT)) {
				PolitoEndHost endHost = (PolitoEndHost) no;
				AllocationNode server = nodes.values().stream().filter(n -> n.getIpAddress().equals(node.getConfiguration().getWebclient().getNameWebServer())).findFirst().orElse(null);
				endHost.installAsWebClient(server.getZ3Node());
			}
			else if(type.equals(FunctionalTypes.WEBSERVER)) {
				PolitoEndHost endHost = (PolitoEndHost) no;
				endHost.installAsWebServer();
			}else if(type.equals(FunctionalTypes.WEBSERVER)) {
				PolitoEndHost endHost = (PolitoEndHost) no;
				endHost.installAsWebServer();
			}else if(node.getFunctionalType() == FunctionalTypes.NAT) {	
				PolitoNat nat = (PolitoNat) no;
				nat.natModel(nctx.am.get(node.getName()));
			}
			
			
		});
		
	}

}
