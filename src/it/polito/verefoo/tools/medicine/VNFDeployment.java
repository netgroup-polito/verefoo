package it.polito.verefoo.tools.medicine;

import java.util.List;

import it.polito.verefoo.jaxb.Host;
import it.polito.verefoo.jaxb.NodeRefType;

/**
 * This class creates the custom placement file that SONATA will read
 * @author Antonio
 *
 */
public class VNFDeployment {
	String placement;
	private List<Host> hosts;
	/**
	 * Create the custom placement based on the output of Verifoo
	 * @param hosts
	 */
	public VNFDeployment(List<Host> hosts) {
		this.hosts = hosts;
		CreatePlacement();
	}

	private void CreatePlacement(){
		placement = "class CustomPlacement(object):\n"
							+"\tdef place(self, nsd, vnfds, saps, dcs):\n";
		int nE = 0;
		for(Host h:hosts){
			for(NodeRefType n : h.getNodeRef()){
				placement += "\t\tvnfds['"+n.getNode().toLowerCase()+"'][\"dc\"] = dcs['"+h.getName()+"']\n";
			}
			if(h.getFixedEndpoint() != null){
				placement += "\t\tvnfds['"+h.getFixedEndpoint().toLowerCase()+"'][\"dc\"] = dcs['"+h.getName()+"']\n";
				placement +="\t\tsaps['ns_ext" + nE + "'][\"dc\"] = dcs['"+h.getName()+"']\n";
				nE++;
			}
		}

	}
	/**
	 * @return the custom placement string that represent the python file to instruct SONATA about the placement
	 */
	public String getPlacementDescription(){
		return placement;
	}
}
