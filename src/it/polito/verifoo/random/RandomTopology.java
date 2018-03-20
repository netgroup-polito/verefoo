package it.polito.verifoo.random;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import it.polito.verifoo.rest.jaxb.Connection;
import it.polito.verifoo.rest.jaxb.Connections;
import it.polito.verifoo.rest.jaxb.FunctionalTypes;
import it.polito.verifoo.rest.jaxb.Host;
import it.polito.verifoo.rest.jaxb.Hosts;
import it.polito.verifoo.rest.jaxb.Node;
import it.polito.verifoo.rest.jaxb.SupportedVNFType;
import it.polito.verifoo.rest.jaxb.TypeOfHost;

public class RandomTopology {
	Random random;
	Hosts hosts = new Hosts();
	Connections connections = new Connections();
	private int MAX_HOSTS = 20;
	private int nMiddle;
	/**
	 * Creates a totally random generated physical topology for Verifoo
	 * @param random a random number generator
	 * @param graphs a RandomGraph object describing the service graph
	 * @param maxHosts the maximum number of hosts on the topology
	 */
	public RandomTopology(Random random, RandomGraph graphs, int maxHosts) {
		this.random = random;
		this.MAX_HOSTS = maxHosts;
		createHosts(graphs.getClients(), graphs.getServers());
		createConnections();
	}
	/**
	 * Creates a totally random generated connections between hosts for Verifoo
	 */
	private void createConnections() {
		List<Connection> randomConnections = new ArrayList<>();
		List<Host> middleboxes = getMiddles();
		int n = 0;
		for(Host c : getClients()){
			n = 0;
			for(Host next : middleboxes){
				if(random.nextBoolean()){
					Connection conn = new Connection();
					conn.setSourceHost(c.getName());
					conn.setDestHost(next.getName());
					conn.setAvgLatency(random.nextInt(1000)+1);
					n++;
					randomConnections.add(conn);
				}
			}
			if(n == 0){
				//at least one connection must be present
				Connection conn = new Connection();
				conn.setSourceHost(c.getName());
				conn.setDestHost(middleboxes.get(random.nextInt(nMiddle)).getName());
				conn.setAvgLatency(random.nextInt(1000)+1);
				randomConnections.add(conn);
			}
		}
		
		for(Host s : getServers()){
			n = 0;
			for(Host next : middleboxes){
				if(random.nextBoolean()){
					Connection conn = new Connection();
					conn.setSourceHost(next.getName());
					conn.setDestHost(s.getName());
					conn.setAvgLatency(random.nextInt(1000)+1);
					n++;
					randomConnections.add(conn);
				}
			}
			if(n == 0){
				//at least one connection must be present
				Connection conn = new Connection();
				conn.setSourceHost(middleboxes.get(random.nextInt(nMiddle)).getName());
				conn.setDestHost(s.getName());
				conn.setAvgLatency(random.nextInt(1000)+1);
				randomConnections.add(conn);
			}
		}
		n = 0;
		for(Host src : middleboxes){
			for(Host dst : middleboxes){
				if(src == dst) continue;
				if(random.nextBoolean()){
					Connection conn = new Connection();
					conn.setSourceHost(src.getName());
					conn.setDestHost(dst.getName());
					conn.setAvgLatency(random.nextInt(1000)+1);
					n++;
					randomConnections.add(conn);
				}
			}
			if(n == 0){
				//at least one connection must be present
				Connection conn = new Connection();
				conn.setSourceHost(src.getName());
				conn.setDestHost(middleboxes.get(random.nextInt(nMiddle)).getName());
				conn.setAvgLatency(random.nextInt(1000)+1);
				randomConnections.add(conn);
			}
		}
		connections.getConnection().addAll(randomConnections);
	}
	/**
	 * Create a random number of hosts for Verifoo
	 * @param clients the clients in the service graph
	 * @param servers the servers in the service graph
	 */
	private void createHosts(List<Node> clients, List<Node> servers) {
		List<Host> randomHosts = new ArrayList<>();
		int i = 0;
		for(Node c : clients){
			Host h = randomHost();
			h.setType(TypeOfHost.CLIENT);
			h.setFixedEndpoint(c.getName());
			h.setName("host"+i);
			i++;
			randomHosts.add(h);
		}
		for(Node s : servers){
			Host h = randomHost();
			h.setType(TypeOfHost.SERVER);
			h.setFixedEndpoint(s.getName());
			h.setName("host"+i);
			i++;
			randomHosts.add(h);
		}
		int numHosts = random.nextInt(MAX_HOSTS)-clients.size()-servers.size();
		if(numHosts <= 0) numHosts = 1;
		nMiddle = numHosts;
		for(int j = 0; j < numHosts; j++){
			Host h = randomHost();
			h.setType(TypeOfHost.MIDDLEBOX);
			h.setName("host"+i);
			List<SupportedVNFType> vnfs = new ArrayList<>();
			for(FunctionalTypes vnf : FunctionalTypes.values()){
				if(random.nextBoolean()){
					SupportedVNFType s = new SupportedVNFType();
					s.setFunctionalType(vnf);
					vnfs.add(s);
				}
			}
			h.getSupportedVNF().addAll(vnfs);
			i++;
			randomHosts.add(h);
		}
		hosts.getHost().addAll(randomHosts);
	}
	
	private Host randomHost(){
		Host h = new Host();
		h.setCores(random.nextInt(8)+1);
		h.setCpu(random.nextInt(2)+1);
		h.setDiskStorage(random.nextInt(200)+1);
		h.setMaxVNF(random.nextInt(10)+1);
		h.setMemory(random.nextInt(64)+1);
		return h;
	}
	/**
	 * Getter for the hosts
	 * @return all the hosts in the topology
	 */
	public Hosts getHosts() {
		return hosts;
	}
	/**
	 * Getter for the connections
	 * @return all the connections in the topology
	 */
	public Connections getConnections() {
		return connections;
	}
	/**
	 * Getter for the client hosts
	 * @return only the client hosts in the topology
	 */
	public List<Host> getClients(){
		return hosts.getHost().stream().filter(h -> h.getType().equals(TypeOfHost.CLIENT)).collect(Collectors.toList());
	}
	/**
	 * Getter for the server hosts
	 * @return only the server hosts in the topology
	 */
	public List<Host> getServers(){
		return hosts.getHost().stream().filter(h -> h.getType().equals(TypeOfHost.SERVER)).collect(Collectors.toList());
	}
	/**
	 * Getter for the hosts in between the clients and the servers
	 * @return only the middle hosts in the topology
	 */
	public List<Host> getMiddles(){
		return hosts.getHost().stream().filter(h -> h.getType().equals(TypeOfHost.MIDDLEBOX)).collect(Collectors.toList());
	}

}
