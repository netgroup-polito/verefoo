package it.polito.verifoo.rest.common;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.ws.rs.NotAllowedException;

import org.apache.logging.log4j.LogManager;

import it.polito.verifoo.rest.jaxb.*;
import it.polito.verifoo.rest.jaxb.NodeConstraints.NodeMetrics;
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
					while (matcher.find()) {
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
	private String firewallAutoConfigSearchSrcPort(Node n, String nrOfRule){
		String tosearch="define-fun .*"+n.getName()+".*_auto_srcp_"+nrOfRule+".* Int\n  .*\\)";
		Pattern pattern = Pattern.compile(tosearch);
		Matcher matcher = pattern.matcher(model);
		String src_portRule = "null";
		while (matcher.find()) {
	        String matchSrcp = matcher.group();
	        src_portRule = matchSrcp.substring(matchSrcp.lastIndexOf("\n  ")+3, matchSrcp.lastIndexOf(")"));
	        //System.out.println("///SrcPRule " + dstRule + "////");
		}
		return src_portRule;
	}
	private String firewallAutoConfigSearchDstPort(Node n, String nrOfRule){
		String tosearch="define-fun .*"+n.getName()+".*_auto_dstp_"+nrOfRule+".* Int\n  .*\\)";
		Pattern pattern = Pattern.compile(tosearch);
		Matcher matcher = pattern.matcher(model);
		String dst_portRule = "null";
		while (matcher.find()) {
	        String matchDstp = matcher.group();
	        dst_portRule = matchDstp.substring(matchDstp.lastIndexOf("\n  ")+3, matchDstp.lastIndexOf(")"));
	        //System.out.println("///DstPRule " + dstRule + "////");
		}
		return dst_portRule;
	}
	/**
	 * Set the firewall auto-configurated rules in the XML according to the verifoo output
	 */
	public void setFirewallAutoConfig(){
		List<Node> autoNodes = g.getNode().stream().filter(n ->n.getFunctionalType().equals(FunctionalTypes.FIREWALL) && n.getConfiguration().getFirewall().getElements().isEmpty()).collect(Collectors.toList());
		List<String> nodes = g.getNode().stream().map(n -> n.getName()).collect(Collectors.toList());
		autoNodes.forEach(n -> {
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
		        String src_port = firewallAutoConfigSearchSrcPort(n, nrOfRule);
		        //System.out.println(src_port);
		        e.setSrcPort(Integer.parseInt(src_port));
		        String dst_port = firewallAutoConfigSearchDstPort(n, nrOfRule);
		        //System.out.println(dst_port);
		        e.setDstPort(Integer.parseInt(dst_port));
				if(!e.getSource().equals("0.0.0.0") && !e.getDestination().equals("0.0.0.0")){
						System.out.println("Auto rule for " + n.getName() + " -> src: " + e.getSource() +
																				" dst: "+e.getDestination() + 
																				" src_p: " + e.getSrcPort() + 
																				" dst_p: " + e.getDstPort());
						n.getConfiguration().getFirewall().getElements().add(e);
				}
		    }
			
		});
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
