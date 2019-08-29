//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.11 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2019.07.31 alle 05:41:07 PM CEST 
//


package it.polito.verefoo.jaxb;

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
 * &lt;complexType&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="sourceHost" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="destHost" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="avgLatency" type="{http://www.w3.org/2001/XMLSchema}int" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "Connection")
public class Connection {

    @XmlAttribute(name = "sourceHost", required = true)
    protected String sourceHost;
    @XmlAttribute(name = "destHost", required = true)
    protected String destHost;
    @XmlAttribute(name = "avgLatency")
    protected Integer avgLatency;

    /**
     * Recupera il valore della proprietà sourceHost.
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
     * Imposta il valore della proprietà sourceHost.
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
     * Recupera il valore della proprietà destHost.
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
     * Imposta il valore della proprietà destHost.
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
     * Recupera il valore della proprietà avgLatency.
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
     * Imposta il valore della proprietà avgLatency.
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
