
package it.polito.verefoo.pojo;

import javax.validation.Valid;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "NFV",
    "additionalProperties"
})
public class Verigraph {

    @JsonProperty("NFV")
    @Valid
    private NFV nfv;
    @JsonProperty("additionalProperties")
    private Object additionalProperties;

    @JsonProperty("NFV")
    public NFV getNfv() {
        return nfv;
    }

    @JsonProperty("NFV")
    public void setNfv(NFV nfv) {
        this.nfv = nfv;
    }

    @JsonProperty("additionalProperties")
    public Object getAdditionalProperties() {
        return additionalProperties;
    }

    @JsonProperty("additionalProperties")
    public void setAdditionalProperties(Object additionalProperties) {
        this.additionalProperties = additionalProperties;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Verigraph.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("nfv");
        sb.append('=');
        sb.append(((this.nfv == null)?"<null>":this.nfv));
        sb.append(',');
        sb.append("additionalProperties");
        sb.append('=');
        sb.append(((this.additionalProperties == null)?"<null>":this.additionalProperties));
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
        result = ((result* 31)+((this.nfv == null)? 0 :this.nfv.hashCode()));
        result = ((result* 31)+((this.additionalProperties == null)? 0 :this.additionalProperties.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Verigraph) == false) {
            return false;
        }
        Verigraph rhs = ((Verigraph) other);
        return (((this.nfv == rhs.nfv)||((this.nfv!= null)&&this.nfv.equals(rhs.nfv)))&&((this.additionalProperties == rhs.additionalProperties)||((this.additionalProperties!= null)&&this.additionalProperties.equals(rhs.additionalProperties))));
    }

}
