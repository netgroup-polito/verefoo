package it.polito.verefoo.rest.spring.repository;

import java.util.Optional;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import it.polito.verefoo.DbConstraints;

@Repository
public interface ConstraintsRepository extends Neo4jRepository<DbConstraints, Long> {



    @Query("CYPHER 3.5 MATCH tmp = (c:DbConstraints)-[*]->(any) WHERE c.graph=$id " +

    // Neglect the foreign-key relationship
    "WITH *, relationships(tmp) as rels " +
    "WHERE NONE( rel in rels WHERE type(rel)='CONSTRAINTS_TO_GRAPH') " +

    "DETACH DELETE c, any")
    void deleteByGraphId(@Param("id") Long id);
    


    @Query("CYPHER 3.5 MATCH tmp = (c:DbConstraints)-[*]->(any) WHERE c.graph=$id " +

    // Neglect the foreign-key relationship
    "WITH *, relationships(tmp) as rels " +
    "WHERE NONE( rel in rels WHERE type(rel)='CONSTRAINTS_TO_GRAPH') " +

    "RETURN (c)-[*]->(any)")
    Optional<DbConstraints> findByGraphId(Long id);


    
    @Query("CYPHER 3.5 MATCH (c:DbConstraints) WHERE id(c)=$id " +
    "WITH c " +
    // OPTIONAL will cause g to be null if the referred graph doesn't exist;
    // then, since g is null, the merge will throw an exception
    "OPTIONAL MATCH (g:DbGraph) WHERE id(g)=c.graph " +
    "WITH c, g " +
    "MERGE (c)-[:CONSTRAINTS_TO_GRAPH]->(g)")
    void bindToGraph(@Param("id") Long id);


    
}
