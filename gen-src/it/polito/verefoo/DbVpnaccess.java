package it.polito.verefoo;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class DbVpnaccess {

    @Id
    @GeneratedValue
    Long id;
    
    protected String vpnexit;

    /**
     * Gets the value of the vpnexit property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVpnexit() {
        return vpnexit;
    }

    /**
     * Sets the value of the vpnexit property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVpnexit(String value) {
        this.vpnexit = value;
    }
}
