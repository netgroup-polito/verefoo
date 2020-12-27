
package it.polito.verefoo.pojo;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


/**
 * Allocation constraint type
 * <p>
 * 
 * 
 */
public enum AllocationConstraintType {

    FORBIDDEN("forbidden"),
    FORCED("forced");
    private final String value;
    private final static Map<String, AllocationConstraintType> CONSTANTS = new HashMap<String, AllocationConstraintType>();

    static {
        for (AllocationConstraintType c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    private AllocationConstraintType(String value) {
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
    public static AllocationConstraintType fromValue(String value) {
        AllocationConstraintType constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

}
