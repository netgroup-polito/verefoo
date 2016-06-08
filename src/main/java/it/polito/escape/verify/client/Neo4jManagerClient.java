package it.polito.escape.verify.client;

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import it.polito.escape.verify.model.Entry;
import it.polito.escape.verify.resources.PathsMessageBodyReader;
import it.polito.nffg.neo4j.jaxb.*;
import it.polito.nffg.neo4j.jaxb.CiType.Attributes;
import it.polito.nffg.neo4j.jaxb.CiType.Attributes.Attribute;
import it.polito.nffg.neo4j.jaxb.EpType.Flowspace;
import it.polito.nffg.neo4j.jaxb.FlowrulesType.Flowspace.Tcp;
import it.polito.nffg.neo4j.jaxb.MonParamsType.Parameter;
import it.polito.nffg.neo4j.jaxb.SpecType.Cpu;
import it.polito.nffg.neo4j.jaxb.SpecType.Deployment;
import it.polito.nffg.neo4j.jaxb.SpecType.Image;
import it.polito.nffg.neo4j.jaxb.SpecType.Memory;
import it.polito.nffg.neo4j.jaxb.SpecType.Storage;

public class Neo4jManagerClient {
	
	private JAXBContext jc; 
	private NffgType nffg;
	
	private List<String> endpoints = new LinkedList<String>();
	private List<String> firewalls = new LinkedList<String>();
	private Map<String, List<Entry>> routingTable = new HashMap<String, List<Entry>>();
	
	private String source;
	private String destination;
	
	private String xmlString;
	

	public Neo4jManagerClient() {
//		this.source = "client_1";
//		this.destination = "server_1";
//		
//		this.endpoints.add("client_1");
//		this.endpoints.add("server_1");
//		
//		this.firewalls.add("firewall_1");
//		
//		for (String node : endpoints){
//			this.routingTable.put(node, new ArrayList<Entry>());
//		}
//		for (String firewall : firewalls){
//			this.routingTable.put(firewall + "_in", new ArrayList<Entry>());
//			this.routingTable.put(firewall + "_out", new ArrayList<Entry>());
//		}
//		this.routingTable.get("client_1").add(new Entry("output", "firewall_1_in"));
//		//this.routingTable.get("server_1").add(new Entry("output", "firewall_1_out"));
//		this.routingTable.get("firewall_1_out").add(new Entry("output", "server_1"));
		
	}
	
	public Neo4jManagerClient(String source, String destination, List<String> endpoints, List<String> firewalls, Map<String, List<Entry>> routingTable){
		this.source = source;
		this.destination = destination;
		this.endpoints = endpoints;
		this.firewalls = firewalls;
		this.routingTable = routingTable;
	}
	
//	public static void main(String[] args) {
//		
//		Neo4jManagerClient manager = new Neo4jManagerClient();
//		
//		try {
//			manager.generateCustomXml();
//		} catch (JAXBException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			System.out.println("Fatal error in the XML generation!");
//			System.exit(1);
//		}
//		
//		Client client = ClientBuilder.newClient();
//		
//		WebTarget baseTarget = client.target("http://localhost:8090/Project-Neo4jManager/rest/");
//		//WebTarget baseTarget = client.target("http://localhost:8080/neo4jmanager/rest/");
//		WebTarget graphsTarget = baseTarget.path("graphs");		
//		WebTarget pathSourceDestination = graphsTarget.path("{graphId}/paths");
//		WebTarget deleteNffg = graphsTarget.path("{graphId}");
//		
//		System.out.println("Deleting graph with id 1...");
//		
//		Response deleteNffgResponse = deleteNffg
//							.resolveTemplate("graphId", "1")
//							.request()
//							.delete();
//		
//		System.out.println( "Graph 1 deletion status: " + deleteNffgResponse.getStatus());
//		
//		System.out.println("Creating graph with id 1 from xml file...");
//		
//		Response createNffgResponse = graphsTarget
//							.request()
//							.post(Entity.xml(manager.xmlString));
//							//.post(Entity.xml(new File("nffg.xml")));
//		
//		System.out.println( "Graph 1 creation status: " + createNffgResponse.getStatus());
//		
//		System.out.println("Getting paths from node \"" + manager.source + "\" to node \"" + manager.destination + "\"...");
//		Response getPath = pathSourceDestination
//							.resolveTemplate("graphId", "1")
//							.queryParam("src", manager.source)
//							.queryParam("dst", manager.destination)
//							.queryParam("dir", "outgoing")
//							.request()
//							.get();
//		System.out.println("Paths from node \"" + manager.source + "\" to node \"" + manager.destination + "\":");
//		Paths paths = getPath.readEntity(Paths.class);
//		
//		for (String path : paths.getPath()){
//			System.out.println(path);
//		}
//		
//		
//	}
	
	public Paths runClient(){
		
		
		try {
			this.generateCustomXml();
		} catch (JAXBException e) {
			e.printStackTrace();
			System.out.println("Fatal error in the XML generation!");
			System.exit(1);
		}
		//return this.xmlString;
		
		//Client client = ClientBuilder.newClient();
		Client client = ClientBuilder.newBuilder()
						.register(PathsMessageBodyReader.class).build();
		
//		WebTarget baseTarget = client.target("http://localhost:8090/Project-Neo4jManager/rest/");
		WebTarget baseTarget = client.target("http://localhost:8080/neo4jmanager/rest/");
		WebTarget graphsTarget = baseTarget.path("graphs");		
		WebTarget pathSourceDestination = graphsTarget.path("{graphId}/paths");
		WebTarget deleteNffg = graphsTarget.path("{graphId}");
		
		System.out.println("Deleting graph with id 1...");
		
		Response deleteNffgResponse = deleteNffg
							.resolveTemplate("graphId", "1")
							.request()
							.delete();
		
		System.out.println( "Graph 1 deletion status: " + deleteNffgResponse.getStatus());
		
		System.out.println("Creating graph with id 1 from xml file...");
		
		Response createNffgResponse = graphsTarget
							.request("application/xml")
							.post(Entity.entity(this.xmlString, "application/xml"));
							//.post(Entity.xml(this.xmlString));
							//.post(Entity.xml(new File("nffg.xml")));
		
		System.out.println( "Graph 1 creation status: " + createNffgResponse.getStatus());
		
		System.out.println("Getting paths from node \"" + this.source + "\" to node \"" + this.destination + "\"...");
		Response getPath = pathSourceDestination
							.resolveTemplate("graphId", "1")
							.queryParam("src", this.source)
							.queryParam("dst", this.destination)
							.queryParam("dir", "outgoing")
							.request(MediaType.APPLICATION_XML)
							.get();
		System.out.println("Paths from node \"" + this.source + "\" to node \"" + this.destination + "\":");
		Paths paths = getPath.readEntity(Paths.class);
		
		return paths;
	}
	
	private void generateCustomXml() throws JAXBException {
		
		jc = JAXBContext.newInstance( "it.polito.nffg.neo4j.jaxb" );
		
		nffg = new NffgType();
		nffg.setId("nffg_1");
		
		generateEndpoints();
		generateFirewalls();
		generateConnections();
		
		JAXBElement<NffgType> root = (new ObjectFactory()).createNffg(nffg);
		Marshaller m;
		try {
			m = jc.createMarshaller();
			m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
			
			StringWriter stringWriter = new StringWriter();
			try {
				XMLStreamWriter xmlStreamWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(stringWriter);
				m.marshal( root, xmlStreamWriter );
				xmlString = stringWriter.getBuffer().toString();
			} catch (XMLStreamException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FactoryConfigurationError e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        //m.marshal( root, new File("nffg.xml") );
			
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}
	}

	private void generateConnections() {
		NelementsType network_elements = new NelementsType();
		NeType network_element = new NeType();
		network_element.setId("ne_1");
		network_element.setType("BiSBiS");
		EpsCpsType ep_cps = new EpsCpsType();
		
		for (String node: routingTable.keySet()){
			EpCpType ep_cp = new EpCpType();
			ep_cp.setIdRef(node);
			FlowrulesType flowrules = new FlowrulesType();
			it.polito.nffg.neo4j.jaxb.FlowrulesType.Flowspace flowspace = new it.polito.nffg.neo4j.jaxb.FlowrulesType.Flowspace();
			flowrules.setFlowspace(flowspace);
			ActionsType actions = new ActionsType();
				for (Entry e : routingTable.get(node)){
				ActionType action = new ActionType();
				action.setType(ActionEnumType.fromValue(e.getDirection()));
				action.setPort(e.getDestination());
				actions.getAction().add(action);
			}
			flowrules.setActions(actions);
			
			ep_cp.getFlowrules().add(flowrules);
			ep_cps.getEpCp().add(ep_cp);
		}
		network_element.setEpsCps(ep_cps);
		
		MonParamsType monitoring_parameters = new MonParamsType();
		Parameter parameter = new Parameter();
		parameter.getValue().add("Bandwith ep_1 cp_1 100mbit");
		monitoring_parameters.getParameter().add(parameter);
		Parameter parameter2 = new Parameter();
		parameter2.getValue().add("Delay ep_1 cp_1 50ms");
		monitoring_parameters.getParameter().add(parameter2);
		
		network_element.setMonitoringParameters(monitoring_parameters);
		
		network_elements.getNetworkElement().add(network_element);
	
		nffg.setNetworkElements(network_elements);
		
	}

	private void generateFirewalls() {
		NfunctionsType network_functions = new NfunctionsType();
		
		for (String firewall : firewalls){
			NfType nf = new NfType();
			nf.setId(firewall);
			nf.setFunctionalType("firewall");
			
			SpecType specification = new SpecType();
			Deployment deployment = new Deployment();
			deployment.setType("PolitoFirewall");
			Image image = new Image();
			image.setUri("http://www.polito.it");
			Cpu cpu = new Cpu();
			cpu.setNumCores((short)(7));
			Memory memory = new Memory();
			memory.setSize("10MiB");
			Storage storage = new Storage();
			storage.setSize("100MiB");
			specification.setDeployment(deployment);
			specification.setImage(image);
			specification.setCpu(cpu);
			specification.setMemory(memory);
			specification.setStorage(storage);
			
			CpointsType connection_points = new CpointsType();
			CpType connection_point = new CpType();
			connection_point.setId(firewall + "_in");
			PortType port = new PortType();
			port.setId(79);
			port.setDirection(PortDirEnumType.IN);
			port.setType("GbE");
			connection_point.setPort(port);
			connection_points.getConnectionPoint().add(connection_point);
			
			CpType connection_point2 = new CpType();			
			connection_point2.setId(firewall + "_out");
			PortType port2 = new PortType();
			port2.setId(77);
			port2.setDirection(PortDirEnumType.OUT);
			port2.setType("10GbE");
			connection_point2.setPort(port2);
			
			connection_points.getConnectionPoint().add(connection_point2);
			
			CtrlInterfacesType control_interfaces = new CtrlInterfacesType();
			CiType control_interface = new CiType();
			control_interface.setId(firewall + "_ci");
			
			Attributes attributes = new Attributes();			
			Attribute attribute = new Attribute();
			attribute.setValue("tcp://127.0.0.1:5555");
			attributes.getAttribute().add(attribute);
			Attribute attribute2 = new Attribute();
			attribute2.setValue("Netconf");
			attributes.getAttribute().add(attribute2);
			control_interface.setAttributes(attributes);
			
			
			control_interfaces.getControlInterface().add(control_interface);
			
			MonParamsType monitoring_parameters = new MonParamsType();
			Parameter parameter = new Parameter();
			parameter.getValue().add("Measure script");
			monitoring_parameters.getParameter().add(parameter);
			
			nf.setSpecification(specification);
			nf.setConnectionPoints(connection_points);
			nf.setControlInterfaces(control_interfaces);
			nf.setMonitoringParameters(monitoring_parameters);
			
			network_functions.getNetworkFunction().add(nf);

		}
		nffg.setNetworkFunctions(network_functions);
		
		
	}

	private void generateEndpoints() {
		EpointsType eps = new EpointsType();
		
		for (String e : endpoints){
			EpType endpoint = new EpType();
			endpoint.setId(e);
			Flowspace flowspace = new Flowspace();
			flowspace.setIngPhysPort("10");
			Tcp tcp = new Tcp();
			tcp.setSrc(80);
			flowspace.setTcp(tcp);
			endpoint.setFlowspace(flowspace);
			eps.getEndpoint().add(endpoint);
		}
		nffg.setEndpoints(eps);
	}

}
