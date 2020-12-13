
package it.polito.verefoo.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "source",
    "destination",
    "requestLatency"
})
public class LinkConstraint {

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("source")
    private String source;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("destination")
    private String destination;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("requestLatency")
    private Double requestLatency;

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

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("requestLatency")
    public Double getRequestLatency() {
        return requestLatency;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("requestLatency")
    public void setRequestLatency(Double requestLatency) {
        this.requestLatency = requestLatency;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(LinkConstraint.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("source");
        sb.append('=');
        sb.append(((this.source == null)?"<null>":this.source));
        sb.append(',');
        sb.append("destination");
        sb.append('=');
        sb.append(((this.destination == null)?"<null>":this.destination));
        sb.append(',');
        sb.append("requestLatency");
        sb.append('=');
        sb.append(((this.requestLatency == null)?"<null>":this.requestLatency));
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
        result = ((result* 31)+((this.destination == null)? 0 :this.destination.hashCode()));
        result = ((result* 31)+((this.requestLatency == null)? 0 :this.requestLatency.hashCode()));
        result = ((result* 31)+((this.source == null)? 0 :this.source.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof LinkConstraint) == false) {
            return false;
        }
        LinkConstraint rhs = ((LinkConstraint) other);
        return ((((this.destination == rhs.destination)||((this.destination!= null)&&this.destination.equals(rhs.destination)))&&((this.requestLatency == rhs.requestLatency)||((this.requestLatency!= null)&&this.requestLatency.equals(rhs.requestLatency))))&&((this.source == rhs.source)||((this.source!= null)&&this.source.equals(rhs.source))));
    }

}
