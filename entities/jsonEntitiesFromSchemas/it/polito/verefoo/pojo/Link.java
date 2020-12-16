
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
    "relationship",
    "referencedResourceURI",
    "method",
    "contentType"
})
public class Link {

    @JsonProperty("relationship")
    private String relationship;
    @JsonProperty("referencedResourceURI")
    private String referencedResourceURI;
    @JsonProperty("method")
    private Link.Method method;
    @JsonProperty("contentType")
    private String contentType;

    @JsonProperty("relationship")
    public String getRelationship() {
        return relationship;
    }

    @JsonProperty("relationship")
    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    @JsonProperty("referencedResourceURI")
    public String getReferencedResourceURI() {
        return referencedResourceURI;
    }

    @JsonProperty("referencedResourceURI")
    public void setReferencedResourceURI(String referencedResourceURI) {
        this.referencedResourceURI = referencedResourceURI;
    }

    @JsonProperty("method")
    public Link.Method getMethod() {
        return method;
    }

    @JsonProperty("method")
    public void setMethod(Link.Method method) {
        this.method = method;
    }

    @JsonProperty("contentType")
    public String getContentType() {
        return contentType;
    }

    @JsonProperty("contentType")
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Link.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("relationship");
        sb.append('=');
        sb.append(((this.relationship == null)?"<null>":this.relationship));
        sb.append(',');
        sb.append("referencedResourceURI");
        sb.append('=');
        sb.append(((this.referencedResourceURI == null)?"<null>":this.referencedResourceURI));
        sb.append(',');
        sb.append("method");
        sb.append('=');
        sb.append(((this.method == null)?"<null>":this.method));
        sb.append(',');
        sb.append("contentType");
        sb.append('=');
        sb.append(((this.contentType == null)?"<null>":this.contentType));
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
        result = ((result* 31)+((this.relationship == null)? 0 :this.relationship.hashCode()));
        result = ((result* 31)+((this.method == null)? 0 :this.method.hashCode()));
        result = ((result* 31)+((this.contentType == null)? 0 :this.contentType.hashCode()));
        result = ((result* 31)+((this.referencedResourceURI == null)? 0 :this.referencedResourceURI.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Link) == false) {
            return false;
        }
        Link rhs = ((Link) other);
        return (((((this.relationship == rhs.relationship)||((this.relationship!= null)&&this.relationship.equals(rhs.relationship)))&&((this.method == rhs.method)||((this.method!= null)&&this.method.equals(rhs.method))))&&((this.contentType == rhs.contentType)||((this.contentType!= null)&&this.contentType.equals(rhs.contentType))))&&((this.referencedResourceURI == rhs.referencedResourceURI)||((this.referencedResourceURI!= null)&&this.referencedResourceURI.equals(rhs.referencedResourceURI))));
    }

    public enum Method {

        GET("GET"),
        POST("POST"),
        PUT("PUT"),
        DELETE("DELETE"),
        PATCH("PATCH"),
        HEAD("HEAD"),
        CONNECT("CONNECT"),
        OPTIONS("OPTIONS"),
        TRACE("TRACE");
        private final String value;
        private final static Map<String, Link.Method> CONSTANTS = new HashMap<String, Link.Method>();

        static {
            for (Link.Method c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        private Method(String value) {
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
        public static Link.Method fromValue(String value) {
            Link.Method constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

}
