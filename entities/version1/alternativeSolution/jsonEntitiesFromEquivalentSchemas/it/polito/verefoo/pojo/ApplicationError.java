
package it.polito.verefoo.pojo;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "type",
    "message"
})
public class ApplicationError {

    @JsonProperty("type")
    private ApplicationError.Type type;
    @JsonProperty("message")
    private String message;

    @JsonProperty("type")
    public ApplicationError.Type getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(ApplicationError.Type type) {
        this.type = type;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    @JsonProperty("message")
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ApplicationError.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("type");
        sb.append('=');
        sb.append(((this.type == null)?"<null>":this.type));
        sb.append(',');
        sb.append("message");
        sb.append('=');
        sb.append(((this.message == null)?"<null>":this.message));
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
        result = ((result* 31)+((this.type == null)? 0 :this.type.hashCode()));
        result = ((result* 31)+((this.message == null)? 0 :this.message.hashCode()));
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
        return (((this.type == rhs.type)||((this.type!= null)&&this.type.equals(rhs.type)))&&((this.message == rhs.message)||((this.message!= null)&&this.message.equals(rhs.message))));
    }

    public enum Type {

        XML_VALIDATION_ERROR("XML_VALIDATION_ERROR"),
        INVALID_SERVER_CLIENT_CONF("INVALID_SERVER_CLIENT_CONF"),
        INVALID_SERVICE_GRAPH("INVALID_SERVICE_GRAPH"),
        PHY_CLIENT_SERVER_NOT_CONNECTED("PHY_CLIENT_SERVER_NOT_CONNECTED"),
        INVALID_PHY_SERVER_CLIENT_CONF("INVALID_PHY_SERVER_CLIENT_CONF"),
        NO_MIDDLE_HOST_DEFINED("NO_MIDDLE_HOST_DEFINED"),
        INVALID_NODE_CONFIGURATION("INVALID_NODE_CONFIGURATION"),
        INVALID_VPN_CONFIGURATION("INVALID_VPN_CONFIGURATION"),
        INVALID_PROPERTY_DEFINITION("INVALID_PROPERTY_DEFINITION"),
        INVALID_PARSING_STRING("INVALID_PARSING_STRING"),
        INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR");
        private final String value;
        private final static Map<String, ApplicationError.Type> CONSTANTS = new HashMap<String, ApplicationError.Type>();

        static {
            for (ApplicationError.Type c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        private Type(String value) {
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
        public static ApplicationError.Type fromValue(String value) {
            ApplicationError.Type constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
