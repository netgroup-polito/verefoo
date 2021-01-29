package it.polito.verefoo.rest.spring.repository;

import java.util.Optional;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import it.polito.verefoo.DbHosts;

@Repository
public interface HostsRepository extends Neo4jRepository<DbHosts, Long> {
    
    /**
     * The super method just deletes all nodes labeled with {@code DbHosts}, while
     * their neighbours remain stored (no cascade).
     */
    @Override
    @Query("CYPHER 3.5 MATCH (h:DbHosts)-[*]-(any) " +
    "DETACH DELETE h, any")
    void deleteAll();

    /**
     * The super method just deletes the node labeled with {@code DbHosts} and with the
     * given id, while its neighbours remain stored (no cascade).
     */
    @Override
    @Query("CYPHER 3.5 MATCH (h:DbHosts)-[*]-(any) WHERE id(h)=$id " +
    "DETACH DELETE h, any")
    void deleteById(@Param("id") Long id);

    /**
     * The super method just deletes the node labeled with {@code DbHosts} and with the
     * given id, while its neighbours remain stored (no cascade).
     */
    @Query("CYPHER 3.5 MATCH (h:DbHosts)-[*]-(any) WHERE id(h)=$id " +
    "DETACH DELETE any")
    void deleteHosts(@Param("id") Long id);

    @Query("CYPHER 3.5 MATCH (s:DbHosts) WHERE id(s)=$substrateId " +
    "WITH s " +
    "MATCH (h:DbHost) WHERE id(h)=$hostId " +
    "MERGE (s)-[:HOST]->(h)")
    void bindHost(@Param("substrateId") Long substrateId, @Param("hostId") Long hostId);

    @Query("MATCH (s:DbHosts)-[r]->(h:DbHost) WHERE id(h) = $hostId " +
    "DELETE r")
    void unbindHost(@Param("substrateId") Long substrateId, @Param("hostId") Long hostId);
    
}
