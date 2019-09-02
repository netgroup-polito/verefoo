//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.11 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2019.09.02 alle 08:15:42 PM CEST 
//


package it.polito.verefoo.murcia.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per ReduceBandwidthActionType complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="ReduceBandwidthActionType"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="downlink_bandwidth_value" type="{http://www.w3.org/2001/XMLSchema}double" /&gt;
 *       &lt;attribute name="uplink_bandwidth_value" type="{http://www.w3.org/2001/XMLSchema}double" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReduceBandwidthActionType")
public class ReduceBandwidthActionType {

    @XmlAttribute(name = "downlink_bandwidth_value")
    protected Double downlinkBandwidthValue;
    @XmlAttribute(name = "uplink_bandwidth_value")
    protected Double uplinkBandwidthValue;

    /**
     * Recupera il valore della proprietà downlinkBandwidthValue.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getDownlinkBandwidthValue() {
        return downlinkBandwidthValue;
    }

    /**
     * Imposta il valore della proprietà downlinkBandwidthValue.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setDownlinkBandwidthValue(Double value) {
        this.downlinkBandwidthValue = value;
    }

    /**
     * Recupera il valore della proprietà uplinkBandwidthValue.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getUplinkBandwidthValue() {
        return uplinkBandwidthValue;
    }

    /**
     * Imposta il valore della proprietà uplinkBandwidthValue.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setUplinkBandwidthValue(Double value) {
        this.uplinkBandwidthValue = value;
    }

}
