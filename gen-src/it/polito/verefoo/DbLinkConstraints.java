package it.polito.verefoo;

import java.util.ArrayList;
import java.util.List;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class DbLinkConstraints {

    @Id
    @GeneratedValue
    Long id;

    protected List<DbLinkMetrics> linkMetrics;

    /**
     * Gets the value of the linkMetrics property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the linkMetrics property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLinkMetrics().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link LinkConstraints.LinkMetrics }
     * 
     * 
     */
    public List<DbLinkMetrics> getLinkMetrics() {
        if (linkMetrics == null) {
            linkMetrics = new ArrayList<DbLinkMetrics>();
        }
        return this.linkMetrics;
    }
}
