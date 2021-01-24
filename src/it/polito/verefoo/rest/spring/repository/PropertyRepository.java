package it.polito.verefoo.rest.spring.repository;

import java.util.Optional;

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
    @Query("CYPHER 3.5 MATCH (p:DbProperty)-[*]-(any) WHERE id(p)=$id " +
    "DETACH DELETE p, any")
    void deleteById(@Param("id") Long id);

    @Override
    @Query("CYPHER 3.5 MATCH (p:DbProperty)-[*]->(any) WHERE id(p)=$id " +
    "RETURN (p)-[*]->(any)")
    Optional<DbProperty> findById(@Param("id") Long id);

}
