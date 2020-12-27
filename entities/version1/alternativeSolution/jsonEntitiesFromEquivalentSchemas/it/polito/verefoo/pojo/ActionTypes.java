
package it.polito.verefoo.pojo;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ActionTypes {

    ALLOW("ALLOW"),
    DENY("DENY");
    private final String value;
    private final static Map<String, ActionTypes> CONSTANTS = new HashMap<String, ActionTypes>();

    static {
        for (ActionTypes c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    private ActionTypes(String value) {
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
    public static ActionTypes fromValue(String value) {
        ActionTypes constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

}
