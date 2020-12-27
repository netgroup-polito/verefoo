
package it.polito.verefoo.pojo;

import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "vpnexit"
})
public class Vpnaccess {

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("vpnexit")
    @NotNull
    private String vpnexit;

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("vpnexit")
    public String getVpnexit() {
        return vpnexit;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("vpnexit")
    public void setVpnexit(String vpnexit) {
        this.vpnexit = vpnexit;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Vpnaccess.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("vpnexit");
        sb.append('=');
        sb.append(((this.vpnexit == null)?"<null>":this.vpnexit));
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
        result = ((result* 31)+((this.vpnexit == null)? 0 :this.vpnexit.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Vpnaccess) == false) {
            return false;
        }
        Vpnaccess rhs = ((Vpnaccess) other);
        return ((this.vpnexit == rhs.vpnexit)||((this.vpnexit!= null)&&this.vpnexit.equals(rhs.vpnexit)));
    }

}
