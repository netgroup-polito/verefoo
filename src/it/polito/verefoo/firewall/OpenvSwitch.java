package it.polito.verefoo.firewall;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import it.polito.verefoo.jaxb.ActionTypes;
import it.polito.verefoo.jaxb.Elements;
import it.polito.verefoo.jaxb.Node;

public class OpenvSwitch {

	private long id;
	private Node node;
	private List<Elements> policies;
	private int scrNetmask;
	private int dstNetmask;
	private int startSrcPort;
	private int endSrcPort;
	private int startDstPort;
	private int endDstPort;
	private int priority;
	private String filename;
	private String srcAddresses;
	private String dstAddresses;
	private String bridgeName;
	private FileWriter configurationWriter;
	
	private  int[][] portBitMasks = new int[][] { { 0, 0xffff },
		{ 0x1, 0xfffe }, { 0x3, 0xfffc }, { 0x7, 0xfff8 }, { 0xf, 0xfff0 },
		{ 0x1f, 0xffe0 }, { 0x3f, 0xffc0 }, { 0x7f, 0xff80 },
		{ 0xff, 0xff00 }, { 0x1ff, 0xfe00 }, { 0x3ff, 0xfc00 },
		{ 0x7ff, 0xf800 }, { 0xfff, 0xf000 }, { 0x1fff, 0xe000 },
		{ 0x3fff, 0xc000 }, { 0x7fff, 0x8000 }, { 0xffff, 0 } };


	public OpenvSwitch(long id, Node node) throws Exception {
		this.id = id;
		// name of the script
		filename = new String("ovsFirewall_" + this.id + ".sh");

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
		if(!node.getConfiguration().getDescription().isEmpty())
				this.bridgeName=node.getConfiguration().getDescription().split(":")[0];
				//or use name without "."
		else {
			throw new Exception();
		}
		System.out.println("\n" + this.node.getName() + "\t" + filename + "\n\n\n");
		getConfigurationFile();
	}
	private void getConfigurationFile() throws IOException {
		
		//ovs-ofctr add-flow name
		//nome del bridge precedentemente creato 
		if (!(policies = this.node.getConfiguration().getFirewall().getElements()).isEmpty()) {

			for (int index = 0; index < policies.size(); index++) {
				//policies.get(index).setPriority("499");
				if(policies.get(index).getPriority()==null)
					priority=-1;
				else {
					if(policies.get(index).getPriority().equals("*"))
						priority=-1;
					else {
						priority=Integer.valueOf(policies.get(index).getPriority());
					}
					//if priority is null (da controllare)
					
				}
				//priority=1,dl_type=0x800,
				//priority =Integer.valueOf((policies.get(index).getPriority().equals(null)?"1":policies.get(index).getPriority()));
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

					String action = (policies.get(index).getAction().equals(ActionTypes.ALLOW)) ? "NORMAL" : "drop";
					boolean direction = (policies.get(index).isDirectional()!=null) ? policies.get(index).isDirectional() : false;

					switch (policies.get(index).getProtocol()) {
					case ANY:
						addPolicy(6,srcAddresses , dstAddresses, priority, action, getPortMask(startSrcPort,endSrcPort), getPortMask(startDstPort,endDstPort),direction);
						addPolicy(17,srcAddresses , dstAddresses, priority, action, getPortMask(startSrcPort,endSrcPort), getPortMask(startDstPort,endDstPort),direction);

						break;
					case TCP:
						addPolicy(6,srcAddresses , dstAddresses, priority, action, getPortMask(startSrcPort,endSrcPort), getPortMask(startDstPort,endDstPort),direction);
						
						break;
					case UDP:
						addPolicy(17,srcAddresses , dstAddresses, priority, action, getPortMask(startSrcPort,endSrcPort), getPortMask(startDstPort,endDstPort),direction);
						
						break;
					default:
						throw new IOException();
					}

				}
			}
	//last priority
		addPolicy(-1,null,null, 65535, (node.getConfiguration().getFirewall().getDefaultAction().equals(ActionTypes.ALLOW)) ? "NORMAL" : "drop", null, null,false);

			configurationWriter.close();

		
	}
	
	
	
	
	
	





	public  String[] getPortMask(int min, int max) {
		if (min < 1 || max > 0xffff || min > max) {
			//System.out.println ( "Error range of port numbers or port number!");
			return null;
		}

		List<String> masks = new ArrayList<String>();

		for (int n = min; n <= max;) {
			int i;
			for (i=1; i <= 16; ++i) {
				int x = n + portBitMasks[i][0];
				if (x > max || (x & portBitMasks[i][1]) != (n & portBitMasks[i][1])) {
					break;
				}
			}

			// because i have a +1, so here is --i mask length
			masks.add(toPortMask(n, --i));
			// prefix at the beginning of a mask place
			n += (portBitMasks[i][0] + 1);
		}

		return masks.toArray(new String[0]);
	}


	private  String toPortMask(int port, int maskLen) {
		if (0 == maskLen) {
			return "0x" + Integer.toHexString(port);
		}

		return "0x" + Integer.toHexString(port & portBitMasks[maskLen][1]) + "/0x"
				+ Integer.toHexString(portBitMasks[maskLen][1]);
	}
 private void addPolicy(int proto,String srcAddresses , String dstAddresses, int priority,String action, String[] srcports, String[] dstports,boolean isDirectional) throws IOException {
	 String priString,srcport,dstport;
		if(isDirectional) {
			 addPolicy(proto,dstAddresses , srcAddresses,priority,action,dstports ,srcports,false);
			 isDirectional=false;
		}
	 if(priority==-1)
		 priString="";
	 else {
		 priString=new String("priority="+(65535 - priority)+",");
	 }
	 if(srcports==null)
		 srcport="";
	 else {
		 srcport= new String(",tp_src="+srcports[0]);
		 for(int i=1;i<srcports.length;i++) {
			 addPolicy(proto,srcAddresses , dstAddresses,priority,action, new String[] {srcports[i]},dstports,isDirectional);
		 }
		 srcports=new String[] {srcports[0]};
	 }
	 if(dstports==null)
		 dstport="";
	 else {
		 dstport= new String(",tp_dst="+dstports[0]);
		 for(int j=1;j<dstports.length;j++) {
			 addPolicy(proto,srcAddresses , dstAddresses,priority,action, srcports,new String[] {dstports[j]},isDirectional);
		 }
	 }
	 if(proto==-1) {
		 configurationWriter.write( "sudo ovs-ofctl add-flow "+bridgeName+" "+priString+"dl_type=0x800"+
				 ",action="+action+"\n"); 
		 System.out.println( "sudo ovs-ofctl add-flow "+bridgeName+" "+priString+"dl_type=0x800"+
				 ",action="+action+"\n"); 
	 }else {
		 configurationWriter.write( "sudo ovs-ofctl add-flow "+bridgeName+" "+priString+"dl_type=0x800,nw_src="+srcAddresses+",nw_dst="+dstAddresses+
				 ",nw_proto="+proto+srcport+dstport+",action="+action+"\n");
		 System.out.println( "sudo ovs-ofctl add-flow "+bridgeName+" "+priString+"dl_type=0x800,nw_src="+srcAddresses+",nw_dst="+dstAddresses+
				 ",nw_proto="+proto+srcport+dstport+",action="+action+"\n");
	 }

	// sudo ovs-ofctr dump-flows bridge
 }
	public String getFilename() {
		return filename;
	}
}
