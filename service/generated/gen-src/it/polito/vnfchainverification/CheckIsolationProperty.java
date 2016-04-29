
package it.polito.vnfchainverification;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per anonymous complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="source" type="{http://www.example.org/checkisolation}VNFName"/>
 *         &lt;element name="destination" type="{http://www.example.org/checkisolation}VNFName"/>
 *         &lt;element name="VNF" type="{http://www.example.org/checkisolation}VNF" maxOccurs="unbounded" minOccurs="2"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "source",
    "destination",
    "vnf"
})
@XmlRootElement(name = "checkIsolationProperty")
public class CheckIsolationProperty {

    @XmlElement(required = true)
    protected VNFName source;
    @XmlElement(required = true)
    protected VNFName destination;
    @XmlElement(name = "VNF", required = true)
    protected List<VNF> vnf;

    /**
     * Recupera il valore della proprietà source.
     * 
     * @return
     *     possible object is
     *     {@link VNFName }
     *     
     */
    public VNFName getSource() {
        return source;
    }

    /**
     * Imposta il valore della proprietà source.
     * 
     * @param value
     *     allowed object is
     *     {@link VNFName }
     *     
     */
    public void setSource(VNFName value) {
        this.source = value;
    }

    /**
     * Recupera il valore della proprietà destination.
     * 
     * @return
     *     possible object is
     *     {@link VNFName }
     *     
     */
    public VNFName getDestination() {
        return destination;
    }

    /**
     * Imposta il valore della proprietà destination.
     * 
     * @param value
     *     allowed object is
     *     {@link VNFName }
     *     
     */
    public void setDestination(VNFName value) {
        this.destination = value;
    }

    /**
     * Gets the value of the vnf property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the vnf property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getVNF().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link VNF }
     * 
     * 
     */
    public List<VNF> getVNF() {
        if (vnf == null) {
            vnf = new ArrayList<VNF>();
        }
        return this.vnf;
    }

}
