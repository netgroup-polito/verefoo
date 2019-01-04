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

public class VNFAllocationManager {
	private Context ctx;
    private NetContext nctx;
    private FWAutoconfigurationManager FWmanager;
	private int nIsolationProp, nReachabilityProp;
	private List<NodeMetrics> nodeMetrics;
	private List<Property> properties;
	private HashMap<String, AllocationNode> nodes;
	
	 public VNFAllocationManager(Context ctx, NetContext nctx, 
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

	public void istanciateDefineVNF() {
		
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
					allocationNode.getPlaceableVNF().put( FunctionalTypes.WEBCLIENT, client);
					
				}
			
			
				else if(node.getFunctionalType() == FunctionalTypes.WEBSERVER) {
					PolitoEndHost server = new PolitoEndHost(allocationNode, ctx, nctx);
					PacketWrapper p = new PacketWrapper(node.getConfiguration().getEndhost(), nctx);
					Property prop =  properties.stream().filter(pr -> pr.getDst().equals(node.getName())).findFirst().orElse(null);
					if(prop != null){ p.setProperties(prop, nctx);}
					
					server.installEndHost(p);
					allocationNode.getPlaceableVNF().put(FunctionalTypes.WEBSERVER, server);
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
					allocationNode.getPlaceableVNF().put(FunctionalTypes.FIREWALL, firewall);
				}
			}
			
		});
	}

	public void VNFchoice(AllocationNode source, AllocationNode origin, AllocationNode finalDest) {
		boolean interested = properties.stream().anyMatch(p -> p.getSrc().equals(origin.getIpAddress()) && p.getDst().equals(finalDest.getIpAddress()));
		if(interested) {
			if(!FWmanager.firewallIsPresent(source)) {
				AclFirewall firewall = new AclFirewall(source, ctx, nctx);
				FWmanager.addFirewall(firewall, source);
				source.getPlaceableVNF().put(FunctionalTypes.FIREWALL, firewall);
			}
			
			FWmanager.setPolicy(source, origin.getNode(), finalDest.getNode());
			
		}
	}
	
	
	public void VNFinstall() {
		
		nodes.values().forEach(allocationNode -> {
			Node node = allocationNode.getNode();
			Map<FunctionalTypes, NetworkObject> vnf = allocationNode.getPlaceableVNF();
			for(Map.Entry<FunctionalTypes, NetworkObject> pair : vnf.entrySet()) {
				FunctionalTypes type = pair.getKey();
				NetworkObject no = pair.getValue();
				
				if(type.equals(FunctionalTypes.WEBCLIENT)) {
					PolitoEndHost endHost = (PolitoEndHost) no;
					AllocationNode server = nodes.values().stream().filter(n -> n.getIpAddress().equals(node.getConfiguration().getWebclient().getNameWebServer())).findFirst().orElse(null);
					endHost.installAsWebClient(server.getZ3Node());
				}
				else if(type.equals(FunctionalTypes.WEBSERVER)) {
					PolitoEndHost endHost = (PolitoEndHost) no;
					endHost.installAsWebServer();
				}
				
			}
			
		});
		
	}

}
