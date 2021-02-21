package it.polito.verefoo.rest.spring.repository;

import java.util.Optional;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import it.polito.verefoo.DbHost;

@Repository
public interface HostRepository extends Neo4jRepository<DbHost, Long> {
    


    /**
     * The super method just deletes the node labeled with {@code DbHost} and with the
     * given id, while its neighbours remain stored (no cascade).
     */
    @Override
    @Query("CYPHER 3.5 MATCH tmp = (h:DbHost)-[*]-(any) WHERE id(h)=$id " +

    // Neglect the foreign-key relationship
    "WITH *, relationships(tmp) as rels " +
    "WHERE NONE( rel in rels WHERE type(rel)='HOST_TO_NODE') " +

    "DETACH DELETE h, any")
    void deleteById(@Param("id") Long id);



    @Query("CYPHER 3.5 MATCH (h:DbHost {name: $name})-[*]->(any) " +
    "RETURN (h)-[*]->(any)")
    Optional<DbHost> findByName(@Param("name") String name);



    @Query("CYPHER 3.5 MATCH (h:DbHost) WHERE id(h)=$id " +
    "WITH h " +
    "MATCH (n:DbNodeRefType) WHERE id(n)=$nodeRefTypeId " +
    "WITH h, n " +
    "MERGE (h)-[:NODE_REF]->(n)")
    void bindNodeRefType(@Param("id") Long id, @Param("nodeRefTypeId") Long nodeRefTypeId);



    @Query("CYPHER 3.5 MATCH (h:DbHost) WHERE id(h)=$id " +
    "WITH h " +
    "MATCH (s:DbSupportedVNFType) WHERE id(s)=$supportedVNFTypeId " +
    "WITH h, s " +
    "MERGE (h)-[:SUPPORTED_V_N_F]->(s)")
    void bindSupportedVNFType(@Param("id") Long id, @Param("supportedVNFTypeId") Long supportedVNFTypeId);

    

}
