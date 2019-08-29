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
 * <p>Classe Java per functionalTypes.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="functionalTypes"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="FIREWALL"/&gt;
 *     &lt;enumeration value="ENDHOST"/&gt;
 *     &lt;enumeration value="ENDPOINT"/&gt;
 *     &lt;enumeration value="ANTISPAM"/&gt;
 *     &lt;enumeration value="CACHE"/&gt;
 *     &lt;enumeration value="DPI"/&gt;
 *     &lt;enumeration value="MAILCLIENT"/&gt;
 *     &lt;enumeration value="MAILSERVER"/&gt;
 *     &lt;enumeration value="NAT"/&gt;
 *     &lt;enumeration value="VPNACCESS"/&gt;
 *     &lt;enumeration value="VPNEXIT"/&gt;
 *     &lt;enumeration value="WEBCLIENT"/&gt;
 *     &lt;enumeration value="WEBSERVER"/&gt;
 *     &lt;enumeration value="FIELDMODIFIER"/&gt;
 *     &lt;enumeration value="FORWARDER"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "functionalTypes")
@XmlEnum
public enum FunctionalTypes {

    FIREWALL,
    ENDHOST,
    ENDPOINT,
    ANTISPAM,
    CACHE,
    DPI,
    MAILCLIENT,
    MAILSERVER,
    NAT,
    VPNACCESS,
    VPNEXIT,
    WEBCLIENT,
    WEBSERVER,
    FIELDMODIFIER,
    FORWARDER;

    public String value() {
        return name();
    }

    public static FunctionalTypes fromValue(String v) {
        return valueOf(v);
    }

}
