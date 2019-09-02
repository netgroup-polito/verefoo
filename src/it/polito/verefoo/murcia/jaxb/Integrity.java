//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.11 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2019.09.02 alle 08:15:42 PM CEST 
//


package it.polito.verefoo.murcia.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per Integrity complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="Integrity"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}TechnologyActionSecurityProperty"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="integrityAlgorithm" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="integrityHeader" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *         &lt;element name="integrityPayload" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Integrity", propOrder = {
    "integrityAlgorithm",
    "integrityHeader",
    "integrityPayload"
})
public class Integrity
    extends TechnologyActionSecurityProperty
{

    protected String integrityAlgorithm;
    protected Boolean integrityHeader;
    protected Boolean integrityPayload;

    /**
     * Recupera il valore della proprietà integrityAlgorithm.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIntegrityAlgorithm() {
        return integrityAlgorithm;
    }

    /**
     * Imposta il valore della proprietà integrityAlgorithm.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIntegrityAlgorithm(String value) {
        this.integrityAlgorithm = value;
    }

    /**
     * Recupera il valore della proprietà integrityHeader.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIntegrityHeader() {
        return integrityHeader;
    }

    /**
     * Imposta il valore della proprietà integrityHeader.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIntegrityHeader(Boolean value) {
        this.integrityHeader = value;
    }

    /**
     * Recupera il valore della proprietà integrityPayload.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIntegrityPayload() {
        return integrityPayload;
    }

    /**
     * Imposta il valore della proprietà integrityPayload.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIntegrityPayload(Boolean value) {
        this.integrityPayload = value;
    }

}
