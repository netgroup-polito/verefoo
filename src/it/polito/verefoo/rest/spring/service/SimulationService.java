package it.polito.verefoo.rest.spring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.polito.verefoo.DbNFV;
import it.polito.verefoo.jaxb.NFV;
import it.polito.verefoo.rest.spring.converter.SimulationConverter;
import it.polito.verefoo.rest.spring.repository.HostRepository;
import it.polito.verefoo.rest.spring.repository.SimulationRepository;

@Service
public class SimulationService {

	@Autowired
	SimulationRepository simulationRepository;

	@Autowired
	HostRepository hostRepository;

	@Autowired
	SimulationConverter converter;

	public Long createSimulationResult(NFV nfv) {
		// return simulationRepository.save(converter.deserializeNFV(nfv)).getId();
		DbNFV dbNFV = converter.deserializeNFV(nfv);
		// create data structures separately (don't deserialize explicitly): receive ids of those data structures
		// retrieve the actual data structures through the ids (DbNFV must store them rather than the ids)
		return null;
	}

    
    
}
