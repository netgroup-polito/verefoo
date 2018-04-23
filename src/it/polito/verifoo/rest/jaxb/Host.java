//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.8-b130911.1802 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2018.04.23 alle 03:30:02 PM CEST 
//


package it.polito.verifoo.rest.jaxb;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
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
 *         &lt;element name="SupportedVNF" type="{}SupportedVNFType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="NodeRef" type="{}NodeRefType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="cpu" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="cores" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="diskStorage" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="memory" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="maxVNF" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="type" type="{}TypeOfHost" />
 *       &lt;attribute name="fixedEndpoint" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="active" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "supportedVNF",
    "nodeRef"
})
@XmlRootElement(name = "Host")
public class Host {

    @XmlElement(name = "SupportedVNF")
    protected List<SupportedVNFType> supportedVNF;
    @XmlElement(name = "NodeRef")
    protected List<NodeRefType> nodeRef;
    @XmlAttribute(name = "name", required = true)
    protected String name;
    @XmlAttribute(name = "cpu", required = true)
    protected int cpu;
    @XmlAttribute(name = "cores", required = true)
    protected int cores;
    @XmlAttribute(name = "diskStorage", required = true)
    protected int diskStorage;
    @XmlAttribute(name = "memory", required = true)
    protected int memory;
    @XmlAttribute(name = "maxVNF")
    protected Integer maxVNF;
    @XmlAttribute(name = "type")
    protected TypeOfHost type;
    @XmlAttribute(name = "fixedEndpoint")
    protected String fixedEndpoint;
    @XmlAttribute(name = "active")
    protected Boolean active;

    /**
     * Gets the value of the supportedVNF property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the supportedVNF property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSupportedVNF().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SupportedVNFType }
     * 
     * 
     */
    public List<SupportedVNFType> getSupportedVNF() {
        if (supportedVNF == null) {
            supportedVNF = new ArrayList<SupportedVNFType>();
        }
        return this.supportedVNF;
    }

    /**
     * Gets the value of the nodeRef property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the nodeRef property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNodeRef().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link NodeRefType }
     * 
     * 
     */
    public List<NodeRefType> getNodeRef() {
        if (nodeRef == null) {
            nodeRef = new ArrayList<NodeRefType>();
        }
        return this.nodeRef;
    }

    /**
     * Recupera il valore della proprietà name.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Imposta il valore della proprietà name.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Recupera il valore della proprietà cpu.
     * 
     */
    public int getCpu() {
        return cpu;
    }

    /**
     * Imposta il valore della proprietà cpu.
     * 
     */
    public void setCpu(int value) {
        this.cpu = value;
    }

    /**
     * Recupera il valore della proprietà cores.
     * 
     */
    public int getCores() {
        return cores;
    }

    /**
     * Imposta il valore della proprietà cores.
     * 
     */
    public void setCores(int value) {
        this.cores = value;
    }

    /**
     * Recupera il valore della proprietà diskStorage.
     * 
     */
    public int getDiskStorage() {
        return diskStorage;
    }

    /**
     * Imposta il valore della proprietà diskStorage.
     * 
     */
    public void setDiskStorage(int value) {
        this.diskStorage = value;
    }

    /**
     * Recupera il valore della proprietà memory.
     * 
     */
    public int getMemory() {
        return memory;
    }

    /**
     * Imposta il valore della proprietà memory.
     * 
     */
    public void setMemory(int value) {
        this.memory = value;
    }

    /**
     * Recupera il valore della proprietà maxVNF.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getMaxVNF() {
        return maxVNF;
    }

    /**
     * Imposta il valore della proprietà maxVNF.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setMaxVNF(Integer value) {
        this.maxVNF = value;
    }

    /**
     * Recupera il valore della proprietà type.
     * 
     * @return
     *     possible object is
     *     {@link TypeOfHost }
     *     
     */
    public TypeOfHost getType() {
        return type;
    }

    /**
     * Imposta il valore della proprietà type.
     * 
     * @param value
     *     allowed object is
     *     {@link TypeOfHost }
     *     
     */
    public void setType(TypeOfHost value) {
        this.type = value;
    }

    /**
     * Recupera il valore della proprietà fixedEndpoint.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFixedEndpoint() {
        return fixedEndpoint;
    }

    /**
     * Imposta il valore della proprietà fixedEndpoint.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFixedEndpoint(String value) {
        this.fixedEndpoint = value;
    }

    /**
     * Recupera il valore della proprietà active.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public boolean isActive() {
        if (active == null) {
            return false;
        } else {
            return active;
        }
    }

    /**
     * Imposta il valore della proprietà active.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setActive(Boolean value) {
        this.active = value;
    }

}
