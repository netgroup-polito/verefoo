package it.polito.verifoo.rest.common;

import java.util.ArrayList;
import static java.util.Comparator.*;
import java.util.List;
import java.util.stream.Collectors;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
		List<Node> clients = nodes.stream().filter(n -> {return n.getFunctionalType().equals(FunctionalTypes.MAILCLIENT) || n.getFunctionalType().equals(FunctionalTypes.WEBCLIENT)|| n.getFunctionalType().equals(FunctionalTypes.ENDHOST);}).collect(Collectors.toList());
        List<Node> servers = nodes.stream().filter(n -> {return n.getFunctionalType().equals(FunctionalTypes.MAILSERVER) || n.getFunctionalType().equals(FunctionalTypes.WEBSERVER);}).collect(Collectors.toList());
        for(Node client:clients){
			for(Node server:servers){
				List<String> neighbours = client.getNeighbour().stream()
												.map(n -> n.getName())
												.collect(Collectors.toList());
				//logger.debug("Found neighbours of " + client.getName() + " ("+ neighbours + ")");
		        for(String neighbour : neighbours){
		        	Node next = nodes.stream().filter(n -> n.getName().equals(neighbour)).findFirst().get();
		        	//logger.debug("Creating path from client " + client.getName() + " to "+ next.getName() +" towards server "+server.getName());
		        	createLink(client, next, client, server, new ArrayList<>(), new ArrayList<>());
		        	//logger.debug("New Link from " + client.getName() + " to "+ next.getName() +" towards server "+server.getName());
		        }
				//links.add(new Link(client.getName(), next.getName()));
			}
		}
        
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
	private boolean createLink(Node prec, Node current, Node client, Node server, List<String> converting, List<String> converted) throws BadGraphError{
		if(current.getName().equals(server.getName())){
			//logger.debug("Found neighbours of " + prec.getName() + " ("+ current.getName() + ") that reaches the server " + server.getName());
			//logger.debug("New Link from " + prec.getName() + " to "+ current.getName() +" towards server "+server.getName());
			links.add(new Link(prec.getName(), current.getName()));
			return true;
		}
		if(current.getFunctionalType().equals(FunctionalTypes.MAILCLIENT) || current.getFunctionalType().equals(FunctionalTypes.WEBCLIENT)|| current.getFunctionalType().equals(FunctionalTypes.ENDHOST)){
			//logger.debug("Link from " + prec.getName() + " to "+ current.getName() +" reaches client "+client.getName());
			return false;
		}
		if(converted.contains(current.getName())){
			//logger.debug("Found neighbours of " + prec.getName() + " ("+ current.getName() + ") that reaches the server " + server.getName());
			//logger.debug("New Link from " + prec.getName() + " to "+ current.getName() +" towards server "+server.getName());
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
				if(createLink(current, next, client, server, converting, converted) ){
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
