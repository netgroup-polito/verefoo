package it.polito.verefoo;

import java.util.ArrayList;
import java.util.List;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

import it.polito.verefoo.jaxb.ActionTypes;
import it.polito.verefoo.jaxb.Elements;

@NodeEntity
public class DbFirewall {

    @Id
    @GeneratedValue
    Long id;

    protected List<DbElements> elements;

    protected DbActionTypes defaultAction;

    /**
     * Gets the value of the elements property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the elements property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getElements().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Elements }
     * 
     * 
     */
    public List<DbElements> getElements() {
        if (elements == null) {
            elements = new ArrayList<DbElements>();
        }
        return this.elements;
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
