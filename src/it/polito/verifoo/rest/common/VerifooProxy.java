package it.polito.verifoo.rest.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.DatatypeExpr;
import com.microsoft.z3.Status;

import it.polito.verifoo.components.RoutingTable;
import it.polito.verifoo.rest.jaxb.*;
import it.polito.verifoo.rest.logger.LoggerStream;
import it.polito.verigraph.mcnet.components.*;
import it.polito.verigraph.mcnet.netobjs.*;

public class VerifooProxy {
	    private Context ctx;
	    private NetContext nctx;
	    private Network net;
	    private HashMap<Node,NetworkObject> netobjs;
	    private HashMap<Node,List<String>> rawConditions;
	    private ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>> adm;
		public Checker check;
		private Logger logger = LogManager.getLogger("mylog");
		private List<VNF> vnfCat;
		List<List<String>> savedChain = new ArrayList<>();
		List<Node> nodes;
		List<Host> hosts;
		List<Connection> connections;
		NFFG nffg;
	    public VerifooProxy(NFFG nffg,Hosts hosts,Connections conns, VNFCatalog vnfCat) throws BadNffgException{
			HashMap<String, String> cfg = new HashMap<String, String>();
		    cfg.put("model", "true");
		    ctx = new Context(cfg);
		    nodes=nffg.getNode();
		    this.hosts = hosts.getHost();
		    this.connections = conns.getConnection();
		    this.vnfCat = vnfCat.getVNF();
		    this.nffg = nffg;
			String[] nodesname=new String[nodes.size()];
			String[] nodesip=new String[nodes.size()];
		    for(int i = 0; i < nodes.size(); i++){
		    	nodesname[i] = new String(nodes.get(i).getName());
				nodesip[i] = new String(nodes.get(i).getIp());
		    }
			nctx = new NetContext (ctx,nodesname,nodesip);
			//System.out.println(nctx.am);
			net = new Network (ctx,new Object[]{nctx});
			netobjs=new HashMap<Node,NetworkObject>();
			nodes.forEach(this::generateNetworkObject);
			adm = new ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>>();
			nodes.forEach(this::generateAddressMapping);
		    net.setAddressMappings(adm);
		    rawConditions=new HashMap<>();
			for(int i = 0; i < nodes.size(); i++){
				 List<String> conditions = new ArrayList<>();
				 rawConditions.put(nodes.get(i), conditions);
			}
			checkPhysicalNetwork();
		    checkNffg();		 
		    nodes.forEach(this::generateAcl);
		    netobjs.forEach(this::attachToNet);
		    check = new Checker(ctx,nctx,net);
	    }
	    
	    private void attachToNet(Node n,NetworkObject obj){
	    	net.attach(obj);
	    }
	    private FName getFunctionalType(String vnf){
	    	return this.vnfCat.stream().filter(nf -> nf.getName().equals(vnf)).findFirst().get().getFunctionalType(); 
	    }
	    private VNF getVNF(Node n){
			return this.vnfCat.stream().filter(nf->nf.getName().equals(n.getVNF())).findFirst().get();
	    }
		private void generateAcl(Node n){
			VNF vnf=getVNF(n);
			if(vnf.getFunctionalType().equals(FName.FW)){
				vnf.getConfiguration().forEach((c)->{
					if(c.getName()!=null && c.getValue() !=null && !c.getName().isEmpty()&& !c.getValue().isEmpty()){
				    	ArrayList<Tuple<DatatypeExpr,DatatypeExpr>> acl = new ArrayList<Tuple<DatatypeExpr,DatatypeExpr>>();
						((AclFirewall)netobjs.get(n)).addAcls(acl);
						Tuple<DatatypeExpr,DatatypeExpr> rule=new Tuple<DatatypeExpr,DatatypeExpr>(nctx.am.get(c.getName()),nctx.am.get(c.getValue()));
						acl.add(rule);
						logger.debug("Added acl:"+ rule.toString());
					}else{
						throw new IllegalArgumentException();
					} 
				});
			}
		}
	    private void generateAddressMapping(Node n){
			ArrayList<DatatypeExpr> al = new ArrayList<DatatypeExpr>();
			//System.out.println("Adding " + n.getIp() +"/"+ nctx.am.get(n.getIp()));
			al.add(nctx.am.get(n.getIp()));
			adm.add(new Tuple<>(netobjs.get(n), al));
	    }
		private void generateNetworkObject(Node n){
			FName ftype=getFunctionalType(n.getVNF());
			switch (ftype) {
				case FW:{
					netobjs.put(n,new AclFirewall(ctx,new Object[]{nctx.nm.get(n.getName()),net,nctx}));
					break;
				}
				case CLASSIFIER:{					
					netobjs.put(n,new Classifier(ctx,new Object[]{nctx.nm.get(n.getName()),net,nctx}));
					break;
				}
				case DUMB:{
					netobjs.put(n,new DumbNode(ctx,new Object[]{nctx.nm.get(n.getName()),net,nctx}));
					break;
				}
				case ENDHOST:{
					//TODO
					netobjs.put(n,new PolitoEndHost(ctx,new Object[]{nctx.nm.get(n.getName()),net,nctx}));
					break;
				}
				case SPAM:{
					PolitoAntispam spam=new PolitoAntispam(ctx,new Object[]{nctx.nm.get(n.getName()),net,nctx});
					netobjs.put(n,spam);
					int[] blacklist=getVNF(n).getConfiguration().stream().map(ConfigurationType::getValue).mapToInt(s->Integer.parseInt(s)).toArray();
					spam.installAntispam(blacklist);
					break;
				}
				case CACHE:{
					//TODO
					netobjs.put(n,new PolitoCache(ctx,new Object[]{nctx.nm.get(n.getName()),net,nctx}));
					break;
				}
				case IDS:{
					PolitoIDS ids=new PolitoIDS(ctx,new Object[]{nctx.nm.get(n.getName()),net,nctx});
					netobjs.put(n,ids);
					int[] blacklist=getVNF(n).getConfiguration().stream().map(ConfigurationType::getValue).mapToInt(s->Integer.parseInt(s)).toArray();
					ids.installIDS(blacklist);
					break;
				}
				case MAIL_CLIENT:{
					netobjs.put(n,new PolitoEndHost(ctx,new Object[]{nctx.nm.get(n.getName()),net,nctx}));
					break;
				}
				// TODO for PolitoMailClient is needed another parameter
				case MAIL_SERVER:{
					netobjs.put(n,new PolitoEndHost(ctx,new Object[]{nctx.nm.get(n.getName()),net,nctx}));
					break;
				}
				case NAT:{					
					netobjs.put(n,new PolitoNat(ctx,new Object[]{nctx.nm.get(n.getName()),net,nctx}));
					break;
				}
				case VPN:{					
					break;
				}
				case WEB_CLIENT:{
					// TODO for PolitoWebClient is needed another parameter
					netobjs.put(n,new PolitoEndHost(ctx,new Object[]{nctx.nm.get(n.getName()),net,nctx}));
					break;
				}
				case WEB_SERVER:{
					netobjs.put(n,new PolitoEndHost(ctx,new Object[]{nctx.nm.get(n.getName()),net,nctx}));
					break;
				}
				default:{
					System.err.println("Braiiinssssssssssss!");
					break;
				}
			}
		}
		
		
		private void checkPhysicalNetwork() throws BadNffgException{
            long nServer = hosts.stream()
	            	 .filter((h) -> {return h.getType() == TypeOfHost.SERVER;})
	            	 .count();
            long nClient = hosts.stream()
	            	 .filter((h) -> {return h.getType() == TypeOfHost.CLIENT;})
	            	 .count();
            /*System.out.println("nPhysicalServer: " + nServer +
            				   " nPhysicalClient: " + nClient);*/
            if(nServer != 1 || nClient != 1){
            	System.err.println("Only one client and one server is allowed in the physical network");
            	throw new BadNffgException();
            }  
            String hostClient = hosts.stream().filter(h -> {return h.getType() == TypeOfHost.CLIENT;}).findFirst().get().getName();
			String hostServer = hosts.stream().filter(h -> {return h.getType() == TypeOfHost.SERVER;}).findFirst().get().getName();
            createHostChain(hostClient, hostServer);
		}
		
		private void createHostChain(String hostClient, String hostServer){
			List<String> hostChain = new ArrayList<>();
			hostChain.add(hostClient);
			
			List<String> destinations = connections.stream()
									.filter(c -> c.getSourceHost().equals(hostClient))
									.map(c -> c.getDestHost())
									.collect(Collectors.toList());
			for(String h:destinations){
				//System.out.println("Adding dest: "+h);
				hostChain.add(h);
				expandHostChain(h, hostServer, hostChain);
				hostChain.remove(h);
			}
			System.out.println("Calculated host chain " + savedChain);
			return;
		}
		
		private void expandHostChain(String lastHost, String hostServer, List<String> hostChain){
			if(lastHost.equals(hostServer)){
				//System.out.println("Dest Reached " + lastHost);
				savedChain.add(new ArrayList<>(hostChain));
				return;
			}
			List<String> destinations = connections.stream()
									.filter(c -> c.getSourceHost().equals(lastHost))
									.map(c -> c.getDestHost())
									.collect(Collectors.toList());
			for(String h:destinations){
				if(hostChain.contains(h)){
					//System.out.println("Host already in chain "+h);
					continue;
				}
				hostChain.add(h);
				//System.out.println("Adding dest: "+h);
				//System.out.println("Host in chain: "+hostChain);
				expandHostChain(h, hostServer, hostChain);
				hostChain.remove(h);
			}
			return;
			
		}
		
		private void checkNffg() throws BadNffgException{
            long nMailServer = nodes.stream()
	            	 .filter((n) -> {return getFunctionalType(n.getVNF()) == FName.MAIL_SERVER;})
	            	 .count();
            long nWebServer = nodes.stream()
	            	 .filter((n) -> {return getFunctionalType(n.getVNF()) == FName.WEB_SERVER;})
	            	 .count();
            long nMailClient = nodes.stream()
	            	 .filter((n) -> {return getFunctionalType(n.getVNF()) == FName.MAIL_CLIENT;})
	            	 .count();
            long nWebClient = nodes.stream()
	            	 .filter((n) -> {return getFunctionalType(n.getVNF()) == FName.WEB_CLIENT;})
	            	 .count();
            /*System.out.println("nMailServer: " + nMailServer +
            				   " nMailClient: " + nMailClient +
            				   " nWebServer: " + nWebServer +
            				   " nWebClient: " + nWebClient);*/
            if(nMailServer != nMailClient || nWebServer != nWebClient || nMailServer+nWebServer>1){
            	System.err.println("Only one client and one server of the same type is allowed");
            	throw new BadNffgException();
            }
            Node client = nodes.stream().filter(n -> {return getFunctionalType(n.getVNF()) == FName.MAIL_CLIENT || getFunctionalType(n.getVNF()) == FName.WEB_CLIENT;}).findFirst().get();
            Node server = nodes.stream().filter(n -> {return getFunctionalType(n.getVNF()) == FName.MAIL_SERVER || getFunctionalType(n.getVNF()) == FName.WEB_SERVER;}).findFirst().get();
            createRoutingTables(client, server);   
		}
		
		private void createRoutingTables(Node client, Node server) throws BadNffgException{
			
			//System.out.println("Searching next hop for " + client.getName() + " towards " + server.getName());
			
			Link link = nffg.getLink().stream().filter(l -> l.getSourceNode().equals(client.getName())).findFirst().get();
			if(link == null){
				logger.error("Route: From CLIENT " + client.getName() 
									+ " to " + nctx.am.get(server.getIp()) 
									+ " -> Dead End");
				throw new BadNffgException();
			}
			Node next = nodes.stream().filter(n -> n.getName().equals(link.getDestNode()) ).findFirst().get();
			//System.out.println("Route from CLIENT " + client.getName() 
			//								+ " to " + nctx.am.get(server.getIp()) 
			//								+ " -> next hop: " + netobjs.get(next));
			String hostClient = hosts.stream().filter(h -> {return h.getType() == TypeOfHost.CLIENT;}).findFirst().get().getName();
			String hostServer = hosts.stream().filter(h -> {return h.getType() == TypeOfHost.SERVER;}).findFirst().get().getName();
			//System.out.println("The host client is: " + hostClient+" and the host server is "+hostServer);
			for(int i = 0; i < savedChain.size(); i++){
				String host1 = savedChain.get(i).get(1);
				//System.out.println("Chain -> " + savedChain.get(i));
				if(setNextHop(next, server, i, 1, hostServer)){
					/*System.out.println("Route from " + client.getName() 
					+ " to " + nctx.am.get(server.getIp()) 
					+ " -> next hop: " + netobjs.get(next));
					System.out.println("From " + hostClient + " to " + host1);
					*/
					//System.out.print("On RT("+next.getName()+") ");
					//System.out.println(next.getName()+"@"+host1);
					rawConditions.get(client).add(next.getName()+"@"+host1);
				}
			}	
			for(Node n : rawConditions.keySet()){
				ArrayList<RoutingTable> rt = new ArrayList<RoutingTable>();
				//System.out.println("-----NODE "+n.getName()+"-----");
				List<String> cond = rawConditions.get(n).stream().distinct().collect(Collectors.toList());
				System.out.println("Condition for node "+ n.getName() +" -> "+ cond);
				for(String s:cond){
					BoolExpr c;
					int latency = 0;
					if(s.lastIndexOf('/') != -1){
						String first = s.substring(0, s.lastIndexOf('/'));
						String second = s.substring(s.lastIndexOf('/')+1);
						String firstHost = first.substring(first.lastIndexOf('@')+1);
						String secondHost = second.substring(second.lastIndexOf('@')+1);
						if(firstHost.equals(secondHost)){
							latency = 0;
						}
						else{
							latency = connections.stream()
									.filter(con -> con.getSourceHost().equals(firstHost) && con.getDestHost().equals(secondHost)).findFirst().get().getAvgLatency();
							
						}
						//System.out.println("Adding (" + first + " AND " + second+") to the routing table");
						c = ctx.mkAnd(ctx.mkBoolConst(first), ctx.mkBoolConst(second));
					}
					else{
						String host = s.substring(s.lastIndexOf('@')+1);
						if(n.getName().equals(client.getName())){
							latency = connections.stream()
									.filter(con -> con.getSourceHost().equals(hostClient) && con.getDestHost().equals(host)).findFirst().get().getAvgLatency();
						}
						else{
							latency = connections.stream()
									.filter(con -> con.getSourceHost().equals(host) && con.getDestHost().equals(hostServer)).findFirst().get().getAvgLatency();
						}
						//System.out.println("Adding "+s+" to the routing table");
						c = ctx.mkBoolConst(s);
					}
					rt.add(new RoutingTable(nctx.am.get(server.getIp()), netobjs.get(next), latency, c));
				}
				//System.out.println("Adding routing table to "+n.getName());
				//should we use routingOptimization() or routingTable2()?
				net.routingTable2(netobjs.get(n), rt);
			}
			
		}

		private boolean setNextHop(Node source, Node server, int nChain, int level, String hostServer) throws BadNffgException{
			String currentHost = savedChain.get(nChain).get(level);
			//System.out.println("Searching next hop for " + source.getName() + " towards " + server.getName());
			if(source.getName().equals(server.getName())){
				if(currentHost.equals(hostServer)){
					//System.out.println("Route from SERVER " + source.getName() + " to " + nctx.am.get(server.getIp())  + " -> next hop: DESTINATION REACHED" + " CurrentHost: " + currentHost);
					//System.out.println("Found path from lv " + level + " of chain " +nChain );
					return true;
				}
				else{
					//System.out.println("Path not found path from lv " + level + " of chain " +nChain );
					return false;
				}
			}
			if(currentHost.equals(hostServer)){
				//System.out.println("Only server node can be deployed on server host -> tried to deploy " + source.getName() + " on " +currentHost );
				return false;
			}
			Link link = nffg.getLink().stream().filter(l -> l.getSourceNode().equals(source.getName())).findFirst().get();
			if(link == null){
				logger.error("Route: From " + source.getName() 
									+ " to " + nctx.am.get(server.getIp()) 
									+ " -> Dead End");
				throw new BadNffgException();
			}
			Node next = nodes.stream().filter(n -> n.getName().equals(link.getDestNode()) ).findFirst().get();
			//System.out.println("Route from " + source.getName()+ " to " + nctx.am.get(server.getIp())+ " -> next hop: " + netobjs.get(next));
			boolean found = false;
			for(int i = level; i < savedChain.get(nChain).size() && i <= level+1; i++){
				String nextHost = savedChain.get(nChain).get(i);
				//System.out.println("RECURSION -> Deploying " + next.getName() +" on lv " + i + " of chain " +nChain +"("+nextHost+")");
				if(setNextHop(next, server, nChain, i, hostServer)){
					//System.out.println("From " + currentHost + " to " + nextHost);
					//System.out.print("On RT("+source.getName()+") ");
					if(nextHost.equals(hostServer)){
						//System.out.println(source.getName()+"@"+currentHost);
						rawConditions.get(source).add(source.getName()+"@"+currentHost);
					}
					else{
						//System.out.println(source.getName()+"@"+currentHost + " AND " + next.getName()+"@"+savedChain.get(nChain).get(i));
						rawConditions.get(source).add(source.getName()+"@"+currentHost + "/" + next.getName()+"@"+savedChain.get(nChain).get(i));
					}
					found = true;
				}
			}
			return found;
		}
		
		public String checkNFFGProperty(){

            Node source = nodes.stream().filter(n -> {return getFunctionalType(n.getVNF()) == FName.MAIL_CLIENT || getFunctionalType(n.getVNF()) == FName.WEB_CLIENT;}).findFirst().get();
            Node dest = nodes.stream().filter(n -> {return getFunctionalType(n.getVNF()) == FName.MAIL_SERVER || getFunctionalType(n.getVNF()) == FName.WEB_SERVER;}).findFirst().get();
            
			IsolationResult ret = this.check.checkIsolationProperty(netobjs.get(source), netobjs.get(dest));
			if (ret.result == Status.UNSATISFIABLE){
		     	   System.out.println("UNSAT"); // Nodes a and b are isolated
		    }else{
		     		System.out.println("SAT ");
		     		logger.debug( ""+ret.model); //p.printModel(ret.model);
		    }
			return ret.model.toString();
		}
		
}
