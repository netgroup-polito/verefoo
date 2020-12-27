
package it.polito.verefoo.pojo;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;


/**
 * Level-four protocol
 * <p>
 * 
 * 
 */
public enum LevelFourProtocolType {

    ANY("ANY"),
    TCP("TCP"),
    UDP("UDP"),
    OTHER("OTHER");
    private final String value;
    private final static Map<String, LevelFourProtocolType> CONSTANTS = new HashMap<String, LevelFourProtocolType>();

    static {
        for (LevelFourProtocolType c: values()) {
            CONSTANTS.put(c.value, c);
        }
    }

    private LevelFourProtocolType(String value) {
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
    public static LevelFourProtocolType fromValue(String value) {
        LevelFourProtocolType constant = CONSTANTS.get(value);
        if (constant == null) {
            throw new IllegalArgumentException(value);
        } else {
            return constant;
        }
    }

}
