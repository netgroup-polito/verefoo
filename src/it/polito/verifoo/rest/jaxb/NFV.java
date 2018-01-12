//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.8-b130911.1802 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2018.01.12 alle 07:11:27 PM CET 
//


package it.polito.verifoo.rest.jaxb;

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
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}graphs"/>
 *         &lt;element ref="{}CapacityDefinition"/>
 *         &lt;element ref="{}PropertyDefinition"/>
 *         &lt;element ref="{}Hosts"/>
 *         &lt;element ref="{}Connections"/>
 *         &lt;element name="ParsingString" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "graphs",
    "capacityDefinition",
    "propertyDefinition",
    "hosts",
    "connections",
    "parsingString"
})
@XmlRootElement(name = "NFV")
public class NFV {

    @XmlElement(required = true)
    protected Graphs graphs;
    @XmlElement(name = "CapacityDefinition", required = true)
    protected CapacityDefinition capacityDefinition;
    @XmlElement(name = "PropertyDefinition", required = true)
    protected PropertyDefinition propertyDefinition;
    @XmlElement(name = "Hosts", required = true)
    protected Hosts hosts;
    @XmlElement(name = "Connections", required = true)
    protected Connections connections;
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
     * Recupera il valore della proprietà capacityDefinition.
     * 
     * @return
     *     possible object is
     *     {@link CapacityDefinition }
     *     
     */
    public CapacityDefinition getCapacityDefinition() {
        return capacityDefinition;
    }

    /**
     * Imposta il valore della proprietà capacityDefinition.
     * 
     * @param value
     *     allowed object is
     *     {@link CapacityDefinition }
     *     
     */
    public void setCapacityDefinition(CapacityDefinition value) {
        this.capacityDefinition = value;
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
