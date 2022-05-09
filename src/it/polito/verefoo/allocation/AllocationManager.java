package it.polito.verefoo.allocation;

import java.util.HashMap;
import java.util.List;

import com.microsoft.z3.Context;

import it.polito.verefoo.extra.WildcardManager;
import it.polito.verefoo.functions.EndHost;
import it.polito.verefoo.functions.Forwarder;
import it.polito.verefoo.functions.GenericFunction;
import it.polito.verefoo.functions.LoadBalancer;
import it.polito.verefoo.functions.NAT;
import it.polito.verefoo.functions.PacketFilter;
import it.polito.verefoo.functions.StatefulPacketFilter;
import it.polito.verefoo.functions.TrafficMonitor;
import it.polito.verefoo.jaxb.ActionTypes;
import it.polito.verefoo.jaxb.FunctionalTypes;
import it.polito.verefoo.jaxb.Node;
import it.polito.verefoo.jaxb.Property;
import it.polito.verefoo.jaxb.NodeConstraints.NodeMetrics;
import it.polito.verefoo.solver.NetContext;

/**
 * This class has the key task to manage the allocation and deployment of Network Functions on the nodes of
 * the Allocation Graph, accordingly to Middle Level Policies.
 * If Low Level Network Functions are specified as well in the input, they are managed in this class too.
 */

public class AllocationManager {
	private Context ctx;
    private NetContext nctx;
	private List<NodeMetrics> nodeMetrics;
	private List<Property> properties;
	private HashMap<String, AllocationNode> nodes;
	private WildcardManager wildcardManager;
	
	/**
	 * Public constructor for the NFAllocationManager class
	 * @param ctx It is the Contect object which manages all the hard and soft contraints in Z3Opt.
	 * @param nctx It is the NetContext object where Z3 objects and formulas are stored.
	 * @param allocationNodes It is a map used to retrieve the nodes of the Allocation Graph by their name.
	 * @param nodeMetrics It is a list of constraints about node (i.e. optionality of a node on the Allocation Graph)
	 * @param properties It is the list of Middle Level Reachability and Isolation policies.
	 * @param FWManager It is an instance of a class which manages the firewalls.
	 */
	 public AllocationManager(Context ctx, NetContext nctx, 
	    		HashMap<String, AllocationNode> allocationNodes, List<NodeMetrics> nodeMetrics, List<Property> properties, WildcardManager wildcardManager) {
			super();
			this.ctx = ctx;
			this.nctx = nctx;
			this.nodeMetrics = nodeMetrics;
			this.properties = properties;
			this.wildcardManager = wildcardManager;
			this.nodes = allocationNodes;
		}

	 

	 /**
	  * This method is invoked by VerefooProxy to instantiate the Network Functions which have been specified in the input, processing the input nodes with a proper Configuration (i.e. node which isn't empty). 
	  * For the moment it's able to instanciate web client/servers, firewalls and NATs. 
	  * Each other NF can be added here following the same pattern (legacy class in older versions: NodeNetworkObject)
	 */
	public void instantiateFunctions() {
		
		nodes.values().forEach(allocationNode -> {
			Node node = allocationNode.getNode();
			if(node.getFunctionalType() != null) {
				if(node.getFunctionalType() == FunctionalTypes.WEBCLIENT) {
					EndHost client = new EndHost(allocationNode, ctx, nctx);
					Property prop =  properties.stream().filter(pr -> pr.getSrc().equals(node.getName())).findFirst().orElse(null);
					
					client.installEndHost();
					allocationNode.setPlacedNF(client);
					allocationNode.setTypeNF(FunctionalTypes.WEBCLIENT);
				}
			
			
				else if(node.getFunctionalType() == FunctionalTypes.WEBSERVER) {
					EndHost server = new EndHost(allocationNode, ctx, nctx);
					Property prop =  properties.stream().filter(pr -> pr.getDst().equals(node.getName())).findFirst().orElse(null);
					
					server.installEndHost();
					allocationNode.setPlacedNF(server);
					allocationNode.setTypeNF(FunctionalTypes.WEBSERVER);
				}
				
				else if(node.getFunctionalType() == FunctionalTypes.FIREWALL) {
					PacketFilter firewall = new PacketFilter(allocationNode, ctx, nctx, wildcardManager);
					firewall.setAutoplace(false);
					
					if(node.getConfiguration().getFirewall().getDefaultAction() != null) {
						
						//This part is used to determine the default action of the packet filter: allow or deny
						if(node.getConfiguration().getFirewall().getDefaultAction().equals(ActionTypes.ALLOW)) {
							firewall.setDefaultAction(true);
						}else if(node.getConfiguration().getFirewall().getDefaultAction().equals(ActionTypes.DENY)){
							firewall.setDefaultAction(false);
						}
					}
					
					/*
					 * This part is used to generate the Access Control Lists for Low Level Configurations specified in the input XML file.
					 * In this case, no auto-configuration will be required.
					 */
					if(!node.getConfiguration().getFirewall().getElements().isEmpty()) {
						firewall.setAutoconfigured(false);
					} 
					
					/*
					 * This part is used to understand if the firewall configured should be optional or not.
					 * It makes sense only if low-level configuration is already specified.
					 * This way the framework can eliminate a wrong configuration, if necessary.
					 */
					
					boolean optional = nodeMetrics.stream().anyMatch(nm -> nm.getNode().equals(node.getName()));
					if(optional) firewall.setAutoplace(true);
					allocationNode.setPlacedNF(firewall);
					allocationNode.setTypeNF(FunctionalTypes.FIREWALL);
				}
				
				else if(node.getFunctionalType() == FunctionalTypes.NAT) {
					NAT nat = new NAT(allocationNode, ctx, nctx);
					allocationNode.setPlacedNF(nat);
					allocationNode.setTypeNF(FunctionalTypes.NAT);
				}
				
				else if(node.getFunctionalType() == FunctionalTypes.LOADBALANCER) {
					LoadBalancer lb = new LoadBalancer(allocationNode, ctx, nctx);
					allocationNode.setPlacedNF(lb);
					allocationNode.setTypeNF(FunctionalTypes.LOADBALANCER);
				}
				
				else if(node.getFunctionalType() == FunctionalTypes.FORWARDER) {
					Forwarder forwarder = new Forwarder(allocationNode, ctx, nctx);
					allocationNode.setPlacedNF(forwarder);
					allocationNode.setTypeNF(FunctionalTypes.FORWARDER);
				}
				
				else if(node.getFunctionalType() == FunctionalTypes.TRAFFIC_MONITOR) {
					TrafficMonitor tm = new TrafficMonitor(allocationNode, ctx, nctx);
					allocationNode.setPlacedNF(tm);
					allocationNode.setTypeNF(FunctionalTypes.FORWARDER);
				}
				
				else if(node.getFunctionalType() == FunctionalTypes.STATEFUL_FIREWALL) {
					StatefulPacketFilter spf = new StatefulPacketFilter(allocationNode, ctx, nctx);
					allocationNode.setPlacedNF(spf);
					allocationNode.setTypeNF(FunctionalTypes.STATEFUL_FIREWALL);
					
					if(node.getConfiguration().getStatefulFirewall().getDefaultAction() != null) { 
						if(node.getConfiguration().getStatefulFirewall().getDefaultAction().equals(ActionTypes.ALLOW)) {
							spf.setDefaultAction(true);
						}else if(node.getConfiguration().getStatefulFirewall().getDefaultAction().equals(ActionTypes.DENY)){
							spf.setDefaultAction(false);
						}
					}
				}
			}
			
		});
	}

	
	/**
	 * This method is invoked in the recursive visit of the graph inside Verefoo Proxy. 
	 * It features an heuristic algorithm to select which NF place on the node. 
	 * The heuristic can be introduced here in the future.
	 * For the moment it considers just one type of NF per time (e.g. packet filter, antispam, etc. accordingly to the tests in which the framework is used)
	 */
	public void chooseFunctions(AllocationNode source, AllocationNode origin, AllocationNode finalDest) {
		if(source.getTypeNF() == null ) {
					PacketFilter firewall = new PacketFilter(source, ctx, nctx, wildcardManager);
					source.setPlacedNF(firewall);
					source.setTypeNF(FunctionalTypes.FIREWALL);
		}
	
	}
	
	/**
	 * This method is invoked in VerefooProxy before the creation of the Checker but after the recursive visit of the graph. 
	 * It allows to create hard and soft contraints for each Network Function placed inside a node.
	 * This way, creation of the Network Function and its installation are decoupled: 
	 * 1) the first one (method instanciateDefineNF happens before the recursive visit;
	 * 2) this method (NFinstall) after the recursive visit, when all the maps of the AllocationNode objects are built.
	 */
	
	public void configureFunctions() {
		
		nodes.values().forEach(allocationNode -> { 
			Node node = allocationNode.getNode();
			FunctionalTypes type = allocationNode.getTypeNF();
			GenericFunction no = allocationNode.getPlacedNF();

			if(no == null) return; //it means no network function has been deployed on this node
				
			if(type.equals(FunctionalTypes.WEBCLIENT)) {
				EndHost endHost = (EndHost) no;
				endHost.configureEndHost();
			}else if(type.equals(FunctionalTypes.WEBSERVER)) {
				EndHost endHost = (EndHost) no;
				endHost.configureEndHost();
			}else if(node.getFunctionalType() == FunctionalTypes.NAT) {	
				NAT nat = (NAT) no;
				nat.natConfiguration();	
			}else if(node.getFunctionalType() == FunctionalTypes.LOADBALANCER) {	
				LoadBalancer lb = (LoadBalancer) no;
				lb.loadBalancerConfiguration(nctx.addressMap.get(node.getName()));		
			}else if(node.getFunctionalType() == FunctionalTypes.FORWARDER) {	
				Forwarder fw = (Forwarder) no;
				fw.forwarderSendRules();
			}else if(node.getFunctionalType() == FunctionalTypes.TRAFFIC_MONITOR) {	
				TrafficMonitor tm = (TrafficMonitor) no;
				tm.trafficMonitorSendRules();
			}else if(type.equals(FunctionalTypes.FIREWALL)) {
				PacketFilter fw = (PacketFilter) no;
				if(fw.isAutoconfigured()) fw.automaticConfiguration();
				else fw.manualConfiguration();
			}else if(type.equals(FunctionalTypes.STATEFUL_FIREWALL)) {
				StatefulPacketFilter fw = (StatefulPacketFilter) no;
				fw.manualConfiguration();
			}
		});
		
	}

}
