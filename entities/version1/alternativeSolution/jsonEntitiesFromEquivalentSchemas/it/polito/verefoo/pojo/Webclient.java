
package it.polito.verefoo.pojo;

import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "nameWebServer"
})
public class Webclient {

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("nameWebServer")
    @NotNull
    private String nameWebServer;

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("nameWebServer")
    public String getNameWebServer() {
        return nameWebServer;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("nameWebServer")
    public void setNameWebServer(String nameWebServer) {
        this.nameWebServer = nameWebServer;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Webclient.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("nameWebServer");
        sb.append('=');
        sb.append(((this.nameWebServer == null)?"<null>":this.nameWebServer));
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
        result = ((result* 31)+((this.nameWebServer == null)? 0 :this.nameWebServer.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Webclient) == false) {
            return false;
        }
        Webclient rhs = ((Webclient) other);
        return ((this.nameWebServer == rhs.nameWebServer)||((this.nameWebServer!= null)&&this.nameWebServer.equals(rhs.nameWebServer)));
    }

}
