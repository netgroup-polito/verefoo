package it.polito.verifoo.rest.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import it.polito.verifoo.rest.jaxb.*;
/**
 * This class implements a parser for verifoo output (the z3 model), in order to translate it into the correct XML. This extension is needed to parse a model that include the BASIC autoconfiguraion 
 */
public class TranslatorBasic extends Translator {
	private VerifooNormalizer norm;
	/**
	 * Constructor
	 * @param model The Verifoo output.
	 * @param nfv The NFV model to complete.
	 * @param g 
	 */
	public TranslatorBasic(String model,NFV nfv, Graph g){
		super(model, nfv,g);
	}
	
	private String firewallAutoConfigSearchDst(Node n, String nrOfRule){
		List<String> nodes = g.getNode().stream().map(no -> no.getName()).collect(Collectors.toList());
		String tosearch=z3Translator.stringToSearchFwDestinationBasic(n, nrOfRule);
		Pattern patternDst = Pattern.compile(tosearch);
		Matcher matcherDst = patternDst.matcher(model);
		String nodeDstName = "";
		while (matcherDst.find()) {
	        String matchDst = matcherDst.group();
	        String dstRule = z3Translator.matchPlainAttribute(matchDst);
	        //System.out.println("///DstRule " + dstRule + "////");
	        //tosearch=z3Translator.stringToSearchAddress(dstRule);
			//Pattern patternNodeDst = Pattern.compile(tosearch);
			//Matcher matcherNodeDst = patternNodeDst.matcher(model);
			
			/*boolean dstFound = false;
			while(matcherNodeDst.find()) {
		        String match = matcherNodeDst.group();
		        String nodeDst = z3Translator.matchNodeName(match);
		        if(nodes.contains(nodeDst)){
		        	//System.out.println("Found dest node " + nodeDst + " with address " + dstRule);
		        	nodeDstName = nodeDst;
		        	dstFound = true;
		        	break;
		        }
		    }*/
	        //if(!dstFound){
	        	//System.out.println("Dest Node with address " + dstRule + " not found");
	        	nodeDstName = dstRule;
	        //}
	        
			nodeDstName = z3Translator.saneString(nodeDstName);
		}
		return nodeDstName;
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
			
			String tosearch=z3Translator.stringToSearchNodeBasic(n);
			Pattern pattern = Pattern.compile(tosearch);
			Matcher matcher = pattern.matcher(model);
			while (matcher.find()) {
				Elements e = new Elements();
				e.setSource("");
				e.setDestination("");
		        String matchSrc = matcher.group();
		        String srcRule = z3Translator.matchPlainAttribute(matchSrc);
		        //tosearch=z3Translator.stringToSearchAddress(srcRule);
				//Pattern patternNodeScr = Pattern.compile(tosearch);
				//Matcher matcherNodeScr = patternNodeScr.matcher(model);
				String nodeSrcName = "";
				//boolean srcFound = false;
				/*while(matcherNodeScr.find()) {
			        String match = matcherNodeScr.group();
			        String nodeSrc = z3Translator.matchNodeName(match);
			        if(nodes.contains(nodeSrc)){
			        	//System.out.println("Found source node " + nodeSrc + " with address " + srcRule);
			        	nodeSrcName = nodeSrc;
			        	srcFound = true;
			        	break;
			        }
			    }*/
		        //if(!srcFound){
		        	//System.out.println("Source Node with address " + srcRule + " not found");
		        	nodeSrcName = srcRule;
		        //}
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
			        /*String src_port = firewallAutoConfigSearchComplexAttribute(z3Translator.stringToSearchFwPort(n, nrOfRule, "src"), z3Translator.Datatype.port_range_constructor);
			        src_port = src_port.replace(" ", "-");
			        //if(!src_port.equals("null") && !src_port.equals("*"))
			        	e.setSrcPort((new PortInterval(src_port).toString()));
			        //System.out.println(e.getSrcPort());
			        
			        String dst_port = firewallAutoConfigSearchComplexAttribute(z3Translator.stringToSearchFwPort(n, nrOfRule, "dst"), z3Translator.Datatype.port_range_constructor);
			        dst_port = dst_port.replace(" ", "-");
			        //if(!dst_port.equals("null") && !dst_port.equals("*"))
			        	e.setDstPort((new PortInterval(dst_port).toString()));
			        //System.out.println(e.getDstPort());
			        String protocol = firewallAutoConfigSearchPlainAttribute(z3Translator.stringToSearchFwProtocol(n, nrOfRule));
			        //if(!protocol.equals("null") && !L4ProtocolTypes.values()[Integer.parseInt(protocol)].equals(L4ProtocolTypes.ANY))
			        	e.setProtocol(L4ProtocolTypes.values()[Integer.parseInt(protocol)]);
			        //System.out.println(e.getProtocol());
			        */
			        String action = firewallAutoConfigSearchPlainAttribute(z3Translator.stringToSearchFwAction(n, "action"));
			        ActionTypes a = action.equals("true")? ActionTypes.ALLOW : ActionTypes.DENY;
			        e.setAction(a);
			        //System.out.println(e.getAction());
			        
			        
					/*System.out.println("Auto rule for " + n.getName() + " -> action: " + e.getAction() +
																			" src: " + e.getSource() +
																		    " dst: "+e.getDestination());*/
					//listOfRules.add(e);
					n.getConfiguration().getFirewall().getElements().add(e);
				}
		    }
			//List<Elements> finalRules = mergeRules(listOfRules);
			//n.getConfiguration().getFirewall().getElements().addAll(finalRules);
		});
		
		
		
	}
	public VerifooNormalizer getNormalizer() {
		return norm;
	}
	public void setNormalizer(VerifooNormalizer norm) {
		this.norm = norm;
		this.originalNfv = norm.getOriginalNfv();
	}
	
}
