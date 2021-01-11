package it.polito.verefoo.rest.spring.repository;

import java.util.List;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import it.polito.verefoo.jaxb.Host;


@Repository
public interface HostRepository extends Neo4jRepository<Host, String> {
    
    @Query("UNWIND $hosts as host " +
    "MATCH (h:Host {name: host.name}) " +
    "WITH h " +
    "MATCH (s:SubstrateId) WHERE id(s) = $substrateId " +
    "WITH h, s " +
    "MERGE (h)-[:BELONG_TO_SUBSTRATE]->(s)")
    void bindHostsToSubstrate(@Param("substrateId") Long substrateId, @Param("hosts") List<Host> hosts);

    @Query("MATCH (h:Host)-[r:BELONG_TO_SUBSTRATE]->(s:SubstrateId) WHERE id(s) = $substrateId " +
    "DELETE r")
    void unbindHostsFromSubstrate(@Param("substrateId") Long substrateId);

    @Query("MATCH (h:Host {name: $hostId})-[r:BELONG_TO_SUBSTRATE]->(s:SubstrateId) WHERE id(s) = $substrateId " +
    "DELETE r")
    void unbindHostFromSubstrate(@Param("substrateId") Long substrateId, @Param("hostId") String hostId);

    @Query("MATCH (h:Host)-[r:BELONG_TO_SUBSTRATE]->(s:SubstrateId) WHERE id(s) = $substrateId RETURN h")
    List<Host> getHostsBySubstrate(@Param("substrateId") Long substrateId);

    @Override
    @Query("MATCH (h:Host {name: $host.name})--(r) " +
    "DETACH DELETE h,r")
    void delete(@Param("host") Host host);
}
