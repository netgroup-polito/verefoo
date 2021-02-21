package it.polito.verefoo.rest.spring.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import it.polito.verefoo.DbNeighbour;

@Repository
public interface NeighbourRepository extends Neo4jRepository<DbNeighbour, Long> {
    
}
