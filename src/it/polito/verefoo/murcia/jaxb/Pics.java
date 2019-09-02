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
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per Pics complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="Pics"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="ICRA" type="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}ICRA"/&gt;
 *         &lt;element name="RSAC" type="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}RSAC"/&gt;
 *         &lt;element name="evaluWEB"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int"&gt;
 *               &lt;minInclusive value="0"/&gt;
 *               &lt;maxInclusive value="2"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="CyberNOTsex"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int"&gt;
 *               &lt;minInclusive value="0"/&gt;
 *               &lt;maxInclusive value="8"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="Weburbia"&gt;
 *           &lt;simpleType&gt;
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int"&gt;
 *               &lt;minInclusive value="0"/&gt;
 *               &lt;maxInclusive value="2"/&gt;
 *             &lt;/restriction&gt;
 *           &lt;/simpleType&gt;
 *         &lt;/element&gt;
 *         &lt;element name="Vancouver" type="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}Vancouver"/&gt;
 *         &lt;element name="SafeNet" type="{http://modeliosoft/xsddesigner/a22bd60b-ee3d-425c-8618-beb6a854051a/ITResource.xsd}SafeNet"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Pics", propOrder = {
    "icra",
    "rsac",
    "evaluWEB",
    "cyberNOTsex",
    "weburbia",
    "vancouver",
    "safeNet"
})
public class Pics {

    @XmlElement(name = "ICRA", required = true)
    protected ICRA icra;
    @XmlElement(name = "RSAC", required = true)
    protected RSAC rsac;
    protected int evaluWEB;
    @XmlElement(name = "CyberNOTsex")
    protected int cyberNOTsex;
    @XmlElement(name = "Weburbia")
    protected int weburbia;
    @XmlElement(name = "Vancouver", required = true)
    protected Vancouver vancouver;
    @XmlElement(name = "SafeNet", required = true)
    protected SafeNet safeNet;

    /**
     * Recupera il valore della proprietà icra.
     * 
     * @return
     *     possible object is
     *     {@link ICRA }
     *     
     */
    public ICRA getICRA() {
        return icra;
    }

    /**
     * Imposta il valore della proprietà icra.
     * 
     * @param value
     *     allowed object is
     *     {@link ICRA }
     *     
     */
    public void setICRA(ICRA value) {
        this.icra = value;
    }

    /**
     * Recupera il valore della proprietà rsac.
     * 
     * @return
     *     possible object is
     *     {@link RSAC }
     *     
     */
    public RSAC getRSAC() {
        return rsac;
    }

    /**
     * Imposta il valore della proprietà rsac.
     * 
     * @param value
     *     allowed object is
     *     {@link RSAC }
     *     
     */
    public void setRSAC(RSAC value) {
        this.rsac = value;
    }

    /**
     * Recupera il valore della proprietà evaluWEB.
     * 
     */
    public int getEvaluWEB() {
        return evaluWEB;
    }

    /**
     * Imposta il valore della proprietà evaluWEB.
     * 
     */
    public void setEvaluWEB(int value) {
        this.evaluWEB = value;
    }

    /**
     * Recupera il valore della proprietà cyberNOTsex.
     * 
     */
    public int getCyberNOTsex() {
        return cyberNOTsex;
    }

    /**
     * Imposta il valore della proprietà cyberNOTsex.
     * 
     */
    public void setCyberNOTsex(int value) {
        this.cyberNOTsex = value;
    }

    /**
     * Recupera il valore della proprietà weburbia.
     * 
     */
    public int getWeburbia() {
        return weburbia;
    }

    /**
     * Imposta il valore della proprietà weburbia.
     * 
     */
    public void setWeburbia(int value) {
        this.weburbia = value;
    }

    /**
     * Recupera il valore della proprietà vancouver.
     * 
     * @return
     *     possible object is
     *     {@link Vancouver }
     *     
     */
    public Vancouver getVancouver() {
        return vancouver;
    }

    /**
     * Imposta il valore della proprietà vancouver.
     * 
     * @param value
     *     allowed object is
     *     {@link Vancouver }
     *     
     */
    public void setVancouver(Vancouver value) {
        this.vancouver = value;
    }

    /**
     * Recupera il valore della proprietà safeNet.
     * 
     * @return
     *     possible object is
     *     {@link SafeNet }
     *     
     */
    public SafeNet getSafeNet() {
        return safeNet;
    }

    /**
     * Imposta il valore della proprietà safeNet.
     * 
     * @param value
     *     allowed object is
     *     {@link SafeNet }
     *     
     */
    public void setSafeNet(SafeNet value) {
        this.safeNet = value;
    }

}
