package it.polito.verefoo;

import java.util.ArrayList;
import java.util.List;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class DbGraph {

    @Id
    @GeneratedValue
    private Long id;

    Boolean serviceGraph;

    List<DbNode> node;

    public DbGraph() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getServiceGraph() {
        return serviceGraph;
    }

    public void setServiceGraph(Boolean serviceGraph) {
        this.serviceGraph = serviceGraph;
    }

    public List<DbNode> getNode() {
        if (node == null) {
            node = new ArrayList<DbNode>();
        }
        return this.node;
    }
}
