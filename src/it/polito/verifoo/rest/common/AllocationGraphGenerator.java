package it.polito.verifoo.rest.common;

import java.util.ArrayList;
import java.util.List;

import it.polito.verifoo.rest.jaxb.Graph;
import it.polito.verifoo.rest.jaxb.NFV;
import it.polito.verifoo.rest.jaxb.Neighbour;
import it.polito.verifoo.rest.jaxb.Node;

public class AllocationGraphGenerator {

	private NFV nfv;
	
	public AllocationGraphGenerator(NFV nfv) {
		this.nfv = nfv;
		for(Graph graph : nfv.getGraphs().getGraph()) {
			if(graph.isServiceGraph()) {
				generateAllocationGraph(graph);
			}		
		}
	}
	
	void generateAllocationGraph(Graph graph) {
		String base = "AllocationPlace";
		int counter = 1;
		List<Node> newNodes = new ArrayList<Node>();
		for(Node node : graph.getNode()) {
			
			List<String> toRemove = new ArrayList<String>();
			List<Neighbour> toAdd = new ArrayList<Neighbour>();
			
			for(Neighbour neighbour : node.getNeighbour()) {
				if(!neighbour.getName().startsWith("AllocationPlace")) {
					Node other = graph.getNode().stream().filter(n -> n.getName().equals(neighbour.getName())).findFirst().orElse(null);
					boolean bidirectional = other.getNeighbour().stream().anyMatch(n -> n.getName().equals(node.getName()));
					
					Node allocationPlace = new Node();
					allocationPlace.setName(base + counter);			
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
					
					//graph.getNode().add(allocationPlace);
					
					newNodes.add(allocationPlace);
				}
			}	
			
			node.getNeighbour().removeIf( n -> toRemove.stream().anyMatch(s -> s.equals(n.getName())));
			node.getNeighbour().addAll(toAdd);
		}
		
		graph.getNode().addAll(newNodes);
	}
	
	
	NFV getAllocationGraph() {
		return nfv;
	}
	
	
	
	
}
