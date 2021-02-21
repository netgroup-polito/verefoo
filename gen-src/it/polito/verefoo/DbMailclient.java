package it.polito.verefoo;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class DbMailclient {

    @Id
    @GeneratedValue
    Long id;
    
    protected String mailserver;

    /**
     * Gets the value of the mailserver property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMailserver() {
        return mailserver;
    }

    /**
     * Sets the value of the mailserver property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMailserver(String value) {
        this.mailserver = value;
    }
}
