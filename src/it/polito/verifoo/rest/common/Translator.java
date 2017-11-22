package it.polito.verifoo.rest.common;

import it.polito.verifoo.rest.jaxb.*;

public class Translator {
	private String model;
	private NFV nfv;
	public Translator(String model,NFV nfv){
		this.model=model;
		this.nfv=nfv;
	}
	public void convert(){
		nfv.getHosts().getHost().forEach(this::searchHost);
	}
	public void searchHost(Host host){
		nfv.getNFFG().getNode().forEach((node)->{
					String tosearch="(define-fun "+node.getName()+"@"+host.getName()+" () Bool\n  true)";
					if(model.contains(tosearch)){
						System.out.println(tosearch);
						host.setActive(true);
						NodeRefType nr=new NodeRefType();
						nr.setNode(node.getName());
						host.getNodeRef().add(nr);
					}
				}
		);
	}
	
}
