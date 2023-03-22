package it.polito.verefoo;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

import it.polito.verefoo.jaxb.ActionTypes;
import it.polito.verefoo.jaxb.L4ProtocolTypes;

@NodeEntity
public class DbElements {

    @Id
    @GeneratedValue
    Long id;

    protected DbActionTypes action;

    protected String source;

    protected String destination;

    protected DbL4ProtocolTypes protocol;

    protected String srcPort;

    protected String dstPort;
    
    protected Boolean directional;

    /**
     * Gets the value of the action property.
     * 
     * @return
     *     possible object is
     *     {@link ActionTypes }
     *     
     */
    public DbActionTypes getAction() {
        return action;
    }

    /**
     * Sets the value of the action property.
     * 
     * @param value
     *     allowed object is
     *     {@link ActionTypes }
     *     
     */
    public void setAction(DbActionTypes value) {
        this.action = value;
    }

    /**
     * Gets the value of the source property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSource() {
        return source;
    }

    /**
     * Sets the value of the source property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSource(String value) {
        this.source = value;
    }

    /**
     * Gets the value of the destination property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDestination() {
        return destination;
    }

    /**
     * Sets the value of the destination property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDestination(String value) {
        this.destination = value;
    }

    /**
     * Gets the value of the protocol property.
     * 
     * @return
     *     possible object is
     *     {@link L4ProtocolTypes }
     *     
     */
    public DbL4ProtocolTypes getProtocol() {
        return protocol;
    }

    /**
     * Sets the value of the protocol property.
     * 
     * @param value
     *     allowed object is
     *     {@link L4ProtocolTypes }
     *     
     */
    public void setProtocol(DbL4ProtocolTypes value) {
        this.protocol = value;
    }

    /**
     * Gets the value of the srcPort property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSrcPort() {
        return srcPort;
    }

    /**
     * Sets the value of the srcPort property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSrcPort(String value) {
        this.srcPort = value;
    }

    /**
     * Gets the value of the dstPort property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDstPort() {
        return dstPort;
    }

    /**
     * Sets the value of the dstPort property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDstPort(String value) {
        this.dstPort = value;
    }

    /**
     * Gets the value of the directional property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isDirectional() {
        return directional;
    }

    /**
     * Sets the value of the directional property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDirectional(Boolean value) {
        this.directional = value;
    }

}
