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
 * <p>Classe Java per E-Type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="E-Type"&gt;
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string"&gt;
 *     &lt;enumeration value="XMLValidationError"/&gt;
 *     &lt;enumeration value="InvalidServerClientConf"/&gt;
 *     &lt;enumeration value="InvalidServiceGraph"/&gt;
 *     &lt;enumeration value="PHYClientServerNotConnected"/&gt;
 *     &lt;enumeration value="InvalidPHYServerClientConf"/&gt;
 *     &lt;enumeration value="NoMiddleHostDefined"/&gt;
 *     &lt;enumeration value="InvalidNodeConfiguration"/&gt;
 *     &lt;enumeration value="InvalidVPNConfiguration"/&gt;
 *     &lt;enumeration value="InvalidPropertyDefinition"/&gt;
 *     &lt;enumeration value="InvalidParsingString"/&gt;
 *     &lt;enumeration value="InternalServerError"/&gt;
 *   &lt;/restriction&gt;
 * &lt;/simpleType&gt;
 * </pre>
 * 
 */
@XmlType(name = "E-Type")
@XmlEnum
public enum EType {

    @XmlEnumValue("XMLValidationError")
    XML_VALIDATION_ERROR("XMLValidationError"),
    @XmlEnumValue("InvalidServerClientConf")
    INVALID_SERVER_CLIENT_CONF("InvalidServerClientConf"),
    @XmlEnumValue("InvalidServiceGraph")
    INVALID_SERVICE_GRAPH("InvalidServiceGraph"),
    @XmlEnumValue("PHYClientServerNotConnected")
    PHY_CLIENT_SERVER_NOT_CONNECTED("PHYClientServerNotConnected"),
    @XmlEnumValue("InvalidPHYServerClientConf")
    INVALID_PHY_SERVER_CLIENT_CONF("InvalidPHYServerClientConf"),
    @XmlEnumValue("NoMiddleHostDefined")
    NO_MIDDLE_HOST_DEFINED("NoMiddleHostDefined"),
    @XmlEnumValue("InvalidNodeConfiguration")
    INVALID_NODE_CONFIGURATION("InvalidNodeConfiguration"),
    @XmlEnumValue("InvalidVPNConfiguration")
    INVALID_VPN_CONFIGURATION("InvalidVPNConfiguration"),
    @XmlEnumValue("InvalidPropertyDefinition")
    INVALID_PROPERTY_DEFINITION("InvalidPropertyDefinition"),
    @XmlEnumValue("InvalidParsingString")
    INVALID_PARSING_STRING("InvalidParsingString"),
    @XmlEnumValue("InternalServerError")
    INTERNAL_SERVER_ERROR("InternalServerError");
    private final String value;

    EType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static EType fromValue(String v) {
        for (EType c: EType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }

}
