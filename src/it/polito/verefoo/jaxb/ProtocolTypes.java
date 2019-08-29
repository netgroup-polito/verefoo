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
 * <p>Classe Java per protocolTypes.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="protocolTypes"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="HTTP_REQUEST"/&gt;
 *     &lt;enumeration value="HTTP_RESPONSE"/&gt;
 *     &lt;enumeration value="POP3_REQUEST"/&gt;
 *     &lt;enumeration value="POP3_RESPONSE"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "protocolTypes")
@XmlEnum
public enum ProtocolTypes {

    HTTP_REQUEST("HTTP_REQUEST"),
    HTTP_RESPONSE("HTTP_RESPONSE"),
    @XmlEnumValue("POP3_REQUEST")
    POP_3_REQUEST("POP3_REQUEST"),
    @XmlEnumValue("POP3_RESPONSE")
    POP_3_RESPONSE("POP3_RESPONSE");
    private final String value;

    ProtocolTypes(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static ProtocolTypes fromValue(String v) {
        for (ProtocolTypes c: ProtocolTypes.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
