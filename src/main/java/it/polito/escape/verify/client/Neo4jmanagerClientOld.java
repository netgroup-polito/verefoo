package it.polito.escape.verify.client;

import java.io.File;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;

import org.glassfish.jersey.jaxb.internal.DocumentBuilderFactoryInjectionProvider;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import it.polito.nffg.neo4j.jaxb.EpType;
import it.polito.nffg.neo4j.jaxb.EpointsType;
import it.polito.nffg.neo4j.jaxb.NffgSetType;
import it.polito.nffg.neo4j.jaxb.NffgType;

public class Neo4jmanagerClientOld {
	
	private Document doc;
	Element root;
	
	public static void main(String[] args) {
		
		Neo4jmanagerClientOld manager = new Neo4jmanagerClientOld();
		try {
			manager.generateCustomXml();
		} catch (ParserConfigurationException | TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Parser or transformer fatal error!");
			System.exit(1);
		}
		
//		Client client = ClientBuilder.newClient();
//		
//		Response response = client.target("http://localhost:8080/Project-Neo4jManager/rest/graphs").request().get();
//		
//		NffgSetType readEntity = response.readEntity(NffgSetType.class);
//		for ( NffgType nffg : readEntity.getNffg()){
//			List<EpType> endpoints = nffg.getEndpoints().getEndpoint();
//			for (EpType endpoint : endpoints){
//				//endpoint.
//			}
//		}
	}

	private void generateCustomXml() throws ParserConfigurationException, TransformerException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		
		DocumentBuilder builder = factory.newDocumentBuilder();
		
		doc = builder.newDocument();
		
		root = doc.createElement("nffg");
		
		doc.appendChild(root);
		
		generateEndpoints();
		generateFirewalls();
		generateConnections();
		
		TransformerFactory xformFactory = TransformerFactory.newInstance ();
        Transformer idTransform = xformFactory.newTransformer ();
        idTransform.setOutputProperty(OutputKeys.INDENT, "yes");
        idTransform.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "./flightInfo.dtd");
        Source input = new DOMSource (doc);

        Result output = new StreamResult (new File ("prova.xml"));
        idTransform.transform (input, output);
	}

	private void generateConnections() {
		Element network_elements = doc.createElement("network_elements");
		Element network_element = doc.createElement("network_element");
		network_element.setAttribute("id", "ne_1");
		network_element.setAttribute("type", "BiSBiS");
		
		Element epsCps = doc.createElement("eps-cps");
		Element epCp = doc.createElement("ep-cp");
		epCp.setAttribute("id_ref", "client_1");
		Element flowrules = doc.createElement("flowrules");
		Element flowspace = doc.createElement("flowspace");
		Element actions = doc.createElement("actions");
		Element action = doc.createElement("action");
		action.setAttribute("type", "output");
		action.setAttribute("port", "cp_1");
		actions.appendChild(action);
		flowrules.appendChild(flowspace);
		flowrules.appendChild(actions);
		epCp.appendChild(flowrules);
		
		epsCps.appendChild(epCp);
		
		Element monitoring_parameters = doc.createElement("monitoring_parameters");
		Element parameter = doc.createElement("parameter");
		parameter.setAttribute("value", "Bandwith ep_1 cp_1 100mbit");
		Element parameter2 = doc.createElement("parameter");
		parameter2.setAttribute("value", "Delay ep_1 cp_1 50ms");
		monitoring_parameters.appendChild(parameter);
		monitoring_parameters.appendChild(parameter2);
		
		network_element.appendChild(monitoring_parameters);
		
		network_elements.appendChild(network_element);
		
		root.appendChild(network_elements);
		
		Element global_monitoring_parameters = doc.createElement("monitoring_parameters");
		global_monitoring_parameters.setAttribute("nil", "true");
		root.appendChild(global_monitoring_parameters);

		
	}

	private void generateFirewalls() {
		// creating network_functions
		Element firewalls = doc.createElement("network_functions");
		
		Element firewall = doc.createElement("network_function");
		firewall.setAttribute("id", "firewall_1");
		firewall.setAttribute("functionalType" , "firewall");
		
		Element specification = doc.createElement("specification");
		// children of specification
		Element deployment = doc.createElement("deployment");
		deployment.setAttribute("type", "PolitoFirewall");
		Element image = doc.createElement("image");
		image.setAttribute("uri", "http://wwww.polito.it");
		Element cpu = doc.createElement("cpu");
		cpu.setAttribute("numCores", "7");
		Element memory = doc.createElement("memory");
		memory.setAttribute("size", "10MiB");
		Element storage = doc.createElement("storage");
		storage.setAttribute("size", "100MiB");
		// append specification children
		specification.appendChild(deployment);
		specification.appendChild(image);
		specification.appendChild(cpu);
		specification.appendChild(memory);
		specification.appendChild(storage);
		
		Element connection_points = doc.createElement("connection_points");
		// children of connection_points
		Element connection_point = doc.createElement("connection_point");
		connection_point.setAttribute("id", "cp_1");
		Element port = doc.createElement("port");
		port.setAttribute("id", "79");
		port.setAttribute("direction", "in");
		port.setAttribute("type", "GbE");
		connection_point.appendChild(port);

		Element connection_point2 = doc.createElement("connection_point");
		connection_point2.setAttribute("id", "cp_2");
		Element port2 = doc.createElement("port");
		port2.setAttribute("id", "77");
		port2.setAttribute("direction", "out");
		port2.setAttribute("type", "10GbE");
		connection_point2.appendChild(port2);
		// append connection points childern
		connection_points.appendChild(connection_point);
		connection_points.appendChild(connection_point2);
		
		Element control_interfaces = doc.createElement("control_interfaces");
		//control_interfaces children
		Element control_interface = doc.createElement("control_interface");
		control_interface.setAttribute("id", "ci_1");		
		Element attributes = doc.createElement("attributes");
		Element attribute = doc.createElement("attribute");
		attribute.setAttribute("value", "tcp://127.0.0.1:5555");
		Element attribute2 = doc.createElement("attribute");
		attribute2.setAttribute("value", "Netconf");
		attributes.appendChild(attribute);
		attributes.appendChild(attribute2);		
		control_interface.appendChild(attributes);
		control_interfaces.appendChild(control_interface);
		// monitoring parameters
		Element monitoring_parameters = doc.createElement("monitoring_parameters");
		Element parameter = doc.createElement("parameter");
		parameter.setAttribute("value", "Measure script");
		monitoring_parameters.appendChild(parameter);
		
		firewalls.appendChild(firewall);
		
		root.appendChild(firewalls);
		
	}

	private void generateEndpoints() {
		// creating endpoints
		Element endpoints = doc.createElement("endpoints");
		
		//creating clients
		Element endpointClient = doc.createElement("endpoint"); 
		endpointClient.setAttribute("id", "client_1");
		Element flowspaceClient = doc.createElement("flowspace");
		flowspaceClient.setAttribute("ingPhysPort", "10");
		Element tcpClient = doc.createElement("tcp");
		tcpClient.setAttribute("src", "80");
		flowspaceClient.appendChild(tcpClient);
		endpointClient.appendChild(flowspaceClient);
		endpoints.appendChild(endpointClient);
		
		//creating servers
		Element endpointServer = doc.createElement("endpoint"); 
		endpointServer.setAttribute("id", "server_1");
		Element flowspaceServer = doc.createElement("flowspace");
		flowspaceServer.setAttribute("ingPhysPort", "10");
		Element tcpServer = doc.createElement("tcp");
		tcpServer.setAttribute("src", "80");
		flowspaceServer.appendChild(tcpServer);
		endpointServer.appendChild(flowspaceServer);
		endpoints.appendChild(endpointServer);
		
		root.appendChild(endpoints);
		
	}

}
