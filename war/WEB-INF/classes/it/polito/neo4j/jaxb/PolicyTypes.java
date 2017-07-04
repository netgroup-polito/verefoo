//
// Questo file � stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.8-b130911.1802 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andr� persa durante la ricompilazione dello schema di origine. 
// Generato il: 2017.03.01 alle 04:27:21 PM CET 
//


package it.polito.neo4j.jaxb;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per policyTypes.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="policyTypes">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="reachability"/>
 *     &lt;enumeration value="traversal"/>
 *     &lt;enumeration value="isolation"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "policyTypes")
@XmlEnum
public enum PolicyTypes {

    @XmlEnumValue("reachability")
    REACHABILITY("reachability"),
    @XmlEnumValue("traversal")
    TRAVERSAL("traversal"),
    @XmlEnumValue("isolation")
    ISOLATION("isolation");
    private final String value;

    PolicyTypes(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static PolicyTypes fromValue(String v) {
        for (PolicyTypes c: PolicyTypes.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
