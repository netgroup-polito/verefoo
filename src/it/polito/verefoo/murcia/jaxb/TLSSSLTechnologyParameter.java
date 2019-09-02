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
 * <p>Classe Java per TLS_SSL_TechnologyParameter complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="TLS_SSL_TechnologyParameter"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}TechnologySpecificParameters"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ciphers-client" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="ssl-version-client" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="ciphers-server" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="ssl-version-server" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TLS_SSL_TechnologyParameter", propOrder = {
    "ciphersClient",
    "sslVersionClient",
    "ciphersServer",
    "sslVersionServer"
})
public class TLSSSLTechnologyParameter
    extends TechnologySpecificParameters
{

    @XmlElement(name = "ciphers-client", required = true)
    protected String ciphersClient;
    @XmlElement(name = "ssl-version-client", required = true)
    protected String sslVersionClient;
    @XmlElement(name = "ciphers-server", required = true)
    protected String ciphersServer;
    @XmlElement(name = "ssl-version-server", required = true)
    protected String sslVersionServer;

    /**
     * Recupera il valore della proprietà ciphersClient.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCiphersClient() {
        return ciphersClient;
    }

    /**
     * Imposta il valore della proprietà ciphersClient.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCiphersClient(String value) {
        this.ciphersClient = value;
    }

    /**
     * Recupera il valore della proprietà sslVersionClient.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSslVersionClient() {
        return sslVersionClient;
    }

    /**
     * Imposta il valore della proprietà sslVersionClient.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSslVersionClient(String value) {
        this.sslVersionClient = value;
    }

    /**
     * Recupera il valore della proprietà ciphersServer.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCiphersServer() {
        return ciphersServer;
    }

    /**
     * Imposta il valore della proprietà ciphersServer.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCiphersServer(String value) {
        this.ciphersServer = value;
    }

    /**
     * Recupera il valore della proprietà sslVersionServer.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSslVersionServer() {
        return sslVersionServer;
    }

    /**
     * Imposta il valore della proprietà sslVersionServer.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSslVersionServer(String value) {
        this.sslVersionServer = value;
    }

}
