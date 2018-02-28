/**
 * 
 */
package it.polito.verifoo.rest.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.polito.verifoo.rest.jaxb.Connection;

/**
 * This is the class that extract all the possible chain from a physical network
 * @author Antonio
 *
 */
public class ChainExtractor {

	private static Logger logger = LogManager.getLogger("mylog");
	private static List<List<String>> savedChain;
	/**
	 * Calculates all the possible paths from the host client to the host server
	 * @param hostClient
	 * @param hostServer
	 * @return 
	 */
	public static List<List<String>> createHostChain(String hostClient, String hostServer, List<Connection> connections, int maxSize){
		List<String> hostChain = new ArrayList<>();
		savedChain = new ArrayList<>();
		
		hostChain.add(hostClient);
		
		List<String> destinations = connections.stream()
								.filter(c -> c.getSourceHost().equals(hostClient))
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
	 * @param lastHost the host from which it calculate the next for the recursion
	 * @param hostServer is the final host of the network
	 * @param hostChain List of all the hosts encountered in the current chain
	 */
	private static boolean expandHostChain(String lastHost, String hostServer, List<String> hostChain, List<Connection> connections, HashMap<String, List<String>> visited, int maxSize){
		if(lastHost.equals(hostServer)){
			//logger.debug("Dest Reached " + lastHost);
			savedChain.add(new ArrayList<>(hostChain));
			return true;
		}
		if(hostChain.size() >= maxSize){ 
			//logger.debug("Chain MAX Size reached");
			return false;
		}
		List<String> destinations = connections.stream()
								.filter(c -> c.getSourceHost().equals(lastHost))
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
}
