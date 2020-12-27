
package it.polito.verefoo.pojo;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


/**
 * Action type
 * <p>
 * 
 * 
 */
public enum ActionType {

    ALLOW("ALLOW"),
    DENY("DENY");
    private final String value;
    private final static Map<String, ActionType> CONSTANTS = new HashMap<String, ActionType>();

    static {
        for (ActionType c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    private ActionType(String value) {
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
    public static ActionType fromValue(String value) {
        ActionType constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

}
