package it.polito.verefoo.rest.spring.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import it.polito.verefoo.DbGraph;

@Repository
public interface GraphRepository extends Neo4jRepository<DbGraph, Long> {



    @Override
    @Query("CYPHER 3.5 MATCH (s:DbGraph)-[*]-(n) " + "RETURN (s)-[*]-(n)")
    List<DbGraph> findAll();



    /**
     * The super method just deletes all nodes labeled with {@code DbGraph}, while
     * their neighbours remain stored (no cascade).
     */
    @Override
    @Query("CYPHER 3.5 MATCH tmp = (s:DbGraph)-[*]-(n) " +

    // Neglect the foreign-key relationship
    "WITH *, relationships(tmp) as rels " +
    "WHERE NONE( rel in rels WHERE type(rel)='PROPERTY_TO_GRAPH') " +
    
    "DETACH DELETE s, n")
    void deleteAll();


    
    /**
     * Check if it exists at least one graph which is referred
     * @param id
     * @return
     */
    @Query("optional match (g:DbGraph)-[r:PROPERTY_TO_GRAPH|CONSTRAINTS_TO_GRAPH]-() " +
    "return case when r is null then false else true end")
    Boolean isAnyReferred();



    /**
     * The super method just deletes the node labeled with DbGraph and with the
     * given id, while its neighbours remain stored (no cascade).
     */
    @Override
    @Query("CYPHER 3.5 MATCH tmp = (s:DbGraph)-[*]-(n) WHERE id(s)=$id " +

    // Neglect the foreign-key relationship
    // "WITH *, relationships(tmp) as rels " +
    // "WHERE NONE( rel in rels WHERE type(rel)='PROPERTY_TO_GRAPH') " +

    "DETACH DELETE s, n")
    void deleteById(@Param("id") Long id);



    @Query("optional match (g:DbGraph)-[r:PROPERTY_TO_GRAPH|CONSTRAINTS_TO_GRAPH]-() WHERE id(g)=$id " +
    "return case when r is null then false else true end")
    Boolean isReferred(@Param("id") Long id);



    @Override
    @Query("CYPHER 3.5 MATCH (s:DbGraph)-[*]-(n) WHERE id(s)=$id " +
    "RETURN (s:DbGraph)-[*]-(n)")
    Optional<DbGraph> findById(@Param("id") Long id);



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
