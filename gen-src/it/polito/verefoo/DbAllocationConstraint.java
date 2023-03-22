package it.polito.verefoo;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class DbAllocationConstraint {

    @Id
    @GeneratedValue
    Long id;

    protected DbAllocationConstraintType type;

    protected String nodeA;

    protected String nodeB;

    /**
     * Gets the value of the type property.
     * 
     * @return possible object is {@link AllocationConstraintType }
     * 
     */
    public DbAllocationConstraintType getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value allowed object is {@link AllocationConstraintType }
     * 
     */
    public void setType(DbAllocationConstraintType value) {
        this.type = value;
    }

    /**
     * Gets the value of the nodeA property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getNodeA() {
        return nodeA;
    }

    /**
     * Sets the value of the nodeA property.
     * 
     * @param value allowed object is {@link String }
     * 
     */
    public void setNodeA(String value) {
        this.nodeA = value;
    }

    /**
     * Gets the value of the nodeB property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public String getNodeB() {
        return nodeB;
    }

    /**
     * Sets the value of the nodeB property.
     * 
     * @param value allowed object is {@link String }
     * 
     */
    public void setNodeB(String value) {
        this.nodeB = value;
    }

}
