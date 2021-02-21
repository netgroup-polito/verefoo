package it.polito.verefoo;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class DbWebclient {

    @Id
    @GeneratedValue
    Long id;
    
    protected String nameWebServer;

    /**
     * Gets the value of the nameWebServer property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNameWebServer() {
        return nameWebServer;
    }

    /**
     * Sets the value of the nameWebServer property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNameWebServer(String value) {
        this.nameWebServer = value;
    }
}
