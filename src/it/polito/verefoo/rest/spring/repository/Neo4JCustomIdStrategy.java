package it.polito.verefoo.rest.spring.repository;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.neo4j.ogm.id.IdStrategy;

import it.polito.verefoo.jaxb.Host;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class Neo4JCustomIdStrategy implements IdStrategy {

    static Logger logger = LogManager.getLogger("neo4jIdLog");

    @Override
    public Object generateId(Object entity) {

        logger.info(entity.getClass().getSimpleName());

        List<Field> fields = Arrays.asList(entity.getClass().getFields());

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
        }

        // field = fields.stream().filter(f -> f.getName().equals("name")).findAny();
        // if (field.isPresent()) {
        //     try {
        //         return field.get().get(field);
        //     } catch (IllegalArgumentException | IllegalAccessException e) {
        //         e.printStackTrace();
        //     }
        // }

        // should never arrive here
        return entity.hashCode();
    }
  }