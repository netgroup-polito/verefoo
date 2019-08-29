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
 *         &lt;element ref="{}NodeConstraints"/&gt;
 *         &lt;element ref="{}LinkConstraints"/&gt;
 *         &lt;element ref="{}AllocationConstraints" minOccurs="0"/&gt;
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
    "nodeConstraints",
    "linkConstraints",
    "allocationConstraints"
})
@XmlRootElement(name = "Constraints")
public class Constraints {

    @XmlElement(name = "NodeConstraints", required = true)
    protected NodeConstraints nodeConstraints;
    @XmlElement(name = "LinkConstraints", required = true)
    protected LinkConstraints linkConstraints;
    @XmlElement(name = "AllocationConstraints")
    protected AllocationConstraints allocationConstraints;

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
     * Recupera il valore della proprietà linkConstraints.
     * 
     * @return
     *     possible object is
     *     {@link LinkConstraints }
     *     
     */
    public LinkConstraints getLinkConstraints() {
        return linkConstraints;
    }

    /**
     * Imposta il valore della proprietà linkConstraints.
     * 
     * @param value
     *     allowed object is
     *     {@link LinkConstraints }
     *     
     */
    public void setLinkConstraints(LinkConstraints value) {
        this.linkConstraints = value;
    }

    /**
     * Recupera il valore della proprietà allocationConstraints.
     * 
     * @return
     *     possible object is
     *     {@link AllocationConstraints }
     *     
     */
    public AllocationConstraints getAllocationConstraints() {
        return allocationConstraints;
    }

    /**
     * Imposta il valore della proprietà allocationConstraints.
     * 
     * @param value
     *     allowed object is
     *     {@link AllocationConstraints }
     *     
     */
    public void setAllocationConstraints(AllocationConstraints value) {
        this.allocationConstraints = value;
    }

}
