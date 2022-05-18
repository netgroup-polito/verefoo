package it.polito.verefoo.firewall;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import it.polito.verefoo.jaxb.ActionTypes;
import it.polito.verefoo.jaxb.Elements;
import it.polito.verefoo.jaxb.L4ProtocolTypes;
import it.polito.verefoo.jaxb.Node;

public class Fortinet {
	private long id;
	private Node node;
	private boolean isDeny = false;
	private List<Elements> policies;
	private int scrNetmask;
	private int dstNetmask;
	private int startSrcPort;
	private int endSrcPort;
	private int startDstPort;
	private int endDstPort;
	private int policyId;
	private String filename;
	private FileWriter configurationWriter;

	public Fortinet(Long id, Node node) throws Exception {
		this.id = id;
		filename = new String("fortinet_firewall_" + this.id + ".conf");

		this.node = node;
		policyId = 1;

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

	private void getConfigurationFile() throws Exception {
		int address8Bit;
		// set transparent mode
		configurationWriter.write("config system settings\n\tset opmode transparent\nend\n");
		// set in-band network management
		// configure address
		// System.out.println("config system settings\n\tset manageip "+
		// this.node.getName()+"/255.255.255.0\nend\n");
		// ////da mettere dopo transparent se si vuole fare unico comando
		// //System.out.println("\tset manageip "+
		// this.node.getName()+"/255.255.255.0\n");
		// //configure default gateway
		// System.out.println("config router static\n\tedit 1\n\t\tset gateway
		// i.p.v.4\n\tnext\nend\n");
		// // enable port1 for remote in- band access
		// System.out.println("config system interface\n\tedit port1\n\t\tset
		// allowaccess ping ssh https snmp\n\nend\n");

		// set category
		configurationWriter.write(
				"config firewall service category\n\tedit \"custom_verefoo\"\n\t\tset comment \"new category for verefoo services\"\n\tnext\nend\n");

		// set schedule always
		configurationWriter.write(
				"config firewall schedule recurring\n\tedit \"always_custom\"\n\t\tset day sunday monday tuesday wednesday thursday friday saturday\n\tnext\nend\n");

		if (node.getConfiguration() == null)
			throw new IOException();
		if (node.getConfiguration().getFirewall() == null)
			throw new IOException();

		if (!(policies = this.node.getConfiguration().getFirewall().getElements()).isEmpty()) {

			if (!(policies.get(0).getPriority() == null)) {

				if (!policies.get(0).getPriority().equals("*"))
					policies = policies.stream().sorted(Comparator.comparing(Elements::getPriority).reversed())
							.collect(Collectors.toList());
			}

			for (int index = 0; index < policies.size(); index++) {

				scrNetmask = 4;
				String[] srcAddr = policies.get(index).getSource().split("\\.");
				for (int indexadd = 0; indexadd < srcAddr.length; indexadd++) {
					try {
						address8Bit = Integer.valueOf(srcAddr[indexadd]);
					} catch (NumberFormatException e) {
						throw new IOException();
					}
					if (address8Bit < -1 || address8Bit > 255)
						throw new IOException();

					if (address8Bit == -1)
						scrNetmask += -1;
				}
				configurationWriter.write("config firewall adress\n\tedit \"src_" + policyId + "\"\n\t\tset uuid "
						+ generateUUID() + "\n\t\tset subnet ");

				switch (scrNetmask) {
				case 1:
					configurationWriter.write(srcAddr[0] + ".0.0.0 255.0.0.0");
					break;
				case 2:
					configurationWriter.write(srcAddr[0] + "." + srcAddr[1] + ".0.0 255.255.0.0");

					break;
				case 3:
					configurationWriter.write(srcAddr[0] + "." + srcAddr[1] + "." + srcAddr[2] + ".0 255.255.255.0");

					break;
				case 4:
					configurationWriter.write(policies.get(index).getSource() + " 255.255.255.255");

					break;
				default:
					throw new Exception();
				}
				configurationWriter.write("\n\tnext\nend\n");

				dstNetmask = 4;
				String[] dstAddr = policies.get(index).getDestination().split("\\.");
				for (int indexadd = 0; indexadd < dstAddr.length; indexadd++) {
					try {
						address8Bit = Integer.valueOf(dstAddr[indexadd]);
					} catch (NumberFormatException e) {
						throw new IOException();
					}
					if (address8Bit < -1 || address8Bit > 255)
						throw new IOException();

					if (address8Bit == -1)
						dstNetmask += -1;
				}
				configurationWriter.write("config firewall adress\n\tedit \"dst_" + policyId + "\"\n\t\tset uuid "
						+ generateUUID() + "\n\t\tset subnet ");
				switch (dstNetmask) {
				case 1:
					configurationWriter.write(dstAddr[0] + ".0.0.0 255.0.0.0");

					break;
				case 2:
					configurationWriter.write(dstAddr[0] + "." + dstAddr[1] + ".0.0 255.255.0.0");

					break;
				case 3:
					configurationWriter.write(dstAddr[0] + "." + dstAddr[1] + "." + dstAddr[2] + ".0 255.255.255.0");

					break;
				case 4:
					configurationWriter.write(policies.get(index).getDestination() + " 255.255.255.255");

					break;
				default:
					throw new Exception();
				}
				configurationWriter.write("\n\tnext\nend\n");

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
				configurationWriter.write("config firewall service custom\n\tedit \"custom_service_" + policyId
						+ "\" \n\t\tset category \"custom_verefoo\"\n\t\tset comment \"sample service\"\n");

				if (policies.get(index).getProtocol().equals(L4ProtocolTypes.TCP)
						|| policies.get(index).getProtocol().equals(L4ProtocolTypes.ANY)) {
					// tcp
					configurationWriter.write("\t\tset tcp-portrange " + startDstPort + "-" + endDstPort + ":"
							+ startSrcPort + "-" + endSrcPort + "\n");

				}
				if (policies.get(index).getProtocol().equals(L4ProtocolTypes.UDP)
						|| policies.get(index).getProtocol().equals(L4ProtocolTypes.ANY)) {
					// udp
					configurationWriter.write("\t\tset udp-portrange " + startDstPort + "-" + endDstPort + ":"
							+ startSrcPort + "-" + endSrcPort + "\n");
				}
				configurationWriter.write("\n\tnext\nend\n");

				String action = (policies.get(index).getAction().equals(ActionTypes.ALLOW)) ? "accept" : "deny";
				configurationWriter.write("config firewall policy\n\tedit " + policyId + "\n\t\tset uuid "
						+ generateUUID() + "\n\t\tset srcintf \"lan\"\n\t\tset dstintf \"lan\"\n\t\tset srcaddr \"src_"
						+ policyId + "\"\n\t\tset dstaddr \"dst_" + policyId + "\"\n\t\tset action " + action
						+ "\n\t\tset schedule \"always_custom\"\n\t\tset service \"custom_service_" + policyId
						+ "\"\n\tnext\nend\n");

				if (policies.get(index).isDirectional() != null)
					if (policies.get(index).isDirectional()) {
						int oldPolicyID = policyId;
						policyId++;

						configurationWriter.write("config firewall service custom\n\tedit \"custom_service_" + policyId
								+ "\" \n\t\tset category \"custom_verefoo\"\n\t\tset comment \"sample service\"\n");

						if (policies.get(index).getProtocol().equals(L4ProtocolTypes.TCP)
								|| policies.get(index).getProtocol().equals(L4ProtocolTypes.ANY)) {
							// tcp
							configurationWriter.write("\t\tset tcp-portrange " + startSrcPort + "-" + endSrcPort + ":"
									+ startDstPort + "-" + endDstPort + "\n");

						}
						if (policies.get(index).getProtocol().equals(L4ProtocolTypes.UDP)
								|| policies.get(index).getProtocol().equals(L4ProtocolTypes.ANY)) {
							// udp
							configurationWriter.write("\t\tset udp-portrange " + startSrcPort + "-" + endSrcPort + ":"
									+ startDstPort + "-" + endDstPort + "\n");
						}

						configurationWriter.write("config firewall policy\n\tedit " + policyId + "\n\t\tset uuid "
								+ generateUUID()
								+ "\n\t\tset srcintf \"lan\"\n\t\tset dstintf \"lan\"\n\t\tset srcaddr \"dst_"
								+ oldPolicyID + "\"\n\t\tset dstaddr \"src_" + oldPolicyID + "\"\n\t\tset action "
								+ action + "\n\t\tset schedule \"always_custom\"\n\t\tset service \"custom_service_"
								+ policyId + "\"\n\tnext\nend\n");

					}

				policyId++;
			}
		}
		// add default policy for enable traffic
		if (!isDeny) {
			configurationWriter.write("config firewall adress\n\tedit \"src_" + policyId + "\"\n\t\tset uuid "
					+ generateUUID() + "\n\tnext\nend\n");

			configurationWriter.write("config firewall adress\n\tedit \"dst_" + policyId + "\"\n\t\tset uuid "
					+ generateUUID() + "\n\tnext\nend\n");

			configurationWriter.write("config firewall service custom\n\tedit \"custom_service_" + policyId
					+ "\" \n\t\tset category"
					+ " \"custom_verefoo\"\n\t\tset comment \"default action service\"\n\t\tset tcp-portrange 0-65535:0-65535\n"
					+ "\t\tset udp-portrange 0-65535:0-65535\n\n\tnext\nend\n");

			configurationWriter.write("config firewall policy\n\tedit " + policyId + "\n\t\tset uuid " + generateUUID()
					+ "\n\t\tset srcintf \"lan\"\n\t\tset dstintf \"lan\"\n\t\tset srcaddr \"src_" + policyId
					+ "\"\n\t\tset dstaddr \"dst_" + policyId
					+ "\"\n\t\tset action accept\n\t\tset schedule \"always_custom\"\n\t\tset service \"custom_service_"
					+ policyId + "\"\n\tnext\nend\n");
		}

		configurationWriter.close();

	}

	private String generateUUID() {

		return UUID.randomUUID().toString();
	}

	public String getFilename() {
		return filename;
	}
}
