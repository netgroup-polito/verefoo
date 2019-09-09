package it.polito.verefoo.murcia;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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

import org.xml.sax.SAXException;


import it.polito.verefoo.VerefooSerializer;
import it.polito.verefoo.jaxb.*;
import it.polito.verefoo.murcia.jaxb.*;
import it.polito.verefoo.murcia.jaxb.Configuration;
import it.polito.verefoo.murcia.jaxb.ObjectFactory;

public class MurciaParser {
	
	NFV result;
	ITResourceOrchestrationType resourceOrchestration;

	public MurciaParser(NFV result) {
		this.result = result;
		resourceOrchestration = new ITResourceOrchestrationType();
	}

	public void parseNFV() {
		
		for(Node node : result.getGraphs().getGraph().get(0).getNode()) {
			if(node.getFunctionalType() == FunctionalTypes.FIREWALL) {
				Firewall verefooFirewall = node.getConfiguration().getFirewall();
				ITResourceType resource = new ITResourceType();
				RuleSetConfiguration configuration = new RuleSetConfiguration();
				configuration.setResolutionStrategy(new ResolutionStrategy());
				configuration.setName("ConfFW");
				
				
				/* Create packet fitlering capability */
				FilteringCapability filteringCapability = new FilteringCapability();
				filteringCapability.setApplicationLayerFiltering(false);
				filteringCapability.setContentInspection(false);
				filteringCapability.setHttpFiltering(false);
				filteringCapability.setStateful(false);
				filteringCapability.setName(CapabilityType.FILTERING_L_4);
				configuration.getCapability().add(filteringCapability);
				
				
				/* Firewall default action */
				ActionTypes verefooDefaultAction = verefooFirewall.getDefaultAction();
				String defaultActionType;
				if(verefooDefaultAction == ActionTypes.ALLOW) defaultActionType = new String("ALLOW");
				else defaultActionType = new String("DENY");
				FilteringAction defaultAction = new FilteringAction();
				defaultAction.setFilteringActionType(defaultActionType);
				configuration.setDefaultAction(defaultAction);
				
				/* Firewall rules */
				int i = 0;
				for(Elements element : verefooFirewall.getElements()) {
					
					ConfigurationRule rule = new ConfigurationRule();
					rule.setName("Rule " + i);
					rule.setIsCNF(false);
					i++;
					
					/* Action of the rule */
					FilteringAction action = new FilteringAction();
					ActionTypes verefooRuleAction = element.getAction();
					String ruleActionType;
					if(verefooRuleAction == ActionTypes.ALLOW) ruleActionType = new String("ALLOW");
					else ruleActionType = new String("DENY");
					action.setFilteringActionType(ruleActionType);
					rule.setConfigurationRuleAction(action);
					
					/* Condition of the rule */
					
					FilteringConfigurationCondition condition = new FilteringConfigurationCondition();
					
					PacketFilterCondition pfCondition = new PacketFilterCondition();
					String IPSource = removeWildcardsFromIPAddress(element.getSource());
					if(IPSource != null) pfCondition.setSourceAddress(IPSource);
					String IPDest = removeWildcardsFromIPAddress(element.getDestination());
					if(IPDest != null) pfCondition.setDestinationAddress(IPDest);
					String portSource = element.getSrcPort();
					if(!portSource.equals("*")) pfCondition.setSourcePort(portSource);
					String portDest = element.getDstPort();
					if(!portDest.equals("*")) pfCondition.setDestinationPort(portDest);
					L4ProtocolTypes lv4proto = element.getProtocol();
					if(lv4proto == L4ProtocolTypes.TCP) pfCondition.setProtocolType("TCP");
					else if(lv4proto == L4ProtocolTypes.UDP) pfCondition.setProtocolType("UDP");
					
					condition.setIsCNF(false);
					condition.setPacketFilterCondition(pfCondition);
					rule.setConfigurationCondition(condition);
					
					/* Set the rule */
					List<ConfigurationRule> listCR = configuration.getConfigurationRule();
					listCR.add(rule);
				}
				
				
				
				/* Set of the configuration */
				resource.setConfiguration(configuration);
				List<ITResourceType> resources = resourceOrchestration.getITResource();
				resources.add(resource);
			}
		}
	}

	private String removeWildcardsFromIPAddress(String ip) {
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