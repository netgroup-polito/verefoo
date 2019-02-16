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

/**
 * This class has the key task to manage the allocation and deployment of Network Functions on the nodes of
 * the Allocation Graph, accordingly to Middle Level Policies.
 * If Low Level Network Functions are specified as well in the input, they are managed in this class too.
 */

public class NFAllocationManager {
	private Context ctx;
    private NetContext nctx;
    private FWAutoconfigurationManager FWmanager;
	private int nIsolationProp, nReachabilityProp;
	private List<NodeMetrics> nodeMetrics;
	private List<Property> properties;
	private HashMap<String, AllocationNode> nodes;
	
	/**
	 * Public constructor for the NFAllocationManager class
	 * @param ctx It is the Contect object which manages all the hard and soft contraints in Z3Opt.
	 * @param nctx It is the NetContext object where Z3 objects and formulas are stored.
	 * @param allocationNodes It is a map used to retrieve the nodes of the Allocation Graph by their name.
	 * @param nodeMetrics It is a list of constraints about node (i.e. optionality of a node on the Allocation Graph)
	 * @param properties It is the list of Middle Level Reachability and Isolation policies.
	 * @param FWManager It is an instance of a class which manages the firewalls.
	 */
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

	 

	 /**
	  * This method is invoked by VerifooProxy to instanciate the Network Functions which have been specified in the input, processing the input nodes with a proper Configuration (i.e. node which isn't empty). 
	  * For the moment it's able to instanciate web client/servers, firewalls and NATs. 
	  * Each other NF can be added here following the same pattern (legacy class in older versions: NodeNetworkObject)
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
						
						//This part is used to determine the default action of the packet filter: allow or deny
						if(node.getConfiguration().getFirewall().getDefaultAction().equals(ActionTypes.ALLOW)) {
							firewall.setDefaultAction(true);
						}else {
							firewall.setDefaultAction(false);
						}
					}
					
					/*
					 * This part is used to generate the Access Control Lists for Low Level Configurations specified in the input XML file.
					 * In this case, no auto-configuration will be required.
					 */
					if(!node.getConfiguration().getFirewall().getElements().isEmpty()) {
						firewall.setAutoconfigured(false);
						firewall.generateAcl();
					} 
					
					/*
					 * This part is used to understand if the firewall configured should be optional or not.
					 * It makes sense only if low-level configuration is already specified.
					 * This way the framework can eliminate a wrong configuration, if necessary.
					 */
					
					boolean optional = nodeMetrics.stream().anyMatch(nm -> nm.getNode().equals(node.getName()));
					if(optional) firewall.setAutoplace(true);
					
					//The firewall is included in the manager.
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

	
	/**
	 * This method is invoked in the recursive visit of the graph inside Verifoo Proxy. 
	 * It features an heuristic algorithm to select which NF place on the node. 
	 * The heuristic can be collocated here in the future.
	 * For the moment it considers just one type of NF per time (e.g. packet filter, antispam, etc. accordingly to the tests in which the framework is used)
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
	
	/**
	 * This method is invoked in VerifooProxy before the creation of the Checker but after the recursive visit of the graph. 
	 * It allows to create hard and soft contraints for each Network Function placed inside a node.
	 * This way, creation of the Network Function and its installation are decoupled: 
	 * 1) the first one (method instanciateDefineNF happens before the recursive visit;
	 * 2) this method (NFinstall) after the recursive visit, when all the maps of the AllocationNode objects are built.
	 */
	
	public void NFinstall() {
		
		nodes.values().forEach(allocationNode -> {
			Node node = allocationNode.getNode();
			FunctionalTypes type = allocationNode.getTypeNF();
			NetworkObject no = allocationNode.getPlacedNF();
			
			if(no == null) return; //it means no network function has been deployed on this node
				
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
