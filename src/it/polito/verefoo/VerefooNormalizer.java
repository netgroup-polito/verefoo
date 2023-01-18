package it.polito.verefoo;

import java.util.List;
import java.util.Map;



import it.polito.verefoo.extra.BadGraphError;
import it.polito.verefoo.jaxb.*;

import static java.util.stream.Collectors.*;


import java.util.HashMap;

/**
 * This class hides some limitation of z3 from the final user (e.g. two
 * properties with same source and destination)
 */
public class VerefooNormalizer {
	private NFV root, originalNfv;
	private Map<String, String> networkGroups, flowGroups;

	/**
	 * Translates the input in a normalized format
	 * 
	 * @param root the NFV element received in input
	 */
	public VerefooNormalizer(NFV root) {
		try {
			networkGroups = new HashMap<>();
			flowGroups = new HashMap<>();
			this.originalNfv = root;
			this.root = root;
			normalize();
		} catch (Exception e) {
			e.printStackTrace();
			throw new BadGraphError("Error during deserializing");
		}
	}

	/**
	 * This method normalize networks and flows. It's a wrapper of two other
	 * specific methods.
	 */
	private void normalize() {
		normalizeProperties();
	}

	/**
	 * Translates properties with a wildcard in a list of properties with the
	 * explicit enumeration of the nodes present in the graph
	 */
	private void normalizeProperties() {
		List<Property> rootProperties = root.getPropertyDefinition().getProperty();
		root.getGraphs().getGraph().forEach((g) -> {
			List<Node> nodes = g.getNode();
			Map<String, List<Property>> propsSrc = root.getPropertyDefinition().getProperty().stream()
					.filter(p -> p.getGraph() == g.getId() && (p.getSrc().contains("-1")))
					.collect(groupingBy(p -> p.getSrc(), toList()));
			propsSrc.entrySet().forEach(e -> {
				List<Node> nodesInNetworkSrc = nodes.stream()
						.filter(n -> inNetwork(e.getKey(), n.getName())
								&& (n.getFunctionalType().equals(FunctionalTypes.WEBCLIENT)
										|| n.getFunctionalType().equals(FunctionalTypes.MAILCLIENT)
										|| n.getFunctionalType().equals(FunctionalTypes.ENDHOST)
										|| n.getFunctionalType().equals(FunctionalTypes.WEBSERVER)
										|| n.getFunctionalType().equals(FunctionalTypes.MAILSERVER)))
						.collect(toList());
				if (nodesInNetworkSrc.isEmpty())
					throw new BadGraphError(
							"You specified a network (" + e.getKey()
									+ ") in the property that contains none of the nodes declared in the service graph",
							EType.INVALID_PROPERTY_DEFINITION);
				e.getValue().forEach(p -> {
					nodesInNetworkSrc.forEach(n -> {
						Property newP = copyProperty(p);
						newP.setSrc(n.getName());
						networkGroups.put(n.getName(), e.getKey());
						rootProperties.add(newP);
					});
					rootProperties.remove(p);
				});

			});
			Map<String, List<Property>> propsDst = root.getPropertyDefinition().getProperty().stream()
					.filter(p -> p.getGraph() == g.getId() && p.getDst().contains("-1"))
					.collect(groupingBy(p -> p.getDst(), toList()));
			propsDst.entrySet().forEach(e -> {
				List<Node> nodesInNetworkDst = nodes.stream()
						.filter(n -> inNetwork(e.getKey(), n.getName())
								&& (n.getFunctionalType().equals(FunctionalTypes.WEBCLIENT)
										|| n.getFunctionalType().equals(FunctionalTypes.MAILCLIENT)
										|| n.getFunctionalType().equals(FunctionalTypes.ENDHOST)
										|| n.getFunctionalType().equals(FunctionalTypes.WEBSERVER)
										|| n.getFunctionalType().equals(FunctionalTypes.MAILSERVER)))
						.collect(toList());
				if (nodesInNetworkDst.isEmpty())
					throw new BadGraphError(
							"You specified a network (" + e.getKey()
									+ ") in the property that contains none of the nodes declared in the service graph",
							EType.INVALID_PROPERTY_DEFINITION);
				e.getValue().forEach(p -> {
					nodesInNetworkDst.forEach(n -> {
						Property newP = copyProperty(p);
						newP.setDst(n.getName());
						networkGroups.put(n.getName(), e.getKey());
						rootProperties.add(newP);
					});
					rootProperties.remove(p);
				});

			});
		});
	}

	/**
	 * This method is used to create a copy of a property in a new object.
	 * 
	 * @param p The original property
	 * @return the copy of the property
	 */
	private Property copyProperty(Property p) {
		Property newP = new Property();
		newP.setDst(p.getDst());
		newP.setDstPort(p.getDstPort());
		newP.setGraph(p.getGraph());
		newP.setHTTPDefinition(p.getHTTPDefinition());
		newP.setLv4Proto(p.getLv4Proto());
		newP.setName(p.getName());
		newP.setPOP3Definition(p.getPOP3Definition());
		newP.setSrc(p.getSrc());
		newP.setSrcPort(p.getSrcPort());
		return newP;
	}

	
	

	/**
	 * This method helps to identify if an Ip address is present in a larger address
	 * range
	 * 
	 * @param network CIDR Address range
	 * @param ip      The specific address
	 * @return true of the ip address is in the specified range
	 */
	private boolean inNetwork(String network, String ip) {
		String[] decimalNotationIp = ip.split("\\.");
		String[] decimalNotationNetwork = network.split("\\.");
		int i = 0;
		for (String s : decimalNotationNetwork) {
			if (!decimalNotationIp[i].equals(s) && !s.equals("-1"))
				return false;
			i++;
		}
		return true;
	}

	/**
	 * Get the original NFV element received in input
	 * 
	 * @return the original NFV element received in input
	 */
	public NFV getOriginalNfv() {
		return originalNfv;
	}

	/**
	 * Set the original NFV element received in input
	 * 
	 * @param originalRoot the original NFV element received in input
	 */
	public void setOriginalNfv(NFV originalRoot) {
		this.originalNfv = originalRoot;
	}

	/**
	 * @return the normalized NFV root element
	 */
	public NFV getRoot() {
		return root;
	}

	/**
	 * @return the map that tells to which network a node belongs
	 */
	public Map<String, String> getNetworkGroups() {
		return networkGroups;
	}

	/**
	 * @return the map that tells to which original node a virtual node (flow)
	 *         belongs
	 */
	public Map<String, String> getFlowGroups() {
		return flowGroups;
	}

	/**
	 * Get a specific graph from the original NFV element received in input
	 * 
	 * @param id the id of the graph in the NFV element
	 * @return a specific graph from the original NFV element received in input
	 */
	public Graph getOriginalGraph(long id) {
		return originalNfv.getGraphs().getGraph().stream().filter(g -> g.getId() == id).findFirst().orElse(null);
	}

}
