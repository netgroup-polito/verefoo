package it.polito.verefoo.firewall;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import it.polito.verefoo.jaxb.ActionTypes;
import it.polito.verefoo.jaxb.Elements;
import it.polito.verefoo.jaxb.Node;

/**
 * 
 * This class is used for create a rule script for IPFW firewall
 *
 */
public class IpFirewall {

	private long id;
	private Node node;
	private boolean isDeny = false;
	private List<Elements> policies;
	private int startSrcPort;
	private int endSrcPort;
	private int startDstPort;
	private int endDstPort;
	private int priority;
	private String filename;
	private String srcAddresses;
	private String dstAddresses;
	private String protocol;
	private FileWriter configurationWriter;

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
	public IpFirewall(long id, Node node) throws Exception {
		this.id = id;
		// id da usare per il nome del file rc_id.rules
		filename = new String("rc_" + this.id + ".rules");

		this.node = node;
		if (node.getConfiguration() == null)
			throw new IOException();
		if (node.getConfiguration().getFirewall() == null)
			throw new IOException();

		if (this.node.getConfiguration().getFirewall().getDefaultAction().equals(ActionTypes.DENY))
			isDeny = true;

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
		configurationWriter.write("#!/bin/sh\ncmd=\"/sbin/ipfw -q\"\n");
		// flush
		// delete set 31
		configurationWriter.write("${cmd} -f flush\n${cmd} delete set 31\n");

		if (!(policies = this.node.getConfiguration().getFirewall().getElements()).isEmpty()) {

			for (int index = 0; index < policies.size(); index++) {
				priority = 1;
				if (!(policies.get(index).getPriority() == null))
					if (!policies.get(0).getPriority().equals("*"))
						priority = Integer.valueOf(policies.get(index).getPriority());
				srcAddresses = getAddressWithNetmask(policies.get(index).getSource(), 4);
				dstAddresses = getAddressWithNetmask(policies.get(index).getDestination(), 4);

				if (policies.get(index).getSrcPort().equals("*")) {
					startSrcPort = 0;
					endSrcPort = 65535;
				} else if (policies.get(index).getSrcPort().contains("-")) {
					startSrcPort = Integer.valueOf(policies.get(index).getSrcPort().split("-")[0]);
					endSrcPort = Integer.valueOf(policies.get(index).getSrcPort().split("-")[1]);
				} else {
					startSrcPort = Integer.valueOf(policies.get(index).getSrcPort());
					endSrcPort = startSrcPort;
				}

				if (policies.get(index).getDstPort().equals("*")) {
					startDstPort = 0;
					endDstPort = 65535;
				} else if (policies.get(index).getDstPort().contains("-")) {
					startDstPort = Integer.valueOf(policies.get(index).getDstPort().split("-")[0]);
					endDstPort = Integer.valueOf(policies.get(index).getDstPort().split("-")[1]);
				} else {
					startDstPort = Integer.valueOf(policies.get(index).getDstPort());
					endDstPort = startDstPort;
				}

				switch (policies.get(index).getProtocol()) {
				case ANY:
					protocol = "ip";
					break;
				case TCP:
					protocol = "tcp";
					break;
				case UDP:
					protocol = "udp";
					break;
				default:
					throw new IOException();
				}

				String action = (policies.get(index).getAction().equals(ActionTypes.ALLOW)) ? "allow" : "deny";

				configurationWriter.write("${cmd} add " + priority + " set 1 " + action + " " + protocol + " from "
						+ srcAddresses + " " + startSrcPort + "-" + endSrcPort + " to " + dstAddresses + " "
						+ startDstPort + "-" + endDstPort + "\n");
				if (policies.get(index).isDirectional() != null)
					if (policies.get(index).isDirectional()) {
						configurationWriter
								.write("${cmd} add " + priority + " set 1 " + action + " " + protocol + " from "

										+ dstAddresses + " " + startDstPort + "-" + endDstPort + " to " + srcAddresses
										+ " " + startSrcPort + "-" + endSrcPort + "\n");
					}

			}
		}

		if (!isDeny) {

			// default action that allows traffic
			configurationWriter.write("${cmd} add 65534 set 31 allow ip from any to any\n");
		} else {

			// default action that denies traffic

			configurationWriter.write("${cmd} add 65534 set 31 deny ip from any to any\n");
		}

		configurationWriter.close();

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
