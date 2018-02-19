package it.polito.verifoo.rest.medicine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.polito.verifoo.rest.common.BadGraphError;
import it.polito.verifoo.rest.common.Link;
import it.polito.verifoo.rest.common.LinkCreator;
import it.polito.verifoo.rest.jaxb.Host;
import it.polito.verifoo.rest.jaxb.NFV;
import it.polito.verifoo.rest.jaxb.Node;
import it.polito.verifoo.rest.jaxb.NodeCapacity;

public class MedicineSimulator {
	
	private Logger logger = LogManager.getLogger("mylog");
	private String target = "http://127.0.0.1:5001/restapi";
	private WebTarget client = ClientBuilder.newClient().target(target);
	private List<Host> hosts;
	private List<Node> nodes;
	private List<Link> links;
	private List<NodeCapacity> capacities;
	PhysicalTopology phy;
	VNFDeployment d;
	public MedicineSimulator(NFV root){
		logger.debug("------------MEDICINE SIMULATION------------");
		hosts = root.getHosts().getHost();
		nodes = root.getGraphs().getGraph().stream().flatMap(g -> g.getNode().stream()).collect(Collectors.toList());
		links = (new LinkCreator(nodes)).getLinks();
		capacities = root.getCapacityDefinition().getCapacityForNode();
		logger.debug("Creating Physical Network");
		phy = new PhysicalTopology(root.getHosts().getHost(), root.getConnections().getConnection());
		createIN("");
		d = new VNFDeployment(root.getHosts().getHost());
		//deployVNF();
		logger.debug("-------------------------------------------");
	}
	
	private void createIN(String file) {
		try {
			InputStreamReader input;
		    OutputStreamWriter output;
	        //Create the process and start it.
	        Process pb = new ProcessBuilder(new String[]{"/bin/bash", "-c", "sudo python "+file}).start();
	        output = new OutputStreamWriter(pb.getOutputStream());
	        input = new InputStreamReader(pb.getInputStream());

	        int bytes;
	        char buffer[] = new char[1024];
	        while ((bytes = input.read(buffer, 0, 1024)) != -1) {
	            if(bytes == 0)
	                continue;
	            //Output the data to console, for debug purposes
	            String data = String.valueOf(buffer, 0, bytes);
	            System.out.println(data);
	            // Check for password request
	            if (data.contains("conteinernet>")) {
	                
	                output.write("client ./start.sh");
	                output.write('\n');
	                output.flush();
	            }
	        }
		} catch (IOException e) {
			System.out.println("The MeDICINE simulation can be run only on linux machines");
		}
		
	}

	public void printTopology(){
		System.out.println(phy.getTopologyDescription());
	}
	public void printPlacement(){
		System.out.println(d.getPlacementDescription());
	}
	
	private void deployVNF(){
		
		logger.debug("Deploying VNF");
		try{
			hosts.forEach(h->{
				if(h.isActive()){
					h.getNodeRef().forEach(n ->{
						int mem_limit = capacities.stream().filter(c -> c.getNode().equals(n.getNode())).map(c -> c.getCapacity()).findFirst().orElse(0);
						String request = "{ \"image\":\"ubuntu:trusty\","
										  + "\"mem_limit\": \""+ mem_limit + "\"}";
						logger.debug(request);
						Response res = client.path("/compute/+" + h.getName() + "/" + n.getNode())
								.request(MediaType.APPLICATION_JSON)
								.accept(MediaType.APPLICATION_JSON)
								.put(Entity.entity(request, MediaType.APPLICATION_JSON));
						logger.debug(res.getEntity().toString());
					});
				}
			});
			logger.debug("Connecting VNF");
			links.forEach(l ->{
				String request = "{\"vnf_src_name\": \""+ l.getSourceNode() + "\","
						 		+ "\"vnf_dst_name\": \""+ l.getDestNode() + "\","
						 		+ "\"bidirectional\": \"true\"}";
				logger.debug(request);
				Response res = client.path("/network")
						.request(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						.put(Entity.entity(request, MediaType.APPLICATION_JSON));
				logger.debug(res.getEntity().toString() + " connecting " + l.getSourceNode() + " -> " + l.getDestNode());
			});
		}catch(Exception e){
			System.err.println("Medicine Remote Service not available");
		}
	}
}
