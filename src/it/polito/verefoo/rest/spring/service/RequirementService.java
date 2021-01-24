package it.polito.verefoo.rest.spring.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.polito.verefoo.DbProperty;
import it.polito.verefoo.DbPropertyDefinition;
import it.polito.verefoo.jaxb.Property;
import it.polito.verefoo.jaxb.PropertyDefinition;
import it.polito.verefoo.rest.spring.converter.RequirementConverter;
import it.polito.verefoo.rest.spring.repository.PropertyRepository;
import it.polito.verefoo.rest.spring.repository.RequirementRepository;

@Service
public class RequirementService {

    @Autowired
    RequirementRepository requirementRepository;

    @Autowired
    PropertyRepository propertyRepository;
    
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

	public void deleteRequirementSet(Long id) {
        requirementRepository.deleteById(id);
	}

	public PropertyDefinition getRequirementsSet(Long id) {
        Optional<DbPropertyDefinition> dbPropertyDefinition = requirementRepository.findById(id);
        if (dbPropertyDefinition.isPresent()) {
            return converter.serializePropertyDefinition(dbPropertyDefinition.get());
        } else return null;
	}

    @Transactional
	public Long updateRequirementsSet(Long id, PropertyDefinition requirementsSet) {
        deleteRequirementSet(id);
        return createRequirementsSet(requirementsSet);
	}

    @Transactional
	public Long createProperty(Long id, Property property) {
        DbProperty dbProperty = propertyRepository.save(converter.deserializeProperty(property));
        requirementRepository.bindProperty(id, dbProperty.getId());
		return dbProperty.getId();
	}

    @Transactional
	public void deleteProperty(Long id, Long propertyId) {
        requirementRepository.unbindProperty(id, propertyId);
        propertyRepository.deleteById(propertyId);
	}

	public Property getProperty(Long id, Long propertyId) {
        Optional<DbProperty> dbProperty = propertyRepository.findById(propertyId);
        if (dbProperty.isPresent()) {
            return converter.serializeProperty(dbProperty.get());
        } else return null;
	}

	public Long updateProperty(Long id, Long propertyId, Property property) {
        deleteProperty(id, propertyId);
		return createProperty(id, property);
	}

}
