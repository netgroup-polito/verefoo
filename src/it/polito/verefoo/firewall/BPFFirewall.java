package it.polito.verefoo.firewall;

import java.io.File;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import it.polito.verefoo.jaxb.ActionTypes;
import it.polito.verefoo.jaxb.Elements;
import it.polito.verefoo.jaxb.Node;

//example rule
//set default
//								 name			   ACCEPT
//polycubectl pcn-iptables chain INPUT set default=DROP

//flush
//								 name
//polycubectl pcn-iptables chain INPUT rule del

//rule
//									    delete
//								  name  insert id=0									     udp										  DENY
// polycubectl pcn-iptables chain INPUT append src=x.x.x.x/mask dst=x.x.x.x/mask l4proto=tcp sport=singleport dport=singleport action=ACCEPT

/**
 * 
 * This class creates a bash script ready to use in EBPF environment
 *
 */
public class BPFFirewall {

	private long id;
	private Node node;
	private List<Elements> policies;
	private List<Elements> policiesWithPriority;
	private List<Elements> policiesWithoutPriority;
	private int startSrcPort;
	private int endSrcPort;
	private int startDstPort;
	private int endDstPort;
	private int temp;
	private String filename;
	private String srcAddresses;
	private String dstAddresses;
	private FileWriter configurationWriter;
	private boolean isFirst = true;
	private boolean srcRanged = false;
	private boolean dstRanged = false;

	/**
	 * Create a script using
	 * 
	 * @param id
	 *            is for distinguish it from other abstract firewall
	 * @param node
	 *            is an abstract firewall that is going to be translated in a real
	 *            configuration
	 * 
	 * @throws Exeption
	 *             if there are problems on creating and writing configuration file
	 *             or if the configuration has any error
	 * 
	 */
	public BPFFirewall(long id, Node node) throws Exception {
		this.id = id;
		// in the node description there is the fire wall number or name
		filename = new String("bpfFirewall_"  + node.getConfiguration().getDescription() + "_" + this.id + ".sh");

		this.node = node;

		File configuration = new File(filename);
		if (!configuration.exists())
			configuration.createNewFile();
		if (configuration.canWrite())
			configurationWriter = new FileWriter(filename);
		else {
			throw new Exception();

		}

		getConfigurationFile();
	}

	/**
	 * 
	 * @throws IOException
	 *             if there are problems on creating and writing configuration file
	 *             or if the policies have any error
	 */
	private void getConfigurationFile() throws IOException {
		// script setting
		configurationWriter.write("#!/bin/sh\ncmd=\"polycubectl pcn-iptables chain\"\n");
		// flush all CHAINS
		configurationWriter.write("${cmd} INPUT rule del\n${cmd} FORWARD rule del\n${cmd} OUTPUT rule del\n");
		if (node.getConfiguration() == null)
			throw new IOException();
		if (node.getConfiguration().getFirewall() == null)
			throw new IOException();
		// set default action of INPUT and OUTPUT to deny and permit ssh traffic (?)
		if (node.getConfiguration().getFirewall().getDefaultAction().equals(ActionTypes.DENY)) {
			configurationWriter.write(
					"${cmd} INPUT set default=DROP\n${cmd} FORWARD set default=DROP\n${cmd} OUTPUT set default=DROP\n");
		} else {
			configurationWriter.write(
					"${cmd} INPUT set default=ACCEPT\n${cmd} FORWARD set default=ACCEPT\n${cmd} OUTPUT set default=ACCEPT\n");
		}

		if (!(policies = node.getConfiguration().getFirewall().getElements()).isEmpty()) {

	    	policiesWithPriority = policies.stream().filter(p -> (p.getPriority() != null && p.getPriority() != "*")).collect(Collectors.toList());
			if(!policiesWithPriority.isEmpty()) {
				policiesWithoutPriority = policies.stream().filter(p -> (p.getPriority() == null  || p.getPriority() == "*" )).collect(Collectors.toList());
				policiesWithPriority = policiesWithPriority.stream().sorted((e1, e2) -> new Integer(e1.getPriority()).compareTo(new Integer(e2.getPriority())) )
						.collect(Collectors.toList());
				if(!policiesWithoutPriority.isEmpty())
					policiesWithPriority.addAll(policiesWithoutPriority);
				policies.clear(); // copy new list to original list
				policies.addAll(policiesWithPriority); 
			}
			for (int index = 0; index < policies.size(); index++) {
				srcAddresses = getAddressWithNetmask(policies.get(index).getSource(), 4);
				dstAddresses = getAddressWithNetmask(policies.get(index).getDestination(), 4);
				srcRanged = false;
				dstRanged = false;
				if (policies.get(index).getSrcPort().equals("*")) {
					startSrcPort = -1;
					endSrcPort = -1;
				} else if (policies.get(index).getSrcPort().contains("-")) {
					startSrcPort = Integer.valueOf(policies.get(index).getSrcPort().split("-")[0]);
					endSrcPort = Integer.valueOf(policies.get(index).getSrcPort().split("-")[1]);
					srcRanged = true;
				} else {
					startSrcPort = Integer.valueOf(policies.get(index).getSrcPort());
					endSrcPort = startSrcPort;
				}

				if (policies.get(index).getDstPort().equals("*")) {
					startDstPort = -1;
					endDstPort = -1;
				} else if (policies.get(index).getDstPort().contains("-")) {
					startDstPort = Integer.valueOf(policies.get(index).getDstPort().split("-")[0]);
					endDstPort = Integer.valueOf(policies.get(index).getDstPort().split("-")[1]);
					dstRanged = true;
				} else {
					startDstPort = Integer.valueOf(policies.get(index).getDstPort());
					endDstPort = startDstPort;
				}

				String action = (policies.get(index).getAction().equals(ActionTypes.ALLOW)) ? "ACCEPT" : "DROP";
				boolean direction = (policies.get(index).isDirectional() != null) ? policies.get(index).isDirectional()
						: false;

				switch (policies.get(index).getProtocol()) {
				case ANY:
					insertRule("tcp", srcAddresses, dstAddresses, startSrcPort, startDstPort, action, srcRanged,
							dstRanged, direction);
					insertRule("udp", srcAddresses, dstAddresses, startSrcPort, startDstPort, action, srcRanged,
							dstRanged, direction);
					break;
				case TCP:
					insertRule("tcp", srcAddresses, dstAddresses, startSrcPort, startDstPort, action, srcRanged,
							dstRanged, direction);
					break;
				case UDP:
					insertRule("udp", srcAddresses, dstAddresses, startSrcPort, startDstPort, action, srcRanged,
							dstRanged, direction);
					break;
				default:
					throw new IOException();
				}

			}
			srcRanged = false;
			dstRanged = false;
		}

		configurationWriter.close();

	}

	/**
	 * 
	 * @param protocol
	 *            that is used to block traffic
	 * @param srcAddresses
	 *            source address with netmask
	 * @param dstAddresses
	 *            destination address with netmask
	 * @param srcPort
	 *            source port to block
	 * @param dstPort
	 *            destination port to block
	 * @param action
	 *            deny or allow traffic
	 * @param srcRangePort
	 *            boolean if the source port is ranged or not
	 * @param dstRangePort
	 *            boolean if the destination port is ranged or not
	 * @param isDirectional
	 *            policy applied from also from destination to source
	 * @throws IOException
	 *             if cannot write to configuration file
	 */

	private void insertRule(String protocol, String srcAddresses, String dstAddresses, int srcPort, int dstPort,
			String action, boolean srcRangePort, boolean dstRangePort, boolean isDirectional) throws IOException {
		String sport, dport;

		if (srcPort == -1) {
			sport = "";
		} else {
			sport = new String(" sport=" + srcPort);
		}
		if (dstPort == -1) {
			dport = "";
		} else {
			dport = new String(" dport=" + dstPort);
		}

		if (isFirst) {

			configurationWriter.write("${cmd} FORWARD insert id=0 src=" + srcAddresses + " dst=" + dstAddresses
					+ " l4proto=" + protocol + sport + dport + " action=" + action + "\n");
			isFirst = false;

		} else {
			configurationWriter.write("${cmd} FORWARD append src=" + srcAddresses + " dst=" + dstAddresses + " l4proto="
					+ protocol + sport + dport + " action=" + action + "\n");
		}


		if (srcRangePort) {
			for (int port = srcPort + 1; port <= endSrcPort; port++) {
				insertRule(protocol, srcAddresses, dstAddresses, port, dstPort, action, false, dstRangePort,
						false);
			}
			srcRangePort = false;
		}
		if (dstRangePort) {
			for (int port = dstPort + 1; port <= endDstPort; port++) {
				insertRule(protocol, srcAddresses, dstAddresses, srcPort, port, action, srcRangePort, false,
						false);
			}

		}
		if (isDirectional) {
			temp = endDstPort;
			endDstPort =endSrcPort;
			endSrcPort = temp;
			insertRule(protocol, dstAddresses, srcAddresses, dstPort, srcPort, action, dstRangePort, srcRangePort,
					false);
			isDirectional = false;
		}
	}

	/**
	 * 
	 * @return filename of this configuration
	 */
	public String getFilename() {
		return filename;
	}

	private String getAddressWithNetmask(String address, int netmask) throws IOException {
		String addressformatted;
		int address8Bit;
		String[] addrArray = address.split("\\.");
		for (int indexadd = 0; indexadd < addrArray.length; indexadd++) {

			try {
				address8Bit = Integer.valueOf(addrArray[indexadd]);
			} catch (NumberFormatException e) {
				throw new IOException();
			}
			if (address8Bit < -1 || address8Bit > 255)
				throw new IOException();

			if (address8Bit == -1)
				netmask += -1;
		}

		switch (netmask) {
		case 0:
			addressformatted = new String("0.0.0.0/0"); // for source/destination up that is ANY
			break;
		case 1:
			addressformatted = new String(addrArray[0] + ".0.0.0/8");
			break;
		case 2:
			addressformatted = new String(addrArray[0] + "." + addrArray[1] + ".0.0/16");
			break;
		case 3:
			addressformatted = new String(addrArray[0] + "." + addrArray[1] + "." + addrArray[2] + ".0/24");
			break;
		case 4:
			addressformatted = new String(address + "/32");
			break;
		default:
			throw new IOException();
		}

		return addressformatted;

	}
}
