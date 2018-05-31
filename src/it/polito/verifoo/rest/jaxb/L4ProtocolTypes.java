//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.8-b130911.1802 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2018.05.31 alle 08:59:38 AM CEST 
//


package it.polito.verifoo.rest.jaxb;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per L4ProtocolTypes.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="L4ProtocolTypes">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="ANY"/>
 *     &lt;enumeration value="TCP"/>
 *     &lt;enumeration value="UDP"/>
 *     &lt;enumeration value="OTHER"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "L4ProtocolTypes")
@XmlEnum
public enum L4ProtocolTypes {

    ANY,
    TCP,
    UDP,
    OTHER;

    public String value() {
        return name();
    }

    public static L4ProtocolTypes fromValue(String v) {
        return valueOf(v);
    }

}
