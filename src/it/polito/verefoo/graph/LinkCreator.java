package it.polito.verefoo.graph;

import java.util.ArrayList;
import java.util.HashMap;

import static java.util.Comparator.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.polito.verefoo.PacketFilterManager;
import it.polito.verefoo.extra.BadGraphError;
import it.polito.verefoo.jaxb.FunctionalTypes;
import it.polito.verefoo.jaxb.Node;
import it.polito.verefoo.jaxb.Path;
import it.polito.verefoo.jaxb.Property;

/**
 * Creates the links from the node's neighbours
 *
 */
public class LinkCreator {
	private List<Link> links = new ArrayList<>();
	private List<Node> nodes;
	private List<Property> properties;
	/**
	 * Public constructor of LinkCreator class
	 * @param ns the list of the nodes in the network service
	 */
	public LinkCreator(List<Node> ns){
		nodes = ns;
	}

	/**
	 * Public constructor of LinkCreator class
	 * @param ns the list of the nodes in the network service
	 * @param properties the list of middle level policies (reachability/isolation)
	 */
	public LinkCreator(List<Node> ns, List<Property> properties){
		nodes = ns;
		this.properties = properties;
	}

		
	/**
	 * Retrives the links of the service graph exploring the node's neighbours
	 * @return the links between nodes in the service graph
	 */
	public List<Link> getLinks(){
		
        for(Property p : properties) {
        	String src = p.getSrc();
        	String dst = p.getDst();
        	Node srcNode = nodes.stream().filter(n -> n.getName().equals(src)).findFirst().orElse(null);
        	Node dstNode = nodes.stream().filter(n -> n.getName().equals(dst)).findFirst().orElse(null);
			List<String> neighbours =srcNode.getNeighbour().stream()
												.map(n -> n.getName())
												.collect(Collectors.toList());
				//logger.debug("Found neighbours of " + client.getName() + " ("+ neighbours + ")");
		        for(String neighbour : neighbours){
		        	Node next = nodes.stream().filter(n -> n.getName().equals(neighbour)).findFirst().get();
		        	//logger.debug("Creating path from client " + client.getName() + " to "+ next.getName() +" towards server "+server.getName());
		        	createLink(srcNode, next, srcNode, dstNode, new ArrayList<>(), new ArrayList<>());
		        	//logger.debug("New Link from " + client.getName() + " to "+ next.getName() +" towards server "+server.getName());
		        }
		}
        
		List<Link> orderedLinks = links.stream()
										.sorted(comparing(Link::getSourceNode).thenComparing(Link::getDestNode))
										.distinct()
										.collect(Collectors.toList());
		return orderedLinks;
	}
	
	/**
	 * Creates the links from the nodes' neighbours exploring the various paths, ensuring to add a link only once
	 * @param prec previous node in the chain
	 * @param current current node in the chain
	 * @param srcNode the source node of the policy
	 * @param dstNode the destination node of the policy
	 * @param converting the list of nodes on the current path, to avoid infinite loops
	 * @param converted the nodes from which all the neighbours are already been explored
	 * @throws BadGraphError
	 */
	private boolean createLink(Node prec, Node current, Node srcNode, Node dstNode, List<String> converting, List<String> converted) throws BadGraphError{
		if(current.getName().equals(dstNode.getName())){
			//logger.debug("Found neighbours of " + prec.getName() + " ("+ current.getName() + ") that reaches the server " + server.getName());
			//logger.debug("New Link from " + prec.getName() + " to "+ current.getName() +" towards server "+server.getName());
			links.add(new Link(prec.getName(), current.getName()));
			return true;
		}
		if(current.getFunctionalType()!= null)
			if(current.getFunctionalType().equals(FunctionalTypes.MAILCLIENT) || current.getFunctionalType().equals(FunctionalTypes.WEBCLIENT)|| current.getFunctionalType().equals(FunctionalTypes.ENDHOST)){
			//logger.debug("Link from " + prec.getName() + " to "+ current.getName() +" reaches client "+client.getName());
			return false;
		}
		if(converted.contains(current.getName())){
			links.add(new Link(prec.getName(), current.getName()));
			return true;
		}
		
		boolean found = false;
		List<String> neighbours = current.getNeighbour().stream()
										.filter(n -> !(n.getName().equals(prec.getName())))
										.map(n -> n.getName())
										.collect(Collectors.toList());
		converting.add(current.getName());
		//logger.debug("From " + prec.getName() + " converting neighbours of " + current.getName() + " " + neighbours +" into links");
		for(String neighbour : neighbours){
			if(!converting.contains(neighbour)){
				Node next = nodes.stream().filter(n -> n.getName().equals(neighbour)).findFirst().get();
				//If the neighbour reaches the server or reaches a node that reaches the server then... 
				if(createLink(current, next, srcNode, dstNode, converting, converted) ){
					//logger.debug("Found neighbours of " + prec.getName() + " ("+ current.getName() + ") that reaches the server " + server.getName());
					Link l = new Link(prec.getName(), current.getName());
					//logger.debug("New Link from " + prec.getName() + " to "+ current.getName() +" towards server "+server.getName());
					links.add(l);
					converted.add(current.getName());
					found = true;
				}
				else{
					//logger.debug("Neighbour from " + current.getName() + " (" + neighbour +") don't reach the server " + server.getName());
				}
			}
		}
		converting.remove(current.getName());
		return found;
	}
}
