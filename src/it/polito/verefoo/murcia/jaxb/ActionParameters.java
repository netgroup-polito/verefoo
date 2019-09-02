//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.11 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2019.09.02 alle 08:15:42 PM CEST 
//


package it.polito.verefoo.murcia.jaxb;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per ActionParameters complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="ActionParameters"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="keyExchange" type="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}KeyExchangeParameter" minOccurs="0"/&gt;
 *         &lt;element name="technologyParameter" type="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}TechnologySpecificParameters" maxOccurs="unbounded"/&gt;
 *         &lt;element name="additionalNetworkConfigurationParameters" type="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}AdditionalNetworkConfigurationParameters" minOccurs="0"/&gt;
 *         &lt;element name="authenticationParameters" type="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}AuthenticationParameters" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ActionParameters", propOrder = {
    "keyExchange",
    "technologyParameter",
    "additionalNetworkConfigurationParameters",
    "authenticationParameters"
})
public class ActionParameters {

    protected KeyExchangeParameter keyExchange;
    @XmlElement(required = true)
    protected List<TechnologySpecificParameters> technologyParameter;
    protected AdditionalNetworkConfigurationParameters additionalNetworkConfigurationParameters;
    protected AuthenticationParameters authenticationParameters;

    /**
     * Recupera il valore della proprietà keyExchange.
     * 
     * @return
     *     possible object is
     *     {@link KeyExchangeParameter }
     *     
     */
    public KeyExchangeParameter getKeyExchange() {
        return keyExchange;
    }

    /**
     * Imposta il valore della proprietà keyExchange.
     * 
     * @param value
     *     allowed object is
     *     {@link KeyExchangeParameter }
     *     
     */
    public void setKeyExchange(KeyExchangeParameter value) {
        this.keyExchange = value;
    }

    /**
     * Gets the value of the technologyParameter property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the technologyParameter property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTechnologyParameter().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TechnologySpecificParameters }
     * 
     * 
     */
    public List<TechnologySpecificParameters> getTechnologyParameter() {
        if (technologyParameter == null) {
            technologyParameter = new ArrayList<TechnologySpecificParameters>();
        }
        return this.technologyParameter;
    }

    /**
     * Recupera il valore della proprietà additionalNetworkConfigurationParameters.
     * 
     * @return
     *     possible object is
     *     {@link AdditionalNetworkConfigurationParameters }
     *     
     */
    public AdditionalNetworkConfigurationParameters getAdditionalNetworkConfigurationParameters() {
        return additionalNetworkConfigurationParameters;
    }

    /**
     * Imposta il valore della proprietà additionalNetworkConfigurationParameters.
     * 
     * @param value
     *     allowed object is
     *     {@link AdditionalNetworkConfigurationParameters }
     *     
     */
    public void setAdditionalNetworkConfigurationParameters(AdditionalNetworkConfigurationParameters value) {
        this.additionalNetworkConfigurationParameters = value;
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
