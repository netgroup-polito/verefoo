package it.polito.verifoo.rest.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.DatatypeExpr;
import com.microsoft.z3.Status;

import it.polito.verifoo.components.RoutingTable;
import it.polito.verifoo.rest.jaxb.*;
import it.polito.verifoo.rest.jaxb.BandwidthConstraints.BandwidthMetrics;
import it.polito.verifoo.rest.jaxb.NodeConstraints.NodeMetrics;
import it.polito.verigraph.mcnet.components.*;
import it.polito.verigraph.mcnet.components.Checker.Prop;
/**
 * 
 * This is the main class that will interface with the Verifoo classes
 *
 */
public class VerifooProxy {
	    private Context ctx;
	    private NetContext nctx;
	    private Network net;
	    private AutoContext autoctx;
	    private NodeNetworkObject netobjs;
	    private HashMap<Node,List<String>> rawConditions;
	    private HashMap<Node, HashMap<String, BoolExpr>> conditionDB;
	    private HashMap<Node, HashMap<String, BoolExpr>> stageConditions;
	    private HashMap<String, Integer> countConditions;
		public Checker check;
		private Logger logger = LogManager.getLogger("mylog");
		private List<List<String>> savedChain = new ArrayList<>();
		private List<List<String>> savedNodeChain = new ArrayList<>();
		private HashMap<Node, HashMap<Node, List<Node>>> routingRule = new HashMap<>();
		private List<Node> nodes;
		//private List<Link> links = new ArrayList<>();
		private LinkProvider linkProvider;
		private List<Host> hosts;
		private List<Connection> connections;
		private Graph graph;
		private List<Path> paths;
		int clientServerCombinations = 0;
		private List<NodeMetrics> nodeMetrics;
		private List<BandwidthMetrics> bandwidthMetrics;
		private int nrOfConditions;
		private ConditionStringBuilder cb;
		private HashMap<Node, Tuple<ArrayList<RoutingTable>, ArrayList<BandwidthMetrics>>> routingMap;
		/**
		 * Public constructor for the verifoo proxy service
		 * @param graph The graph that will be deployed on the network
		 * @param hosts The list of hosts in the network
		 * @param conns The connections between hosts
		 * @param paths 
		 * @param capacityDefinition The list of the capacity for each node that will be deployed
		 * @throws BadGraphError
		 */
	    public VerifooProxy(Graph graph,Hosts hosts,Connections conns, Constraints constraints, List<Property> prop, List<Path> paths) throws BadGraphError{
	    	//System.loadLibrary("z3");
        	//System.loadLibrary("z3java");
	    	HashMap<String, String> cfg = new HashMap<String, String>();
		    cfg.put("model", "true");
		    ctx = new Context(cfg);
		    nodes=graph.getNode();
		    this.hosts = hosts.getHost();
		    this.connections = conns.getConnection();
		    this.graph=graph;
		    this.nodeMetrics = constraints.getNodeConstraints().getNodeMetrics();
		    this.bandwidthMetrics = constraints.getBandwidthConstraints().getBandwidthMetrics();
		    this.paths = paths;
			nctx = NetContextGenerator.generate(ctx,nodes);
			autoctx = new AutoContext(ctx);
			net = new Network (ctx,new Object[]{nctx});
			/* Generate the different network object and map it to XML Node */
			netobjs=new NodeNetworkObject(ctx, nctx, autoctx, net,nodes, prop.size(), nodeMetrics, prop);
			
			AddressMapping adm = new AddressMapping(netobjs, nctx, net);
			adm.setAddressMappings(nodes);
		    
			routingMap = new HashMap<>();
		    rawConditions = new HashMap<>();
		    conditionDB = new HashMap<>();
		    stageConditions = new HashMap<>();
		    countConditions = new HashMap<>();
		    nodes.forEach(n -> {
				rawConditions.put(n, new ArrayList<>());
		    	conditionDB.put(n, new HashMap<>());
		    	stageConditions.put(n, new HashMap<>());
		    });
			cb = new ConditionStringBuilder(ctx, autoctx, connections, rawConditions);
			checkPhysicalNetwork();
		    checkNffg();	
		    netobjs.generateVPN();
		    netobjs.attachToNet();
		    setConditions();
		    check = new Checker(ctx,nctx,net);
		    check.setAutoctx(autoctx);
		    setProperty(prop);
	    }
	    /**
	     * Sets all the conditions for the nodes. First, it sets up the host conditions. 
	     * Then, it creates the conditions to make that a node is deployed only on one host.
	     * After that, it creates the condition that if an host has a node deployed on it, it has to be active.
	     * Lastly, it creates the conditions that the sum of the disk requirements of all the nodes
	     * deployed on an host, is less than disk capacity of that host 
	     * @throws BadGraphError
	     */
		private void setConditions() throws BadGraphError{
			Map<String, List<Tuple<BoolExpr, BoolExpr>>> dependencies = autoctx.getDependencies();
			for(Node n:conditionDB.keySet()){
				for(String h:conditionDB.get(n).keySet()){
					if(countConditions.get(n.getName()+"_"+h) < clientServerCombinations && !ChainExtractor.hostIsServer(h)){
						logger.debug("Found condition that is not valid for all the client/server combinations " + conditionDB.get(n).get(h) +" -> making it false");
						nctx.constraints.add(ctx.mkEq(conditionDB.get(n).get(h), ctx.mkFalse()));
					}
				}
			}
			
			logger.debug("----CONDITION DB----");
			conditionDB.entrySet().forEach(e -> {
				e.getValue().forEach((h,c) -> {
					List<Tuple<BoolExpr, BoolExpr>> list = dependencies.get(e.getKey().getName()+"@"+h);
					if(list != null){
						List<BoolExpr> dep = list.stream().map(t -> t._2).collect(Collectors.toList());
						logger.debug("Dependencies for " + e.getKey().getName()+ "@" + h + ": " + dep);
						BoolExpr tmp[] = new BoolExpr[dep.size()];
						BoolExpr exp = ctx.mkImplies(c, ctx.mkOr(dep.toArray(tmp)));
						//System.out.println("Adding " + exp);
						nctx.constraints.add(exp);
					}
				});
				logger.debug(e.getKey().getName() + " -> " + e.getValue());
			});
			logger.debug("--------------------");
			HashMap<String, BoolExpr> hostCondition = new HashMap<>();
			HashMap<String, BoolExpr> hostSupportedVNF = new HashMap<>();
			hosts.forEach(h -> {
				BoolExpr e = ctx.mkBoolConst(h.getName());
				hostCondition.put(h.getName(),e);
				nctx.softConstraints.add(new Tuple<BoolExpr, String>(ctx.mkNot(e), "servers"));
				
				if(h.getNodeRef().size() > 0){
					h.getNodeRef().forEach(nr ->{
						Node n = nodes.stream().filter(n1 -> n1.getName().equals(nr)).findFirst().orElse(null);
						if(n != null && conditionDB.get(n) != null){
							if(conditionDB.get(n).get(h) != null)
								logger.debug(h.getName()+" has already " + nr + " deployed -> making the corrispondent condition true");
								nctx.constraints.add(ctx.mkEq(conditionDB.get(n).get(h), ctx.mkTrue()));
						}
					});
				}
				
				List<FunctionalTypes> tmp = h.getSupportedVNF().stream().map(s -> s.getFunctionalType()).collect(Collectors.toList());
				for(FunctionalTypes f: FunctionalTypes.values()){
					BoolExpr c = ctx.mkBoolConst(h.getName()+"_composition_"+f);
					if(tmp.contains(f)){
						//logger.debug(h.getName() + " supports " + f + " -> " + ctx.mkEq(c, ctx.mkTrue()));
						nctx.constraints.add(ctx.mkEq(c, ctx.mkTrue()));
					}
					else{
						if(h.getType().equals(TypeOfHost.SERVER) && (f.equals(FunctionalTypes.WEBSERVER) || f.equals(FunctionalTypes.MAILSERVER))){
							logger.debug(h.getName() + " supports " + f + " -> " + ctx.mkEq(c, ctx.mkTrue()));
							nctx.constraints.add(ctx.mkEq(c, ctx.mkTrue()));
						}else{
							nctx.constraints.add(ctx.mkEq(c, ctx.mkFalse()));
						}
					}
					hostSupportedVNF.put(h.getName()+"_composition_"+f, c);
				}
				/*if(h.getType().equals(TypeOfHost.SERVER)){
					BoolExpr c = ctx.mkBoolConst(h.getName()+"_composition_"+FunctionalTypes.WEBSERVER);
					logger.debug(h.getName() + " supports " + FunctionalTypes.WEBSERVER + " -> " + ctx.mkEq(c, ctx.mkTrue()));
					hostSupportedVNF.put(h.getName()+"_composition_"+FunctionalTypes.WEBSERVER, c);
					c = ctx.mkBoolConst(h.getName()+"_composition_"+FunctionalTypes.MAILSERVER);
					nctx.constraints.add(ctx.mkEq(c, ctx.mkTrue()));
					logger.debug(h.getName() + " supports " + FunctionalTypes.MAILSERVER + " -> " + ctx.mkEq(c, ctx.mkTrue()));
					hostSupportedVNF.put(h.getName()+"_composition_"+FunctionalTypes.MAILSERVER, c);
					nctx.constraints.add(ctx.mkEq(hostCondition.get(h.getName()), ctx.mkTrue()));
				}*/
				
			});
			//logger.debug("Host constraint: " + hostCondition);
			conditionDB.entrySet().forEach(e -> {
				List<IntExpr> univocity = new ArrayList<>();
				Map<ArithExpr, ArithExpr> cpuRequirements = new HashMap<>();
				Node nodeCond = e.getKey();
				e.getValue().entrySet().stream()
										.map(pair -> pair.getValue())
										.collect(Collectors.toList())
										.forEach(c -> {
											String node = c.toString().substring(0, c.toString().lastIndexOf('@'));
											String host = c.toString().substring(c.toString().lastIndexOf('@')+1);
											NodeMetrics n = nodeMetrics.stream().filter(n1 -> n1.getNode().equals(node)).findFirst().orElse(null);
											Host h = hosts.stream().filter(h1 -> h1.getName().equals(host)).findFirst().orElse(null);
											univocity.add(nctx.bool_to_int(c));
											long nodeCurrLatency; 
											if(n == null || n.getNrOfOperations() == null)
												nodeCurrLatency = 0;
											else{
												//the host cpu power is expressed in GHz
												nodeCurrLatency = n.getNrOfOperations()/((long) h.getCpu()*1000000000);
											}
											if( n == null || n.getMaxNodeLatency() == null ){
												cpuRequirements.put(ctx.mkMul(ctx.mkInt((int) nodeCurrLatency),nctx.bool_to_int(c)), ctx.mkMul(ctx.mkInt((int) nodeCurrLatency),nctx.bool_to_int(c)));
											}
											else{
												cpuRequirements.put(ctx.mkMul(ctx.mkInt((int) nodeCurrLatency),nctx.bool_to_int(c)), ctx.mkMul(ctx.mkInt(n.getMaxNodeLatency()),nctx.bool_to_int(c)));
											}
										});
				if(univocity.size() > 0){
					ArithExpr[] tmp = new ArithExpr[univocity.size()];
					ArithExpr uniqueNodeConstraint = ctx.mkAdd(univocity.toArray(tmp));
					if(autoctx.nodeIsOptional(e.getKey())){
						NetworkObject no = netobjs.get(e.getKey());
						logger.debug(e.getKey().getName() + " adding soft constraint for minimize the nr of vnf: " + ctx.mkEq(nctx.bool_to_int(ctx.mkNot(no.isUsed())), ctx.mkInt(1)));
						logger.debug(e.getKey().getName() + " adding OPTIONAL univocity: " + ctx.mkEq(ctx.mkAdd(uniqueNodeConstraint, nctx.bool_to_int(ctx.mkNot(no.isUsed()))), ctx.mkInt(1)));
						autoctx.constraints.add(ctx.mkEq(ctx.mkAdd(uniqueNodeConstraint, nctx.bool_to_int(ctx.mkNot(no.isUsed()))), ctx.mkInt(1)));
						autoctx.softConstrAutoPlace.add(new Tuple<BoolExpr, String>(ctx.mkEq(nctx.bool_to_int(ctx.mkNot(no.isUsed())), ctx.mkInt(1)), "optionalPlacement"));
						//nctx.constraints.add(ctx.mkEq(ctx.mkAdd(uniqueNodeConstraint, nctx.bool_to_int(ctx.mkNot(no.isUsed()))), ctx.mkInt(1)));
						//nctx.softConstrAutoPlace.add(new Tuple<BoolExpr, String>(ctx.mkEq(nctx.bool_to_int(ctx.mkNot(no.isUsed())), ctx.mkInt(1)), "optionalPlacement"));
					}else{
						logger.debug(e.getKey().getName() + " adding univocity: " + ctx.mkEq(uniqueNodeConstraint, ctx.mkInt(1)));
						nctx.constraints.add(ctx.mkEq(uniqueNodeConstraint, ctx.mkInt(1)));
					}
					
				}
				if(cpuRequirements.size() > 0){
					cpuRequirements.forEach((k,v) -> {
						//logger.debug("Cpu requirements: " + ctx.mkLe(k, v));
						nctx.constraints.add(ctx.mkLe(k, v));
					});
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
										String cond = i.toString().replace("|", "");
										String node = cond.substring(cond.indexOf(" ")+1, cond.lastIndexOf('@'));
										FunctionalTypes f = nodes.stream().filter(n1 -> n1.getName().equals(node)).findFirst().get().getFunctionalType();
										implications.add(ctx.mkImplies(hostCondition.get(h.getName()), i));
										logger.debug(i + " => " + hostSupportedVNF.get(h.getName()+"_composition_"+f));
										nctx.constraints.add(ctx.mkImplies(i, hostSupportedVNF.get(h.getName()+"_composition_"+f)));
									});
				//System.out.println(h.getName() + " implication: " + implications);
				if(implications.size() > 0){
					BoolExpr[] tmp = new BoolExpr[implications.size()];
					BoolExpr hostImpliesNodeConstraint = ctx.mkOr(implications.toArray(tmp));
					logger.debug(h.getName() + " implication: " + hostImpliesNodeConstraint);
					nctx.constraints.add(hostImpliesNodeConstraint);
				}
			});
			
			hosts.forEach(h -> {
				List<ArithExpr> diskRequirements = new ArrayList<>();
				List<ArithExpr> maxVNFRequirements = new ArrayList<>();
				List<ArithExpr> coreRequirements = new ArrayList<>();
				List<ArithExpr> memoryRequirements = new ArrayList<>();
				conditionDB.entrySet().stream()
									.flatMap(e -> e.getValue().entrySet().stream())
									.filter(e -> e.getKey().equals(h.getName()))
									.map(e -> e.getValue())
									.collect(Collectors.toList())
									.forEach(i -> {
										String node = i.toString().substring(0, i.toString().lastIndexOf('@'));
										NodeMetrics n = nodeMetrics.stream().filter(n1 -> n1.getNode().equals(node)).findFirst().orElse(null);
										if(n != null)
											diskRequirements.add(ctx.mkMul(ctx.mkInt(n.getReqStorage()), nctx.bool_to_int(i)));
										maxVNFRequirements.add(nctx.bool_to_int(i));
										if(n != null)
											coreRequirements.add(ctx.mkMul(ctx.mkInt(n.getCores()),nctx.bool_to_int(i)));
										if(n != null)
											memoryRequirements.add(ctx.mkMul(ctx.mkInt(n.getMemory()), nctx.bool_to_int(i)));
										
									});
				//logger.debug(h.getName() + " disk requirement: " + diskRequirements);
				if(diskRequirements.size() > 0){
					ArithExpr[] tmp = new ArithExpr[diskRequirements.size()];
					ArithExpr diskConstraint = ctx.mkAdd(diskRequirements.toArray(tmp));
					//logger.debug(h.getName() + " left side: " + diskConstraint);
					//logger.debug(h.getName() + " disk requirements: " + ctx.mkLe(diskConstraint, ctx.mkMul(ctx.mkInt(h.getDiskStorage()), nctx.bool_to_int(hostCondition.get(h.getName())))));
					nctx.constraints.add(ctx.mkLe(diskConstraint, ctx.mkMul(ctx.mkInt(h.getDiskStorage()), nctx.bool_to_int(hostCondition.get(h.getName())))));
				}
				if(maxVNFRequirements.size() > 0){
					ArithExpr[] tmp = new ArithExpr[maxVNFRequirements.size()];
					ArithExpr maxVNFConstraint = ctx.mkAdd(maxVNFRequirements.toArray(tmp));
					//logger.debug(h.getName() + " left side: " + diskConstraint);
					if(h.getMaxVNF() != null){
						//logger.debug(h.getName() + " max VNF requirements: " + ctx.mkLe(maxVNFConstraint, ctx.mkMul(ctx.mkInt(h.getMaxVNF()), nctx.bool_to_int(hostCondition.get(h.getName())))));
						nctx.constraints.add(ctx.mkLe(maxVNFConstraint, ctx.mkMul(ctx.mkInt(h.getMaxVNF()), nctx.bool_to_int(hostCondition.get(h.getName())))));
					}
				}
				if(coreRequirements.size() > 0){
					ArithExpr[] tmp = new ArithExpr[coreRequirements.size()];
					ArithExpr coreConstraint = ctx.mkAdd(coreRequirements.toArray(tmp));
					//logger.debug(h.getName() + " left side: " + diskConstraint);
					//logger.debug(h.getName() + " core requirements: " + ctx.mkLe(coreConstraint, ctx.mkMul(ctx.mkInt(h.getCores()), nctx.bool_to_int(hostCondition.get(h.getName())))));
					nctx.constraints.add(ctx.mkLe(coreConstraint, ctx.mkMul(ctx.mkInt(h.getCores()), nctx.bool_to_int(hostCondition.get(h.getName())))));
				}
				if(memoryRequirements.size() > 0){
					ArithExpr[] tmp = new ArithExpr[memoryRequirements.size()];
					ArithExpr memoryConstraint = ctx.mkAdd(memoryRequirements.toArray(tmp));
					//logger.debug(h.getName() + " left side: " + diskConstraint);
					//logger.debug(h.getName() + " disk requirements: " + ctx.mkLe(memoryConstraint, ctx.mkMul(ctx.mkInt(h.getMemory()), nctx.bool_to_int(hostCondition.get(h.getName())))));
					nctx.constraints.add(ctx.mkLe(memoryConstraint, ctx.mkMul(ctx.mkInt(h.getMemory()), nctx.bool_to_int(hostCondition.get(h.getName())))));
				}
			});			
		}

		/**
		 * Checks if in the xml there are only one host client and one host server, then
		 * it calls the function to calculate all the possible paths from the host client to the host server
		 * @throws BadGraphError
		 */
		private void checkPhysicalNetwork() throws BadGraphError{
            long nMiddle = hosts.stream()
	            	 .filter((h) -> {return h.getType() == TypeOfHost.MIDDLEBOX;})
	            	 .count();
            if(nMiddle == 0) throw new BadGraphError("At least one middle host has to be defined",EType.NO_MIDDLE_HOST_DEFINED);
            boolean fixedServer = true;
            List<String> clients = hosts.stream().filter(h -> {return h.getType() == TypeOfHost.CLIENT;}).map(h -> h.getName()).collect(Collectors.toList());
            List<String> servers = hosts.stream().filter(h -> {return h.getType() == TypeOfHost.SERVER;}).map(h -> h.getName()).collect(Collectors.toList());
            if(servers.size() == 0){
            	//the nodeServer can be deployed on a middlebox
            	servers = hosts.stream().filter(h -> {return h.getType() == TypeOfHost.MIDDLEBOX;}).map(h -> h.getName()).collect(Collectors.toList());
            	fixedServer = false;
            }
           try{
		        for(String hostClient: clients){
		        	for(String hostServer: servers){
		        		//logger.debug("Calculating host chain between " + hostClient + " and " + hostServer + " composed by max " + (nodes.size()-clients.size()-servers.size()+2) + " hosts"); 
		        		if(fixedServer)
		        			savedChain.addAll(ChainExtractor.createHostChain(hostClient, hostServer, hosts, connections, nodes.size()-clients.size()-servers.size()+2));
		        		else
		        			savedChain.addAll(ChainExtractor.createHostChain(hostClient, hostServer, hosts, connections, nodes.size()-clients.size()-1+2));
		                if(savedChain.size() == 0) throw new BadGraphError("Host client " + hostClient + " and host " + hostServer +" are not connected",EType.INVALID_PHY_SERVER_CLIENT_CONF);
		            }
		        }
           }catch(StackOverflowError e) {
           		throw new BadGraphError("The service graph is too big",EType.INVALID_SERVICE_GRAPH);
			}
		}
		
		/**
		 * Calls the function to create the link from the node's neighbours and then it calls the functions that create the routing tables
		 * @throws BadGraphError
		 */
		private void checkNffg() throws BadGraphError{
            List<Node> clients = nodes.stream().filter(n -> {return nodeIsClient(n);}).collect(Collectors.toList());
            List<Node> servers = nodes.stream().filter(n -> {return nodeIsServer(n);}).collect(Collectors.toList());
    
            try{
				//links = (new LinkCreator(nodes)).getLinks();
            	linkProvider = new LinkProvider(nodes, paths);
				//logger.debug("Links created");
				//createInternalRouting(clients, servers);
				List<List<String>> validChain = new ArrayList<>();
				for(Node c:clients){
					for(Node s:servers){
						if(!linkProvider.existsPath(c, s)){
							System.out.println("No path found between "+ c.getName() + " and "+ s.getName());
							continue;
						}
						clientServerCombinations++;
						logger.debug(">>>>NEW client/server combination (total: " + clientServerCombinations + ") -> " + c.getName() + " to " + s.getName());
						String fixedHostClient = hosts.stream()
													.filter(h -> h.getFixedEndpoint() != null && h.getFixedEndpoint().equals(c.getName()))
													.map(h -> h.getName())
													.findFirst().orElse(null); 
						String fixedHostServer = hosts.stream()
													.filter(h -> h.getFixedEndpoint() != null && h.getFixedEndpoint().equals(s.getName()))
													.map(h -> h.getName())
													.findFirst().orElse(null);
						if(fixedHostClient == null){
							throw new BadGraphError("The position of the endpoint "+ c.getName() + " is not specified",EType.INVALID_PHY_SERVER_CLIENT_CONF);
						}
						if(fixedHostServer == null){
							validChain = savedChain.stream()
									.filter(list -> list.contains(fixedHostClient))
									.collect(Collectors.toList());
							logger.debug(">>>>Valid Chain found -> " + validChain.size() +" ==>  "+ validChain);
							Map<String, List<List<String>>> currentSubSet = validChain.stream()
																						.collect(Collectors.groupingBy(chain -> chain.get(chain.size()-1), Collectors.toList()));

							logger.debug(">>>>Valid SubSet found -> " + currentSubSet.size() +" ==>  "+ currentSubSet);
							for(Entry<String, List<List<String>>> entry : currentSubSet.entrySet()){
								logger.debug(">>>>Valid sub set from " + fixedHostClient +" to " + entry.getKey() +" found -> " + entry.getValue().size() +" ==>  "+ entry.getValue());
								createRoutingConditions(c, s, entry.getValue(), fixedHostClient, entry.getKey());
							}
						}
						else{
							validChain = savedChain.stream()
													.filter(list -> list.contains(fixedHostClient) && list.contains(fixedHostServer))
													.collect(Collectors.toList());
							logger.debug(">>>>Valid Chain found -> " + validChain.size() /*+ " ==> " + validChain*/);
							createRoutingConditions(c, s, validChain, fixedHostClient, fixedHostServer);
						}
						nodes.forEach(n -> {
							for(String h:stageConditions.get(n).keySet()){
								if(!countConditions.containsKey(n.getName()+"_"+h)){
									countConditions.put(n.getName()+"_"+h, 0);
								}
								countConditions.put(n.getName()+"_"+h, countConditions.get(n.getName()+"_"+h)+1);
							}
							stageConditions.get(n).clear();
							rawConditions.get(n).clear();
					    });
					}
				}
				
				routingMap.forEach((n, tuple) -> {
					//logger.debug("Executing routing optimization for " + n.getName());
					//net.routingOptimizationSG2(netobjs.get(n), tuple._1, tuple._2 destinations);
					//net.routingOptimizationSG(netobjs.get(n), tuple._1, tuple._2);
					//net.routingOptimization(netobjs.get(n), tuple._1);
					net.routingOptimizationSGOptional(netobjs.get(n), tuple._1, tuple._2, autoctx);
				});
			}catch(StackOverflowError e) {
            	throw new BadGraphError("The graph of nodes is invalid",EType.INVALID_SERVICE_GRAPH);
			}
		}
		/**
		 * Creates all the routing rule between the internal nodes
		 * @param clients List of clients in the service graph
		 * @param servers List of servers in the service graph
		 */
		private void createInternalRouting(List<Node> clients, List<Node> servers){
			try{
		        for(Node nodeClient: clients){
		        	for(Node nodeServer: servers){
		        		savedNodeChain.addAll(ChainExtractor.createNodeChain(nodeClient.getName(), nodeServer.getName(), linkProvider.getAllLinks()));
		                if(savedNodeChain.size() == 0) throw new BadGraphError("Node client and node server are not connected",EType.INVALID_SERVICE_GRAPH);
		            }
		        }
            }catch(StackOverflowError e) {
           		throw new BadGraphError("The service graph is too big",EType.INVALID_SERVICE_GRAPH);
			}
            
            for(List<String> chain:savedNodeChain){
            	for(Node src:nodes){
	            	int iSrc = chain.lastIndexOf(src.getName());
	            	if(iSrc < 0){
            			continue;
            		}
	            	if(!routingRule.containsKey(src)){
            			routingRule.put(src, new HashMap<>());
            		}
	            	for(Node dst:nodes){
	            		int iDst = chain.lastIndexOf(dst.getName());
	            		if(iDst < 0 || servers.contains(dst) || src == dst){
	            			continue;
	            		}
	            		int distance = iDst - iSrc;
	            		Node nextHop = nodes.stream().filter(n1 -> n1.getName().equals(chain.get(iSrc+(distance/Math.abs(distance))))).findFirst().orElse(null);
        				assert(nextHop != null);
	            		/*if(clients.contains(nextHop) || servers.contains(nextHop)){
	            			continue;
	            		}*/
	            		if(!routingRule.get(src).containsKey(dst)){
	            			//System.out.println("From " + src.getName() + " (index="+iSrc+") to "+ dst.getName() + " (index="+iDst+") -> " + distance);
	            			List<Node> newRules = new ArrayList<>();
	            			newRules.add(nextHop);
	            			routingRule.get(src).put(dst, newRules);
	            			//logger.debug("From " + src.getName() + " to "+ dst.getName() + " -> NextHop: " + chain.get(iSrc+(distance/Math.abs(distance))) + " dist: " + distance);
	            		}else{
	            			List<Node> rules = routingRule.get(src).get(dst);
            				if(!rules.contains(nextHop))
        						rules.add(nextHop);
        					//routingRule.get(src).put(dst, rules);
        					//logger.debug("Rule ADDED: From " + src.getName() + " to "+ dst.getName() + " -> NextHop: " + chain.get(iSrc+(distance/Math.abs(distance))) + " dist: " + distance);
        					            					            			
	            		}
	            			
	            	}
	            }
            }
            /*routingRule.forEach((src,rules) ->{
            	logger.debug("From " + src.getName() + " -> ");
            	rules.forEach((dst, rule) ->{
            		logger.debug("\tto " + dst.getName());
            		rule.forEach(nextHop ->{
                		logger.debug("\t\t\t\t-> " + nextHop.getName());
                	});
            	});
            	
            });*/
            /*routingRule.forEach((src,rules) ->{
            	rules.forEach((dst, rule) ->{
            		net.internalRoutingOptimizationSG(netobjs.get(src), nctx.am.get(dst.getName()), rule.stream().map(t -> netobjs.get(t._1)).collect(Collectors.toList()));
            	});
            	
            });*/
            
		}
		
		/**
		 * Creates the routing table by adding the rules by exploring for each possible path between 
		 * an host client and host server all the possibles deploying scenarios for the nodes
		 * @param client the node client
		 * @param server the node server
		 * @param stageConditions 
		 * @throws BadGraphError
		 */
		private void createRoutingConditions(Node client, Node server, List<List<String>> validChain, String hostClient, String hostServer) throws BadGraphError{
			
			//System.out.println("Searching next hop for " + client.getName() + " towards " + server.getName());
			
			//List<Link> nextLinks = links.stream().filter(l -> l.getSourceNode().equals(client.getName())).collect(Collectors.toList());
			List<Link> nextLinks = linkProvider.getLinksFrom(client, 0);
			if(nextLinks.size() == 0){
				logger.error("Route: From CLIENT " + client.getName() 
									+ " to " + nctx.am.get(server.getName()) 
									+ " -> Dead End");
				throw new BadGraphError("Nodes must be connected",EType.INVALID_SERVICE_GRAPH);
			}
			//System.out.println("The host client is: " + hostClient+" and the host server is "+hostServer);
			for(Link link : nextLinks){
				Node next = nodes.stream().filter(n -> n.getName().equals(link.getDestNode()) ).findFirst().get();
				//System.out.println("Route from CLIENT " + client.getName() 
				//								+ " to " + nctx.am.get(server.getIp()) 
				//								+ " -> next hop: " + netobjs.get(next));
				
				for(int i = 0; i < validChain.size(); i++){
					String host = validChain.get(i).get(1);
					//System.out.println("Chain -> " + savedChain.get(i));
					if(setNextHop(client, next, server, 1, i, 1, validChain, hostServer, new HashMap<>())){
						/*System.out.println("Route from " + client.getName() 
						+ " to " + nctx.am.get(server.getIp()) 
						+ " -> next hop: " + netobjs.get(next));
						System.out.println("From " + hostClient + " to " + host1);
						*/
						
						rawConditions.get(client).add(cb.buildConditionString(next, host));
					}
				}
				for(Node n : rawConditions.keySet()){
					ArrayList<RoutingTable> rt = new ArrayList<RoutingTable>();
					logger.debug("-----Routing Table NODE "+n.getName()+"-----");
					List<String> cond = rawConditions.get(n).stream().distinct().collect(Collectors.toList());
					logger.debug("Condition for "+ n.getName() +" -> "+ cond);
					Map<String, List<DatatypeExpr>> destinations = new HashMap<>();
					for(String s:cond){
						//logger.debug("Parsing "+s);
						ConditionExtractor ce = new ConditionExtractor(ctx, autoctx, n, connections, conditionDB, stageConditions);
						BoolExpr c = ce.DeploymentConditionFromString(s, client, server, nodes, hostClient, hostServer);
						next = ce.getNext();
						int latency = ce.getLatency();
						if(!nodeIsServer(n)){
							logger.debug("Adding ("+ c +"), from "+ n.getName() +" to " + server.getName() + " next hop is " + next.getName() + " with latency " + latency);
							rt.add(new RoutingTable(nctx.am.get(server.getName()), netobjs.get(next), nctx.addLatency(latency), c));
						}
						/* needed for internal routing
						for(Entry<Node, List<Node>> rule : routingRule.get(n).entrySet()){
							for(Node nextHop : rule.getValue()){
								if(nextHop.getName().equals(next.getName())){
									//logger.debug("Adding ("+ c +"), from "+ n.getName() +" to " + rule.getKey().getName() + " next hop is " + nextHop.getName() + " with latency " + latency);
									rt.add(new RoutingTable(nctx.am.get(rule.getKey().getName()), netobjs.get(nextHop), nctx.addLatency(latency), c));
								}
							}
						}*/
						/*// for backwards paths
						for(Entry<Node, List<Node>> rule : routingRule.get(next).entrySet()){
							for(Node nextHop : rule.getValue()){
								if(nextHop.getName().equals(n.getName())){
									logger.debug("Adding ("+ c +"), from "+ next.getName() +" to " + rule.getKey().getName() + " next hop is " + nextHop.getName() + " with latency " + latency);
									rt.add(new RoutingTable(nctx.am.get(rule.getKey().getName()), netobjs.get(nextHop), nctx.addLatency(latency), c));
								}
							}
						}*/
					}
					List<BandwidthMetrics> bConstraints = bandwidthMetrics.stream().filter(b -> b.getSrc().equals(n.getName())).collect(Collectors.toList());
					//logger.debug(n.getName() + " has this bandwidth constraints: " + bConstraints);
					//logger.debug(n.getName() + " uses the previous next hop for the following destinations: " + destinations);
					//net.internalRoutingOptimizationSG(netobjs.get(n), destinations, netobjs.get(next));
					//System.out.println(n.getName() + " uses " + next.getName() + " as next hop for the following destinations: " + destinations);
					//logger.debug("Adding routing table to "+n.getName());
					
					if(!routingMap.containsKey(n)){
						//logger.debug("Adding to the routing map " + n.getName());
						routingMap.put(n, new Tuple<>(new ArrayList<>(), new ArrayList<>()));
					}
					Tuple<ArrayList<RoutingTable>, ArrayList<BandwidthMetrics>> tuple = routingMap.get(n);
					tuple._1.addAll(rt);
					tuple._2.addAll(bConstraints);
				}
			}
			
			logger.debug("----STAGE CONDITION DB----");
			int nrOfCondition = (int) stageConditions.entrySet().stream().flatMap(e -> e.getValue().values().stream()).count();
			stageConditions.entrySet().forEach(e -> {
				if( !nodeIsClient(e.getKey()) && !nodeIsServer(e.getKey()) && e.getValue().size() == 0 && nrOfCondition > 0){
					logger.debug(" No Constraints on next node " );
					hosts.forEach(h ->{
						if(h.getType().equals(TypeOfHost.MIDDLEBOX)){
							BoolExpr c = ctx.mkBoolConst(e.getKey().getName()+"@"+h.getName());
							e.getValue().put(h.getName(), c);
							conditionDB.get(e.getKey()).put(h.getName(), c);
						}
					});
				}
				logger.debug(e.getKey().getName() + " -> " + e.getValue());
				});
			logger.debug("--------------------");
		}
		/**
		 * Explores recursively all the possible solution for setting a next hop condition
		 * @param source the node from which is exploring the solutions
		 * @param server the node server
		 * @param nodeRecursionLevel the current level of the recursion
		 * @param nChain number of the host chain on which it is trying to deploy all the nodes
		 * @param level on which host in the host chain, it is trying to deploy the remaining nodes
		 * @param validChain 
		 * @param hostServer
		 * @return true if the last node has been deployed on the host server (good path), false otherwise
		 * @throws BadGraphError
		 */
		private boolean setNextHop(Node prec, Node source, Node server, int nodeRecursionLevel, int nChain, int level, List<List<String>> validChain, String hostServer, HashMap<Node, List<String>> visited) throws BadGraphError{
			String currentHost = validChain.get(nChain).get(level);
			//logger.debug("Searching next hop for " + source.getName() + " towards " + server.getName());
			if(source.getName().equals(server.getName())){
				if(currentHost.equals(hostServer)){
					//logger.debug("Route from SERVER " + source.getName() + " to " + nctx.am.get(server.getName())  + " -> next hop: DESTINATION REACHED" + " CurrentHost: " + currentHost);
					rawConditions.get(source).add(cb.buildConditionString(source, currentHost));
					//logger.debug("Found path from lv " + level + " of chain " +nChain );
					return true;
				}
				else{
					//logger.debug("Path not found path from lv " + level + " of chain " +nChain );
					return false;
				}
			}
			if(currentHost.equals(hostServer) && !nodeIsServer(source)){
				//logger.debug("Only server node can be deployed on server host -> tried to deploy " + source.getName() + " on " +currentHost );
				return false;
			}
			
			//List<String> nextDest = links.stream().filter(l -> l.getSourceNode().equals(source.getName())).map(l -> l.getDestNode() ).collect(Collectors.toList());
			List<String> nextDest = linkProvider.getLinksFrom(source, nodeRecursionLevel).stream().map(l -> l.getDestNode()).collect(Collectors.toList());
			if(nextDest.size() == 0){
				/*logger.debug("Route: From " + source.getName() 
									+ " to " + nctx.am.get(server.getName()) 
									+ " -> Dead End");*/
				return false;
			}
			/*logger.debug("Route: From " + source.getName() 
								+ " to " + nctx.am.get(server.getName()) 
								+ " -> Possible Next Hop "+nextDest);*/
			
			if(!visited.containsKey(source)){
				//logger.debug("New node visited -> " + source.getName());
				visited.put(source, new ArrayList<>());
			}
			boolean found = false;
			for(String dest:nextDest){
				Node next = nodes.stream().filter(n -> n.getName().equals(dest)).findFirst().orElse(null);
				if(next == null){
					throw new BadGraphError("Incoherent service graph",EType.INVALID_SERVICE_GRAPH);
				}
				if(visited.get(source).contains(dest) || dest.equals(prec.getName())){
					//logger.debug("Next node already visited -> From " + source.getName() + " to " + next.getName() + " in " + nextDest);
					continue;
				}
				//logger.debug("Adding to visited from " + source.getName() +" to " + dest);
				visited.get(source).add(dest);
				//logger.debug("Route from " + source.getName()+ " to " + nctx.am.get(server.getName())+ " -> next hop: " + netobjs.get(next));
				for(int i = level; i < validChain.get(nChain).size() && i <= level+1; i++){
					String nextHost = validChain.get(nChain).get(i);
					//logger.debug("RECURSION -> Deploying " + next.getName() +" on lv " + i + " of chain " +nChain +"("+nextHost+")");
					if(setNextHop(source, next, server, nodeRecursionLevel+1, nChain, i, validChain, hostServer, visited)){
						if(autoctx.nodeIsOptional(source)){
							autoctx.addOptionalPlacement(netobjs.get(prec), netobjs.get(next), netobjs.get(source));
						}
						//logger.debug("From " + currentHost + " to " + nextHost);
						//logger.debug("On RT("+source.getName()+") ");
						if(nextHost.equals(hostServer)){
							//logger.debug("Route from SERVER " + source.getName() + " to " + nctx.am.get(server.getName())  + " -> next hop: DESTINATION REACHED" + " CurrentHost: " + currentHost);
							rawConditions.get(source).add(cb.buildConditionString(source, currentHost));
						}
						else{
							rawConditions.get(source).add(cb.buildConditionString(source, currentHost, next, validChain.get(nChain).get(i)));
						}
						found = true;
					}
				}
				//logger.debug("Removing to visited from " + source.getName() +" to " + dest);
				visited.get(source).remove(dest);
			}
			//logger.debug("Removing from visited " + source.getName());
			//visited.remove(source);
			return found;
		}
		public void setProperty(List<Property> prop){
			prop.forEach(p ->{
				String src = p.getSrc(), dst = p.getDst();
	            Node source = nodes.stream().filter(n -> {return n.getName().equals(src);}).findFirst().orElse(null);
				Node dest = nodes.stream().filter(n -> {return n.getName().equals(dst);}).findFirst().orElse(null);
				if(source == null || dest == null)
					throw new BadGraphError("Error in the property definition", EType.INVALID_PROPERTY_DEFINITION);
				logger.debug("Adding check on "+ p.getName() + " from " + source.getName() + " to "+ dest.getName());
				switch (p.getName()) {
				case ISOLATION_PROPERTY: 
						check.propertyAdd(netobjs.get(source), netobjs.get(dest), Prop.ISOLATION, p);
						break;
				case REACHABILITY_PROPERTY: 
						check.propertyAdd(netobjs.get(source), netobjs.get(dest), Prop.REACHABILITY, p);
					break;
				default:
					throw new BadGraphError("Error in the property definition", EType.INVALID_PROPERTY_DEFINITION);
				}
			});
		}
		/**
		 * Checks if the client node and the server node in a graph are reachable satisfying all the imposed conditions
		 * @param prop 
		 * @return
		 */
		public IsolationResult checkNFFGProperty(){
			
			IsolationResult ret = this.check.propertyCheck();
			nrOfConditions = (int) conditionDB.entrySet().stream().flatMap(e -> e.getValue().values().stream()).count();
			logger.debug("Nr of Conditions: " + nrOfConditions);
			System.out.println("Nr of Conditions: " + nrOfConditions);
			if(nrOfConditions == 0) ret.result = Status.UNSATISFIABLE;
			if (ret.result == Status.UNSATISFIABLE){
				 	logger.debug("UNSAT"); // Nodes a and b are isolated
				 	
		    }else{
		    	 	logger.debug("SAT ");
		     		logger.debug( ""+ret.model); //p.printModel(ret.model);
		     		
		    }
			return ret;
		}
		/**
		 * Get Net Context
		 * @return the net context
		 */
		public NetContext getNctx() {
			return nctx;
		}
		/**
		 * Returns true if the node is a client
		 * @param n
		 * @return
		 */
		public boolean nodeIsClient(Node n){
			return n.getFunctionalType().equals(FunctionalTypes.MAILCLIENT) || n.getFunctionalType().equals(FunctionalTypes.WEBCLIENT)|| n.getFunctionalType().equals(FunctionalTypes.ENDHOST);
            
		}
		/**
		 * Returns true if the node is a server
		 * @param n
		 * @return
		 */
		public boolean nodeIsServer(Node n){
			return n.getFunctionalType().equals(FunctionalTypes.MAILSERVER) || n.getFunctionalType().equals(FunctionalTypes.WEBSERVER);
    
		}
		/**
		 * @return the nrOfConditions
		 */
		public int getNrOfConditions() {
			return nrOfConditions;
		}
}
