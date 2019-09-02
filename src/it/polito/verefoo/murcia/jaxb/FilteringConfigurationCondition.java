//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.11 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2019.09.02 alle 08:15:42 PM CEST 
//


package it.polito.verefoo.murcia.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per FilteringConfigurationCondition complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="FilteringConfigurationCondition"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}ConfigurationCondition"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="packetFilterCondition" type="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}PacketFilterCondition" minOccurs="0"/&gt;
 *         &lt;element name="statefulCondition" type="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}StatefulCondition" minOccurs="0"/&gt;
 *         &lt;element name="timeCondition" type="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}TimeCondition" minOccurs="0"/&gt;
 *         &lt;element name="applicationLayerCondition" type="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}ApplicationLayerCondition" minOccurs="0"/&gt;
 *         &lt;element name="qosCondition" type="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}QoSCondition" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "FilteringConfigurationCondition", propOrder = {
    "packetFilterCondition",
    "statefulCondition",
    "timeCondition",
    "applicationLayerCondition",
    "qosCondition"
})
@XmlSeeAlso({
    PrivacyConfigurationCondition.class,
    DataAggregationConfigurationCondition.class,
    AnonymityConfigurationCondition.class,
    MonitoringConfigurationCondition.class,
    TrafficDivertConfigurationCondition.class,
    AuthorizationCondition.class,
    AuthenticationCondition.class
})
public class FilteringConfigurationCondition
    extends ConfigurationCondition
{

    protected PacketFilterCondition packetFilterCondition;
    protected StatefulCondition statefulCondition;
    protected TimeCondition timeCondition;
    protected ApplicationLayerCondition applicationLayerCondition;
    protected QoSCondition qosCondition;

    /**
     * Recupera il valore della proprietà packetFilterCondition.
     * 
     * @return
     *     possible object is
     *     {@link PacketFilterCondition }
     *     
     */
    public PacketFilterCondition getPacketFilterCondition() {
        return packetFilterCondition;
    }

    /**
     * Imposta il valore della proprietà packetFilterCondition.
     * 
     * @param value
     *     allowed object is
     *     {@link PacketFilterCondition }
     *     
     */
    public void setPacketFilterCondition(PacketFilterCondition value) {
        this.packetFilterCondition = value;
    }

    /**
     * Recupera il valore della proprietà statefulCondition.
     * 
     * @return
     *     possible object is
     *     {@link StatefulCondition }
     *     
     */
    public StatefulCondition getStatefulCondition() {
        return statefulCondition;
    }

    /**
     * Imposta il valore della proprietà statefulCondition.
     * 
     * @param value
     *     allowed object is
     *     {@link StatefulCondition }
     *     
     */
    public void setStatefulCondition(StatefulCondition value) {
        this.statefulCondition = value;
    }

    /**
     * Recupera il valore della proprietà timeCondition.
     * 
     * @return
     *     possible object is
     *     {@link TimeCondition }
     *     
     */
    public TimeCondition getTimeCondition() {
        return timeCondition;
    }

    /**
     * Imposta il valore della proprietà timeCondition.
     * 
     * @param value
     *     allowed object is
     *     {@link TimeCondition }
     *     
     */
    public void setTimeCondition(TimeCondition value) {
        this.timeCondition = value;
    }

    /**
     * Recupera il valore della proprietà applicationLayerCondition.
     * 
     * @return
     *     possible object is
     *     {@link ApplicationLayerCondition }
     *     
     */
    public ApplicationLayerCondition getApplicationLayerCondition() {
        return applicationLayerCondition;
    }

    /**
     * Imposta il valore della proprietà applicationLayerCondition.
     * 
     * @param value
     *     allowed object is
     *     {@link ApplicationLayerCondition }
     *     
     */
    public void setApplicationLayerCondition(ApplicationLayerCondition value) {
        this.applicationLayerCondition = value;
    }

    /**
     * Recupera il valore della proprietà qosCondition.
     * 
     * @return
     *     possible object is
     *     {@link QoSCondition }
     *     
     */
    public QoSCondition getQosCondition() {
        return qosCondition;
    }

    /**
     * Imposta il valore della proprietà qosCondition.
     * 
     * @param value
     *     allowed object is
     *     {@link QoSCondition }
     *     
     */
    public void setQosCondition(QoSCondition value) {
        this.qosCondition = value;
    }

}
