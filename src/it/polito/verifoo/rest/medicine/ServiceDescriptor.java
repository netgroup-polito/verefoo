package it.polito.verifoo.rest.medicine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.verifoo.rest.common.Link;
import it.polito.verifoo.rest.jaxb.FunctionalTypes;
import it.polito.verifoo.rest.jaxb.Node;

public class ServiceDescriptor {
	
	private String file = "descriptor_version: \"1.0\"\n"
						+ "vendor: \"eu.sonata-nfv\"\n"
						+ "name: \"sonata-service\"\n"
						+ "version: \"0.1\"\n"
						+ "network_functions:\n";
	private String networkBuild = "#!/bin/sh\n";
	private List<String> testCommands = new ArrayList<>();
	/**
	 * Public constructor that creates the file that describes the service graph
	 * @param nodes List of all the nodes in the service graph
	 * @param links List all the links between the nodes
	 * @param vnfds Map that contains for each VNF, its descriptor
	 * @param nExternal Number of clients and server in the service graph
	 */
	public ServiceDescriptor(List<Node> nodes, List<Link> links, Map<String, VNFDescriptor> vnfds, long nExternal) {

		nodes.forEach(n ->{
			file += "   - vnf_id: \""+n.getName().toLowerCase()+"\"\n"
				  + "     vnf_vendor: \"eu.sonata-nfv\"\n"
				  + "     vnf_name: \""+n.getName().toLowerCase()+"-vnf\"\n"
				  + "     vnf_version: \"0.1\"\n";
		});
		file += "connection_points:\n";
		for(int i = 0; i < nExternal; i++){
			file += "  - id: \"ns:ext" + i + "\"\n"
				  + "    interface: \"ipv4\"\n"
				  + "    type: \"ext\"\n";
		}
		file += "virtual_links:\n";
		int nE = 0;
		for(Node n : nodes){
			if(n.getFunctionalType().equals(FunctionalTypes.MAILCLIENT) ||
					   n.getFunctionalType().equals(FunctionalTypes.WEBCLIENT) ||	
					   n.getFunctionalType().equals(FunctionalTypes.ENDHOST)){
				file += "  - id: \"link-ext_to_"+n.getName().toLowerCase()+"\"\n"
					  + "    connectivity_type: \"E-Line\"\n"
					  + "    connection_points_reference:\n"
					  + "      - \"ns:ext" + nE + "\"\n"
					  + "      - \""+n.getName().toLowerCase()+":intf-ext\"\n";
				nE++;
			}
			if(n.getFunctionalType().equals(FunctionalTypes.MAILSERVER) ||
					   n.getFunctionalType().equals(FunctionalTypes.WEBSERVER)){
				file += "  - id: \"link-"+n.getName().toLowerCase()+"_to_ext\"\n"
					  + "    connectivity_type: \"E-Line\"\n"
					  + "    connection_points_reference:\n"
					  + "      - \""+n.getName().toLowerCase()+":intf-ext\"\n"
					  + "      - \"ns:ext" + nE + "\"\n";
				nE++;
			}
		}	  
		links.forEach(l ->{
			file += "  - id: \"link-"+l.getSourceNode()+"_to_"+l.getDestNode()+"\"\n"
				  + "    connectivity_type: \"E-Line\"\n"
				  + "    connection_points_reference:\n"
				  + "      - \""+l.getSourceNode().toLowerCase()+":output"+vnfds.get(l.getSourceNode().toLowerCase()).bookOutInterface()+"\"\n"
				  + "      - \""+l.getDestNode().toLowerCase()+":input"+vnfds.get(l.getDestNode().toLowerCase()).bookInInterface()+"\"\n";
			networkBuild += "son-emu-cli network add -b -src " 
					+ l.getSourceNode().toLowerCase()+":output"+vnfds.get(l.getSourceNode().toLowerCase()).getFreeOutInterface() 
					+ " -dst " + l.getDestNode().toLowerCase()+":input"+vnfds.get(l.getDestNode().toLowerCase()).getFreeInInterface()
					+ "\n";
			testCommands.add(l.getSourceNode().toLowerCase()+" ping -c1 "+l.getDestNode().toLowerCase()); 
		});
	} 
	/**
	 * Get the service descriptor content that will be needed by MeDICINE
	 * @return
	 */
	public String getServiceDescriptor(){
		return file;
	}
	/**
	 * Gets the commands to connect the nodes
	 * @return
	 */
	public String getNetworkBuild(){
		return networkBuild;
	}
	/**
	 * Gets the commands that should be used to test if the nodes are connected and reachable
	 * @return
	 */
	public List<String> getTestCommands() {
		return testCommands;
	}
		      
}
