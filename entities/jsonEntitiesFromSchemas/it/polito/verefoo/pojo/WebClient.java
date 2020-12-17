
package it.polito.verefoo.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * webClient
 * <p>
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "webServerAddress"
})
public class WebClient
    extends NetworkFunction
{

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("webServerAddress")
    private String webServerAddress;

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("webServerAddress")
    public String getWebServerAddress() {
        return webServerAddress;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("webServerAddress")
    public void setWebServerAddress(String webServerAddress) {
        this.webServerAddress = webServerAddress;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(WebClient.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
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
        sb.append("webServerAddress");
        sb.append('=');
        sb.append(((this.webServerAddress == null)?"<null>":this.webServerAddress));
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
        result = ((result* 31)+((this.webServerAddress == null)? 0 :this.webServerAddress.hashCode()));
        result = ((result* 31)+ super.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof WebClient) == false) {
            return false;
        }
        WebClient rhs = ((WebClient) other);
        return (super.equals(rhs)&&((this.webServerAddress == rhs.webServerAddress)||((this.webServerAddress!= null)&&this.webServerAddress.equals(rhs.webServerAddress))));
    }

}
