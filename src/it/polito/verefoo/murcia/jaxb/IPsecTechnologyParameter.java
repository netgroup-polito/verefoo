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
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per IPsecTechnologyParameter complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="IPsecTechnologyParameter"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}TechnologySpecificParameters"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="IPsecProtocol" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="isTunnel" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="localEndpoint" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="remoteEndpoint" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "IPsecTechnologyParameter", propOrder = {
    "iPsecProtocol",
    "isTunnel",
    "localEndpoint",
    "remoteEndpoint"
})
@XmlSeeAlso({
    DTLSTechnologyParameter.class
})
public class IPsecTechnologyParameter
    extends TechnologySpecificParameters
{

    @XmlElement(name = "IPsecProtocol")
    protected String iPsecProtocol;
    protected Boolean isTunnel;
    protected String localEndpoint;
    protected String remoteEndpoint;

    /**
     * Recupera il valore della proprietà iPsecProtocol.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIPsecProtocol() {
        return iPsecProtocol;
    }

    /**
     * Imposta il valore della proprietà iPsecProtocol.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIPsecProtocol(String value) {
        this.iPsecProtocol = value;
    }

    /**
     * Recupera il valore della proprietà isTunnel.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsTunnel() {
        return isTunnel;
    }

    /**
     * Imposta il valore della proprietà isTunnel.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsTunnel(Boolean value) {
        this.isTunnel = value;
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

}
