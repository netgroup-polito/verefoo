package it.polito.verifoo.rest.common;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;

import it.polito.verifoo.rest.jaxb.*;
import it.polito.verifoo.rest.jaxb.NodeConstraints.NodeMetrics;
/**
 * This class implements a parser for verifoo output for find on witch host verifoo deploy a node 
 */
public class Translator {
	private String model;
	private NFV nfv;
	private org.apache.logging.log4j.Logger logger = LogManager.getLogger("mylog");
	private Graph g;
	/**
	 * Costructor
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
		nfv.getHosts().getHost().forEach(this::searchHost);
		setFirewallAutoConfig();
	}
	/**
	 * For each host we search for all possible configuration of nodes(at)host and check if Int value is 1
	 * Then we set the element in the schema accordingly.
	 * @param host Physical Host
	 */
	public void searchHost(Host host){
		List<String> nodesAlreadyDeployed = host.getNodeRef().stream().map(nr -> nr.getNode()).collect(Collectors.toList());
		g.getNode().forEach((node)->{
					String tosearch="define-fun integer_"+node.getName()+"@"+host.getName()+" () Int\n  1)";
					if(model.contains(tosearch)){
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
	
	
	public void setFirewallAutoConfig(){
		List<Node> autoNodes = g.getNode().stream().filter(n ->n.getFunctionalType().equals(FunctionalTypes.FIREWALL) && n.getConfiguration().getFirewall().getElements().isEmpty()).collect(Collectors.toList());
		
		autoNodes.forEach(n -> {
			String tosearch="define-fun "+n.getName()+"_auto_src.* Address\n .*";
			Pattern pattern = Pattern.compile(tosearch);
			Matcher matcher = pattern.matcher(model);
			while (matcher.find()) {
				Elements e = new Elements();
				e.setSource("");
				e.setDestination("");
		        String match = matcher.group();
		        String scrRule = match.substring(match.lastIndexOf("\n")+3, match.lastIndexOf(")")); //+3 because the pattern it's like "\n  node1"
		        e.setSource(scrRule);
		        String nrOfRule = match.substring(match.lastIndexOf("_src_")+5, match.lastIndexOf(" () Address"));
		        tosearch="define-fun "+n.getName()+"_auto_dst_"+nrOfRule+".* Address\n .*";
				Pattern patternDst = Pattern.compile(tosearch);
				Matcher matcherDst = patternDst.matcher(model);
				while (matcherDst.find()) {
			        match = matcherDst.group();
			        String dstRule = match.substring(match.lastIndexOf("\n")+3, match.lastIndexOf(")")); //+3 because the pattern it's like "\n  node1"
			        e.setDestination(dstRule);
			    }
				if(!e.getSource().equals("null") && !e.getDestination().equals("null")){
					System.out.println("Auto rule for " + n.getName() + " -> src: " + e.getSource()+" dst: "+e.getDestination());
					n.getConfiguration().getFirewall().getElements().add(e);
				}
		    }
			
		});
	}
	/**
	 * Reduce the host resources according to the node metrics
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
