//
// Questo file è stato generato dall'architettura JavaTM per XML Binding (JAXB) Reference Implementation, v2.2.8-b130911.1802 
// Vedere <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Qualsiasi modifica a questo file andrà persa durante la ricompilazione dello schema di origine. 
// Generato il: 2018.01.18 alle 04:37:52 PM CET 
//


package it.polito.verifoo.rest.jaxb;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Classe Java per E-Type.
 * 
 * <p>Il seguente frammento di schema specifica il contenuto previsto contenuto in questa classe.
 * <p>
 * <pre>
 * &lt;simpleType name="E-Type">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="XMLValidationError"/>
 *     &lt;enumeration value="InvalidServerClientConf"/>
 *     &lt;enumeration value="InvalidNodeChain"/>
 *     &lt;enumeration value="PHYClientServerNotConnected"/>
 *     &lt;enumeration value="InvalidPHYServerClientConf"/>
 *     &lt;enumeration value="NoMiddleHostDefined"/>
 *     &lt;enumeration value="InvalidNodeConfiguration"/>
 *     &lt;enumeration value="InvalidVPNConfiguration"/>
 *     &lt;enumeration value="InvalidPropertyDefinition"/>
 *     &lt;enumeration value="InvalidParsingString"/>
 *     &lt;enumeration value="InternalServerError"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
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
    @XmlEnumValue("InvalidNodeChain")
    INVALID_NODE_CHAIN("InvalidNodeChain"),
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
