package it.polito.verefoo;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class DbVpnexit {

    @Id
    @GeneratedValue
    Long id;
    
    protected String vpnaccess;

    /**
     * Gets the value of the vpnaccess property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVpnaccess() {
        return vpnaccess;
    }

    /**
     * Sets the value of the vpnaccess property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVpnaccess(String value) {
        this.vpnaccess = value;
    }
}
