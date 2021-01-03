package it.polito.verefoo.rest.spring.repository;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import it.polito.verefoo.SubstrateId;

@Repository
public interface SubstrateRepository extends Neo4jRepository<SubstrateId, Long> {
    
    @Override
    @Query("MATCH (s:SubstrateId) WHERE s.id = $id RETURN count(s) = 1")
    boolean existsById(@Param("id") Long id);

    @Override
    @Query("Merge (s:SubstrateId {id: $substrateId.id}) " +
    "return s")
    SubstrateId save(@Param("substrateId") SubstrateId substrateId);
}
