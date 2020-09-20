package it.polito.verefoo.firewall;

import static java.util.stream.Collectors.toList;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


import com.microsoft.z3.Status;

import it.polito.verefoo.allocation.AllocationGraphGenerator;
import it.polito.verefoo.extra.BadGraphError;
import it.polito.verefoo.jaxb.EType;
import it.polito.verefoo.jaxb.FunctionalTypes;
import it.polito.verefoo.jaxb.Graph;
import it.polito.verefoo.jaxb.NFV;
import it.polito.verefoo.jaxb.Node;
import it.polito.verefoo.jaxb.Path;
import it.polito.verefoo.jaxb.Property;
import it.polito.verefoo.translator.Translator;
import it.polito.verefoo.utils.VerificationResult;


public class FirewallSerializer {
	private NFV nfv;
	
	//private List<Fortinet> Firewalls= new LinkedList<>();
	private List<IpFirewall> Firewalls= new LinkedList<>();
	//private List<BPFFirewall> Firewalls= new LinkedList<>();
//	int time = 0;
//
//	public int getTime() {
//		return time;
//	}
//
//	public void setTime(int time) {
//		this.time = time;
//	}

	public FirewallSerializer(NFV root) {
		this.nfv = root;
		
		root.getGraphs().getGraph().forEach((g) -> {
			List<Node> nodes;
			nodes = g.getNode().stream()
					.filter(n -> n.getFunctionalType().equals(FunctionalTypes.FIREWALL)
							//|| n.getFunctionalType().equals(FunctionalTypes.PRIORITY_FIREWALL)
							//|| n.getFunctionalType().equals(FunctionalTypes.STATEFUL_FIREWALL)
							)
					.collect(toList());
			if(nodes.isEmpty())
				throw new BadGraphError("You specified a network ("+g.getId()+") that contains no firewalls");
			
			//create new firewall
			long id=0;
			for(int index =0;index<nodes.size();index++) {
				try {
					
					Firewalls.add(new IpFirewall(++id,nodes.get(index)));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}	
			}
			
		});
		
	}

	public NFV getNfv() {
		return nfv;
	}




	public Graph getGraphById(long id) {
		return nfv.getGraphs().getGraph().stream().filter(g -> g.getId() == id).findFirst().orElse(null);
	}

}
