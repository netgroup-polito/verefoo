//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.11 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2019.07.31 alle 05:41:07 PM CEST 
//


package it.polito.verefoo.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per anonymous complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element ref="{}graphs"/&gt;
 *         &lt;element ref="{}Constraints" minOccurs="0"/&gt;
 *         &lt;element ref="{}PropertyDefinition"/&gt;
 *         &lt;element ref="{}Hosts" minOccurs="0"/&gt;
 *         &lt;element ref="{}Connections" minOccurs="0"/&gt;
 *         &lt;element ref="{}NetworkForwardingPaths" minOccurs="0"/&gt;
 *         &lt;element name="ParsingString" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "graphs",
    "constraints",
    "propertyDefinition",
    "hosts",
    "connections",
    "networkForwardingPaths",
    "parsingString"
})
@XmlRootElement(name = "NFV")
public class NFV {

    @XmlElement(required = true)
    protected Graphs graphs;
    @XmlElement(name = "Constraints")
    protected Constraints constraints;
    @XmlElement(name = "PropertyDefinition", required = true)
    protected PropertyDefinition propertyDefinition;
    @XmlElement(name = "Hosts")
    protected Hosts hosts;
    @XmlElement(name = "Connections")
    protected Connections connections;
    @XmlElement(name = "NetworkForwardingPaths")
    protected NetworkForwardingPaths networkForwardingPaths;
    @XmlElement(name = "ParsingString")
    protected String parsingString;

    /**
     * Recupera il valore della proprietà graphs.
     * 
     * @return
     *     possible object is
     *     {@link Graphs }
     *     
     */
    public Graphs getGraphs() {
        return graphs;
    }

    /**
     * Imposta il valore della proprietà graphs.
     * 
     * @param value
     *     allowed object is
     *     {@link Graphs }
     *     
     */
    public void setGraphs(Graphs value) {
        this.graphs = value;
    }

    /**
     * Recupera il valore della proprietà constraints.
     * 
     * @return
     *     possible object is
     *     {@link Constraints }
     *     
     */
    public Constraints getConstraints() {
        return constraints;
    }

    /**
     * Imposta il valore della proprietà constraints.
     * 
     * @param value
     *     allowed object is
     *     {@link Constraints }
     *     
     */
    public void setConstraints(Constraints value) {
        this.constraints = value;
    }

    /**
     * Recupera il valore della proprietà propertyDefinition.
     * 
     * @return
     *     possible object is
     *     {@link PropertyDefinition }
     *     
     */
    public PropertyDefinition getPropertyDefinition() {
        return propertyDefinition;
    }

    /**
     * Imposta il valore della proprietà propertyDefinition.
     * 
     * @param value
     *     allowed object is
     *     {@link PropertyDefinition }
     *     
     */
    public void setPropertyDefinition(PropertyDefinition value) {
        this.propertyDefinition = value;
    }

    /**
     * Recupera il valore della proprietà hosts.
     * 
     * @return
     *     possible object is
     *     {@link Hosts }
     *     
     */
    public Hosts getHosts() {
        return hosts;
    }

    /**
     * Imposta il valore della proprietà hosts.
     * 
     * @param value
     *     allowed object is
     *     {@link Hosts }
     *     
     */
    public void setHosts(Hosts value) {
        this.hosts = value;
    }

    /**
     * Recupera il valore della proprietà connections.
     * 
     * @return
     *     possible object is
     *     {@link Connections }
     *     
     */
    public Connections getConnections() {
        return connections;
    }

    /**
     * Imposta il valore della proprietà connections.
     * 
     * @param value
     *     allowed object is
     *     {@link Connections }
     *     
     */
    public void setConnections(Connections value) {
        this.connections = value;
    }

    /**
     * Recupera il valore della proprietà networkForwardingPaths.
     * 
     * @return
     *     possible object is
     *     {@link NetworkForwardingPaths }
     *     
     */
    public NetworkForwardingPaths getNetworkForwardingPaths() {
        return networkForwardingPaths;
    }

    /**
     * Imposta il valore della proprietà networkForwardingPaths.
     * 
     * @param value
     *     allowed object is
     *     {@link NetworkForwardingPaths }
     *     
     */
    public void setNetworkForwardingPaths(NetworkForwardingPaths value) {
        this.networkForwardingPaths = value;
    }

    /**
     * Recupera il valore della proprietà parsingString.
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
     * Imposta il valore della proprietà parsingString.
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
