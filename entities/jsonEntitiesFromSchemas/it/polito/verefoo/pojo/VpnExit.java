
package it.polito.verefoo.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * vpnExit
 * <p>
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "vpnAccess"
})
public class VpnExit
    extends NetworkFunction
{

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("vpnAccess")
    private String vpnAccess;

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("vpnAccess")
    public String getVpnAccess() {
        return vpnAccess;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("vpnAccess")
    public void setVpnAccess(String vpnAccess) {
        this.vpnAccess = vpnAccess;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(VpnExit.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
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
        sb.append("vpnAccess");
        sb.append('=');
        sb.append(((this.vpnAccess == null)?"<null>":this.vpnAccess));
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
        result = ((result* 31)+((this.vpnAccess == null)? 0 :this.vpnAccess.hashCode()));
        result = ((result* 31)+ super.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof VpnExit) == false) {
            return false;
        }
        VpnExit rhs = ((VpnExit) other);
        return (super.equals(rhs)&&((this.vpnAccess == rhs.vpnAccess)||((this.vpnAccess!= null)&&this.vpnAccess.equals(rhs.vpnAccess))));
    }

}
