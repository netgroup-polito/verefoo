package it.polito.verefoo.firewall;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import it.polito.verefoo.jaxb.ActionTypes;
import it.polito.verefoo.jaxb.Elements;
import it.polito.verefoo.jaxb.Node;

// da sperimentare ancora
public class IpFirewall {

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
	private int priority;
	//private int ruleId;
	private String filename;
	private String srcAddresses;
	private String dstAddresses;
	private String protocol;
	private FileWriter configurationWriter;

	public IpFirewall(long id, Node node) throws Exception {
		this.id = id;
		// id da usare per il nome del file rc_id.rules
		filename = new String("rc_" + this.id + ".rules");

		this.node = node;
		//ruleId = 1;
		// node.getId();

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

		System.out.println("\n" + this.node.getName() + "\t" + filename + "\n\n\n");
		getConfigurationFile();
	}

	private void getConfigurationFile() throws IOException {
		// script setting
		 configurationWriter.write("#!/bin/sh\ncmd=\"/sbin/ipfw -q\"\n");
		System.out.println("#!/bin/sh\ncmd=\"/sbin/ipfw -q\"\n");
		// flush
		// delete set 31
		 configurationWriter.write("${cmd} -f flush\n${cmd} delete set 31\n");
		System.out.println("${cmd} -f flush\n${cmd} delete set 31\n");

		if (!(policies = this.node.getConfiguration().getFirewall().getElements()).isEmpty()) {

			for (int index = 0; index < policies.size(); index++) {
				priority=1;
if(!(policies.get(index).getPriority()==null))
	if(!policies.get(0).getPriority().equals("*"))
		priority=Integer.valueOf(policies.get(index).getPriority());
String[] srcAddr=null;
if(policies.get(index).getSource()==null) {
	scrNetmask = -1;
}else {
				scrNetmask = 4;
				srcAddr = policies.get(index).getSource().split("\\.");
				for (int indexadd = 0; indexadd < srcAddr.length; indexadd++) {
					if (Integer.valueOf(srcAddr[indexadd]) == -1)
						scrNetmask += -1;
				}
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
					srcAddr=null;
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
				//or use ruleID for different order
				
				 configurationWriter.write("${cmd} add "+priority+" set 1 "+action+" "+protocol+" from "+srcAddresses+" "+
				 startSrcPort+"-"+endSrcPort+" to "+dstAddresses+" "+startDstPort+"-"+endDstPort+"\n");
				System.out.println("${cmd} add "+priority+" set 1 " + action + " " + protocol + " from "
						+ srcAddresses + " " + startSrcPort + "-" + endSrcPort + " to " + dstAddresses + " "
						+ startDstPort + "-" + endDstPort + "\n");
				if(policies.get(index).isDirectional()!=null)
				if(policies.get(index).isDirectional()) {
					configurationWriter.write("${cmd} add " + priority + " set 1 " + action + " " + protocol + " from "

							+ dstAddresses + " " + startDstPort + "-" + endDstPort + " to " + srcAddresses + " "
							+ startSrcPort + "-" + endSrcPort + "\n");
							System.out.println("${cmd} add " + priority + " set 1 " + action + " " + protocol + " from "

							+ dstAddresses + " " + startDstPort + "-" + endDstPort + " to " + srcAddresses + " "
							+ startSrcPort + "-" + endSrcPort + "\n");
				}

			}
		}
		
		if (!isDeny) {

			// default action that allows traffic
			 configurationWriter.write("${cmd} add 65534 set 31 allow ip from any to any\n");
			System.out.println("${cmd} add 65534 set 31 allow ip from any to any\n");

		} else {

			// default action that denies traffic

			 configurationWriter.write("${cmd} add 65534 set 31 deny ip from any to any\n");
			System.out.println("${cmd} add 65534 set 31 deny ip from any to any\n");

		}

		 configurationWriter.close();

	}
	
	public String getFilename() {
		return filename;
	}
}
