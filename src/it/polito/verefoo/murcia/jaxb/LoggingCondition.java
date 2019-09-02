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
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per LoggingCondition complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="LoggingCondition"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}ConfigurationCondition"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="eventCondition" type="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}EventCondition" minOccurs="0"/&gt;
 *         &lt;element name="object" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="packetCondition" type="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}PacketFilterCondition" maxOccurs="unbounded" minOccurs="0"/&gt;
 *         &lt;element name="applicationCondition" type="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}ApplicationLayerCondition" maxOccurs="unbounded" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "LoggingCondition", propOrder = {
    "eventCondition",
    "object",
    "packetCondition",
    "applicationCondition"
})
public class LoggingCondition
    extends ConfigurationCondition
{

    protected EventCondition eventCondition;
    protected String object;
    protected List<PacketFilterCondition> packetCondition;
    protected List<ApplicationLayerCondition> applicationCondition;

    /**
     * Recupera il valore della proprietà eventCondition.
     * 
     * @return
     *     possible object is
     *     {@link EventCondition }
     *     
     */
    public EventCondition getEventCondition() {
        return eventCondition;
    }

    /**
     * Imposta il valore della proprietà eventCondition.
     * 
     * @param value
     *     allowed object is
     *     {@link EventCondition }
     *     
     */
    public void setEventCondition(EventCondition value) {
        this.eventCondition = value;
    }

    /**
     * Recupera il valore della proprietà object.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getObject() {
        return object;
    }

    /**
     * Imposta il valore della proprietà object.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setObject(String value) {
        this.object = value;
    }

    /**
     * Gets the value of the packetCondition property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the packetCondition property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPacketCondition().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PacketFilterCondition }
     * 
     * 
     */
    public List<PacketFilterCondition> getPacketCondition() {
        if (packetCondition == null) {
            packetCondition = new ArrayList<PacketFilterCondition>();
        }
        return this.packetCondition;
    }

    /**
     * Gets the value of the applicationCondition property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the applicationCondition property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getApplicationCondition().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ApplicationLayerCondition }
     * 
     * 
     */
    public List<ApplicationLayerCondition> getApplicationCondition() {
        if (applicationCondition == null) {
            applicationCondition = new ArrayList<ApplicationLayerCondition>();
        }
        return this.applicationCondition;
    }

}
