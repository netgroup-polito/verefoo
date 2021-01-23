package it.polito.verefoo.rest.spring.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.polito.verefoo.DbPropertyDefinition;
import it.polito.verefoo.jaxb.PropertyDefinition;
import it.polito.verefoo.rest.spring.converter.RequirementConverter;
import it.polito.verefoo.rest.spring.repository.RequirementRepository;

@Service
public class RequirementService {

    @Autowired
    RequirementRepository requirementRepository;
    
    @Autowired
    RequirementConverter converter;

	public Long createRequirementsSet(PropertyDefinition requirementsSet) {
        DbPropertyDefinition dbPropertyDefinition = requirementRepository.save(converter.deserializePropertyDefinition(requirementsSet));
        return dbPropertyDefinition.getId();
	}

	public List<PropertyDefinition> getRequirementsSets() {
        List<PropertyDefinition> requirementsSets = new ArrayList<>();
        requirementRepository.findAll().forEach(requirementsSet -> {
            requirementsSets.add(converter.serializePropertyDefinition(requirementsSet));
        });
		return requirementsSets;
	}

	public void deleteRequirementsSets() {
        requirementRepository.deleteAll();
	}

}
