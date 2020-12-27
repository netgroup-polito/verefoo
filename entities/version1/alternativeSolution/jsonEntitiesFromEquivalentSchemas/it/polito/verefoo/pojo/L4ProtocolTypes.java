
package it.polito.verefoo.pojo;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum L4ProtocolTypes {

    ANY("ANY"),
    TCP("TCP"),
    UDP("UDP"),
    OTHER("OTHER");
    private final String value;
    private final static Map<String, L4ProtocolTypes> CONSTANTS = new HashMap<String, L4ProtocolTypes>();

    static {
        for (L4ProtocolTypes c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    private L4ProtocolTypes(String value) {
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
    public static L4ProtocolTypes fromValue(String value) {
        L4ProtocolTypes constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

}
