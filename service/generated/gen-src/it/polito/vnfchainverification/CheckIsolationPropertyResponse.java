
package it.polito.vnfchainverification;

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
 *         &lt;element name="Satisfied" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
    "satisfied"
})
@XmlRootElement(name = "checkIsolationPropertyResponse")
public class CheckIsolationPropertyResponse {

    @XmlElement(name = "Satisfied")
    protected boolean satisfied;

    /**
     * Recupera il valore della proprietà satisfied.
     * 
     */
    public boolean isSatisfied() {
        return satisfied;
    }

    /**
     * Imposta il valore della proprietà satisfied.
     * 
     */
    public void setSatisfied(boolean value) {
        this.satisfied = value;
    }

}
