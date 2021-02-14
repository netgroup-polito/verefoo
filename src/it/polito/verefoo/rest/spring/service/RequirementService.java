package it.polito.verefoo.rest.spring.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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

    public List<PropertyDefinition> getRequirementsSets() {
        List<PropertyDefinition> requirementsSets = new ArrayList<>();
        requirementRepository.findAll(-1).forEach(dbRequirementsSet -> {
            requirementsSets.add(converter.serializePropertyDefinition(dbRequirementsSet));
        });
        if (requirementsSets.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NO_CONTENT, "No requirement set is in the workspace.");
        } else {
            return requirementsSets;
        }
    }

    public void deleteRequirementsSets() {
        if (requirementRepository.count() > 0) {
            requirementRepository.deleteAll();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_MODIFIED, "The workspace is already clean of requirement sets.");
        }
        
    }

    @Transactional
    public Long createRequirementsSet(PropertyDefinition requirementsSet) {
        DbPropertyDefinition dbPropertyDefinition = requirementRepository
                .save(converter.deserializePropertyDefinition(requirementsSet));

        // create the edge as a foreign key
        try {
            dbPropertyDefinition.getProperty().forEach(property -> {
                propertyRepository.bindToGraph(property.getId());
            });
        } catch (Exception e) {
            // Referential integrity non satisfiable
            throw new ResponseStatusException(HttpStatus.FAILED_DEPENDENCY, "The referred graph doesn't exist");
        }

        return dbPropertyDefinition.getId();
    }

    public void deleteRequirementsSet(Long id) {
        if (requirementRepository.existsById(id)) {
            requirementRepository.deleteById(id);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The requirement set " + id + " doesn't exist.");
        }
    }

    public PropertyDefinition getRequirementsSet(Long id) {
        Optional<DbPropertyDefinition> dbPropertyDefinition = requirementRepository.findById(id, -1);
        if (dbPropertyDefinition.isPresent()) {
            return converter.serializePropertyDefinition(dbPropertyDefinition.get());
        } else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The requirement set " + id + " doesn't exist.");
    }

    @Transactional
    public void updateRequirementsSet(Long id, PropertyDefinition requirementsSet) {
        DbPropertyDefinition newDbPropertyDefinition = converter.deserializePropertyDefinition(requirementsSet);

        DbPropertyDefinition oldDbPropertyDefinition;
        Optional<DbPropertyDefinition> dbPropertyDefinition = requirementRepository.findById(id, -1);
        if (dbPropertyDefinition.isPresent()) {
            oldDbPropertyDefinition = dbPropertyDefinition.get();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The requirement set " + id + " doesn't exist.");
        }

        // merge
        newDbPropertyDefinition.setId(id);
        requirementRepository.save(newDbPropertyDefinition, 0);

        // transfer the information on a variable due to @Transactional behaviour
        Long newDbPropertyDefinitionPropertySize = Long.valueOf(newDbPropertyDefinition.getProperty().size());
        Long oldDbPropertyDefinitionPropertySize = Long.valueOf(oldDbPropertyDefinition.getProperty().size());

        if (newDbPropertyDefinitionPropertySize >= oldDbPropertyDefinitionPropertySize) {
            int i = 0;
            for (; i < oldDbPropertyDefinitionPropertySize; i++) {
                updateProperty(id, oldDbPropertyDefinition.getProperty().get(i).getId(), requirementsSet.getProperty().get(i));
            }
            for (; i < newDbPropertyDefinitionPropertySize; i++) {
                createProperty(id, requirementsSet.getProperty().get(i));
            }
        } else {
            int i = 0;
            for (; i < newDbPropertyDefinitionPropertySize; i++) {
                updateProperty(id, oldDbPropertyDefinition.getProperty().get(i).getId(), requirementsSet.getProperty().get(i));
            }
            for (; i < oldDbPropertyDefinitionPropertySize; i++) {
                deleteProperty(id, oldDbPropertyDefinition.getProperty().get(i).getId());
            }
        }

    }

    @Transactional
    public Long createProperty(Long id, Property property) {
        DbProperty dbProperty = propertyRepository.save(converter.deserializeProperty(property));
        try {
            requirementRepository.bindProperty(id, dbProperty.getId());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The requirements set " + id + " doesn't exist.");
        }
        // create the edge as a foreign key
        try {
            propertyRepository.bindToGraph(dbProperty.getId());
        } catch (Exception e) {
            // Referential integrity non satisfiable
            throw new ResponseStatusException(HttpStatus.FAILED_DEPENDENCY, "The referred graph doesn't exist");
        }
        return dbProperty.getId();
    }

    @Transactional
    public void deleteProperty(Long id, Long propertyId) {
        if (propertyRepository.existsById(propertyId)) {
            requirementRepository.unbindProperty(id, propertyId);
            propertyRepository.deleteById(propertyId);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The property " + propertyId + " doesn't exist.");
        }
        
    }

    public Property getProperty(Long id, Long propertyId) {
        Optional<DbProperty> dbProperty = propertyRepository.findById(propertyId, -1);
        if (dbProperty.isPresent()) {
            return converter.serializeProperty(dbProperty.get());
        } else
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The property " + propertyId + " doesn't exist.");
    }

    @Transactional
    public void updateProperty(Long id, Long propertyId, Property property) {

        DbProperty newDbProperty = converter.deserializeProperty(property);

        DbProperty oldDbProperty;
        Optional<DbProperty> dbProperty = propertyRepository.findById(propertyId, -1);
        if (dbProperty.isPresent()) {
            oldDbProperty = dbProperty.get();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "The property " + propertyId + " doesn't exist.");
        }

        // detach
        propertyRepository.unbindFromGraph(propertyId);
        // merge
        newDbProperty.setId(propertyId);
        newDbProperty.getHTTPDefinition().setId(oldDbProperty.getHTTPDefinition().getId());
        newDbProperty.getPOP3Definition().setId(oldDbProperty.getPOP3Definition().getId());
        propertyRepository.save(newDbProperty);
        try {
            propertyRepository.bindToGraph(propertyId);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.FAILED_DEPENDENCY, "The referred graph doesn't exist");
        }
    }

}
