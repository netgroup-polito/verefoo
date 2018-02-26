//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.8-b130911.1802 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2018.02.26 alle 10:45:10 AM CET 
//


package it.polito.verifoo.rest.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per SupportedVNFType complex type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * 
 * <pre>
 * &lt;complexType name="SupportedVNFType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="functional_type" use="required" type="{}functionalTypes" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SupportedVNFType")
public class SupportedVNFType {

    @XmlAttribute(name = "functional_type", required = true)
    protected FunctionalTypes functionalType;

    /**
     * Recupera il valore della proprietà functionalType.
     * 
     * @return
     *     possible object is
     *     {@link FunctionalTypes }
     *     
     */
    public FunctionalTypes getFunctionalType() {
        return functionalType;
    }

    /**
     * Imposta il valore della proprietà functionalType.
     * 
     * @param value
     *     allowed object is
     *     {@link FunctionalTypes }
     *     
     */
    public void setFunctionalType(FunctionalTypes value) {
        this.functionalType = value;
    }

}
