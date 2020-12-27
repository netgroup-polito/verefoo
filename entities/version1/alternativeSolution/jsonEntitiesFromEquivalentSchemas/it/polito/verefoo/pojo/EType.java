
package it.polito.verefoo.pojo;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum EType {

    XML_VALIDATION_ERROR("XMLValidationError"),
    INVALID_SERVER_CLIENT_CONF("InvalidServerClientConf"),
    INVALID_SERVICE_GRAPH("InvalidServiceGraph"),
    PHY_CLIENT_SERVER_NOT_CONNECTED("PHYClientServerNotConnected"),
    INVALID_PHY_SERVER_CLIENT_CONF("InvalidPHYServerClientConf"),
    NO_MIDDLE_HOST_DEFINED("NoMiddleHostDefined"),
    INVALID_NODE_CONFIGURATION("InvalidNodeConfiguration"),
    INVALID_VPN_CONFIGURATION("InvalidVPNConfiguration"),
    INVALID_PROPERTY_DEFINITION("InvalidPropertyDefinition"),
    INVALID_PARSING_STRING("InvalidParsingString"),
    INTERNAL_SERVER_ERROR("InternalServerError");
    private final String value;
    private final static Map<String, EType> CONSTANTS = new HashMap<String, EType>();

    static {
        for (EType c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    private EType(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }

    @JsonValue
    public String value() {
        return this.value;
    }

    @JsonCreator
    public static EType fromValue(String value) {
        EType constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

}
