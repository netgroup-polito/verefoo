//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.8-b130911.1802 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2018.05.27 alle 03:34:03 PM CEST 
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
 *         &lt;element name="NodeMetrics" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="node" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="nrOfOperations" type="{http://www.w3.org/2001/XMLSchema}long" />
 *                 &lt;attribute name="maxNodeLatency" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                 &lt;attribute name="reqStorage" type="{http://www.w3.org/2001/XMLSchema}int" default="0" />
 *                 &lt;attribute name="cores" type="{http://www.w3.org/2001/XMLSchema}int" default="0" />
 *                 &lt;attribute name="memory" type="{http://www.w3.org/2001/XMLSchema}int" default="0" />
 *                 &lt;attribute name="optional" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
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
    "nodeMetrics"
})
@XmlRootElement(name = "NodeConstraints")
public class NodeConstraints {

    @XmlElement(name = "NodeMetrics")
    protected List<NodeConstraints.NodeMetrics> nodeMetrics;

    /**
     * Gets the value of the nodeMetrics property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the nodeMetrics property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getNodeMetrics().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link NodeConstraints.NodeMetrics }
     * 
     * 
     */
    public List<NodeConstraints.NodeMetrics> getNodeMetrics() {
        if (nodeMetrics == null) {
            nodeMetrics = new ArrayList<NodeConstraints.NodeMetrics>();
        }
        return this.nodeMetrics;
    }


    /**
     * <p>Classe Java per anonymous complex type.
     * 
     * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;attribute name="node" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="nrOfOperations" type="{http://www.w3.org/2001/XMLSchema}long" />
     *       &lt;attribute name="maxNodeLatency" type="{http://www.w3.org/2001/XMLSchema}int" />
     *       &lt;attribute name="reqStorage" type="{http://www.w3.org/2001/XMLSchema}int" default="0" />
     *       &lt;attribute name="cores" type="{http://www.w3.org/2001/XMLSchema}int" default="0" />
     *       &lt;attribute name="memory" type="{http://www.w3.org/2001/XMLSchema}int" default="0" />
     *       &lt;attribute name="optional" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class NodeMetrics {

        @XmlAttribute(name = "node", required = true)
        protected String node;
        @XmlAttribute(name = "nrOfOperations")
        protected Long nrOfOperations;
        @XmlAttribute(name = "maxNodeLatency")
        protected Integer maxNodeLatency;
        @XmlAttribute(name = "reqStorage")
        protected Integer reqStorage;
        @XmlAttribute(name = "cores")
        protected Integer cores;
        @XmlAttribute(name = "memory")
        protected Integer memory;
        @XmlAttribute(name = "optional")
        protected Boolean optional;

        /**
         * Recupera il valore della proprietà node.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getNode() {
            return node;
        }

        /**
         * Imposta il valore della proprietà node.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setNode(String value) {
            this.node = value;
        }

        /**
         * Recupera il valore della proprietà nrOfOperations.
         * 
         * @return
         *     possible object is
         *     {@link Long }
         *     
         */
        public Long getNrOfOperations() {
            return nrOfOperations;
        }

        /**
         * Imposta il valore della proprietà nrOfOperations.
         * 
         * @param value
         *     allowed object is
         *     {@link Long }
         *     
         */
        public void setNrOfOperations(Long value) {
            this.nrOfOperations = value;
        }

        /**
         * Recupera il valore della proprietà maxNodeLatency.
         * 
         * @return
         *     possible object is
         *     {@link Integer }
         *     
         */
        public Integer getMaxNodeLatency() {
            return maxNodeLatency;
        }

        /**
         * Imposta il valore della proprietà maxNodeLatency.
         * 
         * @param value
         *     allowed object is
         *     {@link Integer }
         *     
         */
        public void setMaxNodeLatency(Integer value) {
            this.maxNodeLatency = value;
        }

        /**
         * Recupera il valore della proprietà reqStorage.
         * 
         * @return
         *     possible object is
         *     {@link Integer }
         *     
         */
        public int getReqStorage() {
            if (reqStorage == null) {
                return  0;
            } else {
                return reqStorage;
            }
        }

        /**
         * Imposta il valore della proprietà reqStorage.
         * 
         * @param value
         *     allowed object is
         *     {@link Integer }
         *     
         */
        public void setReqStorage(Integer value) {
            this.reqStorage = value;
        }

        /**
         * Recupera il valore della proprietà cores.
         * 
         * @return
         *     possible object is
         *     {@link Integer }
         *     
         */
        public int getCores() {
            if (cores == null) {
                return  0;
            } else {
                return cores;
            }
        }

        /**
         * Imposta il valore della proprietà cores.
         * 
         * @param value
         *     allowed object is
         *     {@link Integer }
         *     
         */
        public void setCores(Integer value) {
            this.cores = value;
        }

        /**
         * Recupera il valore della proprietà memory.
         * 
         * @return
         *     possible object is
         *     {@link Integer }
         *     
         */
        public int getMemory() {
            if (memory == null) {
                return  0;
            } else {
                return memory;
            }
        }

        /**
         * Imposta il valore della proprietà memory.
         * 
         * @param value
         *     allowed object is
         *     {@link Integer }
         *     
         */
        public void setMemory(Integer value) {
            this.memory = value;
        }

        /**
         * Recupera il valore della proprietà optional.
         * 
         * @return
         *     possible object is
         *     {@link Boolean }
         *     
         */
        public boolean isOptional() {
            if (optional == null) {
                return false;
            } else {
                return optional;
            }
        }

        /**
         * Imposta il valore della proprietà optional.
         * 
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *     
         */
        public void setOptional(Boolean value) {
            this.optional = value;
        }

    }

}
