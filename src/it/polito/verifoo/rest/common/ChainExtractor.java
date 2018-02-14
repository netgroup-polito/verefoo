/**
 * 
 */
package it.polito.verifoo.rest.common;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.polito.verifoo.rest.jaxb.Connection;

/**
 * This is the class that extract all the possible chain from a physical network or 
 * a service graph
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
		for(String h:destinations){
			//System.out.println("Adding dest: "+h);
			hostChain.add(h);
			expandHostChain(h, hostServer, hostChain, connections, maxSize);
			hostChain.remove(h);
		}
		logger.debug("Calculated host chain " + savedChain);
		return new ArrayList<List<String>>(savedChain);
	}
	/**
	 * Explores recursively all the possible paths
	 * @param lastHost the host from which it calculate the next for the recursion
	 * @param hostServer is the final host of the network
	 * @param hostChain List of all the hosts encountered in the current chain
	 */
	private static boolean expandHostChain(String lastHost, String hostServer, List<String> hostChain, List<Connection> connections, int maxSize){
		if(lastHost.equals(hostServer)){
			//logger.debug("Dest Reached " + lastHost);
			savedChain.add(new ArrayList<>(hostChain));
			return true;
		}
		if(hostChain.size() > maxSize) return false;
		List<String> destinations = connections.stream()
								.filter(c -> c.getSourceHost().equals(lastHost))
								.map(c -> c.getDestHost())
								.collect(Collectors.toList());
		for(String h:destinations){
			if(hostChain.contains(h)){
				//logger.debug("Host already in chain "+h);
				continue;
			}
			hostChain.add(h);
			//logger.debug("Adding dest: "+h);
			//logger.debug("Host in chain: "+hostChain);
			expandHostChain(h, hostServer, hostChain, connections, maxSize);
			hostChain.remove(h);
		}
		
		return true;
		
	}
}
