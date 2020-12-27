
package it.polito.verefoo.pojo;

import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "vpnaccess"
})
public class Vpnexit {

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("vpnaccess")
    @NotNull
    private String vpnaccess;

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("vpnaccess")
    public String getVpnaccess() {
        return vpnaccess;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("vpnaccess")
    public void setVpnaccess(String vpnaccess) {
        this.vpnaccess = vpnaccess;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Vpnexit.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("vpnaccess");
        sb.append('=');
        sb.append(((this.vpnaccess == null)?"<null>":this.vpnaccess));
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
        result = ((result* 31)+((this.vpnaccess == null)? 0 :this.vpnaccess.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Vpnexit) == false) {
            return false;
        }
        Vpnexit rhs = ((Vpnexit) other);
        return ((this.vpnaccess == rhs.vpnaccess)||((this.vpnaccess!= null)&&this.vpnaccess.equals(rhs.vpnaccess)));
    }

}
