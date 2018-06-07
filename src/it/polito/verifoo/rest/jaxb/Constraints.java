//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.8-b130911.1802 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2018.06.06 alle 11:59:55 AM CEST 
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
 *         &lt;element ref="{}LinkConstraints"/>
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
    "linkConstraints"
})
@XmlRootElement(name = "Constraints")
public class Constraints {

    @XmlElement(name = "NodeConstraints", required = true)
    protected NodeConstraints nodeConstraints;
    @XmlElement(name = "LinkConstraints", required = true)
    protected LinkConstraints linkConstraints;

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

}
