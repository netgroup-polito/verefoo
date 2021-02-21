package it.polito.verefoo.rest.spring.repository;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import it.polito.verefoo.DbNFV;

@Repository
public interface SimulationRepository extends Neo4jRepository<DbNFV, Long> {
    
    /**
     * The super method just deletes the node labeled with DbProperty and with the
     * given id, while its neighbours remain stored (no cascade).
     */
    @Override
    @Query("CYPHER 3.5 MATCH tmp = (n:DbNFV) WHERE id(n)=$id " +
    "DETACH DELETE n")
    void deleteById(@Param("id") Long id);

}
