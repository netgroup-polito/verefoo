package it.polito.verifoo.rest.medicine;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import it.polito.verifoo.rest.common.Link;
import it.polito.verifoo.rest.common.LinkCreator;
import it.polito.verifoo.rest.common.PhyResourceModel;
import it.polito.verifoo.rest.jaxb.FunctionalTypes;
import it.polito.verifoo.rest.jaxb.Host;
import it.polito.verifoo.rest.jaxb.Hosts;
import it.polito.verifoo.rest.jaxb.NFV;
import it.polito.verifoo.rest.jaxb.Node;
import it.polito.verifoo.rest.jaxb.NodeConstraints.NodeMetrics;

public class MedicineSimulator implements PhyResourceModel {
	
	private Logger logger = LogManager.getLogger("mylog");
	private String target = "http://127.0.0.1:5001/restapi";
	private WebTarget client = ClientBuilder.newClient().target(target);
	private String projDir = System.getProperty("user.dir");
	private String sonEmuDir = "/home/sonata/";
	String topologyFile = "";
	private String projectDesc = "package:\n"
							   + "  description: \"Service Package\"\n"
							   + "  maintainer: \"Verifoo\"\n"
							   + "  name: service\n"
							   + "  vendor: eu.sonata-nfv.package\n"
							   + "  version: '0.4'\n"
							   + "descriptor_extension: \"yml\"\n"
							   + "version: '0.5'\n";
								 
	private List<Host> hosts;
	private List<Node> nodes;
	private List<Link> links;
	private List<NodeMetrics> nodeMetrics;
	private PhysicalTopology phy;
	private VNFDeployment d;
	private Map<String, VNFDescriptor> vnfds = new HashMap<>();
	private ServiceDescriptor sd;
	Process containernet;
	Process executeCommands;
	
	
	
	public MedicineSimulator(NFV root){
		logger.debug("------------MEDICINE SIMULATION------------");
		hosts = root.getHosts().getHost();
		nodes = root.getGraphs().getGraph().stream().flatMap(g -> g.getNode().stream()).collect(Collectors.toList());
		links = (new LinkCreator(nodes)).getLinks();
		nodeMetrics = root.getConstraints().getNodeConstraints().getNodeMetrics();
		logger.debug("Creating Physical Network");
		phy = new PhysicalTopology(root.getHosts().getHost(), root.getConnections().getConnection());
		d = new VNFDeployment(root.getHosts().getHost());
		for(Node n:nodes){
			NodeMetrics nm = nodeMetrics.stream()
											.filter(n1 -> n1.getNode().equals(n.getName()))
											.findFirst().orElse(null);
			int inLinks = (int) links.stream()
								.filter(l -> l.getDestNode().equals(n.getName()))
								.count();
			int outLinks = (int) links.stream()
					.filter(l -> l.getSourceNode().equals(n.getName()))
					.count();
			//System.out.println(n.getName()+" "+ nm + " " + inLinks + " " + outLinks);
			vnfds.put(n.getName().toLowerCase(), new VNFDescriptor(n, nm, inLinks, outLinks));
		}
		long nExternal = nodes.stream()
		   	 .filter((n) -> n.getFunctionalType().equals(FunctionalTypes.WEBCLIENT) || n.getFunctionalType().equals(FunctionalTypes.MAILCLIENT) || n.getFunctionalType().equals(FunctionalTypes.ENDHOST)
		   	  			|| n.getFunctionalType().equals(FunctionalTypes.WEBSERVER) || n.getFunctionalType().equals(FunctionalTypes.MAILSERVER))
		   	 .count();
		sd = new ServiceDescriptor(nodes, links, vnfds, nExternal);

		try {
			setPhysicalTopology();
			deployVNF();
			/*if(containernet != null){
				String line = "";
		        OutputStreamWriter output= new OutputStreamWriter(containernet.getOutputStream());
				BufferedReader input = new BufferedReader(new InputStreamReader(containernet.getInputStream()));
		        
				while (input.ready()) {
					line = input.readLine();
					//System.out.println(line);
		        }
				System.out.println("Testing topology...");
				for(String c : sd.getTestCommands()){
					//BufferedReader errorReader = new BufferedReader(new InputStreamReader (containernet.getErrorStream()));
					input = new BufferedReader(new InputStreamReader(containernet.getInputStream()));
					output= new OutputStreamWriter(containernet.getOutputStream());
					
					logger.debug("Testing simulation> " + c);
			        System.out.println("Testing simulation> " + c);
			        output.write(c);
		            output.write('\n');
		            output.flush();
		            
		            Thread.sleep(1000);
			        while (input.ready() && (line = input.readLine()) != null) {
			            //System.out.println(line);
			            if(line.contains("100% packet loss")){
			            	System.out.println("Packet lost with command: " + c);
			            	logger.debug("Packet lost with command: " + c);
			            }
			        }
				}
				System.out.println("Testing complete");
				logger.debug("Testing complete");
				containernet.destroy();
				//containernet.waitFor();
			}*/
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		logger.debug("-------------------------------------------");
	}
	@Override
	public void setPhysicalTopology() {
		try {
			topologyFile = "topology.py";
			printInFile(topologyFile, phy.getTopologyDescription());
			String placementFile = "placement.py";
			printInFile(placementFile, d.getPlacementDescription());
			//file = "/home/sonata/demo/topologies/test_topology.py";
			String[] command = new String[] {"/bin/bash", "-c", "mv "+placementFile+" "+sonEmuDir+"son-emu/src/emuvim/api/sonata/ 2>&1"};
        	logger.debug("Executing command -> " + command[2]);
            executeCommands = new ProcessBuilder(command).start();
            BufferedReader input = new BufferedReader(new InputStreamReader(executeCommands.getInputStream()));
            BufferedReader errorReader = new BufferedReader(new InputStreamReader (executeCommands.getErrorStream()));
            OutputStreamWriter output= new OutputStreamWriter(executeCommands.getOutputStream());
            String line;
            while ((line = input.readLine()) != null) {
                //System.out.println(line);
                }
            executeCommands.waitFor();   
	        logger.debug("Launching physical topology");
	        System.out.println("Launching physical topology...");
	        String[] args = new String[] {"/bin/bash", "-c", "sudo python " + topologyFile + " 2>&1"};
	        containernet = new ProcessBuilder(args).start();
	        input = new BufferedReader(new InputStreamReader(containernet.getInputStream()));
	        errorReader = new BufferedReader(new InputStreamReader (containernet.getErrorStream()));
	        output= new OutputStreamWriter(containernet.getOutputStream());
	        while ((line = input.readLine()) != null) {
	            //System.out.println(line);
	            if(line.contains("*** Starting CLI")){
	            	System.out.println("Containernet bootstrap terminated");
	            	break;
	            }
	        }
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("The MeDICINE simulation can be run only on linux machines");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public Hosts getPhysicalTopology() {
		if(!containernet.isAlive()) return null;
		List<Host> medicineHosts = new ArrayList<>();
		for(Host host:hosts){
			Response res = client.path("/datacenter/"+host.getName())
								.request(MediaType.APPLICATION_JSON)
								.get();
			if(res.getStatus() != 200) return null;
			
			
			//JSON deserializing
			
			String line = null;
            while ((line) != null) {
                System.out.println(line);
                if(line.contains("metadata")){
                	Host h = new Host();
                	h.setCores(0);
                	h.setCpu(0);
                	h.setDiskStorage(0);
                	h.setMaxVNF(0);
                	h.setMemory(0);
                	h.setType(null);
                	h.setFixedEndpoint("");
                	h.getSupportedVNF().addAll(null);
                	medicineHosts.add(h);
                }
            } 
			logger.debug("Physical Topology Retrived");
		    System.out.println("Physical Topology Retrived");

			Hosts topology = new Hosts();
			topology.getHost().addAll(medicineHosts);
			return topology;
		}
		return null;
	}
	
	public void printTopology(){
		System.out.println(phy.getTopologyDescription());
	}
	public void printPlacement(){
		System.out.println(d.getPlacementDescription());
	}
	public void printVNFDescriptors(){
		vnfds.forEach((n,v) ->{
			System.out.println("-------"+n+"-------");
			System.out.println(v.getVNFDescriptor());
			System.out.println("-------------------");
		});
	}
	public void printServiceDescriptor(){
		System.out.println(sd.getServiceDescriptor());
	}
	public void printAll(){
		this.printTopology();
		this.printPlacement();
		this.printVNFDescriptors();
		this.printServiceDescriptor();
	}
	public void printInFile(String fileName, String content){
		try (PrintWriter out = new PrintWriter(fileName)) {
		    out.println(content);
		}catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void deployVNF() throws IOException, InterruptedException{
		String serviceName = "service";
		System.out.println("Deploying son-emu project...");
        //System.out.println("Current dir:" +projDir);
		projDir+="/" + serviceName + "/";
		String projectDescName = "project.yml";
		String nsdName = "nsd.yml";
		String vnfdName = "-vnfd.yml";
		printInFile(projectDescName, projectDesc);
		printInFile(nsdName, sd.getServiceDescriptor());
		String buildFile = "#!/bin/sh\n"
						 + "mkdir " + serviceName + "\n"
						 + "mkdir " + serviceName + "/sources\n"
						 + "mkdir " + serviceName + "/sources/nsd\n"
						 + "mkdir " + serviceName + "/sources/vnf\n"
						 + "mv " + projectDescName + " " + serviceName + "/" + projectDescName + "\n"
						 + "mv " + nsdName + " " + serviceName + "/sources/nsd/" + nsdName + "\n";
		for(Node n:nodes){
			printInFile(n.getName().toLowerCase()+vnfdName, vnfds.get(n.getName().toLowerCase()).getVNFDescriptor());
			buildFile += "mkdir " + serviceName + "/sources/vnf/"+n.getName().toLowerCase()+"-vnf\n"
					   + "mv " + n.getName().toLowerCase()+vnfdName + " " + serviceName + "/sources/vnf/"+ n.getName().toLowerCase()+"-vnf" + "/" + n.getName().toLowerCase()+vnfdName + "\n";
		}
	   	
		printInFile("./build.sh",buildFile);
		printInFile("./networkBuild.sh", sd.getNetworkBuild());
		List<String[]> commands = new ArrayList<>();
		commands.add(new String[] {"/bin/bash", "-c", "chmod +x build.sh 2>&1"});
		commands.add(new String[] {"/bin/bash", "-c", "./build.sh 2>&1"});
		commands.add(new String[] {"/bin/bash", "-c", "son-package --project "+projDir+" -n " + serviceName + " 2>&1"});
		commands.add(new String[] {"/bin/bash", "-c", "son-access push --upload " + serviceName + ".son 2>&1"});		
		commands.add(new String[] {"/bin/bash", "-c", "son-access push --deploy latest 2>&1"});	
		commands.add(new String[] {"/bin/bash", "-c", "rm -r " + serviceName + "/ 2>&1"});
		//commands.add(new String[] {"/bin/bash", "-c", "rm -r " + serviceName + ".son 2>&1"});
		commands.add(new String[] {"/bin/bash", "-c", "chmod +x networkBuild.sh 2>&1"});
		commands.add(new String[] {"/bin/bash", "-c", "./networkBuild.sh 2>&1"});
		commands.add(new String[] {"/bin/bash", "-c", "rm build.sh 2>&1"});
		commands.add(new String[] {"/bin/bash", "-c", "rm networkBuild.sh 2>&1"});
		commands.add(new String[] {"/bin/bash", "-c", "rm " + topologyFile + " 2>&1"});
        for(String[] c : commands){
        	logger.debug("Executing command -> " + c[2]);
            executeCommands = new ProcessBuilder(c).start();
            BufferedReader input = new BufferedReader(new InputStreamReader(executeCommands.getInputStream()));
            BufferedReader errorReader = new BufferedReader(new InputStreamReader (executeCommands.getErrorStream()));
            OutputStreamWriter output= new OutputStreamWriter(executeCommands.getOutputStream());
            String line;
            while ((line = input.readLine()) != null) {
                //System.out.println(line);
                }
            executeCommands.waitFor();   
        }
        logger.debug("Service Chain deployed");
        System.out.println("Service Chain deployed");
	}

	public void stopSimulation() {
		if(containernet != null){
			containernet.destroy();
		}
	}


}
