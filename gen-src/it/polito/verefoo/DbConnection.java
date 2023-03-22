package it.polito.verefoo;

import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

@RelationshipEntity
public class DbConnection {

    @Id
    @GeneratedValue
    Long id;

    @StartNode
    DbHost source;

    @EndNode
    DbHost dest;
    
    protected String sourceHost;

    protected String destHost;

    protected Integer avgLatency;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DbHost getSource() {
        return source;
    }

    public void setSource(DbHost source) {
        this.source = source;
    }

    public DbHost getDest() {
        return dest;
    }

    public void setDest(DbHost dest) {
        this.dest = dest;
    }

    /**
     * Gets the value of the sourceHost property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSourceHost() {
        return sourceHost;
    }

    /**
     * Sets the value of the sourceHost property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSourceHost(String value) {
        this.sourceHost = value;
    }

    /**
     * Gets the value of the destHost property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDestHost() {
        return destHost;
    }

    /**
     * Sets the value of the destHost property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDestHost(String value) {
        this.destHost = value;
    }

    /**
     * Gets the value of the avgLatency property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getAvgLatency() {
        return avgLatency;
    }

    /**
     * Sets the value of the avgLatency property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setAvgLatency(Integer value) {
        this.avgLatency = value;
    }

}
