//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.11 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2019.09.02 alle 08:15:42 PM CEST 
//


package it.polito.verefoo.murcia.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per TLS_VPN_TechnologyParameter complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="TLS_VPN_TechnologyParameter"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}TechnologySpecificParameters"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="peerPort" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="L4Protocol" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="localEndpoint" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="remoteEndpoint" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="virtualIPSource" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="virtualIPDestination" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="device" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="tlsMode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TLS_VPN_TechnologyParameter", propOrder = {
    "peerPort",
    "l4Protocol",
    "localEndpoint",
    "remoteEndpoint",
    "virtualIPSource",
    "virtualIPDestination",
    "device",
    "tlsMode"
})
public class TLSVPNTechnologyParameter
    extends TechnologySpecificParameters
{

    protected String peerPort;
    @XmlElement(name = "L4Protocol")
    protected String l4Protocol;
    protected String localEndpoint;
    protected String remoteEndpoint;
    protected String virtualIPSource;
    protected String virtualIPDestination;
    protected String device;
    protected String tlsMode;

    /**
     * Recupera il valore della proprietà peerPort.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPeerPort() {
        return peerPort;
    }

    /**
     * Imposta il valore della proprietà peerPort.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPeerPort(String value) {
        this.peerPort = value;
    }

    /**
     * Recupera il valore della proprietà l4Protocol.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getL4Protocol() {
        return l4Protocol;
    }

    /**
     * Imposta il valore della proprietà l4Protocol.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setL4Protocol(String value) {
        this.l4Protocol = value;
    }

    /**
     * Recupera il valore della proprietà localEndpoint.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLocalEndpoint() {
        return localEndpoint;
    }

    /**
     * Imposta il valore della proprietà localEndpoint.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLocalEndpoint(String value) {
        this.localEndpoint = value;
    }

    /**
     * Recupera il valore della proprietà remoteEndpoint.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRemoteEndpoint() {
        return remoteEndpoint;
    }

    /**
     * Imposta il valore della proprietà remoteEndpoint.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRemoteEndpoint(String value) {
        this.remoteEndpoint = value;
    }

    /**
     * Recupera il valore della proprietà virtualIPSource.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVirtualIPSource() {
        return virtualIPSource;
    }

    /**
     * Imposta il valore della proprietà virtualIPSource.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVirtualIPSource(String value) {
        this.virtualIPSource = value;
    }

    /**
     * Recupera il valore della proprietà virtualIPDestination.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVirtualIPDestination() {
        return virtualIPDestination;
    }

    /**
     * Imposta il valore della proprietà virtualIPDestination.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVirtualIPDestination(String value) {
        this.virtualIPDestination = value;
    }

    /**
     * Recupera il valore della proprietà device.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDevice() {
        return device;
    }

    /**
     * Imposta il valore della proprietà device.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDevice(String value) {
        this.device = value;
    }

    /**
     * Recupera il valore della proprietà tlsMode.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTlsMode() {
        return tlsMode;
    }

    /**
     * Imposta il valore della proprietà tlsMode.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTlsMode(String value) {
        this.tlsMode = value;
    }

}
