package it.polito.verefoo.graph;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import it.polito.verefoo.PacketFilterManager;
import it.polito.verefoo.extra.BadGraphError;
import it.polito.verefoo.jaxb.EType;
import it.polito.verefoo.jaxb.Node;
import it.polito.verefoo.jaxb.Path;
import it.polito.verefoo.jaxb.Property;
/**
 * Wraps the choice of the outgoing links from a certain node
 *
 */
public class LinkProvider {
	private List<Link> links;
	private List<Path> paths;
	private List<Property> properties;
	private int lastPathId = -1, lastNodeIndex = -1; 
	
	/**
	 * Public constructor of LinkProvider
	 * @param ns List of nodes
	 * @param ps List of paths
	 * @param properties List of properties
	 */
	public LinkProvider(List<Node> ns, List<Path> ps, List<Property> properties){
		paths = ps;
		this.properties = properties;
		links = (new LinkCreator(ns, this.properties)).getLinks(); 
	}
	
	/**
	 * Get all the links between the nodes (based on the neighbours indications)
	 * @return all the links
	 */
	public List<Link> getAllLinks(){
		return new ArrayList<>(links);
	}
	
	/**
	 * Checks if exists a path between the specified client and server
	 * @return true if exists a path, false otherwise
	 */
	public boolean existsPath(Node client, Node server){
		if(paths == null) return true;
		for(Path p: paths){
			if(p.getPathNode().get(0).getName().equals(client.getName()) && 
					p.getPathNode().get(p.getPathNode().size()-1).getName().equals(server.getName()) && 
					paths.indexOf(p) > lastPathId){
				lastPathId = paths.indexOf(p);
				lastNodeIndex = -1;
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Based on a source node and the level of the recursion (depth of a path), returns the links towards the next node
	 * @param n source node
	 * @param displacement level of the recursion (depth of a path)
	 * @return the links towards the next node
	 */
	public List<Link> getLinksFrom(Node n, int displacement){
		if(lastPathId != -1){
			List<String> p = paths.get(lastPathId).getPathNode().stream().map(pn -> pn.getName()).collect(Collectors.toList());
			lastNodeIndex = p.subList(displacement, p.size()).indexOf(n.getName())+displacement;
			if(lastNodeIndex == -1)
				throw new BadGraphError("Invalid Path with id " + lastPathId,EType.INVALID_SERVICE_GRAPH);
			List<Link> nextLinks = new ArrayList<>();
			nextLinks.add(new Link(p.get(lastNodeIndex), p.get(lastNodeIndex+1)));
			return nextLinks;
		}
		return links.stream().filter(l -> l.getSourceNode().equals(n.getName())).collect(Collectors.toList());
	}

}
