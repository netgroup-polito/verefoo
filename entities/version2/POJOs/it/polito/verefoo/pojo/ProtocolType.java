
package it.polito.verefoo.pojo;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


/**
 * Application protocol
 * <p>
 * 
 * 
 */
public enum ProtocolType {

    HTTP_REQUEST("HTTP_REQUEST"),
    HTTP_RESPONSE("HTTP_RESPONSE"),
    POP_3_REQUEST("POP3_REQUEST"),
    POP_3_RESPONSE("POP3_RESPONSE");
    private final String value;
    private final static Map<String, ProtocolType> CONSTANTS = new HashMap<String, ProtocolType>();

    static {
        for (ProtocolType c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    private ProtocolType(String value) {
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
    public static ProtocolType fromValue(String value) {
        ProtocolType constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

}
