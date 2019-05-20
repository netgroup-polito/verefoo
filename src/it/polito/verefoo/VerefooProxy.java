package it.polito.verefoo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.DatatypeExpr;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Status;

import it.polito.verefoo.allocation.AllocationManager;
import it.polito.verefoo.allocation.AllocationNode;
import it.polito.verefoo.extra.BadGraphError;
import it.polito.verefoo.extra.WildcardManager;
import it.polito.verefoo.graph.Link;
import it.polito.verefoo.graph.LinkProvider;
import it.polito.verefoo.jaxb.*;
import it.polito.verefoo.jaxb.LinkConstraints.LinkMetrics;
import it.polito.verefoo.jaxb.NodeConstraints.NodeMetrics;
import it.polito.verigraph.extra.VerificationResult;
import it.polito.verigraph.functions.PacketFilter;
import it.polito.verigraph.solver.*;
import it.polito.verigraph.solver.Checker.Prop;

/**
 * 
 * This is the main class that will interface with the Verifoo classes
 *
 */
public class VerefooProxy {
	private Context ctx;
	private NetContext nctx;
	private List<Property> properties;
	private WildcardManager wildcardManager;
	private PacketFilterManager pfManager;
	private HashMap<String, AllocationNode> allocationNodes;
	public Checker check;
	private List<Node> nodes;
	private LinkProvider linkProvider;
	private List<Path> paths;
	int clientServerCombinations = 0;
	private List<NodeMetrics> nodeMetrics;
	private int nrOfConditions;
	private AllocationManager allocationManager;
	
	private Logger logger = LogManager.getLogger("mylog");
	/**
	 * Public constructor for the verifoo proxy service
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
		allocationNodes = new HashMap<>();
		HashMap<String, String> cfg = new HashMap<String, String>();
		cfg.put("model", "true");
		ctx = new Context(cfg);
		properties = prop;
		nodes = graph.getNode();
		nodes.forEach(n -> allocationNodes.put(n.getName(), new AllocationNode(n)));

		this.nodeMetrics = constraints.getNodeConstraints().getNodeMetrics();
		this.paths = paths;

		wildcardManager = new WildcardManager(allocationNodes);

		nctx = nctxGenerate(ctx, nodes, prop, allocationNodes);
		nctx.setWildcardManager(wildcardManager);

		pfManager = new PacketFilterManager(wildcardManager, prop);
		allocationManager = new AllocationManager(ctx, nctx, allocationNodes, nodeMetrics, prop, pfManager);

		allocationManager.instantiateFunctions();
		checkNffg();
		pfManager.minimizeRules();
		allocationManager.configureFunctions();
		check = new Checker(ctx, nctx, allocationNodes);
		setProperty(prop);
	}

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
	 * Calls the function to translate the node's neighbours into links and then it
	 * calls the function that creates the routing tables
	 * 
	 * @throws BadGraphError
	 */
	private void checkNffg() throws BadGraphError {
		try {
			linkProvider = new LinkProvider(nodes, paths, properties);
			for (Property property : properties) {
				String src = property.getSrc();
				String dst = property.getDst();
				AllocationNode srcNode = allocationNodes.get(src);
				AllocationNode dstNode = allocationNodes.get(dst);
				if (!linkProvider.existsPath(srcNode.getNode(), dstNode.getNode())) {
					logger.debug("No path found between " + src + " and " + dst);
					continue;
				}
					createRoutingConditions(srcNode, dstNode);
			}
		} catch (StackOverflowError e) {
			throw new BadGraphError("The graph of nodes is invalid", EType.INVALID_SERVICE_GRAPH);
		}
	}

	/**
	 * Explores the graph to find the path between client and server and then it
	 * builds the routing table based on those information (for an XML without
	 * physical topology)
	 * 
	 * @param client
	 * @param server
	 * @throws BadGraphError
	 */
	private void createRoutingConditions(AllocationNode srcNode, AllocationNode dstNode) throws BadGraphError {
		List<Link> nextLinks = linkProvider.getLinksFrom(srcNode.getNode(), 0);
		if (nextLinks.size() == 0) {
			logger.error("Route: From CLIENT " + srcNode.getNode().getName() + " to "
					+ nctx.addressMap.get(dstNode.getNode().getName()) + " -> Dead End");
			throw new BadGraphError("Nodes must be connected", EType.INVALID_SERVICE_GRAPH);
		}
		for (Link link : nextLinks) {
			AllocationNode next = allocationNodes.values().stream()
					.filter(n -> n.getNode().getName().equals(link.getDestNode())).findFirst().get();
			if (setNextHop(srcNode, srcNode, next, dstNode, 1, new HashMap<>())) {
				Map<AllocationNode, Set<AllocationNode>> firstHops = srcNode.getFirstHops();
				if (firstHops.containsKey(dstNode)) {
					firstHops.get(dstNode).add(next);
				} else {
					Set<AllocationNode> set = new HashSet<>();
					set.add(next);
					firstHops.put(dstNode, set);
				}

			}

		}
	}

	/**
	 * Explores recursively the graph in order to know the right sequence of nodes
	 * (for an XML without physical topology)
	 * 
	 * @param prec               the node that precedes source in this exploration
	 * @param source             the node from which is exploring the solutions
	 * @param server             the destination node
	 * @param nodeRecursionLevel the current level of the recursion
	 * @param visited            the list of the nodes already visited
	 * @return
	 * @throws BadGraphError
	 */
	private boolean setNextHop(AllocationNode origin, AllocationNode prec, AllocationNode source,
			AllocationNode finalDest, int nodeRecursionLevel, HashMap<AllocationNode, List<String>> visited)
			throws BadGraphError {
		if (source.getNode().getName().equals(finalDest.getNode().getName())) {
			Map<AllocationNode, Set<AllocationNode>> lastHops = finalDest.getLastHops();
			if (lastHops.containsKey(origin)) {
				lastHops.get(origin).add(prec);
			} else {
				Set<AllocationNode> set = new HashSet<>();
				set.add(prec);
				lastHops.put(origin, set);
			}
			return true;
		}
		List<String> nextDest = linkProvider.getLinksFrom(source.getNode(), nodeRecursionLevel).stream()
				.map(l -> l.getDestNode()).collect(Collectors.toList());
		if (nextDest.size() == 0) {
			return false;
		}

		if (!visited.containsKey(source)) {
			visited.put(source, new ArrayList<>());
		}

		boolean found = false;
		for (String dest : nextDest) {
			AllocationNode next = allocationNodes.values().stream().filter(n -> n.getNode().getName().equals(dest))
					.findFirst().orElse(null);
			assert (next != null);
			if (visited.get(source).contains(dest) || dest.equals(prec.getNode().getName())) {
				continue;
			}
			visited.get(source).add(dest);
			if (setNextHop(origin, source, next, finalDest, nodeRecursionLevel + 1, visited)) {
				found = true;

				Map<AllocationNode, Set<AllocationNode>> leftHops = source.getLeftHops();
				if (leftHops.containsKey(prec)) {
					leftHops.get(prec).add(next);
				} else {
					Set<AllocationNode> set = new HashSet<>();
					set.add(next);
					leftHops.put(prec, set);
				}

				Map<AllocationNode, Set<AllocationNode>> rightHops = source.getRightHops();
				if (rightHops.containsKey(next)) {
					rightHops.get(next).add(prec);
				} else {
					Set<AllocationNode> set = new HashSet<>();
					set.add(prec);
					rightHops.put(next, set);
				}

				allocationManager.chooseFunctions(source, origin, finalDest);

			}
			visited.get(source).remove(dest);
		}
		return found;
	}

	/**
	 * Adds the condition related to a requested policy
	 */

	public void setProperty(List<Property> prop) {
		prop.forEach(p -> {
			String src = p.getSrc(), dst = p.getDst();
			AllocationNode source = allocationNodes.values().stream().filter(n -> {
				return n.getNode().getName().equals(src);
			}).findFirst().orElse(null);
			AllocationNode dest = allocationNodes.values().stream().filter(n -> {
				return n.getNode().getName().equals(dst);
			}).findFirst().orElse(null);
			if (source == null || dest == null)
				throw new BadGraphError("Error in the property definition", EType.INVALID_PROPERTY_DEFINITION);

			switch (p.getName()) {
			case ISOLATION_PROPERTY:
				check.propertyAdd(source, dest, Prop.ISOLATION, p);
				break;
			case REACHABILITY_PROPERTY:
				check.propertyAdd(source, dest, Prop.REACHABILITY, p);
				break;
			default:
				throw new BadGraphError("Error in the property definition", EType.INVALID_PROPERTY_DEFINITION);
			}
		});
	}

	/**
	 * Checks if the service graph satisfies all the imposed conditions
	 * 
	 * @return
	 */
	public VerificationResult checkNFFGProperty() {
		VerificationResult ret = this.check.propertyCheck();
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
	 * Returns true if the node is a client
	 */
	public boolean nodeIsClient(Node n) {
		return n.getFunctionalType().equals(FunctionalTypes.MAILCLIENT)
				|| n.getFunctionalType().equals(FunctionalTypes.WEBCLIENT)
				|| n.getFunctionalType().equals(FunctionalTypes.ENDHOST);

	}

	/**
	 * Returns true if the node is a server
	 */
	public boolean nodeIsServer(Node n) {
		return n.getFunctionalType().equals(FunctionalTypes.MAILSERVER)
				|| n.getFunctionalType().equals(FunctionalTypes.WEBSERVER);

	}

	/**
	 * @return the total number of deployment conditions, which is correlated with
	 *         the complexity of the problem
	 */
	public int getNrOfConditions() {
		return nrOfConditions;
	}

	public Map<String, AllocationNode> getAllocationNodes() {
		return allocationNodes;
	}

}
