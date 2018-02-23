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
import it.polito.verifoo.rest.jaxb.NodeConstraints.NodeMetrics;

public class MedicineSimulator {
	
	private Logger logger = LogManager.getLogger("mylog");
	private String target = "http://127.0.0.1:5001/restapi";
	private WebTarget client = ClientBuilder.newClient().target(target);
	private List<Host> hosts;
	private List<Node> nodes;
	private List<Link> links;
	private List<NodeMetrics> nodeMetrics;
	PhysicalTopology phy;
	VNFDeployment d;
	Process containernet;
	public MedicineSimulator(NFV root){
		logger.debug("------------MEDICINE SIMULATION------------");
		hosts = root.getHosts().getHost();
		nodes = root.getGraphs().getGraph().stream().flatMap(g -> g.getNode().stream()).collect(Collectors.toList());
		links = (new LinkCreator(nodes)).getLinks();
		nodeMetrics = root.getConstraints().getNodeConstraints().getNodeMetrics();
		logger.debug("Creating Physical Network");
		phy = new PhysicalTopology(root.getHosts().getHost(), root.getConnections().getConnection());
		
		createIN("");
		d = new VNFDeployment(root.getHosts().getHost());
		deployVNF();
		try {
			containernet.waitFor();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.debug("-------------------------------------------");
	}
	
	private void createIN(String file) {
		try {
			file = "/home/sonata/demo/topologies/test_topology.py";
		    
	        //Create the process and start it.
	        logger.debug("Launching physical topology");
	        //String[] args = new String[] {"/bin/bash", "-c", "sudo python", "/home/sonata/demo/topologies/test_topology.py", "2>&1"};
	        String[] args = new String[] {"/bin/bash", "-c", "/home/sonata/startTopology.sh"};
	        containernet = new ProcessBuilder(args).start();
	        //containernet = new ProcessBuilder("/home/sonata/startTopology.sh").start();
	        
	        String line = "";
	        BufferedReader input = new BufferedReader(new InputStreamReader(containernet.getInputStream()));
	        BufferedReader errorReader = new BufferedReader(new InputStreamReader (containernet.getErrorStream()));
	        OutputStreamWriter output= new OutputStreamWriter(containernet.getOutputStream());
	        while ((line = input.readLine()) != null) {
	            System.out.println(line);
	            if(line.contains("*** Starting CLI")){
	            	System.out.println("Containernet bootstrap terminated");
	            	break;
	            }
	        };
	        //Process pb = Runtime.getRuntime().exec("sudo ls");
	        output = new OutputStreamWriter(containernet.getOutputStream());
	        //out = containernet.getOutputStream();  
	        output.write("client ./start.sh");
            output.write('\n');
            output.flush();
            System.out.println(input.readLine());
	       
	        logger.debug("Deploying nodes");
	        Process pb = Runtime.getRuntime().exec("son-emu-cli compute start -d hostA -n client -i verifoo/client-vnf");
	        
	        input = new BufferedReader(new InputStreamReader(pb.getInputStream()));
	        
	        
	        
	        while ((line = input.readLine()) != null) {
	            System.out.println(line);
	        }
	        System.out.println("Deploying exited with code:" + pb.waitFor());
	        //System.out.println("Containernet exited with code:" + containernet.waitFor());
	        
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("The MeDICINE simulation can be run only on linux machines");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
						int mem_limit = nodeMetrics.stream().filter(n1 -> n1.getNode().equals(n.getNode())).map(n1 -> n1.getReqStorage()).findFirst().orElse(0);
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
				logger.debug(res.getEntity() + " connecting " + l.getSourceNode() + " -> " + l.getDestNode());
			});
		}catch(Exception e){
			System.err.println("Medicine Remote Service not available");
		}
	}
}
