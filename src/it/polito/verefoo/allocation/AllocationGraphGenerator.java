package it.polito.verefoo.allocation;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import it.polito.verefoo.jaxb.ActionTypes;
import it.polito.verefoo.jaxb.AllocationConstraintType;
import it.polito.verefoo.jaxb.AllocationConstraints;
import it.polito.verefoo.jaxb.Configuration;
import it.polito.verefoo.jaxb.Firewall;
import it.polito.verefoo.jaxb.FunctionalTypes;
import it.polito.verefoo.jaxb.Graph;
import it.polito.verefoo.jaxb.NFV;
import it.polito.verefoo.jaxb.Neighbour;
import it.polito.verefoo.jaxb.Node;
import it.polito.verefoo.jaxb.AllocationConstraints.AllocationConstraint;

/**
 * The AllocationGraphGenerator provides an automatic generation of an Allocation Graph,
 * if the user specifies a Service Graph as input.
 */
public class AllocationGraphGenerator {

	private NFV nfv;
	
	/*
	 * The initialAPIp variable allows to set the first three elements of the IP addresses of the Allocation Places.
	 * If it is set a name, then wildcards won't be exploited by the framework.
	 */
	String initialAPIp = "20.0.0.";
	
	
	/**
	 * Public constructor of the AllocationGraphGenerator class
	 * @param nfv it is the NFV element describing the input NFV element, potentially including some Service Graphs
	 */
	public AllocationGraphGenerator(NFV nfv) {
		this.nfv = nfv;
		for(Graph graph : nfv.getGraphs().getGraph()) {
			if(graph.isServiceGraph()) {
				List<AllocationConstraint> list = new ArrayList<AllocationConstraint>();
				AllocationConstraints ac = nfv.getConstraints().getAllocationConstraints();
				if(ac != null) {
					list.addAll(ac.getAllocationConstraint());
				}

				generateAllocationGraph(graph, list);	
			}		
		}

	}
	
	/**
	 * Special constructor for REACT-VEREFOO implementation
	 * 
	 */
	public AllocationGraphGenerator(NFV nfv, Boolean react) {
		this.nfv = nfv;
		for(Graph graph : nfv.getGraphs().getGraph()) {
			if(graph.isServiceGraph()) {
				List<AllocationConstraint> list = new ArrayList<AllocationConstraint>();
				AllocationConstraints ac = nfv.getConstraints().getAllocationConstraints();
				if(ac != null) {
					list.addAll(ac.getAllocationConstraint());
				}

				generateAllocationGraph_REACT(graph, list);	
			}		
		}

	}


	private void generateAllocationGraph_REACT(Graph graph, List<AllocationConstraint> list) {
		int counter = 1;
		List<Node> newNodes = new ArrayList<Node>();
		
		//List of IPAddresses of configured FWs
		List <String> firewalls = graph.getNode().stream().filter(n -> n.getFunctionalType()!=null)
														  .filter(n -> n.getFunctionalType().value().equals("FIREWALL"))
														  .map(n -> n.getName()).collect(Collectors.toList());
		//List of IPAddresses of configured FORWARDERs
		List <String> forwarders = graph.getNode().stream().filter(n -> n.getFunctionalType()!=null)
														  .filter(n -> n.getFunctionalType().value().equals("FORWARDER"))
														  .map(n -> n.getName()).collect(Collectors.toList());
		//List of IPAddresses of APs
		List <String> allocationPoints = graph.getNode().stream().filter(n -> n.getFunctionalType()==null)
														  .map(n -> n.getName()).collect(Collectors.toList());
		for(Node node : graph.getNode()) {
			//We avoid generating new AP near a FW, another AP, or a FORWARDER
			if(!firewalls.contains(node.getName()) 
					&& !forwarders.contains(node.getName())
					&& !allocationPoints.contains(node.getName())) {
				
				List<String> toRemove = new ArrayList<String>();
				List<Neighbour> toAdd = new ArrayList<Neighbour>();
				
				for(Neighbour neighbour : node.getNeighbour()) {
					// Ignore the neighbour if it is a newly created AP,a FW, a FORWARDER, or an AP already present in the initial graph
					if(!neighbour.getName().startsWith(initialAPIp) 
							&& !firewalls.contains(neighbour.getName())
							&& !allocationPoints.contains(neighbour.getName())
							&& !forwarders.contains(neighbour.getName())) {
						boolean notForbidden = true;
						boolean fwForced = false;
						for(AllocationConstraint ac : list) {
							if(ac.getType().equals(AllocationConstraintType.FORBIDDEN) && 
									(
										(ac.getNodeA().equals(node.getName()) && ac.getNodeB().equals(neighbour.getName()) ) || 
										(ac.getNodeB().equals(node.getName()) && ac.getNodeA().equals(neighbour.getName()) )
									)
								) {
								notForbidden = false;
							}
							
							if(ac.getType().equals(AllocationConstraintType.FORCED) && 
									(
										(ac.getNodeA().equals(node.getName()) && ac.getNodeB().equals(neighbour.getName()) ) || 
										(ac.getNodeB().equals(node.getName()) && ac.getNodeA().equals(neighbour.getName()) )
									)
								) {
								fwForced = true;
							}
						}
						
						if(notForbidden) {
							Node other = graph.getNode().stream().filter(n -> n.getName().equals(neighbour.getName())).findFirst().orElse(null);
							boolean bidirectional = other.getNeighbour().stream().anyMatch(n -> n.getName().equals(node.getName()));
							
							Node allocationPlace = new Node();
							//This is needed to avoid duplicate IP addresses (with respect to previous runs)
							boolean found = false;
							while(!found) {
								String IP= initialAPIp + counter;
								if(!allocationPoints.contains(IP)) {
									allocationPlace.setName(IP);
									found = true;
								} else {
									counter++;
								}
							}
							Neighbour apNeigh = new Neighbour();
							apNeigh.setName(allocationPlace.getName());
							counter++;
							
							//node.getNeighbour().removeIf(n -> n.getName().equals(neighbour.getName()));
							//node.getNeighbour().add(apNeigh);
							toRemove.add(neighbour.getName());
							toAdd.add(apNeigh);
							
							Neighbour nextNeigh = new Neighbour();
							nextNeigh.setName(neighbour.getName());
							allocationPlace.getNeighbour().add(nextNeigh);
							
							if(bidirectional) {
								other.getNeighbour().removeIf(n -> n.getName().equals(node.getName()));
								other.getNeighbour().add(apNeigh);
								Neighbour prevNeigh = new Neighbour();
								prevNeigh.setName(node.getName());
								allocationPlace.getNeighbour().add(prevNeigh);
							}
							
							if(fwForced) {
								allocationPlace.setFunctionalType(FunctionalTypes.FIREWALL);
								Configuration confFW = new Configuration();
								confFW.setDescription("FWDescription");
								confFW.setName("FWConfiguration");
								Firewall fw = new Firewall();
								fw.setDefaultAction(ActionTypes.DENY);
								confFW.setFirewall(fw);
								allocationPlace.setConfiguration(confFW);
							}
							
							newNodes.add(allocationPlace);
						}
						
					}
				}	
				
				node.getNeighbour().removeIf( n -> toRemove.stream().anyMatch(s -> s.equals(n.getName())));
				node.getNeighbour().addAll(toAdd);
			}
		}
		
		graph.getNode().addAll(newNodes);		
		
	}

	/**
	 * This method automatically generates an Allocation Graph with placeholders where Network Security Functions can be allocated.
	 * @param graph it is the input Service Graph, modified by the method into an Allocation Graph
	 * @param list it is the list of Allocation Constraints (i.e. forced and foribdden) set by the user
	 */
	private void generateAllocationGraph(Graph graph, List<AllocationConstraint> list) {
		int counter = 1;
		List<Node> newNodes = new ArrayList<Node>();
		for(Node node : graph.getNode()) {
			
			List<String> toRemove = new ArrayList<String>();
			List<Neighbour> toAdd = new ArrayList<Neighbour>();
			
			for(Neighbour neighbour : node.getNeighbour()) {
				if(!neighbour.getName().startsWith(initialAPIp)) {
					
					boolean notForbidden = true;
					boolean fwForced = false;
					for(AllocationConstraint ac : list) {
						if(ac.getType().equals(AllocationConstraintType.FORBIDDEN) && 
								(
									(ac.getNodeA().equals(node.getName()) && ac.getNodeB().equals(neighbour.getName()) ) || 
									(ac.getNodeB().equals(node.getName()) && ac.getNodeA().equals(neighbour.getName()) )
								)
							) {
							notForbidden = false;
						}
						
						if(ac.getType().equals(AllocationConstraintType.FORCED) && 
								(
									(ac.getNodeA().equals(node.getName()) && ac.getNodeB().equals(neighbour.getName()) ) || 
									(ac.getNodeB().equals(node.getName()) && ac.getNodeA().equals(neighbour.getName()) )
								)
							) {
							fwForced = true;
						}
					}
					
					if(notForbidden) {
						Node other = graph.getNode().stream().filter(n -> n.getName().equals(neighbour.getName())).findFirst().orElse(null);
						boolean bidirectional = other.getNeighbour().stream().anyMatch(n -> n.getName().equals(node.getName()));
						
						Node allocationPlace = new Node();
						allocationPlace.setName(initialAPIp + counter);			
						Neighbour apNeigh = new Neighbour();
						apNeigh.setName(allocationPlace.getName());
						counter++;
						
						//node.getNeighbour().removeIf(n -> n.getName().equals(neighbour.getName()));
						//node.getNeighbour().add(apNeigh);
						toRemove.add(neighbour.getName());
						toAdd.add(apNeigh);
						
						Neighbour nextNeigh = new Neighbour();
						nextNeigh.setName(neighbour.getName());
						allocationPlace.getNeighbour().add(nextNeigh);
						
						if(bidirectional) {
							other.getNeighbour().removeIf(n -> n.getName().equals(node.getName()));
							other.getNeighbour().add(apNeigh);
							Neighbour prevNeigh = new Neighbour();
							prevNeigh.setName(node.getName());
							allocationPlace.getNeighbour().add(prevNeigh);
						}
						
						if(fwForced) {
							allocationPlace.setFunctionalType(FunctionalTypes.FIREWALL);
							Configuration confFW = new Configuration();
							confFW.setDescription("FWDescription");
							confFW.setName("FWConfiguration");
							Firewall fw = new Firewall();
							fw.setDefaultAction(ActionTypes.DENY);
							confFW.setFirewall(fw);
							allocationPlace.setConfiguration(confFW);
						}
						
						newNodes.add(allocationPlace);
					}
					
				}
			}	
			
			node.getNeighbour().removeIf( n -> toRemove.stream().anyMatch(s -> s.equals(n.getName())));
			node.getNeighbour().addAll(toAdd);
		}
		
		graph.getNode().addAll(newNodes);
	}
	
	
	/**
	 * This method returns the computed NFV element.
	 * @return the NFV element with the Allocation Graph
	 */
	public NFV getAllocationGraph() {
		return nfv;
	}
	
	
	
}
