package it.polito.verefoo.rest.spring.repository;

import java.util.List;

import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import it.polito.verefoo.DbConnection;

@Repository
public interface ConnectionRepository extends Neo4jRepository<DbConnection, Long> {
    
    @Query("MATCH (substrate:DbHosts)-[HOST]->(source:DbHost)-[r1:DBCONNECTION]-(dest:DbHost) WHERE id(substrate) = $substrateId " +
    "DELETE r1")
    void deleteAllConnectionsBySubstrateId(@Param("substrateId") Long substrateId);

    @Query("MATCH (substrate:DbHosts)-[HOST]->(source:DbHost)-[r1:DBCONNECTION]-(dest:DbHost) WHERE id(substrate) = $substrateId " +
    "WITH DISTINCT r1 " +
    "RETURN r1")
    List<DbConnection> findAllConnectionsBySubstrateId(@Param("substrateId") Long substrateId);

}
