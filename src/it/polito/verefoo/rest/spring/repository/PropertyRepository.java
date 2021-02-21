package it.polito.verefoo.rest.spring.repository;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import it.polito.verefoo.DbProperty;

@Repository
public interface PropertyRepository extends Neo4jRepository<DbProperty, Long> {



    /**
     * The super method just deletes the node labeled with DbProperty and with the
     * given id, while its neighbours remain stored (no cascade).
     */
    @Override
    @Query("CYPHER 3.5 MATCH tmp = (p:DbProperty)-[*]-(any) WHERE id(p)=$id " +

    // Neglect the foreign-key relationship
    "WITH *, relationships(tmp) as rels " +
    "WHERE NONE( rel in rels WHERE type(rel)='PROPERTY_TO_GRAPH') " +

    "DETACH DELETE p, any")
    void deleteById(@Param("id") Long id);



    @Query("CYPHER 3.5 MATCH (p:DbProperty) WHERE id(p)=$id " +
    "WITH p " +
    // OPTIONAL will cause g to be null if the referred graph doesn't exist;
    // then, since g is null, the merge will throw an exception
    "OPTIONAL MATCH (g:DbGraph) WHERE id(g)=p.graph " +
    "WITH p, g " +
    "MERGE (p)-[:PROPERTY_TO_GRAPH]->(g)")
    void bindToGraph(@Param("id") Long id);



    @Query("CYPHER 3.5 MATCH (p:DbProperty) WHERE id(p)=$id " +
    "WITH p " +
    "MATCH (g:DbGraph) WHERE id(g)=p.graph " +
    "WITH p, g " +
    "MATCH (p)-[r:PROPERTY_TO_GRAPH]->(g) " +
    "DELETE r")
    void unbindFromGraph(@Param("id") Long id);


    
}
