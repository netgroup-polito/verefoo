package it.polito.verefoo.rest.spring.repository;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import it.polito.verefoo.DbSupportedVNFType;

@Repository
public interface SupportedVNFTypeRepository extends Neo4jRepository<DbSupportedVNFType, Long> {
    
}
