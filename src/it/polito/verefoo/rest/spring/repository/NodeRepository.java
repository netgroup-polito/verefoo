package it.polito.verefoo.rest.spring.repository;

import java.util.Optional;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import it.polito.verefoo.DbConfiguration;
import it.polito.verefoo.DbNode;

@Repository
public interface NodeRepository extends Neo4jRepository<DbNode, Long> {
    


    @Query("optional match (n:DbNode)-[r:HOST_TO_NODE]-() WHERE id(n)=$id " +
    "return r is not null")
    Boolean isReferred(@Param("id") Long id);



    /**
     * Use this method in conjunction with {@code isReferred} to enforce foreign key integrity
     */
    @Override
    @Query("CYPHER 3.5 MATCH (n:DbNode)-[*]-(a) WHERE id(n)=$id " +
    "DETACH DELETE n, a")
    void deleteById(@Param("id") Long id);



    @Query("CYPHER 3.5 MATCH (no:DbNode) WHERE id(no)=$id " +
    "WITH no " +
    "MATCH (ne:DbNeighbour) WHERE id(ne)=$neighbourId " +
    "MERGE (no)-[:NEIGHBOUR]->(ne)")
    void bindNeighbour(@Param("id") Long id, @Param("neighbourId") Long neighbourId);



    @Query("CYPHER 3.5 " +
    "MATCH (no:DbNode)-[r:NEIGHBOUR]->(ne:DbNeighbour) WHERE id(no)=$id AND id(ne)=$neighbourId " +
    "DELETE r")
    void unbindNeighbour(@Param("id") Long id, @Param("neighbourId") Long neighbourId);



    @Query("CYPHER 3.5 MATCH (n:DbNode)-[:CONFIGURATION]->(c) WHERE id(n)=$id " +
    "WITH c " +
    "MATCH (c)-[*]->(any) " +
    "RETURN (c)-[*]->(any)")
    DbConfiguration findConfiguration(@Param("id") Long id);



    @Query("CYPHER 3.5 " +
    "MATCH (n:DbNode)-[r:CONFIGURATION]->(c) WHERE id(n)=$id " +
    "DELETE r")
    void unbindConfiguration(@Param("id") Long id);



    @Query("CYPHER 3.5 MATCH (n:DbNode) WHERE id(n)=$id " +
    "WITH n " +
    "MATCH (c:DbConfiguration) WHERE id(c)=$configurationId " +
    "MERGE (n)-[:CONFIGURATION]->(c)")
    void bindConfiguration(@Param("id") Long id, @Param("configurationId") Long configurationId);



}
