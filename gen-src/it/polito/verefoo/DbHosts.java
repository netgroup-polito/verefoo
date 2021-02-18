package it.polito.verefoo;

import java.util.ArrayList;
import java.util.List;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class DbHosts {
    
    /**
     * The substrate's id
     */
    @Id
    @GeneratedValue
    Long id;

    List<DbHost> host;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<DbHost> getHost() {
        if (host == null) {
            host = new ArrayList<DbHost>();
        }
        return this.host;
    }

}
