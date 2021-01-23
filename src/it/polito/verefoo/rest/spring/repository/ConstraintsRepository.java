package it.polito.verefoo.rest.spring.repository;

import java.util.Optional;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import it.polito.verefoo.DbConstraints;

@Repository
public interface ConstraintsRepository extends Neo4jRepository<DbConstraints, Long> {

    @Query("CYPHER 3.5 MATCH (c:DbConstraints)-[*]->(any) WHERE c.graph=$id " +
    "DETACH DELETE c, any")
	void deleteByGraphId(@Param("id") Long id);

    @Query("CYPHER 3.5 MATCH (c:DbConstraints)-[*]->(any) WHERE c.graph=$id " +
    "RETURN (c)-[*]->(any)")
	Optional<DbConstraints> findByGraphId(Long id);
    
}
