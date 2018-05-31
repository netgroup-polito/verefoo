package it.polito.verifoo.rest.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.ws.rs.NotAllowedException;

import org.apache.logging.log4j.LogManager;

import it.polito.verifoo.rest.jaxb.*;
import it.polito.verifoo.rest.jaxb.NodeConstraints.NodeMetrics;
import it.polito.verigraph.mcnet.components.Tuple;
/**
 * This class implements a parser for verifoo output (the z3 model), in order to translate it into the correct XML 
 */
public class Translator {
	private String model;
	private NFV nfv;
	private org.apache.logging.log4j.Logger logger = LogManager.getLogger("mylog");
	private Graph g;
	/**
	 * Constructor
	 * @param model The Verifoo output.
	 * @param nfv The NFV model to complete.
	 * @param g 
	 */
	public Translator(String model,NFV nfv, Graph g){
		this.model=model;
		this.nfv=nfv;
		this.g = g;
	}
	/**
	 * Conversion function
	 */
	public void convert(){
		if(nfv.getHosts() != null) 
			nfv.getHosts().getHost().forEach(this::searchHost);
		setAutoConfig();
	}
	/**
	 * Wraps the translation for all the VNFs that can be auto configurated by Verifoo
	 */
	public void setAutoConfig() {
		setFirewallAutoConfig();
		setDPIAutoConfig();
		setAntispamAutoConfig();
	}
	/**
	 * For each host we search for all possible configuration of nodes(at)host and check if Int value is 1
	 * Then we set the element in the XML accordingly.
	 * @param host Physical Host
	 */
	public void searchHost(Host host){
		List<String> nodesAlreadyDeployed = host.getNodeRef().stream().map(nr -> nr.getNode()).collect(Collectors.toList());
		g.getNode().forEach((node)->{
					String tosearch="define-fun .*integer_.*"+node.getName()+"@"+host.getName()+".* \\(\\) Int\n  1\\)";
					Pattern pattern = Pattern.compile(tosearch);
					Matcher matcher = pattern.matcher(model);
					if (matcher.find()) {
						logger.debug(tosearch);
						host.setActive(true);
						NodeRefType nr=new NodeRefType();
						nr.setNode(node.getName());
						if(!nodesAlreadyDeployed.contains(node.getName())){
							host.getNodeRef().add(nr);
							allocateResources(host, node.getName());
						}
					}/*
					if(model.contains(tosearch)){
						logger.debug(tosearch);
						host.setActive(true);
						NodeRefType nr=new NodeRefType();
						nr.setNode(node.getName());
						if(!nodesAlreadyDeployed.contains(node.getName())){
							host.getNodeRef().add(nr);
							allocateResources(host, node.getName());
						}
					}*/
				});
	}
	
	private String firewallAutoConfigSearchDst(Node n, String nrOfRule){
		List<String> nodes = g.getNode().stream().map(no -> no.getName()).collect(Collectors.toList());
		String tosearch="define-fun .*"+n.getName()+".*_auto_dst_"+nrOfRule+".* Address\n  \\(ip_constructor .*\\)\\)";
		Pattern patternDst = Pattern.compile(tosearch);
		Matcher matcherDst = patternDst.matcher(model);
		String nodeDstName = "";
		while (matcherDst.find()) {
	        String matchDst = matcherDst.group();
	        String dstRule = matchDst.substring(matchDst.lastIndexOf("ip_constructor ")+15, matchDst.lastIndexOf("))"));
	        //System.out.println("///DstRule " + dstRule + "////");
	        tosearch="define-fun .* Address\n  \\(ip_constructor "+dstRule+"\\)\\)";
			Pattern patternNodeDst = Pattern.compile(tosearch);
			Matcher matcherNodeDst = patternNodeDst.matcher(model);
			
			boolean dstFound = false;
			while(matcherNodeDst.find()) {
		        String match = matcherNodeDst.group();
		        String nodeDst = match.substring(match.lastIndexOf("define-fun ")+11, match.lastIndexOf(" () Address"));
		        nodeDst = nodeDst.replace("|", "");
		        if(nodes.contains(nodeDst)){
		        	//System.out.println("Found dest node " + nodeDst + " with address " + dstRule);
		        	nodeDstName = nodeDst;
		        	dstFound = true;
		        	break;
		        }
		    }
	        if(!dstFound){
	        	//System.out.println("Dest Node with address " + dstRule + " not found");
	        	nodeDstName = dstRule;
	        }
	        
			nodeDstName = nodeDstName.replace("(", "").replace(")", "").replace("- ", "-").replace(" ", ".");
		}
		return nodeDstName;
	}
	
	private String stringToSearchProtocol(Node n, String nrOfRule){
		return "define-fun .*"+n.getName()+".*_auto_proto_"+nrOfRule+".* Int\n  .*\\)";
	}	
	private String stringToSearchAction(Node n, String defAction){
		return "define-fun .*"+n.getName()+".*_auto_"+defAction+".* Bool\n  .*\\)";
	}
	private String stringToSearchPort(Node n, String nrOfRule, String srcOrDest){
		return "define-fun .*"+n.getName()+".*_auto_"+srcOrDest+"p_"+nrOfRule+".* Int\n  .*\\)";
	}
	private String stringToSearchPort(Node n, String nrOfRule, String startOrEnd, String srcOrDest){
		return "define-fun .*"+n.getName()+".*_auto_"+startOrEnd+"_"+srcOrDest+"p_"+nrOfRule+".* Int\n  .*\\)";
	}
	private String firewallAutoConfigSearchAttribute(String tosearch){
		Pattern pattern = Pattern.compile(tosearch);
		Matcher matcher = pattern.matcher(model);
		String attribute = "null";
		while (matcher.find()) {
	        String matchp = matcher.group();
	        attribute = matchp.substring(matchp.lastIndexOf("\n  ")+3, matchp.lastIndexOf(")"));
		}
		return attribute;
	}
	/**
	 * Set the firewall auto-configurated rules in the XML according to the verifoo output
	 */
	public void setFirewallAutoConfig(){
		List<Node> autoNodes = g.getNode().stream().filter(n ->n.getFunctionalType().equals(FunctionalTypes.FIREWALL) && n.getConfiguration().getFirewall().getElements().isEmpty()).collect(Collectors.toList());
		List<String> nodes = g.getNode().stream().map(n -> n.getName()).collect(Collectors.toList());
		List<Elements> listOfRules = new ArrayList<>();
		autoNodes.forEach(n -> {
	        String defAction = firewallAutoConfigSearchAttribute(stringToSearchAction(n, "default_action"));
	        ActionTypes da = defAction.equals("true")? ActionTypes.ALLOW : ActionTypes.DENY;
			n.getConfiguration().getFirewall().setDefaultAction(da);
			System.out.println("Auto DEFAULT ACTION for " + n.getName() + " -> " + da);
			
			String tosearch="define-fun .*"+n.getName()+".*_auto_src.* Address\n  \\(ip_constructor .*\\)\\)";
			Pattern pattern = Pattern.compile(tosearch);
			Matcher matcher = pattern.matcher(model);
			while (matcher.find()) {
				Elements e = new Elements();
				e.setSource("");
				e.setDestination("");
		        String matchSrc = matcher.group();
		        String srcRule = matchSrc.substring(matchSrc.lastIndexOf("ip_constructor ")+15, matchSrc.lastIndexOf("))"));
		        tosearch="define-fun .* Address\n  \\(ip_constructor "+srcRule+"\\)\\)";
				Pattern patternNodeScr = Pattern.compile(tosearch);
				Matcher matcherNodeScr = patternNodeScr.matcher(model);
				String nodeSrcName = "";
				boolean srcFound = false;
				while(matcherNodeScr.find()) {
			        String match = matcherNodeScr.group();
			        String nodeSrc = match.substring(match.lastIndexOf("define-fun ")+11, match.lastIndexOf(" () Address"));
			        nodeSrc = nodeSrc.replace("|", "");
			        if(nodes.contains(nodeSrc)){
			        	//System.out.println("Found source node " + nodeSrc + " with address " + srcRule);
			        	nodeSrcName = nodeSrc;
			        	srcFound = true;
			        	break;
			        }
			    }
		        if(!srcFound){
		        	//System.out.println("Source Node with address " + srcRule + " not found");
		        	nodeSrcName = srcRule;
		        }
				nodeSrcName = nodeSrcName.replace("(", "").replace(")", "").replace("- ", "-").replace(" ", ".");
		        //System.out.println(nodeSrcName);
		        e.setSource(nodeSrcName);
		        String nrOfRule = matchSrc.substring(matchSrc.lastIndexOf("_src_")+5, matchSrc.lastIndexOf(" () Address"));
		        nrOfRule = nrOfRule.replace("|", "");
		        //System.out.println("Nr Of Rule: " + nrOfRule);
		        String nodeDstName = firewallAutoConfigSearchDst(n, nrOfRule);
		        //System.out.println(nodeDstName);
		        e.setDestination(nodeDstName);
		        
		        String start_src_port = firewallAutoConfigSearchAttribute(stringToSearchPort(n, nrOfRule, "start", "src"));
		        String end_src_port = firewallAutoConfigSearchAttribute(stringToSearchPort(n, nrOfRule, "end", "src"));
		        String src_port = translateFWRule(start_src_port, end_src_port);
		        //if(!src_port.equals("null") && !src_port.equals("*"))
		        	e.setSrcPort(src_port);
		        //System.out.println(e.getSrcPort());
		        
		        String start_dst_port = firewallAutoConfigSearchAttribute(stringToSearchPort(n, nrOfRule, "start", "dst"));
		        String end_dst_port = firewallAutoConfigSearchAttribute(stringToSearchPort(n, nrOfRule, "end", "dst"));
		        String dst_port = translateFWRule(start_dst_port,end_dst_port);
		        //if(!dst_port.equals("null") && !dst_port.equals("*"))
		        	e.setDstPort(dst_port);
		        //System.out.println(e.getDstPort());
		        
		        String action = firewallAutoConfigSearchAttribute(stringToSearchAction(n, "action"));
		        ActionTypes a = action.equals("true")? ActionTypes.ALLOW : ActionTypes.DENY;
		        e.setAction(a);
		        //System.out.println(e.getAction());
		        
		        String protocol = firewallAutoConfigSearchAttribute(stringToSearchProtocol(n, nrOfRule));
		        //if(!protocol.equals("null") && !L4ProtocolTypes.values()[Integer.parseInt(protocol)].equals(L4ProtocolTypes.ANY))
		        	e.setProtocol(L4ProtocolTypes.values()[Integer.parseInt(protocol)]);
		        //System.out.println(e.getAction());
				if(!e.getSource().equals("0.0.0.0") && !e.getDestination().equals("0.0.0.0")){
						System.out.println("Auto rule for " + n.getName() + " -> action: " + e.getAction() +
																				" src: " + e.getSource() +
																			    " dst: "+e.getDestination() + 
																				" "+ e.getProtocol()+
																				"["+ e.getSrcPort() +
																				":" + e.getDstPort()+"]");
						n.getConfiguration().getFirewall().getElements().add(e);
				}
		    }
			//List<Elements> finalRules = groupRules(listOfRules);
			//n.getConfiguration().getFirewall().getElements().addAll(finalRules);
		});
		
	}
	private List<Elements> groupRules(List<Elements> listOfRules) {
		Elements e1 = new Elements();
		e1.setAction(ActionTypes.DENY);
		e1.setSource("a");
		e1.setDestination("b");
		e1.setSrcPort("10");
		e1.setDstPort("80");
		e1.setProtocol(L4ProtocolTypes.TCP);
		
		Elements e2 = new Elements();
		e2.setAction(ActionTypes.DENY);
		e2.setSource("a");
		e2.setDestination("b");
		e2.setSrcPort("15");
		e2.setDstPort("70");
		e2.setProtocol(L4ProtocolTypes.UDP);
		
		Elements e3 = new Elements();
		e3.setAction(ActionTypes.DENY);
		e3.setSource("c");
		e3.setDestination("b");
		e3.setSrcPort("15");
		e3.setDstPort("87");
		e3.setProtocol(L4ProtocolTypes.TCP);
		
		listOfRules.clear();
		listOfRules.add(e1);
		listOfRules.add(e2);
		listOfRules.add(e3);
		HashMap<String, List<Elements>> finalrules = (HashMap<String, List<Elements>>) listOfRules.stream()
														.collect( Collectors.groupingBy(e -> e.getSource()+"_"+e.getDestination(), Collectors.toList()));
		for(Entry<String, List<Elements>> rule : finalrules.entrySet()){
			String src = rule.getKey().substring(0, rule.getKey().indexOf("_"));
			String dst = rule.getKey().substring(rule.getKey().indexOf("_")+1);
			boolean sameAction = true, sameProtocol = true;
			int minSrc = 65535, maxSrc = 0, minDst = 65535, maxDst = 0;
			ActionTypes a = rule.getValue().get(0).getAction();
			L4ProtocolTypes proto = rule.getValue().get(0).getProtocol();
			for(Elements e : rule.getValue()){
				sameAction = sameAction & a.equals(e.getAction());
				if(!sameAction) break;
				minSrc = Math.min(minSrc, Integer.parseInt(e.getSrcPort()));
				maxSrc = Math.max(maxSrc, Integer.parseInt(e.getSrcPort()));
				minDst = Math.min(minDst, Integer.parseInt(e.getDstPort()));
				maxDst = Math.max(maxDst, Integer.parseInt(e.getDstPort()));
				sameProtocol = sameProtocol & proto.equals(e.getProtocol());
			}
			if(sameAction){
				System.out.print("new rule => ");
				Elements newRule = new Elements();
				newRule.setSource(src);
				newRule.setDestination(dst);
				newRule.setSrcPort(minSrc+"-"+maxSrc);
				newRule.setDstPort(minDst+"-"+maxDst);
				if(sameProtocol)
					newRule.setProtocol(proto);
				else
					newRule.setProtocol(L4ProtocolTypes.ANY);
				rule.getValue().clear();
				rule.getValue().add(newRule);
				System.out.println(newRule.getProtocol() + "["+ src+":" + newRule.getSrcPort() +  " to " + dst + ": "+ newRule.getDstPort()+"]");
			}
			
		}
		
		
		return listOfRules;
	}
	
	private String translateFWRule(String start_src_port, String end_src_port){
		int start_src_portInt = Integer.parseInt(start_src_port), end_src_portInt = Integer.parseInt(end_src_port);
		if(start_src_portInt == 0 && end_src_portInt == 65535)
			return "*";
		if(start_src_portInt == end_src_portInt)
			return String.valueOf(start_src_portInt);
		return start_src_portInt+"-"+end_src_portInt;
	}
	/**
	 * Set the DPI auto-configurated rules in the XML according to the verifoo output
	 */
	public void setDPIAutoConfig(){
		List<Node> autoNodes = g.getNode().stream().filter(n ->n.getFunctionalType().equals(FunctionalTypes.DPI) && n.getConfiguration().getDpi().getNotAllowed().isEmpty())
												   .collect(Collectors.toList());
		List<String> bodies = g.getNode().stream().filter(n ->n.getFunctionalType().equals(FunctionalTypes.ENDHOST))
												.map(e -> e.getConfiguration().getEndhost().getBody())
												.collect(Collectors.toList());
		autoNodes.forEach(n -> {
			String tosearch="define-fun .*"+n.getName()+".*_auto_notAllowed_.* Int\n .*";
			Pattern pattern = Pattern.compile(tosearch);
			Matcher matcher = pattern.matcher(model);
			while (matcher.find()) {
				String match = matcher.group();
			    String hashCode = match.substring(match.lastIndexOf("\n")+3, match.lastIndexOf(")")); //+3 because the pattern it's like "\n  hashCode"
				String notAllowed = null;
			    for(String body : bodies){
					//System.out.println("BodyHashCode: " + String.valueOf(body).hashCode() + " RuleHashCode:" + Integer.parseInt(hashCode));
					if(String.valueOf(body).hashCode() == Integer.parseInt(hashCode)){
						notAllowed = body;
						break;
					}
				}
			    if(notAllowed != null){
					System.out.println("Auto rule for " + n.getName() + " -> notAllowed: " + notAllowed);
			    	n.getConfiguration().getDpi().getNotAllowed().add(notAllowed);
			    }
		    }
			
		});
	}
	/**
	 * Set the antispam auto-configurated rules in the XML according to the verifoo output
	 */
	public void setAntispamAutoConfig() {
		List<Node> autoNodes = g.getNode().stream().filter(n ->n.getFunctionalType().equals(FunctionalTypes.ANTISPAM) && n.getConfiguration().getAntispam().getSource().isEmpty())
				   .collect(Collectors.toList());
		List<String> emailFroms = g.getNode().stream().filter(n ->n.getFunctionalType().equals(FunctionalTypes.ENDHOST))
						.map(e -> e.getConfiguration().getEndhost().getEmailFrom())
						.collect(Collectors.toList());
		autoNodes.forEach(n -> {
			String tosearch="define-fun .*"+n.getName()+".*_auto_emailFrom_.* Int\n .*";
			Pattern pattern = Pattern.compile(tosearch);
			Matcher matcher = pattern.matcher(model);
			while (matcher.find()) {
				String match = matcher.group();
				String hashCode = match.substring(match.lastIndexOf("\n")+3, match.lastIndexOf(")")); //+3 because the pattern it's like "\n  hashCode"
				String emailFrom = null;
				for(String e : emailFroms){
					//System.out.println("BodyHashCode: " + String.valueOf(body).hashCode() + " RuleHashCode:" + Integer.parseInt(hashCode));
					if(String.valueOf(e).hashCode() == Integer.parseInt(hashCode)){
						emailFrom = e;
						break;
					}
				}
				if(emailFrom != null){
					System.out.println("Auto rule for " + n.getName() + " -> emailFrom: " + emailFrom);
					n.getConfiguration().getAntispam().getSource().add(emailFrom);
				}
			}
		
		});
		return;		
	}
	/**
	 * Reduces the host resources according to the node metrics
	 * @param h host
	 * @param nodeName
	 */
	public void allocateResources(Host h, String nodeName){
		NodeMetrics reqResources = nfv.getConstraints().getNodeConstraints()
										.getNodeMetrics().stream()
										.filter(nm -> nm.getNode().equals(nodeName))
										.findFirst().orElse(null);
		if(h.getMaxVNF() != null)
			h.setMaxVNF(h.getMaxVNF()-1);
		if(reqResources == null) return;
		h.setDiskStorage(h.getDiskStorage()-reqResources.getReqStorage());
		h.setMemory(h.getMemory()-reqResources.getMemory());
	}
	
}
