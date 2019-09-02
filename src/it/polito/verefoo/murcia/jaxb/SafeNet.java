//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.11 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2019.09.02 alle 08:15:42 PM CEST 
//


package it.polito.verefoo.murcia.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per SafeNet complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="SafeNet"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="SafeSurfprofanity"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int"&gt;
 *             &lt;minInclusive value="0"/&gt;
 *             &lt;maxInclusive value="4"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="SafeSurfheterosexualthemes"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int"&gt;
 *             &lt;minInclusive value="0"/&gt;
 *             &lt;maxInclusive value="4"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="SafeSurfhomosexualthemes"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int"&gt;
 *             &lt;minInclusive value="0"/&gt;
 *             &lt;maxInclusive value="4"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="SafeSurfviolence"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int"&gt;
 *             &lt;minInclusive value="0"/&gt;
 *             &lt;maxInclusive value="4"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="SafeSurfdruguse"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int"&gt;
 *             &lt;minInclusive value="0"/&gt;
 *             &lt;maxInclusive value="4"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="SafeSurfotheradultthemes"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int"&gt;
 *             &lt;minInclusive value="0"/&gt;
 *             &lt;maxInclusive value="4"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *       &lt;attribute name="SafeSurfgambling"&gt;
 *         &lt;simpleType&gt;
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int"&gt;
 *             &lt;minExclusive value="0"/&gt;
 *             &lt;maxExclusive value="4"/&gt;
 *           &lt;/restriction&gt;
 *         &lt;/simpleType&gt;
 *       &lt;/attribute&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SafeNet")
public class SafeNet {

    @XmlAttribute(name = "SafeSurfprofanity")
    protected Integer safeSurfprofanity;
    @XmlAttribute(name = "SafeSurfheterosexualthemes")
    protected Integer safeSurfheterosexualthemes;
    @XmlAttribute(name = "SafeSurfhomosexualthemes")
    protected Integer safeSurfhomosexualthemes;
    @XmlAttribute(name = "SafeSurfviolence")
    protected Integer safeSurfviolence;
    @XmlAttribute(name = "SafeSurfdruguse")
    protected Integer safeSurfdruguse;
    @XmlAttribute(name = "SafeSurfotheradultthemes")
    protected Integer safeSurfotheradultthemes;
    @XmlAttribute(name = "SafeSurfgambling")
    protected Integer safeSurfgambling;

    /**
     * Recupera il valore della proprietà safeSurfprofanity.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getSafeSurfprofanity() {
        return safeSurfprofanity;
    }

    /**
     * Imposta il valore della proprietà safeSurfprofanity.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setSafeSurfprofanity(Integer value) {
        this.safeSurfprofanity = value;
    }

    /**
     * Recupera il valore della proprietà safeSurfheterosexualthemes.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getSafeSurfheterosexualthemes() {
        return safeSurfheterosexualthemes;
    }

    /**
     * Imposta il valore della proprietà safeSurfheterosexualthemes.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setSafeSurfheterosexualthemes(Integer value) {
        this.safeSurfheterosexualthemes = value;
    }

    /**
     * Recupera il valore della proprietà safeSurfhomosexualthemes.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getSafeSurfhomosexualthemes() {
        return safeSurfhomosexualthemes;
    }

    /**
     * Imposta il valore della proprietà safeSurfhomosexualthemes.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setSafeSurfhomosexualthemes(Integer value) {
        this.safeSurfhomosexualthemes = value;
    }

    /**
     * Recupera il valore della proprietà safeSurfviolence.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getSafeSurfviolence() {
        return safeSurfviolence;
    }

    /**
     * Imposta il valore della proprietà safeSurfviolence.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setSafeSurfviolence(Integer value) {
        this.safeSurfviolence = value;
    }

    /**
     * Recupera il valore della proprietà safeSurfdruguse.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getSafeSurfdruguse() {
        return safeSurfdruguse;
    }

    /**
     * Imposta il valore della proprietà safeSurfdruguse.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setSafeSurfdruguse(Integer value) {
        this.safeSurfdruguse = value;
    }

    /**
     * Recupera il valore della proprietà safeSurfotheradultthemes.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getSafeSurfotheradultthemes() {
        return safeSurfotheradultthemes;
    }

    /**
     * Imposta il valore della proprietà safeSurfotheradultthemes.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setSafeSurfotheradultthemes(Integer value) {
        this.safeSurfotheradultthemes = value;
    }

    /**
     * Recupera il valore della proprietà safeSurfgambling.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getSafeSurfgambling() {
        return safeSurfgambling;
    }

    /**
     * Imposta il valore della proprietà safeSurfgambling.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setSafeSurfgambling(Integer value) {
        this.safeSurfgambling = value;
    }

}
