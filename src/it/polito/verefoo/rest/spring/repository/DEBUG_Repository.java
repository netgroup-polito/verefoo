package it.polito.verefoo.rest.spring.repository;

import java.util.List;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import it.polito.verefoo.jaxb.Host;

@Repository
public interface DEBUG_Repository extends Neo4jRepository<Host, Long> {

    @Query("Match (n) RETURN n")
    List<Object> DEBUG_getAllNodes();

    @Query("MATCH (n) " +
    "DETACH DELETE (n)")
    void DEBUG_cleanDb();
}