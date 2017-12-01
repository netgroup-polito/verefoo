package it.polito.verifoo.rest.common;

import org.apache.logging.log4j.LogManager;

import it.polito.verifoo.rest.jaxb.*;
/**
 * This class implements a parser for verifoo output for find on witch host verifoo deploy a node 
 */
public class Translator {
	private String model;
	private NFV nfv;
	private org.apache.logging.log4j.Logger logger = LogManager.getLogger("mylog");
	/**
	 * Costructor
	 * @param model The Verifoo output.
	 * @param nfv The NFV model to complete.
	 */
	public Translator(String model,NFV nfv){
		this.model=model;
		this.nfv=nfv;
	}
	/**
	 * Conversion function
	 */
	public void convert(){
		nfv.getHosts().getHost().forEach(this::searchHost);
	}
	/**
	 * For each host we search for all possible configuration of nodes(at)host and check if Bool value is true
	 * Then we set the element in the schema accordling.
	 * @param host Phisical Host
	 */
	public void searchHost(Host host){
		nfv.getGraphs().getGraph().forEach((g)->g.getNode().forEach((node)->{
					String tosearch="(define-fun "+node.getName()+"@"+host.getName()+" () Bool\n  true)";
					if(model.contains(tosearch)){
						logger.debug(tosearch);
						host.setActive(true);
						NodeRefType nr=new NodeRefType();
						nr.setNode(node.getName());
						host.getNodeRef().add(nr);
					}
				}
		));
	}
	
}
