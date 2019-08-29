//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.11 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2019.07.31 alle 05:41:07 PM CEST 
//


package it.polito.verefoo.jaxb;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per TypeOfHost.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="TypeOfHost"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="CLIENT"/&gt;
 *     &lt;enumeration value="SERVER"/&gt;
 *     &lt;enumeration value="MIDDLEBOX"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "TypeOfHost")
@XmlEnum
public enum TypeOfHost {

    CLIENT,
    SERVER,
    MIDDLEBOX;

    public String value() {
        return name();
    }

    public static TypeOfHost fromValue(String v) {
        return valueOf(v);
    }

}
