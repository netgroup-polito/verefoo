package it.polito.verefoo.rest.spring.repository;

import java.util.List;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import it.polito.verefoo.Neo4jConnection;

@Repository
public interface ConnectionRepository extends Neo4jRepository<Neo4jConnection, Long> {
    
    @Query("MATCH (source:Host)-[r1:CONNECTION]-(dest:Host)-[r2:BELONG_TO_SUBSTRATE]->(substrate:SubstrateId) WHERE id(substrate) = $substrateId " +
    "DELETE r1")
    void deleteBySubstrateId(@Param("substrateId") Long substrateId);

    @Query("MATCH (source:Host)-[r1:CONNECTION]-(dest:Host)-[r2:BELONG_TO_SUBSTRATE]->(substrate:SubstrateId) WHERE id(substrate) = $substrateId " +
    "WITH DISTINCT r1 " +
    "RETURN r1")
    List<Neo4jConnection> findAllBySubstrateId(@Param("substrateId") Long substrateId);

}
