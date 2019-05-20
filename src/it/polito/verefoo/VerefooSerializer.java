package it.polito.verefoo;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.microsoft.z3.Status;

import it.polito.verefoo.allocation.AllocationGraphGenerator;
import it.polito.verefoo.extra.BadGraphError;
import it.polito.verefoo.jaxb.Connection;
import it.polito.verefoo.jaxb.EType;
import it.polito.verefoo.jaxb.Graph;
import it.polito.verefoo.jaxb.Host;
import it.polito.verefoo.jaxb.NFV;
import it.polito.verefoo.jaxb.Node;
import it.polito.verefoo.jaxb.Path;
import it.polito.verefoo.jaxb.Property;
import it.polito.verefoo.translator.Translator;
import it.polito.verigraph.extra.VerificationResult;

/**
 * This class separates the Verifoo classes implementation from the actual input
 */
public class VerefooSerializer {
	private NFV nfv, result;
	private boolean sat = false;
	private Logger logger = LogManager.getLogger("model");
	private Logger loggerResult = LogManager.getLogger("result");

	/**
	 * Wraps all the Verifoo tasks, executing the z3 procedure for each graph in the
	 * NFV element
	 * 
	 * @param root the NFV element received as input
	 */
	public VerefooSerializer(NFV root) {
		this.nfv = root;
		AllocationGraphGenerator agg = new AllocationGraphGenerator(root);
		root = agg.getAllocationGraph();
		VerefooNormalizer norm = new VerefooNormalizer(root);
		root = norm.getRoot();

		try {
			List<Path> paths = null;
			//TODO if it works
			if (root.getNetworkForwardingPaths() != null)
				paths = root.getNetworkForwardingPaths().getPath();
			for (Graph g : root.getGraphs().getGraph()) {
				List<Property> prop = root.getPropertyDefinition().getProperty().stream()
						.filter(p -> p.getGraph() == g.getId()).collect(Collectors.toList());
				if (prop.size() == 0)
					throw new BadGraphError("No property defined for the Graph " + g.getId(),
							EType.INVALID_PROPERTY_DEFINITION);
				VerefooProxy test = new VerefooProxy(g, root.getHosts(), root.getConnections(), root.getConstraints(),
						prop, paths);
				
				long beginAll = System.currentTimeMillis();
				VerificationResult res = test.checkNFFGProperty();
				long endAll = System.currentTimeMillis();
				loggerResult.debug("Only checker: " + (endAll - beginAll) + "ms");
				
				if (res.result != Status.UNSATISFIABLE && res.result != Status.UNKNOWN) {
					Translator t = new Translator(res.model.toString(), root, g, test.getAllocationNodes());
					t.setNormalizer(norm);
					result = t.convert();
					root = result;
					sat = true;
				} else {
					sat = false;
					result = root;
				}
				root.getPropertyDefinition().getProperty().stream().filter(p -> p.getGraph() == g.getId())
						.forEach(p -> p.setIsSat(res.result != Status.UNSATISFIABLE));
			}
		} catch (BadGraphError e) {
			logger.error("Graph semantically incorrect");
			logger.error(e);
			throw e;
		}
	}


	/**
	 * @return the original NFV object given in the constructor
	 */
	public NFV getNfv() {
		return nfv;
	}

	/**
	 * @return the NFV object after the computation
	 */
	public NFV getResult() {
		return result;
	}

	/**
	 * @return if the z3 model is sat
	 */
	public boolean isSat() {
		return sat;
	}

}
