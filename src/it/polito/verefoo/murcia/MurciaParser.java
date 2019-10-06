package it.polito.verefoo.murcia;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.*;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.xml.sax.SAXException;


import it.polito.verefoo.VerefooSerializer;
import it.polito.verefoo.jaxb.*;
import it.polito.verefoo.murcia.jaxb.*;
import it.polito.verefoo.murcia.jaxb.Configuration;
import it.polito.verefoo.murcia.jaxb.ObjectFactory;
import it.polito.verefoo.utils.Tuple;

public class MurciaParser {
	
	NFV result;
	ITResourceOrchestrationType resourceOrchestration;
	Map<String, String> switchVerefooIPAddress;
	Map<String, List<Tuple<Integer, String>>> switchMap;
	Map<String, String> ip4to6;

	public MurciaParser(NFV result) {
		this.result = result;
		resourceOrchestration = new ITResourceOrchestrationType();
		switchVerefooIPAddress = new HashMap<String, String>() {
		{
		    put("of:000000000000000a", "1.0.0.8");
		    put("of:000000000000000b", "1.0.0.7");
		    put("of:0000000000000001", "1.0.0.01");
		    put("of:0000000000000002", "1.0.0.2");
		    put("of:0000000000000003", "1.0.0.3");
		    put("of:0000000000000004", "1.0.0.4");
		    put("of:0000000000000005", "1.0.0.5");
		    put("of:0000000000000006", "1.0.0.6");
		    put("of:0000000000000007", "1.0.0.9");
		    put("of:0000000000000008", "1.0.0.10");
		    put("of:0000000000000009", "1.0.0.11");
		}};
		
		switchMap = new HashMap<String, List<Tuple<Integer, String>>>() {{
			 put("1.0.0.01", new ArrayList<Tuple<Integer, String>>());
			 put("1.0.0.2", new ArrayList<Tuple<Integer, String>>());
			 put("1.0.0.3", new ArrayList<Tuple<Integer, String>>());
			 put("1.0.0.4", new ArrayList<Tuple<Integer, String>>());
			 put("1.0.0.5", new ArrayList<Tuple<Integer, String>>());
			 put("1.0.0.6", new ArrayList<Tuple<Integer, String>>());
			 put("1.0.0.7", new ArrayList<Tuple<Integer, String>>());
			 put("1.0.0.8", new ArrayList<Tuple<Integer, String>>());
			 put("1.0.0.9", new ArrayList<Tuple<Integer, String>>());
			 put("1.0.0.10", new ArrayList<Tuple<Integer, String>>());
			 put("1.0.0.11", new ArrayList<Tuple<Integer, String>>());		 
		}};
		ip4to6 = new HashMap<String, String>() {{
			put("200.0.0.-1", "200a::/64");
			put("201.0.0.-1", "200b::/64");
			put("202.0.0.-1", "200c::/64");
			put("204.0.0.2", "200d::2/128");
		}};
		

        Object obj;
		try {
			obj = new JSONParser().parse(new FileReader("xsd/murcia_json/hosts_links.json"));
			JSONObject jo = (JSONObject) obj; 
			JSONArray hosts = (JSONArray) jo.get("hosts");
			for (int i = 0; i <  hosts.size(); i++) {
			    JSONObject host = (JSONObject) hosts.get(i);
			    JSONArray addresses = (JSONArray) host.get("ipAddresses");
			    String address = null;
			    for(int j = 0; j < addresses.size(); j++) {
			    	String singleIP = (String) addresses.get(j);
			    	if(singleIP.startsWith("fe")) continue;
			    	if(singleIP.startsWith("200")) address = singleIP;
			    	else if(address == null) address = singleIP;
			    }
			    if(address.equals("94.142.250.1")) continue;
			    if(address.endsWith("1")) address = new String("router");
			    
			    
			    JSONArray locations = (JSONArray) host.get("locations");
			    for(int j = 0; j < locations.size(); j++) {
			    	JSONObject location = (JSONObject) locations.get(j);
			    	String swId = (String) location.get("elementId");
			    	String port = (String) location.get("port");
			    	String swIp = switchVerefooIPAddress.get(swId);
			    	switchMap.get(swIp).add(new Tuple<Integer, String>(new Integer(port), address));
			    }
			  
			}
			
		
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} 
         
	}
	
	
	public void parseNFV() {
		
		int node_number = 0;
		for(Node node : result.getGraphs().getGraph().get(0).getNode()) {
			if(node.getFunctionalType() == FunctionalTypes.FIREWALL) {
				
				
				String confN = new String("Conf" + node_number);
				int rule_number = 0;
				Firewall verefooFirewall = node.getConfiguration().getFirewall();
				String swId = node.getName();
				List<Tuple<Integer, String>> list = switchMap.get(swId);
				String interfaceN = null;
				
				//if(!swId.equals("1.0.0.9")) continue;
				
				for(Map.Entry<String, String> entry : switchVerefooIPAddress.entrySet()) {
					if(entry.getValue().equals(swId)) interfaceN = entry.getKey();
				}
				
				for(Elements element : verefooFirewall.getElements()) {
					String verefooSrc = element.getSource();
					String verefooDst = element.getDestination();
			
					
					if(verefooSrc.startsWith("20")) verefooSrc = ip4to6.get(verefooSrc);
					if(verefooDst.startsWith("20")) verefooDst = ip4to6.get(verefooDst);
					
					//System.out.println(verefooSrc + " --> " + verefooDst);
					
					for(int i = 0; i < list.size(); i++) {
						
				
						Tuple<Integer, String> tuple1 = list.get(i);
						int sport1 = tuple1._1;
						String shost1 = tuple1._2;
						boolean includedSRC1 = includedIn(shost1, verefooSrc);
						boolean includedDST1 = includedIn(shost1, verefooDst);
						boolean exactSRC1 = exactIn(shost1, verefooSrc);
						boolean exactDST1 = exactIn(shost1, verefooDst);
						boolean router1 = shost1.contains("router");
						//System.out.println(shost1);
						//System.out.println(includedSRC1 + " " + includedDST1 + " " + exactSRC1 + " " + exactDST1);
						
						for(int j = i+1; j < list.size(); j++) {
							

							Tuple<Integer, String> tuple2 = list.get(j);
							int sport2 = tuple2._1;
							String shost2 = tuple2._2;
							boolean includedSRC2 = includedIn(shost2, verefooSrc);
							boolean includedDST2 = includedIn(shost2, verefooDst);
							boolean exactSRC2 = exactIn(shost2, verefooSrc);
							boolean exactDST2 = exactIn(shost2, verefooDst);
							boolean router2 = shost2.contains("router");
							//System.out.println(shost2);
							//System.out.println(includedSRC2 + " " + includedDST2 + " " + exactSRC2 + " " + exactDST2);
							
							if((exactSRC1 && exactDST2) || (exactSRC1 && router2) || (exactDST2 && router1)) {
								ITResourceType resource = createRule(confN, new String("Rule" + rule_number), new String (interfaceN + "/" + sport1),  new String (interfaceN + "/" + sport2), removeWildcardsFromIPAddress(verefooSrc), removeWildcardsFromIPAddress(verefooDst));
								List<ITResourceType> resources = resourceOrchestration.getITResource();
								resources.add(resource);
								rule_number++;
							}
							else if(includedSRC1 && includedDST2 && !exactSRC2 && !exactDST1) {
						
								ITResourceType resource = createRule(confN, new String("Rule" + rule_number), new String (interfaceN + "/" + sport1),  new String (interfaceN + "/" + sport2), removeWildcardsFromIPAddress(verefooSrc), removeWildcardsFromIPAddress(verefooDst));
								List<ITResourceType> resources = resourceOrchestration.getITResource();
								resources.add(resource);
								rule_number++;
							}
							
							
							if((exactSRC2 && exactDST1) || (exactSRC2 && router1) || (exactDST1 && router2)) {
								
								ITResourceType resource = createRule(confN, new String("Rule" + rule_number), new String (interfaceN + "/" + sport2),  new String (interfaceN + "/" + sport1), removeWildcardsFromIPAddress(verefooSrc), removeWildcardsFromIPAddress(verefooDst));
								List<ITResourceType> resources = resourceOrchestration.getITResource();
								resources.add(resource);
								rule_number++;
							}
							
							else if(includedSRC2 && includedDST1 && !exactSRC1 && !exactDST2) {
								
								ITResourceType resource = createRule(confN, new String("Rule" + rule_number), new String (interfaceN + "/" + sport2),  new String (interfaceN + "/" + sport1), removeWildcardsFromIPAddress(verefooSrc), removeWildcardsFromIPAddress(verefooDst));
								List<ITResourceType> resources = resourceOrchestration.getITResource();
								resources.add(resource);
								rule_number++;
							}
							
							
							
						}
					}
					
				}
				
				node_number++;
			}
		
			
		
		}
				
		
	}
	
	public boolean includedIn(String first, String second) {
		//System.out.println(first+" "+second+"one");
		if(first.contains("::")) return false;
		if(second.contains("::")) return false;
		//System.out.println(first+" "+second+"two");
		if(first.equals("router")) return true;
		
		String[] parts1 = first.split("\\.");
		String[] parts2 = second.split("\\.");
		
		if(parts2[0].equals("-1")) return true;
		if(parts1[0].equals(parts2[0])) return true;
		return false;
	}
	
	public boolean exactIn(String first, String second) {
		//System.out.println(first + "hh" + second);
		if(first.contains("::")) {
			if(!second.contains("::")) return false;
			String[] parts1 = first.split("::");
			String[] parts2 = second.split("::");
			return parts1[0].equals(parts2[0]);
		}
		
		if(first.equals("router")) return false;
		
		String[] parts1 = first.split("\\.");
		String[] parts2 = second.split("\\.");
		//System.out.println((parts1[0].equals(parts2[0]) && parts1[1].equals(parts2[1]) && parts1[2].equals(parts2[2])));
		return (parts1[0].equals(parts2[0]) && parts1[1].equals(parts2[1]) && parts1[2].equals(parts2[2]));
	}

	
	public ITResourceType createRule(String confN, String ruleN, String sourceInt, String destInt, String sourceAddr, String destAddr) {
		ITResourceType resource = new ITResourceType();
		RuleSetConfiguration configuration = new RuleSetConfiguration();

		/* creating the capability */
		FilteringCapability filteringCapability = new FilteringCapability();
		filteringCapability.setName(CapabilityType.TRAFFIC_DIVERT);
		configuration.getCapability().add(filteringCapability);
		
		/* creating the rule */
		ConfigurationRule rule = new ConfigurationRule();
		
		TrafficDivertAction action = new TrafficDivertAction();
		action.setTrafficDivertActionType(TrafficDivertActionType.FORWARD);
		TrafficDivertConfigurationCondition confAction = new TrafficDivertConfigurationCondition();
		confAction.setIsCNF(false);
		PacketFilterCondition pfcAction = new PacketFilterCondition();
		pfcAction.setInterface(destInt);
		confAction.setPacketFilterCondition(pfcAction);
		action.setPacketDivertAction(confAction);
		rule.setConfigurationRuleAction(action);
		
		TrafficDivertConfigurationCondition condition = new TrafficDivertConfigurationCondition();
		condition.setIsCNF(false);
		PacketFilterCondition pfc = new PacketFilterCondition();
		if(sourceAddr != null) pfc.setSourceAddress(sourceAddr);
		if(destAddr != null) pfc.setDestinationAddress(destAddr);
		pfc.setInterface(sourceInt);
		condition.setPacketFilterCondition(pfc);
		rule.setConfigurationCondition(condition);
		
		Priority prior = new Priority();
		prior.setValue(new BigInteger("60000"));
		rule.setExternalData(prior);
		
	
		rule.setName(ruleN);
		rule.setIsCNF(false);
		configuration.getConfigurationRule().add(rule);
		
		/*final settings */
		configuration.setName(confN);
		resource.setConfiguration(configuration);
		resource.setPriority(new BigInteger("1000"));
		EnablerCandidates candidates = new EnablerCandidates();
		candidates.getEnabler().add("onos_nb");
		resource.setEnablerCandidates(candidates);
		return resource;
	}
	
	
	
	private String removeWildcardsFromIPAddress(String ip) {
		
		if(ip.contains("::")) return ip;
		String[] parts = ip.split("\\.");
		String mask = new String("");
		if(parts[0].equals("-1")) return null;
		else if(parts[1].equals("-1")) mask = new String("/8");
		else if(parts[2].equals("-1")) mask = new String("/16");
		else if(parts[3].equals("-1")) mask = new String("/24");
		String ipWithoutWildcards= ip.replaceAll("-1", "0");
		return ipWithoutWildcards.concat(mask);
	}

	public void createXMLFile(String file) {
		
		
		try {
			JAXBContext jc;
			jc = JAXBContext.newInstance("it.polito.verefoo.murcia.jaxb");
			Marshaller m = jc.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.setProperty(Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION, "./xsd/murcia.xsd");
			SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = sf.newSchema(new File("./xsd/murcia.xsd"));
			m.setSchema(schema);
			JAXBElement<ITResourceOrchestrationType> roElement = (new ObjectFactory()).createITResourceOrchestration(resourceOrchestration);
			m.marshal(roElement, new FileOutputStream(file));
			
		} catch (JAXBException je) {
			System.out.println(je);
			System.exit(1);
		} catch (FileNotFoundException fe) {
			System.out.println(fe);
			System.exit(1);
		} catch (SAXException se) {
			System.out.println(se);
			System.exit(1);
		}
	
	
	}
	

}