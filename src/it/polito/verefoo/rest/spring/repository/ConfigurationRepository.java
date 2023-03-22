package it.polito.verefoo.rest.spring.repository;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import it.polito.verefoo.DbConfiguration;
import it.polito.verefoo.DbFunctionalTypes;

@Repository
public interface ConfigurationRepository extends Neo4jRepository<DbConfiguration, Long> {
    
    @Override
    @Query("CYPHER 3.5 MATCH (c:DbConfiguration)-[*]->(any) WHERE id(c)=$id " +
    "DETACH DELETE c, any")
    void deleteById(@Param("id") Long id);


    
    /**
     * Works as deleteById, but erases only the functions
     * @param id
     */
    @Query("CYPHER 3.5 MATCH (c:DbConfiguration)-[*]->(any) WHERE id(c)=$id " +
    "DETACH DELETE any")
    void deleteFunctionsById(@Param("id") Long id);

    

    @Query("CYPHER 3.5 MATCH (n:DbNode) WHERE id(n)=$nodeId " +
    "SET n.functionalType = $functionalType")
    void updateNodeFunctionalType(@Param("nodeId") Long nodeId, @Param("functionalType") DbFunctionalTypes functionalType);

}
