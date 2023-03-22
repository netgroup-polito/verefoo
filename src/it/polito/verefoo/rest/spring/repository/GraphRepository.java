package it.polito.verefoo.rest.spring.repository;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import it.polito.verefoo.DbGraph;

@Repository
public interface GraphRepository extends Neo4jRepository<DbGraph, Long> {



    /**
     * Use this method in conjunction with {@code isAnyReferred} to enforce foreign key integrity
     */
    @Override
    @Query("CYPHER 3.5 MATCH tmp = (s:DbGraph)-[*]-(n) " + 
    "DETACH DELETE s, n")
    void deleteAll();


    
    /**
     * Check if it exists at least one graph which is referred
     * @param id
     * @return
     */
    @Query("optional match (g:DbGraph)-[r1:PROPERTY_TO_GRAPH|CONSTRAINTS_TO_GRAPH]-() " +
    "WITH r1 " +
    "optional match (g:DbGraph)-[:NODE]-(n:DbNode)-[r2:HOST_TO_NODE]-() " +
    "with r1 is not null or r2 is not null as res " +
    "return res " +
    "limit 1 ")
    Boolean isAnyReferred();



    /**
     * Use this method in conjunction with {@code isReferred} to enforce foreign key integrity
     */
    @Override
    @Query("CYPHER 3.5 MATCH tmp = (s:DbGraph)-[*]-(n) WHERE id(s)=$id " +
    "DETACH DELETE s, n")
    void deleteById(@Param("id") Long id);



    @Query("optional match (g:DbGraph)-[r1:PROPERTY_TO_GRAPH|CONSTRAINTS_TO_GRAPH]-() WHERE id(g)=$id " +
    "WITH r1 " +
    "optional match (g:DbGraph)-[:NODE]-(n:DbNode)-[r2:HOST_TO_NODE]-() WHERE id(g)=$id " +
    "with r1 is not null or r2 is not null as res " +
    "return res " +
    "limit 1")
    Boolean isReferred(@Param("id") Long id);



    @Query("CYPHER 3.5 MATCH (d:DbGraph) WHERE id(d)=$id " +
    "WITH d " +
    "MATCH (n:DbNode) WHERE id(n)=$nodeId " +
    "MERGE (d)-[:NODE]->(n)")
    void bindNode(@Param("id") Long id, @Param("nodeId") Long nodeId);
    


    @Query("CYPHER 3.5 " +
    "MATCH (d:DbGraph)-[r:NODE]->(n:DbNode) WHERE id(d)=$id AND id(n)=$nodeId " +
    "DELETE r")
    void unbindNode(@Param("id") Long id, @Param("nodeId") Long nodeId);
    


}
