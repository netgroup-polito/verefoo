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
    @Query("CYPHER 3.5 MATCH (h:DbHost)-[*]-(any) WHERE id(h)=$id " +
    "DETACH DELETE h, any")
    void deleteById(@Param("id") Long id);

    @Query("CYPHER 3.5 MATCH (h:DbHost {name: $name}) " +
    "RETURN h")
    Optional<DbHost> findByName(@Param("name") String name);

}
