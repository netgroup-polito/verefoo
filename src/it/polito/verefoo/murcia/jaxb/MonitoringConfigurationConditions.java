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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per MonitoringConfigurationConditions complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="MonitoringConfigurationConditions"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}ConfigurationCondition"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ProbeID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/&gt;
 *         &lt;element name="monitoringConfigurationCondition" type="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}MonitoringConfigurationCondition" maxOccurs="unbounded"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MonitoringConfigurationConditions", propOrder = {
    "probeID",
    "monitoringConfigurationCondition"
})
public class MonitoringConfigurationConditions
    extends ConfigurationCondition
{

    @XmlElement(name = "ProbeID")
    protected String probeID;
    @XmlElement(required = true)
    protected List<MonitoringConfigurationCondition> monitoringConfigurationCondition;

    /**
     * Recupera il valore della proprietà probeID.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProbeID() {
        return probeID;
    }

    /**
     * Imposta il valore della proprietà probeID.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProbeID(String value) {
        this.probeID = value;
    }

    /**
     * Gets the value of the monitoringConfigurationCondition property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the monitoringConfigurationCondition property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getMonitoringConfigurationCondition().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link MonitoringConfigurationCondition }
     * 
     * 
     */
    public List<MonitoringConfigurationCondition> getMonitoringConfigurationCondition() {
        if (monitoringConfigurationCondition == null) {
            monitoringConfigurationCondition = new ArrayList<MonitoringConfigurationCondition>();
        }
        return this.monitoringConfigurationCondition;
    }

}
