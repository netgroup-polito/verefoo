package it.polito.verefoo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.microsoft.z3.Context;

import it.polito.verefoo.allocation.AllocationManager;
import it.polito.verefoo.allocation.AllocationNode;
import it.polito.verefoo.extra.BadGraphError;
import it.polito.verefoo.extra.WildcardManager;
import it.polito.verefoo.graph.FlowPath;
import it.polito.verefoo.graph.SecurityRequirement;
import it.polito.verefoo.graph.SimplePredicate;
import it.polito.verefoo.graph.Traffic;
import it.polito.verefoo.graph.Flow;
import it.polito.verefoo.jaxb.*;
import it.polito.verefoo.jaxb.NodeConstraints.NodeMetrics;
import it.polito.verefoo.jaxb.Path.PathNode;
import it.polito.verefoo.solver.*;
import it.polito.verefoo.solver.Checker.Prop;
import it.polito.verefoo.utils.APUtils;
import it.polito.verefoo.utils.VerificationResult;

/**
 * 
 * This is the main class that will interface with the Verefoo classes
 *
 */
public class VerefooProxy {
	private Context ctx;
	private NetContext nctx;
	private List<Property> properties;
	private List<Path> paths;
	private WildcardManager wildcardManager;
	private HashMap<String, AllocationNode> allocationNodes;
	private HashMap<Integer, Flow> trafficFlowsMap;
	private HashMap<Integer, SecurityRequirement> securityRequirements;
	public Checker check;
	private List<Node> nodes;
	private List<NodeMetrics> nodeMetrics;
	private AllocationManager allocationManager;
	private APUtils aputils;
	
	/* Atomic predicates */
	private HashMap<Integer, SimplePredicate> networkAtomicPredicates;
	HashMap<String, Node> transformersNode = new HashMap<>();
	HashMap<String, List<SimplePredicate>> transformersShadowing = new HashMap<>();
	HashMap<String, List<SimplePredicate>> transformersShadowed = new HashMap<>();
	HashMap<String, List<SimplePredicate>> transformersReconversion = new HashMap<>();
	HashMap<String, List<SimplePredicate>> transformersReconverted = new HashMap<>();
	HashMap<String, HashMap<Integer, List<Integer>>> transformationsMap = new HashMap<>();
	
	/**
	 * Public constructor for the Verefoo proxy service
	 * 
	 * @param graph              The graph that will be deployed on the network
	 * @param hosts              The list of hosts in the network
	 * @param conns              The connections between hosts
	 * @param paths              the list of paths that the packet flows needs to
	 *                           follow
	 * @param capacityDefinition The list of the capacity for each node that will be
	 *                           deployed
	 * @throws BadGraphError
	 */
	public VerefooProxy(Graph graph, Hosts hosts, Connections conns, Constraints constraints, List<Property> prop,
			List<Path> paths) throws BadGraphError {
		
		// Initialitation of the variables related to the nodes
		allocationNodes = new HashMap<>();
		nodes = graph.getNode();
		nodes.forEach(n -> allocationNodes.put(n.getName(), new AllocationNode(n)));
		wildcardManager = new WildcardManager(allocationNodes);
		
		// Initialitation of the variables related to the requirements
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
		aputils = new APUtils();
				
		//Creation of the NetContext (z3 variables)
		nctx = nctxGenerate(ctx, nodes, prop, allocationNodes);
		nctx.setWildcardManager(wildcardManager);
		
		/*
		 * Main sequence of methods in VerefooProxy:
		 * 1) given every requirement, all the possible paths of the related flows are computed;
		 * 2) the existing functions are istanciated
		 * 3) the functions to be allocated are associated to Allocation Places
		 * 4) the possible traffic in input to each node is computed
		 * 5) soft and hard constraints are defined for each function
		 * 6) the hard constraints for the requirements are defined
		 */
		
		/* Atomic predicates */
		trafficFlowsMap = generateFlowPaths();
		networkAtomicPredicates = generateAtomicPredicates();
		
		//DEBUG: print resulting ap
		System.out.println("ATOMIC PREDICATES REQUIREMENTS");
		for(HashMap.Entry<Integer, SimplePredicate> ap: networkAtomicPredicates.entrySet()) {
			System.out.print("Simple Predicate " + ap.getKey());
			System.out.println(ap.getValue().toString());
		}
		//END DEBUG
		
		fillTransformationsMap();
		
		//DEBUG: print transformation map
		System.out.println("TRANSFORMATION MAP");
		for(Node node: transformersNode.values()) {
			if(node.getFunctionalType() == FunctionalTypes.NAT) {
				for(HashMap.Entry<Integer, List<Integer>> entry: transformationsMap.get(node.getName()).entrySet()) {
					System.out.print(entry.getKey() + " -> ");
					for(Integer integ: entry.getValue())
						System.out.print(integ + " ");
					System.out.println();
				}
			}
		}
		//END DEBUG
		
		allocationManager = new AllocationManager(ctx, nctx, allocationNodes, nodeMetrics, prop, wildcardManager);
		allocationManager.instantiateFunctions();
		allocateFunctions();
		distributeTrafficFlows();
		allocationManager.configureFunctions();
		
		check = new Checker(ctx, nctx, allocationNodes);
		formalizeRequirements();
		
	}

	HashMap<Integer, SimplePredicate> generateAtomicPredicates(){
		HashMap<Integer, SimplePredicate> retList = new HashMap<>();
		List<SimplePredicate> predicates = new ArrayList<>();
		List<SimplePredicate> atomicPredicates = new ArrayList<>();
		List<String> srcList = new ArrayList<>();
		List<String> dstList = new ArrayList<>();
		List<String> srcPList = new ArrayList<>();
		List<String> dstPList = new ArrayList<>();
		
		//Generate predicates representing source and predicates representing destination of each requirement
		for(SecurityRequirement sr : securityRequirements.values()) {
			Property property = sr.getOriginalProperty();
			String IPSrc = property.getSrc();
			String IPDst = property.getDst();
			String pSrc = property.getSrcPort() != null &&  !property.getSrcPort().equals("null") ? property.getSrcPort() : "*";
			String pDst = property.getDstPort() != null &&  !property.getDstPort().equals("null") ? property.getDstPort() : "*";
			
			if(!srcList.contains(IPSrc) || !srcPList.contains(pSrc)) {
				if(!srcList.contains(IPSrc))
					srcList.add(IPSrc);
				else IPSrc = "*";
				if(!srcPList.contains(pSrc)) 
					srcPList.add(pSrc);
				else pSrc = "*";
				
				SimplePredicate srcPredicate = new SimplePredicate(aputils, IPSrc, false, "*", false, pSrc, false, "*", false);
				predicates.add(srcPredicate);
			}
			
			if(!dstList.contains(IPDst) || !dstPList.contains(pDst)) {
				if(!dstList.contains(IPDst)) dstList.add(IPDst);
				else IPDst = "*";
				if(!dstPList.contains(pDst)) dstPList.add(pDst);
				else pDst = "*";
				
				SimplePredicate dstPredicate = new SimplePredicate(aputils, "*", false, IPDst, false, "*", false, pDst, false);
				predicates.add(dstPredicate);
			}
		}
		
		//Generate predicates representing input packet class for each transformers
		for(Node node: transformersNode.values()) {
			if(node.getFunctionalType() == FunctionalTypes.NAT) {
				List<SimplePredicate> shadowingPredicates =  new ArrayList<>();
				List<SimplePredicate> reconversionPredicates =  new ArrayList<>();
				List<SimplePredicate> shadowedPredicates =  new ArrayList<>();
				List<SimplePredicate> reconvertedPredicates =  new ArrayList<>();
				//Compute list of shadowed and not shadowed addresses
				List<String> shadowedAddressesList = new ArrayList<>();
				for(String shadowedAddress: node.getConfiguration().getNat().getSource()) {
					if(srcList.contains(shadowedAddress) || dstList.contains(shadowedAddress))
						shadowedAddressesList.add(shadowedAddress);
				}
				List<String> notShadowedAddressesList = new ArrayList<>();
				for(String address: srcList) {
					if(!node.getConfiguration().getNat().getSource().contains(address) && !address.equals("*") 
							&& !notShadowedAddressesList.contains(address))
						notShadowedAddressesList.add(address);
				}
				for(String address: dstList) {
					if(!node.getConfiguration().getNat().getSource().contains(address) && !address.equals("*")
							&& !notShadowedAddressesList.contains(address))
						notShadowedAddressesList.add(address);
				}
				//Generate predicates
				//Shadowing
				for(String notShadowed: notShadowedAddressesList) {
					for(String shadowed: shadowedAddressesList) { 
						SimplePredicate p = new SimplePredicate(aputils, shadowed, false, notShadowed, false, "*", false, "*", false);
						shadowingPredicates.add(p);
						//check if it needs also to be inserted into predicates
						if(!srcList.contains(shadowed) && !dstList.contains(notShadowed)) {
							predicates.add(p);
							srcList.add(shadowed); dstList.add(notShadowed);
						} else if(!srcList.contains(shadowed)) {
							SimplePredicate p1 = new SimplePredicate(aputils, shadowed, false, "*", false, "*", false, "*", false);
							predicates.add(p1);
							srcList.add(shadowed); 
						} else if(!dstList.contains(notShadowed)) {
							SimplePredicate p1 = new SimplePredicate(aputils, "*", false, notShadowed, false, "*", false, "*", false);
							predicates.add(p1);
							dstList.add(notShadowed); 
						}
					}
					shadowedPredicates.add(new SimplePredicate(aputils, node.getName(), false, notShadowed, false, "*", false, "*", false));
				}
				//Reconversion
				for(String notShadowedAddress: notShadowedAddressesList) {
					SimplePredicate p = new SimplePredicate(aputils, notShadowedAddress, false, node.getName(), false, "*", false, "*", false);
					reconversionPredicates.add(p);
					for(String shadowedAddress: shadowedAddressesList)
						reconvertedPredicates.add(new SimplePredicate(aputils, notShadowedAddress, false, shadowedAddress, false, "*", false, "*", false));
					//check if it needs also to be inserted into predicates
					if(!dstList.contains(node.getName())) {
						SimplePredicate p2 = new SimplePredicate(aputils, "*", false, node.getName(), false, "*", false, "*", false);
						predicates.add(p2);
						dstList.add(node.getName());
					}
					if(!srcList.contains(notShadowedAddress)) {
						SimplePredicate p3 = new SimplePredicate(aputils, notShadowedAddress, false, "*", false, "*", false, "*", false);
						predicates.add(p3);
						srcList.add(notShadowedAddress);
					}
				}
				//Add predicate after applying trasformation: this is enough, all the others have already been added
				predicates.add(new SimplePredicate(aputils, node.getName(), false, "*", false, "*", false, "*", false));
				transformersShadowing.put(node.getName(), shadowingPredicates);
				transformersReconversion.put(node.getName(), reconversionPredicates);
				transformersShadowed.put(node.getName(), shadowedPredicates);
				transformersReconverted.put(node.getName(), reconvertedPredicates);
				
			} else if(node.getFunctionalType() == FunctionalTypes.FIREWALL) {
				List<SimplePredicate> allowedList = new ArrayList<>();
				List<SimplePredicate> deniedList = new ArrayList<>();
				for(int index = 0; index < node.getConfiguration().getFirewall().getElements().size() + 1; index++) {
					Elements rule;
					if(index < node.getConfiguration().getFirewall().getElements().size())
						rule = node.getConfiguration().getFirewall().getElements().get(index);
					else {
						rule = new Elements();
						rule.setAction(node.getConfiguration().getFirewall().getDefaultAction());
						rule.setSource("*"); rule.setDestination("*"); rule.setSrcPort("*"); rule.setDstPort("*");
					}
					
					if(rule.getAction().equals(ActionTypes.DENY)) {
						//denied <-- denied V rule-i
						deniedList.add(new SimplePredicate(aputils, rule.getSource(), false, rule.getDestination(), false, rule.getSrcPort(), false, rule.getDstPort(), false));
					} else if(rule.getAction().equals(ActionTypes.ALLOW)){
						//allowed <-- allowed V (rule-i AND !denied)
						List<SimplePredicate> resToAdd = new ArrayList<>();
						resToAdd.add(new SimplePredicate(aputils, rule.getSource(), false, rule.getDestination(), false, rule.getSrcPort(), false, rule.getDstPort(), false));
						List<SimplePredicate> tmpList = new ArrayList<>();
						for(SimplePredicate denied: deniedList) {
							List<SimplePredicate> negDeniedList = aputils.neg(denied);
							for(SimplePredicate allow: resToAdd) {
								for(SimplePredicate negDenied: negDeniedList) {
									SimplePredicate res = aputils.computeIntersection(allow, negDenied);
									if(res != null)
										tmpList.add(res);
								}
							}
							if(tmpList.isEmpty()) { //No intersections foud, so rule is useless
								resToAdd.clear();
								break;
							} else {
								resToAdd = new ArrayList<>(tmpList);
								tmpList = new ArrayList<>();
							}
						}
						if(!resToAdd.isEmpty()) allowedList.addAll(resToAdd);
					}
				}
				
				//DEBUG: print allowed rules
				System.out.println("ALLOWED RULES");
				for(SimplePredicate allowed: allowedList) {
					System.out.println(allowed.toString());
				}
			}
		}

		atomicPredicates = aputils.computeAtomicPredicates(atomicPredicates, predicates);
		int id = 0;
		for(SimplePredicate ap: atomicPredicates) {
			retList.put(id, ap);
			id++;
		}

		//DEBUG: print atomic predicates
		System.out.println("INTERESTING PREDICATES (with optimization)");
		for(SimplePredicate ap: predicates) {
			System.out.println(ap.toString());
		}
		//END DEBUG

		return retList; 
	}

	void fillTransformationsMap() {
		for(Node node: transformersNode.values()) {
			if(node.getFunctionalType() == FunctionalTypes.NAT) {
				List<Integer> shadowingInt = new ArrayList<>();
				List<Integer> reconversionInt = new ArrayList<>();
				List<Integer> reconvertedInt = new ArrayList<>();
				List<Integer> shadowedInt = new ArrayList<>();
				HashMap<Integer, List<Integer>> mapValues = new HashMap<>();
				int toFind = transformersShadowing.get(node.getName()).size() + transformersReconversion.get(node.getName()).size() 
						+ transformersShadowed.get(node.getName()).size() + transformersReconverted.get(node.getName()).size();
				int index;
				boolean found;
				//ottimizzazioni: remove from list (da testare però), continue dopo il break;
				for(HashMap.Entry<Integer, SimplePredicate> atomicPredicate: networkAtomicPredicates.entrySet()) {
					found = false;
					//scan pre shadowing
					index = 0;
					for(SimplePredicate shadowingPredicate: transformersShadowing.get(node.getName())) {
						if(aputils.APCompare(atomicPredicate.getValue(), shadowingPredicate)) {
							shadowingInt.add(atomicPredicate.getKey());
							transformersShadowing.get(node.getName()).remove(index);
							found = true;
							toFind--;
							break; //plus continue to next atomic predicate
						} 
						index++;
					}
					if(found) continue;
					if(toFind == 0) break;
					//scan pre reconversion
					index = 0;
					for(SimplePredicate reconversionPredicate: transformersReconversion.get(node.getName())) {
						if(aputils.APCompare(atomicPredicate.getValue(), reconversionPredicate)) {
							reconversionInt.add(atomicPredicate.getKey());
							transformersReconversion.get(node.getName()).remove(index);
							found = true;
							toFind--;
							break; //plus continue to next atomic predicate
						}
						index++;
					}
					if(found) continue;
					if(toFind == 0) break;
					//scan post shadowing
					index = 0;
					for(SimplePredicate shadowedPredicate: transformersShadowed.get(node.getName())) {
						if(aputils.APCompare(atomicPredicate.getValue(), shadowedPredicate)) {
							shadowedInt.add(atomicPredicate.getKey());
							transformersShadowed.get(node.getName()).remove(index);
							found = true;
							toFind--;
							break; //plus continue to next atomic predicate
						}
						index++;
					}
					if(found) continue;
					if(toFind == 0) break;
					//scan post reconversion
					index = 0;
					for(SimplePredicate reconvertedPredicate: transformersReconverted.get(node.getName())) {
						if(aputils.APCompare(atomicPredicate.getValue(), reconvertedPredicate)) {
							reconvertedInt.add(atomicPredicate.getKey());
							transformersReconverted.get(node.getName()).remove(index);
							toFind--;
							break; //plus continue to next atomic predicate
						}
						index++;
					}
					if(toFind == 0) break;
				}

				//Now fill the map
				for(Integer shadowing: shadowingInt) 
					mapValues.put(shadowing, shadowedInt);
				for(Integer reconversion: reconversionInt)
					mapValues.put(reconversion, reconvertedInt);
				transformationsMap.put(node.getName(), mapValues);
			}
		}
	}
	
	
	/**
	 * This method allocates the functions on allocation nodes that are empty.
	 * At the moment only packet-filtering capability is allocated, in the future the decision will depend on the type of requirement.
	 */
	private void allocateFunctions() {
		for(Flow sr : trafficFlowsMap.values()) {
			List<AllocationNode> nodes = sr.getPath().getNodes();
			int lengthList = nodes.size();
			AllocationNode source = nodes.get(0);
			AllocationNode last = nodes.get(lengthList-1);
			for(int i = 1; i < lengthList-1; i++) {
				allocationManager.chooseFunctions(nodes.get(i), source, last);
			}
		}
		
	}


	/**
	 * This method creates the hard constraints in the z3 model for reachability and isolation requirements.
	 */
	private void formalizeRequirements() {
		
		for(SecurityRequirement sr : securityRequirements.values()) {
			switch (sr.getOriginalProperty().getName()) {
			case ISOLATION_PROPERTY:
				check.createRequirementConstraints(sr, Prop.ISOLATION);
				break;
			case REACHABILITY_PROPERTY:
				check.createRequirementConstraints(sr, Prop.REACHABILITY);
				break;
			default:
				throw new BadGraphError("Error in the property definition", EType.INVALID_PROPERTY_DEFINITION);
			}
				
		}
		
	}


	/**
	 * This method distributes into each Allocation Node the traffic flows and computes the characteristics of each ingress traffic
	 */
	private void distributeTrafficFlows() {
		for(Flow flow : trafficFlowsMap.values()) {
			
			boolean forwardUpdate = false;
			boolean backwardUpdate = false;
			
			List<AllocationNode> nodesList = flow.getPath().getNodes();
			
			for(AllocationNode node : nodesList) {
				node.addFlow(flow);
				if((node.getTypeNF().equals(FunctionalTypes.NAT) && node.getNode().getConfiguration().getNat().getSource().contains(flow.getOriginalTraffic().getIPSrc())) || (node.getTypeNF().equals(FunctionalTypes.LOADBALANCER) && node.getNode().getConfiguration().getLoadbalancer().getPool().contains(flow.getOriginalTraffic().getIPSrc()))){
					forwardUpdate = true;
				}
				else if((node.getTypeNF().equals(FunctionalTypes.NAT) && node.getNode().getConfiguration().getNat().getSource().contains(flow.getOriginalTraffic().getIPDst()) ) 
						|| (node.getTypeNF().equals(FunctionalTypes.LOADBALANCER) && node.getNode().getConfiguration().getLoadbalancer().getPool().contains(flow.getOriginalTraffic().getIPDst()))) {
					backwardUpdate = true;
				}
			}
			
			if(forwardUpdate || backwardUpdate) {
				for(int i = 0; i < nodesList.size(); i++) {
					Traffic t = Traffic.copyTraffic(flow.getOriginalTraffic());
					AllocationNode current = nodesList.get(i);
					flow.addModifiedTraffic(current.getNode().getName(), t);
				}
				
				if(forwardUpdate) {
					Traffic t = Traffic.copyTraffic(flow.getOriginalTraffic());
					int listLength = nodesList.size();
					String currentSrc = t.getIPSrc();
					//loop for modifications of IP addresses from source to destination 
					for(int i = 0; i < listLength; i++) {
						AllocationNode currentNode = nodesList.get(i);
						Traffic crossed = flow.getCrossedTraffic(currentNode.getNode().getName());
						crossed.setIPSrc(currentSrc);
						if((currentNode.getTypeNF().equals(FunctionalTypes.NAT) && currentNode.getNode().getConfiguration().getNat().getSource().contains(crossed.getIPSrc())) ||(currentNode.getTypeNF().equals(FunctionalTypes.LOADBALANCER) && currentNode.getNode().getConfiguration().getLoadbalancer().getPool().contains(crossed.getIPSrc())) ){
							currentSrc = currentNode.getNode().getName();
						}
					}
				}
				
				
				
				if(backwardUpdate) {
					Traffic t = Traffic.copyTraffic(flow.getOriginalTraffic());
					int listLength = nodesList.size();
					String currentDst = t.getIPDst();
					//loop for modifications of IP addresses from destination to source
					
					for(int i = listLength-1; i >= 0; i--) {
						AllocationNode currentNode = nodesList.get(i);
						Traffic crossed = flow.getCrossedTraffic(currentNode.getNode().getName());
						if((currentNode.getTypeNF().equals(FunctionalTypes.NAT) && currentNode.getNode().getConfiguration().getNat().getSource().contains(crossed.getIPDst())) ||(currentNode.getTypeNF().equals(FunctionalTypes.LOADBALANCER) && currentNode.getNode().getConfiguration().getLoadbalancer().getPool().contains(crossed.getIPDst())) ){
							currentDst = currentNode.getNode().getName();
						}
						crossed.setIPDst(currentDst);
					}
				}
			}
		}
	}


	/**
	 * For each requirement, this method identifies all the possible the paths of nodes that must be crossed by the traffic flows that are related to the requirement.
	 * @return the map of all the traffic flows
	 */
	private HashMap<Integer, Flow> generateFlowPaths(){
		
		HashMap<Integer, Flow> flowsMap = new HashMap<>();
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
			List<List<AllocationNode>> allPaths = new ArrayList<>();
			List<AllocationNode> localPath = new ArrayList<>();
			//if no forwarding path has been defined by the user, the framework searches for ALL the possible existing path.
			//for each path, a corresponding flow is defined. The traffic characterization will be made in a different moment.
			if(definedPath == null) {
				Set<String> visited = new HashSet<>();
				AllocationNode source = allocationNodes.get(property.getSrc());
				AllocationNode destination = allocationNodes.get(property.getDst());
				recursivePathGeneration(allPaths, localPath, source, destination, source, visited, 0);
				found = allPaths.isEmpty()? false : true;
				visited.clear();
			}else {
				//otherwise, the nodes of the path are simply put in the list
				found = true;
				for(PathNode pn : definedPath.getPathNode()) {
					AllocationNode an = allocationNodes.get(pn.getName());
					localPath.add(an);
				}
				allPaths.add(localPath);
			}
			
			if(found) {
				for(List<AllocationNode> singlePath : allPaths) {
					FlowPath fp = new FlowPath(singlePath);
					Flow flow = new Flow(sr, fp, id);
					flowsMap.put(id, flow);
					sr.getFlowsMap().put(id, flow);
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
	 * This method is recursively called to generate the path of nodes for each requirement.
	 * @param allPaths it is the list of all the paths that have been computed for the requirement
	 * @param currentPath it is the current path that the method is building 
	 * @param source it is the source of the path
	 * @param destination it is the destination of the path
	 * @param current it is the current node in the recursive visit
	 * @param visited it is a list of nodes that have been already visited
	 * @param level it is the recursion level of the visit
	 * @return true if a path has been identified, false otherwise
	 */
	private void recursivePathGeneration(List<List<AllocationNode>> allPaths, List<AllocationNode> currentPath, AllocationNode source,
			AllocationNode destination, AllocationNode current, Set<String> visited, int level) {
		
		currentPath.add(level, current);
		visited.add(current.getNode().getName());
		List<Neighbour> listNeighbours = current.getNode().getNeighbour();
		if(destination.getNode().getName().equals(current.getNode().getName())) {
			//I save the completed path and search for others
			List<AllocationNode> pathToStore = new ArrayList<>();
			for(int i = 0; i < currentPath.size(); i++) {
				if((currentPath.get(i).getNode().getFunctionalType() == FunctionalTypes.NAT 
						|| currentPath.get(i).getNode().getFunctionalType() == FunctionalTypes.FIREWALL)
						&& !transformersNode.containsKey(currentPath.get(i).getNode().getName()))
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
				return;
			}
		}
		
		

		for(Neighbour n : listNeighbours) {
			if(!visited.contains(n.getName())) {
				AllocationNode neighbourNode = allocationNodes.get(n.getName());
				level++;
				recursivePathGeneration(allPaths, currentPath, source, destination, neighbourNode, visited, level);
				level--;
			}
					
		}
		
		visited.remove(current.getNode().getName());
		currentPath.remove(level);
		return;
	}


	
	/**
	 * This method generates the NetContext object for the initialization of z3 model.
	 * @param ctx2 it is the z3 Context object
	 * @param nodes2 is is the list of nodes of the Allocation Graph
	 * @param prop it is the list of properties to be satisfied
	 * @param allocationNodes2 it is the list of allocation nodes
	 * @return the NetContext object
	 */
	private NetContext nctxGenerate(Context ctx2, List<Node> nodes2, List<Property> prop,
			HashMap<String, AllocationNode> allocationNodes2) {
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
		return new NetContext(ctx, allocationNodes, nodesname, nodesip, src_portRange, dst_portRange);
	}

	/**
	 * Checks if the service graph satisfies all the imposed conditions
	 * 
	 * @return
	 */
	public VerificationResult checkNFFGProperty() {
		VerificationResult ret = this.check.propertyCheck();
		ret.time = this.check.getTimeChecker();
		return ret;
	}

	/**
	 * Get Net Context
	 * 
	 * @return the net context
	 */
	public NetContext getNctx() {
		return nctx;
	}


	/**
	 * @return all the allocation nodes
	 */
	public Map<String, AllocationNode> getAllocationNodes() {
		return allocationNodes;
	}

	
	/**
	 * @return all the requirements
	 */
	public Map<Integer, Flow> getTrafficFlowsMap(){
		return trafficFlowsMap;
	}
}
