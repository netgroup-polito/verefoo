package it.polito.verefoo.rest.spring.repository;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.id.IdStrategy;

import it.polito.verefoo.jaxb.Configuration;
import it.polito.verefoo.jaxb.Graph;
import it.polito.verefoo.jaxb.Host;
import it.polito.verefoo.jaxb.Node;
import it.polito.verefoo.jaxb.NodeRefType;

public class Neo4JCustomIdStrategy implements IdStrategy {

    @Override
    public Object generateId(Object entity) {

        // List<Field> fields = Arrays.asList(entity.getClass().getFields());
        // Field idField = fields.stream().filter(field -> field.isAnnotationPresent(Id.class)).findFirst().get();
        // if (idField.getType().equals(String.class)) {
        //     try {
        //         if (idField.get(idField) != null)
        //             return idField.get(idField);
        //         else return idField.getName() + String.valueOf(entity.hashCode());
        //     } catch (IllegalArgumentException | IllegalAccessException e) {
        //         // TODO Auto-generated catch block
        //         e.printStackTrace();
        //     }
        // }
        
        // try {
        //     return idField.get(idField);
        // } catch (IllegalArgumentException | IllegalAccessException e1) {
        //     // TODO Auto-generated catch block
        //     e1.printStackTrace();
        // }

        if (entity instanceof Host) {
            return ((Host) entity).getName();
        } else if (entity instanceof NodeRefType) {
            return ((NodeRefType) entity).getNode();
        } else if (entity instanceof Node) {
            return ((Node) entity).getName();
        } else if (entity instanceof Configuration) {
            return ((Configuration) entity).getName();
        } else if (entity instanceof Graph) {
            if (((Graph) entity).getId() != null)
                return ((Graph) entity).getId();
        }

        // Optional<Field> field = fields.stream().filter(f -> f.getName().equals("id")).findAny();
        // if (field.isPresent()) {
        //     try {
        //         if (field.get().get(field) != null) {
        //             return field.get().get(field);
        //         } else return UUID.randomUUID();
        //     } catch (IllegalArgumentException | IllegalAccessException e) {
        //         e.printStackTrace();
        //     }
        // }

        

        // field = fields.stream().filter(f -> f.getName().equals("name")).findAny();
        // if (field.isPresent()) {
        //     try {
        //         return field.get().get(field);
        //     } catch (IllegalArgumentException | IllegalAccessException e) {
        //         e.printStackTrace();
        //     }
        // }
        

        // the default case
        // return Long.valueOf(Integer.valueOf(entity.hashCode()).longValue());
        return String.valueOf(entity.hashCode());
    }
  }