package it.polito.verefoo.firewall;

import java.io.File;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

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
public class BPFFirewall {

	private long id;
	private Node node;
	private List<Elements> policies;
	private int scrNetmask;
	private int dstNetmask;
	private int startSrcPort;
	private int endSrcPort;
	private int startDstPort;
	private int endDstPort;
	private String filename;
	private String srcAddresses;
	private String dstAddresses;
	private FileWriter configurationWriter;
	private boolean isFirst = true;
	private boolean srcRanged = false;
	private boolean dstRanged = false;

	public BPFFirewall(long id, Node node) throws Exception {
		this.id = id;
		// name of the script
		filename = new String("bpfFirewall_" + this.id + ".sh");

		this.node = node;
		// node.getId();

		File configuration = new File(filename);
		if (!configuration.exists())
			configuration.createNewFile();
		if (configuration.canWrite())
			configurationWriter = new FileWriter(filename);
		else {
			// errore
		}

		System.out.println("\n" + this.node.getName() + "\t" + filename + "\n\n\n");
		getConfigurationFile();
	}

	private void getConfigurationFile() throws IOException {
		// script setting
		configurationWriter.write("#!/bin/sh\ncmd=\"polycubectl pcn-iptables chain\"\n");
		System.out.println("#!/bin/sh\ncmd=\"polycubectl pcn-iptables chain\"\n");
		// flush all CHAINS
		configurationWriter.write("${cmd} INPUT rule del\n${cmd} FORWARD rule del\n${cmd} OUTPUT rule del\n");
		System.out.println("${cmd} INPUT rule del\n${cmd} FORWARD rule del\n${cmd} OUTPUT rule del\n");
		// set default action of INPUT and OUTPUT to deny and permit ssh traffic (?)
		if (node.getConfiguration().getFirewall().getDefaultAction().equals(ActionTypes.DENY)) {
			configurationWriter.write(
					"${cmd} INPUT set default=DROP\n${cmd} FORWARD set default=DROP\n${cmd} OUTPUT set default=DROP\n");
			System.out.println(
					"${cmd} INPUT set default=DROP\n${cmd} FORWARD set default=DROP\n${cmd} OUTPUT set default=DROP\n");
		} else {
			configurationWriter.write(
					"${cmd} INPUT set default=ACCEPT\n${cmd} FORWARD set default=ACCEPT\n${cmd} OUTPUT set default=ACCEPT\n");
			System.out.println(
					"${cmd} INPUT set default=ACCEPT\n${cmd} FORWARD set default=ACCEPT\n${cmd} OUTPUT set default=ACCEPT\n");
		}
		if (!(policies = node.getConfiguration().getFirewall().getElements()).isEmpty()) {

			for (int index = 0; index < policies.size(); index++) {

				scrNetmask = 4;
				String[] srcAddr = policies.get(index).getSource().split("\\.");
				for (int indexadd = 0; indexadd < srcAddr.length; indexadd++) {
					// variabile ausiliaria e fai un try catch per validare l'input
					if (Integer.valueOf(srcAddr[indexadd]) == -1)
						scrNetmask += -1;
				}

				switch (scrNetmask) {
				case 1:
					srcAddresses = new String(srcAddr[0] + ".0.0.0/8");
					break;
				case 2:
					srcAddresses = new String(srcAddr[0] + "." + srcAddr[1] + ".0.0/16");
					break;
				case 3:
					srcAddresses = new String(srcAddr[0] + "." + srcAddr[1] + "." + srcAddr[2] + ".0/24");
					break;
				case 4:
					srcAddresses = new String(policies.get(index).getSource() + "/32");
					break;
				default:
					throw new IOException();
				}

				dstNetmask = 4;
				String[] dstAddr = policies.get(index).getDestination().split("\\.");
				for (int indexadd = 0; indexadd < dstAddr.length; indexadd++) {
					if (Integer.valueOf(dstAddr[indexadd]) == -1)
						dstNetmask += -1;
				}

				switch (dstNetmask) {
				case 1:
					dstAddresses = new String(dstAddr[0] + ".0.0.0/8");
					break;
				case 2:
					dstAddresses = new String(dstAddr[0] + "." + dstAddr[1] + ".0.0/16");
					break;
				case 3:
					dstAddresses = new String(dstAddr[0] + "." + dstAddr[1] + "." + dstAddr[2] + ".0/24");
					break;
				case 4:
					dstAddresses = new String(policies.get(index).getDestination() + "/32");
					break;
				default:
					throw new IOException();
				}

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

				switch (policies.get(index).getProtocol()) {
				case ANY:
					insertRule("tcp", startSrcPort, startDstPort, action, srcRanged, dstRanged);
					insertRule("udp", startSrcPort, startDstPort, action, srcRanged, dstRanged);
					break;
				case TCP:
					insertRule("tcp", startSrcPort, startDstPort, action, srcRanged, dstRanged);
					break;
				case UDP:
					insertRule("udp", startSrcPort, startDstPort, action, srcRanged, dstRanged);
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

	private void insertRule(String protocol, int srcPort, int dstPort, String action, boolean srcRangePort,
			boolean dstRangePort) throws IOException {
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
			System.out.println("${cmd} FORWARD insert id=0 src=" + srcAddresses + " dst=" + dstAddresses + " l4proto="
					+ protocol + sport + dport + " action=" + action + "\n");
			isFirst = false;

		} else {
			configurationWriter.write("${cmd} FORWARD append src=" + srcAddresses + " dst=" + dstAddresses + " l4proto="
					+ protocol + sport + dport + " action=" + action + "\n");
			System.out.println("${cmd} FORWARD append src=" + srcAddresses + " dst=" + dstAddresses + " l4proto="
					+ protocol + sport + dport + " action=" + action + "\n");
		}

		if (srcRangePort) {
			for (int port = srcPort + 1; port <= endSrcPort; port++) {
				insertRule(protocol, port, dstPort, action, false, dstRangePort);
			}
			srcRangePort = false;
		}
		if (dstRangePort) {
			for (int port = dstPort + 1; port <= endDstPort; port++) {
				insertRule(protocol, srcPort, port, action, srcRangePort, false);
			}

		}
	}

}
