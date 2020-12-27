
package it.polito.verefoo.pojo;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ProtocolTypes {

    HTTP_REQUEST("HTTP_REQUEST"),
    HTTP_RESPONSE("HTTP_RESPONSE"),
    POP_3_REQUEST("POP3_REQUEST"),
    POP_3_RESPONSE("POP3_RESPONSE");
    private final String value;
    private final static Map<String, ProtocolTypes> CONSTANTS = new HashMap<String, ProtocolTypes>();

    static {
        for (ProtocolTypes c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    private ProtocolTypes(String value) {
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
    public static ProtocolTypes fromValue(String value) {
        ProtocolTypes constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

}
