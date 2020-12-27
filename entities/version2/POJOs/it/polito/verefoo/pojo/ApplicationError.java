
package it.polito.verefoo.pojo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
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
    "detailedCause",
    "cause",
    "recoveryActions"
})
public class ApplicationError {

    @JsonProperty("detailedCause")
    private String detailedCause;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("cause")
    @NotNull
    private ApplicationError.Cause cause;
    @JsonProperty("recoveryActions")
    @Valid
    private List<String> recoveryActions = new ArrayList<String>();

    @JsonProperty("detailedCause")
    public String getDetailedCause() {
        return detailedCause;
    }

    @JsonProperty("detailedCause")
    public void setDetailedCause(String detailedCause) {
        this.detailedCause = detailedCause;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("cause")
    public ApplicationError.Cause getCause() {
        return cause;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("cause")
    public void setCause(ApplicationError.Cause cause) {
        this.cause = cause;
    }

    @JsonProperty("recoveryActions")
    public List<String> getRecoveryActions() {
        return recoveryActions;
    }

    @JsonProperty("recoveryActions")
    public void setRecoveryActions(List<String> recoveryActions) {
        this.recoveryActions = recoveryActions;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(ApplicationError.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("detailedCause");
        sb.append('=');
        sb.append(((this.detailedCause == null)?"<null>":this.detailedCause));
        sb.append(',');
        sb.append("cause");
        sb.append('=');
        sb.append(((this.cause == null)?"<null>":this.cause));
        sb.append(',');
        sb.append("recoveryActions");
        sb.append('=');
        sb.append(((this.recoveryActions == null)?"<null>":this.recoveryActions));
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
        result = ((result* 31)+((this.cause == null)? 0 :this.cause.hashCode()));
        result = ((result* 31)+((this.detailedCause == null)? 0 :this.detailedCause.hashCode()));
        result = ((result* 31)+((this.recoveryActions == null)? 0 :this.recoveryActions.hashCode()));
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
        return ((((this.cause == rhs.cause)||((this.cause!= null)&&this.cause.equals(rhs.cause)))&&((this.detailedCause == rhs.detailedCause)||((this.detailedCause!= null)&&this.detailedCause.equals(rhs.detailedCause))))&&((this.recoveryActions == rhs.recoveryActions)||((this.recoveryActions!= null)&&this.recoveryActions.equals(rhs.recoveryActions))));
    }

    public enum Cause {

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
        private final static Map<String, ApplicationError.Cause> CONSTANTS = new HashMap<String, ApplicationError.Cause>();

        static {
            for (ApplicationError.Cause c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        private Cause(String value) {
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
        public static ApplicationError.Cause fromValue(String value) {
            ApplicationError.Cause constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
