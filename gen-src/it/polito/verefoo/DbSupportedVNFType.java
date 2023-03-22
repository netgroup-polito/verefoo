package it.polito.verefoo;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class DbSupportedVNFType {

    @Id
    @GeneratedValue
    Long id;

    protected DbFunctionalTypes functionalType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Gets the value of the functionalType property.
     * 
     * @return
     *     possible object is
     *     {@link FunctionalTypes }
     *     
     */
    public DbFunctionalTypes getFunctionalType() {
        return functionalType;
    }

    /**
     * Sets the value of the functionalType property.
     * 
     * @param value
     *     allowed object is
     *     {@link FunctionalTypes }
     *     
     */
    public void setFunctionalType(DbFunctionalTypes value) {
        this.functionalType = value;
    }
}
