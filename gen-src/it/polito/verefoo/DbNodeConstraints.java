package it.polito.verefoo;

import java.util.ArrayList;
import java.util.List;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class DbNodeConstraints {

    @Id
    @GeneratedValue
    Long id;

    protected List<DbNodeMetrics> nodeMetrics;

    /**
     * Gets the value of the nodeMetrics property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the nodeMetrics property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNodeMetrics().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link NodeConstraints.NodeMetrics }
     * 
     * 
     */
    public List<DbNodeMetrics> getNodeMetrics() {
        if (nodeMetrics == null) {
            nodeMetrics = new ArrayList<DbNodeMetrics>();
        }
        return this.nodeMetrics;
    }
       
}
