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
 * <p>Classe Java per ReduceBandwidthAction complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="ReduceBandwidthAction"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}ConfigurationAction"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ReduceBandwidthActionType" type="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}ReduceBandwidthActionType"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReduceBandwidthAction", propOrder = {
    "reduceBandwidthActionType"
})
public class ReduceBandwidthAction
    extends ConfigurationAction
{

    @XmlElement(name = "ReduceBandwidthActionType", required = true)
    protected ReduceBandwidthActionType reduceBandwidthActionType;

    /**
     * Recupera il valore della proprietà reduceBandwidthActionType.
     * 
     * @return
     *     possible object is
     *     {@link ReduceBandwidthActionType }
     *     
     */
    public ReduceBandwidthActionType getReduceBandwidthActionType() {
        return reduceBandwidthActionType;
    }

    /**
     * Imposta il valore della proprietà reduceBandwidthActionType.
     * 
     * @param value
     *     allowed object is
     *     {@link ReduceBandwidthActionType }
     *     
     */
    public void setReduceBandwidthActionType(ReduceBandwidthActionType value) {
        this.reduceBandwidthActionType = value;
    }

}
