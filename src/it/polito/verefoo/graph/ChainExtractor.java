/**
 * 
 */
package it.polito.verefoo.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.polito.verefoo.jaxb.Connection;
import it.polito.verefoo.jaxb.Host;
import it.polito.verefoo.jaxb.TypeOfHost;

/**
 * This is the class that extract all the possible chain from a physical network
 *
 */
public class ChainExtractor {

	private static List<List<String>> savedChain;
	private static List<Host> hosts;
	
	/**
	 * Calculates all the possible paths from the host client to the host server
	 * @param hostClient The first host of the chain
	 * @param hostServer The last host of the chain
	 * @param hs The entire list of hosts
	 * @param connections The connections between the hosts
	 * @param maxSize Max lenght of the chains
	 * @return
	 */
	public static List<List<String>> createHostChain(String hostClient, String hostServer, List<Host> hs, List<Connection> connections, int maxSize){
		List<String> hostChain = new ArrayList<>();
		savedChain = new ArrayList<>();
		hosts = hs;
		hostChain.add(hostClient);
		
		List<String> destinations = connections.stream()
								.filter(c -> c.getSourceHost().equals(hostClient) && !hostIsClient(c.getDestHost()) && !hostIsServer(c.getDestHost()))
								.map(c -> c.getDestHost())
								.collect(Collectors.toList());
		for(String dest:destinations){
			//logger.debug("Adding host to the current hostChain: "+h);
			hostChain.add(dest);
			expandHostChain(dest, hostServer, hostChain, connections, new HashMap<>(), maxSize);
			//logger.debug("Removing host from the current hostChain: "+dest);
			//logger.debug("Host in chain: "+hostChain);
			hostChain.remove(hostChain.lastIndexOf(dest));
		}
		//logger.debug("Calculated host chain " + savedChain);
		return new ArrayList<List<String>>(savedChain);
	}
	
	/**
	 * Explores recursively all the possible paths
	 * @param lastHost the host from which it calculate the next, for the recursion
	 * @param hostServer is the final host of the network
	 * @param hostChain List of all the hosts encountered in the current chain
	 */
	private static boolean expandHostChain(String lastHost, String hostServer, List<String> hostChain, List<Connection> connections, HashMap<String, List<String>> visited, int maxSize){
		if(lastHost.equals(hostServer)){
			//logger.debug("Dest Reached " + lastHost);
			savedChain.add(new ArrayList<>(hostChain));
			return true;
		}
		if(hostChain.size() >= maxSize || hostIsServer(lastHost)){ 
			//logger.debug("Chain MAX Size reached");
			return false;
		}
		List<String> destinations = connections.stream()
								.filter(c -> c.getSourceHost().equals(lastHost) && !hostIsClient(c.getDestHost()))
								.map(c -> c.getDestHost())
								.collect(Collectors.toList());
		if(!visited.containsKey(lastHost)){
			//logger.debug("New host visited -> " + lastHost);
			visited.put(lastHost, new ArrayList<>());
		}
		for(String dest:destinations){
			if(visited.get(lastHost).contains(dest) || dest.equals(lastHost)){
				//logger.debug("Host already visited -> From " + lastHost + " to " + dest + " in " + destinations);
				continue;
			}
			//logger.debug("Adding to visited from " + lastHost +" to " + dest);
			visited.get(lastHost).add(dest);
			/*
			if(hostChain.contains(h)){
				//logger.debug("Host already in chain "+h);
				continue;
			}*/
			hostChain.add(dest);
			//logger.debug("Adding host to the current hostChain: "+dest);
			//logger.debug("Host in chain: "+hostChain);
			expandHostChain(dest, hostServer, hostChain, connections, visited, maxSize);
			//logger.debug("Removing host from the current hostChain: "+dest);
			hostChain.remove(hostChain.lastIndexOf(dest));
			//logger.debug("Host in chain: "+hostChain);
			//logger.debug("Removing to visited from " + lastHost +" to " + dest);
			visited.get(lastHost).remove(dest);
		}
		
		return true;
		
	}
	
	/**
	 * Returns if the name of the host passed as argument is associated to a client host
	 * @param hostName
	 * @return
	 */
	public static boolean hostIsClient(String hostName){
		return hosts.stream().filter(h -> h.getName().equals(hostName) && h.getType().equals(TypeOfHost.CLIENT)).count() > 0;
	}
	
	/**
	 * Returns if the name of the host passed as argument is associated to a server host
	 * @param hostName
	 * @return
	 */
	public static boolean hostIsServer(String hostName){
		return hosts.stream().filter(h -> h.getName().equals(hostName) && h.getType().equals(TypeOfHost.SERVER)).count() > 0;
	}
	
	/**
	 * Calculates all the possible paths from the node client to the node server
	 * @param nodeClient
	 * @param nodeServer
	 * @param links
	 * @return
	 */
	public static List<List<String>> createNodeChain(String nodeClient, String nodeServer, List<Link> links){
		List<String> nodeChain = new ArrayList<>();
		savedChain = new ArrayList<>();
		
		nodeChain.add(nodeClient);
		
		List<String> destinations = links.stream()
								.filter(l -> l.getSourceNode().equals(nodeClient))
								.map(l -> l.getDestNode())
								.collect(Collectors.toList());
		for(String dest:destinations){
			//logger.debug("Adding host to the current hostChain: "+h);
			nodeChain.add(dest);
			expandNodeChain(dest, nodeServer, nodeChain, links);
			//logger.debug("Removing host from the current hostChain: "+dest);
			//logger.debug("Host in chain: "+hostChain);
			nodeChain.remove(nodeChain.lastIndexOf(dest));
		}
		//logger.debug("Calculated node chain " + savedChain);
		return new ArrayList<List<String>>(savedChain);
	}

	/**
	 * Explores recursively all the possible paths
	 * @param lastNode the node from which it calculates the next, for the recursion
	 * @param nodeServer is the final node of the network
	 * @param nodeChain List of all the nodes encountered in the current chain
	 * @param links List of all the links between the nodes
	 * @return
	 */
	private static boolean expandNodeChain(String lastNode, String nodeServer, List<String> nodeChain, List<Link> links){
		if(lastNode.equals(nodeServer)){
			//logger.debug("Dest Reached " + lastnode);
			savedChain.add(new ArrayList<>(nodeChain));
			return true;
		}
		List<String> destinations = links.stream()
								.filter(l -> l.getSourceNode().equals(lastNode))
								.map(l -> l.getDestNode())
								.collect(Collectors.toList());
		for(String dest:destinations){
			//logger.debug("Adding to visited from " + lastnode +" to " + dest);
			if(nodeChain.contains(dest)){
				//logger.debug("node already in chain "+h);
				continue;
			}
			nodeChain.add(dest);
			//logger.debug("Adding node to the current nodeChain: "+dest);
			//logger.debug("node in chain: "+nodeChain);
			expandNodeChain(dest, nodeServer, nodeChain, links);
			//logger.debug("Removing node from the current nodeChain: "+dest);
			nodeChain.remove(dest);
			//logger.debug("node in chain: "+nodeChain);
		}
		
		return true;
		
	}
}
