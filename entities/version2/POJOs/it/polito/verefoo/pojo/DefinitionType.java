
package it.polito.verefoo.pojo;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


/**
 * Application protocol definition
 * <p>
 * 
 * 
 */
public enum DefinitionType {

    POP_3_DEFINITION("POP3Definition"),
    HTTP_DEFINITION("HTTPDefinition");
    private final String value;
    private final static Map<String, DefinitionType> CONSTANTS = new HashMap<String, DefinitionType>();

    static {
        for (DefinitionType c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    private DefinitionType(String value) {
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
    public static DefinitionType fromValue(String value) {
        DefinitionType constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

}
