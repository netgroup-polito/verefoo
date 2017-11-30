//
// Questo file � stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.8-b130911.1802 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andr� persa durante la ricompilazione dello schema di origine. 
// Generato il: 2017.11.30 alle 09:40:53 PM CET 
//


package it.polito.verifoo.rest.jaxb;

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
 *       &lt;choice>
 *         &lt;element ref="{}firewall"/>
 *         &lt;element ref="{}endhost"/>
 *         &lt;element ref="{}endpoint"/>
 *         &lt;element ref="{}antispam"/>
 *         &lt;element ref="{}cache"/>
 *         &lt;element ref="{}dpi"/>
 *         &lt;element ref="{}mailclient"/>
 *         &lt;element ref="{}mailserver"/>
 *         &lt;element ref="{}nat"/>
 *         &lt;element ref="{}vpnaccess"/>
 *         &lt;element ref="{}vpnexit"/>
 *         &lt;element ref="{}webclient"/>
 *         &lt;element ref="{}webserver"/>
 *         &lt;element ref="{}fieldmodifier"/>
 *       &lt;/choice>
 *       &lt;attribute name="id" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="description" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
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
    "fieldmodifier"
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
    @XmlAttribute(name = "id")
    protected Long id;
    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "description")
    protected String description;

    /**
     * Recupera il valore della propriet� firewall.
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
     * Imposta il valore della propriet� firewall.
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
     * Recupera il valore della propriet� endhost.
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
     * Imposta il valore della propriet� endhost.
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
     * Recupera il valore della propriet� endpoint.
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
     * Imposta il valore della propriet� endpoint.
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
     * Recupera il valore della propriet� antispam.
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
     * Imposta il valore della propriet� antispam.
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
     * Recupera il valore della propriet� cache.
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
     * Imposta il valore della propriet� cache.
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
     * Recupera il valore della propriet� dpi.
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
     * Imposta il valore della propriet� dpi.
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
     * Recupera il valore della propriet� mailclient.
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
     * Imposta il valore della propriet� mailclient.
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
     * Recupera il valore della propriet� mailserver.
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
     * Imposta il valore della propriet� mailserver.
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
     * Recupera il valore della propriet� nat.
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
     * Imposta il valore della propriet� nat.
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
     * Recupera il valore della propriet� vpnaccess.
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
     * Imposta il valore della propriet� vpnaccess.
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
     * Recupera il valore della propriet� vpnexit.
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
     * Imposta il valore della propriet� vpnexit.
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
     * Recupera il valore della propriet� webclient.
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
     * Imposta il valore della propriet� webclient.
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
     * Recupera il valore della propriet� webserver.
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
     * Imposta il valore della propriet� webserver.
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
     * Recupera il valore della propriet� fieldmodifier.
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
     * Imposta il valore della propriet� fieldmodifier.
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
     * Recupera il valore della propriet� id.
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
     * Imposta il valore della propriet� id.
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
     * Recupera il valore della propriet� name.
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
     * Imposta il valore della propriet� name.
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
     * Recupera il valore della propriet� description.
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
     * Imposta il valore della propriet� description.
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
