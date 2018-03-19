package it.polito.verifoo.rest.common;

import java.util.List;
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
	}
	/**
	 * For each host we search for all possible configuration of nodes(at)host and check if Int value is 1
	 * Then we set the element in the schema accordingly.
	 * @param host Physical Host
	 */
	public void searchHost(Host host){
		List<String> nodeAlreadyDeployed = host.getNodeRef().stream().map(nr -> nr.getNode()).collect(Collectors.toList());
		g.getNode().forEach((node)->{
					String tosearch="define-fun integer_"+node.getName()+"@"+host.getName()+" () Int\n  1)";
					if(model.contains(tosearch)){
						logger.debug(tosearch);
						host.setActive(true);
						NodeRefType nr=new NodeRefType();
						nr.setNode(node.getName());
						if(!nodeAlreadyDeployed.contains(node.getName())){
							host.getNodeRef().add(nr);
							allocateResources(host, node.getName());
						}
					}
				});
	}
	
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
