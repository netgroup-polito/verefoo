package it.polito.verifoo.rest.common;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;

import com.microsoft.z3.Expr;

import it.polito.verifoo.rest.jaxb.*;
import it.polito.verifoo.rest.jaxb.NodeConstraints.NodeMetrics;
import it.polito.verigraph.mcnet.components.NetworkObject;
import it.polito.verigraph.mcnet.components.PortInterval;
import it.polito.verigraph.mcnet.components.Tuple;
/**
 * This class implements a parser for verifoo output (the z3 model), in order to translate it into the correct XML 
 */
public class Translator {
	protected String model;
	protected NFV nfv;
	protected NFV originalNfv;
	protected org.apache.logging.log4j.Logger logger = LogManager.getLogger("mylog");
	protected Graph g;
	protected VerifooNormalizer norm;
	protected NodeNetworkObject netobjs;
	protected List<Node> removedNodes;
	/**
	 * Constructor
	 * @param model The Verifoo output.
	 * @param nfv The NFV model to complete.
	 * @param g the specific network service graph that is considered
	 */
	public Translator(String model,NFV nfv, Graph g){
		this.model=model;
		this.nfv=nfv;
		this.g = g;
	}
	/**
	 * Constructor
	 * @param model The Verifoo output.
	 * @param nfv The NFV model to complete.
	 * @param g the specific network service graph that is considered
	 * @param netobjs map containing all the network objects
	 * @param removedNodes optional nodes not used (TEMPORARY)
	 */
	public Translator(String model,NFV nfv, Graph g, NodeNetworkObject netobjs, List<Node> removedNodes){
		this.model=model;
		this.nfv=nfv;
		this.g = g;
		this.netobjs = netobjs;
		this.removedNodes = removedNodes;
	}
	/**
	 * Conversion function
	 * @return an NFV object that contains the new information retrieved in the z3 model
	 */
	public NFV convert(){
		if(originalNfv.getHosts() != null) 
			originalNfv.getHosts().getHost().forEach(this::searchHost);
		setAutoConfig();
		setAutoPlacement();
		//originalNfv.setHosts(nfv.getHosts());
		return originalNfv;
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
	 * Search in the model the deployed position of the nodes and updates the correspondent host object
	 * @param host Physical Host
	 */
	public void searchHost(Host host){
		List<String> nodesAlreadyDeployed = host.getNodeRef().stream().map(nr -> nr.getNode()).collect(Collectors.toList());
		g.getNode().forEach((node)->{
					String tosearch=z3Translator.stringToSearchDeploymentCondition(node, host);
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
					}
				});
	}
	/**
	 * Search for the destination of a specific auto-generated rule for a firewall
	 */
	private String firewallAutoConfigSearchDst(Node n, String nrOfRule){
		List<String> nodes = g.getNode().stream().map(no -> no.getName()).collect(Collectors.toList());
		String tosearch=z3Translator.stringToSearchFwDestination(n, nrOfRule);
		Pattern patternDst = Pattern.compile(tosearch);
		Matcher matcherDst = patternDst.matcher(model);
		String nodeDstName = "";
		while (matcherDst.find()) {
	        String matchDst = matcherDst.group();
	        String dstRule = z3Translator.matchComplexAttribute(matchDst, z3Translator.Datatype.ip_constructor);
	        //System.out.println("///DstRule " + dstRule + "////");
	        tosearch=z3Translator.stringToSearchAddress(dstRule);
			Pattern patternNodeDst = Pattern.compile(tosearch);
			Matcher matcherNodeDst = patternNodeDst.matcher(model);
			
			boolean dstFound = false;
			while(matcherNodeDst.find()) {
		        String match = matcherNodeDst.group();
		        String nodeDst = z3Translator.matchNodeName(match);
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
	        
			nodeDstName = z3Translator.saneString(nodeDstName);
		}
		return nodeDstName;
	}
	/**
	 * Generalize the pattern matching of a variable declared as a DatatypeSort in the z3 model
	 * @param tosearch the string to search
	 * @param datatype the type of the z3 variable
	 * @return the string that matches the pattern
	 */
	protected String firewallAutoConfigSearchComplexAttribute(String tosearch, z3Translator.Datatype datatype){
		Pattern pattern = Pattern.compile(tosearch);
		Matcher matcher = pattern.matcher(model);
		String attribute = "null";
		while (matcher.find()) {
	        String match = matcher.group();
	        attribute = z3Translator.matchComplexAttribute(match, datatype);
		}
		return attribute;
	}
	/**
	 *  Generalize the pattern matching of a variable declared as a primitive type (bool, int, etc) in the z3 model
	 */
	protected String firewallAutoConfigSearchPlainAttribute(String tosearch){
		Pattern pattern = Pattern.compile(tosearch);
		Matcher matcher = pattern.matcher(model);
		String attribute = "null";
		while (matcher.find()) {
	        String match = matcher.group();
	        attribute = z3Translator.matchPlainAttribute(match);
		}
		return attribute;
	}
	/**
	 * Set the firewall auto-configurated rules in the XML according to the verifoo output
	 */
	public void setFirewallAutoConfig(){
		//List<Node> autoNodes = g.getNode().stream().filter(n ->n.getFunctionalType().equals(FunctionalTypes.FIREWALL) && n.getConfiguration().getFirewall().getElements().isEmpty()).collect(Collectors.toList());
		List<Node> autoNodes = originalNfv.getGraphs().getGraph().stream().filter(graph -> graph.getId() == g.getId())
				.flatMap(graph -> graph.getNode().stream())
				.filter(n ->n.getFunctionalType().equals(FunctionalTypes.FIREWALL) && n.getConfiguration().getFirewall().getElements().isEmpty())
				.collect(Collectors.toList());List<String> nodes = g.getNode().stream().map(n -> n.getName()).collect(Collectors.toList());
		/*List<Node> originalAutoNodes = originalNfv.getGraphs().getGraph().stream()
						.flatMap(graph -> graph.getNode().stream())
						.filter(n -> autoNodes.stream().map(auN -> auN.getName()).collect(Collectors.toList()).contains(n.getName()))
						//.filter(n ->n.getFunctionalType().equals(FunctionalTypes.FIREWALL) && n.getConfiguration().getFirewall().getElements().isEmpty())
						.collect(Collectors.toList());List<String> nodes = g.getNode().stream().map(n -> n.getName()).collect(Collectors.toList());
		originalAutoNodes.forEach(n->{
			System.out.println(n.getName());
		});*/
		Map<String,String> nameToGroup = new HashMap<>();
		g.getNode().forEach(n -> {
			String name = n.getName();
			if(norm.getFlowGroups().containsKey(n.getName())){
				name = norm.getFlowGroups().get(n.getName());
				
			}
			if(norm.getNetworkGroups().containsKey(name))
				name = norm.getNetworkGroups().get(name);
			nameToGroup.put(n.getName(), name);
		});
		
		autoNodes.forEach(n -> {
			List<Elements> listOfRules = new ArrayList<>();
	        String defAction = firewallAutoConfigSearchPlainAttribute(z3Translator.stringToSearchFwAction(n, "default_action"));
	        ActionTypes da = defAction.equals("true")? ActionTypes.ALLOW : ActionTypes.DENY;
			n.getConfiguration().getFirewall().setDefaultAction(da);
			//System.out.println("Auto DEFAULT ACTION for " + n.getName() + " -> " + da);
			
			String tosearch=z3Translator.stringToSearchNode(n);
			Pattern pattern = Pattern.compile(tosearch);
			Matcher matcher = pattern.matcher(model);
			while (matcher.find()) {
				Elements e = new Elements();
				e.setSource("");
				e.setDestination("");
		        String matchSrc = matcher.group();
		        String srcRule = z3Translator.matchComplexAttribute(matchSrc, z3Translator.Datatype.ip_constructor);
		        tosearch=z3Translator.stringToSearchAddress(srcRule);
				Pattern patternNodeScr = Pattern.compile(tosearch);
				Matcher matcherNodeScr = patternNodeScr.matcher(model);
				String nodeSrcName = "";
				boolean srcFound = false;
				while(matcherNodeScr.find()) {
			        String match = matcherNodeScr.group();
			        String nodeSrc = z3Translator.matchNodeName(match);
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
				nodeSrcName = z3Translator.saneString(nodeSrcName);

		        if(nameToGroup.containsKey(nodeSrcName))
		        	nodeSrcName = nameToGroup.get(nodeSrcName);
		        //System.out.println(nodeSrcName);
		        e.setSource(nodeSrcName);
		        String nrOfRule = z3Translator.matchNrOfRule(matchSrc);
		        //System.out.println("Nr Of Rule: " + nrOfRule);
		        String nodeDstName = firewallAutoConfigSearchDst(n, nrOfRule);
		        //System.out.println(nodeDstName);
		        if(nameToGroup.containsKey(nodeDstName))
		        	nodeDstName = nameToGroup.get(nodeDstName);
		        e.setDestination(nodeDstName);

				if(!e.getSource().equals("0.0.0.0") && !e.getDestination().equals("0.0.0.0")){
			        String src_port = firewallAutoConfigSearchComplexAttribute(z3Translator.stringToSearchFwPort(n, nrOfRule, "src"), z3Translator.Datatype.port_range_constructor);
			        src_port = src_port.replace(" ", "-");
			        //if(!src_port.equals("null") && !src_port.equals("*"))
			        if(src_port.equals("null")) src_port = new String("*");
			        e.setSrcPort((new PortInterval(src_port).toString()));
			        //System.out.println(e.getSrcPort());
			        
			        String dst_port = firewallAutoConfigSearchComplexAttribute(z3Translator.stringToSearchFwPort(n, nrOfRule, "dst"), z3Translator.Datatype.port_range_constructor);
			        dst_port = dst_port.replace(" ", "-");
			        //if(!dst_port.equals("null") && !dst_port.equals("*"))
			        if(dst_port.equals("null")) dst_port = new String("*");
			        e.setDstPort((new PortInterval(dst_port).toString()));
			        //System.out.println(e.getDstPort());
			        
			        String action = firewallAutoConfigSearchPlainAttribute(z3Translator.stringToSearchFwAction(n, "action"));
			        ActionTypes a = action.equals("true")? ActionTypes.ALLOW : ActionTypes.DENY;
			        e.setAction(a);
			        //System.out.println(e.getAction());
			        
			        String protocol = firewallAutoConfigSearchPlainAttribute(z3Translator.stringToSearchFwProtocol(n, nrOfRule));
			        if(!protocol.equals("null") && Integer.parseInt(protocol) < 4 && !L4ProtocolTypes.values()[Integer.parseInt(protocol)].equals(L4ProtocolTypes.ANY))
			        	e.setProtocol(L4ProtocolTypes.values()[Integer.parseInt(protocol)]);
			        else
			        	e.setProtocol(L4ProtocolTypes.ANY);
			        //System.out.println(e.getAction());
					/*System.out.println("Auto rule for " + n.getName() + " -> action: " + e.getAction() +
																			" src: " + e.getSource() +
																		    " dst: "+e.getDestination() + 
																			" "+ e.getProtocol()+
																			"["+ e.getSrcPort() +
																			":" + e.getDstPort()+"]");*/
					listOfRules.add(e);
					//n.getConfiguration().getFirewall().getElements().add(e);
				}
		    }
			List<Elements> finalRules = mergeRules(listOfRules);
			n.getConfiguration().getFirewall().getElements().addAll(finalRules);
		});
		
		
		
	}
	/**
	 * Merge the auto-generated rule by overlapping port intervals, first by source port interval and the by destination port interval
	 * @param listOfRules list of rules to merge
	 * @return the list of firewall rules in an "elements" object
	 */
	private List<Elements> mergeRules(List<Elements> listOfRules) {
		/* TEST CASE
		Elements e1 = new Elements();
		e1.setSource("A");
		e1.setDestination("s1");
		e1.setSrcPort("7-20");
		e1.setDstPort("85-95");
		//e1.setProtocol(L4ProtocolTypes.ANY);
		
		Elements e2 = new Elements();
		e2.setSource("A");
		e2.setDestination("s1");
		e2.setSrcPort("10-15");
		e2.setDstPort("89-96");
		//e2.setProtocol(L4ProtocolTypes.ANY);
		
		Elements e3 = new Elements();
		e3.setSource("A");
		e3.setDestination("s1");
		e3.setSrcPort("12-30");
		e3.setDstPort("7-98");
		//e3.setProtocol(L4ProtocolTypes.ANY);
		
		Elements e4 = new Elements();
		e4.setSource("A");
		e4.setDestination("s1");
		e4.setSrcPort("11");
		e4.setDstPort("99");
		//e3.setProtocol(L4ProtocolTypes.ANY);
		
		listOfRules.clear();
		listOfRules.add(e2);
		listOfRules.add(e1);
		listOfRules.add(e3);
		listOfRules.add(e4);*/
		/*System.out.println("--------Input Rules-------");
		listOfRules.forEach(e -> {
			System.out.println("Rule -> action: " + e.getAction() +
					" src: " + e.getSource() +
				    " dst: "+e.getDestination() + 
					" "+ e.getProtocol()+
					"["+ e.getSrcPort() +
					":" + e.getDstPort()+"]");
		});
		System.out.println("------------------------");*/
		HashMap<String, List<Elements>> rulesMapBySource = (HashMap<String, List<Elements>>) listOfRules.stream()
														.map(e -> new Tuple<Elements,PortInterval>(e, new PortInterval(e.getSrcPort())))
														.sorted(Comparator.comparing(t -> t._2.getEnd()))
														.sorted(Comparator.comparing(t -> t._2.getStart()))
														.map(t -> t._1)
														.collect( Collectors.groupingBy(e -> e.getSource()+"_"+e.getDestination(), Collectors.toList()));
		List<Elements> finalRulesBySource = mergeRulesByMap(rulesMapBySource);
		/*System.out.println("-----Partial Result-----");
		finalRulesBySource.forEach(e -> {
			System.out.println("Rule -> action: " + e.getAction() +
					" src: " + e.getSource() +
				    " dst: "+e.getDestination() + 
					" "+ e.getProtocol()+
					"["+ e.getSrcPort() +
					":" + e.getDstPort()+"]");
		});
		System.out.println("------------------------");*/
		HashMap<String, List<Elements>> rulesMapByDest = (HashMap<String, List<Elements>>) finalRulesBySource.stream()
														.map(e -> new Tuple<Elements,PortInterval>(e, new PortInterval(e.getDstPort())))
														.sorted(Comparator.comparing(t -> t._2.getEnd()))
														.sorted(Comparator.comparing(t -> t._2.getStart()))
														.map(t -> t._1)
														.collect( Collectors.groupingBy(e -> e.getSource()+"_"+e.getDestination(), Collectors.toList()));
		List<Elements> finalRulesByDest = mergeRulesByMap(rulesMapByDest);
		/*System.out.println("------Rule After Merge------");
		finalRulesByDest.forEach(e -> {
			System.out.println("Rule -> action: " + e.getAction() +
					" src: " + e.getSource() +
				    " dst: "+e.getDestination() + 
					" "+ e.getProtocol()+
					"["+ e.getSrcPort() +
					":" + e.getDstPort()+"]");
		});
		System.out.println("----------------------------");*/
		return finalRulesByDest;
	}
	/**
	 * Merge the auto-generated rule by overlapping port intervals
	 * @param rulesMap a data structure of sorted rules
	 */
	private List<Elements> mergeRulesByMap(HashMap<String, List<Elements>> rulesMap) {
		List<Elements> finalRules = new ArrayList<>();
		for(Entry<String, List<Elements>> rule : rulesMap.entrySet()){
			String src = rule.getKey().substring(0, rule.getKey().indexOf("_"));
			String dst = rule.getKey().substring(rule.getKey().indexOf("_")+1);
			Elements last = rule.getValue().get(0);
			PortInterval intervalSrc = new PortInterval(last.getSrcPort()), intervalDst = new PortInterval(last.getDstPort());
			int minSrc = intervalSrc.getStart(), maxSrc = intervalSrc.getEnd(), minDst = intervalDst.getStart(), maxDst = intervalDst.getEnd();
			boolean overlap = false;
			for(int i = 1; i < rule.getValue().size(); i++){
				Elements e = rule.getValue().get(i);
				PortInterval lastSrcInterval = new PortInterval(last.getSrcPort()), currentSrcInterval = new PortInterval(e.getSrcPort());
				overlap = lastSrcInterval.overlapsWith(currentSrcInterval);
				if(!overlap){
					//System.out.println("No source overlapping");
					/*System.out.println("Rule merged-> action: " + e.getAction() +
							" src: " + last.getSource() +
						    " dst: "+last.getDestination() + 
							" "+ last.getProtocol()+
							"["+ last.getSrcPort() +
							":" + last.getDstPort()+"]");*/
					finalRules.add(last);
					last = e;
					continue;
				}
				PortInterval lastDstInterval = new PortInterval(last.getDstPort()), currentDstInterval = new PortInterval(e.getDstPort());
				overlap &= lastDstInterval.overlapsWith(currentDstInterval);
				if(!overlap){
					//System.out.println("No destination overlapping");
					/*System.out.println("Rule merged-> action: " + last.getAction() +
							" src: " + last.getSource() +
						    " dst: "+last.getDestination() + 
							" "+ last.getProtocol()+
							"["+ last.getSrcPort() +
							":" + last.getDstPort()+"]");*/
					finalRules.add(last);
					last = e;
					continue;
				}
				overlap &= (last.getProtocol().equals(e.getProtocol()) || last.getProtocol().equals(L4ProtocolTypes.ANY) || e.getProtocol().equals(L4ProtocolTypes.ANY));
				if(!overlap){
					System.out.println("No protocol overlapping");
					/*System.out.println("Rule merged-> action: " + last.getAction() +
							" src: " + last.getSource() +
						    " dst: "+last.getDestination() + 
							" "+ last.getProtocol()+
							"["+ last.getSrcPort() +
							":" + last.getDstPort()+"]");*/
					finalRules.add(last);
					last = e;
					continue;
				}
				//System.out.println("Overlapping");
				minSrc = Math.min(lastSrcInterval.getStart(), currentSrcInterval.getStart());
				maxSrc = Math.max(lastSrcInterval.getEnd(), currentSrcInterval.getEnd());
				minDst = Math.min(lastDstInterval.getStart(), currentDstInterval.getStart());
				maxDst = Math.max(lastDstInterval.getEnd(), currentDstInterval.getEnd());

				Elements newRule = new Elements();
				newRule.setAction(last.getAction());
				newRule.setSource(src);
				newRule.setDestination(dst);
				newRule.setSrcPort((new PortInterval(minSrc+"-"+maxSrc)).toString());
				newRule.setDstPort((new PortInterval(minDst+"-"+maxDst)).toString());

				if(last.getProtocol().equals(e.getProtocol())){
					newRule.setProtocol(last.getProtocol());
				}else{
					newRule.setProtocol(L4ProtocolTypes.ANY);
				}
				//finalRules.add(newRule);
				//System.out.print("new rule => ");
				//System.out.println(newRule.getProtocol() + "["+ src+":" + newRule.getSrcPort() +  " to " + dst + ": "+ newRule.getDstPort()+"]");
				last = newRule;
				//sameProtocol = sameProtocol & proto.equals(e.getProtocol());
			}
			/*System.out.println("Rule merged-> action: " + last.getAction() +
					" src: " + last.getSource() +
				    " dst: "+last.getDestination() + 
					" "+ last.getProtocol()+
					"["+ last.getSrcPort() +
					":" + last.getDstPort()+"]");*/
			finalRules.add(last);
		}
		return finalRules;
	}
	
	/**
	 * Set the DPI auto-configurated rules in the XML for the verifoo output
	 */
	public void setDPIAutoConfig(){
		//List<Node> autoNodes = g.getNode().stream().filter(n ->n.getFunctionalType().equals(FunctionalTypes.DPI) && n.getConfiguration().getDpi().getNotAllowed().isEmpty()).collect(Collectors.toList());
		List<Node> autoNodes = originalNfv.getGraphs().getGraph().stream().filter(graph -> graph.getId() == g.getId())
				.flatMap(graph -> graph.getNode().stream())
				.filter(n ->n.getFunctionalType().equals(FunctionalTypes.DPI) && n.getConfiguration().getDpi().getNotAllowed().isEmpty())
				.collect(Collectors.toList());List<String> nodes = g.getNode().stream().map(n -> n.getName()).collect(Collectors.toList());
		
		List<String> bodies = g.getNode().stream().filter(n ->n.getFunctionalType().equals(FunctionalTypes.ENDHOST))
												.map(e -> e.getConfiguration().getEndhost().getBody())
												.collect(Collectors.toList());
		autoNodes.forEach(n -> {
			String tosearch=z3Translator.stringToSearchDpiNotAllowed(n);
			Pattern pattern = Pattern.compile(tosearch);
			Matcher matcher = pattern.matcher(model);
			while (matcher.find()) {
				String match = matcher.group();
			    String hashCode = z3Translator.matchPlainAttribute(match); 
				String notAllowed = null;
			    for(String body : bodies){
					//System.out.println("BodyHashCode: " + String.valueOf(body).hashCode() + " RuleHashCode:" + Integer.parseInt(hashCode));
					if(String.valueOf(body).hashCode() == Integer.parseInt(hashCode)){
						notAllowed = body;
						break;
					}
				}
			    if(notAllowed != null){
					//System.out.println("Auto rule for " + n.getName() + " -> notAllowed: " + notAllowed);
			    	n.getConfiguration().getDpi().getNotAllowed().add(notAllowed);
			    }
		    }
			
		});
	}
	/**
	 * Set the antispam auto-configurated rules in the XML for the verifoo output
	 */
	public void setAntispamAutoConfig() {
		//List<Node> autoNodes = g.getNode().stream().filter(n ->n.getFunctionalType().equals(FunctionalTypes.ANTISPAM) && n.getConfiguration().getAntispam().getSource().isEmpty()).collect(Collectors.toList());
		List<Node> autoNodes = originalNfv.getGraphs().getGraph().stream().filter(graph -> graph.getId() == g.getId())
				.flatMap(graph -> graph.getNode().stream())
				.filter(n ->n.getFunctionalType().equals(FunctionalTypes.ANTISPAM) && n.getConfiguration().getAntispam().getSource().isEmpty())
				.collect(Collectors.toList());List<String> nodes = g.getNode().stream().map(n -> n.getName()).collect(Collectors.toList());
		List<String> emailFroms = g.getNode().stream().filter(n ->n.getFunctionalType().equals(FunctionalTypes.ENDHOST))
						.map(e -> e.getConfiguration().getEndhost().getEmailFrom())
						.collect(Collectors.toList());
		autoNodes.forEach(n -> {
			String tosearch=z3Translator.stringToSearchAntispamEmailFrom(n);
			Pattern pattern = Pattern.compile(tosearch);
			Matcher matcher = pattern.matcher(model);
			while (matcher.find()) {
				String match = matcher.group();
				String hashCode = z3Translator.matchPlainAttribute(match); 
				String emailFrom = null;
				for(String e : emailFroms){
					//System.out.println("BodyHashCode: " + String.valueOf(body).hashCode() + " RuleHashCode:" + Integer.parseInt(hashCode));
					if(String.valueOf(e).hashCode() == Integer.parseInt(hashCode)){
						emailFrom = e;
						break;
					}
				}
				if(emailFrom != null){
					//System.out.println("Auto rule for " + n.getName() + " -> emailFrom: " + emailFrom);
					n.getConfiguration().getAntispam().getSource().add(emailFrom);
				}
			}
		
		});
		return;		
	}
	
	
	/**
	 * Remove not used optional network objects from the XML for the verifoo output
	 */
	public void setAutoPlacement() {
		
		List<NodeMetrics> nodeMetrics = originalNfv.getConstraints().getNodeConstraints().getNodeMetrics().stream().collect(Collectors.toList());
		
		List<Node> optionalNodes = originalNfv.getGraphs().getGraph().stream().filter(graph -> graph.getId() == g.getId())
				.flatMap(graph -> graph.getNode().stream())
				.filter(n -> { 
					for(NodeMetrics nm : nodeMetrics) {
						if(n.getName().equals(nm.getNode()))
							return true;
					}
					return false;
				})
				.collect(Collectors.toList());
		
		optionalNodes.forEach(opNode -> {
			String tosearch = z3Translator.stringToSeachNetworkObjectUsed(opNode);
			Pattern pattern = Pattern.compile(tosearch);
			Matcher matcher = pattern.matcher(model);
			while(matcher.find()) {
				Node n = originalNfv.getGraphs().getGraph().stream()
						.filter(graph -> graph.getId() == g.getId())
						.flatMap(graph -> graph.getNode().stream())
						.filter(node -> node.getName().equals(opNode.getName())).findFirst().orElse(null);
				NetworkObject no = netobjs.entrySet().stream().filter(e -> e.getKey().getName().equals(n.getName())).map(e -> e.getValue()).findFirst().orElse(null);
				Map<Expr, Set<Expr>> nodesFrom = no.nodesFrom;
	
				for(Map.Entry<Expr, Set<Expr>> entry : nodesFrom.entrySet()) {
					String precName = netobjs.entrySet().stream().filter(e -> e.getValue().getZ3Node().equals(entry.getKey())).map(e -> e.getKey().getName()).findFirst().orElse(null);
					Node prec = originalNfv.getGraphs().getGraph().stream()
							.filter(graph -> graph.getId() == g.getId())
							.flatMap(graph -> graph.getNode().stream())
							.filter(node -> node.getName().equals(precName)).findFirst().orElse(null);
					List<Neighbour> neighboursPrec = prec.getNeighbour();
					//System.out.println(n.getName() + " " + entry.getKey() + " " + entry.getValue());
					for(Expr exprDest : entry.getValue()) {
						String nextName = netobjs.entrySet().stream().filter(e -> e.getValue().getZ3Node().equals(exprDest)).map(e -> e.getKey().getName()).findFirst().orElse(null);
						Node next = originalNfv.getGraphs().getGraph().stream()
								.filter(graph -> graph.getId() == g.getId())
								.flatMap(graph -> graph.getNode().stream())
								.filter(node -> node.getName().equals(nextName)).findFirst().orElse(null);
						List<Neighbour> neighboursNext =  next.getNeighbour();
						neighboursPrec.removeIf(neigh -> neigh.getName().equals(n.getName()));
						neighboursNext.removeIf(neigh -> neigh.getName().equals(n.getName()));
						NetworkObject prevNO = netobjs.entrySet().stream().filter(e -> e.getKey().getName().equals(prec.getName())).map(e -> e.getValue()).findFirst().orElse(null);
						NetworkObject nextNO = netobjs.entrySet().stream().filter(e -> e.getKey().getName().equals(next.getName())).map(e -> e.getValue()).findFirst().orElse(null);
						no.nodesTo.get(nextNO.getZ3Node()).removeIf(el -> el.equals(prevNO.getZ3Node()));
						boolean presentNext = neighboursPrec.stream().anyMatch(neigh -> neigh.getName().equals(next.getName()));
						if(!presentNext) {
							Neighbour neigh = new Neighbour();
							neigh.setName(next.getName());
							neighboursPrec.add(neigh);
							Map<Expr, Set<Expr>> precNodesFrom = prevNO.nodesFrom;
							Map<Expr, Set<Expr>> precNodesTo = prevNO.nodesTo;
							for(Map.Entry<Expr, Set<Expr>> e : precNodesFrom.entrySet()) {
								if(e.getValue().contains(no.getZ3Node())) {
									e.getValue().remove(no.getZ3Node());
									e.getValue().add(nextNO.getZ3Node());
								}
								
							}
							Set<Expr> toSet = precNodesTo.get(no.getZ3Node());
							precNodesTo.remove(no.getZ3Node());
							precNodesTo.put(nextNO.getZ3Node(), toSet);
						}
						boolean presentPrec = neighboursNext.stream().anyMatch(neigh -> neigh.getName().equals(prec.getName()));
						if(!presentPrec) {
							Neighbour neigh = new Neighbour();
							neigh.setName(prec.getName());
							neighboursNext.add(neigh);
							Map<Expr, Set<Expr>> nextNodesFrom = nextNO.nodesFrom;
							Map<Expr, Set<Expr>> nextNodesTo = nextNO.nodesTo;
							for(Map.Entry<Expr, Set<Expr>> e : nextNodesTo.entrySet()) {
								if(e.getValue().contains(no.getZ3Node())) {
									e.getValue().remove(no.getZ3Node());
									e.getValue().add(prevNO.getZ3Node());
								}
							}
							Set<Expr> fromSet = nextNodesFrom.get(no.getZ3Node());
							if(no.nodesTo.get(nextNO.getZ3Node()).isEmpty()) nextNodesFrom.remove(no.getZ3Node());
							nextNodesFrom.put(prevNO.getZ3Node(), fromSet);
						}

					}
				}
				Graph graphWithOptional = originalNfv.getGraphs().getGraph().stream()
						.filter(graph -> graph.getId() == g.getId())
						.findFirst().orElse(null);
				List<Node> allNodes = graphWithOptional.getNode();
				allNodes.removeIf(node -> node.getName().equals(n.getName()));
				removedNodes.add(n);
			}
			
			
			
		});
		
		
		
	}
	
	
	/**
	 * Reduces the host resources according to the node metrics
	 * @param h the host
	 * @param nodeName the name of the node that is deployed on the host
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
	/**
	 * Get the normalized version of the received service graph
	 * @return the normalized version of the received service graph
	 */
	public VerifooNormalizer getNormalizer() {
		return norm;
	}
	/**
	 * Set the normalized version of the received service graph
	 * @param norm the normalized version of the received service graph
	 */
	public void setNormalizer(VerifooNormalizer norm) {
		this.norm = norm;
		this.originalNfv = norm.getOriginalNfv();
	}
	
}
