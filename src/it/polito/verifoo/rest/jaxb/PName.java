//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.8-b130911.1802 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2018.05.31 alle 08:25:09 PM CEST 
//


package it.polito.verifoo.rest.jaxb;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per P-Name.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="P-Name">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="IsolationProperty"/>
 *     &lt;enumeration value="ReachabilityProperty"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
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
