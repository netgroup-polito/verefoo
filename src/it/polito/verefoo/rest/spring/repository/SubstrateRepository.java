package it.polito.verefoo.rest.spring.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import it.polito.verefoo.SubstrateId;

@Repository
public interface SubstrateRepository extends Neo4jRepository<SubstrateId, Long> {

}
