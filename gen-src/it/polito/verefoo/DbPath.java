package it.polito.verefoo;

import java.util.ArrayList;
import java.util.List;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class DbPath {

    @Id
    @GeneratedValue
    Long id;

    protected List<DbPathNode> pathNode;

    /**
     * Gets the value of the pathNode property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the pathNode property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPathNode().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Path.PathNode }
     * 
     * 
     */
    public List<DbPathNode> getPathNode() {
        if (pathNode == null) {
            pathNode = new ArrayList<DbPathNode>();
        }
        return this.pathNode;
    }

    /**
     * Gets the value of the id property.
     * 
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     */
    public void setId(Long value) {
        this.id = value;
    }

}
