package it.polito.verefoo;

import java.util.ArrayList;
import java.util.List;

import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;

@NodeEntity
public class DbNFV {

    @Id
    @GeneratedValue
    Long id;
    
    List<Long> graphs;

    Long propertyDefinition;

    Long substrate;

    DbNetworkForwardingPaths networkForwardingPaths;

    String parsingString;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Long> getGraph() {
        if (graphs == null) {
            this.graphs = new ArrayList<Long>();
        }
        return this.graphs;
    }

    public Long getSubstrate() {
        return substrate;
    }

    public void setSubstrate(Long substrate) {
        this.substrate = substrate;
    }

    public Long getPropertyDefinition() {
        return propertyDefinition;
    }

    public void setPropertyDefinition(Long propertyDefinition) {
        this.propertyDefinition = propertyDefinition;
    }

    /**
     * Gets the value of the networkForwardingPaths property.
     * 
     * @return
     *     possible object is
     *     {@link NetworkForwardingPathsRepository }
     *     
     */
    public DbNetworkForwardingPaths getNetworkForwardingPaths() {
        return networkForwardingPaths;
    }

    /**
     * Sets the value of the networkForwardingPaths property.
     * 
     * @param value
     *     allowed object is
     *     {@link NetworkForwardingPathsRepository }
     *     
     */
    public void setNetworkForwardingPaths(DbNetworkForwardingPaths value) {
        this.networkForwardingPaths = value;
    }

    /**
     * Gets the value of the parsingString property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getParsingString() {
        return parsingString;
    }

    /**
     * Sets the value of the parsingString property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setParsingString(String value) {
        this.parsingString = value;
    }

}
