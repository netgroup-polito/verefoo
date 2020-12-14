
package it.polito.verefoo.pojo;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;


/**
 * applicationError
 * <p>
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "message",
    "type"
})
public class ApplicationError {

    @JsonProperty("message")
    private String message;
    @JsonProperty("type")
    private ApplicationError.ErrorType type;

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    @JsonProperty("message")
    public void setMessage(String message) {
        this.message = message;
    }

    @JsonProperty("type")
    public ApplicationError.ErrorType getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(ApplicationError.ErrorType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ApplicationError.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("message");
        sb.append('=');
        sb.append(((this.message == null)?"<null>":this.message));
        sb.append(',');
        sb.append("type");
        sb.append('=');
        sb.append(((this.type == null)?"<null>":this.type));
        sb.append(',');
        if (sb.charAt((sb.length()- 1)) == ',') {
            sb.setCharAt((sb.length()- 1), ']');
        } else {
            sb.append(']');
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = ((result* 31)+((this.message == null)? 0 :this.message.hashCode()));
        result = ((result* 31)+((this.type == null)? 0 :this.type.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof ApplicationError) == false) {
            return false;
        }
        ApplicationError rhs = ((ApplicationError) other);
        return (((this.message == rhs.message)||((this.message!= null)&&this.message.equals(rhs.message)))&&((this.type == rhs.type)||((this.type!= null)&&this.type.equals(rhs.type))));
    }

    public enum ErrorType {

        JSON_VALIDATION_ERROR("JSONValidationError"),
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
        private final static Map<String, ApplicationError.ErrorType> CONSTANTS = new HashMap<String, ApplicationError.ErrorType>();

        static {
            for (ApplicationError.ErrorType c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        private ErrorType(String value) {
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
        public static ApplicationError.ErrorType fromValue(String value) {
            ApplicationError.ErrorType constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
