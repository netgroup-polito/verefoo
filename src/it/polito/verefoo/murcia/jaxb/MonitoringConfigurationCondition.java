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
 * <p>Classe Java per MonitoringConfigurationCondition complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="MonitoringConfigurationCondition"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}FilteringConfigurationCondition"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="detectionFilter" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="signatureList" type="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}SignatureList" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MonitoringConfigurationCondition", propOrder = {
    "detectionFilter",
    "signatureList"
})
public class MonitoringConfigurationCondition
    extends FilteringConfigurationCondition
{

    protected String detectionFilter;
    protected SignatureList signatureList;

    /**
     * Recupera il valore della proprietà detectionFilter.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDetectionFilter() {
        return detectionFilter;
    }

    /**
     * Imposta il valore della proprietà detectionFilter.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDetectionFilter(String value) {
        this.detectionFilter = value;
    }

    /**
     * Recupera il valore della proprietà signatureList.
     * 
     * @return
     *     possible object is
     *     {@link SignatureList }
     *     
     */
    public SignatureList getSignatureList() {
        return signatureList;
    }

    /**
     * Imposta il valore della proprietà signatureList.
     * 
     * @param value
     *     allowed object is
     *     {@link SignatureList }
     *     
     */
    public void setSignatureList(SignatureList value) {
        this.signatureList = value;
    }

}
