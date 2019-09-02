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
 * <p>Classe Java per WSSecurityCondition complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="WSSecurityCondition"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}XMLDataSelectionCondition"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="content_vs_element" type="{http://www.w3.org/2001/XMLSchema}boolean"/&gt;
 *         &lt;element name="body" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="attachment" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="headerLocalName" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="headerNameSpace" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "WSSecurityCondition", propOrder = {
    "contentVsElement",
    "body",
    "attachment",
    "headerLocalName",
    "headerNameSpace"
})
public class WSSecurityCondition
    extends XMLDataSelectionCondition
{

    @XmlElement(name = "content_vs_element")
    protected boolean contentVsElement;
    @XmlElement(required = true)
    protected String body;
    @XmlElement(required = true)
    protected String attachment;
    @XmlElement(required = true)
    protected String headerLocalName;
    @XmlElement(required = true)
    protected String headerNameSpace;

    /**
     * Recupera il valore della proprietà contentVsElement.
     * 
     */
    public boolean isContentVsElement() {
        return contentVsElement;
    }

    /**
     * Imposta il valore della proprietà contentVsElement.
     * 
     */
    public void setContentVsElement(boolean value) {
        this.contentVsElement = value;
    }

    /**
     * Recupera il valore della proprietà body.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBody() {
        return body;
    }

    /**
     * Imposta il valore della proprietà body.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBody(String value) {
        this.body = value;
    }

    /**
     * Recupera il valore della proprietà attachment.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAttachment() {
        return attachment;
    }

    /**
     * Imposta il valore della proprietà attachment.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAttachment(String value) {
        this.attachment = value;
    }

    /**
     * Recupera il valore della proprietà headerLocalName.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHeaderLocalName() {
        return headerLocalName;
    }

    /**
     * Imposta il valore della proprietà headerLocalName.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHeaderLocalName(String value) {
        this.headerLocalName = value;
    }

    /**
     * Recupera il valore della proprietà headerNameSpace.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHeaderNameSpace() {
        return headerNameSpace;
    }

    /**
     * Imposta il valore della proprietà headerNameSpace.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHeaderNameSpace(String value) {
        this.headerNameSpace = value;
    }

}
