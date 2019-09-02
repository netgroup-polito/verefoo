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
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per TrafficDivertAction complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="TrafficDivertAction"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}ConfigurationAction"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="TrafficDivertActionType" type="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}TrafficDivertActionType"/&gt;
 *         &lt;element name="packetDivertAction" type="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}TrafficDivertConfigurationCondition"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TrafficDivertAction", propOrder = {
    "trafficDivertActionType",
    "packetDivertAction"
})
@XmlSeeAlso({
    TrafficDivertEncapsulationAction.class
})
public class TrafficDivertAction
    extends ConfigurationAction
{

    @XmlElement(name = "TrafficDivertActionType", required = true)
    @XmlSchemaType(name = "string")
    protected TrafficDivertActionType trafficDivertActionType;
    @XmlElement(required = true)
    protected TrafficDivertConfigurationCondition packetDivertAction;

    /**
     * Recupera il valore della proprietà trafficDivertActionType.
     * 
     * @return
     *     possible object is
     *     {@link TrafficDivertActionType }
     *     
     */
    public TrafficDivertActionType getTrafficDivertActionType() {
        return trafficDivertActionType;
    }

    /**
     * Imposta il valore della proprietà trafficDivertActionType.
     * 
     * @param value
     *     allowed object is
     *     {@link TrafficDivertActionType }
     *     
     */
    public void setTrafficDivertActionType(TrafficDivertActionType value) {
        this.trafficDivertActionType = value;
    }

    /**
     * Recupera il valore della proprietà packetDivertAction.
     * 
     * @return
     *     possible object is
     *     {@link TrafficDivertConfigurationCondition }
     *     
     */
    public TrafficDivertConfigurationCondition getPacketDivertAction() {
        return packetDivertAction;
    }

    /**
     * Imposta il valore della proprietà packetDivertAction.
     * 
     * @param value
     *     allowed object is
     *     {@link TrafficDivertConfigurationCondition }
     *     
     */
    public void setPacketDivertAction(TrafficDivertConfigurationCondition value) {
        this.packetDivertAction = value;
    }

}
