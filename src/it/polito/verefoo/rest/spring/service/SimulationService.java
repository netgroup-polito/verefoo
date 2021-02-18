package it.polito.verefoo.rest.spring.service;

import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import it.polito.verefoo.DbNFV;
import it.polito.verefoo.jaxb.Constraints;
import it.polito.verefoo.jaxb.Graphs;
import it.polito.verefoo.jaxb.LinkConstraints;
import it.polito.verefoo.jaxb.NFV;
import it.polito.verefoo.jaxb.NodeConstraints;
import it.polito.verefoo.rest.spring.converter.SimulationConverter;
import it.polito.verefoo.rest.spring.repository.SimulationRepository;

@Service
public class SimulationService {

	@Autowired
	SimulationRepository simulationRepository;

	@Autowired
	GraphService graphService;

	@Autowired
	RequirementService requirementService;

	@Autowired
	SubstrateService substrateService;

	@Autowired
	SimulationConverter converter;

	@Transactional
	public Long createSimulationResult(NFV nfv) {

		// create data structures separately (don't deserialize explicitly): this allows
		// to exploit the methods in the pertinent service components

		// graphs ids are automatically generated by the db, but the user in any case need some marker to
		// reference them from property resources in order to run the simulation correctly; here store the
		// mapping between the old ids and the new ids generated by the db
		Map<Long, Long> oldNewGraphIds = new TreeMap<>();

		DbNFV dbNFV = new DbNFV();
		dbNFV.getGraph().addAll(graphService.createGraphs(nfv.getGraphs()));

		// populate map: rely on the fact that the order of ids returned by createGraphs is the same as the
		// graphs passed as parameter
		for (int i = 0; i < dbNFV.getGraph().size(); i++) {
			oldNewGraphIds.put(nfv.getGraphs().getGraph().get(i).getId(), dbNFV.getGraph().get(i));
		}

		// not required fields in an NFV are skipped
		if (nfv.getConstraints() != null) {
			dbNFV.getGraph().forEach(graphId -> {
				graphService.createConstraints(graphId, nfv.getConstraints());
			});
		}

		// update properties with the new graph ids
		nfv.getPropertyDefinition().getProperty().forEach(property -> property.setGraph(oldNewGraphIds.get(property.getGraph())));
		
		dbNFV.setPropertyDefinition(requirementService.createRequirementsSet(nfv.getPropertyDefinition()));
		if (nfv.getHosts() != null && nfv.getConnections() != null) {
			dbNFV.setSubstrate(substrateService.createSubstrate());
			substrateService.createHosts(dbNFV.getSubstrate(), nfv.getHosts());
			substrateService.createConnections(dbNFV.getSubstrate(), nfv.getConnections());
		}
		dbNFV.setNetworkForwardingPaths(converter.deserializeNetworkForwardingPaths(nfv.getNetworkForwardingPaths()));
        dbNFV.setParsingString(nfv.getParsingString());

		return simulationRepository.save(dbNFV).getId();
	}

	@Transactional
	public Long createSimulationResult(NFV nfv, Long gid, Long rid, Long sid) {
		DbNFV dbNFV = new DbNFV();
		dbNFV.getGraph().add(gid);
		dbNFV.setPropertyDefinition(rid);
		dbNFV.setSubstrate(sid);
		requirementService.updateRequirementsSet(rid, nfv.getPropertyDefinition());
		return simulationRepository.save(dbNFV).getId();
	}

	@Transactional
	public NFV getSimulationResult(Long id) throws Exception {
		NFV nfv = new NFV();
		DbNFV dbNFV;
		Optional<DbNFV> tmp = simulationRepository.findById(id, -1);
		if (tmp.isPresent()) {
			dbNFV = tmp.get();
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The simulation result " + id + " doesn't exist.");
		}

		nfv.setGraphs(new Graphs());
		for (Long graphId : dbNFV.getGraph()) {
			nfv.getGraphs().getGraph().add(graphService.getGraph(graphId));
		}

		// a set of constraints refers to one graph; since the NFV POJO accepts only one set of constraints,
		// I assume that the constraints are the same for all graphs of the nfv
		try {
			dbNFV.getGraph().forEach(graphId -> nfv.setConstraints(graphService.getConstraints(graphId)));
		} catch (ResponseStatusException e) {
			// no constraints exist for the graph
			Constraints constraints;
			constraints = new Constraints();
			constraints.setNodeConstraints(new NodeConstraints());
			constraints.setLinkConstraints(new LinkConstraints());
			nfv.setConstraints(constraints);
		}

		nfv.setPropertyDefinition(requirementService.getRequirementsSet(dbNFV.getPropertyDefinition()));

		if (dbNFV.getSubstrate() != null) {
			nfv.setHosts(substrateService.getHosts(dbNFV.getSubstrate()));
			nfv.setConnections(substrateService.getConnections(dbNFV.getSubstrate()));
		}

		nfv.setNetworkForwardingPaths(converter.serializeNetworkForwardingPaths(dbNFV.getNetworkForwardingPaths()));
		nfv.setParsingString(dbNFV.getParsingString());

		return nfv;
	}

	public NFV buildNFVFromParams(Long graphId, Long requirementsSetId, Long substrateId) {
		NFV nfv = new NFV();
		nfv.setGraphs(new Graphs());
		nfv.getGraphs().getGraph().add(graphService.getGraph(graphId));
		
		// if no constraints are defined, empty inner data structures are needed by the verefoo core in any case
		Constraints constraints;
		try {
			constraints = graphService.getConstraints(graphId);
		} catch (ResponseStatusException e) {
			// no constraints exist for the graph
			constraints = new Constraints();
			constraints.setNodeConstraints(new NodeConstraints());
			constraints.setLinkConstraints(new LinkConstraints());
		}
		nfv.setConstraints(constraints);

		if (substrateId != null) {
			nfv.setHosts(substrateService.getHosts(substrateId));
			nfv.setConnections(substrateService.getConnections(substrateId));
		}
		if (requirementsSetId != null) {
			nfv.setPropertyDefinition(requirementService.getRequirementsSet(requirementsSetId));
			nfv.getPropertyDefinition().getProperty().forEach(property -> property.setGraph(graphId));
		}
		
		// no network forwarding paths nor parsing string here: their values remain null

		return nfv;

	}

    
}
