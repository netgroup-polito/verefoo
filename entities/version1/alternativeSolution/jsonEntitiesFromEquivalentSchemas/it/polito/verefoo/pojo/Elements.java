
package it.polito.verefoo.pojo;

import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "action",
    "source",
    "destination",
    "protocol",
    "src_port",
    "dst_port",
    "directional"
})
public class Elements {

    @JsonProperty("action")
    private ActionTypes action = null;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("source")
    @NotNull
    private String source;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("destination")
    @NotNull
    private String destination;
    @JsonProperty("protocol")
    private L4ProtocolTypes protocol = null;
    @JsonProperty("src_port")
    private String srcPort;
    @JsonProperty("dst_port")
    private String dstPort;
    @JsonProperty("directional")
    private Boolean directional = true;

    @JsonProperty("action")
    public ActionTypes getAction() {
        return action;
    }

    @JsonProperty("action")
    public void setAction(ActionTypes action) {
        this.action = action;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("source")
    public String getSource() {
        return source;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("source")
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("destination")
    public String getDestination() {
        return destination;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("destination")
    public void setDestination(String destination) {
        this.destination = destination;
    }

    @JsonProperty("protocol")
    public L4ProtocolTypes getProtocol() {
        return protocol;
    }

    @JsonProperty("protocol")
    public void setProtocol(L4ProtocolTypes protocol) {
        this.protocol = protocol;
    }

    @JsonProperty("src_port")
    public String getSrcPort() {
        return srcPort;
    }

    @JsonProperty("src_port")
    public void setSrcPort(String srcPort) {
        this.srcPort = srcPort;
    }

    @JsonProperty("dst_port")
    public String getDstPort() {
        return dstPort;
    }

    @JsonProperty("dst_port")
    public void setDstPort(String dstPort) {
        this.dstPort = dstPort;
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
        sb.append(Elements.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("action");
        sb.append('=');
        sb.append(((this.action == null)?"<null>":this.action));
        sb.append(',');
        sb.append("source");
        sb.append('=');
        sb.append(((this.source == null)?"<null>":this.source));
        sb.append(',');
        sb.append("destination");
        sb.append('=');
        sb.append(((this.destination == null)?"<null>":this.destination));
        sb.append(',');
        sb.append("protocol");
        sb.append('=');
        sb.append(((this.protocol == null)?"<null>":this.protocol));
        sb.append(',');
        sb.append("srcPort");
        sb.append('=');
        sb.append(((this.srcPort == null)?"<null>":this.srcPort));
        sb.append(',');
        sb.append("dstPort");
        sb.append('=');
        sb.append(((this.dstPort == null)?"<null>":this.dstPort));
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
        result = ((result* 31)+((this.protocol == null)? 0 :this.protocol.hashCode()));
        result = ((result* 31)+((this.dstPort == null)? 0 :this.dstPort.hashCode()));
        result = ((result* 31)+((this.directional == null)? 0 :this.directional.hashCode()));
        result = ((result* 31)+((this.destination == null)? 0 :this.destination.hashCode()));
        result = ((result* 31)+((this.action == null)? 0 :this.action.hashCode()));
        result = ((result* 31)+((this.source == null)? 0 :this.source.hashCode()));
        result = ((result* 31)+((this.srcPort == null)? 0 :this.srcPort.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Elements) == false) {
            return false;
        }
        Elements rhs = ((Elements) other);
        return ((((((((this.protocol == rhs.protocol)||((this.protocol!= null)&&this.protocol.equals(rhs.protocol)))&&((this.dstPort == rhs.dstPort)||((this.dstPort!= null)&&this.dstPort.equals(rhs.dstPort))))&&((this.directional == rhs.directional)||((this.directional!= null)&&this.directional.equals(rhs.directional))))&&((this.destination == rhs.destination)||((this.destination!= null)&&this.destination.equals(rhs.destination))))&&((this.action == rhs.action)||((this.action!= null)&&this.action.equals(rhs.action))))&&((this.source == rhs.source)||((this.source!= null)&&this.source.equals(rhs.source))))&&((this.srcPort == rhs.srcPort)||((this.srcPort!= null)&&this.srcPort.equals(rhs.srcPort))));
    }

}
