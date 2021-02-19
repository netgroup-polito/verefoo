package it.polito.verefoo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;

import it.polito.verefoo.allocation.AllocationManager;
import it.polito.verefoo.allocation.AllocationNode;
import it.polito.verefoo.extra.BadGraphError;
import it.polito.verefoo.extra.WildcardManager;
import it.polito.verefoo.functions.Forwarder;
import it.polito.verefoo.graph.FlowPath;
import it.polito.verefoo.graph.Predicate;
import it.polito.verefoo.graph.SecurityRequirement;
import it.polito.verefoo.graph.SimplePredicate;
import it.polito.verefoo.graph.Traffic;
import it.polito.verefoo.graph.Flow;
import it.polito.verefoo.jaxb.*;
import it.polito.verefoo.jaxb.NodeConstraints.NodeMetrics;
import it.polito.verefoo.jaxb.Path.PathNode;
import it.polito.verefoo.solver.*;
import it.polito.verefoo.solver.Checker.Prop;
import it.polito.verefoo.utils.VerificationResult;
import scala.collection.concurrent.FailedNode;

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
	
	/* Atomic predicates */
	private HashMap<Integer, SimplePredicate> networkAtomicPredicates;
	
	
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
		networkAtomicPredicates = generateAtomicPredicates();
		
		trafficFlowsMap = generateFlowPaths();
		allocationManager = new AllocationManager(ctx, nctx, allocationNodes, nodeMetrics, prop, wildcardManager);
		allocationManager.instantiateFunctions();
		allocateFunctions();
		
		distributeTrafficFlows();
		allocationManager.configureFunctions();
		
		/* DEBUG: for each requirement, show all possible traffic flows */
//		for(Map.Entry<Integer, Flow> flows: trafficFlowsMap.entrySet()) {
//			System.out.println("Flow id: " + flows.getKey());
//			SecurityRequirement sr = flows.getValue().getRequirement();
//			System.out.println("Requirement " +sr.getIdRequirement() + " : " + sr.getOriginalProperty().getName()+ 
//					" " + sr.getOriginalProperty().getSrc() + " " + sr.getOriginalProperty().getSrcPort()
//					+ " -> "+ sr.getOriginalProperty().getDst() + " "+ sr.getOriginalProperty().getDstPort() +" " + sr.getOriginalProperty().getLv4Proto());
//			System.out.println("Print list of nodes");
//			for(AllocationNode node: flows.getValue().getPath().getNodes()) {
//				Traffic crossedTraffic = flows.getValue().getCrossedTraffic(node.getNode().getName());
//				System.out.println("Node " +node.getNode().getName() + " " + crossedTraffic.getIPSrc() + "->" + crossedTraffic.getIPDst() );
//			}
//		}
		
		/* END DEBUG */
		
		
		check = new Checker(ctx, nctx, allocationNodes);
		formalizeRequirements();
		
	}
	
	
	
	HashMap<Integer, SimplePredicate> generateAtomicPredicates(){
		List<SimplePredicate> predicates = new ArrayList<>();
		List<SimplePredicate> atomicPredicates = new ArrayList<>();
		List<String> srcList = new ArrayList<>();
		List<String> dstList = new ArrayList<>();
		List<String> srcPList = new ArrayList<>();
		List<String> dstPList = new ArrayList<>();
		
		for(SecurityRequirement sr : securityRequirements.values()) {
			Property property = sr.getOriginalProperty();
			String IPSrc = property.getSrc();
			String IPDst = property.getDst();
			String pSrc = property.getSrcPort() != null ? property.getSrcPort() : "*";
			String pDst = property.getDstPort() != null ? property.getDstPort() : "*";
			
			//Generate predicates representing source and predicates representing destination of the requirement
			if(!srcList.contains(IPSrc)) {
				SimplePredicate srcPredicate = new SimplePredicate(IPSrc, false, "*", false, pSrc, false, "*", false);
				predicates.add(srcPredicate);
				srcList.add(IPSrc);
			}
			
			if(!dstList.contains(IPDst)) {
				SimplePredicate dstPredicate = new SimplePredicate("*", false, IPDst, false, "*", false, pDst, false);
				predicates.add(dstPredicate);
				dstList.add(IPDst);
			}
		}
		
		atomicPredicates = computeAtomicPredicates(atomicPredicates, predicates);
		
		//DEBUG: print atomic predicates
		System.out.println("ATOMIC PREDICATES REQUIREMENTS");
		for(SimplePredicate ap: atomicPredicates) {
			System.out.println(ap.toString());
		}
		//END DEBUG
		
		return null;
	}
	
	List<SimplePredicate> computeAtomicPredicates(List<SimplePredicate> atomicPredicates, List<SimplePredicate> predicates){
		List<SimplePredicate> newAtomicPredicates = new ArrayList<>();
		SimplePredicate first = null;
		List<SimplePredicate> firstNeg = null;
		int count = -1;
		
		for(SimplePredicate sp: predicates) {
			if(atomicPredicates.isEmpty() && count == -1) {
				first = sp;
				firstNeg = sp.neg();
				count = 1;
			}
			else if(count == 1) {
				SimplePredicate sp1 = computeIntersection(first, sp);
				if(sp1 != null) atomicPredicates.add(sp1);
				
				for(SimplePredicate s: firstNeg) {
					SimplePredicate sp2 = computeIntersection(s, sp);
					if(sp2 != null) atomicPredicates.add(sp2);
				}
				
				for(SimplePredicate s: sp.neg()) {
					SimplePredicate sp3 = computeIntersection(first,s);
					if(sp3 != null) atomicPredicates.add(sp3);
				}
				
				for(SimplePredicate s1: sp.neg()) {
					for(SimplePredicate s2: firstNeg) {
						SimplePredicate sp4 = computeIntersection(s1,s2);
						if(sp4 != null) atomicPredicates.add(sp4);
					}
				}
				
				count = -1;
			} else {
				for(SimplePredicate prevSp: atomicPredicates) {
					SimplePredicate res1 = computeIntersection(prevSp, sp);
					if(res1 != null) newAtomicPredicates.add(res1);
					
					for(SimplePredicate s: sp.neg()) {
						SimplePredicate res2 = computeIntersection(prevSp,s);
						if(res2 != null) newAtomicPredicates.add(res2);
					}
				}
				atomicPredicates = new ArrayList<>(newAtomicPredicates);
				newAtomicPredicates = new ArrayList<>();
			}
		}
		return atomicPredicates;
	}
	
	SimplePredicate computeIntersection(SimplePredicate p1, SimplePredicate p2){
		SimplePredicate retPredicate = new SimplePredicate();
		List<String> IPSrcList;
		boolean negIPSrc;
		List<String> IPDstList;
		boolean negIPDst;
		List<String> pSrcList;
		boolean negpSrc;
		List<String> pDstList;
		boolean negpDst;
		
		//Check IPSrc
		if(!p1.isNegIPSrc()) {
			if(!p2.isNegIPSrc()) {   //both not neg
				IPSrcList = intersection(p1.getIPSrcList(), p2.getIPSrcList());
				negIPSrc = false;
			}
			else { //p1 not neg, p2 neg
				if(p1.getIPSrcList().contains("*")) {
					IPSrcList = new ArrayList<>(p2.getIPSrcList());
					negIPSrc = true;
				} else {
					IPSrcList = notContained(p1.getIPSrcList(), p2.getIPSrcList());
					negIPSrc = false;
				}
			}
		} else {
			if(!p2.isNegIPSrc()) { //p1 neg, p2 not neg
				if(p2.getIPSrcList().contains("*")) {
					IPSrcList = new ArrayList<>(p1.getIPSrcList());
					negIPSrc = true;
				} else {
					IPSrcList = notContained(p2.getIPSrcList(), p1.getIPSrcList());
					negIPSrc = false;
				}
			}
			else { //both neg
				IPSrcList = union(p1.getIPSrcList(), p2.getIPSrcList());
				negIPSrc = true;
			}
		}
		if(IPSrcList.isEmpty())
			return null;		//no intersection exists

		//Check IPDst
		if(!p1.isNegIPDst()) {
			if(!p2.isNegIPDst()) {   //both not neg
				IPDstList = intersection(p1.getIPDstList(), p2.getIPDstList());
				negIPDst = false;
			}
			else { //p1 not neg, p2 neg
				if(p1.getIPDstList().contains("*")) {
					IPDstList = new ArrayList<>(p2.getIPDstList());
					negIPDst = true;
				} else {
					IPDstList = notContained(p1.getIPDstList(), p2.getIPDstList());
					negIPDst = false;
				}
			}
		} else {
			if(!p2.isNegIPDst()) { //p1 neg, p2 not neg
				if(p2.getIPDstList().contains("*")) {
					IPDstList = new ArrayList<>(p1.getIPDstList());
					negIPDst = true;
				} else {
					IPDstList = notContained(p2.getIPDstList(), p1.getIPDstList());
					negIPDst = false;
				}
			}
			else { //both neg
				IPDstList = union(p1.getIPDstList(), p2.getIPDstList());
				negIPDst = true;
			}
		}
		if(IPDstList.isEmpty())
			return null;		//no intersection exists

		//Check pSrc
		if(!p1.isNegPSrc()) {
			if(!p2.isNegPSrc()) {   //both not neg
				pSrcList = intersection(p1.getpSrcList(), p2.getpSrcList());
				negpSrc = false;
			}
			else { //p1 not neg, p2 neg
				if(p1.getpSrcList().contains("*")) {
					pSrcList = new ArrayList<>(p2.getpSrcList());
					negpSrc = true;
				} else {
					pSrcList = notContained(p1.getpSrcList(), p2.getpSrcList());
					negpSrc = false;
				}
			}
		} else {
			if(!p2.isNegPSrc()) { //p1 neg, p2 not neg
				if(p2.getpSrcList().contains("*")) {
					pSrcList = new ArrayList<>(p1.getpSrcList());
					negpSrc = true;
				} else {
					pSrcList = notContained(p2.getpSrcList(), p1.getpSrcList());
					negpSrc = false;
				}
			}
			else { //both neg
				pSrcList = union(p1.getpSrcList(), p2.getpSrcList());
				negpSrc = true;
			}
		}
		if(pSrcList.isEmpty())
			return null;		//no intersection exists

		//Check pDst
		if(!p1.isNegPDst()) {
			if(!p2.isNegPDst()) {   //both not neg
				pDstList = intersection(p1.getpDstList(), p2.getpDstList());
				negpDst = false;
			}
			else { //p1 not neg, p2 neg
				if(p1.getpDstList().contains("*")) {
					pDstList = new ArrayList<>(p2.getpDstList());
					negpDst = true;
				} else {
					pDstList = notContained(p1.getpDstList(), p2.getpDstList());
					negpDst = false;
				}
			}
		} else {
			if(!p2.isNegPDst()) { //p1 neg, p2 not neg
				if(p2.getpDstList().contains("*")) {
					pDstList = new ArrayList<>(p1.getpDstList());
					negpDst = true;
				} else {
					pDstList = notContained(p2.getpDstList(), p1.getpDstList());
					negpDst = false;
				}
			}
			else { //both neg
				pDstList = union(p1.getpDstList(), p2.getpDstList());
				negpDst = true;
			}
		}
		if(pDstList.isEmpty())
			return null;		//no intersection exists

		retPredicate.setIPSrcList(IPSrcList);
		retPredicate.setIPDstList(IPDstList);
		retPredicate.setpSrcList(pSrcList);
		retPredicate.setpDstList(pDstList);
		retPredicate.setNegIPSrc(negIPSrc);
		retPredicate.setNegIPDst(negIPDst);
		retPredicate.setNegPSrc(negpSrc);
		retPredicate.setNegPDst(negpDst);
		return retPredicate;
	}
	
	
	
	
	List<String> union(List<String> list1, List<String> list2){
		List<String> retList = new ArrayList<>();
		for(String str: list1) {
			if(!retList.contains(str))
				retList.add(str);
		}
		for(String str: list2) {
			if(!retList.contains(str))
				retList.add(str);
		}
		return retList;
	}
	
	List<String> intersection(List<String> list1, List<String> list2){
		List<String> retList;
		if(list1.contains("*"))
			retList = new ArrayList<>(list2);
		else if(list2.contains("*"))
			retList = new ArrayList<>(list1);
		else {
			retList = new ArrayList<>();
			for(String str: list1) {
				if(list2.contains(str))
					retList.add(str);
			}
		}
		return retList;
	}
	
	List<String> notContained(List<String> list1, List<String> list2){
		List<String> retList = new ArrayList<>();
		for(String str: list1) {
			if(!list2.contains(str))
				retList.add(str);
		}
		return retList;
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
