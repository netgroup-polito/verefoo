//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.11 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2019.07.31 alle 05:41:07 PM CEST 
//


package it.polito.verefoo.jaxb;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per P-Name.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="P-Name"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="IsolationProperty"/&gt;
 *     &lt;enumeration value="ReachabilityProperty"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "P-Name")
@XmlEnum
public enum PName {

    @XmlEnumValue("IsolationProperty")
    ISOLATION_PROPERTY("IsolationProperty"),
    @XmlEnumValue("ReachabilityProperty")
    REACHABILITY_PROPERTY("ReachabilityProperty");
    private final String value;

    PName(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static PName fromValue(String v) {
        for (PName c: PName.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
