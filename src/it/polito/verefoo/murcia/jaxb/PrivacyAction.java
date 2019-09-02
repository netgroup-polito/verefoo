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
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per PrivacyAction complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="PrivacyAction"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}ConfigurationAction"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="PrivacyActionType" type="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}PrivacyActionType"/&gt;
 *         &lt;element name="PrivacyMethod" type="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}PrivacyMethod"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PrivacyAction", propOrder = {
    "privacyActionType",
    "privacyMethod"
})
public class PrivacyAction
    extends ConfigurationAction
{

    @XmlElement(name = "PrivacyActionType", required = true)
    @XmlSchemaType(name = "string")
    protected PrivacyActionType privacyActionType;
    @XmlElement(name = "PrivacyMethod", required = true)
    protected PrivacyMethod privacyMethod;

    /**
     * Recupera il valore della proprietà privacyActionType.
     * 
     * @return
     *     possible object is
     *     {@link PrivacyActionType }
     *     
     */
    public PrivacyActionType getPrivacyActionType() {
        return privacyActionType;
    }

    /**
     * Imposta il valore della proprietà privacyActionType.
     * 
     * @param value
     *     allowed object is
     *     {@link PrivacyActionType }
     *     
     */
    public void setPrivacyActionType(PrivacyActionType value) {
        this.privacyActionType = value;
    }

    /**
     * Recupera il valore della proprietà privacyMethod.
     * 
     * @return
     *     possible object is
     *     {@link PrivacyMethod }
     *     
     */
    public PrivacyMethod getPrivacyMethod() {
        return privacyMethod;
    }

    /**
     * Imposta il valore della proprietà privacyMethod.
     * 
     * @param value
     *     allowed object is
     *     {@link PrivacyMethod }
     *     
     */
    public void setPrivacyMethod(PrivacyMethod value) {
        this.privacyMethod = value;
    }

}
