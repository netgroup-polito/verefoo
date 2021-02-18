package it.polito.verefoo;

import java.util.ArrayList;
import java.util.List;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;


@NodeEntity
public class DbNode {

    @Id
    @GeneratedValue
    protected Long id;

    protected String name;

    protected List<DbNeighbour> neighbour;

    protected DbConfiguration configuration;
    
    protected DbFunctionalTypes functionalType;


    public DbNode() {}

    public List<DbNeighbour> getNeighbour() {
        if (neighbour == null) {
            neighbour = new ArrayList<DbNeighbour>();
        }
        return this.neighbour;
    }

    public DbConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(DbConfiguration value) {
        this.configuration = value;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long value) {
        this.id = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String value) {
        this.name = value;
    }

    public DbFunctionalTypes getFunctionalType() {
        return functionalType;
    }

    public void setFunctionalType(DbFunctionalTypes value) {
        this.functionalType = value;
    }

}

