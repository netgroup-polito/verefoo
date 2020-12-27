
package it.polito.verefoo.pojo;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * vpnAccess
 * <p>
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "vpnexit"
})
public class VpnAccess
    extends NetworkFunction
{

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("vpnexit")
    @Pattern(regexp = "^([0-9]|([1-9][0-9])|(1[0-9][0-9])|(2[0-4][0-9])|(25[0-5]))([.]([0-9]|([1-9][0-9])|(1[0-9][0-9])|(2[0-4][0-9])|(25[0-5]))){3}$")
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
        sb.append(VpnAccess.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        int baseLength = sb.length();
        String superString = super.toString();
        if (superString!= null) {
            int contentStart = superString.indexOf('[');
            int contentEnd = superString.lastIndexOf(']');
            if ((contentStart >= 0)&&(contentEnd >contentStart)) {
                sb.append(superString, (contentStart + 1), contentEnd);
            } else {
                sb.append(superString);
            }
        }
        if (sb.length()>baseLength) {
            sb.append(',');
        }
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
        result = ((result* 31)+ super.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof VpnAccess) == false) {
            return false;
        }
        VpnAccess rhs = ((VpnAccess) other);
        return (super.equals(rhs)&&((this.vpnexit == rhs.vpnexit)||((this.vpnexit!= null)&&this.vpnexit.equals(rhs.vpnexit))));
    }

}
