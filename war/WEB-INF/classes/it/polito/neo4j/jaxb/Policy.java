//
// Questo file � stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.8-b130911.1802 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andr� persa durante la ricompilazione dello schema di origine. 
// Generato il: 2017.03.01 alle 04:27:21 PM CET 
//


package it.polito.neo4j.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
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
 *         &lt;element name="result" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *       &lt;attribute name="source" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="middlebox" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="destination" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="type" use="required" type="{}policyTypes" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "result"
})
@XmlRootElement(name = "policy")
public class Policy {

    protected boolean result;
    @XmlAttribute(name = "source", required = true)
    protected String source;
    @XmlAttribute(name = "middlebox")
    protected String middlebox;
    @XmlAttribute(name = "destination", required = true)
    protected String destination;
    @XmlAttribute(name = "type", required = true)
    protected PolicyTypes type;

    /**
     * Recupera il valore della propriet� result.
     * 
     */
    public boolean isResult() {
        return result;
    }

    /**
     * Imposta il valore della propriet� result.
     * 
     */
    public void setResult(boolean value) {
        this.result = value;
    }

    /**
     * Recupera il valore della propriet� source.
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
     * Imposta il valore della propriet� source.
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
     * Recupera il valore della propriet� middlebox.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMiddlebox() {
        return middlebox;
    }

    /**
     * Imposta il valore della propriet� middlebox.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMiddlebox(String value) {
        this.middlebox = value;
    }

    /**
     * Recupera il valore della propriet� destination.
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
     * Imposta il valore della propriet� destination.
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
     * Recupera il valore della propriet� type.
     * 
     * @return
     *     possible object is
     *     {@link PolicyTypes }
     *     
     */
    public PolicyTypes getType() {
        return type;
    }

    /**
     * Imposta il valore della propriet� type.
     * 
     * @param value
     *     allowed object is
     *     {@link PolicyTypes }
     *     
     */
    public void setType(PolicyTypes value) {
        this.type = value;
    }

}
