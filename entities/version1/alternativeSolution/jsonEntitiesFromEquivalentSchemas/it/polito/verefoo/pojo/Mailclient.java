
package it.polito.verefoo.pojo;

import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "mailserver"
})
public class Mailclient {

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("mailserver")
    @NotNull
    private String mailserver;

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("mailserver")
    public String getMailserver() {
        return mailserver;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("mailserver")
    public void setMailserver(String mailserver) {
        this.mailserver = mailserver;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Mailclient.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("mailserver");
        sb.append('=');
        sb.append(((this.mailserver == null)?"<null>":this.mailserver));
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
        result = ((result* 31)+((this.mailserver == null)? 0 :this.mailserver.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Mailclient) == false) {
            return false;
        }
        Mailclient rhs = ((Mailclient) other);
        return ((this.mailserver == rhs.mailserver)||((this.mailserver!= null)&&this.mailserver.equals(rhs.mailserver)));
    }

}
