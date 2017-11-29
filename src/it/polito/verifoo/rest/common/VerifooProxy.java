package it.polito.verifoo.rest.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Status;

import it.polito.verifoo.components.RoutingTable;
import it.polito.verifoo.rest.jaxb.*;
import it.polito.verigraph.mcnet.components.*;

public class VerifooProxy {
	    private Context ctx;
	    private NetContext nctx;
	    private Network net;
	    private NodeNetworkObject netobjs;
	    private HashMap<Node,List<String>> rawConditions;
	    private HashMap<Node, HashMap<String, BoolExpr>> conditionDB;
		public Checker check;
		private Logger logger = LogManager.getLogger("mylog");
		List<List<String>> savedChain = new ArrayList<>();
		List<Node> nodes;
		List<Link> links = new ArrayList<>();
		List<Host> hosts;
		List<Connection> connections;
		Graph graph;
	    public VerifooProxy(Graph graph,Hosts hosts,Connections conns) throws BadNffgException{
			HashMap<String, String> cfg = new HashMap<String, String>();
		    cfg.put("model", "true");
		    ctx = new Context(cfg);
		    nodes=graph.getNode();
		    this.hosts = hosts.getHost();
		    this.connections = conns.getConnection();
		    this.graph=graph;
			nctx = NetContextGenerator.generate(ctx,nodes);
				
			//System.out.println(nctx.am);
			net = new Network (ctx,new Object[]{nctx});
			
			/* Generate the different network object and map it to XML Node */
			netobjs=new NodeNetworkObject(ctx, nctx, net,nodes);
			
			AddressMapping adm = new AddressMapping(netobjs, nctx, net);
			adm.setAddressMappings(nodes);
		    
		    
		    rawConditions=new HashMap<>();
		    conditionDB=new HashMap<>();
		    nodes.forEach(n -> {
				rawConditions.put(n, new ArrayList<>());
		    	conditionDB.put(n, new HashMap<>());
		    });
			checkPhysicalNetwork();
		    checkNffg();	
		    setConditions();
		    netobjs.generateAcl();
		    //TODO: check
		    netobjs.generateCache();
		    netobjs.attachToNet();
		    check = new Checker(ctx,nctx,net);
	    }
	    
		private void setConditions() throws BadNffgException{
		  	
			HashMap<String, BoolExpr> hostCondition = new HashMap<>();
			hosts.forEach(h -> {
				BoolExpr e = ctx.mkBoolConst(h.getName());
				hostCondition.put(h.getName(),e);
				//nctx.softConstraints.add(new Tuple<BoolExpr, String>(ctx.mkNot(e), "servers"));
			});
			System.out.println("Host constraint: " + hostCondition);
			conditionDB.entrySet().forEach(e -> {
				List<IntExpr> univocity = new ArrayList<>();
				e.getValue().entrySet().stream()
										.map(pair -> pair.getValue())
										.collect(Collectors.toList())
										.forEach(c -> {
											univocity.add(nctx.bool_to_int(c));
										});
				//System.out.println(e.getKey().getName() + " univocity: " + univocity);
				ArithExpr uniqueNodeConstraint = null;
				for(IntExpr u:univocity){
					if(uniqueNodeConstraint == null){
						uniqueNodeConstraint = u;
					}
					else{
						uniqueNodeConstraint = ctx.mkAdd(uniqueNodeConstraint, u);
					}
					//System.out.println(uniqueNodeConstraint);
				}
				if(uniqueNodeConstraint != null){
					System.out.println(e.getKey().getName() + " adding univocity: " + ctx.mkEq(uniqueNodeConstraint, ctx.mkInt(1)));
					nctx.constraints.add(ctx.mkEq(uniqueNodeConstraint, ctx.mkInt(1)));
				}
			});
			hosts.forEach(h -> {
				List<BoolExpr> implications = new ArrayList<>();
				conditionDB.entrySet().stream()
									.flatMap(e -> e.getValue().entrySet().stream())
									.filter(e -> e.getKey().equals(h.getName()))
									.map(e -> e.getValue())
									.collect(Collectors.toList())
									.forEach(i -> {
										implications.add(ctx.mkImplies(hostCondition.get(h.getName()), i));
									});
				//System.out.println(h.getName() + " implication: " + implications);
				BoolExpr hostImpliesNodeConstraint = null;
				for(BoolExpr i:implications){
					if(hostImpliesNodeConstraint == null){
						hostImpliesNodeConstraint = i;
					}
					else{
						hostImpliesNodeConstraint = ctx.mkOr(hostImpliesNodeConstraint, i);
					}
					//System.out.println(hostImpliesNodeConstraint);
				}
				if(hostImpliesNodeConstraint != null){
					System.out.println(h.getName() + " implication: " + hostImpliesNodeConstraint);
					nctx.constraints.add(hostImpliesNodeConstraint);
				}
			});
			
			hosts.forEach(h -> {
				List<ArithExpr> diskRequirements = new ArrayList<>();
				conditionDB.entrySet().stream()
									.flatMap(e -> e.getValue().entrySet().stream())
									.filter(e -> e.getKey().equals(h.getName()))
									.map(e -> e.getValue())
									.collect(Collectors.toList())
									.forEach(i -> {
										String node = i.toString().substring(0, i.toString().lastIndexOf('@'));
										int capacity = nodes.stream().filter(n -> n.getName().equals(node)).findFirst().get().getReqDiskStorage();
										diskRequirements.add(ctx.mkMul(ctx.mkInt(capacity), nctx.bool_to_int(i)));
									});
				System.out.println(h.getName() + " disk requirement: " + diskRequirements);
				ArithExpr diskConstraint = null;
				for(ArithExpr d:diskRequirements){
					if(diskConstraint == null){
						diskConstraint = d;
					}
					else{
						diskConstraint = ctx.mkAdd(diskConstraint, d);
					}
					//System.out.println(hostImpliesNodeConstraint);
				}
				if(diskConstraint != null){
					System.out.println(h.getName() + " left side: " + diskConstraint);
					System.out.println(h.getName() + " requirements: " + ctx.mkLe(diskConstraint, ctx.mkMul(ctx.mkInt(h.getDiskStorage()), nctx.bool_to_int(hostCondition.get(h.getName())))));
					nctx.constraints.add(ctx.mkLe(diskConstraint, ctx.mkMul(ctx.mkInt(h.getDiskStorage()), nctx.bool_to_int(hostCondition.get(h.getName())))));
				}
			});
			
			
			hosts.forEach(h -> {
				nctx.softConstraints.add(new Tuple<BoolExpr, String>(ctx.mkNot(hostCondition.get(h.getName())), "num_servers"));
			});
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
	            	 .filter((n) -> n.getFunctionalType().equals(FunctionalTypes.MAILSERVER))
	            	 .count();
            long nWebServer = nodes.stream()
	            	 .filter((n) -> n.getFunctionalType().equals(FunctionalTypes.WEBSERVER))
	            	 .count();
            long nMailClient = nodes.stream()
	            	 .filter((n) -> n.getFunctionalType().equals(FunctionalTypes.MAILCLIENT))
	            	 .count();
            long nWebClient = nodes.stream()
	            	 .filter((n) -> n.getFunctionalType().equals(FunctionalTypes.WEBCLIENT))
	            	 .count();
            /*System.out.println("nMailServer: " + nMailServer +
            				   " nMailClient: " + nMailClient +
            				   " nWebServer: " + nWebServer +
            				   " nWebClient: " + nWebClient);*/
            if(nMailServer != nMailClient || nWebServer != nWebClient || nMailServer+nWebServer>1){
            	System.err.println("Only one client and one server of the same type is allowed");
            	throw new BadNffgException();
            }
            Node client = nodes.stream().filter(n -> {return n.getFunctionalType().equals(FunctionalTypes.MAILCLIENT) || n.getFunctionalType().equals(FunctionalTypes.WEBCLIENT);}).findFirst().get();
            Node server = nodes.stream().filter(n -> {return n.getFunctionalType().equals(FunctionalTypes.MAILSERVER) || n.getFunctionalType().equals(FunctionalTypes.WEBSERVER);}).findFirst().get();
            if(client.getNeighbour().size() != 1 || server.getNeighbour().size() != 1) throw new BadNffgException();
            String nextName = client.getNeighbour().stream().filter(n -> !(n.getName().equals(client.getName()))).findFirst().get().getName();
			Node next = nodes.stream().filter(n -> n.getName().equals(nextName)).findFirst().get();
            createLink(client, next, server);
            createRoutingTables(client, server);   
		}
		private void createLink(Node prec, Node current, Node server) throws BadNffgException{
			if(current.getName().equals(server.getName())){
				links.add(new Link(prec.getName(), current.getName()));
				return;
			}
			if(current.getNeighbour().size() > 2) throw new BadNffgException();
			System.out.println("New Link from " + prec.getName() + " to "+ current.getName() +" towards server "+server.getName());
			links.add(new Link(prec.getName(), current.getName()));
			String neighbour = current.getNeighbour().stream().filter(n -> !(n.getName().equals(prec.getName()))).findFirst().get().getName();
			Node next = nodes.stream().filter(n -> n.getName().equals(neighbour)).findFirst().get();
			createLink(current, next, server);
		}
		private void createRoutingTables(Node client, Node server) throws BadNffgException{
			
			//System.out.println("Searching next hop for " + client.getName() + " towards " + server.getName());
			
			Link link = links.stream().filter(l -> l.getSourceNode().equals(client.getName())).findFirst().get();
			if(link == null){
				logger.error("Route: From CLIENT " + client.getName() 
									+ " to " + nctx.am.get(server.getName()) 
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
				System.out.println("-----NODE "+n.getName()+"-----");
				List<String> cond = rawConditions.get(n).stream().distinct().collect(Collectors.toList());
				System.out.println("Condition for "+ n.getName() +" -> "+ cond);
				for(String s:cond){
					BoolExpr c;
					int latency = 0;
					if(s.lastIndexOf('/') != -1){
						String first = s.substring(0, s.lastIndexOf('/'));
						String second = s.substring(s.lastIndexOf('/')+1);
						String firstNode = first.substring(0,first.lastIndexOf('@'));
						String secondNode = second.substring(0, second.lastIndexOf('@'));
						String firstHost = first.substring(first.lastIndexOf('@')+1);
						String secondHost = second.substring(second.lastIndexOf('@')+1);
						if(firstHost.equals(secondHost)){
							latency = 0;
						}
						else{
							latency = connections.stream()
									.filter(con -> con.getSourceHost().equals(firstHost) && con.getDestHost().equals(secondHost)).findFirst().get().getAvgLatency();
						}
						System.out.println("Adding (" + first + " AND " + second+") to the routing table");
						c = ctx.mkAnd(ctx.mkBoolConst(first), ctx.mkBoolConst(second));
						if(n.getName().equals(firstNode)){
							conditionDB.get(n).put(firstHost, ctx.mkBoolConst(first));
						}
						if(n.getName().equals(secondNode)){
							conditionDB.get(n).put(secondHost, ctx.mkBoolConst(second));
						}
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
						System.out.println("Adding "+s+" to the routing table");
						c = ctx.mkBoolConst(s);
						if(n != client && n!= server){
							conditionDB.get(n).put(host, c);
						}
						
					}
					Link l = links.stream().filter(li -> li.getSourceNode().equals(n.getName())).findFirst().get();
					next = nodes.stream().filter(node -> node.getName().equals(l.getDestNode()) ).findFirst().get();
					rt.add(new RoutingTable(nctx.am.get(server.getName()), netobjs.get(next), nctx.addLatency(latency), c));
					
				}
				//System.out.println("Adding routing table to "+n.getName());
				net.routingOptimization(netobjs.get(n), rt);
			}
			System.out.println("----CONDITION DB----");
			conditionDB.entrySet().forEach(e -> {System.out.println(e.getKey().getName() + " -> " + e.getValue());});
			System.out.println("--------------------");
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
			Link link = links.stream().filter(l -> l.getSourceNode().equals(source.getName())).findFirst().orElse(null);
			if(link == null){
				logger.error("Route: From " + source.getName() 
									+ " to " + nctx.am.get(server.getName()) 
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
		
		public IsolationResult checkNFFGProperty(){

            Node source = nodes.stream().filter(n -> {return n.getFunctionalType().equals(FunctionalTypes.MAILCLIENT)|| n.getFunctionalType().equals(FunctionalTypes.WEBCLIENT);}).findFirst().get();
            Node dest = nodes.stream().filter(n -> {return n.getFunctionalType().equals(FunctionalTypes.MAILSERVER) || n.getFunctionalType().equals(FunctionalTypes.WEBSERVER);}).findFirst().get();
            System.out.println("Checking reachability from " + source.getName() + " to "+ dest.getName());
			IsolationResult ret = this.check.checkIsolationProperty(netobjs.get(source), netobjs.get(dest));
			if (ret.result == Status.UNSATISFIABLE){
		     	   System.out.println("UNSAT"); // Nodes a and b are isolated
		    }else{
		     		System.out.println("SAT ");
		     		logger.debug( ""+ret.model); //p.printModel(ret.model);
		    }
			return ret;
		}

		public NetContext getNctx() {
			return nctx;
		}
		
}
