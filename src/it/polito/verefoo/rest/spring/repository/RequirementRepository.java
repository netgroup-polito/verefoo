package it.polito.verefoo.rest.spring.repository;

import java.util.List;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import it.polito.verefoo.DbPropertyDefinition;

public interface RequirementRepository extends Neo4jRepository<DbPropertyDefinition, Long> {
    
    @Override
    @Query("CYPHER 3.5 MATCH (p:DbPropertyDefinition)-[*]-(any) " +
    "RETURN (p)-[*]-(any)")
    List<DbPropertyDefinition> findAll();

    @Override
    @Query("CYPHER 3.5 MATCH (p:DbPropertyDefinition)-[*]-(any) " +
    "DETACH DELETE p, any")
    void deleteAll();
    
}
