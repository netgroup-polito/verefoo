//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.8-b130911.1802 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2018.03.27 alle 04:13:36 PM CEST 
//


package it.polito.verifoo.rest.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per Property complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="Property">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="name" use="required" type="{}P-Name" />
 *       &lt;attribute name="graph" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="src" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="dst" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="isSat" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Property")
public class Property {

    @XmlAttribute(name = "name", required = true)
    protected PName name;
    @XmlAttribute(name = "graph", required = true)
    protected long graph;
    @XmlAttribute(name = "src", required = true)
    protected String src;
    @XmlAttribute(name = "dst", required = true)
    protected String dst;
    @XmlAttribute(name = "isSat")
    protected Boolean isSat;

    /**
     * Recupera il valore della proprietà name.
     * 
     * @return
     *     possible object is
     *     {@link PName }
     *     
     */
    public PName getName() {
        return name;
    }

    /**
     * Imposta il valore della proprietà name.
     * 
     * @param value
     *     allowed object is
     *     {@link PName }
     *     
     */
    public void setName(PName value) {
        this.name = value;
    }

    /**
     * Recupera il valore della proprietà graph.
     * 
     */
    public long getGraph() {
        return graph;
    }

    /**
     * Imposta il valore della proprietà graph.
     * 
     */
    public void setGraph(long value) {
        this.graph = value;
    }

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
     * Recupera il valore della proprietà isSat.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isIsSat() {
        return isSat;
    }

    /**
     * Imposta il valore della proprietà isSat.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setIsSat(Boolean value) {
        this.isSat = value;
    }

}
