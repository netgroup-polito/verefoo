//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.8-b130911.1802 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2018.04.21 alle 10:13:57 AM CEST 
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
 *         &lt;element ref="{}NodeConstraints"/>
 *         &lt;element ref="{}BandwidthConstraints"/>
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
    "nodeConstraints",
    "bandwidthConstraints"
})
@XmlRootElement(name = "Constraints")
public class Constraints {

    @XmlElement(name = "NodeConstraints", required = true)
    protected NodeConstraints nodeConstraints;
    @XmlElement(name = "BandwidthConstraints", required = true)
    protected BandwidthConstraints bandwidthConstraints;

    /**
     * Recupera il valore della proprietà nodeConstraints.
     * 
     * @return
     *     possible object is
     *     {@link NodeConstraints }
     *     
     */
    public NodeConstraints getNodeConstraints() {
        return nodeConstraints;
    }

    /**
     * Imposta il valore della proprietà nodeConstraints.
     * 
     * @param value
     *     allowed object is
     *     {@link NodeConstraints }
     *     
     */
    public void setNodeConstraints(NodeConstraints value) {
        this.nodeConstraints = value;
    }

    /**
     * Recupera il valore della proprietà bandwidthConstraints.
     * 
     * @return
     *     possible object is
     *     {@link BandwidthConstraints }
     *     
     */
    public BandwidthConstraints getBandwidthConstraints() {
        return bandwidthConstraints;
    }

    /**
     * Imposta il valore della proprietà bandwidthConstraints.
     * 
     * @param value
     *     allowed object is
     *     {@link BandwidthConstraints }
     *     
     */
    public void setBandwidthConstraints(BandwidthConstraints value) {
        this.bandwidthConstraints = value;
    }

}
