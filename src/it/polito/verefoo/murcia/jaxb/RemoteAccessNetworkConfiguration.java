//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.11 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2019.09.02 alle 08:15:42 PM CEST 
//


package it.polito.verefoo.murcia.jaxb;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per RemoteAccessNetworkConfiguration complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="RemoteAccessNetworkConfiguration"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}AdditionalNetworkConfigurationParameters"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="startIPAddress" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="maxClients" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/&gt;
 *         &lt;element name="netmask" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="routedSubnets" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="dnsServer" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="domainSuffix" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="wins" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="localSubnet" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="remoteSubnet" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RemoteAccessNetworkConfiguration", propOrder = {
    "startIPAddress",
    "maxClients",
    "netmask",
    "routedSubnets",
    "dnsServer",
    "domainSuffix",
    "wins",
    "localSubnet",
    "remoteSubnet"
})
public class RemoteAccessNetworkConfiguration
    extends AdditionalNetworkConfigurationParameters
{

    protected String startIPAddress;
    protected BigInteger maxClients;
    protected String netmask;
    protected String routedSubnets;
    protected String dnsServer;
    protected String domainSuffix;
    protected String wins;
    protected String localSubnet;
    protected String remoteSubnet;

    /**
     * Recupera il valore della proprietà startIPAddress.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStartIPAddress() {
        return startIPAddress;
    }

    /**
     * Imposta il valore della proprietà startIPAddress.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStartIPAddress(String value) {
        this.startIPAddress = value;
    }

    /**
     * Recupera il valore della proprietà maxClients.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getMaxClients() {
        return maxClients;
    }

    /**
     * Imposta il valore della proprietà maxClients.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setMaxClients(BigInteger value) {
        this.maxClients = value;
    }

    /**
     * Recupera il valore della proprietà netmask.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNetmask() {
        return netmask;
    }

    /**
     * Imposta il valore della proprietà netmask.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNetmask(String value) {
        this.netmask = value;
    }

    /**
     * Recupera il valore della proprietà routedSubnets.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRoutedSubnets() {
        return routedSubnets;
    }

    /**
     * Imposta il valore della proprietà routedSubnets.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRoutedSubnets(String value) {
        this.routedSubnets = value;
    }

    /**
     * Recupera il valore della proprietà dnsServer.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDnsServer() {
        return dnsServer;
    }

    /**
     * Imposta il valore della proprietà dnsServer.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDnsServer(String value) {
        this.dnsServer = value;
    }

    /**
     * Recupera il valore della proprietà domainSuffix.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDomainSuffix() {
        return domainSuffix;
    }

    /**
     * Imposta il valore della proprietà domainSuffix.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDomainSuffix(String value) {
        this.domainSuffix = value;
    }

    /**
     * Recupera il valore della proprietà wins.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWins() {
        return wins;
    }

    /**
     * Imposta il valore della proprietà wins.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWins(String value) {
        this.wins = value;
    }

    /**
     * Recupera il valore della proprietà localSubnet.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLocalSubnet() {
        return localSubnet;
    }

    /**
     * Imposta il valore della proprietà localSubnet.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLocalSubnet(String value) {
        this.localSubnet = value;
    }

    /**
     * Recupera il valore della proprietà remoteSubnet.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRemoteSubnet() {
        return remoteSubnet;
    }

    /**
     * Imposta il valore della proprietà remoteSubnet.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRemoteSubnet(String value) {
        this.remoteSubnet = value;
    }

}
