//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.8-b130911.1802 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2018.05.27 alle 03:34:03 PM CEST 
//


package it.polito.verifoo.rest.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
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
 *         &lt;element name="action" type="{}ActionTypes" minOccurs="0"/>
 *         &lt;element name="source" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="destination" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="protocol" type="{}L4ProtocolTypes" minOccurs="0"/>
 *         &lt;element name="src_port" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dst_port" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="directional" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
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
    "action",
    "source",
    "destination",
    "protocol",
    "srcPort",
    "dstPort",
    "directional"
})
@XmlRootElement(name = "elements")
public class Elements {

    @XmlElement(defaultValue = "DENY")
    @XmlSchemaType(name = "string")
    protected ActionTypes action;
    @XmlElement(required = true)
    protected String source;
    @XmlElement(required = true)
    protected String destination;
    @XmlElement(defaultValue = "ANY")
    @XmlSchemaType(name = "string")
    protected L4ProtocolTypes protocol;
    @XmlElement(name = "src_port")
    protected String srcPort;
    @XmlElement(name = "dst_port")
    protected String dstPort;
    @XmlElement(defaultValue = "true")
    protected Boolean directional;

    /**
     * Recupera il valore della proprietà action.
     * 
     * @return
     *     possible object is
     *     {@link ActionTypes }
     *     
     */
    public ActionTypes getAction() {
        return action;
    }

    /**
     * Imposta il valore della proprietà action.
     * 
     * @param value
     *     allowed object is
     *     {@link ActionTypes }
     *     
     */
    public void setAction(ActionTypes value) {
        this.action = value;
    }

    /**
     * Recupera il valore della proprietà source.
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
     * Imposta il valore della proprietà source.
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
     * Recupera il valore della proprietà destination.
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
     * Imposta il valore della proprietà destination.
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
     * Recupera il valore della proprietà protocol.
     * 
     * @return
     *     possible object is
     *     {@link L4ProtocolTypes }
     *     
     */
    public L4ProtocolTypes getProtocol() {
        return protocol;
    }

    /**
     * Imposta il valore della proprietà protocol.
     * 
     * @param value
     *     allowed object is
     *     {@link L4ProtocolTypes }
     *     
     */
    public void setProtocol(L4ProtocolTypes value) {
        this.protocol = value;
    }

    /**
     * Recupera il valore della proprietà srcPort.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSrcPort() {
        return srcPort;
    }

    /**
     * Imposta il valore della proprietà srcPort.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSrcPort(String value) {
        this.srcPort = value;
    }

    /**
     * Recupera il valore della proprietà dstPort.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDstPort() {
        return dstPort;
    }

    /**
     * Imposta il valore della proprietà dstPort.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDstPort(String value) {
        this.dstPort = value;
    }

    /**
     * Recupera il valore della proprietà directional.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isDirectional() {
        return directional;
    }

    /**
     * Imposta il valore della proprietà directional.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setDirectional(Boolean value) {
        this.directional = value;
    }

}
