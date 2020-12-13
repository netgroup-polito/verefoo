
package it.polito.verefoo.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "sourceHost",
    "destinationHost",
    "averageLatency"
})
public class Connection {

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("sourceHost")
    private String sourceHost;
    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("destinationHost")
    private String destinationHost;
    @JsonProperty("averageLatency")
    private Integer averageLatency;

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("sourceHost")
    public String getSourceHost() {
        return sourceHost;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("sourceHost")
    public void setSourceHost(String sourceHost) {
        this.sourceHost = sourceHost;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("destinationHost")
    public String getDestinationHost() {
        return destinationHost;
    }

    /**
     * 
     * (Required)
     * 
     */
    @JsonProperty("destinationHost")
    public void setDestinationHost(String destinationHost) {
        this.destinationHost = destinationHost;
    }

    @JsonProperty("averageLatency")
    public Integer getAverageLatency() {
        return averageLatency;
    }

    @JsonProperty("averageLatency")
    public void setAverageLatency(Integer averageLatency) {
        this.averageLatency = averageLatency;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Connection.class.getName()).append('@').append(Integer.toHexString(System.identityHashCode(this))).append('[');
        sb.append("sourceHost");
        sb.append('=');
        sb.append(((this.sourceHost == null)?"<null>":this.sourceHost));
        sb.append(',');
        sb.append("destinationHost");
        sb.append('=');
        sb.append(((this.destinationHost == null)?"<null>":this.destinationHost));
        sb.append(',');
        sb.append("averageLatency");
        sb.append('=');
        sb.append(((this.averageLatency == null)?"<null>":this.averageLatency));
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
        result = ((result* 31)+((this.sourceHost == null)? 0 :this.sourceHost.hashCode()));
        result = ((result* 31)+((this.averageLatency == null)? 0 :this.averageLatency.hashCode()));
        result = ((result* 31)+((this.destinationHost == null)? 0 :this.destinationHost.hashCode()));
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof Connection) == false) {
            return false;
        }
        Connection rhs = ((Connection) other);
        return ((((this.sourceHost == rhs.sourceHost)||((this.sourceHost!= null)&&this.sourceHost.equals(rhs.sourceHost)))&&((this.averageLatency == rhs.averageLatency)||((this.averageLatency!= null)&&this.averageLatency.equals(rhs.averageLatency))))&&((this.destinationHost == rhs.destinationHost)||((this.destinationHost!= null)&&this.destinationHost.equals(rhs.destinationHost))));
    }

}
