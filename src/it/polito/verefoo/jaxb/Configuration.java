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
 *       &lt;choice&gt;
 *         &lt;element ref="{}firewall"/&gt;
 *         &lt;element ref="{}endhost"/&gt;
 *         &lt;element ref="{}endpoint"/&gt;
 *         &lt;element ref="{}antispam"/&gt;
 *         &lt;element ref="{}cache"/&gt;
 *         &lt;element ref="{}dpi"/&gt;
 *         &lt;element ref="{}mailclient"/&gt;
 *         &lt;element ref="{}mailserver"/&gt;
 *         &lt;element ref="{}nat"/&gt;
 *         &lt;element ref="{}vpnaccess"/&gt;
 *         &lt;element ref="{}vpnexit"/&gt;
 *         &lt;element ref="{}webclient"/&gt;
 *         &lt;element ref="{}webserver"/&gt;
 *         &lt;element ref="{}fieldmodifier"/&gt;
 *         &lt;element ref="{}forwarder"/&gt;
 *       &lt;/choice&gt;
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}long" /&gt;
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="description" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "firewall",
    "endhost",
    "endpoint",
    "antispam",
    "cache",
    "dpi",
    "mailclient",
    "mailserver",
    "nat",
    "vpnaccess",
    "vpnexit",
    "webclient",
    "webserver",
    "fieldmodifier",
    "forwarder"
})
@XmlRootElement(name = "configuration")
public class Configuration {

    protected Firewall firewall;
    protected Endhost endhost;
    protected Endpoint endpoint;
    protected Antispam antispam;
    protected Cache cache;
    protected Dpi dpi;
    protected Mailclient mailclient;
    protected Mailserver mailserver;
    protected Nat nat;
    protected Vpnaccess vpnaccess;
    protected Vpnexit vpnexit;
    protected Webclient webclient;
    protected Webserver webserver;
    protected Fieldmodifier fieldmodifier;
    protected Forwarder forwarder;
    @XmlAttribute(name = "id")
    protected Long id;
    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "description")
    protected String description;

    /**
     * Recupera il valore della proprietà firewall.
     * 
     * @return
     *     possible object is
     *     {@link Firewall }
     *     
     */
    public Firewall getFirewall() {
        return firewall;
    }

    /**
     * Imposta il valore della proprietà firewall.
     * 
     * @param value
     *     allowed object is
     *     {@link Firewall }
     *     
     */
    public void setFirewall(Firewall value) {
        this.firewall = value;
    }

    /**
     * Recupera il valore della proprietà endhost.
     * 
     * @return
     *     possible object is
     *     {@link Endhost }
     *     
     */
    public Endhost getEndhost() {
        return endhost;
    }

    /**
     * Imposta il valore della proprietà endhost.
     * 
     * @param value
     *     allowed object is
     *     {@link Endhost }
     *     
     */
    public void setEndhost(Endhost value) {
        this.endhost = value;
    }

    /**
     * Recupera il valore della proprietà endpoint.
     * 
     * @return
     *     possible object is
     *     {@link Endpoint }
     *     
     */
    public Endpoint getEndpoint() {
        return endpoint;
    }

    /**
     * Imposta il valore della proprietà endpoint.
     * 
     * @param value
     *     allowed object is
     *     {@link Endpoint }
     *     
     */
    public void setEndpoint(Endpoint value) {
        this.endpoint = value;
    }

    /**
     * Recupera il valore della proprietà antispam.
     * 
     * @return
     *     possible object is
     *     {@link Antispam }
     *     
     */
    public Antispam getAntispam() {
        return antispam;
    }

    /**
     * Imposta il valore della proprietà antispam.
     * 
     * @param value
     *     allowed object is
     *     {@link Antispam }
     *     
     */
    public void setAntispam(Antispam value) {
        this.antispam = value;
    }

    /**
     * Recupera il valore della proprietà cache.
     * 
     * @return
     *     possible object is
     *     {@link Cache }
     *     
     */
    public Cache getCache() {
        return cache;
    }

    /**
     * Imposta il valore della proprietà cache.
     * 
     * @param value
     *     allowed object is
     *     {@link Cache }
     *     
     */
    public void setCache(Cache value) {
        this.cache = value;
    }

    /**
     * Recupera il valore della proprietà dpi.
     * 
     * @return
     *     possible object is
     *     {@link Dpi }
     *     
     */
    public Dpi getDpi() {
        return dpi;
    }

    /**
     * Imposta il valore della proprietà dpi.
     * 
     * @param value
     *     allowed object is
     *     {@link Dpi }
     *     
     */
    public void setDpi(Dpi value) {
        this.dpi = value;
    }

    /**
     * Recupera il valore della proprietà mailclient.
     * 
     * @return
     *     possible object is
     *     {@link Mailclient }
     *     
     */
    public Mailclient getMailclient() {
        return mailclient;
    }

    /**
     * Imposta il valore della proprietà mailclient.
     * 
     * @param value
     *     allowed object is
     *     {@link Mailclient }
     *     
     */
    public void setMailclient(Mailclient value) {
        this.mailclient = value;
    }

    /**
     * Recupera il valore della proprietà mailserver.
     * 
     * @return
     *     possible object is
     *     {@link Mailserver }
     *     
     */
    public Mailserver getMailserver() {
        return mailserver;
    }

    /**
     * Imposta il valore della proprietà mailserver.
     * 
     * @param value
     *     allowed object is
     *     {@link Mailserver }
     *     
     */
    public void setMailserver(Mailserver value) {
        this.mailserver = value;
    }

    /**
     * Recupera il valore della proprietà nat.
     * 
     * @return
     *     possible object is
     *     {@link Nat }
     *     
     */
    public Nat getNat() {
        return nat;
    }

    /**
     * Imposta il valore della proprietà nat.
     * 
     * @param value
     *     allowed object is
     *     {@link Nat }
     *     
     */
    public void setNat(Nat value) {
        this.nat = value;
    }

    /**
     * Recupera il valore della proprietà vpnaccess.
     * 
     * @return
     *     possible object is
     *     {@link Vpnaccess }
     *     
     */
    public Vpnaccess getVpnaccess() {
        return vpnaccess;
    }

    /**
     * Imposta il valore della proprietà vpnaccess.
     * 
     * @param value
     *     allowed object is
     *     {@link Vpnaccess }
     *     
     */
    public void setVpnaccess(Vpnaccess value) {
        this.vpnaccess = value;
    }

    /**
     * Recupera il valore della proprietà vpnexit.
     * 
     * @return
     *     possible object is
     *     {@link Vpnexit }
     *     
     */
    public Vpnexit getVpnexit() {
        return vpnexit;
    }

    /**
     * Imposta il valore della proprietà vpnexit.
     * 
     * @param value
     *     allowed object is
     *     {@link Vpnexit }
     *     
     */
    public void setVpnexit(Vpnexit value) {
        this.vpnexit = value;
    }

    /**
     * Recupera il valore della proprietà webclient.
     * 
     * @return
     *     possible object is
     *     {@link Webclient }
     *     
     */
    public Webclient getWebclient() {
        return webclient;
    }

    /**
     * Imposta il valore della proprietà webclient.
     * 
     * @param value
     *     allowed object is
     *     {@link Webclient }
     *     
     */
    public void setWebclient(Webclient value) {
        this.webclient = value;
    }

    /**
     * Recupera il valore della proprietà webserver.
     * 
     * @return
     *     possible object is
     *     {@link Webserver }
     *     
     */
    public Webserver getWebserver() {
        return webserver;
    }

    /**
     * Imposta il valore della proprietà webserver.
     * 
     * @param value
     *     allowed object is
     *     {@link Webserver }
     *     
     */
    public void setWebserver(Webserver value) {
        this.webserver = value;
    }

    /**
     * Recupera il valore della proprietà fieldmodifier.
     * 
     * @return
     *     possible object is
     *     {@link Fieldmodifier }
     *     
     */
    public Fieldmodifier getFieldmodifier() {
        return fieldmodifier;
    }

    /**
     * Imposta il valore della proprietà fieldmodifier.
     * 
     * @param value
     *     allowed object is
     *     {@link Fieldmodifier }
     *     
     */
    public void setFieldmodifier(Fieldmodifier value) {
        this.fieldmodifier = value;
    }

    /**
     * Recupera il valore della proprietà forwarder.
     * 
     * @return
     *     possible object is
     *     {@link Forwarder }
     *     
     */
    public Forwarder getForwarder() {
        return forwarder;
    }

    /**
     * Imposta il valore della proprietà forwarder.
     * 
     * @param value
     *     allowed object is
     *     {@link Forwarder }
     *     
     */
    public void setForwarder(Forwarder value) {
        this.forwarder = value;
    }

    /**
     * Recupera il valore della proprietà id.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getId() {
        return id;
    }

    /**
     * Imposta il valore della proprietà id.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setId(Long value) {
        this.id = value;
    }

    /**
     * Recupera il valore della proprietà name.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Imposta il valore della proprietà name.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Recupera il valore della proprietà description.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Imposta il valore della proprietà description.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

}
