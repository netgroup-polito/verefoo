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
 * <p>Classe Java per HSPL complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="HSPL"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;attribute name="HSPL_id" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *       &lt;attribute name="HSPL_text" type="{http://www.w3.org/2001/XMLSchema}string" /&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "HSPL")
public class HSPL {

    @XmlAttribute(name = "HSPL_id")
    protected String hsplId;
    @XmlAttribute(name = "HSPL_text")
    protected String hsplText;

    /**
     * Recupera il valore della proprietà hsplId.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHSPLId() {
        return hsplId;
    }

    /**
     * Imposta il valore della proprietà hsplId.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHSPLId(String value) {
        this.hsplId = value;
    }

    /**
     * Recupera il valore della proprietà hsplText.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHSPLText() {
        return hsplText;
    }

    /**
     * Imposta il valore della proprietà hsplText.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHSPLText(String value) {
        this.hsplText = value;
    }

}
