
package it.polito.verefoo.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "privateAddress"
})
public class Nat {

    @JsonProperty("privateAddress")
    private String privateAddress;

    @JsonProperty("privateAddress")
    public String getPrivateAddress() {
        return privateAddress;
    }

    @JsonProperty("privateAddress")
    public void setPrivateAddress(String privateAddress) {
        this.privateAddress = privateAddress;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Nat.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("privateAddress");
        sb.append('=');
        sb.append(((this.privateAddress == null)?"<null>":this.privateAddress));
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
        result = ((result* 31)+((this.privateAddress == null)? 0 :this.privateAddress.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Nat) == false) {
            return false;
        }
        Nat rhs = ((Nat) other);
        return ((this.privateAddress == rhs.privateAddress)||((this.privateAddress!= null)&&this.privateAddress.equals(rhs.privateAddress)));
    }

}
