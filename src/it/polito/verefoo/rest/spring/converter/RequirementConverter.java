package it.polito.verefoo.rest.spring.converter;

import org.springframework.stereotype.Component;

import it.polito.verefoo.DbHTTPDefinition;
import it.polito.verefoo.DbL4ProtocolTypes;
import it.polito.verefoo.DbPName;
import it.polito.verefoo.DbPOP3Definition;
import it.polito.verefoo.DbProperty;
import it.polito.verefoo.DbPropertyDefinition;
import it.polito.verefoo.jaxb.HTTPDefinition;
import it.polito.verefoo.jaxb.L4ProtocolTypes;
import it.polito.verefoo.jaxb.PName;
import it.polito.verefoo.jaxb.POP3Definition;
import it.polito.verefoo.jaxb.Property;
import it.polito.verefoo.jaxb.PropertyDefinition;

@Component
public class RequirementConverter {

    public DbPropertyDefinition deserializePropertyDefinition(PropertyDefinition propertyDefinition) {
        if (propertyDefinition == null) return null;
        DbPropertyDefinition dbPropertyDefinition = new DbPropertyDefinition();
        propertyDefinition.getProperty().forEach(property -> {
            dbPropertyDefinition.getProperty().add(deserializeProperty(property));
        });
        return dbPropertyDefinition;
    }

    public DbProperty deserializeProperty(Property property) {
        if (property == null) return null;
        DbProperty dbProperty = new DbProperty();
        dbProperty.setBody(property.getBody());
        dbProperty.setDst(property.getDst());
        dbProperty.setDstPort(property.getDstPort());
        dbProperty.setGraph(property.getGraph());
        dbProperty.setHTTPDefinition(deserializeHTTPDefinition(property.getHTTPDefinition()));
        dbProperty.setIsSat(property.isIsSat());
        dbProperty.setLv4Proto(DbL4ProtocolTypes.fromValue(property.getLv4Proto().name()));
        dbProperty.setName(DbPName.fromValue(property.getName().name().toLowerCase()));
        dbProperty.setPOP3Definition(deserializePOP3Definition(property.getPOP3Definition()));
        dbProperty.setSrc(property.getSrc());
        dbProperty.setSrcPort(property.getSrcPort());
        return dbProperty;
    }

    public DbHTTPDefinition deserializeHTTPDefinition(HTTPDefinition httpDefinition) {
        if (httpDefinition == null) return null;
        DbHTTPDefinition dbHTTPDefinition = new DbHTTPDefinition();
        dbHTTPDefinition.setDomain(httpDefinition.getDomain());
        dbHTTPDefinition.setOptions(httpDefinition.getOptions());
        dbHTTPDefinition.setUrl(httpDefinition.getUrl());
        return dbHTTPDefinition;
    }

    public DbPOP3Definition deserializePOP3Definition(POP3Definition pop3Definition) {
        if (pop3Definition == null) return null;
        DbPOP3Definition dbPOP3Definition = new DbPOP3Definition();
        dbPOP3Definition.setContentType(pop3Definition.getContentType());
        dbPOP3Definition.setFrom(pop3Definition.getFrom());
        dbPOP3Definition.setSender(pop3Definition.getSender());
        dbPOP3Definition.setSubject(pop3Definition.getSubject());
        dbPOP3Definition.setTo(pop3Definition.getTo());
        return dbPOP3Definition;
    }

    public PropertyDefinition serializePropertyDefinition(DbPropertyDefinition dbPropertyDefinition) {
        if (dbPropertyDefinition == null) return null;
        PropertyDefinition propertyDefinition = new PropertyDefinition();
        dbPropertyDefinition.getProperty().forEach(dbProperty -> {
            propertyDefinition.getProperty().add(serializeProperty(dbProperty));
        });
        return propertyDefinition;
    }

    public Property serializeProperty(DbProperty dbProperty) {
        if (dbProperty == null) return null;
        Property property = new Property();
        property.setBody(dbProperty.getBody());
        property.setDst(dbProperty.getDst());
        property.setDstPort(dbProperty.getDstPort());
        property.setGraph(dbProperty.getGraph());
        property.setHTTPDefinition(serializeHTTPDefinition(dbProperty.getHTTPDefinition()));
        property.setIsSat(dbProperty.isIsSat());
        property.setLv4Proto(L4ProtocolTypes.fromValue(dbProperty.getLv4Proto().name()));
        if (dbProperty.getName().equals(DbPName.ISOLATION_PROPERTY)) {
            property.setName(PName.ISOLATION_PROPERTY);
        } else {
            property.setName(PName.REACHABILITY_PROPERTY);
        }
        property.setPOP3Definition(serializePOP3Definition(dbProperty.getPOP3Definition()));
        property.setSrc(dbProperty.getSrc());
        property.setSrcPort(dbProperty.getSrcPort());
        return property;
    }

    public HTTPDefinition serializeHTTPDefinition(DbHTTPDefinition dbHTTPDefinition) {
        if (dbHTTPDefinition == null) return null;
        HTTPDefinition httpDefinition = new HTTPDefinition();
        httpDefinition.setDomain(dbHTTPDefinition.getDomain());
        httpDefinition.setOptions(dbHTTPDefinition.getOptions());
        httpDefinition.setUrl(dbHTTPDefinition.getUrl());
        return httpDefinition;
    }

    public POP3Definition serializePOP3Definition(DbPOP3Definition dbPOP3Definition) {
        if (dbPOP3Definition == null) return null;
        POP3Definition pop3Definition = new POP3Definition();
        pop3Definition.setContentType(dbPOP3Definition.getContentType());
        pop3Definition.setFrom(dbPOP3Definition.getFrom());
        pop3Definition.setSender(dbPOP3Definition.getSender());
        pop3Definition.setSubject(dbPOP3Definition.getSubject());
        pop3Definition.setTo(dbPOP3Definition.getTo());
        return pop3Definition;
    }
    
}
