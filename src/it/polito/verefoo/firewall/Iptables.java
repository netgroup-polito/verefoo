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

public class Iptables {
	
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
	//private boolean isPriority=false;

	public Iptables(long id, Node node) throws Exception {
		this.id = id;
		// name of the script
		filename = new String("iptablesFirewall_" + this.id + ".sh");

		this.node = node;
		// node.getId();

		File configuration = new File(filename);
		if (!configuration.exists())
			configuration.createNewFile();
		if (configuration.canWrite())
			configurationWriter = new FileWriter(filename);
		else {
			throw new Exception();
		}

		System.out.println("\n" + this.node.getName() + "\t" + filename + "\n\n\n");
		getConfigurationFile();
	}

	private void getConfigurationFile() throws IOException {
		// script setting
		configurationWriter.write("#!/bin/sh\ncmd=\"sudo iptables\"\n");
		System.out.println("#!/bin/sh\ncmd=\"sudo iptables\"\n");
		// flush all CHAINS
		configurationWriter.write("${cmd} -F\n");
		System.out.println("${cmd} -F\n");
		// set default action of INPUT and OUTPUT to deny and permit ssh traffic (?)
		if (node.getConfiguration().getFirewall().getDefaultAction().equals(ActionTypes.DENY)) {
			configurationWriter.write(
					"${cmd} -P INPUT DROP\n${cmd} -P FORWARD DROP\n${cmd} -P OUTPUT set DROP\n");
			System.out.println(
					"${cmd} -P INPUT DROP\n${cmd} -P FORWARD DROP\n${cmd} -P OUTPUT DROP\n");
		} else {
			configurationWriter.write(
					"${cmd} -P INPUT ACCEPT\n${cmd} -P FORWARD ACCEPT\n${cmd} -P OUTPUT ACCEPT\n");
			System.out.println(
					"${cmd} -P INPUT ACCEPT\n${cmd} -P FORWARD ACCEPT\n${cmd} -P OUTPUT ACCEPT\n");
		}
		
		
	
		
		if (!(policies = node.getConfiguration().getFirewall().getElements()).isEmpty()) {

			if(!(policies.get(0).getPriority()==null)) {
				
				if(!policies.get(0).getPriority().equals("*"))
					//isPriority =true;
					policies = policies.stream().sorted(Comparator.comparing(Elements::getPriority).reversed()).collect(Collectors.toList());
			}
			
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
boolean isDirectional = (policies.get(index).isDirectional()!=null) ? policies.get(index).isDirectional() : false;
				switch (policies.get(index).getProtocol()) {
				case ANY:
					insertRule(null,srcAddresses,dstAddresses, startSrcPort, startDstPort, endSrcPort, endDstPort, action,isDirectional);
					break;
				case TCP:
					insertRule("tcp",srcAddresses,dstAddresses, startSrcPort, startDstPort, endSrcPort, endDstPort, action,isDirectional);
					break;
				case UDP:
					insertRule("udp",srcAddresses,dstAddresses, startSrcPort, startDstPort, endSrcPort, endDstPort, action,isDirectional);
					break;
				default:
					throw new IOException();
				}

			}

		}
		configurationWriter.write("sudo iptables-save > /etc/iptables/iptables.rules\n");
		System.out.println("sudo iptables-save > /etc/iptables/iptables.rules\n");
		configurationWriter.close();

	}

	private void insertRule(String protocol,String srcAddresses ,String dstAddresses,  int startSrcPort, int startDstPort, int endSrcPort, int endDstPort,
			String action, boolean isDirectional) throws IOException{
		String sport, dport,sprotocol;
		if(protocol!=null) {
			sprotocol="";
		}else {
			sprotocol=new String(" -p "+protocol);
		}
				if (startSrcPort == -1) {
					sport = "";
				} else if(startSrcPort!=endSrcPort){
					sport = new String(" --sport " + startSrcPort+":"+endSrcPort);
				}else {
					sport = new String(" --sport " + startSrcPort);
				}
				if (startDstPort == -1) {
					dport = "";
				} else if(startDstPort!=endDstPort){
					
					dport = new String(" --dport " + startDstPort+":"+endDstPort);
				}else {
					dport = new String(" --dport " + startDstPort);
				}

				if (isFirst) {
					configurationWriter.write("${cmd} -I FORWARD 0"+sprotocol +" -s "+ srcAddresses + " -d " + dstAddresses
							 + sport + dport + " -j " + action + "\n");
					System.out.println("${cmd} -I FORWARD 0"+sprotocol +" -s "+ srcAddresses + " -d " + dstAddresses
							 + sport + dport + " -j " + action + "\n");
					isFirst = false;

				} else {
					configurationWriter.write("${cmd} -A FORWARD"+sprotocol +" -s "+ srcAddresses + " -d " + dstAddresses
							 + sport + dport + " -j " + action + "\n");
					System.out.println("${cmd} -A FORWARD"+sprotocol +" -s "+ srcAddresses + " -d " + dstAddresses
							 + sport + dport + " -j " + action + "\n");
				}
				if(isDirectional) {
					insertRule(protocol,dstAddresses,dstAddresses, startDstPort, startSrcPort, endDstPort, endSrcPort, action,false);
					
				}
		
	}


}
