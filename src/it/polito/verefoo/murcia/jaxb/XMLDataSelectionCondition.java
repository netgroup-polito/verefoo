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
 * <p>Classe Java per XMLDataSelectionCondition complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="XMLDataSelectionCondition"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}DataSelectionCondition"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="xmlDataType" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="xmlNameSpace" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="xmlQueryLanguage" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="xmlQueryLanguageVersion" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="xmlQueryExpression" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "XMLDataSelectionCondition", propOrder = {
    "xmlDataType",
    "xmlNameSpace",
    "xmlQueryLanguage",
    "xmlQueryLanguageVersion",
    "xmlQueryExpression"
})
@XmlSeeAlso({
    WSSecurityCondition.class
})
public class XMLDataSelectionCondition
    extends DataSelectionCondition
{

    @XmlElement(required = true)
    protected String xmlDataType;
    @XmlElement(required = true)
    protected String xmlNameSpace;
    @XmlElement(required = true)
    protected String xmlQueryLanguage;
    @XmlElement(required = true)
    protected String xmlQueryLanguageVersion;
    @XmlElement(required = true)
    protected String xmlQueryExpression;

    /**
     * Recupera il valore della proprietà xmlDataType.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getXmlDataType() {
        return xmlDataType;
    }

    /**
     * Imposta il valore della proprietà xmlDataType.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setXmlDataType(String value) {
        this.xmlDataType = value;
    }

    /**
     * Recupera il valore della proprietà xmlNameSpace.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getXmlNameSpace() {
        return xmlNameSpace;
    }

    /**
     * Imposta il valore della proprietà xmlNameSpace.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setXmlNameSpace(String value) {
        this.xmlNameSpace = value;
    }

    /**
     * Recupera il valore della proprietà xmlQueryLanguage.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getXmlQueryLanguage() {
        return xmlQueryLanguage;
    }

    /**
     * Imposta il valore della proprietà xmlQueryLanguage.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setXmlQueryLanguage(String value) {
        this.xmlQueryLanguage = value;
    }

    /**
     * Recupera il valore della proprietà xmlQueryLanguageVersion.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getXmlQueryLanguageVersion() {
        return xmlQueryLanguageVersion;
    }

    /**
     * Imposta il valore della proprietà xmlQueryLanguageVersion.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setXmlQueryLanguageVersion(String value) {
        this.xmlQueryLanguageVersion = value;
    }

    /**
     * Recupera il valore della proprietà xmlQueryExpression.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getXmlQueryExpression() {
        return xmlQueryExpression;
    }

    /**
     * Imposta il valore della proprietà xmlQueryExpression.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setXmlQueryExpression(String value) {
        this.xmlQueryExpression = value;
    }

}
