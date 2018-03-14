package it.polito.verifoo.rest.medicine;

import java.util.List;

import it.polito.verifoo.rest.jaxb.Connection;
import it.polito.verifoo.rest.jaxb.Host;

public class PhysicalTopology {
	
	public List<Host> hosts;
	public List<Connection> connections;
	private String fileHeader = "import logging\n"
			+"from mininet.log import setLogLevel\n"
			+"from emuvim.dcemulator.net import DCNetwork\n"
			+"from emuvim.api.rest.rest_api_endpoint import RestApiEndpoint\n"
			+"from emuvim.api.sonata import SonataDummyGatekeeperEndpoint\n"
			+"from mininet.node import RemoteController\n"
			+"import os\n"
			+"logging.basicConfig(level=logging.INFO)\n"
			+"def create_topology1():\n"
			+"\tnet = DCNetwork(controller=RemoteController, monitor=False, enable_learning=False)\n";
			
			   
    private String fileFooter = "\tnet.start()\n"
    							+"\tnet.CLI()\n"
							    +"\tnet.stop()\n"
							    +"def main():\n"
							    +"\tsetLogLevel('info')  # set Mininet loglevel\n"
							    +"\tcreate_topology1()\n"
								+"if __name__ == '__main__':\n"
								+"\tmain()\n";
	private String fileBody = "";
			
	public PhysicalTopology(List<Host> hosts, List<Connection> connections) {
		this.hosts = hosts;
		this.connections = connections;
		
		//Create a data center for each host
		for(Host h:hosts){
			fileBody+= "\t"+h.getName() +"  = net.addDatacenter(\""+ h.getName() +"\", metadata={"
																	+ "\"cores\"=\"" + h.getCores() +"\""
																	+ ", \"cpu\"=\"" + h.getCpu() +"\""
																	+ ", \"memory\"=\"" + h.getMemory() +"\""
																	+ ", \"diskStorage\"=\"" + h.getMaxVNF() +"\""
																	+ ", \"type\"=\"" + h.getType() +"\""
																	+ h.getFixedEndpoint()==null?"":", \"fixedEndpoint\"=\"" + h.getFixedEndpoint() +"\""
																	+ ", \"supported_vnfs\"=\"" + h.getSupportedVNF() +"\""
																	+ "})\n";
		}
		//Connect the data center in the same way as the hosts
		 for(Connection c:connections){
			 fileBody+= "\tnet.addLink("+c.getSourceHost()+", "+c.getDestHost()+", delay=\""+(-c.getAvgLatency())+"ms\")\n";
		 }
		 //Add RestAPI
		fileBody+= "\trapi1 = RestApiEndpoint(\"0.0.0.0\", 5001)\n"
					+"\trapi1.connectDCNetwork(net)\n";
		for(Host h:hosts){
			fileBody+= "\trapi1.connectDatacenter("+h.getName()+")\n";
		}
		fileBody+="\trapi1.start()\n";
		//Add SONATA dummy gatekeeper
		fileBody+= "\tsdkg1 = SonataDummyGatekeeperEndpoint(\"0.0.0.0\", 5000, deploy_sap=True)\n";
		for(Host h:hosts){
			fileBody+= "\tsdkg1.connectDatacenter("+h.getName()+")\n";
		}
		fileBody+="\tsdkg1.start()\n";
	}

	public String getTopologyDescription() {
		return fileHeader+fileBody+fileFooter;
	}


}
