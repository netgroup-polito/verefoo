package it.polito.verifoo.rest.medicine;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.verifoo.rest.common.Link;
import it.polito.verifoo.rest.jaxb.FunctionalTypes;
import it.polito.verifoo.rest.jaxb.Node;

public class ServiceDescriptor {
	
	private List<Node> nodes;
	private List<Link> links;
	private String file = "descriptor_version: \"1.0\"\n"
						+ "vendor: \"eu.sonata-nfv\"\n"
						+ "name: \"sonata-service\"\n"
						+ "version: \"0.1\"\n"
						+ "network_functions:\n";
	private String networkBuild = "#!/bin/sh\n";
	private List<String> testCommands = new ArrayList<>();
	public ServiceDescriptor(List<Node> nodes, List<Link> links, Map<String, VNFDescriptor> vnfds) {
		this.nodes = nodes;
		this.links = links;
		
		nodes.forEach(n ->{
			file += "   - vnf_id: \""+n.getName().toLowerCase()+"\"\n"
				  + "     vnf_vendor: \"eu.sonata-nfv\"\n"
				  + "     vnf_name: \""+n.getName().toLowerCase()+"-vnf\"\n"
				  + "     vnf_version: \"0.1\"\n";
		});
		file += "connection_points:\n"
			  + "  - id: \"ns:input\"\n"
			  + "    interface: \"ipv4\"\n"
			  + "    type: \"external\"\n"
			  + "  - id: \"ns:output\"\n"
			  + "    interface: \"ipv4\"\n"
			  + "    type: \"external\"\n"
			  + "virtual_links:\n";
		nodes.forEach(n ->{
			if(n.getFunctionalType().equals(FunctionalTypes.MAILCLIENT) ||
					   n.getFunctionalType().equals(FunctionalTypes.WEBCLIENT) ||	
					   n.getFunctionalType().equals(FunctionalTypes.ENDHOST)){
				file += "  - id: \"link-ext_to_"+n.getName().toLowerCase()+"\"\n"
					  + "    connectivity_type: \"E-Line\"\n"
					  + "    connection_points_reference:\n"
					  + "      - \"ns:input\"\n"
					  + "      - \""+n.getName().toLowerCase()+":intf-ext\"\n";
				
			}
			if(n.getFunctionalType().equals(FunctionalTypes.MAILSERVER) ||
					   n.getFunctionalType().equals(FunctionalTypes.WEBSERVER)){
				file += "  - id: \"link-"+n.getName().toLowerCase()+"_to_ext\"\n"
					  + "    connectivity_type: \"E-Line\"\n"
					  + "    connection_points_reference:\n"
					  + "      - \""+n.getName().toLowerCase()+":intf-ext\"\n"
					  + "      - \"ns:output\"\n";
				
			}
		});	  
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
	public String getServiceDescriptor(){
		return file;
	}
	public String getNetworkBuild(){
		return networkBuild;
	}
	public List<String> getTestCommands() {
		return testCommands;
	}
		      
}
