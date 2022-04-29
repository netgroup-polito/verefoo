package it.polito.verefoo.firewall;

import static java.util.stream.Collectors.toList;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.microsoft.z3.Status;

//import it.polito.verefoo.extra.BadGraphError;
//import it.polito.verefoo.extra.BadGraphError;
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
	// private List<Fortinet> Firewalls= new LinkedList<>();
	// private List<IpFirewall> Firewalls= new LinkedList<>();
	// private List<BPFFirewall> Firewalls= new LinkedList<>();
	// private List<OpenvSwitch> Firewalls= new LinkedList<>();
	// private List<Iptables> Firewalls= new LinkedList<>();
	private boolean IsFortinet = false, IsIPFW = false, IsIptables = false, IsOpenvSwitch = false, IsBPF = false;
	// int time = 0;
	//
	// public int getTime() {
	// return time;
	// }
	//
	// public void setTime(int time) {
	// this.time = time;
	// }

	public FirewallSerializer(NFV root, FirewallDeploy type) {

		switch (type) {
		case FORTINET:
			IsFortinet = true;
			break; // added break in order for the switch case work properly
		case OPENVSWITCH:
			IsOpenvSwitch = true;
			break;
		case IPFIREWALL:
			IsIPFW = true;
			break;
		case IPTABLES:
			IsIptables = true;
			break;
		case EBPF:
			IsBPF = true;
			break;
		case ALL:
			//IsFortinet = true; // Not Relevant for the Containerized virtual environment testing
			IsOpenvSwitch = true;
			//IsIPFW = true; // Not Relevant for the Containerized virtual environment
			IsIptables = true;
			IsBPF = true;
		}

		this.nfv = root;

		deploy();

	}

	public NFV getNfv() {
		return nfv;
	}

	public void deploy() {
		nfv.getGraphs().getGraph().forEach((g) -> {
			List<Node> nodes;
			nodes = g.getNode().stream().filter(n -> n.getFunctionalType().equals(FunctionalTypes.FIREWALL)
			// || n.getFunctionalType().equals(FunctionalTypes.PRIORITY_FIREWALL)
			// || n.getFunctionalType().equals(FunctionalTypes.STATEFUL_FIREWALL)
			).collect(toList());
			if (nodes.isEmpty()) {
				try {
					throw new Exception();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					 System.out.println("Firewall List Empty");
					e.printStackTrace();
				}
			}

			// create new firewall
			long id = 0;
			for (int index = 0; index < nodes.size(); index++) {
				try {
					id++;
					if (IsFortinet)
						new Fortinet(id, nodes.get(index));

					if (IsOpenvSwitch)
						new OpenvSwitch(id, nodes.get(index));
					if (IsIPFW)
						new IpFirewall(id, nodes.get(index));
					if (IsIptables)
						new Iptables(id, nodes.get(index));
					if (IsBPF)
						new BPFFirewall(id, nodes.get(index));

					// Firewalls.add(new Iptables(++id,nodes.get(index)));

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		});
	}

	public void changeFirewallDeploy(FirewallDeploy type) {
		IsFortinet = false;
		IsOpenvSwitch = false;
		IsIPFW = false;
		IsIptables = false;
		IsBPF = false;
		switch (type) {
		case FORTINET:
			IsFortinet = true;
		case OPENVSWITCH:
			IsOpenvSwitch = true;
		case IPFIREWALL:
			IsIPFW = true;
		case IPTABLES:
			IsIptables = true;
		case EBPF:
			IsBPF = true;
		case ALL:
			IsFortinet = true;
			IsOpenvSwitch = true;
			IsIPFW = true;
			IsIptables = true;
			IsBPF = true;
		}
	}

	public Graph getGraphById(long id) {
		return nfv.getGraphs().getGraph().stream().filter(g -> g.getId() == id).findFirst().orElse(null);
	}

}
