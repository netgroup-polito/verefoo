package it.polito.verefoo.rest.spring.repository;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import it.polito.verefoo.DbNodeRefType;

@Repository
public interface NodeRefTypeRepository extends Neo4jRepository<DbNodeRefType, Long> {
    


    @Query("CYPHER 3.5 MATCH (nodeRef:DbNodeRefType) WHERE id(nodeRef)=$id " +
    "WITH nodeRef " +
    // OPTIONAL will cause g to be null if the referred graph doesn't exist;
    // then, since g is null, the merge will throw an exception
    "OPTIONAL MATCH (node:DbNode {name: nodeRef.node}) " +
    "WITH nodeRef, node " +
    "MERGE (nodeRef)-[:HOST_TO_NODE]->(node)")
    void bindToGraph(@Param("id") Long id);



    @Query("CYPHER 3.5 MATCH (nodeRef:DbNodeRefType)-[r:HOST_TO_NODE]->(node:DbNode {name: nodeRef.node}) WHERE id(nodeRef)=$id " +
    "DELETE r")
    void unbindFromGraph(@Param("id") Long id);



}
