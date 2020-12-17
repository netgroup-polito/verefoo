
package it.polito.verefoo.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "action",
    "sourceAddress",
    "destinationAddress",
    "protocol",
    "sourcePort",
    "destinationPort",
    "directional"
})
public class Rule {

    @JsonProperty("action")
    private ActionType action = null;
    @JsonProperty("sourceAddress")
    private String sourceAddress;
    @JsonProperty("destinationAddress")
    private String destinationAddress;
    @JsonProperty("protocol")
    private LevelFourProtocolType protocol = null;
    @JsonProperty("sourcePort")
    private Double sourcePort;
    @JsonProperty("destinationPort")
    private Double destinationPort;
    @JsonProperty("directional")
    private Boolean directional = true;

    @JsonProperty("action")
    public ActionType getAction() {
        return action;
    }

    @JsonProperty("action")
    public void setAction(ActionType action) {
        this.action = action;
    }

    @JsonProperty("sourceAddress")
    public String getSourceAddress() {
        return sourceAddress;
    }

    @JsonProperty("sourceAddress")
    public void setSourceAddress(String sourceAddress) {
        this.sourceAddress = sourceAddress;
    }

    @JsonProperty("destinationAddress")
    public String getDestinationAddress() {
        return destinationAddress;
    }

    @JsonProperty("destinationAddress")
    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    @JsonProperty("protocol")
    public LevelFourProtocolType getProtocol() {
        return protocol;
    }

    @JsonProperty("protocol")
    public void setProtocol(LevelFourProtocolType protocol) {
        this.protocol = protocol;
    }

    @JsonProperty("sourcePort")
    public Double getSourcePort() {
        return sourcePort;
    }

    @JsonProperty("sourcePort")
    public void setSourcePort(Double sourcePort) {
        this.sourcePort = sourcePort;
    }

    @JsonProperty("destinationPort")
    public Double getDestinationPort() {
        return destinationPort;
    }

    @JsonProperty("destinationPort")
    public void setDestinationPort(Double destinationPort) {
        this.destinationPort = destinationPort;
    }

    @JsonProperty("directional")
    public Boolean getDirectional() {
        return directional;
    }

    @JsonProperty("directional")
    public void setDirectional(Boolean directional) {
        this.directional = directional;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Rule.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("action");
        sb.append('=');
        sb.append(((this.action == null)?"<null>":this.action));
        sb.append(',');
        sb.append("sourceAddress");
        sb.append('=');
        sb.append(((this.sourceAddress == null)?"<null>":this.sourceAddress));
        sb.append(',');
        sb.append("destinationAddress");
        sb.append('=');
        sb.append(((this.destinationAddress == null)?"<null>":this.destinationAddress));
        sb.append(',');
        sb.append("protocol");
        sb.append('=');
        sb.append(((this.protocol == null)?"<null>":this.protocol));
        sb.append(',');
        sb.append("sourcePort");
        sb.append('=');
        sb.append(((this.sourcePort == null)?"<null>":this.sourcePort));
        sb.append(',');
        sb.append("destinationPort");
        sb.append('=');
        sb.append(((this.destinationPort == null)?"<null>":this.destinationPort));
        sb.append(',');
        sb.append("directional");
        sb.append('=');
        sb.append(((this.directional == null)?"<null>":this.directional));
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
        result = ((result* 31)+((this.destinationPort == null)? 0 :this.destinationPort.hashCode()));
        result = ((result* 31)+((this.protocol == null)? 0 :this.protocol.hashCode()));
        result = ((result* 31)+((this.sourcePort == null)? 0 :this.sourcePort.hashCode()));
        result = ((result* 31)+((this.sourceAddress == null)? 0 :this.sourceAddress.hashCode()));
        result = ((result* 31)+((this.destinationAddress == null)? 0 :this.destinationAddress.hashCode()));
        result = ((result* 31)+((this.directional == null)? 0 :this.directional.hashCode()));
        result = ((result* 31)+((this.action == null)? 0 :this.action.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Rule) == false) {
            return false;
        }
        Rule rhs = ((Rule) other);
        return ((((((((this.destinationPort == rhs.destinationPort)||((this.destinationPort!= null)&&this.destinationPort.equals(rhs.destinationPort)))&&((this.protocol == rhs.protocol)||((this.protocol!= null)&&this.protocol.equals(rhs.protocol))))&&((this.sourcePort == rhs.sourcePort)||((this.sourcePort!= null)&&this.sourcePort.equals(rhs.sourcePort))))&&((this.sourceAddress == rhs.sourceAddress)||((this.sourceAddress!= null)&&this.sourceAddress.equals(rhs.sourceAddress))))&&((this.destinationAddress == rhs.destinationAddress)||((this.destinationAddress!= null)&&this.destinationAddress.equals(rhs.destinationAddress))))&&((this.directional == rhs.directional)||((this.directional!= null)&&this.directional.equals(rhs.directional))))&&((this.action == rhs.action)||((this.action!= null)&&this.action.equals(rhs.action))));
    }

}
