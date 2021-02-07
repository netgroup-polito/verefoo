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

    public List<PropertyDefinition> getRequirementsSets() {
        List<PropertyDefinition> requirementsSets = new ArrayList<>();
        requirementRepository.findAll(-1).forEach(requirementsSet -> {
            requirementsSets.add(converter.serializePropertyDefinition(requirementsSet));
        });
        return requirementsSets;
    }

    public void deleteRequirementsSets() {
        requirementRepository.deleteAll();
    }

    @Transactional
    public Long createRequirementsSet(PropertyDefinition requirementsSet) {
        DbPropertyDefinition dbPropertyDefinition = requirementRepository
                .save(converter.deserializePropertyDefinition(requirementsSet));

        // create the edge as a foreign key
        // try {
        dbPropertyDefinition.getProperty().forEach(property -> {
            propertyRepository.bindToGraph(property.getId());
        });
        // } catch (Exception e) {
        // // Referential integrity non satisfiable: the referred graph doesn't exist
        // throw new Exception("The referred graph doesn't exist");
        // }

        return dbPropertyDefinition.getId();
    }

    public void deleteRequirementsSet(Long id) {
        requirementRepository.deleteById(id);
    }

    public PropertyDefinition getRequirementsSet(Long id) {
        Optional<DbPropertyDefinition> dbPropertyDefinition = requirementRepository.findById(id, -1);
        if (dbPropertyDefinition.isPresent()) {
            return converter.serializePropertyDefinition(dbPropertyDefinition.get());
        } else
            return null;
    }

    @Transactional
    public void updateRequirementsSet(Long id, PropertyDefinition requirementsSet) {
        DbPropertyDefinition newDbPropertyDefinition = converter.deserializePropertyDefinition(requirementsSet);

        DbPropertyDefinition oldDbPropertyDefinition;
        Optional<DbPropertyDefinition> dbPropertyDefinition = requirementRepository.findById(id, -1);
        if (dbPropertyDefinition.isPresent()) {
            oldDbPropertyDefinition = dbPropertyDefinition.get();
        } else
            return;

        // merge
        newDbPropertyDefinition.setId(id);
        requirementRepository.save(newDbPropertyDefinition, 0);

        if (newDbPropertyDefinition.getProperty().size() >= oldDbPropertyDefinition.getProperty().size()) {
            int i = 0;
            for (; i < oldDbPropertyDefinition.getProperty().size(); i++) {
                updateProperty(id, oldDbPropertyDefinition.getProperty().get(i).getId(), requirementsSet.getProperty().get(i));
            }
            for (; i < newDbPropertyDefinition.getProperty().size(); i++) {
                createProperty(id, requirementsSet.getProperty().get(i));
            }
        } else {
            int i = 0;
            for (; i < newDbPropertyDefinition.getProperty().size(); i++) {
                updateProperty(id, oldDbPropertyDefinition.getProperty().get(i).getId(), requirementsSet.getProperty().get(i));
            }
            for (; i < oldDbPropertyDefinition.getProperty().size(); i++) {
                deleteProperty(id, oldDbPropertyDefinition.getProperty().get(i).getId());
            }
        }

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
        Optional<DbProperty> dbProperty = propertyRepository.findById(propertyId, -1);
        if (dbProperty.isPresent()) {
            return converter.serializeProperty(dbProperty.get());
        } else
            return null;
    }

    public void updateProperty(Long id, Long propertyId, Property property) {

        DbProperty newDbProperty = converter.deserializeProperty(property);

        DbProperty oldDbProperty;
        Optional<DbProperty> dbProperty = propertyRepository.findById(propertyId, -1);
        if (dbProperty.isPresent()) {
            oldDbProperty = dbProperty.get();
        } else
            return;

        // merge
        newDbProperty.setId(propertyId);
        newDbProperty.getHTTPDefinition().setId(oldDbProperty.getHTTPDefinition().getId());
        newDbProperty.getPOP3Definition().setId(oldDbProperty.getPOP3Definition().getId());
        propertyRepository.save(newDbProperty);

    }

}
