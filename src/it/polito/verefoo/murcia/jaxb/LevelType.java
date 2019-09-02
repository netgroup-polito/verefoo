//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.11 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2019.09.02 alle 08:15:42 PM CEST 
//


package it.polito.verefoo.murcia.jaxb;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per LevelType.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="LevelType"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="child"/&gt;
 *     &lt;enumeration value="adolescent"/&gt;
 *     &lt;enumeration value="pgr"/&gt;
 *     &lt;enumeration value="universal"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "LevelType")
@XmlEnum
public enum LevelType {

    @XmlEnumValue("child")
    CHILD("child"),
    @XmlEnumValue("adolescent")
    ADOLESCENT("adolescent"),
    @XmlEnumValue("pgr")
    PGR("pgr"),
    @XmlEnumValue("universal")
    UNIVERSAL("universal");
    private final String value;

    LevelType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static LevelType fromValue(String v) {
        for (LevelType c: LevelType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
