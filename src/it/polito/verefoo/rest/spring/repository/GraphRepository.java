package it.polito.verefoo.rest.spring.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import it.polito.verefoo.DbGraph;

@Repository
public interface GraphRepository extends Neo4jRepository<DbGraph, Long> {

    @Override
    @Query("CYPHER 3.5 MATCH (s:DbGraph)-[*]-(n) " + "RETURN (s)-[*]-(n)")
    List<DbGraph> findAll();

    /**
     * The super method just deletes all nodes labeled with {@code DbGraph}, while
     * their neighbours remain stored (no cascade).
     */
    @Override
    @Query("CYPHER 3.5 MATCH (s:DbGraph)-[*]-(n) " + "DETACH DELETE s, n")
    void deleteAll();

    /**
     * The super method just deletes the node labeled with DbGraph and with the
     * given id, while its neighbours remain stored (no cascade).
     */
    @Override
    @Query("CYPHER 3.5 MATCH (s:DbGraph)-[*]-(n) WHERE id(s)=$id " +
    "DETACH DELETE s, n")
    void deleteById(@Param("id") Long id);

    @Override
    @Query("CYPHER 3.5 MATCH (s:DbGraph)-[*]-(n) WHERE id(s)=$id " +
    "RETURN (s:DbGraph)-[*]-(n)")
    Optional<DbGraph> findById(@Param("id") Long id);
}
