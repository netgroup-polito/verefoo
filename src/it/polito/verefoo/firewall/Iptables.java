package it.polito.verefoo.firewall;

import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import it.polito.verefoo.jaxb.ActionTypes;
import it.polito.verefoo.jaxb.Elements;
import it.polito.verefoo.jaxb.FunctionalTypes;
import it.polito.verefoo.jaxb.Node;

/**
 * 
 * This class is used to create a bash script ready to use in linux machine for
 * configure Iptables firewall
 *
 */
public class Iptables {

	private long id;
	private Node node;
	private List<Elements> policies;
	private List<Elements> policiesWithPriority;
	private List<Elements> policiesWithoutPriority;
	private int startSrcPort;
	private int endSrcPort;
	private int startDstPort;
	private int endDstPort;
	private String filename;
	private String srcAddresses;
	private String dstAddresses;
	private FileWriter configurationWriter;
	private boolean isFirst = true;
	/**
	 * Create a script using:
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
	public Iptables(long id, Node node) throws Exception {
		this.id = id;
		
		if (node.getConfiguration() == null)
			throw new IOException();
		if (node.getConfiguration().getDescription() == null)
			throw new IOException();
		
		// in the node description there is the fire wall number or name
		filename = new String("iptablesFirewall_" + node.getConfiguration().getDescription() + "_" + this.id + ".sh");

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
		configurationWriter.write("#!/bin/sh\ncmd=\"sudo iptables\"\n");
		// flush all CHAINS
		configurationWriter.write("${cmd} -F\n");

		if (node.getConfiguration() == null)
			throw new IOException();
		if (node.getConfiguration().getFirewall() == null)
			throw new IOException();

		// set default action of INPUT and OUTPUT to deny and permit ssh traffic (?)
		if (node.getConfiguration().getFirewall().getDefaultAction().equals(ActionTypes.DENY)) {
			configurationWriter.write("${cmd} -P INPUT DROP\n${cmd} -P FORWARD DROP\n${cmd} -P OUTPUT DROP\n"); 
		} else {
			configurationWriter.write("${cmd} -P INPUT ACCEPT\n${cmd} -P FORWARD ACCEPT\n${cmd} -P OUTPUT ACCEPT\n");
		}

		if (!(policies = node.getConfiguration().getFirewall().getElements()).isEmpty()) { //fixed 

	    	policiesWithPriority = policies.stream().filter(p -> (p.getPriority() != null && p.getPriority() != "*")).collect(Collectors.toList()); // order according to priority
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

				if (policies.get(index).getSrcPort().equals("*")) {
					startSrcPort = -1;
					endSrcPort = -1;
				} else if (policies.get(index).getSrcPort().contains("-")) {
					startSrcPort = Integer.valueOf(policies.get(index).getSrcPort().split("-")[0]);
					endSrcPort = Integer.valueOf(policies.get(index).getSrcPort().split("-")[1]);

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

				} else {
					startDstPort = Integer.valueOf(policies.get(index).getDstPort());
					endDstPort = startDstPort;
				}

				String action = (policies.get(index).getAction().equals(ActionTypes.ALLOW)) ? "ACCEPT" : "DROP";
				boolean isDirectional = (policies.get(index).isDirectional() != null)
						? policies.get(index).isDirectional()
						: false;
				switch (policies.get(index).getProtocol()) {
				case ANY:
					insertRule("tcp", srcAddresses, dstAddresses, startSrcPort, startDstPort, endSrcPort, endDstPort,
							action, isDirectional);
					insertRule("udp", srcAddresses, dstAddresses, startSrcPort, startDstPort, endSrcPort, endDstPort,
							action, isDirectional);

					break;
				case TCP:
					insertRule("tcp", srcAddresses, dstAddresses, startSrcPort, startDstPort, endSrcPort, endDstPort,
							action, isDirectional);
					break;
				case UDP:
					insertRule("udp", srcAddresses, dstAddresses, startSrcPort, startDstPort, endSrcPort, endDstPort,
							action, isDirectional);
					break;
				default:
					throw new IOException();
				}

			}

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
	 * @param startSrcPort
	 *            first value of source port
	 * @param startDstPort
	 *            first value of destination port
	 * @param endSrcPort
	 *            last value of source port
	 * @param endDstPort
	 *            last value of destination port
	 * @param action
	 *            deny or allow traffic
	 * @param isDirectional
	 *            policy applied from also from destination to source
	 * @throws IOException
	 *             if cannot write to configuration file
	 */
	private void insertRule(String protocol, String srcAddresses, String dstAddresses, int startSrcPort,
			int startDstPort, int endSrcPort, int endDstPort, String action, boolean isDirectional) throws IOException {
		String sport, dport, sprotocol;
		if (protocol == null) {
			sprotocol = "";
		} else {
			sprotocol = new String(" -p " + protocol);
		}
		if (startSrcPort == -1) {
			sport = "";
		} else if (startSrcPort != endSrcPort) {
			sport = new String(" --sport " + startSrcPort + ":" + endSrcPort);
		} else {
			sport = new String(" --sport " + startSrcPort);
		}
		if (startDstPort == -1) {
			dport = "";
		} else if (startDstPort != endDstPort) {

			dport = new String(" --dport " + startDstPort + ":" + endDstPort);
		} else {
			dport = new String(" --dport " + startDstPort);
		}

		if (isFirst) {
			// check
			configurationWriter.write("${cmd} -A FORWARD" + sprotocol + " -s " + srcAddresses + " -d " + dstAddresses
					+ sport + dport + " -j " + action + "\n");
			isFirst = false;

		} else {
			configurationWriter.write("${cmd} -A FORWARD" + sprotocol + " -s " + srcAddresses + " -d " + dstAddresses
					+ sport + dport + " -j " + action + "\n");
		}
		if (isDirectional) {
			insertRule(protocol, dstAddresses, srcAddresses, startDstPort, startSrcPort, endDstPort, endSrcPort, action,
					false);

		}

	}

	/**
	 * This function returns the filename of the script that will be generated
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
			addressformatted = new String("0.0.0.0/0"); // for source/destination IP that is ANY ( common problem in all fire wall types )
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
