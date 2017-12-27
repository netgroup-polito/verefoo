//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.8-b130911.1802 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2017.12.27 alle 04:27:08 PM CET 
//


package it.polito.verifoo.rest.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
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
 *       &lt;attribute name="vpnexit" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "vpnaccess")
public class Vpnaccess {

    @XmlAttribute(name = "vpnexit", required = true)
    protected String vpnexit;

    /**
     * Recupera il valore della proprietà vpnexit.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getVpnexit() {
        return vpnexit;
    }

    /**
     * Imposta il valore della proprietà vpnexit.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setVpnexit(String value) {
        this.vpnexit = value;
    }

}
