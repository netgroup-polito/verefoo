//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.8-b130911.1802 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2018.05.31 alle 08:25:09 PM CEST 
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
 *         &lt;element name="LinkMetrics" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="src" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="dst" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *                 &lt;attribute name="reqLatency" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
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
    "linkMetrics"
})
@XmlRootElement(name = "LinkConstraints")
public class LinkConstraints {

    @XmlElement(name = "LinkMetrics")
    protected List<LinkConstraints.LinkMetrics> linkMetrics;

    /**
     * Gets the value of the linkMetrics property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the linkMetrics property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLinkMetrics().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link LinkConstraints.LinkMetrics }
     * 
     * 
     */
    public List<LinkConstraints.LinkMetrics> getLinkMetrics() {
        if (linkMetrics == null) {
            linkMetrics = new ArrayList<LinkConstraints.LinkMetrics>();
        }
        return this.linkMetrics;
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
     *       &lt;attribute name="src" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="dst" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
     *       &lt;attribute name="reqLatency" use="required" type="{http://www.w3.org/2001/XMLSchema}int" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class LinkMetrics {

        @XmlAttribute(name = "src", required = true)
        protected String src;
        @XmlAttribute(name = "dst", required = true)
        protected String dst;
        @XmlAttribute(name = "reqLatency", required = true)
        protected int reqLatency;

        /**
         * Recupera il valore della proprietà src.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSrc() {
            return src;
        }

        /**
         * Imposta il valore della proprietà src.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSrc(String value) {
            this.src = value;
        }

        /**
         * Recupera il valore della proprietà dst.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getDst() {
            return dst;
        }

        /**
         * Imposta il valore della proprietà dst.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setDst(String value) {
            this.dst = value;
        }

        /**
         * Recupera il valore della proprietà reqLatency.
         * 
         */
        public int getReqLatency() {
            return reqLatency;
        }

        /**
         * Imposta il valore della proprietà reqLatency.
         * 
         */
        public void setReqLatency(int value) {
            this.reqLatency = value;
        }

    }

}
