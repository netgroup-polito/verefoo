package it.polito.verefoo;

import java.util.ArrayList;
import java.util.List;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class DbAllocationConstraints {

    @Id
    @GeneratedValue
    Long id;

    protected List<DbAllocationConstraint> allocationConstraint;

    /**
     * Gets the value of the allocationConstraint property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the allocationConstraint property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAllocationConstraint().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AllocationConstraints.AllocationConstraint }
     * 
     * 
     */
    public List<DbAllocationConstraint> getAllocationConstraint() {
        if (allocationConstraint == null) {
            allocationConstraint = new ArrayList<DbAllocationConstraint>();
        }
        return this.allocationConstraint;
    }

}
