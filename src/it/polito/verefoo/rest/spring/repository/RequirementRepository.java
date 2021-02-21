package it.polito.verefoo.rest.spring.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import it.polito.verefoo.DbPropertyDefinition;

public interface RequirementRepository extends Neo4jRepository<DbPropertyDefinition, Long> {



    /**
     * Delete all the requirements sets
     */
    @Override
    @Query("CYPHER 3.5 MATCH tmp = (p:DbPropertyDefinition)-[*]->(any) " +

    // Neglect the foreign-key relationship
    "WITH *, relationships(tmp) as rels " +
    "WHERE NONE( rel in rels WHERE type(rel)='PROPERTY_TO_GRAPH') " +

    "DETACH DELETE tmp")
    void deleteAll();




    /**
     * The super method just deletes the node labeled with DbPropertyDefinition and
     * with the given id, while its neighbours remain stored (no cascade).
     */
    @Override
    @Query("CYPHER 3.5 MATCH tmp = (p:DbPropertyDefinition)-[*]-(any) WHERE id(p)=$id " + 
    
    // Neglect the foreign-key relationship
    "WITH *, relationships(tmp) as rels " +
    "WHERE NONE( rel in rels WHERE type(rel)='PROPERTY_TO_GRAPH') " +
    
    "DETACH DELETE tmp")
    void deleteById(@Param("id") Long id);



    @Query("CYPHER 3.5 MATCH (propertyDefinition:DbPropertyDefinition) WHERE id(propertyDefinition)=$id " +
    "WITH propertyDefinition " +
    "MATCH (property:DbProperty) WHERE id(property)=$propertyId " +
    "MERGE (propertyDefinition)-[:PROPERTY]->(property)")
    void bindProperty(@Param("id") Long id, @Param("propertyId") Long propertyId);

    

    @Query("CYPHER 3.5 MATCH (propertyDefinition:DbPropertyDefinition)-[r:PROPERTY]->(property:DbProperty) WHERE id(propertyDefinition)=$id AND id(property)=$propertyId "
            + "DELETE r")
    void unbindProperty(@Param("id") Long id, @Param("propertyId") Long propertyId);


    
}
