package it.polito.verifoo.rest.common;

import java.util.ArrayList;
import static java.util.Comparator.*;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.polito.verifoo.rest.jaxb.EType;
import it.polito.verifoo.rest.jaxb.FunctionalTypes;
import it.polito.verifoo.rest.jaxb.Node;
/**
 * Creates the link from the node's neighbours
 *
 */
public class LinkCreator {
	private Logger logger = LogManager.getLogger("mylog");
	private List<Link> links = new ArrayList<>();
	private List<Node> nodes;

	public LinkCreator(List<Node> ns){
		nodes = ns;
	}
	/**
	 * Retrives the links of the service graph
	 * @return
	 */
	public List<Link> getLinks(){
		Node client = nodes.stream().filter(n -> {return n.getFunctionalType().equals(FunctionalTypes.MAILCLIENT) || n.getFunctionalType().equals(FunctionalTypes.WEBCLIENT)|| n.getFunctionalType().equals(FunctionalTypes.ENDHOST);}).findFirst().get();
        Node server = nodes.stream().filter(n -> {return n.getFunctionalType().equals(FunctionalTypes.MAILSERVER) || n.getFunctionalType().equals(FunctionalTypes.WEBSERVER);}).findFirst().get();
        if(client.getNeighbour().size() != 1 || server.getNeighbour().size() != 1) throw new BadGraphError("Nodes must have 1 client and 1 server",EType.INVALID_NODE_CHAIN);
        String nextName = client.getNeighbour().stream().filter(n -> !(n.getName().equals(client.getName()))).findFirst().get().getName();
        Node next = nodes.stream().filter(n -> n.getName().equals(nextName)).findFirst().get();
		createLink(client, next, server, new ArrayList<>(), new ArrayList<>());
		logger.debug("New Link from " + client.getName() + " to "+ next.getName() +" towards server "+server.getName());
		links.add(new Link(client.getName(), next.getName()));
		List<Link> orderedLinks = links.stream()
										.sorted(comparing(Link::getSourceNode).thenComparing(Link::getDestNode))
										.distinct()
										.collect(Collectors.toList());
		logger.debug("Unique links:");
		orderedLinks.forEach(l -> logger.debug(l.getSourceNode()+"->"+l.getDestNode()));
		return orderedLinks;
	}
	/**
	 * Creates the links to the nodes' neighbours
	 * @param prec previous node in the chain
	 * @param current current node in the chain
	 * @param server the node that is the server of the chain
	 * @throws BadGraphError
	 */
	private boolean createLink(Node prec, Node current, Node server, List<String> converting, List<String> converted) throws BadGraphError{
		if(current.getName().equals(server.getName())){
			//logger.debug("Found neighbours of " + prec.getName() + " ("+ current.getName() + ") that reaches the server");
			logger.debug("New Link from " + prec.getName() + " to "+ current.getName() +" towards server "+server.getName());
			links.add(new Link(prec.getName(), current.getName()));
			return true;
		}
		if(converted.contains(current.getName())){
			//logger.debug("Found neighbours of " + prec.getName() + " ("+ current.getName() + ") that reaches the server");
			logger.debug("New Link from " + prec.getName() + " to "+ current.getName() +" towards server "+server.getName());
			links.add(new Link(prec.getName(), current.getName()));
			return true;
		}
		
		//if(current.getNeighbour().size() > 2) throw new BadGraphError("Nodes must be in a chain",EType.INVALID_NODE_CHAIN);
		boolean found = false;
		try {
			List<String> neighbours = current.getNeighbour().stream()
											.filter(n -> !(n.getName().equals(prec.getName())))
											.map(n -> n.getName())
											.collect(Collectors.toList());
			converting.add(current.getName());
			//logger.debug("From " + prec.getName() + " converting neighbours of " + current.getName() + " " + neighbours +" into links");
			
			for(String neighbour : neighbours){
				if(!converting.contains(neighbour)){
					Node next = nodes.stream().filter(n -> n.getName().equals(neighbour)).findFirst().get();
					//If neighbour reaches the server or a node that reaches the server then... 
					if(createLink(current, next, server, converting, converted) ){
						//logger.debug("Found neighbours of " + prec.getName() + " ("+ current.getName() + ") that reaches the server");
						Link l = new Link(prec.getName(), current.getName());
						logger.debug("New Link from " + prec.getName() + " to "+ current.getName() +" towards server "+server.getName());
						links.add(l);
						converted.add(current.getName());
						found = true;
					}
					else{
						//logger.debug("Neighbour from " + current.getName() + " (" + neighbour +") don't reach the server");
					}
				}
			}
			converting.remove(current.getName());
		} catch (NoSuchElementException e) {
			throw new BadGraphError("Nodes must be in a chain",EType.INVALID_NODE_CHAIN);
		}
		return found;
	}
}
