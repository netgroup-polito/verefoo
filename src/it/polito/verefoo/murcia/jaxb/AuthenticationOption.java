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
 * <p>Classe Java per AuthenticationOption complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="AuthenticationOption"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="AuthenticationTarget" type="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}PacketFilterCondition"/&gt;
 *         &lt;element name="AuthenticationMethod" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="AuthenticationMechanism" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="AuthenticationParameters" type="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}AuthenticationParameters"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AuthenticationOption", propOrder = {
    "authenticationTarget",
    "authenticationMethod",
    "authenticationMechanism",
    "authenticationParameters"
})
public class AuthenticationOption {

    @XmlElement(name = "AuthenticationTarget", required = true)
    protected PacketFilterCondition authenticationTarget;
    @XmlElement(name = "AuthenticationMethod", required = true)
    protected String authenticationMethod;
    @XmlElement(name = "AuthenticationMechanism", required = true)
    protected String authenticationMechanism;
    @XmlElement(name = "AuthenticationParameters", required = true)
    protected AuthenticationParameters authenticationParameters;

    /**
     * Recupera il valore della proprietà authenticationTarget.
     * 
     * @return
     *     possible object is
     *     {@link PacketFilterCondition }
     *     
     */
    public PacketFilterCondition getAuthenticationTarget() {
        return authenticationTarget;
    }

    /**
     * Imposta il valore della proprietà authenticationTarget.
     * 
     * @param value
     *     allowed object is
     *     {@link PacketFilterCondition }
     *     
     */
    public void setAuthenticationTarget(PacketFilterCondition value) {
        this.authenticationTarget = value;
    }

    /**
     * Recupera il valore della proprietà authenticationMethod.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAuthenticationMethod() {
        return authenticationMethod;
    }

    /**
     * Imposta il valore della proprietà authenticationMethod.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAuthenticationMethod(String value) {
        this.authenticationMethod = value;
    }

    /**
     * Recupera il valore della proprietà authenticationMechanism.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAuthenticationMechanism() {
        return authenticationMechanism;
    }

    /**
     * Imposta il valore della proprietà authenticationMechanism.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAuthenticationMechanism(String value) {
        this.authenticationMechanism = value;
    }

    /**
     * Recupera il valore della proprietà authenticationParameters.
     * 
     * @return
     *     possible object is
     *     {@link AuthenticationParameters }
     *     
     */
    public AuthenticationParameters getAuthenticationParameters() {
        return authenticationParameters;
    }

    /**
     * Imposta il valore della proprietà authenticationParameters.
     * 
     * @param value
     *     allowed object is
     *     {@link AuthenticationParameters }
     *     
     */
    public void setAuthenticationParameters(AuthenticationParameters value) {
        this.authenticationParameters = value;
    }

}
