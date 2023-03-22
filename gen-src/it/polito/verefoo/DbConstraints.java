package it.polito.verefoo;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class DbConstraints {

    @Id
    @GeneratedValue
    private Long id;

    private Long graph;

    protected DbNodeConstraints nodeConstraints;

    protected DbLinkConstraints linkConstraints;

    protected DbAllocationConstraints allocationConstraints;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGraph() {
        return graph;
    }

    public void setGraph(Long graph) {
        this.graph = graph;
    }

    /**
     * Gets the value of the nodeConstraints property.
     * 
     * @return
     *     possible object is
     *     {@link NodeConstraints }
     *     
     */
    public DbNodeConstraints getNodeConstraints() {
        return nodeConstraints;
    }

    /**
     * Sets the value of the nodeConstraints property.
     * 
     * @param value
     *     allowed object is
     *     {@link NodeConstraints }
     *     
     */
    public void setNodeConstraints(DbNodeConstraints value) {
        this.nodeConstraints = value;
    }

    /**
     * Gets the value of the linkConstraints property.
     * 
     * @return
     *     possible object is
     *     {@link LinkConstraints }
     *     
     */
    public DbLinkConstraints getLinkConstraints() {
        return linkConstraints;
    }

    /**
     * Sets the value of the linkConstraints property.
     * 
     * @param value
     *     allowed object is
     *     {@link LinkConstraints }
     *     
     */
    public void setLinkConstraints(DbLinkConstraints value) {
        this.linkConstraints = value;
    }

    /**
     * Gets the value of the allocationConstraints property.
     * 
     * @return
     *     possible object is
     *     {@link AllocationConstraints }
     *     
     */
    public DbAllocationConstraints getAllocationConstraints() {
        return allocationConstraints;
    }

    /**
     * Sets the value of the allocationConstraints property.
     * 
     * @param value
     *     allowed object is
     *     {@link AllocationConstraints }
     *     
     */
    public void setAllocationConstraints(DbAllocationConstraints value) {
        this.allocationConstraints = value;
    }
}