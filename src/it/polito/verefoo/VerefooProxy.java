package it.polito.verefoo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.Collections;
import com.microsoft.z3.Context;

import it.polito.verefoo.allocation.AllocationManager;
import it.polito.verefoo.allocation.AllocationNodeAP;
import it.polito.verefoo.allocation.AllocationNodeMF;
import it.polito.verefoo.extra.BadGraphError;
import it.polito.verefoo.extra.WildcardManager;
import it.polito.verefoo.functions.StatefulPacketFilter;
import it.polito.verefoo.graph.IPAddress;
import it.polito.verefoo.graph.PortInterval;
import it.polito.verefoo.graph.Predicate;
import it.polito.verefoo.graph.SecurityRequirement;
import it.polito.verefoo.graph.AtomicFlow;
import it.polito.verefoo.graph.FlowPathAP;
import it.polito.verefoo.graph.FlowPathMF;
import it.polito.verefoo.jaxb.*;
import it.polito.verefoo.jaxb.NodeConstraints.NodeMetrics;
import it.polito.verefoo.jaxb.Path.PathNode;
import it.polito.verefoo.solver.*;
import it.polito.verefoo.solver.Checker.Prop;
import it.polito.verefoo.utils.APUtilsAP;
import it.polito.verefoo.utils.APUtilsMF;
import it.polito.verefoo.utils.GenerateFlowsTask;
import it.polito.verefoo.utils.TestResults;
import it.polito.verefoo.utils.VerificationResult;
import it.polito.verefoo.functions.Forwarder;
import it.polito.verefoo.graph.MaximalFlow;
import it.polito.verefoo.graph.Traffic;

/**
 * 
 * This is the main class that will interface with the Verefoo classes
 *
 */
public class VerefooProxy {
	private Context ctx;
	private NetContextAP nctxAP;
	private NetContextMF nctxMF;
	private List<Property> properties;
	private List<Path> paths;
	private WildcardManager wildcardManager;
	private HashMap<String, AllocationNodeAP> allocationNodesAP;
	private HashMap<String, AllocationNodeMF> allocationNodesMF;
	private HashMap<Integer, FlowPathAP> trafficFlowsMapAP;
	private HashMap<Integer, FlowPathMF> trafficFlowsMapMF;
	private HashMap<Integer, SecurityRequirement> securityRequirements;
	public Checker check;
	private List<Node> nodes;
	private List<NodeMetrics> nodeMetrics;
	private AllocationManager allocationManager;
	private APUtilsAP aputilsAP;
	private String AlgoUsed = "AP";

	/* Atomic predicates */
	private HashMap<Integer, Predicate> networkAtomicPredicates = new HashMap<>();
	HashMap<String, Node> transformersNode = new HashMap<>();
	private TestResults testResults = new TestResults();
	
	/* Maximal flows */
	HashMap<String, List<Predicate>> natD1map = new HashMap<>();
	HashMap<String, Predicate> natD2map = new HashMap<>();
	HashMap<String, List<Predicate>> natD31map = new HashMap<>();
	HashMap<String, Predicate> natD32map = new HashMap<>();
	HashMap<String, List<Predicate>> natReconvertedMap = new HashMap<>();
	HashMap<String, List<Predicate>> allowedFirewallPredicates = new HashMap<>();
	HashMap<String, List<Predicate>> deniedFirewallPredicates = new HashMap<>();
	private APUtilsMF aputilsMF;

	int maximalFlowId = 0;

	/**
	 * Public constructor for the Verefoo proxy service it executes according to the algorithm chosen
	 * 
	 * @param graph              The graph that will be deployed on the network
	 * @param hosts              The list of hosts in the network
	 * @param conns              The connections between hosts
	 * @param paths              the list of paths that the packet flows needs to
	 *                           follow
	 * @param capacityDefinition The list of the capacity for each node that will be
	 *                           deployed
	 * @param algo 				 The chosen Algorithm "MF" or "AP"
	 * @throws BadGraphError
	 */
	public VerefooProxy(Graph graph, Hosts hosts, Connections conns, Constraints constraints, List<Property> prop,
			List<Path> paths,String algo) throws BadGraphError {

		// Determine what algorithm to be executed
		this.AlgoUsed = algo;
		
		if(AlgoUsed.equals("AP")){ // Atomic Predicate algo execution
			
		// Initialization of the variables related to the nodes
		allocationNodesAP = new HashMap<>();
		nodes = graph.getNode();
		nodes.forEach(n -> allocationNodesAP.put(n.getName(), new AllocationNodeAP(n))); // class for AP
		wildcardManager = new WildcardManager(allocationNodesAP);
			
		// Initialization of the variables related to the requirements
		properties = prop;
		securityRequirements = new HashMap<>();
		int idRequirement = 0;
		for(Property p : properties) {
			securityRequirements.put(idRequirement, new SecurityRequirement(p, idRequirement));
			idRequirement++;
		}
		
		this.paths = paths;
		this.nodeMetrics = constraints.getNodeConstraints().getNodeMetrics();
		//Creation of the z3 context
		HashMap<String, String> cfg = new HashMap<String, String>();
		cfg.put("model", "true");
		ctx = new Context(cfg);
				
		//Creation of the NetContext (z3 variables)
		nctxAP = nctxGenerateAP(ctx, nodes, prop, allocationNodesAP);
		nctxAP.setWildcardManager(wildcardManager);
		aputilsAP = new APUtilsAP(); 
		
		/*
		 * Main sequence of methods in VerefooProxy for Atomic Predicates:
		 * 1) given every requirement, all the possible paths of the related flows are computed;
		 * 2) then starting from requirements and transformers, all relative atomic predicates for the network are computed
		 * 3) the transformation map for each transformer is filled (e.g. NAT1 input ap 5 -> output ap 8)
		 * 4) then all atomic flows are computed 
		 */
		
		allocationManager = new AllocationManager(ctx, nctxAP, allocationNodesAP, nodeMetrics, prop, wildcardManager);
		allocationManager.instantiateFunctions("AP");
		
		/* Atomic predicates */
		aputilsAP = new APUtilsAP();
		long t1 = System.currentTimeMillis();
		trafficFlowsMapAP = generateFlowPathsAP();
		networkAtomicPredicates = generateAtomicPredicateNew();
		long t2 = System.currentTimeMillis();
		testResults.setAtomicPredCompTime(t2-t1);
		fillTransformationMap();
		//printTransformations(); //DEBUG
		computeAtomicFlows();
		t1 = System.currentTimeMillis();
		testResults.setAtomicFlowsCompTime(t1-t2);
		testResults.setBeginMaxSMTTime(t1);
		//distributeTrafficFlows();
		allocateFunctionsAP();
		// Change Execution depending on chosen algorithm
		allocationManager.configureFunctionsAP(); // atomic predicate method
		
		check = new Checker(ctx, nctxAP, allocationNodesAP);
		formalizeRequirementsAP();

		}

		if(AlgoUsed.equals("MF")){ // Maximal flows algorithm execution
			
		// Initialization of the variables related to the nodes
		allocationNodesMF = new HashMap<>();
		nodes = graph.getNode();
		nodes.forEach(n -> allocationNodesMF.put(n.getName(), new AllocationNodeMF(n))); // class for MF
		wildcardManager = new WildcardManager(allocationNodesMF,"MF"); // constructor for MF
			
		
		// Initialization of the variables related to the requirements
		properties = prop;
		securityRequirements = new HashMap<>();
		int idRequirement = 0;
		for(Property p : properties) {
			securityRequirements.put(idRequirement, new SecurityRequirement(p, idRequirement));
			idRequirement++;
		}
		
		this.paths = paths;
		this.nodeMetrics = constraints.getNodeConstraints().getNodeMetrics();
		//Creation of the z3 context
		HashMap<String, String> cfg = new HashMap<String, String>();
		cfg.put("model", "true");
		ctx = new Context(cfg);
				
		//Creation of the NetContext (z3 variables)
		nctxMF = nctxGenerateMF(ctx, nodes, prop, allocationNodesMF);
		nctxMF.setWildcardManager(wildcardManager);
		aputilsMF = new APUtilsMF();  // for Maximal Flows
		
		/*
		 * Main sequence of methods in VerefooProxy for Maximal Flows:
		 * 1) given every requirement, all the possible paths of the related flows are computed;
		 * 2) the existing functions are instantiated
		 * 3) the functions to be allocated are associated to Allocation Places
		 * 4) the possible traffic in input to each node is computed
		 * 5) soft and hard constraints are defined for each function
		 * 6) the hard constraints for the requirements are defined
		 */
		
		 long beginComputingFlows = System.currentTimeMillis();
		 System.out.println("Generating flow paths ...");
		 trafficFlowsMapMF = generateFlowPathsMF();
		 allocationManager = new AllocationManager(ctx, nctxMF, allocationNodesMF, nodeMetrics, prop, wildcardManager);
		 allocationManager.instantiateFunctions("MF");
		 allocateFunctionsMF();
		 System.out.print("Filling trasformers map ...");
		 fillTrasformersMap();
		 System.out.println("Generating maximal flows ...");
		 generateMaximalFlows();
		 long endComputingFlows = System.currentTimeMillis();
		 testResults.setMaximalFlowsCompTime(endComputingFlows- beginComputingFlows);
		 testResults.setStartMaxSMTtime(System.currentTimeMillis());
		 allocationManager.configureFunctionsMF(); // MF method
		 check = new Checker(ctx, nctxMF, allocationNodesMF);
		 formalizeRequirementsMF();
	
		}

	}
/**************************************************Atomic Predicate Methods **************************************************************/
	
	/**
	 * This function receives in input a set of already computed Atomic Predicates
	 * (atomicPredicates) and a set of Predicates (predicates), not yet atomic, to convert
	 * and add to the set.
	 */
	
	private void computeAtomicFlows() {
		ExecutorService threadPool = Executors.newFixedThreadPool(10);
		List<Future<?>> tasks = new ArrayList<Future<?>>();
		
		System.out.println("NUMBER OF REQUIREMENTS: " + securityRequirements.size());
		System.out.println("Computing atomic flows:");
		AtomicInteger atomicId = new AtomicInteger();
		int debugIndex = 0;
		for(SecurityRequirement sr : securityRequirements.values()) {
			if(debugIndex == 150) {
				debugIndex = 0;
				System.out.println();
			}
			debugIndex++;
			System.out.print("*");
			APUtilsAP aputilsNew = new APUtilsAP(); 
			tasks.add(threadPool.submit(new GenerateFlowsTask(sr, networkAtomicPredicates, aputilsNew, transformersNode, atomicId)));
		}
		
		threadPool.shutdown();
		//Join results
		for(Future<?> fut: tasks) {
			try {
				fut.get();
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}
		System.out.println();	
		
	/*	//DEBUG: print atomic flows for each requirement
		for(SecurityRequirement sr : securityRequirements.values()) {
			Property prop = sr.getOriginalProperty();
			System.out.println("\nConsidering requirement {"+prop.getSrc()+","+prop.getSrcPort()+","+prop.getDst()+","+prop.getDstPort()+","+prop.getLv4Proto()+"}");   
			for(FlowPath flow: sr.getFlowsMap().values()) {
				Map<Integer, AtomicFlow> atomicFlowsMap = flow.getAtomicFlowsMap();
				Map<Integer, AtomicFlow> atomicFlowsToDiscardMap = flow.getAtomicFlowsToDiscardMap();
				List<AllocationNode> path = flow.getPath();
				if(atomicFlowsMap != null) {
					System.out.println("Atomic flows accepted");
					for(Map.Entry<Integer, AtomicFlow> entry: atomicFlowsMap.entrySet()) {
						int index = 0;
						System.out.print(entry.getKey() + ": ");
						for(Integer ap: entry.getValue().getAtomicPredicateList()) {
							System.out.print(path.get(index).getIpAddress() + ", " + ap + ", ");
							index++;
						}
						System.out.println(path.get(index).getIpAddress());
					}
					System.out.println("Atomic flows discarded");
					for(Map.Entry<Integer, AtomicFlow> entry: atomicFlowsToDiscardMap.entrySet()) {
						int index = 0;
						System.out.print(entry.getKey() + ": ");
						for(Integer ap: entry.getValue().getAtomicPredicateList()) {
							System.out.print(path.get(index).getIpAddress() + ", " + ap + ", ");
							index++;
						}
						System.out.println();
					}
				}
			}
		}
		System.out.println();
		//END DEBUG*/
		
		//Total number of flows
		long totalFlows = 0;
		for(SecurityRequirement sr : securityRequirements.values()) {
			for(FlowPathAP flowPath: sr.getFlowsMapAP().values()) {
				totalFlows += flowPath.getAtomicFlowsMap().size();
			}
		}
		testResults.setTotalFlows(totalFlows);
		
		//built map that assign to each allocation node the set of atomic predicates in input
		for(SecurityRequirement sr : securityRequirements.values()) {
			for(FlowPathAP flowPath: sr.getFlowsMapAP().values()) {
				List<AllocationNodeAP> path = flowPath.getPath();
				for(AtomicFlow atomicFlow: flowPath.getAtomicFlowsMap().values()) {
					//for source node don't add nothing
					int index = 1;
					for(Integer ap: atomicFlow.getAtomicPredicateList()) {
						path.get(index).addAtomicPredicateInInput(flowPath.getIdFlow(), atomicFlow.getFlowId(), ap);
						if(transformersNode.containsKey(path.get(index).getIpAddress()) 
								&& transformersNode.get(path.get(index).getIpAddress()).getFunctionalType() == FunctionalTypes.FIREWALL) {
							//If the node is a firewall, check if the predicate is allowed to pass or if it is dropped
							if(path.get(index).getForwardBehaviourList().contains(ap) || path.get(index).getDroppedList().contains(ap))
								{
								index++;
								continue; //already checked
								}
							boolean foundIntersection = false;
							for(Predicate allowed: path.get(index).getForwardBehaviourPredicateList()) {
								Predicate intersectionPredicate = aputilsAP.computeIntersection(networkAtomicPredicates.get(ap), allowed);
								if(intersectionPredicate != null && aputilsAP.APCompare(intersectionPredicate, networkAtomicPredicates.get(ap))) {
									foundIntersection = true;	
									break;
								}
							}
							
							if(foundIntersection) {
								path.get(index).addForwardingPredicate(ap);
							} else {
								path.get(index).addDroppedPredicate(ap);
							}
						}
						
						if(transformersNode.containsKey(path.get(index).getIpAddress()) 
								&& transformersNode.get(path.get(index).getIpAddress()).getFunctionalType() == FunctionalTypes.STATEFUL_FIREWALL) {
							StatefulPacketFilter spf = (StatefulPacketFilter) path.get(index).getPlacedNF();
							if(spf.getAllowAtomicPredicates().values().contains(ap) || spf.getDenyAtomicPredicates().contains(ap) || spf.getAllowCondAtomicPredicates().values().contains(ap) || spf.getAllowCondInvAtomicPredicates().values().contains(ap))
								continue; //already checked
							
							boolean foundIntersection = false;
							System.out.println("ALLOWEDCOND: " );
							for(Map.Entry<Integer,Predicate> allowedCond: spf.getAllowCondPredicates().entrySet()) {
								allowedCond.getValue().print();
								Predicate intersectionPredicate = aputilsAP.computeIntersection(networkAtomicPredicates.get(ap), allowedCond.getValue());
								if(intersectionPredicate != null && aputilsAP.APCompare(intersectionPredicate, networkAtomicPredicates.get(ap))) {
									foundIntersection = true;	
									spf.addAllowCondAtomicPredicate(allowedCond.getKey(), ap);
									break;
								} 
							}
							if(!foundIntersection) {
								System.out.println("ALLOWEDCONDINV: " );
								for(Map.Entry<Integer,Predicate> allowedCondInv: spf.getAllowCondInvPredicates().entrySet()) {
									allowedCondInv.getValue().print();
									Predicate intersectionPredicate = aputilsAP.computeIntersection(networkAtomicPredicates.get(ap), allowedCondInv.getValue());
									if(intersectionPredicate != null && aputilsAP.APCompare(intersectionPredicate, networkAtomicPredicates.get(ap))) {
										foundIntersection = true;	
										spf.addAllowCondInvAtomicPredicate(allowedCondInv.getKey(), ap);
										break;
									} 
								}
								if(!foundIntersection) {
									System.out.println("ALLOWED: " );
									for(Map.Entry<Integer,Predicate> allowed: spf.getAllowPredicates().entrySet()) {
										allowed.getValue().print();
										Predicate intersectionPredicate = aputilsAP.computeIntersection(networkAtomicPredicates.get(ap), allowed.getValue());
										if(intersectionPredicate != null && aputilsAP.APCompare(intersectionPredicate, networkAtomicPredicates.get(ap))) {
											foundIntersection = true;	
											spf.addAllowAtomicPredicate(allowed.getKey(), ap);
											break;
										} 
									}
									if(!foundIntersection) {
										System.out.println("DENIED: " );
										System.out.println(ap);
										spf.addDenyAtomicPredicate(ap);
									}
								}
							}
							
						}
												
						index++;
					}
					
					
				}
			}
		}
	}
	
// For debugging purposes must have two classes of it one for AP and one for MF
	/*
	private void printTransformations() {
		for(String node: transformersNode.keySet()) {
			AllocationNode allocNode = allocationNodes.get(node);
			System.out.println("\nNODE " + node);
			System.out.println("Allowed rules");
			for(Predicate pred: allocNode.getForwardBehaviourPredicateList()) {
				pred.print();
			}
			System.out.println("Transformation map");
			for(HashMap.Entry<Integer, List<Integer>> entry: allocNode.getTransformationMap().entrySet()) {
				System.out.print(entry.getKey() + ":" );
				for(Integer res: entry.getValue())
					System.out.print(res + " ");
				System.out.println();
			}
		}
	}*/

	/**
	 * Compute the structures of support for transformers: for each NAT compute the transforming map, for each FIREWALL its deny/allow lists 
	 * i.e. a NAT will have a map of entry for example {10: 5} which means that the atomic predicates 10 arrives at the nat and it is transformed in
	 * atomic predicate 5.
	 * a fire wall instead will have a list of id for example [1,2,5,6,10 ...], that are the identifiers of atomic predicates allowed to cross the firewall,
	 * all the other predicates will be dropped 
	 */
	private void fillTransformationMap() {
		System.out.println("Filling transformers map");
		for(Node node: transformersNode.values()) {
			System.out.print("*");
			HashMap<Integer, List<Integer>> resultMap = allocationNodesAP.get(node.getName()).getTransformationMap();
			if(node.getFunctionalType() == FunctionalTypes.NAT) {
				HashMap<String, List<Integer>> shadowingMap = new HashMap<>(); //grouped by dest address
				HashMap<String, List<Integer>> shadowedMap = new HashMap<>(); //grouped by dest address
				HashMap<String, List<Integer>> reconversionMap = new HashMap<>(); //grouped by source address
				HashMap<String, List<Integer>> reconvertedMap = new HashMap<>();  //grouped by source address
				List<Integer> notChaingingPredicateList = new ArrayList<>();
				List<IPAddress> natIPSrcAddressList = new ArrayList<>();
				for(String src: node.getConfiguration().getNat().getSource()) 
					natIPSrcAddressList.add(new IPAddress(src, false));
				IPAddress natIPAddress = new IPAddress(node.getName(), false);
				
				for(HashMap.Entry<Integer, Predicate> apEntry: networkAtomicPredicates.entrySet()) {
					Predicate ap = apEntry.getValue();
					//if source ip address list or dest ip address list have size != 1, it means it is a complex predicates so it can not be a shodowing/reconversion predicates
					if(ap.getIPSrcListSize() != 1 || ap.getIPDstListSize() != 1) continue;
					if(ap.hasIPDstNotIncludedIn(natIPSrcAddressList) && !ap.hasIPDstEqual(natIPAddress)) {
						if(ap.hasIPSrcEqual(natIPAddress)) {
							//2*: if dest is not a src address of the NAT (so it is a public address) and ip source = ip NAT, this is a shadowed predicate
							//{IP NAT, public address}
							if(!shadowedMap.containsKey(ap.firstIPDstToString())) {
								List<Integer> list = new ArrayList<>();
								list.add(apEntry.getKey());
								shadowedMap.put(ap.firstIPDstToString(), list);
							} else {
								shadowedMap.get(ap.firstIPDstToString()).add(apEntry.getKey());
							}
						} 
						else {
							//1*: if dest is not a src address of the NAT (so it is a public address), while src is a src address of NAT (private address),
							//this is a shadowing predicates {private address, public address}
							if(ap.hasIPSrcEqualOrIncludedIn(natIPSrcAddressList))
								if(!shadowingMap.containsKey(ap.firstIPDstToString())) {
									List<Integer> list = new ArrayList<>();
									list.add(apEntry.getKey());
									shadowingMap.put(ap.firstIPDstToString(), list);
								} else {
									shadowingMap.get(ap.firstIPDstToString()).add(apEntry.getKey());
								}
						}
					} else if(ap.hasIPSrcNotIncludedIn(natIPSrcAddressList) && !ap.hasIPSrcEqual(natIPAddress)) {
						if(ap.hasIPDstEqual(natIPAddress)) {
							//3*: src not included in NAT src, dest = IP NAT -> reconversion predicate {public address, IP NAT}
							if(!reconversionMap.containsKey(ap.firstIPSrcToString())) {
								List<Integer> list = new ArrayList<>();
								list.add(apEntry.getKey());
								reconversionMap.put(ap.firstIPSrcToString(), list);
							} else {
								reconversionMap.get(ap.firstIPSrcToString()).add(apEntry.getKey());
							}
						} else if(ap.hasIPDstEqualOrIncludedIn(natIPSrcAddressList)) {
							//4*: src not included in NAT src, dest included in NAT src -> reconverted predicate {public address, private address}
							if(!reconvertedMap.containsKey(ap.firstIPSrcToString())) {
								List<Integer> list = new ArrayList<>();
								list.add(apEntry.getKey());
								reconvertedMap.put(ap.firstIPSrcToString(), list);
							} else {
								reconvertedMap.get(ap.firstIPSrcToString()).add(apEntry.getKey());
							}
						}
					} else if(ap.hasIPSrcEqualOrIncludedIn(natIPSrcAddressList) && ap.hasIPDstEqualOrIncludedIn(natIPSrcAddressList)) {
						//5*: src included in NAT src (private) and dst included in NAT src (private) -> predicate is just forwarded without transformation
						notChaingingPredicateList.add(apEntry.getKey());
					}
				}
				//Fill the map: to each shadowing predicate assign the corresponding shadowed predicate, to each re conversion predicate assign the corresponding
				//list of re converted predicates. NOTE: take also in consideration the ports and prototype of the predicate
				for(HashMap.Entry<String, List<Integer>> entry: shadowingMap.entrySet()) {
					for(Integer shing: entry.getValue()) {
						List<Integer> result = new ArrayList<>();
						if(!shadowedMap.containsKey(entry.getKey())) break;
						for(Integer shed: shadowedMap.get(entry.getKey())) {
							if(aputilsAP.APComparePrototypeList(
									networkAtomicPredicates.get(shing).getProtoTypeList(), networkAtomicPredicates.get(shed).getProtoTypeList())
									&& aputilsAP.APComparePortList(networkAtomicPredicates.get(shing).getpSrcList(), networkAtomicPredicates.get(shed).getpSrcList())
									&& aputilsAP.APComparePortList(networkAtomicPredicates.get(shing).getpDstList(), networkAtomicPredicates.get(shed).getpDstList())) 
								result.add(shed);
						}
						resultMap.put(shing, result);
					}
				}
				for(HashMap.Entry<String, List<Integer>> entry: reconversionMap.entrySet()) {
					for(Integer rcvion: entry.getValue()) {
						List<Integer> result = new ArrayList<>();
						if(!reconvertedMap.containsKey(entry.getKey())) break;
						for(Integer rcved: reconvertedMap.get(entry.getKey())) {
							if(aputilsAP.APComparePrototypeList(
									networkAtomicPredicates.get(rcvion).getProtoTypeList(), networkAtomicPredicates.get(rcved).getProtoTypeList())
									&& aputilsAP.APComparePortList(networkAtomicPredicates.get(rcvion).getpSrcList(), networkAtomicPredicates.get(rcved).getpSrcList())
									&& aputilsAP.APComparePortList(networkAtomicPredicates.get(rcvion).getpDstList(), networkAtomicPredicates.get(rcved).getpDstList()))
								result.add(rcved);
						}
						resultMap.put(rcvion, result);
					}
				}
			}
		}
		System.out.println();
	}
	
	/** This function, starting from source and destination of each requirement, computes the related atomic predicates. Then add to the computed set
	 * also atomic predicates representing input packet classes for each transformer (here we are considering only NAT and firewall)
	 * @return HashMap<Integer,Predicate> mapping the predicates to integers
	 * */
	private HashMap<Integer, Predicate> generateAtomicPredicateNew(){
		List<Predicate> predicates = new ArrayList<>();
		List<Predicate> atomicPredicates = new ArrayList<>();
		List<String> srcList = new ArrayList<>();
		List<String> dstList = new ArrayList<>();
		List<String> srcPList = new ArrayList<>();
		List<String> dstPList = new ArrayList<>();
		List<L4ProtocolTypes> dstProtoList = new ArrayList<>();

		//Generate predicates representing source and predicates representing destination of each requirement
		for(SecurityRequirement sr : securityRequirements.values()) {
			Property property = sr.getOriginalProperty();
			String IPSrc = property.getSrc();
			String IPDst = property.getDst();
			String pSrc = property.getSrcPort() != null &&  !property.getSrcPort().equals("null") ? property.getSrcPort() : "*";
			String pDst = property.getDstPort() != null &&  !property.getDstPort().equals("null") ? property.getDstPort() : "*";
			L4ProtocolTypes proto = property.getLv4Proto() != null ? property.getLv4Proto() : L4ProtocolTypes.ANY;
			srcList.add("*"); dstList.add("*"); srcPList.add("*"); dstPList.add("*"); dstProtoList.add(L4ProtocolTypes.ANY);
			
			//if we have already inserted this source into the list, we can skip it
			if(!srcList.contains(IPSrc) || !srcPList.contains(pSrc)) {
				if(!srcList.contains(IPSrc))
					srcList.add(IPSrc);
				else IPSrc = "*";
				if(!srcPList.contains(pSrc)) 
					srcPList.add(pSrc);
				else pSrc = "*";
				
				Predicate srcPredicate = new Predicate(IPSrc, false, "*", false, pSrc, false, "*", false, L4ProtocolTypes.ANY);
				predicates.add(srcPredicate);
			}
			
			//if we have already inserted this destination into the list, we can skip it
			if(!dstList.contains(IPDst) || !dstPList.contains(pDst) || !dstProtoList.contains(proto)) {
				if(!dstList.contains(IPDst)) dstList.add(IPDst);
				else IPDst = "*";
				if(!dstPList.contains(pDst)) dstPList.add(pDst);
				else pDst = "*";
				if(!dstProtoList.contains(proto)) dstProtoList.add(proto);
				else proto = L4ProtocolTypes.ANY;
				
				Predicate dstPredicate = new Predicate("*", false, IPDst, false, "*", false, pDst, false, proto);
				predicates.add(dstPredicate);
			}
		}

		//Generate predicates representing input packet class for each transformers
		for(Node node: transformersNode.values()) {
			if(node.getFunctionalType() == FunctionalTypes.NAT) {
				//Compute list of shadowed and reconverted (only those related to requirements sources), considering NAT source addresses list
				List<String> shadowedAddressesListSrc = new ArrayList<>();
				List<String> shadowedAddressesListDst = new ArrayList<>();
				for(String shadowedAddress: node.getConfiguration().getNat().getSource()) {
					for(String ips: srcList) {
						if(shadowedAddress.equals(ips) || aputilsAP.isIncludedIPString(shadowedAddress, ips)) {
							shadowedAddressesListSrc.add(shadowedAddress);
							break;
						}
					}
					for(String ipd: dstList) {
						if(shadowedAddress.equals(ipd) || aputilsAP.isIncludedIPString(shadowedAddress, ipd)) {
							shadowedAddressesListDst.add(shadowedAddress);
							break;
						}
					}
				}
				//Generate and add shadowing predicates
				for(String shadowed: shadowedAddressesListSrc) {
					if(!srcList.contains(shadowed)) {
						Predicate shpred = new Predicate(shadowed, false, "*", false, "*", false, "*", false, L4ProtocolTypes.ANY);
						predicates.add(shpred);
					}
				}
				//Generate and add reconverted predicates
				for(String shadowed: shadowedAddressesListDst) {
					if(!dstList.contains(shadowed)) {
						Predicate rcvedpred = new Predicate("*", false, shadowed, false, "*", false, "*", false, L4ProtocolTypes.ANY);
						predicates.add(rcvedpred);
					}
				}
				//Reconversion predicate
				if(!dstList.contains(node.getName())) {
					Predicate rcpred = new Predicate("*", false, node.getName(), false, "*", false, "*", false, L4ProtocolTypes.ANY);
					predicates.add(rcpred);
				}
				//Add shadowed predicate: this is enough, all the others have already been added
				predicates.add(new Predicate(node.getName(), false, "*", false, "*", false, "*", false, L4ProtocolTypes.ANY));	
			} 
				//If the node is a firewall, compute its allowed rules list
				//Algorithm 1 Yang_Lam 2015
				else if(node.getFunctionalType() == FunctionalTypes.FIREWALL) {
				
				List<Predicate> allowedList = new ArrayList<>();
				List<Predicate> deniedList = new ArrayList<>();
				
				boolean deniedListChanged = false;
				for(Elements rule: node.getConfiguration().getFirewall().getElements()) {
					if(rule.getAction().equals(ActionTypes.DENY)) {
						//deny <--- deny V rule-i
						deniedList.add(new Predicate(rule.getSource(), false, rule.getDestination(), false, 
								rule.getSrcPort(), false, rule.getDstPort(), false, rule.getProtocol()));
						deniedListChanged = true;
					} else {
						//allowed <--- allowed V (rule-i AND !denied)
						Predicate toAdd = new Predicate(rule.getSource(), false, rule.getDestination(), false, 
								rule.getSrcPort(), false, rule.getDstPort(), false, rule.getProtocol());
						List<Predicate> allowedToAdd = aputilsAP.computeAllowedForRule(toAdd, deniedList, deniedListChanged);
						for(Predicate allow: allowedToAdd) {
							if(!aputilsAP.isPredicateContainedIn(allow, allowedList))
								allowedList.add(allow);
						}
					}
				}
				//Check default action: if DENY do nothing
				if(node.getConfiguration().getFirewall().getDefaultAction().equals(ActionTypes.ALLOW)) {
					Predicate toAdd = new Predicate("*", false, "*", false, "*", false, "*", false, L4ProtocolTypes.ANY);
					List<Predicate> allowedToAdd = aputilsAP.computeAllowedForRule(toAdd, deniedList, deniedListChanged);
					for(Predicate allow: allowedToAdd) {
						if(!aputilsAP.isPredicateContainedIn(allow, allowedList))
							allowedList.add(allow);
					}
				}
				
				//Insert allowed list into predicates (with optimization)
				for(Predicate p: allowedList) {
					for(IPAddress IPSrc: p.getIPSrcList()) {
						String ips = IPSrc.toString();
						if(!srcList.contains(ips)) {
							srcList.add(ips);
							predicates.add(new Predicate(ips, false, "*", false, "*", false, "*", false, L4ProtocolTypes.ANY));
						}
					}
					for(IPAddress IPDst: p.getIPDstList()) {
						String ipd = IPDst.toString();
						if(!dstList.contains(ipd)) {
							dstList.add(ipd);
							predicates.add(new Predicate("*", false, ipd, false, "*", false, "*", false, L4ProtocolTypes.ANY));
						}
					}
					for(PortInterval pSrc: p.getpSrcList()) {
						String ps = pSrc.toString();
						if(!srcPList.contains(ps)) {
							srcPList.add(ps);
							predicates.add(new Predicate("*", false, "*", false, ps, false, "*", false, L4ProtocolTypes.ANY));
						}
					}
					for(PortInterval pDst: p.getpDstList()) {
						String pd = pDst.toString();
						if(!dstPList.contains(pd)) {
							dstPList.add(pd);
							predicates.add(new Predicate("*", false, "*", false, "*", false, pd, false, L4ProtocolTypes.ANY));
						}
					}
					for(L4ProtocolTypes proto: p.getProtoTypeList()) {
						if(!dstProtoList.contains(proto)) {
							dstProtoList.add(proto);
							predicates.add(new Predicate("*", false, "*", false, "*", false, "*", false, proto));
						}
					}
				}
				
				//the algorithm returns the allowed predicates list (if we want also the denied predicates list, we can compute allowed list negation)
				allocationNodesAP.get(node.getName()).setForwardBehaviourPredicateList(allowedList);
			} else if(node.getFunctionalType() == FunctionalTypes.STATEFUL_FIREWALL) {
				
				AllocationNodeAP an  = allocationNodesAP.get(node.getName());
				StatefulPacketFilter spf = (StatefulPacketFilter) an.getPlacedNF();
				
				
				List<Predicate> allowedList = new ArrayList<>();
				List<Predicate> deniedList = new ArrayList<>();
				int aIndex = 0;
		   		int dIndex = 0;
		   		int acIndex = 0;
				
				boolean deniedListChanged = false;
				for(Elements rule: node.getConfiguration().getStatefulFirewall().getElements()) {
					if(rule.getAction().equals(ActionTypes.DENY)) {
						//deny <--- deny V rule-i
						Predicate predicate = new Predicate(rule.getSource(), false, rule.getDestination(), false, 
								rule.getSrcPort(), false, rule.getDstPort(), false, rule.getProtocol());
						deniedList.add(predicate);
						spf.addDenyPredicate(dIndex++, predicate);
						deniedListChanged = true;
					} else if (rule.getAction().equals(ActionTypes.ALLOW)) {
						//allowed <--- allowed V (rule-i AND !denied)
						Predicate toAdd = new Predicate(rule.getSource(), false, rule.getDestination(), false, 
								rule.getSrcPort(), false, rule.getDstPort(), false, rule.getProtocol());
						List<Predicate> allowedToAdd = aputilsAP.computeAllowedForRule(toAdd, deniedList, deniedListChanged);
						for(Predicate allow: allowedToAdd) {
							if(!aputilsAP.isPredicateContainedIn(allow, allowedList))
								allowedList.add(allow);
						}
						//spf.addAllowPredicate(aIndex++, toAdd);
					} else {
						Predicate predicate = new Predicate(rule.getSource(), false, rule.getDestination(), false, 
								rule.getSrcPort(), false, rule.getDstPort(), false, rule.getProtocol());
						Predicate invPredicate = new Predicate(rule.getDestination(), false, rule.getSource(), false, 
								rule.getDstPort(), false, rule.getSrcPort(), false, rule.getProtocol());
						if(node.getConfiguration().getStatefulFirewall().getDefaultAction().equals(ActionTypes.DENY)) {
							List<Predicate> allowedToAdd = aputilsAP.computeAllowedForRule(predicate, deniedList, deniedListChanged);
							for(Predicate allow: allowedToAdd) {
								if(!aputilsAP.isPredicateContainedIn(allow, allowedList))
									allowedList.add(allow);
							}
						}
						spf.addAllowCondPredicate(acIndex, predicate);
						spf.addAllowCondInvPredicate(acIndex++, invPredicate);
					}
				}
				//Check default action: if DENY do nothing
				if(node.getConfiguration().getStatefulFirewall().getDefaultAction().equals(ActionTypes.ALLOW)) {
					Predicate toAdd = new Predicate("*", false, "*", false, "*", false, "*", false, L4ProtocolTypes.ANY);
					List<Predicate> allowedToAdd = aputilsAP.computeAllowedForRule(toAdd, deniedList, deniedListChanged);
					for(Predicate allow: allowedToAdd) {
						if(!aputilsAP.isPredicateContainedIn(allow, allowedList))
							allowedList.add(allow);
					}
				}
				
				//Insert allowed list into predicates (with optimization)
				for(Predicate p: allowedList) {
					
					//insert allowed list in the map of SPF
					spf.addAllowPredicate(aIndex++, p);
					
					
					for(IPAddress IPSrc: p.getIPSrcList()) {
						String ips = IPSrc.toString();
						if(!srcList.contains(ips)) {
							srcList.add(ips);
							predicates.add(new Predicate(ips, false, "*", false, "*", false, "*", false, L4ProtocolTypes.ANY));
						}
					}
					for(IPAddress IPDst: p.getIPDstList()) {
						String ipd = IPDst.toString();
						if(!dstList.contains(ipd)) {
							dstList.add(ipd);
							predicates.add(new Predicate("*", false, ipd, false, "*", false, "*", false, L4ProtocolTypes.ANY));
						}
					}
					for(PortInterval pSrc: p.getpSrcList()) {
						String ps = pSrc.toString();
						if(!srcPList.contains(ps)) {
							srcPList.add(ps);
							predicates.add(new Predicate("*", false, "*", false, ps, false, "*", false, L4ProtocolTypes.ANY));
						}
					}
					for(PortInterval pDst: p.getpDstList()) {
						String pd = pDst.toString();
						if(!dstPList.contains(pd)) {
							dstPList.add(pd);
							predicates.add(new Predicate("*", false, "*", false, "*", false, pd, false, L4ProtocolTypes.ANY));
						}
					}
					for(L4ProtocolTypes proto: p.getProtoTypeList()) {
						if(!dstProtoList.contains(proto)) {
							dstProtoList.add(proto);
							predicates.add(new Predicate("*", false, "*", false, "*", false, "*", false, proto));
						}
					}
				}
				
				//Insert predicates related to the "conditional allow" rules
				List<Predicate> mergedList = new ArrayList<>();
				mergedList.addAll(spf.getAllowCondPredicates().values());
				mergedList.addAll(spf.getAllowCondInvPredicates().values());
				for(Predicate p : mergedList) {
					for(IPAddress IPSrc: p.getIPSrcList()) {
						String ips = IPSrc.toString();
						if(!srcList.contains(ips)) {
							srcList.add(ips);
							predicates.add(new Predicate(ips, false, "*", false, "*", false, "*", false, L4ProtocolTypes.ANY));
						}
					}
					for(IPAddress IPDst: p.getIPDstList()) {
						String ipd = IPDst.toString();
						if(!dstList.contains(ipd)) {
							dstList.add(ipd);
							predicates.add(new Predicate("*", false, ipd, false, "*", false, "*", false, L4ProtocolTypes.ANY));
						}
					}
					for(PortInterval pSrc: p.getpSrcList()) {
						String ps = pSrc.toString();
						if(!srcPList.contains(ps)) {
							srcPList.add(ps);
							predicates.add(new Predicate("*", false, "*", false, ps, false, "*", false, L4ProtocolTypes.ANY));
						}
					}
					for(PortInterval pDst: p.getpDstList()) {
						String pd = pDst.toString();
						if(!dstPList.contains(pd)) {
							dstPList.add(pd);
							predicates.add(new Predicate("*", false, "*", false, "*", false, pd, false, L4ProtocolTypes.ANY));
						}
					}
					for(L4ProtocolTypes proto: p.getProtoTypeList()) {
						if(!dstProtoList.contains(proto)) {
							dstProtoList.add(proto);
							predicates.add(new Predicate("*", false, "*", false, "*", false, "*", false, proto));
						}
					}
				}
				
				//the algorithm returns the allowed predicates list (if we want also the denied predicates list, we can compute allowed list negation)
				allocationNodesAP.get(node.getName()).setForwardBehaviourPredicateList(allowedList);
				
			}
		}

		//DEBUG: interesting predicates for requirements source and destination
		System.out.println("INTERESTING PREDICATES: " + predicates.size());
//		for(Predicate p: predicates)
//			p.print();
		//END DEBUG

		//Now we have the list of predicates on which we have to compute the set of atomic predicates, so compute atomic predicates
		atomicPredicates = aputilsAP.computeAtomicPredicates(atomicPredicates, predicates);
		
		//Give to each atomic predicate an identifier
		int index = 0;
		for(Predicate p: atomicPredicates) {
			networkAtomicPredicates.put(index, p);
			index++;
		}
		
		//DEBUG: print atomic predicates
//		System.out.println("ATOMIC PREDICATES " + networkAtomicPredicates.size());
//		for(HashMap.Entry<Integer, Predicate> entry: networkAtomicPredicates.entrySet()) {
//			System.out.print(entry.getKey() + " ");
//			entry.getValue().print();
//		}
		//END DEBUG
	
		return networkAtomicPredicates;
	}
	
/*****************************************************AP duplicated methods**************************************************************************/
	/**
	 * This method allocates the functions on allocation nodes that are empty.
	 * At the moment only packet-filtering capability is allocated, in the future the decision will depend on the type of requirement.
	 */
	private void allocateFunctionsAP() {
		for(FlowPathAP sr : trafficFlowsMapAP.values()) {
			List<AllocationNodeAP> nodes = sr.getPath();
			int lengthList = nodes.size();
			AllocationNodeAP source = nodes.get(0);
			AllocationNodeAP last = nodes.get(lengthList-1);
			for(int i = 1; i < lengthList-1; i++) {
				allocationManager.chooseFunctionsAP(nodes.get(i), source, last);
			}
		}
		
	}


	/**
	 * This method creates the hard constraints in the z3 model for reach-ability and isolation requirements.
	 */
	private void formalizeRequirementsAP() {
		
		for(SecurityRequirement sr : securityRequirements.values()) {
			switch (sr.getOriginalProperty().getName()) {
			case ISOLATION_PROPERTY:
				check.createRequirementConstraintsAP(sr, Prop.ISOLATION);
				break;
			case REACHABILITY_PROPERTY:
				check.createRequirementConstraintsAP(sr, Prop.REACHABILITY);
				break;
			case COMPLETE_REACHABILITY_PROPERTY:
				check.createRequirementConstraintsAP(sr, Prop.COMPLETE_REACHABILITY);
				break;
			default:
				throw new BadGraphError("Error in the property definition", EType.INVALID_PROPERTY_DEFINITION);
			}
				
		}
		
	}


	/**
	 * For each requirement, this method identifies all the possible the paths of nodes that must be crossed by the traffic flows that are related to the requirement.
	 * @return the map of all the traffic flows
	 */
	private HashMap<Integer, FlowPathAP> generateFlowPathsAP(){
		HashMap<Integer, FlowPathAP> flowsMap = new HashMap<>();
		int id = 0;
		
		for(SecurityRequirement sr : securityRequirements.values()) {
			Property property = sr.getOriginalProperty();
			
			//first, this method finds if a forwarding path has been defined by the user for the requirement
			//in that case, the research is not performed for that specific requirement
			
			Path definedPath = null;
			if(paths != null) {
				for(Path p : paths) {
					String first = p.getPathNode().get(0).getName();
					String last = p.getPathNode().get(p.getPathNode().size()-1).getName();
					if(first.equals(property.getSrc()) && last.equals(property.getDst())) {
						definedPath = p;
					}	
				}
			}
			
			
			boolean found = false;
			List<List<AllocationNodeAP>> allPaths = new ArrayList<>();
			List<AllocationNodeAP> localPath = new ArrayList<>();
			//if no forwarding path has been defined by the user, the framework searches for ALL the possible existing path.
			//for each path, a corresponding flow is defined. The traffic characterization will be made in a different moment.
			if(definedPath == null) {
				Set<String> visited = new HashSet<>();
				AllocationNodeAP source = allocationNodesAP.get(property.getSrc());
				AllocationNodeAP destination = allocationNodesAP.get(property.getDst());
				recursivePathGenerationAP(allPaths, localPath, source, destination, source, visited, 0);
				found = allPaths.isEmpty()? false : true;
				visited.clear();
			}else {
				//otherwise, the nodes of the path are simply put in the list
				found = true;
				for(PathNode pn : definedPath.getPathNode()) {
					AllocationNodeAP an = allocationNodesAP.get(pn.getName());
					localPath.add(an);
				}
				allPaths.add(localPath);
			}
			
			if(found) {
				for(List<AllocationNodeAP> singlePath : allPaths) {
					FlowPathAP flow = new FlowPathAP(sr, singlePath, id);
					flowsMap.put(id, flow);
					sr.getFlowsMapAP().put(id, flow);
					id++;
				}
				
			} else {
				throw new BadGraphError("There is no path between " + property.getSrc() + " and " + property.getDst(),
						EType.INVALID_SERVICE_GRAPH);
			}
		
		}
		
		return flowsMap;	
	}

	/**
	 * This method is recursively called to generate the path of nodes for each requirement specific to Atomic Predicates.
	 * @param allPaths it is the list of all the paths that have been computed for the requirement
	 * @param currentPath it is the current path that the method is building 
	 * @param source it is the source of the path
	 * @param destination it is the destination of the path
	 * @param current it is the current node in the recursive visit
	 * @param visited it is a list of nodes that have been already visited
	 * @param level it is the recursion level of the visit
	 * @return true if a path has been identified, false otherwise
	 */
	private void recursivePathGenerationAP(List<List<AllocationNodeAP>> allPaths, List<AllocationNodeAP> currentPath, AllocationNodeAP source,
			AllocationNodeAP destination, AllocationNodeAP current, Set<String> visited, int level) {
		
		currentPath.add(level, current);
		visited.add(current.getNode().getName());
		List<Neighbour> listNeighbours = current.getNode().getNeighbour();
		if(destination.getNode().getName().equals(current.getNode().getName())) {
			//I save the completed path and search for others
			List<AllocationNodeAP> pathToStore = new ArrayList<>();
			for(int i = 0; i < currentPath.size(); i++) {
				if((currentPath.get(i).getNode().getFunctionalType() == FunctionalTypes.NAT 
						|| currentPath.get(i).getNode().getFunctionalType() == FunctionalTypes.FIREWALL
						&& !transformersNode.containsKey(currentPath.get(i).getNode().getName())))
					transformersNode.put(currentPath.get(i).getNode().getName(), currentPath.get(i).getNode());
				pathToStore.add(i, currentPath.get(i));
			}
			allPaths.add(pathToStore);
			visited.remove(current.getNode().getName());
			currentPath.remove(level);
			return;
		}
		if(level != 0) {
			if(current.getNode().getFunctionalType() == FunctionalTypes.WEBCLIENT || current.getNode().getFunctionalType() == FunctionalTypes.WEBSERVER) {
				//traffic is not forwarded anymore
				visited.remove(current.getNode().getName());
				currentPath.remove(level);
				return;
			}
		}
		
		

		for(Neighbour n : listNeighbours) {
			if(!visited.contains(n.getName())) {
				AllocationNodeAP neighbourNode = allocationNodesAP.get(n.getName());
				level++;
				recursivePathGenerationAP(allPaths, currentPath, source, destination, neighbourNode, visited, level);
				level--;
			}
					
		}
		
		visited.remove(current.getNode().getName());
		currentPath.remove(level);
		return;
	}


	
	/**
	 * This method generates the NetContext object for the initialization of z3 model specific to atomic Predicates.
	 * @param ctx2 it is the z3 Context object
	 * @param nodes2 is is the list of nodes of the Allocation Graph
	 * @param prop it is the list of properties to be satisfied
	 * @param allocationNodes2 it is the list of allocation nodes
	 * @return the NetContext object
	 */
	private NetContextAP nctxGenerateAP(Context ctx2, List<Node> nodes2, List<Property> prop,
			HashMap<String, AllocationNodeAP> allocationNodes2) {
		for (Node n : nodes) {
			if (n.getName().contains("@"))
				throw new BadGraphError("Invalid node name " + n.getName() + ", it can't contain @",
						EType.INVALID_SERVICE_GRAPH);
		}
		String[] nodesname = {};
		nodesname = nodes.stream().map((n) -> n.getName()).collect(Collectors.toCollection(ArrayList<String>::new))
				.toArray(nodesname);
		String[] nodesip = nodesname;
		String[] src_portRange = {};
		src_portRange = properties.stream().map(p -> p.getSrcPort()).filter(p -> p != null)
				.collect(Collectors.toCollection(ArrayList<String>::new)).toArray(src_portRange);
		String[] dst_portRange = {};
		dst_portRange = properties.stream().map(p -> p.getDstPort()).filter(p -> p != null)
				.collect(Collectors.toCollection(ArrayList<String>::new)).toArray(dst_portRange);
		return new NetContextAP(ctx, allocationNodesAP, nodesname, nodesip, src_portRange, dst_portRange);
	}

	/**
	 * Checks if the service graph satisfies all the imposed conditions
	 * 
	 * @return
	 */
	public VerificationResult checkNFFGPropertyAP() {
		VerificationResult ret = this.check.propertyCheckAP();
		ret.time = this.check.getTimeChecker();
		return ret;
	}

	/**
	 * Get Net Context
	 * 
	 * @return the net context
	 */
	public NetContextAP getNctxAP() {
		return nctxAP;
	}


	/**
	 * @return all the allocation nodes
	 */
	public Map<String, AllocationNodeAP> getAllocationNodesAP() {
		return allocationNodesAP;
	}

	
	/**
	 * @return all the requirements
	 */
	public Map<Integer, FlowPathAP> getTrafficFlowsMapAP(){
		return trafficFlowsMapAP;
	}
	
	
	/**
	 * Get Time Results
	 * 
	 * @return the Time Results
	 */
	public TestResults getTestTimeResults() {
		return testResults;
	}
	
	/**
	 * Get Context
	 * 
	 * @return the context
	 */
	public Context getCtx() {
		return ctx;
	}

	/**
	 * Get the list of properties
	 * 
	 * @return the property list
	 */
	public List<Property> getProperties() {
		return properties;
	}

	/**
	 * Get the list of paths
	 * 
	 * @return the path list
	 */
	public List<Path> getPaths() {
		return paths;
	}

	/**
	 * Get the wildCardManager object
	 * 
	 * @return the Wild Card Manager object
	 */
	public WildcardManager getWildcardManager() {
		return wildcardManager;
	}

	/**
	 * Get the Security Requirements and there integer mapping
	 * 
	 * @return the HashMap containing Security Requirements with there integer mapping
	 */
	public HashMap<Integer, SecurityRequirement> getSecurityRequirements() {
		return securityRequirements;
	}

	/**
	 * Get the Checker
	 * 
	 * @return the Checker object
	 */
	public Checker getCheck() {
		return check;
	}

	/**
	 * Get the list of Nodes
	 * 
	 * @return the Nodes list
	 */
	public List<Node> getNodes() {
		return nodes;
	}

	/**
	 * Get the list of Node Metrics
	 * 
	 * @return the Node Metrics list
	 */
	public List<NodeMetrics> getNodeMetrics() {
		return nodeMetrics;
	}

	/**
	 * Get the list of properties
	 * 
	 * @return the property list
	 */
	public AllocationManager getAllocationManager() {
		return allocationManager;
	}

	/**
	 * Get the APUtilsAP
	 * 
	 * @return the APUtilsAP class Object
	 */
	public APUtilsAP getAputilsAP() {
		return aputilsAP;
	}
	
	/**
	 * Get the Network Atomic Predicates
	 * 
	 * @return the HashMap of Network Atomic Predicates with integer mapping
	 */
	public HashMap<Integer, Predicate> getNetworkAtomicPredicates() {
		return networkAtomicPredicates;
	}

	public HashMap<String, Node> getTransformersNode() {
		return transformersNode;
	}

	public TestResults getTestResults() {
		return testResults;
	}
/*****************************************************MF duplicate methods***************************************************************************/
	/**
	 * Checks if the service graph satisfies all the imposed conditions
	 * 
	 * @return
	 */
	public VerificationResult checkNFFGPropertyMF() {
		VerificationResult ret = this.check.propertyCheckMF();
		ret.time = this.check.getTimeChecker();
		return ret;
	}

	/**
	 * Get Net Context
	 * 
	 * @return the net context
	 */
	public NetContextMF getNctxMF() {
		return nctxMF;
	}


	/**
	 * @return all the allocation nodes
	 */
	public Map<String, AllocationNodeMF> getAllocationNodesMF() {
		return allocationNodesMF;
	}

	
	/**
	 * @return all the requirements
	 */
	public Map<Integer, FlowPathMF> getTrafficFlowsMapMF(){
		return trafficFlowsMapMF;
	}
	
	/**
	 * Get the APUtilsMF
	 * 
	 * @return the APUtilsMF class Object
	 */
	public APUtilsMF getAputilsMF() {
		return aputilsMF;
	}

	
	/**
	 * This method allocates the functions on allocation nodes that are empty specific to Maximal Flows.
	 * At the moment only packet-filtering capability is allocated, in the future the decision will depend on the type of requirement.
	 */
	private void allocateFunctionsMF() {
		for(FlowPathMF sr : trafficFlowsMapMF.values()) {
			List<AllocationNodeMF> nodes = sr.getPath();
			int lengthList = nodes.size();
			AllocationNodeMF source = nodes.get(0);
			AllocationNodeMF last = nodes.get(lengthList-1);
			for(int i = 1; i < lengthList-1; i++) {
				allocationManager.chooseFunctionsMF(nodes.get(i), source, last);
			}
		}
		
	}
	
	/**
	 * This method is recursively called to generate the path of nodes for each requirement specific to Maximal flows.
	 * @param allPaths it is the list of all the paths that have been computed for the requirement
	 * @param currentPath it is the current path that the method is building 
	 * @param source it is the source of the path
	 * @param destination it is the destination of the path
	 * @param current it is the current node in the recursive visit
	 * @param visited it is a list of nodes that have been already visited
	 * @param level it is the recursion level of the visit
	 * @return true if a path has been identified, false otherwise
	 */
	private void recursivePathGenerationMF(List<List<AllocationNodeMF>> allPaths, List<AllocationNodeMF> currentPath, AllocationNodeMF source,
			AllocationNodeMF destination, AllocationNodeMF current, Set<String> visited, int level) {
		
		currentPath.add(level, current);
		visited.add(current.getNode().getName());
		List<Neighbour> listNeighbours = current.getNode().getNeighbour();
		if(destination.getNode().getName().equals(current.getNode().getName())) {
			//I save the completed path and search for others
			List<AllocationNodeMF> pathToStore = new ArrayList<>();
			for(int i = 0; i < currentPath.size(); i++) {
				if((currentPath.get(i).getNode().getFunctionalType() == FunctionalTypes.NAT 
						|| currentPath.get(i).getNode().getFunctionalType() == FunctionalTypes.FIREWALL
						&& !transformersNode.containsKey(currentPath.get(i).getNode().getName())))
					transformersNode.put(currentPath.get(i).getNode().getName(), currentPath.get(i).getNode());
				pathToStore.add(i, currentPath.get(i));
			}
			allPaths.add(pathToStore);
			visited.remove(current.getNode().getName());
			currentPath.remove(level);
			return;
		}
		if(level != 0) {
			if(current.getNode().getFunctionalType() == FunctionalTypes.WEBCLIENT || current.getNode().getFunctionalType() == FunctionalTypes.WEBSERVER) {
				//traffic is not forwarded anymore
				visited.remove(current.getNode().getName());
				currentPath.remove(level);
				return;
			}
		}
		
		

		for(Neighbour n : listNeighbours) {
			if(!visited.contains(n.getName())) {
				AllocationNodeMF neighbourNode = allocationNodesMF.get(n.getName());
				level++;
				recursivePathGenerationMF(allPaths, currentPath, source, destination, neighbourNode, visited, level);
				level--;
			}
					
		}
		
		visited.remove(current.getNode().getName());
		currentPath.remove(level);
		return;
	}
	
	/**
	 * For each requirement, this method identifies all the possible the paths of nodes that must be crossed by the traffic flows that are related to the requirement specific to Maximal Flows.
	 * @return the map of all the traffic flows
	 */
	private HashMap<Integer, FlowPathMF> generateFlowPathsMF(){
		HashMap<Integer, FlowPathMF> flowsMap = new HashMap<>();
		int id = 0;
		
		for(SecurityRequirement sr : securityRequirements.values()) {
			Property property = sr.getOriginalProperty();
			
			//first, this method finds if a forwarding path has been defined by the user for the requirement
			//in that case, the research is not performed for that specific requirement
			
			Path definedPath = null;
			if(paths != null) {
				for(Path p : paths) {
					String first = p.getPathNode().get(0).getName();
					String last = p.getPathNode().get(p.getPathNode().size()-1).getName();
					if(first.equals(property.getSrc()) && last.equals(property.getDst())) {
						definedPath = p;
					}	
				}
			}
			
			
			boolean found = false;
			List<List<AllocationNodeMF>> allPaths = new ArrayList<>();
			List<AllocationNodeMF> localPath = new ArrayList<>();
			//if no forwarding path has been defined by the user, the framework searches for ALL the possible existing path.
			//for each path, a corresponding flow is defined. The traffic characterization will be made in a different moment.
			if(definedPath == null) {
				Set<String> visited = new HashSet<>();
				AllocationNodeMF source = allocationNodesMF.get(property.getSrc());
				AllocationNodeMF destination = allocationNodesMF.get(property.getDst());
				recursivePathGenerationMF(allPaths, localPath, source, destination, source, visited, 0);
				found = allPaths.isEmpty()? false : true;
				visited.clear();
			}else {
				//otherwise, the nodes of the path are simply put in the list
				found = true;
				for(PathNode pn : definedPath.getPathNode()) {
					AllocationNodeMF an = allocationNodesMF.get(pn.getName());
					localPath.add(an);
				}
				allPaths.add(localPath);
			}
			
			if(found) {
				for(List<AllocationNodeMF> singlePath : allPaths) {
					FlowPathMF flow = new FlowPathMF(sr, singlePath, id);
					flowsMap.put(id, flow);
					sr.getFlowsMapMF().put(id, flow);
					id++;
				}
				
			} else {
				throw new BadGraphError("There is no path between " + property.getSrc() + " and " + property.getDst(),
						EType.INVALID_SERVICE_GRAPH);
			}
		
		}
		
		return flowsMap;	
	}	
	
	/**
	 * This method generates the NetContext object for the initialization of z3 model.
	 * @param ctx2 it is the z3 Context object
	 * @param nodes2 is is the list of nodes of the Allocation Graph
	 * @param prop it is the list of properties to be satisfied
	 * @param allocationNodes2 it is the list of Maximal flows allocation nodes
	 * @return the NetContext object
	 */
	private NetContextMF nctxGenerateMF(Context ctx2, List<Node> nodes2, List<Property> prop,
			HashMap<String, AllocationNodeMF> allocationNodes2) {
		for (Node n : nodes) {
			if (n.getName().contains("@"))
				throw new BadGraphError("Invalid node name " + n.getName() + ", it can't contain @",
						EType.INVALID_SERVICE_GRAPH);
		}
		String[] nodesname = {};
		nodesname = nodes.stream().map((n) -> n.getName()).collect(Collectors.toCollection(ArrayList<String>::new))
				.toArray(nodesname);
		String[] nodesip = nodesname;
		String[] src_portRange = {};
		src_portRange = properties.stream().map(p -> p.getSrcPort()).filter(p -> p != null)
				.collect(Collectors.toCollection(ArrayList<String>::new)).toArray(src_portRange);
		String[] dst_portRange = {};
		dst_portRange = properties.stream().map(p -> p.getDstPort()).filter(p -> p != null)
				.collect(Collectors.toCollection(ArrayList<String>::new)).toArray(dst_portRange);
		return new NetContextMF(ctx, allocationNodesMF, nodesname, nodesip, src_portRange, dst_portRange);
	}
	
	
	/**
	 * This method creates the hard constraints in the z3 model for reach-ability and isolation requirements.
	 */
	private void formalizeRequirementsMF() {
		
		for(SecurityRequirement sr : securityRequirements.values()) {
			switch (sr.getOriginalProperty().getName()) {
			case ISOLATION_PROPERTY:
				check.createRequirementConstraintsMF(sr, Prop.ISOLATION);
				break;
			case REACHABILITY_PROPERTY:
				check.createRequirementConstraintsMF(sr, Prop.REACHABILITY);
				break;
			case COMPLETE_REACHABILITY_PROPERTY:
				check.createRequirementConstraintsMF(sr, Prop.COMPLETE_REACHABILITY);
				break;
			default:
				throw new BadGraphError("Error in the property definition", EType.INVALID_PROPERTY_DEFINITION);
			}
				
		}
		
	}
/***************************************************************** Maximal Flows Methods**************************************************************/
	
 // only for MF method
	private void fillTrasformersMap() {
		int nCrossedFirewalls = 0;
		int counter = 0;
		
		System.out.println(" ("+transformersNode.size() +" transformers)");
		
		//For each NAT/FIREWALL found in the paths, compute its input classes
		for(Node node: transformersNode.values()) {
			System.out.print("*");
			counter++;
			if(counter % 150 == 0) {
				counter = 0;
				System.out.println();
			}
			if(node.getFunctionalType() == FunctionalTypes.NAT) {
				List<IPAddress> sourceNatIPAddressList = new ArrayList<>();
				List<IPAddress> notSourceNatIPAddressList = new ArrayList<>();
				for(String ipSrc: node.getConfiguration().getNat().getSource()) {
					IPAddress natSrcAddress = new IPAddress(ipSrc, false);
					sourceNatIPAddressList.add(natSrcAddress);
					notSourceNatIPAddressList.add(new IPAddress(ipSrc, true));
				}
				notSourceNatIPAddressList.add(new IPAddress(node.getName(), true));
				
				//compute D1 transformation map
				List<Predicate> D1List = new ArrayList<>();
				for(IPAddress privateSrcAddress: sourceNatIPAddressList) {
					Predicate newPredicate = new Predicate("*", false, "*", false, "*", false, "*", false, L4ProtocolTypes.ANY);
					List<IPAddress> srcIPList = new ArrayList<>();
					srcIPList.add(privateSrcAddress);
					newPredicate.setIPSrcList(srcIPList);
					newPredicate.setIPDstList(notSourceNatIPAddressList);
					D1List.add(newPredicate);
				}
				natD1map.put(node.getName(), D1List);
				
				//compute D2 transformation map
				Predicate D2Predicate = new Predicate("*", false, node.getName(), false, "*", false, "*", false, L4ProtocolTypes.ANY);
				D2Predicate.setIPSrcList(notSourceNatIPAddressList);
				natD2map.put(node.getName(), D2Predicate);
				
				//compute D31 transformation
				List<Predicate> D31List = new ArrayList<>();
				for(IPAddress privateSrcAddress1: sourceNatIPAddressList) {
					for(IPAddress privateSrcAddress2: sourceNatIPAddressList) {
						if(!privateSrcAddress1.equals(privateSrcAddress2)) {
							List<IPAddress> srcList = new ArrayList<>();
							List<IPAddress> dstList = new ArrayList<>();
							srcList.add(privateSrcAddress1);
							dstList.add(privateSrcAddress2);
							Predicate newPredicate = new Predicate("*", false, "*", false, "*", false, "*", false, L4ProtocolTypes.ANY);
							newPredicate.setIPSrcList(srcList);
							newPredicate.setIPDstList(dstList);
							D31List.add(newPredicate);
						}
					}
				}
				natD31map.put(node.getName(), D31List);
				
				//Compute reconverted predicates
				List<Predicate> reconvertedList = new ArrayList<>();
				for(IPAddress privateSrcAddress: sourceNatIPAddressList) {
					List<IPAddress> dstList = new ArrayList<>();
					dstList.add(privateSrcAddress);
					Predicate newPredicate = new Predicate("*", false, "*", false, "*", false, "*", false, L4ProtocolTypes.ANY);
					newPredicate.setIPSrcList(notSourceNatIPAddressList);
					newPredicate.setIPDstList(dstList);
					reconvertedList.add(newPredicate);
				}
				natReconvertedMap.put(node.getName(), reconvertedList);
				
				
				//Compute D32 transformation
				Predicate D32Predicate = new Predicate("*", false, "*", false, "*", false, "*", false, L4ProtocolTypes.ANY);
				D32Predicate.setIPSrcList(notSourceNatIPAddressList);
				D32Predicate.setIPDstList(notSourceNatIPAddressList);
				natD32map.put(node.getName(), D32Predicate);
				
				//DEBUG: print all nat transformations
//				System.out.println("D1 transformations");
//				for(Predicate p: natD1map.get(node.getName()))
//					p.print();
//				System.out.println("D2 transformation");
//				natD2map.get(node.getName()).print();
//				System.out.println("D31 transformations");
//				for(Predicate p: natD31map.get(node.getName()))
//					p.print();
//				System.out.println("D32 transformation");
//				natD32map.get(node.getName()).print();
//				System.out.println("Reconverted predicates");
//				for(Predicate p: natReconvertedMap.get(node.getName()))
//					p.print();
//				System.out.println();
				//END DEBUG
				
			}

			else if(node.getFunctionalType() == FunctionalTypes.FIREWALL) {
				nCrossedFirewalls++;
				List<Predicate> allowedList = new ArrayList<>();
				List<Predicate> deniedList = new ArrayList<>();

				for(Elements rule: node.getConfiguration().getFirewall().getElements()) {
					if(rule.getAction().equals(ActionTypes.DENY)) {
						//deny <--- deny V rule-i
						deniedList.add(new Predicate(rule.getSource(), false, rule.getDestination(), false, 
								rule.getSrcPort(), false, rule.getDstPort(), false, rule.getProtocol()));
					} else {
						//allowed <--- allowed V (rule-i AND !denied)
						Predicate toAdd = new Predicate(rule.getSource(), false, rule.getDestination(), false, 
								rule.getSrcPort(), false, rule.getDstPort(), false, rule.getProtocol());
						List<Predicate> allowedToAdd = aputilsMF.computeAllowedForRule(toAdd, deniedList);
						for(Predicate allow: allowedToAdd) {
							if(!aputilsMF.isPredicateContainedIn(allow, allowedList))
								allowedList.add(allow);
						}
					}
				}
				//Check default action: if DENY do nothing
				if(node.getConfiguration().getFirewall().getDefaultAction().equals(ActionTypes.ALLOW)) {
					Predicate toAdd = new Predicate("*", false, "*", false, "*", false, "*", false, L4ProtocolTypes.ANY);
					List<Predicate> allowedToAdd = aputilsMF.computeAllowedForRule(toAdd, deniedList);
					for(Predicate allow: allowedToAdd) {
						if(!aputilsMF.isPredicateContainedIn(allow, allowedList))
							allowedList.add(allow);
					}
				}
				
				//Now we have the allowed list but it contains overlaps -> compute the atomic predicates
				List<Predicate> atomicPredicates = new ArrayList<>();
				List<Predicate> predicates = new ArrayList<>();
				List<String> srcList = new ArrayList<>();
				List<String> dstList = new ArrayList<>();
				List<String> srcPList = new ArrayList<>();
				List<String> dstPList = new ArrayList<>();
				List<L4ProtocolTypes> dstProtoList = new ArrayList<>();
				for(Predicate p: allowedList) {
					for(IPAddress IPSrc: p.getIPSrcList()) {
						String ips = IPSrc.toString();
						if(!srcList.contains(ips)) {
							srcList.add(ips);
							predicates.add(new Predicate(ips, false, "*", false, "*", false, "*", false, L4ProtocolTypes.ANY));
						}
					}
					for(IPAddress IPDst: p.getIPDstList()) {
						String ipd = IPDst.toString();
						if(!dstList.contains(ipd)) {
							dstList.add(ipd);
							predicates.add(new Predicate("*", false, ipd, false, "*", false, "*", false, L4ProtocolTypes.ANY));
						}
					}
					for(PortInterval pSrc: p.getpSrcList()) {
						String ps = pSrc.toString();
						if(!srcPList.contains(ps)) {
							srcPList.add(ps);
							predicates.add(new Predicate("*", false, "*", false, ps, false, "*", false, L4ProtocolTypes.ANY));
						}
					}
					for(PortInterval pDst: p.getpDstList()) {
						String pd = pDst.toString();
						if(!dstPList.contains(pd)) {
							dstPList.add(pd);
							predicates.add(new Predicate("*", false, "*", false, "*", false, pd, false, L4ProtocolTypes.ANY));
						}
					}
					for(L4ProtocolTypes proto: p.getProtoTypeList()) {
						if(!dstProtoList.contains(proto)) {
							dstProtoList.add(proto);
							predicates.add(new Predicate("*", false, "*", false, "*", false, "*", false, proto));
						}
					}
				}
				
				atomicPredicates = aputilsMF.computeAtomicPredicates(atomicPredicates, predicates);
				
				List<Predicate> newAllowedList = new ArrayList<>();
				List<Predicate> newDeniedList = new ArrayList<>();
				for(Predicate ap: atomicPredicates) {
					boolean found = false;
					for(Predicate allowed: allowedList) {
						Predicate intersection = aputilsMF.computeIntersection(allowed, ap);
						if(intersection != null && aputilsMF.APCompare(intersection, ap)) {
							found = true;
							break;
						}
					}
					
					if(found) 
						newAllowedList.add(ap);
					else newDeniedList.add(ap);
				}
				
				allowedFirewallPredicates.put(node.getName(), newAllowedList);
				deniedFirewallPredicates.put(node.getName(), newDeniedList);

				//DEBUG: print firewall allowed list
//				System.out.println("FIREWALL " + node.getName());
//				for(Predicate allowedPred: allowedFirewallPredicates.get(node.getName())) {
//					System.out.print("ALLOW "); allowedPred.print();
//				}
//				for(Predicate deniedPred: deniedFirewallPredicates.get(node.getName())) {
//					System.out.print("DENY "); deniedPred.print();
//				}
//				System.out.println();
				//END DEBUG
			}
		}
		System.out.println();
	}
	// only for MF method
	private void generateMaximalFlows() {
		System.out.println("Number of starting flows "+ trafficFlowsMapMF.size());
		int counter = 0;
		for(FlowPathMF flow : trafficFlowsMapMF.values()) {
			counter++;
			if(counter % 100 == 0) {
				System.out.print("*");
			}
			if(counter % 10000 == 0) {
				counter = 0;
				System.out.println();
			}
			//System.out.print("*");
			Property property = flow.getRequirement().getOriginalProperty();
			String pSrc = property.getSrcPort() != null && !property.getSrcPort().equals("null") ? property.getSrcPort() : "*";
			
			//Generate source predicate
			Predicate predicate = new Predicate(property.getSrc(), false, "*", false, pSrc, false, "*", false, L4ProtocolTypes.ANY);
			List<Predicate> currentMaximalFlow = new ArrayList<>();
			currentMaximalFlow.add(predicate);
			//preallocate the maximal flow list
			for(int i=1; i<flow.getPath().size(); i++) {
				Predicate voidPredicate = new Predicate("*", false, "*", false, "*", false, "*", false, L4ProtocolTypes.ANY);
				currentMaximalFlow.add(voidPredicate);
			}
			
			if(flow.getPath().size() > 1) {
				recursiveGenerateMaximalFlowsForwardUpdate(1, flow.getRequirement(), flow.getPath(), predicate, flow, currentMaximalFlow, false);
			} else {
				//The flow has only one node, so complete the predicate and add it to maximal flow list
				String pDst = property.getDstPort() != null && !property.getDstPort().equals("null") ? property.getDstPort() : "*";
				L4ProtocolTypes proto = property.getLv4Proto() != null ? property.getLv4Proto() : L4ProtocolTypes.ANY;
				Predicate destPredicate = new Predicate("*", false, property.getDst(), false, "*", false, pDst, false, proto);
				Predicate onlyPredicate = currentMaximalFlow.get(0);
				onlyPredicate.setIPDstList(destPredicate.getIPDstList());
				onlyPredicate.setpDstList(destPredicate.getpDstList());
				onlyPredicate.setProtoTypeList(destPredicate.getProtoTypeList());
				flow.addMaximalFlow(maximalFlowId, currentMaximalFlow);
				maximalFlowId++;
			}
		}
		
		
		for(FlowPathMF flow : trafficFlowsMapMF.values()) {
			List<AllocationNodeMF> path = flow.getPath();
			for(MaximalFlow maximalFlow: flow.getMaximalFlowsMap().values()) {
				int index = 0;
				for(Predicate predicate: maximalFlow.getPredicateList()) {
					AllocationNodeMF currentNode = path.get(index);
					
					if(currentNode.getNode().getFunctionalType() == FunctionalTypes.FIREWALL) {
						//Check if input predicate is dropped or allowed to pass
						boolean allowed = false;
						for(Predicate allowedPred: allowedFirewallPredicates.get(currentNode.getIpAddress())) {
							if(aputilsMF.computeIntersection(allowedPred, predicate) != null) {
								allowed = true;
								break;
							}
						}
						if(allowed) 
							currentNode.addForwardedPredicate(predicate);
						else currentNode.addDroppedPredicate(predicate);
					}
					
					currentNode.addPredicateInInput(flow.getIdFlow(), maximalFlow.getFlowId(), predicate);
					index++;
				}
			}
		}
		
		//DEBUG: print maximal flows
//		for(FlowPath flow : trafficFlowsMap.values()) {
//			for(MaximalFlow mf: flow.getMaximalFlowsMap().values()) {
//				System.out.println("\nNuovo flow:");
//				for(Predicate p: mf.getPredicateList())
//					p.print();
//			}
//		}
		//END DEBUG
		testResults.setTotalNumberGeneratedFlows(maximalFlowId);
		System.out.println();
	}
	
	private void recursiveGenerateMaximalFlowsForwardUpdate(int nodeIndex, SecurityRequirement sr, List<AllocationNodeMF> path, Predicate inputPredicate,
			FlowPathMF currentFlowPath, List<Predicate> currentList, boolean somethingChanged) {
		
		if(nodeIndex >= path.size()) {
			return;
		}
		
		AllocationNodeMF node = path.get(nodeIndex);
		
		if(nodeIndex == path.size() -1) {
			//We are in the last node of the path
			//Compute intersection with destination
			String dstPort = sr.getOriginalProperty().getDstPort() != null && !sr.getOriginalProperty().getDstPort().equals("null") ? sr.getOriginalProperty().getDstPort() : "*";
			L4ProtocolTypes proto = sr.getOriginalProperty().getLv4Proto() != null ? sr.getOriginalProperty().getLv4Proto() : L4ProtocolTypes.ANY;
			Predicate destPredicate = new Predicate("*", false, node.getIpAddress(), false, "*", false, dstPort, false, proto);
			Predicate intersectionPredicate = aputilsMF.computeIntersection(destPredicate, inputPredicate);
			
			if(intersectionPredicate != null) {
				currentList.set(nodeIndex, intersectionPredicate);
				//start backward traversal
				recursiveGenerateMaximalFlowsBackwardUpdate(nodeIndex-1, sr, path, intersectionPredicate, currentFlowPath, currentList, false);
			}
			return;
		}
		
		if(natD1map.containsKey(node.getIpAddress())) {
			//Node is a NAT
			//check if input Predicate has sourceIP == to nat IP
			List<IPAddress> natIPAddressList = new ArrayList<>();
			natIPAddressList.add(new IPAddress(node.getIpAddress(), false));
			if(aputilsMF.APCompareIPAddressList(inputPredicate.getIPSrcList(), natIPAddressList)) {
				//the predicate has already been shadowed in previous traversals, so simply change destination and forward
				Predicate newPredicate = new Predicate(currentList.get(nodeIndex));
				newPredicate.setIPDstList(inputPredicate.getIPDstList());
				newPredicate.setpDstList(inputPredicate.getpDstList());
				newPredicate.setProtoTypeList(inputPredicate.getProtoTypeList());
				currentList.set(nodeIndex, newPredicate);
				recursiveGenerateMaximalFlowsForwardUpdate(nodeIndex+1, sr, path, newPredicate, currentFlowPath, currentList, somethingChanged);
				return;
			}
			
			//check if it is a reconverted predicate. In that case forward the input packet saved in this node changing only source
			boolean isReconverted = false;
			for(Predicate reconvertedPredicate: natReconvertedMap.get(node.getIpAddress())) {
				Predicate intersection = aputilsMF.computeIntersection(inputPredicate, reconvertedPredicate);
				if(intersection != null && aputilsMF.APCompare(intersection, inputPredicate)) {
					if(aputilsMF.APCompareIPAddressList(currentList.get(nodeIndex).getIPDstList(), natIPAddressList)) {
						isReconverted = true;
						break;
					}
				}
			}
			if(isReconverted) {
				Predicate newPredicate = new Predicate(currentList.get(nodeIndex));
				newPredicate.setIPSrcList(inputPredicate.getIPSrcList());
				newPredicate.setpDstList(inputPredicate.getpDstList());
				newPredicate.setProtoTypeList(inputPredicate.getProtoTypeList());
				currentList.set(nodeIndex, newPredicate);
				recursiveGenerateMaximalFlowsForwardUpdate(nodeIndex+1, sr, path, newPredicate, currentFlowPath, currentList, somethingChanged);
				return;
			}
			
			//Compute intersection with D1
			for(Predicate D1Predicate: natD1map.get(node.getIpAddress())) {
				Predicate intersectingD1Predicate = aputilsMF.computeIntersection(D1Predicate, inputPredicate);
				if(intersectingD1Predicate != null) {
					//Do shadowing and generate a new flow
					List<Predicate> newCurrentList = aputilsMF.deepCopy(currentList);
					//change this node new input
					newCurrentList.set(nodeIndex, intersectingD1Predicate);
					//Generate new recursion with shadowed predicate as next input predicate (to subsequent node)
					Predicate shadowedPredicate = new Predicate(intersectingD1Predicate);
					List<IPAddress> srcList = new ArrayList<>();
					srcList.add(new IPAddress(node.getIpAddress(), false));
					shadowedPredicate.setIPSrcList(srcList);
					recursiveGenerateMaximalFlowsForwardUpdate(nodeIndex+1, sr, path, shadowedPredicate, currentFlowPath, newCurrentList, true);
				}
			}
			//Compute intersection with D2
			Predicate intersectingD2Predicate = aputilsMF.computeIntersection(natD2map.get(node.getIpAddress()), inputPredicate);
			if(intersectingD2Predicate != null) {
				//Do reconversion and generate new flows
				for(String natSrc: node.getNode().getConfiguration().getNat().getSource()) {
					List<Predicate> newCurrentList = aputilsMF.deepCopy(currentList);
					//change this node new input
					newCurrentList.set(nodeIndex, intersectingD2Predicate);
					//Generate new recursion with reconverted predicate as next input predicate (to subsequent node)
					Predicate reconvertedPredicate = new Predicate(intersectingD2Predicate);
					IPAddress natSrcAddress = new IPAddress(natSrc, false);
					List<IPAddress> dstList = new ArrayList<>();
					dstList.add(natSrcAddress);
					reconvertedPredicate.setIPDstList(dstList);
					recursiveGenerateMaximalFlowsForwardUpdate(nodeIndex+1, sr, path, reconvertedPredicate, currentFlowPath, newCurrentList, true);
				}
			}
			//Compute intersection with D31
			for(Predicate D31Predicate: natD31map.get(node.getIpAddress())) {
				Predicate intersectingD31Predicate = aputilsMF.computeIntersection(D31Predicate, inputPredicate);
				if(intersectingD31Predicate != null) {
					//change this node with new input
					List<Predicate> newCurrentList = aputilsMF.deepCopy(currentList);
					newCurrentList.set(nodeIndex, intersectingD31Predicate);
					//continue recursion without transformation
					recursiveGenerateMaximalFlowsForwardUpdate(nodeIndex+1, sr, path, intersectingD31Predicate, currentFlowPath, newCurrentList, somethingChanged);
				}
			}
			//Compute intersection with D32
			Predicate intersectingD32Predicate = aputilsMF.computeIntersection(natD32map.get(node.getIpAddress()), inputPredicate);
			if(intersectingD32Predicate != null) {
				//change this node with new input
				List<Predicate> newCurrentList = aputilsMF.deepCopy(currentList);
				newCurrentList.set(nodeIndex, intersectingD32Predicate);
				//continue recursion without transformation
				recursiveGenerateMaximalFlowsForwardUpdate(nodeIndex+1, sr, path, intersectingD32Predicate, currentFlowPath, newCurrentList, somethingChanged);
			}
		}
		else if (allowedFirewallPredicates.containsKey(node.getIpAddress())) {
			//Check intersection with allowed and denied list
			List<Predicate> trasformedPredicates = new ArrayList<>();
			for(Predicate allowedPredicate: allowedFirewallPredicates.get(node.getIpAddress())) {
				Predicate intersectionAllowed = aputilsMF.computeIntersection(allowedPredicate, inputPredicate);
				if(intersectionAllowed != null && !aputilsMF.APCompare(intersectionAllowed, inputPredicate))
					trasformedPredicates.add(intersectionAllowed);
			}
			for(Predicate deniedPredicate: deniedFirewallPredicates.get(node.getIpAddress())) {
				Predicate intersectionDenied = aputilsMF.computeIntersection(deniedPredicate, inputPredicate);
				if(intersectionDenied != null && !aputilsMF.APCompare(intersectionDenied, inputPredicate)) 
					trasformedPredicates.add(intersectionDenied);
			}
			
			//Generate the new flows
			if(trasformedPredicates.size() > 0) {
				for(Predicate newPredicate:trasformedPredicates) {
					List<Predicate> newCurrentList = aputilsMF.deepCopy(currentList);
					newCurrentList.set(nodeIndex, newPredicate);
					//continue recursion without transformation
					recursiveGenerateMaximalFlowsForwardUpdate(nodeIndex+1, sr, path, newPredicate, currentFlowPath, newCurrentList, somethingChanged);
				
				}
			} else {
				//simply forward the packet
				currentList.set(nodeIndex, new Predicate(inputPredicate));
				recursiveGenerateMaximalFlowsForwardUpdate(nodeIndex+1, sr, path, inputPredicate, currentFlowPath, currentList, somethingChanged);
			}
		}
		else {
			//node is a simple forwarder, just forward the predicate
			currentList.set(nodeIndex, new Predicate(inputPredicate));
			recursiveGenerateMaximalFlowsForwardUpdate(nodeIndex+1, sr, path, inputPredicate, currentFlowPath, currentList, somethingChanged);
		}
	}

	private void recursiveGenerateMaximalFlowsBackwardUpdate(int nodeIndex, SecurityRequirement sr, List<AllocationNodeMF> path, Predicate inputPredicate,
			FlowPathMF currentFlowPath, List<Predicate> currentList, boolean somethingChanged) {
		
		if(nodeIndex < 0)
			return;
		
		AllocationNodeMF node = path.get(nodeIndex);
		
		if(nodeIndex == 0) {
			//We are in the last first node of the path
			Predicate newPredicate = new Predicate(inputPredicate);
			currentList.set(nodeIndex, newPredicate);
			if(somethingChanged) {
				//start new forward update
				recursiveGenerateMaximalFlowsForwardUpdate(1, sr, path, inputPredicate, currentFlowPath, currentList, false);
			} else {
				currentFlowPath.addMaximalFlow(maximalFlowId, currentList);
				maximalFlowId++;
			}

			return;
		}
		
		if(natD1map.containsKey(node.getIpAddress())) {
			//Node is a NAT
			//check if input Predicate has sourceIP == to nat IP
			List<IPAddress> natIPAddressList = new ArrayList<>();
			natIPAddressList.add(new IPAddress(node.getIpAddress(), false));
			if(aputilsMF.APCompareIPAddressList(inputPredicate.getIPSrcList(), natIPAddressList)) {
				//the predicate has already been shadowed in previous traversals, so simply change destination and forward
				Predicate newPredicate = new Predicate(currentList.get(nodeIndex));
				newPredicate.setIPDstList(inputPredicate.getIPDstList());
				newPredicate.setpDstList(inputPredicate.getpDstList());
				newPredicate.setProtoTypeList(inputPredicate.getProtoTypeList());
				currentList.set(nodeIndex, newPredicate);
				recursiveGenerateMaximalFlowsBackwardUpdate(nodeIndex-1, sr, path, newPredicate, currentFlowPath, currentList, somethingChanged);
				return;
			}
			
			//check if it is a reconverted predicate. In that case forward the input packet saved in this node changing only source
			boolean isReconverted = false;
			for(Predicate reconvertedPredicate: natReconvertedMap.get(node.getIpAddress())) {
				if(aputilsMF.computeIntersection(inputPredicate, reconvertedPredicate) != null) {
					if(aputilsMF.APCompareIPAddressList(currentList.get(nodeIndex).getIPDstList(), natIPAddressList)) {
						isReconverted = true;
						break;
					}
				}
			}
			
			if(isReconverted) {
				Predicate newPredicate = new Predicate(currentList.get(nodeIndex));
				newPredicate.setIPSrcList(inputPredicate.getIPSrcList());
				newPredicate.setpDstList(inputPredicate.getpDstList());
				newPredicate.setProtoTypeList(inputPredicate.getProtoTypeList());
				currentList.set(nodeIndex, newPredicate);
				recursiveGenerateMaximalFlowsBackwardUpdate(nodeIndex-1, sr, path, newPredicate, currentFlowPath, currentList, somethingChanged);
				return;
			}
			
			//Compute intersection with D1
			for(Predicate D1Predicate: natD1map.get(node.getIpAddress())) {
				Predicate intersectingD1Predicate = aputilsMF.computeIntersection(D1Predicate, inputPredicate);
				if(intersectingD1Predicate != null) {
					//Do shadowing and generate a new flow
					List<Predicate> newCurrentList = aputilsMF.deepCopy(currentList);
					//change this node new input
					newCurrentList.set(nodeIndex, intersectingD1Predicate);
					//Generate new recursion with shadowed predicate as next input predicate (to subsequent node)
					Predicate shadowedPredicate = new Predicate(intersectingD1Predicate);
					List<IPAddress> srcList = new ArrayList<>();
					srcList.add(new IPAddress(node.getIpAddress(), false));
					shadowedPredicate.setIPSrcList(srcList);
					recursiveGenerateMaximalFlowsBackwardUpdate(nodeIndex-1, sr, path, shadowedPredicate, currentFlowPath, newCurrentList, true);
				}
			}
			//Compute intersection with D2
			Predicate intersectingD2Predicate = aputilsMF.computeIntersection(natD2map.get(node.getIpAddress()), inputPredicate);
			if(intersectingD2Predicate != null) {
				//Do reconversion and generate new flows
				for(String natSrc: node.getNode().getConfiguration().getNat().getSource()) {
					List<Predicate> newCurrentList = aputilsMF.deepCopy(currentList);
					//change this node new input
					newCurrentList.set(nodeIndex, intersectingD2Predicate);
					//Generate new recursion with reconverted predicate as next input predicate (to subsequent node)
					Predicate reconvertedPredicate = new Predicate(intersectingD2Predicate);
					IPAddress natSrcAddress = new IPAddress(natSrc, false);
					List<IPAddress> dstList = new ArrayList<>();
					dstList.add(natSrcAddress);
					reconvertedPredicate.setIPDstList(dstList);
					recursiveGenerateMaximalFlowsBackwardUpdate(nodeIndex-1, sr, path, reconvertedPredicate, currentFlowPath, newCurrentList, true);
				}
			}
			//Compute intersection with D31
			for(Predicate D31Predicate: natD31map.get(node.getIpAddress())) {
				Predicate intersectingD31Predicate = aputilsMF.computeIntersection(D31Predicate, inputPredicate);
				if(intersectingD31Predicate != null) {
					//change this node with new input
					List<Predicate> newCurrentList = aputilsMF.deepCopy(currentList);
					newCurrentList.set(nodeIndex, intersectingD31Predicate);
					//continue recursion without transformation
					recursiveGenerateMaximalFlowsBackwardUpdate(nodeIndex-1, sr, path, intersectingD31Predicate, currentFlowPath, newCurrentList, somethingChanged);
				}
			}
			//Compute intersection with D32
			Predicate intersectingD32Predicate = aputilsMF.computeIntersection(natD32map.get(node.getIpAddress()), inputPredicate);
			if(intersectingD32Predicate != null) {
				//change this node with new input
				List<Predicate> newCurrentList = aputilsMF.deepCopy(currentList);
				newCurrentList.set(nodeIndex, intersectingD32Predicate);
				//continue recursion without transformation
				recursiveGenerateMaximalFlowsBackwardUpdate(nodeIndex-1, sr, path, intersectingD32Predicate, currentFlowPath, newCurrentList, somethingChanged);
			}
		}
		else if(allowedFirewallPredicates.containsKey(node.getIpAddress())) {
			//we are in a firewall
			//Check intersection with allowed and denied list
			List<Predicate> trasformedPredicates = new ArrayList<>();
			for(Predicate allowedPredicate: allowedFirewallPredicates.get(node.getIpAddress())) {
				Predicate intersectionAllowed = aputilsMF.computeIntersection(allowedPredicate, inputPredicate);
				if(intersectionAllowed != null && !aputilsMF.APCompare(intersectionAllowed, inputPredicate)) 
					trasformedPredicates.add(intersectionAllowed);
			}
			for(Predicate deniedPredicate: deniedFirewallPredicates.get(node.getIpAddress())) {
				Predicate intersectionDenied = aputilsMF.computeIntersection(deniedPredicate, inputPredicate);
				if(intersectionDenied != null && !aputilsMF.APCompare(intersectionDenied, inputPredicate)) 
					trasformedPredicates.add(intersectionDenied);
			}
			
			//Generate the new flows
			if(trasformedPredicates.size() > 0) {
				for(Predicate newPredicate:trasformedPredicates) {
					List<Predicate> newCurrentList = aputilsMF.deepCopy(currentList);
					newCurrentList.set(nodeIndex, newPredicate);
					//continue recursion without transformation
					recursiveGenerateMaximalFlowsBackwardUpdate(nodeIndex-1, sr, path, newPredicate, currentFlowPath, newCurrentList, somethingChanged);
				}
			} else {
				//simply forward the packet
				Predicate newPredicate = new Predicate(inputPredicate);
				currentList.set(nodeIndex, newPredicate);
				recursiveGenerateMaximalFlowsBackwardUpdate(nodeIndex-1, sr, path, newPredicate, currentFlowPath, currentList, somethingChanged);
			}
		}
		else {
			//Just forward the predicate
			Predicate newPredicate = new Predicate(inputPredicate);
			currentList.set(nodeIndex, newPredicate);
			recursiveGenerateMaximalFlowsBackwardUpdate(nodeIndex-1, sr, path, newPredicate, currentFlowPath, currentList, somethingChanged);
		}
	}

}
