
package it.polito.verefoo.pojo;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * mailClient
 * <p>
 * 
 * 
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "mailServerAddress"
})
public class MailClient
    extends NetworkFunction
{

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("mailServerAddress")
    @Pattern(regexp = "^([0-9]|([1-9][0-9])|(1[0-9][0-9])|(2[0-4][0-9])|(25[0-5]))([.]([0-9]|([1-9][0-9])|(1[0-9][0-9])|(2[0-4][0-9])|(25[0-5]))){3}$")
    @NotNull
    private String mailServerAddress;

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("mailServerAddress")
    public String getMailServerAddress() {
        return mailServerAddress;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("mailServerAddress")
    public void setMailServerAddress(String mailServerAddress) {
        this.mailServerAddress = mailServerAddress;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(MailClient.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
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
        sb.append("mailServerAddress");
        sb.append('=');
        sb.append(((this.mailServerAddress == null)?"<null>":this.mailServerAddress));
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
        result = ((result* 31)+((this.mailServerAddress == null)? 0 :this.mailServerAddress.hashCode()));
        result = ((result* 31)+ super.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof MailClient) == false) {
            return false;
        }
        MailClient rhs = ((MailClient) other);
        return (super.equals(rhs)&&((this.mailServerAddress == rhs.mailServerAddress)||((this.mailServerAddress!= null)&&this.mailServerAddress.equals(rhs.mailServerAddress))));
    }

}
