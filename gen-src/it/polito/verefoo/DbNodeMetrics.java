package it.polito.verefoo;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class DbNodeMetrics {

    @Id
    @GeneratedValue
    Long id;

    protected String node;

    protected Long nrOfOperations;

    protected Integer maxNodeLatency;

    protected Integer reqStorage;

    protected Integer cores;

    protected Integer memory;

    protected Boolean optional;

    /**
     * Gets the value of the node property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNode() {
        return node;
    }

    /**
     * Sets the value of the node property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNode(String value) {
        this.node = value;
    }

    /**
     * Gets the value of the nrOfOperations property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getNrOfOperations() {
        return nrOfOperations;
    }

    /**
     * Sets the value of the nrOfOperations property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setNrOfOperations(Long value) {
        this.nrOfOperations = value;
    }

    /**
     * Gets the value of the maxNodeLatency property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMaxNodeLatency() {
        return maxNodeLatency;
    }

    /**
     * Sets the value of the maxNodeLatency property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMaxNodeLatency(Integer value) {
        this.maxNodeLatency = value;
    }

    /**
     * Gets the value of the reqStorage property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public int getReqStorage() {
        if (reqStorage == null) {
            return  0;
        } else {
            return reqStorage;
        }
    }

    /**
     * Sets the value of the reqStorage property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setReqStorage(Integer value) {
        this.reqStorage = value;
    }

    /**
     * Gets the value of the cores property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public int getCores() {
        if (cores == null) {
            return  0;
        } else {
            return cores;
        }
    }

    /**
     * Sets the value of the cores property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setCores(Integer value) {
        this.cores = value;
    }

    /**
     * Gets the value of the memory property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public int getMemory() {
        if (memory == null) {
            return  0;
        } else {
            return memory;
        }
    }

    /**
     * Sets the value of the memory property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMemory(Integer value) {
        this.memory = value;
    }

    /**
     * Gets the value of the optional property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isOptional() {
        if (optional == null) {
            return false;
        } else {
            return optional;
        }
    }

    /**
     * Sets the value of the optional property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setOptional(Boolean value) {
        this.optional = value;
    }
}
