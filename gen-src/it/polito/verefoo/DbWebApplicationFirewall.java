package it.polito.verefoo;

import java.util.ArrayList;
import java.util.List;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class DbWebApplicationFirewall {

    @Id
    @GeneratedValue
    Long id;

    protected List<DbWafElements> wafElements;
    
    protected DbActionTypes defaultAction;

    /**
     * Gets the value of the wafElements property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the wafElements property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getWafElements().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link WafElements }
     * 
     * 
     */
    public List<DbWafElements> getWafElements() {
        if (wafElements == null) {
            wafElements = new ArrayList<DbWafElements>();
        }
        return this.wafElements;
    }

    /**
     * Gets the value of the defaultAction property.
     * 
     * @return
     *     possible object is
     *     {@link ActionTypes }
     *     
     */
    public DbActionTypes getDefaultAction() {
        return defaultAction;
    }

    /**
     * Sets the value of the defaultAction property.
     * 
     * @param value
     *     allowed object is
     *     {@link ActionTypes }
     *     
     */
    public void setDefaultAction(DbActionTypes value) {
        this.defaultAction = value;
    }
}
