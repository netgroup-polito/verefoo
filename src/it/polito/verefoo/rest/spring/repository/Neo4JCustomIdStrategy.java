package it.polito.verefoo.rest.spring.repository;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.id.IdStrategy;

import it.polito.verefoo.jaxb.Host;
import it.polito.verefoo.jaxb.NodeRefType;

public class Neo4JCustomIdStrategy implements IdStrategy {

    @Override
    public Object generateId(Object entity) {

        List<Field> fields = Arrays.asList(entity.getClass().getFields());
        // Field idField = fields.stream().filter(field -> field.isAnnotationPresent(Id.class)).findFirst().get();
        // try {
        //     return idField.get(idField);
        // } catch (IllegalArgumentException | IllegalAccessException e1) {
        //     // TODO Auto-generated catch block
        //     e1.printStackTrace();
        // }

        Optional<Field> field = fields.stream().filter(f -> f.getName().equals("id")).findAny();
        if (field.isPresent()) {
            try {
                return field.get().get(field);
            } catch (IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        if (entity instanceof Host) {
            return ((Host) entity).getName();
        } else if (entity instanceof NodeRefType) {
            return ((NodeRefType) entity).getNode();
        }

        // field = fields.stream().filter(f -> f.getName().equals("name")).findAny();
        // if (field.isPresent()) {
        //     try {
        //         return field.get().get(field);
        //     } catch (IllegalArgumentException | IllegalAccessException e) {
        //         e.printStackTrace();
        //     }
        // }

        // the default case
        return entity.hashCode();
    }
  }